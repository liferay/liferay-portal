/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.web.internal.frontend.data.set.model;

import java.util.Date;

/**
 * @author Eudaldo Alonso
 */
public class FDSAudiencesEntry {

	public FDSAudiencesEntry(
		long audiencesEntryId, Date modifiedDate, String name,
		String userName) {

		_audiencesEntryId = audiencesEntryId;
		_modifiedDate = modifiedDate;
		_name = name;
		_userName = userName;
	}

	public long getAudiencesEntryId() {
		return _audiencesEntryId;
	}

	public Date getModifiedDate() {
		return _modifiedDate;
	}

	public String getName() {
		return _name;
	}

	public String getUserName() {
		return _userName;
	}

	private final long _audiencesEntryId;
	private final Date _modifiedDate;
	private final String _name;
	private final String _userName;

}