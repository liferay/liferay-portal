/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mentions.internal.util;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.SubscriptionSender;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Mikel Lorza
 */
public class MentionsSubscriptionSender extends SubscriptionSender {

	public void setDiscussionClassName(String discussionClassName) {
		_discussionClassName = discussionClassName;
	}

	public void setDiscussionClassPK(long discussionClassPK) {
		_discussionClassPK = discussionClassPK;
	}

	@Override
	protected void populateNotificationEventJSONObject(
		JSONObject notificationEventJSONObject) {

		super.populateNotificationEventJSONObject(notificationEventJSONObject);

		if (Validator.isNull(_discussionClassName)) {
			return;
		}

		notificationEventJSONObject.put(
			"discussionClassName", _discussionClassName
		).put(
			"discussionClassPK", _discussionClassPK
		);
	}

	private String _discussionClassName;
	private long _discussionClassPK;

}