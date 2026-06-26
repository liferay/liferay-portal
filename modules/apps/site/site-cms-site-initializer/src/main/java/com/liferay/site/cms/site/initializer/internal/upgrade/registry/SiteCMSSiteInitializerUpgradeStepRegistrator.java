/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.upgrade.registry;

import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.rest.filter.factory.FilterFactory;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.service.persistence.ObjectDefinitionPersistence;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.site.cms.site.initializer.internal.upgrade.v1_0_0.CMSDefaultPermissionsUpgradeProcess;
import com.liferay.site.cms.site.initializer.internal.upgrade.v1_0_0.CMSObjectRelationshipEdgeUpgradeProcess;
import com.liferay.site.cms.site.initializer.internal.upgrade.v2_0_0.CMSBulkActionTaskTaskResultUpgradeProcess;

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
		registry.register(
			"0.0.0", "1.0.0",
			new CMSDefaultPermissionsUpgradeProcess(
				_filterFactory, _groupLocalService,
				_objectDefinitionLocalService, _objectEntryFolderLocalService));

		registry.register(
			"1.0.0", "2.0.0",
			new CMSObjectRelationshipEdgeUpgradeProcess(
				_companyLocalService, _objectDefinitionLocalService,
				_objectDefinitionPersistence,
				_objectDefinitionSettingLocalService, _objectEntryLocalService,
				_objectFolderLocalService, _objectRelationshipLocalService));

		registry.register(
			"2.0.0", "3.0.0",
			new CMSBulkActionTaskTaskResultUpgradeProcess(
				_companyLocalService, _objectDefinitionLocalService,
				_objectFieldLocalService));
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference(
		target = "(filter.factory.key=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private FilterFactory<Predicate> _filterFactory;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectDefinitionPersistence _objectDefinitionPersistence;

	@Reference
	private ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;

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

}