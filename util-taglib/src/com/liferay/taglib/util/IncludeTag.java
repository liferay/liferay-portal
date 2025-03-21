/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.log.LogUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.portlet.PortletBag;
import com.liferay.portal.kernel.portlet.PortletBagPool;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.servlet.DirectRequestDispatcherFactoryUtil;
import com.liferay.portal.kernel.servlet.FileAvailabilityUtil;
import com.liferay.portal.kernel.servlet.taglib.TagDynamicIdFactory;
import com.liferay.portal.kernel.servlet.taglib.TagDynamicIdFactoryRegistry;
import com.liferay.portal.kernel.servlet.taglib.TagDynamicIncludeUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.CustomJspRegistryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.servlet.PipingServletResponseFactory;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyContent;

import java.io.IOException;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 * @author Eduardo Lundgren
 * @author Raymond Augé
 */
public class IncludeTag extends AttributesTagSupport {

	@Override
	public int doEndTag() throws JspException {
		try {
			String page = getPage();

			if (Validator.isNull(page)) {
				page = getEndPage();
			}

			callSetAttributes();

			if (themeResourceExists(page)) {
				doIncludeTheme(page);

				return EVAL_PAGE;
			}

			if (!FileAvailabilityUtil.isAvailable(getServletContext(), page)) {
				logUnavailablePage(page);

				return processEndTag();
			}

			doInclude(page, false);

			return EVAL_PAGE;
		}
		catch (Exception exception) {
			throw new JspException(exception);
		}
		finally {
			doClearTag();
		}
	}

	@Override
	public int doStartTag() throws JspException {
		try {
			String page = getStartPage();

			callSetAttributes();

			if (themeResourceExists(page)) {
				doIncludeTheme(page);

				return EVAL_BODY_INCLUDE;
			}

			if (!FileAvailabilityUtil.isAvailable(getServletContext(), page)) {
				logUnavailablePage(page);

				return processStartTag();
			}

			doInclude(page, true);

			return EVAL_BODY_INCLUDE;
		}
		catch (Exception exception) {
			throw new JspException(exception);
		}
	}

	public void runTag() throws JspException {
		doStartTag();
		doEndTag();
	}

	public void setPage(String page) {
		_page = page;
	}

	public void setPortletId(String portletId) {
		if (Validator.isNotNull(portletId)) {
			String rootPortletId = PortletIdCodec.decodePortletName(portletId);

			PortletBag portletBag = PortletBagPool.get(rootPortletId);

			setServletContext(portletBag.getServletContext());
		}
	}

	public void setStrict(boolean strict) {
		_strict = strict;
	}

	public void setUseCustomPage(boolean useCustomPage) {
		_useCustomPage = useCustomPage;
	}

	protected void callSetAttributes() {
		HttpServletRequest httpServletRequest = getOriginalServletRequest();

		if (isCleanUpSetAttributes()) {
			if (_setAttributeNames == null) {
				_setAttributeNames = new HashSet<>();
			}
			else {
				_setAttributeNames.clear();
			}

			_trackedRequest = new TrackedServletRequest(
				httpServletRequest, _setAttributeNames);

			httpServletRequest = _trackedRequest;
		}

		Class<? extends IncludeTag> clazz = getClass();

		httpServletRequest.setAttribute(clazz.getName(), this);

		setNamespacedAttribute(
			httpServletRequest, "bodyContent", getBodyContentWrapper());
		setNamespacedAttribute(
			httpServletRequest, "dynamicAttributes", getDynamicAttributes());

		setAttributes(httpServletRequest);
	}

	protected void cleanUp() {
	}

	protected void cleanUpSetAttributes() {
		if (isCleanUpSetAttributes()) {
			for (String name : _setAttributeNames) {
				_trackedRequest.removeAttribute(name);
			}

			_setAttributeNames.clear();

			_trackedRequest = null;
		}
	}

	protected void doClearTag() {
		clearDynamicAttributes();
		clearParams();
		clearProperties();

		cleanUpSetAttributes();

		setPage(null);
		setUseCustomPage(true);

		cleanUp();
	}

	protected void doInclude(
			String page, boolean dynamicIncludeAscendingPriority)
		throws JspException {

		try {
			include(page, dynamicIncludeAscendingPriority);
		}
		catch (Exception exception) {
			HttpServletRequest httpServletRequest = getRequest();

			String currentURL = (String)httpServletRequest.getAttribute(
				WebKeys.CURRENT_URL);

			String message = StringBundler.concat(
				"Current URL ", currentURL, " generates exception: ",
				exception.getMessage());

			LogUtil.log(_log, exception, message);

			if (exception instanceof JspException) {
				throw (JspException)exception;
			}
		}
	}

