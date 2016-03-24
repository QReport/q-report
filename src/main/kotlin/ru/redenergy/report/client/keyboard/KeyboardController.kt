package ru.redenergy.report.client.keyboard

import cpw.mods.fml.client.registry.ClientRegistry
import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.gameevent.InputEvent
import net.minecraft.client.settings.KeyBinding

class KeyboardController {

    var keys : MutableMap<KeyBinding, () -> Unit> = hashMapOf()

    fun submit() {
        keys.forEach { ClientRegistry.registerKeyBinding(it.key) }
        FMLCommonHandler.instance().bus().register(this)
    }

    fun register(key: KeyBinding, func: () -> Unit) = keys.put(key, func)

    fun register(key: Int, description: String, func: () -> Unit) = register(KeyBinding(description, key, "QReport"), func)

    @SubscribeEvent
    fun onKeyPress(event: InputEvent.KeyInputEvent) = this.keys.forEach { if(it.key.isKeyPressed) it.value.invoke() }
}