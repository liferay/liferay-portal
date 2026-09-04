/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.verify.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.db.DBResourceUtil;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.model.ReleaseConstants;
import com.liferay.portal.kernel.model.ServiceComponent;
import com.liferay.portal.kernel.service.ReleaseLocalService;
import com.liferay.portal.kernel.service.ServiceComponentLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.verify.PostupgradeVerifyDatabaseState;
import com.liferay.portal.verify.VerifyException;
import com.liferay.portal.verify.VerifyProcess;
import com.liferay.portal.verify.test.util.BaseVerifyProcessTestCase;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mariano Álvaro Sáiz
 */
@RunWith(Arquillian.class)
public class PostupgradeVerifyDatabaseStateTest
	extends BaseVerifyProcessTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testVerifyPostupgradeCustomTable() throws Exception {
		String tableName = getNormalizedName("TestCustomTable");

		_createTable(tableName);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				PostupgradeVerifyDatabaseState.class.getName(),
				LoggerTestUtil.INFO)) {

			testVerify();

			String message = _getMessage(logCapture, tableName);

			Assert.assertTrue(
				message, message.contains("Custom or untracked tables"));
		}
		finally {
			_dropTable(tableName);
		}
	}

	@Test
	public void testVerifyPostupgradeMissingNonserviceBuilderTable()
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, "textField")),
				ObjectDefinitionConstants.SCOPE_COMPANY);

		String tableName = getNormalizedName(objectDefinition.getDBTableName());

		_dropTable(tableName);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				PostupgradeVerifyDatabaseState.class.getName(),
				LoggerTestUtil.ERROR)) {

			testVerify();

			Assert.assertEquals(
				_getExpectedMessage(
					"Missing tables were detected", StringPool.BLANK,
					tableName),
				_getMessage(logCapture, tableName));
		}
		finally {
			_objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition);
		}
	}

	@Test
	public void testVerifyPostupgradeRequireSchemaVersionBundleTable()
		throws Exception {

		Map<String, String> tablesServletContextNames =
			DBResourceUtil.getTablesServletContextNames();

		Assert.assertEquals(
			"com.liferay.portal.configuration.persistence.impl",
			tablesServletContextNames.get("Configuration_"));

		String tableName = getNormalizedName("Configuration_");

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				PostupgradeVerifyDatabaseState.class.getName(),
				LoggerTestUtil.INFO)) {

			testVerify();

			for (String message : logCapture.getMessages()) {
				Assert.assertFalse(message, message.contains(tableName));
			}
		}
	}

	@Test
	public void testVerifyPostupgradeStaleTable() throws Exception {
		String tableName = getNormalizedName("TestStaleTable");

		_createTable(tableName);

		Release release = null;
		ServiceComponent serviceComponent = _addServiceComponent(tableName);

		try {
			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					PostupgradeVerifyDatabaseState.class.getName(),
					LoggerTestUtil.WARN)) {

				testVerify();

				Assert.assertEquals(
					_getExpectedMessage(
						"Stale tables were detected", _BUILD_NAMESPACE,
						tableName),
					_getMessage(logCapture, tableName));
			}

			release = _releaseLocalService.addRelease(
				_BUILD_NAMESPACE, "1.0.0");

			release.setState(ReleaseConstants.STATE_UPGRADE_FAILURE);

			release = _releaseLocalService.updateRelease(release);

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					PostupgradeVerifyDatabaseState.class.getName(),
					LoggerTestUtil.ERROR)) {

				testVerify();

				Assert.assertEquals(
					StringBundler.concat(
						"Module ", _BUILD_NAMESPACE,
						" has release state upgrade failure, schema version ",
						"1.0.0"),
					_getMessage(logCapture, "release state"));
			}
		}
		finally {
			if (release != null) {
				_releaseLocalService.deleteRelease(release);
			}

			_serviceComponentLocalService.deleteServiceComponent(
				serviceComponent);

			_dropTable(tableName);
		}
	}

	@Test
	public void testVerifyPostupgradeViews() throws Exception {
		Assume.assumeTrue(PropsValues.DATABASE_PARTITION_ENABLED);

		String staleViewName = getNormalizedName(_STALE_VIEW_NAME);

		renameView(_VIEW_NAME, _STALE_VIEW_NAME);

		ServiceComponent serviceComponent = null;

		try {
			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					PostupgradeVerifyDatabaseState.class.getName(),
					LoggerTestUtil.INFO)) {

				testVerify();

				Assert.assertEquals(
					_getExpectedMessage(
						"Missing views were detected",
						ReleaseConstants.DEFAULT_SERVLET_CONTEXT_NAME,
						getNormalizedName(_VIEW_NAME)),
					_getMessage(logCapture, "Missing views were detected"));

				String message = _getMessage(
					logCapture, "Custom or untracked views");

				Assert.assertTrue(message, message.contains(staleViewName));
			}

			serviceComponent = _addServiceComponent(_STALE_VIEW_NAME);

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					PostupgradeVerifyDatabaseState.class.getName(),
					LoggerTestUtil.WARN)) {

				testVerify();

				Assert.assertEquals(
					_getExpectedMessage(
						"Stale views were detected", _BUILD_NAMESPACE,
						staleViewName),
					_getMessage(logCapture, "Stale views were detected"));
			}
		}
		finally {
			if (serviceComponent != null) {
				_serviceComponentLocalService.deleteServiceComponent(
					serviceComponent);
			}

			renameView(_STALE_VIEW_NAME, _VIEW_NAME);
		}
	}

	@Override
	protected void doVerify() throws VerifyException {
		Object dclSingleton = ReflectionTestUtil.getFieldValue(
			PostupgradeVerifyDatabaseState.class,
			"_historicalServiceComponentTablesServletContextNamesDCLSingleton");

		ReflectionTestUtil.invoke(
			dclSingleton, "destroy", new Class<?>[] {Consumer.class},
			(Object)null);

		super.doVerify();
	}

	@Override
	protected VerifyProcess getVerifyProcess() {
		return new PostupgradeVerifyDatabaseState();
	}

	private ServiceComponent _addServiceComponent(String tableName)
		throws Exception {

		ServiceComponent serviceComponent =
			_serviceComponentLocalService.createServiceComponent(
				RandomTestUtil.nextLong());

		serviceComponent.setMvccVersion(0);
		serviceComponent.setBuildNamespace(_BUILD_NAMESPACE);
		serviceComponent.setData(
			StringBundler.concat("<![CDATA[create table ", tableName, " ("));

		return _serviceComponentLocalService.addServiceComponent(
			serviceComponent);
	}

	private void _createTable(String tableName) throws Exception {
		DB db = DBManagerUtil.getDB();

		db.runSQL("create table " + tableName + " (id LONG)");
	}

	private void _dropTable(String tableName) throws Exception {
		DB db = DBManagerUtil.getDB();

		db.runSQL("DROP_TABLE_IF_EXISTS(" + tableName + ")");
	}

	private String _getExpectedMessage(
			String prefix, String servletContextName, String tableName)
		throws Exception {

		if (PropsValues.DATABASE_PARTITION_ENABLED) {
			prefix = StringBundler.concat(
				prefix, " for company ", TestPropsValues.getCompanyId());
		}

		if (!servletContextName.isEmpty()) {
			prefix = StringBundler.concat(
				prefix, " in module ", servletContextName);
		}

		return StringBundler.concat(
			prefix, StringPool.COLON, StringPool.SPACE, StringPool.OPEN_BRACKET,
			tableName, StringPool.CLOSE_BRACKET);
	}

	private String _getMessage(LogCapture logCapture, String text) {
		List<String> messages = logCapture.getMessages();

		for (String message : messages) {
			if (message.contains(text)) {
				return message;
			}
		}

		return messages.toString();
	}

	private static final String _BUILD_NAMESPACE = "com.liferay.test.service";

	private static final String _STALE_VIEW_NAME = "TestStaleView";

	private static final String _VIEW_NAME = "VirtualHost";

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ReleaseLocalService _releaseLocalService;

	@Inject
	private ServiceComponentLocalService _serviceComponentLocalService;

}