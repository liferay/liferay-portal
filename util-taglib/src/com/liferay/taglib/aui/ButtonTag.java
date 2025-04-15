/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.aui;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.taglib.aui.base.BaseButtonTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyTag;

/**
 * @author Julio Camarero
 * @author Jorge Ferrer
 * @author Brian Wing Shun Chan
 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
 *             com.liferay.frontend.taglib.clay.servlet.taglib.ButtonTag}
 */
@Deprecated
public class ButtonTag extends BaseButtonTag implements BodyTag {

	@Override
	public int doStartTag() throws JspException {
		super.doStartTag();

		return BodyTag.EVAL_BODY_BUFFERED;
	}

	@Override
	public void setIconAlign(String iconAlign) {
		if (iconAlign != null) {
			super.setIconAlign(StringUtil.toLowerCase(iconAlign));
		}
	}

	@Override
	protected boolean isCleanUpSetAttributes() {
		return _CLEAN_UP_SET_ATTRIBUTES;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		super.setAttributes(httpServletRequest);

		String value = getValue();

		if (value == null) {
			String type = getType();

			if (type.equals("submit")) {
				value = "save";
			}
			else if (type.equals("cancel")) {
				value = "cancel";
			}
			else if (type.equals("reset")) {
				value = "reset";
			}
		}

		setNamespacedAttribute(httpServletRequest, "value", value);
	}

	private static final boolean _CLEAN_UP_SET_ATTRIBUTES = true;

}