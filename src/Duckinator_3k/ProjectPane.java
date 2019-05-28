/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Duckinator_3k;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

/**
 * @author akkir
 */

public class ProjectPane extends Pane{
    
    private int xPoint, yPoint;
    private Image field, duck;
    private ImageView fieldHolder, duckHolder;
    ArrayList<Integer> xPixel = new ArrayList<Integer>(); 
    ArrayList<Integer> yPixel = new ArrayList<Integer>(); 
    ArrayList<Double> lineLength = new ArrayList<Double>(); 
    ArrayList<Double> actualPathLength = new ArrayList<Double>();
    ArrayList<Double> encoderPathLength = new ArrayList<Double>();
    ArrayList<Double> angleChanges = new ArrayList<Double>();
    ArrayList<String> leftOrRight = new ArrayList<String>();
    ArrayList<String> movements = new ArrayList<String>();
    private Line line;
    private Circle startCircle, nextCircles;
    private int circleTicker, lineTicker = 0;
    private Button clear, generate;
    private Rectangle rect;
    private int fieldMeasurementPixels = 510;
    private int fieldMeasurementInches = 144;
    private double conversionFactorPixelInch = ((double) fieldMeasurementInches/ (double) fieldMeasurementPixels);
    private double wheelDiameter = 4;
    private double ticksPerRotation = 1120;
    private double angleTemp, multiplier;
    private TextArea code;
    private String tankDriveMotors, holonomicDriveMotors, driveMotors, tankDriveInit, holonomicDriveInit, driveInit, movementTemp, moveHere;
    private String resetBusyForwardTank, resetBusyForwardHoloMeca, resetBusyForward, rotateTank, rotateHolo, rotating, tankZPower, holoZPower, zPower;
    private RadioButton tankDrive, holonomicDrive, mecanumDrive;
    private ToggleGroup drives;
    private int togglingKeep = 1;
    private Label wheelDi, ticksPer;
    private TextField wheelDia, ticksPerr;
    private Label careful;
    private Hyperlink github;
    
