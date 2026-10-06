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
        this.grupos.put("Rojo", new Grupo(norte.get(1), norte.get(3), norte.get(4), Valor.RED));
        this.grupos.put("Blanco", new Grupo(norte.get(6), norte.get(7), norte.get(9), Valor.WHITE));

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

        this.grupos.put("Negro", new Grupo(sur.get(1), sur.get(3), Valor.BLACK));

        this.grupos.put("Cian", new Grupo(sur.get(6), sur.get(8), sur.get(9), Valor.CYAN));
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
        this.grupos.put("Morado", new Grupo(oeste.get(1), oeste.get(3), oeste.get(4), Valor.PURPLE));

        // Grupo 4: Amarillo (Solar9, Solar10 y Solar11)
        this.grupos.put("Amarillo", new Grupo(oeste.get(6), oeste.get(7), oeste.get(9), Valor.YELLOW));
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
        this.grupos.put("Verde", new Grupo(este.get(1), este.get(2), este.get(4), Valor.GREEN));

        this.grupos.put("Azul", new Grupo(este.get(7), este.get(9), Valor.BLUE));
    }

    //Para imprimir el tablero, modificamos el méŧodo toString().
    @Override
    public String toString() {
        String taboleiro = "";
        ArrayList<Casilla> sur = this.posiciones.get(0);
        ArrayList<Casilla> oeste = this.posiciones.get(1);
        ArrayList<Casilla> norte = this.posiciones.get(2);
        ArrayList<Casilla> este = this.posiciones.get(3);

        String bordeCasilla = "|-----------";
        String lineaCompleta = "";
        for (int i = 0; i < 11; i++) {
            lineaCompleta += bordeCasilla;
        }
        lineaCompleta += "|\n";

        String huecoCentral = "";
        for (int i = 0; i < 8; i++) {
            huecoCentral += "            ";
            // 12 espacios por cada casilla intermedia
        }huecoCentral += "          ";


        // LADO NORTE (Fila superior)
        taboleiro += lineaCompleta;
        for (int i = 0; i < norte.size(); i++) {
            taboleiro += "| " + casillaespacio(norte.get(i));
        }
        taboleiro += "| " + casillaespacio(este.get(0)) + "|\n";
        taboleiro += lineaCompleta;


        // FILAS INTERMEDIAS (Laterales Oeste y Este)
        for (int i = 1; i < 10; i++) {
            taboleiro += "| " + casillaespacio(oeste.get(10 - i)) + "| " + huecoCentral+ "| " + casillaespacio(este.get(i)) + "|\n";
            if(i!=9){
                taboleiro += "|-----------| " + huecoCentral + "|-----------|\n";
            }
        }

        // LADO SUR (Fila inferior)
        taboleiro += lineaCompleta;

        taboleiro += "| " + casillaespacio(oeste.get(0)) ;

        for (int i = sur.size() - 1; i >= 0; i--) {
            taboleiro += "| " + casillaespacio(sur.get(i));
        }
        taboleiro += "|\n";
        taboleiro += lineaCompleta;

        return taboleiro;
    }

    // Método auxiliar para que todas las casillas ocupen exactamente 10 caracteres
    private String casillaespacio (Casilla c) {
        String texto = c.toString();
        while (texto.length() < 10) {
            texto += " ";
        }
        if (c.getGrupo() != null) {
            return c.getGrupo().getColorGrupo() + texto + Valor.RESET;
        }
        return texto;

    }
    
    //Méŧodo usado para buscar la casilla con el nombre pasado como argumento:
    //public Casilla encontrar_casilla(String nombre){
    //}
}
