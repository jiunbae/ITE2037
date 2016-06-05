package imf.processor;

import java.util.HashMap;

@SuppressWarnings("rawtypes")
public class ProcessManager implements IProcess<String, IProcess>
{
	HashMap<String, IProcess> processors = new HashMap<String, IProcess>();
	IProcess ret;
	IProcessProperty property;
	
	public ProcessManager(IProcessProperty property)
	{
		this.property = property;
	}
	
	public void install(String name, IProcess processor)
	{
		processors.put(name, processor);
	}
	
	public IProcess get(String name)
	{
		return processors.get(name);
	}
	
	public void uninstall(String name)
	{
		processors.remove(name);
	}
	
	@Override
	public void initilize(IProcess manager) 
	{	
		processors.forEach((name, processor)->processor.initilize(manager));
	}

	@Override
	public void loop()
	{
		processors.forEach((name, processor)->processor.loop());
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
	public void setter(String object) 
	{
		ret = processors.get(object);
	}

	@Override
	public IProcess getter() 
	{
		return ret;
	}
}
