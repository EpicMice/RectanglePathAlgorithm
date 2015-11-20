package Core;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import Base.custom.IntArr;
import Base.custom.PairMap;
import Base.custom.SetPairMap;

public class CellGrid {
	BinarySpaceTree bst;
	
	//The Width/Height dimensions of the Cells to generate the rectangular rooms.
	int CELL_SIZE = 177;
	//The minimum width/hegiht variable to apply to the binary space tree.
	int minSize = 49;
	
	//The amount of rooms required to proceed room generation.
	//if the value of CELL_SIZE/minSize changes, the amount of rooms that can be generated also changes. Be sure to change this value based on rooms expected.
	int minimumRoomCount = 30;
	
	///int 1 = 1;
	PairMap<IntArr,Rectangle> cellMap = new PairMap<>(); 
	SetPairMap<IntArr,Rectangle> cellDivisions = new SetPairMap<>();  
	SetPairMap<IntArr,Rectangle> cellPath = new SetPairMap<>();
	SetPairMap<IntArr,Rectangle> cellMarked = new SetPairMap<>();
	//PairMap<Rectangle,Rectangle> connectMap = new PairMap<>();
	SetPairMap<Rectangle,Rectangle> parentMap = new SetPairMap<>();
	Set<Rectangle> borders = new HashSet<>();	
	SetPairMap<Rectangle,Rectangle> rooms = new SetPairMap<>();
	PairMap<IntArr,int[][]> roomLanding = new PairMap<>();
	
	public CellGrid(){
	//	setupGrid(3,3);
	//	pathGrid(cellDivisions.getValues());
	}
	
	
	public void setupGrid(int w, int h){

		Rectangle fill;
		IntArr pos;
		for(int o = 0; o < h; o++){
			for(int i = 0; i < w; i++){			
				cellMap.put(pos=new IntArr(i,o),fill = new Rectangle(i*CELL_SIZE,o*CELL_SIZE,CELL_SIZE,CELL_SIZE));
				roomLanding.put(pos, new int[CELL_SIZE/1][CELL_SIZE/1]);
				bst = new BinarySpaceTree(fill);
				for(Rectangle r: bst.Split(minSize,minSize)){
					cellDivisions.put(pos, r);
				}
			}
		}		
	}
	
