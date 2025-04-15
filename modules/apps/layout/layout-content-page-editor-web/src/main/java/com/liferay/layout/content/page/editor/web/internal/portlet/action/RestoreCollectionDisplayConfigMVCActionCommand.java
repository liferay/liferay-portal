/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.util.layout.structure.LayoutStructureUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/restore_collection_display_config"
	},
	service = MVCActionCommand.class
)
public class RestoreCollectionDisplayConfigMVCActionCommand
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

		String filterFragmentEntryLinks = ParamUtil.getString(
			actionRequest, "filterFragmentEntryLinks");

		JSONArray filterFragmentEntryLinksJSONArray =
			_jsonFactory.createJSONArray(filterFragmentEntryLinks);

		for (Object filterFragmentEntryLink :
				filterFragmentEntryLinksJSONArray) {

			JSONObject fragmentEntryLinkJSONObject =
				(JSONObject)filterFragmentEntryLink;

			FragmentEntryLink fragmentEntryLink =
				_fragmentEntryLinkLocalService.getFragmentEntryLink(
					fragmentEntryLinkJSONObject.getLong("fragmentEntryLinkId"));

			JSONObject editableValuesJSONObject =
				fragmentEntryLinkJSONObject.getJSONObject("editableValues");

			fragmentEntryLink.setEditableValues(
				editableValuesJSONObject.toString());
		}

		LayoutStructureUtil.updateLayoutPageTemplateData(
			themeDisplay.getScopeGroupId(), segmentsExperienceId,
			themeDisplay.getPlid(),
			layoutStructure -> layoutStructure.updateItemConfig(
				_jsonFactory.createJSONObject(itemConfig), itemId));

		return _jsonFactory.createJSONObject();
	}

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private JSONFactory _jsonFactory;

}