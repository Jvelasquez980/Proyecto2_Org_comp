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
    public int entX;
    public int entY;

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

    }

    @Override
    public void run() {
        explorarAreaRiesgo();
    }

    public void explorarAreaRiesgo() {
        while (true) {

            if (this.init) {
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
            } else if (this.acerca) {
                while (getAvenue() != 5) {
                    move();
                }
                this.acerca = false;
                this.buscamos = true;
            } else if (this.buscamos == true && this.nojabrimospa == false) {

                while (getStreet() < 9 && this.inside == false) {
                    if (frontIsClear()) {
                        entX = getAvenue();
                        entY = getStreet();
                        move();
                        this.inside = true;
                        this.buscamos = false;
                        System.out.println("Entré");

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
                while (getAvenue() < 15 && this.inside == false) {
                    if (frontIsClear()) {
                        entX = getAvenue();
                        entY = getStreet();
                        move();
                        this.inside = true;
                        this.buscamos = false;
                        System.out.println("Entré");

                    } else {
                        turnLeft();
                        move();
                        turnLeft();
                        turnLeft();
                        turnLeft();
                    }
                }
            }else
                if (this.inside && this.nojabrimospa == false) {
                    int c = 0;
                    while (true) {
                        if (nextToABeeper() && this.nojabrimospa == false) {
                            if (!nextToARobot()) {
                                recogerPersonas();
                                if (this.personasRescatadas == 4) {
                                    this.nojabrimospa = true;
                                    System.out.println("Meloski");
                                    break;
                                }
                            }
                        }
                        if (frontIsClear() && c != 2) {
                            move();
                            c = 0;

                        } else if (c != 2) {
                            turnLeft();
                            c = c + 1;
                        } else if (c == 2) {
                            turnLeft();
                            c = c + 1;
                        }
                    }
                } else if (this.nojabrimospa == true && this.inside == true) {
                    turnLeft();
                    turnLeft();
                    int c = 0;
                    while (true) {

                        if (frontIsClear() && c != 2) {
                            move();
                            c = 0;
                            if (getAvenue() == entX && getStreet() == entY) {
                                this.inside = false;
                                System.out.println("Salí");
                                break;
                            }

                        } else if (c != 2) {
                            turnLeft();
                            c = c + 1;
                        } else if (c == 2) {
                            turnLeft();
                            c = c + 1;
                        }
                    }
                } else if (this.nojabrimospa == true && this.inside == false) {
                    if (getAvenue() == 15 && getStreet() != 9) {
                        turnLeft();
                        while (getStreet() != 9) {
                            move();
                        }
                    } else if (getStreet() == 9 && getAvenue() != 5) {
                        turnLeft();
                        while (getAvenue() != 5) {
                            move();

                        }

                    } else if (getAvenue() == 5 && getStreet() != 1) {
                        turnLeft();
                        while (getStreet() != 1) {
                            move();
                        }

                    } else if (getStreet() == 1 && getAvenue() != 1) {
                        turnLeft();
                        turnLeft();
                        turnLeft();
                        while (getAvenue() != 1) {
                            move();
                        }
                    } else if (getStreet() == 1 && getAvenue() == 1) {
                        while (anyBeepersInBeeperBag()) {
                            putBeeper();
                            
                        }
                        turnOff();
                    }
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

    public int getStreet() {
        return street;
    }

    public int getAvenue() {
        return avenue;
    }

    public static void main(String[] args) {
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
