import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.*;

public class FigureEditorFrame extends JFrame { //Jframe 클래스를 상속받는다.
    
	PanelA pa; //PanalA 객체 선언 
	public FigureEditorFrame()
	{
		setTitle("My Figure Editor ★");  //frame의 제목을 설정합니다.
		setSize(800,400);                //프레임 크기를 800x400으로 설정합니다.
		
		pa = new PanelA(); // PanelA 객체 생성, 생성자 호출 
		
		Container contentPane = getContentPane(); //콘텍트 페인을 알아낸다.
		contentPane.add(pa, BorderLayout.CENTER); //panel A를 center(가운데)위치에 배치
		contentPane.add(new PanelC(pa),BorderLayout.LINE_START); //pa 객체를 PanelC 객체 생성시 전달
		setVisible(true); //프레임이 화면에 출력되도록 합니다.
	}
}

/*패널 A 클래스 - 도형 그리는 곳 */
class PanelA extends JPanel{ 
	JLabel label;
	Point start, end, clicked, pressed;
	int isSelected;      //도형선택이 되면 1
	int isLoad;          //불러오기 
	int isSave;          //저장
	boolean isDragged;   //도형을 이동할 것인지?
	boolean isFigure;    //도형을 그릴 것인지?
	boolean isSizeCon;   //좌측상단의 control panel을 움직일 것인지?
	boolean isSizeCon2;  //우측하단의 control panel을 움직일 것인지?
	Shape stmp; Shape stmp2; //도형 크기 조절시 객체를 임시로 저장
	
	ArrayList<Shape> shapes = new ArrayList<Shape>(); //지금까지 그린 도형들을 임의로 저장하는 ArrayList
	ArrayList<Shape> sel = new ArrayList<Shape>(); //클릭했을 때 해당 좌표 내에 있는 도형들을 모두 저장하는 리스트
	int[] offX = new int [100]; //도형 이동시 선택된 도형들의 좌표들을 배열에 임의로 저장
	int[] offY = new int [100];
	int[] offMX = new int [100];
	int[] offMY = new int [100];
	
	
	public PanelA() // 생성자 정의
	{
		setBackground(Color.YELLOW); //배경색으로 노란색으로 설정
		label = new JLabel("여기가 그래픽 객체를 그리는 곳입니다."); //라벨 생성
		add(label); //라벨 배치
		setLayout(null);           //마우스 이벤트 처리시 동작해야 하므로 Layout은 null값이다. 
		label.setSize(300,20);    //label의 기본 크기를 지정
		label.setLocation(250,0); //label의 기본 좌표값을 지정
		isSelected = 0;
		isLoad = 0;
		isSave = 0;
		isDragged = false;
		isFigure = false;
		isSizeCon = false;
		isSizeCon2 = false;

		pressed = null;
		clicked = null;
		
		stmp = null;
		stmp2 = null;
		
		addMouseListener(new MyMouseListener());
		//마우스 리스너를 등록합니다. (Pressed, Released, clicked 이벤트를 위함)
		addMouseMotionListener(new MyMouseListener());
		//마우스 모션 리스너를 등록합니다. (드래그 이벤트를 위함)
	}
	
