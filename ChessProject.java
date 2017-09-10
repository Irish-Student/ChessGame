/*
 * Project: Chessboard
 * Name: Youcef O'Connor
 * Number: x*******
 * Date: 20/12/16
 */



import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.Stack;
import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.Box;
import javax.swing.BoxLayout;


public class ChessProject extends JFrame implements MouseListener, MouseMotionListener {
    JLayeredPane layeredPane;
    JPanel chessBoard;
    JLabel chessPiece;
    int xAdjustment;
    int yAdjustment;
	  int startX;
	  int startY;
	  int initialX;
	  int initialY;
	  JPanel panels;
	  JLabel pieces;
    Boolean win;
    String winner;
    Boolean white2Move;
    int moveCounter;
    AIAgent agent;
    Boolean agentwins;
    Stack temporary;
    int optionPicked;

    //declare the method (MethodPicked) that lets the user pick the AI strategy
    public void optionPicked(int n){
      optionPicked = n;
    }

    //Give the pieces a value.
    //Pawn being the lowest value and King being the highest.
    public int pieceValue(int x, int y, int oldx, int oldy){

      int value = 0;
      String pieceColor = null;

      if(!(checkWhiteOponent(oldx, oldy))) pieceColor = "Black";
      else pieceColor = "White";
      try{
        Component c1 = chessBoard.findComponentAt(x,y);
        JLabel awaitingPiece = (JLabel)c1;
        String tmp1 = awaitingPiece.getIcon().toString();
        if(tmp1.contains(pieceColor+ "Pawn")){
          value = 1;
        }
        else if(tmp1.contains(pieceColor+ "Bishop")|| tmp1.contains(pieceColor+ "Knight")){
          value = 3;
        }
        else if(tmp1.contains(pieceColor+ "Rook")){
          value = 5;
        }
        else if(tmp1.contains(pieceColor+ "Queen")){
          value = 9;
        }
        else if(tmp1.contains(pieceColor+ "King")){
          value = 1000;
        }
      }
      catch(Exception e){
        System.out.println("Piece is NOT Present");
        return 0;
      }
      return value;
    }


    public ChessProject(){
        Dimension boardSize = new Dimension(600, 600);

        //  Use a Layered Pane for this application
        layeredPane = new JLayeredPane();
        getContentPane().add(layeredPane);
        layeredPane.setPreferredSize(boardSize);
        layeredPane.addMouseListener(this);
        layeredPane.addMouseMotionListener(this);

        //Add a chess board to the Layered Pane
        chessBoard = new JPanel();
        layeredPane.add(chessBoard, JLayeredPane.DEFAULT_LAYER);
        chessBoard.setLayout( new GridLayout(8, 8) );
        chessBoard.setPreferredSize( boardSize );
        chessBoard.setBounds(0, 0, boardSize.width, boardSize.height);

        for (int i = 0; i < 64; i++) {
            JPanel square = new JPanel( new BorderLayout() );
            chessBoard.add( square );

            int row = (i / 8) % 2;
            if (row == 0)
                square.setBackground( i % 2 == 0 ? Color.white : Color.gray );
            else
                square.setBackground( i % 2 == 0 ? Color.gray : Color.white );
        }

        // Setting up the Initial Chess board.
        for (int i = 8; i < 16; i++) {
            pieces = new JLabel(new ImageIcon("WhitePawn.png"));
            panels = (JPanel) chessBoard.getComponent(i);
            panels.add(pieces);
        }

        pieces = new JLabel(new ImageIcon("WhiteRook.png"));
        panels = (JPanel) chessBoard.getComponent(0);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("WhiteKnight.png"));
        panels = (JPanel) chessBoard.getComponent(1);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("WhiteKnight.png"));
        panels = (JPanel) chessBoard.getComponent(6);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("WhiteBishop.png"));
        panels = (JPanel) chessBoard.getComponent(2);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("WhiteBishop.png"));
        panels = (JPanel) chessBoard.getComponent(5);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("WhiteKing.png"));
        panels = (JPanel) chessBoard.getComponent(3);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("WhiteQueen.png"));
        panels = (JPanel) chessBoard.getComponent(4);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("WhiteRook.png"));
        panels = (JPanel) chessBoard.getComponent(7);
        panels.add(pieces);

        for (int i = 48; i < 56; i++) {
            pieces = new JLabel(new ImageIcon("BlackPawn.png"));
            panels = (JPanel) chessBoard.getComponent(i);
            panels.add(pieces);
        }
        pieces = new JLabel(new ImageIcon("BlackRook.png"));
        panels = (JPanel) chessBoard.getComponent(56);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("BlackKnight.png"));
        panels = (JPanel) chessBoard.getComponent(57);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("BlackKnight.png"));
        panels = (JPanel) chessBoard.getComponent(62);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("BlackBishop.png"));
        panels = (JPanel) chessBoard.getComponent(58);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("BlackBishop.png"));
        panels = (JPanel) chessBoard.getComponent(61);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("BlackKing.png"));
        panels = (JPanel) chessBoard.getComponent(59);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("BlackQueen.png"));
        panels = (JPanel) chessBoard.getComponent(60);
        panels.add(pieces);
        pieces = new JLabel(new ImageIcon("BlackRook.png"));
        panels = (JPanel) chessBoard.getComponent(63);
        panels.add(pieces);


        moveCounter = 0;
        white2Move = true;
        agent = new AIAgent();
        agentwins = false;
        temporary = new Stack();
    }


    //This is how all the pawn moves are stored in a stack.
private Stack getPawnMoves(int x, int y, String piece){
    Stack moves = new Stack();
    Square startingSquare = new Square(x,y, piece);
   //instances of each possible move
   Move validM, validM2, validM3, validM4;
   
  //Temporary int positions
    int tmpx1 = x+1;
    int tmpx2 = x-1;
    int tmpy1 = y+1;
    int tmpy2 = y=2;//first pawn move has gone 2 sq on Y axis
    Square tmp = new Square(tmpx1, tmpy1, piece);
    Square tmp2 = new Square(tmpx2, tmpy1, piece);
    Square tmp3 = new Square(x, tmpy1, piece);
    Square tmp4 = new Square(x, tmpy2, piece);

    System.out.println("Position is ("+x+", "+y+")");
    System.out.println("Temp Values: " +tmpx1+ " "+tmpx2+" "+tmpy1+" "+tmpy2);

    //Checks temperary data to see if the location is empty. If it is valid move. If not then get piece value and push / store move.
    if(y == 1 && !piecePresent(((tmp3.getXC()*75)+20),((tmp3.getYC()*75)+20))){
      validM = new Move(startingSquare, tmp4, pieceValue(((tmp4.getXC()*75)+20), ((tmp4.getYC()*75)+20),x,y));
        if((!piecePresent(((tmp4.getXC()*75)+20), ((tmp4.getYC()*75)+20)))){
          moves.push(validM);
        }
    }
        //Keep pieces inside the board (enviroment)
        if((tmpx1 <= 7 || tmpx2 >= 0)&&(tmpy1 <= 7)){
      //checks pawn enviroment to see there is an apponent & if it is safe to move or attack
      validM = new Move(startingSquare, tmp, pieceValue(((tmp.getXC()*75)+20), ((tmp.getYC()*75)+20),x,y) );
          moves.push(validM);
        }
        //If a piece can be attacked / taken
        validM = new Move(startingSquare, tmp2,pieceValue(((tmp2.getXC()*75)+20), ((tmp2.getYC()*75)+20),x,y));
        if(checkWhiteOponent(((tmp2.getXC()*75)+20), (((tmp2.getYC()*75)+20)))){
          moves.push(validM);
        }
        //making sure there is nothing stoping the pawn from moving forward
        validM = new Move(startingSquare, tmp3,pieceValue(((tmp3.getXC()*75)+20), ((tmp3.getYC()*75)+20),x,y));
        if(!piecePresent(((tmp3.getXC()*75)+20), (((tmp3.getYC()*75)+20)))){
          moves.push(validM);
        }


  return moves;
}



