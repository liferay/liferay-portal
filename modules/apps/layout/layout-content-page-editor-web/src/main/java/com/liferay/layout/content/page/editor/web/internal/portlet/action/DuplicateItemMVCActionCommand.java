/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.fragment.listener.FragmentEntryLinkListener;
import com.liferay.fragment.listener.FragmentEntryLinkListenerRegistry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.manager.ContentManager;
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
 * @author Jürgen Kappler
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/duplicate_item"
	},
	service = MVCActionCommand.class
)
public class DuplicateItemMVCActionCommand
	extends BaseDuplicateItemMVCActionCommand {

	@Override
	protected JSONObject doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long segmentsExperienceId = ParamUtil.getLong(
			actionRequest, "segmentsExperienceId");

		String[] itemIds = null;

		String itemId = ParamUtil.getString(actionRequest, "itemId");

		if (Validator.isNotNull(itemId)) {
			itemIds = new String[] {itemId};
		}
		else {
			itemIds = ParamUtil.getStringValues(actionRequest, "itemIds");
		}

		String[] finalItemIds = itemIds;

		Map<Long, Long> duplicatedFragmentEntryLinkIdsMap = new HashMap<>();
		List<String> duplicatedLayoutStructureItemIds = new ArrayList<>();

		LayoutStructureUtil.updateLayoutPageTemplateData(
			themeDisplay.getScopeGroupId(), segmentsExperienceId,
			themeDisplay.getPlid(),
			layoutStructure -> {
				Map<String, List<LayoutStructureItem>>
					duplicatedLayoutStructureItemsMap =
						layoutStructure.duplicateLayoutStructureItem(
							Arrays.asList(finalItemIds));

				for (Map.Entry<String, List<LayoutStructureItem>> entry :
						duplicatedLayoutStructureItemsMap.entrySet()) {

					duplicatedLayoutStructureItemIds.add(entry.getKey());

					for (LayoutStructureItem duplicatedLayoutStructureItem :
							entry.getValue()) {

						if (!(duplicatedLayoutStructureItem instanceof
								FragmentStyledLayoutStructureItem)) {

							continue;
						}

						FragmentStyledLayoutStructureItem
							fragmentStyledLayoutStructureItem =
								(FragmentStyledLayoutStructureItem)
									duplicatedLayoutStructureItem;

						long originalFragmentEntryLinkId =
							fragmentStyledLayoutStructureItem.
								getFragmentEntryLinkId();

						long fragmentEntryLinkId = duplicateFragmentEntryLink(
							actionRequest, originalFragmentEntryLinkId);

						layoutStructure.updateItemConfig(
							JSONUtil.put(
								"fragmentEntryLinkId", fragmentEntryLinkId),
							duplicatedLayoutStructureItem.getItemId());

						duplicatedFragmentEntryLinkIdsMap.put(
							fragmentEntryLinkId, originalFragmentEntryLinkId);
					}
				}
			});

		for (Map.Entry<Long, Long> entry :
				duplicatedFragmentEntryLinkIdsMap.entrySet()) {

			FragmentEntryLink duplicatedFragmentEntryLink =
				fragmentEntryLinkLocalService.getFragmentEntryLink(
					entry.getKey());

			FragmentEntryLink originalFragmentEntryLink =
				fragmentEntryLinkLocalService.getFragmentEntryLink(
					entry.getValue());

			for (FragmentEntryLinkListener fragmentEntryLinkListener :
					_fragmentEntryLinkListenerRegistry.
						getFragmentEntryLinkListeners()) {

				fragmentEntryLinkListener.onDuplicateFragmentEntryLink(
					duplicatedFragmentEntryLink, originalFragmentEntryLink);
			}
		}

		LayoutStructure layoutStructure =
			LayoutStructureUtil.getLayoutStructure(
				themeDisplay.getScopeGroupId(), themeDisplay.getPlid(),
				segmentsExperienceId);

		return JSONUtil.put(
			"duplicatedFragmentEntryLinks",
			getFragmentEntryLinksJSONArray(
				actionRequest, actionResponse,
				duplicatedFragmentEntryLinkIdsMap.keySet(),
				segmentsExperienceId, themeDisplay)
		).put(
			"duplicatedItemIds", duplicatedLayoutStructureItemIds
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
		return "the-layout-could-not-be-duplicated-because-it-contains-a-" +
			"widget-x-that-can-only-appear-once-on-the-page";
	}

	@Override
	protected String getNoSuchEntryLinkExceptionMessage() {
		return "the-section-could-not-be-duplicated-because-it-no-longer-" +
			"exists";
	}

	@Reference
	private ContentManager _contentManager;

	@Reference
	private FragmentEntryLinkListenerRegistry
		_fragmentEntryLinkListenerRegistry;

}