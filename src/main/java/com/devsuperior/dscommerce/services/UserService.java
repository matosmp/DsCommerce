package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.dto.UserDTO;
import com.devsuperior.dscommerce.entities.Role;
import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.projection.UserDetailsProjection;
import com.devsuperior.dscommerce.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        List<UserDetailsProjection> result = userRepository.searchUserAndRolesByEmail(username);
        if (result.size() == 0) { // Verificar se a lista está vazia.
            throw new UsernameNotFoundException("User not found");
        }

        //Montar o objeto de retorno usuário com lista de roles associada a ele.
        User user = new User();
        user.setEmail(username); // Atribuir usuário
        user.setPassword(result.get(0).getPassword()); // Pegar do result o password que está na posição zero get(0).getPassword()
        // Adicionar a lista de roles desse usuário
        for (UserDetailsProjection projection : result) {
            user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));//Adiciona um objeto Role na lista do usuário
        }
        return user;
    }


    //Método para buscar o usuário autenticado no contexto do Spring
    protected User autheticated() {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();//Cast para o tipo JWT porque tem os claims que buscará o email no token
            String username = jwtPrincipal.getClaim("username");

            return userRepository.findByEmail(username).get();
        } catch (Exception e) {
                throw new UsernameNotFoundException("User not found");
        }

    }
    //Método para retornar o usuário convertido em UserDto para a controller
    @Transactional(readOnly = true)  // @Transactional porque é somente de leitura
    public UserDTO getMe(){
        User user = autheticated();
        return new UserDTO(user);
    }


}