private Boolean checkSurroundingSquares(Square s){
  Boolean possible = false;
  int x = s.getXC()*75;
  int y = s.getYC()*75;
  if(!((getPieceName((x+75), y).contains("BlackKing"))||(getPieceName((x-75), y).contains("BlackKing"))||(getPieceName(x,(y+75)).contains("BlackKing"))||(getPieceName((x), (y-75)).contains("BlackKing"))||(getPieceName((x+75),(y+75)).contains("BlackKing"))||(getPieceName((x-75),(y+75)).contains("BlackKing"))||(getPieceName((x+75),(y-75)).contains("BlackKing"))||(getPieceName((x-75), (y-75)).contains("BlackKing")))){
      possible = true;
  }
  return possible;
}

private Stack getKingSquares(int x, int y, String piece){
  Square startingSquare = new Square(x, y, piece);
  Stack moves = new Stack();
  Move validM, validM2, validM3, validM4;
  int tmpx1 = x+1;
  int tmpx2 = x-1;
  int tmpy1 = y+1;
  int tmpy2 = y-1;

  if(!((tmpx1 > 7))){
    Square tmp = new Square(tmpx1, y, piece);
    Square tmp1 = new Square(tmpx1, tmpy1, piece);
    Square tmp2 = new Square(tmpx1, tmpy2, piece);
    if(checkSurroundingSquares(tmp)){
      validM = new Move(startingSquare, tmp, pieceValue(((tmp.getXC()*75)+20), ((tmp.getYC()*75)+20),x,y));
      if(!piecePresent(((tmp.getXC()*75)+20), (((tmp.getYC()*75)+20)))){
        moves.push(validM);
      }
      else{
        if(checkWhiteOponent(((tmp.getXC()*75)+20), (((tmp.getYC()*75)+20)))){
          moves.push(validM);
        }
      }
    }
    if(!(tmpy1 > 7)){
      if(checkSurroundingSquares(tmp1)){
        validM2 = new Move(startingSquare, tmp1, pieceValue(((tmp1.getXC()*75)+20), ((tmp1.getYC()*75)+20),x,y));
        if(!piecePresent(((tmp1.getXC()*75)+20), (((tmp1.getYC()*75)+20)))){
          moves.push(validM2);
        }
        else{
          if(checkWhiteOponent(((tmp1.getXC()*75)+20), (((tmp1.getYC()*75)+20)))){
            moves.push(validM2);
          }
        }
      }
    }
    if(!(tmpy2 < 0)){
      if(checkSurroundingSquares(tmp2)){
        validM3 = new Move(startingSquare, tmp2, pieceValue(((tmp2.getXC()*75)+20), ((tmp2.getYC()*75)+20),x,y));
        if(!piecePresent(((tmp2.getXC()*75)+20), (((tmp2.getYC()*75)+20)))){
          moves.push(validM3);
        }
        else{
          System.out.println("The values that we are going to be looking at are : "+((tmp2.getXC()*75)+20)+" and the y value is : "+((tmp2.getYC()*75)+20));
          if(checkWhiteOponent(((tmp2.getXC()*75)+20), (((tmp2.getYC()*75)+20)))){
            moves.push(validM3);
          }
        }
      }
    }
  }
  if(!((tmpx2 < 0))){
    Square tmp3 = new Square(tmpx2, y, piece);
    Square tmp4 = new Square(tmpx2, tmpy1, piece);
    Square tmp5 = new Square(tmpx2, tmpy2, piece);
    if(checkSurroundingSquares(tmp3)){
      validM = new Move(startingSquare, tmp3, pieceValue(((tmp3.getXC()*75)+20), ((tmp3.getYC()*75)+20),x,y));
      if(!piecePresent(((tmp3.getXC()*75)+20), (((tmp3.getYC()*75)+20)))){
        moves.push(validM);
      }
      else{
        if(checkWhiteOponent(((tmp3.getXC()*75)+20), (((tmp3.getYC()*75)+20)))){
          moves.push(validM);
        }
      }
    }
    if(!(tmpy1 > 7)){
      if(checkSurroundingSquares(tmp4)){
        validM2 = new Move(startingSquare, tmp4, pieceValue(((tmp4.getXC()*75)+20), ((tmp4.getYC()*75)+20),x,y));
        if(!piecePresent(((tmp4.getXC()*75)+20), (((tmp4.getYC()*75)+20)))){
          moves.push(validM2);
        }
        else{
          if(checkWhiteOponent(((tmp4.getXC()*75)+20), (((tmp4.getYC()*75)+20)))){
            moves.push(validM2);
          }
        }
      }
    }
    if(!(tmpy2 < 0)){
      if(checkSurroundingSquares(tmp5)){
        validM3 = new Move(startingSquare, tmp5, pieceValue(((tmp5.getXC()*75)+20), ((tmp5.getYC()*75)+20),x,y));
        if(!piecePresent(((tmp5.getXC()*75)+20), (((tmp5.getYC()*75)+20)))){
          moves.push(validM3);
        }
        else{
          if(checkWhiteOponent(((tmp5.getXC()*75)+20), (((tmp5.getYC()*75)+20)))){
            moves.push(validM3);
          }
        }
      }
    }
  }
  Square tmp7 = new Square(x, tmpy1, piece);
  Square tmp8 = new Square(x, tmpy2, piece);
  if(!(tmpy1 > 7)){
    if(checkSurroundingSquares(tmp7)){
      validM2 = new Move(startingSquare, tmp7, pieceValue(((tmp7.getXC()*75)+20), ((tmp7.getYC()*75)+20),x,y));
      if(!piecePresent(((tmp7.getXC()*75)+20), (((tmp7.getYC()*75)+20)))){
        moves.push(validM2);
      }
      else{
        if(checkWhiteOponent(((tmp7.getXC()*75)+20), (((tmp7.getYC()*75)+20)))){
          moves.push(validM2);
        }
      }
    }
  }
  if(!(tmpy2 < 0)){
    if(checkSurroundingSquares(tmp8)){
      validM3 = new Move(startingSquare, tmp8, pieceValue(((tmp8.getXC()*75)+20), ((tmp8.getYC()*75)+20),x,y));
      if(!piecePresent(((tmp8.getXC()*75)+20), (((tmp8.getYC()*75)+20)))){
        moves.push(validM3);
      }
      else{
        if(checkWhiteOponent(((tmp8.getXC()*75)+20), (((tmp8.getYC()*75)+20)))){
          moves.push(validM3);
        }
      }
    }
  }
  return moves;
} // end of the method getKingSquares()


