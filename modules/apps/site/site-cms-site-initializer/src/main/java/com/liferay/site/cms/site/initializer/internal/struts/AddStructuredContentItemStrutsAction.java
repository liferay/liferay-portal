/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.struts;

import com.liferay.fragment.listener.FragmentEntryLinkListener;
import com.liferay.fragment.listener.FragmentEntryLinkListenerRegistry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.layout.constants.LayoutTypeSettingsConstants;
import com.liferay.layout.manager.FormManager;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.util.structure.ContainerStyledLayoutStructureItem;
import com.liferay.layout.util.structure.FormStyledLayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.dto.v1_0.Status;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.site.cms.site.initializer.internal.util.ActionUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = "path=/cms/add_structured_content_item",
	service = StrutsAction.class
)
public class AddStructuredContentItemStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		long objectDefinitionId = ParamUtil.getLong(
			httpServletRequest, "objectDefinitionId");

		ObjectDefinition objectDefinition =
			_objectDefinitionService.getObjectDefinition(objectDefinitionId);

		if (!Objects.equals(
				objectDefinition.getScope(),
				ObjectDefinitionConstants.SCOPE_DEPOT)) {

			return null;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Group group = _groupLocalService.getGroup(
			themeDisplay.getCompanyId(), GroupConstants.CMS);

		long classNameId = _portal.getClassNameId(
			objectDefinition.getClassName());

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchDefaultLayoutPageTemplateEntry(
					group.getGroupId(), classNameId, 0);

		if (layoutPageTemplateEntry == null) {
			layoutPageTemplateEntry = _addDefaultLayoutPageTemplateEntry(
				classNameId, group.getGroupId(), objectDefinition.getName(),
				ParamUtil.getLong(httpServletRequest, "plid"),
				ServiceContextFactory.getInstance(httpServletRequest));
		}

		Layout layout = _layoutLocalService.fetchLayout(
			layoutPageTemplateEntry.getPlid());

		ObjectEntryManager objectEntryManager =
			_objectEntryManagerRegistry.getObjectEntryManager(
				objectDefinition.getStorageType());

		ObjectEntry objectEntry = new ObjectEntry();

		objectEntry.setObjectEntryFolderExternalReferenceCode(
			() -> {
				if (Objects.equals(
						objectDefinition.getObjectFolderExternalReferenceCode(),
						ObjectFolderConstants.
							EXTERNAL_REFERENCE_CODE_FILE_TYPES)) {

					return ObjectEntryFolderConstants.
						EXTERNAL_REFERENCE_CODE_FILES;
				}

				return ObjectEntryFolderConstants.
					EXTERNAL_REFERENCE_CODE_CONTENTS;
			});
		objectEntry.setStatus(
			() -> new Status() {
				{
					setCode(() -> WorkflowConstants.STATUS_DRAFT);
				}
			});

		objectEntry = objectEntryManager.addObjectEntry(
			new DefaultDTOConverterContext(
				false, null, null, null, null,
				themeDisplay.getSiteDefaultLocale(), null,
				themeDisplay.getUser()),
			objectDefinition, objectEntry,
			String.valueOf(ParamUtil.getLong(httpServletRequest, "groupId")));

		httpServletResponse.sendRedirect(
			ActionUtil.getEditURL(
				String.valueOf(classNameId),
				String.valueOf(objectEntry.getId()), layout, themeDisplay));

		return null;
	}

	private LayoutPageTemplateEntry _addDefaultLayoutPageTemplateEntry(
			long classNameId, long groupId, String name, long plid,
			ServiceContext serviceContext)
		throws Exception {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				null, serviceContext.getUserId(), groupId, 0, null, classNameId,
				0, name, LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE, 0,
				true, 0, 0, 0, WorkflowConstants.STATUS_APPROVED,
				serviceContext);

		Layout layout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		Layout draftLayout = layout.fetchDraftLayout();

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					draftLayout.getGroupId(), draftLayout.getPlid());

		if (layoutPageTemplateStructure == null) {
			return layoutPageTemplateEntry;
		}

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				draftLayout.getPlid());

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getData(segmentsExperienceId));

		ContainerStyledLayoutStructureItem
			parentContainerStyledLayoutStructureItem =
				(ContainerStyledLayoutStructureItem)
					layoutStructure.addContainerStyledLayoutStructureItem(
						layoutStructure.getMainItemId(), 0);

		parentContainerStyledLayoutStructureItem.updateItemConfig(
			JSONUtil.put(
				"styles",
				JSONUtil.put(
					"paddingBottom", "6"
				).put(
					"paddingTop", "6"
				)));

		ContainerStyledLayoutStructureItem
			childContainerStyledLayoutStructureItem =
				(ContainerStyledLayoutStructureItem)
					layoutStructure.addContainerStyledLayoutStructureItem(
						parentContainerStyledLayoutStructureItem.getItemId(),
						0);

		childContainerStyledLayoutStructureItem.setWidthType("fixed");

		FormStyledLayoutStructureItem formStyledLayoutStructureItem =
			(FormStyledLayoutStructureItem)
				layoutStructure.addFormStyledLayoutStructureItem(
					childContainerStyledLayoutStructureItem.getItemId(), 0);

		formStyledLayoutStructureItem.setClassNameId(
			layoutPageTemplateEntry.getClassNameId());

		Layout backLayout = _layoutLocalService.fetchLayout(plid);

		if (backLayout != null) {
			formStyledLayoutStructureItem.setSuccessMessageJSONObject(
				JSONUtil.put(
					"layout",
					JSONUtil.put(
						"groupId", backLayout.getGroupId()
					).put(
						"layoutId", backLayout.getLayoutId()
					).put(
						"layoutUuid", backLayout.getUuid()
					).put(
						"private", backLayout.isPrivateLayout()
					).put(
						"title", backLayout.getTitle()
					)
				).put(
					"showNotification", true
				).put(
					"type", "page"
				));
		}

		List<FragmentEntryLink> addedFragmentEntryLinks = new ArrayList<>();

		_formManager.addFragmentEntryLinksLayoutStructureItems(
			addedFragmentEntryLinks, _jsonFactory.createJSONObject(),
			formStyledLayoutStructureItem, true, draftLayout, layoutStructure,
			LocaleUtil.getMostRelevantLocale(), segmentsExperienceId,
			serviceContext, null);

		_layoutPageTemplateStructureLocalService.
			updateLayoutPageTemplateStructureData(
				draftLayout.getGroupId(), draftLayout.getPlid(),
				segmentsExperienceId, layoutStructure.toString());

		for (FragmentEntryLink addedFragmentEntryLink :
				addedFragmentEntryLinks) {

			for (FragmentEntryLinkListener fragmentEntryLinkListener :
					_fragmentEntryLinkListenerRegistry.
						getFragmentEntryLinkListeners()) {

				fragmentEntryLinkListener.onAddFragmentEntryLink(
					addedFragmentEntryLink);
			}
		}

		_layoutLocalService.copyLayoutContent(draftLayout, layout);

		draftLayout = _layoutLocalService.getLayout(draftLayout.getPlid());

		draftLayout.setStatus(WorkflowConstants.STATUS_APPROVED);

		draftLayout = _layoutLocalService.updateLayout(draftLayout);

		UnicodeProperties typeSettingsUnicodeProperties =
			draftLayout.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.put(
			LayoutTypeSettingsConstants.KEY_PUBLISHED, Boolean.TRUE.toString());

		_layoutLocalService.updateLayout(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			typeSettingsUnicodeProperties.toString());

		return layoutPageTemplateEntry;
	}

	@Reference
	private FormManager _formManager;

	@Reference
	private FragmentEntryLinkListenerRegistry
		_fragmentEntryLinkListenerRegistry;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Reference
	private ObjectDefinitionService _objectDefinitionService;

	@Reference
	private ObjectEntryManagerRegistry _objectEntryManagerRegistry;

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}