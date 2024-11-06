package com.example.inventoryservice.repository;

import com.example.inventoryservice.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

//we are using spring data gpa to store for the persistance pour simplifier les opérations de persistance des données, probablement en utilisant une base de données relationnelle avec JPA comme couche d'abstraction.
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory>findBySkuCodeIn(List<String> skuCode);
}