/*
    Method to return all the possible moves that a Queen can make
*/
private Stack getQueenMoves(int x, int y, String piece){
  Stack completeMoves = new Stack();
  Stack tmpMoves = new Stack();
  Move tmp;

  tmpMoves = getRookMoves(x, y, piece);
  while(!tmpMoves.empty()){
    tmp = (Move)tmpMoves.pop();
    completeMoves.push(tmp);
  }
  tmpMoves = getBishopMoves(x, y, piece);
  while(!tmpMoves.empty()){
    tmp = (Move)tmpMoves.pop();
    completeMoves.push(tmp);
  }
  return completeMoves;
}

private Stack getRookMoves(int x, int y, String piece){
  Square startingSquare = new Square(x, y, piece);
  Stack moves = new Stack();
  Move validM, validM2, validM3, validM4;

  for(int i=1;i < 8;i++){
    int tmpx = x+i;
    int tmpy = y;
    if(!(tmpx > 7 || tmpx < 0)){
      Square tmp = new Square(tmpx, tmpy, piece);
      validM = new Move(startingSquare, tmp, pieceValue(((tmp.getXC()*75)+20), ((tmp.getYC()*75)+20),x,y));
      if(!piecePresent(((tmp.getXC()*75)+20), (((tmp.getYC()*75)+20)))){
        moves.push(validM);
      }
      else{
        if(checkWhiteOponent(((tmp.getXC()*75)+20), ((tmp.getYC()*75)+20))){
          moves.push(validM);
          break;
        }
        else{
          break;
        }
      }
    }
  }//end of the loop with x increasing and Y doing nothing...
  for(int j=1;j < 8;j++){
    int tmpx1 = x-j;
    int tmpy1 = y;
    if(!(tmpx1 > 7 || tmpx1 < 0)){
      Square tmp2 = new Square(tmpx1, tmpy1, piece);
      validM2 = new Move(startingSquare, tmp2, pieceValue(((tmp2.getXC()*75)+20), ((tmp2.getYC()*75)+20),x,y));
      if(!piecePresent(((tmp2.getXC()*75)+20), (((tmp2.getYC()*75)+20)))){
        moves.push(validM2);
      }
      else{
        if(checkWhiteOponent(((tmp2.getXC()*75)+20), ((tmp2.getYC()*75)+20))){
          moves.push(validM2);
          break;
        }
        else{
          break;
        }
      }
    }
  }
  for(int k=1;k < 8;k++){
    int tmpx3 = x;
    int tmpy3 = y+k;
    if(!(tmpy3 > 7 || tmpy3 < 0)){
      Square tmp3 = new Square(tmpx3, tmpy3, piece);
      validM3 = new Move(startingSquare, tmp3, pieceValue(((tmp3.getXC()*75)+20), ((tmp3.getYC()*75)+20),x,y));
      if(!piecePresent(((tmp3.getXC()*75)+20), (((tmp3.getYC()*75)+20)))){
        moves.push(validM3);
      }
      else{
        if(checkWhiteOponent(((tmp3.getXC()*75)+20), ((tmp3.getYC()*75)+20))){
          moves.push(validM3);
          break;
        }
        else{
          break;
        }
      }
    }
  }
  for(int l=1;l < 8;l++){
    int tmpx4 = x;
    int tmpy4 = y-l;
    if(!(tmpy4 > 7 || tmpy4 < 0)){
      Square tmp4 = new Square(tmpx4, tmpy4, piece);
      validM4 = new Move(startingSquare, tmp4, pieceValue(((tmp4.getXC()*75)+20), ((tmp4.getYC()*75)+20),x,y));
      if(!piecePresent(((tmp4.getXC()*75)+20), (((tmp4.getYC()*75)+20)))){
        moves.push(validM4);
      }
      else{
        if(checkWhiteOponent(((tmp4.getXC()*75)+20), ((tmp4.getYC()*75)+20))){
          moves.push(validM4);
          break;
        }
        else{
          break;
        }
      }
    }
  }
  return moves;
}// end of get Rook Moves.


private Stack getBishopMoves(int x, int y, String piece){
  Square startingSquare = new Square(x, y, piece);
  Stack moves = new Stack();
  Move validM, validM2, validM3, validM4;

  for(int i=1;i < 8;i++){
    int tmpx = x+i;
    int tmpy = y+i;
    if(!(tmpx > 7 || tmpx < 0 || tmpy > 7 || tmpy < 0)){
      Square tmp = new Square(tmpx, tmpy, piece);
      validM = new Move(startingSquare, tmp, pieceValue(((tmp.getXC()*75)+20), ((tmp.getYC()*75)+20),x,y));
      if(!piecePresent(((tmp.getXC()*75)+20), (((tmp.getYC()*75)+20)))){
        moves.push(validM);
      }
      else{
        if(checkWhiteOponent(((tmp.getXC()*75)+20), ((tmp.getYC()*75)+20))){
          moves.push(validM);
          break;
        }
        else{
          break;
        }
      }
    }
  } // end of the first for Loop
  for(int k=1;k < 8;k++){
    int tmpk = x+k;
    int tmpy2 = y-k;
    if(!(tmpk > 7 || tmpk < 0 || tmpy2 > 7 || tmpy2 < 0)){
      Square tmpK1 = new Square(tmpk, tmpy2, piece);
      validM2 = new Move(startingSquare, tmpK1, pieceValue(((tmpK1.getXC()*75)+20), ((tmpK1.getYC()*75)+20),x,y));
      if(!piecePresent(((tmpK1.getXC()*75)+20), (((tmpK1.getYC()*75)+20)))){
        moves.push(validM2);
      }
      else{
        if(checkWhiteOponent(((tmpK1.getXC()*75)+20), ((tmpK1.getYC()*75)+20))){
          moves.push(validM2);
          break;
        }
        else{
          break;
        }
      }
    }
  } //end of second loop.
  for(int l=1;l < 8;l++){
    int tmpL2 = x-l;
    int tmpy3 = y+l;
    if(!(tmpL2 > 7 || tmpL2 < 0 || tmpy3 > 7 || tmpy3 < 0)){
      Square tmpLMov2 = new Square(tmpL2, tmpy3, piece);
      validM3 = new Move(startingSquare, tmpLMov2, pieceValue(((tmpLMov2.getXC()*75)+20), ((tmpLMov2.getYC()*75)+20),x,y));
      if(!piecePresent(((tmpLMov2.getXC()*75)+20), (((tmpLMov2.getYC()*75)+20)))){
        moves.push(validM3);
      }
      else{
        if(checkWhiteOponent(((tmpLMov2.getXC()*75)+20), ((tmpLMov2.getYC()*75)+20))){
          moves.push(validM3);
          break;
        }
        else{
          break;
        }
      }
    }
  }// end of the third loop
  for(int n=1;n < 8;n++){
    int tmpN2 = x-n;
    int tmpy4 = y-n;
    if(!(tmpN2 > 7 || tmpN2 < 0 || tmpy4 > 7 || tmpy4 < 0)){
      Square tmpNmov2 = new Square(tmpN2, tmpy4, piece);
      validM4 = new Move(startingSquare, tmpNmov2, pieceValue(((tmpNmov2.getXC()*75)+20), ((tmpNmov2.getYC()*75)+20),x,y));
      if(!piecePresent(((tmpNmov2.getXC()*75)+20), (((tmpNmov2.getYC()*75)+20)))){
        moves.push(validM4);
      }
      else{
        if(checkWhiteOponent(((tmpNmov2.getXC()*75)+20), ((tmpNmov2.getYC()*75)+20))){
          moves.push(validM4);
          break;
        }
        else{
          break;
        }
      }
    }
  }// end of the last loop
  return moves;
}