	Random r = new Random();
	public Set<Rectangle> pathGrid(Set<Rectangle> availableRects){
		
		Set<Rectangle> middleCellRects = new HashSet<>(availableRects);
		Rectangle start =(Rectangle) middleCellRects.toArray()[r.nextInt(middleCellRects.size())]; //Get the center cell's random rectangle
		
		ArrayList<Rectangle> next = new ArrayList<Rectangle>();
		next.add(start);
		Rectangle current = start;
		int cycles = 0;
		Set<Rectangle> added = new HashSet<>();
		
		SetPairMap<IntArr,Rectangle> cellPathB = new SetPairMap<IntArr,Rectangle>();
		SetPairMap<IntArr,Rectangle> cellMarkedB = new SetPairMap<IntArr,Rectangle>();
		SetPairMap<Rectangle,Rectangle> parentMapB = new SetPairMap<>();
		Set<Rectangle> bordersB = new HashSet<>();
		while(true){
			
			IntArr cell = new IntArr(current.x/CELL_SIZE, current.y/CELL_SIZE);
			cellMarkedB.put(cell,current);
			cellPathB.put(cell, current);		
			
			Rectangle inbounds = new Rectangle(current.x-1,current.y-1,current.width+2,current.height+2);
			
			if(!cellMap.get(cell).contains(inbounds)){
				bordersB.add(current);
			}
			
			PairMap<Rectangle,Rectangle> choices = new PairMap<>();
			PairMap<Rectangle,Integer> adjacent = getAdjacent(current); 
				IntArr adjCell;
				for(Rectangle adjA: adjacent.getKeys()){
					adjCell = new IntArr(adjA.x/CELL_SIZE,adjA.y/CELL_SIZE);
					if(!cellMarkedB.getValues().contains(adjA) && !cellMarked.getValues().contains(adjA)){
						PairMap<Rectangle,Integer> across = getAdjacent(adjA);
						IntArr acrossCell;
						if(across.containsValue(adjacent.get(adjA))){
							for(Rectangle acrA: across.getKeysOfValue(adjacent.get(adjA))){
								acrossCell = new IntArr(acrA.x/CELL_SIZE,acrA.y/CELL_SIZE);
								if(!acrA.equals(current)){
									if(!cellMarkedB.getValues().contains(acrA)){
										
										if(!cellPath.containsValue(acrA)){
										choices.put(acrA,adjA);
										}
									}
								}
							}
						}
					}
				}

				if(choices.size() > 0){
					Rectangle choice =(Rectangle) choices.getKeys().toArray()[(r.nextInt(choices.getKeys().size()))];					
					Rectangle adjacentRect = choices.get(choice);					
					cellPathB.put(adjCell=new IntArr(adjacentRect.x/CELL_SIZE,adjacentRect.y/CELL_SIZE),adjacentRect);
					cellMarkedB.put(adjCell, adjacentRect);
					
					if(true){
						Set<Rectangle> oppRects = new HashSet<>();
						int dir = -1;
						switch(adjacent.get(adjacentRect)){
						case 0:
							dir = 1;
							break;
						case 1:
							dir = 0;
							break;
						case 2:
							dir = 3;						
							break;
						case 3:
							dir = 2;
							break;
						}
						
						if(adjacent.containsValue(dir)){
							oppRects.addAll(adjacent.getKeysOfValue(dir));
							Rectangle oppRect = (Rectangle) oppRects.toArray()[r.nextInt(oppRects.size())];
							IntArr oppCell = new IntArr(oppRect.x/CELL_SIZE,oppRect.y/CELL_SIZE);
							for(Rectangle r: oppRects){
							oppCell =	new IntArr(r.x/CELL_SIZE,r.y/CELL_SIZE);
							cellMarkedB.put(oppCell, r);
							}
						}
					
					}else{
					
						for(Rectangle r: adjacent.getKeys()){
							IntArr rCell = new IntArr(r.x/CELL_SIZE,r.y/CELL_SIZE);
							cellMarkedB.put(rCell, r);
						}
					
					}
					
					if(!cell.equals(new IntArr(choice.x/CELL_SIZE,choice.y/CELL_SIZE))){						
					//	connectMap.put(current, choice);	
					//	System.out.println(current+":"+choice);
					}
					
					parentMapB.put(choice,adjacentRect);
					parentMapB.put(adjacentRect,current);
					if(parentMap.containsKey(choice)){
						for(Rectangle p: parentMap.get(choice)){
						parentMapB.put(choice, p);
						parentMapB.put(p,choice);
						}
					}
					if(parentMap.containsKey(adjacentRect)){
						for(Rectangle p: parentMap.get(adjacentRect)){
							parentMapB.put(adjacentRect, p);
							parentMapB.put(p, adjacentRect);
						}
					}
					if(parentMap.containsKey(current)){
						for(Rectangle p: parentMap.get(current)){
						parentMapB.put(current, p);
						parentMapB.put(p,current);
						}
					}
					
					if(parentMap.containsValue(choice)){
						for(Rectangle p : parentMap.getKeysOfValue(choice)){
						parentMapB.put(p, choice);
						parentMapB.put(choice, p);
						}
					}
					if(parentMap.containsValue(adjacentRect)){
						for(Rectangle p : parentMap.getKeysOfValue(adjacentRect)){
						parentMapB.put(p, adjacentRect);
						parentMapB.put(adjacentRect, p);
						}
					}
					if(parentMap.containsValue(current)){
						for(Rectangle p : parentMap.getKeysOfValue(current)){
						parentMapB.put(p, current);
						parentMapB.put(current, p);
						}
					}
					
					next.add(current);
					next.add(choice);
					added.add(current);
					added.add(choice);
					added.add(adjacentRect);
					current = choice;
				}else{
					if(next.isEmpty()){
						System.out.println("::"+added.size()+":"+cell);
						if(cycles == 1){
							current = ((Rectangle)middleCellRects.toArray()[r.nextInt(middleCellRects.size())]);
							added.clear();
							cycles = 0;	
							cellPathB.clear();
							cellMarkedB.clear();
							parentMapB.clear();
							bordersB.clear();
							continue;
						}else
						if(added.size() < minimumRoomCount){
							current = ((Rectangle)middleCellRects.toArray()[r.nextInt(middleCellRects.size())]);
							cycles = 0;							
							added.clear();
							cellPathB.clear();
							cellMarkedB.clear();
							parentMapB.clear();
							bordersB.clear();
							continue;
						}
						break;
					}else{
						current = next.remove(next.size()-1);
					}
				}
				
				cycles++;	
		}
		
		for(IntArr cp: cellPathB.getKeys()){
			for(Rectangle r: cellPathB.get(cp)){
				cellPath.put(cp, r);
			}
		}
		for(IntArr cp: cellMarkedB.getKeys()){
			for(Rectangle r: cellMarkedB.get(cp)){
				cellMarked.put(cp, r);
			}
		}
		
		for(Rectangle p: parentMapB.getKeys()){
			for(Rectangle c: parentMapB.get(p)){
				parentMap.put(p, c);
			}
		}
		
		for(Rectangle b: bordersB){
			borders.add(b);
		}
		
		for(Rectangle add: added){
			pushRoom(add);
		}
		
			openDoors(added);
			return added;
	}
	
