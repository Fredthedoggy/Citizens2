package net.citizensnpcs.npc.entity;

import net.citizensnpcs.api.event.NPCPushEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.npc.CitizensMobNPC;
import net.citizensnpcs.npc.CitizensNPC;
import net.citizensnpcs.npc.ai.NPCHolder;
import net.citizensnpcs.util.NMSReflection;
import net.citizensnpcs.util.Util;
import net.minecraft.server.EntityChicken;
import net.minecraft.server.World;

import org.bukkit.entity.Chicken;
import org.bukkit.util.Vector;

public class CitizensChickenNPC extends CitizensMobNPC {

    public CitizensChickenNPC(int id, String name) {
        super(id, name, EntityChickenNPC.class);
    }

    @Override
    public Chicken getBukkitEntity() {
        return (Chicken) getHandle().getBukkitEntity();
    }

    public static class EntityChickenNPC extends EntityChicken implements NPCHolder {
        private final CitizensNPC npc;

        public EntityChickenNPC(World world) {
            this(world, null);
        }

        public EntityChickenNPC(World world, NPC npc) {
            super(world);
            this.npc = (CitizensNPC) npc;
            if (npc != null) {
                NMSReflection.clearGoals(goalSelector, targetSelector);
            }
        }

        @Override
        public void bc() {
            super.bc();
            if (npc != null)
                npc.update();
        }

        @Override
        public void collide(net.minecraft.server.Entity entity) {
            // this method is called by both the entities involved - cancelling
            // it will not stop the NPC from moving.
            super.collide(entity);
            Util.callCollisionEvent(npc, entity);
        }

        @Override
        public void g(double x, double y, double z) {
            if (npc == null) {
                super.g(x, y, z);
                return;
            }
            if (NPCPushEvent.getHandlerList().getRegisteredListeners().length == 0)
                return;
            NPCPushEvent event = Util.callPushEvent(npc, new Vector(x, y, z));
            if (!event.isCancelled())
                super.g(x, y, z);
            // when another entity collides, this method is called to push the
            // NPC so we prevent it from doing anything if the event is
            // cancelled.
        }

        @Override
        public NPC getNPC() {
            return npc;
        }
    }
}