package sk.upjs.paz.security;

import lombok.Getter;
import lombok.Setter;

public enum Auth {
    INSTANCE;

    @Getter
    @Setter
    private Principal principal;
}
