/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletApp;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.MimeResponse;
import jakarta.portlet.MutableRenderParameters;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderParameters;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Brian Wing Shun Chan
 * @author Miguel Pastor
 * @author Neil Griffin
 */
public class PortletURLUtil {

	public static PortletURL clone(
			LiferayPortletURL liferayPortletURL, String lifecycle,
			LiferayPortletResponse liferayPortletResponse)
		throws PortletException {

		LiferayPortletURL newLiferayPortletURL =
			liferayPortletResponse.createLiferayPortletURL(lifecycle);

		newLiferayPortletURL.setPortletId(liferayPortletURL.getPortletId());

		WindowState windowState = liferayPortletURL.getWindowState();

		if (windowState != null) {
			newLiferayPortletURL.setWindowState(windowState);
		}

		PortletMode portletMode = liferayPortletURL.getPortletMode();

		if (portletMode != null) {
			newLiferayPortletURL.setPortletMode(portletMode);
		}

		newLiferayPortletURL.setParameters(liferayPortletURL.getParameterMap());

		return newLiferayPortletURL;
	}

	public static PortletURL clone(
			PortletURL portletURL,
			LiferayPortletResponse liferayPortletResponse)
		throws PortletException {

		LiferayPortletURL liferayPortletURL = (LiferayPortletURL)portletURL;

		return clone(
			liferayPortletURL, liferayPortletURL.getLifecycle(),
			liferayPortletResponse);
	}

	public static PortletURL clone(
			PortletURL portletURL, MimeResponse mimeResponse)
		throws PortletException {

		LiferayPortletURL liferayPortletURL = (LiferayPortletURL)portletURL;

		return clone(
			liferayPortletURL, liferayPortletURL.getLifecycle(),
			PortalUtil.getLiferayPortletResponse(mimeResponse));
	}

	public static PortletURL clone(
			PortletURL portletURL, String lifecycle,
			LiferayPortletResponse liferayPortletResponse)
		throws PortletException {

		LiferayPortletURL liferayPortletURL = (LiferayPortletURL)portletURL;

		return clone(liferayPortletURL, lifecycle, liferayPortletResponse);
	}

	public static PortletURL clone(
			PortletURL portletURL, String lifecycle, MimeResponse mimeResponse)
		throws PortletException {

		LiferayPortletURL liferayPortletURL = (LiferayPortletURL)portletURL;

		return clone(
			liferayPortletURL, lifecycle,
			PortalUtil.getLiferayPortletResponse(mimeResponse));
	}

	public static PortletURL getCurrent(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		String attributeName = StringBundler.concat(
			liferayPortletRequest.getPortletName(), StringPool.DASH,
			WebKeys.CURRENT_PORTLET_URL);

		PortletURL portletURL = (PortletURL)liferayPortletRequest.getAttribute(
			attributeName);

		if (portletURL != null) {
			return portletURL;
		}

		Portlet portlet = liferayPortletRequest.getPortlet();

		PortletApp portletApp = portlet.getPortletApp();

		if (portletApp.getSpecMajorVersion() < 3) {
			portletURL = _getCurrentV2(
				liferayPortletRequest, liferayPortletResponse);
		}
		else {
			portletURL = _getCurrentV3(
				liferayPortletRequest, liferayPortletResponse);
		}

		liferayPortletRequest.setAttribute(attributeName, portletURL);

		return portletURL;
	}

	public static PortletURL getCurrent(
		PortletRequest portletRequest, MimeResponse mimeResponse) {

		return getCurrent(
			PortalUtil.getLiferayPortletRequest(portletRequest),
			PortalUtil.getLiferayPortletResponse(mimeResponse));
	}

	public static String getRefreshURL(
		HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay) {

		return getRefreshURL(httpServletRequest, themeDisplay, true);
	}

