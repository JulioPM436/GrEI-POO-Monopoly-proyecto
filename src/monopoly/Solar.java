package monopoly;
import partida.*;
import java.util.ArrayList;

public class Solar extends Propiedad{
    private float impuesto; //Cantidad a pagar por caer en la casilla: el alquiler en solares/servicios/transportes o impuestos.
    private float hipoteca; //Valor otorgado por hipotecar una casilla.
    private boolean hipotecada;



    // Listas con los IDs de los edificios (se crean vacías en todas las casillas)
    private ArrayList<String> casas = new ArrayList<>();
    private ArrayList<String> hotel = new ArrayList<>();
    private ArrayList<String> piscina = new ArrayList<>();
    private ArrayList<String> pistadeporte = new ArrayList<>();


    /**
     * Crea una casilla que se puede comprar guardando además su alquiler.
     * Se usa para los solares.
     *
     * @param nombre   Nombre de la casilla.
     * @param posicion Posición en el tablero, entre 1 y 40.
     * @param valor    Precio de compra.
     * @param duenho   Dueño inicial (la banca).
     * @param impuesto Alquiler que paga quien cae en la casilla.
     */
    public Solar(String nombre, int posicion, float valor, Jugador duenho, float impuesto) {
        super(nombre, posicion, valor, duenho);
        this.impuesto = impuesto;
        this.hipotecada = false;
        this.hipoteca = valor / 2.0f;
    }




    public boolean isHipotecada() {
        return this.hipotecada;
    }
    public void setHipoteca(float hipoteca) {
        this.hipoteca = hipoteca;
    }
    public float getHipoteca() {
        return hipoteca;
    }
    public void setHipotecada(boolean hipotecada) {
        this.hipotecada = hipotecada;
    }
    // Valor que recibe el jugador por hipotecarla
    public float getValorHipoteca() {
        return this.getValor() / 2.0f;
    }


    @Override public boolean alquiler(){
        // Sin dueño (es de la banca) o es suya: no paga nada; o está hipotecada
        float alquiler = impuesto
        if (getDuenho() == banca || getDuenho() == actual || this.isHipotecada()) {
            return true;
        }
        alquiler = impuesto;
        if (getGrupo().esDuenhoGrupo(getDuenho()) && this.getNumEdificios() == 0) {
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
        actual.sumarPagoDeAlquileres(alquiler);//Estadística
        actual.sumarGastos(alquiler);
        getDuenho().sumarFortuna(alquiler);
        getDuenho().sumarCobroDeAlquileres(alquiler);
        this.sumarDineroGenerado(alquiler);
        System.out.println("Se han pagado " + (long) alquiler + "€ de alquiler a "
                + getDuenho().getNombre() + ".");
        return true;
    }

    public void setCasa(String ID) {
        this.casas.add(ID);
    }

    public int getCasas() {

        return this.casas.size();
    }

    public void venderCasas(int i){
        if (i < 1)
            throw new IllegalArgumentException(Valor.RED + "venderCasas: i debe ser al menos 1 y es " + i + Valor.RESET);
        for (int j = 0; j < i && !this.casas.isEmpty(); j++)
            this.casas.remove(0);
    }

    public String getIDCasa(int i) {
        if (!this.casas.isEmpty())
            return this.casas.get(i);
        else
            return null;
    }

    public void setHotel(String ID) {
        this.casas.clear();  // Se cambian las 4 casas por el hotel.
        this.hotel.add(ID);
    }

    public void venderHotel(){
        if (!this.hotel.isEmpty())
            this.hotel.remove(0);
    }

    public int getHotel() {
        return this.hotel.size();
    }
    public String getIDHotel() {
        if (!this.hotel.isEmpty())
            return this.hotel.get(0);
        else
            return null;
    }

    public void setPiscina(String ID) {
        this.piscina.add(ID);
    }

    public void venderPiscina(){
        if (!this.piscina.isEmpty())
            this.piscina.remove(0);
    }

    public int getPiscina() {
        return this.piscina.size();
    }

    public String getIDpiscina() {
        if (!this.piscina.isEmpty())
            return this.piscina.get(0);
        else
            return null;
    }

    public void setPistadeporte(String ID) {
        this.pistadeporte.add(ID);
    }

    public void venderPistadeporte(){
        if (!this.pistadeporte.isEmpty())
            this.pistadeporte.remove(0);
    }

    public int getPistadeporte() {
        return this.pistadeporte.size();
    }

    public String getIDpistadeporte() {
        return this.pistadeporte.get(0);
    }

    public int getNumEdificios() {
        return getCasas() + getHotel() + getPiscina() + getPistadeporte();
    }

    public boolean tieneEdificios() {
        return getNumEdificios() > 0;
    }


    public void actualizarAlquiler() {
        if (getNumEdificios() > 0)
            this.impuesto = getCasas()*Valor.SOLAR_ALQUILERES_CASA[this.getPosicion()]
                    +getHotel()*Valor.SOLAR_ALQUILERES_HOTEL[this.getPosicion()]+getPiscina()*Valor.SOLAR_ALQUILERES_PISCINA[this.getPosicion()]
                    +getPistadeporte()*Valor.SOLAR_ALQUILERES_PISTADEPORTE[this.getPosicion()];
        else
            this.impuesto = Valor.SOLAR_ALQUILERES[this.getPosicion()];
    }

    public float getValorEdificios(){
        float valor_total = 0;
        valor_total += this.getCasas()*Valor.SOLAR_PRECIOS_CASA[this.getPosicion()];
        valor_total += this.getHotel()*Valor.SOLAR_PRECIOS_HOTEL[this.getPosicion()];
        valor_total += this.getPiscina()*Valor.SOLAR_PRECIOS_PISCINA[this.getPosicion()];
        valor_total += this.getPistadeporte()*Valor.SOLAR_PRECIOS_PISTADEPORTE[this.getPosicion()];
        return valor_total;
    }
}
