package partida;

import java.util.Random;

/**
 * Dado de seis caras.
 * Cada tirada da un número aleatorio entre 1 y 6, y el dado recuerda el último
 * valor que ha salido.
 */
public class Dado {

    /** Último valor que ha salido en el dado (0 si todavía no se ha tirado). */
    private int valor;

    /** Generador de números aleatorios para las tiradas. */
    private Random generador = new Random();

    /**
     * Tira el dado y guarda el resultado.
     *
     * @return Número aleatorio entre 1 y 6.
     */
    public int hacerTirada() {
        // nextInt(6) genera de 0 a 5, sumamos 1 para tener de 1 a 6
        this.valor = generador.nextInt(6) + 1;
        return this.valor;
    }

    /**
     * Devuelve el último valor que ha salido en el dado.
     *
     * @return Valor de la última tirada, o 0 si todavía no se ha tirado.
     */
    public int getValor() {
        return valor;
    }
}
