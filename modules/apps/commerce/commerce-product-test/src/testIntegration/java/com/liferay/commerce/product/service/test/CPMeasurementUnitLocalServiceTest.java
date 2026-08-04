/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.constants.CPMeasurementUnitConstants;
import com.liferay.commerce.product.exception.NoSuchCPMeasurementUnitException;
import com.liferay.commerce.product.model.CPMeasurementUnit;
import com.liferay.commerce.product.service.CPMeasurementUnitLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.frutilla.FrutillaRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alessio Antonio Rendina
 */
@RunWith(Arquillian.class)
public class CPMeasurementUnitLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user = UserTestUtil.addUser();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), _user.getUserId());
	}

	@Test
	public void testGetOrAddEmptyCPMeasurementUnit() throws Exception {
		frutillaRule.scenario(
			"Get or add an empty product measurement unit"
		).given(
			"A company and an external reference code"
		).when(
			"An empty product measurement unit is requested"
		).then(
			"A NoSuchCPMeasurementUnitException is thrown while lazy " +
				"referencing is disabled"
		).and(
			"An empty stub with the given external reference code is " +
				"returned while lazy referencing is enabled"
		).and(
			"The same product measurement unit is resolved on subsequent " +
				"requests"
		).and(
			"The empty status is cleared once the stub is updated"
		);

		String externalReferenceCode = RandomTestUtil.randomString();

		try {
			_cpMeasurementUnitLocalService.getOrAddEmptyCPMeasurementUnit(
				externalReferenceCode, _serviceContext.getCompanyId(),
				_serviceContext.getUserId());

			Assert.fail();
		}
		catch (NoSuchCPMeasurementUnitException
					noSuchCPMeasurementUnitException) {

			Assert.assertNotNull(noSuchCPMeasurementUnitException);
		}

		CPMeasurementUnit cpMeasurementUnit = null;

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			cpMeasurementUnit =
				_cpMeasurementUnitLocalService.getOrAddEmptyCPMeasurementUnit(
					externalReferenceCode, _serviceContext.getCompanyId(),
					_serviceContext.getUserId());

			Assert.assertEquals(
				WorkflowConstants.STATUS_EMPTY, cpMeasurementUnit.getStatus());
			Assert.assertEquals(
				externalReferenceCode,
				cpMeasurementUnit.getExternalReferenceCode());

			CPMeasurementUnit resolvedCPMeasurementUnit =
				_cpMeasurementUnitLocalService.getOrAddEmptyCPMeasurementUnit(
					externalReferenceCode, _serviceContext.getCompanyId(),
					_serviceContext.getUserId());

			Assert.assertEquals(
				cpMeasurementUnit.getCPMeasurementUnitId(),
				resolvedCPMeasurementUnit.getCPMeasurementUnitId());
		}

		cpMeasurementUnit =
			_cpMeasurementUnitLocalService.updateCPMeasurementUnit(
				externalReferenceCode,
				cpMeasurementUnit.getCPMeasurementUnitId(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomString(), 1, false, 0,
				CPMeasurementUnitConstants.TYPE_UNIT, _serviceContext);

		Assert.assertNotEquals(
			WorkflowConstants.STATUS_EMPTY, cpMeasurementUnit.getStatus());

		_cpMeasurementUnitLocalService.deleteCPMeasurementUnit(
			cpMeasurementUnit);
	}

	@Rule
	public final FrutillaRule frutillaRule = new FrutillaRule();

	@Inject
	private CPMeasurementUnitLocalService _cpMeasurementUnitLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private ServiceContext _serviceContext;
	private User _user;

}