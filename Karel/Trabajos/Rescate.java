import kareltherobot.*;
import java.awt.Color;
import java.util.*;

class Rescate extends Robot implements Runnable {
    private static final int MAX_PERSONAS = 4;
    private int personasRescatadas = 0;
    private int id;
    private int street;
    private int avenue;
    private boolean inside;
    private boolean init;
    private boolean acerca;
    private boolean buscamos;
    private boolean nojabrimospa;
    private boolean atrapados;
    public int entX;
    public int entY;
    private int c;

    public Rescate(int id, int street, int avenue, Direction direction, int beepers, Color color) {
        super(street, avenue, direction, beepers, color);
        this.id = id;
        this.avenue = avenue;
        this.street = street;
        this.inside = false;
        this.init = true;
        this.acerca = false;
        this.buscamos = false;
        this.nojabrimospa = false;
        this.atrapados = false;
        this.c = 0;

    }

    @Override
    public void run() {
        explorarAreaRiesgo();
    }

    public void explorarAreaRiesgo() {
        this.c = 0;
        while (true) {

            if (this.init) { // En este paso se inicia
                inicio();
            } else if (this.acerca) { // Se acercan al area de riesgo
                acercamiento();
            } else if (this.buscamos == true && this.nojabrimospa == false && entX == 0 && entY == 0) { // Empiezan a buscar rodeando el area de riesgo
                busqueda_de_Entrada();
            } else if (this.inside == true && this.nojabrimospa == false && RescateManager.vacio != true) { // Al
                exploracion_interior(); // encontrar la entrada del edificio se empieza a recorrer el mismo, este proceso se detiene si se dan cuenta que no hay mas

            } else if (this.nojabrimospa == true && this.inside == true) {
                salimos();
            } else if (this.nojabrimospa == true && this.inside == false) {
                regreso_a_base();
            } else if (this.buscamos == true && this.nojabrimospa == false && entX != 0 && entY != 0) {
                regreso_al_recinto();
            }else if(RescateManager.vacio == true){
                this.nojabrimospa = true;
            }
        }
    }

    private void recogerPersonas() {
        while (nextToABeeper() && personasRescatadas < MAX_PERSONAS) {
            pickBeeper();
            personasRescatadas++;
            // Comunicar a otros robots la posición (Implementar)
        }
    }

    public void move() {
        super.move();
        actualizarPosicion();
    }

    private void actualizarPosicion() {
        if (facingNorth()) {
            street++;
        } else if (facingSouth()) {
            street--;
        } else if (facingEast()) {
            avenue++;
        } else if (facingWest()) {
            avenue--;
        }
    }

    private void inicio() {
        while (facingSouth() == false) {
            turnLeft();
        }
        while (getStreet() != 1) {
            move();
        }
        while (facingEast() == false) {
            turnLeft();
        }
        this.init = false;
        this.acerca = true;
    }

    private void acercamiento() {
        while (getAvenue() != 5) {
            move();
        }
        this.acerca = false;
        this.buscamos = true;
    }

    private void busqueda_de_Entrada() {
        while (getStreet() < 9 && this.inside == false) {
            if (frontIsClear()) {
                entX = getAvenue(); // Guardamos la entrada del recito para poder llegar mas rapido la proxima
                entY = getStreet();
                move();
                this.inside = true;
                this.buscamos = false;

            } else {
                turnLeft();
                move();
                turnLeft();
                turnLeft();
                turnLeft();
            }
        }
        if (!this.inside) {
            move();
            turnLeft();
            turnLeft();
            turnLeft();
        }
        while (getAvenue() < 15 && this.inside == false) { // Comienza a buscar la entrada del area de riesgo
            if (frontIsClear()) {
                entX = getAvenue();
                entY = getStreet();
                move();
                this.inside = true;
                this.buscamos = false;

            } else {
                turnLeft();
                move();
                turnLeft();
                turnLeft();
                turnLeft();
            }
        }
    }

    private void exploracion_interior() {
        while (RescateManager.vacio == false) {
            if (nextToABeeper() && this.nojabrimospa == false) {
                this.atrapados = true;
                if (!nextToARobot()) {
                    recogerPersonas();
                    if (this.personasRescatadas == 4) {
                        this.nojabrimospa = true;
                        break;
                    }
                }
            }
            if (this.c > 4 && this.atrapados == false && this.nojabrimospa == false) {
                RescateManager.vacio = true;
                this.nojabrimospa = true;
                break;
            } else if (this.c > 4 && this.atrapados == true) {
                this.nojabrimospa = true;
                break;
            } else if (frontIsClear() && this.c != 2) {
                move();
                this.c = 0;

            } else if (this.c != 2) {
                turnLeft();
                this.c = this.c + 1;
            } else if (this.c == 2) {
                turnLeft();
                this.c = this.c + 1;
            }
        }
        if (RescateManager.vacio == true) {
            this.nojabrimospa = true;
        }

    }

