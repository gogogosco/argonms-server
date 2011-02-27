/*
 * ArgonMS MapleStory server emulator written in Java
 * Copyright (C) 2011  GoldenKevin
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package argonms.game;

import argonms.net.client.ClientPacketProcessor;
import argonms.net.client.ClientRecvOps;
import argonms.net.client.RemoteClient;
import argonms.net.client.handler.DealDamageHandler;
import argonms.net.client.handler.GameChatHandler;
import argonms.net.client.handler.GameEnterHandler;
import argonms.net.client.handler.GameGoToHandler;
import argonms.net.client.handler.GameMovementHandler;
import argonms.net.client.handler.GameNpcHandler;
import argonms.net.client.handler.InventoryHandler;
import argonms.tools.input.LittleEndianReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author GoldenKevin
 */
public class ClientGamePacketProcessor extends ClientPacketProcessor {
	private static final Logger LOG = Logger.getLogger(ClientPacketProcessor.class.getName());

	public void process(LittleEndianReader reader, RemoteClient s) {
		switch (reader.readShort()) {
			case ClientRecvOps.SERVERLIST_REREQUEST:
			case ClientRecvOps.EXIT_CHARLIST:
			case ClientRecvOps.PICK_ALL_CHAR:
			case ClientRecvOps.ENTER_EXIT_VIEW_ALL:
			case ClientRecvOps.CHAR_SELECT:
			case ClientRecvOps.RELOG:
				//lol, char loading lag...
				break;
			case ClientRecvOps.PLAYER_CONNECTED:
				GameEnterHandler.handlePlayerConnection(reader, s);
				break;
			case ClientRecvOps.PONG:
				s.receivedPong();
				break;
			case ClientRecvOps.CLIENT_ERROR:
				s.clientError(reader.readLengthPrefixedString());
				break;
			case ClientRecvOps.AES_IV_UPDATE_REQUEST:
				//no-op
				break;
			case ClientRecvOps.CHANGE_MAP:
				GameGoToHandler.handleMapChange(reader, s);
				break;
			case ClientRecvOps.CHANGE_CHANNEL:
				GameGoToHandler.handleChangeChannel(reader, s);
				break;
			case ClientRecvOps.ENTER_CASH_SHOP:
				GameGoToHandler.handleWarpCs(reader, s);
				break;
			case ClientRecvOps.MOVE_PLAYER:
				GameMovementHandler.handleMovePlayer(reader, s);
				break;
			case ClientRecvOps.MELEE_ATTACK:
				DealDamageHandler.handleMeleeAttack(reader, s);
				break;
			case ClientRecvOps.RANGED_ATTACK:
				DealDamageHandler.handleRangedAttack(reader, s);
				break;
			case ClientRecvOps.MAGIC_ATTACK:
				DealDamageHandler.handleMagicAttack(reader, s);
				break;
			case ClientRecvOps.MAP_CHAT:
				GameChatHandler.handleMapChat(reader, s);
				break;
			case ClientRecvOps.NPC_TALK:
				GameNpcHandler.handleStartConversation(reader, s);
				break;
			case ClientRecvOps.NPC_TALK_MORE:
				GameNpcHandler.handleContinueConversation(reader, s);
				break;
			case ClientRecvOps.ITEM_MOVE:
				InventoryHandler.handleItemMove(reader, s);
				break;
			case ClientRecvOps.MESO_DROP:
				InventoryHandler.handleMesoDrop(reader, s);
				break;
			case ClientRecvOps.CHANGE_MAP_SPECIAL:
				GameGoToHandler.handleEnteredSpecialPortal(reader, s);
				break;
			case ClientRecvOps.QUEST_ACTION:
				GameNpcHandler.handleQuestAction(reader, s);
				break;
			case ClientRecvOps.MOVE_LIFE:
				GameMovementHandler.handleMoveLife(reader, s);
				break;
			case ClientRecvOps.ITEM_PICKUP:
				InventoryHandler.handleMapItemPickUp(reader, s);
				break;
			case ClientRecvOps.PLAYER_UPDATE:
				((GameClient) s).getPlayer().saveCharacter();
				break;
			default:
				LOG.log(Level.FINE, "Received unhandled client packet {0} bytes long:\n{1}", new Object[] { reader.available() + 2, reader });
				break;
		}
	}
}