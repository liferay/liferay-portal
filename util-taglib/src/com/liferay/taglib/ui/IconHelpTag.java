/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.ui;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.taglib.util.TagResourceBundleUtil;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Scott Lee
 * @author Shuyang Zhou
 */
public class IconHelpTag extends IconTag {

	@Override
	protected String getPage() {
		return super.getPage();
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		setCssClass("ml-1 taglib-icon-help");
		setIcon("question-circle-full");
		setId(StringUtil.randomId());
		setLocalizeMessage(false);

		setMessage(
			LanguageUtil.get(
				TagResourceBundleUtil.getResourceBundle(pageContext),
				getMessage()));

		setToolTip(true);

		super.setAttributes(httpServletRequest);
	}

}