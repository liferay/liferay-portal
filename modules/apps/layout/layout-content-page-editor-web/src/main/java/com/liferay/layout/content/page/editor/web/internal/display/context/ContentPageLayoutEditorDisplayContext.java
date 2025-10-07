/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.display.context;

import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.search.InfoSearchClassMapperRegistry;
import com.liferay.item.selector.ItemSelector;
import com.liferay.layout.content.page.editor.sidebar.panel.ContentPageEditorSidebarPanel;
import com.liferay.layout.content.page.editor.web.internal.configuration.PageEditorConfiguration;
import com.liferay.layout.content.page.editor.web.internal.constants.ContentPageEditorActionKeys;
import com.liferay.layout.content.page.editor.web.internal.manager.FragmentCollectionManager;
import com.liferay.layout.content.page.editor.web.internal.manager.FragmentEntryLinkManager;
import com.liferay.layout.content.page.editor.web.internal.segments.SegmentsExperienceUtil;
import com.liferay.layout.manager.ContentManager;
import com.liferay.layout.manager.LayoutLockManager;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureRelLocalService;
import com.liferay.learn.LearnMessage;
import com.liferay.learn.LearnMessageUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermission;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.segments.configuration.provider.SegmentsConfigurationProvider;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.manager.SegmentsExperienceManager;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.model.SegmentsExperimentRel;
import com.liferay.segments.service.SegmentsEntryService;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.service.SegmentsExperimentRelLocalService;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.style.book.service.StyleBookEntryLocalService;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Eudaldo Alonso
 */
