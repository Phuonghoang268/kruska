package com.kruskal;

import javafx.animation.AnimationTimer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.io.*;
import java.util.ArrayList;

/**
 * ----------------- @author nguyenvanquan7826 -----------------
 * ---------------nguyenvanquan7826.wordpress.com --------------
 */
class MyDraw extends JPanel implements MouseListener, MouseMotionListener, ActionListener {
	private static final long serialVersionUID = 1L;
	public static int change=1;
	public MyData data = new MyData();
	private ArrayList<Integer> arrPointResultStep = new ArrayList<Integer>();
	private int len[];
	private int a[][];
	private int p[];
	private int infinity;
	private int x = 0, y = 0, r = 15, r2 = 2 * r; // ban kinh, duong kinh
	private int indexPointBeginLine, indexPointEndLine, indexTemp;
	private Point pointBeginLine;
	private Point point;
	boolean checkDrawLine = false, isFindPoint = true;
	private int draw = 0; // draw point or line or move
	private Color colorBackGround = Color.lightGray, colorCost = Color.white,
			colorIndex = Color.black, colorDraw = Color.white,
			colorStep = Color.getHSBColor(50, 50, 50),
			colorStepMin = Color.blue, colorResult = Color.red;
	private int sizeLine = 1, sizeLineResult = 2;
	private boolean drawResult = false;
	private boolean drawStep = false;
	public static boolean reDraw = false;
	public boolean check=false;
	private boolean resetGraph = false;
	private boolean typeMap = false;
	private boolean checkedPointMin[];
	private int indexBeginPoint, indexEndPoint;
	private int drawWith, drawHeight;
	public Timer timer=new Timer(1000, this);

	/**
	 * new
	 */

	/**
	 * để chuyển màn hình trong 1s
	 */

	public void actionPerformed(ActionEvent ev){
		if(ev.getSource()==timer){
			repaint();// this will call at every 1 second
		}

	}
	public MyDraw() {
		timer.start();
		init();
		addMouseMotionListener(this);
		addMouseListener(this);
	}

	/**
	 *new.
	 */

