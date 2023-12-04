package com.levelgroup.repo;

import com.levelgroup.model.RiaLGObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RiaLGObjectRepository extends JpaRepository<RiaLGObject, Long> {

    @Query("SELECT o FROM RiaLGObject o WHERE o.localRealtyId = :localRealtyId")
    RiaLGObject findRiaLGObjectByLocalRealtyId(@Param("localRealtyId") String localRealtyId);

}
