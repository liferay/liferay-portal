/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.portletext;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletApp;
import com.liferay.portal.kernel.model.PortletWrapper;
import com.liferay.portal.kernel.portlet.PortletContainerUtil;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletLayoutListener;
import com.liferay.portal.kernel.portlet.PortletParameterUtil;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.RestrictPortletServletRequest;
import com.liferay.portal.kernel.portlet.constants.PortletPreferencesFactoryConstants;
import com.liferay.portal.kernel.portlet.render.PortletRenderParts;
import com.liferay.portal.kernel.portlet.render.PortletRenderUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.servlet.DynamicServletRequest;
import com.liferay.portal.kernel.servlet.PipingServletResponse;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.DirectTag;
import com.liferay.taglib.servlet.PipingServletResponseFactory;
import com.liferay.taglib.util.PortalIncludeUtil;
import com.liferay.taglib.util.ThreadLocalUtil;

import jakarta.portlet.GenericPortlet;
import jakarta.portlet.HeaderRequest;
import jakarta.portlet.HeaderResponse;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.TagSupport;

import java.lang.reflect.Method;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;

/**
 * @author Brian Wing Shun Chan
 */
public class RuntimeTag extends TagSupport implements DirectTag {

	public static void doTag(
			String portletName, PageContext pageContext,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		doTag(
			portletName, null, pageContext, httpServletRequest,
			httpServletResponse);
	}

	public static void doTag(
			String portletProviderClassName,
			PortletProvider.Action portletProviderAction, String instanceId,
			String queryString, String defaultPreferences,
			boolean persistSettings, PageContext pageContext,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		String portletId = PortletProviderUtil.getPortletId(
			portletProviderClassName, portletProviderAction);

		if (Validator.isNotNull(portletId)) {
			doTag(
				portletId, instanceId, queryString, _SETTINGS_SCOPE_DEFAULT,
				defaultPreferences, persistSettings, pageContext,
				httpServletRequest, httpServletResponse);
		}
		else {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			Layout layout = themeDisplay.getLayout();

			if (!layout.isTypeControlPanel() &&
				!LayoutPermissionUtil.contains(
					themeDisplay.getPermissionChecker(), layout,
					ActionKeys.UPDATE)) {

				return;
			}

			String errorMessage = LanguageUtil.format(
				httpServletRequest, "an-app-that-can-x-x-belongs-here",
				new Object[] {
					portletProviderAction.name(), portletProviderClassName
				},
				false);

			httpServletRequest.setAttribute(
				"liferay-portlet:runtime:errorMessage", errorMessage);

			PortalIncludeUtil.include(pageContext, _ERROR_PAGE);
		}
	}

	public static void doTag(
			String portletName, String queryString, PageContext pageContext,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		doTag(
			portletName, queryString, null, pageContext, httpServletRequest,
			httpServletResponse);
	}

	public static void doTag(
			String portletName, String queryString, String defaultPreferences,
			PageContext pageContext, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		doTag(
			portletName, StringPool.BLANK, queryString, _SETTINGS_SCOPE_DEFAULT,
			defaultPreferences, true, pageContext, httpServletRequest,
			httpServletResponse);
	}

	public static void doTag(
			String portletName, String instanceId, String queryString,
			String defaultPreferences, PageContext pageContext,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		doTag(
			portletName, instanceId, queryString, _SETTINGS_SCOPE_DEFAULT,
			defaultPreferences, true, pageContext, httpServletRequest,
			httpServletResponse);
	}

