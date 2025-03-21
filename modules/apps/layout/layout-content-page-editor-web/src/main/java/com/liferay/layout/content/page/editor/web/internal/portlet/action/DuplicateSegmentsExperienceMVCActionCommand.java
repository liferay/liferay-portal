/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.PortletRegistry;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.manager.FragmentEntryLinkManager;
import com.liferay.layout.content.page.editor.web.internal.segments.SegmentsExperienceUtil;
import com.liferay.layout.content.page.editor.web.internal.util.layout.structure.LayoutStructureUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Arques
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/duplicate_segments_experience"
	},
	service = MVCActionCommand.class
)
public class DuplicateSegmentsExperienceMVCActionCommand
	extends BaseContentPageEditorTransactionalMVCActionCommand {

	@Override
	protected JSONObject doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long segmentsExperienceId = ParamUtil.getLong(
			actionRequest, "segmentsExperienceId");

		SegmentsExperience segmentsExperience =
			_segmentsExperienceService.getSegmentsExperience(
				segmentsExperienceId);

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			actionRequest);

		SegmentsExperience duplicatedSegmentsExperience =
			_segmentsExperienceService.addSegmentsExperience(
				null, serviceContext.getScopeGroupId(),
				segmentsExperience.getSegmentsEntryId(),
				segmentsExperience.getPlid(),
				Collections.singletonMap(
					LocaleUtil.getSiteDefault(),
					_language.format(
						themeDisplay.getLocale(), "copy-of-x",
						segmentsExperience.getName(
							LocaleUtil.getSiteDefault()))),
				segmentsExperience.isActive(), new UnicodeProperties(true),
				serviceContext);

		SegmentsExperienceUtil.copySegmentsExperienceData(
			_commentManager, themeDisplay.getScopeGroupId(),
			themeDisplay.getLayout(), _portletRegistry, segmentsExperience,
			duplicatedSegmentsExperience, className -> serviceContext,
			themeDisplay.getUserId());

		return JSONUtil.put(
			"fragmentEntryLinks",
			_getFragmentEntryLinksJSONObject(
				actionRequest, actionResponse, themeDisplay.getPlid(),
				themeDisplay.getScopeGroupId(),
				duplicatedSegmentsExperience.getSegmentsExperienceId())
		).put(
			"layoutData",
			_getLayoutDataJSONObject(
				themeDisplay.getPlid(), themeDisplay.getScopeGroupId(),
				duplicatedSegmentsExperience.getSegmentsExperienceId())
		).put(
			"segmentsExperience",
			SegmentsExperienceUtil.getSegmentsExperienceJSONObject(
				duplicatedSegmentsExperience)
		);
	}

	private JSONObject _getFragmentEntryLinksJSONObject(
			ActionRequest actionRequest, ActionResponse actionResponse,
			long plid, long groupId, long segmentsExperienceId)
		throws Exception {

		JSONObject fragmentEntryLinksJSONObject =
			_jsonFactory.createJSONObject();

		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.
				getFragmentEntryLinksBySegmentsExperienceId(
					groupId, segmentsExperienceId, plid);

		LayoutStructure layoutStructure =
			LayoutStructureUtil.getLayoutStructure(
				groupId, plid, segmentsExperienceId);

		for (FragmentEntryLink fragmentEntryLink : fragmentEntryLinks) {
			fragmentEntryLinksJSONObject.put(
				String.valueOf(fragmentEntryLink.getFragmentEntryLinkId()),
				_fragmentEntryLinkManager.getFragmentEntryLinkJSONObject(
					fragmentEntryLink,
					_portal.getHttpServletRequest(actionRequest),
					_portal.getHttpServletResponse(actionResponse),
					layoutStructure));
		}

		return fragmentEntryLinksJSONObject;
	}

	private JSONObject _getLayoutDataJSONObject(
			long classPK, long groupId, long segmentsExperienceId)
		throws Exception {

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(groupId, classPK);

		return _jsonFactory.createJSONObject(
			layoutPageTemplateStructure.getData(segmentsExperienceId));
	}

	@Reference
	private CommentManager _commentManager;

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private FragmentEntryLinkManager _fragmentEntryLinkManager;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private PortletRegistry _portletRegistry;

	@Reference
	private SegmentsExperienceService _segmentsExperienceService;

}