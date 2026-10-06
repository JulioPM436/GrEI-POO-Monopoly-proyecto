package monopoly;

import java.util.ArrayList;
import partida.*;
import java.util.Scanner;
public class Menu {

    //Atributos
    private ArrayList<Jugador> jugadores; //Jugadores de la partida.
    private ArrayList<Avatar> avatares; //Avatares en la partida.
    private int turno = 0; //Índice correspondiente a la posición en el arrayList del jugador (y el avatar) que tienen el turno
    private int lanzamientos; //Variable para contar el número de lanzamientos de un jugador en un turno.
    private Tablero tablero; //Tablero en el que se juega.
    private Dado dado1; //Dos dados para lanzar y avanzar casillas.
    private Dado dado2;
    private Jugador banca; //El jugador banca.
    private boolean tirado; //Booleano para comprobar si el jugador que tiene el turno ha tirado o no.
    private boolean solvente; //Booleano para comprobar si el jugador que tiene el turno es solvente, es decir, si ha pagado sus deudas.

    //contructor
    public Menu(){
        this.banca = new Jugador();
        this.tablero = new Tablero(banca);
        this.dado1 = new Dado();
        this.dado2 = new Dado();
        this.lanzamientos = 0;
        this.tirado = false;
        this.solvente = true;
        ArrayList<Jugador> jugadores = new ArrayList<>();
        ArrayList<Avatar> avatares = new ArrayList<>();
        iniciarPartida();
    }

