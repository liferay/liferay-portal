/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.search;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyHTMLRewriterUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public class EmptyOnClickRowChecker extends RowChecker {

	public EmptyOnClickRowChecker(PortletResponse portletResponse) {
		super(portletResponse);
	}

	@Override
	protected String getOnClick(
		String checkBoxRowIds, String checkBoxAllRowIds,
		String checkBoxPostOnClick) {

		return StringPool.BLANK;
	}

	@Override
	protected String getRowCheckBox(
		HttpServletRequest httpServletRequest, boolean checked,
		boolean disabled, String name, String value, String checkBoxRowIds,
		String checkBoxAllRowIds, String checkBoxPostOnClick) {

		return StringBundler.concat(
			"<div class=\"custom-checkbox custom-control\"><label>",
			_getInput(
				httpServletRequest, checked, disabled, name, value,
				checkBoxRowIds, checkBoxAllRowIds, checkBoxPostOnClick),
			"<span class=\"custom-control-label\"></span></label></div>");
	}

	private String _getInput(
		HttpServletRequest httpServletRequest, boolean checked,
		boolean disabled, String name, String value, String checkBoxRowIds,
		String checkBoxAllRowIds, String checkBoxPostOnClick) {

		StringBundler sb = new StringBundler(18);

		sb.append("<input ");

		String rowElementId = (String)httpServletRequest.getAttribute(
			"liferay-ui:search-container-row:rowElementId");

		if (rowElementId != null) {
			sb.append("aria-labelledby=\"");
			sb.append(rowElementId);
			sb.append("\" ");
		}

		if (checked) {
			sb.append("checked ");
		}

		sb.append("class=\"custom-control-input ");
		sb.append(getCssClass());
		sb.append("\" ");

		if (disabled) {
			sb.append("disabled ");
		}

		sb.append("name=\"");
		sb.append(name);
		sb.append("\" title=\"");
		sb.append(LanguageUtil.get(httpServletRequest.getLocale(), "select"));
		sb.append("\" type=\"checkbox\" value=\"");
		sb.append(HtmlUtil.escapeAttribute(value));
		sb.append("\" ");

		if (Validator.isNotNull(getAllRowIds())) {
			sb.append(
				getOnClick(
					checkBoxRowIds, checkBoxAllRowIds, checkBoxPostOnClick));
		}

		sb.append(">");

		return ContentSecurityPolicyHTMLRewriterUtil.rewriteInlineAttributes(
			sb.toString(), httpServletRequest, false);
	}

}