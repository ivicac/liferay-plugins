package com.liferay.lcs.util;

import com.liferay.lcs.messaging.CommandMessage;
import com.liferay.lcs.messaging.Message;

import java.util.Comparator;

/**
 * Igor Beslic
 */
public class MessagePriorityComparator implements Comparator<Message> {

	public int compare(Message o1, Message o2) {
		if (o1 instanceof CommandMessage) {
			CommandMessage commandMessage = (CommandMessage)o1;

			String type = commandMessage.getCommandType();
			if (type.equals(CommandMessage
				.COMMAND_TYPE_SEND_PATCHES) ||
				type.equals(CommandMessage
					.COMMAND_TYPE_SEND_PORTAL_PROPERTIES)) {
				return 1;
			}
		}

		return -1;
	}
}