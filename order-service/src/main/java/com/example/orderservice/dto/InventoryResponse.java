package com.example.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

//est un objet utilisé pour représenter la réponse du service d'inventaire lors de la vérification de la disponibilité des produits.
public class InventoryResponse {
    private String skuCode;
    private boolean isInStock;
}