package Controller;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import Model.GameModel;
import View.GameView;
import Entity.Enemy.EnemyProjectile;
import Config.EnemyConfig;
import Data.GameState;

public class GameController extends KeyAdapter implements ActionListener {

    private final GameModel model;
    private final GameView view;
    private final Timer gameLoop;

    public GameController(GameModel model, GameView view) {
        this.model = model;
        this.view = view;

        this.view.addGameKeyListener(this);

        this.gameLoop = new Timer(GameModel.GAME_SPEED_MS, this);
    }

    public void startGame() {
        gameLoop.start();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        
        if (model.getGameState() == GameState.LEVEL_UP_CHOICE) {
            if (keyCode == KeyEvent.VK_1 || keyCode == KeyEvent.VK_NUMPAD1) {
                model.getUpgradeManager().selectUpgrade(0);
                model.setGameState(GameState.PLAYING);
                // Resume game loop after selecting an upgrade
                if (!gameLoop.isRunning()) {
                    gameLoop.start();
                }
            } else if (keyCode == KeyEvent.VK_2 || keyCode == KeyEvent.VK_NUMPAD2) {
                model.getUpgradeManager().selectUpgrade(1);
                model.setGameState(GameState.PLAYING);
                if (!gameLoop.isRunning()) {
                    gameLoop.start();
                }
            } else if (keyCode == KeyEvent.VK_3 || keyCode == KeyEvent.VK_NUMPAD3) {
                model.getUpgradeManager().selectUpgrade(2);
                model.setGameState(GameState.PLAYING);
                if (!gameLoop.isRunning()) {
                    gameLoop.start();
                }
            }
            view.repaint();
            return;
        }
        
        if (keyCode == KeyEvent.VK_ESCAPE) {
            if (model.getGameState() == GameState.PLAYING || 
                model.getGameState() == GameState.PAUSED) {
                model.togglePause();
                view.repaint();
            }
            return;
        }

        // DEBUG: spawn a test EnemyProjectile aimed at the player
        if (keyCode == KeyEvent.VK_F9) {
            if (model.getPlayer() != null) {
                int px = model.getPlayer().x + (model.getPlayer().getSpriteWidth() / 2);
                int py = model.getPlayer().y + (model.getPlayer().getSpriteHeight() / 2);
                int startX = px;
                int startY = py - 300;
                Entity.Enemy.EnemyProjectile ep = new EnemyProjectile('z', startX, startY, px, py, EnemyConfig.LOUVADEUS_PROJECTILE_SPEED);
                model.addEnemy(ep);
                System.out.println("[DEBUG] Spawned test EnemyProjectile via F9");
            }
            return;
        }

        if (keyCode == KeyEvent.VK_ENTER) {
            if (model.getGameState() == GameState.MAIN_MENU) {
                model.startNewGame();
                view.repaint();
            } else if (model.getGameState() == GameState.GAME_OVER) {
                model.returnToMenu();
                view.repaint();
            } else if (model.getGameState() == GameState.ENTERING_NAME) {
                model.submitHighScore();
                view.repaint();
            }
            return;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();

        if (model.getGameState() == GameState.PLAYING) {
            if (c == KeyEvent.VK_BACK_SPACE) {
                model.backspaceTypedWord();
            } else if (Character.isLetter(c)) {
                model.appendTypedCharacter(c);
            }
            view.repaint();
        } else if (model.getGameState() == GameState.ENTERING_NAME) {
            if (c == KeyEvent.VK_BACK_SPACE) {
                model.backspacePlayerName();
            } else if (Character.isLetterOrDigit(c)) {
                model.appendToPlayerName(c);
            }
            view.repaint();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        GameState currentState = model.getGameState();
        
        if (currentState == GameState.PLAYING) {
            model.updateGameState();
        } else if (currentState == GameState.LEVEL_UP_CHOICE) {
            // Pause the timer while the player chooses an upgrade.
            if (gameLoop.isRunning()) {
                gameLoop.stop();
            }
        }
        
        view.repaint();
    }
}