public class ContentPageLayoutEditorDisplayContext
	extends ContentPageEditorDisplayContext {

	public ContentPageLayoutEditorDisplayContext(
		List<ContentPageEditorSidebarPanel> contentPageEditorSidebarPanels,
		ContentManager contentManager,
		FragmentCollectionManager fragmentCollectionManager,
		FragmentEntryLinkManager fragmentEntryLinkManager,
		FragmentEntryLinkLocalService fragmentEntryLinkLocalService,
		FrontendTokenDefinitionRegistry frontendTokenDefinitionRegistry,
		GroupLocalService groupLocalService,
		HttpServletRequest httpServletRequest,
		InfoItemServiceRegistry infoItemServiceRegistry,
		InfoSearchClassMapperRegistry infoSearchClassMapperRegistry,
		ItemSelector itemSelector, JSONFactory jsonFactory, Language language,
		LayoutLocalService layoutLocalService,
		LayoutLockManager layoutLockManager,
		LayoutSetLocalService layoutSetLocalService,
		LayoutPageTemplateEntryLocalService layoutPageTemplateEntryLocalService,
		LayoutPageTemplateEntryService layoutPageTemplateEntryService,
		LayoutPageTemplateStructureLocalService
			layoutPageTemplateStructureLocalService,
		LayoutPageTemplateStructureRelLocalService
			layoutPageTemplateStructureRelLocalService,
		LayoutPermission layoutPermission,
		PageEditorConfiguration pageEditorConfiguration, Portal portal,
		PortletRequest portletRequest,
		PortletResourcePermission portletResourcePermission,
		PortletURLFactory portletURLFactory, RenderResponse renderResponse,
		SegmentsConfigurationProvider segmentsConfigurationProvider,
		SegmentsExperienceManager segmentsExperienceManager,
		SegmentsExperienceLocalService segmentsExperienceLocalService,
		SegmentsExperimentRelLocalService segmentsExperimentRelLocalService,
		SegmentsEntryService segmentsEntryService, Staging staging,
		StagingGroupHelper stagingGroupHelper,
		StyleBookEntryLocalService styleBookEntryLocalService,
		WorkflowDefinitionLinkLocalService workflowDefinitionLinkLocalService) {

		super(
			contentPageEditorSidebarPanels, contentManager,
			fragmentCollectionManager, fragmentEntryLinkManager,
			fragmentEntryLinkLocalService, frontendTokenDefinitionRegistry,
			httpServletRequest, infoItemServiceRegistry,
			infoSearchClassMapperRegistry, itemSelector, jsonFactory, language,
			layoutLocalService, layoutLockManager,
			layoutPageTemplateEntryLocalService, layoutPageTemplateEntryService,
			layoutPermission, layoutSetLocalService, pageEditorConfiguration,
			portal, portletRequest, portletResourcePermission,
			portletURLFactory, renderResponse, segmentsConfigurationProvider,
			segmentsExperienceManager, segmentsExperienceLocalService,
			segmentsExperimentRelLocalService, segmentsEntryService, staging,
			stagingGroupHelper, styleBookEntryLocalService,
			workflowDefinitionLinkLocalService);

		_groupLocalService = groupLocalService;
		_layoutPageTemplateStructureLocalService =
			layoutPageTemplateStructureLocalService;
		_layoutPageTemplateStructureRelLocalService =
			layoutPageTemplateStructureRelLocalService;
		_segmentsExperimentRelLocalService = segmentsExperimentRelLocalService;
	}

	@Override
	public Map<String, Object> getEditorContext() throws Exception {
		Map<String, Object> editorContext = super.getEditorContext();

		if (!_isShowSegmentsExperiences()) {
			return editorContext;
		}

		Map<String, Object> configContext =
			(Map<String, Object>)editorContext.get("config");

		configContext.put(
			"addSegmentsExperienceURL",
			HttpComponentsUtil.addParameter(
				HttpComponentsUtil.addParameter(
					getFragmentEntryActionURL(
						"/layout_content_page_editor/add_segments_experience"),
					getPortletNamespace() + "plid", themeDisplay.getPlid()),
				getPortletNamespace() + "groupId",
				themeDisplay.getScopeGroupId()));

		LearnMessage learnMessage = LearnMessageUtil.getLearnMessage(
			"content-page-personalization",
			language.getLanguageId(httpServletRequest),
			"layout-content-page-editor-web");

		configContext.put(
			"contentPagePersonalizationLearnURL", learnMessage.getURL());

		configContext.put(
			"defaultSegmentsEntryId", SegmentsEntryConstants.ID_DEFAULT);
		configContext.put(
			"deleteSegmentsExperienceURL",
			getFragmentEntryActionURL(
				"/layout_content_page_editor/delete_segments_experience"));
		configContext.put("editSegmentsEntryURL", _getEditSegmentsEntryURL());
		configContext.put("plid", themeDisplay.getPlid());
		configContext.put(
			"selectedSegmentsEntryId", String.valueOf(_getSegmentsEntryId()));
		configContext.put(
			"singleSegmentsExperienceMode", _isSingleSegmentsExperienceMode());

		Map<String, Object> stateContext =
			(Map<String, Object>)editorContext.get("state");

		stateContext.put(
			"availableSegmentsExperiences",
			SegmentsExperienceUtil.getAvailableSegmentsExperiences(
				httpServletRequest));
		stateContext.put("layoutDataList", _getLayoutDataList());

		SegmentsExperience segmentsExperience =
			segmentsExperienceLocalService.fetchSegmentsExperience(
				getSegmentsExperienceId());

		stateContext.put(
			"segmentsExperimentStatus",
			SegmentsExperienceUtil.getSegmentsExperimentStatus(
				themeDisplay, segmentsExperience.getSegmentsExperienceKey()));

		Map<String, Object> permissionsContext =
			(Map<String, Object>)stateContext.get("permissions");

		permissionsContext.put(
			ContentPageEditorActionKeys.EDIT_SEGMENTS_ENTRY,
			_hasEditSegmentsEntryPermission());
		permissionsContext.put(
			ContentPageEditorActionKeys.LOCKED_SEGMENTS_EXPERIMENT,
			_isLockedSegmentsExperience(getSegmentsExperienceId()));

		return editorContext;
	}

	@Override
	protected long getSegmentsExperienceId() {
		if (_segmentsExperienceId != null) {
			return _segmentsExperienceId;
		}

		_segmentsExperienceId = ParamUtil.getLong(
			portal.getOriginalServletRequest(httpServletRequest),
			"segmentsExperienceId", -1);

		if (_segmentsExperienceId != -1) {
			SegmentsExperience segmentsExperience =
				segmentsExperienceLocalService.fetchSegmentsExperience(
					_segmentsExperienceId);

			if (segmentsExperience != null) {
				_segmentsExperienceId =
					segmentsExperience.getSegmentsExperienceId();
			}
			else {
				_segmentsExperienceId = super.getSegmentsExperienceId();
			}
		}
		else {
			_segmentsExperienceId = super.getSegmentsExperienceId();
		}

		_segmentsExperienceId = getDraftSegmentsExperienceId(
			themeDisplay.getPlid(), _segmentsExperienceId);

		return _segmentsExperienceId;
	}

	private String _getEditSegmentsEntryURL() throws Exception {
		if (_editSegmentsEntryURL != null) {
			return _editSegmentsEntryURL;
		}

		PortletURL portletURL = PortletProviderUtil.getPortletURL(
			httpServletRequest, SegmentsEntry.class.getName(),
			PortletProvider.Action.EDIT);

		if (portletURL == null) {
			_editSegmentsEntryURL = StringPool.BLANK;
		}
		else {
			_editSegmentsEntryURL = layoutLockManager.getUnlockDraftLayoutURL(
				portal.getLiferayPortletResponse(renderResponse),
				() -> {
					Layout layout = themeDisplay.getLayout();

					portletURL.setParameter(
						"redirect", themeDisplay.getURLCurrent());
					portletURL.setParameter(
						"backURLTitle",
						layout.getName(themeDisplay.getLocale()));

					return portletURL.toString();
				});
		}

		return _editSegmentsEntryURL;
	}

	private List<Map<String, Object>> _getLayoutDataList() throws Exception {
		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					themeDisplay.getScopeGroupId(), themeDisplay.getPlid());

		return TransformUtil.transform(
			_layoutPageTemplateStructureRelLocalService.
				getLayoutPageTemplateStructureRels(
					layoutPageTemplateStructure.
						getLayoutPageTemplateStructureId()),
			layoutPageTemplateStructureRel ->
				HashMapBuilder.<String, Object>put(
					"layoutData",
					JSONFactoryUtil.createJSONObject(
						layoutPageTemplateStructureRel.getData())
				).put(
					"segmentsExperienceId",
					layoutPageTemplateStructureRel.getSegmentsExperienceId()
				).build());
	}

	private long _getSegmentsEntryId() {
		if (_segmentsEntryId != null) {
			return _segmentsEntryId;
		}

		_segmentsEntryId = ParamUtil.getLong(
			portal.getOriginalServletRequest(httpServletRequest),
			"segmentsEntryId");

		return _segmentsEntryId;
	}

	private boolean _hasEditSegmentsEntryPermission() throws Exception {
		return Validator.isNotNull(_getEditSegmentsEntryURL());
	}

	private Boolean _isLockedSegmentsExperience(long segmentsExperienceId)
		throws Exception {

		if (_lockedSegmentsExperience != null) {
			return _lockedSegmentsExperience;
		}

		SegmentsExperience segmentsExperience =
			segmentsExperienceLocalService.getSegmentsExperience(
				segmentsExperienceId);

		_lockedSegmentsExperience = segmentsExperience.hasSegmentsExperiment();

		return _lockedSegmentsExperience;
	}

	private boolean _isShowSegmentsExperiences() throws Exception {
		if (_showSegmentsExperiences != null) {
			return _showSegmentsExperiences;
		}

		Group group = _groupLocalService.getGroup(getGroupId());

		if (!group.isLayoutSetPrototype() && !group.isUser()) {
			_showSegmentsExperiences = true;
		}
		else {
			_showSegmentsExperiences = false;
		}

		return _showSegmentsExperiences;
	}

	private boolean _isSingleSegmentsExperienceMode() {
		long segmentsExperienceId = ParamUtil.getLong(
			portal.getOriginalServletRequest(httpServletRequest),
			"segmentsExperienceId", -1);

		if (segmentsExperienceId == -1) {
			return false;
		}

		SegmentsExperience segmentsExperience =
			segmentsExperienceLocalService.fetchSegmentsExperience(
				segmentsExperienceId);

		if (segmentsExperience != null) {
			List<SegmentsExperimentRel> segmentsExperimentRels =
				_segmentsExperimentRelLocalService.
					getSegmentsExperimentRelsBySegmentsExperienceKey(
						segmentsExperience.getSegmentsExperienceKey(),
						themeDisplay.getPlid());

			if (segmentsExperimentRels.isEmpty()) {
				return false;
			}

			SegmentsExperimentRel segmentsExperimentRel =
				segmentsExperimentRels.get(0);

			try {
				return !segmentsExperimentRel.isControl();
			}
			catch (PortalException portalException) {
				_log.error(portalException);
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ContentPageLayoutEditorDisplayContext.class);

	private String _editSegmentsEntryURL;
	private final GroupLocalService _groupLocalService;
	private final LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;
	private final LayoutPageTemplateStructureRelLocalService
		_layoutPageTemplateStructureRelLocalService;
	private Boolean _lockedSegmentsExperience;
	private Long _segmentsEntryId;
	private Long _segmentsExperienceId;
	private final SegmentsExperimentRelLocalService
		_segmentsExperimentRelLocalService;
	private Boolean _showSegmentsExperiences;

}