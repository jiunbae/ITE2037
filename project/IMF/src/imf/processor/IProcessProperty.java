package imf.processor;

public interface IProcessProperty<T, R> {
	void setter(T object);
	R getter();
}