    private void salimos() {
        if (this.c == 0) {

            turnLeft();
            turnLeft();
        } else if (this.c == 1) {
            turnLeft();
        } else if (this.c == 2) {

        } else if (this.c == 3) {
            turnLeft();
            turnLeft();
            turnLeft();
        } else if (this.c == 4) {
            turnLeft();
            turnLeft();
        } else if (this.c == 5) {
            turnLeft();
        }
        this.c = 0;
        while (true) {
            if (frontIsClear() && this.c != 2) {
                move();
                this.c = 0;
                if (getAvenue() == entX && getStreet() == entY) {
                    this.inside = false;
                    this.atrapados = false;
                    break;
                }

            } else if (this.c != 2) {
                turnLeft();
                this.c = this.c + 1;
            } else if (this.c == 2) {
                turnLeft();
                this.c = this.c + 1;
            }
        }
    }

    private void regreso_a_base() {
        if (getAvenue() == 15 && getStreet() != 9) {
            while (!facingNorth()) {
                turnLeft();
            }
            while (getStreet() != 9) {
                move();
            }
        } else if (getStreet() == 9 && getAvenue() != 5) {
            while (!facingWest()) {
                turnLeft();
            }
            while (getAvenue() != 5) {
                move();

            }

        } else if (getAvenue() == 5 && getStreet() != 1) {
            while (!facingSouth()) {
                turnLeft();
            }
            while (getStreet() != 1) {
                move();
            }

        } else if (getStreet() == 1 && getAvenue() != 1) {
            while (!facingWest()) {
                turnLeft();
            }
            while (getAvenue() != 1) {
                move();
            }
        } else if (getStreet() == 1 && getAvenue() == 1) {
            while (anyBeepersInBeeperBag()) {
                putBeeper();
                this.personasRescatadas = this.personasRescatadas - 1;

            }
            if (RescateManager.vacio == true) {
                while (!facingNorth()) {
                    turnLeft();
                }
                while (getStreet() != this.id) {
                    move();
                }
                turnOff();
            } else {
                this.init = true;
                this.nojabrimospa = false;

            }
        }
    }

    private void regreso_al_recinto() {
        while (!facingNorth()) {
            turnLeft();
        }
        if (entX == 5) {
            while (getStreet() != entY) {
                move();
            }
            while (!facingEast()) {
                turnLeft();
            }
            move();
            this.buscamos = false;
            this.inside = true;
        } else if (entX == 15) {
            while (getStreet() != 9) {
                move();
            }
            while (!facingEast()) {
                turnLeft();
            }
            while (getAvenue() != 15) {
                move();
            }
            while (!facingSouth()) {
                turnLeft();
            }
            while (getStreet() != entY) {
                move();
            }
            while (!facingWest()) {
                turnLeft();
            }
            move();
            this.buscamos = false;
            this.inside = true;
        } else if (entY == 9) {
            while (getStreet() != 9) {
                move();
            }
            while (!facingEast()) {
                turnLeft();
            }
            while (getAvenue() != entX) {
                move();
            }
            while (!facingSouth()) {
                turnLeft();
            }
            move();
            this.buscamos = false;
            this.inside = true;
        }
    }

    public int getStreet() {
        return street;
    }

    public int getAvenue() {
        return avenue;
    }

    public static void main(String[] args) {
        World.setDelay(7); // Configura la velocidad al mínimo
        World.readWorld("Mundo.kwld");
        World.setVisible(true);

        int cantidadRobots = Integer.parseInt(args[1]); // Captura la cantidad de robots desde el argumento `-r`
        for (int i = 0; i < cantidadRobots; i++) {
            Rescate robot = new Rescate(i + 1, i + 1, 1, East, 0, getRandomColor());
            Thread thread = new Thread(robot);
            thread.start();
        }
    }

    private static Color getRandomColor() {
        Color[] colores = { Color.blue, Color.red, Color.green, Color.yellow, Color.orange, Color.pink, Color.magenta,
                Color.cyan };
        return colores[(int) (Math.random() * colores.length)];
    }
}

class RescateManager {
    public static volatile boolean vacio = false;
}