	class MyMouseListener extends MouseAdapter //필요없는 메서드 구현을 제외하기 위해서 adapter 상속
	{
		   @Override /* 마우스 누를 시*/
		   public void mousePressed(MouseEvent e) { 
		        start = e.getPoint(); //마우스가 눌려졌을 때 현재 위치를 start 변수에 저장
		        pressed = e.getPoint();
		        System.out.println("mousePressed : " + e.getX() + "," + e.getY()); //그냥 테스트 출력
		        
		        if(shapes.size()==0) //처음 그림 그릴 때 
		        {
		        	isFigure = true;
		        	PanelB.isLabel = 1;
		        }
		        
		         if(isSelected == 1 && isDragged == false && isSizeCon == false &&PanelB.isCopy==0 &&PanelB.isDelete==0) 
		        	 //도형 선택이 되었을 때 누를 때 -> 이미 선택되었기 때문에 유효성검사 할 필요X
		                {
		            	  for(int i = 0; i < sel.size(); i++) //shapes 리스트 크기만큼 loop
	    		           {
		            		  if(sel.get(i) instanceof Rectangle || sel.get(i) instanceof Oval) //도형 이동을 위함 
			            		{
				                        isDragged = true;
				                        isFigure = false;
				                        offX[i] = (int)(pressed.getX() - sel.get(i).x); //마우스에 상대 좌표 저장
				                        offY[i] = (int)(pressed.getY() - sel.get(i).y); 
				                        offMX[i] = (int)(sel.get(i).max_x - pressed.getX());
				                        offMY[i] = (int)(sel.get(i).max_y - pressed.getY());
				                        PanelB.isLabel = 0;
			            		   }
		            		        else if(sel.get(i) instanceof Line)//클릭된 도형이 직선일 때 , 도형 이동을 위함 
		            		           { 
				                          int sx = ((Line) sel.get(i)).getSx(); //다운캐스팅, Line형으로 형변환 해주어서 호출
									      int ex =((Line) sel.get(i)).getEx();  //직선만 이렇게 해주어야 한다 -> 이유 : 직선은 시작점과 끝점의 좌표가 있기 때문 
									      int sy = ((Line) sel.get(i)).getSy();
									      int ey = ((Line) sel.get(i)).getEy();
				                          isDragged = true;
				                          isFigure = false;
				                          offX[i] = (int)(pressed.getX() - sx); //마우스의 상대 좌표 저장
				                          offY[i] = (int)(pressed.getY() - sy); //y좌표
				                          offMX[i] = (int)(ex - pressed.getX());
				                          offMY[i] = (int)(ey - pressed.getY());
				                          PanelB.isLabel = 0;
		            	              }
		            		      if(sel.get(i).x-2 <=pressed.x && sel.get(i).x+2 >= pressed.x //도형 크기를 변경하기 위함 
		                          && sel.get(i).y-2<=pressed.y && sel.get(i).y+2 >=pressed.y &&
		                          PanelB.isCopy== 0 && PanelB.isDelete == 0)
		                         {
		            	            isSizeCon = true;
		            	            isSizeCon2 = false;
		            	            stmp = sel.get(i);
	                                isDragged = false;
		                         }
		            		      else if(sel.get(i).max_x-2 <=pressed.x && sel.get(i).max_x+2 >= pressed.x //도형 크기를 변경하기 위함 
		                          && sel.get(i).max_y-2<=pressed.y && sel.get(i).max_y+2 >=pressed.y &&
		                          PanelB.isCopy== 0 && PanelB.isDelete == 0)
		            		      {
		            		    	  isSizeCon = false;
		            		    	  isSizeCon2 = true;
		            		    	  stmp2 = sel.get(i);
		            		    	  isDragged = false;
		            		      }
		                       }
		                    }
		                    repaint();
		              }
		        
		     @Override /*마우스 클릭 시 */
		     public void mouseClicked(MouseEvent e) {
		            super.mouseClicked(e);
		            clicked = e.getPoint();
		            
		            if(PanelB.isCopy==0&&PanelB.isDelete==0) //도형 복사 또는 삭제가 아닐 경우에
		            {   
		               isSelected = 0;
		               for(int i = 0; i < sel.size(); i++)
		               {
		                   sel.clear(); // 클릭시마다 선택된 좌표들은 달라지므로
		               }
		            }
		            
		           if(isDragged == false && isSizeCon == false && isSizeCon2 == false) {  
		        	//도형 이동 또는 도형 크기 조절 말고 도형 선택시에만 작동
		            clicked = e.getPoint(); //현재 마우스 위치를 clicked 변수에 저장
		            System.out.println("mouseClicked : " + e.getX() + "," + e.getY()); //테스트 출력
		            for(int i = 0; i < shapes.size(); i++) //ArrayList의 크기만큼 반복
		            {
		            	  System.out.println(shapes.get(i).max_x);
		            	  System.out.println(shapes.get(i).y);
		            	  System.out.println(shapes.get(i).max_y);
		            	  System.out.println(clicked.y);
		                  if(shapes.get(i).x <=clicked.x && shapes.get(i).max_x >= clicked.x
		     		         && shapes.get(i).max_y >= clicked.y && shapes.get(i).y <=clicked.y) {
		                	  //클릭한 좌표가 도형 안에 있으면
		                          isDragged = false;
		                          isSelected = 1;
		                          sel.add(shapes.get(i));//sel 리스트에 더해주세요. (배열 원소 추가)
		                	   }            
		                    /*else { // 클릭한 도형이 직선일 때
		                         Shape n = null; 
		                         n = (Line)shapes.get(i); // 업캐스팅
		                         int a = ((Line)n).getEx() - ((Line)n).getSx(); // 직선으로 다운캐스팅
		                         int b = ((Line)n).getEy() - ((Line)n).getSy();
		                         int c = (((Line)n).getSx() * ((Line)n).getSy()) - (((Line)n).getEx() * ((Line)n).getEy());
		                         double Dist = Math.abs(a*clicked.x + b*clicked.y + c) / Math.sqrt(a*a + b*b);
		                         if(Dist > 10) { // 점과 직선 사이의 거리가 10보다 크거나 작으면
		                        	 System.out.println(Dist);
		                             System.out.println("직선 위가 아님");
		                             isFigure = false;
		                        }
		                        else { // 점과 직선 사이의 거리가 0이면
		                             isDragged = false;
		                             isSelected = 1;
		                             sel.add(shapes.get(i));//sel 리스트에 더해주세요. (배열 원소 추가)
		                             System.out.println(Dist);
		                             System.out.println("직선 위임");
		                       }
		                    }*/
		                 }
		                  repaint(); //반복문을 빠져나와서 호출 -> PaintCommponent 메소드 자동 호출
		             }
		         }    

