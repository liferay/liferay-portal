/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.contributor.FragmentCollectionContributorRegistry;
import com.liferay.fragment.model.FragmentComposition;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererRegistry;
import com.liferay.fragment.service.FragmentCompositionService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.constants.ContentPageEditorConstants;
import com.liferay.layout.content.page.editor.web.internal.manager.FragmentCollectionManager;
import com.liferay.layout.content.page.editor.web.internal.manager.FragmentEntryLinkManager;
import com.liferay.layout.content.page.editor.web.internal.util.layout.structure.LayoutStructureUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.util.structure.DropZoneLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.constants.SegmentsExperienceConstants;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/update_fragments_highlighted_configuration"
	},
	service = MVCActionCommand.class
)
public class UpdateFragmentsHighlightedConfigurationMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		JSONPortletResponseUtil.writeJSON(
			actionRequest, actionResponse,
			_updateFragmentsHighlightedConfiguration(actionRequest));
	}

	private String _getFragmentEntryKey(ActionRequest actionRequest) {
		String fragmentEntryKey = ParamUtil.getString(
			actionRequest, "fragmentEntryKey");

		if (Validator.isNull(fragmentEntryKey)) {
			return null;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Map<String, Map<String, Object>> layoutElementMaps =
			_getLayoutElementMaps(themeDisplay.getPermissionChecker());

		if (layoutElementMaps.containsKey(fragmentEntryKey)) {
			return fragmentEntryKey;
		}

		FragmentRenderer fragmentRenderer =
			_fragmentRendererRegistry.getFragmentRenderer(fragmentEntryKey);

		if (fragmentRenderer != null) {
			return fragmentEntryKey;
		}

		FragmentEntry fragmentEntry =
			_fragmentCollectionContributorRegistry.getFragmentEntry(
				fragmentEntryKey);

		if (fragmentEntry != null) {
			return fragmentEntryKey;
		}

		FragmentComposition fragmentComposition =
			_fragmentCollectionContributorRegistry.getFragmentComposition(
				fragmentEntryKey);

		if (fragmentComposition != null) {
			return fragmentEntryKey;
		}

		long groupId = ParamUtil.getLong(actionRequest, "groupId");

		fragmentEntry = _fragmentEntryLocalService.fetchFragmentEntry(
			groupId, fragmentEntryKey);

		if (fragmentEntry != null) {
			return _getFragmentUniqueKey(fragmentEntryKey, groupId);
		}

		fragmentComposition =
			_fragmentCompositionService.fetchFragmentComposition(
				groupId, fragmentEntryKey);

		if (fragmentComposition != null) {
			return _getFragmentUniqueKey(fragmentEntryKey, groupId);
		}

		return null;
	}

	private String _getFragmentUniqueKey(
		String fragmentEntryKey, long groupId) {

		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return fragmentEntryKey;
		}

		return fragmentEntryKey + StringPool.POUND + group.getGroupKey();
	}

	private JSONArray _getHighlightedFragmentsJSONArray(
		Set<String> highlightedFragmentEntryKeys,
		HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		DropZoneLayoutStructureItem masterDropZoneLayoutStructureItem =
			_getMasterDropZoneLayoutStructureItem(themeDisplay);

		Map<String, JSONObject> highlightedFragmentJSONObjects =
			new TreeMap<>();

		for (String key : highlightedFragmentEntryKeys) {
			int pos = key.indexOf(StringPool.POUND);

			long groupId = 0;

			if (pos > 0) {
				String groupKey = GetterUtil.getString(key.substring(pos + 1));

				Group group = _groupLocalService.fetchGroup(
					themeDisplay.getCompanyId(), groupKey);

				if (group != null) {
					groupId = group.getGroupId();
				}

				key = key.substring(0, pos);
			}

			if (!_isAllowedFragmentEntryKey(
					key, masterDropZoneLayoutStructureItem)) {

				continue;
			}

			Map<String, Map<String, Object>> layoutElementMaps =
				_getLayoutElementMaps(themeDisplay.getPermissionChecker());

			if (layoutElementMaps.containsKey(key)) {
				Map<String, Object> layoutElementMap = layoutElementMaps.get(
					key);

				String label = _language.get(
					themeDisplay.getLocale(),
					(String)layoutElementMap.get("languageKey"));

				highlightedFragmentJSONObjects.put(
					label,
					JSONUtil.put(
						"fragmentEntryKey", key
					).put(
						"highlighted", true
					).put(
						"icon", (String)layoutElementMap.get("icon")
					).put(
						"itemType", (String)layoutElementMap.get("itemType")
					).put(
						"name", label
					));
			}

			FragmentRenderer fragmentRenderer =
				_fragmentRendererRegistry.getFragmentRenderer(key);

			if (fragmentRenderer != null) {
				String label = fragmentRenderer.getLabel(
					themeDisplay.getLocale());

				highlightedFragmentJSONObjects.put(
					label,
					JSONUtil.put(
						"fragmentEntryKey", fragmentRenderer.getKey()
					).put(
						"highlighted", true
					).put(
						"icon", fragmentRenderer.getIcon()
					).put(
						"imagePreviewURL",
						fragmentRenderer.getImagePreviewURL(httpServletRequest)
					).put(
						"name", label
					));

				continue;
			}

			FragmentEntry fragmentEntry =
				_fragmentEntryLinkManager.getFragmentEntry(
					groupId, key, themeDisplay.getLocale());

			if (fragmentEntry != null) {
				highlightedFragmentJSONObjects.put(
					fragmentEntry.getName(),
					JSONUtil.put(
						"fragmentEntryKey", fragmentEntry.getFragmentEntryKey()
					).put(
						"groupId", fragmentEntry.getGroupId()
					).put(
						"highlighted", true
					).put(
						"icon", fragmentEntry.getIcon()
					).put(
						"imagePreviewURL",
						fragmentEntry.getImagePreviewURL(themeDisplay)
					).put(
						"name", fragmentEntry.getName()
					).put(
						"type",
						FragmentConstants.getTypeLabel(fragmentEntry.getType())
					));

				continue;
			}

			FragmentComposition fragmentComposition =
				_fragmentCollectionContributorRegistry.getFragmentComposition(
					key);

			if (fragmentComposition == null) {
				fragmentComposition =
					_fragmentCompositionService.fetchFragmentComposition(
						groupId, key);
			}

			if (fragmentComposition == null) {
				continue;
			}

			highlightedFragmentJSONObjects.put(
				fragmentComposition.getName(),
				JSONUtil.put(
					"fragmentEntryKey",
					fragmentComposition.getFragmentCompositionKey()
				).put(
					"groupId", fragmentComposition.getGroupId()
				).put(
					"highlighted", true
				).put(
					"icon", fragmentComposition.getIcon()
				).put(
					"imagePreviewURL",
					fragmentComposition.getImagePreviewURL(themeDisplay)
				).put(
					"name", fragmentComposition.getName()
				).put(
					"type", ContentPageEditorConstants.TYPE_COMPOSITION
				));
		}

		List<JSONObject> sortedHighlightedFragments = new ArrayList<>(
			highlightedFragmentJSONObjects.values());

		return JSONUtil.putAll(
			sortedHighlightedFragments.toArray(new JSONObject[0]));
	}

	private Map<String, Map<String, Object>> _getLayoutElementMaps(
		PermissionChecker permissionChecker) {

		if (_layoutElementMaps != null) {
			return _layoutElementMaps;
		}

		Map<String, Map<String, Object>> layoutElementMaps = new HashMap<>();

		Map<String, List<Map<String, Object>>> layoutElementMapsListMap =
			_fragmentCollectionManager.getLayoutElementMapsListMap(
				permissionChecker);

		for (Map.Entry<String, List<Map<String, Object>>> entry :
				layoutElementMapsListMap.entrySet()) {

			for (Map<String, Object> layoutElementMap : entry.getValue()) {
				String fragmentEntryKey = (String)layoutElementMap.get(
					"fragmentEntryKey");

				layoutElementMaps.put(fragmentEntryKey, layoutElementMap);
			}
		}

		_layoutElementMaps = layoutElementMaps;

		return _layoutElementMaps;
	}

	private DropZoneLayoutStructureItem _getMasterDropZoneLayoutStructureItem(
		ThemeDisplay themeDisplay) {

		Layout layout = themeDisplay.getLayout();

		if (layout.getMasterLayoutPlid() <= 0) {
			return null;
		}

		LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByPlid(
					layout.getMasterLayoutPlid());

		if (masterLayoutPageTemplateEntry == null) {
			return null;
		}

		try {
			LayoutStructure masterLayoutStructure =
				LayoutStructureUtil.getLayoutStructure(
					masterLayoutPageTemplateEntry.getGroupId(),
					masterLayoutPageTemplateEntry.getPlid(),
					SegmentsExperienceConstants.KEY_DEFAULT);

			return (DropZoneLayoutStructureItem)
				masterLayoutStructure.getDropZoneLayoutStructureItem();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to get master layout structure", exception);
			}
		}

		return null;
	}

	private boolean _isAllowedFragmentEntryKey(
		String fragmentEntryKey,
		DropZoneLayoutStructureItem masterDropZoneLayoutStructureItem) {

		if (masterDropZoneLayoutStructureItem == null) {
			return true;
		}

		List<String> fragmentEntryKeys =
			masterDropZoneLayoutStructureItem.getFragmentEntryKeys();

		if (masterDropZoneLayoutStructureItem.isAllowNewFragmentEntries()) {
			if (ListUtil.isEmpty(fragmentEntryKeys) ||
				!fragmentEntryKeys.contains(fragmentEntryKey)) {

				return true;
			}

			return false;
		}

		if (ListUtil.isNotEmpty(fragmentEntryKeys) &&
			fragmentEntryKeys.contains(fragmentEntryKey)) {

			return true;
		}

		return false;
	}

	private JSONObject _updateFragmentsHighlightedConfiguration(
			ActionRequest actionRequest)
		throws Exception {

		String fragmentEntryKey = _getFragmentEntryKey(actionRequest);

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			actionRequest);

		if (Validator.isNull(fragmentEntryKey)) {
			hideDefaultSuccessMessage(actionRequest);

			return JSONUtil.put(
				"error",
				_language.get(
					httpServletRequest, "an-unexpected-error-occurred"));
		}

		boolean highlighted = ParamUtil.getBoolean(
			actionRequest, "highlighted");

		PortalPreferences portalPreferences =
			_portletPreferencesFactory.getPortalPreferences(httpServletRequest);

		Set<String> highlightedFragmentEntryKeys = SetUtil.fromArray(
			portalPreferences.getValues(
				ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
				"highlightedFragmentEntryKeys", new String[0]));

		if (highlighted) {
			highlightedFragmentEntryKeys.add(fragmentEntryKey);
		}
		else {
			highlightedFragmentEntryKeys.remove(fragmentEntryKey);
		}

		portalPreferences.setValues(
			ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
			"highlightedFragmentEntryKeys",
			highlightedFragmentEntryKeys.toArray(new String[0]));

		return JSONUtil.put(
			"highlightedFragments",
			_getHighlightedFragmentsJSONArray(
				highlightedFragmentEntryKeys, httpServletRequest));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpdateFragmentsHighlightedConfigurationMVCActionCommand.class);

	@Reference
	private FragmentCollectionContributorRegistry
		_fragmentCollectionContributorRegistry;

	@Reference
	private FragmentCollectionManager _fragmentCollectionManager;

	@Reference
	private FragmentCompositionService _fragmentCompositionService;

	@Reference
	private FragmentEntryLinkManager _fragmentEntryLinkManager;

	@Reference
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Reference
	private FragmentRendererRegistry _fragmentRendererRegistry;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	private Map<String, Map<String, Object>> _layoutElementMaps;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private PortletPreferencesFactory _portletPreferencesFactory;

}