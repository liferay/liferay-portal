/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.upgrade.registry;

import com.liferay.knowledge.base.web.internal.upgrade.v1_0_0.UpgradePortletId;
import com.liferay.knowledge.base.web.internal.upgrade.v1_0_0.UpgradePortletSettings;
import com.liferay.knowledge.base.web.internal.upgrade.v1_1_0.UpgradePortletPreferences;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 * @author Roberto Díaz
 */
@Component(service = UpgradeStepRegistrator.class)
public class KnowledgeBaseWebUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.registerInitialization();

		registry.register("0.0.1", "0.0.2", new UpgradePortletId());

		registry.register("0.0.2", "1.0.0", new UpgradePortletSettings());

		registry.register("1.0.0", "1.1.0", new UpgradePortletPreferences());

		registry.register(
			"1.1.0", "1.2.0",
			new com.liferay.knowledge.base.web.internal.upgrade.v1_2_0.
				UpgradePortletPreferences());
	}

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

}