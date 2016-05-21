package io;

import java.util.ArrayList;
import java.util.List;

public class Processor implements EventProcessor{
	List<EventProcessor> processors = new ArrayList<EventProcessor>();
	
	public void install(EventProcessor processor)
	{
		processors.add(processor);
	}
	
	public void uninstall(EventProcessor processor)
	{
		processors.remove(processor);
	}

	@Override
	public void Initilize() 
	{
		for(EventProcessor processor : processors)
			processor.Initilize();
	}

	@Override
	public void Event() 
	{
		for(EventProcessor processor : processors)
			processor.Event();
	}

	@Override
	public void EventIterator() 
	{
		for(EventProcessor processor : processors)
			processor.EventIterator();
	}

	@Override
	public void Finalize() 
	{
		for(EventProcessor processor : processors)
			processor.Finalize();
	}
}
