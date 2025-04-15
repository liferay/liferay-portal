/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLinkService;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.manager.FragmentEntryLinkManager;
import com.liferay.layout.content.page.editor.web.internal.util.layout.structure.LayoutStructureUtil;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/update_collection_display_config"
	},
	service = MVCActionCommand.class
)
public class UpdateCollectionDisplayConfigMVCActionCommand
	extends BaseContentPageEditorTransactionalMVCActionCommand {

	@Override
	protected JSONObject doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long segmentsExperienceId = ParamUtil.getLong(
			actionRequest, "segmentsExperienceId");
		String itemConfig = ParamUtil.getString(actionRequest, "itemConfig");
		String itemId = ParamUtil.getString(actionRequest, "itemId");

		JSONArray fragmentEntryLinksJSONArray = _jsonFactory.createJSONArray();

		List<FragmentEntryLink> fragmentEntryLinks = new ArrayList<>(
			_fragmentEntryLinkLocalService.
				getFragmentEntryLinksBySegmentsExperienceId(
					themeDisplay.getScopeGroupId(), segmentsExperienceId,
					themeDisplay.getPlid(),
					_KEY_COLLECTION_FILTER_FRAGMENT_RENDERER));

		fragmentEntryLinks.addAll(
			_fragmentEntryLinkLocalService.
				getFragmentEntryLinksBySegmentsExperienceId(
					themeDisplay.getScopeGroupId(), segmentsExperienceId,
					themeDisplay.getPlid(),
					_KEY_COLLECTION_APPLIED_FILTERS_FRAGMENT_RENDERER));

		LayoutStructure layoutStructure =
			LayoutStructureUtil.getLayoutStructure(
				themeDisplay.getScopeGroupId(), themeDisplay.getPlid(),
				segmentsExperienceId);

		for (FragmentEntryLink fragmentEntryLink : fragmentEntryLinks) {
			JSONObject editableValuesJSONObject = _jsonFactory.createJSONObject(
				fragmentEntryLink.getEditableValues());

			String configuration = editableValuesJSONObject.getString(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR);

			if ((configuration == null) ||
				!JSONUtil.isJSONObject(configuration)) {

				continue;
			}

			JSONObject configurationJSONObject =
				editableValuesJSONObject.getJSONObject(
					FragmentEntryProcessorConstants.
						KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR);

			if (!configurationJSONObject.has("targetCollections")) {
				continue;
			}

			List<String> targetCollections = JSONUtil.toStringList(
				configurationJSONObject.getJSONArray("targetCollections"));

			if (!targetCollections.contains(itemId)) {
				continue;
			}

			targetCollections.remove(itemId);

			configurationJSONObject.put(
				"targetCollections",
				JSONUtil.toJSONArray(
					targetCollections,
					targetCollectionItemId -> targetCollectionItemId));

			if (targetCollections.isEmpty()) {
				configurationJSONObject.put("filterKey", StringPool.BLANK);
			}

			editableValuesJSONObject.put(
				FragmentEntryProcessorConstants.
					KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				configurationJSONObject);

			long fragmentEntryLinkId =
				fragmentEntryLink.getFragmentEntryLinkId();

			fragmentEntryLink =
				_fragmentEntryLinkService.updateFragmentEntryLink(
					fragmentEntryLinkId, editableValuesJSONObject.toString());

			fragmentEntryLinksJSONArray.put(
				_fragmentEntryLinkManager.getFragmentEntryLinkJSONObject(
					fragmentEntryLink,
					_portal.getHttpServletRequest(actionRequest),
					_portal.getHttpServletResponse(actionResponse),
					layoutStructure));
		}

		return JSONUtil.put(
			"fragmentEntryLinks", fragmentEntryLinksJSONArray
		).put(
			"layoutData",
			LayoutStructureUtil.updateLayoutPageTemplateData(
				themeDisplay.getScopeGroupId(), segmentsExperienceId,
				themeDisplay.getPlid(),
				curLayoutStructure -> curLayoutStructure.updateItemConfig(
					_jsonFactory.createJSONObject(itemConfig), itemId))
		);
	}

	private static final String
		_KEY_COLLECTION_APPLIED_FILTERS_FRAGMENT_RENDERER =
			"com.liferay.fragment.renderer.collection.filter.internal." +
				"CollectionAppliedFiltersFragmentRenderer";

	private static final String _KEY_COLLECTION_FILTER_FRAGMENT_RENDERER =
		"com.liferay.fragment.renderer.collection.filter.internal." +
			"CollectionFilterFragmentRenderer";

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private FragmentEntryLinkManager _fragmentEntryLinkManager;

	@Reference
	private FragmentEntryLinkService _fragmentEntryLinkService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

}