/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypeAccessPolicy;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.ActionResult;
import com.liferay.portal.kernel.portlet.PortletContainer;
import com.liferay.portal.kernel.portlet.PortletContainerException;
import com.liferay.portal.kernel.portlet.PortletContainerUtil;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.security.auth.AuthTokenWhitelistUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.TempAttributesServletRequest;
import com.liferay.portal.kernel.struts.LastPath;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.LayoutTypeAccessPolicyTracker;
import com.liferay.portal.util.PropsValues;

import jakarta.portlet.Event;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;

/**
 * @author Tomas Polesovsky
 * @author Raymond Augé
 */
public class SecurityPortletContainerWrapper implements PortletContainer {

	public SecurityPortletContainerWrapper(PortletContainer portletContainer) {
		_portletContainer = portletContainer;
	}

	@Override
	public void preparePortlet(
			HttpServletRequest httpServletRequest, Portlet portlet)
		throws PortletContainerException {

		_portletContainer.preparePortlet(httpServletRequest, portlet);
	}

	@Override
	public ActionResult processAction(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Portlet portlet)
		throws PortletContainerException {

		try {
			HttpServletRequest ownerLayoutHttpServletRequest =
				getOwnerLayoutRequestWrapper(httpServletRequest, portlet);

			checkAction(ownerLayoutHttpServletRequest, portlet);

			return _portletContainer.processAction(
				httpServletRequest, httpServletResponse, portlet);
		}
		catch (PrincipalException principalException) {
			return processActionException(
				httpServletRequest, httpServletResponse, portlet,
				principalException);
		}
		catch (PortletContainerException portletContainerException) {
			throw portletContainerException;
		}
		catch (Exception exception) {
			throw new PortletContainerException(exception);
		}
	}

	@Override
	public List<Event> processEvent(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Portlet portlet,
			Layout layout, Event event)
		throws PortletContainerException {

		return _portletContainer.processEvent(
			httpServletRequest, httpServletResponse, portlet, layout, event);
	}

	@Override
	public void processPublicRenderParameters(
		HttpServletRequest httpServletRequest, Layout layout) {

		_portletContainer.processPublicRenderParameters(
			httpServletRequest, layout);
	}

