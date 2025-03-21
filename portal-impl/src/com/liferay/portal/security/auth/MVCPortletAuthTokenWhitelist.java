/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.auth;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.auth.BaseAuthTokenWhitelist;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Tomas Polesovsky
 */
public class MVCPortletAuthTokenWhitelist extends BaseAuthTokenWhitelist {

	public MVCPortletAuthTokenWhitelist() {
		trackWhitelistServices(
			"auth.token.ignore.mvc.action", MVCActionCommand.class,
			_portletCSRFWhitelist);
		trackWhitelistServices(
			"portlet.add.default.resource.check.whitelist.mvc.action",
			MVCActionCommand.class, _portletInvocationWhitelistAction);
		trackWhitelistServices(
			"portlet.add.default.resource.check.whitelist.mvc.action",
			MVCRenderCommand.class, _portletInvocationWhitelistRender);
		trackWhitelistServices(
			"portlet.add.default.resource.check.whitelist.mvc.action",
			MVCResourceCommand.class, _portletInvocationWhitelistResource);
	}

	@Override
	public boolean isPortletCSRFWhitelisted(
		HttpServletRequest httpServletRequest, Portlet portlet) {

		String portletId = portlet.getPortletId();

		String[] mvcActionCommandNames = getMVCActionCommandNames(
			httpServletRequest, portletId);

		return _containsAll(
			portletId, _portletCSRFWhitelist, mvcActionCommandNames);
	}

	@Override
	public boolean isPortletInvocationWhitelisted(
		HttpServletRequest httpServletRequest, Portlet portlet) {

		String portletId = portlet.getPortletId();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay.isLifecycleAction()) {
			String[] mvcActionCommandNames = getMVCActionCommandNames(
				httpServletRequest, portletId);

			return _containsAll(
				portletId, _portletInvocationWhitelistAction,
				mvcActionCommandNames);
		}
		else if (themeDisplay.isLifecycleRender()) {
			String namespace = PortalUtil.getPortletNamespace(portletId);

			String mvcRenderCommandName = httpServletRequest.getParameter(
				namespace.concat("mvcRenderCommandName"));

			return _contains(
				portletId, _portletInvocationWhitelistRender,
				mvcRenderCommandName);
		}
		else if (themeDisplay.isLifecycleResource()) {
			String ppid = httpServletRequest.getParameter("p_p_id");

			if (!portletId.equals(ppid)) {
				return false;
			}

			String mvcResourceCommandName = httpServletRequest.getParameter(
				"p_p_resource_id");

			return _contains(
				portletId, _portletInvocationWhitelistResource,
				mvcResourceCommandName);
		}

