package cab.management.CabManagementSystem.userlogin.repository;

import cab.management.CabManagementSystem.userlogin.model.UserCredentialsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CredentialsRepository extends JpaRepository<UserCredentialsModel, Long> {

    @Query("SELECT user FROM UserCredentialsModel user WHERE LOWER(user.username) = LOWER(:username)")
    public UserCredentialsModel findByUsername(@Param("username") String username);


}
