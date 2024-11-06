package com.example.orderservice.service;
import com.example.orderservice.dto.InventoryResponse;
import com.example.orderservice.dto.OrderLineItemsDto;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderLineItems;
import com.example.orderservice.repository.OrderRepository;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
// Extrait une liste d'objets OrderLineItems à partir de l'objet orderRequest. Cette liste est obtenue en utilisant des opérations de flux (stream) et de mappage (map), puis convertie en une liste (toList()).

      List<OrderLineItems> orderLineItems =  orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                        .toList();
        order.setOrderLineItemsList(orderLineItems);


        //Cette ligne extrait les codes SKU (Stock Keeping Unit) de chaque article de la commande (order) en utilisant un flux (stream) sur la liste des articles de commande (orderLineItemsList). Pour chaque article de commande, la méthode getSkuCode() est appelée pour récupérer son code SKU. Ces codes SKU sont ensuite collectés dans une liste (List<String>) en utilisant la méthode toList().
       List<String> skuCodes = order.getOrderLineItemsList().stream().map(OrderLineItems::getSkuCode).toList();

        // Call Inventory Service, and place order if product is in stock

        InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build() )
                .retrieve()//Effectue la requête HTTP et récupère la réponse.
                .bodyToMono(InventoryResponse[].class)//read the data from the web client resp, Transforme le corps de la réponse en un objet Mono qui émet un tableau d'objets InventoryResponse.
                        .block();
//Cette ligne vérifie si tous les produits sont en stock en utilisant la méthode allMatch() pour vérifier si la propriété isInStock de chaque objet InventoryResponse est vraie pour tous les éléments du tableau.
        boolean allProductsInStock = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::isInStock);
        if(allProductsInStock){
            orderRepository.save(order);

        }
        else{
            throw new IllegalArgumentException("Product is not in stock, please try again later");
        }



    }

    public void deleteOrder(Long orderId) {
        // Check if the order exists
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));

        // Log the deletion attempt
        log.info("Deleting order with id: {}", orderId);

        // Delete the order from the repository
        orderRepository.delete(order);

        // Log successful deletion
        log.info("Order with id {} deleted successfully", orderId);
    }

    public Order getOrderById(Long orderId) {
        log.info("Retrieving order with id: {}", orderId);
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));
    }

    //cette méthode est utilisée pour mapper un objet de type OrderLineItemsDto vers un objet de type OrderLineItems. Cela permet de convertir les données d'un objet DTO vers un objet de modèle qui peut être utilisé dans la logique métier ou la persistance des données.







    private OrderLineItems mapToDto (OrderLineItemsDto orderLineItemsDto){
            OrderLineItems orderLineItems = new OrderLineItems();
            orderLineItems.setPrice(orderLineItemsDto.getPrice());
            orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
            orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
            return orderLineItems;
        }

}