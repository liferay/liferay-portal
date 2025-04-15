/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.pop;

import jakarta.mail.Message;

import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public interface MessageListener {

	public boolean accept(
		String from, List<String> recipients, Message message);

	public void deliver(String from, List<String> recipients, Message message)
		throws MessageListenerException;

	public String getId();

}