		return false;
	}

	@Override
	public boolean isPortletURLCSRFWhitelisted(
		LiferayPortletURL liferayPortletURL) {

		return _containsAll(
			liferayPortletURL.getPortletId(), _portletCSRFWhitelist,
			getMVCActionCommandNames(liferayPortletURL));
	}

	@Override
	public boolean isPortletURLPortletInvocationWhitelisted(
		LiferayPortletURL liferayPortletURL) {

		String portletId = liferayPortletURL.getPortletId();

		String lifecycle = liferayPortletURL.getLifecycle();

		if (lifecycle.equals(PortletRequest.ACTION_PHASE)) {
			return _containsAll(
				portletId, _portletInvocationWhitelistAction,
				getMVCActionCommandNames(liferayPortletURL));
		}
		else if (lifecycle.equals(PortletRequest.RENDER_PHASE)) {
			String mvcRenderCommandName = liferayPortletURL.getParameter(
				"mvcRenderCommandName");

			return _contains(
				portletId, _portletInvocationWhitelistRender,
				mvcRenderCommandName);
		}
		else if (lifecycle.equals(PortletRequest.RESOURCE_PHASE)) {
			String mvcResourceCommandName = liferayPortletURL.getResourceID();

			return _contains(
				portletId, _portletInvocationWhitelistResource,
				mvcResourceCommandName);
		}

		return false;
	}

	protected String[] getMVCActionCommandNames(
		HttpServletRequest httpServletRequest, String portletId) {

		String namespace = PortalUtil.getPortletNamespace(portletId);

		return httpServletRequest.getParameterValues(
			namespace.concat(ActionRequest.ACTION_NAME));
	}

	protected String[] getMVCActionCommandNames(
		LiferayPortletURL liferayPortletURL) {

		Map<String, String[]> parameterMap =
			liferayPortletURL.getParameterMap();

		return parameterMap.get(ActionRequest.ACTION_NAME);
	}

	protected String getWhitelistValue(
		String portletName, String whitelistAction) {

		return StringBundler.concat(
			portletName, StringPool.POUND, whitelistAction);
	}

	protected void trackWhitelistServices(
		String whitelistName, Class<?> serviceClass, Set<String> whiteList) {

		ServiceTracker<Object, Object> serviceTracker = new ServiceTracker<>(
			SystemBundleUtil.getBundleContext(),
			SystemBundleUtil.createFilter(
				StringBundler.concat(
					"(&(&(", whitelistName, "=*)(jakarta.portlet.name=*))",
					"(objectClass=", serviceClass.getName(), "))")),
			new TokenWhitelistTrackerCustomizer(whiteList));

		serviceTracker.open();

		serviceTrackers.add(serviceTracker);
	}

	private boolean _contains(
		String portletId, Set<String> whitelist, String item) {

		if (Validator.isBlank(item)) {
			return false;
		}

		String rootPortletId = PortletIdCodec.decodePortletName(portletId);

		return whitelist.contains(getWhitelistValue(rootPortletId, item));
	}

	private boolean _containsAll(
		String portletId, Set<String> whitelist, String[] items) {

		if (ArrayUtil.isEmpty(items)) {
			return false;
		}

		String rootPortletId = PortletIdCodec.decodePortletName(portletId);

		for (String action : items) {
			if (!whitelist.contains(getWhitelistValue(rootPortletId, action))) {
				return false;
			}
		}

		return true;
	}

	private final Set<String> _portletCSRFWhitelist = Collections.newSetFromMap(
		new ConcurrentHashMap<>());
	private final Set<String> _portletInvocationWhitelistAction =
		Collections.newSetFromMap(new ConcurrentHashMap<>());
	private final Set<String> _portletInvocationWhitelistRender =
		Collections.newSetFromMap(new ConcurrentHashMap<>());
	private final Set<String> _portletInvocationWhitelistResource =
		Collections.newSetFromMap(new ConcurrentHashMap<>());

	private class TokenWhitelistTrackerCustomizer
		implements ServiceTrackerCustomizer<Object, Object> {

		public TokenWhitelistTrackerCustomizer(Set<String> whitelist) {
			_whitelist = whitelist;
		}

		@Override
		public Object addingService(ServiceReference<Object> serviceReference) {
			Collection<String> whitelistValues = new ArrayList<>();

			List<String> whitelistActions = StringUtil.asList(
				serviceReference.getProperty("mvc.command.name"));

			List<String> portletNames = StringUtil.asList(
				serviceReference.getProperty("jakarta.portlet.name"));

			for (String portletName : portletNames) {
				for (String whitelistAction : whitelistActions) {
					whitelistValues.add(
						getWhitelistValue(portletName, whitelistAction));
				}
			}

			_whitelist.addAll(whitelistValues);

			return whitelistValues;
		}

		@Override
		public void modifiedService(
			ServiceReference<Object> serviceReference, Object object) {

			removedService(serviceReference, object);

			addingService(serviceReference);
		}

		@Override
		public void removedService(
			ServiceReference<Object> serviceReference, Object object) {

			Collection<String> whitelistValues = (Collection<String>)object;

			_whitelist.removeAll(whitelistValues);
		}

		private final Set<String> _whitelist;

	}

}