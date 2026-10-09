/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.upgrade.v2_4_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.internal.test.util.CTCollectionTestUtil;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTPreferences;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTPreferencesLocalService;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import java.sql.Connection;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Samuel Trong Tran
 */
@RunWith(Arquillian.class)
public class UpgradeCTSchemaVersionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_ctCollection1 = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, "P1", null);

		_ctCollection2 =
			CTCollectionTestUtil.createCTCollectionWithIncompleteStatus(
				TestPropsValues.getUser());

		_ctPreferences = _ctPreferencesLocalService.getCTPreferences(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId());

		_ctPreferences.setCtCollectionId(_ctCollection1.getCtCollectionId());
		_ctPreferences.setPreviousCtCollectionId(
			_ctCollection2.getCtCollectionId());

		_ctPreferences = _ctPreferencesLocalService.updateCTPreferences(
			_ctPreferences);

		_upgradeStepRegistrator.register(
			new UpgradeStepRegistrator.Registry() {

				@Override
				public void register(
					String fromSchemaVersionString,
					String toSchemaVersionString, UpgradeStep... upgradeSteps) {

					for (UpgradeStep upgradeStep : upgradeSteps) {
						if (!fromSchemaVersionString.equals("2.3.0")) {
							continue;
						}

						UpgradeProcess upgradeProcess =
							(UpgradeProcess)upgradeStep;

						for (UpgradeStep innerUpgradeStep :
								upgradeProcess.getUpgradeSteps()) {

							_upgradeSteps.add((UpgradeProcess)innerUpgradeStep);
						}
					}
				}

			});
	}

	@Test
	public void testUpgradeSetsExpiredStatusAndResetsPreferences()
		throws Exception {

		try (Connection connection = DataAccess.getConnection()) {
			DB db = DBManagerUtil.getDB();
			DBInspector dbInspector = new DBInspector(connection);

			if (dbInspector.hasTable("CTSchemaVersion")) {
				db.runSQL("drop table CTSchemaVersion");
			}

			if (dbInspector.hasColumn("CTCollection", "schemaVersionId")) {
				db.alterTableDropColumn(
					connection, "CTCollection", "schemaVersionId");
			}
		}

		for (UpgradeProcess upgradeProcess : _upgradeSteps) {
			upgradeProcess.upgrade();
		}

		CacheRegistryUtil.clear();

		_ctCollection1 = _ctCollectionLocalService.getCTCollection(
			_ctCollection1.getCtCollectionId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_EXPIRED, _ctCollection1.getStatus());

		_ctCollection2 = _ctCollectionLocalService.getCTCollection(
			_ctCollection2.getCtCollectionId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_EXPIRED, _ctCollection2.getStatus());

		_ctPreferences = _ctPreferencesLocalService.getCTPreferences(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId());

		Assert.assertEquals(
			CTConstants.CT_COLLECTION_ID_PRODUCTION,
			_ctPreferences.getCtCollectionId());
		Assert.assertEquals(
			CTConstants.CT_COLLECTION_ID_PRODUCTION,
			_ctPreferences.getPreviousCtCollectionId());
	}

	@DeleteAfterTestRun
	private CTCollection _ctCollection1;

	@DeleteAfterTestRun
	private CTCollection _ctCollection2;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@DeleteAfterTestRun
	private CTPreferences _ctPreferences;

	@Inject
	private CTPreferencesLocalService _ctPreferencesLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.change.tracking.internal.upgrade.registry.ChangeTrackingServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

	private final List<UpgradeProcess> _upgradeSteps = new ArrayList<>();

}