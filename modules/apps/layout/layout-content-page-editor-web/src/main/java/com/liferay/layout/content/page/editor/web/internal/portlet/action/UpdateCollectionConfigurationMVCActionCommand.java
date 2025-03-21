/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.util.layout.structure.LayoutStructureUtil;
import com.liferay.layout.util.structure.CollectionStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructureItem;
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
 * @author Lourdes Fernández Besada
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/update_collection_configuration"
	},
	service = MVCActionCommand.class
)
public class UpdateCollectionConfigurationMVCActionCommand
	extends BaseContentPageEditorMVCActionCommand {

	@Override
	protected void doCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long segmentsExperienceId = ParamUtil.getLong(
			actionRequest, "segmentsExperienceId");
		long plid = ParamUtil.getLong(actionRequest, "plid");

		LayoutStructureUtil.updateLayoutPageTemplateData(
			themeDisplay.getScopeGroupId(), segmentsExperienceId, plid,
			layoutStructure -> {
				String itemId = ParamUtil.getString(actionRequest, "itemId");

				LayoutStructureItem layoutStructureItem =
					layoutStructure.getLayoutStructureItem(itemId);

				if (!(layoutStructureItem instanceof
						CollectionStyledLayoutStructureItem)) {

					return;
				}

				String collectionConfig = ParamUtil.getString(
					actionRequest, "collectionConfig");

				CollectionStyledLayoutStructureItem
					collectionStyledLayoutStructureItem =
						(CollectionStyledLayoutStructureItem)
							layoutStructureItem;

				JSONObject collectionJSONObject =
					collectionStyledLayoutStructureItem.
						getCollectionJSONObject();

				collectionJSONObject.put(
					"config", _jsonFactory.createJSONObject(collectionConfig));

				collectionStyledLayoutStructureItem.setCollectionJSONObject(
					collectionJSONObject);

				layoutStructure.updateItemConfig(
					collectionStyledLayoutStructureItem.
						getItemConfigJSONObject(),
					itemId);
			});
	}

	@Reference
	private JSONFactory _jsonFactory;

}