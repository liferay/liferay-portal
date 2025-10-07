/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db.partition.file.install.deploy.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.db.partition.test.util.BaseDBPartitionTestCase;
import com.liferay.portal.db.partition.util.DBPartitionUtil;
import com.liferay.portal.file.install.FileInstaller;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.kernel.util.PropsValues;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Objects;

import javax.sql.DataSource;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Luis Ortiz
 */
@RunWith(Arquillian.class)
public class DBPartitionFileInstallDeployTest extends BaseDBPartitionTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseDBPartitionTestCase.setUpClass();

		BaseDBPartitionTestCase.setUpDBPartitions();

		Bundle bundle = FrameworkUtil.getBundle(
			DBPartitionFileInstallDeployTest.class);

		try (ServiceTrackerList<FileInstaller> serviceTrackerList =
				ServiceTrackerListFactory.open(
					bundle.getBundleContext(), FileInstaller.class)) {

			for (FileInstaller fileInstaller : serviceTrackerList.toList()) {
				Class<?> clazz = fileInstaller.getClass();

				if (Objects.equals(
						clazz.getName(),
						"com.liferay.portal.file.install.internal." +
							"configuration.ConfigurationFileInstaller")) {

					_fileInstaller = fileInstaller;

					break;
				}
			}
		}

		_dataSource = InfrastructureUtil.getDataSource();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		BaseDBPartitionTestCase.tearDownDBPartitions();
	}

	@Test
	public void testCompanyScopedConfiguration() throws Exception {
		_testCompanyScopedConfiguration(
			ExtendedObjectClassDefinition.Scope.COMPANY.getPropertyKey(),
			COMPANY_IDS[1]);
	}

	@Test
	public void testCompanyScopedPortableKeyConfiguration() throws Exception {
		_testCompanyScopedConfiguration(
			ExtendedObjectClassDefinition.Scope.COMPANY.
				getPortablePropertyKey(),
			"Test" + COMPANY_IDS[1]);
	}

	@Test
	public void testGroupScopedConfiguration() throws Exception {
		_testGroupScopedConfiguration(
			ExtendedObjectClassDefinition.Scope.GROUP.getPropertyKey(),
			RandomTestUtil.randomLong());
	}

	@Test
	public void testGroupScopedPortableKeyConfiguration() throws Exception {
		_testGroupScopedConfiguration(
			ExtendedObjectClassDefinition.Scope.GROUP.getPortablePropertyKey(),
			RandomTestUtil.randomLong());
	}

	@Test
	public void testPortletInstanceScopedConfiguration() throws Exception {
		_testPortletInstanceScopedConfiguration(
			ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE.
				getPropertyKey(),
			RandomTestUtil.randomLong());
	}

	@Test
	public void testPortletInstanceScopedPortableKeyConfiguration()
		throws Exception {

		_testPortletInstanceScopedConfiguration(
			ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE.
				getPortablePropertyKey(),
			RandomTestUtil.randomLong());
	}

	@Test
	public void testSystemScopedConfiguration() throws Exception {
		_testScopedConfiguration(
			null, null,
			() -> _checkConfigurationExists(
				PortalInstancePool.getDefaultCompanyId(), _TEST_VALUE_1),
			() -> _checkConfigurationExists(
				PortalInstancePool.getDefaultCompanyId(), _TEST_VALUE_2),
			unsupportedOperationException -> Assert.fail(), true);
	}

	private void _checkConfiguration(
			UnsafeBiConsumer<Long, ResultSet, Exception> unsafeBiConsumer)
		throws Exception {

		DBPartitionUtil.forEachCompanyId(
			companyId -> {
				try (Connection connection = _dataSource.getConnection();
					PreparedStatement preparedStatement =
						connection.prepareStatement(
							"select dictionary from Configuration_ where " +
								"configurationId = ?")) {

					preparedStatement.setString(1, _CONFIGURATION_FACTORY_PID);

					try (ResultSet resultSet =
							preparedStatement.executeQuery()) {

						unsafeBiConsumer.accept(companyId, resultSet);
					}
				}
			});
	}

	private void _checkConfigurationExists(long companyId, String expectedValue)
		throws Exception {

		_checkConfiguration(
			(currentCompanyId, resultSet) -> {
				if (currentCompanyId == companyId) {
					Assert.assertTrue(resultSet.next());

					String value = resultSet.getString(1);

					Assert.assertTrue(
						value.contains(
							StringBundler.concat(
								_TEST_KEY, StringPool.EQUAL, StringPool.QUOTE,
								expectedValue, StringPool.QUOTE)));
				}
				else {
					Assert.assertFalse(resultSet.next());
				}
			});
	}

	private void _checkConfigurationNotExists() throws Exception {
		_checkConfiguration(
			(companyId, resultSet) -> Assert.assertFalse(resultSet.next()));
	}

	private String _convertDictionaryValue(Object value) {
		if (value instanceof Long) {
			return StringBundler.concat("L\"", value, StringPool.QUOTE);
		}

		return StringBundler.concat(StringPool.QUOTE, value, StringPool.QUOTE);
	}

	private void _testCompanyScopedConfiguration(
			String dictionaryKey, Object dictionaryValue)
		throws Exception {

		_testScopedConfiguration(
			dictionaryKey, dictionaryValue,
			() -> _checkConfigurationExists(COMPANY_IDS[1], _TEST_VALUE_1),
			() -> _checkConfigurationExists(COMPANY_IDS[1], _TEST_VALUE_2),
			unsupportedOperationException -> Assert.fail(), true);
	}

	private void _testGroupScopedConfiguration(
			String dictionaryKey, Object dictionaryValue)
		throws Exception {

		_testScopedConfiguration(
			dictionaryKey, dictionaryValue,
			() -> _checkConfigurationNotExists(),
			() -> _checkConfigurationNotExists(),
			unsupportedOperationException -> Assert.assertEquals(
				"Group scoped configuration files do not support database " +
					"partitioning",
				unsupportedOperationException.getMessage()),
			false);
	}

	private void _testPortletInstanceScopedConfiguration(
			String dictionaryKey, Object dictionaryValue)
		throws Exception {

		_testScopedConfiguration(
			dictionaryKey, dictionaryValue,
			() -> _checkConfigurationNotExists(),
			() -> _checkConfigurationNotExists(),
			unsupportedOperationException -> Assert.assertEquals(
				"Portlet-instance scoped configuration files do not support " +
					"database partitioning",
				unsupportedOperationException.getMessage()),
			false);
	}

	private void _testScopedConfiguration(
			String dictionaryKey, Object dictionaryValue,
			UnsafeRunnable<Exception> addValidatorRunnable,
			UnsafeRunnable<Exception> updateValidatorRunnable,
			UnsafeConsumer<UnsupportedOperationException, Exception>
				exceptionValidatorConsumer,
			boolean supportedConfiguration)
		throws Exception {

		Assert.assertNotNull(_fileInstaller);

		Path path = Paths.get(
			PropsValues.LIFERAY_HOME,
			_CONFIGURATION_FACTORY_PID.concat(".config"));

		try {
			try {
				String content = StringBundler.concat(
					_TEST_KEY, StringPool.EQUAL, StringPool.QUOTE,
					_TEST_VALUE_1, StringPool.QUOTE);

				if (dictionaryKey != null) {
					content = StringBundler.concat(
						content, StringPool.RETURN_NEW_LINE, dictionaryKey,
						StringPool.EQUAL,
						_convertDictionaryValue(dictionaryValue));
				}

				Files.write(path, content.getBytes());

				_fileInstaller.transformURL(path.toFile());

				if (!supportedConfiguration) {
					Assert.fail();
				}
			}
			catch (UnsupportedOperationException
						unsupportedOperationException) {

				exceptionValidatorConsumer.accept(
					unsupportedOperationException);
			}

			addValidatorRunnable.run();

			try {
				String content = StringBundler.concat(
					_TEST_KEY, StringPool.EQUAL, StringPool.QUOTE,
					_TEST_VALUE_2, StringPool.QUOTE);

				if (dictionaryKey != null) {
					content = StringBundler.concat(
						content, StringPool.RETURN_NEW_LINE, dictionaryKey,
						StringPool.EQUAL,
						_convertDictionaryValue(dictionaryValue));
				}

				Files.write(path, content.getBytes());

				_fileInstaller.transformURL(path.toFile());

				if (!supportedConfiguration) {
					Assert.fail();
				}
			}
			catch (UnsupportedOperationException
						unsupportedOperationException) {

				exceptionValidatorConsumer.accept(
					unsupportedOperationException);
			}

			updateValidatorRunnable.run();

			Files.deleteIfExists(path);

			_fileInstaller.uninstall(path.toFile());

			_checkConfigurationNotExists();
		}
		finally {
			Files.deleteIfExists(path);

			DBPartitionUtil.forEachCompanyId(
				companyId -> {
					try (Connection connection = _dataSource.getConnection();
						PreparedStatement preparedStatement =
							connection.prepareStatement(
								"delete from Configuration_ where " +
									"configurationId = ?")) {

						preparedStatement.setString(
							1, _CONFIGURATION_FACTORY_PID);
						preparedStatement.executeUpdate();
					}
				});

			_fileInstaller.uninstall(path.toFile());
		}
	}

	private static final String _CONFIGURATION_FACTORY_PID =
		DBPartitionFileInstallDeployTest.class.getName() + "Configuration~" +
			RandomTestUtil.randomString();

	private static final String _TEST_KEY = "testKey";

	private static final String _TEST_VALUE_1 = "testValue1";

	private static final String _TEST_VALUE_2 = "testValue2";

	private static DataSource _dataSource;
	private static FileInstaller _fileInstaller;

}