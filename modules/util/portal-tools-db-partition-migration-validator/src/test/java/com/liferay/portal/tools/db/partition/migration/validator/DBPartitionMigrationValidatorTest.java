/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.db.partition.migration.validator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.tools.db.partition.migration.validator.util.BaseTestCase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.skyscreamer.jsonassert.JSONAssert;

/**
 * @author Luis Ortiz
 */
public class DBPartitionMigrationValidatorTest extends BaseTestCase {

	@Before
	public void setUp() {
		_errorFile = new File(StringUtil.randomString());
		_outputFile = new File(StringUtil.randomString());
	}

	@After
	public void tearDown() {
		_errorFile.delete();
		_outputFile.delete();
	}

	@Test
	@TestInfo("LPD-6742")
	public void testExportDefaultDatabase() throws Exception {
		_testExport(
			true, Collections.singletonList(RandomTestUtil.randomLong()), true);
	}

	@Test
	@TestInfo("LPD-6742")
	public void testExportDefaultDatabaseWithMultipleCompanies()
		throws Exception {

		_testExport(
			true,
			Arrays.asList(
				RandomTestUtil.randomLong(), RandomTestUtil.randomLong()),
			true);
	}

	@Test
	@TestInfo("LPD-6742")
	public void testExportNondefaultDatabase() throws Exception {
		_testExport(
			true, Collections.singletonList(RandomTestUtil.randomLong()),
			false);
	}

	@Test
	@TestInfo("LPD-6742")
	public void testExportNondefaultDatabaseWithMultipleCompanies()
		throws Exception {

		_testExport(
			true,
			Arrays.asList(
				RandomTestUtil.randomLong(), RandomTestUtil.randomLong()),
			false);
	}

	@Test
	@TestInfo("LPD-39640")
	public void testExportNonexistentDatabase() throws Exception {
		_testExport(
			false, Collections.singletonList(RandomTestUtil.randomLong()),
			false);
	}

	@Test
	@TestInfo("LPD-39640")
	public void testExportNonexistentDatabaseWithMultipleCompanies()
		throws Exception {

		_testExport(
			false,
			Arrays.asList(
				RandomTestUtil.randomLong(), RandomTestUtil.randomLong()),
			false);
	}

	@Test
	@TestInfo("LPD-6742")
	public void testValidateFailure() throws Exception {
		String[] messages = {
			"[ERROR] Company ID 3007447931789165977 already exists in the " +
				"target database",
			"[ERROR] Module com.liferay.address.impl needs to be verified in " +
				"the source database before the migration",
			"[ERROR] Module com.liferay.comment.page.comments.web has a " +
				"failed release state in the source database",
			"[ERROR] Module com.liferay.exportimport.service needs to be " +
				"installed in the source database before the migration",
			"[ERROR] Module com.liferay.knowledge.base.web needs to be " +
				"upgraded in the target database before the migration",
			"[ERROR] Module com.liferay.organizations.service has a failed " +
				"release state in the target database",
			"[ERROR] Module com.liferay.organizations.service needs to be " +
				"verified in the target database before the migration",
			"[ERROR] Module com.liferay.wiki.web needs to be upgraded in the " +
				"source database before the migration",
			"[WARN] Company name Liferay DXP already exists in the target " +
				"database. You must set a different value in the portal " +
					"instance import request.",
			"[WARN] Module com.liferay.asset.publisher.web is not present in " +
				"the source database",
			"[WARN] Module com.liferay.license.manager.web is not present in " +
				"the target database",
			"[WARN] Table CommercePriceList is not present in the source " +
				"database",
			"[WARN] Table DDMTemplate is not present in the target database",
			"[WARN] Virtual host localhost already exists in the target " +
				"database. You must set a different value in the portal " +
					"instance import request.",
			"[WARN] Web ID liferay.com already exists in the target " +
				"database. You must set a different value in the portal " +
					"instance import request."
		};

		_testValidate(
			"source-failure.json", "target-failure.json",
			runtimeException -> {
				Assert.assertEquals("1", runtimeException.getMessage());

				String outputFileContent = new String(
					Files.readAllBytes(_outputFile.toPath()), StringPool.UTF8);

				for (String message : messages) {
					Assert.assertTrue(outputFileContent.contains(message));
				}
			},
			() -> {
			});
	}

	@Test
	@TestInfo("LPD-6742")
	public void testValidateSuccess() throws Exception {
		_testValidate(
			"source-success.json", "target-success.json",
			runtimeException -> Assert.assertEquals(
				"0", runtimeException.getMessage()),
			() -> {
				Assert.assertEquals(
					0, Files.readAllBytes(_errorFile.toPath()).length);

				String outputFileContent = new String(
					Files.readAllBytes(_outputFile.toPath()), StringPool.UTF8);

				Assert.assertTrue(
					outputFileContent.contains(_BETA_FEATURE_MESSAGE));
			});
	}

