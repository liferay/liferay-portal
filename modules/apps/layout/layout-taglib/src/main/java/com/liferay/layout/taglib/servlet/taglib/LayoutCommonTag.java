/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.taglib.servlet.taglib;

import com.liferay.frontend.taglib.clay.servlet.taglib.AlertTag;
import com.liferay.layout.taglib.internal.servlet.ServletContextUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.aui.ScriptTag;
import com.liferay.taglib.portletext.RuntimeTag;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class LayoutCommonTag extends IncludeTag {

	public boolean getDisplaySessionMessages() {
		return _displaySessionMessages;
	}

	public void setDisplaySessionMessages(boolean displaySessionMessages) {
		_displaySessionMessages = displaySessionMessages;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_displaySessionMessages = false;
		_includeStaticPortlets = false;
		_includeWebServerDisplayNode = false;
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
	protected int processEndTag() throws Exception {
		HttpServletRequest httpServletRequest = getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!themeDisplay.isStatePopUp()) {
			if (!themeDisplay.isStateExclusive() && !themeDisplay.isWidget()) {
				_includeStaticPortlets = true;
			}

			if (_WEB_SERVER_DISPLAY_NODE) {
				_includeWebServerDisplayNode = true;
			}
		}

		if (_includeStaticPortlets) {
			Company company = themeDisplay.getCompany();
			HttpServletResponse httpServletResponse =
				(HttpServletResponse)pageContext.getResponse();

			for (String portletId : _LAYOUT_STATIC_PORTLETS_ALL) {
				if (PortletLocalServiceUtil.hasPortlet(
						company.getCompanyId(), portletId)) {

					RuntimeTag.doTag(
						portletId, pageContext, httpServletRequest,
						httpServletResponse);
				}
			}
		}

		JspWriter jspWriter = pageContext.getOut();

		if (_includeWebServerDisplayNode) {
			jspWriter.write("<div class=\"alert-container cadmin container\">");
			jspWriter.write("<div class=\"alert-notifications ");
			jspWriter.write("alert-notifications-fixed\" ");
			jspWriter.write("id=\"WebServerDisplayNodeContainer\">");

			AlertTag alertTag = new AlertTag();

			alertTag.setDismissible(true);

			StringBundler sb = new StringBundler(6);

			sb.append(LanguageUtil.get(themeDisplay.getLocale(), "node"));
			sb.append(StringPool.COLON);
			sb.append(StringPool.SPACE);
			sb.append(StringUtil.toLowerCase(PortalUtil.getComputerName()));
			sb.append(StringPool.COLON);
			sb.append(PortalUtil.getPortalLocalPort(false));

			alertTag.setTitle(sb.toString());

			jspWriter.write(alertTag.doTagAsString(pageContext));

			jspWriter.write("</div></div>");
		}

		String scriptBodyContent = _getSessionMessagesScriptBodyContent();

		if (Validator.isNotNull(scriptBodyContent)) {
			ScriptTag.doTag(
				null, null, "liferay-util", scriptBodyContent, bodyContent,
				pageContext);
		}

		jspWriter.write("<form action=\"#\" aria-hidden=\"true\" ");
		jspWriter.write("class=\"hide\" id=\"hrefFm\" method=\"post\" ");
		jspWriter.write("name=\"hrefFm\"><span></span><button hidden ");
		jspWriter.write("type=\"submit\">");
		jspWriter.write(LanguageUtil.get(httpServletRequest, "hidden"));
		jspWriter.write("</button></form>");

		return EVAL_PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
	}

	private String _getScript(String message, String type) {
		StringBundler sb = new StringBundler(7);

		sb.append("Liferay.Util.openToast({autoClose: 10000, message: '");
		sb.append(HtmlUtil.escapeJS(message));
		sb.append("', title: '");
		sb.append(LanguageUtil.get(getRequest(), type));
		sb.append(":', type: '");
		sb.append(type);
		sb.append("',});");

		return sb.toString();
	}

	private String _getSessionMessagesScriptBodyContent() {
		if (!_displaySessionMessages) {
			return null;
		}

		HttpServletRequest httpServletRequest = getRequest();

		if (SessionMessages.isEmpty(httpServletRequest)) {
			return null;
		}

		StringBundler sb = new StringBundler();

		List<String> keys = new ArrayList<>();

		Iterator<String> iterator = SessionMessages.iterator(
			httpServletRequest);

		while (iterator.hasNext()) {
			String key = iterator.next();

			if (Validator.isNull(key)) {
				continue;
			}

			String message = GetterUtil.getString(
				SessionMessages.get(httpServletRequest, key));

			if (key.endsWith("requestProcessed")) {
				if (Validator.isNull(message) ||
					Objects.equals(message, "request_processed") ||
					Objects.equals(message, key)) {

					message = LanguageUtil.get(
						httpServletRequest,
						"your-request-completed-successfully");
				}

				sb.append(_getScript(message, "success"));

				keys.add(key);
			}
			else if (key.endsWith("_requestProcessedSuccess") &&
					 Validator.isNotNull(message)) {

				sb.append(_getScript(message, "success"));

				keys.add(key);
			}
			else if (key.endsWith("_requestProcessedWarning") &&
					 Validator.isNotNull(message)) {

				sb.append(_getScript(message, "warning"));

				keys.add(key);
			}
		}

		for (String key : keys) {
			SessionMessages.remove(httpServletRequest, key);
		}

		return sb.toString();
	}

	private static final String[] _LAYOUT_STATIC_PORTLETS_ALL =
		PropsUtil.getArray(PropsKeys.LAYOUT_STATIC_PORTLETS_ALL);

	private static final String _PAGE = "/layout_common/page.jsp";

	private static final boolean _WEB_SERVER_DISPLAY_NODE =
		GetterUtil.getBoolean(PropsUtil.get(PropsKeys.WEB_SERVER_DISPLAY_NODE));

	private boolean _displaySessionMessages;
	private boolean _includeStaticPortlets;
	private boolean _includeWebServerDisplayNode;

}