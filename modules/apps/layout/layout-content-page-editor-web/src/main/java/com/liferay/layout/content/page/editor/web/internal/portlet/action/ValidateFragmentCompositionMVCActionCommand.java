/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.manager.FragmentCollectionManager;
import com.liferay.layout.content.page.editor.web.internal.util.layout.structure.LayoutStructureUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Georgel Pop
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/validate_fragment_composition"
	},
	service = MVCActionCommand.class
)
public class ValidateFragmentCompositionMVCActionCommand
	extends BaseContentPageEditorTransactionalMVCActionCommand {

	@Override
	protected JSONObject doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		boolean hasMissingFragmentEntry =
			LayoutStructureUtil.hasMissingFragmentEntryFragmentEntryLinks(
				_fragmentCollectionManager.getGroupIds(
					themeDisplay.getCompanyId(),
					themeDisplay.getCompanyGroupId(),
					themeDisplay.getScopeGroupId()),
				ParamUtil.getString(actionRequest, "itemId"),
				LayoutStructureUtil.getLayoutStructure(
					themeDisplay.getScopeGroupId(), themeDisplay.getPlid(),
					ParamUtil.getLong(actionRequest, "segmentsExperienceId")));

		return JSONUtil.put("valid", !hasMissingFragmentEntry);
	}

	@Reference
	private FragmentCollectionManager _fragmentCollectionManager;

}