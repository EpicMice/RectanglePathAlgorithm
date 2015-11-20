package Base.custom;

public class IntArr {

	int[] localints;
	
	public IntArr(int...i){
		localints = i;
	}
	public IntArr(){
		localints = new int[0];
	}
	
	public IntArr(String...i){
		int[] hold = new int[i.length];
		for(int n = 0; n < i.length; n++){
			hold[n] = Integer.parseInt(i[n]);
		}
		this.localints = hold;
	}
	
	public boolean set(int index, int num){
		if(index < localints.length && index >= 0){
			localints[index] = num;
			return true;
		}
		return false;
	}
	
	public void append(int num){
		int[] hold = new int[localints.length+1];
		for(int i = 0; i < localints.length; i++){
			hold[i] = localints[i];
		}
		hold[localints.length] = num;
		localints = hold;
	}
	
	public int[] get(){
		return localints;
	}
	
	public int get(int index){
		if(index < localints.length && index >= 0){
			return localints[index];
		}
		return localints[index];
	}
	
	public boolean matches(int index, int num){
		if(index < localints.length && index >= 0){
			return localints[index] == num;
		}
		return false;
	}
	
	public void insertArray(int...i){
		this.localints = i;
	}
	
	public void insertArray(String...i){
		int[] hold = new int[i.length];
		for(int n = 0; n < i.length; n++){
			hold[n] = Integer.parseInt(i[n]);
		}
		
		this.localints = hold;
	}
	
	public int size(){
		return localints.length;
	}
	
	public IntArr getOffset(int index, int increment){
		IntArr hold = new IntArr(localints);
		hold.localints[index] += increment;
		return hold;
	}
	
	
	@Override 
	public String toString(){
		
		String s = "[";
		if(localints == null){
			return null;
		}
		for(int i = 0; i < localints.length; i++){
			if(i == 0){
			s+=	localints[i];
			}else{
			s+= ","+localints[i];
			}
		}
		s += "]";
		
		return s;
	}
	
	@Override
	public int hashCode(){		
		return this.toString().hashCode()+(""+localints.length).hashCode();
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj == null){
			return false;
		}
		if(!(obj instanceof IntArr)){
			return false;
		}		
		return this.hashCode() == obj.hashCode();
	}
	
}