	public PairMap<Rectangle,Integer> getAdjacent(Rectangle area){
		PairMap<Rectangle,Integer> rects = new PairMap<>();
			
			Rectangle[] checkArea = 
					{new Rectangle(area.x,area.y-1,area.width,area.height),
					new Rectangle(area.x,area.y+1,area.width,area.height),
					new Rectangle(area.x-1,area.y,area.width,area.height),
					new Rectangle(area.x+1,area.y,area.width,area.height)}
			;
			
			IntArr cell = new IntArr(area.x/CELL_SIZE,area.y/CELL_SIZE);
			
			IntArr[] adjPos = {	new IntArr(cell.get(0),cell.get(1)),
								new IntArr(cell.get(0),cell.get(1)-1),
								new IntArr(cell.get(0),cell.get(1)+1),
								new IntArr(cell.get(0)-1,cell.get(1)),
								new IntArr(cell.get(0)+1,cell.get(1))};
			
			for(IntArr ia: adjPos){
				if(cellDivisions.containsKey(ia)){
					for(Rectangle r: cellDivisions.get(ia)){
						for(int i = 0; i < checkArea.length; i++){
							if(checkArea[i].intersects(r) || checkArea[i].contains(r)){
								if(!checkArea[i].equals(area)){
								rects.put(r, i);
								}
							}
						}
					}
				}
			}
			
			
			
		 
		return rects;
	}
	
	
	public Set<Rectangle> appendGrid(Set<IntArr> cellPositions){
		Rectangle fill;
		Set<Rectangle> hold = new HashSet<>();
		for(IntArr cp : cellPositions){
			if(!cellDivisions.containsKey(cp)){
				cellMap.put(cp, fill = new Rectangle(cp.get(0)*CELL_SIZE,cp.get(1)*CELL_SIZE,CELL_SIZE,CELL_SIZE));
				roomLanding.put(cp, new int[CELL_SIZE][CELL_SIZE]);
				bst = new BinarySpaceTree(fill);
				for(Rectangle r: bst.Split(minSize,minSize)){
					cellDivisions.put(cp, r);
					//hold.add(r);
				}
			
				for(Rectangle r: borders){
					Rectangle outerCell = new Rectangle(cellMap.get(cp));
					outerCell.setRect(outerCell.x-1,outerCell.y-1,outerCell.width+2,outerCell.height+2);
					if(outerCell.intersects(r) || outerCell.contains(r)){
						hold.add(r);
					}
				}
			}
			//hold.addAll(borders);
		}		
		
		return pathGrid(hold);
	}
	
	public void pushRoom(Rectangle r){		
		Set<Rectangle> placeDown = new HashSet<>();
		Set<Rectangle> remove = new HashSet<>();
		//rooms.removeKey(r);
				if(rooms.containsKey(r)){
					return;
				}
		for(int o = 0; o < r.height/1; o++){
			for(int i = 0; i < r.width/1; i++){
				
				Rectangle hold = new Rectangle(r.x+i,r.y+o,1,1);
				if(i == 0 || o == 0 || o==r.height-1 || i == r.width-1){
						placeDown.add(hold.getBounds());
					if((i == 0 && o == 0)||(i == 0 && o == r.height/1-1)||(i == r.width/1-1 && o == r.height/1-1) || (i == r.width/1-1 && o == 0)){
						continue;
					}				
					
					Rectangle holdB = hold.getBounds();
					if(o == 0){
						holdB.translate(0, -1);
						if(rooms.containsValue(holdB)){
						remove.add(holdB.getBounds());
						holdB.translate(0, 1);
						remove.add(holdB.getBounds());
						}
					}
					if(o == r.height-1){
						holdB.translate(0, 1);
						if(rooms.containsValue(holdB)){
						remove.add(holdB.getBounds());
						holdB.translate(0, -1);
						remove.add(holdB.getBounds());
						}
					}
					
					if(i == 0){
						holdB.translate(-1, 0);
						if(rooms.containsValue(holdB)){
						remove.add(holdB.getBounds());
						holdB.translate(1, 0);
						remove.add(holdB.getBounds());
						}
					}
					if(i == r.width/1-1){
						holdB.translate(1, 0);
						if(rooms.containsValue(holdB)){
						remove.add(holdB.getBounds());
						holdB.translate(-1, 0);
						remove.add(holdB.getBounds());	
						}
					}
					
				}
				
			}
		}
		
		for(Rectangle p: placeDown){
			rooms.put(r, p);
			roomLanding.get(new IntArr(p.x/CELL_SIZE,p.y/CELL_SIZE))[(p.y-((p.y/CELL_SIZE)*CELL_SIZE))/1][(p.x-((p.x/CELL_SIZE)*CELL_SIZE))/1] = 1;
		}
		/*
		Set<Rectangle> connectors = new HashSet<>();
		if(parentMap.containsKey(r)){
			connectors.addAll(parentMap.get(r));
		}
		if(parentMap.containsValue(r)){
			connectors.addAll(parentMap.getKeysOfValue(r));
		}
			connectors.add(r);
		for(Rectangle c: connectors){
			for(Rectangle p: remove){				
				if(rooms.containsKey(c) && rooms.get(c).contains(p)){
					rooms.removeValue(p);
				}
			}
		}
		//for(Rectangle p: remove){
		//	rooms.removeValue(p);
		//}
		*/
		 
	}
	
