package com.gmail.merikbest2015.ecommerce.controller;

import com.gmail.merikbest2015.ecommerce.constants.Pages;
import com.gmail.merikbest2015.ecommerce.constants.PathConstants;
import com.gmail.merikbest2015.ecommerce.service.CartService;

import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import static com.gmail.merikbest2015.ecommerce.util.TestConstants.USER_EMAIL;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Sql(value = {"/sql/create-perfumes-before.sql", "/sql/create-user-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/sql/create-user-after.sql", "/sql/create-perfumes-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class CartControllerTest {

    @Autowired
    private CartController cartController;

    @MockBean
    private CartService cartService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithUserDetails(USER_EMAIL)
    @DisplayName("[200] GET /cart - Get Cart")
    public void getCart() throws Exception {
        mockMvc.perform(get(PathConstants.CART))
                .andExpect(status().isOk())
                .andExpect(view().name(Pages.CART))
                .andExpect(model().attribute("perfumes", hasSize(0)));
    }

    //Added Test Case

    @Test
    public void testAddPerfumeToCart() throws Exception {
        doNothing().when(cartService).addPerfumeToCart((Long) any());
        MockHttpServletRequestBuilder postResult = MockMvcRequestBuilders.post("/cart/add");
        MockHttpServletRequestBuilder requestBuilder = postResult.param("perfumeId", String.valueOf(1L));
        MockMvcBuilders.standaloneSetup(cartController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.model().size(0))
                .andExpect(MockMvcResultMatchers.view().name("redirect:/cart"))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/cart"));
    }

    //2nd Test Case

    @Test
    public void testRemovePerfumeFromCart() throws Exception {
        doNothing().when(cartService).removePerfumeFromCart((Long) any());
        MockHttpServletRequestBuilder postResult = MockMvcRequestBuilders.post("/cart/remove");
        MockHttpServletRequestBuilder requestBuilder = postResult.param("perfumeId", String.valueOf(1L));
        MockMvcBuilders.standaloneSetup(cartController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.model().size(0))
                .andExpect(MockMvcResultMatchers.view().name("redirect:/cart"))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/cart"));
    }

    @Test
    @WithUserDetails(USER_EMAIL)
    @DisplayName("[300] POST /cart/add - Add Perfume To Cart")
    public void addPerfumeToCart() throws Exception {
        mockMvc.perform(post(PathConstants.CART + "/add")
                        .param("perfumeId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(PathConstants.CART));
    }

    @Test
    @WithUserDetails(USER_EMAIL)
    @DisplayName("[300] POST /cart/remove - Remove Perfume From Cart")
    public void removePerfumeFromCart() throws Exception {
        mockMvc.perform(post(PathConstants.CART + "/remove")
                        .param("perfumeId", "44"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(PathConstants.CART));
    }


    // 3rd Test Case
    @org.junit.Test
    public void testGetCart() throws Exception {
        when(cartService.getPerfumesInCart()).thenReturn(new ArrayList<>());
        SecurityMockMvcRequestBuilders.FormLoginRequestBuilder requestBuilder = SecurityMockMvcRequestBuilders
                .formLogin();
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(cartController)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

}
