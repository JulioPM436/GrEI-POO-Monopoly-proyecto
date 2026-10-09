package partida;

import monopoly.*;

import java.util.ArrayList;
import java.util.Random;


public class Avatar {

    //Atributos
    private String id; //Identificador: una letra generada aleatoriamente.
    private String tipo; //Sombrero, Esfinge, Pelota, Coche
    private Jugador jugador; //Un jugador al que pertenece ese avatar.
    private Casilla lugar; //Los avatares se sitúan en casillas del tablero.

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

    //Constructor vacío
    public Avatar() {
    }

    /*Constructor principal. Requiere éstos parámetros:
    * Tipo del avatar, jugador al que pertenece, lugar en el que estará ubicado, y un arraylist con los
    * avatares creados (usado para crear un ID distinto del de los demás avatares).
     */
    public Avatar(String tipo, Jugador jugador, Casilla lugar, ArrayList<Avatar> avCreados) {
        // Se asignan los atributos del objeto.
        this.tipo = tipo;
        this.jugador = jugador;
        this.lugar = lugar;
        // La propia función asigna el ID del avatar.
        generarId(avCreados);
    }

    //A continuación, tenemos otros métodos útiles para el desarrollo del juego.
    /*Método que permite mover a un avatar a una casilla concreta. Parámetros:
    * - Un array con las casillas del tablero. Se trata de un arrayList de arrayList de casillas (uno por lado).
    * - Un entero que indica el numero de casillas a moverse (será el valor sacado en la tirada de los dados).
    * EN ESTA VERSIÓN SUPONEMOS QUE valorTirada siemrpe es positivo.
     */

    public void setLugar(Casilla lugar) {
        this.lugar = lugar;
    }

    public void moverAvatar(ArrayList<ArrayList<Casilla>> casillas, int valorTirada) {
        if (this.lugar != null) {
            this.lugar.eliminarAvatar(this);
        }
        int posicion = lugar.getPosicion();
        posicion = (posicion + valorTirada)%40;

        int lado = posicion / 10;
        int indice = posicion % 10; //aqui se calcula en que lado y numero está
        this.lugar = casillas.get(lado).get(indice);
        this.lugar.anhadirAvatar(this);
    }

    /*Método que permite generar un ID para un avatar. Sólo lo usamos en esta clase (por ello es privado).
    * El ID generado será una letra mayúscula. Parámetros:
    * - Un arraylist de los avatares ya creados, con el objetivo de evitar que se generen dos ID iguales.
     */
    private void generarId(ArrayList<Avatar> avCreados) {
        // Se inicializa variables auxiliares, un booleano y una string.
        boolean repetido = false;
        String id;
        // Creamos un generador de números aleatorios.
        Random r = new Random();
        // Mientras que el ID generado siga estando repetido...
        do {
            // se generará otro aleatorio entre cero y 26 y se suma al ASCII de A, para crear una letra de A a Z
            id = String.valueOf('A' + r.nextInt(26));
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
}
