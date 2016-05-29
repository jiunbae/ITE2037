package imf.processor;

import java.util.ArrayList;
import java.util.List;

public class ProcessManager implements Process{
	List<Process> processors = new ArrayList<Process>();
	
	public void install(Process processor)
	{
		processors.add(processor);
	}
	
	public void uninstall(Process processor)
	{
		processors.remove(processor);
	}

	@Override
	public void initilize() 
	{
		for(Process processor : processors)
			processor.initilize();
	}

	@Override
	public void process() 
	{
		for(Process processor : processors)
			processor.process();
	}

	@Override
	public void finalize() 
	{
		for(Process processor : processors)
			processor.finalize();
	}
}
