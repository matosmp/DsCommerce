package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.entities.Role;
import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.projection.UserDetailsProjection;
import com.devsuperior.dscommerce.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        List<UserDetailsProjection> result = userRepository.searchUserAndRolesByEmail(username);
        if(result.size() == 0){ // Verificar se a lista está vazia.
            throw new UsernameNotFoundException("User not found");
        }

        //Montar o objeto de retorno usuário com lista de roles associada a ele.
        User user = new User();
        user.setEmail(username); // Atribuir usuário
        user.setPassword(result.get(0).getPassword()); // Pegar do result o password que está na posição zero get(0).getPassword()
        // Adicionar a lista de roles desse usuário
        for (UserDetailsProjection projection : result){
            user.addRole(new Role(projection.getRoleId(),projection.getAuthority()));//Adiciona um objeto Role na lista do usuário
        }
        return user;
    }

}