		@Override
		/*마우스 드래그시*/
		public void mouseDragged(MouseEvent e) { 
			super.mouseDragged(e); //이전의 잔상을 지우기 위해 mouseDragged 함수 호출
			if(isDragged == false && isSelected == 0 && isSizeCon == false) {
				    isFigure = true;
				    end = e.getPoint();    //마우스가 드래그되어지는 순간순간을 end 변수에 할당하고
				    repaint(); //그림 그리기 ->PaintComponent 호출
			}
			if(isSelected == 1 && isDragged == true && isSizeCon == false) //도형 이동시에 
			{
			  for(int i = 0; i < sel.size(); i++) 
			  {
				  	if(sel.get(i) instanceof Rectangle || sel.get(i) instanceof Oval) {
				  		sel.get(i).x = e.getX() - offX[i]; //마우스가 드래그 되어질때마다 도형의 좌표를 바꾸는 거
				  		sel.get(i).y = e.getY() - offY[i];
				  		sel.get(i).max_x = e.getX() + offMX[i];
				  		sel.get(i).max_y = e.getY() + offMY[i];
				  		repaint();
				  	}
			        else //sel arrayList의 원소가 직선일 때
			       {
			        	((Line)sel.get(i)).setSx(e.getX() - offX[i]);
			        	((Line)sel.get(i)).setSy(e.getY() - offY[i]);
			        	((Line)sel.get(i)).setEx(e.getX() + offMX[i]);
			        	((Line)sel.get(i)).setEy(e.getY() + offMY[i]);
			        	/*삼항연산자를 통해 시작점 끝점 비교하여 MAX MIN 값에 대입*/
			        	((Line)sel.get(i)).max_x =
			            (e.getX() - offX[i]) > (e.getX() + offMX[i]) ? (e.getX() - offX[i]) : (e.getX() + offMX[i]);
			            ((Line)sel.get(i)).max_y =
					    (e.getY() - offY[i]) > (e.getY() + offMY[i]) ? (e.getY() - offY[i]) : (e.getY() + offMY[i]);
					    ((Line)sel.get(i)).x =
					    (e.getX() - offX[i]) < (e.getX() + offMX[i]) ? (e.getX() - offX[i]) : (e.getX() + offMX[i]);
					    ((Line)sel.get(i)).y =
					    (e.getY() - offY[i]) < (e.getY() + offMY[i]) ? (e.getY() - offY[i]) : (e.getY() + offMY[i]);
			        	repaint();
			       }  	
			    }
			}
			else if(isSelected == 1 && isSizeCon == true && isSizeCon2 == false && isDragged == false) //좌측상단
			{
				  stmp.x = e.getX();
				  stmp.y = e.getY();
				  repaint();  //repaint()함수를 계속해서 호출
			}
			else if(isSelected == 1 && isSizeCon2 == true && isSizeCon == false && isDragged == false)//우측하단
			{
				stmp2.max_x = e.getX();
				stmp2.max_y = e.getY();
				repaint();
			}
		}

