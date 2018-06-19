package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import entities.User;
import entities.UserWithLinks;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import services.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
@ContextConfiguration(classes = UserControllerUnitTestConfig.class)
public class UserControllerMockMvcTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    private static List<User> userList = new ArrayList<>();

    @Before
    public void setUp() {
        userList.add(new User(1, "Vitalii", "Chief", true));
        userList.add(new User(2, "Volodya", "Chief", true));
        userList.add(new User(3, "Petro", "Developer", false));
        userList.add(new User(4, "Oleg", "Manager", false));
        userList.add(new User(5, "Nazar", "Homeless", true));
        userList.add(new User(6, "Adam", "Homeless", true));
    }

    @After
    public void shutDown() {
        userList = new ArrayList<>();
    }

    @Test
    public void findAll() throws Exception {
        BDDMockito.given(userService.getAllUsers()).willReturn(userList);
        mockMvc.perform(get("/user/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(userList)));
    }

    @Test
    public void getOneUser() throws Exception {
        BDDMockito.given(userService.getUserWithId(1)).willReturn(userList.get(1));
        mockMvc.perform(get("/user/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(userList.get(1))));
    }

    @Test
    public void testUpdateUser() throws Exception {
        User user = new User(3, "Petro", "Capitan", false);
        mockMvc.perform(put("/v2/user/").contentType(MediaType.APPLICATION_JSON_UTF8).content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void cachedUser() throws Exception {
        BDDMockito.given(userService.getUserWithId(1)).willReturn(userList.get(3));
        mockMvc.perform(get("/user/firstUser"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(header().string("Cache-Control", "max-age=60"))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(userList.get(3))));
    }

    @Test
    public void crearCache() throws Exception {
        User user = new User(3, "Petro", "Capitan", false);
        mockMvc.perform(put("/user/firstUser").contentType(MediaType.APPLICATION_JSON_UTF8).content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void getAllUsersV2() throws Exception {
        BDDMockito.given(userService.getAllUsersV2()).willReturn(userList);
        mockMvc.perform(get("/v2/user/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(userList)));
    }

    @Test
    public void getUserOrg() throws Exception{
        BDDMockito.given(userService.getUserWithId(1)).willReturn(userList.get(2));
        mockMvc.perform(get("/user/{value}/org", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(userList.get(2))));
    }

    @Test
    public void createUser() throws Exception {
        User user = new User(7, "Valera", "Capitan", false);
        mockMvc.perform(post("/v2/user/").contentType(MediaType.APPLICATION_JSON_UTF8).content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(delete("/v2/user/{id}", "1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void getUserLinks() throws Exception {
        UserWithLinks linkedUser = new UserWithLinks(userList.get(1));
        linkedUser.add(linkTo(methodOn(UserController.class).getUserLinks(1)).withSelfRel());
        linkedUser.add(linkTo(methodOn(UserController.class).getUserOrganisation(1)).withRel("Get_users_organization"));
        linkedUser.add(linkTo(methodOn(UserController.class).updateUser(null)).withRel("Update_with_PUT_method"));
        linkedUser.add(linkTo(methodOn(UserController.class).updateUser(null)).withRel("Delete_with_DELETE_method"));
        BDDMockito.given(userService.getUserWithId(1)).willReturn(userList.get(1));
        mockMvc.perform(get("/v2/user/{value}", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }
}
