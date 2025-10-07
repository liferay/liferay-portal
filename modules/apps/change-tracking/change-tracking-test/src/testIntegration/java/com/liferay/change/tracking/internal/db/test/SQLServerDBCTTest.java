/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.db.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.sample.model.CTSChild;
import com.liferay.change.tracking.sample.service.CTSChildLocalService;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTCollectionService;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.test.util.CTSampleTestUtil;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Gislayne Vitorino
 */
@DataGuard(scope = DataGuard.Scope.NONE)
@RunWith(Arquillian.class)
public class SQLServerDBCTTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		CTSampleTestUtil.reset();

		_db = DBManagerUtil.getDB();

		_ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, SQLServerDBCTTest.class.getSimpleName(), StringPool.BLANK);
	}

	@After
	public void tearDown() throws Exception {
		CTSampleTestUtil.reset();

		_ctCollectionLocalService.deleteCTCollection(_ctCollection);
	}

	@Test
	public void testDeleteCTCollectionWithOver65535CTEntries()
		throws Exception {

		try (LoggingTimer loggingTimer = new LoggingTimer();
			SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			CTSampleTestUtil.addCTSChild(_BATCH_SIZE_QUERY_PROCESSOR);
		}

		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			_ctCollectionLocalService.deleteCTCollection(_ctCollection);
		}

		try (LoggingTimer loggingTimer = new LoggingTimer();
			Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"select * from CTSChild where ctCollectionId = " +
					_ctCollection.getCtCollectionId());
			ResultSet resultSet = preparedStatement.executeQuery()) {

			Assert.assertFalse(resultSet.next());
		}
	}

	@Test
	public void testPublishCTCollectionWithOver2000CTEntries()
		throws Exception {

		long parentCTSChildId = 0;

		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			parentCTSChildId = CTSampleTestUtil.addCTSChild();

			CTSampleTestUtil.addCTSChild(
				0, parentCTSChildId, null, _BATCH_SIZE_HIBERNATE);
		}

		List<CTSChild> ctsChildren =
			_ctsChildLocalService.getCTSChildrenByParentCTSChildId(
				parentCTSChildId);

		try (LoggingTimer loggingTimer = new LoggingTimer();
			SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			for (CTSChild ctsChild : ctsChildren) {
				_ctsChildLocalService.updateCTSChild(ctsChild);
			}
		}

		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			_ctCollectionService.publishCTCollection(
				TestPropsValues.getUserId(), _ctCollection.getCtCollectionId());
		}

		_ctCollection = _ctCollectionLocalService.getCTCollection(
			_ctCollection.getCtCollectionId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, _ctCollection.getStatus());

		try (LoggingTimer loggingTimer = new LoggingTimer();
			 Connection connection = DataAccess.getConnection();

			 PreparedStatement preparedStatement = connection.prepareStatement(
				 "select * from CTSChild where ctCollectionId = " +
				 -_ctCollection.getCtCollectionId());
			 ResultSet resultSet = preparedStatement.executeQuery()) {

			Assert.assertFalse(resultSet.next());
		}
	}

	@Test
	public void testPublishCTCollectionWithOver65535CTEntries()
		throws Exception {

		try (LoggingTimer loggingTimer = new LoggingTimer();
			SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			CTSampleTestUtil.addCTSChild(_BATCH_SIZE_QUERY_PROCESSOR);
		}

		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			_ctCollectionService.publishCTCollection(
				TestPropsValues.getUserId(), _ctCollection.getCtCollectionId());
		}

		_ctCollection = _ctCollectionLocalService.getCTCollection(
			_ctCollection.getCtCollectionId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, _ctCollection.getStatus());
	}

	private static final int _BATCH_SIZE_HIBERNATE = 2001;

	private static final int _BATCH_SIZE_QUERY_PROCESSOR = 65536;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	private CTCollection _ctCollection;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private CTCollectionService _ctCollectionService;

	@Inject
	private CTEntryLocalService _ctEntryLocalService;

	@Inject
	private CTSChildLocalService _ctsChildLocalService;

	private DB _db;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject
	private DLFolderLocalService _dlFolderLocalService;

}