/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.ui;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.servlet.MultiSessionMessages;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.aui.ScriptTag;
import com.liferay.taglib.util.IncludeTag;
import com.liferay.taglib.util.TagResourceBundleUtil;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.BodyTag;

import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author Brian Wing Shun Chan
 */
public class SuccessTag extends IncludeTag implements BodyTag {

	@Override
	public int doEndTag() throws JspException {
		if (_hasMessage) {
			return super.doEndTag();
		}

		return EVAL_PAGE;
	}

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		HttpServletRequest httpServletRequest = getRequest();

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST);

		if (portletRequest == null) {
			if (SessionMessages.contains(httpServletRequest, _key)) {
				_hasMessage = true;

				return super.doStartTag();
			}
		}
		else if (MultiSessionMessages.contains(portletRequest, _key)) {
			_hasMessage = true;

			return super.doStartTag();
		}

		return SKIP_BODY;
	}

	public String getKey() {
		return _key;
	}

	public String getMessage() {
		return _message;
	}

	public String getTargetNode() {
		return _targetNode;
	}

	public int getTimeout() {
		return _timeout;
	}

	public boolean isEmbed() {
		return _embed;
	}

	public boolean isTranslateMessage() {
		return _translateMessage;
	}

	@Override
	public int processEndTag() throws Exception {
		String message = _message;

		String bodyContentString = null;

		Object bodyContent = getBodyContentWrapper();

		if (bodyContent != null) {
			bodyContentString = bodyContent.toString();
		}

		HttpServletRequest httpServletRequest = getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		ResourceBundle resourceBundle = TagResourceBundleUtil.getResourceBundle(
			httpServletRequest, themeDisplay.getLocale());

		if (Validator.isNotNull(bodyContentString)) {
			message = bodyContentString;
		}
		else if (_translateMessage) {
			message = LanguageUtil.get(resourceBundle, message);
		}

		Map<String, String> values = HashMapBuilder.put(
			"title", LanguageUtil.get(resourceBundle, "success")
		).build();

		if (_embed) {
			values.put("message", HtmlUtil.escape(message));

			String result = StringUtil.replace(
				_CONTENT_EMBED_TMPL, StringPool.DOLLAR, StringPool.DOLLAR,
				values);

			JspWriter jspWriter = pageContext.getOut();

			jspWriter.write(result);
		}
		else {
			values.put("message", HtmlUtil.escapeJS(message));

			String result = StringUtil.replace(
				_CONTENT_TOAST_TMPL, StringPool.DOLLAR, StringPool.DOLLAR,
				values);

			ScriptTag.doTag(
				null, null, null, result, getBodyContent(), pageContext);
		}

		return EVAL_PAGE;
	}

	public void setEmbed(boolean embed) {
		_embed = embed;
	}

	public void setKey(String key) {
		_key = key;
	}

	public void setMessage(String message) {
		_message = message;
	}

	public void setTargetNode(String targetNode) {
		_targetNode = targetNode;
	}

	public void setTimeout(int timeout) {
		_timeout = timeout;
	}

	public void setTranslateMessage(boolean translateMessage) {
		_translateMessage = translateMessage;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_embed = false;
		_hasMessage = false;
		_key = null;
		_message = null;
		_targetNode = null;
		_timeout = 5000;
		_translateMessage = true;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected boolean isCleanUpSetAttributes() {
		return super.isCleanUpSetAttributes();
	}

	@Override
	protected int processStartTag() throws Exception {
		return EVAL_BODY_BUFFERED;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
	}

	private static final String _ATTRIBUTE_NAMESPACE = "liferay-ui:success:";

	private static final String _CONTENT_EMBED_TMPL = StringUtil.read(
		SuccessTag.class, "success/embed.tmpl");

	private static final String _CONTENT_TOAST_TMPL = StringUtil.read(
		SuccessTag.class, "success/toast.tmpl");

	private static final String _PAGE = "/html/taglib/ui/success/page.jsp";

	private boolean _embed;
	private boolean _hasMessage;
	private String _key;
	private String _message;
	private String _targetNode;
	private int _timeout = 5000;
	private boolean _translateMessage = true;

}