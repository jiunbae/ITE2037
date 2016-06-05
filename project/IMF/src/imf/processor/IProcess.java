package imf.processor;

public interface IProcess<T, R> extends IProcessProperty<T, R>
{
	void initilize(@SuppressWarnings("rawtypes") IProcess manager);
	
	void loop();
	
	void process();
	
	void finalize();
}
