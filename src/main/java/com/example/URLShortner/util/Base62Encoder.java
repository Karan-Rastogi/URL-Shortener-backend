package com.example.URLShortner.util;

import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {

    private static final String CHARACTERS =
            "abcdefghijklmnopqrstuvwxyz" +
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "0123456789";

    private static final int BASE = 62;

    public String encode(long id) {
        StringBuilder result = new StringBuilder();

        while (id > 0) {
            int remainder = (int) (id % BASE);
            result.append(CHARACTERS.charAt(remainder));
            id = id / BASE;
        }

        // reverse because we built it backwards
        return result.reverse().toString();
    }


}
