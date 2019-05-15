/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Duckinator_3k;

import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

/**
 *
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
    ArrayList<Double> lineSlope = new ArrayList<Double>();
    ArrayList<Double> angleChanges = new ArrayList<Double>();
    ArrayList<String> leftOrRight = new ArrayList<String>();
    private Line line;
    private Circle startCircle, nextCircles;
    private int circleTicker, lineTicker = 0;
    private int countingCircles = 0;
    private Button clear, generate;
    private Rectangle rect;
    private int fieldMeasurementPixels = 510;
    private int fieldMeasurementInches = 144;
    private double conversionFactorPixelInch = ((double) fieldMeasurementInches/ (double) fieldMeasurementPixels);
    private double wheelRadius = 3;
    private int ticksPerRotation = 1120;
    private double slopeTemp, angleTemp;
    private TextArea code;
    private String moveHere;
    //private String leftOrRight;
    
    public ProjectPane (){
        
        rect = new Rectangle(1200, 600, Color.BLANCHEDALMOND);
        getChildren().add(rect);
        
        //field = new Image("file:src/Duckinator_3k/field.png");
        field = new Image(this.getClass().getResourceAsStream("/Duckinator_3k/field.png"));
        fieldHolder = new ImageView(field);
        fieldHolder.setFitHeight(fieldMeasurementPixels);
        fieldHolder.setFitWidth(fieldMeasurementPixels);
        fieldHolder.setLayoutX(0);
        fieldHolder.setLayoutY(0);
        getChildren().add(fieldHolder);
        
        
        //duck = new Image("file:src/Duckinator_3k/duck.png");
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
        
        generate = new Button("Generate Code");
        generate.setLayoutX(600);
        generate.setLayoutY(20);
        getChildren().add(generate);
        
        code = new TextArea("Press the \"Generate Code\" Button to generate pastable code!");
        code.setLayoutX(540);
        code.setLayoutY(70);
        getChildren().add(code);
        
        clear.setOnAction(this::processButtonPress);
        generate.setOnAction(this::generation);
        fieldHolder.setOnMouseClicked(this::processMousePress);
        
        countingCircles = 0;
        

    }
    
    public void processMousePress(MouseEvent e){
        if (e.getSource() == fieldHolder){
            xPoint = (int) e.getSceneX();
            yPoint = (int) e.getSceneY();
            xPixel.add(xPoint);
            yPixel.add(yPoint);
            //System.out.println("(" + (xPoint)+ ", " + (yPoint)+ ")");
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
                slopeTemp = (((double)yPixel.get(lineTicker) - (double)yPixel.get(lineTicker-1))/((double)xPixel.get(lineTicker)-(double)xPixel.get(lineTicker-1)));
                lineSlope.add(-slopeTemp);
                encoderPathLength.add(convertInchesToEncoderTicks(actualPathLength.get(lineTicker-1)));
                getChildren().add(line);       
            } if (lineTicker > 1){
                //angleChanges.add(getAngle(lineSlope.get(lineTicker-2), lineSlope.get(lineTicker-1)));
                angleChanges.add(getAngle2((double)xPixel.get(lineTicker-2), (double)xPixel.get(lineTicker-1), (double)xPixel.get(lineTicker), (double)yPixel.get(lineTicker-2), (double)yPixel.get(lineTicker-1), (double)yPixel.get(lineTicker), lineLength.get(lineTicker-2), lineLength.get(lineTicker-1)));
                orientation(xPixel.get(lineTicker-2), xPixel.get(lineTicker-1), xPixel.get(lineTicker),yPixel.get(lineTicker-2), yPixel.get(lineTicker-1), yPixel.get(lineTicker));
                if (leftOrRight.get(lineTicker-2).equals("Right")){
                    angleChanges.set(lineTicker-2, -angleChanges.get(lineTicker-2));
                }
                
            }
            
        } 
    }
    
    public void processButtonPress(ActionEvent ev){
        if (ev.getSource() == clear){
            getChildren().clear();
            getChildren().add(rect);
            getChildren().add(clear);
            getChildren().add(generate);
            getChildren().add(fieldHolder);
            getChildren().add(code);
            xPixel.clear();
            yPixel.clear();
            lineLength.clear();
            actualPathLength.clear();
            encoderPathLength.clear();
            leftOrRight.clear();
            lineSlope.clear();
            angleChanges.clear();
            code.clear();
            circleTicker = 0;
            lineTicker = 0;
        }
    }
    
    public double convertInchesToEncoderTicks(double c){
        return ((c/(2*Math.PI*wheelRadius))*ticksPerRotation);
    }
    
    public double getAngle(double slope1, double slope2){
        angleTemp = Math.atan(Math.abs((slope2-slope1)/(1+(slope1*slope2))));
        return ((angleTemp*180)/Math.PI);
    }
    
    public double getAngle2(double x1, double x2, double x3, double y1, double y2, double y3, double length1, double length2){
        double dx1 = x2-x1;
        double dx2 = x3-x2;
        double dy1 = y2-y1;
        double dy2 = y3-y2;
        angleTemp = Math.acos(((dx1*dx2)+(dy1*dy2))/(length1 * length2));
        return ((angleTemp*180)/Math.PI);
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
    
    public void generation(ActionEvent DIO){
        if (DIO.getSource()==generate){
            if ((encoderPathLength.size()>0)&&(encoderPathLength.size()<16)){
                setMoveHere();
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
"    private DcMotor leftWheel;\n" +
"    private DcMotor rightWheel;\n" +
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
"        leftWheel = hardwareMap.dcMotor.get(\"leftWheel\");\n" +
"        rightWheel = hardwareMap.dcMotor.get(\"rightWheel\");\n" +
"        rightWheel.setDirection(DcMotor.Direction.REVERSE);\n" +
"        waitForStart();\n" +
"        if (opModeIsActive()){\n" +
        moveHere +
"\n" +
"        }\n" +
"    }\n" +
"    public void motorReset() {\n" +
"        leftWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);\n" +
"        rightWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);\n" +
"        rightWheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);\n" +
"        leftWheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);\n" +
"    }\n" +
"    public void powerBusy() {\n" +
"        leftWheel.setPower(0.65);\n" +
"        rightWheel.setPower(0.65);\n" +
"        while ((rightWheel.isBusy() && leftWheel.isBusy())){}\n" +
"        leftWheel.setPower(0);\n" +
"        rightWheel.setPower(0);\n" +
"    }\n" +
"    public void goForward(int gofront){\n" +
"        motorReset();\n" +
"        rightWheel.setTargetPosition(gofront);\n" +
"        leftWheel.setTargetPosition(gofront);\n" +
"        powerBusy();\n" +
"    }\n" +
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
"        rightWheel.setPower(rightPower);\n" +
"        if (degrees < 0) {//right\n" +
"            while (opModeIsActive() && getAngle() == 0) {}\n" +
"            while (opModeIsActive() && getAngle() > degrees) {}\n" +
"        } else {//left\n" +
"            while (opModeIsActive() && getAngle() < degrees) {}\n" +
"        }\n" +
"        rightWheel.setPower(0);\n" +
"        leftWheel.setPower(0);\n" +
"        sleep(1000);\n" +
"        resetAngle();\n" +
"    }\n" +
"}"
                );
            }else if (encoderPathLength.size()>15){
                code.setText("Sorry, but as of now we can only support up to 15 separate paths.\n\nHowever, we do plan to keep updating in the future :)");
            }
            
        }
    }
    
    private void setMoveHere(){
        if (encoderPathLength.size()==1){
            moveHere = ("            goForward(" + (int)Math.round(encoderPathLength.get(0)) + ");");
        } else if (encoderPathLength.size()==2){
            moveHere = ("            goForward(" + (int)Math.round(encoderPathLength.get(0)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(0)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(1)) + ");"
                    );
        } else if (encoderPathLength.size()==3){
            moveHere = ("            goForward(" + (int)Math.round(encoderPathLength.get(0)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(0)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(1)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(1)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(2)) + ");"
                    );
        }else if (encoderPathLength.size()==4){
            moveHere = ("            goForward(" + (int)Math.round(encoderPathLength.get(0)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(0)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(1)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(1)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(2)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(2)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(3)) + ");"
                    );
        }else if (encoderPathLength.size()==5){
            moveHere = ("            goForward(" + (int)Math.round(encoderPathLength.get(0)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(0)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(1)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(1)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(2)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(2)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(3)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(3)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(4)) + ");"
                    );
        }else if (encoderPathLength.size()==6){
            moveHere = ("            goForward(" + (int)Math.round(encoderPathLength.get(0)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(0)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(1)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(1)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(2)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(2)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(3)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(3)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(4)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(4)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(5)) + ");"
                    );
        }else if (encoderPathLength.size()==7){
            moveHere = ("            goForward(" + (int)Math.round(encoderPathLength.get(0)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(0)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(1)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(1)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(2)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(2)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(3)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(3)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(4)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(4)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(5)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(5)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(6)) + ");"
                    );
        }else if (encoderPathLength.size()==8){
            moveHere = ("            goForward(" + (int)Math.round(encoderPathLength.get(0)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(0)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(1)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(1)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(2)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(2)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(3)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(3)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(4)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(4)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(5)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(5)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(6)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(6)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(7)) + ");"
                    );
        }else if (encoderPathLength.size()==9){
            moveHere = ("            goForward(" + (int)Math.round(encoderPathLength.get(0)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(0)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(1)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(1)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(2)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(2)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(3)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(3)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(4)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(4)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(5)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(5)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(6)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(6)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(7)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(7)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(8)) + ");"
                    );
            }else if (encoderPathLength.size()==10){
            moveHere = ("            goForward(" + (int)Math.round(encoderPathLength.get(0)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(0)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(1)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(1)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(2)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(2)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(3)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(3)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(4)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(4)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(5)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(5)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(6)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(6)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(7)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(7)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(8)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(8)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(9)) + ");"
                    );
            }else if (encoderPathLength.size()==11){
            moveHere = ("            goForward(" + (int)Math.round(encoderPathLength.get(0)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(0)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(1)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(1)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(2)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(2)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(3)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(3)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(4)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(4)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(5)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(5)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(6)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(6)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(7)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(7)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(8)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(8)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(9)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(9)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(10)) + ");"
                    );
            }else if (encoderPathLength.size()==12){
            moveHere = ("            goForward(" + (int)Math.round(encoderPathLength.get(0)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(0)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(1)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(1)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(2)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(2)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(3)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(3)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(4)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(4)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(5)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(5))  + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(6)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(6)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(7)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(7)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(8)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(8)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(9)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(9)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(10)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(10)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(11)) + ");"
                    );
            }else if (encoderPathLength.size()==13){
            moveHere = ("            goForward(" + (int)Math.round(encoderPathLength.get(0)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(0)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(1)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(1)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(2)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(2)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(3)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(3)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(4)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(4)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(5)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(5)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(6)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(6)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(7)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(7)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(8)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(8)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(9)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(9)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(10)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(10)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(11)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(11)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(12)) + ");"
                    );
            }else if (encoderPathLength.size()==14){
            moveHere = ("            goForward(" + (int)Math.round(encoderPathLength.get(0)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(0)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(1)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(1)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(2)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(2)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(3)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(3)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(4)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(4)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(5)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(5)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(6)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(6)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(7)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(7)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(8)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(8)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(9)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(9)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(10)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(10)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(11)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(11)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(12 )) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(12)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(13) ) + ");"
                    );
            }else if (encoderPathLength.size()==15){
            moveHere = ("            goForward(" + (int)Math.round(encoderPathLength.get(0)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(0)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(1)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(1)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(2)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(2)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(3)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(3)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(4)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(4)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(5)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(5)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(6)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(6)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(7)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(7)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(8)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(8)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(9)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(9)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(10)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(10)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(11)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(11)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(12)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(12)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(13)) + ");\n"
                    + "            rotate(" + (int)Math.round(angleChanges.get(13)) + ");\n"
                    + "            goForward(" + (int)Math.round(encoderPathLength.get(14)) + ");"
                    );
            }
    }
    
}
