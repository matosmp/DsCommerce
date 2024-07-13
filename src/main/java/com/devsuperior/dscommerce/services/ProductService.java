package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.dto.ProductDTO;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.repositories.ProductRepository;
import com.devsuperior.dscommerce.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Transactional(readOnly = true)  //anotação do spring e não do jakarta
    public ProductDTO findById(Long id) {

        //Tratado a exceção com o orElseThrow atráves de uma exceção customizada
        Product product = repository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Recurso não encontrado"));
        return new ProductDTO(product);

        /* Buscando o produto por id sem tratar exceção
         * Product product = repository.findById(id).get();
         * return new ProductDTO(product);
         */
        /* Forma mais didática para entender o passo a passo de um get no banco de dados
            Optional<Product> result = repository.findById(id);
            Product product = result.get();
            ProductDTO dto = new ProductDTO(product);
            return dto;
        */
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAll(Pageable pageable){
        Page<Product> result = repository.findAll(pageable); // o Spring já tem um método findAll que recebe um objeto do tipo Pageable
        return result.map(x -> new ProductDTO(x));
    }

    public ProductDTO insert(ProductDTO dto){
        Product entity = new Product();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setImgUrl(dto.getImgUrl());

        entity = repository.save(entity);

        return new ProductDTO(entity);
    }

    @Transactional
    public void delete(Long id){
        repository.deleteById(id);
    }

    @Transactional
    public ProductDTO update(Long id , ProductDTO dto){

        /* getReferenceById não busca os dados no banco de dados, ele prepara os
         * dados para atualizar, criando uma instância do objeto com o id e depois o dto atualiza a entity
         * atráves do método copyDtoToEntity
         * */
        Product entity = repository.getReferenceById(id);
        copyDtoToEntity(dto,entity);

        entity = repository.save(entity);

        return new ProductDTO(entity);
    }


    private void copyDtoToEntity(ProductDTO dto, Product entity) {

            entity.setName(dto.getName());
            entity.setDescription(dto.getDescription());
            entity.setPrice(dto.getPrice());
            entity.setImgUrl(dto.getImgUrl());
        }
    }