    // Método para inciar una partida: crea los jugadores y avatares.
    private void iniciarPartida() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.print("Introduce un comando: ");
            String linea = sc.nextLine();
            analizarComando(linea);
        }
    }
    
    /*Método que interpreta el comando introducido y toma la accion correspondiente.
    * Parámetro: cadena de caracteres (el comando).
    */
    private void analizarComando(String comando) {
        String[] partes = comando.trim().split("\\s+");
        switch (partes[0]) {
            case "crear":
                crearJugador(partes);
            case "jugador":

            case "describir jugador":
                descJugador(partes);

            case "listar":


            case "lanzar":


            case "acabar":


            case "salir":


            case "describir":


            case "comprar":


            case "ver":

            case "salir_partida":
                System.exit(0);

        }
    }



    /*Método que realiza las acciones asociadas al comando 'crear jugador'.
     * Formato: crear jugador <nombre> <avatar>
     */
    private void crearJugador(String[] partes) {
        // Se comprueba que el formato del comando sea correcto, esto es, tiene 4 argumentos.
        if (partes.length == 4){
            // Se busca cuál es la casilla de salida, todos los jugadores empiezan ahí.
            Casilla salida = tablero.encontrar_casilla("Salida");
            // Se crea el jugador como tal.
            Jugador j = new Jugador(partes[2],partes[3],salida,avatares);
            // Se guardan sus datos y su avatar en un arraylist para no perder su referencia.
            jugadores.add(j);
            avatares.add(j.getAvatar());
        }
        else {
            // Si no, se asume que el comando era erróneo y no hace nada.
            System.out.println("ERROR: crear jugador <nombre> <avatar>\n");
        }
    }

    /*Método que realiza las acciones asociadas al comando 'describir jugador'.
    * Parámetro: comando introducido
    * Formato: describir jugador <nombre>
     */
    private void descJugador(String[] partes) {
        // Si la cantidad de parámetros introducidos es menor que 2, el comando no se introdujo correctamente.
        /// TODO: SI SÓLO PROHIBE MENOR QUE DOS, PERMITIRÍA INFINITOS ARGUMENTOS. NO AFECTARÍAN AL COMANDO PERO NO ES ADECUADO???
        if (partes.length <= 2){
            System.out.println("No ha introducido el nombre del jugador\n");
            return;
        }
        // De otro modo, se busca al jugador que tenga ese nombre.
        Jugador j = buscarjugador(partes[2]);
        // Si la función devuelve nulo, no hay jugador con ese nombre.
        if (j == null) {
            System.out.println("No existe el jugador\n");
            return;
        }
        // Si existe, se imprimen por pantalla los atributos del jugador.
        System.out.println("nombre: "+ j.getNombre());
        System.out.println("avatar: "+ j.getAvatar().getIdAvatar());
        System.out.println("fortuna: "+ j.getFortuna());
        System.out.println("propiedades: ");
        // Se itera sobre las propiedades del jugador imprimiendo su nombre.
        for(Casilla elemento: j.getPropiedades()){
            System.out.print("["+ elemento.getNombre() + "] ");
        }
        System.out.println();
    }

    // Método para buscar jugadores. Dada una string con el nombre del jugador, devuelve su objeto.
    private Jugador buscarjugador(String nombre_jugador){
        // Se itera sobre la lista de jugadores.
        for(Jugador jugador: jugadores){
            // Si la string coincide con el nombre de alguno, devuelve ese objeto.
            if(jugador.getNombre().equals(nombre_jugador)){
                return jugador;
            }
        }
        // Si no hay jugador con ese nombre, devuelve NULL.
        return null;
    }

    /*Método que realiza las acciones asociadas al comando 'describir avatar'.
    * Parámetro: id del avatar a describir.
    * Formato:
    */
    private void descAvatar(String ID) {
        Avatar a = buscarAvatar(ID);
        if (a == null) {
            System.out.println("No existe el avatar\n");
            return;
        }
        System.out.println("tipo: "+ a.getTipo());
        System.out.println("lugar: "+ a.getLugar().getNombre());
        System.out.println("jugador: "+ a.getJugador().getNombre());

    }
    private Avatar buscarAvatar(String ID){
        for(Avatar avatar: avatares){
            if(avatar.getIdAvatar().equals(ID)){
                return avatar;
            }
        }
        return null;
    }

    /* Método que realiza las acciones asociadas al comando 'describir nombre_casilla'.
    * Parámetros: nombre de la casilla a describir.
    */
    private void descCasilla(String nombre) {
    }

    //Método que ejecuta todas las acciones relacionadas con el comando 'lanzar dados'.
    private void lanzarDados() {
        if(tirado==true){
            System.out.println("El jugador ya ha tirado los dados");
            return;
        }
        //CARCEL/// COMPROBAR SI ESTÁ EN LA CARCEL Y SACA DOBLES O SI ES SU TERCER TURNO EN ELLA
        int tirada1 = dado1.hacerTirada();
        int tirada2 = dado2.hacerTirada();

        int suma = tirada1 + tirada2;
        boolean sonDobles = (tirada1 == tirada2);

        moverAvatar(tablero.getPosiciones(),)



    }

    /*Método que ejecuta todas las acciones realizadas con el comando 'comprar nombre_casilla'.
    * Parámetro: cadena de caracteres con el nombre de la casilla.
     */
    private void comprar(String nombre) {
    }

    //Método que ejecuta todas las acciones relacionadas con el comando 'salir carcel'. 
    private void salirCarcel() {

    }

    // Método que realiza las acciones asociadas al comando 'listar enventa'.
    private void listarVenta() {
    }

    // Método que realiza las acciones asociadas al comando 'listar jugadores'.
    private void listarJugadores() {
        if (jugadores.isEmpty()){
            System.out.println("No hay jugadores en la partida.");
            return;
        }
        for (int i = 0; i < jugadores.size(); i++){
            Jugador j = jugadores.get(i);

            System.out.println("nombre: " + j.getNombre() + ",");
            //System.out.println("avatar: " + j.getAvatar().getId() + ",");
            System.out.println("fortuna: " + (long) j.getFortuna() + ",");
            System.out.println("propiedades: " + j.getPropiedades());
            System.out.println("hipotecas: -");
            System.out.println("edificios: -");

    }
    }

    // Método que realiza las acciones asociadas al comando 'listar avatares'.
    private void listarAvatares() {
    }
    //Método que indica de quién es el turno
    private void turnoActual(){
        if (this.jugadores.isEmpty()) {
            System.out.println("Aún no hay jugadores en la partida.\n");
            return;
        }
        System.out.println("El turno actual es de: " + jugadores.get(turno).getNombre());
    }
    // Método que realiza las acciones asociadas al comando 'acabar turno'.
    private void acabarTurno(){


    }

}
