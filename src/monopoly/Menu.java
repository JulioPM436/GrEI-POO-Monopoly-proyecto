package monopoly;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import partida.*;
import java.util.Objects;
import java.util.Scanner;

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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////// MÉTODOS PRINCIPALES ///////////////////////////////////////////////////
    // Aquí se colocan los métodos más importantes o que por facilidades al escribir código deben estar arriba del todo.

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
                    System.out.println("Comando incorrecto. Formato: comprar <casilla>");
                    break;
                }
                comprar(partes[1]);
                break;

            case "crear":
                crearJugador(partes);
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

            case "edificar":
                if (partes.length < 2 || partes.length > 3) {
                    System.out.println(Valor.ERROR_USO + "edificar casa|hotel|piscina|pista_deporte");
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

            case "jugador":
                turnoActual();
                break;

            case "lanzar":
                lanzarDados(partes);
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
                    default:
                        System.out.println(Valor.ERROR_USO + "listar jugadores|avatares|enventa|edificios [<color>]");
                }
                break;
            case "salir":
                if (partes[1].equals("carcel")) {
                    salirCarcel();
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
                System.out.println("\tedificar casa|hotel|piscina|pista_deporte");
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
                System.out.println(Valor.ERROR + "Comando desconocido: " + comando);
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////// MÉTODOS SECUNDARIOS ///////////////////////////////////////////////////

    /**
     * Ejecuta el comando {@code acabar turno}: pasa el turno al siguiente jugador,
     * siguiendo el orden en que se dieron de alta, y lo muestra por pantalla.
     */
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

    /**
     * Ejecuta el comando {@code comprar <casilla>}: el jugador con el turno compra
     * la casilla indicada. Solo es posible si su avatar está en ella, si todavía
     * pertenece a la banca y si el jugador tiene dinero suficiente.
     *
     * @param nombre Nombre de la casilla que se quiere comprar.
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
        System.out.println("Su fortuna actual es de " + (long)getJugadorActual().getFortuna() + "€.");
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
            if (!partes[3].equalsIgnoreCase("Coche") &&!partes[3].equalsIgnoreCase("Esfinge") &&!partes[3].equalsIgnoreCase("Sombrero") && !partes[3].equalsIgnoreCase("Pelota")) {
                System.out.println("ERROR: Tipo de avatar no admitido. Permitidos: Coche, Esfinge, Sombrero, Pelota.");
                return;
            }
            if (jugadores.size() < 4) {
                // Se trocea el comando para tomar cada argumento
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
                System.out.println("{");
                System.out.println("  nombre: " + j.getNombre() + ",");
                System.out.println("  avatar: " + j.getAvatar().getId());
                System.out.println("}");
                System.out.println(tablero);
            } else {
                System.out.println("ERROR: No se pueden crear más de 4 jugadores.");
            }
        }
        else {
            // Si no, se asume que el comando era erróneo y no hace nada.
            System.out.println(Valor.ERROR_USO + "crear jugador <nombre> <coche|esfinge|sombrero|pelota>");
        }
    }

    /**
     * Ejecuta el comando {@code describir avatar <ID>}: muestra el identificador,
     * el tipo, la casilla en la que está y el jugador al que pertenece el avatar.
     *
     * @param ID Letra que identifica al avatar.
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
            System.out.println("No existe ninguna casilla con el nombre: " + nombre);
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
            System.out.println("No existe ninguna casilla con el nombre: " + nombrecasilla);
            return;
        }

        if (casilla.getDuenho() == null || casilla.getDuenho() != jugadorActual) {
            System.out.println(jugadorActual.getNombre() + " no puede deshipotecar " + casilla.getNombre() + ". No es una propiedad que le pertenece.");
            return;
        }
        if (!casilla.isHipotecada()) {
            System.out.println(jugadorActual.getNombre() + " no puede deshipotecar " + casilla.getNombre() + ". No está hipotecada.");
            return;
        }
        float sumaHipoteca = casilla.getValorHipoteca();
        if(jugadorActual.getFortuna() < sumaHipoteca){
            System.out.println("No puede deshipotecar porque no tiene saldo suficiente");
            return;
        }

        jugadorActual.sumarFortuna(-sumaHipoteca);
        casilla.setHipotecada(false);
        String grupo = casilla.getGrupo().getColorGrupo();
        System.out.println(jugadorActual.getNombre() + "paga" + sumaHipoteca + "para deshipotecar " +casilla.getNombre() + ". Ahora puede cobrar alquileres y edificar en el grupo " + grupo);

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
            System.out.println("Aún no hay jugadores en la partida.\n");
            return;
        }
        // Obtenemos los datos del jugador,casilla en la que está y el grupo.
        Jugador actual = getJugadorActual();
        assert actual != null;
        int actualCasillaPos = actual.getAvatar().getLugar().getPosicion();
        Casilla actualCasilla = actual.getAvatar().getLugar();
        Grupo actualGrupo = actual.getAvatar().getLugar().getGrupo();
        String actualCasillaTipo = actualCasilla.getTipo();
        int numeroCasas = actualCasilla.getCasas();
        int hayHotel = actualCasilla.getHotel();
        int hayPiscina = actualCasilla.getPiscina();
        int hayPistadeporte = actualCasilla.getPistadeporte();
        float precio;
        // Sólo se puede edificar en los solares.
        if (!actualCasillaTipo.equals("Solar")){
            System.out.println("ERROR: Sólo se puede edificar en los solares.");
            return;
        }
        // Si es dueño de todas las casillas del grupo, puede edificar.
        if (actualGrupo.esDuenhoGrupo(actual)) {
            // Si es dueño del grupo, puede empezar a construir.
            switch (partes[1]) {
                case "casa":
                    // Si hay 4 casas edificadas o si hay un hotel, no se pueden construir más casas.
                    if (numeroCasas >= 4){
                        System.out.println("Ya hay 4 casas construidas.");
                        return;
                    }
                    if (hayHotel == 1) {
                        System.out.println("No se pueden edificar más casas si ya hay un hotel.");
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
                            System.out.println("La cantidad debe ser un número entero entre 1 y 4.");
                        }
                        // Si no se especifica cantidad, se asume que se compra una sola casa.
                    } else if (partes.length == 2) {
                        cantidad = 1;
                    }
                    // Se revisa el número introducido. No pueden construirse más de 4 casas o pasar un número negativo.
                    if (partes.length == 3 || cantidad == 1) {
                        if (cantidad > 4 || cantidad <= 0) {
                            System.out.println("ERROR: No puedes construir más de 4 casas en una casilla.");
                        } else {
                            // Se calculan la cantidad de casas disponibles para construir restando las casas que pretendemos
                            //      construir al máximo de cuatro. No podemos edificar más casas de las que hay disponibles.
                            int disponibles = 4-numeroCasas;
                            if (disponibles < cantidad) {
                                System.out.println("No puedes construir más de 4 casas en una casilla. Quedan " + disponibles +
                                        " espacios para construir casas en este solar.");
                            } else {
                                // El precio de las casas es el precio de la casa por la cantidad que compre.
                                precio = (cantidad * Valor.SOLAR_PRECIOS_CASA[actualCasillaPos]);
                                // Pero primero debe poder pagarlas.
                                if (actual.getFortuna() >= precio) {
                                    // Se le resta el valor de la casa en esa casilla, tantas veces como casas compre.
                                    actual.sumarFortuna(-precio);
                                    // Se actualiza el número de casas.
                                    actualCasilla.setCasa(cantidad);
                                    // Por cosmética, se imprime un mensaje dependiendo de si se compra una casa o varias.
                                    if (cantidad == 1) {
                                        System.out.println("Se ha edificado una casa en " + actualCasilla.getNombre() + ". La " +
                                                "fortuna de " + actual.getNombre() + " se reduce en " + precio + "€.");
                                    } else {
                                        System.out.println("Se han edificado " + cantidad + " casas en " + actualCasilla.getNombre() +
                                                ". La fortuna de " + actual.getNombre() + " se reduce en " + precio + "€.");
                                    }
                                } else {
                                    System.out.println("ERROR: Dinero insuficiente.");
                                }
                            }
                        }
                    }
                    break;
                case "hotel":
                    // Aquí no puede admitir una cantidad (no serviría de nada).
                    if (partes.length > 2){
                        System.out.println("ERROR: edificar casa <cantidad>|hotel|piscina|pistadeporte");
                        return;
                    }
                    // Si ya hay hotel, o si no hay 4 casas, no se puede construir un hotel.
                    if (hayHotel == 1){
                        System.out.println("Ya se ha construido un hotel en esta casilla.");
                        return;
                    }
                    if (numeroCasas != 4){
                        System.out.println("Debes tener 4 casas para construir un hotel.");
                        return;
                    }
                    // El precio de las casas es el precio de la casa por la cantidad que compre.
                    precio = (Valor.SOLAR_PRECIOS_HOTEL[actualCasillaPos]);
                    // Pero primero debe poder pagarlas.
                    if (actual.getFortuna() >= precio) {
                        // Se le resta el valor del hotel a la fortuna del jugador.
                        actual.sumarFortuna(-precio);
                        // Se actualiza la cantidad de hoteles.
                        actualCasilla.setHotel();
                        // Se imprime el aviso de la compra.
                        System.out.println("Se ha edificado un hotel en " + actualCasilla.getNombre() + ". La " +
                                "fortuna de " + actual.getNombre() + " se reduce en " + precio + "€.");
                    } else {
                        System.out.println("ERROR: Dinero insuficiente.");
                    }
                    break;
                case "piscina":
                    // Aquí no puede admitir una cantidad (no serviría de nada).
                    if (partes.length > 2) {
                        System.out.println("ERROR: edificar casa <cantidad>|hotel|piscina|pistadeporte");
                        return;
                    }
                    if (hayHotel == 0) {
                        System.out.println("No se puede edificar una piscina, ya que no se dispone de un hotel.");
                        return;
                    }
                    if (hayPiscina == 1) {
                        System.out.println("Ya se dispone de una piscina.");
                        return;
                    }
                    // El precio de las casas es el precio de la casa por la cantidad que compre.
                    precio = (Valor.SOLAR_PRECIOS_PISCINA[actualCasillaPos]);
                    // Pero primero debe poder pagarlas.
                    if (actual.getFortuna() >= precio) {
                        // Se le resta el valor de la casa en esa casilla, tantas veces como casas compre.
                        actual.sumarFortuna(-precio);
                        // Se actualiza el número de casas.
                        actualCasilla.setPiscina();
                        // Se imprime el aviso de la compra.
                        System.out.println("Se ha edificado una piscina en " + actualCasilla.getNombre() + ". La " +
                                "fortuna de " + actual.getNombre() + " se reduce en " + precio + "€.");
                    }
                    break;
                case "pistadeporte":
                    // Aquí no puede admitir una cantidad (no serviría de nada).
                    if (partes.length > 2) {
                        System.out.println("ERROR: edificar casa <cantidad>|hotel|piscina|pistadeporte");
                        return;
                    }
                    if (hayHotel == 0) {
                        System.out.println("No se puede edificar una pista de deporte, ya que no se dispone de un hotel.");
                        return;
                    }
                    if (hayPiscina == 0) {
                        System.out.println("No se puede edificar una pista de deporte, ya que no se dispone de una piscina.");
                        return;
                    }
                    if (hayPistadeporte == 1) {
                        System.out.println("Ya se dispone de una pista de deporte.");
                        return;
                    }
                    // El precio de las casas es el precio de la casa por la cantidad que compre.
                    precio = (Valor.SOLAR_PRECIOS_PISTADEPORTE[actualCasillaPos]);
                    // Pero primero debe poder pagarlas.
                    if (actual.getFortuna() >= precio) {
                        // Se le resta el valor de la casa en esa casilla, tantas veces como casas compre.
                        actual.sumarFortuna(-precio);
                        // Se actualiza el número de casas.
                        actualCasilla.setPistadeporte();
                        // Se imprime el aviso de la compra.
                        System.out.println("Se ha edificado una pista de deporte en " + actualCasilla.getNombre() + ". La " +
                                "fortuna de " + actual.getNombre() + " se reduce en " + precio + "€.");
                    }
                    break;
                default:
                    System.out.println("ERROR: edificar casa <cantidad>|hotel|piscina|pistadeporte");
                    break;
            }
        } else {
            System.out.println("ERROR: Debes ser dueño de todo el grupo para poder construir.");
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
            System.out.println("No existe dicha casilla1");
            return;
        }

        if(casilla.getDuenho() != jugadoractual){
            System.out.println(jugadoractual.getNombre() + "no puede hipotecar" + casilla.getNombre() + "Esta casilla no pertenece al jugador actual");
            return;
        }

        if(casilla.isHipotecada()){
            System.out.println("Esta casilla ya está hipotecada");
            return;
        }

        if (casilla.getTipo().equalsIgnoreCase("Solar") && casilla.tieneEdificios()) {
            System.out.println(jugadoractual.getNombre() + " no puede hipotecar " + casilla.getNombre() + ". Se deben vender todos los edificios antes.");
            return;
        }

        casilla.setHipotecada(true);
        float sumaHipoteca = casilla.getValorHipoteca();
        jugadoractual.sumarFortuna(sumaHipoteca);

        String grupo = casilla.getGrupo().getColorGrupo();

        System.out.println("El jugador" + jugadoractual.getNombre() + "recibe" + sumaHipoteca + "por hipotecar" + casilla.getNombre() + "No puede edificar ni cobrar alquilerers en el grupo" + grupo);

    }

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
     * Ejecuta el comando {@code listar avatares}: muestra el identificador, el tipo,
     * la casilla y el jugador de cada avatar de la partida.
     */
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

    /**
     * Ejecuta el comando {@code listar jugadores}: muestra, para cada jugador, su
     * nombre, avatar, fortuna, propiedades, hipotecas y edificios.
     */
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
            System.out.println("No hay casillas en venta.");
        }
    }

    /**
     * Lleva el avatar del jugador hasta una casilla concreta, avanzando por el
     * tablero. Si por el camino pasa por la Salida, cobra lo que le corresponde
     * (de eso se encarga {@code moverAvatar}). Pensado para las cartas que mandan
     * a una casilla determinada.
     *
     * @param actual  Jugador cuyo avatar se mueve.
     * @param destino Casilla a la que tiene que llegar.
     */
    private void moverHasta(Jugador actual, Casilla destino) {
        int pasos = (destino.getPosicion() - actual.getAvatar().getLugar().getPosicion() + 40) % 40;
        actual.getAvatar().moverAvatar(tablero.getPosiciones(), pasos);
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
        } else if (casilla.getTipo().equals("Suerte") || casilla.getTipo().equals("Comunidad")) {
            //sacarCarta(actual, casilla.getTipo());
        }

        else {
            casilla.evaluarCasilla(actual, banca, suma, tablero.encontrar_casilla("Parking"));
        }
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
            System.out.println(actual.getNombre() + " no puede pagar " + (long) cantidad
                    + "€. Debe hipotecar alguna propiedad o declararse en bancarrota.");
            return false;
        }
        actual.sumarFortuna(-cantidad);
        actual.sumarGastos(cantidad);
        return true;
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

    /**
     * Saca la siguiente carta de Suerte o de Caja de Comunidad, muestra su
     * descripción y aplica su acción al jugador. Las cartas de cada mazo salen en
     * orden y, después de la última, se vuelve a empezar por la primera.
     *
     * @param j    Jugador que ha caído en la casilla.
     * @param tipo Tipo de la casilla en la que ha caído (Suerte o Caja de
     *             Comunidad), que indica de qué mazo se saca la carta.
     */
    private void sacarCarta(Jugador j, String tipo){
        if (tipo.equals("Suerte")){
            numCartaSuerte ++;
            switch (numCartaSuerte) {
                case 1:
                    System.out.println(CARTAS_SUERTE[0]);

                    break;

                case 2:
                    System.out.println(CARTAS_SUERTE[1]);
                    break;

                case 3:
                    System.out.println(CARTAS_SUERTE[2]);
                    getJugadorActual().sumarFortuna(1000000);
                    break;

                case 4:
                    System.out.println(CARTAS_SUERTE[3]);
                    int numJugadores = jugadores.size();
                    pagar(Objects.requireNonNull(getJugadorActual()),250000 * numJugadores);
                    for(int i = 0; i < numJugadores; i++){
                        Jugador jey = jugadores.get(i);
                        if(jey != getJugadorActual()){
                            jey.sumarFortuna(250000);
                        }
                    }
                    break;

                case 5:
                    System.out.println(CARTAS_SUERTE[4]);
                    break;

                case 6:
                    System.out.println(CARTAS_SUERTE[5]);
                    pagar(Objects.requireNonNull(getJugadorActual()),150000);
                    break;

                case 7:
                    System.out.println(CARTAS_SUERTE[6]);
                    break;
                default:
                    System.out.println("ERROR, esto no es una posicion de carta valida, esto nunca lo deberia leer el ususario");
                    break;
            }
        }
        else{
            switch (numCartaComunidad) {
                case 1:
                    System.out.println(CARTAS_COMUNIDAD[0]);
                    pagar(Objects.requireNonNull(getJugadorActual()),150000);
                    break;

                case 2:
                    System.out.println(CARTAS_COMUNIDAD[1]);
                    break;

                case 3:
                    System.out.println(CARTAS_COMUNIDAD[2]);
                    break;

                case 4:
                    System.out.println(CARTAS_COMUNIDAD[3]);
                    getJugadorActual().sumarFortuna(500000);
                    break;

                case 5:
                    System.out.println(CARTAS_COMUNIDAD[4]);
                    break;

                case 6:
                    System.out.println(CARTAS_COMUNIDAD[5]);
                    break;

                default:
                    System.out.println("ERROR, esto no es una posicion de carta valida, esto nunca lo deberia leer el ususario");
                    break;
            }

        }
    }

    /**
     * Ejecuta el comando {@code salir carcel}: el jugador con el turno paga la
     * fianza (500.000€) y sale de la Cárcel, tras lo cual puede lanzar los dados.
     * Solo es posible si está en la Cárcel y tiene dinero suficiente.
     */
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

    /**
     * Ejecuta el comando {@code jugador}: muestra el nombre y el avatar del jugador
     * que tiene el turno.
     */
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
}
