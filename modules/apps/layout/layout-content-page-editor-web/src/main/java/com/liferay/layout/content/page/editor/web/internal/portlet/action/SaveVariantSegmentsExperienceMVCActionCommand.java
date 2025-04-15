/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.servlet.MultiSessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mikel Lorza
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/save_variant_segments_experience"
	},
	service = MVCActionCommand.class
)
public class SaveVariantSegmentsExperienceMVCActionCommand
	extends BaseContentPageEditorMVCActionCommand {

	@Override
	protected void doCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Layout draftLayout = _layoutLocalService.getLayout(
			themeDisplay.getPlid());

		if (!draftLayout.isDraftLayout()) {
			return;
		}

		Layout layout = _layoutLocalService.getLayout(draftLayout.getClassPK());

		LayoutPermissionUtil.checkLayoutUpdatePermission(
			themeDisplay.getPermissionChecker(), draftLayout);

		LayoutPermissionUtil.checkLayoutUpdatePermission(
			themeDisplay.getPermissionChecker(), layout);

		long segmentsExperienceId = ParamUtil.getLong(
			actionRequest, "segmentsExperienceId");

		SegmentsExperience sourceSegmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperienceId);

		SegmentsExperience targetSegmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				layout.getGroupId(),
				sourceSegmentsExperience.getSegmentsExperienceKey(),
				layout.getPlid());

		_layoutLocalService.copyLayoutContent(
			sourceSegmentsExperience.getSegmentsExperienceId(), draftLayout,
			targetSegmentsExperience.getSegmentsExperienceId(), layout);

		hideDefaultSuccessMessage(actionRequest);

		MultiSessionMessages.add(actionRequest, "variantSaved");
	}

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}