	/**
	 *chỗ nãy hàm này kế thừa từ Jpanel, dùng để update cái màn hình nhá
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(colorBackGround);
		Graphics2D g2d = (Graphics2D) g;
		int changer=0;
		// draw line
		reDraw(g2d, false,-1);

		// redraw the begin graph
		if (reDraw) {
			while(data.getArrMyLine().get(change).colo==Color.blue
					&&change<data.getArrMyLine().size()-1) change++;
			double min=data.getArrMyLine().get(change).getCost();

			for(int i=change;i<data.getArrMyLine().size();i++){
				if(min>data.getArrMyLine().get(i).getCost()
						&&data.getArrMyLine().get(i).colo!=Color.blue){
					min=data.getArrMyLine().get(i).getCost();
				}
			}
			if(data.getArrMyLine().get(change).colo!=Color.blue
					&&data.getArrMyLine().get(change).getCost()==min){
				data.getArrMyLine().get(change).colo=Color.blue;
				data.getArrMyPoint().get(data.getArrMyLine().get(change).getIndexPointA()).colo=Color.red;
				data.getArrMyPoint().get(data.getArrMyLine().get(change).getIndexPointB()).colo=Color.red;
			}
			for(int i=change+1;i<data.getArrMyLine().size();i++){
				if(data.getArrMyLine().get(i).getCost()==min){
					change=i;
					break;

				}
			}

			data.getArrMyLine().get(change).colo=Color.blue;//chuyen canh
			data.getArrMyPoint().get(data.getArrMyLine().get(change).getIndexPointA()).colo=Color.red;
			data.getArrMyPoint().get(data.getArrMyLine().get(change).getIndexPointB()).colo=Color.red;
			reDraw(g2d, false,change);

			int i;
			for(i=change+1;i<data.getArrMyLine().size();i++){
				if(data.getArrMyLine().get(i).getCost()==min){
					break;
				}
			}
			if(i==data.getArrMyLine().size()) change=1;
		}

		// reset graph to graph space
		if (resetGraph) {
			resetGraph(g2d);
			init();
			resetGraph = false;
		}
	}



	@Override
	public void mouseClicked(MouseEvent e) { // click
		x = e.getX();
		y = e.getY();
		if (draw == 1) { // draw point
			Ellipse2D.Float el = new Ellipse2D.Float(x - r, y - r, r2, r2);
			MyPoint mp = new MyPoint(el);
			data.getArrMyPoint().add(mp);
			repaint();
		}
		// mouse right
		if (e.getButton() == MouseEvent.BUTTON3) {
			System.out.println("Right Clicked");
			isRightClick = true;
			pointRight = e.getPoint();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) { // nhan vao
		pointBeginLine = e.getPoint();
		point = e.getPoint();
		e.getPoint();
		e.getPoint();
		data.getArrMyPoint().get(indexTemp).getEl().x = e.getX() - r;
		data.getArrMyPoint().get(indexTemp).getEl().y = e.getY() - r;
	}

	@Override
	public void mouseReleased(MouseEvent e) { // nha ra
		boolean drawAgaine = false;
		if (checkDrawLine) {
			indexPointEndLine = indexPointContain(new Point(e.getX(), e.getY()));
			if (indexPointEndLine > 0) {
				isFindPoint = false;
			}

			for (int i = 1; i < data.getArrMyLine().size(); i++) {
				MyLine line = data.getArrMyLine().get(i);
				if (typeMap) { // directed
					// draw again <=> change cost
					if (line.getIndexPointA() == indexPointBeginLine
							&& line.getIndexPointB() == indexPointEndLine) {
						drawAgaine = true;
						break;
					} // draw line reverse <=> not change cost
					else if (line.getIndexPointA() == indexPointEndLine
							&& line.getIndexPointB() == indexPointBeginLine) {
						addLineToList(indexPointBeginLine, indexPointEndLine,
								line.getCost());
						drawAgaine = true;
						break;
					}
				} else { // undirected
					// draw again <=> change cost
					if ((line.getIndexPointA() == indexPointBeginLine && line
							.getIndexPointB() == indexPointEndLine)
							|| (line.getIndexPointA() == indexPointEndLine && line
									.getIndexPointB() == indexPointBeginLine)) {
						drawAgaine = true;
						break;
					}
				}
			}
			if (!drawAgaine) {
				int cost = showDialogCost(indexPointBeginLine,
						indexPointEndLine);
				addLineToList(indexPointBeginLine, indexPointEndLine, cost);
			}
			checkDrawLine = false;
		}
		data.getArrMyLine()
				.get(indexTemp)
				.setIndexPointA(
						data.getArrMyLine().get(indexTemp).getIndexPointB());
		updateLine();
		repaint();// xoa doan thang ko dung
		isFindPoint = true; // cho phep tim diem dau
	}

	@Override
	public void mouseEntered(MouseEvent e) { // vao frame
		// System.out.println("Entered");
	}

	@Override
	public void mouseExited(MouseEvent e) { // ra khoi frame
		// System.out.println("Exited");
	}

	@Override
	public void mouseDragged(MouseEvent e) { // keo chuot
		if (isFindPoint) { // find point is true
			indexPointBeginLine = indexPointContain(pointBeginLine);
			if (indexPointBeginLine > 0) {
				isFindPoint = false;
			}
		}
		// drawing line or point
		if (draw == 2 || draw == 1 || indexPointBeginLine >= 0) {
			int dx = e.getX() - point.x;
			int dy = e.getY() - point.y;
			// move point
			if ((draw == 1 || draw == 3) && indexPointBeginLine > 0) {
				Ellipse2D.Float el = data.getArrMyPoint()
						.get(indexPointBeginLine).getEl();

				el.x += dx;
				el.y += dy;
				data.getArrMyPoint().get(indexPointBeginLine).setEl(el);
			}
			// draw line
			if (draw == 2 && indexPointBeginLine >= 0) {
				checkDrawLine = true;
				data.getArrMyLine().get(indexTemp)
						.setIndexPointA(indexPointBeginLine);
				Ellipse2D.Float el = data.getArrMyPoint().get(indexTemp)
						.getEl();
				el.x += dx;
				el.y += dy;
				data.getArrMyPoint().get(indexTemp).setEl(el);
			}
			updateLine();
			repaint();
			point.x += dx;
			point.y += dy;
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	// find index Point in list Point
	protected int indexPointContain(Point point) {
		for (int i = 1; i < data.getArrMyPoint().size(); i++) {
			if (data.getArrMyPoint().get(i).getEl().getBounds2D()
					.contains(point)) {
				return i;
			}
		}
		return -1;
	}

	// show dialog input cost
	protected int showDialogCost(int indexPointBeginLine, int indexPointEndLine) {
		int cost = 0;
		if (indexPointEndLine > 0
				&& indexPointEndLine < data.getArrMyPoint().size()
				&& indexPointEndLine != indexPointBeginLine) {
			String c = null;
			boolean ok = false;
			while (!ok) {
				try {
					c = JOptionPane.showInputDialog(null, "Input Cost from "
							+ indexPointBeginLine + " to " + indexPointEndLine,
							"Dijstra Graphics", 1);
					cost = Integer.parseInt(c);
					if (cost > 0) {
						return cost;
					}
				} catch (NumberFormatException ex) { // input error number
				}
				// cancel
				if (c == null)
					break;
			}
		}
		return cost;
	}

	// Add line to list line
	protected void addLineToList(int indexPointBeginLine,
			int indexPointEndLine, int cost) {
		if (cost > 0) {
			MyLine ml = new MyLine(creatLine(
					data.getArrMyPoint().get(indexPointBeginLine).getP(), data
							.getArrMyPoint().get(indexPointEndLine).getP()),
					indexPointBeginLine, indexPointEndLine, cost);
			data.getArrMyLine().add(ml);
			repaint();
		}
	}


	public void init() {
		data.getArrMyLine().clear();
		data.getArrMyPoint().clear();
		MyPoint p0 = new MyPoint(new Ellipse2D.Float(50, 50, 50, 50));
		data.getArrMyPoint().add(p0);
		data.getArrMyLine().add(
				new MyLine(creatLine(p0.getP(), p0.getP()), 0, 0, -1));
	}

	private Line2D.Double creatLine(Point p1, Point p2) {
		Line2D.Double l = new Line2D.Double(p1.x, p1.y, p2.x, p2.y);
		return l;
	}

	private void updateLine() { // update location line after move point
		for (int i = 0; i < data.getArrMyLine().size(); i++) {
			data.getArrMyLine()
					.get(i)
					.setL(creatLine(
							data.getArrMyPoint()
									.get(data.getArrMyLine().get(i)
											.getIndexPointA()).getP(),
							data.getArrMyPoint()
									.get(data.getArrMyLine().get(i)
											.getIndexPointB()).getP()));
		}
	}

	public void resetGraph(Graphics2D g2d) {
		g2d.setColor(Color.pink);
		g2d.fillRect(0, 0, 1500, 600);
	}

	/**
	 *new.
	 */

