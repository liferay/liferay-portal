/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.util.layout.structure.LayoutStructureUtil;
import com.liferay.layout.helper.DropZoneAllowedFragmentEntriesHelper;
import com.liferay.layout.util.structure.DropZoneLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
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
		"mvc.command.name=/layout_content_page_editor/update_item_config"
	},
	service = MVCActionCommand.class
)
public class UpdateItemConfigMVCActionCommand
	extends BaseContentPageEditorTransactionalMVCActionCommand {

	@Override
	protected JSONObject doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		return _updateItemConfig(actionRequest);
	}

	private JSONObject _toItemConfigJSONObject(
			long companyId, String itemConfig,
			LayoutStructureItem layoutStructureItem, long scopeGroupId)
		throws PortalException {

		JSONObject itemConfigJSONObject = _jsonFactory.createJSONObject(
			itemConfig);

		if (layoutStructureItem instanceof DropZoneLayoutStructureItem) {
			itemConfigJSONObject =
				_dropZoneAllowedFragmentEntriesHelper.
					normalizeItemConfigJSONObject(
						companyId, itemConfigJSONObject, scopeGroupId);
		}

		return itemConfigJSONObject;
	}

	private JSONObject _updateItemConfig(ActionRequest actionRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Layout layout = _layoutLocalService.fetchLayout(themeDisplay.getPlid());

		LayoutPermissionUtil.checkLayoutRestrictedUpdatePermission(
			themeDisplay.getPermissionChecker(), layout);

		long segmentsExperienceId = ParamUtil.getLong(
			actionRequest, "segmentsExperienceId");
		String itemConfig = ParamUtil.getString(actionRequest, "itemConfig");

		String[] itemIds = null;

		String itemId = ParamUtil.getString(actionRequest, "itemId");

		if (Validator.isNotNull(itemId)) {
			itemIds = new String[] {itemId};
		}
		else {
			itemIds = ParamUtil.getStringValues(actionRequest, "itemIds");
		}

		String[] finalItemIds = itemIds;

		return JSONUtil.put(
			"layoutData",
			LayoutStructureUtil.updateLayoutPageTemplateData(
				themeDisplay.getScopeGroupId(), segmentsExperienceId,
				themeDisplay.getPlid(),
				layoutStructure -> {
					for (String curItemId : finalItemIds) {
						layoutStructure.updateItemConfig(
							_toItemConfigJSONObject(
								layout.getCompanyId(), itemConfig,
								layoutStructure.getLayoutStructureItem(
									curItemId),
								themeDisplay.getScopeGroupId()),
							curItemId);
					}
				}));
	}

	@Reference
	private DropZoneAllowedFragmentEntriesHelper
		_dropZoneAllowedFragmentEntriesHelper;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LayoutLocalService _layoutLocalService;

}