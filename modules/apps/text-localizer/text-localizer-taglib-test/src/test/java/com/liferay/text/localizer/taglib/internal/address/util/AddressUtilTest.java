/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.text.localizer.taglib.internal.address.util;

import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.AddressWrapper;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.CountryWrapper;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.model.RegionWrapper;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Locale;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Drew Brokke
 */
public class AddressUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		_locale = LocaleThreadLocal.getThemeDisplayLocale();

		LocaleThreadLocal.setThemeDisplayLocale(LocaleUtil.getDefault());

		_localeStringMap = RandomTestUtil.randomLocaleStringMap(
			LocaleThreadLocal.getThemeDisplayLocale());
	}

	@AfterClass
	public static void tearDownClass() {
		LocaleThreadLocal.setThemeDisplayLocale(_locale);
	}

	@Test
	public void testGetCountryNameOptional() {
		_testGetCountryNameOptional(null, null);
		_testGetCountryNameOptional(
			new AddressWrapper(null) {

				@Override
				public Country getCountry() {
					return null;
				}

			},
			null);
		_testGetCountryNameOptional(
			new AddressWrapper(null) {

				@Override
				public Country getCountry() {
					return new CountryWrapper(null) {

						@Override
						public boolean isNew() {
							return true;
						}

					};
				}

			},
			null);
		_testGetCountryNameOptional(
			new AddressWrapper(null) {

				@Override
				public Country getCountry() {
					return new CountryWrapper(null) {

						@Override
						public String getTitle(Locale locale) {
							return _localeStringMap.get(locale);
						}

						@Override
						public boolean isNew() {
							return false;
						}

					};
				}

			},
			_localeStringMap.get(LocaleUtil.getDefault()));
	}

	@Test
	public void testGetRegionNameOptional() {
		_testGetRegionNameOptional(null, null);
		_testGetRegionNameOptional(
			new AddressWrapper(null) {

				@Override
				public Region getRegion() {
					return null;
				}

			},
			null);
		_testGetRegionNameOptional(
			new AddressWrapper(null) {

				@Override
				public Region getRegion() {
					return new RegionWrapper(null) {

						@Override
						public boolean isNew() {
							return true;
						}

					};
				}

			},
			null);
		_testGetRegionNameOptional(
			new AddressWrapper(null) {

				@Override
				public Region getRegion() {
					return new RegionWrapper(null) {

						@Override
						public String getName() {
							return _localeStringMap.get(
								LocaleUtil.getDefault());
						}

						@Override
						public boolean isNew() {
							return false;
						}

					};
				}

			},
			_localeStringMap.get(LocaleUtil.getDefault()));
	}

	private void _testGetCountryNameOptional(
		Address address, String expectedName) {

		Assert.assertEquals(expectedName, AddressUtil.getCountryName(address));
	}

	private void _testGetRegionNameOptional(
		Address address, String expectedName) {

		Assert.assertEquals(expectedName, AddressUtil.getRegionName(address));
	}

	private static Locale _locale;
	private static Map<Locale, String> _localeStringMap;

}