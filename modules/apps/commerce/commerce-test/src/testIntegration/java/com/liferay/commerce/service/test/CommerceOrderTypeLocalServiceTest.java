/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.exception.NoSuchOrderTypeException;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.service.CommerceOrderTypeLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Calendar;

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
public class CommerceOrderTypeLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_user = UserTestUtil.addUser();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_user.getCompanyId(), 0, _user.getUserId());
	}

	@Test
	public void testGetOrAddEmptyCommerceOrderType() throws Exception {
		frutillaRule.scenario(
			"Get or add an empty commerce order type"
		).given(
			"A company and an external reference code"
		).when(
			"An empty commerce order type is requested"
		).then(
			"A NoSuchOrderTypeException is thrown while lazy referencing is " +
				"disabled"
		).and(
			"An empty stub with the given external reference code is " +
				"returned while lazy referencing is enabled"
		).and(
			"The same commerce order type is resolved on subsequent requests"
		).and(
			"The empty status is cleared once the stub is updated"
		);

		String externalReferenceCode = RandomTestUtil.randomString();

		try {
			_commerceOrderTypeLocalService.getOrAddEmptyCommerceOrderType(
				externalReferenceCode, _user.getCompanyId(), _user.getUserId());

			Assert.fail();
		}
		catch (NoSuchOrderTypeException noSuchOrderTypeException) {
			Assert.assertNotNull(noSuchOrderTypeException);
		}

		CommerceOrderType commerceOrderType;

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			commerceOrderType =
				_commerceOrderTypeLocalService.getOrAddEmptyCommerceOrderType(
					externalReferenceCode, _user.getCompanyId(),
					_user.getUserId());

			Assert.assertEquals(
				WorkflowConstants.STATUS_EMPTY, commerceOrderType.getStatus());
			Assert.assertEquals(
				externalReferenceCode,
				commerceOrderType.getExternalReferenceCode());

			CommerceOrderType resolvedCommerceOrderType =
				_commerceOrderTypeLocalService.getOrAddEmptyCommerceOrderType(
					externalReferenceCode, _user.getCompanyId(),
					_user.getUserId());

			Assert.assertEquals(
				commerceOrderType.getCommerceOrderTypeId(),
				resolvedCommerceOrderType.getCommerceOrderTypeId());
		}

		Calendar calendar = CalendarFactoryUtil.getCalendar(
			_user.getTimeZone());

		commerceOrderType =
			_commerceOrderTypeLocalService.updateCommerceOrderType(
				externalReferenceCode, _user.getUserId(),
				commerceOrderType.getCommerceOrderTypeId(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(), true,
				calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE),
				calendar.get(Calendar.YEAR), calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, 0, true,
				_serviceContext);

		Assert.assertNotEquals(
			WorkflowConstants.STATUS_EMPTY, commerceOrderType.getStatus());
		Assert.assertFalse(
			commerceOrderType.getNameMap(
			).isEmpty());
	}

	@Rule
	public final FrutillaRule frutillaRule = new FrutillaRule();

	@Inject
	private CommerceOrderTypeLocalService _commerceOrderTypeLocalService;

	private ServiceContext _serviceContext;
	private User _user;

}