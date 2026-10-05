package monopoly;

import partida.*;
import java.util.ArrayList;
import java.util.HashMap;


public class Tablero {
    //Atributos.
    private ArrayList<ArrayList<Casilla>> posiciones; //Posiciones del tablero: se define como un arraylist de arraylists de casillas (uno por cada lado del tablero).
    private HashMap<String, Grupo> grupos; //Grupos del tablero, almacenados como un HashMap con clave String (será el color del grupo).
    private Jugador banca; //Un jugador que será la banca.

    //Constructor: únicamente le pasamos el jugador banca (que se creará desde el menú).
    public Tablero(Jugador banca)
    {
        this.banca = banca;
        this.posiciones = new ArrayList<>();
        this.grupos = new HashMap<>();
        this.generarCasillas();;
    }

    
    //Méŧodo para crear todas las casillas del tablero. Formado a su vez por cuatro métodos (1/lado).
    private void generarCasillas() {
        this.insertarLadoSur();
        this.insertarLadoOeste();
        this.insertarLadoNorte();
        this.insertarLadoEste();
    }
    
    //Méŧodo para insertar las casillas del lado norte.
    private void insertarLadoNorte() {
        ArrayList<Casilla> norte = new ArrayList<>();

        norte.add(new Casilla("Parking", "especial", 21, this.banca));
        norte.add(new Casilla("Solar12", "Solar", 22, 2200000f, this.banca));
        norte.add(new Casilla("Suerte", "Suerte", 23, this.banca));
        norte.add(new Casilla("Solar13", "Solar", 24, 2200000f, this.banca));
        norte.add(new Casilla("Solar14", "Solar", 25, 2400000f, this.banca));
        norte.add(new Casilla("Trans3", "Transporte", 26, 500000f, this.banca));
        norte.add(new Casilla("Solar15", "Solar", 27, 2600000f, this.banca));
        norte.add(new Casilla("Solar16", "Solar", 28, 2600000f, this.banca));
        norte.add(new Casilla("Serv2", "Servicios", 29, 500000f, this.banca));
        norte.add(new Casilla("Solar17", "Solar", 30, 2800000f, this.banca));

        this.posiciones.add(norte);

    }

    //Méŧodo para insertar las casillas del lado sur.
    private void insertarLadoSur() {
        ArrayList<Casilla> sur = new ArrayList<>();

        sur.add(new Casilla("Salida", "especial", 1, this.banca));
        sur.add(new Casilla("Solar1", "Solar", 2, 600000f, this.banca));
        sur.add(new Casilla("Caja", "Comunidad", 3, this.banca));
        sur.add(new Casilla("Solar2", "Solar", 4, 600000f, this.banca));
        sur.add(new Casilla("Imp1", 5, 2000000f, this.banca));
        sur.add(new Casilla("Trans1", "Transporte", 6, 500000f, this.banca));
        sur.add(new Casilla("Solar3", "Solar", 7, 1000000f, this.banca));
        sur.add(new Casilla("Suerte", "Suerte", 8, this.banca));
        sur.add(new Casilla("Solar4", "Solar", 9, 1000000f, this.banca));
        sur.add(new Casilla("Solar5", "Solar", 10, 1200000f, this.banca));

        this.posiciones.add(sur);
    }

    //Méŧodo que inserta casillas del lado oeste.
    private void insertarLadoOeste() {
        ArrayList<Casilla> oeste = new ArrayList<>();

        oeste.add(new Casilla("Cárcel", "especial", 11, this.banca));
        oeste.add(new Casilla("Solar6", "Solar", 12, 1400000f, this.banca));
        oeste.add(new Casilla("Serv1", "Servicios", 13, 500000f, this.banca));
        oeste.add(new Casilla("Solar7", "Solar", 14, 1400000f, this.banca));
        oeste.add(new Casilla("Solar8", "Solar", 15, 1600000f, this.banca));
        oeste.add(new Casilla("Trans2", "Transporte", 16, 500000f, this.banca));
        oeste.add(new Casilla("Solar9", "Solar", 17, 1800000f, this.banca));
        oeste.add(new Casilla("Solar10", "Solar", 18, 1800000f, this.banca));
        oeste.add(new Casilla("Caja", "Comunidad", 19, this.banca));
        oeste.add(new Casilla("Solar11", "Solar", 20, 2200000f, this.banca));

        this.posiciones.add(oeste);
    }

    //Méŧodo que inserta las casillas del lado este.
    private void insertarLadoEste() {
        ArrayList<Casilla> este = new ArrayList<>();

        este.add(new Casilla("IrCarcel", "especial", 31, this.banca));
        este.add(new Casilla("Solar18", "Solar", 32, 3000000f, this.banca));
        este.add(new Casilla("Solar19", "Solar", 33, 3000000f, this.banca));
        este.add(new Casilla("Caja", "Comunidad", 34, this.banca));
        este.add(new Casilla("Solar20", "Solar", 35, 3200000f, this.banca));
        este.add(new Casilla("Trans4", "Transporte", 36, 500000f, this.banca));
        este.add(new Casilla("Suerte", "Suerte", 37, this.banca));
        este.add(new Casilla("Solar21", "Solar", 38, 3500000f, this.banca));
        este.add(new Casilla("Imp2", 39, 2000000f, this.banca));
        este.add(new Casilla("Solar22", "Solar", 40, 4000000f, this.banca));

        this.posiciones.add(este);
    }

    //Para imprimir el tablero, modificamos el méŧodo toString().
    @Override
    public String toString() {
        String taboleiro = "";

        ArrayList<Casilla> norte = this.posiciones.get(2);
        ArrayList<Casilla> este = this.posiciones.get(3);

        for (int i = 0; i < norte.size(); i++) {
            taboleiro += "| " + norte.get(i).toString() + " ";
        }
        taboleiro += "| " + este.get(0).toString() + " |\n";



        return taboleiro;



    }
    
    //Méŧodo usado para buscar la casilla con el nombre pasado como argumento:
    //public Casilla encontrar_casilla(String nombre){
    //}
}
