package util;

public class MyList2<T> {
	private T[] itemList;

	public MyList2()
		{ clear(); }

	private void clear()
		{ itemList = newGenericArray(0); }
	
	@SuppressWarnings("unchecked")
	private T[] newGenericArray(int size)
		{ return (T[])new Object[size]; }

	public void addFront(T item)
		{ insert(0, item); }

	public void add(T item) 
		{ insert(itemList.length, item); }

	public Boolean hasNext()
		{ return size() > 0; }

	public T next() {
		T obj = itemList[0];
		remove(0);
		return obj;
	}

	public T nextFront() {
		T obj = itemList[size() - 1];
		remove(size() - 1);
		return obj;
	}

	public void insert(int index, T item) {
		int size = itemList.length + 1;
		T[] list = itemList;
		itemList = newGenericArray(size);
		for (int i = 0, i2 = 0; i < size; i++, i2++) {
			if (i == index)
				itemList[i2++] = item;
			if (i < size - 1)
				itemList[i2] = list[i];
		}
	}

	public void remove(int index) {
		int size = itemList.length;
		T[] list = newGenericArray(size - 1);
		for (int i = 0, i2 = 0; i < size; i++)
			if (i != index) 
				list[i2++] = itemList[i];
		itemList = list;
	}

	public void remove(T obj) {
		for (int i = 0; i < size(); i++)
			if (itemList[i].equals(obj))
				remove(i);
	}

	public int size()
		{ return itemList.length; }

	public T get(int index)
		{ return itemList[index]; }

	public int indexOf(T obj) {
		for (int i = 0; i < size(); i++)
			if (itemList[i].equals(obj))
				return i;
		return -1;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("[");
		for (int i = 0; i < size(); i++) {
			result.append(itemList[i]);
			if ((i + 1) < size())
				result.append(", ");
		}
		result.append("]");
		return result.toString();
	}

}
