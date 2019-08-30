/*
 * CRITTERS Critter.java
 * EE422C Project 5 submission by
 * Replace <...> with your actual data.
 * <Student1 Name>
 * <Student1 EID>
 * <Student1 5-digit Unique No.>
 * <Student2 Name>
 * <Student2 EID>
 * <Student2 5-digit Unique No.>
 * Slip days used: <0>
 * Spring 2019
 */

package assignment5;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.effect.Bloom;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;

import java.util.*;

/*
 * See the PDF for descriptions of the methods and fields in this
 * class.
 * You may add fields, methods or inner classes to Critter ONLY
 * if you make your additions private; no new public, protected or
 * default-package code or data can be added to Critter.
 */

public abstract class Critter {

    /* START --- NEW FOR PROJECT 5 */
    public enum CritterShape {
        CIRCLE,
        SQUARE,
        TRIANGLE,
        DIAMOND,
        STAR
    }

    /* the default color is white, which I hope makes critters invisible by default
     * If you change the background color of your View component, then update the default
     * color to be the same as you background
     *
     * critters must override at least one of the following three methods, it is not
     * proper for critters to remain invisible in the view
     *
     * If a critter only overrides the outline color, then it will look like a non-filled
     * shape, at least, that's the intent. You can edit these default methods however you
     * need to, but please preserve that intent as you implement them.
     */
    public javafx.scene.paint.Color viewColor() {
        return javafx.scene.paint.Color.WHITE;
    }

    public javafx.scene.paint.Color viewOutlineColor() {
        return viewColor();
    }

    public javafx.scene.paint.Color viewFillColor() {
        return viewColor();
    }

    public abstract CritterShape viewShape();

    protected final String look(int direction, boolean steps) {
        int look_x = x_coord;
        int look_y = y_coord;

        switch(direction) {
            case 0:
                look_x += (steps ? 2 : 1);
                break;
            case 1:
                look_x += (steps ? 2 : 1);
                look_y += (steps ? 2 : 1);
                break;
            case 2:
                y_coord += (steps ? 2 : 1);
                break;
            case 3:
                look_x += -(steps ? 2 : 1);
                look_y += (steps ? 2 : 1);
                break;
            case 4:
                look_x += -(steps ? 2 : 1);
                break;
            case 5:
                look_x += -(steps ? 2 : 1);
                look_y += -(steps ? 2 : 1);
                break;
            case 6:
                look_y += (steps ? 2 : 1);
                break;
            case 7:
                look_x += (steps ? 2 : 1);
                look_y += -(steps ? 2 : 1);
                break;
        }
        this.energy -= Params.LOOK_ENERGY_COST;
        Critter critterLooked = getCritterAt(look_x, look_y);
        if(critterLooked == null) {
            return null;
        }
        return critterLooked.toString();
    }

    public static String runStats(List<Critter> critters) {
        StringBuilder res = new StringBuilder("" + critters.size() + " critters as follows -- ");
        Map<String, Integer> critter_count = new HashMap<String, Integer>();
        for (Critter crit : critters) {
            String crit_string = crit.toString();
            critter_count.put(crit_string,
                    critter_count.getOrDefault(crit_string, 0) + 1);
        }
        String prefix = "";
        for (String s : critter_count.keySet()) {
            res.append(prefix + s + ":" + critter_count.get(s));
            prefix = ", ";
        }
        return res.toString();
    }


