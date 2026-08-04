/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.pricing.exception.CommercePricingClassTitleException;
import com.liferay.commerce.pricing.exception.NoSuchPricingClassException;
import com.liferay.commerce.pricing.model.CommercePricingClass;
import com.liferay.commerce.pricing.model.CommercePricingClassCPDefinitionRel;
import com.liferay.commerce.pricing.service.CommercePricingClassCPDefinitionRelLocalService;
import com.liferay.commerce.pricing.service.CommercePricingClassLocalService;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;

import org.frutilla.FrutillaRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Riccardo Alberti
 */
@RunWith(Arquillian.class)
public class CommercePricingClassLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user = UserTestUtil.addUser();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_user.getCompanyId(), _group.getGroupId(), _user.getUserId());
	}

	@After
	public void tearDown() throws Exception {
		_commercePricingClassLocalService.deleteCommercePricingClasses(
			_group.getCompanyId());
	}

	@Test
	public void testAddProductToPricingClass() throws Exception {
		frutillaRule.scenario(
			"A CPDefinition is added to a pricing class"
		).given(
			"A product in a catalog"
		).and(
			"A pricing class"
		).when(
			"The product is added to the pricing class"
		).then(
			"It should be returned when querying the pricing class content"
		);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				LocaleUtil.US.getDisplayLanguage(), _serviceContext);

		CommercePricingClass commercePricingClass =
			_commercePricingClassLocalService.addCommercePricingClass(
				_user.getUserId(), RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(), _serviceContext);

		CPInstance cpInstance = CPTestUtil.addCPInstance(
			commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		_commercePricingClassCPDefinitionRelLocalService.
			addCommercePricingClassCPDefinitionRel(
				commercePricingClass.getCommercePricingClassId(),
				cpDefinition.getCPDefinitionId(), _serviceContext);

		List<CommercePricingClassCPDefinitionRel>
			commercePricingClassCPDefinitionRels =
				_commercePricingClassCPDefinitionRelLocalService.
					getCommercePricingClassCPDefinitionRels(
						commercePricingClass.getCommercePricingClassId());

		Assert.assertEquals(
			commercePricingClassCPDefinitionRels.toString(), 1,
			commercePricingClassCPDefinitionRels.size());

		CommercePricingClassCPDefinitionRel
			commercePricingClassCPDefinitionRel =
				commercePricingClassCPDefinitionRels.get(0);

		Assert.assertEquals(
			cpDefinition.getCPDefinitionId(),
			commercePricingClassCPDefinitionRel.getCPDefinitionId());
	}

	@Test
	public void testCreateNewPricingClass() throws Exception {
		frutillaRule.scenario(
			"A new pricing class is added"
		).given(
			"A company and a user"
		).when(
			"A pricing class is created"
		).then(
			"The count of pricing classes shall increase to 1"
		);

		Assert.assertEquals(
			0,
			_commercePricingClassLocalService.getCommercePricingClassesCount(
				_user.getCompanyId()));

		_commercePricingClassLocalService.addCommercePricingClass(
			_user.getUserId(), RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), _serviceContext);

		Assert.assertEquals(
			1,
			_commercePricingClassLocalService.getCommercePricingClassesCount(
				_user.getCompanyId()));
	}

	@Test(expected = CommercePricingClassTitleException.class)
	public void testCreatePricingClassWithoutTitle() throws Exception {
		frutillaRule.scenario(
			"Creating a pricing class with no title will result in an exception"
		).given(
			"A company"
		).when(
			"I create a new pricing class with no title"
		).then(
			"A CommercePricingClassTitle shall be raised"
		);

		_commercePricingClassLocalService.addCommercePricingClass(
			_user.getUserId(), null, RandomTestUtil.randomLocaleStringMap(),
			_serviceContext);
	}

	@Test
	public void testDeletePricingClass() throws Exception {
		frutillaRule.scenario(
			"When deleting a pricing class all rels are deleted as well"
		).given(
			"A pricing class with at least a rel"
		).when(
			"I delete the pricing class"
		).then(
			"There should be no rels associated to the pricing class"
		);

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				LocaleUtil.US.getDisplayLanguage(), _serviceContext);

		CommercePricingClass commercePricingClass =
			_commercePricingClassLocalService.addCommercePricingClass(
				_user.getUserId(), RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(), _serviceContext);

		CPInstance cpInstance = CPTestUtil.addCPInstance(
			commerceCatalog.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		_commercePricingClassCPDefinitionRelLocalService.
			addCommercePricingClassCPDefinitionRel(
				commercePricingClass.getCommercePricingClassId(),
				cpDefinition.getCPDefinitionId(), _serviceContext);

		int commercePricingClassRelsCount =
			_commercePricingClassCPDefinitionRelLocalService.
				getCommercePricingClassCPDefinitionRelsCount(
					commercePricingClass.getCommercePricingClassId());

		Assert.assertEquals(1, commercePricingClassRelsCount);

		_commercePricingClassLocalService.deleteCommercePricingClass(
			commercePricingClass.getCommercePricingClassId());

		commercePricingClassRelsCount =
			_commercePricingClassCPDefinitionRelLocalService.
				getCommercePricingClassCPDefinitionRelsCount(
					commercePricingClass.getCommercePricingClassId());

		Assert.assertEquals(0, commercePricingClassRelsCount);
	}

	@Test
	public void testGetOrAddEmptyCommercePricingClass() throws Exception {
		frutillaRule.scenario(
			"Get or add an empty product group"
		).given(
			"A company and an external reference code"
		).when(
			"An empty product group is requested"
		).then(
			"A NoSuchPricingClassException is thrown while lazy referencing " +
				"is disabled"
		).and(
			"An empty stub with the given external reference code is " +
				"returned while lazy referencing is enabled"
		).and(
			"The same product group is resolved on subsequent requests"
		).and(
			"The empty status is cleared once the stub is updated"
		);

		String externalReferenceCode = RandomTestUtil.randomString();

		try {
			_commercePricingClassLocalService.getOrAddEmptyCommercePricingClass(
				externalReferenceCode, _user.getCompanyId(), _user.getUserId());

			Assert.fail();
		}
		catch (NoSuchPricingClassException noSuchPricingClassException) {
			Assert.assertNotNull(noSuchPricingClassException);
		}

		CommercePricingClass commercePricingClass = null;

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			commercePricingClass =
				_commercePricingClassLocalService.
					getOrAddEmptyCommercePricingClass(
						externalReferenceCode, _user.getCompanyId(),
						_user.getUserId());

			Assert.assertEquals(
				WorkflowConstants.STATUS_EMPTY,
				commercePricingClass.getStatus());
			Assert.assertEquals(
				externalReferenceCode,
				commercePricingClass.getExternalReferenceCode());

			CommercePricingClass resolvedCommercePricingClass =
				_commercePricingClassLocalService.
					getOrAddEmptyCommercePricingClass(
						externalReferenceCode, _user.getCompanyId(),
						_user.getUserId());

			Assert.assertEquals(
				commercePricingClass.getCommercePricingClassId(),
				resolvedCommercePricingClass.getCommercePricingClassId());
		}

		commercePricingClass =
			_commercePricingClassLocalService.updateCommercePricingClass(
				commercePricingClass.getCommercePricingClassId(),
				_user.getUserId(), RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(), _serviceContext);

		Assert.assertNotEquals(
			WorkflowConstants.STATUS_EMPTY, commercePricingClass.getStatus());
	}

	@Rule
	public FrutillaRule frutillaRule = new FrutillaRule();

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@Inject
	private CommercePricingClassCPDefinitionRelLocalService
		_commercePricingClassCPDefinitionRelLocalService;

	@Inject
	private CommercePricingClassLocalService _commercePricingClassLocalService;

	private Group _group;
	private ServiceContext _serviceContext;
	private User _user;

}