		@Override /*마우스 뗄 때*/
		public void mouseReleased(MouseEvent e) { 
			super.mouseReleased(e);  //이전의 잔상을 지우기 위해 호출
	         
			if(isDragged == true  && isSizeCon == false) //도형 이동할 때 
			{
				System.out.println("뗀다.");
				isSelected = 0;
				isFigure = false;
				isDragged = false;
				for(int i = 0; i < offX.length; i++) 
			    //어차피 offX의 길이는 sel에 저장된 객체 배열의 크기와 같으므로
				{
				   offX[i] = 0;
				   offY[i] = 0;
				   offMX[i] = 0;
				   offMY[i] = 0;
			    }
			}
			else if(isSizeCon == true && stmp!= null && isDragged == false)
			{
				System.out.println("sizecon 뗀다.");
				isSelected = 0;
				stmp = null;
				isFigure = false;
				isSizeCon = false;
			}
			else if(isSizeCon2 == true && stmp2!= null && isDragged == false)
			{
				System.out.println("sizecon2 뗀다.");
				isSelected = 0;
				stmp2 = null;
				isFigure = false;
				isSizeCon2 = false;
			}
			else if(PanelB.isLabel == 1 && isDragged == false && isSizeCon == false && isFigure == true) //도형 그리고 나서 
			{
				 end = e.getPoint();
				 int min_x = Math.min(start.x, end.x);     //시작점과 끝점 중 작은 x값을 반환
		         int min_y = Math.min(start.y, end.y);     //시작점과 끝점 중 작은 y값을 반환
		         int width = Math.abs(start.x - end.x);    //시작점과 끝점의 x좌표 차이를 절댓값으로 반환
		         int height = Math.abs(start.y - end.y);   //시작점과 끝점의 y좌표 차이를 절댓값으로 반환
		         int max_y = Math.max(start.y, end.y);
		         int max_x = Math.max(start.x, end.x);
		         
				 Shape s = null; // Shape 클래스의 ref 변수를 null 값으로 초기화
		         if(label.getText().equals("사각"))       //라벨의 텍스트가 사각이라면
		         {
		               s = new Rectangle(min_x,min_y,width,height,max_x,max_y); //Rectangle 생성자 호출
		               shapes.add(s); //shapes ArrayList에 업캐스팅한 객체 할당
		         }
		         else if(label.getText().equals("타원"))  //라벨의 텍스트가 타원이라면
		         {
		                s = new Oval(min_x,min_y,width,height,max_x, max_y);      //Oval 클래스의 생성자 호출
		                 shapes.add(s);
		         }
		         else if(label.getText().equals("직선"))  
		         {
		            s = new Line(min_x, min_y, start.x, start.y, end.x, end.y, max_x, max_y, width, height); //직선 클래스의 생성자 호출
		            shapes.add(s); 
		         }
		      }
			isFigure = false;
			isSelected = 0;
			isDragged = false;
	     }
	}

