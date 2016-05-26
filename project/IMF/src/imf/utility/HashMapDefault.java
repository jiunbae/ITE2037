package imf.utility;

import java.util.HashMap;

@SuppressWarnings("serial")
public class HashMapDefault<K,V> extends HashMap<K,V> {
	  protected V defaultValue;
	  public HashMapDefault(V defaultValue) {
		  this.defaultValue = defaultValue;
	  }
	  @Override
	  public V get(Object k) {
		  return containsKey(k) ? super.get(k) : defaultValue;
	  }
}