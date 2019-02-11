package cab.management.CabManagementSystem.userlogin.repository;

import cab.management.CabManagementSystem.userlogin.model.TokenAuthModel;
import cab.management.CabManagementSystem.userlogin.model.UserCredentialsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TokenRepository extends JpaRepository<TokenAuthModel, Long> {


    @Query("SELECT user FROM TokenAuthModel user WHERE user.userId = :userId")
    public TokenAuthModel findByUserId(@Param("userId") Long userId);

}

