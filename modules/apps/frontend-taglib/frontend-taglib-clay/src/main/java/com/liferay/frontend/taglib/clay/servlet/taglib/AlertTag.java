/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.internal.servlet.taglib.BaseContainerTag;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyHTMLRewriterUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.taglib.util.TagResourceBundleUtil;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;

import java.util.Set;

/**
 * @author Chema Balsas
 */
public class AlertTag extends BaseContainerTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		setDynamicAttribute(StringPool.BLANK, "role", "alert");

		return super.doStartTag();
	}

	public boolean getAutoClose() {
		return _autoClose;
	}

	public boolean getDefaultTitleDisabled() {
		return _defaultTitleDisabled;
	}

	public boolean getDismissible() {
		return _dismissible;
	}

	public String getDisplayType() {
		return _displayType;
	}

	public String getMessage() {
		return _message;
	}

	public String getSymbol() {
		return _symbol;
	}

	public String getTitle() {
		return _title;
	}

	public String getVariant() {
		return _variant;
	}

	public void setAutoClose(boolean autoClose) {
		_autoClose = autoClose;
	}

	public void setDefaultTitleDisabled(boolean defaultTitleDisabled) {
		_defaultTitleDisabled = defaultTitleDisabled;
	}

	public void setDismissible(boolean dismissible) {
		_dismissible = dismissible;
	}

	public void setDisplayType(String displayType) {
		_displayType = displayType;
	}

	public void setMessage(String message) {
		_message = message;
	}

	public void setSymbol(String symbol) {
		_symbol = symbol;
	}

	public void setTitle(String title) {
		_title = title;
	}

	public void setVariant(String variant) {
		_variant = variant;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_autoClose = false;
		_defaultTitleDisabled = false;
		_dismissible = false;
		_displayType = "info";
		_message = null;
		_symbol = null;
		_title = null;
		_variant = null;
	}

	@Override
	protected String processCssClasses(Set<String> cssClasses) {
		cssClasses.add("alert");

		if (_dismissible) {
			cssClasses.add("alert-dismissible");
		}

		if (Validator.isNotNull(_variant) && _variant.equals("stripe")) {
			cssClasses.add("alert-fluid");
		}

		if (Validator.isNotNull(_displayType)) {
			cssClasses.add("alert-" + _displayType);
		}

		return super.processCssClasses(cssClasses);
	}

	@Override
	protected int processEndTag() throws Exception {
		JspWriter jspWriter = pageContext.getOut();

		jspWriter.write("</div></div>");

		if (_dismissible) {
			StringBundler sb = new StringBundler(7);

			sb.append("<button aria-label=\"");
			sb.append(
				LanguageUtil.get(
					TagResourceBundleUtil.getResourceBundle(pageContext),
					"close"));
			sb.append("\" class=\"close\" onclick=\"");
			sb.append("event.target.closest('[role=alert]').remove()\" ");
			sb.append("type=\"button\">");

			IconTag iconTag = new IconTag();

			iconTag.setSymbol("times");

			sb.append(iconTag.doTagAsString(pageContext));

			sb.append("</button>");

			jspWriter.write(
				ContentSecurityPolicyHTMLRewriterUtil.rewriteInlineAttributes(
					sb.toString(), getRequest(), false));
		}

		if (Validator.isNotNull(_variant) && _variant.equals("stripe")) {
			jspWriter.write("</div>");
		}

		jspWriter.write("</div>");

		return super.processEndTag();
	}

	@Override
	protected int processStartTag() throws Exception {
		super.processStartTag();

		JspWriter jspWriter = pageContext.getOut();

		if (Validator.isNotNull(_variant) && _variant.equals("stripe")) {
			jspWriter.write("<div class=\"container-fluid ");
			jspWriter.write("container-fluid-max-xl\">");
		}

		jspWriter.write("<div class=\"alert-autofit-row autofit-row\"><div ");
		jspWriter.write("class=\"autofit-col\"><div ");
		jspWriter.write("class=\"autofit-section\"><span ");
		jspWriter.write("class=\"alert-indicator\">");

		IconTag iconTag = new IconTag();

		if (Validator.isNotNull(_symbol)) {
			iconTag.setSymbol(_symbol);
		}
		else {
			iconTag.setSymbol(_getIcon(_displayType));
		}

		iconTag.doTag(pageContext);

		jspWriter.write("</span></div></div><div class=\"autofit-col ");
		jspWriter.write("autofit-col-expand\"><div class=\"autofit-section\">");

		if (_defaultTitleDisabled) {
			if (Validator.isNotNull(_title)) {
				jspWriter.write("<strong class=\"lead\">");
				jspWriter.write(
					LanguageUtil.get(
						TagResourceBundleUtil.getResourceBundle(pageContext),
						_title));
				jspWriter.write("</strong>");
			}
		}
		else {
			jspWriter.write("<strong class=\"lead\">");
			jspWriter.write(_getTitle(_title, _displayType));
			jspWriter.write(":</strong>");
		}

		if (Validator.isNotNull(_message)) {
			jspWriter.write(
				LanguageUtil.get(
					TagResourceBundleUtil.getResourceBundle(pageContext),
					_message));

			return SKIP_BODY;
		}

		return EVAL_BODY_INCLUDE;
	}

	private String _getIcon(String displayType) {
		if (displayType.equals("danger")) {
			return "exclamation-full";
		}
		else if (displayType.equals("success")) {
			return "check-circle-full";
		}
		else if (displayType.equals("warning")) {
			return "warning-full";
		}
		else if (displayType.equals("secondary")) {
			return "password-policies";
		}

		return "info-circle";
	}

	private String _getTitle(String title, String displayType) {
		if (Validator.isNull(title)) {
			title = displayType;
		}

		if (title.equals("danger")) {
			title = "error";
		}

		return LanguageUtil.get(
			TagResourceBundleUtil.getResourceBundle(pageContext), title);
	}

	private static final String _ATTRIBUTE_NAMESPACE = "clay:alert:";

	private boolean _autoClose;
	private boolean _defaultTitleDisabled;
	private boolean _dismissible;
	private String _displayType = "info";
	private String _message;
	private String _symbol;
	private String _title;
	private String _variant;

}