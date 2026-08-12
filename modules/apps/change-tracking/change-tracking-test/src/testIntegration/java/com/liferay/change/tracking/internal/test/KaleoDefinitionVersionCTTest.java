/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.workflow.constants.WorkflowDefinitionConstants;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionVersionLocalService;
import com.liferay.portal.workflow.kaleo.util.comparator.KaleoDefinitionVersionTitleComparator;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pedro Leite
 */
@RunWith(Arquillian.class)
public class KaleoDefinitionVersionCTTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, KaleoDefinitionVersionCTTest.class.getName(), null);

		_serviceContext = ServiceContextTestUtil.getServiceContext();

		_kaleoDefinition = _kaleoDefinitionLocalService.addKaleoDefinition(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), null, null,
			WorkflowDefinitionConstants.SCOPE_ALL, false, 1, _serviceContext);

		_kaleoDefinitionLocalService.activateKaleoDefinition(
			_kaleoDefinition.getKaleoDefinitionId(), _serviceContext);
	}

	@Test
	public void testGetLatestKaleoDefinitionVersions() throws Exception {
		KaleoDefinitionVersion kaleoDefinitionVersion1 =
			_kaleoDefinitionVersionLocalService.addKaleoDefinitionVersion(
				_kaleoDefinition.getKaleoDefinitionId(),
				_kaleoDefinition.getName(), _kaleoDefinition.getTitle(),
				RandomTestUtil.randomString(), _kaleoDefinition.getContent(),
				"2.0", _serviceContext);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			KaleoDefinitionVersion kaleoDefinitionVersion2 =
				_kaleoDefinitionVersionLocalService.addKaleoDefinitionVersion(
					_kaleoDefinition.getKaleoDefinitionId(),
					_kaleoDefinition.getName(), _kaleoDefinition.getTitle(),
					RandomTestUtil.randomString(),
					_kaleoDefinition.getContent(), "3.0", _serviceContext);

			Assert.assertEquals(
				Collections.singletonList(kaleoDefinitionVersion2),
				_kaleoDefinitionVersionLocalService.
					getLatestKaleoDefinitionVersions(
						_kaleoDefinition.getCompanyId(),
						_kaleoDefinition.getName(),
						WorkflowConstants.STATUS_ANY, LocaleUtil.US,
						QueryUtil.ALL_POS, QueryUtil.ALL_POS,
						new KaleoDefinitionVersionTitleComparator(true)));
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setProductionModeWithSafeCloseable()) {

			Assert.assertEquals(
				Collections.singletonList(kaleoDefinitionVersion1),
				_kaleoDefinitionVersionLocalService.
					getLatestKaleoDefinitionVersions(
						_kaleoDefinition.getCompanyId(),
						_kaleoDefinition.getName(),
						WorkflowConstants.STATUS_ANY, LocaleUtil.US,
						QueryUtil.ALL_POS, QueryUtil.ALL_POS,
						new KaleoDefinitionVersionTitleComparator(true)));
		}
	}

	@DeleteAfterTestRun
	private CTCollection _ctCollection;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@DeleteAfterTestRun
	private KaleoDefinition _kaleoDefinition;

	@Inject
	private KaleoDefinitionLocalService _kaleoDefinitionLocalService;

	@Inject
	private KaleoDefinitionVersionLocalService
		_kaleoDefinitionVersionLocalService;

	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private KaleoDefinition _serviceContext_kaleoDefinition;

}