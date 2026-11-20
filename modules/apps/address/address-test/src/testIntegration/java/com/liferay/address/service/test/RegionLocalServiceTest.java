/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.address.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.DuplicateRegionException;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Country;
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
import com.liferay.portal.kernel.test.util.CountryTestUtil;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
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
public class RegionLocalServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAddRegion() throws Exception {
		Country country = _addCountry();

		Region region = _addRegion(country.getCountryId());

		String languageId = RandomTestUtil.randomString(2);

		_regionLocalService.updateRegionLocalization(
			region, languageId, RandomTestUtil.randomString());

		Assert.assertNotNull(
			_regionLocalService.fetchRegion(region.getRegionId()));

		Assert.assertNotNull(
			_regionLocalService.getRegionLocalization(
				region.getRegionId(), languageId));

		try {
			_addRegion(
				true, region.getCountryId(), RandomTestUtil.randomString(),
				region.getRegionCode());

			Assert.fail();
		}
		catch (DuplicateRegionException duplicateRegionException) {
			Assert.assertNotNull(duplicateRegionException);
		}
	}

	@Test
	public void testDeleteRegion() throws Exception {
		User user = TestPropsValues.getUser();

		Country country = _addCountry();

		Region region = _addRegion(country.getCountryId());

		_regionLocalService.updateRegionLocalization(
			region, RandomTestUtil.randomString(2),
			RandomTestUtil.randomString());

		Address address = _addressLocalService.addAddress(
			null, user.getUserId(), null, user.getContactId(),
			region.getCountryId(), RandomTestUtil.randomLong(),
			region.getRegionId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), false, RandomTestUtil.randomString(),
			false, RandomTestUtil.randomString(), null, null, null,
			RandomTestUtil.randomString(), "1234567890",
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(region.getCountryId(), address.getCountryId());
		Assert.assertEquals(region.getRegionId(), address.getRegionId());

		Organization organization = OrganizationTestUtil.addOrganization();

		organization.setRegionId(region.getRegionId());
		organization.setCountryId(region.getCountryId());

		organization = _organizationLocalService.updateOrganization(
			organization);

		Assert.assertFalse(
			ListUtil.isEmpty(
				_organizationLocalService.search(
					country.getCompanyId(),
					OrganizationConstants.ANY_PARENT_ORGANIZATION_ID, null,
					null, region.getRegionId(), country.getCountryId(), null,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)));

		_regionLocalService.deleteRegion(region.getRegionId());

		Assert.assertNull(
			_regionLocalService.fetchRegion(region.getRegionId()));
		Assert.assertTrue(
			ListUtil.isEmpty(
				_regionLocalService.getRegionLocalizations(
					region.getRegionId())));

		Assert.assertNull(
			_addressLocalService.fetchAddress(address.getAddressId()));

		organization = _organizationLocalService.getOrganization(
			organization.getOrganizationId());

		Assert.assertEquals(0, organization.getRegionId());

		Assert.assertTrue(
			ListUtil.isEmpty(
				_organizationLocalService.search(
					country.getCompanyId(),
					OrganizationConstants.ANY_PARENT_ORGANIZATION_ID, null,
					null, region.getRegionId(), country.getCountryId(), null,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS)));
	}

	@Test
	public void testSearchRegions() throws Exception {
		String keywords = RandomTestUtil.randomString();

		Country country1 = _addCountry();
		Country country2 = _addCountry();

		Region region1 = _addRegion(
			true, country1.getCountryId(), RandomTestUtil.randomString());

		Region region2 = _addRegion(
			true, country1.getCountryId(),
			keywords + RandomTestUtil.randomString());
		Region region3 = _addRegion(
			true, country1.getCountryId(),
			keywords + RandomTestUtil.randomString());
		Region region4 = _addRegion(
			false, country1.getCountryId(),
			keywords + RandomTestUtil.randomString());

		Region region5 = _addRegion(
			true, country2.getCountryId(),
			keywords + RandomTestUtil.randomString());

		_testSearchRegions(true, keywords, null, region2, region3, region5);

		_testSearchRegions(
			true, keywords,
			LinkedHashMapBuilder.<String, Object>put(
				"countryId", country1.getCountryId()
			).build(),
			region2, region3);

		_testSearchRegions(false, keywords, null, region4);
		_testSearchRegions(
			null, keywords, null, region2, region3, region4, region5);

		String localizedRegionName = RandomTestUtil.randomString();

		_regionLocalService.updateRegionLocalization(
			region1, "de_DE", localizedRegionName);

		_testSearchRegions(true, localizedRegionName, null, region1);
	}

	@Test
	public void testSearchRegionsByRegionCode() throws Exception {
		Country country = _addCountry();

		String regionCode = "regionCode";

		Region region = _addRegion(
			true, country.getCountryId(), RandomTestUtil.randomString(),
			regionCode);

		_testSearchRegions(true, regionCode, null, region);
	}

	@Test
	public void testSearchRegionsPagination() throws Exception {
		String keywords = RandomTestUtil.randomString();

		Country country = _addCountry();

		List<Region> expectedRegions = Arrays.asList(
			_addRegion(
				true, country.getCountryId(),
				keywords + RandomTestUtil.randomString()),
			_addRegion(
				true, country.getCountryId(),
				keywords + RandomTestUtil.randomString()),
			_addRegion(
				true, country.getCountryId(),
				keywords + RandomTestUtil.randomString()),
			_addRegion(
				true, country.getCountryId(),
				keywords + RandomTestUtil.randomString()),
			_addRegion(
				true, country.getCountryId(),
				keywords + RandomTestUtil.randomString()));

		_addRegion(
			false, country.getCountryId(),
			keywords + RandomTestUtil.randomString());

		_assertSearchRegionsPaginationSort(
			expectedRegions, keywords,
			OrderByComparatorFactoryUtil.create("Region", "regionId", true),
			ServiceContextTestUtil.getServiceContext());

		Comparator<Region> comparator = Comparator.comparing(
			Region::getRegionId);

		_assertSearchRegionsPaginationSort(
			ListUtil.sort(expectedRegions, comparator.reversed()), keywords,
			OrderByComparatorFactoryUtil.create("Region", "regionId", false),
			ServiceContextTestUtil.getServiceContext());
	}

	@Test
	public void testUpdateRegion() throws Exception {
		Country country = _addCountry();

		Region region = _addRegion(country.getCountryId());

		boolean active = RandomTestUtil.randomBoolean();
		String name = RandomTestUtil.randomString();
		double position = RandomTestUtil.randomDouble();
		String regionCode = RandomTestUtil.randomString();

		region = _regionLocalService.updateRegion(
			region.getExternalReferenceCode(), region.getRegionId(), active,
			name, position, regionCode);

		Assert.assertEquals(active, region.isActive());
		Assert.assertEquals(name, region.getName());
		Assert.assertEquals(position, region.getPosition(), 0);
		Assert.assertEquals(regionCode, region.getRegionCode());
	}

	@Test
	public void testUpdateRegionWhenStatusIsEmpty() throws Exception {
		Country country = _addCountry();

		Region region = _addRegion(country.getCountryId());

		region.setStatus(WorkflowConstants.STATUS_EMPTY);

		region = _regionLocalService.updateRegion(region);

		String name = RandomTestUtil.randomString();
		String regionCode = RandomTestUtil.randomString();

		region = _regionLocalService.updateRegion(
			region.getExternalReferenceCode(), region.getRegionId(), true, name,
			0D, regionCode);

		Assert.assertEquals(name, region.getName());
		Assert.assertEquals(regionCode, region.getRegionCode());
		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, region.getStatus());
	}

	private Country _addCountry() throws Exception {
		return _countryLocalService.addCountry(
			null, CountryTestUtil.randomA2(TestPropsValues.getCompanyId()),
			CountryTestUtil.randomA3(TestPropsValues.getCompanyId()), true,
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomDouble(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean(),
			ServiceContextTestUtil.getServiceContext());
	}

	private Region _addRegion(boolean active, long countryId, String name)
		throws Exception {

		return _addRegion(
			active, countryId, name, RandomTestUtil.randomString());
	}

	private Region _addRegion(
			boolean active, long countryId, String name, String regionCode)
		throws Exception {

		return _regionLocalService.addRegion(
			null, countryId, active, name, RandomTestUtil.randomDouble(),
			regionCode, ServiceContextTestUtil.getServiceContext());
	}

	private Region _addRegion(long countryId) throws Exception {
		return _addRegion(
			RandomTestUtil.randomBoolean(), countryId,
			RandomTestUtil.randomString());
	}

	private void _assertSearchRegionsPaginationSort(
			List<Region> expectedRegions, String keywords,
			OrderByComparator<Region> orderByComparator,
			ServiceContext serviceContext)
		throws Exception {

		int end = 3;
		int start = 1;

		BaseModelSearchResult<Region> baseModelSearchResult =
			_regionLocalService.searchRegions(
				serviceContext.getCompanyId(), true, keywords, null, start, end,
				orderByComparator);

		List<Region> actualRegions = baseModelSearchResult.getBaseModels();

		Assert.assertEquals(
			actualRegions.toString(), end - start, actualRegions.size());

		for (int i = 0; i < (end - start); i++) {
			Assert.assertEquals(
				expectedRegions.get(start + i), actualRegions.get(i));
		}
	}

	private void _testSearchRegions(
			Boolean active, String keywords,
			LinkedHashMap<String, Object> params, Region... expectedRegions)
		throws Exception {

		BaseModelSearchResult<Region> baseModelSearchResult =
			_regionLocalService.searchRegions(
				TestPropsValues.getCompanyId(), active, keywords, params,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				OrderByComparatorFactoryUtil.create("Region", "name", true));

		Assert.assertEquals(
			expectedRegions.length, baseModelSearchResult.getLength());

		Assert.assertEquals(
			ListUtil.sort(
				Arrays.asList(expectedRegions),
				Comparator.comparing(
					Region::getName, String.CASE_INSENSITIVE_ORDER)),
			ListUtil.sort(
				baseModelSearchResult.getBaseModels(),
				Comparator.comparing(
					Region::getName, String.CASE_INSENSITIVE_ORDER)));
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