	@Test
	@TestInfo("LPD-6742")
	public void testValidateTargetNondefaultPartition() throws Exception {
		_testValidate(
			"source-success.json", "target-nondefault.json",
			runtimeException -> {
				String errorFileContent = new String(
					Files.readAllBytes(_errorFile.toPath()), StringPool.UTF8);

				Assert.assertEquals("1", runtimeException.getMessage());
				Assert.assertTrue(
					errorFileContent.contains(
						"Target is not the default partition"));

				String outputFileContent = new String(
					Files.readAllBytes(_outputFile.toPath()), StringPool.UTF8);

				Assert.assertTrue(
					outputFileContent.contains(_BETA_FEATURE_MESSAGE));
			},
			() -> {
			});
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private static <T> T _deserialize(
			String string, TypeReference<T> typeReference)
		throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();

		Base64.Decoder decoder = Base64.getDecoder();

		String decodedString = new String(decoder.decode(string));

		return objectMapper.readValue(decodedString, typeReference);
	}

	private static void _mockDatabase(
			List<Company> companies, List<Long> companyIds,
			List<Long> companyInfoIds, boolean defaultPartition,
			String password, List<Release> releases, String schemaName,
			List<String> tableNames, String url, String user)
		throws Exception {

		mockGetColumns(tableNames);
		mockGetCompanies(companies);
		mockGetCompanyIds(companyIds);
		mockGetCompanyInfos(companyInfoIds);
		mockGetConnection(
			password, StringUtil.replace(url, "lportal", schemaName), user);
		mockGetReleases(releases);
		mockGetTables(defaultPartition);
	}

	private void _execute(List<String> args, List<String> jvmArgs)
		throws Exception {

		List<String> commands = new ArrayList<>();

		commands.add(
			StringBundler.concat(
				System.getProperty("java.home"), File.separator, "bin",
				File.separator, "java"));
		commands.addAll(jvmArgs);
		commands.add("-cp");
		commands.add(System.getProperty("java.class.path"));
		commands.add(MockedDBPartitionMigrationValidator.class.getName());
		commands.addAll(args);

		Process process = new ProcessBuilder(
			commands
		).redirectOutput(
			_outputFile
		).redirectError(
			_errorFile
		).start();

		process.waitFor();

		throw new RuntimeException(String.valueOf(process.exitValue()));
	}

	private String _getJVMArg(String key, String value) {
		return StringBundler.concat("-D", key, StringPool.EQUAL, value);
	}

	private String _getPathString(String fileName) throws Exception {
		URL url = DBPartitionMigrationValidatorTest.class.getResource(
			"dependencies/" + fileName);

		Path path = Paths.get(url.toURI());

		return path.toString();
	}

	private String _read(File file) throws Exception {
		StringBuilder sb = new StringBuilder();

		try (BufferedReader bufferedReader = new BufferedReader(
				new FileReader(file))) {

			String line = null;

			while ((line = bufferedReader.readLine()) != null) {
				sb.append(line);
			}
		}

		return sb.toString();
	}

	private String _serialize(Object object) throws JsonProcessingException {
		Base64.Encoder encoder = Base64.getEncoder();

		ObjectMapper objectMapper = new ObjectMapper();

		String json = objectMapper.writeValueAsString(object);

		return encoder.encodeToString(json.getBytes());
	}

