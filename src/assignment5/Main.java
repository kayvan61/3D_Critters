package assignment5;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.Light;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;


public class Main extends Application {

    private static ArrayList<CheckBox> chkBoxs;

    private final Group root = new Group();
    private Transform world = new Transform();
    private final Group gameField = new Group();
    private final Group allCritters = new Group();

    private final MailBox<Double> mouseSens = new MailBox<>(1.0);
    private final MailBox<Double> movementSpeed = new MailBox<>(1.0);
    private final MailBox<Double> scrollSpeed = new MailBox<>(1.0);

    private double cam_pos_x = 0;
    private double cam_pos_z = 0;
    private double cam_pos_y = 0;
    private double mousePosX;
    private double mousePosY;

    private final PerspectiveCamera mainCamera = new PerspectiveCamera(true);
    private final Transform cameraXYRot = new Transform();
    private final Transform cameraXYTrans = new Transform();
    private final Transform cameraZRot = new Transform();
    private final double cameraDist = 450;

    public void initRoot() {
        root.getChildren().add(world);
    }

    public void initCamera() {
        root.getChildren().add(cameraXYRot);
        cameraXYRot.getChildren().add(cameraXYTrans);
        cameraXYTrans.getChildren().add(cameraZRot);
        cameraZRot.getChildren().add(mainCamera);
        cameraZRot.setRotateZ(180);

        mainCamera.setNearClip(.1);
        mainCamera.setFarClip(10000);
        mainCamera.setTranslateZ(-cameraDist);
        cameraXYRot.ry.setAngle(320.0);
        cameraXYRot.rx.setAngle(40.0);

    }

    private void initGameField() {
        gameField.getChildren().clear();

        PhongMaterial mat = new PhongMaterial();
        Image diffuseMap = new Image(Main.class.getResource("/assignment5/tile.png").toString());
        mat.setDiffuseMap(diffuseMap);
        mat.setSpecularColor(Color.WHITE);

        for(int i = 0; i < Params.WORLD_WIDTH; i++) {
            for(int j = 0; j < Params.WORLD_HEIGHT; j++) {
                Box tile = new Box(5, .2, 5);
                tile.setMaterial(mat);
                tile.setTranslateZ(j*5 - (Params.WORLD_HEIGHT * 5 / 2.0));
                tile.setTranslateX(i*5 - (Params.WORLD_WIDTH * 5 / 2.0));
                gameField.getChildren().add(tile);
            }
        }
        if(!world.getChildren().contains(gameField)) {
            world.getChildren().add(gameField);
        }
    }

    private void resetGameField() {
        gameField.getChildren().clear();
        allCritters.getChildren().clear();
    }

    private void initCrittersGroup() {
        world.getChildren().add(allCritters);
    }

