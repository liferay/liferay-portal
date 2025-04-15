/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.aui;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.aui.base.BaseFieldWrapperTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspWriter;

import java.util.Map;
import java.util.Objects;

/**
 * @author Julio Camarero
 * @author Jorge Ferrer
 * @author Brian Wing Shun Chan
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 */
@Deprecated
public class FieldWrapperTag extends BaseFieldWrapperTag {

	@Override
	protected String getEndPage() {
		if (Objects.equals(getInlineLabel(), "right")) {
			return super.getEndPage();
		}

		return null;
	}

	@Override
	protected String getStartPage() {
		if (Validator.isNotNull(getLabel()) &&
			!Objects.equals(getInlineLabel(), "right")) {

			return super.getStartPage();
		}

		return null;
	}

	@Override
	protected boolean isCleanUpSetAttributes() {
		return _CLEAN_UP_SET_ATTRIBUTES;
	}

	@Override
	protected int processEndTag() throws Exception {
		JspWriter jspWriter = pageContext.getOut();

		jspWriter.write("</div>");

		return EVAL_PAGE;
	}

	@Override
	protected int processStartTag() throws Exception {
		JspWriter jspWriter = pageContext.getOut();

		jspWriter.write("<div class=\"");

		String controlGroupCss = "lfr-ddm-field-group mb-3";

		if (getInlineField()) {
			controlGroupCss = controlGroupCss.concat(
				" align-middle d-inline-block lfr-ddm-field-group-inline");
		}

		if (Validator.isNotNull(getInlineLabel())) {
			controlGroupCss = controlGroupCss.concat(" form-inline");
		}

		jspWriter.write(controlGroupCss);

		jspWriter.write(StringPool.SPACE);

		jspWriter.write(
			AUIUtil.buildCss(
				"field-wrapper", getDisabled(), getFirst(), getLast(),
				getCssClass()));

		jspWriter.write("\" ");

		jspWriter.write(AUIUtil.buildData((Map<String, Object>)getData()));

		jspWriter.write(">");

		return EVAL_BODY_INCLUDE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		super.setAttributes(httpServletRequest);

		String label = getLabel();

		if (label == null) {
			label = TextFormatter.format(getName(), TextFormatter.K);
		}

		setNamespacedAttribute(httpServletRequest, "label", label);
	}

	private static final boolean _CLEAN_UP_SET_ATTRIBUTES = true;

}