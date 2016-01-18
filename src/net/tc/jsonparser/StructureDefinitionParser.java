package net.tc.jsonparser;

import java.io.FileReader;
import java.util.Map;

import org.json.simple.parser.JSONParser;

import net.tc.data.db.Schema;
import net.tc.data.db.Table;

public interface StructureDefinitionParser {

	public Schema parseSchema(JSONParser parser, FileReader fr, Map tableInstances);
	public Table parseTable(JSONParser parser,FileReader fr);
	
}
