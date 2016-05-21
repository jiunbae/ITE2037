package io;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import utility.HashMapDefault;
import utility.Pair;

public class Stage
{	
	public class objectInfo
	{
		public String name;
		public HashMapDefault<String, String> attrs = new HashMapDefault<String, String>("0");
		
		public objectInfo(String name) 
		{
			this.name = name;
		}
		
		protected void insert(String name, String option)
		{
			attrs.put(name, option);
		}
	}
	
	public class settings
	{
	}

	public String rootName;
	public List<objectInfo> nodes = new ArrayList<objectInfo>();
	
	public Stage(String name, int options)
	{
		try {
			File file = new File(name);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			
			rootName = doc.getDocumentElement().getNodeName();
			NodeList nList =  doc.getElementsByTagName("object");
			
			for(int i  =0 ; i < nList.getLength(); ++i)
			{
				Node nNode = nList.item(i);
				if(nNode.getNodeType() == Node.ELEMENT_NODE)
				{
					Element eElement = (Element) nNode;
					NamedNodeMap attrs = eElement.getAttributes();
					objectInfo oNode = new objectInfo(eElement.getNodeName());
					
					for(int j = 0; j < attrs.getLength(); ++j)
						oNode.insert(attrs.item(j).getNodeName(), attrs.item(j).getNodeValue());
					
					nodes.add(oNode);
				}
			}
			
		} catch (ParserConfigurationException e) {
			return;
		} catch (SAXException | IOException e) {
			return;
		}
	}

	public void loop(Consumer<objectInfo> func)
	{
		for(objectInfo oi : nodes)
			func.accept(oi);;
	}
}
