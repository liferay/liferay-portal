/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
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
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Preston Crary
 */
@DataGuard(scope = DataGuard.Scope.NONE)
@RunWith(Arquillian.class)
public class OracleDBCTTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		CTSampleTestUtil.reset();

		_ctCollection1 = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), null);
		_ctCollection2 = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), null);
		_ctCollection3 = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), null);
	}

	@After
	public void tearDown() throws Exception {
		CTSampleTestUtil.reset();

		_ctCollectionLocalService.deleteCTCollection(_ctCollection1);
		_ctCollectionLocalService.deleteCTCollection(_ctCollection2);
		_ctCollectionLocalService.deleteCTCollection(_ctCollection3);
	}

	@Test
	public void testDeleteCTCollectionWithOver1000CTEntries() throws Exception {
		long parentCTSChildId = 0;

		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			parentCTSChildId = CTSampleTestUtil.addCTSChild();

			CTSampleTestUtil.addCTSChild(
				0, parentCTSChildId, null, _BATCH_SIZE);
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection1.getCtCollectionId())) {

			_ctsChildLocalService.deleteCTSChild(parentCTSChildId);
		}

		_ctCollectionLocalService.deleteCTCollection(_ctCollection1);

		List<CTSChild> ctsChildren =
			_ctsChildLocalService.getCTSChildrenByParentCTSChildId(
				parentCTSChildId);

		Assert.assertEquals(
			ctsChildren.toString(), _BATCH_SIZE, ctsChildren.size());
	}

	@Test
	public void testMoveAndDiscardCTEntryWithOver1000CTEntries()
		throws Exception {

		long parentCTSChildId = 0;

		try (LoggingTimer loggingTimer = new LoggingTimer();
			SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection1.getCtCollectionId())) {

			parentCTSChildId = CTSampleTestUtil.addCTSChild();

			CTSampleTestUtil.addCTSChild(
				0, parentCTSChildId, null, _BATCH_SIZE);
		}

		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			_ctCollectionService.moveCTEntry(
				_ctCollection1.getCtCollectionId(),
				_ctCollection2.getCtCollectionId(),
				_classNameLocalService.getClassNameId(CTSChild.class),
				parentCTSChildId);
		}

		Assert.assertEquals(
			0,
			_ctEntryLocalService.getCTCollectionCTEntriesCount(
				_ctCollection1.getCtCollectionId()));

		Assert.assertNotEquals(
			0,
			_ctEntryLocalService.getCTCollectionCTEntriesCount(
				_ctCollection2.getCtCollectionId()));

		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			_ctCollectionService.discardCTEntry(
				_ctCollection2.getCtCollectionId(),
				_classNameLocalService.getClassNameId(CTSChild.class),
				parentCTSChildId);
		}

		Assert.assertEquals(
			0,
			_ctEntryLocalService.getCTCollectionCTEntriesCount(
				_ctCollection2.getCtCollectionId()));
	}

	@Test
	public void testPublishAndRevertCTCollectionWithOver1000CTEntries()
		throws Exception {

		try (LoggingTimer loggingTimer = new LoggingTimer();
			SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection1.getCtCollectionId())) {

			CTSampleTestUtil.addCTSChild(_BATCH_SIZE);

			_ctCollectionService.publishCTCollection(
				TestPropsValues.getUserId(),
				_ctCollection1.getCtCollectionId());
		}

		_ctCollection1 = _ctCollectionLocalService.getCTCollection(
			_ctCollection1.getCtCollectionId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, _ctCollection1.getStatus());

		CTCollection revertedCTCollection = null;

		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			revertedCTCollection = _ctCollectionLocalService.undoCTCollection(
				_ctCollection1.getCtCollectionId(), TestPropsValues.getUserId(),
				RandomTestUtil.randomString(), null);
		}

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, revertedCTCollection.getStatus());

		_ctCollectionLocalService.deleteCTCollection(revertedCTCollection);
	}

	@Test
	public void testPublishCTCollectionWithOver1000CTEntries()
		throws Exception {

		try (LoggingTimer loggingTimer = new LoggingTimer();
			SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection1.getCtCollectionId())) {

			CTSampleTestUtil.addCTSChild(_BATCH_SIZE);

			_ctCollectionService.publishCTCollection(
				TestPropsValues.getUserId(),
				_ctCollection1.getCtCollectionId());
		}

		_ctCollection1 = _ctCollectionLocalService.getCTCollection(
			_ctCollection1.getCtCollectionId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, _ctCollection1.getStatus());

		try (LoggingTimer loggingTimer = new LoggingTimer();
			SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection2.getCtCollectionId())) {

			for (CTSChild ctsChild :
					_ctsChildLocalService.getCTSChildren(
						TestPropsValues.getCompanyId())) {

				_ctsChildLocalService.updateCTSChild(ctsChild);
			}

			_ctCollectionService.publishCTCollection(
				TestPropsValues.getUserId(),
				_ctCollection2.getCtCollectionId());
		}

		_ctCollection2 = _ctCollectionLocalService.getCTCollection(
			_ctCollection2.getCtCollectionId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, _ctCollection2.getStatus());

		try (LoggingTimer loggingTimer = new LoggingTimer();
			SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection3.getCtCollectionId())) {

			_ctsChildLocalService.deleteCTSChildren(
				TestPropsValues.getCompanyId());

			_ctCollectionService.publishCTCollection(
				TestPropsValues.getUserId(),
				_ctCollection3.getCtCollectionId());
		}

		_ctCollection3 = _ctCollectionLocalService.getCTCollection(
			_ctCollection3.getCtCollectionId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, _ctCollection3.getStatus());
	}

	private static final int _BATCH_SIZE = 1001;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	private CTCollection _ctCollection1;
	private CTCollection _ctCollection2;
	private CTCollection _ctCollection3;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private CTCollectionService _ctCollectionService;

	@Inject
	private CTEntryLocalService _ctEntryLocalService;

	@Inject
	private CTSChildLocalService _ctsChildLocalService;

}