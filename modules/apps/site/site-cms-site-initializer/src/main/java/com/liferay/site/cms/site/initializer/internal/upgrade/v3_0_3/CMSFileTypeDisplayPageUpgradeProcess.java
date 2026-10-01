/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.upgrade.v3_0_3;

import com.liferay.fragment.contributor.FragmentCollectionContributor;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.site.cms.site.initializer.internal.util.ActionUtil;
import com.liferay.site.cms.site.initializer.internal.util.CMSFileTypeUtil;

import java.util.List;
import java.util.Objects;

/**
 * @author Mikel Lorza
 */
public class CMSFileTypeDisplayPageUpgradeProcess extends UpgradeProcess {

	public CMSFileTypeDisplayPageUpgradeProcess(
		FragmentCollectionContributor
			basicComponentFragmentCollectionContributor,
		ClassNameLocalService classNameLocalService,
		CompanyLocalService companyLocalService,
		FragmentEntryLinkLocalService fragmentEntryLinkLocalService,
		GroupLocalService groupLocalService,
		LayoutLocalService layoutLocalService,
		LayoutPageTemplateEntryLocalService layoutPageTemplateEntryLocalService,
		LayoutPageTemplateStructureLocalService
			layoutPageTemplateStructureLocalService,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectFolderLocalService objectFolderLocalService,
		SegmentsExperienceLocalService segmentsExperienceLocalService) {

		_basicComponentFragmentCollectionContributor =
			basicComponentFragmentCollectionContributor;
		_classNameLocalService = classNameLocalService;
		_companyLocalService = companyLocalService;
		_fragmentEntryLinkLocalService = fragmentEntryLinkLocalService;
		_groupLocalService = groupLocalService;
		_layoutLocalService = layoutLocalService;
		_layoutPageTemplateEntryLocalService =
			layoutPageTemplateEntryLocalService;
		_layoutPageTemplateStructureLocalService =
			layoutPageTemplateStructureLocalService;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectFolderLocalService = objectFolderLocalService;
		_segmentsExperienceLocalService = segmentsExperienceLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_companyLocalService.forEachCompanyId(this::_upgradeCompany);
	}

	private FragmentEntryLink _addFriendlyURLHelpFragmentEntryLink(
			Layout layout, long segmentsExperienceId)
		throws PortalException {

		FragmentEntry fragmentEntry = null;

		for (FragmentEntry basicComponentFragmentEntry :
				_basicComponentFragmentCollectionContributor.
					getFragmentEntries()) {

			if (Objects.equals(
					basicComponentFragmentEntry.getFragmentEntryKey(),
					"BASIC_COMPONENT-paragraph")) {

				fragmentEntry = basicComponentFragmentEntry;

				break;
			}
		}

		if (fragmentEntry == null) {
			return null;
		}

		return _fragmentEntryLinkLocalService.addFragmentEntryLink(
			null, layout.getUserId(), layout.getGroupId(), null,
			fragmentEntry.getExternalReferenceCode(), null,
			segmentsExperienceId, layout.getPlid(), fragmentEntry.getCss(),
			fragmentEntry.getHtml(), fragmentEntry.getJs(),
			fragmentEntry.getConfiguration(),
			ActionUtil.getFriendlyURLHelpEditableValues(layout.getCompanyId()),
			StringPool.BLANK, 0, fragmentEntry.getFragmentEntryKey(),
			fragmentEntry.getType(), new ServiceContext());
	}

	private void _upgradeCompany(long companyId) throws PortalException {
		Group group = _groupLocalService.fetchGroup(
			companyId, GroupConstants.CMS);

		if (group == null) {
			return;
		}

		ObjectFolder objectFolder =
			_objectFolderLocalService.fetchObjectFolderByExternalReferenceCode(
				ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES,
				companyId);

		if (objectFolder == null) {
			return;
		}

		for (ObjectDefinition objectDefinition :
				_objectDefinitionLocalService.getObjectFolderObjectDefinitions(
					objectFolder.getObjectFolderId())) {

			if (CMSFileTypeUtil.hasFileObjectField(objectDefinition)) {
				_upgradeObjectDefinition(group, objectDefinition);
			}
		}
	}

	private void _upgradeLayout(Layout layout) throws PortalException {
		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_layoutPageTemplateStructureLocalService.
				fetchLayoutPageTemplateStructure(
					layout.getGroupId(), layout.getPlid());

		if (layoutPageTemplateStructure == null) {
			return;
		}

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid());

		LayoutStructure layoutStructure = LayoutStructure.of(
			layoutPageTemplateStructure.getData(segmentsExperienceId));

		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.getFragmentEntryLinksByPlid(
				layout.getGroupId(), layout.getPlid());

		for (FragmentEntryLink fragmentEntryLink : fragmentEntryLinks) {
			if (!Objects.equals(
					fragmentEntryLink.getRendererKey(),
					"INPUTS-friendly-url-input")) {

				continue;
			}

			LayoutStructureItem layoutStructureItem =
				layoutStructure.getLayoutStructureItemByFragmentEntryLinkId(
					fragmentEntryLink.getFragmentEntryLinkId());

			if ((layoutStructureItem == null) ||
				layoutStructure.isItemMarkedForDeletion(
					layoutStructureItem.getItemId())) {

				continue;
			}

			FragmentEntryLink friendlyURLHelpFragmentEntryLink =
				_addFriendlyURLHelpFragmentEntryLink(
					layout, segmentsExperienceId);

			if (friendlyURLHelpFragmentEntryLink == null) {
				return;
			}

			LayoutStructureItem parentLayoutStructureItem =
				layoutStructure.getLayoutStructureItem(
					layoutStructureItem.getParentItemId());

			List<String> childrenItemIds =
				parentLayoutStructureItem.getChildrenItemIds();

			ActionUtil.addFriendlyURLHelpLayoutStructureItem(
				friendlyURLHelpFragmentEntryLink.getFragmentEntryLinkId(),
				layoutStructure, parentLayoutStructureItem.getItemId(),
				childrenItemIds.indexOf(layoutStructureItem.getItemId()) + 1);

			_layoutPageTemplateStructureLocalService.
				updateLayoutPageTemplateStructureData(
					layout.getUserId(), layout.getGroupId(), layout.getPlid(),
					segmentsExperienceId, layoutStructure.toString());

			return;
		}
	}

	private void _upgradeObjectDefinition(
			Group group, ObjectDefinition objectDefinition)
		throws PortalException {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchDefaultLayoutPageTemplateEntry(
					group.getGroupId(),
					_classNameLocalService.getClassNameId(
						objectDefinition.getClassName()),
					0);

		if (layoutPageTemplateEntry == null) {
			return;
		}

		Layout layout = _layoutLocalService.fetchLayout(
			layoutPageTemplateEntry.getPlid());

		if (layout == null) {
			return;
		}

		_upgradeLayout(layout);

		Layout draftLayout = layout.fetchDraftLayout();

		if (draftLayout != null) {
			_upgradeLayout(draftLayout);
		}
	}

	private final FragmentCollectionContributor
		_basicComponentFragmentCollectionContributor;
	private final ClassNameLocalService _classNameLocalService;
	private final CompanyLocalService _companyLocalService;
	private final FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;
	private final GroupLocalService _groupLocalService;
	private final LayoutLocalService _layoutLocalService;
	private final LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;
	private final LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectFolderLocalService _objectFolderLocalService;
	private final SegmentsExperienceLocalService
		_segmentsExperienceLocalService;

}