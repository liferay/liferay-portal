/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharepoint.rest.repository.internal.document.library.repository.external.model;

import com.liferay.document.library.repository.external.ExtRepositoryFileEntry;
import com.liferay.document.library.repository.external.ExtRepositoryFileVersion;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.sharepoint.rest.repository.internal.helper.SharepointURLHelper;

import java.util.Date;

/**
 * @author Adolfo Pérez
 */
public class SharepointFileEntry
	implements ExtRepositoryFileEntry, SharepointModel {

	public SharepointFileEntry(
		String extRepositoryModelKey, String name, String title,
		Date createDate, Date modifiedDate, long size,
		String fileVersionExtRepositoryModelKey, String version, String owner,
		String checkedOutBy, long effectiveBasePermissionsBits,
		SharepointURLHelper sharepointURLHelper) {

		_extRepositoryModelKey = extRepositoryModelKey;
		_name = name;
		_title = title;
		_createDate = createDate;
		_modifiedDate = modifiedDate;
		_size = size;
		_fileVersionExtRepositoryModelKey = fileVersionExtRepositoryModelKey;
		_version = version;
		_owner = owner;
		_checkedOutBy = checkedOutBy;
		_effectiveBasePermissionsBits = effectiveBasePermissionsBits;
		_sharepointURLHelper = sharepointURLHelper;
	}

	@Override
	public ExtRepositoryFileVersion asFileVersion() {
		if (_extRepositoryFileVersion == null) {
			_extRepositoryFileVersion = new SharepointFileEntryFileVersion(
				this, _fileVersionExtRepositoryModelKey, _version);
		}

		return _extRepositoryFileVersion;
	}

	@Override
	public boolean containsPermission(
		ExtRepositoryPermission extRepositoryPermission) {

		if ((extRepositoryPermission == ExtRepositoryPermission.ACCESS) ||
			(extRepositoryPermission ==
				ExtRepositoryPermission.ADD_DISCUSSION) ||
			(extRepositoryPermission == ExtRepositoryPermission.ADD_DOCUMENT) ||
			(extRepositoryPermission == ExtRepositoryPermission.ADD_FOLDER) ||
			(extRepositoryPermission == ExtRepositoryPermission.ADD_SHORTCUT) ||
			(extRepositoryPermission ==
				ExtRepositoryPermission.ADD_SUBFOLDER) ||
			(extRepositoryPermission ==
				ExtRepositoryPermission.DELETE_DISCUSSION) ||
			(extRepositoryPermission == ExtRepositoryPermission.PERMISSIONS) ||
			(extRepositoryPermission ==
				ExtRepositoryPermission.UPDATE_DISCUSSION)) {

			return false;
		}

		int bitIndex = 0;

		if (extRepositoryPermission == ExtRepositoryPermission.DELETE) {
			bitIndex = 4;
		}
		else if (extRepositoryPermission == ExtRepositoryPermission.UPDATE) {
			bitIndex = 3;
		}
		else if (extRepositoryPermission == ExtRepositoryPermission.VIEW) {
			bitIndex = 1;
		}

		if ((bitIndex == 0) ||
			((_effectiveBasePermissionsBits & (1 << (bitIndex - 1))) == 0)) {

			return false;
		}

		return true;
	}

	@Override
	public String getCanonicalContentURL() {
		return _sharepointURLHelper.getFileEntryContentURL(this);
	}

	@Override
	public String getCheckedOutBy() {
		return _checkedOutBy;
	}

	@Override
	public Date getCreateDate() {
		return _createDate;
	}

	@Override
	public String getDescription() {
		return StringPool.BLANK;
	}

	@Override
	public ExtRepositoryFileEntry getExtRepositoryFileEntry() {
		return this;
	}

	@Override
	public String getExtRepositoryModelKey() {
		return _extRepositoryModelKey;
	}

	@Override
	public String getExtension() {
		return FileUtil.getExtension(_name);
	}

	@Override
	public String getMimeType() {
		return MimeTypesUtil.getContentType(_name);
	}

	@Override
	public Date getModifiedDate() {
		return _modifiedDate;
	}

	@Override
	public String getOwner() {
		return _owner;
	}

	@Override
	public long getSize() {
		return _size;
	}

	@Override
	public String getTitle() {
		return _title;
	}

	private final String _checkedOutBy;
	private final Date _createDate;
	private final long _effectiveBasePermissionsBits;
	private ExtRepositoryFileVersion _extRepositoryFileVersion;
	private final String _extRepositoryModelKey;
	private final String _fileVersionExtRepositoryModelKey;
	private final Date _modifiedDate;
	private final String _name;
	private final String _owner;
	private final SharepointURLHelper _sharepointURLHelper;
	private final long _size;
	private final String _title;
	private final String _version;

}