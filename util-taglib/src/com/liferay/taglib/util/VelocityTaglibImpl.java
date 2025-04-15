/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.servlet.DirectRequestDispatcherFactoryUtil;
import com.liferay.portal.kernel.servlet.JSPSupportServlet;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.TagSupport;
import com.liferay.taglib.portlet.ActionURLTag;
import com.liferay.taglib.portletext.IconOptionsTag;
import com.liferay.taglib.portletext.IconPortletTag;
import com.liferay.taglib.portletext.RuntimeTag;
import com.liferay.taglib.security.DoAsURLTag;
import com.liferay.taglib.security.PermissionsURLTag;
import com.liferay.taglib.servlet.PageContextWrapper;
import com.liferay.taglib.theme.MetaTagsTag;
import com.liferay.taglib.theme.WrapPortletTag;
import com.liferay.taglib.ui.IconHelpTag;
import com.liferay.taglib.ui.IconTag;
import com.liferay.taglib.ui.LanguageTag;

import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspFactory;
import jakarta.servlet.jsp.PageContext;

import java.io.Writer;

import java.util.Map;
import java.util.Set;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public class VelocityTaglibImpl implements VelocityTaglib {

	public VelocityTaglibImpl(
		ServletContext servletContext, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse,
		Map<String, Object> contextObjects) {

		_servletContext = servletContext;
		_httpServletRequest = httpServletRequest;
		_httpServletResponse = httpServletResponse;
		_contextObjects = contextObjects;

		JspFactory jspFactory = JspFactory.getDefaultFactory();

		_pageContext = jspFactory.getPageContext(
			new JSPSupportServlet(_servletContext), httpServletRequest,
			httpServletResponse, null, false, 0, false);
	}

	@Override
	public String actionURL(long plid, String portletName, String queryString)
		throws Exception {

		String windowState = WindowState.NORMAL.toString();
		String portletMode = PortletMode.VIEW.toString();

		return actionURL(
			windowState, portletMode, plid, portletName, queryString);
	}

	@Override
	public String actionURL(String portletName, String queryString)
		throws Exception {

		return actionURL(
			LayoutConstants.DEFAULT_PLID, portletName, queryString);
	}

	@Override
	public String actionURL(
			String windowState, String portletMode, Boolean secure,
			Boolean copyCurrentRenderParameters, Boolean escapeXml, String name,
			long plid, long refererPlid, String portletName, Boolean anchor,
			Boolean encrypt, long doAsGroupId, long doAsUserId,
			Boolean portletConfiguration, String queryString)
		throws Exception {

		String resourceID = null;
		String cacheability = null;
		Map<String, String[]> parameterMap =
			HttpComponentsUtil.parameterMapFromString(queryString);
		Set<String> removedParameterNames = null;

		PortletURL portletURL = ActionURLTag.doTag(
			PortletRequest.ACTION_PHASE, windowState, portletMode, secure,
			copyCurrentRenderParameters, escapeXml, name, resourceID,
			cacheability, plid, refererPlid, portletName, anchor, encrypt,
			doAsGroupId, doAsUserId, portletConfiguration, parameterMap,
			removedParameterNames, _httpServletRequest);

		return portletURL.toString();
	}

	@Override
	public String actionURL(
			String windowState, String portletMode, long plid,
			String portletName, String queryString)
		throws Exception {

		Boolean secure = null;
		Boolean copyCurrentRenderParameters = null;
		Boolean escapeXml = null;
		long refererPlid = LayoutConstants.DEFAULT_PLID;
		String name = null;
		Boolean anchor = null;
		Boolean encrypt = null;
		long doAsGroupId = 0;
		long doAsUserId = 0;
		Boolean portletConfiguration = null;

		return actionURL(
			windowState, portletMode, secure, copyCurrentRenderParameters,
			escapeXml, name, plid, refererPlid, portletName, anchor, encrypt,
			doAsGroupId, doAsUserId, portletConfiguration, queryString);
	}

	@Override
	public String actionURL(
			String windowState, String portletMode, String portletName,
			String queryString)
		throws Exception {

		return actionURL(
			windowState, portletMode, LayoutConstants.DEFAULT_PLID, portletName,
			queryString);
	}

	@Override
	public void doAsURL(long doAsUserId) throws Exception {
		DoAsURLTag.doTag(doAsUserId, _httpServletRequest);
	}

	@Override
	public IconTag getIconTag() throws Exception {
		IconTag iconTag = new IconTag();

		setUp(iconTag);

		return iconTag;
	}

	@Override
	public PageContext getPageContext() {
		return _pageContext;
	}

	@Override
	public String getSetting(String name) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return themeDisplay.getThemeSetting(name);
	}

	@Override
	public WindowState getWindowState(String windowState) {
		return new WindowState(windowState);
	}

	@Override
	public void icon(String image, boolean label, String message, String url)
		throws Exception {

		IconTag iconTag = new IconTag();

		setUp(iconTag);

		iconTag.setImage(image);
		iconTag.setLabel(label);
		iconTag.setMessage(message);
		iconTag.setUrl(url);

		iconTag.runTag();
	}

	@Override
	public void iconHelp(String message) throws Exception {
		IconHelpTag iconHelpTag = new IconHelpTag();

		setUp(iconHelpTag);

		iconHelpTag.setMessage(message);

		iconHelpTag.runTag();
	}

	@Override
	public void include(ServletContext servletContext, String page)
		throws Exception {

		RequestDispatcher requestDispatcher =
			servletContext.getRequestDispatcher(page);

		requestDispatcher.include(_httpServletRequest, _httpServletResponse);
	}

	@Override
	public void include(String page) throws Exception {
		RequestDispatcher requestDispatcher =
			DirectRequestDispatcherFactoryUtil.getRequestDispatcher(
				_servletContext, page);

		requestDispatcher.include(_httpServletRequest, _httpServletResponse);
	}

	@Override
	public void language() throws Exception {
		LanguageTag languageTag = new LanguageTag();

		setUp(languageTag);

		languageTag.runTag();
	}

	@Override
	public void language(
			String formName, String formAction, String name,
			String ddmTemplateKey)
		throws Exception {

		LanguageTag languageTag = new LanguageTag();

		setUp(languageTag);

		languageTag.setDdmTemplateKey(ddmTemplateKey);
		languageTag.setFormAction(formAction);
		languageTag.setFormName(formName);
		languageTag.setName(name);

		languageTag.runTag();
	}

	@Override
	public void language(
			String formName, String formAction, String name,
			String[] languageIds, String ddmTemplateKey)
		throws Exception {

		LanguageTag languageTag = new LanguageTag();

		setUp(languageTag);

		languageTag.setDdmTemplateKey(ddmTemplateKey);
		languageTag.setFormAction(formAction);
		languageTag.setFormName(formName);
		languageTag.setLanguageIds(languageIds);
		languageTag.setName(name);

		languageTag.runTag();
	}

	@Override
	public void metaTags() throws Exception {
		MetaTagsTag.doTag(
			_servletContext, _httpServletRequest, _httpServletResponse);
	}

	@Override
	public String permissionsURL(
			String redirect, String modelResource,
			String modelResourceDescription, Object resourceGroupId,
			String resourcePrimKey, String windowState, int[] roleTypes)
		throws Exception {

		return PermissionsURLTag.doTag(
			redirect, modelResource, modelResourceDescription, resourceGroupId,
			resourcePrimKey, windowState, roleTypes, _httpServletRequest);
	}

	@Override
	public void portletIconOptions() throws Exception {
		IconOptionsTag iconOptionsTag = new IconOptionsTag();

		setUp(iconOptionsTag);

		iconOptionsTag.runTag();
	}

	@Override
	public void portletIconOptions(String direction, String markupView)
		throws Exception {

		IconOptionsTag iconOptionsTag = new IconOptionsTag();

		setUp(iconOptionsTag);

		iconOptionsTag.setDirection(direction);
		iconOptionsTag.setMarkupView(markupView);

		iconOptionsTag.runTag();
	}

	@Override
	public void portletIconPortlet() throws Exception {
		IconPortletTag iconPortletTag = new IconPortletTag();

		setUp(iconPortletTag);

		iconPortletTag.runTag();
	}

	@Override
	public void portletIconPortlet(Portlet portlet) throws Exception {
		IconPortletTag iconPortletTag = new IconPortletTag();

		setUp(iconPortletTag);

		iconPortletTag.setPortlet(portlet);

		iconPortletTag.runTag();
	}

	@Override
	public String renderURL(long plid, String portletName, String queryString)
		throws Exception {

		String windowState = WindowState.NORMAL.toString();
		String portletMode = PortletMode.VIEW.toString();

		return renderURL(
			windowState, portletMode, plid, portletName, queryString);
	}

	@Override
	public String renderURL(String portletName, String queryString)
		throws Exception {

		return renderURL(
			LayoutConstants.DEFAULT_PLID, portletName, queryString);
	}

	@Override
	public String renderURL(
			String windowState, String portletMode, Boolean secure,
			Boolean copyCurrentRenderParameters, Boolean escapeXml, long plid,
			long refererPlid, String portletName, Boolean anchor,
			Boolean encrypt, long doAsGroupId, long doAsUserId,
			Boolean portletConfiguration, String queryString)
		throws Exception {

		String name = null;
		String resourceID = null;
		String cacheability = null;
		Map<String, String[]> parameterMap =
			HttpComponentsUtil.parameterMapFromString(queryString);
		Set<String> removedParameterNames = null;

		PortletURL portletURL = ActionURLTag.doTag(
			PortletRequest.RENDER_PHASE, windowState, portletMode, secure,
			copyCurrentRenderParameters, escapeXml, name, resourceID,
			cacheability, plid, refererPlid, portletName, anchor, encrypt,
			doAsGroupId, doAsUserId, portletConfiguration, parameterMap,
			removedParameterNames, _httpServletRequest);

		return portletURL.toString();
	}

	@Override
	public String renderURL(
			String windowState, String portletMode, long plid,
			String portletName, String queryString)
		throws Exception {

		Boolean secure = null;
		Boolean copyCurrentRenderParameters = null;
		Boolean escapeXml = null;
		long referPlid = LayoutConstants.DEFAULT_PLID;
		Boolean anchor = null;
		Boolean encrypt = null;
		long doAsGroupId = 0;
		long doAsUserId = 0;
		Boolean portletConfiguration = null;

		return renderURL(
			windowState, portletMode, secure, copyCurrentRenderParameters,
			escapeXml, plid, referPlid, portletName, anchor, encrypt,
			doAsGroupId, doAsUserId, portletConfiguration, queryString);
	}

	@Override
	public String renderURL(
			String windowState, String portletMode, String portletName,
			String queryString)
		throws Exception {

		return renderURL(
			windowState, portletMode, LayoutConstants.DEFAULT_PLID, portletName,
			queryString);
	}

	@Override
	public void runtime(String portletName) throws Exception {
		runtime(portletName, (String)null);
	}

	@Override
	public void runtime(
			String portletProviderClassName,
			PortletProvider.Action portletProviderAction)
		throws Exception {

		RuntimeTag.doTag(
			portletProviderClassName, portletProviderAction, StringPool.BLANK,
			null, null, true, _pageContext, _httpServletRequest,
			_httpServletResponse);
	}

	@Override
	public void runtime(
			String portletProviderClassName,
			PortletProvider.Action portletProviderAction, String instanceId)
		throws Exception {

		RuntimeTag.doTag(
			portletProviderClassName, portletProviderAction, instanceId, null,
			null, true, _pageContext, _httpServletRequest,
			_httpServletResponse);
	}

	@Override
	public void runtime(
			String portletProviderClassName,
			PortletProvider.Action portletProviderAction, String instanceId,
			String defaultPreferences)
		throws Exception {

		RuntimeTag.doTag(
			portletProviderClassName, portletProviderAction, instanceId, null,
			defaultPreferences, true, _pageContext, _httpServletRequest,
			_httpServletResponse);
	}

	@Override
	public void runtime(String portletName, String queryString)
		throws Exception {

		RuntimeTag.doTag(
			portletName, queryString, _pageContext, _httpServletRequest,
			_httpServletResponse);
	}

	@Override
	public void runtime(
			String portletName, String queryString, String defaultPreferences)
		throws Exception {

		RuntimeTag.doTag(
			portletName, queryString, defaultPreferences, _pageContext,
			_httpServletRequest, _httpServletResponse);
	}

	@Override
	public void runtime(
			String portletName, String instanceId, String queryString,
			String defaultPreferences)
		throws Exception {

		RuntimeTag.doTag(
			portletName, instanceId, queryString, defaultPreferences,
			_pageContext, _httpServletRequest, _httpServletResponse);
	}

	@Override
	public String wrapPortlet(String wrapPage, String portletPage)
		throws Exception {

		return WrapPortletTag.doTag(
			wrapPage, portletPage, _servletContext, _httpServletRequest,
			_httpServletResponse);
	}

	protected void setUp(TagSupport tagSupport) throws Exception {
		PageContextWrapper pageContextWrapper = new PageContextWrapper(
			_pageContext);

		Writer writer = null;

		if (_contextObjects != null) {
			writer = (Writer)_contextObjects.get(TemplateConstants.WRITER);
		}

		if (writer == null) {
			writer = _httpServletResponse.getWriter();
		}

		pageContextWrapper.pushBody(writer);

		tagSupport.setPageContext(pageContextWrapper);
	}

	private final Map<String, Object> _contextObjects;
	private final HttpServletRequest _httpServletRequest;
	private final HttpServletResponse _httpServletResponse;
	private final PageContext _pageContext;
	private final ServletContext _servletContext;

}