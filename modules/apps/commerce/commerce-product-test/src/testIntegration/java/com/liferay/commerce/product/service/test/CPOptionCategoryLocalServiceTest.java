/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.exception.CPOptionCategoryKeyException;
import com.liferay.commerce.product.exception.CPOptionCategoryTitleException;
import com.liferay.commerce.product.exception.NoSuchCPOptionCategoryException;
import com.liferay.commerce.product.model.CPOptionCategory;
import com.liferay.commerce.product.service.CPOptionCategoryLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
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
 * @author Jan Brychta
 */
@RunWith(Arquillian.class)
public class CPOptionCategoryLocalServiceTest {

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

	@Test(expected = CPOptionCategoryKeyException.class)
	public void testAddOrUpdateCPOptionCategoryOptionWithoutKey()
		throws Exception {

		frutillaRule.scenario(
			"Add Option Category"
		).given(
			"There is no Option Category"
		).when(
			"Option Category is added without required key field"
		).then(
			"Option Category should not be created"
		);

		_cpOptionCategoryLocalService.addOrUpdateCPOptionCategory(
			RandomTestUtil.randomString(), _serviceContext.getUserId(), 0L,
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomDouble(), null, _serviceContext);
	}

	@Test(expected = CPOptionCategoryTitleException.class)
	public void testAddOrUpdateCPOptionCategoryOptionWithoutTitle()
		throws Exception {

		frutillaRule.scenario(
			"Add Option Category"
		).given(
			"There is no Option Category"
		).when(
			"Option Category is added without required title field"
		).then(
			"Option Category should not be created"
		);

		_cpOptionCategoryLocalService.addOrUpdateCPOptionCategory(
			RandomTestUtil.randomString(), _serviceContext.getUserId(), 0L,
			null, RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomDouble(), RandomTestUtil.randomString(),
			_serviceContext);
	}

	@Test
	public void testGetOrAddEmptyCPOptionCategory() throws Exception {
		frutillaRule.scenario(
			"Get or add an empty product option category"
		).given(
			"A company and an external reference code"
		).when(
			"An empty product option category is requested"
		).then(
			"A NoSuchCPOptionCategoryException is thrown while lazy " +
				"referencing is disabled"
		).and(
			"An empty stub with the given external reference code is " +
				"returned while lazy referencing is enabled"
		).and(
			"The same product option category is resolved on subsequent " +
				"requests"
		).and(
			"The empty status is cleared once the stub is updated"
		);

		String externalReferenceCode = RandomTestUtil.randomString();

		try {
			_cpOptionCategoryLocalService.getOrAddEmptyCPOptionCategory(
				externalReferenceCode, _serviceContext.getCompanyId(),
				_serviceContext.getUserId());

			Assert.fail();
		}
		catch (NoSuchCPOptionCategoryException
					noSuchCPOptionCategoryException) {

			Assert.assertNotNull(noSuchCPOptionCategoryException);
		}

		CPOptionCategory cpOptionCategory = null;

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			cpOptionCategory =
				_cpOptionCategoryLocalService.getOrAddEmptyCPOptionCategory(
					externalReferenceCode, _serviceContext.getCompanyId(),
					_serviceContext.getUserId());

			Assert.assertEquals(
				WorkflowConstants.STATUS_EMPTY, cpOptionCategory.getStatus());
			Assert.assertEquals(
				externalReferenceCode,
				cpOptionCategory.getExternalReferenceCode());

			CPOptionCategory resolvedCPOptionCategory =
				_cpOptionCategoryLocalService.getOrAddEmptyCPOptionCategory(
					externalReferenceCode, _serviceContext.getCompanyId(),
					_serviceContext.getUserId());

			Assert.assertEquals(
				cpOptionCategory.getCPOptionCategoryId(),
				resolvedCPOptionCategory.getCPOptionCategoryId());
		}

		cpOptionCategory = _cpOptionCategoryLocalService.updateCPOptionCategory(
			externalReferenceCode, cpOptionCategory.getCPOptionCategoryId(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), 0,
			RandomTestUtil.randomString());

		Assert.assertNotEquals(
			WorkflowConstants.STATUS_EMPTY, cpOptionCategory.getStatus());

		_cpOptionCategoryLocalService.deleteCPOptionCategory(cpOptionCategory);
	}

	@Rule
	public final FrutillaRule frutillaRule = new FrutillaRule();

	@Inject
	private CPOptionCategoryLocalService _cpOptionCategoryLocalService;

	private Group _group;
	private ServiceContext _serviceContext;
	private User _user;

}