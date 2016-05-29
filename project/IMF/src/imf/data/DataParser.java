package imf.data;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DataParser
{	
	private Element root;
	private List<DataObject> datas = new ArrayList<DataObject>();
	
	public DataParser(String name, int options)
	{
		try {
			File file = new File(name);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			
			
			root = doc.getDocumentElement();
			appendNode(doc.getElementsByTagName("object"));
			appendNode(doc.getElementsByTagName("me"));
			
		} catch (ParserConfigurationException e) {
			return;
		} catch (SAXException | IOException e) {
			return;
		}
	}
	
	private void appendNode(NodeList nodes)
	{
		for(int i = 0; i < nodes.getLength(); ++i)
			datas.add(appendData(nodes.item(i)));
	}
	
	private DataObject appendData(Node node)
	{
		DataObject data = new DataObject(node.getNodeName());
		NamedNodeMap attrs = node.getAttributes();

		for(int j = 0; j < attrs.getLength(); ++j)
			data.insert(attrs.item(j).getNodeName(), attrs.item(j).getNodeValue());
		
		return data;
	}
	
	public DataObject get(String key)
	{
		for(DataObject o : datas)
			if(o.ID.equals(key))
				return o;
		return null;
	}
	
	public void loop(Consumer<DataObject> func)
	{
		for(DataObject oi : datas)
			func.accept(oi);
	}
}
