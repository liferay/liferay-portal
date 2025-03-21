/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.aui;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Shuyang Zhou
 */
public class AUIUtil {

	public static final String BUTTON_PREFIX = "btn";

	public static final String FIELD_PREFIX = "field";

	public static String buildControlGroupCss(
		boolean inlineField, String inlineLabel, String wrapperCssClass,
		String baseType) {

		StringBundler sb = new StringBundler(8);

		sb.append("form-group");

		if (inlineField) {
			sb.append(" form-group-inline");
		}

		if (Validator.isNotNull(inlineLabel)) {
			sb.append(" form-inline");
		}

		if (Validator.isNotNull(wrapperCssClass)) {
			sb.append(StringPool.SPACE);
			sb.append(wrapperCssClass);
		}

		if (Validator.isNotNull(baseType)) {
			sb.append(" input-");
			sb.append(baseType);
			sb.append("-wrapper");
		}

		return sb.toString();
	}

	public static String buildCss(
		String prefix, boolean disabled, boolean first, boolean last,
		String cssClass) {

		StringBundler sb = new StringBundler(7);

		sb.append(prefix);

		if (disabled) {
			sb.append(" disabled");
		}

		if (first) {
			sb.append(StringPool.SPACE);
			sb.append(prefix);
			sb.append("-first");
		}
		else if (last) {
			sb.append(StringPool.SPACE);
			sb.append(prefix);
			sb.append("-last");
		}

		if (Validator.isNotNull(cssClass)) {
			sb.append(StringPool.SPACE);
			sb.append(cssClass);
		}

		return sb.toString();
	}

	public static String buildData(Map<String, Object> data) {
		return HtmlUtil.buildData(data);
	}

	public static String buildLabel(
		String baseType, boolean inlineField, boolean showForLabel,
		String forLabel) {

		return buildLabel(baseType, inlineField, showForLabel, forLabel, false);
	}

	public static String buildLabel(
		String baseType, boolean inlineField, boolean showForLabel,
		String forLabel, boolean disabled) {

		return buildLabel(
			baseType, inlineField, showForLabel, forLabel, false,
			StringPool.BLANK);
	}

	public static String buildLabel(
		String baseType, boolean inlineField, boolean showForLabel,
		String forLabel, boolean disabled, String labelCssClass) {

		StringBundler sb = new StringBundler(10);

		if (baseType.equals("boolean")) {
			baseType = "checkbox";
		}

		sb.append("class=\"");

		if (baseType.equals("checkbox") || baseType.equals("radio")) {
			sb.append(labelCssClass);

			if (inlineField) {
				sb.append(StringPool.SPACE);
				sb.append(baseType);
				sb.append("-inline");
			}
		}
		else {
			sb.append(labelCssClass);
			sb.append(" control-label");
		}

		if (disabled) {
			sb.append(" disabled");
		}

		sb.append("\"");

		if (showForLabel) {
			sb.append(" for=\"");
			sb.append(HtmlUtil.escapeAttribute(forLabel));
			sb.append("\"");
		}

		return sb.toString();
	}

	public static Object getAttribute(
		HttpServletRequest httpServletRequest, String namespace, String key) {

		Map<String, Object> dynamicAttributes =
			(Map<String, Object>)httpServletRequest.getAttribute(
				namespace.concat("dynamicAttributes"));

		if ((dynamicAttributes != null) && dynamicAttributes.containsKey(key)) {
			return httpServletRequest.getAttribute(namespace.concat(key));
		}

		return null;
	}

	public static String getNamespace(HttpServletRequest httpServletRequest) {
		return GetterUtil.getString(
			httpServletRequest.getAttribute("aui:form:portletNamespace"));
	}

	public static String getNamespace(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		String namespace = StringPool.BLANK;

		if (portletRequest == null) {
			return namespace;
		}

		boolean auiFormUseNamespace = GetterUtil.getBoolean(
			(String)portletRequest.getAttribute("aui:form:useNamespace"), true);

		if ((portletResponse != null) && auiFormUseNamespace) {
			namespace = GetterUtil.getString(
				portletRequest.getAttribute("aui:form:portletNamespace"),
				portletResponse.getNamespace());
		}

		return namespace;
	}

	public static boolean isOpensNewWindow(String target) {
		if ((target != null) &&
			(target.equals("_blank") || target.equals("_new"))) {

			return true;
		}

		return false;
	}

	public static String normalizeId(String name) {
		char[] chars = null;

		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);

			if ((_VALID_CHARS.length <= c) || !_VALID_CHARS[c]) {
				if (chars == null) {
					chars = new char[name.length()];

					name.getChars(0, chars.length, chars, 0);
				}

				chars[i] = CharPool.DASH;
			}
		}

		if (chars == null) {
			return name;
		}

		return new String(chars);
	}

	private static final boolean[] _VALID_CHARS = new boolean[128];

	static {
		for (int i = 'a'; i <= 'z'; i++) {
			_VALID_CHARS[i] = true;
		}

		for (int i = 'A'; i <= 'Z'; i++) {
			_VALID_CHARS[i] = true;
		}

		for (int i = '0'; i <= '9'; i++) {
			_VALID_CHARS[i] = true;
		}

		_VALID_CHARS['-'] = true;
		_VALID_CHARS['_'] = true;
	}

}