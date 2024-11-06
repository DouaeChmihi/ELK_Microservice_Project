package com.example.orderservice.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
   // OrderLineItemsDto est utilisé dans OrderRequest pour représenter les articles de commande lors de la création d'une nouvelle commande.
    private List<OrderLineItemsDto> orderLineItemsDtoList;
}
