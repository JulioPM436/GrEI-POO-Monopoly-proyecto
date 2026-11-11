package monopoly;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import partida.*;

import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;
import java.util.StringJoiner;

/**
 * El menú contiene todo lo necesario para crear la partida.
 */
public class Menu {

    //Atributos:
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
    private int numCartaSuerte = 0; //Última carta de Suerte que ha salido (0 = ninguna todavía).
    private int numCartaComunidad = 0; //Última carta de Caja de Comunidad que ha salido.
    // Textos de las cartas (Apéndice A). La carta 1 está en la posición 0 del array.
    private static final String[] CARTAS_SUERTE = {
            "Decides hacer un viaje de placer. Avanza hasta Solar19. Si pasas por la casilla de Salida, cobra 2.000.000€.",
            "Los acreedores te persiguen por impago. Ve a la Cárcel. Ve directamente sin pasar por la casilla de Salida y sin" +
                    " cobrar los 2.000.000€.",
            "¡Has ganado el bote de la lotería! Recibe 1.000.000€.",
            "Has sido elegido presidente de la junta directiva. Paga a cada jugador 250.000€.",
            "¡Hora punta de tráfico! Retrocede tres casillas.",
            "Te multan por usar el móvil mientras conduces. Paga 150.000€.",
            "Avanza hasta la casilla de transporte más cercana. Si no tiene dueño, puedes comprarla. Si tiene dueño, paga al" +
                    " dueño el doble de la operación indicada."
    };
    private static final String[] CARTAS_COMUNIDAD = {
            "Paga 500.000€ por un fin de semana en un balneario de 5 estrellas.",
            "Te investigan por fraude de identidad. Ve a la Cárcel. Ve directamente sin pasar por la casilla de Salida y" +
                    " sin cobrar los 2.000.000€.",
            "Colócate en la casilla de Salida. Cobra 2.000.000€.",
            "Devolución de Hacienda. Cobra 500.000€.",
            "Retrocede hasta Solar1 para comprar antigüedades exóticas.",
            "Ve a Solar20 para disfrutar del San Fermín. Si pasas por la casilla de Salida, cobra 2.000.000€."
    };

    // Constructor del menú.
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

    /*
     * ÍNDICE:
     *  - PARTIDA Y COMANDOS
     *  - JUGADORES Y TURNOS
     *  - DADOS Y MOVIMIENTO
     *  - PROPIEDADES Y DINERO
     *  - CARTAS
     *  - LISTADOS Y DESCRIPCIONES
     *  - BÚSQUEDAS AUXILIARES
     */
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////// PARTIDA Y COMANDOS ////////////////////////////////////////////////////

    /**
     * Interpreta un comando y ejecuta la acción correspondiente.
     * Trocea el texto por espacios y, según la primera palabra (y la segunda cuando
     * hace falta), llama al método que se encarga de ese comando. Si el comando no
     * existe o no tiene el formato correcto, muestra un aviso.
     *
     * @param comando Línea de texto introducida por el usuario o leída de un fichero.
     */
    private void analizarComando(String comando) {
        // Se trocea el comando introducido usando los espacios como separador.
        String[] partes = comando.trim().split("\\s+");
        // El primer argumento decide la clase de comando que es.
        switch (partes[0]) {
            case "acabar":
                acabarTurno();
                break;

            case "comandos":
                procesarComandos(partes);
                break;

            case "comprar":
                if (partes.length != 2) {
                    System.out.println(Valor.ERROR_USO + "comprar <casilla>");
                    break;
                }
                comprar(partes[1]);
                break;

            case "crear":
                crearJugador(partes);
                break;

            case "describir":
                if (partes.length == 1) {
                    System.out.println(Valor.ERROR_USO + "describir <casilla> | describir jugador <nombre> | describir avatar <ID>");
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

            case "edificar":
                if (partes.length < 2 || partes.length > 3) {
                    System.out.println(Valor.ERROR_USO + "edificar casa|hotel|piscina|pistadeporte");
                    break;
                }
                edificar(partes);
                break;

            case "hipotecar":
                if (partes.length > 1) {
                    hipotecarPropiedad(partes[1], getJugadorActual());
                } else {
                    System.out.println(Valor.ERROR_USO + "hipotecar <casilla>");
                }
                break;

            case "deshipotecar":
                if (partes.length == 2) {
                    deshipotecar(partes[1], getJugadorActual());
                } else {
                    System.out.println(Valor.ERROR_USO + "deshipotecar <casilla>");
                }
                break;

            case "jugador":
                turnoActual();
                break;

            case "lanzar":
                lanzarDados(partes);
                break;

            case "vender":
                if (partes.length != 4) {
                    System.out.println(Valor.ERROR_USO + "vender casas|hoteles|piscina|pistadeporte <solar> <cantidad>");
                    break;
                }
                vender(partes);
                break;

            case "listar":
                if (partes.length == 1) {
                    System.out.println(Valor.ERROR_USO + "listar jugadores|avatares|enventa|edificios [<color>]");
                    break;
                }
                // Algunos comandos tienen varias opciones distintas.
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
                    case "edificios":
                        listarEdificios(partes);
                        break;
                    default:
                        System.out.println(Valor.ERROR_USO + "listar jugadores|avatares|enventa|edificios [<color>]");
                }
                break;
            case "salir":
                if (partes[1].equals("carcel")) {
                    salirCarcel();
                }
                break;

            case "estadisticas":
                if (partes.length == 1) {
                    mostrarEstadisticasJuego();
                } else if (partes.length == 2) {
                    mostrarEstadisticas(partes[1]);
                } else {
                    System.out.println(Valor.ERROR_USO + "estadisticas [<jugador>]");
                }
                break;

            case "ver":
                System.out.println(tablero);
                break;
            // Comandos auxiliares. Valor.ERROR_USO +
            case "help":
                System.out.println("\tacabar turno");
                System.out.println("\tcomandos <fichero>");
                System.out.println("\tcomprar <casilla>");
                System.out.println("\tcrear jugador <nombre> <coche|esfinge|sombrero|pelota>");
                System.out.println("\tdescribir <casilla> | describir jugador <nombre> | describir avatar <ID>");
                System.out.println("\tdeshipotecar <casilla>");
                System.out.println("\tedificar casa|hotel|piscina|pistadeporte");
                System.out.println("\testadisticas [<jugador>]");
                System.out.println("\thipotecar <casilla>");
                System.out.println("\tjugador");
                System.out.println("\tlanzar dados [X+Y] (X e Y entre 1 y 6)");
                System.out.println("\tlistar jugadores|avatares|enventa|edificios [<color>]");
                System.out.println("\tsalir carcel");
                System.out.println("\tvender <tipo> <casilla> <cantidad>");
                System.out.println("\tver tablero");
                break;
            case "exit":
                System.exit(0);
                break;
            case "clear":
                System.out.print("\033[H\033[2J\033[3J");
                System.out.flush();
                break;
            // Opción por defecto, es decir, comando mal introducido.
            default:
                System.out.println(Valor.ERROR + "Comando desconocido: " + comando + ". Escriba 'help' para ver opciones de comandos.");
        }
    }

