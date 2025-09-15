package com.shortdick.Shorty.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Base62ConverterTest {

    @Test
    void testEncodeYDecode() {
        // Prueba 1: El caso base
        assertEquals("0", Base62Converter.encode(0L));
        assertEquals(0L, Base62Converter.decode("0"));

        // Prueba 2: Un número simple
        assertEquals("a", Base62Converter.encode(10L));
        assertEquals(10L, Base62Converter.decode("a"));

        // Prueba 3: El límite del primer dígito
        assertEquals("Z", Base62Converter.encode(61L));
        assertEquals(61L, Base62Converter.decode("Z"));

        // Prueba 4: El primer número de dos dígitos
        assertEquals("10", Base62Converter.encode(62L));
        assertEquals(62L, Base62Converter.decode("10"));
        
        // Prueba 5: El ejemplo que ya conocemos
        assertEquals("21", Base62Converter.encode(125L));
        assertEquals(125L, Base62Converter.decode("21"));
    }

    @Test
    void testDeIdaYVuelta() {
        // Prueba con un número grande al azar
        Long numeroOriginal = 123456789L;
        
        String codigo = Base62Converter.encode(numeroOriginal);
        Long numeroDecodificado = Base62Converter.decode(codigo);

        // Verificamos que después de codificar y decodificar, el número es el mismo
        assertEquals(numeroOriginal, numeroDecodificado);
    }
}
