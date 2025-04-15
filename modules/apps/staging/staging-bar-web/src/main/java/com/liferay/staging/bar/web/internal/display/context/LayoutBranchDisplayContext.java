/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.bar.web.internal.display.context;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.LayoutBranch;
import com.liferay.portal.kernel.model.LayoutBranchConstants;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Mariano Álvaro Sáiz
 */
public class LayoutBranchDisplayContext {

	public LayoutBranchDisplayContext(HttpServletRequest httpServletRequest) {
		_httpServletRequest = httpServletRequest;
	}

	public String getLayoutBranchDisplayName(LayoutBranch layoutBranch) {
		return getLayoutBranchDisplayName(layoutBranch.getName());
	}

	public String getLayoutBranchDisplayName(String layoutBranchName) {
		if (_shouldTranslateLayoutBranchName(layoutBranchName)) {
			return LanguageUtil.get(_httpServletRequest, layoutBranchName);
		}

		return layoutBranchName;
	}

	private boolean _shouldTranslateLayoutBranchName(
		LayoutBranch layoutBranch) {

		return _shouldTranslateLayoutBranchName(layoutBranch.getName());
	}

	private boolean _shouldTranslateLayoutBranchName(String layoutBranchName) {
		return LayoutBranchConstants.MASTER_BRANCH_NAME.equals(
			layoutBranchName);
	}

	private final HttpServletRequest _httpServletRequest;

}