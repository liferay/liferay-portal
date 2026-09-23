/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.lar;

/**
 * @author Petteri Karttunen
 */
public class ExportImportGroup {

	public ExportImportGroup(
		int childGroupsCount, String descriptiveName,
		String externalReferenceCode, long groupId,
		String parentGroupExternalReferenceCode, String path) {

		_childGroupsCount = childGroupsCount;
		_descriptiveName = descriptiveName;
		_externalReferenceCode = externalReferenceCode;
		_groupId = groupId;
		_parentGroupExternalReferenceCode = parentGroupExternalReferenceCode;
		_path = path;
	}

	public int getChildGroupsCount() {
		return _childGroupsCount;
	}

	public String getDescriptiveName() {
		return _descriptiveName;
	}

	public String getExternalReferenceCode() {
		return _externalReferenceCode;
	}

	public long getGroupId() {
		return _groupId;
	}

	public String getParentGroupExternalReferenceCode() {
		return _parentGroupExternalReferenceCode;
	}

	public String getPath() {
		return _path;
	}

	private final int _childGroupsCount;
	private final String _descriptiveName;
	private final String _externalReferenceCode;
	private final long _groupId;
	private final String _parentGroupExternalReferenceCode;
	private final String _path;

}