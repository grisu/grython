package grisu;

import grisu.frontend.view.cli.GrisuCliParameters;
import grith.jgrith.utils.CommandlineArgumentHelpers;

import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterDescription;



public class GrythonNew {
    public static void main(String[] args) {
    	
    	GrisuCliParameters p = new GrisuCliParameters();
    	
    	String[] gps = CommandlineArgumentHelpers.extractGridParameters(p, args);
    	String[] ngps = CommandlineArgumentHelpers.extractNonGridParameters(p, args);
    	
    	for ( String g : gps ) {
    		System.out.println("Grid: "+g);
    	}
    	for ( String g : ngps ) {
    		System.out.println("Non-Grid: "+g);
    	}
    	
    	JCommander jc = new JCommander(new GrisuCliParameters(), gps);
    	
    	
    }
}
