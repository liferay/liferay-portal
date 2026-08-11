/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.test.util.BaseCTUpgradeProcessTestCase;
import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.db.index.IndexUpdaterUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.IndexMetadata;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.v7_4_x.LayoutDuplicateExternalReferenceCodeUpgradeProcess;

import java.sql.Connection;
import java.sql.PreparedStatement;

import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Georgel Pop
 */
@RunWith(Arquillian.class)
public class LayoutDuplicateExternalReferenceCodeUpgradeProcessTest
	extends BaseCTUpgradeProcessTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_connection = DataAccess.getConnection();

		_dbInspector = new DBInspector(_connection);

		_group = GroupTestUtil.addGroup();
	}

	@After
	public void tearDown() throws Exception {
		try {
			for (IndexMetadata indexMetadata : _indexMetadatas) {
				if (!_dbInspector.hasIndex(
						"Layout", indexMetadata.getIndexName())) {

					IndexUpdaterUtil.updatePortalIndexes();

					break;
				}
			}
		}
		finally {
			DataAccess.cleanUp(_connection);
		}
	}

	@Override
	@Test
	public void testMissingCtCollectionId() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				IndexUpdaterUtil.class.getName(), LoggerTestUtil.OFF)) {

			super.testMissingCtCollectionId();
		}
	}

	@Test
	@TestInfo("LPD-99950")
	public void testUpgrade() throws Exception {
		String externalReferenceCode = PortalUUIDUtil.generate();

		Layout otherGroupExternalReferenceCodeLayout = _addLayout(
			GroupTestUtil.addGroup(), externalReferenceCode, false);
		Layout renamedExternalReferenceCodeLayout1 = _addLayout(
			_group, externalReferenceCode, false);

		Layout renamedExternalReferenceCodeLayout2 = _addLayout(
			_group, externalReferenceCode, false);

		Layout reservedExternalReferenceCodeLayout = _addLayout(
			_group,
			String.valueOf(renamedExternalReferenceCodeLayout2.getPlid()),
			false);

		Layout uniqueExternalReferenceCodeLayout =
			LayoutTestUtil.addTypePortletLayout(_group, false);

		Layout maxPlidExternalReferenceCodeLayout = _addLayout(
			_group, externalReferenceCode, true);

		long plid = CounterLocalServiceUtil.increment(Layout.class.getName());

		runUpgrade();

		Assert.assertEquals(
			externalReferenceCode,
			_getExternalReferenceCode(
				maxPlidExternalReferenceCodeLayout.getPlid()));
		Assert.assertEquals(
			externalReferenceCode,
			_getExternalReferenceCode(
				otherGroupExternalReferenceCodeLayout.getPlid()));
		Assert.assertEquals(
			String.valueOf(renamedExternalReferenceCodeLayout1.getPlid()),
			_getExternalReferenceCode(
				renamedExternalReferenceCodeLayout1.getPlid()));
		Assert.assertEquals(
			String.valueOf(plid + 1),
			_getExternalReferenceCode(
				renamedExternalReferenceCodeLayout2.getPlid()));
		Assert.assertEquals(
			String.valueOf(renamedExternalReferenceCodeLayout2.getPlid()),
			_getExternalReferenceCode(
				reservedExternalReferenceCodeLayout.getPlid()));
		Assert.assertEquals(
			uniqueExternalReferenceCodeLayout.getExternalReferenceCode(),
			_getExternalReferenceCode(
				uniqueExternalReferenceCodeLayout.getPlid()));

		IndexUpdaterUtil.updatePortalIndexes();

		for (IndexMetadata indexMetadata : _indexMetadatas) {
			Assert.assertTrue(
				_dbInspector.hasIndex("Layout", indexMetadata.getIndexName()));
		}
	}

	@Override
	protected CTModel<?> addCTModel() throws Exception {
		String externalReferenceCode = PortalUUIDUtil.generate();

		Layout layout = _addLayout(_group, externalReferenceCode, false);

		_addLayout(_group, externalReferenceCode, true);

		_entityCache.clearCache();
		_multiVMPool.clear();

		return _layoutLocalService.getLayout(layout.getPlid());
	}

	@Override
	protected void deleteCTModel(long primaryKey) throws Exception {
		_layoutLocalService.deleteLayout(
			_layoutLocalService.getLayout(primaryKey));
	}

	@Override
	protected CTService<?> getCTService() {
		return _layoutLocalService;
	}

	@Override
	protected void runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess =
			new LayoutDuplicateExternalReferenceCodeUpgradeProcess();

		upgradeProcess.upgrade();

		_entityCache.clearCache();
		_multiVMPool.clear();
	}

	@Override
	protected CTModel<?> updateCTModel(CTModel<?> ctModel) throws Exception {
		Layout layout = (Layout)ctModel;

		layout.setPriority(RandomTestUtil.randomInt());

		return _layoutLocalService.updateLayout(layout);
	}

	private Layout _addLayout(
			Group group, String externalReferenceCode, boolean privateLayout)
		throws Exception {

		if (_indexMetadatas.isEmpty()) {
			_indexMetadatas = _dropUniqueIndexes(
				"Layout", "externalReferenceCode");
		}

		Layout layout = LayoutTestUtil.addTypePortletLayout(
			group, privateLayout);

		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				"update Layout set externalReferenceCode = ? where plid = ?")) {

			preparedStatement.setString(1, externalReferenceCode);
			preparedStatement.setLong(2, layout.getPlid());

			preparedStatement.executeUpdate();
		}

		return layout;
	}

	private List<IndexMetadata> _dropUniqueIndexes(
			String tableName, String columnName)
		throws Exception {

		DB db = DBManagerUtil.getDB();

		List<IndexMetadata> indexMetadatas = db.getIndexMetadatas(
			_connection, tableName, columnName, true);

		for (IndexMetadata indexMetadata : indexMetadatas) {
			db.runSQL(_connection, indexMetadata.getDropSQL());
		}

		return indexMetadatas;
	}

	private String _getExternalReferenceCode(long plid) throws Exception {
		Layout layout = _layoutLocalService.getLayout(plid);

		return layout.getExternalReferenceCode();
	}

	private Connection _connection;
	private DBInspector _dbInspector;

	@Inject
	private EntityCache _entityCache;

	@DeleteAfterTestRun
	private Group _group;

	private List<IndexMetadata> _indexMetadatas = Collections.emptyList();

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private MultiVMPool _multiVMPool;

}