    public static void displayWorld(Object pane) {
        if(!(pane instanceof Group)) {
            return;
        }

        ((Group) pane).getChildren().clear();

        for(Critter c: population) {
            Shape3D rep;
            PhongMaterial mat = new PhongMaterial();
            mat.setDiffuseColor(Color.WHITE);
            if(c.viewColor() != Color.WHITE) {
                mat.setDiffuseColor(c.viewColor());
            }
            else if(c.viewFillColor() != Color.WHITE) {
                mat.setDiffuseColor(c.viewFillColor());
            }
            else if(c.viewOutlineColor() != Color.WHITE) {
                mat.setDiffuseColor(c.viewOutlineColor());
            }

            mat.setSpecularColor(c.viewOutlineColor());
            if(c.viewShape() == CritterShape.SQUARE)
            {
                rep = new Box(5, 5 ,5);
            }
            else if(c.viewShape() == CritterShape.CIRCLE)
            {
                rep = new Sphere(2.5);
            }
            else if(c.viewShape() == CritterShape.TRIANGLE)
            {
                TriangleMesh mesh = new TriangleMesh();
                mesh.getPoints().addAll(
                     2.5f, -2.5f, 2.5f, //1
                        -2.5f, -2.5f, 2.5f,  //2
                        2.5f, -2.5f, -2.5f,  //3
                        -2.5f, -2.5f, -2.5f, //4
                        0f, 2.5f, 0f  //5
                );
                mesh.getTexCoords().addAll(
                        1f, 1f,
                        1f, 1f,
                        1f, 1f,
                        1f, 1f,
                        1f, 1f
                );
                mesh.getFaces().addAll(
                    0, 0, 1, 1, 2, 2,
                        1, 1, 3, 3, 2, 2,
                        0, 0, 4, 4, 1, 1,
                        1, 1, 4, 4, 3, 3,
                        2, 2, 3, 3, 4, 4,
                        0, 0, 2, 2, 4, 4
                );
                rep = new MeshView(mesh);
            }
            else if(c.viewShape() == CritterShape.DIAMOND) {
                rep = new Box(3.5, 5 ,3.5);
                rep.getTransforms().add(new Rotate(45, new Point3D(0, 1, 0)));
            }
            else {
                //star
                TriangleMesh mesh = new TriangleMesh();
                mesh.getPoints().addAll(
                         7.889562f * .3f, 1.150329f * .3f - 2.5f, -2.173651f * .3f,
                 2.212808f * .3f, 1.150329f * .3f - 2.5f, -2.230414f * .3f,
                 0.068023f * .3f, 1.150328f * .3f - 2.5f, -7.923502f * .3f,
                 -2.151306f * .3f, 1.150329f * .3f - 2.5f, -2.254857f * .3f,
                 -7.817406f * .3f, 1.150328f * .3f - 2.5f, -2.261558f * .3f,
                 -3.523133f * .3f, 1.150328f * .3f - 2.5f, 1.888122f * .3f,
                 -4.869315f * .3f, 1.150328f * .3f - 2.5f, 6.987552f * .3f,
                 -0.006854f * .3f, 1.150329f * .3f - 2.5f, 4.473047f * .3f,
                 4.838127f * .3f, 1.150328f * .3f - 2.5f, 7.041885f * .3f,
                 3.538153f * .3f, 1.150329f * .3f - 2.5f, 1.927652f * .3f,
                 0.033757f * .3f, 0.000000f * .3f - 2.5f, -0.314657f * .3f,
                    0.035668f * .3f, 2.269531f * .3f - 2.5f, -0.312831f * .3f
                );
                mesh.getTexCoords().addAll (
                        1f, 1f,
                        1f, 1f
                );
                mesh.getFaces().addAll(
                        0,1, 10,1, 1,1,
                        1,1, 10,1, 2,1,
                        2,1, 10,1, 3,1,
                        3,1, 10,1, 4,1,
                        4,1, 10,1, 5,1,
                        5,1, 10,1, 6,1,
                        6,1, 10,1, 7,1,
                        7,1, 10,1, 8,1,
                        8,1, 10,1, 9,1,
                        9,1, 10,1, 0,1,
                        0,1, 11,1, 9,1,
                        9,1, 11,1, 8,1,
                        8,1, 11,1, 7,1,
                        7,1, 11,1, 6,1,
                        6,1, 11,1, 5,1,
                        5,1, 11,1, 4,1,
                        4,1, 11,1, 3,1,
                        3,1, 11,1, 2,1,
                        2,1, 11,1, 1,1,
                        1,1, 11,1, 0,1
                );
                rep = new MeshView(mesh);
            }
            rep.setTranslateY(2.5);
            rep.setTranslateX(c.x_coord * 5 - (Params.WORLD_WIDTH* 5 / 2.0));
            rep.setTranslateZ(c.y_coord * 5 - (Params.WORLD_HEIGHT * 5 / 2.0));
            rep.setMaterial(mat);

            rep.setEffect(new Bloom(.00001));
            ((Group)pane).getChildren().add(rep);
        }
    }

	/* END --- NEW FOR PROJECT 5
			rest is unchanged from Project 4 */

    private int energy = 0;
    private static int timeStep = 0;

    private int x_coord;
    private int y_coord;

    private static List<Critter> population = new ArrayList<Critter>();
    private static List<Critter> babies = new ArrayList<Critter>();

    /* Gets the package name.  This assumes that Critter and its
     * subclasses are all in the same package. */
    private static String myPackage;

