package com.quemistry.auth_ms.repository;

import com.quemistry.auth_ms.entity.Role;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query("select r from Role r left join fetch r.grantedWith where r.name = :name")
    Optional<Role> findByName(@Param("name") String name);

    @Query("select r from Role r left join fetch r.grantedWith where r.name in :names")
    List<Role> findByNames(@Param("names") String[] names);
}

