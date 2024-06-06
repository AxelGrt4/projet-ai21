package fr.utc.model;

import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.xml.sax.ErrorHandler;

import fr.utc.gui.Traceur;
import fr.utc.parsing.LogoParser.AvContext;
import fr.utc.parsing.LogoParser.BcContext;
import fr.utc.parsing.LogoParser.FcapContext;
import fr.utc.parsing.LogoParser.FccContext;
import fr.utc.parsing.LogoParser.FloatContext;
import fr.utc.parsing.LogoParser.FposContext;
import fr.utc.parsing.LogoParser.LcContext;
import fr.utc.parsing.LogoParser.ReContext;
import fr.utc.parsing.LogoParser.TdContext;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

import java.util.*;

public class LogoTreeVisitor extends LogoStoppableTreeVisitor {
	fr.utc.gui.Traceur traceur;
	Log log;

	public LogoTreeVisitor() {
		traceur = new Traceur();
		log = new Log();
	}

	public StringProperty logProperty() {
		return log;
	}

	public fr.utc.gui.Traceur getTraceur() {
		return traceur;
	}

	/*
	 * Map des attributs associés à chaque noeud de l'arbre
	 * key = node, value = valeur de l'expression du node
	 */
	ParseTreeProperty<Double> atts = new ParseTreeProperty<Double>();
	ParseTreeProperty<Color> attsColor = new ParseTreeProperty<Color>();

	public void setValue(ParseTree node, double value) {
		atts.put(node, value);
	}

	public double getValue(ParseTree node) {
		Double value = atts.get(node);
		if (value == null) {
			throw new NullPointerException();
		}
		return value;
	}

	public Color getValueColor(ParseTree node) {
		Color value = attsColor.get(node);
		if (value == null) {
			throw new NullPointerException();
		}
		return value;
	}

	// Instructions de base

	@Override
	public Integer visitTd(TdContext ctx) {
		Pair<Integer, Double> bilan = evaluate(ctx.expr());
		if (bilan.a == 0) {
			traceur.td(bilan.b);
			log.defaultLog(ctx);
			log.appendLog("Tourne de", String.valueOf(bilan.b));
		} else {
			log.defaultLog(ctx);
			return 0;
		}
		return 0;
	}

	@Override
	public Integer visitAv(AvContext ctx) {
		Pair<Integer, Double> bilan = evaluate(ctx.expr());
		if (bilan.a == 0) {
			traceur.avance(bilan.b);
			// Différents type de log possibles. Voir classe Log
			log.defaultLog(ctx);
			log.appendLog("Avance de", String.valueOf(bilan.b));
		}
		return bilan.a;
	}

	@Override
	public Integer visitRe(ReContext ctx) {
		Pair<Integer, Double> bilan = evaluate(ctx.expr());
		if (bilan.a == 0) {
			traceur.recule(bilan.b);
			// Différents type de log possibles. Voir classe Log
			log.defaultLog(ctx);
			log.appendLog("Recule de", String.valueOf(bilan.b));
		}
		return bilan.a;
	}

	// Expressions

	@Override
	public Integer visitFloat(FloatContext ctx) {
		String floatText = ctx.FLOAT().getText();
		setValue(ctx, Double.valueOf(floatText));
		return 0;
	}

	@Override
	public Integer visitBc(BcContext ctx) {
		traceur.bc();
		log.appendLog("Le crayon se baisse et écris");
		return 0;
	}

	@Override
	public Integer visitLc(LcContext ctx) {
		traceur.lc();
		log.appendLog("Le crayon se lève et n'écris plus");
		return 0;
	}

	@Override
	public Integer visitFpos(FposContext ctx) {
		Pair<Integer, Double> exprX = evaluate(ctx.expr(0));
		Pair<Integer, Double> exprY = evaluate(ctx.expr(0));
		if (exprX.a == 0 && exprY.a == 0) {
			traceur.fpos(exprX.b, exprY.b);
			log.appendLog("La position est maintenant ", String.valueOf(exprX.b), ", ", String.valueOf(exprY.b));
		}

		return 0;
	}

	@Override
	public Integer visitFcap(FcapContext ctx) {
		Pair<Integer, Double> bilan = evaluate(ctx.expr());
		if (bilan.a == 0) {
			traceur.fcap(bilan.b);
			log.appendLog("le cap est maintenant de ", String.valueOf(bilan.b), " degrés");
		}
		return 0;
	}

	@Override
	public Integer visitFcc(FccContext ctx) {
		Pair<Integer, Double> bilan = evaluate(ctx.expr());
		if (bilan.a == 0) {
			traceur.fcc(bilan.b);
			log.appendLog("La couleur est maintenant ", String.valueOf(traceur.getCouleur()));
		}

		return 0;
	}

	/**
	 * Visite le noeud expression
	 * S'il n'y a pas d'erreur (la valeur de retour de la visite vaut 0)
	 * on récupère la valeur de l'expressions à partir de la map
	 * sinon
	 * on affecte une valeur quelconque
	 * On retourne une paire, (code de visite, valeur)
	 * 
	 * @param expr
	 * @return
	 */
	private Pair<Integer, Double> evaluate(ParseTree expr) {
		int b = visit(expr);
		Double val = b == 0 ? getValue(expr) : Double.POSITIVE_INFINITY;
		return new Pair<Integer, Double>(b, val);
	}

}
