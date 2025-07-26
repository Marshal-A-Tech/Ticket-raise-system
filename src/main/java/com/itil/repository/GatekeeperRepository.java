package com.itil.repository;


import com.itil.entity.Gatekeeper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GatekeeperRepository extends JpaRepository<Gatekeeper, Long> {
    Optional<Gatekeeper> findFirstByOrderByIdAsc();
}
