package com.tengen;



public class Denek {
 
	private static <T extends Enum<T>> T findEnumValue(final Class<T> cls, final Object value) 
	{
	    if (value == null) {
	        return null;
	    }
	    if (value instanceof Enum<?>) {
	        return (T) value;
	    }
	    if (value instanceof String) {
	        for (T item : cls.getEnumConstants()) {
	            if (item.name().equals(value)) {
	                return item;
	            }
	        }
	    }
	    return null;
	}

	public enum Name {
	    JOHN,
	    MARK;
	}
 public static void main(String[] args) {
	 System.out.println(Denek.findEnumValue(Name.class, Name.JOHN.name()));
 }
}
