package fr.tt54.pycmod.events;

import java.util.UUID;

public record PlayerDamagedByPlayerEvent(UUID attackedPlayerUUID, UUID damagerPlayerUUID, float damageAmount) {
}
