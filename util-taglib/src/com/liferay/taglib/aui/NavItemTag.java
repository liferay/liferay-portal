/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.aui;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.aui.base.BaseNavItemTag;
import com.liferay.taglib.util.TagResourceBundleUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyTag;

/**
 * @author Eduardo Lundgren
 * @author Bruno Basto
 * @author Nathan Cavanaugh
 * @author Julio Camarero
 * @author Carlos Sierra Andrés
 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
 *             com.liferay.frontend.taglib.clay.servlet.taglib.NavigationBarTag}
 */
@Deprecated
public class NavItemTag extends BaseNavItemTag implements BodyTag {

	@Override
	public int doStartTag() throws JspException {
		NavBarTag navBarTag = (NavBarTag)findAncestorWithClass(
			this, NavBarTag.class);

		if ((navBarTag != null) && getSelected()) {
			String title = getTitle();

			if (Validator.isNull(title)) {
				title = getLabel();
			}

			navBarTag.setSelectedItemName(title);
		}

		super.doStartTag();

		return BodyTag.EVAL_BODY_BUFFERED;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		super.setAttributes(httpServletRequest);

		if (!getUseDialog() && AUIUtil.isOpensNewWindow(getTarget())) {
			String title = getTitle();

			if (title == null) {
				title = StringPool.BLANK;
			}

			title = title.concat(
				LanguageUtil.get(
					TagResourceBundleUtil.getResourceBundle(pageContext),
					"opens-new-window"));

			setNamespacedAttribute(
				httpServletRequest, "title", String.valueOf(title));
		}
	}

}