/*
 * ReadMappingMain.java
 * This class functions as a short read mapper for small sequences.
 * (c) Eric Hertz, 2011
 * 
 * @author Eric Hertz
 * @version 0.1
 * @date 05/10/2011
 */
package readmapping;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Eric
 */

public class ReadMappingMain {
    
    //Change these paths to the paths on your file system for the input files
    private static String inPath = "C:\\Users\\Eric\\Documents\\CS124\\ReadMapping\\test_match.txt";
    private static String longMatchPath = "C:\\Users\\Eric\\Documents\\CS124\\ReadMapping\\test_input_long.txt";
    private static String matchPath = "C:\\Users\\Eric\\Documents\\CS124\\ReadMapping\\test_input.txt";
    private static String threeBillionPath = "C:\\Users\\Eric\\Documents\\CS124\\ReadMapping\\generated.txt";
    private static String inData;
    private static String matchData;
    private static long totalMatches = 0;
    
    //This is the mismatch threshold - change it how you want
    private static int maxMisses = 2;
    private static boolean use_three_billion = true;
    
    //Buffer size constants
    private static long longLength = 2000000000;
    private static int maxBufferSize = 1000000;
    
    public static void writeLongFile(String data, String pathName, long length, boolean rewrite) {
        try {
            FileWriter fw = new FileWriter(pathName);
            BufferedWriter bfw = new BufferedWriter(fw);
            
            StringBuilder buffer = new StringBuilder(data);
            long curr = buffer.length();
            while(curr < length) {
                bfw.write(buffer.substring(0) + '\n');
                curr += buffer.length();
            }
            
            bfw.close();
            
            if(use_three_billion && rewrite) {
                writeLongFile(data, pathName, 1000000000, false);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static List<String[]> findMatches(String input, String baseString, int mismatches) {
        int foundMismatches = 0;
        List<String[]> result = new ArrayList<String[]>();
        
        for(int i = 0; i < baseString.length() - 30; i++) {
            foundMismatches = 0;
            String sub = baseString.substring(i, i+30);
            if(sub.equals(input)) {
                totalMatches++;
                result.add(new String[]{sub, Integer.toString(i)});
            } else {
                for(int j = 0; j < input.length(); j++) {
                    if(sub.charAt(j) != input.charAt(j)) {
                        foundMismatches++;
                        if(foundMismatches > mismatches) {
                            foundMismatches = 0;
                            break;
                        }
                    }
                }
                if(foundMismatches > 0) {
                    totalMatches++;
                    result.add(new String[]{sub, Integer.toString(i)});
                }
            }
        }
        
        return result;
    }
    
    public static void main(String[] args) {
        //write new long file
        //boolean testLong = false; boolean writeNewLong = true; boolean testSuperLong = false;
        
        //test against normal long file
        //boolean testLong = true; boolean writeNewLong = false; boolean testSuperLong = false;
        
        //test against super long file
        boolean testSuperLong = true; boolean writeNewLong = false; boolean testLong = false;
        
        //test against small file
        //boolean testLong = false; boolean writeNewLong = false; boolean testSuperLong = false;
        
        StopWatch s = new StopWatch();
        String input;
        StringBuilder buffer = new StringBuilder();
        
        try {
            // Read in sequence to match
            File inFile = new File(inPath);
            FileReader ifs = new FileReader(inFile);
            BufferedReader bfs = new BufferedReader(ifs);
            while((input = bfs.readLine()) != null) {
                buffer.append(input);
            }
            inData = buffer.substring(0);
            buffer = null;
            buffer = new StringBuilder();
            // Read in sequence to match against
            File matchFile;
            if(testLong) {
                matchFile = new File(longMatchPath);
            } else if(testSuperLong){
                matchFile = new File(threeBillionPath);
            } else {
                matchFile = new File(matchPath);
            }
            FileReader ifsMatch = new FileReader(matchFile);
            BufferedReader bfsMatch = new BufferedReader(ifsMatch);
            List<String[]> results = new ArrayList<String[]>();
            s.start();
            if(writeNewLong) {
                System.out.println("Writing new long test file.");
                while((input = bfsMatch.readLine()) != null) {
                    buffer.append(input);
                }
                matchData = buffer.substring(0);
                if(use_three_billion) {
                    System.out.println("Writing 3 billion characters to file.");
                    writeLongFile(matchData, threeBillionPath, longLength, true);
                } else {
                    System.out.println("Writing 2 billion characters to file.");
                    writeLongFile(matchData, longMatchPath, longLength, true);
                }
                System.out.println("Done writing new long test file.");
            } else {
                char c;
                int rvalue;
                System.out.println("Testing data now.");
                long setNum = 0;
                while((rvalue = bfsMatch.read()) != -1) {
                    c = (char)rvalue;
                    buffer.append(c);
                    if(buffer.length() >= maxBufferSize) {
                        results = findMatches(inData, buffer.substring(0), maxMisses);
                        System.out.println("Matching set #" + setNum + " with " + results.size() + " matches.");
                        buffer.delete(30, buffer.length());
                        setNum++;
                    }
                }
                results = findMatches(inData, buffer.substring(0), maxMisses);
                System.out.println("Matching set #" + setNum + " with " + results.size() + " matches.");
                System.out.println("Done testing data.");
            }
            s.stop();
                   
            // write out any matches to standard output
            if( ! testLong ) {
                System.out.println("Number of results: " + results.size());
            }
            System.out.println("Total matches: " + totalMatches);
            System.out.println("Elapsed time in seconds: " + s.getElapsedTimeSecs());
            System.out.println("Elapsed time in milliseconds: " + s.getElapsedTime());
       } catch(Exception e) {
           e.printStackTrace();
       }
    }
}