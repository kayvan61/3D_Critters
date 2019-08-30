package assignment5;

import javafx.scene.paint.Color;

public class Critter1 extends Critter {

    int myEnergy;

    public Critter1 () {
        myEnergy = Params.START_ENERGY;
    }

    @Override
    public CritterShape viewShape() {
        return CritterShape.STAR;
    }

    @Override
    public javafx.scene.paint.Color viewOutlineColor() {
        return Color.BLUE;
    }

    @Override
    public javafx.scene.paint.Color viewFillColor() {
        return Color.BLUE;
    }

    @Override
    public void doTimeStep() {

        //make sure we can always fight well
        if(myEnergy >= 100) {
            if (getRandomInt(100) < 12) {
                walk(getRandomInt(8));
                myEnergy -= Params.WALK_ENERGY_COST;
            } else if (getRandomInt(100) < 12) {
                run(getRandomInt(8));
                myEnergy -= Params.RUN_ENERGY_COST;
            }
        }
        if(myEnergy > Params.MIN_REPRODUCE_ENERGY*2) {
            Critter1 child = new Critter1();
            reproduce(child, 0);
        }
    }

    @Override
    public boolean fight(String oponent) {
        if(oponent.equals("2")) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "1";
    }
}
