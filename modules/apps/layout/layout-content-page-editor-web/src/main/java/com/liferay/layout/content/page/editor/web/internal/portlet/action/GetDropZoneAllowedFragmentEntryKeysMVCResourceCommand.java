/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.util.layout.structure.LayoutStructureUtil;
import com.liferay.layout.helper.DropZoneAllowedFragmentEntriesHelper;
import com.liferay.layout.util.structure.DropZoneLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.constants.SegmentsExperienceConstants;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/get_drop_zone_allowed_fragment_entry_keys"
	},
	service = MVCResourceCommand.class
)
public class GetDropZoneAllowedFragmentEntryKeysMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		LayoutStructure layoutStructure =
			LayoutStructureUtil.getLayoutStructure(
				layout.getGroupId(), layout.getPlid(),
				SegmentsExperienceConstants.KEY_DEFAULT);

		LayoutStructureItem layoutStructureItem =
			layoutStructure.getLayoutStructureItem(
				ParamUtil.getString(resourceRequest, "itemId"));

		List<String> fragmentEntryKeys = Collections.emptyList();

		if (layoutStructureItem instanceof DropZoneLayoutStructureItem) {
			fragmentEntryKeys =
				_dropZoneAllowedFragmentEntriesHelper.getFragmentEntryKeys(
					layout.getCompanyId(),
					(DropZoneLayoutStructureItem)layoutStructureItem,
					layout.getGroupId());
		}

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			JSONUtil.put(
				"fragmentEntryKeys",
				JSONUtil.putAll(fragmentEntryKeys.toArray())));
	}

	@Reference
	private DropZoneAllowedFragmentEntriesHelper
		_dropZoneAllowedFragmentEntriesHelper;

}