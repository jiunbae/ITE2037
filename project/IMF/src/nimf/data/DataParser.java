package nimf.data;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import imf.Const;
import imf.utility.HashMapDefault;
import nimf.data.DataObject.Type;
import nimf.manager.IManager;
import nimf.manager.ScriptManager;
import nimf.manager.SpriteManager;
import nimf.object.ScriptObject;
import nimf.object.SpriteObject;

import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Data Parser Class
 * 
 * simple data parser
 * 
 * @package	imf.data
 * @author MaybeS
 * @version 1.0.0
 */
public class DataParser implements IManager
{	
	public static DataObject stage;
	public static List<DataObject> datas;
	
	public static void parse(String filename)
	{
		clear();
		try {
			File file = new File(filename);
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
			parseNode(doc.getDocumentElement());
			for (String s : Const.NODES)
				parseNodes(doc.getElementsByTagName(s));
		} catch (ParserConfigurationException e) {
			return;
		} catch (SAXException | IOException e) {
			// error can't find file
			return;
		}
	}
	
	public static String insertNode(DataObject object) {
		String ret = "";
		switch (object.type) {
			case stage:
			case sprite:
				SpriteObject s= new SpriteObject(object.s);
				SpriteManager.put(s);
				ret = s.name;
				break;
			case script:
				ScriptObject c = new ScriptObject(object.s);
				ScriptManager.put(c);
				ret = c.name;
				break;
			case none:
		}
		return ret;
	}
	
	private static void parseNodes(NodeList nodes) {
		for (int i = 0; i < nodes.getLength(); ++i)
			parseNode(nodes.item(i));
	}
	
	private static void parseNode(Node node) {
		DataObject data = new DataObject(node.getNodeName());
		data.put("parent", DataObject.nodes.get(node.getParentNode()));
		Node child = node.getFirstChild();
		if (child != null) {
			String s = child.getNodeValue().trim();
			if (s.length() > 0 && data.type.equals(Type.script)) 
				data.put("script", s.replace("\t", ""));
		}
		parseAttrs(data, node.getAttributes());
		DataObject.nodes.put(node, insertNode(data));
	}
	
	private static void parseAttrs(DataObject data, NamedNodeMap attrs) {
		for (int i = 0; attrs != null && i < attrs.getLength(); ++i)
			parseAttr(data, attrs.item(i));
	}
	
	private static void parseAttr(DataObject data, Node attr) {
		data.put(attr.getNodeName(), attr.getNodeValue());
	}

	public static void forEach(Consumer<DataObject> func) {
		datas.forEach((o)->func.accept(o));
	}
	
	public static void clear() {
		stage = null;
		datas = new ArrayList<DataObject>();
		DataObject.nodes = new HashMapDefault<Node, String>("");
	}
}