    static {
        myPackage = Critter.class.getPackage().toString().split(" ")[1];
    }

    private static Random rand = new Random();

    public static int getRandomInt(int max) {
        return rand.nextInt(max);
    }

    public static void setSeed(long new_seed) {
        rand = new Random(new_seed);
    }

    /**
     * create and initialize a Critter subclass.
     * critter_class_name must be the qualified name of a concrete
     * subclass of Critter, if not, an InvalidCritterException must be
     * thrown.
     *
     * @param critter_class_name
     * @throws InvalidCritterException
     */
    public static void createCritter(String critter_class_name)
            throws InvalidCritterException {
        //does the desired critter class exist????
        Class<?> myCritClass;
        try {
            myCritClass = Class.forName(myPackage+"."+critter_class_name);
            if(!Critter.class.isAssignableFrom(myCritClass)) {
                throw new InvalidCritterException(critter_class_name);
            }
        } catch (ClassNotFoundException e) {
            throw new InvalidCritterException(critter_class_name);
        }

        //init the critters data and check for collisions
        try {
            //set the critters position and energy
            Critter critterToAdd = (Critter)(myCritClass.newInstance());
            critterToAdd.x_coord = getRandomInt(Params.WORLD_WIDTH);
            critterToAdd.y_coord = getRandomInt(Params.WORLD_HEIGHT);
            critterToAdd.energy = Params.START_ENERGY;

            //if two critters collide on spawning then their fight is done on the next time step

            population.add(critterToAdd);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new InvalidCritterException(critter_class_name);
        }
    }

    /**
     * Gets a list of critters of a specific type.
     *
     * @param critter_class_name What kind of Critter is to be listed.
     *                           Unqualified class name.
     * @return List of Critters.
     * @throws InvalidCritterException
     */
    public static List<Critter> getInstances(String critter_class_name)
            throws InvalidCritterException {
        // TODO: Complete this method
        // TODO: Complete this method
        List<Critter> crits = new ArrayList<>();
        Class<?> goalCritClass;
        try {
            goalCritClass = Class.forName(myPackage+"."+critter_class_name);
        } catch (ClassNotFoundException e) {
            throw new InvalidCritterException(critter_class_name);
        }

        for(Critter crit: population) {
            if(goalCritClass.isInstance(crit)) {
                crits.add(crit);
            }
        }
        return crits;
    }

    /**
     * Clear the world of all critters, dead and alive
     */
    public static void clearWorld() {
        // TODO: Complete this method
        population.clear();
        babies.clear();
    }

    private static Critter getCritterAt(int x, int y) {
        for(Critter c: population) {
            if(c.x_coord == x && c.y_coord == y) {
                return c;
            }
        }
        return null;
    }

    private static boolean collidingCritters(Critter a, Critter b) {
        return a.x_coord == b.x_coord && a.y_coord == b.y_coord;
    }

    public static void worldTimeStep() {
        timeStep++;
        ArrayList<Critter> newPop = new ArrayList<>(population);
        newPop.forEach((Critter::doTimeStep));
        for(Critter crit: newPop) {
            if(crit.energy <= 0) {
                continue;
            }
            for(Critter otherCrit: newPop) {
                if(crit.energy <= 0) {
                    break;
                }
                if(crit == otherCrit || otherCrit.energy <= 0) {
                    continue;
                }
                if (collidingCritters(crit, otherCrit)) {
                    boolean aFight = crit.fight(otherCrit.toString());
                    int aRoll = 0;
                    boolean bFight = otherCrit.fight(crit.toString());
                    int bRoll = 0;
                    //will there be a fight?
                    if(!aFight && !bFight) {
                        crit.run(getRandomInt(8));
                        otherCrit.run(getRandomInt(8));
                    } else {
                        aRoll = Critter.getRandomInt(crit.energy);
                        bRoll = Critter.getRandomInt(otherCrit.energy);
                    }
                    //if they're still on the same tile then fight
                    if(collidingCritters(crit, otherCrit)) {
                        if (aRoll > bRoll) {
                            crit.energy += otherCrit.energy / 2;
                            otherCrit.energy = 0;
                        } else if (aRoll < bRoll) {
                            otherCrit.energy += crit.energy / 2;
                            crit.energy = 0;
                        } else {
                            crit.energy += otherCrit.energy / 2;
                            otherCrit.energy = 0;
                        }
                    }
                    if(crit.energy <= 0) {
                        population.remove(crit);
                    }
                    if(otherCrit.energy <= 0) {
                        population.remove(otherCrit);
                    }
                }
            }
        }
        newPop.removeIf((Critter c) -> (c.getEnergy() <= 0));
        newPop.forEach((Critter c) -> c.energy -= Params.REST_ENERGY_COST);
        for (int i = 0; i < Params.REFRESH_CLOVER_COUNT; i++) {
            try {
                createCritter("Clover");
            } catch(InvalidCritterException e) {
                assert(false);
            }
        }

        newPop.addAll(babies);
        babies.clear();
        population.removeAll(newPop);
        newPop.addAll(population);
        population = newPop;
    }