	public static String getRefreshURL(
		HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay,
		boolean includeParameters) {

		StringBundler sb = new StringBundler(36);

		sb.append(themeDisplay.getPathMain());
		sb.append("/portal/render_portlet?p_l_id=");

		sb.append(themeDisplay.getPlid());

		HttpServletRequest originalHttpServletRequest =
			PortalUtil.getOriginalServletRequest(httpServletRequest);

		String layoutMode = ParamUtil.getString(
			originalHttpServletRequest, "p_l_mode");

		if (Validator.isNotNull(layoutMode)) {
			sb.append("&p_l_mode=");
			sb.append(layoutMode);
		}

		Portlet portlet = (Portlet)httpServletRequest.getAttribute(
			WebKeys.RENDER_PORTLET);

		String portletId = portlet.getPortletId();

		sb.append("&p_p_id=");
		sb.append(portletId);

		sb.append("&p_p_lifecycle=0&p_t_lifecycle=");
		sb.append(themeDisplay.getLifecycle());

		WindowState windowState = WindowState.NORMAL;

		if (themeDisplay.isStatePopUp()) {
			windowState = LiferayWindowState.POP_UP;
		}
		else {
			LayoutTypePortlet layoutTypePortlet =
				themeDisplay.getLayoutTypePortlet();

			if (layoutTypePortlet.hasStateMaxPortletId(portletId)) {
				windowState = WindowState.MAXIMIZED;
			}
			else if (layoutTypePortlet.hasStateMinPortletId(portletId)) {
				windowState = WindowState.MINIMIZED;
			}
		}

		sb.append("&p_p_state=");
		sb.append(windowState);

		sb.append("&p_p_mode=view&p_p_col_id=");

		String columnId = (String)httpServletRequest.getAttribute(
			WebKeys.RENDER_PORTLET_COLUMN_ID);

		sb.append(columnId);

		Integer columnPos = (Integer)httpServletRequest.getAttribute(
			WebKeys.RENDER_PORTLET_COLUMN_POS);

		sb.append("&p_p_col_pos=");
		sb.append(columnPos);

		Integer columnCount = (Integer)httpServletRequest.getAttribute(
			WebKeys.RENDER_PORTLET_COLUMN_COUNT);

		sb.append("&p_p_col_count=");
		sb.append(columnCount);

		if (portlet.isStatic()) {
			sb.append("&p_p_static=1");

			if (portlet.isStaticStart()) {
				sb.append("&p_p_static_start=1");
			}
		}

		sb.append("&p_p_isolated=1");

		long sourceGroupId = ParamUtil.getLong(
			httpServletRequest, "p_v_l_s_g_id");

		if (sourceGroupId > 0) {
			sb.append("&p_v_l_s_g_id=");
			sb.append(sourceGroupId);
		}

		String doAsUserId = themeDisplay.getDoAsUserId();

		if (Validator.isNotNull(doAsUserId)) {
			sb.append("&doAsUserId=");
			sb.append(URLCodec.encodeURL(doAsUserId));
		}

		sb.append("&currentURL=");
		sb.append(
			URLCodec.encodeURL(PortalUtil.getCurrentURL(httpServletRequest)));

		String ppid = ParamUtil.getString(httpServletRequest, "p_p_id");

		if (!ppid.equals(portletId)) {
			return sb.toString();
		}

		String p_p_auth = ParamUtil.getString(httpServletRequest, "p_p_auth");

		if (!Validator.isBlank(p_p_auth)) {
			sb.append("&p_p_auth=");
			sb.append(URLCodec.encodeURL(p_p_auth));
		}

		String settingsScope = (String)httpServletRequest.getAttribute(
			WebKeys.SETTINGS_SCOPE);

		settingsScope = ParamUtil.get(
			httpServletRequest, "settingsScope", settingsScope);

		if (Validator.isNotNull(settingsScope)) {
			sb.append("&settingsScope=");
			sb.append(settingsScope);
		}

		if (includeParameters) {
			Map<String, String[]> parameters = getRefreshURLParameters(
				httpServletRequest);

			for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
				String name = entry.getKey();
				String[] values = entry.getValue();

				for (String value : values) {
					sb.append(StringPool.AMPERSAND);
					sb.append(name);
					sb.append(StringPool.EQUAL);
					sb.append(URLCodec.encodeURL(value));
				}
			}
		}

