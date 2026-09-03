/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.cleanup.internal.verify.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.data.cleanup.util.OrphanReferencesDataCleanupUtil;
import com.liferay.portal.test.log.LogCapture;
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
public class ResourcePermissionPostupgradeDataCleanupProcessTest
	extends BasePostUpgradeDataCleanupProcessTestCase {

	@Test
	public void testLiferayPortletFoundResourcePermissionIsNotDeleted()
		throws Exception {

		List<Portlet> portlets = PortletLocalServiceUtil.getPortlets();
		long resourcePermissionId = RandomTestUtil.nextLong();

		Portlet portlet = portlets.get(0);

		test(
			logCapture -> {
				_assertResourcePermissionNotDeleted(
					logCapture, portlet.getPortletName());

				Assert.assertTrue(_hasResourcePermission(resourcePermissionId));
			},
			() -> _deleteResourcePermission(resourcePermissionId),
			() -> _addResourcePermission(
				portlet.getPortletName(), RandomTestUtil.nextLong(),
				resourcePermissionId, RandomTestUtil.nextLong()));
	}

	@Test
	public void testLiferayPortletNotFoundResourcePermissionIsDeleted()
		throws Exception {

		String portletName = "com_liferay_" + RandomTestUtil.randomString();
		long resourcePermissionId1 = RandomTestUtil.nextLong();
		long resourcePermissionId2 = RandomTestUtil.nextLong();

		test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(
					messages.toString(),
					messages.contains(
						StringBundler.concat(
							"Table ",
							dbInspector.normalizeName("ResourcePermission"),
							", 2 rows deleted because \"", portletName,
							"\" was not found")));

				Assert.assertFalse(
					_hasResourcePermission(resourcePermissionId1));
				Assert.assertFalse(
					_hasResourcePermission(resourcePermissionId2));
			},
			() -> {
				_deleteResourcePermission(resourcePermissionId1);
				_deleteResourcePermission(resourcePermissionId2);
			},
			() -> {
				_addResourcePermission(
					portletName, RandomTestUtil.nextLong(),
					resourcePermissionId1, RandomTestUtil.nextLong());
				_addResourcePermission(
					portletName, RandomTestUtil.nextLong(),
					resourcePermissionId2, RandomTestUtil.nextLong());
			});
	}

	@Test
	public void testNonliferayPortletResourcePermissionIsNotDeleted()
		throws Exception {

		String portletName =
			"com_test_not_liferay_" + RandomTestUtil.randomString();
		long resourcePermissionId = RandomTestUtil.nextLong();

		test(
			logCapture -> {
				_assertResourcePermissionNotDeleted(logCapture, portletName);

				Assert.assertTrue(_hasResourcePermission(resourcePermissionId));
			},
			() -> _deleteResourcePermission(resourcePermissionId),
			() -> _addResourcePermission(
				portletName, RandomTestUtil.nextLong(), resourcePermissionId,
				RandomTestUtil.nextLong()));
	}

	@Test
	public void testResourcePermissionDataCleanupPreupgradeProcessIsExecuted()
		throws Exception {

		String className = JournalArticle.class.getName();
		long primKeyId = RandomTestUtil.nextLong();
		long resourcePermissionId = RandomTestUtil.nextLong();

		test(
			logCapture -> {
				List<String> messages = logCapture.getMessages();

				Assert.assertTrue(
					messages.toString(),
					messages.contains(
						StringBundler.concat(
							"Table ",
							dbInspector.normalizeName("ResourcePermission"),
							", 1 row deleted because ",
							dbInspector.normalizeName("primKeyId"),
							StringPool.SPACE, primKeyId,
							" was not found in column ",
							dbInspector.normalizeName("resourcePrimKey"),
							" from table ",
							dbInspector.normalizeName("JournalArticle"))));

				Assert.assertFalse(
					_hasResourcePermission(resourcePermissionId));
			},
			() -> _deleteResourcePermission(resourcePermissionId),
			() -> _addResourcePermission(
				className, primKeyId, resourcePermissionId,
				RandomTestUtil.nextLong()),
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
						"ResourcePermissionPostupgradeDataCleanupProcess " +
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
		return new Object[] {connection, _resourcePermissionLocalService};
	}

	@Override
	protected Class<?>[] getPostUpgradeDataCleanupProcessArgumentTypes() {
		return new Class<?>[] {
			Connection.class, ResourcePermissionLocalService.class
		};
	}

	@Override
	protected String getPostUpgradeDataCleanupProcessClassName() {
		return "com.liferay.data.cleanup.internal.verify." +
			"ResourcePermissionPostupgradeDataCleanupProcess";
	}

	private void _addResourcePermission(
			String portletName, long primKeyId, long resourcePermissionId,
			long roleId)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"insert into ResourcePermission (mvccVersion, ",
					"ctCollectionId, resourcePermissionId, name, primKeyId, ",
					"roleId, scope) values (0, 0, ?, ?, ?, ?, ?)"))) {

			preparedStatement.setLong(1, resourcePermissionId);
			preparedStatement.setString(2, portletName);
			preparedStatement.setLong(3, primKeyId);
			preparedStatement.setLong(4, roleId);
			preparedStatement.setInt(5, ResourceConstants.SCOPE_INDIVIDUAL);

			preparedStatement.executeUpdate();
		}
	}

	private void _assertResourcePermissionNotDeleted(
		LogCapture logCapture, String portletName) {

		for (String message : logCapture.getMessages()) {
			Assert.assertFalse(
				message,
				message.contains("\"" + portletName + "\" was not found"));
		}
	}

	private void _deleteResourcePermission(long resourcePermissionId)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"delete from ResourcePermission where resourcePermissionId = " +
					"?")) {

			preparedStatement.setLong(1, resourcePermissionId);

			preparedStatement.executeUpdate();
		}
	}

	private boolean _hasResourcePermission(long resourcePermissionId)
		throws Exception {

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select * from ResourcePermission where resourcePermissionId " +
					"= ?")) {

			preparedStatement.setLong(1, resourcePermissionId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				return resultSet.next();
			}
		}
	}

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

}