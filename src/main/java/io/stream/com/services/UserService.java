package io.stream.com.services;

import io.stream.com.controllers.exceptions.UserNotFoundException;
import io.stream.com.mappers.UserMapper;
import io.stream.com.models.User;
import io.stream.com.models.dtos.AuthenticationDto;
import io.stream.com.models.dtos.LoginDto;
import io.stream.com.models.dtos.ProfileDto;
import io.stream.com.models.dtos.SignUpDto;
import io.stream.com.repositories.UserRepository;
import io.stream.com.security.JWTService;
import io.stream.com.utils.EmailUtil;
import io.stream.com.utils.KeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    @Autowired 
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CacheService cacheService;

    public User getCurrentLoggedInUser(){ 
        return loadUserByUsername(getUsernameFromSecurityContextHolder()); 
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = repository.findByUsername(username);

        if(!userOptional.isPresent()){
            log.error("User not found with username {}", username);
            throw new UsernameNotFoundException(username);
        }

        return userOptional.get();
    }

    public List<User> getAll(){
        return repository.findAll();
    }

    public ProfileDto update(ProfileDto profileDto, Long id){
		Optional<User> optionalUser = repository.findById(id);

		if(!optionalUser.isPresent())
            throw new UserNotFoundException(id);

		User user = optionalUser.get();
		user.setUsername(profileDto.getUsername());
        user.setAvatarId(profileDto.getAvatarId());
        user.setBio(profileDto.getBio());
        user.setFullName(profileDto.getFullName());

		return UserMapper.mapProfile(repository.save(user));
	}

    public boolean isNotMatching(String password, String confirmedPassword){ 
        return !password.equals(confirmedPassword); 
    }

    public AuthenticationDto authenticate(String username, String password) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        SecurityContextHolder.getContext().setAuthentication(authenticate);
        
        return new AuthenticationDto(jwtService.generateToken((User) authenticate.getPrincipal()));
    }

    public AuthenticationDto signup(SignUpDto signUpDto) {
        String token = KeyUtil.generate();

        repository.save(UserMapper.mapSignUp(signUpDto, passwordEncoder.encode(signUpDto.getPassword())));
        
        //emailService.sendVerification(signUpDto.getEmail(), token);

        //cacheService.addEmailVerifyingToken(token, signUpDto.getEmail());

        return authenticate(signUpDto.getUsername(), signUpDto.getPassword());
    }

    public boolean isEmailTokenNotValid(String token){ 
        return !cacheService.isExistAndValidEmailToken(token); 
    }

    public void enableAccount(String token){
        repository.enableAccountByEmail(cacheService.getEmailOfToken(token));
    }

    public void lastseen(){
        repository.updateLastSeen(getCurrentLoggedInUser().getUserId(), new Date());
    }

    private String getUsernameFromSecurityContextHolder() { 
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails)
            return ((User)principal).getUsername();
        else
            return principal.toString();
    }

    public ProfileDto getProfile() { 
        return UserMapper.mapProfile(getCurrentLoggedInUser());
    }

    public boolean isEmailExists(String email) { 
        return repository.existsByEmail(email); 
    }

    public boolean isUsernameExists(String username) { 
        return repository.existsByUsername(username); 
    }
}
