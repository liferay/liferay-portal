/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.admin.web.internal.dao.search;

import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.service.SegmentsEntryRoleLocalServiceUtil;

import jakarta.portlet.RenderResponse;

/**
 * @author Pei-Jung Lan
 */
public class SegmentsEntryRoleChecker extends EmptyOnClickRowChecker {

	public SegmentsEntryRoleChecker(
		RenderResponse renderResponse, long roleId) {

		super(renderResponse);

		_roleId = roleId;
	}

	@Override
	public boolean isChecked(Object object) {
		SegmentsEntry segmentsEntry = (SegmentsEntry)object;

		return SegmentsEntryRoleLocalServiceUtil.hasSegmentEntryRole(
			segmentsEntry.getSegmentsEntryId(), _roleId);
	}

	@Override
	public boolean isDisabled(Object object) {
		return isChecked(object);
	}

	private final long _roleId;

}