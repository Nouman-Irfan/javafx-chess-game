import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
 * =========================================================
 *  CHESS GAME – JavaFX/GUI
 *  Original Authors: Muhammad Nouman 
 * =========================================================
 *
 *  All original rules faithfully preserved:
 *   ✔ All piece movement rules (P/R/N/B/Q/K)
 *   ✔ Path-clear validation for sliders
 *   ✔ Cannot capture own piece
 *   ✔ Cannot leave own king in check
 *   ✔ Check detection & announcement
 *   ✔ Checkmate detection
 *   ✔ Stalemate detection
 *   ✔ Castling – Kingside & Queenside (with all 3 safety checks)
 *   ✔ Pawn Promotion popup (Q / R / B / N)
 *   ✔ Legal-move highlights (Yellow=selected, Green=move,
 *                             Red=capture, Blue=castling)
 *   ✔ Undo  (Z key or button)
 *   ✔ Redo  (Y key or button)
 *   ✔ Resign button
 *   ✔ white_wins / black_wins / draw images shown on game-end
 *   ✔ Assets loaded from  src/assets/  (same folder as source)
 */
public class Main extends Application
{

    // ─── Board / tile size (matches SFML version: 1500px → 8 tiles)
    //     We use 800px for a crisp modern display
    private static final int BOARD_SIZE = 700;
    private static final int TILE       = BOARD_SIZE / 8;   // 100 px

    // ─── Board (1-D array, uppercase=White, lowercase=Black, '.'=empty)
    private final char[] board = new char[64];

    // ─── Game-state flags
    private boolean whiteTurn   = true;
    private boolean gameOver    = false;

    // Castling-eligibility flags (exact mirror of C++ booleans)
    private boolean wKingMoved  = false,  bKingMoved  = false;
    private boolean wRookAMoved = false,  wRookHMoved = false;
    private boolean bRookAMoved = false,  bRookHMoved = false;

    // Selection state
    private int            selectedX       = -1, selectedY = -1;
    private List<MoveSq>   legalHighlights = new ArrayList<>();

    // Pawn-promotion state
    private boolean promotionActive = false;
    private int     promoRow = -1,  promoCol = -1;
    private boolean promoWhite      = true;

    // Result display
    private boolean whiteWon    = false;
    private boolean isStalemate = false;
    private boolean showResult  = false;

    // Undo / Redo stacks
    private final List<GameState> undoStack = new ArrayList<>();
    private final List<GameState> redoStack = new ArrayList<>();

    // ─── JavaFX drawing
    private Canvas           canvas;
    private GraphicsContext  gc;
    private Label            statusLabel;

    // ─── Image assets  (loaded once at startup)
    private Image imgBoard;
    // pieces[0..11] → wP wR wN wB wQ wK  bP bR bN bB bQ bK
    private final Image[] imgPieces = new Image[12];
    private Image imgWhiteWins, imgBlackWins, imgDraw;

