package sk.upjs.paz.security;


import sk.upjs.paz.exceptions.AuthenticationException;

public interface AuthDao {
    Principal authenticate(String email, String password) throws AuthenticationException;
}
