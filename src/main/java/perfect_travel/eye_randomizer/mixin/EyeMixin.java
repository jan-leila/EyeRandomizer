package perfect_travel.eye_randomizer.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EyeOfEnderEntity;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(EyeOfEnderEntity.class)
public abstract class EyeMixin extends Entity implements FlyingItemEntity {
    @Shadow
    private double velocityX;
    @Shadow
    private double velocityZ;
    @Shadow
    private double velocityY;
    @Shadow
    private boolean dropsItem;
    @Shadow
    private int useCount;

    private int xPos = 0;
    private int zPos = 0;

    public EyeMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Redirect(method = "tick", at=@At(value = "INVOKE", target = "Lnet/minecraft/entity/EyeOfEnderEntity;remove()V"))
    private void removeRedirect(EyeOfEnderEntity eyeOfEnderEntity){
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        player.sendChatMessage("stronghold cords: " + xPos + " " + zPos);

        if(new Random().nextInt(1000000) == 0){
            player.sendChatMessage("(trans rights btw)");
        }
        eyeOfEnderEntity.remove();
    }

    /**
     * @author closet_witch fuck you btw
     */
    @Overwrite
    public void moveTowards(BlockPos fakePos){
        Random random = new Random();

        double whole = Math.PI * 2;
        double third = whole / 3;

        double rotation = random.nextFloat() * whole;
        double dist = random.nextInt(96) + 80;

        double playerRotation = Math.atan2(this.getX(), this.getZ());
        if(playerRotation < 0){
            playerRotation += whole;
        }
        System.out.println(playerRotation);
//        System.out.println(rotation);

        while(Math.abs(playerRotation - rotation) > third){
            rotation += third;
            if(rotation > whole){
                rotation -= whole;
            }
        }

        xPos = (int) Math.floor(Math.sin(rotation) * dist) * 16 + 8;
        zPos = (int) Math.floor(Math.cos(rotation) * dist) * 16 + 8;

        BlockPos pos = new BlockPos(xPos, 32, zPos);

        double d = pos.getX();
        int i = pos.getY();
        double e = pos.getZ();
        double f = d - this.getX();
        double g = e - this.getZ();
        float h = MathHelper.sqrt(f * f + g * g);
        if (h > 12.0F) {
            this.velocityX = this.getX() + f / (double)h * 12.0D;
            this.velocityZ = this.getZ() + g / (double)h * 12.0D;
            this.velocityY = this.getY() + 8.0D;
        } else {
            this.velocityX = d;
            this.velocityY = i;
            this.velocityZ = e;
        }

        dropsItem = false;
        this.useCount = 0;
    }
}