	protected void doIncludeTheme(String page) throws Exception {
		HttpServletResponse httpServletResponse =
			(HttpServletResponse)pageContext.getResponse();

		HttpServletRequest httpServletRequest = getRequest();

		Theme theme = (Theme)httpServletRequest.getAttribute(WebKeys.THEME);

		ThemeUtil.include(
			getServletContext(), httpServletRequest, httpServletResponse, page,
			theme);
	}

	protected Object getBodyContentWrapper() {
		final BodyContent bodyContent = getBodyContent();

		if (bodyContent == null) {
			return null;
		}

		return new Object() {

			@Override
			public String toString() {
				return bodyContent.getString();
			}

		};
	}

	protected String getCustomPage(
		ServletContext servletContext, HttpServletRequest httpServletRequest,
		String page) {

		if (Validator.isNull(page)) {
			return null;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay == null) {
			return null;
		}

		Group group = themeDisplay.getScopeGroup();

		if (group.isStagingGroup() && !group.isStagedRemotely()) {
			group = group.getLiveGroup();
		}

		UnicodeProperties typeSettingsUnicodeProperties =
			group.getTypeSettingsProperties();

		String customJspServletContextName =
			typeSettingsUnicodeProperties.getProperty(
				"customJspServletContextName");

		if (Validator.isNull(customJspServletContextName)) {
			return null;
		}

		if (customJspServletContextName.contains("/../")) {
			_log.error(
				"Illegal directory traversal in " +
					customJspServletContextName);

			return null;
		}

		String customPage = CustomJspRegistryUtil.getCustomJspFileName(
			customJspServletContextName, page);

		if (FileAvailabilityUtil.isAvailable(servletContext, customPage)) {
			return customPage;
		}

		return null;
	}

	protected String getEndPage() {
		return null;
	}

	protected HttpServletRequest getOriginalServletRequest() {
		return (HttpServletRequest)pageContext.getRequest();
	}

	protected String getPage() {
		return _page;
	}

	protected String getStartPage() {
		return null;
	}

	protected void include(String page, boolean doStartTag) throws Exception {
		HttpServletResponse httpServletResponse = null;

		Class<?> clazz = getClass();

		String tagClassName = clazz.getName();

		String tagDynamicId = null;

		String tagPointPrefix = null;

		if (doStartTag) {
			tagPointPrefix = "doStartTag#";
		}
		else {
			tagPointPrefix = "doEndTag#";
		}

		TagDynamicIdFactory tagDynamicIdFactory =
			TagDynamicIdFactoryRegistry.getTagDynamicIdFactory(tagClassName);

		HttpServletRequest httpServletRequest = getRequest();

		if (tagDynamicIdFactory != null) {
			httpServletResponse =
				PipingServletResponseFactory.createPipingServletResponse(
					pageContext);

			tagDynamicId = tagDynamicIdFactory.getTagDynamicId(
				httpServletRequest, httpServletResponse, this);

			TagDynamicIncludeUtil.include(
				httpServletRequest, httpServletResponse, tagClassName,
				tagDynamicId, tagPointPrefix + "before", doStartTag);
		}

		if (_useCustomPage) {
			String customPage = getCustomPage(
				getServletContext(), httpServletRequest, page);

			if (Validator.isNotNull(customPage)) {
				page = customPage;
			}
		}

		if (_THEME_JSP_OVERRIDE_ENABLED) {
			httpServletRequest.setAttribute(
				WebKeys.SERVLET_CONTEXT_INCLUDE_FILTER_STRICT, _strict);
		}

		includePage(
			page,
			PipingServletResponseFactory.createPipingServletResponse(
				pageContext));

		if (_THEME_JSP_OVERRIDE_ENABLED) {
			httpServletRequest.removeAttribute(
				WebKeys.SERVLET_CONTEXT_INCLUDE_FILTER_STRICT);
		}

		if (tagDynamicIdFactory != null) {
			TagDynamicIncludeUtil.include(
				httpServletRequest, httpServletResponse, tagClassName,
				tagDynamicId, tagPointPrefix + "after", doStartTag);
		}
	}

