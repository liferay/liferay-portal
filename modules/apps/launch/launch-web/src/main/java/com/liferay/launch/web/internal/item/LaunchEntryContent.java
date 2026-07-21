/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.web.internal.item;

import java.util.Date;

/**
 * @author David Truong
 */
public class LaunchEntryContent {

	public LaunchEntryContent(
		long groupId, Date modifiedDate, int status, String title,
		String typeName, String userName) {

		_groupId = groupId;
		_modifiedDate = modifiedDate;
		_status = status;
		_title = title;
		_typeName = typeName;
		_userName = userName;
	}

	public long getGroupId() {
		return _groupId;
	}

	public Date getModifiedDate() {
		return _modifiedDate;
	}

	public int getStatus() {
		return _status;
	}

	public String getTitle() {
		return _title;
	}

	public String getTypeName() {
		return _typeName;
	}

	public String getUserName() {
		return _userName;
	}

	private final long _groupId;
	private final Date _modifiedDate;
	private final int _status;
	private final String _title;
	private final String _typeName;
	private final String _userName;

}