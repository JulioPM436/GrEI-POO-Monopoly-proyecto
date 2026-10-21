package monopoly;

import partida.*;
import java.util.ArrayList;
import java.util.StringJoiner;

/**
 * Casilla del tablero del Monopoly.
 * Guarda su nombre, tipo, posición y dueño y, según el tipo, su valor, alquiler,
 * hipoteca, grupo y edificios, además de los avatares que hay en ella. La casilla
 * del Parking usa su valor como bote.
 */
public class Casilla {
    private String nombre; //Nombre de la casilla
    private String tipo; //Tipo de casilla (Solar, Especial, Transporte, Servicios, Comunidad, Suerte y Impuesto).
    private float valor; //Valor de esa casilla (será valor de compra, en la casilla parking se usará como el bote).
    private int posicion; //Posición que ocupa la casilla en el tablero (entero entre 1 y 40).
    private Jugador duenho; //Dueño de la casilla (por defecto sería la banca).
    private Grupo grupo; //Grupo al que pertenece la casilla (si es solar).
    private float impuesto; //Cantidad a pagar por caer en la casilla: el alquiler en solares/servicios/transportes o impuestos.
    private float hipoteca; //Valor otorgado por hipotecar una casilla.
    private ArrayList<Avatar> avatares = new ArrayList<>(); //Avatares que están situados en la casilla.
    private int casas, hotel, piscina, pistadeporte; // Número de edificaciones dentro de cada casilla.
    private boolean hipotecada;

    /**
     * Crea una casilla sin datos.
     */
    public Casilla() {
    }

