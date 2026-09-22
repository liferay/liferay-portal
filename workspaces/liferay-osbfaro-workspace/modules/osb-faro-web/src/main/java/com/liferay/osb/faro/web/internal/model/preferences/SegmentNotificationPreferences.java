/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.model.preferences;

import com.liferay.portal.kernel.util.GetterUtil;

/**
 * @author Caio Pinheiro
 */
public class SegmentNotificationPreferences {

	public SegmentNotificationPreferences() {
	}

	public SegmentNotificationPreferences(
		String emailFrequency, Boolean newMemberAdded) {

		_emailFrequency = emailFrequency;
		_newMemberAdded = newMemberAdded;
	}

	public String getEmailFrequency() {
		return _emailFrequency;
	}

	public Boolean getNewMemberAdded() {
		return GetterUtil.getBoolean(_newMemberAdded);
	}

	public void setEmailFrequency(String emailFrequency) {
		_emailFrequency = emailFrequency;
	}

	public void setNewMemberAdded(Boolean newMemberAdded) {
		_newMemberAdded = newMemberAdded;
	}

	private String _emailFrequency;
	private Boolean _newMemberAdded;

}