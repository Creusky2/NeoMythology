// package com.mrcreusky.neomythology.powers.spells;

// import com.mrcreusky.neomythology.powers.Spell;
// import com.mrcreusky.neomythology.entities.LightBeamEntity;
// import net.minecraft.server.level.ServerPlayer;
// import net.minecraft.world.entity.player.Player;
// import net.minecraft.world.level.Level;
// import net.minecraft.server.level.ServerLevel;


// public class LightAbsorptionSpell extends Spell {


//     public LightAbsorptionSpell() {
//         super("Light Absorption", 200, 10);
//     }

//     @Override
//     public void castSpell(ServerPlayer player, ServerLevel level) {
//         if (!canCast(player)) {
//             return;
//         }

//         int totalLightLevel = calculateLightLevelAroundPlayer(player, level);
//         float power = calculatePower(totalLightLevel);
//         // launchLightBeam(player, level, power);

//         applyCooldown(player);
//         consumeResource(player);
//     }

//     private int calculateLightLevelAroundPlayer(ServerPlayer player, ServerLevel level) {
//         // Logique de calcul de lumière
//         return 100;  // Exemple
//     }

//     private float calculatePower(int totalLightLevel) {
//         return totalLightLevel / 10.0f;
//     }

//     // private void launchLightBeam(ServerPlayer player, ServerLevel level, float power) {
//     //     LightBeamEntity beam = new LightBeamEntity(ModEntities.LIGHT_BEAM, level, power);
//     //     beam.setPos(player.getX(), player.getEyeY(), player.getZ());
//     //     beam.shoot(beam.getX(), beam.getY(), beam.getZ(), 3.0F, 1.0F);
//     //     level.addFreshEntity(beam);

//     // }

//     private void applyCooldown(Player player) {
//         // Gestion du cooldown
//     }

//     private void consumeResource(Player player) {
//         // Consommation des ressources (mana, etc.)
//     }

// }