	/**
	 * cái này là sẽ redraw cạnh thứ t. chuyển sang  màu xanh
	 */
	private void reDraw(Graphics2D g2d, boolean checkReDraw,int t) {
		resetGraph(g2d);
		for (int i = 0; i < data.getArrMyLine().size(); i++) {
			data.getArrMyLine().get(i).drawLine(g2d,
					data.getArrMyPoint().get(data.getArrMyLine().get(i).getIndexPointA()).getP(),
					data.getArrMyPoint().get(data.getArrMyLine().get(i).getIndexPointB()).getP(),
					colorCost, data.getArrMyLine().get(i).colo, sizeLine, typeMap);
		}

		// draw point
		for (int i = 1; i < data.getArrMyPoint().size(); i++) {
			data.getArrMyPoint().get(i).draw(g2d, i, data.getArrMyPoint().get(i).colo, colorIndex);
		}
	}

	public void drawResultStep(Graphics2D g2d) {

		/*String cost;
		// draw update cost
		for (int i = 0; i < arrPointResultStep.size(); i++) {
			if ((indexEndPoint != -1 && !checkedPointMin[indexEndPoint])
					|| indexEndPoint == -1) {
				for (int j = 1; j < data.getArrMyPoint().size(); j++) {
					cost = String.valueOf(len[j]);
					if (p[j] > 0 && a[p[j]][j] < infinity
							&& a[arrPointResultStep.get(i)][j] < infinity
							&& !checkedPointMin[j]) {

						MyLine ml = new MyLine(creatLine(data.getArrMyPoint()
								.get(p[j]).getP(), data.getArrMyPoint().get(j)
								.getP()), p[j], j,
								a[arrPointResultStep.get(i)][j]);

						ml.drawLine(
								g2d,
								data.getArrMyPoint()
										.get(arrPointResultStep.get(i)).getP(),
								data.getArrMyPoint().get(j).getP(), colorCost,
								colorStep, sizeLine, typeMap);

						data.getArrMyPoint()
								.get(j)
								.drawResult(g2d, j, colorStep, colorIndex,
										cost, colorStep);
					}
				}

			}

			// draw line min len
			if (p[arrPointResultStep.get(i)] > 0) {
				cost = String.valueOf(len[arrPointResultStep.get(i)]);
				MyLine ml = new MyLine(creatLine(
						data.getArrMyPoint().get(p[arrPointResultStep.get(i)])
								.getP(),
						data.getArrMyPoint().get(arrPointResultStep.get(i))
								.getP()), p[arrPointResultStep.get(i)], i,
						a[p[arrPointResultStep.get(i)]][arrPointResultStep
								.get(i)]);

				ml.drawLine(g2d,
						data.getArrMyPoint().get(p[arrPointResultStep.get(i)])
								.getP(),
						data.getArrMyPoint().get(arrPointResultStep.get(i))
								.getP(), colorStepMin, colorStepMin, sizeLine,
						typeMap);

			}

		}

		// draw point cost is min
		for (int i = 0; i < arrPointResultStep.size(); i++) {
			if (p[arrPointResultStep.get(i)] < infinity) {
				cost = String.valueOf(len[arrPointResultStep.get(i)]);
				data.getArrMyPoint()
						.get(arrPointResultStep.get(i))
						.drawResult(g2d, arrPointResultStep.get(i),
								colorStepMin, colorIndex, cost, colorStepMin);
			}
		}

		// draw result
		if (indexEndPoint != -1 && checkedPointMin[indexEndPoint]) {
			int i = indexEndPoint;
			while (i != indexBeginPoint) {
				cost = String.valueOf(len[i]);
				MyLine ml = new MyLine(creatLine(data.getArrMyPoint().get(i)
						.getP(), data.getArrMyPoint().get(p[i]).getP()), i,
						p[i], a[p[i]][i]);

				ml.drawLine(g2d, data.getArrMyPoint().get(p[i]).getP(), data
						.getArrMyPoint().get(i).getP(), colorCost, colorResult,
						sizeLineResult, typeMap);

				data.getArrMyPoint()
						.get(i)
						.drawResult(g2d, i, colorResult, colorIndex, cost,
								colorResult);

				i = p[i];
			}
			cost = String.valueOf(len[i]);
			data.getArrMyPoint()
					.get(indexBeginPoint)
					.drawResult(g2d, indexBeginPoint, colorResult, colorIndex,
							cost, colorResult);
		}*/
	}

