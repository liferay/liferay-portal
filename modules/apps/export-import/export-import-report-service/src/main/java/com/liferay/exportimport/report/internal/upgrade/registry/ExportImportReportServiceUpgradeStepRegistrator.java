/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.report.internal.upgrade.registry;

import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import org.osgi.service.component.annotations.Component;

/**
 * @author Petteri Karttunen
 */
@Component(service = UpgradeStepRegistrator.class)
public class ExportImportReportServiceUpgradeStepRegistrator
	implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register(
			"1.0.0", "2.0.0",
			UpgradeProcessFactory.addColumns(
				"ExportImportReportEntry", "classPK LONG",
				"modelName VARCHAR(75) null", "origin INTEGER",
				"scope VARCHAR(75) null", "scopeKey VARCHAR(75) null",
				"status INTEGER"),
			UpgradeProcessFactory.dropColumns(
				"ExportImportReportEntry", "resolved"));

		registry.register(
			"2.0.0", "2.1.0",
			UpgradeProcessFactory.alterColumnName(
				"ExportImportReportEntry", "error", "errorMessage TEXT null"),
			UpgradeProcessFactory.alterColumnType(
				"ExportImportReportEntry", "modelName", "VARCHAR(255) null"));
	}

}