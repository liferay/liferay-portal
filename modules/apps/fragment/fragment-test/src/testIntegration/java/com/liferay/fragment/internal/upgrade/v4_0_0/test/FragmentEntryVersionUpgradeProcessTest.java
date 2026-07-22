/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.upgrade.v4_0_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
import com.liferay.fragment.configuration.FragmentEntryVersionConfiguration;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryVersion;
import com.liferay.fragment.service.persistence.FragmentEntryVersionUtil;
import com.liferay.fragment.test.util.FragmentEntryTestUtil;
import com.liferay.fragment.test.util.FragmentTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.List;

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
public class FragmentEntryVersionUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	@TestInfo("LPD-75909")
	public void testUpgrade() throws Exception {
		FragmentEntryVersionConfiguration fragmentEntryVersionConfiguration =
			_configurationProvider.getCompanyConfiguration(
				FragmentEntryVersionConfiguration.class,
				TestPropsValues.getCompanyId());

		int maximumVersionsPerEntry =
			fragmentEntryVersionConfiguration.maximumVersionsPerEntry();

		_testUpgrade(
			maximumVersionsPerEntry + 1, maximumVersionsPerEntry + 1, 0);
		_testUpgrade(
			maximumVersionsPerEntry, maximumVersionsPerEntry + 1,
			maximumVersionsPerEntry);
		_testUpgrade(
			maximumVersionsPerEntry + 1, maximumVersionsPerEntry,
			maximumVersionsPerEntry);
	}

	private List<Integer> _addFragmentEntryVersions(
			int count, long ctCollectionId, FragmentEntry fragmentEntry)
		throws Exception {

		List<Integer> versions = new ArrayList<>(count);

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select max(version) as maxVersion from FragmentEntryVersion " +
					"where ctCollectionId = ? and fragmentEntryId = ?");
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					StringBundler.concat(
						"insert into FragmentEntryVersion (mvccVersion, ",
						"ctCollectionId, fragmentEntryVersionId, version, ",
						"fragmentEntryId, groupId, companyId, userId, ",
						"createDate, modifiedDate, name, status) values (0, ",
						"?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"))) {

			preparedStatement1.setLong(1, ctCollectionId);
			preparedStatement1.setLong(2, fragmentEntry.getFragmentEntryId());

			int maxVersion = 0;

			try (ResultSet resultSet = preparedStatement1.executeQuery()) {
				if (resultSet.next()) {
					maxVersion = resultSet.getInt("maxVersion");
				}
			}

			for (int i = 1; i <= count; i++) {
				int version = maxVersion + i;

				versions.add(version);

				Timestamp now = new Timestamp(System.currentTimeMillis());

				preparedStatement2.setLong(1, ctCollectionId);
				preparedStatement2.setLong(
					2,
					CounterLocalServiceUtil.increment(
						FragmentEntryVersion.class.getName()));
				preparedStatement2.setInt(3, version);
				preparedStatement2.setLong(
					4, fragmentEntry.getFragmentEntryId());
				preparedStatement2.setLong(5, fragmentEntry.getGroupId());
				preparedStatement2.setLong(6, fragmentEntry.getCompanyId());
				preparedStatement2.setLong(7, fragmentEntry.getUserId());
				preparedStatement2.setTimestamp(8, now);
				preparedStatement2.setTimestamp(9, now);
				preparedStatement2.setString(10, RandomTestUtil.randomString());
				preparedStatement2.setInt(
					11, WorkflowConstants.STATUS_APPROVED);

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}

		FragmentEntryVersionUtil.clearCache();

		return versions;
	}

	private int _getFromIndex(int maximumVersionsPerEntry, int size) {
		if (maximumVersionsPerEntry <= 0) {
			return 0;
		}

		return Math.max(0, size - maximumVersionsPerEntry);
	}

	private List<Integer> _getVersions(
			long ctCollectionId, FragmentEntry fragmentEntry)
		throws Exception {

		List<Integer> versions = new ArrayList<>();

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"select version from FragmentEntryVersion where " +
					"ctCollectionId = ? and fragmentEntryId = ? order by " +
						"version")) {

			preparedStatement.setLong(1, ctCollectionId);
			preparedStatement.setLong(2, fragmentEntry.getFragmentEntryId());

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					versions.add(resultSet.getInt("version"));
				}
			}
		}

		return versions;
	}

	private void _runUpgrade() throws Exception {
		for (UpgradeProcess upgradeProcess :
				UpgradeTestUtil.getUpgradeSteps(
					_upgradeStepRegistrator, new Version(4, 0, 0))) {

			upgradeProcess.upgrade();
		}

		_entityCache.clearCache();
		_multiVMPool.clear();
	}

	private void _testUpgrade(
			int ctCollectionFragmentEntryVersionsCount,
			int fragmentEntryVersionsCount, int maximumVersionsPerEntry)
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						FragmentEntryVersionConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"maximumVersionsPerEntry", maximumVersionsPerEntry
						).build())) {

			FragmentCollection fragmentCollection =
				FragmentTestUtil.addFragmentCollection(_group.getGroupId());

			FragmentEntry fragmentEntry =
				FragmentEntryTestUtil.addFragmentEntry(
					fragmentCollection.getFragmentCollectionId());

			List<Integer> versions = new ArrayList<>(
				_getVersions(
					CTConstants.CT_COLLECTION_ID_PRODUCTION, fragmentEntry));

			long ctCollectionId = RandomTestUtil.randomLong();
			List<Integer> ctCollectionVersions = new ArrayList<>();

			if (ctCollectionFragmentEntryVersionsCount > 0) {
				ctCollectionVersions = _addFragmentEntryVersions(
					ctCollectionFragmentEntryVersionsCount, ctCollectionId,
					fragmentEntry);
			}

			versions.addAll(
				_addFragmentEntryVersions(
					fragmentEntryVersionsCount - 1,
					CTConstants.CT_COLLECTION_ID_PRODUCTION, fragmentEntry));

			_runUpgrade();

			Assert.assertEquals(
				versions.subList(
					_getFromIndex(maximumVersionsPerEntry, versions.size()),
					versions.size()),
				_getVersions(
					CTConstants.CT_COLLECTION_ID_PRODUCTION, fragmentEntry));

			if (ctCollectionFragmentEntryVersionsCount > 0) {
				Assert.assertEquals(
					ctCollectionVersions.subList(
						_getFromIndex(
							maximumVersionsPerEntry,
							ctCollectionVersions.size()),
						ctCollectionVersions.size()),
					_getVersions(ctCollectionId, fragmentEntry));
			}
		}
	}

	@Inject
	private ConfigurationProvider _configurationProvider;

	@Inject
	private EntityCache _entityCache;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject(
		filter = "(&(component.name=com.liferay.fragment.internal.upgrade.registry.FragmentServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}