private Stack getKnightMoves(int x, int y, String piece){
  Square startingSquare = new Square(x, y, piece);
  Stack moves = new Stack();
  Stack attackingMove = new Stack();
  Square s = new Square(x+1, y+2, piece);
  moves.push(s);
  Square s1 = new Square(x+1, y-2, piece);
  moves.push(s1);
  Square s2 = new Square(x-1, y+2, piece);
  moves.push(s2);
  Square s3 = new Square(x-1, y-2, piece);
  moves.push(s3);
  Square s4 = new Square(x+2, y+1, piece);
  moves.push(s4);
  Square s5 = new Square(x+2, y-1, piece);
  moves.push(s5);
  Square s6 = new Square(x-2, y+1, piece);
  moves.push(s6);
  Square s7 = new Square(x-2, y-1, piece);
  moves.push(s7);

  for(int i=0;i < 8;i++){
    Square tmp = (Square)moves.pop();
    Move tmpmove = new Move(startingSquare, tmp, pieceValue(((tmp.getXC()*75)+20), ((tmp.getYC()*75)+20),x,y));
    if((tmp.getXC() < 0)||(tmp.getXC() > 7)||(tmp.getYC() < 0)||(tmp.getYC() > 7)){
    }

    else if(piecePresent(((tmp.getXC()*75)+20), (((tmp.getYC()*75)+20)))){
      if(piece.contains("White")){
        if(checkWhiteOponent(((tmp.getXC()*75)+20), ((tmp.getYC()*75)+20))){
          attackingMove.push(tmpmove);
        }
      }
    }
    else{
      attackingMove.push(tmpmove);
    }
  }
  return attackingMove;
}


/*
        Method to colour a stack of Squares
*/
private void colorSquares(Stack squares){
  Border greenBorder = BorderFactory.createLineBorder(Color.GREEN, 3);
  while(!squares.empty()){
    Square s = (Square)squares.pop();
    int location = s.getXC() + ((s.getYC())*8);
    JPanel panel = (JPanel)chessBoard.getComponent(location);
    panel.setBorder(greenBorder);
  }
}

/*
    Method to get the landing square of a bunch of moves...
*/
private void getLandingSquares(Stack found){
  Move tmp;
  Square landing;
  Stack squares = new Stack();
  while(!found.empty()){
    tmp = (Move)found.pop();
    landing = (Square)tmp.getLanding();
    squares.push(landing);
  }
  colorSquares(squares);
}


/*
  Method to find all the White Pieces.
*/
  private Stack findWhitePieces(){
    Stack squares = new Stack();
    String icon;
    int x;
    int y;
    String pieceName;
    for(int i=0;i < 600;i+=75){
      for(int j=0;j < 600;j+=75){
        y = i/75;
        x=j/75;
        Component tmp = chessBoard.findComponentAt(j, i);
        if(tmp instanceof JLabel){
          chessPiece = (JLabel)tmp;
          icon = chessPiece.getIcon().toString();
          pieceName = icon.substring(0, (icon.length()-4));
          if(pieceName.contains("White")){
            Square stmp = new Square(x, y, pieceName);
            squares.push(stmp);
          }
        }
      }
    }
    return squares;
  }


  /*
    Method to find all the Black Pieces.
  */
    private Stack findBlackPieces(){
      Stack squares = new Stack();
      String icon;
      int x;
      int y;
      String pieceName;
      for(int i=0;i < 600;i+=75){
        for(int j=0;j < 600;j+=75){
          y = i/75;
          x=j/75;
          Component tmp = chessBoard.findComponentAt(j, i);
          if(tmp instanceof JLabel){
            chessPiece = (JLabel)tmp;
            icon = chessPiece.getIcon().toString();
            pieceName = icon.substring(0, (icon.length()-4));
            if(pieceName.contains("Black")){
              Square stmp = new Square(x, y, pieceName);
              squares.push(stmp);
            }
          }
        }
      }
      return squares;
    }

	/*
		This method checks if there is a piece present on a particular square.
	*/
	private Boolean piecePresent(int x, int y){
		Component c = chessBoard.findComponentAt(x, y);
		if(c instanceof JPanel){
			return false;
		}
		else{
			return true;
		}
	}

	/*
		This is a method to check if a piece is a Black piece.
	*/
	private Boolean checkWhiteOponent(int newX, int newY){
		Boolean oponent;
    try{
		Component c1 = chessBoard.findComponentAt(newX, newY);
		JLabel awaitingPiece = (JLabel)c1;
		String tmp1 = awaitingPiece.getIcon().toString();
		if(((tmp1.contains("Black")))){
			oponent = true;
		}
		else{
			oponent = false;
		}
  }
  catch(Exception e){
    oponent = false;
  }
		return oponent;
	}

  private void resetBorders(){
    Border empty = BorderFactory.createEmptyBorder();
    for(int i=0;i < 64;i++){
      JPanel tmppanel = (JPanel)chessBoard.getComponent(i);
      tmppanel.setBorder(empty);
    }
  }

  /*
    The method printStack takes in a Stack of Moves and prints out all possible moves.
  */
