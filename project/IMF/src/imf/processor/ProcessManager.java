package imf.processor;

import java.util.HashMap;

public class ProcessManager implements IProcess
{
	HashMap<String, IProcess> processors = new HashMap<String, IProcess>();
	HashMap<String, IProcessUtility> processUtilitys = new HashMap<String, IProcessUtility>();
	
	public void install(String name, IProcess processor)
	{
		processors.put(name, processor);
	}
	public void installUtility(String name, IProcessUtility processUtility)
	{
		processUtilitys.put(name, processUtility);
	}
	
	public IProcess get(String name)
	{
		return processors.get(name);
	}
	public IProcessUtility getUtility(String name)
	{
		return processUtilitys.get(name);
	}
	
	public void uninstall(String name)
	{
		processors.remove(name);
	}
	public void uninstallUtility(String name)
	{
		processUtilitys.remove(name);
	}
	
	@Override
	public void initilize() 
	{	
		processors.forEach((name, processor)->processor.initilize());
	}

	@Override
	public void process() 
	{
		processors.forEach((name, processor)->processor.process());
	}

	@Override
	public void finalize() 
	{
		processors.forEach((name, processor)->processor.finalize());
	}
	
	@Override
	public void utility(Integer arg) 
	{
		
	}
}
