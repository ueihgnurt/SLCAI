package chess;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import piece.*;

public class AI 
{
	private final Board board;
	private final String AIcolour;
	private Move bestMove;
	private final Movement movement;
	private final int startingDepth;
	public AI(Board board, String colour, Movement movment) 
			throws Exception
	{
		//System.out.println(colour);
		if(colour!= Board.White && colour!=Board.Black)
			throw new Exception("Colour invalid exception in AI class.");
		if(board == null)
			throw new Exception("Null Board exception in AI class.");
		if(movment == null)
			throw new Exception("Null movement exception in AI class.");
		
		this.board = board;
		this.AIcolour = colour;
		this.movement = movment;
		startingDepth = 4;
		System.out.println(colour);
	}
	private int valueOf(Piece piece)
	{
		if(piece instanceof King)
			return 900;
		else if(piece instanceof Queen)
			return 90;
		else if(piece instanceof Rook)
			return 50;
		else if(piece instanceof Bishop)
			return 30;
		else if(piece instanceof Knight)
			return 30;
		else if(piece instanceof Pawn)
			return 10;
		else
			return 0;
	}
	private int evaluate (Board board)
	{
		int valueOfBoard= 0;
		CopyOnWriteArrayList<Piece> ownPieces = board.getPieces(AIcolour);
		int ownPieceValue= 0;
		for(Piece ownPiece : ownPieces)
		{
			if(board.isKilled(ownPiece))
				continue;
			ownPieceValue += valueOf(ownPiece);			
		}
		
		int oppPieceValue = 0;
		CopyOnWriteArrayList<Piece> oppPieces = board.getPieces(Board.opposite(AIcolour));
		for(Piece oppPiece: oppPieces)
		{	
			if(board.isKilled(oppPiece))
				continue;
			oppPieceValue += valueOf(oppPiece);			
		}
		valueOfBoard = oppPieceValue - ownPieceValue;
		return valueOfBoard;
	}
	public void playNextMove()
	{
		Cell from = null, to=null;
		Move move = getNextMove();
		from = move.getSource();
		to = move.getDestination();
		board.clicked(from.row, from.col);
		board.clicked(to.row, to.col);
	}
	private double minimax(int depth, Board board, String playerColour, 
						double alpha, double beta)
	{
		if(depth == 0)
		{
			if(playerColour.equals(AIcolour))
				return -evaluate(board);
			else
				return evaluate(board);
		}
		else if(playerColour.equals(this.AIcolour))
		{	
			Move move = null;
			CopyOnWriteArrayList<Piece> ownPieces = board.getPieces(playerColour);
			
			for(Piece ownPiece: ownPieces)
			{	
				if(board.isKilled(ownPiece))
					continue;
				
				ArrayList<Cell> moves = movement.getAllMoves(ownPiece);
				for(Cell dest: moves)
				{	
					Move tempMove = movement.moveTo(ownPiece, dest);
					//System.out.println(ownPiece+" "+moves+" "+tempMove+" "+dest);
					if(tempMove == null)
					{	continue;
						//tempMove.getSource();
					}
					//if(move == null)
					//	move = tempMove;
					double value = minimax(depth-1, board, 
							Board.opposite(playerColour), alpha, beta);
					//System.out.printf("%d ", value);
					if(tempMove.moveType.equals(Movement.castlingMove))
					{	
						value+= 50;
					}
					if(alpha < value)
					{
						alpha = value;
						move = tempMove;
					}
					movement.undoMove();

					if(alpha == beta)break;	
				}
				if(alpha == beta)break;
			
			}
			if(depth == startingDepth)
				bestMove = move;
			return alpha;
		}
		else
		{
			CopyOnWriteArrayList<Piece> oppPieces = board.getPieces(playerColour);
			
			for(Piece oppPiece: oppPieces)
			{	
				if(board.isKilled(oppPiece))
					continue;
				
				ArrayList<Cell> moves = movement.getAllMoves(oppPiece);
				for(Cell dest: moves)
				{
					Move tempMove = movement.moveTo(oppPiece, dest);
					if(tempMove == null)
					{	continue;
					}
					double value = minimax(depth-1, board, Board.opposite(playerColour), 
							alpha, beta);
					if(tempMove.moveType.equals(Movement.castlingMove))
					{	
						value-= 50;
					}
					if(beta > value)
					{
						beta = value;
					}
					movement.undoMove();

					if(alpha == beta)break;
				}
				if(alpha == beta)break;
			}
			return beta;
		}
	}
	private Move getNextMove()
	{
		final int inf = 1000000;
		long startTime = System.nanoTime();
		this.minimax(startingDepth, board, this.AIcolour, -inf, inf);
//		System.out.println(nodeCount+" nodes visited.");	
		long stopTime = System.nanoTime();
		System.out.println((stopTime-startTime)/(1e9)+ " second(s)");
		System.gc();
		return bestMove;
	}
	
	/**
	 * @return AI colour.
	 * */
	
	public String getColour()
	{
		return this.AIcolour;
	}
}