	@Override
	public void processPublicRenderParameters(
		HttpServletRequest httpServletRequest, Layout layout, Portlet portlet) {

		_portletContainer.processPublicRenderParameters(
			httpServletRequest, layout, portlet);
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Portlet portlet)
		throws PortletContainerException {

		try {
			checkRender(httpServletRequest, portlet);

			_portletContainer.render(
				httpServletRequest, httpServletResponse, portlet);
		}
		catch (PrincipalException principalException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(principalException);
			}

			processRenderException(
				httpServletRequest, httpServletResponse, portlet);
		}
		catch (PortletContainerException portletContainerException) {
			throw portletContainerException;
		}
		catch (Exception exception) {
			throw new PortletContainerException(exception);
		}
	}

	@Override
	public void renderHeaders(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Portlet portlet)
		throws PortletContainerException {

		try {
			checkRender(httpServletRequest, portlet);

			_portletContainer.renderHeaders(
				httpServletRequest, httpServletResponse, portlet);
		}
		catch (PrincipalException principalException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(principalException);
			}

			processRenderException(
				httpServletRequest, httpServletResponse, portlet);
		}
		catch (PortletContainerException portletContainerException) {
			throw portletContainerException;
		}
		catch (Exception exception) {
			throw new PortletContainerException(exception);
		}
	}

	@Override
	public void serveResource(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Portlet portlet)
		throws PortletContainerException {

		try {
			HttpServletRequest ownerLayoutHttpServletRequest =
				getOwnerLayoutRequestWrapper(httpServletRequest, portlet);

			checkResource(ownerLayoutHttpServletRequest, portlet);

			_portletContainer.serveResource(
				httpServletRequest, httpServletResponse, portlet);
		}
		catch (PrincipalException principalException) {
			processServeResourceException(
				httpServletRequest, httpServletResponse, portlet,
				principalException);
		}
		catch (PortletContainerException portletContainerException) {
			throw portletContainerException;
		}
		catch (Exception exception) {
			throw new PortletContainerException(exception);
		}
	}

	protected void check(HttpServletRequest httpServletRequest, Portlet portlet)
		throws Exception {

		if (portlet == null) {
			return;
		}

		if (!isValidPortletId(portlet.getPortletId())) {
			if (_log.isWarnEnabled()) {
				_log.warn("Invalid portlet ID " + portlet.getPortletId());
			}

			throw new PrincipalException(
				"Invalid portlet ID " + portlet.getPortletId());
		}

		if (portlet.isUndeployedPortlet()) {
			return;
		}

		Layout layout = (Layout)httpServletRequest.getAttribute(WebKeys.LAYOUT);

		LayoutTypeAccessPolicy layoutTypeAccessPolicy =
			LayoutTypeAccessPolicyTracker.getLayoutTypeAccessPolicy(layout);

		layoutTypeAccessPolicy.checkAccessAllowedToPortlet(
			httpServletRequest, layout, portlet);
	}

	protected void checkAction(
			HttpServletRequest httpServletRequest, Portlet portlet)
		throws Exception {

		checkCSRFProtection(httpServletRequest, portlet);

		check(httpServletRequest, portlet);
	}

	protected void checkCSRFProtection(
			HttpServletRequest httpServletRequest, Portlet portlet)
		throws PortalException {

		Map<String, String> initParams = portlet.getInitParams();

		boolean checkAuthToken = GetterUtil.getBoolean(
			initParams.get("check-auth-token"), true);

		if (AuthTokenWhitelistUtil.isPortletCSRFWhitelisted(
				httpServletRequest, portlet)) {

			checkAuthToken = false;
		}

		if (checkAuthToken) {
			AuthTokenUtil.checkCSRFToken(
				httpServletRequest,
				SecurityPortletContainerWrapper.class.getName());
		}
	}

	protected void checkRender(
			HttpServletRequest httpServletRequest, Portlet portlet)
		throws Exception {

		check(httpServletRequest, portlet);
	}

	protected void checkResource(
			HttpServletRequest httpServletRequest, Portlet portlet)
		throws Exception {

		check(httpServletRequest, portlet);
	}

	protected String getOriginalURL(HttpServletRequest httpServletRequest) {
		LastPath lastPath = (LastPath)httpServletRequest.getAttribute(
			WebKeys.LAST_PATH);

		if (lastPath == null) {
			return String.valueOf(httpServletRequest.getRequestURI());
		}

		return StringBundler.concat(
			PortalUtil.getPortalURL(httpServletRequest),
			lastPath.getContextPath(), lastPath.getPath());
	}

	protected HttpServletRequest getOwnerLayoutRequestWrapper(
			HttpServletRequest httpServletRequest, Portlet portlet)
		throws Exception {

		if (!PropsValues.PORTLET_EVENT_DISTRIBUTION_LAYOUT_SET ||
			PropsValues.PORTLET_CROSS_LAYOUT_INVOCATION_MODE.equals("render")) {

			return httpServletRequest;
		}

		Layout ownerLayout = null;
		LayoutTypePortlet ownerLayoutTypePortlet = null;

		Layout requestLayout = (Layout)httpServletRequest.getAttribute(
			WebKeys.LAYOUT);

		List<LayoutTypePortlet> layoutTypePortlets =
			PortletContainerUtil.getLayoutTypePortlets(requestLayout);

		for (LayoutTypePortlet layoutTypePortlet : layoutTypePortlets) {
			if (layoutTypePortlet.hasPortletId(portlet.getPortletId())) {
				ownerLayoutTypePortlet = layoutTypePortlet;

				ownerLayout = layoutTypePortlet.getLayout();

				break;
			}
		}

		if (ownerLayout == null) {
			return httpServletRequest;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout currentLayout = themeDisplay.getLayout();

		if (currentLayout.equals(ownerLayout)) {
			return httpServletRequest;
		}

		ThemeDisplay themeDisplayClone = (ThemeDisplay)themeDisplay.clone();

		themeDisplayClone.setLayout(ownerLayout);
		themeDisplayClone.setLayoutTypePortlet(ownerLayoutTypePortlet);

		TempAttributesServletRequest tempAttributesServletRequest =
			new TempAttributesServletRequest(httpServletRequest);

		tempAttributesServletRequest.setTempAttribute(
			WebKeys.LAYOUT, ownerLayout);
		tempAttributesServletRequest.setTempAttribute(
			WebKeys.THEME_DISPLAY, themeDisplayClone);

		return tempAttributesServletRequest;
	}

	protected boolean isValidPortletId(String portletId) {
		for (int i = 0; i < portletId.length(); i++) {
			char c = portletId.charAt(i);

			if ((c >= CharPool.LOWER_CASE_A) && (c <= CharPool.LOWER_CASE_Z)) {
				continue;
			}

			if ((c >= CharPool.UPPER_CASE_A) && (c <= CharPool.UPPER_CASE_Z)) {
				continue;
			}

			if ((c >= CharPool.NUMBER_0) && (c <= CharPool.NUMBER_9)) {
				continue;
			}

			if ((c == CharPool.DOLLAR) || (c == CharPool.POUND) ||
				(c == CharPool.UNDERLINE)) {

				continue;
			}

			return false;
		}

		return true;
	}

	protected ActionResult processActionException(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, Portlet portlet,
		PrincipalException principalException) {

		if (_log.isDebugEnabled()) {
			_log.debug(principalException);
		}

		if (_log.isWarnEnabled() &&
			!(principalException instanceof
				PrincipalException.MustHaveSessionCSRFToken)) {

			String url = getOriginalURL(httpServletRequest);

			_log.warn(
				String.format(
					"User %s is not allowed to access URL %s and portlet %s: " +
						"%s",
					PortalUtil.getUserId(httpServletRequest), url,
					portlet.getPortletId(), principalException.getMessage()));
		}

		httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);

		return ActionResult.EMPTY_ACTION_RESULT;
	}

	protected void processRenderException(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, Portlet portlet)
		throws PortletContainerException {

		String portletContent = null;

		if (portlet.isShowPortletAccessDenied()) {
			portletContent = "/html/portal/portlet_access_denied.jsp";
		}

		try {
			if (portletContent != null) {
				HttpServletRequest originalHttpServletRequest =
					PortalUtil.getOriginalServletRequest(httpServletRequest);

				RequestDispatcher requestDispatcher =
					originalHttpServletRequest.getRequestDispatcher(
						portletContent);

				requestDispatcher.include(
					httpServletRequest, httpServletResponse);
			}
		}
		catch (Exception exception) {
			throw new PortletContainerException(exception);
		}
	}

	protected void processServeResourceException(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, Portlet portlet,
		PrincipalException principalException) {

		if (_log.isDebugEnabled()) {
			_log.debug(principalException);
		}

		httpServletResponse.setHeader(
			HttpHeaders.CACHE_CONTROL,
			HttpHeaders.CACHE_CONTROL_NO_CACHE_VALUE);
		httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);

		if (principalException instanceof
				PrincipalException.MustHaveSessionCSRFToken) {

			return;
		}

		if (_log.isWarnEnabled()) {
			String url = getOriginalURL(httpServletRequest);

			_log.warn(
				String.format(
					"User %s is not allowed to serve resource for %s on %s: %s",
					PortalUtil.getUserId(httpServletRequest), url,
					portlet.getPortletId(), principalException.getMessage()));
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SecurityPortletContainerWrapper.class);

	private final PortletContainer _portletContainer;

}