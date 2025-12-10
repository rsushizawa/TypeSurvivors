package Audio;

import java.io.File;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.JarURLConnection;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import Config.PathsConfig;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioManager {

    private static Clip mainMenuMusic;
    private static Clip introMusic;
    private static Clip bossMusic;
    private static Clip projectileSfx;
    private static List<Clip> projectileSfxList = new ArrayList<>();
    private static Random rand = new Random();
    private static Clip fireballSfx;
    private static Clip louvaAttackSfx;
    private static Clip wrongKeySfx;
    private static Clip stunsfx;
    private static Clip levelUpsfx;
    private static Clip barriersfx;
    private static Clip damagePerSecondsfx;
    private static Clip death1sfx;
    private static Clip death2sfx;
    private static Clip death3sfx;
    

    private static float musicVolume = Config.GameConfig.MUSIC_VOLUME;
    private static float sfxVolume = Config.GameConfig.SFX_VOLUME;

    private static Clip loadClip(String path) {
        try {
            File audioFile = new File(path);
            if (audioFile.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                return clip;
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error loading audio from file: " + path + " -> " + e.getMessage());
        }

        try (InputStream ris = AudioManager.class.getClassLoader().getResourceAsStream(path)) {
            if (ris != null) {
                try (BufferedInputStream bis = new BufferedInputStream(ris)) {
                    AudioInputStream audioStream = AudioSystem.getAudioInputStream(bis);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioStream);
                    return clip;
                }
            } else {
                System.err.println("Audio resource not found: " + path);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error loading audio from resource: " + path + " -> " + e.getMessage());
        }

        return null;
    }
    
    private static Clip loadClipFromStream(InputStream is, String name) {
        if (is == null) return null;
        try (BufferedInputStream bis = new BufferedInputStream(is)) {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bis);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error loading audio stream: " + name + " -> " + e.getMessage());
            return null;
        }
    }
    
    public static void init() {
        mainMenuMusic = loadClip(PathsConfig.MAIN_MENU_MUSIC);
        introMusic = loadClip(PathsConfig.INTRO_MUSIC);
        bossMusic = loadClip(PathsConfig.BOSS_MUSIC);
        projectileSfx = loadClip(PathsConfig.PROJECTILE_SFX);
        File projDir = new File(PathsConfig.SFX_DIR + "/Projectile");
        if (projDir.exists() && projDir.isDirectory()) {
            File[] files = projDir.listFiles((d, name) -> name.toLowerCase().endsWith(".wav"));
            if (files != null) {
                for (File f : files) {
                    Clip c = loadClip(f.getPath());
                    if (c != null) projectileSfxList.add(c);
                }
            }
        } else {
            try {
                URL dirUrl = AudioManager.class.getClassLoader().getResource("Assets/SFX/Projectile");
                if (dirUrl != null) {
                    if ("jar".equals(dirUrl.getProtocol())) {
                        JarURLConnection jarConn = (JarURLConnection) dirUrl.openConnection();
                        JarFile jf = jarConn.getJarFile();
                        Enumeration<JarEntry> entries = jf.entries();
                        while (entries.hasMoreElements()) {
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            if (name.startsWith("Assets/SFX/Projectile/") && name.toLowerCase().endsWith(".wav")) {
                                try (InputStream is = AudioManager.class.getClassLoader().getResourceAsStream(name)) {
                                    Clip c = loadClipFromStream(is, name);
                                    if (c != null) projectileSfxList.add(c);
                                }
                            }
                        }
                    } else if ("file".equals(dirUrl.getProtocol())) {
                        File d = new File(dirUrl.toURI());
                        File[] files = d.listFiles((xx, name) -> name.toLowerCase().endsWith(".wav"));
                        if (files != null) for (File f : files) {
                            Clip c = loadClip(f.getPath());
                            if (c != null) projectileSfxList.add(c);
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error enumerating projectile SFX resources: " + e.getMessage());
            }
        }
        fireballSfx = loadClip(PathsConfig.FIREBALL_SFX);
        barriersfx = loadClip(PathsConfig.BARRIER_SFX);
        louvaAttackSfx = loadClip(PathsConfig.LOUVA_ATTACK_SFX);
        stunsfx = loadClip(PathsConfig.STUN_SFX);
        levelUpsfx = loadClip(PathsConfig.LEVEL_UP_SFX);
        death1sfx = loadClip(PathsConfig.DEATH1_SFX);
        death2sfx = loadClip(PathsConfig.DEATH2_SFX);
        death3sfx = loadClip(PathsConfig.DEATH3_SFX);
        wrongKeySfx = loadClip(PathsConfig.WRONG_KEY_SFX);
        damagePerSecondsfx = loadClip(PathsConfig.DAMAGE_PER_SECOND_SFX);
        setMusicVolume(musicVolume);
        setSfxVolume(sfxVolume);
    }

    public static void playMainMenuMusic() {
        new Thread(() -> {
            stopAllMusicSync();
            if (introMusic != null) {
                try {
                    applyVolume(introMusic, musicVolume);
                    introMusic.setFramePosition(0);
                    LineListener listener = new LineListener() {
                        @Override
                        public void update(LineEvent event) {
                            if (event.getType() == LineEvent.Type.STOP) {
                                introMusic.removeLineListener(this);
                                if (mainMenuMusic != null) {
                                    applyVolume(mainMenuMusic, musicVolume);
                                    mainMenuMusic.setFramePosition(0);
                                    mainMenuMusic.loop(Clip.LOOP_CONTINUOUSLY);
                                }
                            }
                        }
                    };
                    introMusic.addLineListener(listener);
                    introMusic.start();
                } catch (Exception e) {
                    System.err.println("Error playing intro music: " + e.getMessage());
                    if (mainMenuMusic != null) {
                        applyVolume(mainMenuMusic, musicVolume);
                        mainMenuMusic.setFramePosition(0);
                        mainMenuMusic.loop(Clip.LOOP_CONTINUOUSLY);
                    }
                }
            } else {
                if (mainMenuMusic != null) {
                    applyVolume(mainMenuMusic, musicVolume);
                    mainMenuMusic.setFramePosition(0);
                    mainMenuMusic.loop(Clip.LOOP_CONTINUOUSLY);
                }
            }
        }).start();
    }

    public static void playGameMusic() {
        new Thread(() -> {
            try {
                if (bossMusic != null && bossMusic.isRunning()) {
                    bossMusic.stop();
                }
                // play intro once then loop interludio (mainMenuMusic)
                if (introMusic != null) {
                    try {
                        applyVolume(introMusic, musicVolume);
                        introMusic.setFramePosition(0);
                        LineListener listener = new LineListener() {
                            @Override
                            public void update(LineEvent event) {
                                if (event.getType() == LineEvent.Type.STOP) {
                                    introMusic.removeLineListener(this);
                                    if (mainMenuMusic != null) {
                                        applyVolume(mainMenuMusic, musicVolume);
                                        mainMenuMusic.setFramePosition(0);
                                        mainMenuMusic.loop(Clip.LOOP_CONTINUOUSLY);
                                    }
                                }
                            }
                        };
                        introMusic.addLineListener(listener);
                        introMusic.start();
                        return;
                    } catch (Exception ex) {
                        System.err.println("Error playing intro music: " + ex.getMessage());
                    }
                }
                if (mainMenuMusic != null && !mainMenuMusic.isRunning()) {
                    applyVolume(mainMenuMusic, musicVolume);
                    mainMenuMusic.setFramePosition(0);
                    mainMenuMusic.loop(Clip.LOOP_CONTINUOUSLY);
                }
            } catch (Exception e) {
                System.err.println("Error switching to game music: " + e.getMessage());
            }
        }).start();
    }

    public static void playBossMusic() {
        new Thread(() -> {
            stopAllMusicSync();
            if (bossMusic != null) {
                applyVolume(bossMusic, musicVolume);
                bossMusic.setFramePosition(0);
                bossMusic.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }).start();
    }

    public static void stopAllMusic() {
        new Thread(AudioManager::stopAllMusicSync).start();
    }

    private static void stopAllMusicSync() {
        try {
            if (mainMenuMusic != null && mainMenuMusic.isRunning()) {
                mainMenuMusic.stop();
            }
            if (introMusic != null && introMusic.isRunning()) {
                introMusic.stop();
            }
            if (bossMusic != null && bossMusic.isRunning()) {
                bossMusic.stop();
            }
        } catch (Exception e) {
            System.err.println("Error stopping music: " + e.getMessage());
        }
    }

    public static void playProjectileSfx() {
        try {
            if (!projectileSfxList.isEmpty()) {
                Clip c = projectileSfxList.get(rand.nextInt(projectileSfxList.size()));
                if (c != null) {
                    applyVolume(c, sfxVolume);
                    c.setFramePosition(0);
                    c.start();
                    return;
                }
            }
            if (projectileSfx != null) {
                applyVolume(projectileSfx, sfxVolume);
                projectileSfx.setFramePosition(0);
                projectileSfx.start();
            }
        } catch (Exception e) {
            System.err.println("Error playing projectile sfx: " + e.getMessage());
        }
    }

    public static void playFireballSfx() {
        try {
            if (fireballSfx != null) {
                applyVolume(fireballSfx, sfxVolume);
                fireballSfx.setFramePosition(0);
                fireballSfx.start();
            }
        } catch (Exception e) {
             System.err.println("Error playing fireball sfx: " + e.getMessage());
        }
    }

    public static void playLouvaAttackSfx() {
        try {
            if (louvaAttackSfx != null) {
                applyVolume(louvaAttackSfx, sfxVolume);
                louvaAttackSfx.setFramePosition(0);
                louvaAttackSfx.start();
            }
        } catch (Exception e) {
             System.err.println("Error playing louva sfx: " + e.getMessage());
        }
    }

    public static void playBarrierSfx() {
        try {
            if (barriersfx != null) {
                applyVolume(barriersfx, sfxVolume);
                barriersfx.setFramePosition(0);
                barriersfx.start();
            }
        } catch (Exception e) {
             System.err.println("Error playing barrier sfx: " + e.getMessage());
        }
    }

    public static void playStunSfx() {
        try {
            if (stunsfx != null) {
                applyVolume(stunsfx, sfxVolume);
                stunsfx.setFramePosition(0);
                stunsfx.start();
            }
        } catch (Exception e) {
             System.err.println("Error playing stun sfx: " + e.getMessage());
        }
    }

    public static void playLevelUpSfx() {
        try {
            if (levelUpsfx != null) {
                applyVolume(levelUpsfx, sfxVolume);
                levelUpsfx.setFramePosition(0);
                levelUpsfx.start();
            }
        } catch (Exception e) {
             System.err.println("Error playing LevelUp sfx: " + e.getMessage());
        }
    }

    public static void playDeath1Sfx() {
        try {
            if (death1sfx != null) {
                applyVolume(death1sfx, sfxVolume);
                death1sfx.setFramePosition(0);
                death1sfx.start();
            }
        } catch (Exception e) {
             System.err.println("Error playing death1 sfx: " + e.getMessage());
        }
    }

    public static void playDeath2Sfx() {
        try {
            if (death2sfx != null) {
                applyVolume(death2sfx, sfxVolume);
                death2sfx.setFramePosition(0);
                death2sfx.start();
            }
        } catch (Exception e) {
             System.err.println("Error playing death2 sfx: " + e.getMessage());
        }
    }

    public static void playDeath3Sfx() {
        try {
            if (death3sfx != null) {
                applyVolume(death3sfx, sfxVolume);
                death3sfx.setFramePosition(0);
                death3sfx.start();
            }
        } catch (Exception e) {
             System.err.println("Error playing death3 sfx: " + e.getMessage());
        }
    }

    public static void playDamagePerSecondSfx() {
        
            try {
                
                if (damagePerSecondsfx != null) {
                    applyVolume(damagePerSecondsfx, sfxVolume);
                    damagePerSecondsfx.setFramePosition(0);
                    damagePerSecondsfx.start();
                }
            } catch (Exception e) {
                System.err.println("Error playing damage per second sfx: " + e.getMessage());
            }
        
    }

    public static void playWrongKeySfx() {
        try {
            applyVolume(wrongKeySfx, sfxVolume);
            wrongKeySfx.setFramePosition(0);
            wrongKeySfx.start();
        } catch (Exception e) {
            System.err.println("Error playing WrongKey SFX: " + e.getMessage());
        }
    }

    public static void setMusicVolume(float volume) {
        musicVolume = Math.max(0f, Math.min(1f, volume));
        Config.GameConfig.MUSIC_VOLUME = musicVolume;
        applyVolume(introMusic, musicVolume);
        applyVolume(mainMenuMusic, musicVolume);
        applyVolume(bossMusic, musicVolume);
    }

    public static float getMusicVolume() {
        return musicVolume;
    }

    public static void setSfxVolume(float volume) {
        sfxVolume = Math.max(0f, Math.min(1f, volume));
        Config.GameConfig.SFX_VOLUME = sfxVolume;
        applyVolume(projectileSfx, sfxVolume);
        for (Clip c : projectileSfxList) applyVolume(c, sfxVolume);
        applyVolume(fireballSfx, sfxVolume);
        applyVolume(louvaAttackSfx, sfxVolume);
        applyVolume(wrongKeySfx, sfxVolume);
        applyVolume(barriersfx, sfxVolume);
        applyVolume(damagePerSecondsfx, sfxVolume);
    }

    public static float getSfxVolume() {
        return sfxVolume;
    }

    private static void applyVolume(Clip clip, float volume) {
        if (clip == null) return;
        try {
            if (clip.isControlSupported(javax.sound.sampled.FloatControl.Type.MASTER_GAIN)) {
                javax.sound.sampled.FloatControl vol = (javax.sound.sampled.FloatControl) clip.getControl(javax.sound.sampled.FloatControl.Type.MASTER_GAIN);
                float min = vol.getMinimum();
                float max = vol.getMaximum();
                float dB;
                if (volume <= 0f) {
                    dB = min;
                } else {
                    dB = (float) (20.0 * Math.log10(volume));
                    dB = Math.max(min, Math.min(max, dB));
                }
                vol.setValue(dB);
            }
        } catch (Exception e) {
        }
    }
}