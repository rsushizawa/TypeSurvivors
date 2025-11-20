package Entity.Enemy;

import Animation.SpriteSheetLoader;
import java.awt.image.BufferedImage;
import java.util.Random;
import Model.GameModel;
import Entity.Enemy.EnemyProjectile;


public class LouvaDeusEnemy extends Enemy {
    private static final String SPRITE_PATH = "Assets/Enemy/mantis sprite sheet.png";
    private static final int SPRITE_WIDTH = 184;
    private static final int SPRITE_HEIGHT = 184;
    private static final int ANIMATION_FRAMES = 6;
    private static final int ANIMATION_SPEED = 6;

    // Rows: walking (row 0) and attack row configured in EnemyConfig
    private static final int WALK_DOWN_ROW = 0;
    private static final int ATTACK_ROW = Config.EnemyConfig.LOUVADEUS_ATTACK_ROW;

    private double worldSpeedX;
    
    // timing / attack state
    private int attackTickCounter = 0;
    private final int requiredAttackTicks;

    // Repeatable attack support
    private final int attackCooldownTicks;
    private int attackCooldownRemaining = 0;
    private int attackCount = 0;
    private final int maxAttacks;
    private boolean playingAttackAnimation = false;
    private double savedZSpeed = 0.0;

    private static final Random random = new Random();

    private static BufferedImage[] walkSprites = null;
    private static BufferedImage[] attackSprites = null;

    static {
        loadSprites();
    }

    private static void loadSprites() {
        walkSprites = SpriteSheetLoader.loadSpriteRow(
            SPRITE_PATH,
            WALK_DOWN_ROW,
            ANIMATION_FRAMES,
            SPRITE_WIDTH,
            SPRITE_HEIGHT
        );

        attackSprites = SpriteSheetLoader.loadSpriteRow(
            SPRITE_PATH,
            ATTACK_ROW,
            ANIMATION_FRAMES,
            SPRITE_WIDTH,
            SPRITE_HEIGHT
        );
    }

    public LouvaDeusEnemy(String text, double worldX, double zSpeed, double worldSpeedX) {
        super(text, worldX, zSpeed, walkSprites, ANIMATION_SPEED);
        this.worldSpeedX = worldSpeedX;

        // Convert attack delay seconds to ticks based on game loop timing
        this.requiredAttackTicks = (int)Math.ceil((Config.EnemyConfig.LOUVADEUS_ATTACK_DELAY_SECONDS * 1000.0) / (double)GameModel.GAME_SPEED_MS);
        this.attackCooldownTicks = (int)Math.ceil((Config.EnemyConfig.LOUVADEUS_ATTACK_COOLDOWN_SECONDS * 1000.0) / (double)GameModel.GAME_SPEED_MS);
        this.maxAttacks = Config.EnemyConfig.LOUVADEUS_MAX_ATTACKS;
    }

    public static boolean spritesLoaded() {
        return walkSprites != null && attackSprites != null;
    }

    @Override
    public void update(){
        this.z += this.zSpeed;

        this.worldX += this.worldSpeedX;

        if (this.worldX > this.MAX_WIDTH) {
            double overshoot = this.worldX - this.MAX_WIDTH;
            this.worldSpeedX = -Math.abs(this.worldSpeedX);
            this.worldX = this.MAX_WIDTH - (overshoot * Config.EnemyConfig.BOUNCE_FACTOR_LARGE);
        } else if (this.worldX < this.MIN_WIDTH) {
            double overshoot = this.MIN_WIDTH - this.worldX;
            this.worldSpeedX = Math.abs(this.worldSpeedX);
            this.worldX = this.MIN_WIDTH + (overshoot * Config.EnemyConfig.BOUNCE_FACTOR_LARGE);
        }

        updatePerspective();
    }

    @Override
    public void onModelUpdate(GameModel model) {
        // Decrease cooldown timer if active
        if (attackCooldownRemaining > 0) {
            attackCooldownRemaining--;
        }

        // If currently playing attack animation, wait until it's finished to resume walking
        if (playingAttackAnimation) {
            if (this.animatedSprite.isAnimationFinished()) {
                // restore walking animation and movement
                playingAttackAnimation = false;
                if (walkSprites != null) {
                    this.animatedSprite.setLoopAnimation(true);
                    this.animatedSprite.setSprites(walkSprites);
                }
                this.zSpeed = savedZSpeed;
            }
            return; // while attack anim plays, do not trigger another attack
        }

        // Not currently playing attack animation: consider attack if delay passed and cooldown ready
        attackTickCounter++;

        boolean canAttackByCount = (maxAttacks < 0) || (attackCount < maxAttacks);
        if (attackTickCounter >= requiredAttackTicks && attackCooldownRemaining <= 0 && canAttackByCount) {
            // Start attack: stop forward movement, play one-shot attack animation
            savedZSpeed = this.zSpeed;
            this.zSpeed = 0.0;
            if (attackSprites != null) {
                this.animatedSprite.setLoopAnimation(false);
                this.animatedSprite.setSprites(attackSprites);
            }

            // Launch configured number of projectiles at the player
            int count = Config.EnemyConfig.LOUVADEUS_ATTACK_PROJECTILES;
            if (model.getPlayer() != null) {
                for (int i = 0; i < count; i++) {
                    int targetX = model.getPlayer().x + (model.getPlayer().getSpriteWidth() / 2) + random.nextInt(41) - 20;
                    int targetY = model.getPlayer().y + random.nextInt(41) - 20;
                    int startX = this.x;
                    int startY = this.y - (this.getScaledHeight() / 2);
                    // Spawn a mini-enemy projectile (single-letter) so it becomes targetable by typing
                    char letter = (char)('a' + random.nextInt(26));
                    // Debug: print when spawning a mini-enemy
                    System.out.println(String.format("[DEBUG] Louva spawning EnemyProjectile '%c' from (%d,%d) -> (%d,%d)", letter, startX, startY, targetX, targetY));
                    model.addEnemy(new EnemyProjectile(letter, startX, startY, targetX, targetY, Config.EnemyConfig.LOUVADEUS_PROJECTILE_SPEED));
                }
            }

            // Play louva-specific attack SFX
            Audio.AudioManager.playLouvaAttackSfx();

            // Set cooldown and counters
            attackCooldownRemaining = Math.max(0, attackCooldownTicks);
            attackCount++;
            playingAttackAnimation = true;
        }
    }
}