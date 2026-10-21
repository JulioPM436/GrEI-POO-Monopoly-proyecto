package monopoly;

import partida.*;
import java.util.ArrayList;

/**
 * Grupo de solares del mismo color.
 * Guarda sus casillas y su color, y permite saber si un jugador posee el grupo
 * completo, que es el requisito para edificar en él.
 */
class Grupo {

    //Atributos:
    private ArrayList<Casilla> miembros; //Casillas miembros del grupo.
    private String colorGrupo; //Color del grupo
    private int numCasillas; //Número de casillas del grupo.ç

    /**
     * Crea un grupo vacío, sin casillas ni color.
     */
    public Grupo() {
        this.miembros = new ArrayList<>();
        this.colorGrupo = "";
        this.numCasillas = 0;
    }

    /**
     * Crea un grupo de dos casillas y enlaza cada una con el grupo.
     *
     * @param cas1       Primera casilla del grupo.
     * @param cas2       Segunda casilla del grupo.
     * @param colorGrupo Color del grupo.
     */
    public Grupo(Casilla cas1, Casilla cas2, String colorGrupo) {
        this.miembros = new ArrayList<>();
        this.miembros.add(cas1);
        this.miembros.add(cas2);
        this.colorGrupo = colorGrupo;
        this.numCasillas = 2;
        cas1.setGrupo(this);
        cas2.setGrupo(this);
    }

    /**
     * Crea un grupo de tres casillas y enlaza cada una con el grupo.
     *
     * @param cas1       Primera casilla del grupo.
     * @param cas2       Segunda casilla del grupo.
     * @param cas3       Tercera casilla del grupo.
     * @param colorGrupo Color del grupo.
     */
    public Grupo(Casilla cas1, Casilla cas2, Casilla cas3, String colorGrupo) {
        this.miembros = new ArrayList<>();
        this.miembros.add(cas1);
        this.miembros.add(cas2);
        this.miembros.add(cas3);
        this.colorGrupo = colorGrupo;
        this.numCasillas = 3;

        cas1.setGrupo(this);
        cas2.setGrupo(this);
        cas3.setGrupo(this);
    }

    /**
     * Añade una casilla al grupo y aumenta en uno su número de casillas.
     *
     * @param miembro Casilla que se añade al grupo.
     */
    public void anhadirCasilla(Casilla miembro) {
        if (this.miembros == null) {
            this.miembros = new ArrayList<>();
        }
        this.miembros.add(miembro);
        this.numCasillas += 1;
    }

    /**
     * Comprueba si un jugador es dueño de todas las casillas del grupo, que es el
     * requisito para poder edificar en ellas.
     *
     * @param jugador Jugador que se quiere comprobar.
     * @return {@code true} si es dueño de todas las casillas del grupo;
     *         {@code false} en otro caso.
     */
    public boolean esDuenhoGrupo(Jugador jugador) {
        for (Casilla c : miembros) {
            if (c.getDuenho() != jugador) {
                return false;
            }
        }
        return true;
    }

    // Getters y setters.
    public String getColorGrupo() {
        return colorGrupo;
    }

    public void setColorGrupo(String colorGrupo) {
        this.colorGrupo = colorGrupo;
    }

    public ArrayList<Casilla> getMiembros() {
        return miembros;
    }

    public int getNumCasillas() {
        return numCasillas;
    }
}