	@Override
	public void paintComponent(Graphics g) { //paintCommponent 메소드 재정의 
		super.paintComponent(g); 
 		g.setColor(Color.BLUE);   //그래픽 선의 색을 파란색으로 할게요
 		
		/*여기서부터 도형 선택시 구현한 부분!!*/
		if(isSelected == 1 && sel.size()>0) {  //도형선택이 되었고 sel 리스트에 하나라도 있는 경우 
			  if(PanelB.isDelete!=1 && PanelB.isCopy!=1) {
				    for(int i = 0; i < sel.size(); i++) {
				    	 if(sel.get(i) instanceof Rectangle || sel.get(i) instanceof Oval) {
				    		 g.drawRect((sel.get(i).x)-2, (sel.get(i).y)-2, 4, 4);  //사각형 좌측 상단 그리기
				    		 g.drawRect((sel.get(i).max_x)-2, (sel.get(i).max_y)-2, 4, 4); //우측 하단 사각형
				    		 isFigure = false;
				    		 PanelB.isLabel = 0;
				          }
				    	 else
				    	 {
				    		 Shape n = null; //임의의 Shape 객체 n
				    		 n = (Line)sel.get(i); //업캐스팅을 먼저 해준다 -> 다운캐스팅을 진행하기 위함
						     int sx = ((Line) n).getSx(); //다운캐스팅, Line형으로 형변환 해주어서 호출
						     int ex =((Line) n).getEx();  //직선은 시작점과 끝점의 좌표가 있기 때문 
						     int sy = ((Line) n).getSy();
							 int ey = ((Line) n).getEy();
				    		 g.drawRect(sx, sy, 4, 4);
				    		 g.drawRect(ex, ey, 4, 4);
				    		 isFigure = false;
				    		 PanelB.isLabel = 0;
				    	 }
				       } 
				    }
			        System.out.println("delete : " + PanelB.isDelete);
			        System.out.println("copy : " + PanelB.isCopy);
			        if(PanelB.isCopy == 1)  //복사
			        {
			    	   for(int i = 0; i<sel.size(); i++) {
					    int new_x = sel.get(i).x+10;
						int new_y = sel.get(i).y+10;
						int nmax_x = sel.get(i).max_x+10;
						int nmax_y = sel.get(i).max_y+10;
						
						if(sel.get(i) instanceof Rectangle)
						{
							Shape s = null;
						    g.drawRect(new_x, new_y, sel.get(i).width, sel.get(i).height);
					        s = new Rectangle(new_x,new_y,sel.get(i).width,sel.get(i).height,nmax_x,nmax_y);
					        System.out.println("1");
					        System.out.println(shapes.size());
						    shapes.add(s);
						}
						else if(sel.get(i) instanceof Oval)
						{
							Shape s = null;
							g.drawOval(new_x, new_y, sel.get(i).width, sel.get(i).height);
							s = new Oval(new_x,new_y,sel.get(i).width,sel.get(i).height,nmax_x, nmax_y); 
							shapes.add(s);
						}
						else if(sel.get(i) instanceof Line)
						{
	                        Shape n = null; //임의의 Shape 객체 n
							Shape s = null; 
							n = (Line)sel.get(i); //업캐스팅을 먼저 해준다 -> 다운캐스팅을 진행하기 위함
							int new_sx = ((Line) n).getSx() + 10; //다운캐스팅, Line형으로 형변환 해주어서 호출
							int new_ex =((Line) n).getEx() + 10;  //직선만 이렇게 해주어야 한다 -> 이유 : 직선은 시작점과 끝점의 좌표가 있기 때문 
							int new_sy = ((Line) n).getSy();
							int new_ey = ((Line) n).getEy();
							g.drawLine(new_sx,new_sy,new_ex,new_ey);
							s = new Line(new_x, new_y, new_sx, new_sy, new_ex, new_ey, nmax_x, nmax_y, sel.get(i).width, sel.get(i).height);
							shapes.add(s);
						}
			         }
			    	  PanelB.isCopy = 0; isSelected = 0;
			    }
			    if(PanelB.isDelete == 1)
				{
			    	      Shape target = null;
						  while(sel.size()!=0) {
							     target = null;
							     for(int i = 0; i <sel.size(); i++) {
								 target = sel.get(i);
								 sel.remove(i);
								 for(int j = 0; j <shapes.size(); j++)
								 {
									 if(target.equals(shapes.get(j)))
									 {
										shapes.remove(j);
										System.out.println("yes");
									 }
						          }
							     }
						  }				
					PanelB.isDelete = 0; target = null; isSelected = 0;
				}
		}
		
		if(isSave == 1) //저장 버튼을 눌렀을 때
		{
			System.out.println("Good");
//			for(int i = 0; i <shapes.size(); i++)
//			{
//				shapes.clear();
//			}
			for(int i = 0; i <sel.size(); i++)
			{
				sel.clear();
			}
			isSave = 0;
		}

		if(isLoad == 1) //불러오기 버튼을 눌렀을 때
		{
			System.out.println("loaded");
			isLoad = 0;
		}

		if(start !=null && end!=null && PanelB.isLabel==1 && isDragged == false && isSizeCon==false && isFigure == true) { //도형 그리는 함수 
	      super.paintComponent(g); 
	      int minx = Math.min(start.x, end.x); //시작점과 끝점 중 작은 x값을 반환
	      int miny = Math.min(start.y, end.y); //시작점과 끝점 중 작은 y값을 반환
	      int width = Math.abs(start.x - end.x); //시작점과 끝점의 x좌표 차이를 절댓값으로 반환
	      int height = Math.abs(start.y - end.y);//시작점과 끝점의 y좌표 차이를 절댓값으로 반환

	      if(label.getText().equals("타원")) //라벨에 쓰여진 텍스트가 타원이라면
	       {
	          g.drawOval(minx,miny,width,height); //타원을 그리는 함수 호출
	       }
	      else if(label.getText().equals("사각")) //사각이라면 
	      {
	         g.drawRect(minx,miny,width,height); //사각형 그리는 함수 호출
	      }
	      else if(label.getText().equals("직선"))// 그 외 (직선이라면)
	      {
	          g.drawLine(start.x, start.y, end.x, end.y);   //직선을 그리는 함수 호출
	      }
	  }
		//지금까지 그린 그림들을 지우지 않고 모두 보여주는 디폴트 (기본) 
      for(int i = 0; i < shapes.size(); i++) //ArrayList의 크기만큼 반복
	    {
		   shapes.get(i).draw(g); //요소들을 뽑아내어 각 draw()함수 호출 -> 동적 바인딩
		   System.out.println("repaint() 디폴트가 불림");
	    }
	} //component 메소드가 끝나는 시점
	
