package com.tfc.effekseer4j;//package com.tfc.effekseer4j;
//
//import java.lang.reflect.Field;
//
//@SuppressWarnings({"unused", "RedundantSuppression"})
//class Unsafe {
//	private static final sun.misc.Unsafe theUnsafe;
//
//	static {
//		try {
//			sun.misc.Unsafe instance;
//			try {
//				instance = sun.misc.Unsafe.getUnsafe();
//			} catch (Throwable ignored) {
//				Class<sun.misc.Unsafe> unsafe = sun.misc.Unsafe.class;
//				Field f = unsafe.getDeclaredField("theUnsafe");
//				f.setAccessible(true);
//				instance = (sun.misc.Unsafe) f.get(null);
//			}
//			theUnsafe = instance;
//		} catch (Throwable err) {
//			if (err instanceof RuntimeException) throw (RuntimeException) err;
//			throw new RuntimeException(err);
//		}
//	}
//
//	@SuppressWarnings("unchecked")
//	public static <T> T allocate(Class<T> clazz) {
//		try {
//			return (T) theUnsafe.allocateInstance(clazz);
//		} catch (Throwable err) {
//			if (err instanceof RuntimeException) throw (RuntimeException) err;
//			throw new RuntimeException(err);
//		}
//	}
//
//	public static <T, A> void setObject(T object, String fieldName, A value) {
//		theUnsafe.putObject(object, theUnsafe.objectFieldOffset(getField(object, fieldName)), value);
//	}
//
//	private static <T> Field getField(T object, String fieldName) {
//		for (Field field : object.getClass().getFields()) if (field.getName().equals(fieldName)) return field;
//		for (Field field : object.getClass().getDeclaredFields()) if (field.getName().equals(fieldName)) return field;
//		throw new RuntimeException(new NoSuchFieldException("Field " + fieldName + " could not be found in " + object.getClass().getName()));
//	}
//}
