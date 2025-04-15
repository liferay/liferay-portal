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
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.model.SegmentsExperiment;
import com.liferay.segments.model.SegmentsExperimentRel;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.service.SegmentsExperienceService;
import com.liferay.segments.service.SegmentsExperimentRelService;
import com.liferay.segments.service.SegmentsExperimentService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Arques
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/add_segments_experience"
	},
	service = MVCActionCommand.class
)
public class AddSegmentsExperienceMVCActionCommand
	extends BaseContentPageEditorTransactionalMVCActionCommand {

	protected JSONObject addSegmentsExperience(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		SegmentsExperiment segmentsExperiment = _getSegmentsExperiment(
			actionRequest);

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			actionRequest);

		SegmentsExperience segmentsExperience = _addSegmentsExperience(
			actionRequest, themeDisplay.getPlid(), segmentsExperiment,
			serviceContext);

		SegmentsExperience baseSegmentsExperience = _getBaseSegmentsExperience(
			themeDisplay.getPlid(), segmentsExperiment);

		SegmentsExperienceUtil.copySegmentsExperienceData(
			_commentManager, themeDisplay.getScopeGroupId(),
			themeDisplay.getLayout(), _portletRegistry, baseSegmentsExperience,
			segmentsExperience, className -> serviceContext,
			_portal.getUserId(actionRequest));

		JSONObject jsonObject = JSONUtil.put(
			"fragmentEntryLinks",
			_getFragmentEntryLinksJSONObject(
				actionRequest, actionResponse, themeDisplay.getPlid(),
				themeDisplay.getScopeGroupId(),
				segmentsExperience.getSegmentsExperienceId())
		).put(
			"layoutData",
			_getLayoutDataJSONObject(
				themeDisplay.getPlid(), themeDisplay.getScopeGroupId(),
				segmentsExperience.getSegmentsExperienceId())
		).put(
			"segmentsExperience",
			SegmentsExperienceUtil.getSegmentsExperienceJSONObject(
				segmentsExperience)
		);

		if (segmentsExperiment == null) {
			return jsonObject;
		}

		SegmentsExperimentRel segmentsExperimentRel = _addSegmentsExperimentRel(
			actionRequest, segmentsExperiment, segmentsExperience);

		jsonObject.put(
			"segmentsExperimentRel",
			_getSegmentsSegmentsExperimentRelJSONObject(
				segmentsExperimentRel, _portal.getLocale(actionRequest)));

		_initializeDraftLayout(
			themeDisplay.getScopeGroupId(), themeDisplay.getPlid(),
			segmentsExperience, baseSegmentsExperience, serviceContext);

		return jsonObject;
	}

	@Override
	protected JSONObject doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		return addSegmentsExperience(actionRequest, actionResponse);
	}

	private SegmentsExperience _addSegmentsExperience(
			ActionRequest actionRequest, long plid,
			SegmentsExperiment segmentsExperiment,
			ServiceContext serviceContext)
		throws PortalException {

		if (segmentsExperiment != null) {
			SegmentsExperience segmentsExperience =
				_segmentsExperienceService.getSegmentsExperience(
					segmentsExperiment.getSegmentsExperienceId());

			return _segmentsExperienceService.appendSegmentsExperience(
				serviceContext.getScopeGroupId(),
				segmentsExperience.getSegmentsEntryId(), plid,
				Collections.singletonMap(
					LocaleUtil.getSiteDefault(),
					ParamUtil.getString(actionRequest, "name")),
				false, serviceContext);
		}

		return _segmentsExperienceService.addSegmentsExperience(
			null, serviceContext.getScopeGroupId(),
			ParamUtil.getLong(actionRequest, "segmentsEntryId"), plid,
			Collections.singletonMap(
				LocaleUtil.getSiteDefault(),
				ParamUtil.getString(actionRequest, "name")),
			ParamUtil.getBoolean(actionRequest, "active", true),
			new UnicodeProperties(true), serviceContext);
	}

	private SegmentsExperimentRel _addSegmentsExperimentRel(
			ActionRequest actionRequest, SegmentsExperiment segmentsExperiment,
			SegmentsExperience segmentsExperience)
		throws PortalException {

		return _segmentsExperimentRelService.addSegmentsExperimentRel(
			segmentsExperiment.getSegmentsExperimentId(),
			segmentsExperience.getSegmentsExperienceId(),
			ServiceContextFactory.getInstance(actionRequest));
	}

	private SegmentsExperience _getBaseSegmentsExperience(
		long plid, SegmentsExperiment segmentsExperiment) {

		if (segmentsExperiment == null) {
			return _segmentsExperienceLocalService.
				fetchDefaultSegmentsExperience(plid);
		}

		return _segmentsExperienceLocalService.fetchSegmentsExperience(
			segmentsExperiment.getSegmentsExperienceId());
	}

	private JSONObject _getFragmentEntryLinksJSONObject(
			ActionRequest actionRequest, ActionResponse actionResponse,
			long plid, long groupId, long segmentsExperienceId)
		throws PortalException {

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
		throws PortalException {

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(groupId, classPK);

		return _jsonFactory.createJSONObject(
			layoutPageTemplateStructure.getData(segmentsExperienceId));
	}

	private SegmentsExperiment _getSegmentsExperiment(
			ActionRequest actionRequest)
		throws PortalException {

		SegmentsExperiment segmentsExperiment = null;

		long segmentsExperimentId = ParamUtil.getLong(
			actionRequest, "segmentsExperimentId");

		if (segmentsExperimentId != GetterUtil.DEFAULT_LONG) {
			segmentsExperiment =
				_segmentsExperimentService.getSegmentsExperiment(
					segmentsExperimentId);
		}

		return segmentsExperiment;
	}

	private JSONObject _getSegmentsSegmentsExperimentRelJSONObject(
			SegmentsExperimentRel segmentsExperimentRel, Locale locale)
		throws PortalException {

		return JSONUtil.put(
			"name", segmentsExperimentRel.getName(locale)
		).put(
			"segmentsExperienceId",
			segmentsExperimentRel.getSegmentsExperienceId()
		).put(
			"segmentsExperimentId",
			segmentsExperimentRel.getSegmentsExperimentId()
		).put(
			"segmentsExperimentRelId",
			segmentsExperimentRel.getSegmentsExperimentRelId()
		).put(
			"split", segmentsExperimentRel.getSplit()
		);
	}

	private void _initializeDraftLayout(
			long groupId, long classPK, SegmentsExperience segmentsExperience,
			SegmentsExperience baseSegmentsExperience,
			ServiceContext serviceContext)
		throws PortalException {

		Layout draftLayout = _layoutLocalService.fetchDraftLayout(classPK);

		if (draftLayout == null) {
			return;
		}

		SegmentsExperience newSegmentsExperience =
			_segmentsExperienceService.appendSegmentsExperience(
				serviceContext.getScopeGroupId(),
				segmentsExperience.getSegmentsEntryId(), draftLayout.getPlid(),
				segmentsExperience.getNameMap(), false, serviceContext);

		newSegmentsExperience.setSegmentsExperienceKey(
			segmentsExperience.getSegmentsExperienceKey());

		newSegmentsExperience =
			_segmentsExperienceLocalService.updateSegmentsExperience(
				newSegmentsExperience);

		SegmentsExperienceUtil.copySegmentsExperienceData(
			_commentManager, groupId, draftLayout, _portletRegistry,
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				baseSegmentsExperience.getGroupId(),
				baseSegmentsExperience.getSegmentsExperienceKey(),
				draftLayout.getPlid()),
			newSegmentsExperience, className -> serviceContext,
			serviceContext.getUserId());
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
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private PortletRegistry _portletRegistry;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Reference
	private SegmentsExperienceService _segmentsExperienceService;

	@Reference
	private SegmentsExperimentRelService _segmentsExperimentRelService;

	@Reference
	private SegmentsExperimentService _segmentsExperimentService;

}