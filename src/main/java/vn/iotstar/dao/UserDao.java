package vn.iotstar.dao;

import java.util.List;
import vn.iotstar.entity.User;

public interface UserDao {
    User get(String username);
    User getByEmail(String email);
    User findById(int id);
    List<User> findAll();
    void insert(User user);
    void update(User user);
    void delete(int id);
    boolean checkExistEmail(String email);
    boolean checkExistUsername(String username);
    boolean checkExistPhone(String phone);
}
