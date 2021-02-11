package com.project.TabernasSevilla.web;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.project.TabernasSevilla.configuration.SecurityConfiguration;
import com.project.TabernasSevilla.controller.OrderCancellationController;
import com.project.TabernasSevilla.domain.Actor;
import com.project.TabernasSevilla.domain.Dish;
import com.project.TabernasSevilla.domain.Establishment;
import com.project.TabernasSevilla.domain.RestaurantOrder;
import com.project.TabernasSevilla.repository.AdminRepository;
import com.project.TabernasSevilla.repository.BookingRepository;
import com.project.TabernasSevilla.repository.CookRepository;
import com.project.TabernasSevilla.repository.CurriculumRepository;
import com.project.TabernasSevilla.repository.CustomerRepository;
import com.project.TabernasSevilla.repository.EstablishmentRepository;
import com.project.TabernasSevilla.repository.ManagerRepository;
import com.project.TabernasSevilla.repository.OrderCancellationRepository;
import com.project.TabernasSevilla.repository.OrderLogRepository;
import com.project.TabernasSevilla.repository.OrderRepository;
import com.project.TabernasSevilla.repository.RegKeyRepository;
import com.project.TabernasSevilla.repository.ReviewRepository;
import com.project.TabernasSevilla.repository.TableRepository;
import com.project.TabernasSevilla.repository.WaiterRepository;
import com.project.TabernasSevilla.security.Authority;
import com.project.TabernasSevilla.security.AuthorityRepository;
import com.project.TabernasSevilla.security.AuthorityService;
import com.project.TabernasSevilla.security.User;
import com.project.TabernasSevilla.security.UserService;
import com.project.TabernasSevilla.service.ActorService;
import com.project.TabernasSevilla.service.AdminService;
import com.project.TabernasSevilla.service.DishService;
import com.project.TabernasSevilla.service.EstablishmentService;
import com.project.TabernasSevilla.service.OrderService;

@WebMvcTest(controllers = OrderCancellationController.class, 
	excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), 
	excludeAutoConfiguration = SecurityConfiguration.class, 
	includeFilters = {@ComponentScan.Filter(Service.class), @ComponentScan.Filter(Repository.class) })
public class OrderCancellationControllerTest {
	
	@Mock
	Actor actor;
	
	@Mock
	RestaurantOrder order;
	
	@MockBean
	private UserService userService;
	
	@MockBean
	private OrderService orderService;

	@MockBean
	private ActorService actorService;
	
	@MockBean
	private AdminService adminService;

	@MockBean
	private DishService dishService;

	@MockBean
	private EstablishmentService establishmentService;
	
	@MockBean
	private AuthorityService authService;

	@MockBean
	private ReviewRepository reviewRepository;

	@MockBean
	private AuthorityRepository authRepository;

	@MockBean
	private AdminRepository adminRepository;

	@MockBean
	private BookingRepository bookingRepository;

	@MockBean
	private EstablishmentRepository establishmentRepository;

	@MockBean
	private TableRepository tableRepository;

	@MockBean
	private CurriculumRepository cur;

	@MockBean
	private CookRepository cook;

	@MockBean
	private CustomerRepository cus;

	@MockBean
	private OrderRepository ord;

	@MockBean
	private RegKeyRepository reg;

	@MockBean
	private WaiterRepository wait;

	@MockBean
	private ManagerRepository man;

	@MockBean
	private OrderCancellationRepository canc;

	@MockBean
	private OrderLogRepository logga;

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	void setup() { 
		
	}
	
	@WithMockUser(value = "spring")
	@Test
	void testView() throws Exception {
		mockMvc.perform(get("/order/cancel/view/{id}", 1)).andExpect(status().isOk()).andExpect(view().name("order/view"));
	}
	

	
	@WithMockUser(value = "spring")
	@Test
	void testCancelId() throws Exception {
		User mockUser = new User();
		Set<Authority> ls = new HashSet<>();
		ls.add(new Authority("ADMIN"));
		mockUser.setAuthorities(ls);
		mockUser.setUsername("mockito");
		given(this.userService.getPrincipal()).willReturn(mockUser);
		given(this.actorService.getPrincipal()).willReturn(actor);		
		
		Establishment est = new Establishment();
		est.setId(1);
		est.setTitle("prueba");
		est.setAddress("calle ");
		est.setOpeningHours("24/7");
		est.setScore(2);
		given(this.establishmentService.findById(1)).willReturn(est); 

		RestaurantOrder orderR = new RestaurantOrder();
		orderR.setAddress("Calle Calamar");
		orderR.setEstablishment(this.establishmentService.findById(1));
		orderR.setDish(new ArrayList<Dish>());
		User user = new User();
		user.setUsername("mocki");
		given(actor.getUser()).willReturn(user);
		actor.setUser(user);
		orderR.setActor(actor);
		orderR.setType(RestaurantOrder.DELIVERY);
		orderR.setStatus(RestaurantOrder.OPEN);
		given(this.orderService.findById(1)).willReturn(Optional.of(orderR));
		given(order.getActor()).willReturn(actor);
		given(order.getActor().getUser()).willReturn(user);


		mockMvc.perform(get("/order/cancel/{id}", 1)).andExpect(status().isOk()).andExpect(view().name("order/cancel/edit"));
	}
	

	@ExceptionHandler
	@WithMockUser(value = "spring", roles = "ADMIN") 
	@Test
	void testSaveOrderCancel() throws Exception{
		mockMvc.perform(post("/order/cancel/save").with(csrf())
							.param("reason", "sample")
							.param("placementDate", "2021-02-08T14:56:00Z"))
						.andExpect(status().is3xxRedirection())
						.andExpect(view().name("redirect:/index"));
	}
	
	@ExceptionHandler
	@WithMockUser(value = "spring", roles = "ADMIN") 
	@Test
	void testBADSaveOrderCancel() throws Exception{
		mockMvc.perform(post("/order/cancel/save").with(csrf())
							.param("placementDate", "2021-02-08T14:56:00Z"))
						.andExpect(status().isOk())
						.andExpect(view().name("order/cancel/edit"));
	}
	
	
}