    public ProjectPane (){  
        rect = new Rectangle(1200, 600, Color.BLANCHEDALMOND);
        getChildren().add(rect);
        
        field = new Image(this.getClass().getResourceAsStream("/Duckinator_3k/field.png"));
        fieldHolder = new ImageView(field);
        fieldHolder.setFitHeight(fieldMeasurementPixels);
        fieldHolder.setFitWidth(fieldMeasurementPixels);
        fieldHolder.setLayoutX(0);
        fieldHolder.setLayoutY(0);
        getChildren().add(fieldHolder);
        
        duck = new Image(this.getClass().getResourceAsStream("/Duckinator_3k/duck.png"));
        duckHolder = new ImageView(duck);
        duckHolder.setFitHeight(150);
        duckHolder.setFitWidth(163);
        duckHolder.setLayoutX(870);
        duckHolder.setLayoutY(350);
        getChildren().add(duckHolder);
        
        clear = new Button("Clear");
        clear.setLayoutX(540);
        clear.setLayoutY(20);
        getChildren().add(clear);
        
        github = new Hyperlink("github.com/yup-its-rowan");
        github.setLayoutX(850);
        github.setLayoutY(22);
        getChildren().add(github);
        
        careful = new Label("Careful: \nAdjusting wheel dia or TPR mid-path affects the resulting code");
        careful.setLayoutX(540);
        careful.setLayoutY(450);
        getChildren().add(careful);
        
        wheelDi = new Label("Wheel Diameter (Inches): ");
        wheelDi.setLayoutX(540);
        wheelDi.setLayoutY(320);
        getChildren().add(wheelDi);
        
        wheelDia = new TextField("4");
        wheelDia.setLayoutX(680);
        wheelDia.setLayoutY(317);
        getChildren().add(wheelDia);
        
        ticksPer = new Label("Ticks Per Rotation: ");
        ticksPer.setLayoutX(540);
        ticksPer.setLayoutY(360);
        getChildren().add(ticksPer);
        
        ticksPerr = new TextField("1120");
        ticksPerr.setLayoutX(680);
        ticksPerr.setLayoutY(357);
        getChildren().add(ticksPerr);
        
        generate = new Button("Generate Code");
        generate.setLayoutX(600);
        generate.setLayoutY(20);
        getChildren().add(generate);
        
        code = new TextArea("Click on the field to make points on a path for your robot to follow. \n\nThen, hit the \"Generate Code\" button to generate copy and pastable code!");
        code.setLayoutX(540);
        code.setLayoutY(70);
        getChildren().add(code);
        
        drives = new ToggleGroup();
        
        tankDrive = new RadioButton("Tank Drive");
        tankDrive.setLayoutX(545);
        tankDrive.setLayoutY(270);
        tankDrive.setToggleGroup(drives);
        tankDrive.setSelected(true);
        getChildren().add(tankDrive);

        holonomicDrive = new RadioButton("X-Drive");
        holonomicDrive.setLayoutX(670);
        holonomicDrive.setLayoutY(270);
        holonomicDrive.setToggleGroup(drives);
        getChildren().add(holonomicDrive);
        
        mecanumDrive = new RadioButton("Mecanum");
        mecanumDrive.setLayoutX(795);
        mecanumDrive.setLayoutY(270);
        mecanumDrive.setToggleGroup(drives);
        getChildren().add(mecanumDrive);
        
        tankDriveMotors = (
        "    private DcMotor leftWheel;\n" +
        "    private DcMotor rightWheel;\n"
                );
        
        holonomicDriveMotors = (
        "    private DcMotor fl;\n" +
        "    private DcMotor fr;\n" +
        "    private DcMotor bl;\n" +
        "    private DcMotor br;\n" +
        "//holonomic encoder counts are slightly innacurate and need to be tested due to different amounts of force and friction on the wheels depending on what you get\n"+
        "//please adjust personally to each program, we have accounted for slight slippage but just please make sure\n"        
                );
        
        tankDriveInit = (
        "        leftWheel = hardwareMap.dcMotor.get(\"leftWheel\");\n" +
        "        rightWheel = hardwareMap.dcMotor.get(\"rightWheel\");\n" +
        "        rightWheel.setDirection(DcMotor.Direction.REVERSE);\n"        
                );
        
        holonomicDriveInit = (
        "        fl = hardwareMap.dcMotor.get(\"fl\");\n" +   
        "        fr = hardwareMap.dcMotor.get(\"fr\");\n" +         
        "        bl = hardwareMap.dcMotor.get(\"bl\");\n" +
        "        br = hardwareMap.dcMotor.get(\"br\");\n" 
                );
        
        resetBusyForwardTank = (
        "    public void motorReset() {\n" +
"        leftWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);\n" +
"        rightWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);\n" +
"        rightWheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);\n" +
"        leftWheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);\n" +
"    }\n" +
"    public void powerBusy() {\n" +
"        leftWheel.setPower(0.5);\n" +
"        rightWheel.setPower(0.5);\n" +
"        while ((rightWheel.isBusy() && leftWheel.isBusy())){}\n" +
"        leftWheel.setPower(0);\n" +
"        rightWheel.setPower(0);\n" +
"    }\n" +
"    public void goForward(int gofront){\n" +
"        motorReset();\n" +
"        rightWheel.setTargetPosition(gofront);\n" + 
"        leftWheel.setTargetPosition(gofront);\n" +
"        powerBusy();\n" +
"    }\n"              
                );
        
        
        
        rotateTank = (
"    private void rotate(int degrees) {\n" +
"        double leftPower, rightPower;\n" +
"        resetAngle();\n" +
"        if (degrees < 0) {   // turn right.\n" +
"            leftPower = 0.5;\n" +
"            rightPower = -0.5;\n" +
"        }\n" +
"        else if (degrees > 0) {   // turn left.\n" +
"            leftPower = -0.5;\n" +
"            rightPower = 0.5;\n" +
"        }\n" +
"        else return;\n" +
"        leftWheel.setPower(leftPower);\n" +
"        rightWheel.setPower(rightPower);\n"                 
                );
        
        rotateHolo = (
"    private void rotate(int degrees) {\n" +
"        double flp, frp, blp, brp;\n" +
"        resetAngle();\n" +
"        if (degrees < 0) {   // turn right.\n" +
"            flp = 0.5;\n" +
"            frp = 0.5;\n" +
"            blp = 0.5;\n" +
"            brp = 0.5;\n" +
"        }\n" +
"        else if (degrees > 0) {   // turn left.\n" +
"            flp = -0.5;\n" +
"            frp = -0.5;\n" +
"            blp = -0.5;\n" +
"            brp = -0.5;\n"  +
"        }\n" +
"        else return;\n" +
"        fl.setPower(flp);\n" +
"        fr.setPower(frp);\n" +
"        bl.setPower(blp);\n" +
"        br.setPower(brp);\n"                
                );
        
        tankZPower = (
"        rightWheel.setPower(0);\n" +
"        leftWheel.setPower(0);\n"                 
                );
        
        holoZPower = (
"        fl.setPower(0);\n" +
"        fr.setPower(0);\n" +
"        bl.setPower(0);\n" +
"        br.setPower(0);\n"               
                );
        
        tankDrive.setOnAction(this::processRadioButtons);
        holonomicDrive.setOnAction(this::processRadioButtons);
        mecanumDrive.setOnAction(this::processRadioButtons);
        clear.setOnAction(this::processButtonPress);
        generate.setOnAction(this::generation);
        fieldHolder.setOnMouseClicked(this::processMousePress);
        code.setOnKeyPressed(this::processKeyPress);
        github.setOnAction(this::hyperlinky);
        driveMotors = tankDriveMotors;
        driveInit = tankDriveInit;
        resetBusyForward = resetBusyForwardTank;
        rotating = rotateTank;
        zPower = tankZPower;   
    }
        
