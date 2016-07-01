package nimf.object;

import java.util.HashMap;

import imf.Const;
import imf.ValidateException;
import nimf.manager.ScriptManager.State;

public class ScriptObject extends Object{
	public String target = "", attribute = "", attr_name = "", attr_value = "";
	public State state;
	
	public ScriptObject() {
		super();
	}

	public ScriptObject(String name) {
		super(name);
	}
	
	public ScriptObject(HashMap<String, String> data) {
		
		// get attributes from data
		target = data.get("target");
		state = State.toState(data.get("state"));
		attribute = data.get("attribute");
		parse(attribute, attr_name, attr_value);
		
		// check validate
		if(Const.validate)
			try {
				validate(data.get("state"));
			} catch (ValidateException v){
				System.out.println("scripte validate error");
			}
	}
	
	private static boolean validate(String state) {
		String[] states = {"", "default", "always", "hover", "click", "interact", "collision" };
		for(String s : states)
			if (s.equals(state))
				return true;
		throw new ValidateException();
	}
	
	private static void parse(String attribute, String attr_name, String attr_value) {
		int pos = attribute.indexOf('=');
		if (pos == -1)
			return;
		attr_name = attribute.substring(0, pos);
		attr_value = attribute.substring(pos + 1);
	}
}
