<div align="center">

# 🎲 Monopoly ETSE

**Versión en consola del Monopoly clásico, desarrollada en Java**

Programación Orientada a Objetos · Grado en Ingeniería Informática · Universidade de Santiago de Compostela

![Java](https://img.shields.io/badge/Java-17%2B-orange?logo=openjdk&logoColor=white)
![Curso](https://img.shields.io/badge/curso-2025--2026-blue)
![Entrega](https://img.shields.io/badge/entrega-Parte%201-green)

</div>

---

## 📖 Sobre el proyecto

Implementación del Monopoly sobre un tablero de 40 casillas que se pinta en modo texto, con cada grupo de solares en su color y los avatares de los jugadores sobre su casilla. Los jugadores interactúan con la partida mediante comandos escritos en consola o leídos desde un fichero.

El proyecto se desarrolla en varias entregas a lo largo del curso. Este repositorio contiene la **Parte 1**.

## 🗂️ Estructura

```
src/
├── monopoly/
│   ├── MonopolyETSE.java   Punto de entrada (main)
│   ├── Menu.java           Bucle de juego e intérprete de comandos
│   ├── Tablero.java        Casillas, grupos y pintado del tablero
│   ├── Casilla.java        Cada una de las 40 casillas
│   ├── Grupo.java          Grupos de solares por color
│   └── Valor.java          Constantes: precios, alquileres y colores
└── partida/
    ├── Jugador.java        Jugadores y banca
    ├── Avatar.java         Ficha de cada jugador en el tablero
    └── Dado.java           Tiradas de dados
```

## ▶️ Compilar y ejecutar

Desde la raíz del repositorio:

```bash
javac -encoding UTF-8 -d out src/monopoly/*.java src/partida/*.java
java -cp out monopoly.MonopolyETSE
```

> Los colores del tablero usan códigos ANSI. Si en la consola de IntelliJ aparecen caracteres como `[32m`, ejecútalo desde una terminal.

## ⌨️ Comandos

| Comando | Descripción |
|---|---|
| `crear jugador <nombre> <avatar>` | Da de alta un jugador (`coche`, `esfinge`, `sombrero` o `pelota`) |
| `jugador` | Muestra el jugador que tiene el turno |
| `listar jugadores` | Lista los jugadores con su fortuna y propiedades |
| `listar avatares` | Lista los avatares de la partida |
| `listar enventa` | Muestra las propiedades que aún pertenecen a la banca |
| `lanzar dados` | Tira los dados y mueve el avatar |
| `lanzar dados <X+Y>` | Tira con valores forzados, por ejemplo `lanzar dados 2+4` |
| `acabar turno` | Pasa el turno al siguiente jugador |
| `salir carcel` | Paga la fianza y sale de la Cárcel |
| `describir <casilla>` | Muestra la información de una casilla |
| `describir jugador <nombre>` | Muestra la información de un jugador |
| `describir avatar <ID>` | Muestra la información de un avatar |
| `comprar <casilla>` | Compra la casilla en la que está el avatar |
| `ver tablero` | Imprime el tablero |
| `comandos <fichero>` | Ejecuta los comandos de un fichero, uno por línea |

### Ejemplo de partida

```
$> crear jugador Pedro coche
$> crear jugador Maria pelota
$> lanzar dados
$> comprar Solar1
$> acabar turno
$> describir jugador Pedro
```

## 🎯 Reglas principales

| Concepto | Valor |
|---|---|
| Fortuna inicial | 15.000.000 € |
| Cobro al pasar por Salida | 2.000.000 € |
| Precio de transportes y servicios | 500.000 € |
| Alquiler de transporte | 250.000 € |
| Factor de servicio | 50.000 € |
| Casillas de impuestos | 2.000.000 € (van al bote del Parking) |
| Salir de la Cárcel | 500.000 € |

Tres dobles seguidos envían el avatar a la Cárcel. Caer en el Parking da derecho a cobrar el bote acumulado.

## ✅ Estado de la Parte 1

- [ ] Tablero en consola con colores y avatares
- [ ] Alta de jugadores y avatares
- [ ] Turnos y lanzamiento de dados (aleatorio y forzado)
- [ ] Alquileres, impuestos, Parking e Ir a Cárcel
- [ ] Salida de la Cárcel
- [ ] Compra de propiedades
- [ ] Comandos de descripción y listado
- [ ] Bancarrota
- [ ] Lectura de comandos desde fichero

## 🤝 Forma de trabajo

1. `git pull` antes de empezar a trabajar.
2. Commits pequeños con un mensaje que diga qué cambia.
3. `git pull` y después `git push` al terminar cada sesión, también las de Code With Me.
4. Nunca `git push --force` sin avisar al resto del grupo.

## 👥 Autores

- [Adam Pérez González](https://github.com/adampgbit)
- [Gabriel Prieto Martínez](https://github.com/GabrielPrietoMartinez)
- [Julio Pazos Morales](https://github.com/JulioPM436)