		return sb.toString();
	}

	public static Map<String, String[]> getRefreshURLParameters(
		HttpServletRequest httpServletRequest) {

		Map<String, String[]> refreshURLParameters = new HashMap<>();

		String ppid = ParamUtil.getString(httpServletRequest, "p_p_id");

		Portlet portlet = (Portlet)httpServletRequest.getAttribute(
			WebKeys.RENDER_PORTLET);

		if (ppid.equals(portlet.getPortletId())) {
			String namespace = PortalUtil.getPortletNamespace(
				portlet.getPortletId());

			Map<String, String[]> parameters =
				httpServletRequest.getParameterMap();

			for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
				String name = entry.getKey();

				if (name.startsWith(StringPool.UNDERLINE) &&
					!name.startsWith(namespace)) {

					continue;
				}

				if (!PortalUtil.isReservedParameter(name) &&
					!name.equals("currentURL") &&
					!name.equals("settingsScope") &&
					!isRefreshURLReservedParameter(name, namespace)) {

					String[] values = entry.getValue();

					if (values == null) {
						continue;
					}

					refreshURLParameters.put(name, values);
				}
			}
		}

		return refreshURLParameters;
	}

	protected static boolean isRefreshURLReservedParameter(
		String parameter, String namespace) {

		if (ArrayUtil.isEmpty(_PORTLET_URL_REFRESH_URL_RESERVED_PARAMETERS)) {
			return false;
		}

		for (String reservedParameter :
				_PORTLET_URL_REFRESH_URL_RESERVED_PARAMETERS) {

			if (parameter.equals(reservedParameter) ||
				parameter.equals(namespace.concat(reservedParameter))) {

				return true;
			}
		}

		return false;
	}

	private static PortletURL _getCurrentV2(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		Enumeration<String> enumeration =
			liferayPortletRequest.getParameterNames();

		while (enumeration.hasMoreElements()) {
			String param = enumeration.nextElement();

			String[] values = liferayPortletRequest.getParameterValues(param);

			boolean addParam = true;

			// Do not set parameter values that are over 32 kb. See LEP-1755.

			for (String value : values) {
				if ((value == null) ||
					(value.length() > _CURRENT_URL_PARAMETER_THRESHOLD)) {

					addParam = false;

					break;
				}
			}

			if (addParam) {
				portletURL.setParameter(param, values);
			}
		}

		return portletURL;
	}

	private static PortletURL _getCurrentV3(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		PortletURL portletURL = liferayPortletResponse.createRenderURL(
			MimeResponse.Copy.NONE);

		MutableRenderParameters mutableRenderParameters =
			portletURL.getRenderParameters();

		RenderParameters renderParameters =
			liferayPortletRequest.getRenderParameters();

		Set<String> renderParameterNames = renderParameters.getNames();

		renderParameter:
		for (String renderParameterName : renderParameterNames) {
			String[] values = renderParameters.getValues(renderParameterName);

			// Do not set parameter values that are over 32 kb. See LEP-1755.

			if (values == null) {
				continue;
			}

			for (String value : values) {
				if ((value != null) &&
					(value.length() > _CURRENT_URL_PARAMETER_THRESHOLD)) {

					continue renderParameter;
				}
			}

			mutableRenderParameters.setValues(renderParameterName, values);
		}

		return portletURL;
	}

	private static final int _CURRENT_URL_PARAMETER_THRESHOLD = 32768;

	private static final String[] _PORTLET_URL_REFRESH_URL_RESERVED_PARAMETERS =
		PropsUtil.getArray(
			PropsKeys.PORTLET_URL_REFRESH_URL_RESERVED_PARAMETERS);

}