    // ========================================================
    //  Application entry-point
    // ========================================================
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage stage) {
        loadAssets();
        initBoard();

        // ── Root layout ──────────────────────────────────────
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1a1a2e;");

        root.setTop(buildTopBar());
        root.setBottom(buildBottomBar());

        canvas = new Canvas(BOARD_SIZE, BOARD_SIZE);
        gc     = canvas.getGraphicsContext2D();
        canvas.setCursor(Cursor.HAND);
        canvas.setOnMouseClicked(this::handleMouseClick);

        StackPane boardWrap = new StackPane(canvas);
        boardWrap.setPadding(new Insets(8));
        root.setCenter(boardWrap);

        drawAll();

        Scene scene = new Scene(root);
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.Z) doUndo();
            if (e.getCode() == KeyCode.Y) doRedo();
        });

        stage.setTitle("Chess  [Z = Undo | Y = Redo]");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        // Give key events to the scene (canvas doesn't grab focus by default)
        canvas.setFocusTraversable(true);
        canvas.requestFocus();
    }

    // --------------------------------------------------------
    //  Asset loading  –  from src/assets/ (classpath root)
    // --------------------------------------------------------
    private void loadAssets() {
        imgBoard      = load("/assets/board.png");
        imgPieces[0]  = load("/assets/pieces/wp.png");
        imgPieces[1]  = load("/assets/pieces/wr.png");
        imgPieces[2]  = load("/assets/pieces/wn.png");
        imgPieces[3]  = load("/assets/pieces/wb.png");
        imgPieces[4]  = load("/assets/pieces/wq.png");
        imgPieces[5]  = load("/assets/pieces/wk.png");
        imgPieces[6]  = load("/assets/pieces/bp.png");
        imgPieces[7]  = load("/assets/pieces/br.png");
        imgPieces[8]  = load("/assets/pieces/bn.png");
        imgPieces[9]  = load("/assets/pieces/bb.png");
        imgPieces[10] = load("/assets/pieces/bq.png");
        imgPieces[11] = load("/assets/pieces/bk.png");
        imgWhiteWins  = load("/assets/game_end/white_wins.png");
        imgBlackWins  = load("/assets/game_end/black_wins.png");
        imgDraw       = load("/assets/game_end/draw.png");
    }

    /** Load an image from the classpath; returns null on failure (handled at draw-time). */
    private Image load(String path) {
        try {
            var url = getClass().getResource(path);
            if (url == null) { System.err.println("Asset not found: " + path); return null; }
            return new Image(url.toExternalForm());
        } catch (Exception ex) {
            System.err.println("Failed to load: " + path + " – " + ex.getMessage());
            return null;
        }
    }

    // --------------------------------------------------------
    //  UI construction
    // --------------------------------------------------------
    private HBox buildTopBar() {
        HBox bar = new HBox(10);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(10, 16, 10, 16));
        bar.setStyle("-fx-background-color: #16213e; -fx-border-color: #0f3460; -fx-border-width: 0 0 2 0;");

        Label title = new Label("♟  CHESS");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#e2b96f"));

        Label credits = new Label("  —  Muhammad Nouman");
        credits.setFont(Font.font("Georgia", 11));
        credits.setTextFill(Color.web("#7777aa"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnUndo   = styledBtn("↩  Undo (Z)",  "#1e3a5f", "#89d4fe");
        Button btnRedo   = styledBtn("↪  Redo (Y)",  "#1e3a5f", "#89d4fe");
        Button btnResign = styledBtn("🏳  Resign",    "#5f1e1e", "#ff8888");
        Button btnNew    = styledBtn("⟳  New Game",  "#1e5f2e", "#88ffaa");

        btnUndo  .setOnAction(e -> doUndo());
        btnRedo  .setOnAction(e -> doRedo());
        btnResign.setOnAction(e -> doResign());
        btnNew   .setOnAction(e -> newGame());

        bar.getChildren().addAll(title, credits, spacer, btnNew, btnUndo, btnRedo, btnResign);
        return bar;
    }

    private HBox buildBottomBar() {
        HBox bar = new HBox();
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(8));
        bar.setStyle("-fx-background-color: #16213e; -fx-border-color: #0f3460; -fx-border-width: 2 0 0 0;");

        statusLabel = new Label("White's turn to move");
        statusLabel.setFont(Font.font("Georgia", FontWeight.BOLD, 15));
        statusLabel.setTextFill(Color.web("#e2b96f"));

        bar.getChildren().add(statusLabel);
        return bar;
    }

    private Button styledBtn(String text, String bg, String fg) {
        Button b = new Button(text);
        b.setStyle(
                "-fx-background-color:" + bg + ";" +
                        "-fx-text-fill:" + fg + ";" +
                        "-fx-font-family:Georgia;" +
                        "-fx-font-size:12px;" +
                        "-fx-padding:6 14 6 14;" +
                        "-fx-background-radius:6;" +
                        "-fx-cursor:hand;"
        );
        b.setOnMouseEntered(e -> b.setOpacity(0.75));
        b.setOnMouseExited (e -> b.setOpacity(1.00));
        return b;
    }

    // --------------------------------------------------------
    //  Board initialisation  (identical to C++ char[] initial)
    // --------------------------------------------------------
    private void initBoard() {
        char[] init = {
                'r','n','b','q','k','b','n','r',
                'p','p','p','p','p','p','p','p',
                '.','.','.','.','.','.','.','.',
                '.','.','.','.','.','.','.','.',
                '.','.','.','.','.','.','.','.',
                '.','.','.','.','.','.','.','.',
                'P','P','P','P','P','P','P','P',
                'R','N','B','Q','K','B','N','R'
        };
        System.arraycopy(init, 0, board, 0, 64);
    }

    private void newGame() {
        initBoard();
        whiteTurn   = true;  gameOver  = false;
        wKingMoved  = false; bKingMoved  = false;
        wRookAMoved = false; wRookHMoved = false;
        bRookAMoved = false; bRookHMoved = false;
        selectedX   = -1;    selectedY   = -1;
        legalHighlights.clear();
        promotionActive = false;
        showResult  = false; isStalemate = false; whiteWon = false;
        undoStack.clear(); redoStack.clear();
        drawAll();
    }

    // ========================================================
    //  DRAWING  (full image-based rendering matching SFML)
    // ========================================================
    private void drawAll() {
        drawBoard();          // board.png stretched over canvas
        drawHighlights();     // selection + legal-move overlays
        drawPieces();         // 12 piece PNGs
        if (showResult)       drawResultOverlay();   // white_wins / black_wins / draw
        if (promotionActive)  drawPromotionPopup();  // 4-piece promo menu
        updateStatusBar();
    }

    /** Draw board.png scaled to BOARD_SIZE × BOARD_SIZE */
    private void drawBoard() {
        if (imgBoard != null) {
            gc.drawImage(imgBoard, 0, 0, BOARD_SIZE, BOARD_SIZE);
        } else {
            // Fallback: plain coloured squares
            for (int r = 0; r < 8; r++)
                for (int c = 0; c < 8; c++) {
                    gc.setFill((r + c) % 2 == 0 ? Color.web("#f0d9b5") : Color.web("#b58863"));
                    gc.fillRect(c * TILE, r * TILE, TILE, TILE);
                }
        }
    }

    /**
     * Draw coloured semi-transparent rectangles exactly as SFML does:
     *   • Selected square  → yellow  (255,255,0,100)
     *   • Castling target  → blue    (0,100,255,130)
     *   • Capture target   → red     (255,0,0,110)
     *   • Normal move      → green   (0,255,0,90)
     */
    private void drawHighlights() {
        if (selectedX == -1) return;

        // Selected square
        gc.setFill(Color.rgb(255, 255, 0, 100.0 / 255.0));
        gc.fillRect(selectedX * TILE, selectedY * TILE, TILE, TILE);

        char selPiece = board[selectedY * 8 + selectedX];

        for (MoveSq m : legalHighlights) {
            boolean isCastling = (selPiece == 'K' || selPiece == 'k')
                    && Math.abs(m.c - selectedX) == 2;
            if (isCastling) {
                gc.setFill(Color.rgb(0, 100, 255, 130.0 / 255.0));
            } else if (m.capture) {
                gc.setFill(Color.rgb(255, 0, 0, 110.0 / 255.0));
            } else {
                gc.setFill(Color.rgb(0, 255, 0, 90.0 / 255.0));
            }
            gc.fillRect(m.c * TILE, m.r * TILE, TILE, TILE);
        }
    }

    /**
     * Draw all 12 piece images, scaled to TILE × TILE,
     * matching SFML's setScale + setPosition logic.
     */
    private void drawPieces() {
        for (int i = 0; i < 64; i++) {
            char p = board[i];
            if (p == '.') continue;
            int row = i / 8, col = i % 8;
            int idx = pieceIndex(p);
            if (idx < 0 || imgPieces[idx] == null) continue;
            gc.drawImage(imgPieces[idx],
                    col * TILE, row * TILE,
                    TILE, TILE);
        }
    }

    /**
     * Map piece char → imgPieces index  (identical to C++ if-chain):
     *  P=0 R=1 N=2 B=3 Q=4 K=5   p=6 r=7 n=8 b=9 q=10 k=11
     */
    private int pieceIndex(char p) {
        return switch (p) {
            case 'P' -> 0; case 'R' -> 1; case 'N' -> 2;
            case 'B' -> 3; case 'Q' -> 4; case 'K' -> 5;
            case 'p' -> 6; case 'r' -> 7; case 'n' -> 8;
            case 'b' -> 9; case 'q' -> 10; case 'k' -> 11;
            default  -> -1;
        };
    }

    /**
     * Show white_wins.png / black_wins.png / draw.png
     * stretched over the board (matching SFML setScale / setPosition logic).
     */
    private void drawResultOverlay() {
        Image img = isStalemate ? imgDraw : (whiteWon ? imgWhiteWins : imgBlackWins);
        if (img != null) {
            // C++ uses extraX=1.5, extraY=1.0 and shifts left by (extraX-1)/2*BOARD_SIZE
            // In Java we simply stretch the image over the whole board for simplicity,
            // keeping the same visual intent (full-screen result banner).
            gc.drawImage(img, 0, 0, BOARD_SIZE, BOARD_SIZE);
        } else {
            // Fallback text panel when images aren't available
            gc.setFill(Color.rgb(0, 0, 0, 0.72));
            gc.fillRect(0, 0, BOARD_SIZE, BOARD_SIZE);
            double pw = 460, ph = 160;
            double px = (BOARD_SIZE - pw) / 2.0, py = (BOARD_SIZE - ph) / 2.0;
            gc.setFill(Color.rgb(22, 33, 62));
            gc.fillRoundRect(px, py, pw, ph, 18, 18);
            gc.setStroke(Color.web("#e2b96f")); gc.setLineWidth(2.5);
            gc.strokeRoundRect(px, py, pw, ph, 18, 18);
            gc.setFont(Font.font("Georgia", FontWeight.BOLD, 30));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setFill(Color.web("#e2b96f"));
            String msg = isStalemate ? "STALEMATE – DRAW"
                    : (whiteWon ? "WHITE WINS!" : "BLACK WINS!");
            gc.fillText(msg, BOARD_SIZE / 2.0, py + 68);
            gc.setFont(Font.font("Georgia", 15));
            gc.setFill(Color.web("#aaaacc"));
            gc.fillText(isStalemate ? "No legal moves · Draw"
                    : "Checkmate!", BOARD_SIZE / 2.0, py + 110);
            gc.setTextAlign(TextAlignment.LEFT);
        }
    }

    /**
     * Pawn-promotion popup – exactly as in SFML:
     *   4 boxes centred on the board, each showing a piece image.
     */
    private void drawPromotionPopup() {
        // Dim overlay
        gc.setFill(Color.rgb(0, 0, 0, 160.0 / 255.0));
        gc.fillRect(0, 0, BOARD_SIZE, BOARD_SIZE);

        int boxSize = TILE;
        int startX  = BOARD_SIZE / 2 - 2 * boxSize;
        int startY  = BOARD_SIZE / 2 - boxSize / 2;

        // Choices match C++ order: Q R B N
        char[] choices = promoWhite
                ? new char[]{'Q', 'R', 'B', 'N'}
                : new char[]{'q', 'r', 'b', 'n'};

        // Label above boxes
        gc.setFont(Font.font("Georgia", FontWeight.BOLD, 16));
        gc.setFill(Color.web("#e2b96f"));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("Choose promotion piece", BOARD_SIZE / 2.0, startY - 14);
        gc.setTextAlign(TextAlignment.LEFT);

        for (int i = 0; i < 4; i++) {
            int bx = startX + i * boxSize;

            // White semi-transparent box (matches SFML Color(255,255,255,200))
            gc.setFill(Color.rgb(255, 255, 255, 200.0 / 255.0));
            gc.fillRect(bx, startY, boxSize, boxSize);

            // Piece image inside the box
            int idx = pieceIndex(choices[i]);
            if (idx >= 0 && imgPieces[idx] != null) {
                gc.drawImage(imgPieces[idx], bx, startY, boxSize, boxSize);
            }
        }
    }

    private void updateStatusBar() {
        if (showResult) {
            String msg = isStalemate ? "Stalemate – Draw!"
                    : (whiteWon ? "White wins by Checkmate!" : "Black wins by Checkmate!");
            statusLabel.setText("Game Over  ·  " + msg + "  ·  Press Z to undo or ⟳ for new game");
            statusLabel.setTextFill(Color.web("#ff9944"));
            return;
        }
        if (promotionActive) {
            statusLabel.setText("Pawn promotion – click a piece to promote");
            statusLabel.setTextFill(Color.web("#89d4fe"));
            return;
        }
        boolean inCheck = isKingInCheck(board, whiteTurn);
        String side = whiteTurn ? "White" : "Black";
        if (inCheck) {
            statusLabel.setText(side + " is in CHECK!  Must escape.");
            statusLabel.setTextFill(Color.web("#ff4444"));
        } else {
            statusLabel.setText(side + "'s turn to move");
            statusLabel.setTextFill(Color.web("#e2b96f"));
        }
    }

    // ========================================================
    //  MOUSE CLICK HANDLER  (faithful port of SFML event loop)
    // ========================================================
    private void handleMouseClick(MouseEvent e) {
        if (gameOver) return;

        int mx = (int) e.getX();
        int my = (int) e.getY();

        // ── Promotion popup takes priority ────────────────────
        if (promotionActive) {
            int boxSize = TILE;
            int startX  = BOARD_SIZE / 2 - 2 * boxSize;
            int startY  = BOARD_SIZE / 2 - boxSize / 2;

            for (int i = 0; i < 4; i++) {
                int bx = startX + i * boxSize;
                if (mx >= bx && mx <= bx + boxSize
                        && my >= startY && my <= startY + boxSize) {

                    char newPiece;
                    if (promoWhite) {
                        newPiece = switch (i) { case 0->'Q'; case 1->'R'; case 2->'B'; default->'N'; };
                    } else {
                        newPiece = switch (i) { case 0->'q'; case 1->'r'; case 2->'b'; default->'n'; };
                    }

                    board[promoRow * 8 + promoCol] = newPiece;
                    promotionActive = false;
                    promoRow = promoCol = -1;

                    // Switch turn then check/checkmate (matches C++ promotion handler)
                    whiteTurn = !whiteTurn;
                    checkGameEnd();
                    drawAll();
                    return;
                }
            }
            return; // click outside boxes – ignore
        }

        // ── Convert pixel → grid (col, row) ──────────────────
        int x = mx / TILE;   // col
        int y = my / TILE;   // row
        if (x < 0 || x >= 8 || y < 0 || y >= 8) return;

        // ── STATE 1: No piece selected yet ────────────────────
        if (selectedX == -1 && selectedY == -1) {
            char p = board[y * 8 + x];
            if (p == '.') return;
            // Enforce turn (matches C++ isupper/islower checks)
            if (whiteTurn  && !Character.isUpperCase(p)) return;
            if (!whiteTurn && !Character.isLowerCase(p)) return;

            selectedX = x; selectedY = y;
            legalHighlights = getLegalMovesForSelected(
                    board, selectedX, selectedY, whiteTurn,
                    wKingMoved, bKingMoved,
                    wRookAMoved, wRookHMoved,
                    bRookAMoved, bRookHMoved);
            drawAll();
            return;
        }

        // ── STATE 2: A piece is already selected ──────────────

        // Clicked the same square → deselect
        if (selectedX == x && selectedY == y) {
            selectedX = selectedY = -1;
            legalHighlights.clear();
            drawAll();
            return;
        }

        int sc = selectedX, sr = selectedY;
        int dc = x,         dr = y;

        // Re-select own piece instead of moving
        char clicked = board[y * 8 + x];
        if (whiteTurn  && Character.isUpperCase(clicked)) {
            selectedX = x; selectedY = y;
            legalHighlights = getLegalMovesForSelected(
                    board, selectedX, selectedY, whiteTurn,
                    wKingMoved, bKingMoved,
                    wRookAMoved, wRookHMoved,
                    bRookAMoved, bRookHMoved);
            drawAll(); return;
        }
        if (!whiteTurn && Character.isLowerCase(clicked)) {
            selectedX = x; selectedY = y;
            legalHighlights = getLegalMovesForSelected(
                    board, selectedX, selectedY, whiteTurn,
                    wKingMoved, bKingMoved,
                    wRookAMoved, wRookHMoved,
                    bRookAMoved, bRookHMoved);
            drawAll(); return;
        }

        // Check whether destination is in the pre-computed legal list
        boolean legal = false;
        for (MoveSq m : legalHighlights)
            if (m.r == dr && m.c == dc) { legal = true; break; }

        if (!legal) {
            // Invalid destination – deselect (matches C++ "Invalid Move!" branch)
            selectedX = selectedY = -1;
            legalHighlights.clear();
            drawAll();
            return;
        }

        // ── Execute the move ──────────────────────────────────

        // Save state for undo BEFORE making any change
        undoStack.add(saveState());
        redoStack.clear();

        char piece = board[sr * 8 + sc];
        int  rDiff = dr - sr,  cDiff = dc - sc;

        board[dr * 8 + dc] = piece;
        board[sr * 8 + sc] = '.';

        // Castling: move rook (matches SFML guimain exactly)
        if ((piece == 'K' || piece == 'k') && Math.abs(cDiff) == 2) {
            int row = sr;
            if (cDiff == 2) {                      // kingside
                board[row * 8 + 5] = board[row * 8 + 7];
                board[row * 8 + 7] = '.';
            } else {                               // queenside
                board[row * 8 + 3] = board[row * 8 + 0];
                board[row * 8 + 0] = '.';
            }
        }

        // Update castling flags (matches C++ flag logic)
        if (piece == 'K') wKingMoved = true;
        if (piece == 'k') bKingMoved = true;
        if (piece == 'R') {
            if (sr == 7 && sc == 0) wRookAMoved = true;
            if (sr == 7 && sc == 7) wRookHMoved = true;
        }
        if (piece == 'r') {
            if (sr == 0 && sc == 0) bRookAMoved = true;
            if (sr == 0 && sc == 7) bRookHMoved = true;
        }

        // Pawn promotion trigger (matches C++ condition exactly)
        if ((piece == 'P' && dr == 0) || (piece == 'p' && dr == 7)) {
            promotionActive = true;
            promoRow = dr; promoCol = dc;
            promoWhite = (piece == 'P');
            selectedX = selectedY = -1;
            legalHighlights.clear();
            drawAll();
            return;
        }

        // Switch turn & check game-end (matches C++ post-move logic)
        whiteTurn = !whiteTurn;
        selectedX = selectedY = -1;
        legalHighlights.clear();
        checkGameEnd();
        drawAll();
    }

    /** Check / Checkmate / Stalemate – identical to C++ post-move block */
    private void checkGameEnd() {
        boolean inCheck = isKingInCheck(board, whiteTurn);
        boolean hasMove = hasAnyLegalMove(board, whiteTurn);

        if (!hasMove) {
            gameOver   = true;
            showResult = true;
            if (inCheck) {
                isStalemate = false;
                whiteWon    = !whiteTurn;   // the side that just moved wins
            } else {
                isStalemate = true;
            }
        }
    }

    // ========================================================
    //  Undo / Redo / Resign / New Game
    // ========================================================
    private void doUndo() {
        if (undoStack.isEmpty()) return;
        redoStack.add(saveState());
        loadState(undoStack.remove(undoStack.size() - 1));
        selectedX = selectedY = -1; legalHighlights.clear();
        showResult = false; isStalemate = false;
        promotionActive = false;
        drawAll();
    }

    private void doRedo() {
        if (redoStack.isEmpty()) return;
        undoStack.add(saveState());
        loadState(redoStack.remove(redoStack.size() - 1));
        selectedX = selectedY = -1; legalHighlights.clear();
        showResult = false; isStalemate = false;
        promotionActive = false;
        drawAll();
    }

    private void doResign() {
        if (gameOver) return;
        gameOver    = true;
        showResult  = true;
        isStalemate = false;
        whiteWon    = !whiteTurn;   // opponent wins
        selectedX = selectedY = -1; legalHighlights.clear();
        drawAll();
    }

    // ========================================================
    //  Save / Load Game State  (for undo / redo stack)
    // ========================================================
    private GameState saveState() {
        GameState s = new GameState();
        System.arraycopy(board, 0, s.board, 0, 64);
        s.whiteTurn   = whiteTurn;
        s.wKingMoved  = wKingMoved;   s.bKingMoved  = bKingMoved;
        s.wRookAMoved = wRookAMoved;  s.wRookHMoved = wRookHMoved;
        s.bRookAMoved = bRookAMoved;  s.bRookHMoved = bRookHMoved;
        s.gameOver    = gameOver;
        return s;
    }

    private void loadState(GameState s) {
        System.arraycopy(s.board, 0, board, 0, 64);
        whiteTurn   = s.whiteTurn;
        wKingMoved  = s.wKingMoved;   bKingMoved  = s.bKingMoved;
        wRookAMoved = s.wRookAMoved;  wRookHMoved = s.wRookHMoved;
        bRookAMoved = s.bRookAMoved;  bRookHMoved = s.bRookHMoved;
        gameOver    = s.gameOver;
    }

    // ========================================================
    //  CHESS LOGIC  –  direct 1-to-1 port from C++ functions
    // ========================================================

    // ── isPathClear ─────────────────────────────────────────
    static boolean isPathClear(char[] board, int sc, int sr, int dc, int dr) {
        int stepR = Integer.compare(dr - sr, 0);
        int stepC = Integer.compare(dc - sc, 0);
        int r = sr + stepR, c = sc + stepC;
        while (r != dr || c != dc) {
            if (board[r * 8 + c] != '.') return false;
            r += stepR; c += stepC;
        }
        return true;
    }

    // ── isSquareAttacked ────────────────────────────────────
    static boolean isSquareAttacked(char[] board, int tr, int tc, boolean byWhite) {
        for (int i = 0; i < 64; i++) {
            char a = board[i];
            if (a == '.') continue;
            if ( byWhite && !Character.isUpperCase(a)) continue;
            if (!byWhite && !Character.isLowerCase(a)) continue;
            int r = i / 8, c = i % 8;
            int dr = tr - r, dc = tc - c;
            if (a == 'P' && dr == -1 && Math.abs(dc) == 1) return true;
            if (a == 'p' && dr ==  1 && Math.abs(dc) == 1) return true;
            if ((a=='N'||a=='n') &&
                    ((Math.abs(dr)==2&&Math.abs(dc)==1)||(Math.abs(dr)==1&&Math.abs(dc)==2))) return true;
            if ((a=='K'||a=='k') && Math.abs(dr)<=1 && Math.abs(dc)<=1) return true;
            if (a=='R'||a=='r'||a=='B'||a=='b'||a=='Q'||a=='q') {
                if ((a=='R'||a=='r') && !(dr==0||dc==0)) continue;
                if ((a=='B'||a=='b') && Math.abs(dr)!=Math.abs(dc)) continue;
                if ((a=='Q'||a=='q') && !((dr==0||dc==0)||(Math.abs(dr)==Math.abs(dc)))) continue;
                if (isPathClear(board, c, r, tc, tr)) return true;
            }
        }
        return false;
    }

    // ── isKingInCheck ───────────────────────────────────────
    static boolean isKingInCheck(char[] board, boolean checkingWhite) {
        int ki = -1;
        char king = checkingWhite ? 'K' : 'k';
        for (int i = 0; i < 64; i++) if (board[i] == king) { ki = i; break; }
        if (ki == -1) return false;
        int kr = ki / 8, kc = ki % 8;
        for (int i = 0; i < 64; i++) {
            char a = board[i];
            if (a == '.') continue;
            if ( checkingWhite && !Character.isLowerCase(a)) continue;
            if (!checkingWhite && !Character.isUpperCase(a)) continue;
            int r = i / 8, c = i % 8;
            int dr = kr - r, dc = kc - c;
            if (a=='p' && dr== 1 && Math.abs(dc)==1) return true;
            if (a=='P' && dr==-1 && Math.abs(dc)==1) return true;
            if ((a=='n'||a=='N') &&
                    ((Math.abs(dr)==2&&Math.abs(dc)==1)||(Math.abs(dr)==1&&Math.abs(dc)==2))) return true;
            if ((a=='k'||a=='K') && Math.abs(dr)<=1 && Math.abs(dc)<=1) return true;
            if (a=='r'||a=='R'||a=='b'||a=='B'||a=='q'||a=='Q') {
                if ((a=='r'||a=='R') && !(dr==0||dc==0)) continue;
                if ((a=='b'||a=='B') && Math.abs(dr)!=Math.abs(dc)) continue;
                if ((a=='q'||a=='Q') && !((dr==0||dc==0)||(Math.abs(dr)==Math.abs(dc)))) continue;
                if (isPathClear(board, c, r, kc, kr)) return true;
            }
        }
        return false;
    }

    // ── isPseudoLegalMove ───────────────────────────────────
    static boolean isPseudoLegalMove(char[] board, int sc, int sr, int dc, int dr, boolean white) {
        char piece  = board[sr * 8 + sc];
        char target = board[dr * 8 + dc];
        if (piece == '.') return false;
        if ( white && !Character.isUpperCase(piece)) return false;
        if (!white && !Character.isLowerCase(piece)) return false;
        if (sc < 0||sc > 7||dc < 0||dc > 7||sr < 0||sr > 7||dr < 0||dr > 7) return false;
        if (sc == dc && sr == dr) return false;
        if (target != '.') {
            if ( white && Character.isUpperCase(target)) return false;
            if (!white && Character.isLowerCase(target)) return false;
        }
        int rDiff = dr - sr, cDiff = dc - sc;
        if (piece == 'P') {
            if (cDiff == 0 && rDiff == -1 && target == '.') return true;
            if (sr == 6 && cDiff == 0 && rDiff == -2 && target == '.' && board[(sr-1)*8+sc] == '.') return true;
            if (Math.abs(cDiff) == 1 && rDiff == -1 && Character.isLowerCase(target)) return true;
            return false;
        }
        if (piece == 'p') {
            if (cDiff == 0 && rDiff == 1 && target == '.') return true;
            if (sr == 1 && cDiff == 0 && rDiff == 2 && target == '.' && board[(sr+1)*8+sc] == '.') return true;
            if (Math.abs(cDiff) == 1 && rDiff == 1 && Character.isUpperCase(target)) return true;
            return false;
        }
        if (piece=='R'||piece=='r') return (sr==dr||sc==dc) && isPathClear(board,sc,sr,dc,dr);
        if (piece=='B'||piece=='b') return Math.abs(rDiff)==Math.abs(cDiff) && isPathClear(board,sc,sr,dc,dr);
        if (piece=='N'||piece=='n') return (Math.abs(rDiff)==2&&Math.abs(cDiff)==1)||(Math.abs(rDiff)==1&&Math.abs(cDiff)==2);
        if (piece=='Q'||piece=='q') return ((sr==dr||sc==dc)||(Math.abs(rDiff)==Math.abs(cDiff))) && isPathClear(board,sc,sr,dc,dr);
        if (piece=='K'||piece=='k') return Math.abs(rDiff)<=1 && Math.abs(cDiff)<=1;
        return false;
    }

    // ── hasAnyLegalMove ─────────────────────────────────────
    static boolean hasAnyLegalMove(char[] board, boolean white) {
        for (int sr = 0; sr < 8; sr++)
            for (int sc = 0; sc < 8; sc++) {
                char piece = board[sr * 8 + sc];
                if (piece == '.') continue;
                if ( white && !Character.isUpperCase(piece)) continue;
                if (!white && !Character.isLowerCase(piece)) continue;
                for (int dr = 0; dr < 8; dr++)
                    for (int dc = 0; dc < 8; dc++) {
                        if (!isPseudoLegalMove(board, sc, sr, dc, dr, white)) continue;
                        char cap = board[dr * 8 + dc];
                        board[dr * 8 + dc] = piece; board[sr * 8 + sc] = '.';
                        boolean inCheck = isKingInCheck(board, white);
                        board[sr * 8 + sc] = piece; board[dr * 8 + dc] = cap;
                        if (!inCheck) return true;
                    }
            }
        return false;
    }

    // ── getLegalMovesForSelected ─────────────────────────────
    // Mirrors C++ getLegalMovesForSelected() exactly, including
    // the full castling safety-check block (3 squares must be safe).
    static List<MoveSq> getLegalMovesForSelected(
            char[] board, int sc, int sr, boolean white,
            boolean wK, boolean bK,
            boolean wRa, boolean wRh,
            boolean bRa, boolean bRh) {

        List<MoveSq> moves = new ArrayList<>();
        char piece = board[sr * 8 + sc];
        if (piece == '.') return moves;
        if ( white && !Character.isUpperCase(piece)) return moves;
        if (!white && !Character.isLowerCase(piece)) return moves;

        for (int dr = 0; dr < 8; dr++) {
            for (int dc = 0; dc < 8; dc++) {
                if (dr == sr && dc == sc) continue;

                boolean basicMove     = isPseudoLegalMove(board, sc, sr, dc, dr, white);
                boolean castleAttempt = (piece=='K'||piece=='k') && dr==sr && Math.abs(dc-sc)==2;

                if (!basicMove && !castleAttempt) continue;

                // ── Castling: full safety validation (matches C++ getLegalMovesForSelected) ──
                if ((piece=='K'||piece=='k') && Math.abs(dc-sc)==2) {
                    int row   = sr;
                    boolean isW = (piece == 'K');
                    boolean opp = !isW;
                    boolean canCastle = false;

                    if (dc > sc) {  // kingside
                        boolean kM = isW ? wK : bK;
                        boolean rM = isW ? wRh : bRh;
                        if (!kM && !rM
                                && board[row*8+5]=='.' && board[row*8+6]=='.'
                                && !isSquareAttacked(board,row,4,opp)
                                && !isSquareAttacked(board,row,5,opp)
                                && !isSquareAttacked(board,row,6,opp))
                            canCastle = true;
                    } else {        // queenside
                        boolean kM = isW ? wK : bK;
                        boolean rM = isW ? wRa : bRa;
                        if (!kM && !rM
                                && board[row*8+1]=='.' && board[row*8+2]=='.' && board[row*8+3]=='.'
                                && !isSquareAttacked(board,row,4,opp)
                                && !isSquareAttacked(board,row,3,opp)
                                && !isSquareAttacked(board,row,2,opp))
                            canCastle = true;
                    }
                    if (!canCastle) continue;
                }

                // ── Safety check: does this move expose own king? ──
                char captured = board[dr * 8 + dc];
                board[dr * 8 + dc] = piece; board[sr * 8 + sc] = '.';
                boolean illegal = isKingInCheck(board, white);
                board[sr * 8 + sc] = piece; board[dr * 8 + dc] = captured;

                if (!illegal)
                    moves.add(new MoveSq(dr, dc, captured != '.'));
            }
        }
        return moves;
    }

    // ========================================================
    //  Inner data classes
    // ========================================================

    /** Represents a single legal destination square for a selected piece */
    static class MoveSq {
        int r, c; boolean capture;
        MoveSq(int r, int c, boolean capture) { this.r = r; this.c = c; this.capture = capture; }
    }

    /** Full snapshot of game state used for undo / redo stacks */
    static class GameState {
        char[]  board       = new char[64];
        boolean whiteTurn;
        boolean wKingMoved,  bKingMoved;
        boolean wRookAMoved, wRookHMoved;
        boolean bRookAMoved, bRookHMoved;
        boolean gameOver;
    }
}
