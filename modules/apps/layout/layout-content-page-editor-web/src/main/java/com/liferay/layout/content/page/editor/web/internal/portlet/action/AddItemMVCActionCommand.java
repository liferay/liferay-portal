/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.util.layout.structure.LayoutStructureUtil;
import com.liferay.layout.responsive.ViewportSize;
import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;
import com.liferay.layout.util.structure.CollectionStyledLayoutStructureItem;
import com.liferay.layout.util.structure.ColumnLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.layout.util.structure.RowStyledLayoutStructureItem;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/add_item"
	},
	service = MVCActionCommand.class
)
public class AddItemMVCActionCommand
	extends BaseContentPageEditorTransactionalMVCActionCommand {

	@Override
	protected JSONObject doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		return _addItemToLayoutData(actionRequest);
	}

	private LayoutStructureItem _addCollectionStyledLayoutStructureItem(
		LayoutStructure layoutStructure, String parentItemId, int position) {

		CollectionStyledLayoutStructureItem
			collectionStyledLayoutStructureItem =
				(CollectionStyledLayoutStructureItem)
					layoutStructure.addCollectionStyledLayoutStructureItem(
						parentItemId, position);

		collectionStyledLayoutStructureItem.setViewportConfiguration(
			ViewportSize.MOBILE_LANDSCAPE.getViewportSizeId(),
			JSONUtil.put("numberOfColumns", 1));
		collectionStyledLayoutStructureItem.setViewportConfiguration(
			ViewportSize.PORTRAIT_MOBILE.getViewportSizeId(),
			JSONUtil.put("numberOfColumns", 1));
		collectionStyledLayoutStructureItem.setViewportConfiguration(
			ViewportSize.TABLET.getViewportSizeId(),
			JSONUtil.put("numberOfColumns", 1));

		return collectionStyledLayoutStructureItem;
	}

	private JSONObject _addItemToLayoutData(ActionRequest actionRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long segmentsExperienceId = ParamUtil.getLong(
			actionRequest, "segmentsExperienceId");
		String itemType = ParamUtil.getString(actionRequest, "itemType");
		String parentItemId = ParamUtil.getString(
			actionRequest, "parentItemId");
		int position = ParamUtil.getInteger(actionRequest, "position");

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		JSONObject layoutDataJSONObject = null;

		if (Objects.equals(
				itemType, LayoutDataItemTypeConstants.TYPE_COLLECTION)) {

			layoutDataJSONObject =
				LayoutStructureUtil.updateLayoutPageTemplateData(
					themeDisplay.getScopeGroupId(), segmentsExperienceId,
					themeDisplay.getPlid(),
					layoutStructure -> {
						LayoutStructureItem layoutStructureItem =
							_addCollectionStyledLayoutStructureItem(
								layoutStructure, parentItemId, position);

						jsonObject.put(
							"addedItemId", layoutStructureItem.getItemId());
					});
		}
		else if (Objects.equals(
					itemType, LayoutDataItemTypeConstants.TYPE_ROW)) {

			layoutDataJSONObject =
				LayoutStructureUtil.updateLayoutPageTemplateData(
					themeDisplay.getScopeGroupId(), segmentsExperienceId,
					themeDisplay.getPlid(),
					layoutStructure -> {
						LayoutStructureItem layoutStructureItem =
							_addRowSyledLayoutStructureItem(
								layoutStructure, parentItemId, position);

						jsonObject.put(
							"addedItemId", layoutStructureItem.getItemId());
					});
		}
		else {
			layoutDataJSONObject =
				LayoutStructureUtil.updateLayoutPageTemplateData(
					themeDisplay.getScopeGroupId(), segmentsExperienceId,
					themeDisplay.getPlid(),
					layoutStructure -> {
						LayoutStructureItem layoutStructureItem =
							layoutStructure.addLayoutStructureItem(
								itemType, parentItemId, position);

						jsonObject.put(
							"addedItemId", layoutStructureItem.getItemId());
					});
		}

		return jsonObject.put("layoutData", layoutDataJSONObject);
	}

	private LayoutStructureItem _addRowSyledLayoutStructureItem(
		LayoutStructure layoutStructure, String parentItemId, int position) {

		RowStyledLayoutStructureItem rowStyledLayoutStructureItem =
			(RowStyledLayoutStructureItem)
				layoutStructure.addRowStyledLayoutStructureItem(
					parentItemId, position, _DEFAULT_ROW_COLUMNS);

		rowStyledLayoutStructureItem.setViewportConfiguration(
			ViewportSize.MOBILE_LANDSCAPE.getViewportSizeId(),
			JSONUtil.put("modulesPerRow", 1));

		for (int i = 0; i < _DEFAULT_ROW_COLUMNS; i++) {
			ColumnLayoutStructureItem columnLayoutStructureItem =
				(ColumnLayoutStructureItem)
					layoutStructure.addColumnLayoutStructureItem(
						rowStyledLayoutStructureItem.getItemId(), i);

			columnLayoutStructureItem.setSize(4);
			columnLayoutStructureItem.setViewportConfiguration(
				ViewportSize.MOBILE_LANDSCAPE.getViewportSizeId(),
				JSONUtil.put("size", 12));
		}

		return rowStyledLayoutStructureItem;
	}

	private static final int _DEFAULT_ROW_COLUMNS = 3;

	@Reference
	private JSONFactory _jsonFactory;

}