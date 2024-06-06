/*
  * Created on 12 may. 2018
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package fr.utc.gui;

import java.util.Stack;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

public class Traceur {

	private class Position {
		double posX, posY, angle;

		public Position(double posX, double posY, double angle) {
			this.posX = posX;
			this.posY = posY;
			this.angle = angle;
		}

		public double getPosX() {
			return posX;
		}

		public double getPosY() {
			return posY;
		}

		public double getAngle() {
			return angle;
		}

		public void setAngle(double angle) {
			this.angle = angle;
		}
	}

	private Color couleur = Color.BLACK;
	private double initx = 350, inity = 350; // position initiale
	private double posx = initx, posy = inity; // position courante
	private double angle = 90;
	private double teta;
	private boolean crayon = true;
	ObjectProperty<GraphLineParameter> line;
	private Stack<Position> stackPositions = new Stack<>();

	public Traceur() {
		setTeta();
		line = new SimpleObjectProperty<GraphLineParameter>();
	}


	public Color getCouleur() {
		return couleur;
	}



	public ObjectProperty<GraphLineParameter> lineProperty() {
		return line;
	}

	private void setTeta() {
		teta = Math.toRadians(angle);
	}

	private void addLine(double x1, double y1, double x2, double y2) {
		line.setValue(new GraphLineParameter(x1, y1, x2, y2, couleur));
	}

	public void avance(double r) {
		double a = posx + r * Math.cos(teta);
		double b = posy - r * Math.sin(teta);
		if (crayon) {
			addLine(posx, posy, a, b);
		}

		posx = a;
		posy = b;
	}

	public void td(double r) {
		angle = (angle - r) % 360;
		setTeta();
	}

	public void tg(double r) {
		angle = (angle + r) % 360;
		setTeta();
	}

	public void lc() {
		crayon = false;
	}

	public void bc() {
		crayon = true;
	}

	public void recule(double r) {
		double a = posx - r * Math.cos(teta);
		double b = posy + r * Math.sin(teta);
		if (crayon) {
			addLine(posx, posy, a, b);
		}
		posx = a;
		posy = b;
	}

	public void fpos(double x, double y) {
		posx = x;
		posy = y;
	}

	public void fcap(double cap){
		angle = cap % 360;
		setTeta();
	}

	public void fcc(double color){
		// Pour faciliter le travail, on fait le choix de
		// limiter le choix de couleurs à 5, selon un modulo
		switch(((int)color) % 5) {
			case 0:
				couleur = Color.BLACK;
				break;
			case 1:
				couleur = Color.GREEN;
				break;
			case 2:
				couleur = Color.YELLOW;
				break;
			case 3:
				couleur = Color.RED;
				break;
			case 4:
				couleur = Color.BLUE;
				break;
			default:
				couleur = Color.BLACK;
				break;
		}
	}

	public Integer store(){
		stackPositions.push(new Position(posx, posy, angle));
		return 0;
	}

	public Integer move(){
		if (!stackPositions.isEmpty()){
			Position p = stackPositions.pop();
			posx = p.getPosX();
			posy = p.getPosY();
			angle = p.getAngle();
			setTeta();
		}
		return 0;
	}

}
