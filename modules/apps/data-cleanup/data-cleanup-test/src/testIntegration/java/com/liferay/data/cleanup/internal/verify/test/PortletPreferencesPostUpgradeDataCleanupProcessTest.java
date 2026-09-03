/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.cleanup.internal.verify.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.data.cleanup.util.OrphanReferencesDataCleanupUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;

/**
 * @author Luis Ortiz
 */
@RunWith(Arquillian.class)
public class PortletPreferencesPostUpgradeDataCleanupProcessTest
	extends BasePostUpgradeDataCleanupProcessTestCase {

	@Test
	public void testInstanceScopedPortletPreferenceLinkedToLiferayPortletIsNotDeleted()
		throws Exception {

		List<Portlet> portlets = PortletLocalServiceUtil.getPortlets();

		Portlet portlet = portlets.get(0);

		String portletId =
			portlet.getPortletId() + "_INSTANCE_" +
				RandomTestUtil.randomString();

		test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(messages.toString(), messages.isEmpty());

				Assert.assertTrue(_hasPortletPreference(portletId));
			},
			() -> _deletePortletPreference(portletId),
			() -> _addPortletPreference(0, portletId),
			OrphanReferencesDataCleanupUtil.class.getName(),
			LoggerTestUtil.INFO);
	}

	@Test
	public void testInstanceScopedPortletPreferenceLinkedToNotFoundLiferayPortletIsDeleted()
		throws Exception {

		String portletId = StringBundler.concat(
			"com.liferay.", RandomTestUtil.randomString(), "_INSTANCE_",
			RandomTestUtil.randomString());

		test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(
					messages.toString(),
					messages.contains(
						StringBundler.concat(
							"Table ",
							dbInspector.normalizeName("PortletPreferences"),
							", 1 row deleted because ",
							dbInspector.normalizeName("portletId"), " ",
							portletId, " was not found in column ",
							dbInspector.normalizeName("portletId"),
							" from table ",
							dbInspector.normalizeName(_TEMP_TABLE_NAME))));

				Assert.assertFalse(_hasPortletPreference(portletId));
			},
			() -> _deletePortletPreference(portletId),
			() -> _addPortletPreference(0, portletId),
			OrphanReferencesDataCleanupUtil.class.getName(),
			LoggerTestUtil.INFO);
	}

	@Test
	public void testInstanceScopedPortletPreferenceLinkedToNotFoundLiferayPortletIsNotDeletedInReadonlyMode()
		throws Exception {

		_deletePortlets = false;

		String portletId = StringBundler.concat(
			"com.liferay.", RandomTestUtil.randomString(), "_INSTANCE_",
			RandomTestUtil.randomString());

		test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(
					messages.toString(),
					messages.contains(
						StringBundler.concat(
							"Table ",
							dbInspector.normalizeName("PortletPreferences"),
							", 1 row should be deleted because ",
							dbInspector.normalizeName("portletId"), " ",
							portletId, " was not found in column ",
							dbInspector.normalizeName("portletId"),
							" from table ",
							dbInspector.normalizeName(_TEMP_TABLE_NAME))));

				Assert.assertTrue(_hasPortletPreference(portletId));
			},
			() -> _deletePortletPreference(portletId),
			() -> _addPortletPreference(0, portletId),
			OrphanReferencesDataCleanupUtil.class.getName(),
			LoggerTestUtil.INFO);
	}

	@Test
	public void testNonliferayPortletIsNotDeleted() throws Exception {
		String portletId =
			"com.test.not.liferay." + RandomTestUtil.randomString();

		test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(messages.toString(), messages.isEmpty());

				Assert.assertTrue(_hasPortlet(portletId));
			},
			() -> _deletePortlet(portletId), () -> _addPortlet(portletId));
	}

	@Test
	public void testNotFoundLiferayPortletIsDeleted() throws Exception {
		String portletId = "com.liferay." + RandomTestUtil.randomString();

		test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(
					messages.toString(),
					messages.contains(
						StringBundler.concat(
							"Table ", dbInspector.normalizeName("Portlet"),
							", 1 row deleted because \"", portletId,
							"\" is not installed")));

				Assert.assertFalse(_hasPortlet(portletId));
			},
			() -> _deletePortlet(portletId), () -> _addPortlet(portletId));
	}

	@Test
	public void testNotFoundLiferayPortletIsNotDeletedInReadonlyMode()
		throws Exception {

		_deletePortlets = false;

		String portletId = "com.liferay." + RandomTestUtil.randomString();

		test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(
					messages.toString(),
					messages.contains(
						StringBundler.concat(
							"Table ", dbInspector.normalizeName("Portlet"),
							", 1 row should be deleted because \"", portletId,
							"\" is not installed")));

				Assert.assertTrue(_hasPortlet(portletId));
			},
			() -> _deletePortlet(portletId), () -> _addPortlet(portletId));
	}

	@Test
	public void testPortletPreferenceLinkedToExistentLayoutRevisionIsNotDeleted()
		throws Exception {

		List<Portlet> portlets = PortletLocalServiceUtil.getPortlets();

		Portlet portlet = portlets.get(0);

		String portletId = portlet.getPortletId();

		long plid = RandomTestUtil.nextLong();

		test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(messages.toString(), messages.isEmpty());

				Assert.assertTrue(_hasPortletPreference(portletId));
			},
			() -> {
				_deleteLayoutRevision(plid);
				_deletePortletPreference(portletId);
			},
			() -> {
				_addLayoutRevision(plid);
				_addPortletPreference(plid, portletId);
			},
			OrphanReferencesDataCleanupUtil.class.getName(),
			LoggerTestUtil.INFO);
	}

	@Test
	public void testPortletPreferenceLinkedToLiferayPortletIsNotDeleted()
		throws Exception {

		List<Portlet> portlets = PortletLocalServiceUtil.getPortlets();

		String portletId = portlets.get(
			0
		).getPortletId();

		test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(messages.toString(), messages.isEmpty());

				Assert.assertTrue(_hasPortletPreference(portletId));
			},
			() -> _deletePortletPreference(portletId),
			() -> _addPortletPreference(0, portletId),
			OrphanReferencesDataCleanupUtil.class.getName(),
			LoggerTestUtil.INFO);
	}

	@Test
	public void testPortletPreferenceLinkedToNonexistentLayoutIsNotDeletedInReadonlyMode()
		throws Exception {

		_deletePortlets = false;

		List<Portlet> portlets = PortletLocalServiceUtil.getPortlets();

		Portlet portlet = portlets.get(0);

		String portletId = portlet.getPortletId();

		long plid = RandomTestUtil.nextLong();

		test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertFalse(
					messages.toString(),
					messages.contains(String.valueOf(plid)));

				Assert.assertTrue(_hasPortletPreference(portletId));
			},
			() -> _deletePortletPreference(portletId),
			() -> _addPortletPreference(plid, portletId),
			OrphanReferencesDataCleanupUtil.class.getName(),
			LoggerTestUtil.INFO);
	}

	@Test
	public void testPortletPreferenceLinkedToNotFoundLiferayPortletIsDeleted()
		throws Exception {

		String portletId = "com.liferay." + RandomTestUtil.randomString();

		test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(
					messages.toString(),
					messages.contains(
						StringBundler.concat(
							"Table ",
							dbInspector.normalizeName("PortletPreferences"),
							", 1 row deleted because ",
							dbInspector.normalizeName("portletId"), " ",
							portletId, " was not found in column ",
							dbInspector.normalizeName("portletId"),
							" from table ",
							dbInspector.normalizeName(_TEMP_TABLE_NAME))));

				Assert.assertFalse(_hasPortletPreference(portletId));
			},
			() -> _deletePortletPreference(portletId),
			() -> _addPortletPreference(0, portletId),
			OrphanReferencesDataCleanupUtil.class.getName(),
			LoggerTestUtil.INFO);
	}

	@Test
	public void testPortletPreferenceLinkedToNotFoundLiferayPortletIsNotDeletedInReadonlyMode()
		throws Exception {

		_deletePortlets = false;

		String portletId = "com.liferay." + RandomTestUtil.randomString();

		test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(
					messages.toString(),
					messages.contains(
						StringBundler.concat(
							"Table ",
							dbInspector.normalizeName("PortletPreferences"),
							", 1 row should be deleted because ",
							dbInspector.normalizeName("portletId"), " ",
							portletId, " was not found in column ",
							dbInspector.normalizeName("portletId"),
							" from table ",
							dbInspector.normalizeName(_TEMP_TABLE_NAME))));

				Assert.assertTrue(_hasPortletPreference(portletId));
			},
			() -> _deletePortletPreference(portletId),
			() -> _addPortletPreference(0, portletId),
			OrphanReferencesDataCleanupUtil.class.getName(),
			LoggerTestUtil.INFO);
	}

	@Test
	public void testPortletPreferenceValueLinkedToNonexistentPortletPreferenceIsDeleted()
		throws Exception {

		long portletPreferencesId = RandomTestUtil.nextLong();

		test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(
					messages.toString(),
					messages.contains(
						StringBundler.concat(
							"Table ",
							dbInspector.normalizeName("PortletPreferenceValue"),
							", 1 row deleted because ",
							dbInspector.normalizeName("portletPreferencesId"),
							" ", portletPreferencesId,
							" was not found in column ",
							dbInspector.normalizeName("portletPreferencesId"),
							" from table ",
							dbInspector.normalizeName("PortletPreferences"))));

				Assert.assertFalse(
					_hasPortletPreferenceValue(portletPreferencesId));
			},
			() -> _deletePortletPreferenceValue(portletPreferencesId),
			() -> _addPortletPreferenceValue(portletPreferencesId),
			OrphanReferencesDataCleanupUtil.class.getName(),
			LoggerTestUtil.INFO);
	}

	@Test
	public void testPortletPreferenceValueLinkedToNonexistentPortletPreferenceIsNotDeletedInReadonlyMode()
		throws Exception {

		_deletePortlets = false;

		long portletPreferencesId = RandomTestUtil.nextLong();

		test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertFalse(
					messages.toString(),
					messages.contains(String.valueOf(portletPreferencesId)));

				Assert.assertTrue(
					_hasPortletPreferenceValue(portletPreferencesId));
			},
			() -> _deletePortletPreferenceValue(portletPreferencesId),
			() -> _addPortletPreferenceValue(portletPreferencesId),
			OrphanReferencesDataCleanupUtil.class.getName(),
			LoggerTestUtil.INFO);
	}

	@Test
	public void testUserScopedPortletPreferenceLinkedToLiferayPortletIsNotDeleted()
		throws Exception {

		List<Portlet> portlets = PortletLocalServiceUtil.getPortlets();

		Portlet portlet = portlets.get(0);

		String portletId =
			portlet.getPortletId() + "_USER_" + RandomTestUtil.randomString();

		test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(messages.toString(), messages.isEmpty());

				Assert.assertTrue(_hasPortletPreference(portletId));
			},
			() -> _deletePortletPreference(portletId),
			() -> _addPortletPreference(0, portletId),
			OrphanReferencesDataCleanupUtil.class.getName(),
			LoggerTestUtil.INFO);
	}

	@Test
	public void testUserScopedPortletPreferenceLinkedToNotFoundLiferayPortletIsDeleted()
		throws Exception {

		String portletId = StringBundler.concat(
			"com.liferay.", RandomTestUtil.randomString(), "_USER_",
			RandomTestUtil.randomString());

		test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(
					messages.toString(),
					messages.contains(
						StringBundler.concat(
							"Table ",
							dbInspector.normalizeName("PortletPreferences"),
							", 1 row deleted because ",
							dbInspector.normalizeName("portletId"), " ",
							portletId, " was not found in column ",
							dbInspector.normalizeName("portletId"),
							" from table ",
							dbInspector.normalizeName(_TEMP_TABLE_NAME))));

				Assert.assertFalse(_hasPortletPreference(portletId));
			},
			() -> _deletePortletPreference(portletId),
			() -> _addPortletPreference(0, portletId),
			OrphanReferencesDataCleanupUtil.class.getName(),
			LoggerTestUtil.INFO);
	}

	@Test
	public void testUserScopedPortletPreferenceLinkedToNotFoundLiferayPortletIsNotDeletedInReadonlyMode()
		throws Exception {

		_deletePortlets = false;

		String portletId = StringBundler.concat(
			"com.liferay.", RandomTestUtil.randomString(), "_USER_",
			RandomTestUtil.randomString());

		test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(
					messages.toString(),
					messages.contains(
						StringBundler.concat(
							"Table ",
							dbInspector.normalizeName("PortletPreferences"),
							", 1 row should be deleted because ",
							dbInspector.normalizeName("portletId"), " ",
							portletId, " was not found in column ",
							dbInspector.normalizeName("portletId"),
							" from table ",
							dbInspector.normalizeName(_TEMP_TABLE_NAME))));

				Assert.assertTrue(_hasPortletPreference(portletId));
			},
			() -> _deletePortletPreference(portletId),
			() -> _addPortletPreference(0, portletId),
			OrphanReferencesDataCleanupUtil.class.getName(),
			LoggerTestUtil.INFO);
	}

	@Test
	public void testVerifyDoesNotRunIfModulesNotStarted() throws Exception {
		AtomicReference<Bundle> bundleAtomicReference = new AtomicReference<>();

		test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(
					messages.toString(),
					messages.contains(
						"PortletPreferencesPostUpgradeDataCleanupProcess " +
							"cannot be executed because there are modules " +
								"that are inactive"));
			},
			() -> {
				Bundle bundle = bundleAtomicReference.get();

				if (bundle != null) {
					installBundle(bundle, SystemBundleUtil.getBundleContext());
				}
			},
			() -> bundleAtomicReference.set(
				uninstallBundle(
					SystemBundleUtil.getBundleContext(),
					"com.liferay.dynamic.data.mapping.service")));
	}

	@Override
	protected Object[] getPostUpgradeDataCleanupProcessArguments() {
		return new Object[] {connection, _deletePortlets, _portletLocalService};
	}

	@Override
	protected Class<?>[] getPostUpgradeDataCleanupProcessArgumentTypes() {
		return new Class<?>[] {
			Connection.class, boolean.class, PortletLocalService.class
		};
	}

	@Override
	protected String getPostUpgradeDataCleanupProcessClassName() {
		return "com.liferay.data.cleanup.internal.verify." +
			"PortletPreferencesPostUpgradeDataCleanupProcess";
	}

	private void _addLayoutRevision(long layoutRevisionId) throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"insert into LayoutRevision (mvccVersion, layoutRevisionId) " +
					"values (0, ?)")) {

			preparedStatement.setLong(1, layoutRevisionId);

			preparedStatement.executeUpdate();
		}
	}

	private void _addPortlet(String portletId) throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"insert into Portlet (mvccVersion, id_, portletId) values ",
					"(0, ", RandomTestUtil.nextLong(), ", ?)"))) {

			preparedStatement.setString(1, portletId);

			preparedStatement.executeUpdate();
		}
	}

	private void _addPortletPreference(long plid, String portletId)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"insert into PortletPreferences (mvccVersion, ",
					"ctCollectionId, portletPreferencesId, ownerType, plid, ",
					"portletId) values (0, 0, ", RandomTestUtil.nextLong(),
					", ", PortletKeys.PREFS_OWNER_TYPE_LAYOUT, ", ?, ?)"))) {

			preparedStatement.setLong(1, plid);
			preparedStatement.setString(2, portletId);

			preparedStatement.executeUpdate();
		}
	}

	private void _addPortletPreferenceValue(long portletPreferencesId)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"insert into PortletPreferenceValue (mvccVersion, ",
					"ctCollectionId, portletPreferenceValueId, ",
					"portletPreferencesId) values (0, 0, ",
					RandomTestUtil.nextLong(), ", ?)"))) {

			preparedStatement.setLong(1, portletPreferencesId);

			preparedStatement.executeUpdate();
		}
	}

	private void _deleteLayoutRevision(long layoutRevisionId) throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"delete from LayoutRevision where layoutRevisionId = ?")) {

			preparedStatement.setLong(1, layoutRevisionId);

			preparedStatement.executeUpdate();
		}
	}

	private void _deletePortlet(String portletId) throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"delete from Portlet where portletId = ?")) {

			preparedStatement.setString(1, portletId);

			preparedStatement.executeUpdate();
		}
	}

	private void _deletePortletPreference(String portletId) throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"delete from PortletPreferences where portletId = ?")) {

			preparedStatement.setString(1, portletId);

			preparedStatement.executeUpdate();
		}
	}

	private void _deletePortletPreferenceValue(long portletPreferencesId)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"delete from PortletPreferenceValue where " +
					"portletPreferencesId = ?")) {

			preparedStatement.setLong(1, portletPreferencesId);

			preparedStatement.executeUpdate();
		}
	}

	private boolean _hasPortlet(String portletId) throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select * from Portlet where portletId = ?")) {

			preparedStatement.setString(1, portletId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				return resultSet.next();
			}
		}
	}

	private boolean _hasPortletPreference(String portletId) throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select * from PortletPreferences where portletId = ?")) {

			preparedStatement.setString(1, portletId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				return resultSet.next();
			}
		}
	}

	private boolean _hasPortletPreferenceValue(long portletPreferencesId)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select * from PortletPreferenceValue where " +
					"portletPreferencesId = ?")) {

			preparedStatement.setLong(1, portletPreferencesId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				return resultSet.next();
			}
		}
	}

	private static final String _TEMP_TABLE_NAME = "TEMP_TABLE_PORTLET";

	private boolean _deletePortlets = true;

	@Inject
	private PortletLocalService _portletLocalService;

}