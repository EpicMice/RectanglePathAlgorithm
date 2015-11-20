package Base.custom;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public  class PairMap<K,V> {

	//Holds Values of Keys.
	//K = key, V = value. 
	
	/**
	 *  keyMap holds the Keys to Values
	 */
	public HashMap<K,V> keyMap = new HashMap<K,V>();
	/**
	 * 	valueMap holds Values deposited in keyMap as Keys holding the set of keys they are mapped to
	 * in keyMap.
	 * 
	 * Example: <String,Integer> keyMap would have "hello world" mapped to 0.
	 * the valueMap would have 0 hold "hello world" within a set of other keys also mapped to 0 in keyMap
	 */
	public HashMap<V,Set<K>> valueMap = new HashMap<V,Set<K>>();
	
	public PairMap(){
		
	}
	
	
	
	public V put(K key, V value){
		if(getValueMap(getKeyMap(key)) != null){
		getValueMap(getKeyMap(key)).remove(key);
		if(getValueMap(getKeyMap(key)).isEmpty()){
			removeValueMap(getKeyMap(key));
		}
		}
		if(keyMap.containsKey(key)){///////////////////////////////////////////////
			if(getValueMap(value) == null){
				valueMap.put(value, new HashSet<K>());
			}
			getValueMap(value).add(key);											///
		}else{													//Adds value to valueMap as a key if the key exists.
			if(getValueMap(value) == null){
			valueMap.put(value, new HashSet<K>());
			}			//If it doesn't exist, place the set of keys to allow for adding into first.
			getValueMap(value).add(key);											///
		}//////////////////////////////////////////////////////////////////////////////		

		keyMap.put(key, value);		
		
		return value;
	}
	
	public V removeKey(K key){		
		if(keyMap.containsKey(key)){			
			if(getValueMap(getKeyMap(key)).size() == 1){
				removeValueMap(getKeyMap(key));
			}else{
				getValueMap(getKeyMap(key)).remove(key);
			}
			return removeKeyMap(key);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Set<K> removeValue(V val){
		
		if(keyMap.containsValue(val)){
			
			Object[] fill = getValueMap(val).toArray();
			
			for(int i = 0; i < fill.length; i++){
				removeKeyMap((K)fill[i]);
			}			
			return removeValueMap(val);			
		}
		return null;
	}
	
	public boolean containsKey(K key){
		return keyMap.containsKey(key);
	}
	
	public boolean containsValue(V val){
		return keyMap.containsValue(val);
	}
	
	public Set<K> getKeysOfValue(V val){
		return getValueMap(val);
	}
	
	public Set<K> getKeys(){
		return keyMap.keySet();
	}
	
	public Set<V> getValues(){
		return valueMap.keySet();
	}
	
	public boolean valueIsMappedToKey(K key, V val){
		return getValueMap(val).contains(key);
	}
	
	@Override
	public String toString(){
		return "Key Map: "+keyMap + "\n" + "Value Map: "+valueMap;
	}
	
	protected V getKeyMap(K key){
		return keyMap.get(key);
	}
	
	protected Set<K> getValueMap(V val){
		return valueMap.get(val);
	}
	
	protected V removeKeyMap(K key){		
		return keyMap.remove(key);		
	}
	
	protected Set<K> removeValueMap(V val){
		return valueMap.remove(val);
	}
	
	public V get(K key){
		return getKeyMap(key);
	}

	public void clear() {
		keyMap.clear();
		valueMap.clear();
	}

	public int size() {
		return this.keyMap.keySet().size();
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
		System.out.println(this.getClass().getName()+": Null Key of Value.");
		return null;
	}
	
}
