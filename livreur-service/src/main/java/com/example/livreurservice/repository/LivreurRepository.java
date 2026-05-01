package com.example.livreurservice.repository;

import com.example.livreurservice.entity.Livreur;
import com.example.livreurservice.entity.StatutLivreur;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LivreurRepository extends JpaRepository<Livreur, Long> {
    List<Livreur> findByStatut(StatutLivreur statut);
}