/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.aui;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyHTMLRewriterUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.aui.base.BaseATag;
import com.liferay.taglib.util.InlineUtil;
import com.liferay.taglib.util.TagResourceBundleUtil;

import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.BodyContent;
import jakarta.servlet.jsp.tagext.BodyTag;

import java.io.CharArrayWriter;

import java.util.Map;

/**
 * @author Julio Camarero
 * @author Jorge Ferrer
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
 *             com.liferay.frontend.taglib.clay.servlet.taglib.LinkTag}
 */
@Deprecated
public class ATag extends BaseATag implements BodyTag {

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_charArrayWriter.reset();
	}

	@Override
	protected int processEndTag() throws Exception {
		JspWriter jspWriter = pageContext.getOut();

		BodyContent bodyContent = getBodyContent();

		if (bodyContent != null) {
			_charArrayWriter.write(bodyContent.getString());
		}

		if (Validator.isNotNull(getHref())) {
			if (AUIUtil.isOpensNewWindow(getTarget()) &&
				Validator.isNull(getIcon())) {

				HttpServletRequest httpServletRequest = getRequest();

				ThemeDisplay themeDisplay =
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				_charArrayWriter.write(StringPool.SPACE);
				_charArrayWriter.write("<svg class=\"lexicon-icon ");
				_charArrayWriter.write(
					"lexicon-icon-shortcut\" focusable=\"false\" ");
				_charArrayWriter.write("role=\"img\"><use href=\"");
				_charArrayWriter.write(themeDisplay.getPathThemeSpritemap());
				_charArrayWriter.write("#shortcut\" /><span ");
				_charArrayWriter.write("class=\"sr-only\">");

				String opensNewWindowLabel = LanguageUtil.get(
					TagResourceBundleUtil.getResourceBundle(pageContext),
					"opens-new-window");

				_charArrayWriter.write(opensNewWindowLabel);

				_charArrayWriter.write("</span>");
				_charArrayWriter.write("<title>");
				_charArrayWriter.write(opensNewWindowLabel);
				_charArrayWriter.write("</title>");
				_charArrayWriter.write("</svg>");
			}

			_charArrayWriter.write("</a>");
		}
		else {
			_charArrayWriter.write("</span>");
		}

		jspWriter.write(
			ContentSecurityPolicyHTMLRewriterUtil.rewriteInlineAttributes(
				_charArrayWriter.toString(), getRequest(), false));

		return EVAL_PAGE;
	}

	@Override
	protected int processStartTag() throws Exception {
		String ariaLabel = getAriaLabel();
		String ariaRole = getAriaRole();
		String cssClass = getCssClass();
		Map<String, Object> data = getData();
		String href = getHref();
		String id = getId();
		String iconCssClass = getIconCssClass();
		String label = getLabel();
		String lang = getLang();
		String onClick = getOnClick();
		String title = getTitle();

		if (Validator.isNotNull(href)) {
			_charArrayWriter.write("<a ");

			_charArrayWriter.write("href=\"");
			_charArrayWriter.write(HtmlUtil.escapeAttribute(href));
			_charArrayWriter.write("\" ");

			String target = getTarget();

			if (Validator.isNotNull(target)) {
				_charArrayWriter.write("target=\"");
				_charArrayWriter.write(target);
				_charArrayWriter.write("\" ");
			}
		}
		else {
			_charArrayWriter.write("<span ");
		}

		if (Validator.isNotNull(ariaLabel)) {
			_charArrayWriter.write("aria-label=\"");
			_charArrayWriter.write(ariaLabel);
			_charArrayWriter.write("\" ");
		}

		if (Validator.isNotNull(cssClass)) {
			_charArrayWriter.write("class=\"");
			_charArrayWriter.write(cssClass);
			_charArrayWriter.write("\" ");
		}

		if (Validator.isNotNull(id)) {
			_charArrayWriter.write("id=\"");
			_charArrayWriter.write(_getNamespace());
			_charArrayWriter.write(id);
			_charArrayWriter.write("\" ");
		}

		if (Validator.isNotNull(lang)) {
			_charArrayWriter.write("lang=\"");
			_charArrayWriter.write(lang);
			_charArrayWriter.write("\" ");
		}

		if (Validator.isNotNull(onClick)) {
			_charArrayWriter.write("onClick=\"");
			_charArrayWriter.write(HtmlUtil.escapeAttribute(onClick));
			_charArrayWriter.write("\" ");
		}

		if (Validator.isNotNull(ariaRole)) {
			_charArrayWriter.write("role=\"");
			_charArrayWriter.write(ariaRole);
			_charArrayWriter.write("\" ");
		}

		if (Validator.isNotNull(title)) {
			_charArrayWriter.write("title=\"");

			if (Validator.isNotNull(title)) {
				_charArrayWriter.write(
					LanguageUtil.get(
						TagResourceBundleUtil.getResourceBundle(pageContext),
						title));
			}

			_charArrayWriter.write("\" ");
		}

		if ((data != null) && !data.isEmpty()) {
			_charArrayWriter.write(AUIUtil.buildData(data));
		}

		String dynamicAttributesString = InlineUtil.buildDynamicAttributes(
			getDynamicAttributes());

		if (!dynamicAttributesString.isEmpty()) {
			_charArrayWriter.write(dynamicAttributesString);
		}

		_charArrayWriter.write(">");

		if (Validator.isNotNull(label)) {
			if (getLocalizeLabel()) {
				_charArrayWriter.write(
					LanguageUtil.get(
						TagResourceBundleUtil.getResourceBundle(pageContext),
						label));
			}
			else {
				_charArrayWriter.write(label);
			}
		}

		if (Validator.isNotNull(iconCssClass)) {
			_charArrayWriter.write("<span class=\"icon-monospaced ");
			_charArrayWriter.write(iconCssClass);
			_charArrayWriter.write("\"></span>");
		}

		return EVAL_BODY_BUFFERED;
	}

	private String _getNamespace() {
		HttpServletRequest httpServletRequest =
			(HttpServletRequest)pageContext.getRequest();

		PortletResponse portletResponse =
			(PortletResponse)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE);

		if (portletResponse == null) {
			return StringPool.BLANK;
		}

		if (GetterUtil.getBoolean(
				(String)httpServletRequest.getAttribute(
					"aui:form:useNamespace"),
				true)) {

			return portletResponse.getNamespace();
		}

		return StringPool.BLANK;
	}

	private final CharArrayWriter _charArrayWriter = new CharArrayWriter();

}