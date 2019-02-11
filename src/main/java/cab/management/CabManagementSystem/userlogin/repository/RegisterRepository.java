package cab.management.CabManagementSystem.userlogin.repository;

import cab.management.CabManagementSystem.userlogin.model.UserRegistrationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegisterRepository extends JpaRepository<UserRegistrationModel, Long> {


}