    /**
     * Crea una casilla que se puede comprar: un solar, un servicio o un transporte.
     *
     * @param nombre   Nombre de la casilla.
     * @param tipo     Tipo de la casilla: Solar, Servicios o Transporte.
     * @param posicion Posición en el tablero, entre 1 y 40.
     * @param valor    Precio de compra.
     * @param duenho   Dueño inicial (la banca).
     */
    public Casilla(String nombre, String tipo, int posicion, float valor, Jugador duenho) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.posicion = posicion;
        this.valor = valor;
        this.duenho = duenho;
    }

    /**
     * Crea una casilla que se puede comprar guardando además su alquiler.
     * Se usa para los solares.
     *
     * @param nombre   Nombre de la casilla.
     * @param tipo     Tipo de la casilla (Solar).
     * @param posicion Posición en el tablero, entre 1 y 40.
     * @param valor    Precio de compra.
     * @param duenho   Dueño inicial (la banca).
     * @param impuesto Alquiler que paga quien cae en la casilla.
     */
    public Casilla(String nombre, String tipo, int posicion, float valor, Jugador duenho, float impuesto) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.posicion = posicion;
        this.valor = valor;
        this.duenho = duenho;
        this.impuesto = impuesto;
    }

    /**
     * Crea una casilla de impuesto. Su tipo queda fijado como Impuesto.
     *
     * @param nombre   Nombre de la casilla.
     * @param posicion Posición en el tablero, entre 1 y 40.
     * @param impuesto Cantidad que paga quien cae en la casilla.
     * @param duenho   Dueño de la casilla (la banca).
     */
    public Casilla(String nombre, int posicion, float impuesto, Jugador duenho) {
        this.nombre = nombre;
        this.posicion = posicion;
        this.impuesto = impuesto;
        this.duenho = duenho;
        this.tipo = "Impuesto";
    }

    /**
     * Crea una casilla que no se puede comprar ni cobra impuesto: Suerte, Caja de
     * Comunidad y las especiales (Salida, Cárcel, Parking e IrCarcel).
     *
     * @param nombre   Nombre de la casilla.
     * @param tipo     Tipo de la casilla: Suerte, Comunidad o Especial.
     * @param posicion Posición en el tablero, entre 1 y 40.
     * @param duenho   Dueño de la casilla (la banca).
     */
    public Casilla(String nombre, String tipo, int posicion, Jugador duenho) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.posicion = posicion;
        this.duenho = duenho;
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////// MÉTODOS //////////////////////////////////////////////////////////

    /**
     * Aplica la acción de la casilla al jugador que ha caído en ella: pagar el
     * alquiler si es una propiedad de otro jugador, pagar el impuesto al bote del
     * Parking, cobrar el bote, ir a la Cárcel...
     *
     * @param actual  Jugador cuyo avatar ha caído en la casilla.
     * @param banca   La banca, para ciertas comprobaciones.
     * @param tirada  Valor de la tirada, para calcular el alquiler de los servicios.
     * @param parking Casilla del Parking, donde se deposita lo que se paga en
     *                impuestos.
     * @return {@code true} si el jugador es solvente (ha podido pagar sus deudas);
     *         {@code false} si no.
     */
    public boolean evaluarCasilla(Jugador actual, Jugador banca, int tirada, Casilla parking) {
        float alquiler;
        switch (tipo) {
            case "Solar":
                // Sin dueño (es de la banca) o es suya: no paga nada; o está hipotecada
                if (duenho == banca || duenho == actual || this.isHipotecada() == true) {
                    return true;
                }
                alquiler = impuesto;
                if (grupo.esDuenhoGrupo(duenho)) {
                    alquiler = impuesto * 2;
                }
                // ¿Puede pagar?
                if (actual.getFortuna() < alquiler) {
                    System.out.println(Valor.YELLOW + actual.getNombre() + " no puede pagar " + (long) alquiler
                            + "€. Debe hipotecar alguna propiedad o declararse en bancarrota." + Valor.RESET);
                    return false;
                }
                //Paga
                actual.sumarFortuna(-alquiler);
                actual.sumarGastos(alquiler);
                duenho.sumarFortuna(alquiler);
                System.out.println("Se han pagado " + (long) alquiler + "€ de alquiler a "
                        + duenho.getNombre() + ".");
                return true;
            case "Transporte":
                if (duenho == banca || duenho == actual || this.isHipotecada() == true) {
                    return true;
                }
                alquiler = 250000;
                if (actual.getFortuna() < alquiler) {
                    System.out.println(Valor.YELLOW + actual.getNombre() + " no puede pagar " + (long) alquiler +
                            "€. Debe hipotecar alguna propiedad o declararse en bancarrota." + Valor.RESET);
                    return false;
                }
                actual.sumarFortuna(-alquiler);
                actual.sumarGastos(alquiler);
                duenho.sumarFortuna(alquiler);
                System.out.println("Se han pagado " + (long) alquiler + "€ de alquiler a " + duenho.getNombre() + ".");
                return true;
            case "Servicios":
                if (duenho == banca || duenho == actual || this.isHipotecada() == true) {
                    return true;
                }
                alquiler = 4 * tirada * 50000;
                if (actual.getFortuna() < alquiler) {
                    System.out.println(Valor.YELLOW + actual.getNombre() + " no puede pagar " + (long) alquiler +
                            "€. Debe hipotecar alguna propiedad o declararse en bancarrota." + Valor.RESET);
                    return false;
                }
                actual.sumarFortuna(-alquiler);
                actual.sumarGastos(alquiler);
                duenho.sumarFortuna(alquiler);
                System.out.println("Se han pagado " + (long) alquiler + "€ de alquiler a " + duenho.getNombre() + ".");
                return true;
            case "Impuesto":
                if (actual.getFortuna() < impuesto) {
                    System.out.println();
                    System.out.println(Valor.YELLOW + actual.getNombre() + " no puede pagar el impuesto. " +
                            "Debe hipotecar alguna propiedad o declararse en bancarrota." + Valor.RESET);
                    return false;
                }
                actual.sumarFortuna(-impuesto);
                actual.sumarGastos(impuesto);
                parking.sumarValor(impuesto);
                System.out.println("El jugador paga " + (long) impuesto + "€ que se depositan en el Parking.");
                return true;
            case "especial":
                if (nombre.equals("Parking")) {
                    actual.sumarFortuna(valor);
                    System.out.println("El jugador " + actual.getNombre() + " recibe " + (long) valor + "€.");
                    valor = 0;
                }
                return true;
            default:   // Suerte y Comunidad
                return true;
        }
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
        if (!this.tipo.equals("Solar") && !this.tipo.equals("Servicios") && !this.tipo.equals("Transporte")) {
            System.out.println(Valor.YELLOW + "No puedes comprar este tipo de casilla." + Valor.RESET);
            return;
        }

        solicitante.sumarFortuna(-this.valor);
        solicitante.sumarGastos(this.valor);
        banca.sumarFortuna(this.valor);

        this.duenho = solicitante;
        solicitante.anhadirPropiedad(this);

        System.out.println("El jugador " + solicitante.getNombre() + " compra la casilla " + this.nombre + " por " + this.valor + "€.");
    }

    /**
     * Suma una cantidad al valor de la casilla. Sirve para acumular el bote en el
     * Parking y para revalorizar los solares que no se compran tras cuatro vueltas
     * de todos los jugadores.
     *
     * @param suma Cantidad que se añade al valor de la casilla.
     */
    public void sumarValor (float suma){
        this.valor += suma;
    }

    /**
     * Devuelve la descripción de la casilla para el comando {@code describir}, con
     * un formato distinto según su tipo. Para Suerte, Caja de Comunidad e IrCarcel
     * devuelve un aviso de que no se pueden describir.
     *
     * @return Texto con la información de la casilla.
     */
    public String infoCasilla() {
        StringBuilder info = new StringBuilder("{\n" + "  nombre: " + this.nombre + "\n" + "  tipo: " + this.tipo + "\n");
        if (this.duenho != null) {
            info.append("  propietario: ").append(this.duenho.getNombre()).append("\n").append("  valor: ").append(this.valor).
                    append("€\n");
        }
        if (this.getNombre().equals("Caja") || this.getNombre().equals("IrCarcel") || this.getNombre().equals("Suerte")) {
            info = new StringBuilder(Valor.YELLOW + "No se puede describir la casilla " + this.getNombre() + "\n" + Valor.RESET);
            return info.toString();
        }
        if (this.tipo.equals("Impuesto")) {
            info = new StringBuilder("{\n" + "  tipo: " + this.tipo + "\n" + "  a pagar: " + Valor.IMPUESTOS + "\n" + "}");
            return info.toString();
        }
        if (this.tipo.equals("Transporte")) {
            info.append("  alquiler: ").append(Valor.TRANSPORTE_ALQUILER).append("\n");
            return info.toString();
        }
        if (this.getNombre().equals("Parking")) {
            info = new StringBuilder(("  bote: " + this.valor + "\n" + "  jugadores: "));
            StringJoiner nombres = new StringJoiner(",","[","]"); nombres.setEmptyValue("-");
            for (Avatar av : avatares) {
                nombres.add(av.getJugador().getNombre());
            }
            info.append(nombres).append("\n").append("}");
            return info.toString();
        }
        if (this.getNombre().equals("Salida")) {
            info = new StringBuilder("  cobro: " + Valor.SUMA_VUELTA + "\n" + "  jugadores: ");
            StringJoiner nombres = new StringJoiner(",","[","]"); nombres.setEmptyValue("-");
            for (Avatar av : avatares) {
                nombres.add(av.getJugador().getNombre());
            }
            info.append(nombres).append("\n").append("}");
            return info.toString();
        }
        if (this.tipo.equals("Servicios")) {
            info = new StringBuilder("{\n" + "  tipo: " + this.tipo + "\n" + "  propietario: " + this.duenho.getNombre() + "\n" +
                    "  valor: " + this.valor + "\n" + "  factor de servicio: " + Valor.FACTOR_SERVICIO + "\n}");
            return info.toString();
        }
        if (this.getNombre().equals("Carcel")) {
            info = new StringBuilder("  salir: " + Valor.CARCEL_SALIR + "\n" + "  jugadores: ");
            StringJoiner nombres = new StringJoiner(",","[","]"); nombres.setEmptyValue("-");
            for (Avatar av : avatares) {
                Jugador j = av.getJugador();
                nombres.add("[" + j.getNombre() + "," + j.getTiradasCarcel() + "]");
            }
            info.append(nombres).append("\n").append("}");
            return info.toString();
        }
        info.append("  grupo: ").append(this.grupo.getNombreGrupo()).append("\n");
        info.append("  alquiler: ").append(Valor.SOLAR_ALQUILERES[this.posicion]).append("€\n");
        info.append("  valor hotel: ").append(Valor.SOLAR_PRECIOS_HOTEL[this.posicion]).append("€\n");
        info.append("  valor casa: ").append(Valor.SOLAR_PRECIOS_CASA[this.posicion]).append("€\n");
        info.append("  valor piscina: ").append(Valor.SOLAR_PRECIOS_PISCINA[this.posicion]).append("€\n");
        info.append("  valor pista de deporte: ").append(Valor.SOLAR_PRECIOS_PISTADEPORTE[this.posicion]).append("€\n");
        info.append("  alquiler casa: ").append(Valor.SOLAR_ALQUILERES_CASA[this.posicion]).append("€\n");
        info.append("  alquiler hotel: ").append((long) Valor.SOLAR_ALQUILERES_HOTEL[this.posicion]).append("€\n");
        info.append("  alquiler piscina: ").append(Valor.SOLAR_ALQUILERES_PISCINA[this.posicion]).append("€\n");
        info.append("  alquiler pista de deporte: ").append(Valor.SOLAR_ALQUILERES_PISTADEPORTE[this.posicion])
                .append("€\n").append("}");
        return info.toString();
    }

    /**
     * Devuelve la información de la casilla para el comando {@code listar enventa}:
     * su nombre, su tipo y su valor.
     *
     * @return Texto con la información de la casilla en venta.
     */
    public String casEnVenta () {
        String info = "{\n";
        info += "  nombre: " + this.nombre + ",\n";
        info += "  tipo: " + this.tipo + ",\n";
        info += "  valor: " + (long) this.valor + "\n";
        info += "}";
        return info;
    }

    /**
     * Devuelve la representación de la casilla como texto.
     *
     * @return Texto que representa la casilla.
     */
    @Override
    public String toString () {
        StringBuilder res = new StringBuilder(this.nombre);
        if (this.avatares != null && !this.avatares.isEmpty()) {
            res.append(" &");
            for (Avatar a : this.avatares) {
                res.append(a.getId());
            }
        }
        return res.toString();
    }


    // ===== Getters y setters =====

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public Jugador getDuenho() {
        return duenho;
    }

    public void setDuenho(Jugador duenho) {
        this.duenho = duenho;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public float getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(float impuesto) {
        this.impuesto = impuesto;
    }

    public float getHipoteca() {
        return hipoteca;
    }

    public void setHipoteca(float hipoteca) {
        this.hipoteca = hipoteca;
    }

    public ArrayList<Avatar> getAvatares() {
        return avatares;
    }

    public void setAvatares(ArrayList<Avatar> avatares) {
        this.avatares = avatares;
    }

    public void setCasa(int numCasas) {
        this.casas += numCasas;
    }

    public int getCasas() {
        return this.casas;
    }

    public void setHotel() {
        this.casas = 0;  // Se cambian las 4 casas por el hotel.
        this.hotel = 1;
    }

    public int getHotel() {
        return this.hotel;
    }

    public void setPiscina() {
        this.piscina = 1;
    }

    public int getPiscina() {
        return this.piscina;
    }

    public void setPistadeporte() {
        this.pistadeporte = 1;
    }

    public int getPistadeporte() {
        return this.pistadeporte;
    }

    public boolean isHipotecada() {
        return this.hipotecada;
    }

    public void setHipotecada(boolean hipotecada) {
        this.hipotecada = hipotecada;
    }

    public int getNumEdificios() {
        return this.casas + this.hotel + this.piscina + this.pistadeporte;
    }

    // Valor que recibe el jugador por hipotecarla
    public float getValorHipoteca() {
        return this.valor / 2.0f;
    }

    public boolean tieneEdificios() {
        return getNumEdificios() > 0;
    }

    public void anhadirAvatar(Avatar av) {
        avatares.add(av);
    }

    public void eliminarAvatar(Avatar av) {
        avatares.remove(av);
    }
}