private void printStack(Stack input){
  Move m;
  Square s, l;
  while(!input.empty()){
    m = (Move)input.pop();
    s = (Square)m.getStart();
    l = (Square)m.getLanding();
    System.out.println("The possible move that was found is : ("+s.getXC()+" , "+s.getYC()+"), landing at ("+l.getXC()+" , "+l.getYC()+")");
  }
}

  private void makeAIMove(){
    /*
      When the AI Agent decides on a move, a red border shows the square from where the move started and the
      landing square of the move.
    */
    resetBorders();
    layeredPane.validate();
    layeredPane.repaint();
    Stack white = findWhitePieces();
    Stack completeMoves = new Stack();
    Move tmp;
    while(!white.empty()){
      Square s = (Square)white.pop();
      String tmpString = s.getName();
      Stack tmpMoves = new Stack();
      Stack temporary = new Stack();
      /*
          We need to identify all the possible moves that can be made by the AI Opponent
      */
      if(tmpString.contains("Knight")){
        tmpMoves = getKnightMoves(s.getXC(), s.getYC(), s.getName());
      }
      else if(tmpString.contains("Bishop")){
        tmpMoves = getBishopMoves(s.getXC(), s.getYC(), s.getName());
      }
      else if(tmpString.contains("Pawn")){
        tmpMoves = getPawnMoves(s.getXC(), s.getYC(), s.getName());
      }
      else if(tmpString.contains("Rook")){
        tmpMoves = getRookMoves(s.getXC(), s.getYC(), s.getName());
      }
      else if(tmpString.contains("Queen")){
        tmpMoves = getQueenMoves(s.getXC(), s.getYC(), s.getName());
      }
      else if(tmpString.contains("King")){
        tmpMoves = getKingSquares(s.getXC(), s.getYC(), s.getName());
      }

      while(!tmpMoves.empty()){
        tmp = (Move)tmpMoves.pop();
        completeMoves.push(tmp);
      }
    }
    temporary = (Stack)completeMoves.clone();
    getLandingSquares(temporary);
    printStack(temporary);
/*
  So now we should have a copy of all the possible moves to make in our Stack called completeMoves
*/
  if(completeMoves.size() == 0){
/*
      In Chess if you cannot make a valid move but you are not in Check this state is referred to
      as a Stale Mate
*/
  JOptionPane.showMessageDialog(null, "Cogratulations, you have placed the AI component in a Stale Mate Position");
  System.exit(0);

  }
  else{
    
      System.out.println("=============================================================");
      Stack testing = new Stack();
      while(!completeMoves.empty()){
        Move tmpMove = (Move)completeMoves.pop();
        Square s1 = (Square)tmpMove.getStart();
        Square s2 = (Square)tmpMove.getLanding();
        int thisValue = (int)tmpMove.getPoints();
        System.out.println("The "+s1.getName()+" can move from ("+s1.getXC()+", "+s1.getYC()+") to the following square: ("+s2.getXC()+", "+s2.getYC()+")"+thisValue);
        testing.push(tmpMove);
      }
       System.out.println("=============================================================");
       Border redBorder = BorderFactory.createLineBorder(Color.RED, 3);
       Move selectedMove = agent.MethodPicked(optionPicked, testing);
       Square startingPoint = (Square)selectedMove.getStart();
       Square landingPoint = (Square)selectedMove.getLanding();
       int startX1 = (startingPoint.getXC()*75)+20;
       int startY1 = (startingPoint.getYC()*75)+20;
       int landingX1 = (landingPoint.getXC()*75)+20;
       int landingY1 = (landingPoint.getYC()*75)+20;
       System.out.println("-------- Move "+startingPoint.getName()+" ("+startingPoint.getXC()+", "+startingPoint.getYC()+") to ("+landingPoint.getXC()+", "+landingPoint.getYC()+")");

       Component c  = (JLabel)chessBoard.findComponentAt(startX1, startY1);
       Container parent = c.getParent();
       parent.remove(c);
       int panelID = (startingPoint.getYC() * 8)+startingPoint.getXC();
       panels = (JPanel)chessBoard.getComponent(panelID);
       panels.setBorder(redBorder);
       parent.validate();

       Component l = chessBoard.findComponentAt(landingX1, landingY1);
       if(l instanceof JLabel){
          Container parentlanding = l.getParent();
          JLabel awaitingName = (JLabel)l;
          String agentCaptured = awaitingName.getIcon().toString();
          if(agentCaptured.contains("King")){
            agentwins = true;
          }
          parentlanding.remove(l);
          parentlanding.validate();
          pieces = new JLabel( new ImageIcon(startingPoint.getName()+".png") );
          int landingPanelID = (landingPoint.getYC()*8)+landingPoint.getXC();
          panels = (JPanel)chessBoard.getComponent(landingPanelID);
          panels.add(pieces);
          panels.setBorder(redBorder);
          layeredPane.validate();
          layeredPane.repaint();

          if(agentwins){
            JOptionPane.showMessageDialog(null, "The AI Agent has won!");
            System.exit(0);
          }
        }
        else{
          pieces = new JLabel( new ImageIcon(startingPoint.getName()+".png") );
          int landingPanelID = (landingPoint.getYC()*8)+landingPoint.getXC();
          panels = (JPanel)chessBoard.getComponent(landingPanelID);
          panels.add(pieces);
          panels.setBorder(redBorder);
          layeredPane.validate();
          layeredPane.repaint();
        }
        white2Move = false;
  }
}

  /*
    This is a method to check if a piece is a Black piece.
  */
  private Boolean checkBlackOponent(int newX, int newY){
    Boolean oponent;
    Component c1 = chessBoard.findComponentAt(newX, newY);
    JLabel awaitingPiece = (JLabel)c1;
    String tmp1 = awaitingPiece.getIcon().toString();
    if(((tmp1.contains("White")))){
      oponent = true;
    }
    else{
      oponent = false;
    }
    return oponent;
  }

  private String getPieceName(int x, int y){
    Component c1 = chessBoard.findComponentAt(x, y);
    if(c1 instanceof JPanel){
			return "empty";
		}
		else if(c1 instanceof JLabel){
      JLabel awaitingPiece = (JLabel)c1;
  		String tmp1 = awaitingPiece.getIcon().toString();
			return tmp1;
		}
    else{
      return "empty";
    }
  }

  private Boolean pieceMove(int x, int y){
        if((startX == x)&&(startY == y)){
          return false;
        }
        else{
          return true;
        }
    }

	/*
		This method is called when the Mouse is pressed. 
	*/
    public void mousePressed(MouseEvent e){
        chessPiece = null;
        String name = getPieceName(e.getX(), e.getY());
        
        Component c =  chessBoard.findComponentAt(e.getX(), e.getY());
        if (c instanceof JPanel)
			     return;

        Point parentLocation = c.getParent().getLocation();
        xAdjustment = parentLocation.x - e.getX();
        yAdjustment = parentLocation.y - e.getY();
        chessPiece = (JLabel)c;
		    initialX = e.getX();
		    initialY = e.getY();
		    startX = (e.getX()/75);
	      startY = (e.getY()/75);

        if(name.contains("Knight")){

          getKnightMoves(startX, startY, name);
        }

        chessPiece.setLocation(e.getX() + xAdjustment, e.getY() + yAdjustment);
        chessPiece.setSize(chessPiece.getWidth(), chessPiece.getHeight());
        layeredPane.add(chessPiece, JLayeredPane.DRAG_LAYER);
    }

    public void mouseDragged(MouseEvent me) {
        if (chessPiece == null) return;
         chessPiece.setLocation(me.getX() + xAdjustment, me.getY() + yAdjustment);
     }

 	/*
		This method is used when the Mouse is released...we need to make sure the move was valid before
		putting the piece back on the board.
	*/
    public void mouseReleased(MouseEvent e) {
        if(chessPiece == null) return;

        chessPiece.setVisible(false);
		    Boolean success =false;
        Boolean promotion = false;
        Boolean progression = false;
        Component c =  chessBoard.findComponentAt(e.getX(), e.getY());

        String tmp = chessPiece.getIcon().toString();
		    String pieceName = tmp.substring(0, (tmp.length()-4));
		    Boolean validMove = false;

        int landingX = (e.getX()/75);
        int landingY  = (e.getY()/75);

        int xMovement = Math.abs((e.getX()/75)-startX);
        int yMovement = Math.abs((e.getY()/75)-startY);
        System.out.println("----------------------------------------------");
        System.out.println("The piece that is being moved is : "+pieceName);
        System.out.println("The starting coordinates are : "+"( "+startX+","+startY+")");
        System.out.println("The xMovement is : "+xMovement);
        System.out.println("The yMovement is : "+yMovement);
        System.out.println("The landing coordinates are : "+"( "+landingX+","+landingY+")");
        System.out.println("----------------------------------------------");

        /*
          We need a process to identify whos turn it is to make a move.
        */
        Boolean possible = false;

        if(white2Move){
          if(pieceName.contains("White")){
            possible = true;
          }
        }
        else{
          if(pieceName.contains("Black")){
            possible = true;
          }
        }
      if(possible){

        if(pieceName.contains("Bishop")){
          /*
            The Bishop is a simple piece and has a very long range so usually is used to attack from far away.
            It moves along the diagonal as long as nothing is in the way to its destination. Like all other Chess
            pieces it can take its opponent piece but cannot take its own piece.
          */
          Boolean inTheWay = false;
          if(((landingX < 0) || (landingX > 7))||((landingY < 0)||(landingY > 7))){
            validMove = false;
          }
          else if(!pieceMove(landingX, landingY)){
            validMove = false;
          }
          else{
            validMove = true;
            /*
              The condition to check for a validMove simple asks is the x movement is the same as the y
              movement, if so we are moving along a diagonal. We then need to identify which diagonal and
              make sure there is nothing in the way.
            */
            if(xMovement == yMovement){
              if((startX-landingX < 0)&&(startY-landingY < 0)){
                for(int i=0; i < xMovement;i++){
                  if(piecePresent((initialX+(i*75)), (initialY+(i*75)))){
                    inTheWay = true;
                  }
                }
              }
              else if((startX-landingX < 0)&&(startY-landingY > 0)){
                for(int i=0; i < xMovement;i++){
                  if(piecePresent((initialX+(i*75)), (initialY-(i*75)))){
                    inTheWay = true;
                  }
                }
              }
              else if((startX-landingX > 0)&&(startY-landingY > 0)){
                for(int i=0; i < xMovement;i++){
                  if(piecePresent((initialX-(i*75)), (initialY-(i*75)))){
                    inTheWay = true;
                  }
                }
              }
              else if((startX-landingX > 0)&&(startY-landingY < 0)){
                for(int i=0; i < xMovement;i++){
                  if(piecePresent((initialX-(i*75)), (initialY+(i*75)))){
                    inTheWay = true;
                  }
                }
              } // end of the conditions to check the appropriate diagonal to see if there is a piece in the way.
              if(inTheWay){
                validMove = false;
              }
              else{
                
                if(piecePresent(e.getX(), (e.getY()))){
                  if(pieceName.contains("White")){
                    if(checkWhiteOponent(e.getX(), e.getY())){
                      validMove = true;
                      if(getPieceName(e.getX(), e.getY()).contains("King")){
                        winner = "White Wins!";
                      }
                    }
                    else{
                      validMove = false;
                    }
                  }
                  else{
                    if(checkBlackOponent(e.getX(), e.getY())){
                      validMove = true;
                      if(getPieceName(e.getX(), e.getY()).contains("King")){
                        winner = "Black Wins!";
                      }
                    }
                    else{
                      validMove = false;
                    }
                  }
                }
                else{ // There is no piecePresent on the square that we are moving to.
                  validMove = true;
                }
              }
            }
            else{
              validMove = false;
            }
          }
        }
        else if(pieceName.contains("Rook")){
          /*
            The Rook moves along the x direction or the y direction any number of squares as long as there
            is nothing in its way. It can take an opponents piece and as you know it cannot take its own piece.
          */
            Boolean intheway = false;
            if(((landingX < 0) || (landingX > 7))||((landingY < 0)||(landingY > 7))){
              validMove = false;
            }
            else if(!pieceMove(landingX, landingY)){
              validMove = false;
            }
            else{
              /*
                The following condition simply asks if we have either a movement in the x direction
                or in the y direction.
              */
              if(((xMovement!=0)&&(yMovement == 0))||((xMovement==0)&&(yMovement!=0)))
              {
                if(xMovement!=0){ // we have a movement in the x direction
                  if(startX-landingX > 0){
                    /*
                        We need to check all the squares up to the landing square to make sure nothing
                        is in the way.
                    */
                    for(int i=0;i < xMovement;i++){
                      if(piecePresent(initialX-(i*75), e.getY())){
                        intheway = true;
                        break;
                      }
                      else{
                        intheway = false;
                      }
                    }
                  }
                  else{ // we have a movement in the y direction
                    for(int i=0;i < xMovement;i++){
                      if(piecePresent(initialX+(i*75), e.getY())){
                        intheway = true;
                        break;
                      }
                      else{
                        intheway = false;
                      }
                    }
                  }
                }
                else{ // we must have a movement in the y dirction
                  if(startY-landingY > 0){
                    for(int i=0;i < yMovement;i++){
                      if(piecePresent(e.getX(),initialY-(i*75))){
                        intheway = true;
                        break;
                      }
                      else{
                        intheway = false;
                      }
                    }
                  }
                  else{
                    for(int i=0;i < yMovement;i++){
                      if(piecePresent(e.getX(),initialY+(i*75))){
                        intheway = true;
                        break;
                      }
                      else{
                        intheway = false;
                      }
                    }
                  }
                }
                if(intheway){
                  /*
                      if we have found that there is something in the way to the square we are trying to
                      move to we simply set the validMove boolean to false...
                  */
                  validMove = false;
                }
                else{
                  /*
                      okay so we have established that there is nothing in the way and we want to make try and
                      move the piece. The other condiion that we need to check is to see if there is a piece at
                      the landing square that we want to move to.
                  */
                  if(piecePresent(e.getX(), (e.getY()))){
                    if(pieceName.contains("White")){
                      if(checkWhiteOponent(e.getX(), e.getY())){
                        validMove = true;
                        /*
                          Every time we make a take a piece we need to check if the piece that has been captured
                          is a King.
                        */

                        if(getPieceName(e.getX(), e.getY()).contains("King")){
                          winner = "White Wins!";
                        }
                      }
                      else{
                        validMove = false;
                      }
                    }
                    else{
                      if(checkBlackOponent(e.getX(), e.getY())){
                        validMove = true;
                        if(getPieceName(e.getX(), e.getY()).contains("King")){
                          winner = "Black Wins!";
                        }
                      }
                      else{
                        validMove = false;
                      }
                    }
                  }
                  else{
                    validMove = true;
                  }
                }
            }
            else{
              validMove = false;
            }
          }
        }
        else if(pieceName.contains("Knight")){

          /*
              We know that the Knight can move in an L direction. It means that if the xMovement == 1 then the yMovement
              must equal 2 and also the other way around...
              We need to check the square that we are moving to and make sure that if there is a piece present
              that its not our own piece...
          */
          if(((xMovement == 1)&&(yMovement ==2))||((xMovement==2)&&(yMovement==1))){
            if(!piecePresent(e.getX(), e.getY())){
              validMove = true;
            }
            else{
              if(pieceName.contains("White")){
                if(checkWhiteOponent(e.getX(), e.getY())){
                  validMove = true;
                  if(getPieceName(e.getX(), e.getY()).contains("King")){
                    winner = "White Wins!";
                  }
                }
              }
              else{
                if(checkBlackOponent(e.getX(), e.getY())){
                  validMove = true;
                  if(getPieceName(e.getX(), e.getY()).contains("King")){
                    winner = "Black Wins!";
                  }
                }
              }
            }
          }
        }
        else if(pieceName.contains("King")){
          if((xMovement == 0) && (yMovement==0)){
                  validMove = false;
          }
          else{
            if(((landingX < 0) || (landingX > 7))||((landingY < 0)||(landingY > 7))){
              validMove = false;
            }
            else{
              if((xMovement > 1) || (yMovement > 1)){
                validMove = false;
              }
              else{
                /*
                    The Kind can only move one square at a time but also needs to make sure that there is at least one square between
                    both Kings once the move has concluded.
                */
                if((getPieceName((e.getX()+75), e.getY()).contains("King"))||(getPieceName((e.getX()-75), e.getY()).contains("King"))||(getPieceName((e.getX()),(e.getY()+75)).contains("King"))||(getPieceName((e.getX()), (e.getY()-75)).contains("King"))||(getPieceName((e.getX()+75),(e.getY()+75)).contains("King"))||(getPieceName((e.getX()-75),(e.getY()+75)).contains("King"))||(getPieceName((e.getX()+75),(e.getY()-75)).contains("King"))||(getPieceName((e.getX()-75), (e.getY()-75)).contains("King"))){
                        validMove = false;
                }
                else{
                  /*
                      We need to make sure that if there is a piece on the potential landing square that the King cannot takes its
                      own piece but can take an opponents piece.
                  */
                  if(piecePresent(e.getX(), e.getY())){
                          if(pieceName.contains("White")){
                            if(checkWhiteOponent(e.getX(), e.getY())){
                              validMove = true;
                            }
                          }
                          else{
                            if(checkBlackOponent(e.getX(), e.getY())){
                              validMove = true;
                            }
                          }
                        }
                        else{
                          validMove = true;
                        }
                      }
                    }
                  }
          }//end of zero distance check
        }
        else if(pieceName.contains("Queen")){
          boolean inTheWay = false;
          if(((landingX < 0) || (landingX > 7))||((landingY < 0)||(landingY > 7))){
            validMove = false;
          }
          else if(!pieceMove(landingX, landingY)){
            validMove = false;
          }
          else{
		          /*
			           The Queen can either move like the Rook or a Bishop...You already have this code working!

                 The first check is to see if the Queen is making a valid Bishop Like move...
		          */
              if(xMovement == yMovement){
                if((startX-landingX < 0)&&(startY-landingY < 0)){
                  for(int i=0; i < xMovement;i++){
                    if(piecePresent((initialX+(i*75)), (initialY+(i*75)))){
                      inTheWay = true;
                    }
                  }
                }
                else if((startX-landingX < 0)&&(startY-landingY > 0)){
                  for(int i=0; i < xMovement;i++){
                    if(piecePresent((initialX+(i*75)), (initialY-(i*75)))){
                      inTheWay = true;
                    }
                  }
                }
                else if((startX-landingX > 0)&&(startY-landingY > 0)){
                  for(int i=0; i < xMovement;i++){
                    if(piecePresent((initialX-(i*75)), (initialY-(i*75)))){
                      inTheWay = true;
                    }
                  }
                }
                else if((startX-landingX > 0)&&(startY-landingY < 0)){
                  for(int i=0; i < xMovement;i++){
                    if(piecePresent((initialX-(i*75)), (initialY+(i*75)))){
                      inTheWay = true;
                    }
                  }
                }

                if(inTheWay){
                  validMove = false;
                }
                else{
                  /*
                    If there is nothing in the way when moving the Queen like a Bishop, we have two cases:
                      - there is a piece on the square we are trying to move to and we need to make sure that its
                        an enemy piece
                      - or there is nothing in the way and everything is cool
                  */
                  if(piecePresent(e.getX(), (e.getY()))){
                    if(pieceName.contains("White")){
                      if(checkWhiteOponent(e.getX(), e.getY())){
                        validMove = true;
                        if(getPieceName(e.getX(), e.getY()).contains("King")){
                          winner = "White Wins!";
                        }
                      }
                    }
                    else{ // We are moving the Black Queen...
                      if(checkBlackOponent(e.getX(), e.getY())){
                        validMove = true;
                        if(getPieceName(e.getX(), e.getY()).contains("King")){
                          winner = "Black Wins!";
                        }
                      }
                    }
                  }
                  else{
                    validMove = true;
                  }
                }
              } // end of the condition where the Queen is moving like a Bishop
              else if(((xMovement!=0)&&(yMovement == 0))|| ((xMovement==0)&&(yMovement!=0))){
                /*
                    This is the condition that handles the Queen movements that are moving like a Rook
                */
                if(xMovement!=0){
                  //we have movement along the x axis
                  if(startX-landingX > 0){
                    //movement in the left direction....
                    for(int i=0;i < xMovement;i++){
                      if(piecePresent(initialX-(i*75), e.getY())){
                        inTheWay = true;
                        break;
                      }
                      else{
                        inTheWay = false;
                      }
                    }
                  }
                  else{
                    for(int i=0;i < xMovement;i++){
                      if(piecePresent(initialX+(i*75), e.getY())){
                        inTheWay = true;
                        break;
                      }
                      else{
                        inTheWay = false;
                      }
                    }
                  }
                }// This is the end of the xMovement condition
                else{
                  //we have movement along the y axis
                  if(startY-landingY > 0){
                    //movement in the left direction....
                    for(int i=0;i < yMovement;i++){
                      if(piecePresent(e.getX(),initialY-(i*75))){
                        inTheWay = true;
                        break;
                      }
                      else{
                        inTheWay = false;
                      }
                    }
                  }
                  else{
                    for(int i=0;i < yMovement;i++){
                      if(piecePresent(e.getX(),initialY+(i*75))){
                        inTheWay = true;
                        break;
                      }
                      else{
                        inTheWay = false;
                      }
                    }
                  }
                }

                if(inTheWay){
                  validMove = false;
                }
                else{
                  
                  if(piecePresent(e.getX(), (e.getY()))){
                    if(pieceName.contains("White")){
                      if(checkWhiteOponent(e.getX(), e.getY())){
                        validMove = true;
                        if(getPieceName(e.getX(), e.getY()).contains("King")){
                          winner = "White Wins!";
                        }
                      }
                    }
                    else{
                      if(checkBlackOponent(e.getX(), e.getY())){
                        validMove = true;
                        if(getPieceName(e.getX(), e.getY()).contains("King")){
                          winner = "Black Wins!";
                        }
                      }
                    }
                  }
                  else{
                    validMove = true;
                  }
                }
              }
            }
        }
        else if(pieceName.equals("BlackPawn")){
          if(startY == 6){ // This is were the pawn is making its first move....
            /*
            if the pawn is makeing it's first movement....
            the pawn can either move one square or two squares...in the Y direction
            as long as we are moving up the board!!! and also there is no movement in the
            x direction
            */
            if(((yMovement==1)||(yMovement == 2))&&(startY > landingY)&&(xMovement ==0)){
              if(yMovement == 2){
                if((!piecePresent(e.getX(), e.getY()))&&(!piecePresent(e.getX(), (e.getY()+75)))){
                  validMove = true;
                }
              }
              else{
                if(!piecePresent(e.getX(), e.getY())){
                  validMove = true;
                }
              }
            }
            else if((yMovement == 1)&&(startY > landingY)&&(xMovement == 1)){
              if(piecePresent(e.getX(), e.getY())){
                if(checkBlackOponent(e.getX(), e.getY())){
                  validMove = true;
                  if(getPieceName(e.getX(), e.getY()).contains("King")){
                    winner = "Black Wins!";
                  }
                }
              }
            }
          }
          else{// This is were the pawn is making all subsequent moves...
            if(((yMovement==1))&&(startY > landingY)&&(xMovement ==0)){
              if(!piecePresent(e.getX(), e.getY())){
                validMove = true;
                /*
                  We need a variable to inform us that the Black Pawn has reached the top
                  of the board and can now trun into a Black Queen
                */
                if(landingY == 0){
                  progression = true;
                }
              }
            }
            else if((yMovement == 1)&&(startY > landingY)&&(xMovement == 1)){
              if(piecePresent(e.getX(), e.getY())){
                if(checkBlackOponent(e.getX(), e.getY())){
                  validMove = true;
                  if(landingY == 0){
                    progression = true;
                  }
                  if(getPieceName(e.getX(), e.getY()).contains("King")){
                    winner = "Black Wins!";
                  }
                }
              }
            }
          }
        }
        else if(pieceName.equals("WhitePawn")){
			       if(startY == 1){
               if(((xMovement == 0))&&((yMovement==1)||((yMovement)==2))){
                 if(yMovement==2){
                   if((!piecePresent(e.getX(), (e.getY())))&&(!piecePresent(e.getX(), (e.getY()-75)))){
                     validMove = true;
                   }
                 }
                 else{
                   if((!piecePresent(e.getX(), (e.getY())))){
                     validMove = true;
                   }
                 }
               }
               else if((piecePresent(e.getX(), e.getY()))&&(xMovement == yMovement)&&(xMovement == 1)&&(startY < landingY)){
                 /*
                    This is the condition were we are checking for a move to take a piece when the white pawn is making its
                    first move. We check the following:
                      - that there is a piece present,
                      - we are moving in a diagonal,
                      - the movement is only one square,
                      - we are moving down the board
                 */
                 if(checkWhiteOponent(e.getX(), e.getY())){
                   validMove = true;
                   if(startY == 6){
                     success = true;
                   }
                   if(getPieceName(e.getX(), e.getY()).contains("King")){
                     winner = "White Wins!";
                   }
                 }
               }
             } // End of the movements for when the pawn is making its first move.
             else{
               if((startX-1 >= 0)||(startX +1 <=7)){
                 if((piecePresent(e.getX(), e.getY()))&&(xMovement == yMovement)&&(xMovement == 1)){
                   if(checkWhiteOponent(e.getX(), e.getY())){
                     validMove = true;
                     if(startY == 6){
                       success = true;
                     }
                     if(getPieceName(e.getX(), e.getY()).contains("King")){
                       winner = "White Wins!";
                     }
                   }
                 }// checking the case for when there is a piecePresent
                 else{
                   if(!piecePresent(e.getX(), (e.getY()))){
                     if(((xMovement == 0))&&((e.getY()/75)-startY)==1){
                       if(startY == 6){
                         success = true;
                       }
                       validMove = true;
                     }
                   }
                 }
               }
             }
           }
    } //end of possible.

    if(!validMove){
      int location=0;
      if(startY ==0){
        location = startX;
      }
      else{
        location  = (startY*8)+startX;
      }
      String pieceLocation = pieceName+".png";
      pieces = new JLabel( new ImageIcon(pieceLocation) );
      panels = (JPanel)chessBoard.getComponent(location);
      panels.add(pieces);
		}
		else{ // this is the condition where we have a valid move and need to visually show it.
      if(white2Move){
        white2Move = false;
      }
      else{
        white2Move = true;
      }

      if(progression){
        int location = 0 + (e.getX()/75);
				if (c instanceof JLabel){
	        Container parent = c.getParent();
          parent.remove(0);
					pieces = new JLabel( new ImageIcon("BlackQueen.png") );
					parent = (JPanel)chessBoard.getComponent(location);
			    parent.add(pieces);
				}
        if(winner !=null){
          JOptionPane.showMessageDialog(null, winner);
          System.exit(0);
        }
      }
			else if(success){
				int location = 56 + (e.getX()/75);
				if (c instanceof JLabel){
          Container parent = c.getParent();
          parent.remove(0);
					pieces = new JLabel( new ImageIcon("WhiteQueen.png") );
					parent = (JPanel)chessBoard.getComponent(location);
          parent.add(pieces);
				}
				else{
					Container parent = (Container)c;
          pieces = new JLabel( new ImageIcon("WhiteQueen.png") );
					parent = (JPanel)chessBoard.getComponent(location);
          parent.add(pieces);
				}
			}
			else{
				if (c instanceof JLabel){
          Container parent = c.getParent();
          parent.remove(0);
          parent.add( chessPiece );
        }
        else {
          Container parent = (Container)c;
          parent.add( chessPiece );
        }
        chessPiece.setVisible(true);
        if(winner !=null){
          JOptionPane.showMessageDialog(null, winner);
          System.exit(0);
        }
			}
      makeAIMove();
		} // end of the else condition
  }// end of the mouseReleased event.

    public void mouseClicked(MouseEvent e) {

    }
    public void mouseMoved(MouseEvent e) {
   }
    public void mouseEntered(MouseEvent e){

    }
    public void mouseExited(MouseEvent e) {

    }

    public void startGame(){
      System.out.println("Let the game begin");
    }
	/*
		Main method that gets the ball moving.
	*/
    public static void main(String[] args) {
        ChessProject frame = new ChessProject();
        frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE );
        frame.pack();
        frame.setResizable(true);
        frame.setLocationRelativeTo( null );
        frame.setVisible(true);
        Object[] options = {"Random Moves","Best Next Move","Based on Opponents Moves"};
        int n = JOptionPane.showOptionDialog(frame,"Lets play some Chess, choose your AI opponent","Introduction to AI Continuous Assessment", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,null,options,options[2]);
        System.out.println("The selected variable is : "+n);
        frame.makeAIMove();
  }
}