    public void hyperlinky(ActionEvent eeeee){
        if (eeeee.getSource() == github){
            if (Desktop.isDesktopSupported()){
                try {
                    Desktop.getDesktop().browse(new URI("https://www.github.com/yup-its-rowan"));
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (URISyntaxException e1) {
                    e1.printStackTrace();
                }           
            }          
        }
    }
    
    public void processMousePress(MouseEvent e){
        if (e.getSource() == fieldHolder){
            xPoint = (int) e.getSceneX();
            yPoint = (int) e.getSceneY();
            xPixel.add(xPoint);
            yPixel.add(yPoint);
            robotSpecs();
            if (circleTicker == 0){
                circleTicker = 1;
                Circle startCircle = new Circle(xPoint, yPoint, 3, Color.RED);
                getChildren().add(startCircle);
            }else if (circleTicker == 1){              
                Circle nextCircles = new Circle(xPoint, yPoint, 4);       
                getChildren().add(nextCircles);
                lineTicker++;
                Line line = new Line(xPixel.get(lineTicker-1),yPixel.get(lineTicker-1),xPixel.get(lineTicker),yPixel.get(lineTicker));
                lineLength.add(Math.sqrt( ( ( xPixel.get(lineTicker) - xPixel.get(lineTicker-1) ) * ( xPixel.get(lineTicker) - xPixel.get(lineTicker-1) ) ) + ( ( yPixel.get(lineTicker) - yPixel.get(lineTicker-1) ) * ( yPixel.get(lineTicker) - yPixel.get(lineTicker-1) ) ) ));
                actualPathLength.add((lineLength.get(lineTicker-1)*conversionFactorPixelInch));
                encoderPathLength.add(convertInchesToEncoderTicks(actualPathLength.get(lineTicker-1)));
                getChildren().add(line);
                if (lineTicker == 1){
                    movements.add("            goForward(" + (int)Math.round(encoderPathLength.get(0)) + ");\n");
                }else if (lineTicker > 1){
                    movementTemp = ("            goForward(" + (int)Math.round(encoderPathLength.get(lineTicker-1)) + ");\n");
                }
            } if (lineTicker > 1){
                angleChanges.add(getAngle2((double)xPixel.get(lineTicker-2), (double)xPixel.get(lineTicker-1), (double)xPixel.get(lineTicker), (double)yPixel.get(lineTicker-2), (double)yPixel.get(lineTicker-1), (double)yPixel.get(lineTicker), lineLength.get(lineTicker-2), lineLength.get(lineTicker-1)));
                orientation(xPixel.get(lineTicker-2), xPixel.get(lineTicker-1), xPixel.get(lineTicker),yPixel.get(lineTicker-2), yPixel.get(lineTicker-1), yPixel.get(lineTicker));
                if (leftOrRight.get(lineTicker-2).equals("Right")){
                    angleChanges.set(lineTicker-2, -angleChanges.get(lineTicker-2));
                }
                movements.add("            rotate(" + (int)Math.round(angleChanges.get(lineTicker-2)) + ");\n");
                movements.add(movementTemp);
            }        
        } 
    }
    
    public void processButtonPress(ActionEvent ev){
        if (ev.getSource() == clear){
            drives.selectToggle(null);
            getChildren().clear();
            getChildren().add(rect);
            getChildren().add(clear);
            getChildren().add(generate);
            getChildren().add(fieldHolder);
            getChildren().add(code);
            getChildren().add(duckHolder);
            getChildren().add(tankDrive);
            getChildren().add(mecanumDrive);
            getChildren().add(holonomicDrive);
            getChildren().add(ticksPer);
            getChildren().add(ticksPerr);
            getChildren().add(wheelDi);
            getChildren().add(wheelDia);
            getChildren().add(careful);
            getChildren().add(github);
            if (togglingKeep ==1){
                tankDrive.setSelected(true);
            }else if (togglingKeep ==2){
                holonomicDrive.setSelected(true);
            } else if (togglingKeep == 3){
                mecanumDrive.setSelected(true);
            }
            xPixel.clear();
            yPixel.clear();
            lineLength.clear();
            actualPathLength.clear();
            encoderPathLength.clear();
            leftOrRight.clear();
            angleChanges.clear();
            code.clear();
            movements.clear();
            circleTicker = 0;
            lineTicker = 0;
        }
    }
    
    public double convertInchesToEncoderTicks(double c){
        return ((c/(Math.PI*wheelDiameter))*ticksPerRotation);
    }
    
    public double getAngle2(double x1, double x2, double x3, double y1, double y2, double y3, double length1, double length2){
        double dx1 = x2-x1;
        double dx2 = x3-x2;
        double dy1 = y2-y1;
        double dy2 = y3-y2;
        angleTemp = Math.acos(((dx1*dx2)+(dy1*dy2))/(length1 * length2));
        return ((angleTemp*180)/Math.PI);
    }
    
    public void processKeyPress(KeyEvent event){
        if (event.getCode()== KeyCode.ENTER){
            if ((code.getText().equals("6183"))||(code.getText().equals("duck"))){
                code.setText("quack quack losers");
            }
        }
    }
    
    public void orientation(int x1, int x2, int x3, int y1, int y2, int y3){   
        if (((x2 - x1)*(y3 - y1) - (y2 - y1)*(x3 - x1)) > 0){
            leftOrRight.add("Right");
        } else if (((x2 - x1)*(y3 - y1) - (y2 - y1)*(x3 - x1)) < 0){
            leftOrRight.add("Left");
        } else if ((((x2 - x1)*(y3 - y1) - (y2 - y1)*(x3 - x1)) == 0)){
            leftOrRight.add("Middle?");
        }
    }
    
    public void processRadioButtons(ActionEvent e){
        
        if ((e.getSource() == tankDrive)){
            driveMotors = tankDriveMotors;
            driveInit = tankDriveInit;
            resetBusyForward = resetBusyForwardTank;
            rotating = rotateTank;
            zPower = tankZPower;
            togglingKeep = 1;
        } else if (e.getSource() == holonomicDrive){
            driveMotors = holonomicDriveMotors;
            driveInit = holonomicDriveInit;
            multiplier = 1.2;
            resety();
            resetBusyForward = resetBusyForwardHoloMeca;
            rotating = rotateHolo;
            zPower = holoZPower;
            togglingKeep = 2;
            
        } else if (e.getSource() == mecanumDrive){
            driveMotors = holonomicDriveMotors;
            driveInit = holonomicDriveInit;
            multiplier = 1;
            resety();
            resetBusyForward = resetBusyForwardHoloMeca;
            rotating = rotateHolo;
            zPower = holoZPower;
            togglingKeep = 3;
            
        }
    }
    
    public void resety(){
        resetBusyForwardHoloMeca = (
        "    public void motorReset() {\n" +
"        fl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);\n" +
"        fr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);\n" +
"        bl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);\n" +
"        br.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);\n" +
"        fl.setMode(DcMotor.RunMode.RUN_TO_POSITION);\n" +
"        fr.setMode(DcMotor.RunMode.RUN_TO_POSITION);\n" +
"        bl.setMode(DcMotor.RunMode.RUN_TO_POSITION);\n" +
"        br.setMode(DcMotor.RunMode.RUN_TO_POSITION);\n" +
"    }\n" +
"    public void powerBusy() {\n" +
"        fl.setPower(0.5);\n" +
"        fr.setPower(0.5);\n" +
"        bl.setPower(0.5);\n" +
"        br.setPower(0.5);\n" +                
"        while ((fl.isBusy() && fr())&&(bl.isBusy() && br.isBusy())){}\n" +
"        fl.setPower(0);\n" +
"        fr.setPower(0);\n" +
"        bl.setPower(0);\n" +
"        br.setPower(0);\n" +
"    }\n" +
"    public void goForward(int gofront){\n" +
"        motorReset();\n" +
"        fl.setTargetPosition((int)Math.round("+multiplier+"*gofront));\n" + 
"        fr.setTargetPosition((int)Math.round(-"+multiplier+"*gofront));\n" +
"        bl.setTargetPosition((int)Math.round("+multiplier+"*gofront));\n" + 
"        br.setTargetPosition((int)Math.round("+multiplier+"*gofront ));\n" +
"        powerBusy();\n" +
"    }\n"         
                );
    }
    
    public void robotSpecs(){
        wheelDiameter = new Double(wheelDia.getText());
        ticksPerRotation = new Double(ticksPerr.getText());
    }
    
    public void generation(ActionEvent DIO){
        if (DIO.getSource()==generate){
            if (encoderPathLength.size()>0){
                setMoveHere2();
                code.setText(
"package org.firstinspires.ftc.teamcode;\n" +
"import com.qualcomm.hardware.bosch.BNO055IMU;\n" +
"import com.qualcomm.robotcore.eventloop.opmode.Autonomous;\n" +
"import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;\n" +
"import com.qualcomm.robotcore.hardware.DcMotor;\n" +
"\n" +
"import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;\n" +
"import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;\n" +
"import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;\n" +
"import org.firstinspires.ftc.robotcore.external.navigation.Orientation;\n" +
"import org.firstinspires.ftc.robotcore.external.navigation.Position;\n" +
"import org.firstinspires.ftc.robotcore.external.navigation.Velocity;\n" +
"\n" +
"/**\n" +
" * Created with Team 6183's Duckinator 3000\n" +
" */\n" +
"\n" +
"@Autonomous(name = \"DuckinatorAuto\", group = \"DuckSquad\")\n" +
"public class DuckinatorAuto extends LinearOpMode {\n" +
driveMotors+
"    private int globalAngle;\n" +
"    BNO055IMU imu;\n" +
"    Orientation lastAngles = new Orientation();\n" +
"    @Override\n" +
"    public void runOpMode() throws InterruptedException {\n" +
"        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();\n" +
"        parameters.mode = BNO055IMU.SensorMode.IMU;\n" +
"        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;\n" +
"        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;\n" +
"        parameters.loggingEnabled = false;\n" +
"        imu = hardwareMap.get(BNO055IMU.class, \"imu\");\n" +
"        imu.initialize(parameters);\n" +
"        // make sure the imu gyro is calibrated before continuing.\n" +
"        while (!isStopRequested() && !imu.isGyroCalibrated())\n" +
"        {\n" +
"            sleep(50);\n" +
"            idle();\n" +
"        }\n" +
driveInit+
"        waitForStart();\n" +
"        if (opModeIsActive()){\n" +
        moveHere +
"\n" +
"        }\n" +
"    }\n" +
resetBusyForward+
"    private void resetAngle() {\n" +
"        lastAngles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);\n" +
"        globalAngle = 0;\n" +
"    }\n" +
"    private double getAngle() {\n" +
"        Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);\n" +
"        double deltaAngle = angles.firstAngle - lastAngles.firstAngle;\n" +
"        if (deltaAngle < -180)\n" +
"            deltaAngle += 360;\n" +
"        else if (deltaAngle > 180)\n" +
"            deltaAngle -= 360;\n" +
"        globalAngle += deltaAngle;\n" +
"        lastAngles = angles;\n" +
"        return globalAngle;\n" +
"    }\n" +
rotating+
"        if (degrees < 0) {//right\n" +
"            while (opModeIsActive() && getAngle() == 0) {}\n" +
"            while (opModeIsActive() && getAngle() > degrees) {}\n" +
"        } else {//left\n" +
"            while (opModeIsActive() && getAngle() < degrees) {}\n" +
"        }\n" +
zPower+
"        sleep(1000);\n" +
"        resetAngle();\n" +
"    }\n" +
"}"
                );
            }     
        }
    }
    
    private String convertArrayList(ArrayList<String> stringList) {
        String joinedString = String.join("", stringList);
        return joinedString;
    }
    
    private void setMoveHere2(){
        if (encoderPathLength.size()>0){
            moveHere = convertArrayList(movements);
        }
    }
    
}
