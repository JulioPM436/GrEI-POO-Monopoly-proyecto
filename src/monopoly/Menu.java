package monopoly;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
    private Jugador jugador_actual = jugadores.get(turno);

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
                break;

            case "jugador":
                turnoActual();
                break;

            case "listar":
                switch (partes[1]) {
                    case "jugadores":

                        break;
                    case "avatares":

                        break;
                    case "enventa":

                        break;
                }
                break;

            case "describir":
                switch (partes[1]) {
                    case "jugador":
                        descJugador(partes);
                        break;
                    case "avatar":
                        descAvatar(partes[1]);
                        break;
                    default:
                        descCasilla(partes[1]);
                        break;
                }
                break;

            case "lanzar":
                if (partes.length == 2) {
                    lanzarDados();
                } else {
                    ///////// TODO: Lanzar dados ese de con números 1 a 6 unidos de + como: 2+3? Como va eso???????
                    lanzarDados();
                }

            case "acabar":
                acabarTurno();
                break;

            case "salir":
                if (partes[1].equals("carcel")) {
                    salirCarcel();
                }
                break;

            case "comprar":
//                if (partes.length != 2) {
//                    System.out.println("Comando incorrecto. Formato: comprar <casilla>");
//                    break;
//                }
                //comprarCasilla(partes);
                break;

            case "ver":
                //verTablero();
                break;
            case "comandos":
                if (partes.length != 2) {
                    System.out.println("Comando erróneo. Formato: comandos <ruta>\n");
                    break;
                }
                // En un try-catch, la parte try es un bloque de código que puede fallar, como por ejemplo abrir
                //      un archivo y leerlo. "partes[1]" es la ruta al archivo. "new FileReader" abre ese archivo y crea
                //      un objeto que puede leerlo. "new BufferedReader" recibe ese objeto y lo envuelve, añadiendo un búfer
                //      para agilizar las lecturas, y el método readLine(), que junta caracteres hasta encontrar un cambio
                //      de línea, entonces devuelve la línea entera.
                // "BufferedReader lector" es la variable en la que se guarda el resultado, y así se puede usar "lector" para no
                //      interactuar con FileReader directamente.
                try (BufferedReader lector = new BufferedReader(new FileReader(partes[1]))) {
                    String linea;
                    // Mientras que lea líneas (no haya llegado al final del archivo),
                    while ((linea = lector.readLine()) != null) {
                        // a cada línea le saca los espacios en blanco que hayan al principio y al final,
                        //      dejando los del medio.
                        linea = linea.trim();
                        // Si es una línea vacía, empieza por # o por //, la salta, porque o la línea no tiene nada o
                        //      son comentarios.
                        if (linea.isEmpty() || linea.startsWith("#") || linea.startsWith("//")) {
                            continue;
                        }
                        // Si es un posible comando, lo imprime en pantalla para saber de qué es la salida.
                        System.out.println("$> " + linea);
                        // Llamada recursiva a analizarComando() para intentar ejecutar la línea.
                        analizarComando(linea);
                    } // Como se asume que lo de try puede fallar, se tiene el catch; un código que se lanza cuando falle
                    //      el try. Si el archivo no pudiera leerse, saltaría esa excepción y dejaría de ejecutar más código.
                    //   Imprime un mensaje de error.
                } catch (IOException e) {
                    System.out.println("ERROR: No se pudo leer el archivo: " + partes[1]);
                }

                break;

            case "salir_partida":
                System.exit(0);
                break;

            default:
                System.out.println("Comando erróneo.\n");
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
    private void lanzarDados(String[] partes) {
        if (tirado) {
            System.out.println("El jugador ya ha tirado los dados");
            return;
        }
        if(partes.length==3){
            //TERMINAR ESTO PARA FORZAR LOS DADOS
        }
        boolean sonDobles = false;
        //CARCEL/// COMPROBAR SI ESTÁ EN LA CARCEL Y SACA DOBLES O SI ES SU TERCER TURNO EN ELLA


        int tirada1 = dado1.hacerTirada();
        int tirada2 = dado2.hacerTirada();

        int suma = tirada1 + tirada2;
        System.out.println("Tirada: " + tirada1 + " y " + tirada2 + " (Total: " + suma + ")");
        sonDobles = (tirada1 == tirada2);


        //PRIMERA PARTE: SI EL JUGADOR ESTÁ EN LA CÁRCEL
        if(jugador_actual.isEnCarcel()==true) {
            if (sonDobles == true) {
                System.out.println("Son dobles, sales de la cárcel");
                jugador_actual.setEnCarcel(false);

            } else {
                jugador_actual.sumaTiradasCarcel(); //se usa esta función para sumar 1;
                if (jugador_actual.getTiradasCarcel() == 3) {
                    System.out.println("Tercer intento sin sacar dobles: pagas");
                    jugador_actual.setEnCarcel(false);
                    jugador_actual.setTiradasCarcel(0);
                    jugador_actual.getAvatar().moverAvatar(tablero.getPosiciones(), suma);
                } else {
                    System.out.println("No son dobles, lamentablemente te quedas en la cárcel");
                    tirado = true;
                }

            }
            return;
        }


        //SEGUNDA PARTE: TIRO NORMALITO
        if (sonDobles) {
            lanzamientos++;
            if (lanzamientos == 3) {
                System.out.println("Vas a la Cárcel.");
                jugador_actual.encarcelar(tablero.getPosiciones());

                lanzamientos = 0;
                tirado = true;
                return;
            }
            tirado = false;
        } else {
            lanzamientos = 0;
            tirado = true;
            jugador_actual.getAvatar().moverAvatar(tablero.getPosiciones(), suma);
            Casilla casillaActual = jugador_actual.getAvatar().getLugar();
            casillaActual.evaluarCasilla(jugador_actual, banca, suma);
            return;
        }


    }

    /*Método que ejecuta todas las acciones realizadas con el comando 'comprar nombre_casilla'.
    * Parámetro: cadena de caracteres con el nombre de la casilla.
     */
    private void comprar(String nombre) {

        if(!jugador_actual.getAvatar().getLugar().getNombre().equals(nombre)){
            System.out.println("No se puede comprar está casilla");
            return;
        }
        Casilla casilla_a_comprar = jugador_actual.getAvatar().getLugar();

        casilla_a_comprar.comprarCasilla(jugador_actual,banca);


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
            System.out.println("avatar: " + j.getAvatar().getId() + ",");
            System.out.println("fortuna: " + (long) j.getFortuna() + ",");
            //System.out.println("propiedades: " + j.getPropiedades());
            System.out.print("propiedades: [");
            for (Casilla c : j.getPropiedades()) {
                System.out.print(c.getNombre() + " ");
            }
            System.out.println("]");
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