	public void SaveFigures()  //도형 저장하는 함수
	{
		isSave = 1;
		ObjectOutputStream out = null;
		try
		{
		    out = new ObjectOutputStream(new FileOutputStream("myframe.txt"));
		    out.writeObject(shapes);
		    out.close();
		}catch(IOException e)
		{
			e.printStackTrace();
		}
		repaint();
	}
	
	public void loadFigures() //도형 불러오는 함수
	{
		ObjectInputStream in = null;
		try 
		{
			in = new ObjectInputStream(new FileInputStream("myframe.txt"));
			shapes = (ArrayList<Shape>)in.readObject();
			in.close();
		}catch(IOException e)
		{
			e.printStackTrace();
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		isLoad = 1;
		repaint();
	}
}//패널 A 끝!

/*버튼들이 일렬로 위치한 패널 B*/
class PanelB extends JPanel{      //JPanel을 상속받는다.
	 JButton button1, button2, button3, btn4, btn5, btn6, btn7;
	 PanelA pa; //panelA 객체를 멤버 변수로 가지고 있기 때문에 참조 가능 
	 static int isCopy;  //static 변수로 선언 -> 패널A에서 접근 가능
	 static int isDelete;
	 static int isLabel;
	 
    public PanelB(PanelA pa) //생성자
    {
    	this.pa = pa;
    	isCopy = 0;
    	isDelete = 0;
    	isLabel = 0;
      	setBackground(Color.BLUE);  //배경색을 파란색으로 설정
   	    setLayout(new GridLayout(7,1,5,5)); //레이아웃을 7행 1열 좌우 상하 간격을 5로 설정
   	    button1 = new JButton("사각");
     	button2 = new JButton("직선"); 
   	    button3 = new JButton("타원"); 
   	    btn4 = new JButton("복사");
   	    btn5 = new JButton("삭제");
   	    btn6 = new JButton("저장");
   	    btn7 = new JButton("불러오기");
   	    add(button1); 
   	    add(button2);  
   	    add(button3); 
   	    add(btn4);
   	    add(btn5);
   	    add(btn6);
   	    add(btn7);
   	
   	    ActionListener listener = new MyActionListener(); //ActionListener 객체 생성
   	    button1.addActionListener(listener); //버튼 1에 리스너 등록 
   	    button2.addActionListener(listener); //버튼 2에 리스너 등록
   	    button3.addActionListener(listener); //버튼 3에 리스너 등록
   	    btn4.addActionListener(listener); 
   	    btn5.addActionListener(listener);
   	    btn6.addActionListener(listener);
   	    btn7.addActionListener(listener); 
    }
    
    public class MyActionListener implements ActionListener{ //ActionListener 인터페이스를 상속

		@Override 
		public void actionPerformed(ActionEvent e) { //actionPerformed 재구현
			pa.label.setText(e.getActionCommand()); //라벨에 버튼 명령어를 문자열로 등록한다.
			JButton b = (JButton)e.getSource();
			
			if(b.getText().equals("사각") || b.getText().equals("타원") || b.getText().equals("직선"))
			{
				System.out.println("라벨버튼클릭");
				isLabel = 1;
			}
			else if(b.getText().equals("복사"))
			{
				System.out.println("복사버튼클릭");
				isCopy = 1;
			}
			else if(b.getText().equals("삭제"))
			{
				isDelete = 1;
				System.out.println("삭제버튼클릭");
			}
			else if(b.getText().equals("저장"))
			{
				pa.SaveFigures();
				pa.label.setText("저장되었습니다.");
			}
			else if(b.getText().equals("불러오기"))
			{
				pa.loadFigures();
				pa.label.setText("성공적으로 불러왔습니다.");
			}
		}
    }
    
} //패널 B 클래스 여기까지 

