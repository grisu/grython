package grisu;

import grisu.frontend.view.cli.GrisuCliParameters;
import grith.jgrith.utils.CommandlineArgumentHelpers;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;

public class TestClass {

	public static void main(String[] args) {

		GrisuCliParameters p = new GrisuCliParameters();

		String[] r = CommandlineArgumentHelpers.extractNonGridParameters(p, args);
		List<String> l = Arrays.asList(r);
		System.out.println(StringUtils.join(l, "|"));

        //FileManager fm = GrisuRegistryManager.getDefault(si).getFileManager();
        //GridFile file = fm.ls('grid://sites/Auckland/gram.uoa.nesi.org.nz/home/markus.binsteiner');
        //file.getChildren()

	}

}
