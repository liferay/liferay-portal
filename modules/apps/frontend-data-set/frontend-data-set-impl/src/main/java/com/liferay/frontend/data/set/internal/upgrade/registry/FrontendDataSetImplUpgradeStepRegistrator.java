/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.internal.upgrade.registry;

import com.liferay.frontend.data.set.internal.upgrade.v1_0_0.DataSetOrderValuesUpgradeProcess;
import com.liferay.frontend.data.set.internal.upgrade.v1_1_0.DataSetShowSearchUpgradeProcess;
import com.liferay.frontend.data.set.internal.upgrade.v1_2_0.DataSetFragmentEntryLinkClassNameUpgradeProcess;
import com.liferay.object.action.engine.ObjectActionEngine;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Daniel Sanz
 */
@Component(service = UpgradeStepRegistrator.class)
public class FrontendDataSetImplUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.registerInitialization();

		registry.register(
			"0.0.1", "1.0.0",
			new DataSetOrderValuesUpgradeProcess(
				_companyLocalService, _objectActionEngine,
				_objectDefinitionLocalService, _objectEntryLocalService));

		registry.register(
			"1.0.0", "1.1.0",
			new DataSetShowSearchUpgradeProcess(
				_companyLocalService, _objectDefinitionLocalService,
				_objectEntryLocalService));

		registry.register(
			"1.1.0", "1.2.0",
			new DataSetFragmentEntryLinkClassNameUpgradeProcess(
				_objectDefinitionLocalService));
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private ObjectActionEngine _objectActionEngine;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

}