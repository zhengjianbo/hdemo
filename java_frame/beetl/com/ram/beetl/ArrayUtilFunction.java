package com.ram.beetl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.ram.server.util.BaseLog;

public class ArrayUtilFunction {

	/**
	 * 
	 * @author 张健川 dlut.zjc@gmail.com
	 */
	public Object[] range(Object arrayOrCollection, int start, int end) {
		if (arrayOrCollection.getClass().isArray()) {
			Object[] array = (Object[]) arrayOrCollection;
			if (start >= end || start < 0 || end > array.length)
				throw new RuntimeException("start,end参数设置不正确");
			Object[] newArray = new Object[end - start];
			int index = 0;
			for (int i = start; i < end; i++) {
				newArray[index++] = array[i];
			}
			return newArray;
		} else if (arrayOrCollection instanceof Collection) {
			Collection<Object> collections = (Collection<Object>) arrayOrCollection;
			if (start >= end || start < 0 || end > collections.size())
				throw new RuntimeException("start,end参数设置不正确");
			Object[] array = new Object[end - start];
			Iterator<Object> iterator = collections.iterator();
			int i = 0;
			int index = 0;
			while (iterator.hasNext()) {
				if (i >= start && i < end) {
					array[index] = iterator.next();
					index++;
				} else {
					if (i >= end)
						break;
					iterator.next();
				}
				i++;
			}
			return array;
		}
		return null;
	}

	public Object remove(Object arrayOrCollection, Object item) {
		if (arrayOrCollection != null) {
			if (arrayOrCollection.getClass().isArray()) {
				Object[] oldArray = (Object[]) arrayOrCollection;
				Object[] newArray = new Object[oldArray.length - 1];
				Class type = arrayOrCollection.getClass().getComponentType();
				if (!(item.getClass().equals(type))) {
					throw new RuntimeException("item类型不正确");
				} else {
					int flag = 0;
					for (int i = 0; i < oldArray.length; i++) {
						if (oldArray[i] == item || oldArray[i].equals(item)) {
							flag = 1;
							continue;
						}
						if (i - flag == oldArray.length - 1)
							return oldArray;
						newArray[i - flag] = oldArray[i];
					}
					return newArray;
				}
			} else if (arrayOrCollection instanceof Collection) {
				((Collection) arrayOrCollection).remove(item);
				return arrayOrCollection;
			}
		}
		return null;
	}

	public Object add(Object o, Object item) {
		
		return this.add(o, item, 0);
	}

	public Object add(Object o, Object item, int index) {

		if (o != null) {
//			BaseLog.debug("o.getClass().isArray():"+o.getClass().isArray());
			if (o.getClass().isArray()) {
				Object[] oldArray = (Object[]) o;
				Object[] newArray = new Object[oldArray.length + 1];

				Class type = o.getClass().getComponentType();
				if (!(item.getClass().equals(type)) || index < 0
						|| index > oldArray.length) {
					throw new RuntimeException("item或者index参数不正确");
				} else {
					int flag = 0;
					for (int i = 0; i < oldArray.length + 1; i++) {
						if (i != index) {
							newArray[i] = oldArray[i - flag];
						} else {
							newArray[i] = item;
							flag = 1;
						}
					}
				}
				return newArray;
			} else if (o instanceof Collection) {
				Collection<Object> collection = (Collection<Object>) o;
				((Collection) o).add(item);
				return collection;
			}
		}
		return null;
	}

	public Object addArray(Object o, Object array) {

		if (o != null) {
			if (o.getClass().isArray()) {
				Object[] oldArray = (Object[]) o;
				Object[] addArray = (Object[]) array;

				Object[] newArray = new Object[oldArray.length
						+ addArray.length];

				Class type = o.getClass().getComponentType();

				int flag = 0;
				for (int i = 0; i < newArray.length; i++) {
					if (i <= oldArray.length - 1) {
						newArray[i] = oldArray[i];
					} else {
						newArray[i] = addArray[i - oldArray.length];
					}
				}
				return newArray;
			} else if (o instanceof Collection) {
				Collection<Object> collection = (Collection<Object>) o;
				((Collection) o).addAll((Collection) array);
				return collection;
			}
		}
		return null;
	}

	public Boolean contain(Object arrayOrCollection, Object item) {
		if (arrayOrCollection != null) {
			if (arrayOrCollection.getClass().isArray()) {
				Object[] array = (Object[]) arrayOrCollection;
				Class type = arrayOrCollection.getClass().getComponentType();
				if (!(item.getClass().equals(type))) {
					throw new RuntimeException("item或者index参数不正确");
				} else {
					for (int i = 0; i < array.length; i++) {
						if (array[i].equals(item) || array[i] == item) {
							return true;
						}
					}
					return false;
				}
			} else if (arrayOrCollection instanceof Collection) {
				Collection<Object> collection = (Collection<Object>) arrayOrCollection;
				return collection.contains(item);
			}
		}
		return false;
	}

	public Object[] toArray(Object... objects) {
		return objects;
	}

	public Object[] collection2Array(Collection cols) {
		return cols.toArray();
	}

	public static void main(String[] args) {
		ArrayUtilFunction util = new ArrayUtilFunction();
		List list = Arrays.asList(new String[] { "a", "b", "c", "d" });
		Object[] o = (Object[]) util.range(list.toArray(), 0, 2);
		int a = 1;

		Object[] x = (Object[]) util.addArray(o, new Object[] { "zz", "1" });

		for (Object z : x) {
			System.out.println("z:" + z);
		}

	}
}
