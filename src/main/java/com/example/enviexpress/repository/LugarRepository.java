package com.example.enviexpress.repository;

import com.example.enviexpress.model.Lugar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LugarRepository extends JpaRepository<Lugar, Long> {
}