	SetPairMap<Rectangle,Rectangle> doorMap = new SetPairMap<>();
	public void openDoors(Set<Rectangle> rects){
		Set<Rectangle> remove = new HashSet<>();
		for(Rectangle r: rects){
			Set<Rectangle> connectors = new HashSet<>();
				if(parentMap.containsKey(r)){
					connectors.addAll(parentMap.get(r));
				}
				if(parentMap.containsValue(r)){
					connectors.addAll(parentMap.getKeysOfValue(r));
				}
				connectors.remove(r);
			for(Rectangle wall: rooms.get(r)){
				
				if(wall.x == r.x && (wall.y == r.y || wall.y == r.getMaxY()-1)){
					continue;
				}
				if(wall.x == r.getMaxX()-1 && (wall.y == r.y || wall.y == r.getMaxY()-1)){
					continue;
				}
					
				Rectangle hold = wall.getBounds();				
				hold.translate(0, -1);
				if(connectors.contains(rooms.getAvailableKeyOfValue(hold))){
					remove.add(hold.getBounds());
				}
				hold = wall.getBounds();				
				hold.translate(0, 1);
				if(connectors.contains(rooms.getAvailableKeyOfValue(hold))){
					remove.add(hold.getBounds());
				}
				hold = wall.getBounds();				
				hold.translate(-1, 0);
				if(connectors.contains(rooms.getAvailableKeyOfValue(hold))){
					remove.add(hold.getBounds());
				}
				hold = wall.getBounds();				
				hold.translate(1, 0);
				if(connectors.contains(rooms.getAvailableKeyOfValue(hold))){
					remove.add(hold.getBounds());
				}
				
			}			
		}
		
		for(Rectangle room: rects){			
			SetPairMap<Integer,Rectangle> sides = new SetPairMap<>();
			PairMap<Integer,Boolean> doorDir = new PairMap<>();
			boolean hasDoor = false;
			for(Rectangle rem: remove){
				if(rem.x == room.x && (rem.y == room.getMaxY()-1 || rem.y == room.y)){					
					continue;
				}
				if(rem.x == room.getMaxX()-1 && (rem.y == room.getMaxY()-1 || rem.y == room.y)){
					
					continue;
				}
			
				if(room.contains(rem)||room.intersects(rem)){
				//	rooms.removeValue(rem);
					int dir = -1;
					if(rem.y == room.y){//removing from top
						sides.put(dir=0, rem.getBounds());						
					}
					if(rem.y == room.getMaxY()-1){//removing from bottom
						sides.put(dir=1, rem.getBounds());
					}
					if(rem.x == room.x){//removing from left
						sides.put(dir=2, rem.getBounds());
					}
					if(rem.x == room.getMaxX()-1){//removing from right
						sides.put(dir=3, rem.getBounds());
					}
					int type = 0;
					hasDoor = false;
					if(!doorDir.containsKey(dir)){
					doorDir.put(dir, hasDoor);
					}
					IntArr rCell = new IntArr(rem.x/CELL_SIZE,rem.y/CELL_SIZE);
					int previousType = roomLanding.get(rCell)[(rem.y-((rem.y/CELL_SIZE)*CELL_SIZE))/1][(rem.x-((rem.x/CELL_SIZE)*CELL_SIZE))/1];					
					if(previousType != 2){
					roomLanding.get(rCell)[(rem.y-((rem.y/CELL_SIZE)*CELL_SIZE))/1][(rem.x-((rem.x/CELL_SIZE)*CELL_SIZE))/1]=type;
					}else{
						hasDoor = true;
						doorDir.put(dir,hasDoor);
					}
				}
			}
			if(doorDir.containsValue(false)){
				//System.out.println("test");
				for(Integer side: doorDir.getKeysOfValue(false)){
					if(!sides.containsKey(side)){
						continue;
					}
					
					int doorPos = sides.get(side).size();
					switch(sides.get(side).size()){
					case 1:
						doorPos = 0;
						break;
					case 2:
						doorPos = r.nextInt(2);
						break;
					case 3:
						doorPos = 1;
						break;
					default:
						doorPos = r.nextInt(sides.get(side).size()-(2))+(2);
						if(doorPos == 0){
							doorPos = 1;
						}
						if(doorPos == sides.get(side).size()-1){
							doorPos = sides.get(side).size()-2;
						}
						break;
					}
					
					Set<Rectangle> availableDoors = new HashSet<>(sides.get(side));
					if(sides.get(side).size() > 2){
						if(side == 0 || side == 1){
							Rectangle minX=null;
							Rectangle maxX = null;
							for(Rectangle s: availableDoors){
								if(minX == null || s.getMinX() < minX.getMinX()){
									minX = s;
								}
								if(maxX == null || s.getMaxX() > maxX.getMaxX()){
									maxX = s;
								}
							}
							availableDoors.remove(minX);
							availableDoors.remove(maxX);
							
						}else if(side == 3 || side == 2){
							Rectangle minY=null;
							Rectangle maxY = null;
							for(Rectangle s: availableDoors){
								if(minY == null || s.getMinY() < minY.getMinY()){
									minY = s;
								}
								if(maxY == null || s.getMaxY() > maxY.getMaxY()){
									maxY = s;
								}
							}
							availableDoors.remove(minY);
							availableDoors.remove(maxY);
						}
					}
					
					for(Rectangle doorHold: availableDoors){
						Rectangle connectorA;
						Rectangle connectorB;
						Rectangle doorHoldA;
						Rectangle doorHoldB;
						
						Rectangle doorA = doorHold.getBounds();					
						connectorA = rooms.getAvailableKeyOfValue(doorA);
						doorHoldA = doorA.getBounds();
						//rooms.removeValue(doorA);
						IntArr rCell = new IntArr(doorA.x/CELL_SIZE,doorA.y/CELL_SIZE);
						roomLanding.get(rCell)[(doorA.y-((doorA.y/CELL_SIZE)*CELL_SIZE))/1][(doorA.x-((doorA.x/CELL_SIZE)*CELL_SIZE))/1] = 2;
						doorA = doorA.getBounds();
						switch(side){
						case 0:
							doorA.translate(0, -1);
							break;
						case 1:
							doorA.translate(0, 1);
							break;
						case 2:
							doorA.translate(-1, 0);
							break;
						case 3:
							doorA.translate(1, 0);
							break;
						}
						connectorB = rooms.getAvailableKeyOfValue(doorA.getBounds());
						doorHoldB = doorA.getBounds();
						
						doorMap.put(doorHoldA, connectorA);
						doorMap.put(doorHoldA, connectorB);
						doorMap.put(doorHoldB, connectorA);
						doorMap.put(doorHoldB, connectorB);
					
						//rooms.removeValue(doorA);
						rCell = new IntArr(doorA.x/CELL_SIZE,doorA.y/CELL_SIZE);
						roomLanding.get(rCell)[(doorA.y-((doorA.y/CELL_SIZE)*CELL_SIZE))/1][(doorA.x-((doorA.x/CELL_SIZE)*CELL_SIZE))/1] = 2;
					}
				}
			}
			
		}
		/*	
			Rectangle current = rooms.getAvailableKeyOfValue(r);
			if(r.x == current.x && (r.y == current.getMaxY()-1 || r.y == current.y)){
				restore.put(current,r);
				continue;
			}
			if(r.x == current.getMaxX()-1 && (r.y == current.getMaxY()-1 || r.y == current.y)){
				restore.put(current,r);
				continue;
			}
			if(!restore.getValues().contains(r)){
			rooms.removeValue(r);
			}
		}
		for(Rectangle r: restore.getKeys()){
			for(Rectangle res: restore.get(r)){
			rooms.put(r, res);
			}
		}
		
	*/
		
	}
	
}