	protected void includePage(
			String page, HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		RequestDispatcher requestDispatcher =
			DirectRequestDispatcherFactoryUtil.getRequestDispatcher(
				getServletContext(), page);

		requestDispatcher.include(getRequest(), httpServletResponse);
	}

	protected boolean isCleanUpSetAttributes() {
		return _CLEAN_UP_SET_ATTRIBUTES;
	}

	protected boolean isPortalPage(String page) {
		if (page.startsWith("/html/taglib/") &&
			(page.endsWith("/end.jsp") || page.endsWith("/page.jsp") ||
			 page.endsWith("/start.jsp"))) {

			return true;
		}

		return false;
	}

	protected boolean isUseCustomPage() {
		return _useCustomPage;
	}

	protected void logUnavailablePage(String page) {
		if ((page == null) || !_log.isDebugEnabled()) {
			return;
		}

		StringBundler sb = new StringBundler(8);

		sb.append("Unable to find ");
		sb.append(page);
		sb.append(" in the context ");

		ServletContext servletContext = getServletContext();

		String contextPath = PortalUtil.getPathContext(
			servletContext.getContextPath());

		if (contextPath.equals(StringPool.BLANK)) {
			contextPath = StringPool.SLASH;
		}

		sb.append(contextPath);

		sb.append(".");

		boolean portalContext = false;

		String portalContextPath = PortalUtil.getPathContext();

		if (portalContextPath.equals(StringPool.BLANK)) {
			portalContextPath = StringPool.SLASH;
		}

		if (contextPath.equals(portalContextPath)) {
			portalContext = true;
		}

		if (isPortalPage(page)) {
			if (portalContext) {
				return;
			}

			sb.append(" You must not use a taglib from a module and set the ");
			sb.append("attribute \"servletContext\". Inline the content ");
			sb.append("directly where the taglib is invoked.");
		}
		else if (portalContext) {
			Class<?> clazz = getClass();

			if (clazz.equals(IncludeTag.class)) {
				sb.append(" You must set the attribute \"servletContext\" ");
				sb.append("with the value \"<%= application %>\" when ");
				sb.append("invoking a taglib from a module.");
			}
			else {
				sb.append(" You must not use a taglib from a module and set ");
				sb.append("the attribute \"file\". Inline the content ");
				sb.append("directly where the taglib is invoked.");
			}
		}

		_log.debug(sb.toString());
	}

	protected int processEndTag() throws Exception {
		return EVAL_PAGE;
	}

	protected int processStartTag() throws Exception {
		return EVAL_BODY_INCLUDE;
	}

	protected void setAttributes(HttpServletRequest httpServletRequest) {
	}

	protected boolean themeResourceExists(String page) throws Exception {
		if ((page == null) || !_THEME_JSP_OVERRIDE_ENABLED || _strict) {
			return false;
		}

		HttpServletRequest httpServletRequest = getRequest();

		Theme theme = (Theme)httpServletRequest.getAttribute(WebKeys.THEME);

		if (theme == null) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			if (themeDisplay != null) {
				theme = themeDisplay.getTheme();
			}
		}

		if (theme == null) {
			return false;
		}

		boolean exists = theme.resourceExists(
			getServletContext(), ThemeUtil.getPortletId(httpServletRequest),
			page);

		if (_log.isDebugEnabled() && exists) {
			_log.debug(theme.getResourcePath(getServletContext(), null, page));
		}

		return exists;
	}

	private static final boolean _CLEAN_UP_SET_ATTRIBUTES = false;

	private static final boolean _THEME_JSP_OVERRIDE_ENABLED =
		GetterUtil.getBoolean(
			PropsUtil.get(PropsKeys.THEME_JSP_OVERRIDE_ENABLED));

	private static final Log _log = LogFactoryUtil.getLog(IncludeTag.class);

	private String _page;
	private Set<String> _setAttributeNames;
	private boolean _strict;
	private TrackedServletRequest _trackedRequest;
	private boolean _useCustomPage = true;

	private static class TrackedServletRequest
		extends HttpServletRequestWrapper {

		@Override
		public void setAttribute(String name, Object object) {
			_setAttributeNames.add(name);

			super.setAttribute(name, object);
		}

		private TrackedServletRequest(
			HttpServletRequest httpServletRequest,
			Set<String> setAttributeNames) {

			super(httpServletRequest);

			_setAttributeNames = setAttributeNames;
		}

		private final Set<String> _setAttributeNames;

	}

}