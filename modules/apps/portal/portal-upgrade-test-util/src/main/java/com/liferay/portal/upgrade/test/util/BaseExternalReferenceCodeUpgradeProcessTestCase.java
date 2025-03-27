/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.test.util;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.IndexMetadata;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ExternalReferenceCodeModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.List;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Rubén Pulido
 */
public abstract class BaseExternalReferenceCodeUpgradeProcessTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		dataSource = InfrastructureUtil.getDataSource();
		db = DBManagerUtil.getDB();
	}

	@Before
	public void setUp() throws Exception {
		group = GroupTestUtil.addGroup();

		serviceContext = ServiceContextTestUtil.getServiceContext(
			group.getGroupId());

		ServiceContextThreadLocal.pushServiceContext(serviceContext);
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testUpgradeProcess() throws Exception {
		for (String tableName : getTableNames()) {
			List<IndexMetadata> indexMetadatas = _dropIndexes(tableName);

			try {
				ExternalReferenceCodeModel[] externalReferenceCodeModels =
					addExternalReferenceCodeModels(tableName);

				_prepareDatabaseForUpgradeProcess(
					externalReferenceCodeModels,
					_hasColumn(tableName, "groupId"), tableName);

				_runUpgrade();

				_assertDatabaseAfterUpgradeProcess(
					externalReferenceCodeModels, tableName);
			}
			finally {
				_addIndexes(indexMetadatas);
			}
		}
	}

	protected abstract ExternalReferenceCodeModel[]
			addExternalReferenceCodeModels(String tableName)
		throws PortalException;

	protected void assertExternalReferenceCode(
			String[] externalReferenceCodes, boolean hasGroupIdColumn,
			String tableName)
		throws Exception {

		String sql = StringBundler.concat(
			"select 1 from ", tableName, " where externalReferenceCode in ('",
			ArrayUtil.toString(
				externalReferenceCodes, StringPool.BLANK, "', '"),
			"')");

		if (hasGroupIdColumn) {
			sql = sql + " AND groupId = ?";
		}

		try (Connection connection = dataSource.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				sql)) {

			if (hasGroupIdColumn) {
				preparedStatement.setLong(1, group.getGroupId());
			}

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				Assert.assertFalse(resultSet.next());
			}
		}
	}

	protected abstract ExternalReferenceCodeModel
			fetchExternalReferenceCodeModel(
				ExternalReferenceCodeModel externalReferenceCodeModel,
				String tableName)
		throws PortalException;

	protected String getExternalReferenceCode(
		ExternalReferenceCodeModel externalReferenceCodeModel,
		String tableName) {

		if (externalReferenceCodeModel instanceof StagedModel) {
			StagedModel stagedModel = (StagedModel)externalReferenceCodeModel;

			return stagedModel.getUuid();
		}

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected abstract String[] getTableNames();

	protected UpgradeProcess getUpgradeProcess() {
		UpgradeProcess[] upgradeProcesses = UpgradeTestUtil.getUpgradeSteps(
			getUpgradeStepRegistrator(), getVersion());

		return upgradeProcesses[0];
	}

	protected abstract UpgradeStepRegistrator getUpgradeStepRegistrator();

	protected abstract Version getVersion();

	protected void updateExternalReferenceCode(
			String[] externalReferenceCodes, boolean hasGroupIdColumn,
			String tableName)
		throws Exception {

		String sql = StringBundler.concat(
			"update ", tableName, " set externalReferenceCode = null where ",
			"externalReferenceCode in ('",
			ArrayUtil.toString(
				externalReferenceCodes, StringPool.BLANK, "', '"),
			"')");

		if (hasGroupIdColumn) {
			sql = StringBundler.concat(
				sql, " and groupId = ", group.getGroupId());
		}

		db.runSQL(sql);

		entityCache.clearCache();
		multiVMPool.clear();
	}

	protected static DataSource dataSource;
	protected static DB db;

	@Inject
	protected EntityCache entityCache;

	@DeleteAfterTestRun
	protected Group group;

	@Inject
	protected MultiVMPool multiVMPool;

	protected ServiceContext serviceContext;

	private void _addIndexes(List<IndexMetadata> indexMetadatas)
		throws Exception {

		try (Connection connection = dataSource.getConnection()) {
			if (ListUtil.isNotEmpty(indexMetadatas)) {
				db.addIndexes(connection, indexMetadatas);
			}
		}
	}

	private void _assertDatabaseAfterUpgradeProcess(
			ExternalReferenceCodeModel[] externalReferenceCodeModels,
			String tableName)
		throws Exception {

		for (ExternalReferenceCodeModel externalReferenceCodeModel :
				externalReferenceCodeModels) {

			ExternalReferenceCodeModel updatedExternalReferenceCodeModel =
				fetchExternalReferenceCodeModel(
					externalReferenceCodeModel, tableName);

			Assert.assertTrue(
				updatedExternalReferenceCodeModel.getExternalReferenceCode(),
				Validator.isNotNull(
					updatedExternalReferenceCodeModel.
						getExternalReferenceCode()));
			Assert.assertEquals(
				getExternalReferenceCode(
					updatedExternalReferenceCodeModel, tableName),
				updatedExternalReferenceCodeModel.getExternalReferenceCode());
		}
	}

	private List<IndexMetadata> _dropIndexes(String tableName)
		throws Exception {

		try (Connection connection = dataSource.getConnection()) {
			return db.dropIndexes(
				connection, tableName, "externalReferenceCode");
		}
	}

	private boolean _hasColumn(String tableName, String columnName)
		throws Exception {

		try (Connection connection = DataAccess.getConnection()) {
			DBInspector dbInspector = new DBInspector(connection);

			return dbInspector.hasColumn(tableName, columnName);
		}
	}

	private void _prepareDatabaseForUpgradeProcess(
			ExternalReferenceCodeModel[] externalReferenceCodeModels,
			boolean hasGroupIdColumn, String tableName)
		throws Exception {

		String[] externalReferenceCodes = TransformUtil.transform(
			externalReferenceCodeModels,
			ExternalReferenceCodeModel::getExternalReferenceCode, String.class);

		updateExternalReferenceCode(
			externalReferenceCodes, hasGroupIdColumn, tableName);

		assertExternalReferenceCode(
			externalReferenceCodes, hasGroupIdColumn, tableName);
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = getUpgradeProcess();

		upgradeProcess.upgrade();

		entityCache.clearCache();
		multiVMPool.clear();
	}

}