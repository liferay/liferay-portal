/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletApp;
import com.liferay.portal.kernel.model.PublicRenderParameter;
import com.liferay.portal.kernel.portlet.LiferayPortletMode;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletQNameUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portlet.PublicRenderParametersPool;
import com.liferay.portlet.RenderParametersPool;

import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.ResourceURL;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Neil Griffin
 */
public class RenderStateUtil {

	public static String generateJSON(
		HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay) {

		return generateJSON(
			httpServletRequest, themeDisplay, Collections.emptyMap());
	}

	public static String generateJSON(
		HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay,
		Map<String, RenderData> renderDataMap) {

		LayoutTypePortlet layoutTypePortlet =
			themeDisplay.getLayoutTypePortlet();

		if (layoutTypePortlet != null) {
			return String.valueOf(
				_getPageStateJSONObject(
					httpServletRequest, themeDisplay, layoutTypePortlet,
					renderDataMap));
		}

		return StringPool.BLANK;
	}

	private static String _createActionURL(
		HttpServletRequest httpServletRequest, Layout layout, Portlet portlet) {

		LiferayPortletURL liferayPortletURL = _createLiferayPortletURL(
			httpServletRequest, layout, portlet, PortletRequest.ACTION_PHASE,
			MimeResponse.Copy.NONE);

		return liferayPortletURL.toString();
	}

	private static LiferayPortletURL _createLiferayPortletURL(
		HttpServletRequest httpServletRequest, Layout layout, Portlet portlet,
		String lifecycle, MimeResponse.Copy copy) {

		LiferayPortletURLPrivilegedAction liferayPortletURLPrivilegedAction =
			new LiferayPortletURLPrivilegedAction(
				portlet.getPortletId(), lifecycle, copy, layout, portlet,
				httpServletRequest);

		return liferayPortletURLPrivilegedAction.run();
	}

	private static String _createRenderURL(
		HttpServletRequest httpServletRequest, Layout layout, Portlet portlet) {

		LiferayPortletURL liferayPortletURL = _createLiferayPortletURL(
			httpServletRequest, layout, portlet, PortletRequest.RENDER_PHASE,
			MimeResponse.Copy.NONE);

		return liferayPortletURL.toString();
	}

	private static String _createResourceURL(
		HttpServletRequest httpServletRequest, Layout layout, Portlet portlet) {

		LiferayPortletURL liferayPortletURL = _createLiferayPortletURL(
			httpServletRequest, layout, portlet, PortletRequest.RESOURCE_PHASE,
			MimeResponse.Copy.NONE);

		liferayPortletURL.setCacheability(ResourceURL.FULL);

		return StringUtil.removeSubstring(
			liferayPortletURL.toString(), "&p_p_cacheability=cacheLevelFull");
	}

	private static JSONArray _getAllowedPortletModesJSONArray(Portlet portlet) {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		Set<String> allPortletModes = portlet.getAllPortletModes();

		for (String portletMode : allPortletModes) {
			jsonArray.put(portletMode);
		}

		return jsonArray;
	}

	private static JSONArray _getAllowedWindowStatesJSONArray(Portlet portlet) {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		Set<String> allWindowStates = portlet.getAllWindowStates();

		for (String windowState : allWindowStates) {
			jsonArray.put(windowState);
		}

		return jsonArray;
	}

	private static Map<String, String[]> _getChangedPublicRenderParameters(
		HttpServletRequest httpServletRequest, long plid,
		List<Portlet> portlets) {

		Map<String, String[]> changedPublicRenderParameters = new HashMap<>();

		Map<String, String[]> currentPublicRenderParameters =
			PublicRenderParametersPool.get(httpServletRequest, plid);

		for (Portlet portlet : portlets) {
			Set<PublicRenderParameter> publicRenderParameters =
				portlet.getPublicRenderParameters();

			PortletApp portletApp = portlet.getPortletApp();

			if (portletApp.getSpecMajorVersion() >= 3) {
				for (PublicRenderParameter publicRenderParameter :
						publicRenderParameters) {

					String[] currentValue = currentPublicRenderParameters.get(
						PortletQNameUtil.getPublicRenderParameterName(
							publicRenderParameter.getQName()));

					if (currentValue != null) {
						changedPublicRenderParameters.put(
							publicRenderParameter.getIdentifier(),
							currentValue);
					}
				}

				continue;
			}

			Map<String, String[]> privateRenderParameterMap =
				RenderParametersPool.get(
					httpServletRequest, plid, portlet.getPortletId());

			if (privateRenderParameterMap != null) {
				for (PublicRenderParameter publicRenderParameter :
						publicRenderParameters) {

					String[] values = privateRenderParameterMap.get(
						publicRenderParameter.getIdentifier());

					if (values != null) {
						changedPublicRenderParameters.put(
							publicRenderParameter.getIdentifier(), values);
					}
				}
			}
		}

		return changedPublicRenderParameters;
	}

