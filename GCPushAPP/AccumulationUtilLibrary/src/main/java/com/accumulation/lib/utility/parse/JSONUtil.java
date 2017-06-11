package com.accumulation.lib.utility.parse;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Simple JSON/Java object converter, only support java public field, and the
 * field name must match with the one in JSON
 * 
 * @author Zexu
 * 
 */
public class JSONUtil {

	public static <T> List<T> fromJSONArray(String jsonStr, Class<T> typeOfT)
			throws JSONException {
		return fromJSONArray(new JSONArray(jsonStr), typeOfT);
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> fromJSONArray(JSONArray jsa, Class<T> typeOfT) {
		List<T> list = new ArrayList<T>();
		try {
			boolean isPrimitive = isPrimitive(typeOfT);
			for (int i = 0; i < jsa.length(); i++) {
				Object item = jsa.get(i);
				if (isPrimitive)
					list.add((T) item);
				else {
					T object = fromJSON(jsa.getJSONObject(i), typeOfT);
					if (object != null)
						list.add(object);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static <T> JSONArray toJSONArray(List<T> list) {
		JSONArray jsa = new JSONArray();
		for (T item : list) {
			if (isPrimitive(item.getClass()))
				jsa.put(item);
			else
				jsa.put(toJSON(item));
		}
		return jsa;
	}

	public static <T> T fromJSON(String jsonStr, Class<T> typeOfT)
			throws JSONException {
		return fromJSON(new JSONObject(jsonStr), typeOfT);
	}

	/**
	 * Convert JSON to java object, only support public fields
	 * 
	 * @param root
	 * @param typeOfT
	 * @return
	 */
	public static <T> T fromJSON(JSONObject root, Class<T> typeOfT) {
		T object = null;
		try {
			object = (T) typeOfT.newInstance();
			Field[] fds = typeOfT.getFields();
			for (Field f : fds) {
				try {
					if (root.has(f.getName())) {
						Object val = root.opt(f.getName());
						if (val != null) {
							if (f.getType().isAssignableFrom(val.getClass())) {
								f.set(object, val);
							} else {
								if (isPrimitive(val.getClass())) {
									f.set(object, val);
								} else if (val instanceof JSONObject) {
									f.set(object,
											fromJSON((JSONObject) val,
													f.getType()));
								} else if (val instanceof JSONArray) {
									ParameterizedType stringListType = (ParameterizedType) f
											.getGenericType();
									Class<?> listItemClass = (Class<?>) stringListType
											.getActualTypeArguments()[0];
									f.set(object,
											fromJSONArray((JSONArray) val,
													listItemClass));
								}
							}
						}
					}
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return object;
	}

	/**
	 * Convert to JSON object, only support public fields
	 * 
	 * @param obj
	 * @return
	 */
	public static JSONObject toJSON(Object obj) {
		JSONObject root = new JSONObject();
		Field[] fds = obj.getClass().getFields();
		for (Field f : fds) {
			try {
				Object val = f.get(obj);
				if (val == null || Modifier.isTransient(f.getModifiers()))
					continue;
				if (isPrimitive(val.getClass())) {
					root.put(f.getName(), val);
				} else if (val instanceof List) {
					root.put(f.getName(), toJSONArray((List<?>) val));
				} else {
					root.put(f.getName(), toJSON(val));
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return root;
	}

	/**
	 * Checks first whether it is primitive and then whether it's wrapper is a
	 * primitive wrapper. Returns true if either is true
	 * 
	 * @param c
	 * @return whether it's a primitive type itself or it's a wrapper for a
	 *         primitive type
	 */
	public static boolean isPrimitive(Class<?> c) {
		if (c.isPrimitive()) {
			return true;
		} else if (c == Byte.class || c == Short.class || c == Integer.class
				|| c == Long.class || c == Float.class || c == Double.class
				|| c == Boolean.class || c == Character.class
				|| c == String.class) {
			return true;
		} else {
			return false;
		}
	}
}