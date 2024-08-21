package com.devsuperior.dscommerce.dto;

import com.devsuperior.dscommerce.entities.Order;
import com.devsuperior.dscommerce.entities.OrderItem;
import com.devsuperior.dscommerce.entities.OrderStatus;
import jakarta.validation.constraints.NotEmpty;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class OrderDTO {

    private Long id;
    private Instant moment;
    private OrderStatus status;
    // Criar esse objeto com os outros DTO's
    private ClientDTO client;
    private PaymentDTO payment;

    @NotEmpty(message = "Deve ter pelo menos um item")
    private List<OrderItemDTO> items = new ArrayList<>();

    //Gerar o construtor sem os itens (items)
    public OrderDTO(Long id, Instant moment, OrderStatus status, ClientDTO client, PaymentDTO payment) {
        this.id = id;
        this.moment = moment;
        this.status = status;
        this.client = client;
        this.payment = payment;
    }

    //Criar o objeto completo que será idêntico ao JSON
    public OrderDTO(Order entity) {
        this.id = entity.getId();
        this.moment = entity.getMoment();
        this.status = entity.getStatus();
        this.client = new ClientDTO(entity.getClient()); // Instanciar de acordo com o construtor
        //No caso do pagamento pode ser nulo ao gerar um pedido, então deve a possibilidade de iniciar com null
        this.payment = (entity.getPayment() == null ? null : new PaymentDTO(entity.getPayment()));

        for(OrderItem item : entity.getItems()){
            OrderItemDTO itemDTO = new OrderItemDTO(item);
            this.items.add(itemDTO);
        }
    }
    public Long getId() {
        return id;
    }
    public Instant getMoment() {
        return moment;
    }
    public OrderStatus getStatus() {
        return status;
    }
    public ClientDTO getClient() {
        return client;
    }
    public PaymentDTO getPayment() {
        return payment;
    }
    public List<OrderItemDTO> getItems() {
        return items;
    }

    public Double getTotal(){
        double sum = 0.00;
        for (OrderItemDTO item : items){
            sum += item.getSubTotal();
        }

        return sum;
    }
}
