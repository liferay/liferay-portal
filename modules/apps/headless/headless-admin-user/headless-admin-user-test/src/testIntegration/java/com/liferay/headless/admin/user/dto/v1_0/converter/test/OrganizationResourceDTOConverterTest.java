/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.dto.v1_0.converter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.user.dto.v1_0.Location;
import com.liferay.headless.admin.user.dto.v1_0.Organization;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CountryTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Balazs Breier
 */
@RunWith(Arquillian.class)
public class OrganizationResourceDTOConverterTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		Country country = _countryLocalService.addCountry(
			null, CountryTestUtil.randomA2(TestPropsValues.getCompanyId()),
			CountryTestUtil.randomA3(TestPropsValues.getCompanyId()),
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomString(),
			StringUtil.toLowerCase(RandomTestUtil.randomString()),
			RandomTestUtil.randomString(), RandomTestUtil.randomDouble(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomBoolean(),
			ServiceContextTestUtil.getServiceContext(
				GroupTestUtil.addGroup(), TestPropsValues.getUserId()));

		Map<String, String> titleMap = new HashMap<>();

		for (Locale locale : _language.getAvailableLocales()) {
			titleMap.put(_language.getLanguageId(locale), null);
		}

		_countryLocalService.updateCountryLocalizations(country, titleMap);

		_serviceBuilderOrganization = _organizationLocalService.addOrganization(
			null, TestPropsValues.getUserId(),
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(),
			OrganizationConstants.TYPE_ORGANIZATION, 0, country.getCountryId(),
			_listTypeLocalService.getListTypeId(
				TestPropsValues.getCompanyId(),
				ListTypeConstants.ORGANIZATION_STATUS_DEFAULT,
				ListTypeConstants.ORGANIZATION_STATUS),
			RandomTestUtil.randomString(), true,
			ServiceContextTestUtil.getServiceContext(
				GroupTestUtil.addGroup(), TestPropsValues.getUserId()));
	}

	@Test
	public void testToDTO() throws Exception {
		Set<Locale> originalAvailableLocales =
			_language.getCompanyAvailableLocales(
				TestPropsValues.getCompanyId());

		StringBuilder sb = new StringBuilder();

		for (Locale locale : originalAvailableLocales) {
			sb.append(locale.toString());
			sb.append(",");
		}

		_companyLocalService.updatePreferences(
			TestPropsValues.getCompanyId(),
			UnicodePropertiesBuilder.put(
				"locales", "en_US,pt_BR"
			).build());

		Organization organization = _getOrganization(
			_serviceBuilderOrganization);

		Location location = organization.getLocation();

		Map<String, String> titleMap = location.getAddressCountry_i18n();

		Assert.assertEquals(titleMap.toString(), 2, titleMap.size());
		Assert.assertTrue(titleMap.containsKey("en_US"));
		Assert.assertTrue(titleMap.containsKey("pt_BR"));

		_companyLocalService.updatePreferences(
			TestPropsValues.getCompanyId(),
			UnicodePropertiesBuilder.put(
				"locales", sb.toString()
			).build());
	}

	private Organization _getOrganization(
			com.liferay.portal.kernel.model.Organization
				serviceBuilderOrganization)
		throws Exception {

		DTOConverter<com.liferay.portal.kernel.model.Organization, Organization>
			organizationDTOConverter =
				(DTOConverter
					<com.liferay.portal.kernel.model.Organization,
					 Organization>)_dtoConverterRegistry.getDTOConverter(
						 "Liferay.Headless.Admin.User",
						 com.liferay.portal.kernel.model.Organization.class.
							 getName(),
						 "v1.0");

		DTOConverterContext dtoConverterContext =
			new DefaultDTOConverterContext(
				true, _dtoConverterRegistry,
				serviceBuilderOrganization.getOrganizationId(), null, null,
				null);

		return organizationDTOConverter.toDTO(
			dtoConverterContext, serviceBuilderOrganization);
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private CountryLocalService _countryLocalService;

	@Inject
	private DTOConverterRegistry _dtoConverterRegistry;

	@Inject
	private Language _language;

	@Inject
	private ListTypeLocalService _listTypeLocalService;

	@Inject
	private OrganizationLocalService _organizationLocalService;

	private com.liferay.portal.kernel.model.Organization
		_serviceBuilderOrganization;

}