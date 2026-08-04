/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.utility.page.internal.upgrade.registry;

import com.liferay.layout.utility.page.internal.upgrade.v1_4_0.LayoutUtilityPageEntryUpgradeProcess;
import com.liferay.layout.utility.page.internal.upgrade.v1_4_1.LayoutUtilityPageEntryLayoutFriendlyURLUpgradeProcess;
import com.liferay.layout.utility.page.internal.upgrade.v1_4_2.LayoutUtilityPageEntryLayoutExternalReferenceCodeUpgradeProcess;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = UpgradeStepRegistrator.class)
public class LayoutUtilityPageEntryUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register("0.0.1", "1.0.0", new DummyUpgradeStep());

		registry.register(
			"1.0.0", "1.1.0",
			UpgradeProcessFactory.addColumns(
				"LayoutUtilityPageEntry", "previewFileEntryId LONG"));

		registry.register(
			"1.1.0", "1.2.0",
			UpgradeProcessFactory.alterColumnType(
				"LayoutUtilityPageEntry", "type_", "VARCHAR(75) null"));

		registry.register(
			"1.2.0", "1.3.0",
			UpgradeProcessFactory.alterColumnType(
				"LayoutUtilityPageEntry", "type_", "VARCHAR(75) null"));

		registry.register(
			"1.3.0", "1.4.0",
			new LayoutUtilityPageEntryUpgradeProcess(_layoutLocalService));

		registry.register(
			"1.4.0", "1.4.1",
			new LayoutUtilityPageEntryLayoutFriendlyURLUpgradeProcess(
				_layoutLocalService));

		registry.register(
			"1.4.1", "1.4.2",
			new LayoutUtilityPageEntryLayoutExternalReferenceCodeUpgradeProcess());
	}

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.segments.service)(release.schema.version>=3.1.0))"
	)
	private Release _release;

}