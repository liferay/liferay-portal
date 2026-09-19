/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.conflict;

import com.liferay.portal.kernel.language.LanguageUtil;

import java.util.ResourceBundle;

/**
 * @author Samuel Trong Tran
 */
public class ModificationConflictInfo extends BaseConflictInfo {

	public ModificationConflictInfo(long modelClassPK, boolean resolved) {
		_modelClassPK = modelClassPK;
		_resolved = resolved;
	}

	@Override
	public long getCTAutoResolutionInfoId() {
		return _ctAutoResolutionInfoId;
	}

	@Override
	public String getConflictDescription(ResourceBundle resourceBundle) {
		return LanguageUtil.get(resourceBundle, "modification-conflict");
	}

	@Override
	public String getResolutionDescription(ResourceBundle resourceBundle) {
		String message = "the-conflict-cannot-be-automatically-resolved";

		if (isResolved()) {
			message = "the-conflict-was-automatically-resolved";
		}

		return LanguageUtil.get(resourceBundle, message);
	}

	@Override
	public long getSourcePrimaryKey() {
		return _modelClassPK;
	}

	@Override
	public long getTargetPrimaryKey() {
		return _modelClassPK;
	}

	@Override
	public boolean isResolved() {
		return _resolved;
	}

	public void setCtAutoResolutionInfoId(long ctAutoResolutionInfoId) {
		_ctAutoResolutionInfoId = ctAutoResolutionInfoId;
	}

	private long _ctAutoResolutionInfoId;
	private final long _modelClassPK;
	private final boolean _resolved;

}