	private static JSONObject _getPageStateJSONObject(
		HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay,
		LayoutTypePortlet layoutTypePortlet,
		Map<String, RenderData> renderDataMap) {

		List<Portlet> portlets = layoutTypePortlet.getAllPortlets();

		return JSONUtil.put(
			"encodedCurrentURL",
			URLCodec.encodeURL(
				PortalUtil.getCurrentCompleteURL(httpServletRequest))
		).put(
			"portlets",
			_getPortletsJSONObject(
				httpServletRequest, themeDisplay, layoutTypePortlet, portlets,
				renderDataMap)
		).put(
			"prpMap", _getPRPGroupsJSONObject(portlets)
		);
	}

	private static JSONObject _getPortletJSONObject(
		HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay,
		LayoutTypePortlet layoutTypePortlet, Portlet portlet,
		RenderData renderData,
		Map<String, String[]> changedPublicRenderParameters) {

		return JSONUtil.put(
			"allowedPM", _getAllowedPortletModesJSONArray(portlet)
		).put(
			"allowedWS", _getAllowedWindowStatesJSONArray(portlet)
		).put(
			"encodedActionURL",
			URLCodec.encodeURL(
				_createActionURL(
					httpServletRequest, themeDisplay.getLayout(), portlet))
		).put(
			"encodedRenderURL",
			URLCodec.encodeURL(
				_createRenderURL(
					httpServletRequest, themeDisplay.getLayout(), portlet))
		).put(
			"encodedResourceURL",
			URLCodec.encodeURL(
				_createResourceURL(
					httpServletRequest, themeDisplay.getLayout(), portlet))
		).put(
			"pubParms", _getPortletPRPJSONObject(portlet)
		).put(
			"renderData", _getRenderDataJSONObject(renderData)
		).put(
			"state",
			_getPortletStateJSONObject(
				httpServletRequest, themeDisplay, layoutTypePortlet, portlet,
				changedPublicRenderParameters)
		);
	}

	private static PortletMode _getPortletMode(
		LayoutTypePortlet layoutTypePortlet, String portletId) {

		if (layoutTypePortlet.hasModeAboutPortletId(portletId)) {
			return LiferayPortletMode.ABOUT;
		}

		if (layoutTypePortlet.hasModeConfigPortletId(portletId)) {
			return LiferayPortletMode.CONFIG;
		}

		if (layoutTypePortlet.hasModeEditDefaultsPortletId(portletId)) {
			return LiferayPortletMode.EDIT_DEFAULTS;
		}

		if (layoutTypePortlet.hasModeEditGuestPortletId(portletId)) {
			return LiferayPortletMode.EDIT_GUEST;
		}

		if (layoutTypePortlet.hasModeEditPortletId(portletId)) {
			return LiferayPortletMode.EDIT;
		}

		if (layoutTypePortlet.hasModeHelpPortletId(portletId)) {
			return LiferayPortletMode.HELP;
		}

		if (layoutTypePortlet.hasModePreviewPortletId(portletId)) {
			return LiferayPortletMode.PREVIEW;
		}

		if (layoutTypePortlet.hasModePrintPortletId(portletId)) {
			return LiferayPortletMode.PRINT;
		}

		String customPortletMode =
			layoutTypePortlet.getAddedCustomPortletMode();

		if (customPortletMode != null) {
			return new PortletMode(customPortletMode);
		}

		return LiferayPortletMode.VIEW;
	}

	private static JSONObject _getPortletParametersJSONObject(
		HttpServletRequest httpServletRequest, long plid, Portlet portlet,
		Map<String, String[]> changedPublicRenderParameters) {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		Map<String, String[]> privateRenderParameters =
			RenderParametersPool.get(
				httpServletRequest, plid, portlet.getPortletId());

		if (privateRenderParameters != null) {
			for (Map.Entry<String, String[]> entry :
					privateRenderParameters.entrySet()) {

				jsonObject.put(entry.getKey(), entry.getValue());
			}
		}

		Set<PublicRenderParameter> publicRenderParameters =
			portlet.getPublicRenderParameters();

		for (PublicRenderParameter publicRenderParameter :
				publicRenderParameters) {

			String[] values = changedPublicRenderParameters.get(
				publicRenderParameter.getIdentifier());

			if (values != null) {
				jsonObject.put(publicRenderParameter.getIdentifier(), values);
			}
		}

		return jsonObject;
	}

