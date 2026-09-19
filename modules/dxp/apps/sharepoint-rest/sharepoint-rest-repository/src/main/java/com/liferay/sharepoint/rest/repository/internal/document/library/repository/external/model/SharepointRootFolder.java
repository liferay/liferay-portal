/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharepoint.rest.repository.internal.document.library.repository.external.model;

import com.liferay.document.library.repository.external.ExtRepositoryFolder;
import com.liferay.petra.string.StringPool;

import java.util.Date;

/**
 * @author Adolfo Pérez
 */
public class SharepointRootFolder implements ExtRepositoryFolder {

	public SharepointRootFolder(String extRepositoryModelKey) {
		_extRepositoryModelKey = extRepositoryModelKey;
	}

	@Override
	public boolean containsPermission(
		ExtRepositoryPermission extRepositoryPermission) {

		return true;
	}

	@Override
	public Date getCreateDate() {
		return null;
	}

	@Override
	public String getDescription() {
		return StringPool.BLANK;
	}

	@Override
	public String getExtRepositoryModelKey() {
		return _extRepositoryModelKey;
	}

	@Override
	public String getExtension() {
		return StringPool.BLANK;
	}

	@Override
	public Date getModifiedDate() {
		return null;
	}

	@Override
	public String getName() {
		return StringPool.BLANK;
	}

	@Override
	public String getOwner() {
		return null;
	}

	@Override
	public long getSize() {
		return 0;
	}

	@Override
	public boolean isRoot() {
		return true;
	}

	private final String _extRepositoryModelKey;

}