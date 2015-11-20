package Core;

import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

public class BinarySpaceTree{

	//Currently being overridden in the Split method.
	/** size is the value of multiples to round the minimum size to.*/
	public int size = 2;
	
	public class Node{
		public Rectangle rect;
		public Node[] children = new Node[2];
		public Node parent = null;
		public Node(Rectangle rect){
			this.rect = rect;
		}		
		public boolean hasChildren(){
			return children[0] != null || children[1] != null;
		}
		public boolean hasChild(int index){
			return children[index] != null;
		}
		public Stack<Node> getAvailableChildren(){
			Stack<Node> hold = new Stack<>();
			if(hasChild(0)){
				hold.push(children[0]);
			}
			if(hasChild(1)){
				hold.push(children[1]);
			}
			return hold;
		}
	}
	
	Node start;
	
	public BinarySpaceTree(Rectangle r){
		start = new Node(r);
	}
	Random r =  new Random();
	
	
	//((minWidth/8)*3),
	//(minWidth/4)*2,//(minWidth/6)*4,
	//(minWidth/4)*3,//(minWidth/4)*3,
	//(minWidth/10)*3,
	//(minWidth/10)*4,
	//((minWidth/5)*3),
	//((minWidth/10)*2),
	//((minWidth/3)*2),
	//(minWidth/7)*4,
	//(minWidth/12)*8
	
	/**Minimum Width and Height are the sizes which end Nodes will be placed if they reach the threshold.*/
	public Set<Rectangle> Split(int minWidth, int minHeight){
		
		//This int[] determines what multiples to round the minWidth and minHeight to.
		//Make sure the outcome is even. If it's not even, placing doors based on center sides is not possible with how I'm currently doing it.
		int[] sizes = {				
				(minWidth/29)*12,	
				(minWidth/29)*14,	
				(minWidth/29)*16,	
				(minWidth/29)*18,	
				(minWidth/17)*8,	
				(minWidth/17)*10,	
				(minWidth/17)*6,				
				(minWidth/6)*4,				
				(minWidth/5)*2,	
				(minWidth/10)*6,
				};

		Set<Rectangle> rects = new HashSet<>();
		Stack<Node> ends = new Stack<>();
		Stack<Node> hold = new Stack<>();
		hold.add(start);
		
		while(!hold.isEmpty()){
			Node current;
			size = sizes[r.nextInt(sizes.length)];
			if((current = hold.pop()).hasChildren()){
				
				hold.push(current.children[0]);
				hold.push(current.children[1]);				
				
			}else{
				
				if(current.rect.width < minWidth && current.rect.height < minHeight){
					
					ends.add(current);
				}else{
					
				Rectangle[] boxes = new Rectangle[2];				
					if(current.rect.width == 0 || current.rect.height == 0){
						continue;
					}
					//Check if the width or height are 1.5 times greater than either, divide the rectangle further.
					if((current.rect.width/current.rect.height > 1.5) && (current.rect.height/current.rect.width < 1.5)){//horizontal
						int minX = (int)(current.rect.getMinX()+r.nextInt(current.rect.width/size)*size);						
						int nWidth = minX-current.rect.x;					
						boxes[0] = new Rectangle(current.rect.x,current.rect.y,nWidth,current.rect.height);						
						boxes[1] = new Rectangle(minX,current.rect.y,current.rect.width-nWidth,current.rect.height);					
											
					}else if((current.rect.height/current.rect.width > 1.5) && (current.rect.width/current.rect.height < 1.5)){//vertical
						int minY = (int)(current.rect.getMinY()+r.nextInt(current.rect.height/size)*size);						
						int nHeight = minY-current.rect.y;					
						boxes[0] = new Rectangle(current.rect.x,current.rect.y,current.rect.width,nHeight);						
						boxes[1] = new Rectangle(current.rect.x,minY,current.rect.width,current.rect.height-nHeight);					
						
					}else{
						if((current.rect.height < current.rect.width) || (current.rect.height == current.rect.width && r.nextBoolean())){
							int minX = (int)(current.rect.getMinX()+r.nextInt(current.rect.width/size)*size);						
							int nWidth = minX-current.rect.x;					
							boxes[0] = new Rectangle(current.rect.x,current.rect.y,nWidth,current.rect.height);						
							boxes[1] = new Rectangle(minX,current.rect.y,current.rect.width-nWidth,current.rect.height);				
						}else{
							int minY = (int)(current.rect.getMinY()+r.nextInt(current.rect.height/size)*size);						
							int nHeight = minY-current.rect.y;					
							boxes[0] = new Rectangle(current.rect.x,current.rect.y,current.rect.width,nHeight);						
							boxes[1] = new Rectangle(current.rect.x,minY,current.rect.width,current.rect.height-nHeight);	
						}
						
					}
					//If the height and width of the rectangles to be pushed into the division stack are equal, try again.
						if(boxes[0].getSize().equals(boxes[1].getSize())){
						hold.push(current);								
						}else{
						(current.children[0] = new Node(boxes[0])).parent = current;
						(current.children[1] = new Node(boxes[1])).parent = current;					
						hold.push(current.children[0]);
						hold.push(current.children[1]);
						System.out.println(size);
						}
				}	
			}
		}
			
		//Add all end nodes to the set.
		for(Node n: ends){
			rects.add(n.rect);
		}
		//Return the ends result set of nodes.
		return rects;
	}
	
}