	public static void doTag(
			String portletName, String instanceId, String queryString,
			String settingsScope, String defaultPreferences,
			boolean persistSettings, PageContext pageContext,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		instanceId = PortalUtil.getJsSafePortletId(instanceId);

		if (pageContext != null) {
			if (httpServletResponse == pageContext.getResponse()) {
				httpServletResponse =
					PipingServletResponseFactory.createPipingServletResponse(
						pageContext);
			}
			else {
				httpServletResponse = new PipingServletResponse(
					httpServletResponse, pageContext.getOut());
			}
		}

		HttpServletRequest originalHttpServletRequest =
			PortalUtil.getOriginalServletRequest(httpServletRequest);

		RestrictPortletServletRequest restrictPortletServletRequest =
			new RestrictPortletServletRequest(originalHttpServletRequest);

		Map<String, String[]> parameterMap = new HashMap<>(
			httpServletRequest.getParameterMap());

		String portletInstanceKey = portletName;

		if (Validator.isNotNull(instanceId) && !instanceId.equals("0")) {
			portletInstanceKey = PortletIdCodec.encode(
				PortletIdCodec.decodePortletName(portletName),
				PortletIdCodec.decodeUserId(portletName), instanceId);
		}

		boolean resetLifecycleRender = false;

		if (!Objects.equals(
				portletInstanceKey,
				httpServletRequest.getParameter("p_p_id"))) {

			resetLifecycleRender = true;

			Set<String> keySet = parameterMap.keySet();

			keySet.removeIf(key -> key.startsWith("p_p_"));
		}

		String portletNamespace = PortalUtil.getPortletNamespace(
			portletInstanceKey);

		Map<String, String[]> originalParameterMap =
			originalHttpServletRequest.getParameterMap();

		for (Map.Entry<String, String[]> entry :
				originalParameterMap.entrySet()) {

			String key = entry.getKey();

			if (key.startsWith(portletNamespace)) {
				parameterMap.put(key, entry.getValue());
			}
		}

		if (Validator.isNotNull(queryString)) {
			queryString = PortletParameterUtil.addNamespace(
				portletInstanceKey, queryString);

			httpServletRequest = DynamicServletRequest.addQueryString(
				restrictPortletServletRequest, parameterMap, queryString,
				false);
		}
		else {
			httpServletRequest = new DynamicServletRequest(
				restrictPortletServletRequest, parameterMap, false);
		}

		try {
			httpServletRequest.setAttribute(
				WebKeys.RENDER_PORTLET_RESOURCE, Boolean.TRUE);

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			Portlet portlet = getPortlet(
				themeDisplay.getCompanyId(), portletInstanceKey);

			Stack<String> embeddedPortletIds = _embeddedPortletIds.get();

			if (embeddedPortletIds == null) {
				embeddedPortletIds = new Stack<>();

				_embeddedPortletIds.set(embeddedPortletIds);
			}

			String embeddedPortletId = portlet.getPortletId();

			if (embeddedPortletIds.search(embeddedPortletId) > -1) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"The application cannot include itself: " +
							embeddedPortletId);
				}

				String errorMessage = LanguageUtil.get(
					originalHttpServletRequest,
					"the-application-cannot-include-itself");

				originalHttpServletRequest.setAttribute(
					"liferay-portlet:runtime:errorMessage", errorMessage);

				PortalIncludeUtil.include(pageContext, _ERROR_PAGE);

