package com.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.entities.User;
import com.entities.UserWithLinks;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.services.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {UserController.class, UserService.class})
public class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private UserController userController;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    private static List<User> userList = new ArrayList<>();

    @BeforeClass
    public static void setUpClass() {
        userList.add(new User(1, "Vitalii", "Chief", true));
        userList.add(new User(2, "Volodya", "Chief", true));
        userList.add(new User(3, "Petro", "Developer", false));
        userList.add(new User(4, "Oleg", "Manager", false));
        userList.add(new User(5, "Nazar", "Homeless", true));
        userList.add(new User(6, "Adam", "Homeless", true));
    }

    @Test
    public void testOkResponse() throws Exception {
        mockMvc.perform(get("/user/all")).andExpect(status().isOk());
    }

    @Test
    public void testFindAll() throws Exception {
        BDDMockito.given(userService.getAllUsers()).willReturn(userList);
        mockMvc.perform(get("/user/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=utf-8"))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(userList)));
    }

    @Test
    public void testGetOneUser() throws Exception {
        BDDMockito.given(userService.getUserWithId(1)).willReturn(userList.get(1));
        mockMvc.perform(get("/user/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=utf-8"))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(userList.get(1))));
    }

    @Test
    public void testUpdateUser() throws Exception {
        User user = new User(3, "Petro", "Capitan", false);
        mockMvc.perform(put("/v2/user/").contentType("application/json;charset=utf-8").content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testCachedUser() throws Exception {
        BDDMockito.given(userService.getUserWithId(1)).willReturn(userList.get(3));
        mockMvc.perform(get("/user/firstUser"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=utf-8"))
                .andExpect(header().string("Cache-Control", "max-age=60"))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(userList.get(3))));
    }

    @Test
    public void testCrearCache() throws Exception {
        User user = new User(3, "Petro", "Capitan", false);
        mockMvc.perform(put("/user/firstUser").contentType("application/json;charset=utf-8").content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetAllUsersV2() throws Exception {
        BDDMockito.given(userService.getAllUsersV2()).willReturn(userList);
        mockMvc.perform(get("/v2/user/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=utf-8"))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(userList)));
    }

    @Test
    public void testGetUserOrg() throws Exception{
        BDDMockito.given(userService.getUserWithId(1)).willReturn(userList.get(2));
        mockMvc.perform(get("/user/{value}/org", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=utf-8"))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(userList.get(2))));
    }

    @Test
    public void testCreateUser() throws Exception {
        User user = new User(7, "Valera", "Capitan", false);
        mockMvc.perform(post("/v2/user/").contentType("application/json;charset=utf-8").content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(delete("/v2/user/{id}", "1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetUserLinks() throws Exception {
        UserWithLinks linkedUser = new UserWithLinks(userList.get(1));
        linkedUser.add(linkTo(methodOn(UserController.class).getUserLinks(1)).withSelfRel());
        linkedUser.add(linkTo(methodOn(UserController.class).getUserOrg(1)).withRel("Get users organization"));
        linkedUser.add(linkTo(methodOn(UserController.class).updateUser(null)).withRel("Update with PUT method"));
        linkedUser.add(linkTo(methodOn(UserController.class).updateUser(null)).withRel("Delete with DELETE method"));
        BDDMockito.given(userService.getUserWithId(1)).willReturn(userList.get(1));
        mockMvc.perform(get("/v2/user/{value}", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=utf-8"))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(linkedUser)));
    }
}
