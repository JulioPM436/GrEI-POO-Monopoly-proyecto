package monopoly;

import partida.Jugador;

public abstract class Propiedad extends Casilla{
    private Jugador duenho; //Dueño de la casilla (por defecto sería la banca).
    private float valor; //Valor de esa casilla (será valor de compra, en la casilla parking se usará como el bote).

    public boolean perteneceAJugador(Jugador jugador) {
        return this.duenho != null && this.duenho.equals(jugador);
    }

    /**
     * Vende la casilla al jugador que la solicita: le cobra su valor, la casilla
     * pasa a ser suya y se añade a sus propiedades. La casilla tiene que pertenecer
     * todavía a la banca.
     *
     * @param solicitante Jugador que compra la casilla.
     * @param banca       La banca, dueña de las casillas que aún no se han comprado.
     */
    public void comprarCasilla (Jugador solicitante, Jugador banca){
        if (this.getDuenho() != banca) {
            System.out.println(Valor.YELLOW + "No puedes comprar esta casilla porque es de otro jugador" + Valor.RESET);
            return;
        } else if (solicitante.getFortuna()<this.getValor()){
            System.out.println();
            System.out.println(Valor.YELLOW + "No puedes comprar esta casilla porque no tienes saldo suficiente. |SALDO ACTUAL:"
                    + solicitante.getFortuna() + Valor.RESET);
            return;
        }
        solicitante.sumarFortuna(-this.valor);
        solicitante.sumarGastos(this.valor);
        solicitante.sumarDineroInvertido(this.valor);
        banca.sumarFortuna(this.valor);

        this.duenho = solicitante;
        solicitante.anhadirPropiedad(this);

        System.out.println("El jugador " + solicitante.getNombre() + " compra la casilla " + this.nombre + " por " + this.valor + "€.");
    }

    public abstract boolean alquiler();
    public abstract float valor();

    public Jugador getDuenho() { return duenho; }
    public void setDuenho(Jugador duenho) { this.duenho = duenho; }
    public float getValorBase() { return valor; }




}



