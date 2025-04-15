/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.util.layout.structure.LayoutStructureUtil;
import com.liferay.layout.util.structure.LayoutStructureRule;
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
 * @author Sandro Chinea
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/add_rule"
	},
	service = MVCActionCommand.class
)
public class AddRuleMVCActionCommand
	extends BaseContentPageEditorTransactionalMVCActionCommand {

	@Override
	protected JSONObject doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		jsonObject.put(
			"layoutData",
			LayoutStructureUtil.updateLayoutPageTemplateData(
				themeDisplay.getScopeGroupId(),
				ParamUtil.getLong(actionRequest, "segmentsExperienceId"),
				themeDisplay.getPlid(),
				layoutStructure -> {
					LayoutStructureRule layoutStructureRule =
						layoutStructure.addLayoutStructureRule(
							ParamUtil.getString(actionRequest, "name"));

					layoutStructureRule.setActionsJSONArray(
						_jsonFactory.createJSONArray(
							ParamUtil.getString(actionRequest, "actions")));
					layoutStructureRule.setConditionsJSONArray(
						_jsonFactory.createJSONArray(
							ParamUtil.getString(actionRequest, "conditions")));
					layoutStructureRule.setConditionType(
						ParamUtil.getString(actionRequest, "conditionType"));

					jsonObject.put("addedRuleId", layoutStructureRule.getId());
				}));

		return jsonObject;
	}

	@Reference
	private JSONFactory _jsonFactory;

}