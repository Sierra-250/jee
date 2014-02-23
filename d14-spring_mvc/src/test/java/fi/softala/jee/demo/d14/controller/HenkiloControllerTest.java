package fi.softala.jee.demo.d14.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import fi.softala.jee.demo.d14.bean.HenkiloImpl;
import fi.softala.jee.demo.d14.dao.HenkiloDAO;
import fi.softala.jee.demo.d14.dao.HenkiloaEiLoydyPoikkeus;

@RunWith(MockitoJUnitRunner.class)
@WebAppConfiguration
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring-servlet.xml")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class })
public class HenkiloControllerTest {

	@Inject
	private MockMvc mockMvc;

	@InjectMocks
	private HenkiloController controller;
	@Mock
	private HenkiloDAO mockDao;

	@Before
	public void setup() {

		//controller unders
		this.mockMvc = MockMvcBuilders.standaloneSetup(this.controller).build();

		//configure the return values of the mock object 
		Mockito.when(mockDao.etsi(5)).thenReturn(new HenkiloImpl());
		Mockito.when(mockDao.etsi(0)).thenThrow(
				new HenkiloaEiLoydyPoikkeus(null));
	}

	@Test
	public void testEtsiJaLoyda() throws Exception {
		//create a request
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/henkilot/5");

		//check status, model size and view name
		mockMvc.perform(requestBuilder)
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.model().size(1))
				.andExpect(MockMvcResultMatchers.view().name("henk/view"));
		//verify that the mock has been called
		verify(mockDao, times(1)).etsi(5);
	}

	
	@Test
	public void testEtsiJaEiLoyda() throws Exception {
		//create a request
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/henkilot/0");

		//check that http status 404 has been generated
		mockMvc.perform(requestBuilder).andExpect(
				MockMvcResultMatchers.status().isNotFound());

		//verify that the mock has been called
		verify(mockDao, times(1)).etsi(0);
	}

}
