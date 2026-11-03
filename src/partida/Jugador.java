package partida;

import java.util.ArrayList;
import monopoly.*;

/**
 * Jugador de la partida, con su avatar, su dinero y sus propiedades.
 * La banca también es un objeto de esta clase, creado con el constructor sin
 * parámetros.
 */
public class Jugador {

    //Atributos:
    private String nombre; //Nombre del jugador
    private Avatar avatar; //Avatar que tiene en la partida.
    private float fortuna; //Dinero que posee.
    private float gastos; //Gastos realizados a lo largo del juego.
    private boolean enCarcel; //Será true si el jugador está en la carcel
    private int tiradasCarcel; //Cuando está en la carcel, contará las tiradas sin éxito que ha hecho allí para intentar salir (se usa para limitar el numero de intentos).
    private int vueltas; //Cuenta las vueltas dadas al tablero.
    private ArrayList<Casilla> propiedades; //Propiedades que posee el jugador.

    /**
     * Crea la banca. No tiene avatar, empieza con {@code Valor.FORTUNA_BANCA} de
     * fortuna y con la lista de propiedades vacía.
     */
    public Jugador() {
        this.nombre = "Banca";
        this.fortuna = Valor.FORTUNA_BANCA;
        this.propiedades = new ArrayList<>();
    }

    /**
     * Crea un jugador con {@code Valor.FORTUNA_INICIAL} de fortuna, sin propiedades
     * y con un avatar nuevo del tipo indicado, colocado en la casilla de inicio.
     * No comprueba si ya existe otro jugador con ese nombre: hay que hacerlo antes
     * de crearlo.
     *
     * @param nombre     Nombre del jugador.
     * @param tipoAvatar Tipo de avatar: coche, esfinge, sombrero o pelota.
     * @param inicio     Casilla en la que empieza (la Salida).
     * @param avCreados  Avatares ya creados, para que el nuevo no repita
     *                   identificador.
     */
    public Jugador(String nombre, String tipoAvatar, Casilla inicio, ArrayList<Avatar> avCreados) {
        this.nombre = nombre;
        this.avatar = new Avatar(tipoAvatar,this,inicio,avCreados);
        this.fortuna = Valor.FORTUNA_INICIAL;
        this.propiedades = new ArrayList<>();
    }

    /**
     * Añade una casilla a las propiedades del jugador.
     *
     * @param casilla Casilla que se añade.
     */
    public void anhadirPropiedad(Casilla casilla) {
        if (casilla == null) {
            return;
        }
        if (!this.propiedades.contains(casilla)) {
            this.propiedades.add(casilla);
        }
    }

    /**
     * Quita una casilla de las propiedades del jugador.
     *
     * @param casilla Casilla que se quita.
     */
    public void eliminarPropiedad(Casilla casilla) {
        if (casilla == null) {
            return;
        }
        this.propiedades.remove(casilla);
    }

    /**
     * Suma una cantidad a la fortuna del jugador. Para quitarle dinero, se pasa una
     * cantidad negativa.
     *
     * @param valor Cantidad que se suma (negativa para restar).
     */
    public void sumarFortuna(float valor) {
        this.fortuna += valor;
    }

    /**
     * Suma una cantidad a los gastos del jugador, como el precio de una casilla que
     * compra o un impuesto que paga. Solo lleva la cuenta: el dinero se le quita
     * aparte, con {@code sumarFortuna}.
     *
     * @param valor Cantidad que se añade a los gastos.
     */
    public void sumarGastos(float valor) {
        if (valor > 0) {
            this.gastos += valor;
        }
    }

    /**
     * Envía al jugador a la Cárcel: coloca su avatar en la casilla de la Cárcel y
     * lo marca como encarcelado. Va directamente, sin pasar por la Salida, así que
     * no cobra.
     *
     * @param pos Casillas del tablero, en una lista por cada lado, para encontrar
     *            la de la Cárcel.
     */
    public void encarcelar(ArrayList<ArrayList<Casilla>> pos) {
        if (this.avatar != null) {
            this.avatar.getLugar().eliminarAvatar(this.avatar);
        }
        Casilla carcel = pos.get(1).get(0);
        if (this.avatar != null) {
            this.avatar.setLugar(carcel);
            carcel.anhadirAvatar(this.avatar);
        }
        this.enCarcel = true;
        this.tiradasCarcel = 0;
    }

    // Getters y setters:
    public String getNombre() {
        return nombre;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public float getFortuna() {
        return fortuna;
    }

    public boolean isEnCarcel() {
        return enCarcel;
    }

    public void setEnCarcel(boolean enCarcel) {
        this.enCarcel = enCarcel;
    }

    public void sumaTiradasCarcel() {
        this.tiradasCarcel = this.tiradasCarcel + 1;
    }

    public int getTiradasCarcel() {
        return tiradasCarcel;
    }

    public void setTiradasCarcel(int tiradasCarcel) {
        this.tiradasCarcel = tiradasCarcel;
    }

    public ArrayList<Casilla> getPropiedades() {
        return propiedades;
    }

}
