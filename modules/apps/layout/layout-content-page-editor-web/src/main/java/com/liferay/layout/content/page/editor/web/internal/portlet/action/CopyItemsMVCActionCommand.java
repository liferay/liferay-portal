/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.fragment.listener.FragmentEntryLinkListener;
import com.liferay.fragment.listener.FragmentEntryLinkListenerRegistry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.manager.ContentManager;
import com.liferay.layout.content.page.editor.web.internal.manager.FormItemManager;
import com.liferay.layout.content.page.editor.web.internal.util.layout.structure.LayoutStructureUtil;
import com.liferay.layout.util.structure.FragmentStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Georgel Pop
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/copy_items"
	},
	service = MVCActionCommand.class
)
public class CopyItemsMVCActionCommand
	extends BaseDuplicateItemMVCActionCommand {

	@Override
	protected JSONObject doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long segmentsExperienceId = ParamUtil.getLong(
			actionRequest, "segmentsExperienceId");

		String parentItemId = ParamUtil.getString(
			actionRequest, "parentItemId");

		String[] itemIds = null;

		String itemId = ParamUtil.getString(actionRequest, "itemId");

		if (Validator.isNotNull(itemId)) {
			itemIds = new String[] {itemId};
		}
		else {
			itemIds = ParamUtil.getStringValues(actionRequest, "itemIds");
		}

		String[] finalItemIds = itemIds;

		Map<Long, Long> copiedFragmentEntryLinkIdsMap = new HashMap<>();
		List<String> copiedLayoutStructureItemIds = new ArrayList<>();

		LayoutStructureUtil.updateLayoutPageTemplateData(
			themeDisplay.getScopeGroupId(), segmentsExperienceId,
			themeDisplay.getPlid(),
			layoutStructure -> {
				_formItemManager.checkFormContainerParentItemRequired(
					finalItemIds, layoutStructure, parentItemId);

				List<LayoutStructureItem> copiedLayoutStructureItems =
					layoutStructure.copyLayoutStructureItems(
						Arrays.asList(finalItemIds), parentItemId);

				for (LayoutStructureItem copiedLayoutStructureItem :
						copiedLayoutStructureItems) {

					copiedLayoutStructureItemIds.add(
						copiedLayoutStructureItem.getItemId());

					if (!(copiedLayoutStructureItem instanceof
							FragmentStyledLayoutStructureItem)) {

						continue;
					}

					FragmentStyledLayoutStructureItem
						fragmentStyledLayoutStructureItem =
							(FragmentStyledLayoutStructureItem)
								copiedLayoutStructureItem;

					long originalFragmentEntryLinkId =
						fragmentStyledLayoutStructureItem.
							getFragmentEntryLinkId();

					long fragmentEntryLinkId = duplicateFragmentEntryLink(
						actionRequest, originalFragmentEntryLinkId);

					layoutStructure.updateItemConfig(
						JSONUtil.put(
							"fragmentEntryLinkId", fragmentEntryLinkId),
						copiedLayoutStructureItem.getItemId());

					copiedFragmentEntryLinkIdsMap.put(
						fragmentEntryLinkId, originalFragmentEntryLinkId);
				}
			});

		for (Map.Entry<Long, Long> entry :
				copiedFragmentEntryLinkIdsMap.entrySet()) {

			FragmentEntryLink copiedFragmentEntryLink =
				fragmentEntryLinkLocalService.getFragmentEntryLink(
					entry.getKey());

			FragmentEntryLink originalFragmentEntryLink =
				fragmentEntryLinkLocalService.getFragmentEntryLink(
					entry.getValue());

			for (FragmentEntryLinkListener fragmentEntryLinkListener :
					_fragmentEntryLinkListenerRegistry.
						getFragmentEntryLinkListeners()) {

				fragmentEntryLinkListener.onCopyFragmentEntryLink(
					copiedFragmentEntryLink, originalFragmentEntryLink);
			}
		}

		LayoutStructure layoutStructure =
			LayoutStructureUtil.getLayoutStructure(
				themeDisplay.getScopeGroupId(), themeDisplay.getPlid(),
				segmentsExperienceId);

		return JSONUtil.put(
			"copiedFragmentEntryLinks",
			getFragmentEntryLinksJSONArray(
				actionRequest, actionResponse,
				copiedFragmentEntryLinkIdsMap.keySet(), segmentsExperienceId,
				themeDisplay)
		).put(
			"copiedItemIds", copiedLayoutStructureItemIds
		).put(
			"layoutData", layoutStructure.toJSONObject()
		).put(
			"restrictedItemIds",
			_contentManager.getRestrictedItemIds(
				portal.getHttpServletRequest(actionRequest), layoutStructure,
				themeDisplay)
		);
	}

	@Override
	protected String getNoninstanceablePortletExceptionMessage() {
		return "the-layout-could-not-be-copied-because-it-contains-a-widget-" +
			"x-that-can-only-appear-once-on-the-page";
	}

	@Override
	protected String getNoSuchEntryLinkExceptionMessage() {
		return "the-section-could-not-be-copied-because-it-no-longer-exists";
	}

	@Reference
	private ContentManager _contentManager;

	@Reference
	private FormItemManager _formItemManager;

	@Reference
	private FragmentEntryLinkListenerRegistry
		_fragmentEntryLinkListenerRegistry;

}