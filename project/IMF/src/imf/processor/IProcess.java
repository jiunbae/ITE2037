package imf.processor;

public interface IProcess<T, R>
{
	void initilize(IProcess manager);
	
	void setter(T object);
	R getter();
	
	void loop();
	
	void process();
	
	void finalize();
}
