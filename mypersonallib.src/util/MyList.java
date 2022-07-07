package util;

import java.util.Objects;

public class MyList<T> {
	private int lenght = 0;
	private T item;
	private MyList<T> firstItem, prevItem, nextItem, lastItem;

	public MyList()
		{	clear(); }

	public MyList(MyList<T> firstItem, int lenght, T item) {
		this();
		this.firstItem = firstItem;
		this.lenght = lenght;
		this.item = item;
	}

	public MyList(MyList<T> firstItem, MyList<T> lastItem, int lenght, T item) {
		this(firstItem, lenght, item);
		this.lastItem = lastItem;
	}

	public MyList(MyList<T> cloneList)
		{ cloneFrom(cloneList); }

	private void clear() {
		firstItem = prevItem = nextItem = lastItem = null;
		item = null;
		lenght = 0;
	}

	public void cloneFrom(MyList<T> listToCloneFrom) {
		if (listToCloneFrom == null)
			throw new RuntimeException("The origin list is null");
		firstItem = listToCloneFrom.firstItem;
		prevItem = listToCloneFrom.prevItem;
		nextItem = listToCloneFrom.nextItem;
		lastItem = listToCloneFrom.lastItem;
		item = listToCloneFrom.item;
		lenght = listToCloneFrom.lenght;
	}

	private void setFirstIndex(MyList<T> item) {
		MyList<T> next = firstItem;
		while (next != null) {
			next.firstItem = item;
			next = next.nextItem;
		}
	}

	private void setLastIndex(MyList<T> item) {
		MyList<T> next = firstItem;
		while (next != null) {
			next.lastItem = item;
			next = next.nextItem;
		}
	}
	
	private void firstToSecond() {
		MyList<T> second = firstItem.nextItem;
		firstItem.nextItem = second.nextItem;
		if (second.nextItem != null)
			second.nextItem.prevItem = firstItem;
		firstItem.item = second.item;
	}

	public void addFront(T item)
		{ insert(size(), item); }

	public void add(T item)
		{ insert(0, item); }

	public Boolean hasNext()
		{ return size() > 0; }

	public T next() {
		T item = get(0);
		remove(0);
		return item;
	}

	public T nextLast() {
		T item = get(size() - 1);
		remove(size() - 1);
		return item;
	}

	public void insert(int index, T item) {
		if (index < 0 || index > size())
			throw new IndexOutOfBoundsException();
		else if (size() == 0) { // Novo item em lista vazia
			lenght++;
			this.item = item;
			prevItem = nextItem = null;
			firstItem = lastItem = this;
		}
		else { // Novo item em uma lista existente, em um determinado indice
			MyList<T> current = firstItem, newItem = new MyList<T>(firstItem, lastItem, lenght + 1, item);
			int pos = 0, max = size();
			while (current != null || pos == max) {
				if (pos == index) {
					if (pos == max) {
						lastItem.nextItem = newItem;
						newItem.prevItem = lastItem;
						setLastIndex(newItem);
					}
					else {
						newItem.prevItem = current.prevItem;
						newItem.nextItem = current;
						if (pos > 0)
							current.prevItem.nextItem = newItem;
						current.prevItem = newItem;
						if (pos == 0)
							setFirstIndex(newItem);
					}
				}
				if (current != null) {
					current.lenght++;
					current = current.nextItem;
				}
				pos++;
			}
		}
	}
	
	public void remove(int index) {
		int inc = index < lenght / 2 ? 1 : -1, pos = inc == 1 ? 0 : lenght - 1;
		MyList<T> next = inc == 1 ? firstItem : lastItem, b1, b2;
		while (next != null) {
			next.lenght--;
			if (pos == index) {
				b1 = next.prevItem;
				b2 = next.nextItem;
				if (pos == 0 && next.nextItem != null && b2 != null)
					firstToSecond();
				else {
					if (pos > 0 && pos + 1 == lenght)
						setLastIndex(next.prevItem);
					if (b1 != null)
						b1.nextItem = b2;
					if (b2 != null)
						b2.prevItem = b1;
				}
			}
			next = inc == 1 ? next.nextItem : next.prevItem;
			pos += inc;
		}
		if (lenght == 0)
			clear();
	}

	public void remove(T item) {
		MyList<T> next = firstItem;
		int index = 0;
		while (next != null) {
			if (next.item.equals(item)) { 
				remove(index);
				break;
			}
			index++;
			next = next.nextItem;
		}
	}

	public int size()
		{ return lenght; }

	public T get(int index) {
		int inc = index < lenght / 2 ? 1 : -1, pos = inc == 1 ? 0 : lenght - 1;
		MyList<T> next = inc == 1 ? firstItem : lastItem;
		while (pos != index && (pos += inc) >= 0 && pos < lenght
		    && (inc == 1 ? next.nextItem : next.prevItem) != null)
						next = (inc == 1 ? next.nextItem : next.prevItem);
		return next.item;
	}

	public int indexOf(T item) {
		MyList<T> next = firstItem;
		for (int index = 0; next != null; index++, next = next.nextItem)
			if (next.item.equals(item))
				return index;
		return -1;
	}
	
	@Override
	public int hashCode()
		{ return Objects.hash(item); }

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		return Objects.equals(item, ((MyList) obj).item);
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("[");
		MyList<T> next = firstItem;
		while (next != null) {
			result.append(next.item);
			next = next.nextItem;
			if (next != null) result.append(", ");
		}
		result.append("]");
		return result.toString();
	}
	
}