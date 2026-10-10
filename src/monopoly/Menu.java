package monopoly;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import partida.*;

import java.util.Objects;
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
    private boolean solvente; //Booleano para comprobar si el jugador que tiene el turno ha pagado sus deudas.

    //contructor
    public Menu(){
        this.banca = new Jugador();
        this.tablero = new Tablero(banca);
        this.dado1 = new Dado();
        this.dado2 = new Dado();
        this.lanzamientos = 0;
        this.tirado = false;
        this.solvente = true;
        this.jugadores = new ArrayList<>();
        this.avatares = new ArrayList<>();
        iniciarPartida();
    }
    private Jugador getJugadorActual() {
        if (jugadores == null || jugadores.isEmpty()) {
            return null;
        }
        return jugadores.get(turno);
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
                if (partes.length == 1) {
                    System.out.println("ERROR: listar jugadores|avatares|enventa");
                    break;
                }
                switch (partes[1]) {
                    case "jugadores":
                        listarJugadores();
                        break;
                    case "avatares":
                        listarAvatares();
                        break;
                    case "enventa":
                        listarVenta();
                        break;
                    default:
                        System.out.println("ERROR: listar jugadores|avatares|enventa");
                }
                break;

            case "describir":
                if (partes.length == 1) {
                    System.out.println("ERROR: describir jugador <nombre>|avatar <id>");
                    break;
                }
                switch (partes[1]) {
                    case "jugador":
                        descJugador(partes);
                        break;
                    case "avatar":
                        descAvatar(partes[2]);
                        break;
                    default:
                        descCasilla(partes[1]);
                        break;
                }
                break;

            case "lanzar":
                lanzarDados(partes);
                break;

            case "acabar":
                acabarTurno();
                break;

            case "salir":
                if (partes[1].equals("carcel")) {
                    salirCarcel();
                }
                break;

            case "comprar":
                if (partes.length != 2) {
                    System.out.println("Comando incorrecto. Formato: comprar <casilla>");
                   break;
               }
                comprar(partes[1]);
                break;

            case "ver":
                System.out.println(tablero);
                break;
            case "comandos":
                procesarComandos(partes);
                break;

            case "exit":
                System.exit(0);
                break;

            case "clear":
                System.out.print("\033[H\033[2J\033[3J");
                System.out.flush();
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
            // Se impide crear más jugadores si ya hay 4.
            if (jugadores.size() < 4) {

                String nombre = partes[2];
                String tipo = partes[3];

                if (!tipo.equals("coche") && !tipo.equals("esfinge") && !tipo.equals("sombrero") && !tipo.equals("pelota")) {
                    System.out.println("ERROR: El avatar debe ser coche, esfinge, sombrero o pelota.");
                    return;  //comprueba que sea uno de los 4 posibles avatares
                }

                if (buscarjugador(nombre) != null) {
                    System.out.println("ERROR: Ya existe un jugador llamado " + nombre + ".");
                    return;
                }


                // Se busca cuál es la casilla de salida, todos los jugadores empiezan ahí.
                Casilla salida = tablero.encontrar_casilla("Salida");
                // Se crea el jugador como tal.
                Jugador j = new Jugador(partes[2], partes[3], salida, avatares);

                // se colocan los avatares en la casilla de salida
                salida.anhadirAvatar(j.getAvatar());
                // Se guardan sus datos y su avatar en un arraylist para no perder su referencia.
                jugadores.add(j);
                avatares.add(j.getAvatar());
            } else {
                System.out.println("ERROR: No se pueden crear más de 4 jugadores.");
            }
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
        System.out.println("{");
        System.out.println("  nombre: " + j.getNombre() + ",");
        System.out.println("  avatar: " + j.getAvatar().getId() + ",");
        System.out.println("  fortuna: " + (long) j.getFortuna() + ",");
        //System.out.println("propiedades: " + j.getPropiedades());
        System.out.print("  propiedades: [");
        for (Casilla c : j.getPropiedades()) {
            System.out.print(c.getNombre() + " ");
        }
        System.out.println("  ]");
        System.out.println("  hipotecas: -");
        System.out.println("  edificios: -");
        System.out.println("}");
    }

    // Método para buscar jugadores. Dada una string con el nombre del jugador, devuelve su objeto.
    private Jugador buscarjugador(String nombre_jugador) {
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

    // Método que devuelve un objeto avatar según su ID.
    private Avatar buscarAvatar(String ID) {
        for(Avatar avatar: avatares){
            if(avatar.getId().equals(ID)){
                return avatar;
            }
        }
        return null;
    }

    /* Método que realiza las acciones asociadas al comando 'describir nombre_casilla'.
    * Parámetros: nombre de la casilla a describir.
    */
    private void descCasilla(String nombre) {
        Casilla c = tablero.encontrar_casilla(nombre);
        if (c == null) {
            System.out.println("No existe ninguna casilla con el nombre: " + nombre);
            return;
        }
        System.out.println(c.infoCasilla());
    }

    //Método que ejecuta todas las acciones relacionadas con el comando 'lanzar dados'.
    // Ya incluye el modo de tirada fija.

    /// TODO:       Debería decir en texto las casillas que se mueve y a cuál va a parar, igual que en el guión?

    private void lanzarDados(String[] partes) {
        if (jugadores.isEmpty()) {
            System.out.println("Aún no hay jugadores en la partida.\n");
            return;
        }

        if (jugadores.size() < 2) {
            System.out.println("Se necesitan al menos 2 jugadores para empezar la partida.");
            return;
        }

        if (tirado) {
            System.out.println("El jugador ya ha tirado los dados");
            return;
        }
        int tirada1;
        int tirada2;
        if (partes.length==3) {
            tirada1 = partes[2].charAt(0) - '0';
            tirada2 = partes[2].charAt(2) - '0';
        } else {
            tirada1 = dado1.hacerTirada();
            tirada2 = dado2.hacerTirada();
        }
        boolean sonDobles;
        int suma = tirada1 + tirada2;
        System.out.println("Tirada: " + tirada1 + " y " + tirada2 + " (Total: " + suma + ")");
        sonDobles = (tirada1 == tirada2);

        Jugador actual = getJugadorActual();

        //PRIMERA PARTE: SI EL JUGADOR ESTÁ EN LA CÁRCEL
        if (actual.isEnCarcel()) {
            if (sonDobles) {
                System.out.println("Son dobles, sales de la cárcel.");
                actual.setEnCarcel(false);
                actual.setTiradasCarcel(0);
            } else {
                actual.sumaTiradasCarcel(); //se usa esta función para sumar 1;
                // Primer o segundo intento fallido: se queda en la cárcel.
                if (actual.getTiradasCarcel() < 3) {
                    System.out.println("No son dobles, lamentablemente te quedas en la cárcel");
                    tirado = true;
                    return;
                }
                // Tercer intento fallido: está obligado a pagar para salir.
                if (actual.getFortuna() < Valor.CARCEL_SALIR) {
                    System.out.println(actual.getNombre() + " no puede pagar " + (long) Valor.CARCEL_SALIR
                            + "€ para salir de la cárcel. Debe declararse en bancarrota.");
                    tirado = true;
                    return;
                }
                actual.sumarFortuna(-Valor.CARCEL_SALIR);
                actual.sumarGastos(Valor.CARCEL_SALIR);
                actual.setEnCarcel(false);
                actual.setTiradasCarcel(0);
                System.out.println("Tercer intento sin dobles: " + actual.getNombre() + " paga "
                        + (long) Valor.CARCEL_SALIR + "€ y sale de la cárcel.");
            }
            // Al salir de la cárcel avanza con esta tirada, pero no vuelve a tirar aunque sean dobles.
            tirado = true;
            moverYEvaluar(actual, suma);
            return;
        }
        //SEGUNDA PARTE: TIRO NORMALITO

        if (sonDobles) {
            lanzamientos++;
            if (lanzamientos == 3) {
                System.out.println("Vas a la Cárcel.");
                getJugadorActual().encarcelar(tablero.getPosiciones());
                lanzamientos = 0;
                tirado = true;
                return;
            }
            tirado = false;
        } else {
            lanzamientos = 0;
            tirado = true;
        }
        moverYEvaluar(getJugadorActual(), suma);

        // Si ha caído en IrCarcel ya no puede volver a tirar, aunque hubiera sacado dobles.
        if (getJugadorActual().isEnCarcel()) {
            tirado = true;
            lanzamientos = 0;
        }
    }

    // Mueve el avatar del jugador y realiza la acción de la casilla en la que cae.
    private void moverYEvaluar(Jugador actual, int suma) {
        actual.getAvatar().moverAvatar(tablero.getPosiciones(), suma);
        Casilla casilla = actual.getAvatar().getLugar();
        if (casilla.getNombre().equals("IrCarcel")) {
            System.out.println("Has caído en IrCarcel: el avatar se coloca en la casilla de Cárcel.");
            actual.encarcelar(tablero.getPosiciones());
        } else {
            casilla.evaluarCasilla(actual, banca, suma, tablero.encontrar_casilla("Parking"));
        }
    }

    /*Método que ejecuta todas las acciones realizadas con el comando 'comprar nombre_casilla'.
    * Parámetro: cadena de caracteres con el nombre de la casilla.
     */
    private void comprar(String nombre) {

        if (jugadores.isEmpty()) {
            System.out.println("Aún no hay jugadores en la partida.\n");
            return;
        }

        if (!Objects.requireNonNull(getJugadorActual()).getAvatar().getLugar().getNombre().equalsIgnoreCase(nombre)) {
            System.out.println("No se puede comprar está casilla");
            return;
        }

        getJugadorActual().getAvatar().getLugar().comprarCasilla(getJugadorActual(),banca);


    }

    //Método que ejecuta todas las acciones relacionadas con el comando 'salir carcel'. 
    private void salirCarcel() {
        if (jugadores.isEmpty()) {
            System.out.println("Aún no hay jugadores en la partida.\n");
            return;
        }

        if (!Objects.requireNonNull(getJugadorActual()).isEnCarcel()) {
            System.out.println("El jugador actual no está en la cárcel");
            return;
        }
        float fianza = 500000f;
        if (getJugadorActual().getFortuna() < fianza) {
            System.out.println("El jugador no tiene dinero suficiente para salir de la cárcel");
            return;
        }
        getJugadorActual().sumarFortuna(-fianza);
        getJugadorActual().sumarGastos(fianza);

        getJugadorActual().setEnCarcel(false);
        getJugadorActual().setTiradasCarcel(0);

        System.out.println(getJugadorActual().getNombre() + " paga " + fianza + "€ y sale de la cárcel. Ya puede lanzar los dados.");
    }

    // Método que realiza las acciones asociadas al comando 'listar enventa'.
    private void listarVenta() {
        boolean hayEnVenta = false;
        // Se recorren los cuatro lados del tablero y, dentro de cada uno, sus casillas.
        for (ArrayList<Casilla> lado : tablero.getPosiciones()) {
            for (Casilla c : lado) {
                // Solo se pueden comprar solares, transportes y servicios.
                String tipo = c.getTipo();
                boolean comprable = tipo.equals("Solar") || tipo.equals("Transporte") || tipo.equals("Servicios");
                // Está en venta si es comprable y todavía es de la banca.
                if (comprable && c.getDuenho() == banca) {
                    System.out.println(c.casEnVenta());
                    hayEnVenta = true;
                }
            }
        }
        if (!hayEnVenta) {
            System.out.println("No hay casillas en venta.");
        }
    }

    // Método que realiza las acciones asociadas al comando 'listar jugadores'.
    private void listarJugadores() {
        if (jugadores.isEmpty()){
            System.out.println("No hay jugadores en la partida.");
            return;
        }
        for (int i = 0; i < jugadores.size(); i++){
            Jugador j = jugadores.get(i);
            System.out.println("{");
            System.out.println("  nombre: " + j.getNombre() + ",");
            System.out.println("  avatar: " + j.getAvatar().getId() + ",");
            System.out.println("  fortuna: " + (long) j.getFortuna() + "€,");
            //System.out.println("propiedades: " + j.getPropiedades());
            System.out.print("  propiedades: [");
            for (Casilla c : j.getPropiedades()) {
                System.out.print(c.getNombre() + " ");
            }
            System.out.println("  ]");
            System.out.println("  hipotecas: -");
            System.out.println("  edificios: -");
            if (jugadores.size()-1 == i){
                System.out.println("}");
            } else {
                System.out.println("},");
            }

        }
    }

    // Método que realiza las acciones asociadas al comando 'listar avatares'.
    private void listarAvatares() {
        // Si no hay avatares, no hay nada que listar.
        if (avatares.isEmpty()) {
            System.out.println("No hay avatares en la partida.");
            return;
        }
        // Se recorre la lista de avatares imprimiendo los datos de cada uno.
        for (Avatar a : avatares) {
            System.out.println("{");
            System.out.println("  id: " + a.getId() + ",");
            System.out.println("  tipo: " + a.getTipo() + ",");
            System.out.println("  casilla: " + a.getLugar().getNombre() + ",");
            System.out.println("  jugador: " + a.getJugador().getNombre());
            System.out.println("}");
        }
    }

    //Método que indica de quién es el turno
    private void turnoActual() {
        if (this.jugadores.isEmpty()) {
            System.out.println("Aún no hay jugadores en la partida.\n");
            return;
        }
        System.out.println("{");
        System.out.println("  nombre: " + jugadores.get(turno).getNombre() + ",");
        System.out.println("  avatar: " + jugadores.get(turno).getAvatar().getId());
        System.out.println("}");
    }
    // Método que realiza las acciones asociadas al comando 'acabar turno'.
    private void acabarTurno() {
        if (jugadores.isEmpty()) {
            System.out.println("Aún no hay jugadores en la partida.");
            return;
        }
        if(!tirado){
            System.out.println("Aún tiene que tirar los dados.");
            return;
        }
        turno = (turno + 1)% jugadores.size();
        tirado = false;
        lanzamientos = 0; //Ya está hecho en lanzarDados() pero bueno
        System.out.println("Le toca a " + Objects.requireNonNull(getJugadorActual()).getNombre());
    }

    // Método que procesa los comandos dentro de un archivo de texto.
    private void procesarComandos(String[] partes){
        if (partes.length != 2) {
            System.out.println("Comando erróneo. Formato: comandos <ruta>\n");
            return;
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
    }
}
