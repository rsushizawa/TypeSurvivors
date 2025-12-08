package Entity.Enemy;

import Animation.SpriteSheetLoader;
import java.awt.image.BufferedImage;
import java.util.Random;
import Model.GameModel;

public class VespaEnemy extends Enemy {
    
    private static final String SPRITE_PATH = "Assets/Enemy/vespa color resize.png"; 
    private static final int SPRITE_WIDTH = 184*2; 
    private static final int SPRITE_HEIGHT = 184*2;
    private static final int WALK_DOWN_ROW = 0;
    private static final int ANIMATION_FRAMES = 3;
    private static final int ANIMATION_SPEED = 12;

    private double worldSpeedX;
    
    private static BufferedImage[] orcSprites = null;
    private static BufferedImage[] attackSprites = null;

    private int attackTickCounter = 0;
    private final int requiredAttackTicks;
    private final int attackCooldownTicks;
    private int attackCooldownRemaining = 0;
    private int attackCount = 0;
    private final int maxAttacks;
    private boolean playingAttackAnimation = false;
    private double savedZSpeed = 0.0;

    private static final Random random = new Random();
    
    static {
        loadSprites();
    }
    
    private static void loadSprites() {
        orcSprites = SpriteSheetLoader.loadSpriteRow(
            SPRITE_PATH, 
            WALK_DOWN_ROW, 
            ANIMATION_FRAMES, 
            SPRITE_WIDTH, 
            SPRITE_HEIGHT
        );
        attackSprites = SpriteSheetLoader.loadSpriteRow(
            SPRITE_PATH,
            0,
            ANIMATION_FRAMES,
            SPRITE_WIDTH,
            SPRITE_HEIGHT
        );
    }

    public VespaEnemy(String text, double worldX, double zSpeed, double worldSpeedX) {
        super(text, worldX, zSpeed, orcSprites, ANIMATION_SPEED);
        this.worldSpeedX = worldSpeedX;
        this.requiredAttackTicks = (int)Math.ceil((Config.EnemyConfig.VESPA_ATTACK_DELAY_SECONDS * 1000.0) / (double)GameModel.GAME_SPEED_MS);
        this.attackCooldownTicks = (int)Math.ceil((Config.EnemyConfig.VESPA_ATTACK_COOLDOWN_SECONDS * 1000.0) / (double)GameModel.GAME_SPEED_MS);
        this.maxAttacks = Config.EnemyConfig.VESPA_MAX_ATTACKS;
    }
    
    public static boolean spritesLoaded() {
        return orcSprites != null;
    }

    @Override
    public void onModelUpdate(GameModel model) {
        if (attackCooldownRemaining > 0) attackCooldownRemaining--;

        if (playingAttackAnimation) {
            if (this.animatedSprite.isAnimationFinished()) {
                playingAttackAnimation = false;
                if (orcSprites != null) {
                    this.animatedSprite.setLoopAnimation(true);
                    this.animatedSprite.setSprites(orcSprites);
                }
                this.zSpeed = savedZSpeed;
            }
            return;
        }

        attackTickCounter++;
        boolean canAttackByCount = (maxAttacks < 0) || (attackCount < maxAttacks);
        if (attackTickCounter >= requiredAttackTicks && attackCooldownRemaining <= 0 && canAttackByCount) {
            savedZSpeed = this.zSpeed;
            this.zSpeed = 0.0;
            if (attackSprites != null) {
                this.animatedSprite.setLoopAnimation(false);
                this.animatedSprite.setSprites(attackSprites);
            }

            if (model.getPlayer() != null) {
                int targetX = model.getPlayer().x + (model.getPlayer().getSpriteWidth() / 2) + random.nextInt(41) - 20;
                int targetY = model.getPlayer().y + random.nextInt(41) - 20;
                int startX = this.x;
                int startY = this.y - (this.getScaledHeight() / 2);
                char letter = (char)('a' + random.nextInt(26));
                model.addEnemy(new VespaProjectile(letter, startX, startY, targetX, targetY, Config.EnemyConfig.VESPA_PROJECTILE_SPEED));
            }

            Audio.AudioManager.playLouvaAttackSfx();

            attackCooldownRemaining = Math.max(0, attackCooldownTicks);
            attackCount++;
            playingAttackAnimation = true;
        }
    }

    @Override
    public void update(){
        this.z += this.zSpeed;
        
        this.worldX += this.worldSpeedX;
        
        // Bounce against road edges computed for this z so bees follow the curved road
        double[] bounds = Config.PerspectiveConfig.getWorldXBoundsForZ(this.z);
        double minW = bounds[0];
        double maxW = bounds[1];
        if (this.worldX > maxW) {
            double overshoot = this.worldX - maxW;
            this.worldSpeedX = -Math.abs(this.worldSpeedX);
            this.worldX = maxW - (overshoot * Config.EnemyConfig.BOUNCE_FACTOR_LARGE);
        } else if (this.worldX < minW) {
            double overshoot = minW - this.worldX;
            this.worldSpeedX = Math.abs(this.worldSpeedX);
            this.worldX = minW + (overshoot * Config.EnemyConfig.BOUNCE_FACTOR_LARGE);
        }
        
        updatePerspective();
    }
}