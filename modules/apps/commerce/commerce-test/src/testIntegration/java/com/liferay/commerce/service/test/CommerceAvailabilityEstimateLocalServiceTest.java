/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.exception.NoSuchAvailabilityEstimateException;
import com.liferay.commerce.model.CommerceAvailabilityEstimate;
import com.liferay.commerce.service.CommerceAvailabilityEstimateLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Locale;
import java.util.Map;

import org.frutilla.FrutillaRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Michele Vigilante
 */
@RunWith(Arquillian.class)
public class CommerceAvailabilityEstimateLocalServiceTest {

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
	public void testGetOrAddEmptyCommerceAvailabilityEstimate()
		throws Exception {

		frutillaRule.scenario(
			"Get or add an empty commerce availability estimate"
		).given(
			"A company and an external reference code"
		).when(
			"An empty commerce availability estimate is requested"
		).then(
			"A NoSuchAvailabilityEstimateException is thrown while lazy " +
				"referencing is disabled"
		).and(
			"An empty stub with the given external reference code is " +
				"returned while lazy referencing is enabled"
		).and(
			"The same commerce availability estimate is resolved on " +
				"subsequent requests"
		).and(
			"The empty status is cleared once the stub is updated"
		);

		String externalReferenceCode = RandomTestUtil.randomString();

		try {
			_commerceAvailabilityEstimateLocalService.
				getOrAddEmptyCommerceAvailabilityEstimate(
					externalReferenceCode, _user.getCompanyId(),
					_user.getUserId());

			Assert.fail();
		}
		catch (NoSuchAvailabilityEstimateException
					noSuchAvailabilityEstimateException) {

			Assert.assertNotNull(noSuchAvailabilityEstimateException);
		}

		CommerceAvailabilityEstimate commerceAvailabilityEstimate = null;

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			commerceAvailabilityEstimate =
				_commerceAvailabilityEstimateLocalService.
					getOrAddEmptyCommerceAvailabilityEstimate(
						externalReferenceCode, _user.getCompanyId(),
						_user.getUserId());

			Assert.assertEquals(
				WorkflowConstants.STATUS_EMPTY,
				commerceAvailabilityEstimate.getStatus());
			Assert.assertEquals(
				externalReferenceCode,
				commerceAvailabilityEstimate.getExternalReferenceCode());
			Assert.assertEquals(
				externalReferenceCode,
				commerceAvailabilityEstimate.getTitle(
					LocaleUtil.getSiteDefault()));

			CommerceAvailabilityEstimate resolvedCommerceAvailabilityEstimate =
				_commerceAvailabilityEstimateLocalService.
					getOrAddEmptyCommerceAvailabilityEstimate(
						externalReferenceCode, _user.getCompanyId(),
						_user.getUserId());

			Assert.assertEquals(
				commerceAvailabilityEstimate.
					getCommerceAvailabilityEstimateId(),
				resolvedCommerceAvailabilityEstimate.
					getCommerceAvailabilityEstimateId());
		}

		commerceAvailabilityEstimate =
			_commerceAvailabilityEstimateLocalService.
				updateCommerceAvailabilityEstimate(
					externalReferenceCode,
					commerceAvailabilityEstimate.
						getCommerceAvailabilityEstimateId(),
					RandomTestUtil.randomLocaleStringMap(),
					RandomTestUtil.randomDouble(), _serviceContext);

		Assert.assertNotEquals(
			WorkflowConstants.STATUS_EMPTY,
			commerceAvailabilityEstimate.getStatus());

		Map<Locale, String> titleMap =
			commerceAvailabilityEstimate.getTitleMap();

		Assert.assertFalse(titleMap.isEmpty());
	}

	@Rule
	public final FrutillaRule frutillaRule = new FrutillaRule();

	@Inject
	private CommerceAvailabilityEstimateLocalService
		_commerceAvailabilityEstimateLocalService;

	private ServiceContext _serviceContext;
	private User _user;

}