package ro.mihai.tpt.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class NamedEntityCollection {

	public static String sortedNames(Collection<? extends INamedEntity> entities) {
		ArrayList<String> names = new ArrayList<String>();
		for(INamedEntity s : entities) names.add(s.getName());
		Collections.sort(names);
		
		StringBuilder b = new StringBuilder();
		for(String n : names)
			b.append(n+" ");
		return b.toString();
	}

	public static String sortedIDs(Collection<? extends INamedEntity> entities) {
		ArrayList<String> ids = new ArrayList<String>();
		for(INamedEntity s : entities) ids.add(s.getId());
		Collections.sort(ids);
		
		StringBuilder b = new StringBuilder();
		for(String id : ids)
			b.append(id+" ");
		return b.toString();
	}
	
	
}