package imf.processor;

public interface IProcess extends IProcessUtility
{	
	/**
	 * 
	 */
	void initilize();
	
	/**
	 * 
	 */
	void process();
	
	/**
	 * 
	 */
	void finalize();
}
