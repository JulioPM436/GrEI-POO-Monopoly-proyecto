package partida;

import monopoly.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Ficha con la que un jugador se mueve por el tablero.
 * Tiene un identificador de una letra, que es lo que se ve en el tablero, un
 * tipo (coche, esfinge, sombrero o pelota), el jugador al que pertenece y la
 * casilla en la que está.
 */
public class Avatar {

    //Atributos
    private String id; //Identificador: una letra generada aleatoriamente.
    private String tipo; //Sombrero, Esfinge, Pelota, Coche
    private Jugador jugador; //Un jugador al que pertenece ese avatar.
    private Casilla lugar; //Los avatares se sitúan en casillas del tablero.

    /**
     * Crea un avatar sin datos.
     */
    public Avatar() {
    }

    /**
     * Crea el avatar de un jugador, le asigna un identificador que no tiene ningún
     * otro avatar y lo sitúa en la casilla indicada.
     *
     * @param tipo      Tipo de avatar: coche, esfinge, sombrero o pelota.
     * @param jugador   Jugador al que pertenece el avatar.
     * @param lugar     Casilla en la que empieza (la Salida).
     * @param avCreados Avatares ya creados, para no repetir identificador.
     */
    public Avatar(String tipo, Jugador jugador, Casilla lugar, ArrayList<Avatar> avCreados) {
        // Se asignan los atributos del objeto.
        this.tipo = tipo;
        this.jugador = jugador;
        this.lugar = lugar;
        // La propia función asigna el ID del avatar.
        generarId(avCreados);
    }

    /**
     * Cambia la casilla en la que está el avatar, pero sin actualizar las listas de
     * avatares de las casillas. Para mover el avatar, usa {@code moverAvatar} o
     * {@code colocarEn}.
     *
     * @param lugar Nueva casilla del avatar.
     */
    public void setLugar(Casilla lugar) {
        this.lugar = lugar;
    }

    /**
     * Avanza el avatar tantas casillas como indique la tirada, dando la vuelta al
     * tablero si hace falta. Lo quita de la casilla en la que estaba y lo añade a la
     * de destino; si pasa por la Salida, el jugador cobra lo que le corresponde.
     * Supone que {@code valorTirada} es positivo.
     *
     * @param casillas    Casillas del tablero, en una lista por cada lado.
     * @param valorTirada Número de casillas que avanza (normalmente, la suma de los
     *                    dados).
     */
    public void moverAvatar(ArrayList<ArrayList<Casilla>> casillas, int valorTirada) {
        if (this.lugar != null) {
            this.lugar.eliminarAvatar(this);
        }
        assert lugar != null;
        if ((lugar.getPosicion() - 1 + valorTirada) / 40 >= 1) {
            System.out.println(this.jugador.getNombre() + " pasa por la Salida y cobra " + (long) Valor.SUMA_VUELTA + "€.");
            this.jugador.sumarFortuna(Valor.SUMA_VUELTA);
        }
        int posicion = (lugar.getPosicion() - 1 + valorTirada) % 40;
        int lado = posicion / 10;
        int indice = posicion % 10; //aqui se calcula en que lado y numero está
        this.lugar = casillas.get(lado).get(indice);
        this.lugar.anhadirAvatar(this);
    }

    /**
     * Genera el identificador del avatar: una letra mayúscula aleatoria que no
     * tenga ningún otro avatar de la partida.
     *
     * @param avCreados Avatares ya creados, para comprobar que la letra no está
     *                  cogida.
     */
    private void generarId(ArrayList<Avatar> avCreados) {
        // Se inicializa variables auxiliares, un booleano y una string.
        boolean repetido;
        String id;
        // Creamos un generador de números aleatorios.
        Random r = new Random();
        // Mientras que el ID generado siga estando repetido...
        do {
            // se generará otro aleatorio entre cero y 26 y se suma al ASCII de A, para crear una letra de A a Z
            id = String.valueOf((char) ('A' + r.nextInt(26)));
            // se reincia el detector de repetición para que una sola coincidencia no haga bucle infinito.
            repetido = false;
            // Para cada una se itera sobre todos los IDs.
            for (Avatar a : avCreados){
                // Si alguno coincide, se marca coincidencia y sale del for (no hace falta iterar sobre el resto.
                if (id.equals(a.id)){
                    repetido = true;
                    break;
                }
            }

        } while (repetido);

        // Si sale del bucle, es que el ID no está repetido. Se asigna el atributo.
        this.id = id;
    }

    /**
     * Coloca el avatar directamente en la casilla indicada, sin recorrer el tablero,
     * así que no cobra la Salida aunque pase por ella. Lo quita de su casilla actual
     * y lo añade a la de destino. Sirve, por ejemplo, para enviarlo a la Cárcel.
     *
     * @param destino Casilla en la que se coloca el avatar.
     */
    public void colocarEn(Casilla destino) {
        this.lugar.eliminarAvatar(this);
        this.lugar = destino;
        destino.anhadirAvatar(this);
    }

    // Getters y setters:
    public Jugador getJugador() {
        return jugador;
    }

    public Casilla getLugar() {
        return lugar;
    }

    public String getTipo() {
        return tipo;
    }

    public String getId(){
        return id;
    }
}