    public abstract void doTimeStep();

    public abstract boolean fight(String oponent);

    /* a one-character long string that visually depicts your critter
     * in the ASCII interface */
    public String toString() {
        return "";
    }

    protected int getEnergy() {
        return energy;
    }

    private void moveVert(int stepSize) {
        this.y_coord += stepSize;
        if(this.y_coord >= Params.WORLD_HEIGHT) {
            this.y_coord -= (Params.WORLD_HEIGHT);
        }
        if(this.y_coord < 0) {
            this.y_coord += (Params.WORLD_HEIGHT);
        }
    }
    private void moveHorz(int stepSize) {
        this.x_coord += stepSize;
        if(this.x_coord >= Params.WORLD_WIDTH) {
            this.x_coord -= (Params.WORLD_WIDTH);
        }
        if(this.x_coord < 0) {
            this.x_coord += Params.WORLD_WIDTH;
        }
    }
    private void doMove(int dir, int size) {
        switch(dir) {
            case 6:
                moveVert(-1 * size);
                break;
            case 7:
                moveVert(-1 * size);
                moveHorz(size);
                break;
            case 0:
                moveHorz(size);
                break;
            case 1:
                moveVert(size);
                moveHorz(size);
                break;
            case 2:
                moveVert(size);
                break;
            case 3:
                moveVert(size);
                moveHorz(-1 * size);
                break;
            case 4:
                moveHorz(-1 * size);
                break;
            case 5:
                moveVert(-1 * size);
                moveHorz(-1 * size);
                break;
        }
    }

    protected final void walk(int direction) {
        // TODO: Complete this method
        doMove(direction, 1);
        this.energy -= Params.WALK_ENERGY_COST;
    }

    protected final void run(int direction) {
        // TODO: Complete this method
        doMove(direction, 1);
        doMove(direction, 1);
        this.energy -= Params.RUN_ENERGY_COST;
    }

    protected final void reproduce(Critter offspring, int direction) {
        // TODO: Complete this method
        if(this.energy < Params.MIN_REPRODUCE_ENERGY) {
            return;
        }
        offspring.energy = this.energy/2;
        offspring.x_coord = this.x_coord;
        offspring.y_coord = this.y_coord;

        this.energy /= 2;

        babies.add(offspring);
    }

    /**
     * The TestCritter class allows some critters to "cheat". If you
     * want to create tests of your Critter model, you can create
     * subclasses of this class and then use the setter functions
     * contained here.
     * <p>
     * NOTE: you must make sure that the setter functions work with
     * your implementation of Critter. That means, if you're recording
     * the positions of your critters using some sort of external grid
     * or some other data structure in addition to the x_coord and
     * y_coord functions, then you MUST update these setter functions
     * so that they correctly update your grid/data structure.
     */
    static abstract class TestCritter extends Critter {

        protected void setEnergy(int new_energy_value) {
            super.energy = new_energy_value;
        }

        protected void setX_coord(int new_x_coord) {
            super.x_coord = new_x_coord;
        }

        protected void setY_coord(int new_y_coord) {
            super.y_coord = new_y_coord;
        }

        protected int getX_coord() {
            return super.x_coord;
        }

        protected int getY_coord() {
            return super.y_coord;
        }

        /**
         * This method getPopulation has to be modified by you if you
         * are not using the population ArrayList that has been
         * provided in the starter code.  In any case, it has to be
         * implemented for grading tests to work.
         */
        protected static List<Critter> getPopulation() {
            return population;
        }

        /**
         * This method getBabies has to be modified by you if you are
         * not using the babies ArrayList that has been provided in
         * the starter code.  In any case, it has to be implemented
         * for grading tests to work.  Babies should be added to the
         * general population at either the beginning OR the end of
         * every timestep.
         */
        protected static List<Critter> getBabies() {
            return babies;
        }
    }
}
