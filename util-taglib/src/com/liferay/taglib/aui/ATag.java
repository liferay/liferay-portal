/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.aui;

import com.liferay.petra.string.StringBundler;
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

		_sb.setIndex(0);
	}

	@Override
	protected int processEndTag() throws Exception {
		JspWriter jspWriter = pageContext.getOut();

		BodyContent bodyContent = getBodyContent();

		if (bodyContent != null) {
			_sb.append(bodyContent.getString());
		}

		if (Validator.isNotNull(getHref())) {
			if (AUIUtil.isOpensNewWindow(getTarget()) &&
				Validator.isNull(getIcon())) {

				HttpServletRequest httpServletRequest = getRequest();

				ThemeDisplay themeDisplay =
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				_sb.append(" <svg class=\"lexicon-icon ");
				_sb.append("lexicon-icon-shortcut\" focusable=\"false\" ");
				_sb.append("role=\"img\"><use href=\"");
				_sb.append(themeDisplay.getPathThemeSpritemap());
				_sb.append("#shortcut\" /><span class=\"sr-only\">");

				String opensNewWindowLabel = LanguageUtil.get(
					TagResourceBundleUtil.getResourceBundle(pageContext),
					"opens-new-window");

				_sb.append(opensNewWindowLabel);

				_sb.append("</span><title>");
				_sb.append(opensNewWindowLabel);
				_sb.append("</title></svg>");
			}

			_sb.append("</a>");
		}
		else {
			_sb.append("</span>");
		}

		jspWriter.write(
			ContentSecurityPolicyHTMLRewriterUtil.rewriteInlineAttributes(
				_sb.toString(), getRequest(), false));

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
			_sb.append("<a href=\"");
			_sb.append(HtmlUtil.escapeAttribute(href));
			_sb.append("\" ");

			String target = getTarget();

			if (Validator.isNotNull(target)) {
				_sb.append("target=\"");
				_sb.append(target);
				_sb.append("\" ");
			}
		}
		else {
			_sb.append("<span ");
		}

		if (Validator.isNotNull(ariaLabel)) {
			_sb.append("aria-label=\"");
			_sb.append(ariaLabel);
			_sb.append("\" ");
		}

		if (Validator.isNotNull(cssClass)) {
			_sb.append("class=\"");
			_sb.append(cssClass);
			_sb.append("\" ");
		}

		if (Validator.isNotNull(id)) {
			_sb.append("id=\"");
			_sb.append(_getNamespace());
			_sb.append(id);
			_sb.append("\" ");
		}

		if (Validator.isNotNull(lang)) {
			_sb.append("lang=\"");
			_sb.append(lang);
			_sb.append("\" ");
		}

		if (Validator.isNotNull(onClick)) {
			_sb.append("onClick=\"");
			_sb.append(HtmlUtil.escapeAttribute(onClick));
			_sb.append("\" ");
		}

		if (Validator.isNotNull(ariaRole)) {
			_sb.append("role=\"");
			_sb.append(ariaRole);
			_sb.append("\" ");
		}

		if (Validator.isNotNull(title)) {
			_sb.append("title=\"");

			if (Validator.isNotNull(title)) {
				_sb.append(
					LanguageUtil.get(
						TagResourceBundleUtil.getResourceBundle(pageContext),
						title));
			}

			_sb.append("\" ");
		}

		if ((data != null) && !data.isEmpty()) {
			_sb.append(AUIUtil.buildData(data));
		}

		String dynamicAttributesString = InlineUtil.buildDynamicAttributes(
			getDynamicAttributes());

		if (!dynamicAttributesString.isEmpty()) {
			_sb.append(dynamicAttributesString);
		}

		_sb.append(">");

		if (Validator.isNotNull(label)) {
			if (getLocalizeLabel()) {
				_sb.append(
					LanguageUtil.get(
						TagResourceBundleUtil.getResourceBundle(pageContext),
						label));
			}
			else {
				_sb.append(label);
			}
		}

		if (Validator.isNotNull(iconCssClass)) {
			_sb.append("<span class=\"icon-monospaced ");
			_sb.append(iconCssClass);
			_sb.append("\"></span>");
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

	private final StringBundler _sb = new StringBundler();

}