    public void start(Stage primaryStage) {
        initRoot();
        initCamera();
        initGameField();
        initCrittersGroup();
        Scene scene_3d = new Scene(root, 300, 300, true);
        scene_3d.setFill(Color.GREY);
        scene_3d.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.W) {
                    cameraXYTrans.setTy(cam_pos_y += movementSpeed.getValue());
                }
                if(event.getCode() == KeyCode.S) {
                    cameraXYTrans.setTy(cam_pos_y -= movementSpeed.getValue());
                }
                if(event.getCode() == KeyCode.EQUALS) {
                    cameraXYTrans.setTz(cam_pos_z += movementSpeed.getValue());
                }
                if(event.getCode() == KeyCode.MINUS) {
                    cameraXYTrans.setTz(cam_pos_z -= movementSpeed.getValue());
                }
                if(event.getCode() == KeyCode.A) {
                    cameraXYTrans.setTx(cam_pos_x += movementSpeed.getValue());
                }
                if(event.getCode() == KeyCode.D) {
                    cameraXYTrans.setTx(cam_pos_x -= movementSpeed.getValue());
                }
            }
        });
        scene_3d.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mousePosX = event.getSceneX();
                mousePosY = event.getSceneY();
            }
        });
        scene_3d.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double dx = (mousePosX - event.getSceneX()) ;
                double dy = (mousePosY - event.getSceneY());
                cameraXYRot.rx.setAngle(cameraXYRot.rx.getAngle() -
                             (dy / 1) * (Math.PI / 180) * mouseSens.getValue());
                cameraXYRot.ry.setAngle(cameraXYRot.ry.getAngle() -
                             (-dx / 1) * (Math.PI / 180) * mouseSens.getValue());
                mousePosX = event.getSceneX();
                mousePosY = event.getSceneY();
            }
        });
        scene_3d.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                cameraXYTrans.setTz(cam_pos_z -= movementSpeed.getValue() * -(event.getDeltaY() / Math.abs(event.getDeltaY())) * scrollSpeed.getValue());
            }
        });

        primaryStage.setTitle("Critters!");
        primaryStage.setScene(scene_3d);
        primaryStage.show();
        scene_3d.setCamera(mainCamera);
        createControllerStage(primaryStage);

    }

    public void updateView() {
        for(CheckBox c : chkBoxs) {
            c.selectedProperty().set(!c.isSelected());
            c.selectedProperty().set(!c.isSelected());
        }
        Critter.displayWorld(allCritters);
    }

    public void createControllerStage(Stage parent) {
        Stage controller = new Stage();
        controller.setTitle("Controller!");
        controller.initOwner(parent);
        controller.initModality(Modality.NONE);
        TabPane tp = new TabPane();

        Tab contTab = new Tab("Controls");
        Tab animTab = new Tab("Animations");
        Tab statsTab = new Tab("Stats");
        Tab paramTab = new Tab("Parameters");
        Tab worldSettingsTab = new Tab("Settings");

        contTab.setContent(new ScrollPane(getControllerContent()));
        animTab.setContent(new ScrollPane(getAnimContent()));
        statsTab.setContent(new ScrollPane(getStatsContent()));
        paramTab.setContent(new ScrollPane(getParamsContent()));
        worldSettingsTab.setContent(new ScrollPane(getSettingsContent()));

        tp.getTabs().addAll(contTab, animTab, statsTab, paramTab, worldSettingsTab);

        controller.setScene(new Scene(tp, 400, 400));
        controller.show();
    }

    public Pane getSettingsContent() {
        GridPane gp = new GridPane();

        Slider movmt = new Slider(.1, 15, 1);
        Label movmtLbl = new Label("camera movement speed");
        Slider lk = new Slider(.1, 30, 1);
        Label lkLbl = new Label("mouse sensitivity");
        Slider scrl = new Slider(.1, 15, 1);
        Label scrlLbl = new Label("Scroll speed");

        movmt.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                movementSpeed.setValue(newValue.doubleValue());
            }
        });
        lk.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                mouseSens.setValue(newValue.doubleValue());
            }
        });
        scrl.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                scrollSpeed.setValue(newValue.doubleValue());
            }
        });

        GridPane.setConstraints(movmtLbl, 0, 0);
        GridPane.setConstraints(movmt, 0, 1);
        GridPane.setConstraints(lkLbl, 0, 2);
        GridPane.setConstraints(lk, 0, 3);
        GridPane.setConstraints(scrlLbl, 0, 4);
        GridPane.setConstraints(scrl, 0, 5);

        gp.getChildren().addAll(movmt, lk, lkLbl, movmtLbl, scrl, scrlLbl);

        return gp;
    }

    public Pane getParamsContent() {
        GridPane gp = new GridPane();

        ArrayList<String> paramsToChange = new ArrayList<>();
        ArrayList<Label> lbls = new ArrayList<>();
        ArrayList<TextField> fields = new ArrayList<>();

        Button update = new Button("Update");
        update.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                boolean needToClear = !fields.get(0).getText().equals("") || !fields.get(1).getText().equals("");
                if(needToClear)
                    needToClear = Params.WORLD_WIDTH > Integer.parseInt(fields.get(0).getText()) ||
                            Params.WORLD_HEIGHT > Integer.parseInt(fields.get(1).getText());

                Params.WORLD_WIDTH = fields.get(0).getText().equals("") ? Params.WORLD_WIDTH :
                        Integer.parseInt(fields.get(0).getText());
                Params.WORLD_HEIGHT = fields.get(1).getText().equals("") ? Params.WORLD_HEIGHT :
                        Integer.parseInt(fields.get(1).getText());
                Params.WALK_ENERGY_COST = fields.get(2).getText().equals("") ? Params.WALK_ENERGY_COST :
                        Integer.parseInt(fields.get(2).getText());
                Params.RUN_ENERGY_COST = fields.get(3).getText().equals("") ? Params.RUN_ENERGY_COST :
                        Integer.parseInt(fields.get(3).getText());
                Params.REST_ENERGY_COST = fields.get(4).getText().equals("") ? Params.REST_ENERGY_COST :
                        Integer.parseInt(fields.get(4).getText());
                Params.MIN_REPRODUCE_ENERGY = fields.get(5).getText().equals("") ? Params.MIN_REPRODUCE_ENERGY :
                        Integer.parseInt(fields.get(5).getText());
                Params.REFRESH_CLOVER_COUNT = fields.get(6).getText().equals("") ? Params.REFRESH_CLOVER_COUNT :
                        Integer.parseInt(fields.get(6).getText());
                Params.PHOTOSYNTHESIS_ENERGY_AMOUNT = fields.get(7).getText().equals("") ? Params.PHOTOSYNTHESIS_ENERGY_AMOUNT :
                        Integer.parseInt(fields.get(7).getText());
                Params.START_ENERGY = fields.get(8).getText().equals("") ? Params.START_ENERGY :
                        Integer.parseInt(fields.get(8).getText());
                Params.LOOK_ENERGY_COST = fields.get(9).getText().equals("") ? Params.LOOK_ENERGY_COST :
                        Integer.parseInt(fields.get(9).getText());

                if(needToClear) {
                    Critter.clearWorld();
                    resetGameField();
                }
                initGameField();
            }
        });

        Collections.addAll(paramsToChange, "WORLD_WIDTH",
        "WORLD_HEIGHT",
        "WALK_ENERGY_COST",
        "RUN_ENERGY_COST",
        "REST_ENERGY_COST",
        "MIN_REPRODUCE_ENERGY",
        "REFRESH_CLOVER_COUNT",
        "PHOTOSYNTHESIS_ENERGY_AMOUNT",
        "START_ENERGY",
        "LOOK_ENERGY_COST");

        int i = 0;
        for(String s: paramsToChange) {
            lbls.add(new Label(s));
            fields.add(new TextField());
            GridPane.setConstraints(lbls.get(i), 1 ,i);
            GridPane.setConstraints(fields.get(i), 0, i);
            i++;
        }
        GridPane.setConstraints(update, 0, i);
        gp.getChildren().add(update);

        gp.getChildren().addAll(lbls);
        gp.getChildren().addAll(fields);

        return gp;
    }

    public Pane getStatsContent() {

        GridPane res = new GridPane();

        chkBoxs = new ArrayList<>();
        ArrayList<Label> lbls = new ArrayList<>();

        ArrayList<String> critters = getAllCrits();
        int i = 0;
        for(String s: critters) {
            Label curStats = new Label();
            CheckBox checkBox = new CheckBox(s + "  ");
            GridPane.setConstraints(checkBox, 0, i);
            GridPane.setConstraints(curStats, 1, i);
            chkBoxs.add(checkBox);
            lbls.add(curStats);
            res.getChildren().addAll(curStats, checkBox);
            i++;
        }

        for(CheckBox c: chkBoxs) {
            c.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if(newValue) {
                        Label toChange = lbls.get(chkBoxs.indexOf(c));
                        try {
                            toChange.setText(Critter.runStats(Critter.getInstances(c.getText().trim())));
                        } catch (Exception e) {
                            System.out.println(e.toString());
                        }
                    }
                    else {
                        Label toChange = lbls.get(chkBoxs.indexOf(c));
                        toChange.setText("");
                    }
                }
            });
        }

        return res;
    }

    public Pane getAnimContent() {

        GridPane cn = new GridPane();

        Label animationControlLabel = new Label("Animation Controls");
        animationControlLabel.setStyle("-fx-font-weight: bold");

        Label animationSpeedLabel = new Label("speed that the animation frames are run");

        Label animationTimeStepsLabel = new Label("Time steps per animation frame");

        Slider animationTimeStepsSlider = new Slider(0, 10, 1);
        animationTimeStepsSlider.setShowTickLabels(true);
        animationTimeStepsSlider.setMajorTickUnit(1);
        animationTimeStepsSlider.snapToTicksProperty().set(true);
        animationTimeStepsSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                animationTimeStepsSlider.setValue(Math.round(newValue.doubleValue()));
            }
        });

        MailBox<Double> animationSpeed = new MailBox<>(9.0);

        Slider animationSpeedSlider = new Slider(0, 10, 1);
        animationSpeedSlider.setShowTickLabels(true);
        animationSpeedSlider.setMajorTickUnit(1);
        animationSpeedSlider.snapToTicksProperty().set(true);
        animationSpeedSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                animationSpeedSlider.setValue(Math.round(newValue.doubleValue()));
                animationSpeed.setValue(animationSpeedSlider.getMax() - newValue.doubleValue());
            }
        });

        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;
            @Override
            public void handle(long now) {
                if((now - lastUpdate) <= animationSpeed.getValue() * 100000000) {
                    return;
                }
                lastUpdate = now;
                for(int i = 0; i < animationTimeStepsSlider.getValue(); i++) {
                    Critter.worldTimeStep();
                }
                updateView();
            }
        };

        ToggleButton animationStartBtn = new ToggleButton("Run");
        animationStartBtn.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue) {
                    timer.start();
                }
                else {
                    timer.stop();
                }
            }
        });


        GridPane.setConstraints(animationControlLabel, 0, 0);
        GridPane.setConstraints(animationTimeStepsLabel, 0, 1);
        GridPane.setConstraints(animationTimeStepsSlider, 0,2);
        GridPane.setConstraints(animationStartBtn, 0, 5);
        GridPane.setConstraints(animationSpeedLabel, 0, 3);
        GridPane.setConstraints(animationSpeedSlider, 0, 4);


        cn.getChildren().addAll(animationStartBtn, animationControlLabel, animationTimeStepsLabel,
                animationTimeStepsSlider, animationSpeedLabel, animationSpeedSlider);

        return cn;
    }

    public Pane getControllerContent() {
        GridPane controllerContent = new GridPane();

        //spacers
        Label spacer1 = new Label("");
        Label spacer2 = new Label("");

        //invalid stuff

        Label exceptionLabel = new Label("Invalid Input!");
        exceptionLabel.setStyle("-fx-text-fill: red");
        exceptionLabel.setVisible(false);

        //time stepping elements
        Label timeStepLbl = new Label("Time step controls:");
        timeStepLbl.setStyle("-fx-font-weight: bold");
        Button timeStepBtn = new Button("Do time-stepping");
        TextField timeStepField = new TextField();
        timeStepBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String rawTimeSteps = timeStepField.getText();
                try {
                    int timeSteps = Integer.parseInt(rawTimeSteps);
                    exceptionLabel.setVisible(false);
                    for(int i = 0; i < timeSteps; i++) {
                        Critter.worldTimeStep();
                    }
                    updateView();
                }catch (IllegalArgumentException e) {
                    exceptionLabel.setVisible(true);
                }
            }
        });

        //seeding random numbers elements
        Label randSeedLbl = new Label("Random seed controls:");
        randSeedLbl.setStyle("-fx-font-weight: bold");
        Button randSeedBtn = new Button("set seed");
        TextField randSeedField = new TextField();
        randSeedBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String rawSeed = randSeedField.getText();
                exceptionLabel.setVisible(false);
                try {
                    int newSeed = Integer.parseInt(rawSeed);
                    Critter.setSeed(newSeed);
                }catch (IllegalArgumentException e) {
                    exceptionLabel.setVisible(true);
                }
            }
        });

        //make Critters elements
        Label makeCritLbl = new Label("Create critter Controls:");
        makeCritLbl.setStyle("-fx-font-weight: bold");
        ComboBox<String> makeCritCB = new ComboBox<>();
        ArrayList<String> crittersToDisp = getAllCrits();
        makeCritCB.getItems().addAll(crittersToDisp);
        TextField makeCritterField = new TextField();
        Button makeCritBtn = new Button("Make Critters");
        makeCritBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String critter = makeCritCB.getSelectionModel().getSelectedItem();
                try {
                    int numCrits;
                    if(makeCritterField.getText().equals("")) {
                        numCrits = 1;
                    }
                    else {
                        numCrits = Integer.parseInt(makeCritterField.getText());
                    }
                    exceptionLabel.setVisible(false);
                    for(int i = 0; i < numCrits; i++) {
                        Critter.createCritter(critter);
                    }
                } catch(IllegalArgumentException e) {
                    exceptionLabel.setVisible(true);
                } catch(InvalidCritterException e) {
                    e.printStackTrace();
                }
                updateView();
            }
        });

        //set time stepping elements constrains
        GridPane.setConstraints(timeStepLbl, 0, 0);
        GridPane.setConstraints(timeStepField, 0, 1);
        GridPane.setConstraints(timeStepBtn, 1, 1);
        GridPane.setConstraints(spacer1, 0 ,2);

        //seed rand element constraints
        GridPane.setConstraints(randSeedLbl, 0, 3);
        GridPane.setConstraints(randSeedField, 0 ,4);
        GridPane.setConstraints(randSeedBtn, 1, 4);
        GridPane.setConstraints(spacer2, 0, 5);

        //set make critter element constrains
        GridPane.setConstraints(makeCritLbl, 0, 6);
        GridPane.setConstraints(makeCritCB, 0, 7);
        GridPane.setConstraints(makeCritBtn, 0, 8);
        GridPane.setConstraints(makeCritterField, 1, 7);

        GridPane.setConstraints(exceptionLabel, 0, 9);

        //add all Timestepping stuff
        controllerContent.getChildren().addAll(timeStepBtn, timeStepField, timeStepLbl);
        //add all seed rand
        controllerContent.getChildren().addAll(randSeedBtn, randSeedField, randSeedLbl);
        //add all create critters rand
        controllerContent.getChildren().addAll(makeCritBtn, makeCritCB, makeCritLbl, makeCritterField);
        //add all spacers
        controllerContent.getChildren().addAll(spacer1, spacer2, exceptionLabel);

        return controllerContent;
    }

    public ArrayList<String> getAllCrits() {
        ArrayList<String> res = new ArrayList<>();
        ClassFinder.findClasses(new Visitor<String>() {
            @Override
            public boolean visit(String clazz) {
                if(clazz.startsWith(Critter.class.getPackage().toString().split(" ")[1])
                    && !clazz.contains("Critter$"))
                    res.add(clazz);
                return true; // return false if you don't want to see any more classes
            }
        });
        Iterator<String> it = res.iterator();
        while(it.hasNext()) {
            String s = it.next();
            try {
                Class<?> myClass;
                myClass = Class.forName(s);
                if (!Critter.class.isAssignableFrom(myClass)) {
                    it.remove();
                }
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        for(int i = 0; i < res.size(); i++) {
            res.set(i, res.get(i).split("\\.")[1]);
        }
        res.remove("Critter");
        return res;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
