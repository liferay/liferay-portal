/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletCategory;
import com.liferay.portal.kernel.model.PortletItem;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.PortletItemLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.util.comparator.PortletTitleComparator;
import com.liferay.portal.util.WebAppPool;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/update_portlets_highlighted_configuration"
	},
	service = MVCActionCommand.class
)
public class UpdatePortletsHighlightedConfigurationMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		JSONPortletResponseUtil.writeJSON(
			actionRequest, actionResponse,
			_updatePortletsHighlightedConfiguration(actionRequest));
	}

	private JSONArray _getPortletItemsJSONArray(
		Portlet portlet, ThemeDisplay themeDisplay) {

		List<PortletItem> portletItems =
			_portletItemLocalService.getPortletItems(
				themeDisplay.getScopeGroupId(), portlet.getPortletId(),
				PortletPreferences.class.getName());

		if (ListUtil.isEmpty(portletItems)) {
			return _jsonFactory.createJSONArray();
		}

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		for (PortletItem portletItem : portletItems) {
			jsonArray.put(
				JSONUtil.put(
					"instanceable", portlet.isInstanceable()
				).put(
					"portletId", portlet.getPortletId()
				).put(
					"portletItemId", portletItem.getPortletItemId()
				).put(
					"title", HtmlUtil.escape(portletItem.getName())
				));
		}

		return jsonArray;
	}

	private List<Portlet> _getPortlets(
		Set<String> portletIds, ThemeDisplay themeDisplay) {

		List<Portlet> portlets = new ArrayList<>();

		for (String portletId : portletIds) {
			Portlet portlet = _portletLocalService.getPortletById(
				themeDisplay.getCompanyId(), portletId);

			if ((portlet == null) ||
				ArrayUtil.contains(
					_UNSUPPORTED_PORTLETS_NAMES, portlet.getPortletName())) {

				continue;
			}

			try {
				if (PortletPermissionUtil.contains(
						themeDisplay.getPermissionChecker(),
						themeDisplay.getLayout(), portlet,
						ActionKeys.ADD_TO_PAGE)) {

					portlets.add(portlet);
				}
			}
			catch (PortalException portalException) {
				_log.error(
					"Unable to check portlet permissions for " +
						portlet.getPortletId(),
					portalException);
			}
		}

		return portlets;
	}

	private JSONArray _getPortletsJSONArray(
		HttpServletRequest httpServletRequest, Set<String> portletIds,
		ThemeDisplay themeDisplay) {

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		HttpSession httpSession = httpServletRequest.getSession();

		ServletContext servletContext = httpSession.getServletContext();

		List<Portlet> portlets = _getPortlets(portletIds, themeDisplay);

		portlets = ListUtil.sort(
			portlets,
			new PortletTitleComparator(
				servletContext, themeDisplay.getLocale()));

		for (Portlet portlet : portlets) {
			jsonArray.put(
				JSONUtil.put(
					"highlighted", true
				).put(
					"instanceable", portlet.isInstanceable()
				).put(
					"portletId", portlet.getPortletId()
				).put(
					"portletItems",
					_getPortletItemsJSONArray(portlet, themeDisplay)
				).put(
					"title",
					_portal.getPortletTitle(
						portlet, servletContext, themeDisplay.getLocale())
				));
		}

		return jsonArray;
	}

	private JSONObject _updatePortletsHighlightedConfiguration(
			ActionRequest actionRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String portletId = PortletIdCodec.decodePortletName(
			ParamUtil.getString(actionRequest, "portletId"));

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			actionRequest);

		if (!PortletPermissionUtil.contains(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroupId(), themeDisplay.getLayout(),
				portletId, ActionKeys.ADD_TO_PAGE)) {

			hideDefaultSuccessMessage(actionRequest);

			return JSONUtil.put(
				"error",
				_language.get(
					httpServletRequest,
					"you-do-not-have-the-roles-required-to-access-this-" +
						"portlet"));
		}

		boolean highlighted = ParamUtil.getBoolean(
			actionRequest, "highlighted");

		PortalPreferences portalPreferences =
			_portletPreferencesFactory.getPortalPreferences(httpServletRequest);

		Set<String> highlightedPortletIds = SetUtil.fromArray(
			portalPreferences.getValues(
				ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
				"highlightedPortletIds", new String[0]));

		if (highlighted) {
			highlightedPortletIds.add(portletId);
		}
		else {
			highlightedPortletIds.remove(portletId);
		}

		portalPreferences.setValues(
			ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
			"highlightedPortletIds",
			highlightedPortletIds.toArray(new String[0]));

		PortletCategory rootPortletCategory = (PortletCategory)WebAppPool.get(
			themeDisplay.getCompanyId(), WebKeys.PORTLET_CATEGORY);

		PortletCategory highlightedPortletCategory =
			rootPortletCategory.getCategory("category.highlighted");

		Set<String> defaultHighlightedPortletIds =
			highlightedPortletCategory.getPortletIds();

		Set<String> nonhighlightedPortletIds = SetUtil.fromArray(
			portalPreferences.getValues(
				ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
				"nonhighlightedPortletIds", new String[0]));

		if (defaultHighlightedPortletIds.contains(portletId)) {
			if (highlighted) {
				nonhighlightedPortletIds.remove(portletId);
			}
			else {
				nonhighlightedPortletIds.add(portletId);
			}

			portalPreferences.setValues(
				ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
				"nonhighlightedPortletIds",
				nonhighlightedPortletIds.toArray(new String[0]));
		}

		Set<String> portletIds = new TreeSet<>(defaultHighlightedPortletIds);

		portletIds.addAll(highlightedPortletIds);
		portletIds.removeAll(nonhighlightedPortletIds);

		return JSONUtil.put(
			"highlightedPortlets",
			_getPortletsJSONArray(
				httpServletRequest, portletIds, themeDisplay));
	}

	private static final String[] _UNSUPPORTED_PORTLETS_NAMES = {
		"com_liferay_nested_portlets_web_portlet_NestedPortletsPortlet"
	};

	private static final Log _log = LogFactoryUtil.getLog(
		UpdatePortletsHighlightedConfigurationMVCActionCommand.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private PortletItemLocalService _portletItemLocalService;

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference
	private PortletPreferencesFactory _portletPreferencesFactory;

}