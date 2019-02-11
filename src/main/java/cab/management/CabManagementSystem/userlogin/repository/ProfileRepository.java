package cab.management.CabManagementSystem.userlogin.repository;

import cab.management.CabManagementSystem.userlogin.model.UserCredentialsModel;
import cab.management.CabManagementSystem.userlogin.model.UserProfileModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProfileRepository extends JpaRepository<UserProfileModel, Long> {

    @Query("SELECT user FROM UserProfileModel user WHERE LOWER(user.nstId) = LOWER(:nstId)")
    public UserProfileModel findByNstId(@Param("nstId") String nstId);
}
