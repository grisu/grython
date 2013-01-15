package grisu;

import grisu.frontend.view.cli.GrisuCliParameters;
import grith.jgrith.utils.CommandlineArgumentHelpers;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class TestClass {
	
	public static void main(String[] args) {
		
		GrisuCliParameters p = new GrisuCliParameters();
		
		String[] r = CommandlineArgumentHelpers.extractNonGridParameters(p, args);
		List<String> l = Arrays.asList(r);
		System.out.println(StringUtils.join(l, "|"));
		
	}

}
