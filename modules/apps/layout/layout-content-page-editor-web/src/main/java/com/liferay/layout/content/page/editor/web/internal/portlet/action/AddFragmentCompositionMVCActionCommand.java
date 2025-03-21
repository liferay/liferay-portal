/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.portlet.action;

import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentComposition;
import com.liferay.fragment.service.FragmentCollectionService;
import com.liferay.fragment.service.FragmentCompositionService;
import com.liferay.layout.content.page.editor.constants.ContentPageEditorPortletKeys;
import com.liferay.layout.content.page.editor.web.internal.constants.ContentPageEditorConstants;
import com.liferay.layout.page.template.serializer.LayoutStructureItemJSONSerializer;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentPageEditorPortletKeys.CONTENT_PAGE_EDITOR_PORTLET,
		"mvc.command.name=/layout_content_page_editor/add_fragment_composition"
	},
	service = MVCActionCommand.class
)
public class AddFragmentCompositionMVCActionCommand
	extends BaseContentPageEditorTransactionalMVCActionCommand {

	@Override
	protected JSONObject doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long fragmentCollectionId = ParamUtil.getLong(
			actionRequest, "fragmentCollectionId");

		FragmentCollection fragmentCollection =
			_fragmentCollectionService.fetchFragmentCollection(
				fragmentCollectionId);

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			actionRequest);

		if (fragmentCollection == null) {
			String fragmentCollectionName = _language.get(
				themeDisplay.getRequest(), "saved-fragments");

			fragmentCollection =
				_fragmentCollectionService.addFragmentCollection(
					null, themeDisplay.getScopeGroupId(),
					fragmentCollectionName, fragmentCollectionName,
					serviceContext);
		}

		String name = ParamUtil.getString(actionRequest, "name");
		String description = ParamUtil.getString(actionRequest, "description");

		String itemId = ParamUtil.getString(actionRequest, "itemId");
		boolean saveInlineContent = ParamUtil.getBoolean(
			actionRequest, "saveInlineContent");
		boolean saveMappingConfiguration = ParamUtil.getBoolean(
			actionRequest, "saveMappingConfiguration");
		long segmentsExperienceId = ParamUtil.getLong(
			actionRequest, "segmentsExperienceId");

		String layoutStructureItemJSON =
			_layoutStructureItemJSONSerializer.toJSONString(
				_layoutLocalService.getLayout(themeDisplay.getPlid()), itemId,
				saveInlineContent, saveMappingConfiguration,
				segmentsExperienceId);

		FragmentComposition fragmentComposition =
			_fragmentCompositionService.addFragmentComposition(
				null, themeDisplay.getScopeGroupId(),
				fragmentCollection.getFragmentCollectionId(), null, name,
				description, layoutStructureItemJSON, 0,
				WorkflowConstants.STATUS_APPROVED, serviceContext);

		long fileEntryId = ParamUtil.getLong(actionRequest, "fileEntryId");

		if (fileEntryId > 0) {
			FileEntry previewFileEntry = _addPreviewImage(
				fileEntryId, fragmentComposition.getFragmentCompositionId(),
				serviceContext, themeDisplay);

			if (previewFileEntry != null) {
				fragmentComposition =
					_fragmentCompositionService.updateFragmentComposition(
						fragmentComposition.getFragmentCompositionId(),
						previewFileEntry.getFileEntryId());
			}
		}

		return JSONUtil.put(
			"fragmentComposition",
			JSONUtil.put(
				"fragmentCollectionId",
				String.valueOf(fragmentCollection.getFragmentCollectionId())
			).put(
				"fragmentCollectionName", fragmentCollection.getName()
			).put(
				"fragmentEntryKey",
				fragmentComposition.getFragmentCompositionKey()
			).put(
				"groupId", fragmentComposition.getGroupId()
			).put(
				"icon", "edit-layout"
			).put(
				"imagePreviewURL",
				fragmentComposition.getImagePreviewURL(themeDisplay)
			).put(
				"name", fragmentComposition.getName()
			).put(
				"type", ContentPageEditorConstants.TYPE_COMPOSITION
			)
		).put(
			"url",
			PortletURLBuilder.create(
				_portal.getControlPanelPortletURL(
					_portal.getHttpServletRequest(actionRequest),
					themeDisplay.getScopeGroup(), FragmentPortletKeys.FRAGMENT,
					0, 0, PortletRequest.RENDER_PHASE)
			).setParameter(
				"fragmentCollectionId", fragmentCollectionId
			).buildString()
		);
	}

	private FileEntry _addPreviewImage(
			long fileEntryId, long fragmentCompositionId,
			ServiceContext serviceContext, ThemeDisplay themeDisplay)
		throws Exception {

		FileEntry fileEntry = _dlAppLocalService.getFileEntry(fileEntryId);

		Repository repository =
			PortletFileRepositoryUtil.fetchPortletRepository(
				themeDisplay.getScopeGroupId(), FragmentPortletKeys.FRAGMENT);

		if (repository == null) {
			serviceContext.setAddGroupPermissions(true);
			serviceContext.setAddGuestPermissions(true);

			repository = PortletFileRepositoryUtil.addPortletRepository(
				themeDisplay.getScopeGroupId(), FragmentPortletKeys.FRAGMENT,
				serviceContext);
		}

		return _portletFileRepository.addPortletFileEntry(
			null, themeDisplay.getScopeGroupId(), themeDisplay.getUserId(),
			FragmentComposition.class.getName(), fragmentCompositionId,
			FragmentPortletKeys.FRAGMENT, repository.getDlFolderId(),
			fileEntry.getContentStream(),
			fragmentCompositionId + "_preview." + fileEntry.getExtension(),
			fileEntry.getMimeType(), false);
	}

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private FragmentCollectionService _fragmentCollectionService;

	@Reference
	private FragmentCompositionService _fragmentCompositionService;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutStructureItemJSONSerializer
		_layoutStructureItemJSONSerializer;

	@Reference
	private Portal _portal;

	@Reference
	private PortletFileRepository _portletFileRepository;

}