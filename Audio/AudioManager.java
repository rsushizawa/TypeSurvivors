package Audio;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import Config.PathsConfig;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public class AudioManager {

    private static Clip mainMenuMusic;
    private static Clip bossMusic;
    private static Clip projectileSfx;
    private static List<Clip> projectileSfxList = new ArrayList<>();
    private static Random rand = new Random();
    private static Clip fireballSfx;
    private static Clip louvaAttackSfx;
    private static Clip wrongKeySfx;

    private static Clip loadClip(String path) {
        try {
            File audioFile = new File(path);
            if (!audioFile.exists()) {
                System.err.println("Audio file not found: " + path);
                return null;
            }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error loading audio: " + path);
            e.printStackTrace();
            return null;
        }
    }
    public static void init() {
    mainMenuMusic = loadClip(PathsConfig.MAIN_MENU_MUSIC);
    bossMusic = loadClip(PathsConfig.BOSS_MUSIC);
    projectileSfx = loadClip(PathsConfig.PROJECTILE_SFX);
    // Load all projectile sfx files from Assets/SFX/Projectile
    File projDir = new File(PathsConfig.SFX_DIR + "/Projectile");
    if (projDir.exists() && projDir.isDirectory()) {
        File[] files = projDir.listFiles((d, name) -> name.toLowerCase().endsWith(".wav"));
        if (files != null) {
            for (File f : files) {
                Clip c = loadClip(f.getPath());
                if (c != null) projectileSfxList.add(c);
            }
        }
    }
    fireballSfx = loadClip(PathsConfig.FIREBALL_SFX);
    louvaAttackSfx = loadClip(PathsConfig.LOUVA_ATTACK_SFX);
    wrongKeySfx = loadClip(PathsConfig.WRONG_KEY_SFX);
    }

    public static void playMainMenuMusic() {
        stopAllMusic();
        if (mainMenuMusic != null) {
            mainMenuMusic.setFramePosition(0); 
            mainMenuMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public static void playGameMusic() {
        if (bossMusic != null && bossMusic.isRunning()) {
            bossMusic.stop();
        }
        if (mainMenuMusic != null && !mainMenuMusic.isRunning()) {
            mainMenuMusic.setFramePosition(0);
            mainMenuMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public static void playBossMusic() {
        stopAllMusic();
        if (bossMusic != null) {
            bossMusic.setFramePosition(0);
            bossMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public static void stopAllMusic() {
        if (mainMenuMusic != null && mainMenuMusic.isRunning()) {
            mainMenuMusic.stop();
        }
        if (bossMusic != null && bossMusic.isRunning()) {
            bossMusic.stop();
        }
    }


    public static void playProjectileSfx() {
        try {
            if (!projectileSfxList.isEmpty()) {
                Clip c = projectileSfxList.get(rand.nextInt(projectileSfxList.size()));
                if (c != null) {
                    c.setFramePosition(0);
                    c.start();
                    return;
                }
            }
            if (projectileSfx != null) {
                projectileSfx.setFramePosition(0);
                projectileSfx.start();
            }
        } catch (Exception e) {
            // Swallow audio playback exceptions to avoid crashing the game loop
            System.err.println("Error playing projectile sfx: " + e.getMessage());
        }
    }

    public static void playFireballSfx() {
        if (fireballSfx != null) {
            fireballSfx.setFramePosition(0);
            fireballSfx.start();
        }
    }

    public static void playLouvaAttackSfx() {
        if (louvaAttackSfx != null) {
            louvaAttackSfx.setFramePosition(0);
            louvaAttackSfx.start();
        }
    }

    public static void playWrongKeySfx() {
        try {
            wrongKeySfx.setFramePosition(0);
            wrongKeySfx.start();
        } catch (Exception e) {
            System.err.println("Error playing WrongKey SFX: " + e.getMessage());
        }
    }
}