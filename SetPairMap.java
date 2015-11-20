package Base.custom;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class SetPairMap<K,V> {

	private HashMap<K,Set<V>> keyMap;
	
	private HashMap<V,Set<K>> valueMap;
	
	public SetPairMap(){
		 keyMap = new HashMap<K,Set<V>>();
		 valueMap = new HashMap<V,Set<K>>();
	}
	
	public SetPairMap(int size) {
		keyMap = new HashMap<K,Set<V>>(size);
		valueMap = new HashMap<V,Set<K>>(size*4);
	}

	public void put(K key,V val){
		
		if(!keyMap.containsKey(key)){
			keyMap.put(key, new HashSet<V>());
		}
		keyMap.get(key).add(val);
		
		if(!valueMap.containsKey(val)){
			valueMap.put(val, new HashSet<K>());
		}
		valueMap.get(val).add(key);
		
	}
	
	public void removeKey(K key){
		if(!keyMap.containsKey(key)){
			return;
		}
		Object[] keys = keyMap.get(key).toArray();
		
		for(int i = 0; i < keys.length; i++){
			@SuppressWarnings("unchecked")
			V val = (V) keys[i];
			valueMap.get(val).remove(key);
			if(valueMap.get(val).size() == 0){
				valueMap.remove(val);
			}
		}
		keyMap.remove(key);		
	}
	
	public void removeValue(V val){
		if(valueMap.get(val) == null){
			return;
		}
		Object[] keys = valueMap.get(val).toArray();
		
		for(int i = 0; i < keys.length; i++){
			@SuppressWarnings("unchecked")
			K key = (K) keys[i];
			if(keyMap.containsKey(key)){
			keyMap.get(key).remove(val);
			if(keyMap.get(key).size() == 0){
				keyMap.remove(key);
			}
			}
		}
		valueMap.remove(val);		
		
	}
	
	public Set<V> getValues(){
		return valueMap.keySet();
	}
	
	public Set<V> get(K key){
		return keyMap.get(key);
	}
	
	@Override
	public String toString(){
		return "Key Map: "+keyMap + "\n" + "Value Map: "+valueMap;
	}
	
	public boolean keyIsMappedToValue(K key, V val){
		
		if(keyMap.get(key) != null  && !keyMap.isEmpty()){
			if(keyMap.get(key).contains(val)){
				return true;
			}
		}
		
		return false;
		
	}
	
	public boolean valueIsMappedToKey(K key, V val){
		if(valueMap.get(val) != null  && !valueMap.isEmpty()){
			if(valueMap.get(val).contains(key)){
				return true;
			}
		}
		return false;
	}
	
	public Set<K> getKeysOfValue(V val){
		return valueMap.get(val);
	}
	
	public Set<K> getKeys(){
		return keyMap.keySet();
	}

	public boolean containsKey(K key) {
		return this.keyMap.containsKey(key);
	}

	public int size(){
		return this.keyMap.keySet().size();
	}

	public void clear() {
		keyMap.clear();
		valueMap.clear();
		
	}
	
	public void removeValueAtKey(K k, V v){
		if(keyMap.containsKey(k)){
			keyMap.get(k).remove(v);
			if(keyMap.get(k).isEmpty()){
				keyMap.remove(k);
			}
			if(valueMap.containsKey(v)){
				valueMap.get(v).remove(k);
				if(valueMap.get(v).isEmpty()){
					valueMap.remove(v);
				}
			}
		}
	}
	
	public void removeSetOfValues(Set<V> values){
		for(V v: values){
			this.removeValue(v);
		}
	}

	public boolean containsValue(V val) {	
	//	System.out.println(keyMap.values()+":"+valueMap.keySet());
		return this.valueMap.containsKey(val);
	}
	
	/**Returns the first available key at the value.
	 * valueMap.get(VALUE).toArray()[0] <--
	 * If it is in the valueMap.
	 * */
	@SuppressWarnings("unchecked")
	public K getAvailableKeyOfValue(V val){
		if(valueMap.containsKey(val)){
			return (K) valueMap.get(val).toArray()[0];
		}
		return null;
	}
	
}
