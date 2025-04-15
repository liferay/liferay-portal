/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.manager.FragmentCollectionManager;
import com.liferay.layout.content.page.editor.web.internal.util.layout.structure.LayoutStructureUtil;
import com.liferay.layout.portlet.category.PortletCategoryManager;
import com.liferay.layout.util.structure.DropZoneLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.constants.SegmentsExperienceConstants;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/update_fragment_portlet_sets_sort_configuration"
	},
	service = MVCActionCommand.class
)
public class UpdateFragmentPortletSetsSortConfigurationMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		JSONPortletResponseUtil.writeJSON(
			actionRequest, actionResponse,
			_updateFragmentPortletSetsSortConfiguration(actionRequest));
	}

	private DropZoneLayoutStructureItem _getMasterDropZoneLayoutStructureItem(
		ThemeDisplay themeDisplay) {

		Layout layout = themeDisplay.getLayout();

		if (layout.getMasterLayoutPlid() <= 0) {
			return null;
		}

		try {
			LayoutStructure masterLayoutStructure =
				LayoutStructureUtil.getLayoutStructure(
					layout.getGroupId(), layout.getMasterLayoutPlid(),
					SegmentsExperienceConstants.KEY_DEFAULT);

			LayoutStructureItem layoutStructureItem =
				masterLayoutStructure.getDropZoneLayoutStructureItem();

			if (layoutStructureItem == null) {
				return null;
			}

			return (DropZoneLayoutStructureItem)layoutStructureItem;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to get master layout structure", exception);
			}
		}

		return null;
	}

	private String[] _mergeFragmentCollectionKeys(
		List<String> newFragmentCollectionKeys,
		List<String> oldFragmentCollectionKeys) {

		if (ListUtil.isEmpty(oldFragmentCollectionKeys)) {
			return newFragmentCollectionKeys.toArray(new String[0]);
		}

		List<String> oldVisibleFragmentCollectionKeys = new ArrayList<>();

		for (String fragmentCollectionKey : oldFragmentCollectionKeys) {
			if (newFragmentCollectionKeys.contains(fragmentCollectionKey)) {
				oldVisibleFragmentCollectionKeys.add(fragmentCollectionKey);
			}
		}

		Map<String, String> swapCollectionKeysMap = new LinkedHashMap<>();

		for (int i = 0;
			 (i < newFragmentCollectionKeys.size()) &&
			 (i < oldVisibleFragmentCollectionKeys.size()); i++) {

			String fragmentCollectionKey = newFragmentCollectionKeys.get(i);
			String samePosOldVisibleFragmentCollectionKey =
				oldVisibleFragmentCollectionKeys.get(i);

			if (Objects.equals(
					fragmentCollectionKey,
					samePosOldVisibleFragmentCollectionKey)) {

				continue;
			}

			swapCollectionKeysMap.put(
				samePosOldVisibleFragmentCollectionKey, fragmentCollectionKey);
		}

		List<String> fragmentCollectionKeys = new LinkedList<>();

		for (String fragmentCollectionKey : oldFragmentCollectionKeys) {
			String swapFragmentCollectionKey = swapCollectionKeysMap.remove(
				fragmentCollectionKey);

			if (swapFragmentCollectionKey == null) {
				fragmentCollectionKeys.add(fragmentCollectionKey);

				continue;
			}

			fragmentCollectionKeys.add(swapFragmentCollectionKey);
		}

		if (newFragmentCollectionKeys.size() >
				oldVisibleFragmentCollectionKeys.size()) {

			fragmentCollectionKeys.addAll(
				ListUtil.subList(
					newFragmentCollectionKeys,
					oldVisibleFragmentCollectionKeys.size(),
					newFragmentCollectionKeys.size()));
		}

		return fragmentCollectionKeys.toArray(new String[0]);
	}

	private Object _updateFragmentPortletSetsSortConfiguration(
			ActionRequest actionRequest)
		throws Exception {

		String fragmentCollectionKeys = ParamUtil.getString(
			actionRequest, "fragmentCollectionKeys");
		String portletCategoryKeys = ParamUtil.getString(
			actionRequest, "portletCategoryKeys");

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			actionRequest);

		if (Validator.isNull(fragmentCollectionKeys) &&
			Validator.isNull(portletCategoryKeys)) {

			hideDefaultSuccessMessage(actionRequest);

			return JSONUtil.put(
				"error",
				_language.get(
					httpServletRequest, "an-unexpected-error-occurred"));
		}

		JSONArray fragmentCollectionKeysJSONArray = null;
		JSONArray portletCategoryKeysJSONArray = null;

		try {
			if (Validator.isNotNull(fragmentCollectionKeys)) {
				fragmentCollectionKeysJSONArray = _jsonFactory.createJSONArray(
					fragmentCollectionKeys);
			}

			if (Validator.isNotNull(portletCategoryKeys)) {
				portletCategoryKeysJSONArray = _jsonFactory.createJSONArray(
					portletCategoryKeys);
			}
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}

			hideDefaultSuccessMessage(actionRequest);

			return JSONUtil.put(
				"error",
				_language.get(
					httpServletRequest, "an-unexpected-error-occurred"));
		}

		PortalPreferences portalPreferences =
			_portletPreferencesFactory.getPortalPreferences(httpServletRequest);

		if (fragmentCollectionKeysJSONArray != null) {
			List<String> sortedFragmentCollectionKeys = JSONUtil.toStringList(
				fragmentCollectionKeysJSONArray);

			List<String> oldSortedFragmentCollectionKeys =
				_fragmentCollectionManager.getSortedFragmentCollectionKeys(
					portalPreferences);

			_fragmentCollectionManager.updateSortedFragmentCollectionKeys(
				portalPreferences,
				_mergeFragmentCollectionKeys(
					sortedFragmentCollectionKeys,
					oldSortedFragmentCollectionKeys));
		}

		if (portletCategoryKeysJSONArray != null) {
			List<String> sortedPortletCategoryKeys = JSONUtil.toStringList(
				portletCategoryKeysJSONArray);

			_portletCategoryManager.updateSortedPortletCategoryKeys(
				portalPreferences,
				sortedPortletCategoryKeys.toArray(new String[0]));
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return JSONUtil.put(
			"fragmentCollections",
			_fragmentCollectionManager.getFragmentCollectionMapsList(
				themeDisplay.getScopeGroupId(), httpServletRequest, false, true,
				_getMasterDropZoneLayoutStructureItem(themeDisplay),
				themeDisplay)
		).put(
			"portletCategories",
			_portletCategoryManager.getPortletsJSONArray(
				httpServletRequest, themeDisplay)
		);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpdateFragmentPortletSetsSortConfigurationMVCActionCommand.class);

	@Reference
	private FragmentCollectionManager _fragmentCollectionManager;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private PortletCategoryManager _portletCategoryManager;

	@Reference
	private PortletPreferencesFactory _portletPreferencesFactory;

}