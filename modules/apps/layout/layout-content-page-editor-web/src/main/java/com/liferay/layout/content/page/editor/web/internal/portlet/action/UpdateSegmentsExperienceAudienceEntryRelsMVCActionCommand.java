/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.service.SegmentsExperienceAudienceEntryRelService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/update_segments_experience_audience_entry_rels"
	},
	service = MVCActionCommand.class
)
public class UpdateSegmentsExperienceAudienceEntryRelsMVCActionCommand
	extends BaseContentPageEditorTransactionalMVCActionCommand {

	@Override
	protected JSONObject doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_segmentsExperienceAudienceEntryRelService.
			updateSegmentsExperienceAudienceEntryRels(
				themeDisplay.getScopeGroupId(),
				JSONUtil.toStringArray(
					_jsonFactory.createJSONArray(
						ParamUtil.getString(
							actionRequest, "audienceEntryERCs"))),
				ParamUtil.getString(actionRequest, "segmentsExperienceERC"));

		return _jsonFactory.createJSONObject();
	}

	@Override
	protected boolean isLayoutLockRequired() {
		return false;
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private SegmentsExperienceAudienceEntryRelService
		_segmentsExperienceAudienceEntryRelService;

}