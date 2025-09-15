package com.shortdick.Shorty.util;

public final class Base62Converter {

    public static final String alfabeto = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final int base = alfabeto.length();

    private Base62Converter() {

    }

    public static String encode(Long id) {
        StringBuilder stringBuilder = new StringBuilder();

        if(id==0) {
            return String.valueOf(alfabeto.charAt(0));
        }

        while (id>0) {
            Long res = id % base;
            stringBuilder.append(alfabeto.charAt(res.intValue()));
            id = id / base;
        }

        return stringBuilder.reverse().toString();
    }

    public static Long decode(String shortCode) {
        Long id = 0L;

        for(int i = 0; i<shortCode.length(); i++) {
            char caracter = shortCode.charAt(i);
            int posicionAlfabeto = alfabeto.indexOf(caracter);
            id = (id * base) + posicionAlfabeto;
        }
        return id;
    }
}
