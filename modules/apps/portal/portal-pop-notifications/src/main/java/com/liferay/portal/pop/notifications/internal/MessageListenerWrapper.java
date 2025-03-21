/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.pop.notifications.internal;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.pop.MessageListener;
import com.liferay.portal.kernel.pop.MessageListenerException;
import com.liferay.portal.kernel.util.ClassUtil;

import jakarta.mail.Message;

import java.util.List;
import java.util.Objects;

/**
 * @author Brian Wing Shun Chan
 */
public class MessageListenerWrapper implements MessageListener {

	public MessageListenerWrapper(MessageListener messageListener) {
		_messageListener = messageListener;
	}

	@Override
	public boolean accept(
		String from, List<String> recipients, Message message) {

		if (_log.isDebugEnabled()) {
			_log.debug("Listener " + ClassUtil.getClassName(_messageListener));
			_log.debug("From " + from);
			_log.debug("Recipients " + recipients.toString());
		}

		boolean value = _messageListener.accept(from, recipients, message);

		if (_log.isDebugEnabled()) {
			_log.debug("Accept " + value);
		}

		return value;
	}

	@Override
	public void deliver(String from, List<String> recipients, Message message)
		throws MessageListenerException {

		if (_log.isDebugEnabled()) {
			_log.debug("Listener " + ClassUtil.getClassName(_messageListener));
			_log.debug("From " + from);
			_log.debug("Recipients " + recipients.toString());
			_log.debug("Message " + message);
		}

		_messageListener.deliver(from, recipients, message);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof MessageListenerWrapper)) {
			return false;
		}

		MessageListenerWrapper messageListener = (MessageListenerWrapper)object;

		return Objects.equals(getId(), messageListener.getId());
	}

	@Override
	public String getId() {
		return _messageListener.getId();
	}

	public MessageListener getMessageListener() {
		return _messageListener;
	}

	@Override
	public int hashCode() {
		String id = _messageListener.getId();

		return id.hashCode();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MessageListenerWrapper.class);

	private final MessageListener _messageListener;

}