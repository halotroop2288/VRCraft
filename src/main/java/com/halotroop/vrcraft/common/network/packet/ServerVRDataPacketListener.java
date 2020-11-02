package com.halotroop.vrcraft.common.network.packet;

import net.minecraft.network.listener.PacketListener;

public interface ServerVRDataPacketListener extends PacketListener {
	void onControllerDataSync0(UberPacket.ControllerData0 packet);
	void onControllerDataSync1(UberPacket.ControllerData0 packet);
	void onHeadDataSync(UberPacket.HeadData packet);
	void onUberPacketSync(UberPacket packet); // No-op
}