	private static JSONObject _getPortletPRPJSONObject(Portlet portlet) {
		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		Set<PublicRenderParameter> publicRenderParameters =
			portlet.getPublicRenderParameters();

		for (PublicRenderParameter publicRenderParameter :
				publicRenderParameters) {

			jsonObject.put(
				publicRenderParameter.getIdentifier(),
				PortletQNameUtil.getPublicRenderParameterName(
					publicRenderParameter.getQName()));
		}

		return jsonObject;
	}

	private static JSONObject _getPortletsJSONObject(
		HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay,
		LayoutTypePortlet layoutTypePortlet, List<Portlet> portlets,
		Map<String, RenderData> renderDataMap) {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		Map<String, String[]> changedPublicRenderParameters =
			_getChangedPublicRenderParameters(
				httpServletRequest, themeDisplay.getPlid(), portlets);

		for (Portlet portlet : portlets) {
			jsonObject.put(
				PortalUtil.getPortletNamespace(portlet.getPortletId()),
				_getPortletJSONObject(
					httpServletRequest, themeDisplay, layoutTypePortlet,
					portlet, renderDataMap.get(portlet.getPortletId()),
					changedPublicRenderParameters));
		}

		return jsonObject;
	}

	private static JSONObject _getPortletStateJSONObject(
		HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay,
		LayoutTypePortlet layoutTypePortlet, Portlet portlet,
		Map<String, String[]> changedPublicRenderParameters) {

		return JSONUtil.put(
			"parameters",
			_getPortletParametersJSONObject(
				httpServletRequest, themeDisplay.getPlid(), portlet,
				changedPublicRenderParameters)
		).put(
			"portletMode",
			_getPortletMode(layoutTypePortlet, portlet.getPortletId())
		).put(
			"windowState",
			_getWindowState(layoutTypePortlet, portlet.getPortletId())
		);
	}

	private static JSONObject _getPRPGroupsJSONObject(List<Portlet> portlets) {
		Map<String, PRPGroup> map = new LinkedHashMap<>();

		for (Portlet portlet : portlets) {
			Set<PublicRenderParameter> publicRenderParameters =
				portlet.getPublicRenderParameters();

			for (PublicRenderParameter publicRenderParameter :
					publicRenderParameters) {

				String publicRenderParameterName =
					PortletQNameUtil.getPublicRenderParameterName(
						publicRenderParameter.getQName());

				PRPGroup prpGroup = map.get(publicRenderParameterName);

				if (prpGroup == null) {
					prpGroup = new PRPGroup(
						publicRenderParameter.getIdentifier(), new HashSet<>());
				}

				Set<String> portletIds = prpGroup.getPortletIds();

				portletIds.add(
					PortalUtil.getPortletNamespace(portlet.getPortletId()));

				map.put(publicRenderParameterName, prpGroup);
			}
		}

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		for (Map.Entry<String, PRPGroup> entry : map.entrySet()) {
			JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

			PRPGroup prpGroup = entry.getValue();

			for (String portletId : prpGroup.getPortletIds()) {
				String value = portletId.concat(StringPool.PIPE);

				jsonArray.put(value.concat(prpGroup.getIdentifier()));
			}

			jsonObject.put(entry.getKey(), jsonArray);
		}

		return jsonObject;
	}

	private static JSONObject _getRenderDataJSONObject(RenderData renderData) {
		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		if (renderData == null) {
			jsonObject.put(
				"content", StringPool.BLANK
			).put(
				"mimeType", StringPool.BLANK
			);
		}
		else {
			jsonObject.put(
				"content", renderData.getContent()
			).put(
				"mimeType", renderData.getContentType()
			);
		}

		return jsonObject;
	}

	private static WindowState _getWindowState(
		LayoutTypePortlet layoutTypePortlet, String portletId) {

		if (layoutTypePortlet.hasStateMaxPortletId(portletId)) {
			return WindowState.MAXIMIZED;
		}

		if (layoutTypePortlet.hasStateMinPortletId(portletId)) {
			return WindowState.MINIMIZED;
		}

		return WindowState.NORMAL;
	}

	private static class PRPGroup {

		public String getIdentifier() {
			return _identifier;
		}

		public Set<String> getPortletIds() {
			return _portletIds;
		}

		private PRPGroup(String identifier, Set<String> portletIds) {
			_identifier = identifier;
			_portletIds = portletIds;
		}

		private final String _identifier;
		private final Set<String> _portletIds;

	}

}