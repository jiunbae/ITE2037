package imf.processor;

import java.util.ArrayList;
import java.util.List;

public class ProcessManager implements ProcessEvent{
	List<ProcessEvent> processors = new ArrayList<ProcessEvent>();
	
	public void install(ProcessEvent processor)
	{
		processors.add(processor);
	}
	
	public void uninstall(ProcessEvent processor)
	{
		processors.remove(processor);
	}

	@Override
	public void Initilize() 
	{
		for(ProcessEvent processor : processors)
			processor.Initilize();
	}

	@Override
	public void Event() 
	{
		for(ProcessEvent processor : processors)
			processor.Event();
	}

	@Override
	public void EventIterator() 
	{
		for(ProcessEvent processor : processors)
			processor.EventIterator();
	}

	@Override
	public void Finalize() 
	{
		for(ProcessEvent processor : processors)
			processor.Finalize();
	}
}
