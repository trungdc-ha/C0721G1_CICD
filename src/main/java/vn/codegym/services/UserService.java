package vn.codegym.services;

import org.springframework.security.core.userdetails.UserDetails;
import vn.codegym.model.User;

import java.util.List;

public interface UserService {
    List<User> findAll();
    //CRUD
}
