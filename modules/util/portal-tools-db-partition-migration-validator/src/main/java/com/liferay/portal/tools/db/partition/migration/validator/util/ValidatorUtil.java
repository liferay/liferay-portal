/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.db.partition.migration.validator.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.tools.db.partition.migration.validator.Company;
import com.liferay.portal.tools.db.partition.migration.validator.LiferayDatabase;
import com.liferay.portal.tools.db.partition.migration.validator.Recorder;
import com.liferay.portal.tools.db.partition.migration.validator.Release;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Luis Ortiz
 */
public class ValidatorUtil {

	public static Recorder validateDatabases(
		LiferayDatabase sourceLiferayDatabase,
		LiferayDatabase targetLiferayDatabase) {

		Recorder recorder = new Recorder();

		_validateCompany(
			recorder, sourceLiferayDatabase, targetLiferayDatabase);
		_validatePartitionedTables(
			recorder, sourceLiferayDatabase, targetLiferayDatabase);
		_validateRelease(
			recorder, sourceLiferayDatabase, targetLiferayDatabase);

		return recorder;
	}

	private static List<String> _getFailedServletContextNames(
		LiferayDatabase liferayDatabase) {

		List<String> failedServletContextNames = new ArrayList<>();

		for (Release release : liferayDatabase.getReleases()) {
			if (release.getState() != 0) {
				failedServletContextNames.add(release.getServletContextName());
			}
		}

		return failedServletContextNames;
	}

	private static Map<String, Release> _getReleases(
		LiferayDatabase liferayDatabase) {

		Map<String, Release> releases = new HashMap<>();

		for (Release release : liferayDatabase.getReleases()) {
			releases.put(release.getServletContextName(), release);
		}

		return releases;
	}

	private static void _validateCompany(
		Company company, Recorder recorder, Company sourceCompany) {

		if (Objects.equals(
				company.getCompanyId(), sourceCompany.getCompanyId())) {

			recorder.registerError(
				StringBundler.concat(
					"Company ID ", sourceCompany.getCompanyId(),
					" already exists in the target database"));
		}

		if (Objects.equals(
				company.getCompanyName(), sourceCompany.getCompanyName())) {

			recorder.registerWarning(
				StringBundler.concat(
					"Company name ", sourceCompany.getCompanyName(),
					" already exists in the target database. You must set a ",
					"different value in the portal instance import request."));
		}

		if (Objects.equals(
				company.getVirtualHostname(),
				sourceCompany.getVirtualHostname())) {

			recorder.registerWarning(
				StringBundler.concat(
					"Virtual host ", sourceCompany.getVirtualHostname(),
					" already exists in the target database. You must set a ",
					"different value in the portal instance import request."));
		}

		if (Objects.equals(company.getWebId(), sourceCompany.getWebId())) {
			recorder.registerWarning(
				StringBundler.concat(
					"Web ID ", sourceCompany.getWebId(),
					" already exists in the target database. You must set a ",
					"different value in the portal instance import request."));
		}
	}

	private static void _validateCompany(
		Recorder recorder, LiferayDatabase sourceLiferayDatabase,
		LiferayDatabase targetLiferayDatabase) {

		Company sourceCompany = null;

		for (Company company : sourceLiferayDatabase.getCompanies()) {
			if (Objects.equals(
					company.getCompanyId(),
					sourceLiferayDatabase.getExportedCompanyId())) {

				sourceCompany = company;

				break;
			}
		}

		for (Company company : targetLiferayDatabase.getCompanies()) {
			_validateCompany(company, recorder, sourceCompany);
		}
	}

