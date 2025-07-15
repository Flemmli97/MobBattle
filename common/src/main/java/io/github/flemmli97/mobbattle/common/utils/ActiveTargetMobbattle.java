package io.github.flemmli97.mobbattle.common.utils;

/**
 * A check whether the current mob is in an active mob battle
 * This way vanilla mechanics/behaviours are not changed unless required
 */
public interface ActiveTargetMobbattle {

    void mobbattle$setTargeting(boolean flag);

    boolean mobbattle$IsActiveTargeting();
}
