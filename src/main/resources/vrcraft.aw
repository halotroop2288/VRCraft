accessWidener   v1	named
#
# CreeperIgniteGoal
accessible field net/minecraft/entity/ai/goal/CreeperIgniteGoal creeper Lnet/minecraft/entity/mob/CreeperEntity;
mutable field net/minecraft/entity/ai/goal/CreeperIgniteGoal creeper Lnet/minecraft/entity/mob/CreeperEntity;
accessible field net/minecraft/entity/ai/goal/CreeperIgniteGoal target Lnet/minecraft/entity/LivingEntity;
mutable field net/minecraft/entity/ai/goal/CreeperIgniteGoal target Lnet/minecraft/entity/LivingEntity;
#
# EndermanEntity
extendable method net/minecraft/entity/mob/EndermanEntity teleportRandomly ()Z
accessible method net/minecraft/entity/mob/EndermanEntity teleportRandomly ()Z
accessible class net/minecraft/entity/mob/EndermanEntity$ChasePlayerGoal
accessible field net/minecraft/entity/mob/EndermanEntity$ChasePlayerGoal enderman Lnet/minecraft/entity/mob/EndermanEntity;
mutable field net/minecraft/entity/mob/EndermanEntity$ChasePlayerGoal enderman Lnet/minecraft/entity/mob/EndermanEntity;
accessible field net/minecraft/entity/mob/EndermanEntity$ChasePlayerGoal target Lnet/minecraft/entity/LivingEntity;
mutable field net/minecraft/entity/mob/EndermanEntity$ChasePlayerGoal target Lnet/minecraft/entity/LivingEntity;
accessible class net/minecraft/entity/mob/EndermanEntity$TeleportTowardsPlayerGoal
accessible field net/minecraft/entity/mob/EndermanEntity$TeleportTowardsPlayerGoal enderman Lnet/minecraft/entity/mob/EndermanEntity;
mutable field net/minecraft/entity/mob/EndermanEntity$TeleportTowardsPlayerGoal enderman Lnet/minecraft/entity/mob/EndermanEntity;
accessible field net/minecraft/entity/mob/EndermanEntity$TeleportTowardsPlayerGoal targetPlayer Lnet/minecraft/entity/player/PlayerEntity;
mutable field net/minecraft/entity/mob/EndermanEntity$TeleportTowardsPlayerGoal targetPlayer Lnet/minecraft/entity/player/PlayerEntity;
accessible field net/minecraft/entity/mob/EndermanEntity$TeleportTowardsPlayerGoal lookAtPlayerWarmup I
mutable field net/minecraft/entity/mob/EndermanEntity$TeleportTowardsPlayerGoal lookAtPlayerWarmup I
accessible field net/minecraft/entity/mob/EndermanEntity$TeleportTowardsPlayerGoal ticksSinceUnseenTeleport I
mutable field net/minecraft/entity/mob/EndermanEntity$TeleportTowardsPlayerGoal ticksSinceUnseenTeleport I
accessible field net/minecraft/entity/mob/EndermanEntity$TeleportTowardsPlayerGoal staringPlayerPredicate Lnet/minecraft/entity/ai/TargetPredicate;
mutable field net/minecraft/entity/mob/EndermanEntity$TeleportTowardsPlayerGoal staringPlayerPredicate Lnet/minecraft/entity/ai/TargetPredicate;
accessible field net/minecraft/entity/mob/EndermanEntity$TeleportTowardsPlayerGoal validTargetPredicate Lnet/minecraft/entity/ai/TargetPredicate;
mutable field net/minecraft/entity/mob/EndermanEntity$TeleportTowardsPlayerGoal validTargetPredicate Lnet/minecraft/entity/ai/TargetPredicate;
extendable method net/minecraft/entity/mob/EndermanEntity teleportTo (Lnet/minecraft/entity/Entity;)Z
accessible method net/minecraft/entity/mob/EndermanEntity teleportTo (Lnet/minecraft/entity/Entity;)Z
extendable method net/minecraft/entity/mob/EndermanEntity teleportTo (DDD)Z
accessible method net/minecraft/entity/mob/EndermanEntity teleportTo (DDD)Z
#
# GoalSelector
accessible field net/minecraft/entity/ai/goal/GoalSelector goals Ljava/util/Set;
#
# MobEntity
accessible field net/minecraft/entity/mob/MobEntity goalSelector Lnet/minecraft/entity/ai/goal/GoalSelector;
mutable field net/minecraft/entity/mob/MobEntity goalSelector Lnet/minecraft/entity/ai/goal/GoalSelector;
accessible field net/minecraft/entity/mob/MobEntity targetSelector Lnet/minecraft/entity/ai/goal/GoalSelector;
mutable field net/minecraft/entity/mob/MobEntity targetSelector Lnet/minecraft/entity/ai/goal/GoalSelector;