				return;
			}

			if (themeDisplay.isStateMaximized()) {
				LayoutTypePortlet layoutTypePortlet =
					themeDisplay.getLayoutTypePortlet();

				if (layoutTypePortlet.hasStateMaxPortletId(
						portletInstanceKey)) {

					// A portlet in the maximized state has already been
					// processed

					return;
				}
			}

			Layout layout = themeDisplay.getLayout();

			httpServletRequest.setAttribute(
				WebKeys.SETTINGS_SCOPE, settingsScope);

			String layoutMode = ParamUtil.getString(
				httpServletRequest, "p_l_mode", Constants.VIEW);

			if (!layoutMode.equals(Constants.PREVIEW)) {
				if (persistSettings &&
					!layout.isPortletEmbedded(
						portlet.getPortletId(), layout.getGroupId())) {

					PortletPreferencesFactoryUtil.getLayoutPortletSetup(
						themeDisplay.getCompanyId(),
						themeDisplay.getScopeGroupId(),
						PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
						PortletKeys.PREFS_PLID_SHARED, portletInstanceKey,
						defaultPreferences);
				}

				if (persistSettings) {
					long count =
						PortletPreferencesLocalServiceUtil.
							getPortletPreferencesCount(
								PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
								themeDisplay.getPlid(), portletInstanceKey);

					if (count < 1) {
						PortletPreferencesFactoryUtil.getLayoutPortletSetup(
							layout, portletInstanceKey, defaultPreferences);

						PortletPreferencesFactoryUtil.getPortletSetup(
							httpServletRequest, portletInstanceKey,
							defaultPreferences);

						PortletLayoutListener portletLayoutListener =
							portlet.getPortletLayoutListenerInstance();

						if (portletLayoutListener != null) {
							portletLayoutListener.onAddToLayout(
								portletInstanceKey, themeDisplay.getPlid());
						}
					}
				}
			}

			PortletRenderParts portletRenderParts =
				PortletRenderUtil.getPortletRenderParts(
					httpServletRequest, StringPool.BLANK, portlet);

			PortletRenderUtil.writeHeaderPaths(
				httpServletResponse, portletRenderParts);

			embeddedPortletIds.push(embeddedPortletId);

			boolean lifecycleRender = themeDisplay.isLifecycleRender();

			try {
				if (resetLifecycleRender) {
					themeDisplay.setLifecycleRender(true);
				}

				if (_isHeaderPortlet(portlet)) {
					PortletContainerUtil.renderHeaders(
						httpServletRequest, httpServletResponse, portlet);
				}

				PortletContainerUtil.render(
					httpServletRequest, httpServletResponse, portlet);
			}
			finally {
				if (resetLifecycleRender) {
					themeDisplay.setLifecycleRender(lifecycleRender);
				}
			}

			embeddedPortletIds.pop();

			PortletRenderUtil.writeFooterPaths(
				httpServletResponse, portletRenderParts);
		}
		finally {
			restrictPortletServletRequest.mergeSharedAttributes();
		}
	}

	@Override
	public int doEndTag() throws JspException {
		try {
			HttpServletRequest httpServletRequest =
				(HttpServletRequest)pageContext.getRequest();

			Layout layout = (Layout)httpServletRequest.getAttribute(
				WebKeys.LAYOUT);

			if (layout == null) {
				return EVAL_PAGE;
			}

			HttpServletResponse httpServletResponse =
				(HttpServletResponse)pageContext.getResponse();

			if (Validator.isNotNull(_portletProviderClassName) &&
				(_portletProviderAction != null)) {

				doTag(
					_portletProviderClassName, _portletProviderAction,
					_instanceId, _queryString, _defaultPreferences,
					_persistSettings, pageContext, httpServletRequest,
					httpServletResponse);
			}
			else {
				doTag(
					_portletName, _instanceId, _queryString,
					_SETTINGS_SCOPE_DEFAULT, _defaultPreferences,
					_persistSettings, pageContext, httpServletRequest,
					httpServletResponse);
			}

			return EVAL_PAGE;
		}
		catch (Exception exception) {
			_log.error(exception);

			throw new JspException(exception);
		}
	}

	public void setDefaultPreferences(String defaultPreferences) {
		_defaultPreferences = defaultPreferences;
	}

	public void setInstanceId(String instanceId) {
		_instanceId = instanceId;
	}

	public void setPersistSettings(boolean persistSettings) {
		_persistSettings = persistSettings;
	}

	public void setPortletName(String portletName) {
		_portletName = portletName;
	}

	public void setPortletProviderAction(
		PortletProvider.Action portletProviderAction) {

		_portletProviderAction = portletProviderAction;
	}

	public void setPortletProviderClassName(String portletProviderClassName) {
		_portletProviderClassName = portletProviderClassName;
	}

	public void setQueryString(String queryString) {
		_queryString = queryString;
	}

	/**
	 * @deprecated As of Mueller (7.2.x), with no direct replacement
	 */
	@Deprecated
	public void setSettingsScope(String settingsScope) {
	}

	/**
	 * @see com.liferay.portal.model.impl.LayoutTypePortletImpl#getStaticPortlets(
	 *      String)
	 */
	protected static Portlet getPortlet(long companyId, String portletId)
		throws Exception {

		Portlet portlet = PortletLocalServiceUtil.getPortletById(
			companyId, portletId);

		// See LayoutTypePortletImpl#getStaticPortlets for why we only clone
		// non-instanceable portlets

		if (!portlet.isInstanceable()) {
			portlet = new PortletWrapper(portlet) {

				@Override
				public boolean getStatic() {
					return _staticPortlet;
				}

				@Override
				public boolean isStatic() {
					return _staticPortlet;
				}

				@Override
				public void setStatic(boolean staticPortlet) {
					_staticPortlet = staticPortlet;
				}

				private boolean _staticPortlet;

			};
		}

		portlet.setStatic(true);

		return portlet;
	}

	private static boolean _isHeaderPortlet(Portlet portlet) {
		PortletApp portletApp = portlet.getPortletApp();

		if (portletApp.getSpecMajorVersion() < 3) {
			return false;
		}

		String portletClassName = portlet.getPortletClass();

		if (Objects.equals(
				portletClassName,
				"jakarta.portlet.faces.GenericFacesPortlet")) {

			return true;
		}

		ServletContext servletContext = portletApp.getServletContext();

		if (servletContext == null) {
			return false;
		}

		ClassLoader classLoader = servletContext.getClassLoader();

		if (classLoader == null) {
			return false;
		}

		try {
			Class<?> portletClass = classLoader.loadClass(portletClassName);

			if (ClassUtil.isSubclass(
					portletClass,
					"jakarta.portlet.faces.GenericFacesPortlet")) {

				return true;
			}

			Method renderHeadersMethod = portletClass.getMethod(
				"renderHeaders", HeaderRequest.class, HeaderResponse.class);

			if (GenericPortlet.class !=
					renderHeadersMethod.getDeclaringClass()) {

				return true;
			}
		}
		catch (ClassNotFoundException classNotFoundException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to load portlet class " + portletClassName,
					classNotFoundException);
			}
		}
		catch (NoSuchMethodException noSuchMethodException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchMethodException);
			}
		}

		return false;
	}

	private static final String _ERROR_PAGE =
		"/html/taglib/portlet/runtime/error.jsp";

	private static final String _SETTINGS_SCOPE_DEFAULT =
		PortletPreferencesFactoryConstants.SETTINGS_SCOPE_PORTLET_INSTANCE;

	private static final Log _log = LogFactoryUtil.getLog(RuntimeTag.class);

	private static final ThreadLocal<Stack<String>> _embeddedPortletIds =
		ThreadLocalUtil.create(
			RuntimeTag.class, "_embeddedPortletIds",
			name -> new CentralizedThreadLocal<>(name));

	private String _defaultPreferences;
	private String _instanceId;
	private boolean _persistSettings = true;
	private String _portletName;
	private PortletProvider.Action _portletProviderAction;
	private String _portletProviderClassName;
	private String _queryString;

}