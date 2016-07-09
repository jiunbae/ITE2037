package imf.utility;

@SuppressWarnings("rawtypes")
public class Pair<T, F>
{
	public T first;
	public F second;
	public Pair(T _first, F _second)
	{
		this.first =  _first;
		this.second = _second;
	}

	public boolean equalsFirst(Pair compare) {
		return compare.first.equals(first);
	}
	public boolean equalsSecond(Pair compare) {
		return compare.second.equals(first);
	}
	public boolean equals(Pair compare) {
		return equalsFirst(compare) && equalsSecond(compare);
	}
}