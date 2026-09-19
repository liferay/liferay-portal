/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.friendly.url.internal.upgrade.v3_4_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.friendly.url.service.persistence.FriendlyURLEntryLocalizationPersistence;
import com.liferay.friendly.url.service.persistence.FriendlyURLEntryMappingPersistence;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.LayoutFriendlyURLLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.model.impl.LayoutFriendlyURLImpl;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class LayoutFriendlyURLEntryUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
		_privateLayoutClassNameId = _classNameLocalService.getClassNameId(
			_resourceActions.getCompositeModelName(
				Layout.class.getName(), Boolean.TRUE.toString()));
		_publicLayoutClassNameId = _classNameLocalService.getClassNameId(
			_resourceActions.getCompositeModelName(
				Layout.class.getName(), Boolean.FALSE.toString()));
	}

	@Test
	public void testUpgrade() throws Exception {
		Layout layout1 = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout1 = layout1.fetchDraftLayout();

		_assertFriendlyURLEntries(draftLayout1, layout1);

		Layout layout2 = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout2 = layout2.fetchDraftLayout();

		_deleteFriendlyURLEntries(draftLayout2, layout2);

		_assertUpgrade(draftLayout1, draftLayout2, layout1, layout2);
	}

	@Test
	public void testUpgradeDuplicateFriendlyURLEntryLocalization()
		throws Exception {

		try (Connection connection = DataAccess.getConnection()) {
			DB db = DBManagerUtil.getDB();
			DBInspector dbInspector = new DBInspector(connection);

			Layout layout = null;

			try {
				String name = RandomTestUtil.randomString();

				layout = LayoutTestUtil.addTypePortletLayout(
					_group.getGroupId(), false,
					HashMapBuilder.put(
						LocaleUtil.US, name
					).build(),
					HashMapBuilder.put(
						LocaleUtil.US,
						_friendlyURLNormalizer.normalizeWithEncoding(
							StringPool.SLASH + name)
					).build());

				_insertLayoutFriendlyURLEntry(
					layout,
					_friendlyURLNormalizer.normalizeWithEncoding(
						StringPool.SLASH + name),
					LanguageUtil.getLanguageId(LocaleUtil.SPAIN));

				db.runSQL(
					"create unique index IX_8AB5CAE on " +
						"FriendlyURLEntryLocalization (groupId, classNameId, " +
							"urlTitle[$COLUMN_LENGTH:255$])");
				db.runSQL(
					"create unique index IX_C753170C on " +
						"FriendlyURLEntryLocalization (groupId, classNameId, " +
							"urlTitle[$COLUMN_LENGTH:255$], ctCollectionId)");

				_entityCache.clearCache(LayoutFriendlyURLImpl.class);
				_finderCache.clearCache(LayoutFriendlyURLImpl.class);

				_assertUpgrade(layout);
			}
			finally {
				if (dbInspector.hasIndex(
						"FriendlyURLEntryLocalization", "IX_8AB5CAE")) {

					db.runSQL(
						"drop index IX_8AB5CAE on " +
							"FriendlyURLEntryLocalization");
				}

				if (dbInspector.hasIndex(
						"FriendlyURLEntryLocalization", "IX_C753170C")) {

					db.runSQL(
						"drop index IX_C753170C on " +
							"FriendlyURLEntryLocalization");
				}

				if (layout != null) {
					_layoutLocalService.deleteLayout(layout);
				}
			}
		}
	}

	@Test
	public void testUpgradeFriendlyURLEntryLocalizationMissingForSomeLanguage()
		throws Exception {

		Layout privateLayout1 = _addTypePortletLayout(true);
		Layout publicLayout1 = _addTypePortletLayout(false);

		_assertFriendlyURLEntries(privateLayout1, publicLayout1);

		Layout privateLayout2 = _addTypePortletLayout(true);
		Layout publicLayout2 = _addTypePortletLayout(false);

		_deleteFriendlyURLEntryLocalization(
			privateLayout2, LocaleUtil.toLanguageId(LocaleUtil.US));
		_deleteFriendlyURLEntryLocalization(
			publicLayout2, LocaleUtil.toLanguageId(LocaleUtil.SPAIN));

		_assertUpgrade(
			privateLayout1, publicLayout1, privateLayout2, publicLayout2);
	}

	@Test
	public void testUpgradeIsIdempotent() throws Exception {
		Layout layout1 = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout1 = layout1.fetchDraftLayout();

		_assertFriendlyURLEntries(draftLayout1, layout1);

		Layout layout2 = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout2 = layout2.fetchDraftLayout();

		_deleteFriendlyURLEntries(draftLayout2, layout2);

		long expectedFriendlyURLEntriesCount =
			_getRowsCount("FriendlyURLEntry") + 2;
		long expectedFriendlyURLEntryMappingsCount =
			_getRowsCount("FriendlyURLEntryMapping") + 2;
		long expectedFriendlyURLEntryLocalizationsCount =
			_getRowsCount("FriendlyURLEntryLocalization") + 2;

		_assertUpgrade(draftLayout1, draftLayout2, layout1, layout2);

		Assert.assertEquals(
			expectedFriendlyURLEntriesCount, _getRowsCount("FriendlyURLEntry"));
		Assert.assertEquals(
			expectedFriendlyURLEntryMappingsCount,
			_getRowsCount("FriendlyURLEntryMapping"));
		Assert.assertEquals(
			expectedFriendlyURLEntryLocalizationsCount,
			_getRowsCount("FriendlyURLEntryLocalization"));

		_assertUpgrade(draftLayout1, draftLayout2, layout1, layout2);

		Assert.assertEquals(
			expectedFriendlyURLEntriesCount, _getRowsCount("FriendlyURLEntry"));
		Assert.assertEquals(
			expectedFriendlyURLEntryMappingsCount,
			_getRowsCount("FriendlyURLEntryMapping"));
		Assert.assertEquals(
			expectedFriendlyURLEntryLocalizationsCount,
			_getRowsCount("FriendlyURLEntryLocalization"));
	}

	@Test
	public void testUpgradeNoFriendlyURLEntryLocalization() throws Exception {
		Layout layout1 = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout1 = layout1.fetchDraftLayout();

		_assertFriendlyURLEntries(draftLayout1, layout1);

		Layout layout2 = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout2 = layout2.fetchDraftLayout();

		_deleteFriendlyURLEntryLocalizations(draftLayout2, layout2);

		_assertUpgrade(draftLayout1, draftLayout2, layout1, layout2);
	}

	@Test
	public void testUpgradeNoFriendlyURLEntryMapping() throws Exception {
		Layout layout1 = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout1 = layout1.fetchDraftLayout();

		_assertFriendlyURLEntries(draftLayout1, layout1);

		Layout layout2 = LayoutTestUtil.addTypeContentLayout(_group);

		Layout draftLayout2 = layout2.fetchDraftLayout();

		_deleteFriendlyURLEntryMappings(draftLayout2, layout2);

		_assertUpgrade(draftLayout1, draftLayout2, layout1, layout2);
	}

	private Layout _addTypePortletLayout(boolean privateLayout)
		throws Exception {

		return LayoutTestUtil.addTypePortletLayout(
			_group.getGroupId(), privateLayout,
			HashMapBuilder.put(
				LocaleUtil.SPAIN, RandomTestUtil.randomString()
			).put(
				LocaleUtil.US, RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.SPAIN,
				_friendlyURLNormalizer.normalizeWithEncoding(
					StringPool.SLASH + RandomTestUtil.randomString())
			).put(
				LocaleUtil.US,
				_friendlyURLNormalizer.normalizeWithEncoding(
					StringPool.SLASH + RandomTestUtil.randomString())
			).build());
	}

	private void _assertFriendlyURLEntries(Layout... layouts) throws Exception {
		for (Layout layout : layouts) {
			_assertFriendlyURLEntry(layout, _getFriendlyURLEntry(layout));
		}
	}

	private void _assertFriendlyURLEntry(
			Layout layout, FriendlyURLEntry friendlyURLEntry)
		throws Exception {

		String[] availableLanguageIds =
			friendlyURLEntry.getAvailableLanguageIds();

		Assert.assertTrue(
			availableLanguageIds.toString(),
			ArrayUtil.containsAll(
				availableLanguageIds, layout.getAvailableLanguageIds()));

		for (String languageId : layout.getAvailableLanguageIds()) {
			FriendlyURLEntryLocalization friendlyURLEntryLocalization =
				_friendlyURLEntryLocalService.getFriendlyURLEntryLocalization(
					friendlyURLEntry.getFriendlyURLEntryId(), languageId);

			Assert.assertNotNull(friendlyURLEntryLocalization);

			Assert.assertEquals(
				layout.getFriendlyURL(LocaleUtil.fromLanguageId(languageId)),
				friendlyURLEntryLocalization.getUrlTitle());
		}
	}

	private void _assertUpgrade(Layout... layouts) throws Exception {
		_runUpgrade();

		_assertFriendlyURLEntries(layouts);
	}

	private void _deleteFriendlyURLEntries(Layout... layouts) throws Exception {
		for (Layout layout : layouts) {
			_deleteFriendlyURLEntry(layout);
		}
	}

	private void _deleteFriendlyURLEntry(Layout layout) throws Exception {
		FriendlyURLEntry friendlyURLEntry = _getFriendlyURLEntry(layout);

		_assertFriendlyURLEntry(layout, friendlyURLEntry);

		_friendlyURLEntryLocalService.deleteFriendlyURLEntry(friendlyURLEntry);

		List<FriendlyURLEntry> friendlyURLEntries =
			_friendlyURLEntryLocalService.getFriendlyURLEntries(
				layout.getGroupId(), friendlyURLEntry.getClassNameId(),
				layout.getPlid());

		Assert.assertEquals(
			friendlyURLEntries.toString(), 0, friendlyURLEntries.size());
	}

	private void _deleteFriendlyURLEntryLocalization(Layout layout)
		throws Exception {

		FriendlyURLEntry friendlyURLEntry = _getFriendlyURLEntry(layout);

		String[] availableLanguageIds =
			friendlyURLEntry.getAvailableLanguageIds();

		Assert.assertTrue(ArrayUtil.isNotEmpty(availableLanguageIds));

		List<FriendlyURLEntryLocalization> friendlyURLEntryLocalizations =
			_friendlyURLEntryLocalService.getFriendlyURLEntryLocalizations(
				friendlyURLEntry.getFriendlyURLEntryId());

		Assert.assertEquals(
			friendlyURLEntryLocalizations.toString(),
			availableLanguageIds.length, friendlyURLEntryLocalizations.size());

		for (FriendlyURLEntryLocalization friendlyURLEntryLocalization :
				friendlyURLEntryLocalizations) {

			_runSQL(
				StringBundler.concat(
					"delete from FriendlyURLEntryLocalization where ",
					"friendlyURLEntryLocalizationId = ",
					friendlyURLEntryLocalization.
						getFriendlyURLEntryLocalizationId()));
		}
	}

	private void _deleteFriendlyURLEntryLocalization(
			Layout layout, String languageId)
		throws Exception {

		FriendlyURLEntry friendlyURLEntry = _getFriendlyURLEntry(layout);

		String[] availableLanguageIds =
			friendlyURLEntry.getAvailableLanguageIds();

		Assert.assertTrue(ArrayUtil.isNotEmpty(availableLanguageIds));

		Assert.assertTrue(ArrayUtil.contains(availableLanguageIds, languageId));

		List<FriendlyURLEntryLocalization> friendlyURLEntryLocalizations =
			_friendlyURLEntryLocalService.getFriendlyURLEntryLocalizations(
				friendlyURLEntry.getFriendlyURLEntryId());

		Assert.assertEquals(
			friendlyURLEntryLocalizations.toString(),
			availableLanguageIds.length, friendlyURLEntryLocalizations.size());

		FriendlyURLEntryLocalization friendlyURLEntryLocalization =
			_friendlyURLEntryLocalService.getFriendlyURLEntryLocalization(
				friendlyURLEntry.getFriendlyURLEntryId(), languageId);

		_runSQL(
			StringBundler.concat(
				"delete from FriendlyURLEntryLocalization where ",
				"friendlyURLEntryLocalizationId = ",
				friendlyURLEntryLocalization.
					getFriendlyURLEntryLocalizationId()));
	}

	private void _deleteFriendlyURLEntryLocalizations(Layout... layouts)
		throws Exception {

		for (Layout layout : layouts) {
			_deleteFriendlyURLEntryLocalization(layout);
		}

		_multiVMPool.clear();
	}

	private void _deleteFriendlyURLEntryMapping(Layout layout)
		throws Exception {

		FriendlyURLEntry friendlyURLEntry = _getFriendlyURLEntry(layout);

		_runSQL(
			StringBundler.concat(
				"delete from FriendlyURLEntryMapping where friendlyURLEntryId ",
				"= ", friendlyURLEntry.getFriendlyURLEntryId()));
	}

	private void _deleteFriendlyURLEntryMappings(Layout... layouts)
		throws Exception {

		for (Layout layout : layouts) {
			_deleteFriendlyURLEntryLocalization(layout);
			_deleteFriendlyURLEntryMapping(layout);
		}

		_multiVMPool.clear();
	}

	private FriendlyURLEntry _getFriendlyURLEntry(Layout layout)
		throws Exception {

		long classNameId = _publicLayoutClassNameId;

		if (layout.isPrivateLayout()) {
			classNameId = _privateLayoutClassNameId;
		}

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
				classNameId, layout.getPlid());

		Assert.assertNotNull(friendlyURLEntry);

		return friendlyURLEntry;
	}

	private long _getRowsCount(String tableName) throws Exception {
		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"select count(*) as count from " + tableName);

			ResultSet resultSet = preparedStatement.executeQuery()) {

			if (resultSet.next()) {
				return resultSet.getLong("count");
			}
		}

		return 0;
	}

	private void _insertLayoutFriendlyURLEntry(
			Layout layout, String friendlyURL, String languageId)
		throws Exception {

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"insert into LayoutFriendlyURL (mvccVersion, ",
					"ctCollectionId, uuid_, layoutFriendlyURLId, groupId, ",
					"companyId, userId, userName, createDate, modifiedDate, ",
					"plid, privateLayout, friendlyURL, languageId, ",
					"lastPublishDate) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ",
					"?, ?, ?, ?, ?)"))) {

			preparedStatement.setLong(1, 0);
			preparedStatement.setLong(2, 0);
			preparedStatement.setString(3, PortalUUIDUtil.generate());
			preparedStatement.setLong(4, _counterLocalService.increment());
			preparedStatement.setLong(5, _group.getGroupId());
			preparedStatement.setLong(6, _group.getCompanyId());
			preparedStatement.setLong(7, TestPropsValues.getUserId());
			preparedStatement.setString(8, null);
			preparedStatement.setTimestamp(9, timestamp);
			preparedStatement.setTimestamp(10, timestamp);
			preparedStatement.setLong(11, layout.getPlid());
			preparedStatement.setBoolean(12, layout.isPrivateLayout());
			preparedStatement.setString(13, friendlyURL);
			preparedStatement.setString(14, languageId);
			preparedStatement.setTimestamp(15, null);

			preparedStatement.executeUpdate();
		}
	}

	private void _runSQL(String sql) throws Exception {
		DB db = DBManagerUtil.getDB();

		db.runSQL(sql);
	}

	private void _runUpgrade() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.WARN)) {

			UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
				_upgradeStepRegistrator, _CLASS_NAME);

			upgradeProcess.upgrade();

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 0, logEntries.size());

			_multiVMPool.clear();
		}
	}

	private static final String _CLASS_NAME =
		"com.liferay.friendly.url.internal.upgrade.v3_4_1." +
			"LayoutFriendlyURLEntryUpgradeProcess";

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CounterLocalService _counterLocalService;

	@Inject
	private EntityCache _entityCache;

	@Inject
	private FinderCache _finderCache;

	@Inject
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Inject
	private FriendlyURLEntryLocalizationPersistence
		_friendlyURLEntryLocalizationPersistence;

	@Inject
	private FriendlyURLEntryMappingPersistence
		_friendlyURLEntryMappingPersistence;

	@Inject
	private FriendlyURLNormalizer _friendlyURLNormalizer;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutFriendlyURLLocalService _layoutFriendlyURLLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private MultiVMPool _multiVMPool;

	private long _privateLayoutClassNameId;
	private long _publicLayoutClassNameId;

	@Inject
	private ResourceActions _resourceActions;

	@Inject(
		filter = "(&(component.name=com.liferay.friendly.url.internal.upgrade.registry.FriendlyURLServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}