	public void convertMatrixToData() {
		int anpha = 360 / a.length;
		int height = this.getHeight();
		int width = this.getWidth();
		Point pointI = new Point(height / 2, width / 2);

		init();

		for (int i = 0; i < a.length; i++) {
			int x = (int) (pointI.x + Math.cos(anpha * i));
			int y = (int) (pointI.y + Math.sin(anpha));
			data.getArrMyPoint().add(
					new MyPoint(new Ellipse2D.Float(x, y, r2, r2)));
		}
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a.length; j++) {
				if (a[i][j] > 0) {
					data.getArrMyLine()
							.add(new MyLine(creatLine(
									data.getArrMyPoint().get(i + 1).getP(),
									data.getArrMyPoint().get(j + 1).getP()),
									i + 1, j + 1, a[i][j]));
				}
			}
		}
	}

	public int getDrawWith() {
		return drawWith;
	}

	public void setDrawWith(int drawWith) {
		this.drawWith = drawWith;
	}

	public int getDrawHeight() {
		return drawHeight;
	}

	public void setDrawHeight(int drawHeight) {
		this.drawHeight = drawHeight;
	}

	protected boolean isRightClick = false;
	protected Point pointRight;

	public MyData getData() {
		return data;
	}

	public void setData(MyData data) {
		this.data = data;
	}

	public boolean isResetGraph() {
		return resetGraph;
	}

	public void setResetGraph(boolean resetGraph) {
		this.resetGraph = resetGraph;
	}

	public boolean isReDraw() {
		return reDraw;
	}

	public void setReDraw(boolean reDraw) {
		this.reDraw = reDraw;
	}

	public void setIndexBeginPoint(int indexBeginPoint) {
		this.indexBeginPoint = indexBeginPoint;
	}

	public int getIndexBeginPoint() {
		return indexBeginPoint;
	}

	public void setIndexEndPoint(int indexEndPoint) {
		this.indexEndPoint = indexEndPoint;
	}

	public int getIndexEndPoint() {
		return indexEndPoint;
	}

	public boolean[] getCheckedPointMin() {
		return checkedPointMin;
	}

	public void setCheckedPointMin(boolean[] checkedPointMin) {
		this.checkedPointMin = checkedPointMin;
	}

	public boolean isDrawStep() {
		return drawStep;
	}

	public void setDrawStep(boolean drawStep) {
		this.drawStep = drawStep;
	}

	public ArrayList<Integer> getArrPointResultStep() {
		return arrPointResultStep;
	}

	public void setArrPointResultStep(ArrayList<Integer> arrPointResultStep) {
		this.arrPointResultStep = arrPointResultStep;
	}

	public int[] getP() {
		return p;
	}

	public void setP(int[] p) {
		this.p = p;
	}

	public int[][] getA() {
		return a;
	}

	public void setA(int[][] a) {
		this.a = a;
	}

	public int getInfinity() {
		return infinity;
	}

	public void setInfinity(int infinity) {
		this.infinity = infinity;
	}

	public int[] getLen() {
		return len;
	}

	public void setLen(int[] len) {
		this.len = len;
	}

	public boolean isDrawResult() {
		return drawResult;
	}

	public void setDrawResult(boolean drawResult) {
		this.drawResult = drawResult;
	}

	public boolean isTypeMap() {
		return typeMap;
	}

	public void setTypeMap(boolean typeMap) {
		this.typeMap = typeMap;
	}

	public int getDraw() {
		return draw;
	}

	public void setDraw(int draw) {
		this.draw = draw;
	}

}