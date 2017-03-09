/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gvgenerator;

import gvgenerator.Constants.CentralityMetric;
import static gvgenerator.Helper.getCentralityScore;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.swing.JFileChooser;

/**
 *
 * @author Zach Witter, Izzet Fatih
 */
public class GvGenerator {
    
    
    /**
     * convert runs the conversion as run by Izzet by Janies request.
     */
    private static void convert(){
        Component sample = new Component() {};
        JFileChooser chooser = new JFileChooser();
        //chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.showOpenDialog(sample);
        File inFile = chooser.getSelectedFile();
        String logFile = inFile.getAbsolutePath();
        System.out.println(logFile);
        InputFile iFile = readNewFormatArrowFile(logFile, true);

        ArrayList<String> locations = iFile.getLocations();

        int[][] ambigMatrix = iFile.getAmbigMatrix();

        int[][] nonAmbigMatrix = iFile.getNonAmbigMatrix();

        //Calculate the total matrix. I run centrality on the combined matrix including ambig and nonambig transmissions.

        int[][] fullMatrix = new int[ambigMatrix.length][ambigMatrix.length];

        for (int i=0; i<locations.size(); i++)

        for (int j=0; j<locations.size(); j++)

        fullMatrix[i][j] = ambigMatrix[i][j] + nonAmbigMatrix[i][j];
        
        CentralityMetric metric = CentralityMetric.BETWEENNESS;
        double scores[] = getCentralityScore(metric, fullMatrix);

        //Generate graphviz file

        printGraphviz(locations, scores, ambigMatrix, nonAmbigMatrix, logFile.substring(0, logFile.length()-4).replace('.', '_') + ".gv",iFile);
    }
    
    
    public static InputFile readNewFormatArrowFile(String inputFile, boolean isMax) {

        //int matrix[][] = null;

        int ambigMatrix[][] = null, nonAmbigMatrix[][] = null;

        ArrayList<String> locations = new ArrayList<String>();

        int count = getLocationsFromNewFormatArrowFile(inputFile).size();
        //int count = 0;
        
        InputFile iFile = new InputFile();
        
        try {

            BufferedReader br = new BufferedReader(new FileReader(inputFile));

            

            //matrix = new int[count][count];

            ambigMatrix = new int[count][count];

            nonAmbigMatrix = new int[count][count];

            String line = null;
            
            
            
            int sId = -1, dId = -1;

            

                while ((line=br.readLine()) != null) {



                    if (line.length() > 0) {



                    String source;

                    String destination;
                    // Zach new edits
                        
                        // If the line contains a transmission, run similar
                        // logic to izzet's only splitting on the arrow head 
                        // as apposed to tabs/spaces
                        if(line.contains("-->")){
                            String[] lineData = line.split("-->");
                            source = lineData[0].trim();
                            destination = lineData[1].trim();
                            
                            if(!locations.contains(source)){
                                locations.add(source);
                            }
                            if (!locations.contains(destination)) {
                                locations.add(destination);
                            }     
                            
                            sId = locations.indexOf(source);
                            dId = locations.indexOf(destination);
                            ambigMatrix[sId][dId] = ambigMatrix[sId][dId] + 1;
                        }
                        // run the same logic as with ambiguous line. just
                        // seperating on the ambiguity. 
                        else if(line.contains("==>")){
                            String[] lineData = line.split("==>");
                            source = lineData[0].trim();
                            destination = lineData[1].trim();
                            
                             if(!locations.contains(source)){
                                locations.add(source);
                            }
                            if (!locations.contains(destination)) {
                                locations.add(destination);
                            } 
                            
                            sId = locations.indexOf(source);
                            dId = locations.indexOf(destination);
                            nonAmbigMatrix[sId][dId] = nonAmbigMatrix[sId][dId] + 1;
                        }
                        // If the line contains a colon the I know that it is 
                        // parameter data
                        else if(line.contains(":")){
                            String[] lineData = line.split(":");
                            
                            String firstVal = lineData[0];
                            String secondVal = lineData[1];
                            
                            if(firstVal.equals("scaling")){
                                iFile.setScaleFactor(secondVal);
                            }
                            else if(firstVal.equals("label")){
                                boolean toDisplay = false;
                                if(secondVal.equals("on")){
                                    toDisplay = true;
                                }
                                else{
                                    toDisplay = false;
                                }
                                iFile.setLabelDisp(toDisplay);
                            }
                            else if(firstVal.equals("fontname")){
                                iFile.setfontName(secondVal);
                            }
                            else if(firstVal.equals("rankdir")){
                                iFile.setDir(secondVal);
                            }
                            else if(firstVal.equals("fontsize")){
                                iFile.setFontSize(secondVal);
                            }
                            else if(firstVal.contains("[")){
                                String place = firstVal.substring(firstVal.indexOf("[") + 1,firstVal.indexOf("]"));
                                iFile.addColorList(place,secondVal);
                                
                            }
                            else if(firstVal.equals("size")){
                                iFile.setSize(secondVal);
                            }
                        }
                    // Zach new edits
                    }

                }

            br.close();

            int i = 0;

            for (String s:locations)

            System.out.println(i++ + " " + s);


        } catch (Exception e) {

            e.printStackTrace();

        }


        iFile.setLocations(locations);

        iFile.setAmbigMatrix(ambigMatrix);

        iFile.setNonAmbigMatrix(nonAmbigMatrix);

        return iFile;

}
    
    
    private static ArrayList<String> getLocationsFromNewFormatArrowFile(String filename) {

ArrayList<String> locations = new ArrayList<String>();

String line = null;

        try {

            BufferedReader br = new BufferedReader(new FileReader(filename));

            while ((line=br.readLine()) != null) {

            	

            if (line.length() > 0 && !line.contains(":")) {

            	

            String source;

            String destination;

            	

            if (line.contains("\t")) {

            source = line.split("\t")[0];

            destination = line.split("\t")[2];

            } else {

            source = line.split(" ")[0];

            destination = line.split(" ")[2];	

            }

            	

            if (!locations.contains(source))

            locations.add(source); 	

            if (!locations.contains(destination))

            locations.add(destination);      	

            }

            }

            br.close();

            //System.out.println(locations.size());

            //for (String s:locations)

            //	System.out.println(s);

        } catch (Exception e) {

            e.printStackTrace();

        }

        return locations;

}
    