 class PanelC extends JPanel{ //Jpanel을 상속 받습니다.
	
	public PanelC(PanelA pa) { //Panel C 생성자에서 pa 객체를 파라미터로 받습니다. 
	    add(new PanelB(pa)); //패널 C는 패널 B를 포함합니다.(자식과 부모의 관계)
	}
}

interface LineXY  //직선의 시작점과 끝점을 구분하기 위한 인터페이스 선언
{                 //직선 클래스에만 필요하기 때문에 인터페이스로 구현 -> 재정의
	int getSx();
	int getSy();
	int getEx();
    int getEy();
    void setSx(int n);
    void setSy(int n);
    void setEx(int n);
    void setEy(int n);
}

abstract class Shape implements Serializable{ //추상클래스 선언 - 부모 클래스이며 추상 메소드를 포함합니다.
	
	int x;
	int y;
	int max_x;
	int max_y;
	int width;
	int height;
	
	public Shape(int x, int y, int max_x, int max_y, int width, int height) //생성자
	{
		this.x = x; //멤버 변수들을 초기화합니다.
		this.y = y;
		this.max_y = max_y;
		this.max_x = max_x;
		this.height = height;
		this.width = width;
	}
	
	abstract public void draw(Graphics g); //추상메소드로써 매개변수로 그래픽 변수 g를 전달받고 원형만 선언합니다.
	
	   @Override
	   public boolean equals(Object obj) {
	      if (this == obj)
	         return true;
	      if (obj == null)
	         return false;
	      if (getClass() != obj.getClass())
	         return false;
	      Shape other = (Shape) obj;
	      return height == other.height && width == other.width && x == other.x && y == other.y;
	   }
}
class Rectangle extends Shape //도형 - 사각형 클래스
{	
	public Rectangle(int x, int y, int width, int height, int max_x, int max_y) //생성자
	{
		super(x,y, max_x, max_y, width, height); //부모 클래스를 호출하여 x,y값을 초기화 해줍니다.
	}

	@Override
	public void draw(Graphics g) { //draw 메소드를 부모 클래스로부터 오버라이딩 
		g.drawRect(x, y, width, height); //사각형 그리는 그래픽 함수 호출
	}
	
}

class Oval extends Shape // 도형 - 타원 클래스
{	
	public Oval(int x, int y, int width, int height, int max_x, int max_y) //생성자
	{
		super(x,y,max_x,max_y, width, height); //부모 클래스를 호출하여 x,y 변수를 전달받은 값으로 초기화
	}

	@Override
	public void draw(Graphics g) { //draw 메소드 - 오버라이딩하여 타원을 그리는 함수를 호출합니다.
		g.drawOval(x, y, width, height); //각각 x, y, 폭, 높이값
	}
}

class Line extends Shape implements LineXY // 도형 - 직선 클래스
{	
	private int ex;  //끝나는 시점의 x
	private int ey;  //끝나는 시점의 y
	private int sx;  //시작시점의 x
	private int sy;  //시작시점의 y
	
	public Line(int min_x, int min_y, int sx, int sy, int ex, int ey, int max_x, int max_y, int width, int height) //생성자
	{
		super(min_x, min_y, max_x, max_y, width, height); //super 클래스인 Shape 클래스를 통해 시작점의 x,y값 초기화
		this.ex = ex; //전달받은 인자들을 통해 멤버 변수 초기화
		this.ey = ey;
		this.sx = sx;
		this.sy = sy;
	}

	public int getEx() {  //시작점과 끝점을 리턴하는 getter 메소드 4개 -> 직선의 시작점 끝점 알기 위함
		return ex;
	}

	public int getEy() {
		return ey;
	}

	public int getSx() {
		return sx;
	}

	public int getSy() {
		return sy;
	}
	
	public void setEx(int n) //setter 함수 4개 -> 직선 이동시 x,y좌표 설정 위함
	{
		this.ex = n;
	}
	
	public void setEy(int n)
	{
		this.ey = n;
	}
	
	public void setSx(int n)
	{
		this.sx = n;
	}
	
	public void setSy(int n)
	{
		this.sy = n;
	}

	@Override
	public void draw(Graphics g) { //draw 함수 오버라이딩 - 4개의 변수를 인자로 받아 직선을 그립니다.
		g.drawLine(sx, sy, ex, ey);
	}
 }