package fr.utc.model;

import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.xml.sax.ErrorHandler;

import fr.utc.gui.Traceur;
import fr.utc.parsing.LogoParser.AvContext;
import fr.utc.parsing.LogoParser.BcContext;
import fr.utc.parsing.LogoParser.CosContext;
import fr.utc.parsing.LogoParser.DiffContext;
import fr.utc.parsing.LogoParser.EgalContext;
import fr.utc.parsing.LogoParser.FcapContext;
import fr.utc.parsing.LogoParser.FccContext;
import fr.utc.parsing.LogoParser.FloatContext;
import fr.utc.parsing.LogoParser.FposContext;
import fr.utc.parsing.LogoParser.HasardContext;
import fr.utc.parsing.LogoParser.IfContext;
import fr.utc.parsing.LogoParser.InfContext;
import fr.utc.parsing.LogoParser.InfegContext;
import fr.utc.parsing.LogoParser.LcContext;
import fr.utc.parsing.LogoParser.LoopContext;
import fr.utc.parsing.LogoParser.MoveContext;
import fr.utc.parsing.LogoParser.MultContext;
import fr.utc.parsing.LogoParser.ParentheseContext;
import fr.utc.parsing.LogoParser.ReContext;
import fr.utc.parsing.LogoParser.ReadContext;
import fr.utc.parsing.LogoParser.RepeteContext;
import fr.utc.parsing.LogoParser.StoreContext;
import fr.utc.parsing.LogoParser.SumContext;
import fr.utc.parsing.LogoParser.SupContext;
import fr.utc.parsing.LogoParser.SupegContext;
import fr.utc.parsing.LogoParser.TdContext;
import fr.utc.parsing.LogoParser.TgContext;
import fr.utc.parsing.LogoParser.WhileContext;
import fr.utc.parsing.LogoParser.WriteContext;
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
	Stack<TableSymbole> tableSymboles = new Stack<>();
	Double indexRep;



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
	public Integer visitTg(TgContext ctx) {
		Pair<Integer, Double> bilan = evaluate(ctx.expr());
		if (bilan.a == 0) {
			traceur.tg(bilan.b);
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


	// Expressions

	@Override
	public Integer visitFloat(FloatContext ctx) {
		String floatText = ctx.FLOAT().getText();
		setValue(ctx, Double.valueOf(floatText));
		return 0;
	}

	// TP 2


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
		Pair<Integer, Double> exprX = evaluate(ctx.expr(0));   // Besoin de récupérer 2 paires de valeurs pour la position
		Pair<Integer, Double> exprY = evaluate(ctx.expr(1));
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

	// TP 3

	@Override
	public Integer visitHasard(HasardContext ctx) {
		try {

			Pair<Integer, Double> bilan = evaluate(ctx.expr());

			if (bilan.a == 0)
				setValue(ctx, Math.random() * bilan.b);
			else
				return bilan.a;

		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}
		return 0;
	}



	@Override
	public Integer visitParenthese(ParentheseContext ctx) {
		Pair<Integer, Double> expr = evaluate(ctx.expr());
		if (expr.a == 0) {
			setValue(ctx, expr.b);
		}
		return 0;
	}



	@Override
	public Integer visitLoop(LoopContext ctx) {
		if (indexRep != null) {
			setValue(ctx, indexRep);            //indexRep correspond à la valeur de l'itération dans la boucle repete
		}
		return 0;
	}



	@Override
	public Integer visitMult(MultContext ctx) {
		Pair<Integer, Double> exprL = evaluate(ctx.expr(0));   //valeur gauche de l'expression
		Pair<Integer, Double> exprR = evaluate(ctx.expr(1));   //valeur droite de l'expression
		String operateur = ctx.getChild(1).getText();     // Récupère l'élément en seconde position dans la ligne de code

		if (exprL.a == 0 && exprR.a == 0) {
			switch (operateur) {
				case "*":
					setValue(ctx, exprL.b * exprR.b);
					break;
				case "/":
					if (exprR.b != 0) {
						setValue(ctx, exprL.b / exprR.b);
					} else {
						log.appendLog("Attention : division par 0, instruction ignorée");
						return 1;
					}
					break;
				default:
					break;
			}
		}
		return 0;
	}



	@Override
	public Integer visitCos(CosContext ctx) {
		Pair<Integer, Double> bilan = evaluate(ctx.expr());
		String operateur = ctx.getChild(0).getText();

		if (bilan.a == 0) {
			switch (operateur) {
				case "cos(":
					setValue(ctx, Math.cos(Math.toRadians(bilan.b)));
					break;
				case "sin(":
					setValue(ctx, Math.sin(Math.toRadians(bilan.b)));
					break;
				default:
					break;
			}

		}
		return 0;
	}



	@Override
	public Integer visitSum(SumContext ctx) {
		Pair<Integer, Double> exprL = evaluate(ctx.expr(0));
		Pair<Integer, Double> exprR = evaluate(ctx.expr(1));
		String operateur = ctx.getChild(1).getText();

		if (exprL.a == 0 && exprR.a == 0) {
			switch (operateur) {
				case "+":
					setValue(ctx, exprL.b + exprR.b);
					break;
				case "-":
					setValue(ctx, exprL.b - exprR.b);
					break;
				default:
					break;
			}
		}
		return 0;
	}


	

	@Override
	public Integer visitRepete(RepeteContext ctx) {
		Pair<Integer, Double> exprRepet = evaluate(ctx.expr());
		if (exprRepet.a == 0) {
			for (int i = 1; i <= exprRepet.b; i++) {
				indexRep = (double) i;
				visit(ctx.liste_instructions());
			}
			indexRep = null;
		}
		return 0;

	}



	@Override
	public Integer visitMove(MoveContext arg0) {
		traceur.move();
		return 0;
	}

	@Override
	public Integer visitStore(StoreContext arg0) {
		traceur.store();
		return 0;
	}

	// TP 4

	@Override
	public Integer visitDiff(DiffContext ctx) {
		Pair<Integer, Double> exprL = evaluate(ctx.expr(0));
		Pair<Integer, Double> exprR = evaluate(ctx.expr(1));

		if (exprL.a == 0 && exprR.a == 0) {
			if (!(exprL.b == exprR.b)) {
				setValue(ctx, 1.0);
			} else {
				setValue(ctx, 0.0);
			}
		} else {
			return exprL.a == 0 ? exprR.a : exprL.a;
		}
		return 0;
	}

	@Override
	public Integer visitEgal(EgalContext ctx) {
		Pair<Integer, Double> exprL = evaluate(ctx.expr(0));
		Pair<Integer, Double> exprR = evaluate(ctx.expr(1));

		if (exprL.a == 0 && exprR.a == 0) {
			if (exprL.b == exprR.b) {
				setValue(ctx, 1.0);
			} else {
				setValue(ctx, 0.0);
			}
		} else {
			return exprL.a == 0 ? exprR.a : exprL.a;
		}
		return 0;
	}

	@Override
	public Integer visitInf(InfContext ctx) {
		Pair<Integer, Double> exprL = evaluate(ctx.expr(0));
		Pair<Integer, Double> exprR = evaluate(ctx.expr(1));

		if (exprL.a == 0 && exprR.a == 0) {
			if (exprL.b < exprR.b) {
				setValue(ctx, 1.0);
			} else {
				setValue(ctx, 0.0);
			}
		} else {
			return exprL.a == 0 ? exprR.a : exprL.a;
		}
		return 0;
	}

	@Override
	public Integer visitInfeg(InfegContext ctx) {
		Pair<Integer, Double> exprL = evaluate(ctx.expr(0));
		Pair<Integer, Double> exprR = evaluate(ctx.expr(1));

		if (exprL.a == 0 && exprR.a == 0) {
			if (exprL.b <= exprR.b) {
				setValue(ctx, 1.0);
			} else {
				setValue(ctx, 0.0);
			}
		} else {
			return exprL.a == 0 ? exprR.a : exprL.a;
		}
		return 0;
	}

	@Override
	public Integer visitRead(ReadContext ctx) {
		setValue(ctx, this.tableSymboles.peek().getEntry(ctx.ID().toString()));
		return 0;
	}

	@Override
	public Integer visitSup(SupContext ctx) {
		Pair<Integer, Double> exprL = evaluate(ctx.expr(0));
		Pair<Integer, Double> exprR = evaluate(ctx.expr(1));

		if (exprL.a == 0 && exprR.a == 0) {
			if (exprL.b > exprR.b) {
				setValue(ctx, 1.0);
			} else {
				setValue(ctx, 0.0);
			}
		} else {
			return exprL.a == 0 ? exprR.a : exprL.a;
		}
		return 0;
	}

	@Override
	public Integer visitSupeg(SupegContext ctx) {
		Pair<Integer, Double> exprL = evaluate(ctx.expr(0));
		Pair<Integer, Double> exprR = evaluate(ctx.expr(1));

		if (exprL.a == 0 && exprR.a == 0) {
			if (exprL.b >= exprR.b) {
				setValue(ctx, 1.0);
			} else {
				setValue(ctx, 0.0);
			}
		} else {
			return exprL.a == 0 ? exprR.a : exprL.a;
		}
		return 0;
	}

	@Override
	public Integer visitWrite(WriteContext ctx) {
		Pair<Integer, Double> bilan = evaluate(ctx.expr());
		if (bilan.a == 0) {
			if (this.tableSymboles.isEmpty()) {
				this.tableSymboles.push(new TableSymbole());
			}
			this.tableSymboles.peek().addEntry(ctx.ID().toString(), bilan.b);
		}
		return 0;
	}

	@Override
	public Integer visitIf(IfContext ctx) {
		Pair<Integer, Double> bilan = evaluate(ctx.expr());
		if (bilan.a == 0) {
			if (bilan.b != 0) {
				visit(ctx.liste_instructions(0));
			} else if (ctx.liste_instructions().size() > 1) {
				visit(ctx.liste_instructions(1));
			}
		}
		return 0;
	}

	@Override
	public Integer visitWhile(WhileContext ctx) {
		Pair<Integer, Double> bilan = evaluate(ctx.expr());
		if (bilan.a == 0) {
			while (bilan.b != 0) {
				visit(ctx.liste_instructions());
				bilan = evaluate(ctx.expr());
			}
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
