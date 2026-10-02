/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.upgrade.registry;

import com.liferay.fragment.contributor.FragmentCollectionContributor;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.site.cms.site.initializer.internal.upgrade.v1_0_0.CMSDefaultPermissionsUpgradeProcess;
import com.liferay.site.cms.site.initializer.internal.upgrade.v1_0_0.CMSObjectRelationshipEdgeUpgradeProcess;
import com.liferay.site.cms.site.initializer.internal.upgrade.v2_0_0.CMSBulkActionTaskTaskResultUpgradeProcess;
import com.liferay.site.cms.site.initializer.internal.upgrade.v3_0_1.CMSObjectFolderPermissionsUpgradeProcess;
import com.liferay.site.cms.site.initializer.internal.upgrade.v3_0_2.CMSAdministratorRoleUpgradeProcess;
import com.liferay.site.cms.site.initializer.internal.upgrade.v3_0_3.CMSFileTypeDisplayPageUpgradeProcess;
import com.liferay.site.cms.site.initializer.internal.upgrade.v3_0_4.CMSFileTypeFriendlyURLUpgradeProcess;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 * @author Víctor Galán
 */
@Component(service = UpgradeStepRegistrator.class)
public class SiteCMSSiteInitializerUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.registerInitialization();

		registry.register(
			"0.0.1", "1.0.0",
			new CMSDefaultPermissionsUpgradeProcess(
				_filterFactory, _groupLocalService,
				_objectDefinitionLocalService, _objectEntryFolderLocalService));

		registry.register(
			"1.0.0", "2.0.0",
			new CMSObjectRelationshipEdgeUpgradeProcess(
				_companyLocalService, _objectDefinitionLocalService,
				_objectFolderLocalService, _objectRelationshipLocalService));

		registry.register(
			"2.0.0", "3.0.0",
			new CMSBulkActionTaskTaskResultUpgradeProcess(
				_companyLocalService, _objectDefinitionLocalService,
				_objectFieldLocalService));

		registry.register(
			"3.0.0", "3.0.1",
			new CMSObjectFolderPermissionsUpgradeProcess(
				_companyLocalService, _objectFolderLocalService,
				_resourceActionLocalService, _resourcePermissionLocalService,
				_roleLocalService));

		registry.register(
			"3.0.1", "3.0.2",
			new CMSAdministratorRoleUpgradeProcess(
				_companyLocalService, _roleLocalService));

		registry.register(
			"3.0.2", "3.0.3",
			new CMSFileTypeDisplayPageUpgradeProcess(
				_basicComponentFragmentCollectionContributor,
				_classNameLocalService, _companyLocalService,
				_fragmentEntryLinkLocalService, _groupLocalService,
				_layoutLocalService, _layoutPageTemplateEntryLocalService,
				_layoutPageTemplateStructureLocalService,
				_objectDefinitionLocalService, _objectFolderLocalService,
				_segmentsExperienceLocalService));

		registry.register(
			"3.0.3", "3.0.4",
			new CMSFileTypeFriendlyURLUpgradeProcess(
				_companyLocalService, _objectDefinitionLocalService,
				_objectEntryLocalService, _objectFolderLocalService));
	}

	@Reference(target = "(fragment.collection.key=BASIC_COMPONENT)")
	private FragmentCollectionContributor
		_basicComponentFragmentCollectionContributor;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private LayoutPageTemplateStructureLocalService
		_layoutPageTemplateStructureLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectFieldLocalService _objectFieldLocalService;

	@Reference
	private ObjectFolderLocalService _objectFolderLocalService;

	@Reference
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}