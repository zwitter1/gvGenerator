/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gvgenerator;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Izzet Fatih
 */
public class InputFile {
  private ArrayList<String> locations;
  private int ambigMatrix[][], nonAmbigMatrix[][];
// Zach's added file parameters
  private int scaleFact = 8;
  private HashMap<String,String> colorhash = new HashMap();
  private boolean showLabels = true;
  private String fontname = "\"Times New Roman\"";
  private String fontsize = "12";
  private String direction = "\"LR\"";
  private String size = "\"8,8\"";
  
  public ArrayList<String> getLocations() {

    return locations;

  }
  
  public void setSize(String input){
      size = input;
  }
  
  public String getSize(){
      return size;
  }
  
  public void setDir(String inVal){
      direction = inVal;
  }
  
  public int getScale(){
      return scaleFact;
  }
  
  public String getFontName(){
      return fontname;
  }
  
  public String getFontSize(){
      return fontsize;
  }
  
  public String getDirection(){
      return direction;
  }
  
  public boolean isLabel(){
      return showLabels;
  }
  
  
  public void addColorList(String place,String color){
      colorhash.put(place, color);
  }
  
  public String checkColor(String test){
      if(colorhash.containsKey(test)){
          return colorhash.get(test);
      }
      else{
          return "";
      }
  }
  
  public void setFontSize(String inNum){
     fontsize = inNum;
  }
  
  public void setfontName(String infont){
      fontname = infont;
  }
  
  public void setLabelDisp(boolean status){
      showLabels = status;
  }
  
  public void setScaleFactor(String inNum){
      scaleFact = Integer.parseInt(inNum);
  }
  
  public void setLocations(ArrayList<String> locations) {

    this.locations = locations;

  }

  public int[][] getAmbigMatrix() {

    return ambigMatrix;

  }

  public void setAmbigMatrix(int[][] ambigMatrix) {

    this.ambigMatrix = ambigMatrix;

  }

  public int[][] getNonAmbigMatrix() {

    return nonAmbigMatrix;

  }

  public void setNonAmbigMatrix(int[][] nonAmbigMatrix) {

    this.nonAmbigMatrix = nonAmbigMatrix;

  }
}