    /**
     * 
     * @param locations locations loaded in from given input file
     * @param scores scores loaded from centrality
     * @param ambigMatrix ambiguous matrix connections
     * @param nonAmbigMatrix the matrix loaded from the file with non ambig connections
     * @param outputFile Where is this sucker being written too? 
     */
    public static void printGraphviz(ArrayList<String> locations, double[] scores, 
    int[][] ambigMatrix, int[][] nonAmbigMatrix, String outputFile, InputFile ifile) {

        try {    



            PrintWriter pw = new PrintWriter(new FileWriter(outputFile));

            pw.println("digraph \"" + outputFile.substring(5, outputFile.length()-3) + "\" {");

            pw.println("\tgraph [	fontname = "+ifile.getFontName()+",");

            pw.println("\t\tfontsize ="+ ifile.getFontSize()+" ,");

            pw.println("\t\tlabel = \"\\n\\n\\n\\n" + outputFile.substring(5, outputFile.length()-3) + "\"");

            pw.println("\t\tsize = "+ifile.getSize()+",");

            pw.println("\t\trankdir = "+ifile.getDirection()+",");


            pw.println("]");



            double totalScore = 0;

            for (int i=0; i<scores.length; i++)

            totalScore += scores[i];



            //Print nodes

            for (int i=0; i<locations.size(); i++) {
                String color ="";
                if(ifile.checkColor(locations.get(i)).length() == 0){
                    double avg = scores[i]/totalScore;

                    int colorCode = (int) (255-(100*avg));

                    color = Integer.toHexString(colorCode) + "0000";

               }
                else{
                    color = color + ifile.checkColor(locations.get(i));
                }
                pw.println("\tnode [shape = circle, fontname = \"Times bold\" color=\"#" + color + "\", style=filled, fixedsize = \"true\", fontsize=30, width=" + String.format("%.3g", (2+scores[i]*ifile.getScale())) + "]; " + locations.get(i) + ";");
     
            }



            //Print ambiguous edges

            for (int i = 0; i < ambigMatrix.length; i++) {

                for (int j = 0; j < ambigMatrix.length; j++)

                if (ambigMatrix[i][j] > 0)

                pw.println("\t" + locations.get(i) + " -> " + locations.get(j) + " [ label = " + "\"A (" + ambigMatrix[i][j] + ") \" style=\"dotted\" minlen=4 fontname = \"Times bold\" fontcolor=\"#ff0000\" fontsize=30 penwidth = " + ambigMatrix[i][j] + "];");       

            }



            //Print non-ambiguous edges

            for (int i = 0; i < nonAmbigMatrix.length; i++) {

                for (int j = 0; j < nonAmbigMatrix.length; j++)

                if (nonAmbigMatrix[i][j] > 0)

                pw.println("\t" + locations.get(i) + " -> " + locations.get(j) + " [ label = " + "\"N (" + nonAmbigMatrix[i][j] + ") \" style=\"solid\" minlen=4, fontname = \"Times bold\" fontcolor=\"#ff0000\" fontsize=30 penwidth = " + nonAmbigMatrix[i][j] + "];");       

            }

            pw.println("}");

            pw.close();   


        } catch (Exception e) {
            System.out.println("an error occurred and this will not print");
            e.printStackTrace();

        }

    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        System.out.println("Starting conversion");
        convert();
    }
    
}
