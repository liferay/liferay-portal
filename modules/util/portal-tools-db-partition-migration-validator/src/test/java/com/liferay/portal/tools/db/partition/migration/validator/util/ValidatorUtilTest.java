/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.db.partition.migration.validator.util;

import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.tools.db.partition.migration.validator.Company;
import com.liferay.portal.tools.db.partition.migration.validator.LiferayDatabase;
import com.liferay.portal.tools.db.partition.migration.validator.Recorder;
import com.liferay.portal.tools.db.partition.migration.validator.Release;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Luis Ortiz
 */
public class ValidatorUtilTest {

	@Before
	public void setUp() {
		System.setOut(new PrintStream(_byteArrayOutputStream));

		_init(_sourceLiferayDatabase);
		_init(_targetLiferayDatabase);
	}

	@After
	public void tearDown() {
		System.setOut(_originalOut);
	}

	@Test
	@TestInfo("LPD-6742")
	public void testValidateCompany() throws Exception {
		Company company = new Company(
			RandomTestUtil.randomLong(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		_testValidateCompany(
			false, true, true, true, company,
			() -> _assertValidateDatabases(
				true, false,
				Arrays.asList(
					"[ERROR] Company ID " + company.getCompanyId() +
						" already exists in the target database")));
		_testValidateCompany(
			true, false, true, true, company,
			() -> _assertValidateDatabases(
				false, true,
				Arrays.asList(
					"[WARN] Company name ", company.getCompanyName(),
					" already exists in the target database. You must set a ",
					"different value in the portal instance import request.")));
		_testValidateCompany(
			true, true, false, true, company,
			() -> _assertValidateDatabases(
				false, true,
				Arrays.asList(
					"[WARN] Virtual host ", company.getVirtualHostname(),
					" already exists in the target database. You must set a ",
					"different value in the portal instance import request.")));
		_testValidateCompany(
			true, true, true, false, company,
			() -> _assertValidateDatabases(
				false, true,
				Arrays.asList(
					"[WARN] Web ID ", company.getWebId(),
					" already exists in the target database. You must set a ",
					"different value in the portal instance import request.")));
	}

	@Test
	@TestInfo("LPD-6742")
	public void testValidatePartitionedTables() throws Exception {
		_testValidatePartitionedTables(
			new ArrayList<>(
				Arrays.asList("table1", "table2", "table3", "table5")),
			new ArrayList<>(
				Arrays.asList("table1", "table3", "table4", "table5")),
			() -> _assertValidateDatabases(
				false, true,
				Arrays.asList(
					"[WARN] Table table2 is not present in the target database",
					"[WARN] Table table4 is not present in the source " +
						"database")));
		_testValidatePartitionedTables(
			new ArrayList<>(Arrays.asList("table1", "table3", "table4")),
			new ArrayList<>(
				Arrays.asList(
					"table1", "table2", "table3", "table4", "table5")),
			() -> _assertValidateDatabases(
				false, true,
				Arrays.asList(
					"[WARN] Table table2 is not present in the source database",
					"[WARN] Table table5 is not present in the source " +
						"database")));
		_testValidatePartitionedTables(
			new ArrayList<>(
				Arrays.asList(
					"table1", "table2", "table3", "table4", "table5")),
			new ArrayList<>(Arrays.asList("table1", "table3", "table4")),
			() -> _assertValidateDatabases(
				false, true,
				Arrays.asList(
					"[WARN] Table table2 is not present in the target database",
					"[WARN] Table table5 is not present in the target " +
						"database")));
	}

	@Test
	@TestInfo("LPD-6742")
	public void testValidateReleaseMissingSourceModules() {
		List<Release> targetReleases = new ArrayList<>();

		List<Release> sourceReleases = _getReleases();

		for (Release release : sourceReleases) {
			if (!Objects.equals(release.getServletContextName(), "module1")) {
				targetReleases.add(release);
			}
		}

		_sourceLiferayDatabase.setReleases(sourceReleases);
		_targetLiferayDatabase.setReleases(targetReleases);

		_assertValidateDatabases(
			false, true,
			Arrays.asList(
				"[WARN] Module module1 is not present in the target database"));
	}

	@Test
	@TestInfo("LPD-6742")
	public void testValidateReleaseMissingTargetModules() throws Exception {
		_testValidateReleaseMissingTargetModule(
			"module1",
			() -> _assertValidateDatabases(
				false, true,
				Arrays.asList(
					"[WARN] Module module1 is not present in the source " +
						"database")));
		_testValidateReleaseMissingTargetModule(
			"module2.service",
			() -> _assertValidateDatabases(
				true, false,
				Arrays.asList(
					"[ERROR] Module module2.service needs to be installed in " +
						"the source database before the migration")));
	}

	@Test
	@TestInfo("LPD-6742")
	public void testValidateReleaseState() throws Exception {
		List<String> failedServletContextNames = Arrays.asList(
			"module1", "module2");

		_testValidateReleaseState(
			failedServletContextNames, new ArrayList<>(),
			() -> _assertValidateDatabases(
				true, false,
				Arrays.asList(
					"[ERROR] Module module1 has a failed release state in " +
						"the source database",
					"[ERROR] Module module2 has a failed release state in " +
						"the source database")));
		_testValidateReleaseState(
			new ArrayList<>(), failedServletContextNames,
			() -> _assertValidateDatabases(
				true, false,
				Arrays.asList(
					"[ERROR] Module module1 has a failed release state in " +
						"the target database",
					"[ERROR] Module module2 has a failed release state in " +
						"the target database")));
	}

	@Test
	@TestInfo("LPD-6742")
	public void testValidateReleaseUnverifiedModules() throws Exception {
		_testValidateReleaseUnverifiedModule(
			"module2.service", true,
			() -> _assertValidateDatabases(
				true, false,
				Arrays.asList(
					"[ERROR] Module module2.service needs to be verified in " +
						"the source database before the migration")));
		_testValidateReleaseUnverifiedModule(
			"module2", false,
			() -> _assertValidateDatabases(
				true, false,
				Arrays.asList(
					"[ERROR] Module module2 needs to be verified in the " +
						"target database before the migration")));
	}

	@Test
	@TestInfo("LPD-6742")
	public void testValidateReleaseVersionModule() throws Exception {
		_testValidateReleaseVersionModule(
			"1.0.0", "module2.service",
			() -> _assertValidateDatabases(
				true, false,
				Arrays.asList(
					"[ERROR] Module module2.service needs to be upgraded in " +
						"the target database before the migration")));
		_testValidateReleaseVersionModule(
			"10.0.0", "module1",
			() -> _assertValidateDatabases(
				true, false,
				Arrays.asList(
					"[ERROR] Module module1 needs to be upgraded in the " +
						"source database before the migration")));
	}

	private void _assertValidateDatabases(
		boolean hasErrors, boolean hasWarnings, List<String> messages) {

		Recorder recorder = ValidatorUtil.validateDatabases(
			_sourceLiferayDatabase, _targetLiferayDatabase);

		Assert.assertEquals(hasErrors, recorder.hasErrors());
		Assert.assertEquals(hasWarnings, recorder.hasWarnings());

		recorder.printMessages();

		String string = _byteArrayOutputStream.toString();

		if (messages == null) {
			Assert.assertTrue(string.isEmpty());
		}
		else {
			for (String message : messages) {
				Assert.assertTrue(string.contains(message));
			}
		}
	}

	private List<Release> _getReleases() {
		return Arrays.asList(
			new Release(
				Version.parseVersion("3.5.1"), "module1.service", 0, true),
			new Release(
				Version.parseVersion("5.0.0"), "module2.service", 0, false),
			new Release(Version.parseVersion("2.3.2"), "module1", 0, true),
			new Release(Version.parseVersion("5.1.0"), "module2", 0, true));
	}

	private void _init(LiferayDatabase liferayDatabase) {
		liferayDatabase.setCompanies(new ArrayList<>());
		liferayDatabase.setExportedCompanyId(RandomTestUtil.randomLong());
		liferayDatabase.setExportedCompanyDefault(true);
		liferayDatabase.setReleases(new ArrayList<>());
		liferayDatabase.setTableNames(new ArrayList<>());
	}

	private void _testValidateCompany(
			boolean changeCompanyId, boolean changeName,
			boolean changeVirtualHostname, boolean changeWebId,
			Company sourceCompany, UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		_sourceLiferayDatabase.setCompanies(
			Collections.singletonList(sourceCompany));
		_sourceLiferayDatabase.setExportedCompanyId(
			sourceCompany.getCompanyId());

		Company targetCompany = new Company(
			RandomTestUtil.randomLong(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		if (!changeCompanyId) {
			targetCompany.setCompanyId(sourceCompany.getCompanyId());
		}

		if (!changeName) {
			targetCompany.setCompanyName(sourceCompany.getCompanyName());
		}

		if (!changeVirtualHostname) {
			targetCompany.setVirtualHostname(
				sourceCompany.getVirtualHostname());
		}

		if (!changeWebId) {
			targetCompany.setWebId(sourceCompany.getWebId());
		}

		_targetLiferayDatabase.setCompanies(
			Collections.singletonList(targetCompany));

		unsafeRunnable.run();
	}

	private void _testValidatePartitionedTables(
			List<String> sourceTableNames, List<String> targetTableNames,
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		_sourceLiferayDatabase.setTableNames(sourceTableNames);
		_targetLiferayDatabase.setTableNames(targetTableNames);

		unsafeRunnable.run();
	}

	private void _testValidateReleaseMissingTargetModule(
			String targetServletContextName,
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		List<Release> sourceReleases = new ArrayList<>();
		List<Release> targetReleases = new ArrayList<>();

		for (Release release : _getReleases()) {
			targetReleases.add(release);

			if (!targetServletContextName.equals(
					release.getServletContextName())) {

				sourceReleases.add(release);
			}
		}

		_sourceLiferayDatabase.setReleases(sourceReleases);
		_targetLiferayDatabase.setReleases(targetReleases);

		unsafeRunnable.run();
	}

	private void _testValidateReleaseState(
			List<String> sourceFailedServletContextNames,
			List<String> targetFailedServletContextNames,
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		List<Release> sourceReleases = new ArrayList<>();
		List<Release> targetReleases = new ArrayList<>();

		for (Release release : _getReleases()) {
			if (sourceFailedServletContextNames.contains(
					release.getServletContextName())) {

				release = new Release(
					release.getSchemaVersion(), release.getServletContextName(),
					1, release.getVerified());
			}

			sourceReleases.add(release);
		}

		for (Release release : _getReleases()) {
			if (targetFailedServletContextNames.contains(
					release.getServletContextName())) {

				release = new Release(
					release.getSchemaVersion(), release.getServletContextName(),
					1, release.getVerified());
			}

			targetReleases.add(release);
		}

		_sourceLiferayDatabase.setReleases(sourceReleases);
		_targetLiferayDatabase.setReleases(targetReleases);

		unsafeRunnable.run();
	}

	private void _testValidateReleaseUnverifiedModule(
			String targetServletContextName, boolean targetVerified,
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		List<Release> targetReleases = new ArrayList<>();

		List<Release> sourceReleases = _getReleases();

		for (Release release : sourceReleases) {
			if (targetServletContextName.equals(
					release.getServletContextName())) {

				release = new Release(
					release.getSchemaVersion(), targetServletContextName,
					release.getState(), targetVerified);
			}

			targetReleases.add(release);
		}

		_sourceLiferayDatabase.setReleases(sourceReleases);
		_targetLiferayDatabase.setReleases(targetReleases);

		unsafeRunnable.run();
	}

	private void _testValidateReleaseVersionModule(
			String targetVersion, String targetServletContextName,
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		List<Release> targetReleases = new ArrayList<>();

		List<Release> sourceReleases = _getReleases();

		_sourceLiferayDatabase.setReleases(sourceReleases);

		for (Release release : sourceReleases) {
			if (targetServletContextName.equals(
					release.getServletContextName())) {

				release = new Release(
					Version.parseVersion(targetVersion),
					targetServletContextName, release.getState(),
					release.getVerified());
			}

			targetReleases.add(release);
		}

		_targetLiferayDatabase.setReleases(targetReleases);

		unsafeRunnable.run();
	}

	private final ByteArrayOutputStream _byteArrayOutputStream =
		new ByteArrayOutputStream();
	private final PrintStream _originalOut = System.out;
	private final LiferayDatabase _sourceLiferayDatabase =
		new LiferayDatabase();
	private final LiferayDatabase _targetLiferayDatabase =
		new LiferayDatabase();

}