	private void _testExport(
			boolean companyExists, List<Long> companyIds,
			boolean defaultPartition)
		throws Exception {

		List<Company> companies = Arrays.asList(
			new Company(
				RandomTestUtil.randomLong(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString()),
			new Company(
				RandomTestUtil.randomLong(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString()));
		long companyId =
			companyExists ? companyIds.get(0) : RandomTestUtil.randomLong();
		String password = RandomTestUtil.randomString();
		String schemaName = RandomTestUtil.randomString();
		String user = RandomTestUtil.randomString();

		File outputDirectory = temporaryFolder.newFolder();

		try {
			_execute(
				Arrays.asList(
					"export", "--company-id", String.valueOf(companyId),
					"--jdbc-url", _JDBC_URL, "--output-dir",
					outputDirectory.getAbsolutePath(), "--password", password,
					"--schema-name", schemaName, "--user", user),
				Arrays.asList(
					_getJVMArg(
						_SYSTEM_PROPERTY_KEY_COMPANIES, _serialize(companies)),
					_getJVMArg(
						_SYSTEM_PROPERTY_KEY_COMPANY_IDS,
						_serialize(companyIds)),
					_getJVMArg(
						_SYSTEM_PROPERTY_KEY_DEFAULT_PARTITION,
						String.valueOf(defaultPartition)),
					_getJVMArg(_SYSTEM_PROPERTY_KEY_PASSWORD, password),
					_getJVMArg(_SYSTEM_PROPERTY_KEY_SCHEMA_NAME, schemaName),
					_getJVMArg(_SYSTEM_PROPERTY_KEY_USER, user)));
		}
		catch (RuntimeException runtimeException) {
			String errorFileContent = new String(
				Files.readAllBytes(_errorFile.toPath()), StringPool.UTF8);

			if (!companyExists) {
				Assert.assertTrue(
					errorFileContent.contains(
						"Company " + companyId + " does not exist"));
				Assert.assertEquals("1", runtimeException.getMessage());

				File[] files = outputDirectory.listFiles();

				Assert.assertEquals(Arrays.toString(files), 0, files.length);

				return;
			}

			Assert.assertEquals("0", runtimeException.getMessage());
		}

		File[] files = outputDirectory.listFiles();

		Assert.assertEquals(Arrays.toString(files), 1, files.length);

		String content = _read(files[0]);

		JSONAssert.assertEquals(
			new JSONObject(
			).put(
				"companies", new JSONArray(companies)
			).toString(),
			content, false);
		JSONAssert.assertEquals(
			new JSONObject(
			).put(
				"exportedCompanyDefault", defaultPartition
			).toString(),
			content, false);

		Long exportedCompanyId = null;

		if (companyIds.size() == 1) {
			exportedCompanyId = companyIds.get(0);
		}

		JSONAssert.assertEquals(
			new JSONObject(
			).put(
				"exportedCompanyId", exportedCompanyId
			).toString(),
			content, false);

		JSONAssert.assertEquals(
			new JSONObject(
			).put(
				"releases", new JSONArray(_releases)
			).toString(),
			content, false);
		JSONAssert.assertEquals(
			new JSONObject(
			).put(
				"tableNames", new JSONArray(Arrays.asList("Table1", "Table2"))
			).toString(),
			content, false);
	}

	private void _testValidate(
			String sourceFileName, String targetFileName,
			UnsafeConsumer<RuntimeException, Exception> unsafeConsumer,
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try {
			_execute(
				Arrays.asList(
					"validate", "--source-file", _getPathString(sourceFileName),
					"--target-file", _getPathString(targetFileName)),
				Collections.emptyList());
		}
		catch (RuntimeException runtimeException) {
			unsafeConsumer.accept(runtimeException);
		}

		unsafeRunnable.run();
	}

	private static final String _BETA_FEATURE_MESSAGE =
		"This tool is a beta feature. It is experimental and not supported.";

	private static final String _JDBC_URL =
		"jdbc:mysql://localhost:3306/lportal?useUnicode=true";

	private static final String _SYSTEM_PROPERTY_KEY_COMPANIES =
		"dbpartitionmigrationvalidatortest.companies";

	private static final String _SYSTEM_PROPERTY_KEY_COMPANY_IDS =
		"dbpartitionmigrationvalidatortest.company.ids";

	private static final String _SYSTEM_PROPERTY_KEY_DEFAULT_PARTITION =
		"dbpartitionmigrationvalidatortest.defaultPartition";

	private static final String _SYSTEM_PROPERTY_KEY_PASSWORD =
		"dbpartitionmigrationvalidatortest.password";

	private static final String _SYSTEM_PROPERTY_KEY_SCHEMA_NAME =
		"dbpartitionmigrationvalidatortest.schemaName";

	private static final String _SYSTEM_PROPERTY_KEY_USER =
		"dbpartitionmigrationvalidatortest.user";

	private static final List<Release> _releases = Arrays.asList(
		new Release(Version.parseVersion("14.2.4"), "module1", 0, true),
		new Release(Version.parseVersion("2.0.1"), "module2", 1, false));

	private File _errorFile;
	private File _outputFile;

	private static class MockedDBPartitionMigrationValidator {

		public static void main(String[] args) throws Exception {
			if (args[0].equals("export")) {
				List<Company> companies = _deserialize(
					System.getProperty(_SYSTEM_PROPERTY_KEY_COMPANIES),
					new TypeReference<List<Company>>() {
					});
				List<Long> companyIds = _deserialize(
					System.getProperty(_SYSTEM_PROPERTY_KEY_COMPANY_IDS),
					new TypeReference<List<Long>>() {
					});
				boolean defaultPartition = GetterUtil.getBoolean(
					System.getProperty(_SYSTEM_PROPERTY_KEY_DEFAULT_PARTITION));
				String password = System.getProperty(
					_SYSTEM_PROPERTY_KEY_PASSWORD);
				String schemaName = System.getProperty(
					_SYSTEM_PROPERTY_KEY_SCHEMA_NAME);
				String user = System.getProperty(_SYSTEM_PROPERTY_KEY_USER);

				_mockDatabase(
					companies, companyIds, companyIds, defaultPartition,
					password, _releases, schemaName,
					Arrays.asList(
						"Company", "Object_x_" + companyIds.get(0), "Table1",
						"Table2"),
					_JDBC_URL, user);
			}

			DBPartitionMigrationValidator.main(args);
		}

	}

}