	private static void _validatePartitionedTables(
		Recorder recorder, LiferayDatabase sourceLiferayDatabase,
		LiferayDatabase targetLiferayDatabase) {

		List<String> sourcePartitionedTableNames = new ArrayList<>(
			sourceLiferayDatabase.getTableNames());
		List<String> targetPartitionedTableNames = new ArrayList<>(
			targetLiferayDatabase.getTableNames());

		for (String sourcePartitionedTableName : sourcePartitionedTableNames) {
			if (targetPartitionedTableNames.remove(
					sourcePartitionedTableName)) {

				continue;
			}

			recorder.registerWarning(
				"Table " + sourcePartitionedTableName +
					" is not present in the target database");
		}

		for (String targetPartitionedTableName : targetPartitionedTableNames) {
			recorder.registerWarning(
				"Table " + targetPartitionedTableName +
					" is not present in the source database");
		}
	}

	private static void _validateRelease(
		Recorder recorder, LiferayDatabase sourceLiferayDatabase,
		LiferayDatabase targetLiferayDatabase) {

		_validateReleaseState(
			recorder, sourceLiferayDatabase, targetLiferayDatabase);

		List<String> higherVersionModules = new ArrayList<>();
		List<String> lowerVersionModules = new ArrayList<>();
		List<String> missingSourceModules = new ArrayList<>();
		List<String> missingTargetModules = new ArrayList<>();
		List<String> missingTargetServiceModules = new ArrayList<>();
		Map<String, Release> targetReleases = _getReleases(
			targetLiferayDatabase);
		List<String> unverifiedSourceModules = new ArrayList<>();
		List<String> unverifiedTargetModules = new ArrayList<>();

		for (Release sourceRelease : sourceLiferayDatabase.getReleases()) {
			String sourceServletContextName =
				sourceRelease.getServletContextName();

			Release targetRelease = targetReleases.remove(
				sourceServletContextName);

			if (targetRelease == null) {
				missingSourceModules.add(sourceServletContextName);

				continue;
			}

			Version sourceVersion = sourceRelease.getSchemaVersion();
			Version targetVersion = targetRelease.getSchemaVersion();

			if (sourceVersion.compareTo(targetVersion) < 0) {
				lowerVersionModules.add(sourceServletContextName);
			}
			else if (sourceVersion.compareTo(targetVersion) > 0) {
				higherVersionModules.add(sourceServletContextName);
			}

			if (sourceRelease.getVerified() && !targetRelease.getVerified()) {
				unverifiedTargetModules.add(sourceServletContextName);
			}
			else if (!sourceRelease.getVerified() &&
					 targetRelease.getVerified()) {

				unverifiedSourceModules.add(sourceServletContextName);
			}
		}

		for (Release targetRelease : targetReleases.values()) {
			String targetServletContextName =
				targetRelease.getServletContextName();

			if (targetServletContextName.endsWith(".service")) {
				missingTargetServiceModules.add(targetServletContextName);
			}
			else {
				missingTargetModules.add(targetServletContextName);
			}
		}

		recorder.registerErrors(
			"needs to be upgraded in the target database before the migration",
			higherVersionModules);
		recorder.registerErrors(
			"needs to be upgraded in the source database before the migration",
			lowerVersionModules);
		recorder.registerWarnings(
			"is not present in the target database", missingSourceModules);
		recorder.registerWarnings(
			"is not present in the source database", missingTargetModules);
		recorder.registerErrors(
			"needs to be installed in the source database before the migration",
			missingTargetServiceModules);
		recorder.registerErrors(
			"needs to be verified in the source database before the migration",
			unverifiedSourceModules);
		recorder.registerErrors(
			"needs to be verified in the target database before the migration",
			unverifiedTargetModules);
	}

	private static void _validateReleaseState(
		Recorder recorder, LiferayDatabase sourceLiferayDatabase,
		LiferayDatabase targetLiferayDatabase) {

		String message = "has a failed release state in the %s database";

		List<String> failedServletContextNames = _getFailedServletContextNames(
			sourceLiferayDatabase);

		if (!failedServletContextNames.isEmpty()) {
			recorder.registerErrors(
				String.format(message, "source"), failedServletContextNames);
		}

		failedServletContextNames = _getFailedServletContextNames(
			targetLiferayDatabase);

		if (!failedServletContextNames.isEmpty()) {
			recorder.registerErrors(
				String.format(message, "target"), failedServletContextNames);
		}
	}

}