/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.storage.internal.upgrade.registry;

import com.liferay.portal.kernel.service.ReleaseLocalService;
import com.liferay.portal.kernel.upgrade.BaseSQLServerDatetimeUpgradeProcess;
import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.security.audit.storage.internal.upgrade.v1_0_1.util.AuditEventTable;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.release.ReleaseRenamingUpgradeStep;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Samuel Ziemer
 */
@Component(service = UpgradeStepRegistrator.class)
public class AuditStorageServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.registerReleaseCreationUpgradeSteps(
			new ReleaseRenamingUpgradeStep(
				"com.liferay.portal.security.audit.storage.service",
				"audit-portlet", _releaseLocalService));

		registry.register("0.0.1", "1.0.0", new DummyUpgradeStep());

		registry.register(
			"1.0.0", "1.0.1",
			UpgradeProcessFactory.alterColumnType(
				"Audit_AuditEvent", "clientIP", "VARCHAR(255) null"));

		registry.register(
			"1.0.1", "2.0.0",
			new BaseSQLServerDatetimeUpgradeProcess(
				new Class<?>[] {AuditEventTable.class}));

		registry.register("2.0.0", "2.0.1", new DummyUpgradeStep());

		registry.register(
			"2.0.1", "2.1.0",
			UpgradeProcessFactory.addColumns(
				"Audit_AuditEvent", "groupId LONG"));

		registry.register(
			"2.1.0", "2.2.0",
			UpgradeProcessFactory.addColumns(
				"Audit_AuditEvent", "accountEntryId LONG",
				"contextName VARCHAR(75) null"));

		registry.register(
			"2.2.0", "2.3.0",
			UpgradeProcessFactory.addColumns(
				"Audit_AuditEvent", "correlationId VARCHAR(75) null",
				"httpMethod VARCHAR(75) null", "impersonated BOOLEAN",
				"impersonatedUserEmailAddress VARCHAR(75) null",
				"impersonatedUserId LONG",
				"impersonatedUserName VARCHAR(75) null",
				"objectName VARCHAR(75) null", "requestId VARCHAR(75) null",
				"requestIdGenerated BOOLEAN", "resourceAction VARCHAR(75) null",
				"resourceType VARCHAR(75) null", "roles TEXT null",
				"userAgent VARCHAR(255) null",
				"userEmailAddress VARCHAR(75) null"));
	}

	@Reference
	private ReleaseLocalService _releaseLocalService;

}