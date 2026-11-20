/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.dto.v1_0.converter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.user.dto.v1_0.PostalAddress;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CountryTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
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
 * @author Brian I. Kim
 */
@RunWith(Arquillian.class)
public class PostalAddressResourceDTOConverterTest {

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

		User user = TestPropsValues.getUser();

		_address = _addressLocalService.addAddress(
			null, user.getUserId(), null, user.getContactId(),
			country.getCountryId(), RandomTestUtil.randomLong(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), false,
			RandomTestUtil.randomString(), false, RandomTestUtil.randomString(),
			null, null, null, RandomTestUtil.randomString(), "1234567890",
			ServiceContextTestUtil.getServiceContext(
				GroupTestUtil.addGroup(), TestPropsValues.getUserId()));
	}

	@Test
	public void testToPostalAddress() throws Exception {
		Set<Locale> originalAvailableLocales =
			_language.getCompanyAvailableLocales(
				TestPropsValues.getCompanyId());

		_companyLocalService.updatePreferences(
			TestPropsValues.getCompanyId(),
			UnicodePropertiesBuilder.put(
				"locales", "en_US,pt_BR"
			).build());

		PostalAddress postalAddress = _getPostalAddress(_address);

		Map<String, String> titleMap = postalAddress.getAddressCountry_i18n();

		Assert.assertTrue(titleMap.containsKey("en_US"));
		Assert.assertTrue(titleMap.containsKey("pt_BR"));
		Assert.assertEquals(titleMap.toString(), 2, titleMap.size());

		_companyLocalService.updatePreferences(
			TestPropsValues.getCompanyId(),
			UnicodePropertiesBuilder.put(
				"locales", StringUtil.merge(originalAvailableLocales)
			).build());
	}

	private PostalAddress _getPostalAddress(Address address) throws Exception {
		DTOConverter<Address, PostalAddress> postalAddressDTOConverter =
			(DTOConverter<Address, PostalAddress>)
				_dtoConverterRegistry.getDTOConverter(
					"Liferay.Headless.Admin.User", Address.class.getName(),
					"v1.0");

		DTOConverterContext dtoConverterContext =
			new DefaultDTOConverterContext(
				true, _dtoConverterRegistry, _address.getAddressId(),
				LocaleUtil.getDefault(), null, null);

		return postalAddressDTOConverter.toDTO(dtoConverterContext, address);
	}

	private Address _address;

	@Inject
	private AddressLocalService _addressLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private CountryLocalService _countryLocalService;

	@Inject
	private DTOConverterRegistry _dtoConverterRegistry;

	@Inject
	private Language _language;

}