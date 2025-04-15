/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.security;

import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.WindowStateFactory;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.configuration.kernel.util.PortletConfigurationApplicationType;

import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.TagSupport;

/**
 * @author Brian Wing Shun Chan
 */
public class PermissionsURLTag extends TagSupport {

	public static String doTag(
			String redirect, String modelResource, Object resourceGroupId,
			String windowState, HttpServletRequest httpServletRequest)
		throws Exception {

		redirect = _getRedirect(httpServletRequest, redirect, windowState);

		return PortletURLBuilder.create(
			_getPorletURL(
				httpServletRequest, modelResource, resourceGroupId, windowState)
		).setRedirect(
			redirect
		).setParameter(
			"returnToFullPageURL", redirect
		).buildString();
	}

	/**
	 * Returns the URL for opening the resource's permissions configuration
	 * dialog and for configuring the resource's permissions.
	 *
	 * @param  redirect the redirect. If the redirect is <code>null</code> or
	 *         the dialog does not open as a pop-up, the current URL is obtained
	 *         via {@link PortalUtil#getCurrentURL(HttpServletRequest)} and
	 *         used.
	 * @param  modelResource the resource's class for which to configure
	 *         permissions
	 * @param  modelResourceDescription the human-friendly description of the
	 *         resource
	 * @param  resourceGroupId the group ID to which the resource belongs. The
	 *         ID can be a number, string containing a number, or substitution
	 *         string. If the resource group ID is <code>null</code>, it is
	 *         obtained via {@link ThemeDisplay#getScopeGroupId()}.
	 * @param  resourcePrimKey the primary key of the resource
	 * @param  windowState the window state to use when opening the permissions
	 *         configuration dialog. For more information, see {@link
	 *         LiferayWindowState}.
	 * @param  roleTypes the role types
	 * @param  httpServletRequest the current request
	 * @return the URL for opening the resource's permissions configuration
	 *         dialog and for configuring the resource's permissions
	 * @throws Exception if an exception occurred
	 */
	public static String doTag(
			String redirect, String modelResource,
			String modelResourceDescription, Object resourceGroupId,
			String resourcePrimKey, String windowState, int[] roleTypes,
			HttpServletRequest httpServletRequest)
		throws Exception {

		return PortletURLBuilder.create(
			_getPorletURL(
				httpServletRequest, modelResource, resourceGroupId, windowState)
		).setRedirect(
			_getRedirect(httpServletRequest, redirect, windowState)
		).setParameter(
			"modelResourceDescription", modelResourceDescription
		).setParameter(
			"resourcePrimKey", resourcePrimKey
		).setParameter(
			"returnToFullPageURL",
			() -> {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				if (!themeDisplay.isStateMaximized()) {
					return _getRedirect(
						httpServletRequest, redirect, windowState);
				}

				return null;
			}
		).setParameter(
			"roleTypes",
			() -> {
				if (roleTypes != null) {
					return StringUtil.merge(roleTypes);
				}

				return null;
			}
		).buildString();
	}

	@Override
	public int doEndTag() throws JspException {
		try {
			String portletURLToString = doTag(
				_redirect, _modelResource, _modelResourceDescription,
				_resourceGroupId, _resourcePrimKey, _windowState, _roleTypes,
				(HttpServletRequest)pageContext.getRequest());

			if (Validator.isNotNull(_var)) {
				pageContext.setAttribute(_var, portletURLToString);
			}
			else {
				JspWriter jspWriter = pageContext.getOut();

				jspWriter.write(portletURLToString);
			}
		}
		catch (Exception exception) {
			throw new JspException(exception);
		}

		return EVAL_PAGE;
	}

	public void setModelResource(String modelResource) {
		_modelResource = modelResource;
	}

	public void setModelResourceDescription(String modelResourceDescription) {
		_modelResourceDescription = modelResourceDescription;
	}

	public void setRedirect(String redirect) {
		_redirect = redirect;
	}

	public void setResourceGroupId(Object resourceGroupId) {
		_resourceGroupId = resourceGroupId;
	}

	public void setResourcePrimKey(String resourcePrimKey) {
		_resourcePrimKey = resourcePrimKey;
	}

	public void setRoleTypes(int[] roleTypes) {
		_roleTypes = roleTypes;
	}

	public void setVar(String var) {
		_var = var;
	}

	public void setWindowState(String windowState) {
		_windowState = windowState;
	}

	private static PortletURL _getPorletURL(
			HttpServletRequest httpServletRequest, String modelResource,
			Object resourceGroupId, String windowState)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return PortletURLBuilder.create(
			PortletProviderUtil.getPortletURL(
				httpServletRequest,
				PortletConfigurationApplicationType.PortletConfiguration.
					CLASS_NAME,
				PortletProvider.Action.VIEW)
		).setMVCPath(
			"/edit_permissions.jsp"
		).setPortletResource(
			() -> {
				PortletDisplay portletDisplay =
					themeDisplay.getPortletDisplay();

				return portletDisplay.getId();
			}
		).setParameter(
			"modelResource", modelResource
		).setParameter(
			"portletConfiguration", true
		).setParameter(
			"resourceGroupId",
			_getResourceGroupId(resourceGroupId, themeDisplay)
		).setWindowState(
			_getWindowState(themeDisplay, windowState)
		).buildPortletURL();
	}

	private static String _getRedirect(
		HttpServletRequest httpServletRequest, String redirect,
		String windowState) {

		if (Validator.isNotNull(redirect) ||
			(Validator.isNotNull(windowState) &&
			 StringUtil.equals(
				 windowState, LiferayWindowState.POP_UP.toString()))) {

			return redirect;
		}

		return PortalUtil.getCurrentURL(httpServletRequest);
	}

	private static Object _getResourceGroupId(
		Object resourceGroupId, ThemeDisplay themeDisplay) {

		if (resourceGroupId instanceof Number) {
			Number resourceGroupIdNumber = (Number)resourceGroupId;

			if (resourceGroupIdNumber.longValue() < 0) {
				resourceGroupId = null;
			}
		}
		else if (resourceGroupId instanceof String) {
			String resourceGroupIdString = (String)resourceGroupId;

			if (resourceGroupIdString.length() == 0) {
				resourceGroupId = null;
			}
		}

		if (resourceGroupId == null) {
			resourceGroupId = String.valueOf(themeDisplay.getScopeGroupId());
		}

		return resourceGroupId;
	}

	private static WindowState _getWindowState(
		ThemeDisplay themeDisplay, String windowState) {

		if (Validator.isNotNull(windowState)) {
			return WindowStateFactory.getWindowState(windowState);
		}

		if (themeDisplay.isStatePopUp()) {
			return LiferayWindowState.POP_UP;
		}

		return WindowState.MAXIMIZED;
	}

	private String _modelResource;
	private String _modelResourceDescription;
	private String _redirect;
	private Object _resourceGroupId;
	private String _resourcePrimKey;
	private int[] _roleTypes;
	private String _var;
	private String _windowState;

}