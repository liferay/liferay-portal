/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.manager.FormItemManager;
import com.liferay.layout.content.page.editor.web.internal.manager.FragmentEntryLinkManager;
import com.liferay.layout.content.page.editor.web.internal.util.layout.structure.LayoutStructureUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
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

import java.util.Arrays;
import java.util.Collections;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/undo_form_item_config"
	},
	service = MVCActionCommand.class
)
public class UndoFormItemConfigMVCActionCommand
	extends BaseContentPageEditorTransactionalMVCActionCommand {

	@Override
	protected JSONObject doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String[] addedItemIds = ParamUtil.getStringValues(
			actionRequest, "addedItemIds");
		JSONObject configJSONObject = _jsonFactory.createJSONObject(
			ParamUtil.getString(actionRequest, "config"));
		String itemId = ParamUtil.getString(actionRequest, "itemId");
		JSONArray movedItemsJSONArray = _jsonFactory.createJSONArray(
			ParamUtil.getString(actionRequest, "movedItemIds"));
		String[] removedItemIds = ParamUtil.getStringValues(
			actionRequest, "removedItemIds");
		long segmentsExperienceId = ParamUtil.getLong(
			actionRequest, "segmentsExperienceId");

		return JSONUtil.put(
			"fragmentEntryLinks",
			() -> {
				long stepperFragmentEntryLinkId = ParamUtil.getLong(
					actionRequest, "stepperFragmentEntryLinkId");

				FragmentEntryLink stepperFragmentEntryLink =
					_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
						stepperFragmentEntryLinkId);

				if (stepperFragmentEntryLink == null) {
					return null;
				}

				stepperFragmentEntryLink =
					_formItemManager.updateNumberOfStepps(
						_portal.getHttpServletRequest(actionRequest),
						_portal.getHttpServletResponse(actionResponse),
						configJSONObject.getInt("numberOfSteps"),
						stepperFragmentEntryLink);

				LayoutPageTemplateStructure layoutPageTemplateStructure =
					_layoutPageTemplateStructureLocalService.
						fetchLayoutPageTemplateStructure(
							themeDisplay.getScopeGroupId(),
							themeDisplay.getPlid());

				LayoutStructure layoutStructure = LayoutStructure.of(
					layoutPageTemplateStructure.getData(segmentsExperienceId));

				return JSONUtil.put(
					String.valueOf(
						stepperFragmentEntryLink.getFragmentEntryLinkId()),
					_fragmentEntryLinkManager.getFragmentEntryLinkJSONObject(
						stepperFragmentEntryLink,
						_portal.getHttpServletRequest(actionRequest),
						_portal.getHttpServletResponse(actionResponse),
						layoutStructure));
			}
		).put(
			"layoutData",
			LayoutStructureUtil.updateLayoutPageTemplateData(
				themeDisplay.getScopeGroupId(), segmentsExperienceId,
				themeDisplay.getPlid(),
				layoutStructure -> {
					LayoutStructureItem layoutStructureItem =
						layoutStructure.getLayoutStructureItem(itemId);

					layoutStructureItem.updateItemConfig(configJSONObject);

					for (int i = 0; i < movedItemsJSONArray.length(); i++) {
						JSONObject jsonObject =
							movedItemsJSONArray.getJSONObject(i);

						layoutStructure.moveLayoutStructureItem(
							jsonObject.getString("itemId"),
							jsonObject.getString("parentId"),
							jsonObject.getInt("position"));
					}

					layoutStructure.markLayoutStructureItemForDeletion(
						Arrays.asList(removedItemIds), Collections.emptyList());

					for (String addedItemId : addedItemIds) {
						layoutStructure.unmarkLayoutStructureItemForDeletion(
							addedItemId);
					}
				})
		);
	}

	@Reference
	private FormItemManager _formItemManager;

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private FragmentEntryLinkManager _fragmentEntryLinkManager;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Reference
	private Portal _portal;

}