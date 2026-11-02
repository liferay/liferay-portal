/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.address.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.CountryTitleException;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.CountryLocalization;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.RegionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Albert Lee
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class CountryLocalServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAddCountry() throws Exception {
		boolean billingAllowed = RandomTestUtil.randomBoolean();
		String number = RandomTestUtil.randomString();
		double position = RandomTestUtil.randomDouble();
		boolean shippingAllowed = RandomTestUtil.randomBoolean();
		boolean subjectToVAT = RandomTestUtil.randomBoolean();
		boolean zipRequired = RandomTestUtil.randomBoolean();

		Country country = _addCountry(
			billingAllowed, number, position, shippingAllowed, subjectToVAT,
			zipRequired);

		Assert.assertEquals(billingAllowed, country.isBillingAllowed());
		Assert.assertEquals(number, country.getNumber());
		Assert.assertEquals(position, country.getPosition(), 0);
		Assert.assertEquals(shippingAllowed, country.isShippingAllowed());
		Assert.assertEquals(subjectToVAT, country.isSubjectToVAT());
		Assert.assertEquals(zipRequired, country.isZipRequired());

		Assert.assertNotNull(
			_countryLocalService.fetchCountry(country.getCountryId()));
	}

	@Test
	public void testDeleteCountry() throws Exception {
		Country country = _addCountry(
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomString(),
			RandomTestUtil.randomDouble(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean());

		User user = TestPropsValues.getUser();

		Address address = _addressLocalService.addAddress(
			null, user.getUserId(), null, user.getContactId(),
			country.getCountryId(), RandomTestUtil.randomLong(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), false,
			RandomTestUtil.randomString(), false, RandomTestUtil.randomString(),
			null, null, null, RandomTestUtil.randomString(), "1234567890",
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(country.getCountryId(), address.getCountryId());

		Organization organization = OrganizationTestUtil.addOrganization();

		organization.setCountryId(country.getCountryId());

		organization = _organizationLocalService.updateOrganization(
			organization);

		Assert.assertFalse(
			ListUtil.isEmpty(
				_organizationLocalService.search(
					country.getCompanyId(),
					OrganizationConstants.ANY_PARENT_ORGANIZATION_ID, null,
					null, null, country.getCountryId(), null, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS)));

		Region region = _addRegion(country.getCountryId());

		_regionLocalService.updateRegionLocalization(
			region, RandomTestUtil.randomString(2),
			RandomTestUtil.randomString());

		_countryLocalService.deleteCountry(country);

		Assert.assertNull(
			_countryLocalService.fetchCountry(country.getCountryId()));

		Assert.assertNull(
			_addressLocalService.fetchAddress(address.getAddressId()));

		organization = _organizationLocalService.getOrganization(
			organization.getOrganizationId());

		Assert.assertEquals(0, organization.getCountryId());

		Assert.assertTrue(
			ListUtil.isEmpty(
				_organizationLocalService.search(
					country.getCompanyId(),
					OrganizationConstants.ANY_PARENT_ORGANIZATION_ID, null,
					null, null, country.getCountryId(), null, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS)));
		Assert.assertNull(
			_regionLocalService.fetchRegion(region.getRegionId()));
		Assert.assertTrue(
			ListUtil.isEmpty(
				_regionLocalService.getRegionLocalizations(
					region.getRegionId())));
	}

	@Test
	public void testSearchCountries() throws Exception {
		Country country1 = _addCountry(
			"a1", "a11", true, RandomTestUtil.randomString() + "_a11");

		String keywords = RandomTestUtil.randomString();

		Country country2 = _addCountry("a2", "a22", true, keywords + "_a22");
		Country country3 = _addCountry("a3", "a33", true, keywords + "_a33");
		Country country4 = _addCountry("a4", "a44", false, keywords + "_a44");

		_testSearchCountries(keywords, true, country2, country3);
		_testSearchCountries(keywords, false, country4);
		_testSearchCountries(keywords, null, country2, country3, country4);

		String localizedCountryName = RandomTestUtil.randomString();

		_countryLocalService.updateCountryLocalization(
			country1, "de_DE", localizedCountryName);

		_testSearchCountries(localizedCountryName, true, country1);
	}

	@Test
	public void testSearchCountriesByISOCodes() throws Exception {
		String a2 = "a1";
		String a3 = "a11";
		String number = "aaa";

		Country country = _addCountry(
			a2, a3, true, RandomTestUtil.randomString(), number);

		_testSearchCountries(a2, true, country);
		_testSearchCountries(a3, true, country);
		_testSearchCountries(number, true, country);
	}

	@Test
	public void testSearchCountriesPagination() throws Exception {
		String keywords = RandomTestUtil.randomString();

		List<Country> expectedCountries = Arrays.asList(
			_addCountry(
				"a1", "a11", true, keywords + RandomTestUtil.randomString()),
			_addCountry(
				"a2", "a22", true, keywords + RandomTestUtil.randomString()),
			_addCountry(
				"a3", "a33", true, keywords + RandomTestUtil.randomString()),
			_addCountry(
				"a4", "a44", true, keywords + RandomTestUtil.randomString()),
			_addCountry(
				"a5", "a55", true, keywords + RandomTestUtil.randomString()));

		_addCountry(
			"a6", "a66", false, keywords + RandomTestUtil.randomString());

		_assertSearchCountriesPaginationSort(
			expectedCountries, keywords,
			OrderByComparatorFactoryUtil.create("Country", "countryId", true),
			ServiceContextTestUtil.getServiceContext());

		Comparator<Country> comparator = Comparator.comparing(
			Country::getCountryId);

		_assertSearchCountriesPaginationSort(
			ListUtil.sort(expectedCountries, comparator.reversed()), keywords,
			OrderByComparatorFactoryUtil.create("Country", "countryId", false),
			ServiceContextTestUtil.getServiceContext());
	}

	@Test
	public void testUpdateCountry() throws Exception {
		boolean billingAllowed = RandomTestUtil.randomBoolean();
		boolean shippingAllowed = RandomTestUtil.randomBoolean();
		boolean subjectToVAT = RandomTestUtil.randomBoolean();

		Country country = _addCountry(
			billingAllowed, RandomTestUtil.randomString(),
			RandomTestUtil.randomDouble(), shippingAllowed, subjectToVAT, true);

		String number = String.valueOf(9999 + RandomTestUtil.nextInt());
		int position = RandomTestUtil.randomInt();

		Country updatedCountry = _countryLocalService.updateCountry(
			country.getExternalReferenceCode(), country.getCountryId(),
			country.getA2(), country.getA3(), country.isActive(),
			!billingAllowed, country.getIdd(), country.getName(), number,
			position, !shippingAllowed, !subjectToVAT);

		Assert.assertEquals(!billingAllowed, updatedCountry.isBillingAllowed());
		Assert.assertEquals(number, updatedCountry.getNumber());
		Assert.assertEquals(position, updatedCountry.getPosition(), 0);
		Assert.assertEquals(
			!shippingAllowed, updatedCountry.isShippingAllowed());
		Assert.assertEquals(!subjectToVAT, updatedCountry.isSubjectToVAT());
	}

	@Test(expected = CountryTitleException.MustNotExceedMaximumLength.class)
	public void testUpdateCountryLocalizations() throws Exception {
		int maxTitleLength = ModelHintsUtil.getMaxLength(
			CountryLocalization.class.getName(), "title");

		_countryLocalService.updateCountryLocalizations(
			_countryLocalService.createCountry(0L),
			Collections.singletonMap(
				"de_DE", RandomTestUtil.randomString(maxTitleLength + 1)));
	}

	@Test
	public void testUpdateCountryWhenStatusIsEmpty() throws Exception {
		Country country = _addCountry(
			"aa", "aaa", false, RandomTestUtil.randomString());

		country.setStatus(WorkflowConstants.STATUS_EMPTY);

		country = _countryLocalService.updateCountry(country);

		String name = RandomTestUtil.randomString();
		String number = String.valueOf(RandomTestUtil.nextInt());

		country = _countryLocalService.updateCountry(
			country.getExternalReferenceCode(), country.getCountryId(),
			country.getA2(), country.getA3(), true, false, null, name, number,
			0D, false, false);

		Assert.assertEquals(name, country.getName());
		Assert.assertEquals(number, country.getNumber());
		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, country.getStatus());

		_countryLocalService.deleteCountry(country);
	}

	private Country _addCountry(
			boolean billingAllowed, String number, double position,
			boolean shippingAllowed, boolean subjectToVAT, boolean zipRequired)
		throws Exception {

		return _countryLocalService.addCountry(
			null, "aa", "aaa", true, billingAllowed,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			number, position, shippingAllowed, subjectToVAT, zipRequired,
			ServiceContextTestUtil.getServiceContext());
	}

	private Country _addCountry(
			String a2, String a3, boolean active, String name)
		throws Exception {

		return _addCountry(a2, a3, active, name, RandomTestUtil.randomString());
	}

	private Country _addCountry(
			String a2, String a3, boolean active, String name, String number)
		throws Exception {

		return _countryLocalService.addCountry(
			null, a2, a3, active, RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomString(), name, number,
			RandomTestUtil.randomDouble(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean(),
			ServiceContextTestUtil.getServiceContext());
	}

	private Region _addRegion(long countryId) throws Exception {
		return _regionLocalService.addRegion(
			null, countryId, RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomString(), RandomTestUtil.randomDouble(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext());
	}

	private void _assertSearchCountriesPaginationSort(
			List<Country> expectedCountries, String keywords,
			OrderByComparator<Country> orderByComparator,
			ServiceContext serviceContext)
		throws Exception {

		int end = 3;
		int start = 1;

		BaseModelSearchResult<Country> baseModelSearchResult =
			_countryLocalService.searchCountries(
				serviceContext.getCompanyId(), true, keywords, start, end,
				orderByComparator);

		List<Country> actualCountries = baseModelSearchResult.getBaseModels();

		Assert.assertEquals(
			actualCountries.toString(), end - start, actualCountries.size());

		for (int i = 0; i < (end - start); i++) {
			Assert.assertEquals(
				expectedCountries.get(start + i), actualCountries.get(i));
		}
	}

	private void _testSearchCountries(
			String keywords, Boolean active, Country... countries)
		throws Exception {

		List<Country> expectedCountries = Arrays.asList(countries);

		Arrays.sort(
			countries,
			Comparator.comparing(
				Country::getName, String.CASE_INSENSITIVE_ORDER));

		BaseModelSearchResult<Country> baseModelSearchResult =
			_countryLocalService.searchCountries(
				TestPropsValues.getCompanyId(), active, keywords,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				OrderByComparatorFactoryUtil.create("Country", "name", true));

		Assert.assertEquals(
			expectedCountries.size(), baseModelSearchResult.getLength());
		Assert.assertEquals(
			expectedCountries, baseModelSearchResult.getBaseModels());
	}

	@Inject
	private AddressLocalService _addressLocalService;

	@Inject
	private CountryLocalService _countryLocalService;

	@Inject
	private OrganizationLocalService _organizationLocalService;

	@Inject
	private RegionLocalService _regionLocalService;

}