    /**
     * Inicia la partida: crea la banca, el tablero y las listas de jugadores y
     * avatares, y entra en el bucle que lee y ejecuta los comandos de la consola.
     */
    private void iniciarPartida() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.print("Introduce un comando: ");
            String linea = sc.nextLine();
            analizarComando(linea);
        }
    }

    /**
     * Ejecuta el comando {@code comandos <fichero>}: lee el fichero línea a línea y
     * ejecuta cada una como un comando, mostrándola antes precedida de
     * {@code $>}. Se saltan las líneas vacías y las que empiezan por {@code #}.
     *
     * @param partes Comando troceado por espacios: {@code partes[1]} es la ruta del
     *               fichero.
     */
    private void procesarComandos(String[] partes){
        if (partes.length != 2) {
            System.out.println(Valor.ERROR_USO + "comandos <fichero>");
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
            System.out.println(Valor.ERROR + "No se pudo leer el archivo: " + partes[1]);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////// JUGADORES Y TURNOS ////////////////////////////////////////////////////

    /**
     * Ejecuta el comando {@code acabar turno}: pasa el turno al siguiente jugador,
     * siguiendo el orden en que se dieron de alta, y lo muestra por pantalla.
     */
    private void acabarTurno() {
        if (jugadores.isEmpty()) {
            System.out.println(Valor.YELLOW + "Aún no hay jugadores en la partida." + Valor.RESET);
            return;
        }
        if(!tirado){
            System.out.println(Valor.YELLOW + "Aún tiene que tirar los dados." + Valor.RESET);
            return;
        }
        turno = (turno + 1)% jugadores.size();
        tirado = false;
        lanzamientos = 0; //Ya está hecho en lanzarDados() pero bueno
        System.out.println("Le toca a " + Objects.requireNonNull(getJugadorActual()).getNombre());
    }

    /**
     * Ejecuta el comando {@code crear jugador <nombre> <avatar>}: da de alta un
     * jugador con su avatar, lo coloca en la Salida y repinta el tablero.
     * Los tipos de avatar válidos son coche, esfinge, sombrero y pelota, y la
     * partida admite como máximo cuatro jugadores.
     *
     * @param partes Comando troceado por espacios: {@code partes[2]} es el nombre
     *               y {@code partes[3]} el tipo de avatar.
     */
    private void crearJugador(String[] partes) {
        // Se comprueba que el formato del comando sea correcto, esto es, tiene 4 argumentos.
        if (partes.length == 4){
            // Se impide crear más jugadores si ya hay 4.
            if (!partes[3].equalsIgnoreCase("Coche") &&!partes[3].equalsIgnoreCase("Esfinge")
                    &&!partes[3].equalsIgnoreCase("Sombrero") && !partes[3].equalsIgnoreCase("Pelota")) {
                System.out.println(Valor.YELLOW + "Tipo de avatar no admitido. Permitidos: Coche, Esfinge, Sombrero, Pelota." +
                        Valor.RESET);
                return;
            }
            if (jugadores.size() < 4) {
                // Se trocea el comando para tomar cada argumento
                String nombre = partes[2];
                String tipo = partes[3];
                if (!tipo.equals("coche") && !tipo.equals("esfinge") && !tipo.equals("sombrero") && !tipo.equals("pelota")) {
                    System.out.println(Valor.YELLOW + "El avatar debe ser coche, esfinge, sombrero o pelota." + Valor.RESET);
                    return;  //comprueba que sea uno de los 4 posibles avatares
                }
                if (buscarjugador(nombre) != null) {
                    System.out.println(Valor.YELLOW + "Ya existe un jugador llamado " + nombre + "." + Valor.RESET);
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
                System.out.println("{");
                System.out.println("  nombre: " + j.getNombre() + ",");
                System.out.println("  avatar: " + j.getAvatar().getId());
                System.out.println("}");
                System.out.println(tablero);
            } else {
                System.out.println(Valor.YELLOW + "No se pueden crear más de 4 jugadores." + Valor.RESET);
            }
        }
        else {
            // Si no, se asume que el comando era erróneo y no hace nada.
            System.out.println(Valor.ERROR_USO + "crear jugador <nombre> <coche|esfinge|sombrero|pelota>");
        }
    }

    /**
     * Devuelve el jugador que tiene el turno.
     *
     * @return El jugador al que le toca jugar, o {@code null} si todavía no hay
     *         jugadores en la partida.
     */
    private Jugador getJugadorActual() {
        if (jugadores == null || jugadores.isEmpty()) {
            return null;
        }
        return jugadores.get(turno);
    }

    /**
     * Ejecuta el comando {@code jugador}: muestra el nombre y el avatar del jugador
     * que tiene el turno.
     */
    private void turnoActual() {
        if (this.jugadores.isEmpty()) {
            System.out.println(Valor.YELLOW + "Aún no hay jugadores en la partida." + Valor.RESET);
            return;
        }
        System.out.println("{");
        System.out.println("  nombre: " + jugadores.get(turno).getNombre() + ",");
        System.out.println("  avatar: " + jugadores.get(turno).getAvatar().getId());
        System.out.println("}");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////// DADOS Y MOVIMIENTO ////////////////////////////////////////////////////

    /**
     * Ejecuta el comando {@code lanzar dados}, con valores aleatorios, o
     * {@code lanzar dados X+Y}, con los valores forzados. Mueve el avatar, aplica la
     * acción de la casilla de destino y gestiona los dobles (los terceros seguidos
     * mandan a la Cárcel) y los intentos de salir de la Cárcel.
     *
     * @param partes Comando troceado por espacios. Si tiene tres elementos,
     *               {@code partes[2]} contiene los valores forzados con el formato
     *               {@code X+Y}.
     */
    private void lanzarDados(String[] partes) {
        if (jugadores.isEmpty()) {
            System.out.println(Valor.YELLOW + "Aún no hay jugadores en la partida." + Valor.RESET);
            return;
        }
        if (jugadores.size() < 2) {
            System.out.println(Valor.YELLOW + "Se necesitan al menos 2 jugadores para empezar la partida." + Valor.RESET);
            return;
        }
        if (tirado) {
            System.out.println(Valor.YELLOW + "El jugador ya ha tirado los dados" + Valor.RESET);
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
        assert actual != null;
        String nombre = actual.getAvatar().getLugar().getNombre();
        int actualPos = actual.getAvatar().getLugar().getPosicion();
        int destinoPos = (actualPos - 1 + suma) % 40 + 1;

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
            Casilla destinoCas = tablero.getCasillaPorPosicion(destinoPos);
            String nombre2 = destinoCas.getNombre();
            System.out.println("El avatar " + actual.getAvatar().getId() + " avanza " + suma +  " posiciones, desde " +
                    nombre + " hasta " + nombre2 + ".");
            System.out.println(tablero);
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
        Casilla destinoCas = tablero.getCasillaPorPosicion(destinoPos);
        String nombre2 = destinoCas.getNombre();
        System.out.println("El avatar " + actual.getAvatar().getId() + " avanza " + suma +  " posiciones, desde " +
                nombre + " hasta " + nombre2 + ".");
        System.out.println(tablero);
    }

    /**
     * Lleva el avatar del jugador hasta una casilla concreta, avanzando por el
     * tablero. Si por el camino pasa por la Salida, cobra lo que le corresponde
     * (de eso se encarga {@code moverAvatar}). Pensado para las cartas que mandan
     * a una casilla determinada.
     *
     * @param j  Jugador cuyo avatar se mueve.
     * @param nombreDestino Casilla a la que tiene que llegar.
     * @param puedeCobrarSalida aaaaaa
     */
    private void moverHasta(Jugador j, String nombreDestino, boolean puedeCobrarSalida) {
        Avatar av = j.getAvatar();
        Casilla origen = av.getLugar();
        Casilla destino = tablero.encontrar_casilla(nombreDestino);

        if (puedeCobrarSalida) {
            if (destino.getPosicion() < origen.getPosicion()) {
                j.sumarFortuna(2000000);
                System.out.println(j.getNombre() + " pasa por Salida y cobra "
                        + (long) 2000000 + "€.");
            }
        }

        // Movemos el avatar

        av.colocarEn(destino);

        System.out.println(tablero);

        if (destino.getTipo().equals("Suerte") || destino.getTipo().equals("Comunidad")) {
            sacarCarta(j, destino.getTipo());
        } else {
            solvente = destino.evaluarCasilla(j, banca, 0, tablero.getCasillaPorPosicion(21));
        }
    }

    /**
     * Mueve el avatar del jugador las casillas indicadas y aplica la acción de la
     * casilla en la que cae: alquiler, impuesto, Parking, IrCarcel, cartas...
     * Muestra el movimiento por pantalla y repinta el tablero.
     *
     * @param actual Jugador cuyo avatar se mueve.
     * @param suma   Número de casillas que avanza (la suma de los dados).
     */
    private void moverYEvaluar(Jugador actual, int suma) {
        actual.getAvatar().moverAvatar(tablero.getPosiciones(), suma);
        Casilla casilla = actual.getAvatar().getLugar();

        if (casilla.getNombre().equals("IrCarcel")) {
            System.out.println("Has caído en IrCarcel: el avatar se coloca en la casilla de Cárcel.");
            actual.encarcelar(tablero.getPosiciones());
            actual.incrementarVecesEnLaCarcel();
        } else if (casilla.getTipo().equals("Suerte") || casilla.getTipo().equals("Comunidad")) {
            sacarCarta(actual, casilla.getTipo());
        }

        else {
            casilla.evaluarCasilla(actual, banca, suma, tablero.encontrar_casilla("Parking"));
        }
    }

    /**
     * Ejecuta el comando {@code salir carcel}: el jugador con el turno paga la
     * fianza (500.000 €) y sale de la Cárcel, tras lo cual puede lanzar los dados.
     * Solo es posible si está en la Cárcel y tiene dinero suficiente.
     */
    private void salirCarcel() {
        if (jugadores.isEmpty()) {
            System.out.println(Valor.YELLOW + "Aún no hay jugadores en la partida." + Valor.RESET);
            return;
        }

        if (!Objects.requireNonNull(getJugadorActual()).isEnCarcel()) {
            System.out.println(Valor.YELLOW + "El jugador actual no está en la cárcel" + Valor.RESET);
            return;
        }
        float fianza = 500000f;
        if (getJugadorActual().getFortuna() < fianza) {
            System.out.println(Valor.YELLOW + "El jugador no tiene dinero suficiente para salir de la cárcel" + Valor.RESET);
            return;
        }
        getJugadorActual().sumarFortuna(-fianza);
        getJugadorActual().sumarGastos(fianza);

        getJugadorActual().setEnCarcel(false);
        getJugadorActual().setTiradasCarcel(0);

        System.out.println(getJugadorActual().getNombre() + " paga " + fianza + "€ y sale de la cárcel. Ya puede lanzar los dados.");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////// PROPIEDADES Y DINERO ///////////////////////////////////////////////////

    /**
     * Ejecuta el comando {@code comprar <casilla>}: el jugador con el turno compra
     * la casilla indicada. Solo es posible si su avatar está en ella, si todavía
     * pertenece a la banca y si el jugador tiene dinero suficiente.
     *
     * @param nombre Nombre de la casilla que se quiere comprar.
     */
    private void comprar(String nombre) {

        if (jugadores.isEmpty()) {
            System.out.println(Valor.YELLOW + "Aún no hay jugadores en la partida." + Valor.RESET);
            return;
        }

        if (!Objects.requireNonNull(getJugadorActual()).getAvatar().getLugar().getNombre().equalsIgnoreCase(nombre)) {
            System.out.println(Valor.YELLOW + "No se puede comprar está casilla" + Valor.RESET);
            return;
        }

        getJugadorActual().getAvatar().getLugar().comprarCasilla(getJugadorActual(),banca);
        System.out.println("Su fortuna actual es de " + (long)getJugadorActual().getFortuna() + "€.");
    }

    /**
     * Ejecuta el comando {@code deshipotecar <casilla>}: el jugador paga la misma
     * cantidad que recibió al hipotecar la propiedad, que vuelve a cobrar alquileres
     * y permite edificar en su grupo. Solo es posible si la propiedad le pertenece,
     * está hipotecada y el jugador tiene dinero suficiente.
     *
     * @param nombrecasilla Nombre de la propiedad que se quiere deshipotecar.
     * @param jugadorActual Jugador que tiene el turno.
     */
    private void deshipotecar(String nombrecasilla, Jugador jugadorActual){
        Casilla casilla = this.tablero.encontrar_casilla(nombrecasilla);
        if (casilla == null) {
            System.out.println(Valor.YELLOW + "No existe ninguna casilla con el nombre: " + nombrecasilla + Valor.RESET);
            return;
        }

        if (casilla.getDuenho() == null || casilla.getDuenho() != jugadorActual) {
            System.out.println(Valor.YELLOW + jugadorActual.getNombre() + " no puede deshipotecar " + casilla.getNombre() +
                    ". No es una propiedad que le pertenece." + Valor.RESET);
            return;
        }
        if (!casilla.isHipotecada()) {
            System.out.println(Valor.YELLOW + jugadorActual.getNombre() + " no puede deshipotecar " + casilla.getNombre() +
                    ". No está hipotecada." + Valor.RESET);
            return;
        }
        float sumaHipoteca = casilla.getValorHipoteca();
        if(jugadorActual.getFortuna() < sumaHipoteca){
            System.out.println(Valor.YELLOW + "No puede deshipotecar porque no tiene saldo suficiente" + Valor.RESET);
            return;
        }

        jugadorActual.sumarFortuna(-sumaHipoteca);
        casilla.setHipotecada(false);
        if (casilla.getGrupo() != null) {
            String grupo = casilla.getGrupo().getColorGrupo();
            System.out.println(jugadorActual.getNombre() + " paga " + sumaHipoteca + "€ para deshipotecar " + casilla.getNombre() +
                    ". Ahora puede cobrar alquileres y edificar en el grupo " + grupo + ".");
        } else {
            System.out.println(jugadorActual.getNombre() + " paga " + sumaHipoteca + "€ para deshipotecar " + casilla.getNombre() +
                    ". Ahora puede cobrar alquileres de esta casilla.");
        }

    }

    /**
     * Ejecuta el comando {@code edificar <tipo>}: construye un edificio (casa,
     * hotel, piscina o pista_deporte) en la casilla donde está el avatar del
     * jugador con el turno. Comprueba las reglas de edificación (grupo completo,
     * máximo de cuatro casas, hotel antes de piscina y pista...), genera el
     * identificador del edificio y descuenta su coste de la fortuna del jugador.
     *
     * @param partes Comando troceado por espacios: {@code partes[1]} es el tipo
     *               de edificio.
     */
    private void edificar(String[] partes) {
        // Lógicamente deben haber dos jugadores para empezar la partida.
        if (this.jugadores.isEmpty()) {
            System.out.println(Valor.YELLOW + "Aún no hay jugadores en la partida." + Valor.RESET);
            return;
        }
        // Obtenemos los datos del jugador,casilla en la que está y el grupo.
        Jugador actual = getJugadorActual();
        assert actual != null;
        int actualCasillaPos = actual.getAvatar().getLugar().getPosicion();
        Casilla actualCasilla = actual.getAvatar().getLugar();
        Grupo actualGrupo = actual.getAvatar().getLugar().getGrupo();
        String actualCasillaTipo = actualCasilla.getTipo();

        // Sólo se puede edificar en los solares.
        if (!actualCasillaTipo.equals("Solar")){
            System.out.println(Valor.YELLOW + "Sólo se puede edificar en los solares." + Valor.RESET);
            return;
        }
        // Necesitamos saber el número de edificaciones en la casilla.
        int numeroCasas = actualCasilla.getCasas();
        int hayHotel = actualCasilla.getHotel();
        int hayPiscina = actualCasilla.getPiscina();
        int hayPistadeporte = actualCasilla.getPistadeporte();
        float precio;

        // Si es dueño de todas las casillas del grupo, puede edificar.
        if (actualGrupo.esDuenhoGrupo(actual)) {
            // Si es dueño del grupo, puede empezar a construir.
            switch (partes[1]) {
                case "casa":
                    // Si hay 4 casas edificadas o si hay un hotel, no se pueden construir más casas.
                    if (numeroCasas >= 4){
                        System.out.println(Valor.YELLOW + "Ya hay 4 casas construidas." + Valor.RESET);
                        return;
                    }
                    if (hayHotel == 1) {
                        System.out.println(Valor.YELLOW + "No se pueden edificar más casas si ya hay un hotel." + Valor.RESET);
                        return;
                    }
                    // Se guardará como INT la cantidad de casas a comprar.
                    int cantidad = 0;
                    // Si se introdujeron 3 argumentos en el comando,
                    if (partes.length == 3) {
                        // El tercero será el número de casas. Con un catch se prepara el mensaje de error por si no
                        //      se introdujo un número. Con parseInt se convierte de String a Int.
                        try {
                            cantidad = Integer.parseInt(partes[2]);
                        } catch (NumberFormatException e) {
                            System.out.println(Valor.YELLOW + "La cantidad debe ser un número entero entre 1 y 4." + Valor.RESET);
                        }
                        // Si no se especifica cantidad, se asume que se compra una sola casa.
                    } else if (partes.length == 2) {
                        cantidad = 1;
                    }
                    // Se revisa el número introducido. No pueden construirse más de 4 casas o pasar un número negativo.
                    if (partes.length == 3 || cantidad == 1) {
                        if (cantidad > 4 || cantidad <= 0) {
                            System.out.println(Valor.YELLOW + "No puedes construir más de 4 casas en una casilla." + Valor.RESET);
                        } else {
                            // Se calculan la cantidad de casas disponibles para construir restando las casas que pretendemos
                            //      construir al máximo de cuatro. No podemos edificar más casas de las que hay disponibles.
                            int disponibles = 4-numeroCasas;
                            if (disponibles < cantidad) {
                                System.out.println(Valor.YELLOW + "No puedes construir más de 4 casas en una casilla. Quedan " +
                                        disponibles + " espacios para construir casas en este solar." + Valor.RESET);
                            } else {
                                // El precio de las casas es el precio de la casa por la cantidad que compre.
                                precio = (cantidad * Valor.SOLAR_PRECIOS_CASA[actualCasillaPos]);
                                // Pero primero debe poder pagarlas.
                                if (actual.getFortuna() >= precio) {
                                    // Se le resta el valor de la casa en esa casilla, tantas veces como casas compre.
                                    actual.sumarFortuna(-precio);
                                    // Se actualiza el número de casas.
                                    for (int i = 0; i < cantidad; i++){
                                        actualCasilla.setCasa(tablero.generarIDcasa());
                                    }
                                    // Una vez se añade la casa, se recalcula el alquiler.
                                    actualCasilla.actualizarAlquiler();
                                    // Por cosmética, se imprime un mensaje dependiendo de si se compra una casa o varias.
                                    if (cantidad == 1) {
                                        System.out.println("Se ha edificado una casa en " + actualCasilla.getNombre() + ". La " +
                                                "fortuna de " + actual.getNombre() + " se reduce en " + precio + "€.");
                                    } else {
                                        System.out.println("Se han edificado " + cantidad + " casas en " + actualCasilla.getNombre() +
                                                ". La fortuna de " + actual.getNombre() + " se reduce en " + precio + "€.");
                                    }
                                } else {
                                    System.out.println(Valor.YELLOW + "Dinero insuficiente." + Valor.RESET);
                                }
                            }
                        }
                    }
                    break;
                case "hotel":
                    // Aquí no puede admitir una cantidad (no serviría de nada).
                    if (partes.length > 2){
                        System.out.println(Valor.ERROR_USO + "edificar casa [<cantidad>]|hotel|piscina|pistadeporte");
                        return;
                    }
                    // Si ya hay hotel, o si no hay 4 casas, no se puede construir un hotel.
                    if (hayHotel == 1){
                        System.out.println(Valor.YELLOW + "Ya se ha construido un hotel en esta casilla." + Valor.RESET);
                        return;
                    }
                    if (numeroCasas != 4){
                        System.out.println(Valor.YELLOW + "Debes tener 4 casas para construir un hotel." + Valor.RESET);
                        return;
                    }
                    // El precio de las casas es el precio de la casa por la cantidad que compre.
                    precio = (Valor.SOLAR_PRECIOS_HOTEL[actualCasillaPos]);
                    // Pero primero debe poder pagarlas.
                    if (actual.getFortuna() >= precio) {
                        // Se le resta el valor del hotel a la fortuna del jugador.
                        actual.sumarFortuna(-precio);
                        // Se actualiza la cantidad de hoteles.
                        actualCasilla.setHotel(tablero.generarIDhotel());
                        // Una vez se añade el hotel, se recalcula el alquiler.
                        actualCasilla.actualizarAlquiler();
                        // Se imprime el aviso de la compra.
                        System.out.println("Se ha edificado un hotel en " + actualCasilla.getNombre() + ". La " +
                                "fortuna de " + actual.getNombre() + " se reduce en " + precio + "€.");
                    } else {
                        System.out.println(Valor.YELLOW + "Dinero insuficiente." + Valor.RESET);
                    }
                    break;
                case "piscina":
                    // Aquí no puede admitir una cantidad (no serviría de nada).
                    if (partes.length > 2) {
                        System.out.println(Valor.ERROR_USO + "edificar casa [<cantidad>]|hotel|piscina|pistadeporte");
                        return;
                    }
                    if (hayHotel == 0) {
                        System.out.println(Valor.YELLOW + "No se puede edificar una piscina, ya que no se dispone de un hotel."
                                + Valor.RESET);
                        return;
                    }
                    if (hayPiscina == 1) {
                        System.out.println(Valor.YELLOW + "Ya se dispone de una piscina." + Valor.RESET);
                        return;
                    }
                    // El precio de las casas es el precio de la casa por la cantidad que compre.
                    precio = (Valor.SOLAR_PRECIOS_PISCINA[actualCasillaPos]);
                    // Pero primero debe poder pagarlas.
                    if (actual.getFortuna() >= precio) {
                        // Se le resta el valor de la casa en esa casilla, tantas veces como casas compre.
                        actual.sumarFortuna(-precio);
                        // Se actualiza el número de casas.
                        actualCasilla.setPiscina(tablero.generarIDpiscina());
                        // Una vez se añade la piscina, se recalcula el alquiler.
                        actualCasilla.actualizarAlquiler();
                        // Se imprime el aviso de la compra.
                        System.out.println("Se ha edificado una piscina en " + actualCasilla.getNombre() + ". La " +
                                "fortuna de " + actual.getNombre() + " se reduce en " + precio + "€.");
                    }
                    break;
                case "pistadeporte":
                    // Aquí no puede admitir una cantidad (no serviría de nada).
                    if (partes.length > 2) {
                        System.out.println(Valor.ERROR_USO + "edificar casa [<cantidad>]|hotel|piscina|pistadeporte");
                        return;
                    }
                    if (hayHotel == 0) {
                        System.out.println(Valor.YELLOW + "No se puede edificar una pista de deporte, ya que no se dispone de un hotel."
                                + Valor.RESET);
                        return;
                    }
                    if (hayPiscina == 0) {
                        System.out.println(Valor.YELLOW + "No se puede edificar una pista de deporte, ya que no " +
                                "se dispone de una piscina." + Valor.RESET);
                        return;
                    }
                    if (hayPistadeporte == 1) {
                        System.out.println(Valor.YELLOW + "Ya se dispone de una pista de deporte." + Valor.RESET);
                        return;
                    }
                    // El precio de las casas es el precio de la casa por la cantidad que compre.
                    precio = (Valor.SOLAR_PRECIOS_PISTADEPORTE[actualCasillaPos]);
                    // Pero primero debe poder pagarlas.
                    if (actual.getFortuna() >= precio) {
                        // Se le resta el valor de la casa en esa casilla, tantas veces como casas compre.
                        actual.sumarFortuna(-precio);
                        // Se actualiza el número de casas.
                        actualCasilla.setPistadeporte(tablero.generarIDpistadeporte());
                        // Una vez se añade la pista de deporte, se recalcula el alquiler.
                        actualCasilla.actualizarAlquiler();
                        // Se imprime el aviso de la compra.
                        System.out.println("Se ha edificado una pista de deporte en " + actualCasilla.getNombre() + ". La " +
                                "fortuna de " + actual.getNombre() + " se reduce en " + precio + "€.");
                    }
                    break;
                default:
                    System.out.println(Valor.ERROR_USO + "edificar casa [<cantidad>]|hotel|piscina|pistadeporte");
                    break;
            }
        } else {
            System.out.println(Valor.YELLOW + "Debes ser dueño de todo el grupo para poder construir." + Valor.RESET);
        }
    }




    private void vender(String[] partes){
        if (this.jugadores.isEmpty()) {
            System.out.println(Valor.YELLOW + "Aún no hay jugadores en la partida." + Valor.RESET);
            return;
        }
        Jugador actual = getJugadorActual();

        String tipo = partes[1].toLowerCase();

        if (!tipo.equals("casa") && !tipo.equals("hotel")
                && !tipo.equals("piscina") && !tipo.equals("pistadeporte")) {
            System.out.println(Valor.ERROR + "Tipo de edificio no válido: " + partes[1]);
            return;
        }

        // Casilla donde se vende
        String nombreCasilla = partes[2];
        Casilla casilla = tablero.encontrar_casilla(nombreCasilla);

        // ¿Existe la casilla?
        if (casilla == null) {
            System.out.println(Valor.ERROR + "No existe la casilla " + nombreCasilla + ".");
            return;
        }
        // ¿Es un solar?
        if (!casilla.getTipo().equals("Solar")) {
            System.out.println(Valor.YELLOW + "Solo se pueden vender edificios en los solares." + Valor.RESET);
            return;
        }

        if (casilla.getDuenho() != actual) {
            System.out.println(Valor.YELLOW + "No se pueden vender " + tipo + "s en " + casilla.getNombre()
                    + ". Esta propiedad no pertenece a " + actual.getNombre() + "." + Valor.RESET);
            return;
        }
        int cantidad;
        try {
            cantidad = Integer.parseInt(partes[3]);
        } catch (NumberFormatException e) {
            System.out.println(Valor.ERROR + "La cantidad debe ser un número: " + partes[3]);
            return;
        }

        // ¿La cantidad es mayor que 0?
        if (cantidad <= 0) {
            System.out.println(Valor.YELLOW + "La cantidad debe ser mayor que 0." + Valor.RESET);
            return;
        }
        int disponibles = 0;     // cuántos hay en la casilla
        float precio = 0;        // precio de un edificio

        switch (tipo) {
            case "casa":
                disponibles = casilla.getCasas();
                precio = Valor.SOLAR_PRECIOS_CASA[casilla.getPosicion()];
                // 2. Los tres casos
                if (disponibles == 0) {
                    // no hay ninguna: avisar y salir
                    System.out.println(Valor.YELLOW + "No hay casas en " + casilla.getNombre() + "." + Valor.RESET);
                    return;

                } else if (cantidad <= disponibles) {
                    // hay suficientes: se venden las que pide
                    casilla.venderCasas(cantidad);
                    actual.sumarFortuna(precio * cantidad);
                    // Se recalcula el alquiler tras la venta.
                    casilla.actualizarAlquiler();
                    System.out.println(actual.getNombre() + " ha vendido " + cantidad + " casas en "
                            + casilla.getNombre() + ", recibiendo " + (long) (precio * cantidad)
                            + "€. En la propiedad quedan " + casilla.getCasas() + " casas.");


                } else {
                    // hay alguna, pero menos de las que pide: se venden las que hay
                    casilla.venderCasas(disponibles);
                    actual.sumarFortuna(precio * disponibles);
                    // Se recalcula el alquiler tras la venta.
                    casilla.actualizarAlquiler();
                    System.out.println("Solamente se puede vender " + disponibles + " casas, recibiendo "
                            + (long) (precio * disponibles) + "€.");

                }

                break;

            case "hotel":
                disponibles = casilla.getHotel();
                precio = Valor.SOLAR_PRECIOS_HOTEL[casilla.getPosicion()];

                if (casilla.getPiscina() > 0){
                    System.out.println(Valor.YELLOW + "Hay piscinas en " + casilla.getNombre() + "por lo que no puedes vender." + Valor.RESET);
                    return;
                }

                if (disponibles == 0) {
                    // No hay hotel: avisar y salir
                    System.out.println(Valor.YELLOW + "No hay hoteles en " + casilla.getNombre() + "." + Valor.RESET);
                    return;

                } else if (cantidad <= disponibles) {
                    // Pide 1 y hay 1: se vende
                    casilla.venderHotel();
                    actual.sumarFortuna(precio);
                    // Se recalcula el alquiler tras la venta.
                    casilla.actualizarAlquiler();
                    System.out.println(actual.getNombre() + " ha vendido 1 hotel en "
                            + casilla.getNombre() + ", recibiendo " + (long) precio
                            + "€. En la propiedad quedan " + casilla.getHotel() + " hoteles.");

                } else {
                    // Pide más de 1, pero solo hay 1: se vende ese
                    casilla.venderHotel();
                    actual.sumarFortuna(precio);
                    // Se recalcula el alquiler tras la venta.
                    casilla.actualizarAlquiler();
                    System.out.println("Solamente se puede vender 1 hotel, recibiendo "
                            + (long) precio + "€.");
                }
                break;

            case "piscina":
                disponibles = casilla.getPiscina();
                precio = Valor.SOLAR_PRECIOS_PISCINA[casilla.getPosicion()];
                if (casilla.getPistadeporte() > 0){
                    System.out.println(Valor.YELLOW + "Hay pistas de deporte en " + casilla.getNombre() + "por lo que no puedes vender." + Valor.RESET);
                    return;
                }

                if (disponibles == 0) {
                    // No hay piscina: avisar y salir
                    System.out.println(Valor.YELLOW + "No hay piscinas en " + casilla.getNombre() + "." + Valor.RESET);
                    return;

                } else if (cantidad <= disponibles) {
                    // Pide 1 y hay 1: se vende
                    casilla.venderPiscina();
                    actual.sumarFortuna(precio);
                    // Se recalcula el alquiler tras la venta.
                    casilla.actualizarAlquiler();
                    System.out.println(actual.getNombre() + " ha vendido 1 piscina en "
                            + casilla.getNombre() + ", recibiendo " + (long) precio + "€.");

                } else {
                    // Pide más de 1, pero solo hay 1: se vende esa
                    casilla.venderPiscina();
                    actual.sumarFortuna(precio);
                    // Se recalcula el alquiler tras la venta.
                    casilla.actualizarAlquiler();
                    System.out.println("Solamente se puede vender 1 piscina, recibiendo "
                            + (long) precio + "€.");
                }
                break;

            case "pistadeporte":
                disponibles = casilla.getPistadeporte();
                precio = Valor.SOLAR_PRECIOS_PISTADEPORTE[casilla.getPosicion()];

                if (disponibles == 0) {
                    // No hay pista de deporte: avisar y salir
                    System.out.println(Valor.YELLOW + "No hay pistas de deporte en " + casilla.getNombre() + "." + Valor.RESET);
                    return;

                } else if (cantidad <= disponibles) {
                    // Pide 1 y hay 1: se vende
                    casilla.venderPistadeporte();
                    actual.sumarFortuna(precio);
                    // Se recalcula el alquiler tras la venta.
                    casilla.actualizarAlquiler();
                    System.out.println(actual.getNombre() + " ha vendido 1 pista de deporte en "
                            + casilla.getNombre() + ", recibiendo " + (long) precio + "€.");

                } else {
                    // Pide más de 1, pero solo hay 1: se vende esa
                    casilla.venderPistadeporte();
                    actual.sumarFortuna(precio);
                    // Se recalcula el alquiler tras la venta.
                    casilla.actualizarAlquiler();
                    System.out.println("Solamente se puede vender 1 pista de deporte, recibiendo "
                            + (long) precio + "€.");
                }
                break;
        }

    }

    /**
     * Ejecuta el comando {@code hipotecar <casilla>}: el jugador recibe el valor de
     * hipoteca de la propiedad, que deja de cobrar alquileres y bloquea la
     * edificación en su grupo hasta que se deshipoteque. Solo es posible si la
     * propiedad le pertenece y no está ya hipotecada; si es un solar con edificios,
     * antes hay que venderlos.
     *
     * @param nombrecasilla Nombre de la propiedad que se quiere hipotecar.
     * @param jugadoractual Jugador que tiene el turno.
     */
    private void hipotecarPropiedad(String nombrecasilla, Jugador jugadoractual){
        Casilla casilla = this.tablero.encontrar_casilla(nombrecasilla);

        if(casilla == null) {
            System.out.println(Valor.YELLOW + "No existe dicha casilla." + Valor.RESET);
            return;
        }
        if(casilla.getDuenho() != jugadoractual){
            System.out.println(Valor.YELLOW + jugadoractual.getNombre() + " no puede hipotecar " + casilla.getNombre() +
                    "Esta casilla no pertenece al jugador actual." + Valor.RESET);
            return;
        }
        if(casilla.isHipotecada()){
            System.out.println(Valor.YELLOW + "Esta casilla ya está hipotecada." + Valor.RESET);
            return;
        }
        if (casilla.getTipo().equalsIgnoreCase("Solar") && casilla.tieneEdificios()) {
            System.out.println(Valor.YELLOW + jugadoractual.getNombre() + " no puede hipotecar " + casilla.getNombre() +
                    ". Se deben vender todos los edificios antes." + Valor.RESET);
            return;
        }

        casilla.setHipotecada(true);
        float sumaHipoteca = casilla.getValorHipoteca();
        jugadoractual.sumarFortuna(sumaHipoteca);

        if (casilla.getGrupo() != null) {
            String grupo = casilla.getGrupo().getColorGrupo();
            System.out.println(jugadoractual.getNombre() + " recibe " + sumaHipoteca + "€ por la hipoteca de " + casilla.getNombre() +
                    ". No puede recibir alquileres ni edificar en el grupo " + grupo + ".");
        } else {
            System.out.println(jugadoractual.getNombre() + " recibe " + sumaHipoteca + "€ por la hipoteca de " + casilla.getNombre() +
                    ". No puede recibir alquileres de esta casilla.");
        }

    }

    private void mostrarEstadisticas(String nombreJugador) {
        Jugador j = buscarjugador(nombreJugador);
        if (j == null) {
            System.out.println("No existe ningún jugador con el nombre: " + nombreJugador);
            return;
        }
        System.out.println(j.obtenerEstadisticas());
    }
    /**
     * Intenta cobrar una cantidad al jugador. Si no tiene dinero suficiente, se lo
     * indica y no le cobra nada.
     *
     * @param actual   Jugador que tiene que pagar.
     * @param cantidad Cantidad que se le quiere cobrar.
     * @return {@code true} si se ha hecho el pago; {@code false} si no tenía dinero
     *         suficiente.
     */

    private boolean pagar(Jugador actual, float cantidad) {
        if (actual.getFortuna() < cantidad) {
            System.out.println(Valor.YELLOW + actual.getNombre() + " no puede pagar " + (long) cantidad
                    + "€. Debe hipotecar alguna propiedad o declararse en bancarrota." + Valor.RESET);
            return false;
        }
        actual.sumarFortuna(-cantidad);
        actual.sumarGastos(cantidad);
        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////// CARTAS //////////////////////////////////////////////////////////

    /**
     * Saca la siguiente carta de Suerte o de Caja de Comunidad, muestra su
     * descripción y aplica su acción al jugador. Las cartas de cada mazo salen en
     * orden y, después de la última, se vuelve a empezar por la primera.
     *
     * @param j    Jugador que ha caído en la casilla.
     * @param tipo Tipo de la casilla en la que ha caído (Suerte o Caja de
     *             Comunidad), que indica de qué mazo se saca la carta.
     */
    //metodo para seleccionar la carta
    private void sacarCarta(Jugador j, String tipo){
        if (tipo.equals("Suerte")){
            if (numCartaSuerte == 7){
                numCartaSuerte = 0;
            }
            numCartaSuerte ++;
            switch (numCartaSuerte) {
                case 1:
                    System.out.println(CARTAS_SUERTE[0]);
                    moverHasta(j,"Solar19", true);
                    break;

                case 2:
                    System.out.println(CARTAS_SUERTE[1]);
                    j.encarcelar(tablero.getPosiciones());
                    break;

                case 3:
                    System.out.println(CARTAS_SUERTE[2]);
                    Objects.requireNonNull(getJugadorActual()).sumarFortuna(1000000);
                    break;

                case 4:
                    System.out.println(CARTAS_SUERTE[3]);
                    int numJugadores = jugadores.size();

                    // Paga a todos los demás de una vez
                    solvente = pagar(j, 250000 * (numJugadores - 1));

                    // Solo si ha podido pagar, cada jugador recibe su parte
                    if (solvente) {
                        for (int i = 0; i < numJugadores; i++) {
                            Jugador jey = jugadores.get(i);
                            if (jey != j) {
                                jey.sumarFortuna(250000);
                            }
                        }
                    }
                    break;

                case 5:
                    System.out.println(CARTAS_SUERTE[4]);
                    Avatar av = j.getAvatar();
                    Casilla cas_act = av.getLugar();
                    int pos_tres_atras = cas_act.getPosicion() - 3;
                    String nombre_cas_tres_atras = tablero.getCasillaPorPosicion(pos_tres_atras).getNombre();
                    moverHasta(j,nombre_cas_tres_atras, false);
                    break;

                case 6:
                    System.out.println(CARTAS_SUERTE[5]);
                    solvente = pagar(j, 150000);
                    break;

                case 7:
                    System.out.println(CARTAS_SUERTE[6]);
                    Avatar avat = j.getAvatar();
                    moverHasta(j, transporteMasCercano(avat.getLugar()).getNombre(), true);
                    break;
                default:
                    System.out.println("ERROR, esto no es una posicion de carta valida, esto nunca lo deberia leer el ususario");
                    break;
            }
        }
        else{
            if (numCartaComunidad == 6){
                numCartaComunidad = 0;
            }
            numCartaComunidad ++;
            switch (numCartaComunidad) {
                case 1:
                    System.out.println(CARTAS_COMUNIDAD[0]);
                    solvente = pagar(j, 500000);
                    break;

                case 2:
                    System.out.println(CARTAS_COMUNIDAD[1]);
                    j.encarcelar(tablero.getPosiciones());
                    break;

                case 3:
                    System.out.println(CARTAS_COMUNIDAD[2]);
                    moverHasta(j,"Salida", true);
                    break;

                case 4:
                    System.out.println(CARTAS_COMUNIDAD[3]);
                    Objects.requireNonNull(getJugadorActual()).sumarFortuna(500000);
                    break;

                case 5:
                    System.out.println(CARTAS_COMUNIDAD[4]);
                    moverHasta(j,"Solar1", false);
                    break;

                case 6:
                    System.out.println(CARTAS_COMUNIDAD[5]);
                    moverHasta(j,"Solar20", true);
                    break;

                default:
                    System.out.println("ERROR, esto no es una posicion de carta valida, esto nunca lo deberia leer el ususario");
                    break;
            }

        }
    }

    /**
     * Busca la primera casilla de transporte avanzando desde una casilla dada, y
     * da la vuelta al tablero si hace falta. Se usa en la carta de Suerte que manda
     * al transporte más cercano.
     *
     * @param desde Casilla desde la que se empieza a buscar.
     * @return La primera casilla de transporte que se encuentra.
     */
    private Casilla transporteMasCercano(Casilla desde) {
        int indice = desde.getPosicion() - 1;
        for (int i = 1; i <= 40; i++) {
            Casilla c = casillaEnIndice((indice + i) % 40);
            if (c.getTipo().equals("Transporte")) {
                return c;
            }
        }
        return desde;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////// LISTADOS Y DESCRIPCIONES ////////////////////////////////////////////////

    /**
     * Ejecuta el comando {@code describir avatar <ID>}: muestra el identificador,
     * el tipo, la casilla en la que está y el jugador al que pertenece el avatar.
     *
     * @param ID Letra que identifica al avatar.
     */
    private void descAvatar(String ID) {
        Avatar a = buscarAvatar(ID);
        if (a == null) {
            System.out.println(Valor.YELLOW + "No existe el avatar." + Valor.RESET);
            return;
        }
        System.out.println("tipo: "+ a.getTipo());
        System.out.println("lugar: "+ a.getLugar().getNombre());
        System.out.println("jugador: "+ a.getJugador().getNombre());

    }

    /**
     * Ejecuta el comando {@code describir <casilla>}: muestra la información de la
     * casilla, con un formato distinto según su tipo. Las casillas de Suerte, Caja
     * de Comunidad e IrCarcel no se pueden describir.
     *
     * @param nombre Nombre de la casilla que se quiere describir.
     */
    private void descCasilla(String nombre) {
        Casilla c = tablero.encontrar_casilla(nombre);
        if (c == null) {
            System.out.println(Valor.YELLOW + "No existe ninguna casilla con el nombre: " + nombre + Valor.RESET);
            return;
        }
        System.out.println(c.infoCasilla());
    }

    /**
     * Ejecuta el comando {@code describir jugador <nombre>}: muestra el nombre, el
     * avatar, la fortuna, las propiedades, las hipotecas y los edificios del jugador.
     *
     * @param partes Comando troceado por espacios: {@code partes[2]} es el nombre
     *               del jugador.
     */
    private void descJugador(String[] partes) {
        // Si la cantidad de parámetros introducidos es menor que 2, el comando no se introdujo correctamente.
        if (partes.length <= 2){
            System.out.println(Valor.YELLOW + "No ha introducido el nombre del jugador." + Valor.RESET);
            return;
        }
        // De otro modo, se busca al jugador que tenga ese nombre.
        Jugador j = buscarjugador(partes[2]);
        // Si la función devuelve nulo, no hay jugador con ese nombre.
        if (j == null) {
            System.out.println(Valor.YELLOW + "No existe el jugador." + Valor.RESET);
            return;
        }
        // Si existe, se imprimen por pantalla los atributos del jugador.
        System.out.println("{");
        System.out.println("  nombre: " + j.getNombre() + ",");
        System.out.println("  avatar: " + j.getAvatar().getId() + ",");
        System.out.println("  fortuna: " + (long) j.getFortuna() + ",");
        // Se itera sobre las casillas que tiene el jugador y se imprime su nombre.
        System.out.print("  propiedades: ");
        StringJoiner casillas = new StringJoiner(", ", "[", "]"); casillas.setEmptyValue("[ - ]");
        for (Casilla c : j.getPropiedades()) {
            casillas.add(c.getNombre());
        }
        System.out.println(casillas + ",");
        // Se itera sobre las propiedades del jugador, y si están hipotecadas, salen aquí.
        System.out.print("  hipotecas: ");
        StringJoiner hipotecas = new StringJoiner(", ", "[", "]"); hipotecas.setEmptyValue("[ - ]");
        for (Casilla c : j.getPropiedades()) {
            if (c.isHipotecada())
                hipotecas.add(c.getNombre());
        }
        System.out.println(hipotecas + ",");
        System.out.print("  edificios: ");
        // Hay que re-visitar las casillas que tiene el jugador, y para cada una imprimir los IDs de los edificios.
        if (j.getPropiedades().isEmpty())
            System.out.println("[-]");
        StringJoiner edificios = new StringJoiner(", ", "[", "]"); edificios.setEmptyValue("[ - ]");
        for (Casilla c : j.getPropiedades()) {
            if (c.getCasas() > 0){
                for (int i = 0; i < c.getCasas(); i++){
                    edificios.add(c.getIDCasa(i));
                }
            }
            if (c.getHotel() > 0)
                edificios.add(c.getIDHotel());
            if (c.getPiscina() > 0)
                edificios.add(c.getIDpiscina());
            if (c.getPistadeporte() > 0)
                edificios.add(c.getIDpistadeporte());
        }
        System.out.println(edificios);
        System.out.println("}");
    }

    /**
     * Ejecuta el comando {@code listar avatares}: muestra el identificador, el tipo,
     * la casilla y el jugador de cada avatar de la partida.
     */
    private void listarAvatares() {
        // Si no hay avatares, no hay nada que listar.
        if (avatares.isEmpty()) {
            System.out.println(Valor.YELLOW + "No hay avatares en la partida." + Valor.RESET);
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

    /**
     * Ejecuta el comando {@code listar edificios [<color>]}: muestra todos los datos de todos los edificios construidos. Si
     * se especificó un grupo (color), se imprimirán solo los edificios en casillas de ese grupo.
     */
    private void listarEdificios(String[] partes) {
        // Si el comando tiene más de 3 argumentos, está mal.
        if (partes.length > 3){
            System.out.println(Valor.ERROR_USO + "listar jugadores|avatares|enventa|edificios [<color>]");
            return;
        }
        // En esta variable irá el nombre del color del grupo, si no se introdujo un argumento con color, será null.
        String color = null;
        // Si el color coincide con alguno, se asigna a otra variable y se sale del bucle. Si no
        if (partes.length == 3) {
            // Se recorren los posibles nombres de grupo, si coincide, se asigna ese nombre.
            for (int i = 0; i < Valor.colores.length; i++)
                if (partes[2].equalsIgnoreCase(Valor.colores[i])) {
                    color = partes[2];
                    break;
                }
            // Si tras esto, el color sigue siendo NULL (cuando, al ser 3 argumentos se supone que debería coincidir
            //      con algún color), se avisa de que el color es erróneo y se sale.
            if (color == null){
                System.out.println(Valor.YELLOW + "No existe un grupo con ese color." + Valor.RESET);
                return;
            } else {
                int casasLibres = 0, hotelesLibres = 0, piscinasLibres = 0, pistadeportesLibres = 0;
                // Se recorren los cuatro lados del tablero.
                for (ArrayList<Casilla> lado : tablero.getPosiciones()) {
                    // Para cada lado del tablero, se recorren sus casillas.
                    for (Casilla c : lado) {
                        if (c.getTipo().equals("Solar") && c.getNumEdificios() == 0
                                && c.getGrupo().getNombreGrupo().equalsIgnoreCase(color)) {
                            System.out.println(c.getNombre() + ": Sin edificios.");
                            casasLibres += 4;
                            hotelesLibres += 1;
                            piscinasLibres += 1;
                            pistadeportesLibres += 1;
                        }
                        if (c.getTipo().equals("Solar") && c.getNumEdificios() > 0
                                && c.getGrupo().getNombreGrupo().equalsIgnoreCase(color)) {
                            System.out.println("{");
                            System.out.println("  propiedad: " + c.getNombre() + ",");
                            System.out.print("  hoteles: ");
                            if (c.getHotel() == 1)
                                System.out.println("[" + c.getIDHotel() + "],");
                            else
                                System.out.println("[ - ],");
                            System.out.print("  casas: ");
                            StringJoiner casas = new StringJoiner(", ", "[", "]");
                            casas.setEmptyValue("[ - ]");
                            for (int i = 0; i < c.getCasas(); i++) {
                                casas.add(c.getIDCasa(i));
                            }
                            System.out.println(casas);
                            //
                            System.out.print("  piscinas: ");
                            if (c.getPiscina() == 1)
                                System.out.println("[" + c.getIDpiscina() + "],");
                            else
                                System.out.println("[ - ],");
                            System.out.print("  pistasDeDeporte: ");
                            //
                            if (c.getPistadeporte() == 1)
                                System.out.println("[" + c.getIDpistadeporte() + "],");
                            else
                                System.out.println("[ - ],");
                            System.out.println("  alquiler: " + c.getImpuesto());
                            System.out.println("},");
                            // Se evalúa la cantidad de sitios libres para edificaciones.
                            if (c.getCasas() >= 0 && c.getHotel() == 0){
                                casasLibres += 4 - c.getCasas();
                                hotelesLibres += 1;
                                piscinasLibres += 1;
                                pistadeportesLibres += 1;
                            }
                            else if (c.getHotel() == 1 && c.getPiscina() == 0 && c.getPistadeporte() == 0) {
                                piscinasLibres += 1;
                                pistadeportesLibres += 1;
                            }
                            else if (c.getHotel() == 1 && c.getPiscina() == 1 && c.getPistadeporte() == 0)
                                pistadeportesLibres += 1;
                        }
                    }
                }
                int libres = casasLibres+hotelesLibres+piscinasLibres+pistadeportesLibres;
                if (libres == 0)
                    System.out.println("No se pueden construir más edificios en este grupo.");
                else if (casasLibres > 0 && hotelesLibres > 0 && piscinasLibres > 0 && pistadeportesLibres > 0)
                    System.out.println("Se pueden construir " + casasLibres + " casa(s), " + hotelesLibres + " hotel(es), "
                            + piscinasLibres + " piscina(s) y " + pistadeportesLibres + " pista(s) de deporte.");
                else if (casasLibres == 0 && hotelesLibres > 0 && piscinasLibres > 0 && pistadeportesLibres > 0)
                    System.out.println("No se pueden construir más casas. Se pueden construir " + hotelesLibres + " hotel(es), "
                            + piscinasLibres + " piscina(s) y " + pistadeportesLibres + " pista(s) de deporte.");
                else if (casasLibres == 0 && hotelesLibres == 0 && piscinasLibres > 0 && pistadeportesLibres > 0)
                    System.out.println("No se pueden construir más casas ni hoteles. Se pueden construir " + piscinasLibres +
                            " piscina(s) y " + pistadeportesLibres + " pista(s) de deporte");
                else if (casasLibres == 0 && hotelesLibres == 0 && piscinasLibres == 0 && pistadeportesLibres > 0)
                    System.out.println("No se pueden construir más casas, hoteles ni piscinas. Se pueden construir "
                            + pistadeportesLibres + " pista(s) de deporte.");
                else
                    System.out.println(Valor.RED+"Error al contabilizar edificios libres."+Valor.RESET);
            }
        // Si no se especifica color, el comando es listar edificios en general.
        } else {
            // Se recorren los cuatro lados del tablero.
            for (ArrayList<Casilla> lado : tablero.getPosiciones()) {
                // Para cada lado del tablero, se recorren sus casillas.
                for (Casilla c : lado) {
                    // Si la casilla es un solar, se imprimen sus edificios. Si se especifica un grupo, su color debe coincidir.
                    if (c.getTipo().equals("Solar")) {
                        // Se imprimen las casas de la casilla.
                        for (int i = 0; i < c.getCasas(); i++) {
                            System.out.println("{");
                            System.out.println("  id: " + c.getIDCasa(i) + ",");
                            System.out.println("  propietario: " + c.getDuenho().getNombre() + ",");
                            System.out.println("  casilla: " + c.getNombre() + ",");
                            System.out.println("  grupo: " + c.getGrupo().getNombreGrupo() + ",");
                            System.out.println("  coste: " + Valor.SOLAR_PRECIOS_CASA[c.getPosicion()]);
                            System.out.println("},");
                        }
                        // Se imprime el hotel.
                        for (int i = 0; i < c.getHotel(); i++) {
                            System.out.println("{");
                            System.out.println("  id: " + c.getIDHotel() + ",");
                            System.out.println("  propietario: " + c.getDuenho().getNombre() + ",");
                            System.out.println("  casilla: " + c.getNombre() + ",");
                            System.out.println("  grupo: " + c.getGrupo().getNombreGrupo() + ",");
                            System.out.println("  coste: " + Valor.SOLAR_PRECIOS_HOTEL[c.getPosicion()]);
                            System.out.println("},");
                        }
                        // Se imprime la piscina.
                        for (int i = 0; i < c.getPiscina(); i++) {
                            System.out.println("{");
                            System.out.println("  id: " + c.getIDpiscina() + ",");
                            System.out.println("  propietario: " + c.getDuenho().getNombre() + ",");
                            System.out.println("  casilla: " + c.getNombre() + ",");
                            System.out.println("  grupo: " + c.getGrupo().getNombreGrupo() + ",");
                            System.out.println("  coste: " + Valor.SOLAR_PRECIOS_PISCINA[c.getPosicion()]);
                            System.out.println("},");
                        }
                        // Se imprime la pista de deportes.
                        for (int i = 0; i < c.getPistadeporte(); i++) {
                            System.out.println("{");
                            System.out.println("  id: " + c.getIDpistadeporte() + ",");
                            System.out.println("  propietario: " + c.getDuenho().getNombre() + ",");
                            System.out.println("  casilla: " + c.getNombre() + ",");
                            System.out.println("  grupo: " + c.getGrupo().getNombreGrupo() + ",");
                            System.out.println("  coste: " + Valor.SOLAR_PRECIOS_PISTADEPORTE[c.getPosicion()]);
                            System.out.println("},");
                        }
                    }
                }
            }
        }
    }

    /**
     * Ejecuta el comando {@code listar jugadores}: muestra, para cada jugador, su
     * nombre, avatar, fortuna, propiedades, hipotecas y edificios.
     */
    private void listarJugadores() {
        if (jugadores.isEmpty()){
            System.out.println(Valor.YELLOW + "No hay jugadores en la partida." + Valor.RESET);
            return;
        }
        for (int i = 0; i < jugadores.size(); i++){
            Jugador j = jugadores.get(i);

            System.out.println("{");
            System.out.println("  nombre: " + j.getNombre() + ",");
            System.out.println("  avatar: " + j.getAvatar().getId() + ",");
            System.out.println("  fortuna: " + (long) j.getFortuna() + ",");
            // Se itera sobre las casillas que tiene el jugador y se imprime su nombre.
            System.out.print("  propiedades: ");
            StringJoiner casillas = new StringJoiner(", ", "[", "]"); casillas.setEmptyValue("[ - ]");
            for (Casilla c : j.getPropiedades()) {
                casillas.add(c.getNombre());
            }
            System.out.println(casillas + ",");
            // Se itera sobre las propiedades del jugador, y si están hipotecadas, salen aquí.
            System.out.print("  hipotecas: ");
            StringJoiner hipotecas = new StringJoiner(", ", "[", "]"); hipotecas.setEmptyValue("[ - ]");
            for (Casilla c : j.getPropiedades()) {
                if (c.isHipotecada())
                    hipotecas.add(c.getNombre());
            }
            System.out.println(hipotecas + ",");
            System.out.print("  edificios: ");
            // Hay que re-visitar las casillas que tiene el jugador, y para cada una imprimir los IDs de los edificios.
            if (j.getPropiedades().isEmpty())
                System.out.println("[-]");
            StringJoiner edificios = new StringJoiner(", ", "[", "]"); edificios.setEmptyValue("[ - ]");
            for (Casilla c : j.getPropiedades()) {
                if (c.getCasas() > 0){
                    for (int w = 0; w < c.getCasas(); w++){
                        edificios.add(c.getIDCasa(w));
                    }
                }
                if (c.getHotel() > 0)
                    edificios.add(c.getIDHotel());
                if (c.getPiscina() > 0)
                    edificios.add(c.getIDpiscina());
                if (c.getPistadeporte() > 0)
                    edificios.add(c.getIDpistadeporte());
            }
            System.out.println(edificios);
            if (jugadores.size()-1 == i){
                System.out.println("}");
            } else {
                System.out.println("},");
            }

        }
    }

    /**
     * Ejecuta el comando {@code listar enventa}: muestra los solares, transportes y
     * servicios que todavía pertenecen a la banca.
     */
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
            System.out.println(Valor.YELLOW + "No hay casillas en venta." + Valor.RESET);
        }
    }


    private void mostrarEstadisticasJuego() {
        String txtRentables = "";
        float maxRentable = -1;

        String txtFrecuentadas = "";
        int maxFrecuentada = -1;

        // 1 y 2. Casillas más rentables y más frecuentadas
        for (ArrayList<Casilla> paso : this.tablero.getPosiciones()) {
            for (Casilla cas : paso) {
                // Rentabilidad
                if (cas.getDineroGenerado() > maxRentable) {
                    maxRentable = cas.getDineroGenerado();
                    txtRentables = cas.getNombre();
                } else if (cas.getDineroGenerado() == maxRentable && maxRentable > 0) {
                    txtRentables += ", " + cas.getNombre();
                }

                // Frecuencia
                if (cas.getVecesVisitada() > maxFrecuentada) {
                    maxFrecuentada = cas.getVecesVisitada();
                    txtFrecuentadas = cas.getNombre();
                } else if (cas.getVecesVisitada() == maxFrecuentada && maxFrecuentada > 0) {
                    txtFrecuentadas += ", " + cas.getNombre();
                }
            }
        }

        // 3. Grupo más rentable (con empates)
        String txtGrupos = "";
        float maxGrupo = -1;
        for (Grupo g : this.tablero.getGrupos().values()) {
            if (g.getRentabilidadGrupo() > maxGrupo) {
                maxGrupo = g.getRentabilidadGrupo();
                txtGrupos = g.getColorGrupo();
            } else if (g.getRentabilidadGrupo() == maxGrupo && maxGrupo > 0) {
                txtGrupos += ", " + g.getColorGrupo();
            }
        }

        // 4. Jugador con más vueltas (con empates)
        String txtVueltas = "";
        int maxVueltas = -1;
        for (Jugador j : this.jugadores) {
            if (j.getVueltas() > maxVueltas) {
                maxVueltas = j.getVueltas();
                txtVueltas = j.getNombre();
            } else if (j.getVueltas() == maxVueltas) {
                txtVueltas += ", " + j.getNombre();
            }
        }

        // 5. Jugador en cabeza (uno solo)
        Jugador jugador_cabeza = null;
        for (Jugador j : this.jugadores) {
            if (jugador_cabeza == null || j.getFortunaTotal() > jugador_cabeza.getFortunaTotal()) {
                jugador_cabeza = j;
            }
        }

        // Formato de salida
        System.out.println("casilla mas rentable: " + txtRentables);
        System.out.println("grupo mas rentable: " + txtGrupos);
        System.out.println("casilla mas frecuentada: " + txtFrecuentadas);
        System.out.println("jugador mas vueltas: " + txtVueltas);
        System.out.println("jugador en cabeza: " + jugador_cabeza.getNombre());
    }



        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////// BÚSQUEDAS AUXILIARES //////////////////////////////////////////////////

    /**
     * Busca un avatar de la partida a partir de su identificador.
     *
     * @param ID Letra que identifica al avatar.
     * @return El avatar con ese identificador, o {@code null} si no existe.
     */
    private Avatar buscarAvatar(String ID) {
        for(Avatar avatar: avatares){
            if(avatar.getId().equals(ID)){
                return avatar;
            }
        }
        return null;
    }

    /**
     * Busca un jugador de la partida a partir de su nombre.
     *
     * @param nombre_jugador Nombre del jugador, tal como se dio de alta.
     * @return El jugador con ese nombre, o {@code null} si no existe.
     */
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

    /**
     * Devuelve la casilla que ocupa un índice del tablero.
     * El índice empieza en 0, así que equivale a la posición de la casilla menos 1:
     * la Salida es el 0 y Solar22 el 39. Supone que cada lado del tablero está
     * guardado en orden de juego, empezando por su esquina.
     *
     * @param indice Índice de la casilla, entre 0 y 39.
     * @return La casilla que ocupa ese índice.
     */
    private Casilla casillaEnIndice(int indice) {
        return tablero.getPosiciones().get(indice / 10).get(indice % 10);
    }

    private int getEdificiosTotal(Jugador j) {
        int resultado = 0;
        for (ArrayList<Casilla> lado : tablero.getPosiciones())
            // Para cada lado del tablero, se recorren sus casillas.
            for (Casilla c : lado)
                if (c.getTipo().equals("Solar"))
                    if (c.getDuenho().equals(j))
                        resultado += c.getNumEdificios();
        return resultado;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
