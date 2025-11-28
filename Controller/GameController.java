package Controller;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import Model.GameModel;
import Data.Upgrades.Upgrade;
import View.GameView;
import Entity.Enemy.EnemyProjectile;
import Config.EnemyConfig;
import Data.GameState;

public class GameController extends KeyAdapter implements ActionListener {

    private final GameModel model;
    private final GameView view;
    private final Timer gameLoop;
    private Data.GameState previousState = null;

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
                // apply the chosen upgrade immediately so utility upgrades take effect now
                Upgrade choice0 = null;
                if (!model.getUpgradeManager().getCurrentLevelUpOffer().isEmpty()) {
                    choice0 = model.getUpgradeManager().getCurrentLevelUpOffer().get(0);
                }
                model.getUpgradeManager().selectUpgrade(0);
                if (choice0 != null) choice0.apply(model, null);
                model.setGameState(GameState.PLAYING);
                // Resume game loop after selecting an upgrade
                if (!gameLoop.isRunning()) {
                    gameLoop.start();
                }
            } else if (keyCode == KeyEvent.VK_2 || keyCode == KeyEvent.VK_NUMPAD2) {
                Upgrade choice1 = null;
                if (model.getUpgradeManager().getCurrentLevelUpOffer().size() > 1) {
                    choice1 = model.getUpgradeManager().getCurrentLevelUpOffer().get(1);
                }
                model.getUpgradeManager().selectUpgrade(1);
                if (choice1 != null) choice1.apply(model, null);
                model.setGameState(GameState.PLAYING);
                if (!gameLoop.isRunning()) {
                    gameLoop.start();
                }
            } else if (keyCode == KeyEvent.VK_3 || keyCode == KeyEvent.VK_NUMPAD3) {
                Upgrade choice2 = null;
                if (model.getUpgradeManager().getCurrentLevelUpOffer().size() > 2) {
                    choice2 = model.getUpgradeManager().getCurrentLevelUpOffer().get(2);
                }
                model.getUpgradeManager().selectUpgrade(2);
                if (choice2 != null) choice2.apply(model, null);
                model.setGameState(GameState.PLAYING);
                if (!gameLoop.isRunning()) {
                    gameLoop.start();
                }
            }
            view.repaint();
            return;
        }
        
        // Open options from main menu or pause
        if (keyCode == KeyEvent.VK_O) {
            if (model.getGameState() == GameState.MAIN_MENU || model.getGameState() == GameState.PAUSED) {
                previousState = model.getGameState();
                model.setGameState(GameState.OPTIONS);
                view.repaint();
            }
            return;
        }

        // Menu selection with arrows when in main menu
        if (model.getGameState() == GameState.MAIN_MENU) {
            if (keyCode == KeyEvent.VK_UP) {
                int cur = view.getMainMenuSelection();
                view.setMainMenuSelection(Math.max(0, cur - 1));
                view.repaint();
                return;
            } else if (keyCode == KeyEvent.VK_DOWN) {
                int cur = view.getMainMenuSelection();
                view.setMainMenuSelection(cur + 1);
                view.repaint();
                return;
            } else if (keyCode == KeyEvent.VK_ENTER) {
                view.activateMainMenuSelection();
                return;
            }
        }

        // Options menu keyboard navigation
        if (model.getGameState() == GameState.OPTIONS) {
            if (keyCode == KeyEvent.VK_UP) {
                int cur = view.getOptionsSelection();
                view.setOptionsSelection(Math.max(0, cur - 1));
                view.repaint();
                return;
            } else if (keyCode == KeyEvent.VK_DOWN) {
                int cur = view.getOptionsSelection();
                view.setOptionsSelection(Math.min(cur + 1, 3));
                view.repaint();
                return;
            } else if (keyCode == KeyEvent.VK_LEFT) {
                int cur = view.getOptionsSelection();
                if (cur == 0) {
                    Audio.AudioManager.setMusicVolume(Audio.AudioManager.getMusicVolume() - 0.05f);
                    view.repaint();
                } else if (cur == 1) {
                    Audio.AudioManager.setSfxVolume(Audio.AudioManager.getSfxVolume() - 0.05f);
                    view.repaint();
                }
                return;
            } else if (keyCode == KeyEvent.VK_RIGHT) {
                int cur = view.getOptionsSelection();
                if (cur == 0) {
                    Audio.AudioManager.setMusicVolume(Audio.AudioManager.getMusicVolume() + 0.05f);
                    view.repaint();
                } else if (cur == 1) {
                    Audio.AudioManager.setSfxVolume(Audio.AudioManager.getSfxVolume() + 0.05f);
                    view.repaint();
                }
                return;
            } else if (keyCode == KeyEvent.VK_ENTER) {
                int cur = view.getOptionsSelection();
                if (cur == 2) {
                    view.setFullscreen(!Config.GameConfig.FULLSCREEN);
                } else if (cur == 3) {
                    model.setGameState(GameState.MAIN_MENU);
                }
                view.repaint();
                return;
            }
        }

        // Pause menu keyboard navigation
        if (model.getGameState() == GameState.PAUSED) {
            if (keyCode == KeyEvent.VK_UP) {
                int cur = view.getPauseSelection();
                view.setPauseSelection(Math.max(0, cur - 1));
                view.repaint();
                return;
            } else if (keyCode == KeyEvent.VK_DOWN) {
                int cur = view.getPauseSelection();
                view.setPauseSelection(Math.min(cur + 1, 2));
                view.repaint();
                return;
            } else if (keyCode == KeyEvent.VK_ENTER) {
                // activate pause selection
                int cur = view.getPauseSelection();
                if (cur == 0) { // Resume
                    model.togglePause();
                } else if (cur == 1) { // Options
                    previousState = GameState.PAUSED;
                    model.setGameState(GameState.OPTIONS);
                } else if (cur == 2) { // Main Menu
                    model.returnToMenu();
                }
                view.repaint();
                return;
            }
        }
        
        if (keyCode == KeyEvent.VK_ESCAPE) {
            if (model.getGameState() == GameState.OPTIONS) {
                // close options and return to previous
                model.setGameState(previousState == null ? GameState.MAIN_MENU : previousState);
                previousState = null;
                view.repaint();
                return;
            }
            if (model.getGameState() == GameState.PLAYING || 
                model.getGameState() == GameState.PAUSED) {
                model.togglePause();
                view.repaint();
            }
            return;
        }

        if (keyCode == KeyEvent.VK_TAB) {
            model.tryActivateWall();
            view.repaint();
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

        if (model.getGameState() == GameState.OPTIONS) {
            // M/N adjust music up/down, K/J adjust sfx up/down, F toggle fullscreen
            int kc = Character.toUpperCase(c);
            try {
                if (kc == 'M') {
                    Audio.AudioManager.setMusicVolume(Audio.AudioManager.getMusicVolume() + 0.1f);
                    view.repaint();
                    return;
                } else if (kc == 'N') {
                    Audio.AudioManager.setMusicVolume(Audio.AudioManager.getMusicVolume() - 0.1f);
                    view.repaint();
                    return;
                } else if (kc == 'K') {
                    Audio.AudioManager.setSfxVolume(Audio.AudioManager.getSfxVolume() + 0.1f);
                    view.repaint();
                    return;
                } else if (kc == 'J') {
                    Audio.AudioManager.setSfxVolume(Audio.AudioManager.getSfxVolume() - 0.1f);
                    view.repaint();
                    return;
                } else if (kc == 'F') {
                    boolean newFs = !Config.GameConfig.FULLSCREEN;
                    view.setFullscreen(newFs);
                    view.repaint();
                    return;
                }
            } catch (Exception ex) {
                // ignore
            }
        }

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
            if (gameLoop.isRunning()) {
                gameLoop.stop();
            }
        }
        
        view.repaint();
    }
}