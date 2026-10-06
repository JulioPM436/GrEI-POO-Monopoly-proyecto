// Modificar  aaa

package monopoly;
import partida.*;
public class MonopolyETSE {

    public static void main(String[] args) {
        Jugador banca = new Jugador();

        Tablero tablero = new Tablero(banca);

        System.out.println(tablero);
    }
}
