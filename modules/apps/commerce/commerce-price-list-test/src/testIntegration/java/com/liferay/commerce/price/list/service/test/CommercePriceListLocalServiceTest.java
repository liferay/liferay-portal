/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.price.list.constants.CommercePriceListConstants;
import com.liferay.commerce.price.list.exception.CommercePriceListCurrencyException;
import com.liferay.commerce.price.list.exception.NoSuchPriceListException;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceListLocalService;
import com.liferay.commerce.price.list.service.CommercePriceListService;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CommerceCatalogLocalServiceUtil;
import com.liferay.commerce.test.util.price.list.CommercePriceListTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import org.frutilla.FrutillaRule;

import org.hamcrest.CoreMatchers;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Zoltán Takács
 * @author Ethan Bustad
 * @author Luca Pellizzon
 */
@RunWith(Arquillian.class)
public class CommercePriceListLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_company = CompanyTestUtil.addCompany();
	}

	@Before
	public void setUp() throws Exception {
		User guestUser = _company.getGuestUser();

		_group = GroupTestUtil.addGroup(
			_company.getCompanyId(), guestUser.getUserId(), 0);
	}

	@After
	public void tearDown() throws Exception {
		_commercePriceListLocalService.deleteCommercePriceLists(
			_company.getCompanyId());
	}

	@Test
	public void testAddCommercePriceList1() throws Exception {
		frutillaRule.scenario(
			"Adding a new Price List"
		).given(
			"A site (group)"
		).and(
			"A currency code expressed with 3-letter ISO 4217 format"
		).and(
			"The name of the new list"
		).when(
			"The name of the Price List"
		).and(
			"The currency code are checked against the input data"
		).then(
			"The result should be a new Price List on the given site"
		);

		Currency currency = Currency.getInstance(LocaleUtil.US);
		String name = RandomTestUtil.randomString();

		List<CommerceCatalog> commerceCatalogs =
			CommerceCatalogLocalServiceUtil.getCommerceCatalogs(
				_group.getCompanyId(), true);

		CommerceCatalog commerceCatalog = commerceCatalogs.get(0);

		CommercePriceList commercePriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				null, commerceCatalog.getGroupId(), currency.getCurrencyCode(),
				name, RandomTestUtil.randomDouble(), true, null, null);

		_assertPriceListAttributes(currency, name, commercePriceList);
	}

	@Test
	public void testAddCommercePriceList2() throws Exception {
		frutillaRule.scenario(
			"Adding a new Price List with an external reference code"
		).given(
			"A site (group)"
		).and(
			"A currency code expressed with 3-letter ISO 4217 format"
		).and(
			"The name of the new list"
		).and(
			"The external reference code (externalReferenceCode)"
		).when(
			"The name of the Price List"
		).and(
			"The currency code"
		).and(
			"The external reference code are checked against the input data"
		).then(
			"The result should be a new Price List on the given site"
		);

		Currency currency = Currency.getInstance(LocaleUtil.US);
		String externalReferenceCode = RandomTestUtil.randomString();
		String name = RandomTestUtil.randomString();

		List<CommerceCatalog> commerceCatalogs =
			CommerceCatalogLocalServiceUtil.getCommerceCatalogs(
				_group.getCompanyId(), true);

		CommerceCatalog commerceCatalog = commerceCatalogs.get(0);

		CommercePriceList commercePriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				externalReferenceCode, commerceCatalog.getGroupId(),
				currency.getCurrencyCode(), name, RandomTestUtil.randomDouble(),
				true, null, null);

		_assertPriceListAttributes(currency, name, commercePriceList);

		Assert.assertThat(
			commercePriceList.getExternalReferenceCode(),
			CoreMatchers.equalTo(externalReferenceCode));
	}

	@Test
	public void testAddCommercePriceList3() throws Exception {
		frutillaRule.scenario(
			"Adding a new Price List"
		).given(
			"A site (group)"
		).and(
			"A currency code expressed with 3-letter ISO 4217 format"
		).and(
			"A parent price list ID"
		).and(
			"The name of the new list"
		).when(
			"The name of the Price List"
		).and(
			"The parent price list ID (parentCommercePriceListId)"
		).and(
			"The currency code are checked against the input data"
		).then(
			"The result should be a new Price List on the given site"
		);

		Currency currency = Currency.getInstance(LocaleUtil.US);
		String name = RandomTestUtil.randomString();
		long parentCommercePriceListId = RandomTestUtil.randomLong();

		List<CommerceCatalog> commerceCatalogs =
			CommerceCatalogLocalServiceUtil.getCommerceCatalogs(
				_group.getCompanyId(), true);

		CommerceCatalog commerceCatalog = commerceCatalogs.get(0);

		CommercePriceList commercePriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				null, commerceCatalog.getGroupId(), currency.getCurrencyCode(),
				parentCommercePriceListId, name, RandomTestUtil.randomDouble(),
				true, null, null);

		_assertPriceListAttributes(currency, name, commercePriceList);

		Assert.assertThat(
			commercePriceList.getParentCommercePriceListId(),
			CoreMatchers.equalTo(parentCommercePriceListId));
	}

	@Test
	public void testAddCommercePriceList4() throws Exception {
		frutillaRule.scenario(
			"Adding a new Price List with an external reference code"
		).given(
			"A site (group)"
		).and(
			"A currency code expressed with 3-letter ISO 4217 format"
		).and(
			"A parent price list ID"
		).and(
			"The name of the new list"
		).and(
			"The external reference code (externalReferenceCode)"
		).when(
			"The name of the Price List"
		).and(
			"The parent price list ID (parentCommercePriceListId)"
		).and(
			"The currency code"
		).and(
			"The external reference code are checked against the input data"
		).then(
			"The result should be a new Price List on the given site"
		);

		Currency currency = Currency.getInstance(LocaleUtil.US);
		String externalReferenceCode = RandomTestUtil.randomString();
		String name = RandomTestUtil.randomString();
		long parentCommercePriceListId = RandomTestUtil.randomLong();

		List<CommerceCatalog> commerceCatalogs =
			CommerceCatalogLocalServiceUtil.getCommerceCatalogs(
				_group.getCompanyId(), true);

		CommerceCatalog commerceCatalog = commerceCatalogs.get(0);

		CommercePriceList commercePriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				externalReferenceCode, commerceCatalog.getGroupId(),
				currency.getCurrencyCode(), parentCommercePriceListId, name,
				RandomTestUtil.randomDouble(), true, null, null);

		_assertPriceListAttributes(currency, name, commercePriceList);

		Assert.assertThat(
			commercePriceList.getExternalReferenceCode(),
			CoreMatchers.equalTo(externalReferenceCode));
		Assert.assertThat(
			commercePriceList.getParentCommercePriceListId(),
			CoreMatchers.equalTo(parentCommercePriceListId));
	}

	@Test
	public void testAddOrUpdateCommercePriceList1() throws Exception {
		frutillaRule.scenario(
			"Adding a new Price List"
		).given(
			"A site (group)"
		).and(
			"A currency code expressed with 3-letter ISO 4217 format"
		).and(
			"The name of the new list"
		).when(
			"The external reference code (externalReferenceCode)"
		).and(
			"(commercePriceListId) is not used in the method invocation"
		).then(
			"The result should be a new Price List on the given site"
		);

		Currency currency = Currency.getInstance(LocaleUtil.US);
		String name = RandomTestUtil.randomString();

		List<CommerceCatalog> commerceCatalogs =
			CommerceCatalogLocalServiceUtil.getCommerceCatalogs(
				_group.getCompanyId(), true);

		CommerceCatalog commerceCatalog = commerceCatalogs.get(0);

		CommercePriceList commercePriceList =
			CommercePriceListTestUtil.addOrUpdateCommercePriceList(
				null, commerceCatalog.getGroupId(), 0,
				currency.getCurrencyCode(), name, RandomTestUtil.randomDouble(),
				true, null, null);

		_assertPriceListAttributes(currency, name, commercePriceList);
	}

	@Test
	public void testAddOrUpdateCommercePriceList2() throws Exception {
		frutillaRule.scenario(
			"Update an existing Price List"
		).given(
			"A site (group)"
		).and(
			"A currency code expressed with 3-letter ISO 4217 format"
		).and(
			"The name of the list"
		).when(
			"The external reference code (externalReferenceCode) is used"
		).and(
			"(commercePriceListId) is not used in the method invocation"
		).then(
			"The result should be the updated Price List with the given " +
				"(externalReferenceCode) on the given site"
		);

		Currency currency = Currency.getInstance(LocaleUtil.US);
		String externalReferenceCode = RandomTestUtil.randomString();
		String name = RandomTestUtil.randomString();

		List<CommerceCatalog> commerceCatalogs =
			CommerceCatalogLocalServiceUtil.getCommerceCatalogs(
				_group.getCompanyId(), true);

		CommerceCatalog commerceCatalog = commerceCatalogs.get(0);

		CommercePriceListTestUtil.addCommercePriceList(
			externalReferenceCode, commerceCatalog.getGroupId(),
			currency.getCurrencyCode(), name, RandomTestUtil.randomDouble(),
			true, null, null);

		Currency updatedCurrency = Currency.getInstance(LocaleUtil.UK);
		String updatedName = RandomTestUtil.randomString();

		CommercePriceListTestUtil.addOrUpdateCommercePriceList(
			externalReferenceCode, _group.getGroupId(), 0,
			updatedCurrency.getCurrencyCode(), updatedName,
			RandomTestUtil.randomDouble(), true, null, null);

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.
				fetchCommercePriceListByExternalReferenceCode(
					externalReferenceCode, _group.getCompanyId());

		_assertPriceListAttributes(
			updatedCurrency, updatedName, commercePriceList);
	}

	@Test
	public void testAddOrUpdateCommercePriceList3() throws Exception {
		frutillaRule.scenario(
			"Update an existing Price List"
		).given(
			"A site (group)"
		).and(
			"A currency code expressed with 3-letter ISO 4217 format"
		).and(
			"The name of the new list"
		).when(
			"The external reference code (externalReferenceCode) is not used"
		).and(
			"(commercePriceListId) is used in the method invocation"
		).then(
			"The result should be the updated Price List with the given " +
				"(commercePriceListId) on the given site"
		);

		Currency currency = Currency.getInstance(LocaleUtil.US);
		String name = RandomTestUtil.randomString();

		List<CommerceCatalog> commerceCatalogs =
			CommerceCatalogLocalServiceUtil.getCommerceCatalogs(
				_group.getCompanyId(), true);

		CommerceCatalog commerceCatalog = commerceCatalogs.get(0);

		CommercePriceList commercePriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				null, commerceCatalog.getGroupId(), currency.getCurrencyCode(),
				name, RandomTestUtil.randomDouble(), true, null, null);

		Currency updatedCurrency = Currency.getInstance(LocaleUtil.UK);
		String updatedName = RandomTestUtil.randomString();

		CommercePriceListTestUtil.addOrUpdateCommercePriceList(
			null, _group.getGroupId(),
			commercePriceList.getCommercePriceListId(),
			updatedCurrency.getCurrencyCode(), updatedName,
			RandomTestUtil.randomDouble(), true, null, null);

		CommercePriceList updatedCommercePriceList =
			_commercePriceListLocalService.getCommercePriceList(
				commercePriceList.getCommercePriceListId());

		_assertPriceListAttributes(
			updatedCurrency, updatedName, updatedCommercePriceList);
	}

	@Test(expected = CommercePriceListCurrencyException.class)
	public void testAddOrUpdateCommercePriceList4() throws Exception {
		frutillaRule.scenario(
			"Adding a new Price List"
		).given(
			"A site (group)"
		).and(
			"A non-existent currency code on the site"
		).and(
			"The name of the new list"
		).when(
			"The external reference code (externalReferenceCode)"
		).and(
			"(commercePriceListId) is not used in the method invocation"
		).then(
			"CommercePriceListCurrencyException should be thrown"
		);

		Currency currency = Currency.getInstance("KGS");
		String name = RandomTestUtil.randomString();

		CommercePriceListTestUtil.addOrUpdateCommercePriceList(
			null, _group.getGroupId(), 0, currency.getCurrencyCode(), name,
			RandomTestUtil.randomDouble(), true, null, null);
	}

	@Test
	public void testAddOrUpdateCommercePriceList5() throws Exception {
		frutillaRule.scenario(
			"Adding a new Price List"
		).given(
			"A site (group)"
		).and(
			"A currency code expressed with 3-letter ISO 4217 format"
		).and(
			"The name of the new list"
		).when(
			"The external reference code (externalReferenceCode) is not used"
		).and(
			"A non-existent but greater than 0 (commercePriceListId) is used " +
				"in the method invocation"
		).then(
			"The result should be a new Price List on the given site"
		);

		Currency currency = Currency.getInstance(LocaleUtil.US);
		String name = RandomTestUtil.randomString();

		List<CommerceCatalog> commerceCatalogs =
			CommerceCatalogLocalServiceUtil.getCommerceCatalogs(
				_group.getCompanyId(), true);

		CommerceCatalog commerceCatalog = commerceCatalogs.get(0);

		CommercePriceList commercePriceList =
			CommercePriceListTestUtil.addOrUpdateCommercePriceList(
				null, commerceCatalog.getGroupId(), 1,
				currency.getCurrencyCode(), name, RandomTestUtil.randomDouble(),
				true, null, null);

		_assertPriceListAttributes(currency, name, commercePriceList);
	}

	@Test
	public void testAddOrUpdateCommercePriceList6() throws Exception {
		frutillaRule.scenario(
			"Adding a new Price List"
		).given(
			"A site (group)"
		).and(
			"A currency code expressed with 3-letter ISO 4217 format"
		).and(
			"A parent price list ID"
		).and(
			"The name of the new list"
		).when(
			"The external reference code (externalReferenceCode)"
		).and(
			"(commercePriceListId) is not used in the method invocation"
		).then(
			"The result should be a new Price List with the given " +
				"(parentCommercePriceListId) on the given site"
		);

		Currency currency = Currency.getInstance(LocaleUtil.US);
		String name = RandomTestUtil.randomString();
		long parentCommercePriceListId = RandomTestUtil.randomLong();

		List<CommerceCatalog> commerceCatalogs =
			CommerceCatalogLocalServiceUtil.getCommerceCatalogs(
				_group.getCompanyId(), true);

		CommerceCatalog commerceCatalog = commerceCatalogs.get(0);

		CommercePriceList commercePriceList =
			CommercePriceListTestUtil.addOrUpdateCommercePriceList(
				null, commerceCatalog.getGroupId(), 1,
				currency.getCurrencyCode(), parentCommercePriceListId, name,
				RandomTestUtil.randomDouble(), true, null, null);

		Assert.assertThat(
			commercePriceList.getParentCommercePriceListId(),
			CoreMatchers.equalTo(parentCommercePriceListId));
	}

	@Test
	public void testAddOrUpdateCommercePriceList7() throws Exception {
		frutillaRule.scenario(
			"Update an existing Price List"
		).given(
			"A site (group)"
		).and(
			"A currency code expressed with 3-letter ISO 4217 format"
		).and(
			"A parent price list ID"
		).and(
			"The name of the list"
		).when(
			"The external reference code (externalReferenceCode) is used"
		).and(
			"(commercePriceListId) is not used in the method invocation"
		).then(
			"The result should be the updated Price List with the given " +
				"(parentCommercePriceListId) on the given site"
		);

		Currency currency = Currency.getInstance(LocaleUtil.US);
		String name = RandomTestUtil.randomString();
		long parentCommercePriceListId = RandomTestUtil.randomLong();

		List<CommerceCatalog> commerceCatalogs =
			CommerceCatalogLocalServiceUtil.getCommerceCatalogs(
				_group.getCompanyId(), true);

		CommerceCatalog commerceCatalog = commerceCatalogs.get(0);

		CommercePriceList commercePriceList =
			CommercePriceListTestUtil.addOrUpdateCommercePriceList(
				null, commerceCatalog.getGroupId(), 1,
				currency.getCurrencyCode(), parentCommercePriceListId, name,
				RandomTestUtil.randomDouble(), true, null, null);

		Assert.assertThat(
			commercePriceList.getParentCommercePriceListId(),
			CoreMatchers.equalTo(parentCommercePriceListId));
	}

	@Test
	public void testCreateGrossPriceList() throws Exception {
		frutillaRule.scenario(
			"Adding a new Price List with gross price entries"
		).given(
			"A site (group)"
		).and(
			"A currency code expressed with 3-letter ISO 4217 format"
		).and(
			"The name of the new list"
		).when(
			"The name of the Price List"
		).and(
			"The currency code are checked against the input data"
		).then(
			"The result should be a new Price List on the given site"
		);

		Currency currency = Currency.getInstance(LocaleUtil.US);
		String name = RandomTestUtil.randomString();

		List<CommerceCatalog> commerceCatalogs =
			CommerceCatalogLocalServiceUtil.getCommerceCatalogs(
				_group.getCompanyId(), true);

		CommerceCatalog commerceCatalog = commerceCatalogs.get(0);

		CommercePriceList commercePriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				null, commerceCatalog.getGroupId(), currency.getCurrencyCode(),
				false, name, RandomTestUtil.randomDouble(), true, null, null);

		_assertPriceListAttributes(currency, name, commercePriceList);

		Assert.assertFalse(commercePriceList.isNetPrice());
	}

	@Test
	public void testGetCommercePriceListsWithoutVisibleCatalogs()
		throws Exception {

		List<CommerceCatalog> commerceCatalogs =
			CommerceCatalogLocalServiceUtil.getCommerceCatalogs(
				_company.getCompanyId(), true);

		CommerceCatalog commerceCatalog = commerceCatalogs.get(0);

		CommercePriceListTestUtil.addCommercePriceList(
			null, commerceCatalog.getGroupId(),
			Currency.getInstance(
				LocaleUtil.US
			).getCurrencyCode(),
			RandomTestUtil.randomString(), RandomTestUtil.randomDouble(), true,
			null, null);

		User user = UserTestUtil.addUser(_company);

		Role role = RoleTestUtil.addRole(
			RandomTestUtil.randomString(), RoleConstants.TYPE_REGULAR,
			CommercePriceList.class.getName(), ResourceConstants.SCOPE_COMPANY,
			String.valueOf(_company.getCompanyId()), ActionKeys.VIEW);

		_userLocalService.addRoleUser(role.getRoleId(), user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, PermissionCheckerFactoryUtil.create(user))) {

			List<CommercePriceList> commercePriceLists =
				_commercePriceListService.getCommercePriceLists(
					_company.getCompanyId(), WorkflowConstants.STATUS_APPROVED,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

			Assert.assertTrue(
				commercePriceLists.toString(), commercePriceLists.isEmpty());

			Assert.assertEquals(
				0,
				_commercePriceListService.getCommercePriceListsCount(
					_company.getCompanyId(),
					WorkflowConstants.STATUS_APPROVED));
		}
	}

	@Test
	public void testGetOrAddEmptyCommercePriceList() throws Exception {
		frutillaRule.scenario(
			"Get or add an empty commerce price list"
		).given(
			"A company and an external reference code"
		).when(
			"An empty commerce price list is requested"
		).then(
			"A NoSuchPriceListException is thrown while lazy referencing is " +
				"disabled"
		).and(
			"An empty stub with the given external reference code is " +
				"returned while lazy referencing is enabled"
		).and(
			"The same commerce price list is resolved on subsequent requests"
		).and(
			"The empty status is cleared once the stub is updated"
		);

		User guestUser = _company.getGuestUser();

		String externalReferenceCode = RandomTestUtil.randomString();

		try {
			_commercePriceListLocalService.getOrAddEmptyCommercePriceList(
				externalReferenceCode, _group.getGroupId(),
				_company.getCompanyId(), guestUser.getUserId());

			Assert.fail();
		}
		catch (NoSuchPriceListException noSuchPriceListException) {
			Assert.assertNotNull(noSuchPriceListException);
		}

		CommercePriceList commercePriceList;

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			commercePriceList =
				_commercePriceListLocalService.getOrAddEmptyCommercePriceList(
					externalReferenceCode, _group.getGroupId(),
					_company.getCompanyId(), guestUser.getUserId());

			Assert.assertEquals(
				WorkflowConstants.STATUS_EMPTY, commercePriceList.getStatus());
			Assert.assertEquals(
				externalReferenceCode,
				commercePriceList.getExternalReferenceCode());

			CommercePriceList resolvedCommercePriceList =
				_commercePriceListLocalService.getOrAddEmptyCommercePriceList(
					externalReferenceCode, _group.getGroupId(),
					_company.getCompanyId(), guestUser.getUserId());

			Assert.assertEquals(
				commercePriceList.getCommercePriceListId(),
				resolvedCommercePriceList.getCommercePriceListId());
		}

		Currency currency = Currency.getInstance(LocaleUtil.US);

		Calendar calendar = CalendarFactoryUtil.getCalendar();

		commercePriceList =
			_commercePriceListLocalService.updateCommercePriceList(
				commercePriceList.getCommercePriceListId(), 0, false,
				currency.getCurrencyCode(), calendar.get(Calendar.DATE),
				calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.YEAR), 0, 0, 0, 0, 0,
				RandomTestUtil.randomString(), false, true, 0,
				CommercePriceListConstants.TYPE_PRICE_LIST,
				ServiceContextTestUtil.getServiceContext(
					_company.getCompanyId(), _group.getGroupId(),
					guestUser.getUserId()));

		Assert.assertNotEquals(
			WorkflowConstants.STATUS_EMPTY, commercePriceList.getStatus());
	}

	@Test
	public void testUpdateCommercePriceList1() throws Exception {
		frutillaRule.scenario(
			"Update an existing Price List"
		).given(
			"An existing Price List"
		).and(
			"A new currency code expressed with 3-letter ISO 4217 format"
		).and(
			"A new name for the list"
		).and(
			"A new display date"
		).and(
			"A new expiration date"
		).when(
			"The new values are used in the method invocation"
		).and(
			"Model getters are used for all other method invocation parameters"
		).then(
			"The result should be an updated Price List with only the " +
				"currency code, name, display date, and expiration date changed"
		);

		Currency currency = Currency.getInstance(LocaleUtil.US);
		String name = RandomTestUtil.randomString();

		List<CommerceCatalog> commerceCatalogs =
			CommerceCatalogLocalServiceUtil.getCommerceCatalogs(
				_group.getCompanyId(), true);

		CommerceCatalog commerceCatalog = commerceCatalogs.get(0);

		CommercePriceList commercePriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				null, commerceCatalog.getGroupId(), currency.getCurrencyCode(),
				name, RandomTestUtil.randomDouble(), true, null, null);

		currency = Currency.getInstance(LocaleUtil.UK);
		name = RandomTestUtil.randomString();
		Date displayDate = new Date();
		Date expirationDate = new Date(
			System.currentTimeMillis() + Integer.MAX_VALUE);

		CommercePriceList updatedCommercePriceList =
			CommercePriceListTestUtil.updateCommercePriceList(
				commercePriceList.getGroupId(),
				commercePriceList.getCommercePriceListId(),
				commercePriceList.getParentCommercePriceListId(),
				commercePriceList.isCatalogBasePriceList(),
				currency.getCurrencyCode(), displayDate, expirationDate, name,
				commercePriceList.isNetPrice(), false,
				commercePriceList.getPriority());

		_assertPriceListAttributes(currency, name, updatedCommercePriceList);

		Assert.assertThat(
			updatedCommercePriceList.getDisplayDate(),
			CoreMatchers.equalTo(_truncateSeconds(displayDate)));
		Assert.assertThat(
			updatedCommercePriceList.getExpirationDate(),
			CoreMatchers.equalTo(_truncateSeconds(expirationDate)));

		Assert.assertThat(
			updatedCommercePriceList.getGroupId(),
			CoreMatchers.equalTo(commercePriceList.getGroupId()));
		Assert.assertThat(
			updatedCommercePriceList.getCommercePriceListId(),
			CoreMatchers.equalTo(commercePriceList.getCommercePriceListId()));
		Assert.assertThat(
			updatedCommercePriceList.getParentCommercePriceListId(),
			CoreMatchers.equalTo(
				commercePriceList.getParentCommercePriceListId()));
		Assert.assertThat(
			updatedCommercePriceList.getPriority(),
			CoreMatchers.equalTo(commercePriceList.getPriority()));
	}

	@Test(expected = NoSuchPriceListException.class)
	public void testUpdateCommercePriceList2() throws Exception {
		frutillaRule.scenario(
			"Update a nonexistent Price List"
		).given(
			"A nonexistent Price List ID"
		).when(
			"The value is used in the method invocation"
		).then(
			"NoSuchPriceListException should be thrown"
		);

		long commercePriceListId = RandomTestUtil.randomLong();
		Currency currency = Currency.getInstance(LocaleUtil.US);

		List<CommerceCatalog> commerceCatalogs =
			CommerceCatalogLocalServiceUtil.getCommerceCatalogs(
				_group.getCompanyId(), true);

		CommerceCatalog commerceCatalog = commerceCatalogs.get(0);

		CommercePriceListTestUtil.updateCommercePriceList(
			commerceCatalog.getGroupId(), commercePriceListId, 0, false,
			currency.getCurrencyCode(), null, null,
			RandomTestUtil.randomString(), true, true,
			RandomTestUtil.randomDouble());
	}

	@Test
	public void testUpdateCommercePriceList3() throws Exception {
		frutillaRule.scenario(
			"Update an existing Price List"
		).given(
			"An existing Price List"
		).and(
			"A new parent price list ID (parentCommercePriceListId)"
		).when(
			"The new value is used in the method invocation"
		).then(
			"The result should be the new (parentCommercePriceListId) set on " +
				"the Price List"
		);

		Currency currency = Currency.getInstance(LocaleUtil.US);
		String name = RandomTestUtil.randomString();

		List<CommerceCatalog> commerceCatalogs =
			CommerceCatalogLocalServiceUtil.getCommerceCatalogs(
				_group.getCompanyId(), true);

		CommerceCatalog commerceCatalog = commerceCatalogs.get(0);

		CommercePriceList commercePriceList =
			CommercePriceListTestUtil.addCommercePriceList(
				null, commerceCatalog.getGroupId(), currency.getCurrencyCode(),
				0, name, RandomTestUtil.randomDouble(), true, null, null);

		long parentCommercePriceListId = RandomTestUtil.randomLong();

		CommercePriceList updatedCommercePriceList =
			CommercePriceListTestUtil.updateCommercePriceList(
				commerceCatalog.getGroupId(),
				commercePriceList.getCommercePriceListId(),
				parentCommercePriceListId,
				commercePriceList.isCatalogBasePriceList(),
				currency.getCurrencyCode(), commercePriceList.getDisplayDate(),
				commercePriceList.getExpirationDate(), name,
				commercePriceList.isNetPrice(), false,
				commercePriceList.getPriority());

		Assert.assertThat(
			updatedCommercePriceList.getParentCommercePriceListId(),
			CoreMatchers.equalTo(parentCommercePriceListId));
	}

	@Rule
	public final FrutillaRule frutillaRule = new FrutillaRule();

	private void _assertPriceListAttributes(
			Currency currency, String name, CommercePriceList commercePriceList)
		throws Exception {

		CommerceCurrency commerceCurrency =
			commercePriceList.getCommerceCurrency();

		Assert.assertThat(
			Currency.getInstance(commerceCurrency.getCode()),
			CoreMatchers.equalTo(currency));

		Assert.assertThat(
			commercePriceList.getName(), CoreMatchers.equalTo(name));
	}

	private Date _truncateSeconds(Date date) {
		Calendar calendar = CalendarFactoryUtil.getCalendar();

		calendar.setTime(date);

		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}

	private static Company _company;

	@Inject
	private CommercePriceListLocalService _commercePriceListLocalService;

	@Inject
	private CommercePriceListService _commercePriceListService;

	private Group _group;

	@Inject
	private UserLocalService _userLocalService;

}