package net.goose.lifesteal.capability;

import net.goose.lifesteal.LifeSteal;
import net.goose.lifesteal.api.IHeartCap;
import net.goose.lifesteal.api.ILevelCap;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LevelCapAttacher {
    public static void attach(final AttachCapabilitiesEvent<Level> event) {
        class LevelCapProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

            public static final ResourceLocation IDENTIFIER = new ResourceLocation(LifeSteal.MOD_ID, "bannedmap");
            private final ILevelCap backend = new LevelCap(event.getObject());
            private final LazyOptional<ILevelCap> optionalData = LazyOptional.of(() -> backend);

            @NotNull
            @Override
            public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                return CapabilityRegistry.LEVEL_CAP_CAPABILITY.orEmpty(cap, this.optionalData);
            }

            @Override
            public CompoundTag serializeNBT() {
                return this.backend.serializeNBT();
            }

            @Override
            public void deserializeNBT(CompoundTag nbt) {
                this.backend.deserializeNBT(nbt);
            }
        }

        final LevelCapProvider provider = new LevelCapProvider();

        event.addCapability(LevelCapProvider.IDENTIFIER, provider);
    }
}
