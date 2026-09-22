package partida;

import java.util.Random;


public class Dado {

    private int valor;
    private Random generador = new Random();

    public int hacerTirada() {
        // nextInt(6) genera de 0 a 5, sumamos 1 para tener de 1 a 6
        this.valor = generador.nextInt(6) + 1;
        return this.valor;
    }

    public int getValor() {
        return valor;



    /*
    //El dado solo tiene un atributo en nuestro caso: su valor.
    private int valor;

    //Metodo para simular lanzamiento de un dado: devolverá un valor aleatorio entre 1 y 6.
    public int hacerTirada() {
        return 0;
    }


    */

    }}