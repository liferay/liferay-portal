/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.bar.web.internal.display.context;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.LayoutSetBranch;
import com.liferay.portal.kernel.model.LayoutSetBranchConstants;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Mariano Álvaro Sáiz
 */
public class LayoutSetBranchDisplayContext {

	public LayoutSetBranchDisplayContext(
		HttpServletRequest httpServletRequest) {

		_httpServletRequest = httpServletRequest;
	}

	public String getLayoutSetBranchDisplayName(
		LayoutSetBranch layoutSetBranch) {

		return getLayoutSetBranchDisplayName(layoutSetBranch.getName());
	}

	public String getLayoutSetBranchDisplayName(String layoutSetBranchName) {
		if (_shouldTranslateLayoutSetBranchName(layoutSetBranchName)) {
			return LanguageUtil.get(_httpServletRequest, layoutSetBranchName);
		}

		return layoutSetBranchName;
	}

	private boolean _shouldTranslateLayoutSetBranchName(
		LayoutSetBranch layoutSetBranch) {

		return _shouldTranslateLayoutSetBranchName(layoutSetBranch.getName());
	}

	private boolean _shouldTranslateLayoutSetBranchName(
		String layoutSetBranchName) {

		return LayoutSetBranchConstants.MASTER_BRANCH_NAME.equals(
			layoutSetBranchName);
	}

	private final HttpServletRequest _httpServletRequest;

}