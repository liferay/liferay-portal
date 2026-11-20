/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.address.dto.v1_0.converter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.address.dto.v1_0.Country;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.CountryLocalService;
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
 * @author Matyas Wollner
 */
@RunWith(Arquillian.class)
public class CountryResourceDTOConverterTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_serviceBuilderCountry = _countryLocalService.addCountry(
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

		_countryLocalService.updateCountryLocalizations(
			_serviceBuilderCountry, titleMap);
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

		Country country = _getCountry(_serviceBuilderCountry);

		Map<String, String> titleMap = country.getTitle_i18n();

		Assert.assertEquals(titleMap.toString(), 2, titleMap.size());
		Assert.assertTrue(titleMap.containsKey("en_US"));
		Assert.assertTrue(titleMap.containsKey("pt_BR"));

		_companyLocalService.updatePreferences(
			TestPropsValues.getCompanyId(),
			UnicodePropertiesBuilder.put(
				"locales", sb.toString()
			).build());
	}

	private Country _getCountry(
			com.liferay.portal.kernel.model.Country serviceBuilderCountry)
		throws Exception {

		DTOConverter<com.liferay.portal.kernel.model.Country, Country>
			countryDTOConverter =
				(DTOConverter<com.liferay.portal.kernel.model.Country, Country>)
					_dtoConverterRegistry.getDTOConverter(
						com.liferay.portal.kernel.model.Country.class.
							getName());

		DTOConverterContext dtoConverterContext =
			new DefaultDTOConverterContext(
				_dtoConverterRegistry, serviceBuilderCountry.getCountryId(),
				null, null, null);

		return countryDTOConverter.toDTO(
			dtoConverterContext, serviceBuilderCountry);
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private CountryLocalService _countryLocalService;

	@Inject
	private DTOConverterRegistry _dtoConverterRegistry;

	@Inject
	private Language _language;

	private com.liferay.portal.kernel.model.Country _serviceBuilderCountry;

}