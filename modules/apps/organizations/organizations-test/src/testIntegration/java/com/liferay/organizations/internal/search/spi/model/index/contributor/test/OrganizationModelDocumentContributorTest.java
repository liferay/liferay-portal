/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.organizations.internal.search.spi.model.index.contributor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.search.ReindexCacheThreadLocal;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.CountryService;
import com.liferay.portal.kernel.service.ListTypeService;
import com.liferay.portal.kernel.service.RegionService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jiefeng Wu
 */
@RunWith(Arquillian.class)
public class OrganizationModelDocumentContributorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testContributeAddressInReindexMode() throws Exception {
		_organization = OrganizationTestUtil.addOrganization();

		Country country = _countryService.getCountryByName(
			_organization.getCompanyId(), "canada");

		Region region = _regionService.getRegion(country.getCountryId(), "AB");

		List<ListType> listTypes = _listTypeService.getListTypes(
			_organization.getCompanyId(),
			ListTypeConstants.ORGANIZATION_ADDRESS);

		ListType listType = listTypes.get(0);

		_addressLocalService.addAddress(
			null, TestPropsValues.getUserId(), Organization.class.getName(),
			_organization.getOrganizationId(), country.getCountryId(),
			listType.getListTypeId(), region.getRegionId(),
			RandomTestUtil.randomString(), null, false, null, false,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			null, new ServiceContext());

		Document document = new DocumentImpl();

		_organizationModelDocumentContributor.contribute(
			document, _organization);

		_assertAddressFields(document, region);

		Document reindexModeDocument = new DocumentImpl();

		try (SafeCloseable safeCloseable =
				ReindexCacheThreadLocal.openReindexMode()) {

			_organizationModelDocumentContributor.contribute(
				reindexModeDocument, _organization);
		}

		_assertAddressFields(reindexModeDocument, region);
	}

	private void _assertAddressFields(Document document, Region region) {
		Assert.assertTrue(
			ArrayUtil.contains(document.getValues("country"), "canada"));
		Assert.assertArrayEquals(
			new String[] {StringUtil.toLowerCase(region.getName())},
			document.getValues("region"));
	}

	@Inject
	private AddressLocalService _addressLocalService;

	@Inject
	private CountryService _countryService;

	@Inject
	private ListTypeService _listTypeService;

	@DeleteAfterTestRun
	private Organization _organization;

	@Inject(
		filter = "component.name=com.liferay.organizations.internal.search.spi.model.index.contributor.OrganizationModelDocumentContributor"
	)
	private ModelDocumentContributor<Organization>
		_organizationModelDocumentContributor;

	@Inject
	private RegionService _regionService;

}