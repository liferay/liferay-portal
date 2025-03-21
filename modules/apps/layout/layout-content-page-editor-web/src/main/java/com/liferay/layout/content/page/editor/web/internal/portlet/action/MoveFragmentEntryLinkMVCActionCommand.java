/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.manager.FormItemManager;
import com.liferay.layout.content.page.editor.web.internal.util.layout.structure.LayoutStructureUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Arrays;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/move_fragment_entry_link"
	},
	service = MVCActionCommand.class
)
public class MoveFragmentEntryLinkMVCActionCommand
	extends BaseContentPageEditorTransactionalMVCActionCommand {

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

		String[] parentItemIds = ParamUtil.getStringValues(
			actionRequest, "parentItemIds");

		if (parentItemIds.length < 2) {
			String curParentItemId = parentItemIds[0];

			parentItemIds = new String[finalItemIds.length];

			Arrays.fill(parentItemIds, curParentItemId);
		}

		String[] finalParentItemIds = parentItemIds;

		int[] positions = ParamUtil.getIntegerValues(
			actionRequest, "positions");

		if (positions.length < 2) {
			int curPosition = positions[0];

			positions = new int[finalItemIds.length];

			for (int i = 0; i < positions.length; i++) {
				positions[i] = i + curPosition;
			}
		}

		int[] finalPositions = positions;

		return LayoutStructureUtil.updateLayoutPageTemplateData(
			themeDisplay.getScopeGroupId(), segmentsExperienceId,
			themeDisplay.getPlid(),
			layoutStructure -> {
				for (int i = 0; i < finalItemIds.length; i++) {
					_formItemManager.checkFormContainerParentItemRequired(
						new String[] {finalItemIds[i]}, layoutStructure,
						finalParentItemIds[i]);

					layoutStructure.moveLayoutStructureItem(
						finalItemIds[i], finalParentItemIds[i],
						finalPositions[i]);
				}
			});
	}

	@Reference
	private FormItemManager _formItemManager;

}