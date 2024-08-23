package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.dto.OrderDTO;
import com.devsuperior.dscommerce.dto.OrderItemDTO;
import com.devsuperior.dscommerce.dto.ProductDTO;
import com.devsuperior.dscommerce.entities.*;
import com.devsuperior.dscommerce.repositories.OrderItemRepository;
import com.devsuperior.dscommerce.repositories.OrderRepository;
import com.devsuperior.dscommerce.repositories.ProductRepository;
import com.devsuperior.dscommerce.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Transactional(readOnly = true)  //anotação do spring e não do jakarta
    public OrderDTO findById(Long id) {

        //Tratado a exceção com o orElseThrow atráves de uma exceção customizada
        Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado"));

        authService.validateSelfOrAdmin(order.getClient().getId()); // Validando se o usuário é dono desse pedido ou ADMIN
        return new OrderDTO(order);

    }

    @Transactional
    public OrderDTO insert(OrderDTO dto) {
        //Criar o objeto do pedido
        Order order = new Order();

        order.setMoment(Instant.now()); // Momento em que o pedido foi gerado, nesse caso o exato momento que foi inserido
        order.setStatus(OrderStatus.WAITING_PAYMENT);// Inserindo o Status aguardando pagamento no momento que foi criado o pedido

        // Inserir o cliente, nesse caso é o usuário logado.Injetar o usuário com @Autowired
        User user = userService.autheticated(); // Pegando o cliente que é usuário logado
        order.setClient(user); // Inserindo o cliente no pedido

        //Buscar os itens no DTO
        for (OrderItemDTO itemDto : dto.getItems()) { // Percorrendo todos os itens que vieram na requisição
            //Criar uma instância de product com getReferenceById e associar Product com Order

            Product product = productRepository.getReferenceById(itemDto.getProductId());
            //Associando o item com o order
            OrderItem item = new OrderItem(order, product, itemDto.getQuantity(), product.getPrice());

            order.getItems().add(item); //Associando o Order com os itens
        }


        orderRepository.save(order);
        orderItemRepository.saveAll(order.getItems());

        return new OrderDTO(order);
    }


}
