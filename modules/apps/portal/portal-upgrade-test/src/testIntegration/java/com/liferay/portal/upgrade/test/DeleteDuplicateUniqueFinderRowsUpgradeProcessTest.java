/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.db.index.IndexUpdaterUtil;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.IndexMetadata;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.model.PortalPreferences;
import com.liferay.portal.kernel.model.PortletItem;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.PortalPreferencesLocalService;
import com.liferay.portal.kernel.service.PortletItemLocalService;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.upgrade.DeleteDuplicateUniqueFinderRowsUpgradeProcess;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.social.kernel.model.SocialActivitySetting;
import com.liferay.social.kernel.service.SocialActivitySettingLocalService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jorge Avalos
 */
@RunWith(Arquillian.class)
public class DeleteDuplicateUniqueFinderRowsUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_connection = DataAccess.getConnection();
		_db = DBManagerUtil.getDB();

		_dbInspector = new DBInspector(_connection);
	}

	@Test
	public void testUpgradePortalPreferences() throws Exception {
		List<IndexMetadata> indexMetadatas = _dropUniqueIndexes(
			"PortalPreferences", "ownerId");

		PortalPreferences portalPreferences1 =
			_portalPreferencesLocalService.createPortalPreferences(1);

		portalPreferences1.setOwnerId(1);
		portalPreferences1.setOwnerType(1);

		List<PortalPreferences> portalPreferencesList = new ArrayList<>();

		portalPreferencesList.add(
			_portalPreferencesLocalService.addPortalPreferences(
				portalPreferences1));

		PortalPreferences portalPreferences2 =
			portalPreferences1.cloneWithOriginalValues();

		portalPreferences2.setPortalPreferencesId(2);

		portalPreferencesList.add(
			_portalPreferencesLocalService.addPortalPreferences(
				portalPreferences2));

		_runUpgrade(
			"PortalPreferences", new String[] {"ownerType", "ownerId"},
			"portalPreferencesId asc");

		Collections.sort(portalPreferencesList, Collections.reverseOrder());

		Assert.assertEquals(
			portalPreferencesList.get(0),
			_portalPreferencesLocalService.fetchPortalPreferences(1, 1));

		_assertIndexes("PortalPreferences", indexMetadatas);
	}

	@Test
	public void testUpgradePortletItem() throws Exception {
		List<IndexMetadata> indexMetadatas = _dropUniqueIndexes(
			"PortletItem", "groupId");

		PortletItem portletItem1 = _portletItemLocalService.createPortletItem(
			1);

		portletItem1.setGroupId(1);
		portletItem1.setName("Test");
		portletItem1.setPortletId("1");
		portletItem1.setClassNameId(
			ClassNameLocalServiceUtil.getClassNameId(
				PortletItem.class.getName()));

		List<PortletItem> portletItems = new ArrayList<>();

		portletItems.add(_portletItemLocalService.addPortletItem(portletItem1));

		PortletItem portletItem2 = portletItem1.cloneWithOriginalValues();

		portletItem2.setPortletItemId(2);

		portletItems.add(_portletItemLocalService.addPortletItem(portletItem2));

		_runUpgrade(
			"PortletItem",
			new String[] {"groupId", "classNameId", "portletId", "name"},
			"portletItemId asc");

		Collections.sort(portletItems, Collections.reverseOrder());

		Assert.assertEquals(
			portletItems.get(0),
			_portletItemLocalService.getPortletItem(
				1, "Test", "1", PortletItem.class.getName()));

		_assertIndexes("PortletItem", indexMetadatas);
	}

	@Test
	public void testUpgradeSocialActivitySetting() throws Exception {
		List<IndexMetadata> indexMetadatas = _dropUniqueIndexes(
			"SocialActivitySetting", "groupId");

		SocialActivitySetting socialActivitySetting1 =
			_socialActivitySettingLocalService.createSocialActivitySetting(1);

		socialActivitySetting1.setGroupId(1);
		socialActivitySetting1.setClassNameId(1);
		socialActivitySetting1.setActivityType(1);
		socialActivitySetting1.setName("Test");

		List<SocialActivitySetting> socialActivitySettings = new ArrayList<>();

		socialActivitySettings.add(
			_socialActivitySettingLocalService.addSocialActivitySetting(
				socialActivitySetting1));

		SocialActivitySetting socialActivitySetting2 =
			socialActivitySetting1.cloneWithOriginalValues();

		socialActivitySetting2.setActivitySettingId(2);

		socialActivitySettings.add(
			_socialActivitySettingLocalService.addSocialActivitySetting(
				socialActivitySetting2));

		_runUpgrade(
			"SocialActivitySetting",
			new String[] {"groupId", "classNameId", "activityType", "name"},
			"activitySettingId asc");

		Collections.sort(socialActivitySettings, Collections.reverseOrder());

		Assert.assertEquals(
			socialActivitySettings.get(0),
			_socialActivitySettingLocalService.getSocialActivitySetting(2));

		_assertIndexes("SocialActivitySetting", indexMetadatas);
	}

	@Test
	public void testUpgradeTicket() throws Exception {
		List<IndexMetadata> indexMetadatas = _dropUniqueIndexes(
			"Ticket", "key_");

		Ticket ticket1 = _ticketLocalService.createTicket(1);

		ticket1.setKey("key_");

		List<Ticket> tickets = new ArrayList<>();

		tickets.add(_ticketLocalService.addTicket(ticket1));

		Ticket ticket2 = ticket1.cloneWithOriginalValues();

		ticket2.setTicketId(2);

		tickets.add(_ticketLocalService.addTicket(ticket2));

		_runUpgrade("Ticket", new String[] {"key_"}, "ticketId asc");

		Collections.sort(tickets, Collections.reverseOrder());

		Assert.assertEquals(
			tickets.get(0), _ticketLocalService.fetchTicket("key_"));

		_assertIndexes("Ticket", indexMetadatas);
	}

	private void _assertCount(
			String tableName, String[] columnNames, boolean duplicatesRemoved)
		throws Exception {

		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				StringBundler.concat(
					"select count(*) from ", tableName, " group by ",
					String.join(", ", columnNames), " having count(*) > 1"));
			ResultSet resultSet = preparedStatement.executeQuery()) {

			if (!duplicatesRemoved) {
				Assert.assertTrue(resultSet.next());

				Assert.assertEquals(2, resultSet.getInt(1));

				return;
			}

			Assert.assertFalse(resultSet.next());
		}
	}

	private void _assertIndexes(
			String tableName, List<IndexMetadata> indexMetadatas)
		throws Exception {

		IndexUpdaterUtil.updatePortalIndexes();

		for (IndexMetadata indexMetadata : indexMetadatas) {
			Assert.assertTrue(
				_dbInspector.hasIndex(tableName, indexMetadata.getIndexName()));
		}
	}

	private List<IndexMetadata> _dropUniqueIndexes(
			String tableName, String columnName)
		throws Exception {

		List<IndexMetadata> indexMetadatas = _db.getIndexMetadatas(
			_connection, tableName, columnName, true);

		for (IndexMetadata indexMetadata : indexMetadatas) {
			_db.runSQL(_connection, indexMetadata.getDropSQL());
		}

		return indexMetadatas;
	}

	private void _runUpgrade(
			String tableName, String[] columnNames, String orderByClause)
		throws Exception {

		_assertCount(tableName, columnNames, false);

		DeleteDuplicateUniqueFinderRowsUpgradeProcess upgradeProcess =
			new DeleteDuplicateUniqueFinderRowsUpgradeProcess(
				tableName, columnNames, orderByClause);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.kernel.dao.db." +
					"DuplicateUniqueFinderRowsCleaner",
				LoggerTestUtil.OFF)) {

			upgradeProcess.upgrade();
		}
		finally {
			EntityCacheUtil.clearCache();
		}

		_assertCount(tableName, columnNames, true);
	}

	@Inject
	private static CompanyLocalService _companyLocalService;

	private static Connection _connection;
	private static DB _db;
	private static DBInspector _dbInspector;

	@Inject
	private static PortalPreferencesLocalService _portalPreferencesLocalService;

	@Inject
	private static PortletItemLocalService _portletItemLocalService;

	@Inject
	private static SocialActivitySettingLocalService
		_socialActivitySettingLocalService;

	@Inject
	private static TicketLocalService _ticketLocalService;

}