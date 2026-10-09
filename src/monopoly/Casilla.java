package monopoly;

import partida.*;
import java.util.ArrayList;


public class Casilla {

    //IR HACIENDO GETTERS Y SETTERS DE LOS ATRIBUTOS PARA EMPEZAR

    //Atributos:
    private String nombre; //Nombre de la casilla
    private String tipo; //Tipo de casilla (Solar, Especial, Transporte, Servicios, Comunidad, Suerte y Impuesto).
    private float valor; //Valor de esa casilla (en la mayoría será valor de compra, en la casilla parking se usará como el bote).
    private int posicion; //Posición que ocupa la casilla en el tablero (entero entre 1 y 40).
    private Jugador duenho; //Dueño de la casilla (por defecto sería la banca).
    private Grupo grupo; //Grupo al que pertenece la casilla (si es solar).
    private float impuesto; //Cantidad a pagar por caer en la casilla: el alquiler en solares/servicios/transportes o impuestos.
    private float hipoteca; //Valor otorgado por hipotecar una casilla
    private ArrayList<Avatar> avatares = new ArrayList<>();//Avatares que están situados en la casilla.


    //Constructores:
    public Casilla() {
    }//Parámetros vacíos


    /*Constructor para casillas tipo Solar, Servicios o Transporte:
     * Parámetros: nombre casilla, tipo (debe ser solar, serv. o transporte), posición en el tablero, valor y dueño.
     */
    public Casilla(String nombre, String tipo, int posicion, float valor, Jugador duenho) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.posicion = posicion;
        this.valor = valor;
        this.duenho = duenho;
    }

    /*Constructor utilizado para inicializar las casillas de tipo IMPUESTOS.
     * Parámetros: nombre, posición en el tablero, impuesto establecido y dueño.
     */
    public Casilla(String nombre, int posicion, float impuesto, Jugador duenho) {
        this.nombre = nombre;
        this.posicion = posicion;
        this.impuesto = impuesto;
        this.duenho = duenho;
        this.tipo = "Impuesto";
    }

    /*Constructor utilizado para crear las otras casillas (Suerte, Caja de comunidad y Especiales):
     * Parámetros: nombre, tipo de la casilla (será uno de los que queda), posición en el tablero y dueño.
     */
    public Casilla(String nombre, String tipo, int posicion, Jugador duenho) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.posicion = posicion;
        this.duenho = duenho;
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

    //Método utilizado para añadir un avatar al array de avatares en casilla.
    public void anhadirAvatar(Avatar av) {
        avatares.add(av);
    }

    //Método utilizado para eliminar un avatar del array de avatares en casilla.
    public void eliminarAvatar(Avatar av) {
        avatares.remove(av);
    }

    /*Método para evaluar qué hacer en una casilla concreta. Parámetros:
     * - Jugador cuyo avatar está en esa casilla.
     * - La banca (para ciertas comprobaciones).
     * - El valor de la tirada: para determinar impuesto a pagar en casillas de servicios.
     * Valor devuelto: true en caso de ser solvente (es decir, de cumplir las deudas), y false
     * en caso de no cumplirlas.*/
    public boolean evaluarCasilla(Jugador actual, Jugador banca, int tirada) {
        float alquiler;
        switch (tipo) {

            case "Solar":

                // Sin dueño (es de la banca) o es suya: no paga nada
                if (duenho == banca || duenho == actual) {
                    return true;
                }

                alquiler = impuesto;
                if (grupo.esDuenhoGrupo(duenho)) {
                    alquiler = impuesto * 2;
                }

                // ¿Puede pagar?
                if (actual.getFortuna() < alquiler) {
                    System.out.println(actual.getNombre() + " no puede pagar " + (long) alquiler
                            + "€. Debe hipotecar alguna propiedad o declararse en bancarrota.");
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
                if (duenho == banca || duenho == actual) {
                    return true;
                }
                alquiler = 250000;
                if (actual.getFortuna() < alquiler) {
                    System.out.println(actual.getNombre() + " no puede pagar " + (long) alquiler + "€. Debe hipotecar alguna propiedad o declararse en bancarrota.");
                    return false;
                }
                actual.sumarFortuna(-alquiler);
                actual.sumarGastos(alquiler);
                duenho.sumarFortuna(alquiler);
                System.out.println("Se han pagado " + (long) alquiler + "€ de alquiler a " + duenho.getNombre() + ".");
                return true;
            case "Servicios":
                if (duenho == banca || duenho == actual) {
                    return true;
                }
                alquiler = 4 * tirada * 50000;
                if (actual.getFortuna() < alquiler) {
                    System.out.println(actual.getNombre() + " no puede pagar " + (long) alquiler + "€. Debe hipotecar alguna propiedad o declararse en bancarrota.");
                    return false;
                }
                actual.sumarFortuna(-alquiler);
                actual.sumarGastos(alquiler);
                duenho.sumarFortuna(alquiler);
                System.out.println("Se han pagado " + (long) alquiler + "€ de alquiler a " + duenho.getNombre() + ".");
                return true;


            case "Impuesto":
                if (actual.getFortuna() < impuesto) {
                    System.out.println(actual.getNombre() + " no puede pagar el impuesto. " + "Debe hipotecar alguna propiedad o declararse en bancarrota.");
                    return false;
                }
                actual.sumarFortuna(-impuesto);
                actual.sumarGastos(impuesto);
                parking.sumarValor(impuesto);
                System.out.println("El jugador paga " + (long) impuesto + "€ que se depositan en el Parking.");
                return true;
            case "Especial":
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


        /*Método usado para comprar una casilla determinada. Parámetros:
         * - Jugador que solicita la compra de la casilla.
         * - Banca del monopoly (es el dueño de las casillas no compradas aún).*/
        public void comprarCasilla (Jugador solicitante, Jugador banca){
            if(this.getDuenho() != banca) {
                System.out.println("No puedes comprar esta casilla porque es de otro jugador");
                return;
            }else if(solicitante.getFortuna()<this.getValor()){
                System.out.println("No puedes comprar esta casilla porque no tienes saldo suficiente. |SALDO ACTUAL:" + solicitante.getFortuna());
                return;
            }if (!this.tipo.equals("Solar") && !this.tipo.equals("Servicio") && !this.tipo.equals("Transporte")) {
                System.out.println("No puedes comprar este tipo de casilla.");
                return;
            }

            solicitante.sumarFortuna(-this.valor);
            solicitante.sumarGastos(this.valor);
            banca.sumarFortuna(this.valor);

            this.duenho = solicitante;
            solicitante.anhadirPropiedad(this);

            System.out.println("El jugador " + solicitante.getNombre() + " compra la casilla " + this.nombre + " por " + this.valor + "€.");

        }

        /*Método para añadir valor a una casilla. Utilidad:
         * - Sumar valor a la casilla de parking.
         * - Sumar valor a las casillas de solar al no comprarlas tras cuatro vueltas de todos los jugadores.
         * Este método toma como argumento la cantidad a añadir del valor de la casilla.*/
        public void sumarValor ( float suma){
            this.valor += suma;
        }

        /*Método para mostrar información sobre una casilla.
         * Devuelve una cadena con información específica de cada tipo de casilla.*/
        public String infoCasilla() {
            String info = "nombre: " + this.nombre + "\n" + "tipo: " + this.tipo + "\n";

            if (this.duenho != null) {
                info += "propietario: " + this.duenho.getNombre() + "\n" +"valor: " + this.valor + "€\n";
            }

            return info;
        }

        /* Método para mostrar información de una casilla en venta.
         * Valor devuelto: texto con esa información.
         */
        public String casEnVenta () {

            String info = "{\n";
            info += "  nombre: " + this.nombre + ",\n";
            info += "  tipo: " + this.tipo + ",\n";
            info += "  valor: " + (long) this.valor + "\n";
            info += "}";
            return info;

        }


        @Override
        public String toString () {
            String res = this.nombre;
            if (this.avatares != null && !this.avatares.isEmpty()) {
                res += " &";
                for (Avatar a : this.avatares) {
                    res += a.getId();
                }
            }
            return res;
        }

    /*public String nombreSinAvatares() {
        return nombre;
    }

     */
    }


