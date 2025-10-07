/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.db.partition.migration.validator.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.db.partition.test.util.BaseDBPartitionTestCase;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.tools.db.partition.migration.validator.DBPartitionMigrationValidator;
import com.liferay.portal.tools.db.partition.migration.validator.LiferayDatabase;
import com.liferay.portal.tools.db.partition.migration.validator.Recorder;
import com.liferay.portal.tools.db.partition.migration.validator.util.DatabaseUtil;
import com.liferay.portal.tools.db.partition.migration.validator.util.ValidatorUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import java.util.Arrays;

import org.apache.commons.io.FileUtils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luis Ortiz
 */
@RunWith(Arquillian.class)
public class DBPartitionMigrationValidatorTest extends BaseDBPartitionTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseDBPartitionTestCase.setUpClass();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_deleteCompany();
	}

	@Before
	public void setUp() throws Exception {
		System.setErr(new PrintStream(_errByteArrayOutputStream));
		System.setOut(new PrintStream(_outByteArrayOutputStream));

		if (_company == null) {
			_company = CompanyTestUtil.addCompany();
		}

		_outputDirectory = new File(PropsValues.LIFERAY_HOME, "exports");

		_outputDirectory.mkdirs();
	}

	@After
	public void tearDown() throws Exception {
		FileUtils.deleteDirectory(_outputDirectory);

		System.setErr(_originalErrPrintStream);
		System.setOut(_originalOutPrintStream);
	}

	@Test
	@TestInfo("LPD-6742")
	public void testValidateFailure() throws Exception {
		String sourceFileName = _testExport(_company.getCompanyId());
		String targetFileName = _testExport(
			PortalInstancePool.getDefaultCompanyId());

		File[] files = _outputDirectory.listFiles();

		Assert.assertEquals(Arrays.toString(files), 2, files.length);

		String[] messages = {
			"[ERROR] Company ID " + _company.getCompanyId() +
				" already exists in the target database",
			"[WARN] Company name ",
			_company.getName() +
				" already exists in the target database. You must set a " +
					"different value in " +
						"ImportPortalInstanceConfiguration.config.",
			"[WARN] Virtual host " + _company.getVirtualHostname() +
				" already exists in the target database. You must set a " +
					"different value in " +
						"ImportPortalInstanceConfiguration.config.",
			"[WARN] Web ID ",
			_company.getWebId() +
				" already exists in the target database. You must set a " +
					"different value in " +
						"ImportPortalInstanceConfiguration.config."
		};

		_testValidate(
			sourceFileName, targetFileName,
			runtimeException -> {
				String string = _outByteArrayOutputStream.toString();

				for (String message : messages) {
					Assert.assertTrue(string.contains(message));
				}
			},
			() -> {
			});
	}

	@Test
	@TestInfo("LPD-6742")
	public void testValidateSuccess() throws Exception {
		String sourceFileName = _testExport(_company.getCompanyId());

		_deleteCompany();

		String targetFileName = _testExport(
			PortalInstancePool.getDefaultCompanyId());

		File[] files = _outputDirectory.listFiles();

		Assert.assertEquals(Arrays.toString(files), 2, files.length);

		_testValidate(
			sourceFileName, targetFileName,
			runtimeException -> Assert.assertEquals(
				"0", runtimeException.getMessage()),
			() -> {
				Assert.assertTrue(
					_outByteArrayOutputStream.toString(
					).isEmpty());
				Assert.assertTrue(
					_errByteArrayOutputStream.toString(
					).isEmpty());
			});
	}

	private static void _deleteCompany() throws Exception {
		if (_company != null) {
			_companyLocalService.deleteCompany(_company);
		}

		_company = null;
	}

	private String _testExport(long companyId) throws Exception {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(companyId)) {

			return ReflectionTestUtil.invoke(
				DBPartitionMigrationValidator.class, "_write",
				new Class<?>[] {LiferayDatabase.class, String.class},
				DatabaseUtil.exportLiferayDatabase(connection, companyId),
				_outputDirectory.getAbsolutePath());
		}
	}

	private void _testValidate(
			String sourceFileName, String targetFileName,
			UnsafeConsumer<RuntimeException, Exception> unsafeConsumer,
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try {
			LiferayDatabase sourceLiferayDatabase = ReflectionTestUtil.invoke(
				DBPartitionMigrationValidator.class, "_read",
				new Class<?>[] {String.class}, sourceFileName);
			LiferayDatabase targetLiferayDatabase = ReflectionTestUtil.invoke(
				DBPartitionMigrationValidator.class, "_read",
				new Class<?>[] {String.class}, targetFileName);

			Recorder recorder = ValidatorUtil.validateDatabases(
				sourceLiferayDatabase, targetLiferayDatabase);

			recorder.printMessages();
		}
		catch (RuntimeException runtimeException) {
			unsafeConsumer.accept(runtimeException);
		}

		unsafeRunnable.run();
	}

	private static Company _company;

	@Inject
	private static CompanyLocalService _companyLocalService;

	private static File _outputDirectory;

	private final ByteArrayOutputStream _errByteArrayOutputStream =
		new ByteArrayOutputStream();
	private final PrintStream _originalErrPrintStream = System.err;
	private final PrintStream _originalOutPrintStream = System.out;
	private final ByteArrayOutputStream _outByteArrayOutputStream =
		new ByteArrayOutputStream();

}