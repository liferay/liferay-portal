/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.engine.fixed.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.tax.engine.fixed.model.CommerceTaxFixedRateAddressRel;
import com.liferay.commerce.tax.engine.fixed.service.CommerceTaxFixedRateAddressRelLocalService;
import com.liferay.commerce.test.util.CommerceTaxTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class CommerceTaxFixedRateAddressRelLocalServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_cpTaxCategoryId = CommerceTaxTestUtil.addTaxCategoryId(
			TestPropsValues.getGroupId());
	}

	@Test
	public void testFetchCommerceTaxFixedRateAddressRel() throws Exception {
		_testFetchCommerceTaxFixedRateAddressRelAllCountries();
		_testFetchCommerceTaxFixedRateAddressRelCountry();
		_testFetchCommerceTaxFixedRateAddressRelGuest();
		_testFetchCommerceTaxFixedRateAddressRelOldest();
		_testFetchCommerceTaxFixedRateAddressRelOtherTaxCategory();
		_testFetchCommerceTaxFixedRateAddressRelOtherTaxMethod();
		_testFetchCommerceTaxFixedRateAddressRelRegion();
		_testFetchCommerceTaxFixedRateAddressRelZip();
	}

	private CommerceTaxFixedRateAddressRel _addCommerceTaxFixedRateAddressRel(
			long commerceTaxMethodId, long countryId, long regionId, String zip)
		throws Exception {

		return _commerceTaxFixedRateAddressRelLocalService.
			addCommerceTaxFixedRateAddressRel(
				TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
				commerceTaxMethodId, _cpTaxCategoryId, countryId, regionId, zip,
				RandomTestUtil.randomDouble());
	}

	private CommerceTaxFixedRateAddressRel
			_addOldestCommerceTaxFixedRateAddressRel(
				long commerceTaxMethodId, long countryId, long regionId,
				String zip)
		throws Exception {

		CommerceTaxFixedRateAddressRel commerceTaxFixedRateAddressRel =
			_addCommerceTaxFixedRateAddressRel(
				commerceTaxMethodId, countryId, regionId, zip);

		commerceTaxFixedRateAddressRel.setCreateDate(
			new Date(System.currentTimeMillis() - Time.DAY));

		return _commerceTaxFixedRateAddressRelLocalService.
			updateCommerceTaxFixedRateAddressRel(
				commerceTaxFixedRateAddressRel);
	}

	private void _assertFetchCommerceTaxFixedRateAddressRel(
		CommerceTaxFixedRateAddressRel expectedCommerceTaxFixedRateAddressRel,
		long commerceTaxMethodId, long countryId, long regionId, String zip) {

		Assert.assertEquals(
			expectedCommerceTaxFixedRateAddressRel,
			_commerceTaxFixedRateAddressRelLocalService.
				fetchCommerceTaxFixedRateAddressRel(
					commerceTaxMethodId, _cpTaxCategoryId, countryId, regionId,
					zip));
		Assert.assertEquals(
			expectedCommerceTaxFixedRateAddressRel,
			_commerceTaxFixedRateAddressRelLocalService.
				fetchCommerceTaxFixedRateAddressRel(
					commerceTaxMethodId, countryId, regionId, zip));
	}

	private void _testFetchCommerceTaxFixedRateAddressRelAllCountries()
		throws Exception {

		long commerceTaxMethodId = RandomTestUtil.randomLong();

		CommerceTaxFixedRateAddressRel commerceTaxFixedRateAddressRel =
			_addCommerceTaxFixedRateAddressRel(
				commerceTaxMethodId, 0, 0, StringPool.BLANK);

		_assertFetchCommerceTaxFixedRateAddressRel(
			commerceTaxFixedRateAddressRel, commerceTaxMethodId,
			RandomTestUtil.randomLong(), RandomTestUtil.randomLong(),
			RandomTestUtil.randomString());
		_assertFetchCommerceTaxFixedRateAddressRel(
			commerceTaxFixedRateAddressRel, commerceTaxMethodId,
			RandomTestUtil.randomLong(), 0, StringPool.BLANK);
	}

	private void _testFetchCommerceTaxFixedRateAddressRelCountry()
		throws Exception {

		long commerceTaxMethodId = RandomTestUtil.randomLong();
		long countryId = RandomTestUtil.randomLong();

		CommerceTaxFixedRateAddressRel commerceTaxFixedRateAddressRel =
			_addCommerceTaxFixedRateAddressRel(
				commerceTaxMethodId, countryId, 0, StringPool.BLANK);

		_assertFetchCommerceTaxFixedRateAddressRel(
			commerceTaxFixedRateAddressRel, commerceTaxMethodId, countryId,
			RandomTestUtil.randomLong(), RandomTestUtil.randomString());
		_assertFetchCommerceTaxFixedRateAddressRel(
			commerceTaxFixedRateAddressRel, commerceTaxMethodId, countryId, 0,
			StringPool.BLANK);

		_assertFetchCommerceTaxFixedRateAddressRel(
			null, commerceTaxMethodId, RandomTestUtil.randomLong(),
			RandomTestUtil.randomLong(), RandomTestUtil.randomString());
	}

	private void _testFetchCommerceTaxFixedRateAddressRelGuest()
		throws Exception {

		long commerceTaxMethodId = RandomTestUtil.randomLong();
		long countryId = RandomTestUtil.randomLong();
		long regionId = RandomTestUtil.randomLong();

		_addCommerceTaxFixedRateAddressRel(
			commerceTaxMethodId, countryId, 0, StringPool.BLANK);
		_addCommerceTaxFixedRateAddressRel(
			commerceTaxMethodId, countryId, regionId, StringPool.BLANK);
		_addCommerceTaxFixedRateAddressRel(
			commerceTaxMethodId, countryId, regionId,
			RandomTestUtil.randomString());

		_assertFetchCommerceTaxFixedRateAddressRel(
			null, commerceTaxMethodId, 0, 0, StringPool.BLANK);

		CommerceTaxFixedRateAddressRel commerceTaxFixedRateAddressRel =
			_addCommerceTaxFixedRateAddressRel(
				commerceTaxMethodId, 0, 0, StringPool.BLANK);

		_assertFetchCommerceTaxFixedRateAddressRel(
			commerceTaxFixedRateAddressRel, commerceTaxMethodId, 0, 0,
			StringPool.BLANK);
	}

	private void _testFetchCommerceTaxFixedRateAddressRelOldest()
		throws Exception {

		long commerceTaxMethodId = RandomTestUtil.randomLong();
		long countryId = RandomTestUtil.randomLong();
		long regionId = RandomTestUtil.randomLong();
		String zip = RandomTestUtil.randomString();

		CommerceTaxFixedRateAddressRel commerceTaxFixedRateAddressRel =
			_addOldestCommerceTaxFixedRateAddressRel(
				commerceTaxMethodId, countryId, 0, StringPool.BLANK);

		_addCommerceTaxFixedRateAddressRel(
			commerceTaxMethodId, 0, 0, StringPool.BLANK);
		_addCommerceTaxFixedRateAddressRel(
			commerceTaxMethodId, countryId, regionId, StringPool.BLANK);
		_addCommerceTaxFixedRateAddressRel(
			commerceTaxMethodId, countryId, regionId, zip);

		_assertFetchCommerceTaxFixedRateAddressRel(
			commerceTaxFixedRateAddressRel, commerceTaxMethodId, countryId,
			regionId, zip);

		commerceTaxMethodId = RandomTestUtil.randomLong();

		commerceTaxFixedRateAddressRel =
			_addOldestCommerceTaxFixedRateAddressRel(
				commerceTaxMethodId, countryId, regionId, zip);

		_addCommerceTaxFixedRateAddressRel(
			commerceTaxMethodId, 0, 0, StringPool.BLANK);
		_addCommerceTaxFixedRateAddressRel(
			commerceTaxMethodId, countryId, 0, zip);

		_assertFetchCommerceTaxFixedRateAddressRel(
			commerceTaxFixedRateAddressRel, commerceTaxMethodId, countryId,
			regionId, zip);
	}

	private void _testFetchCommerceTaxFixedRateAddressRelOtherTaxCategory()
		throws Exception {

		long commerceTaxMethodId = RandomTestUtil.randomLong();

		CommerceTaxFixedRateAddressRel commerceTaxFixedRateAddressRel =
			_addCommerceTaxFixedRateAddressRel(
				commerceTaxMethodId, 0, 0, StringPool.BLANK);

		long countryId = RandomTestUtil.randomLong();
		long regionId = RandomTestUtil.randomLong();
		String zip = RandomTestUtil.randomString();

		Assert.assertNull(
			_commerceTaxFixedRateAddressRelLocalService.
				fetchCommerceTaxFixedRateAddressRel(
					commerceTaxMethodId, RandomTestUtil.randomLong(), countryId,
					regionId, zip));
		Assert.assertEquals(
			commerceTaxFixedRateAddressRel,
			_commerceTaxFixedRateAddressRelLocalService.
				fetchCommerceTaxFixedRateAddressRel(
					commerceTaxMethodId, countryId, regionId, zip));
	}

	private void _testFetchCommerceTaxFixedRateAddressRelOtherTaxMethod()
		throws Exception {

		long commerceTaxMethodId = RandomTestUtil.randomLong();

		CommerceTaxFixedRateAddressRel commerceTaxFixedRateAddressRel =
			_addCommerceTaxFixedRateAddressRel(
				commerceTaxMethodId, 0, 0, StringPool.BLANK);

		long countryId = RandomTestUtil.randomLong();
		long regionId = RandomTestUtil.randomLong();
		String zip = RandomTestUtil.randomString();

		_assertFetchCommerceTaxFixedRateAddressRel(
			commerceTaxFixedRateAddressRel, commerceTaxMethodId, countryId,
			regionId, zip);
		_assertFetchCommerceTaxFixedRateAddressRel(
			null, RandomTestUtil.randomLong(), countryId, regionId, zip);
	}

	private void _testFetchCommerceTaxFixedRateAddressRelRegion()
		throws Exception {

		long commerceTaxMethodId = RandomTestUtil.randomLong();
		long countryId = RandomTestUtil.randomLong();
		long regionId = RandomTestUtil.randomLong();

		CommerceTaxFixedRateAddressRel commerceTaxFixedRateAddressRel =
			_addCommerceTaxFixedRateAddressRel(
				commerceTaxMethodId, countryId, regionId, StringPool.BLANK);

		_assertFetchCommerceTaxFixedRateAddressRel(
			commerceTaxFixedRateAddressRel, commerceTaxMethodId, countryId,
			regionId, RandomTestUtil.randomString());
		_assertFetchCommerceTaxFixedRateAddressRel(
			commerceTaxFixedRateAddressRel, commerceTaxMethodId, countryId,
			regionId, StringPool.BLANK);

		_assertFetchCommerceTaxFixedRateAddressRel(
			null, commerceTaxMethodId, countryId, RandomTestUtil.randomLong(),
			RandomTestUtil.randomString());
		_assertFetchCommerceTaxFixedRateAddressRel(
			null, commerceTaxMethodId, RandomTestUtil.randomLong(), regionId,
			RandomTestUtil.randomString());
	}

	private void _testFetchCommerceTaxFixedRateAddressRelZip()
		throws Exception {

		long commerceTaxMethodId = RandomTestUtil.randomLong();
		long countryId = RandomTestUtil.randomLong();
		long regionId = RandomTestUtil.randomLong();
		String zip = RandomTestUtil.randomString();

		CommerceTaxFixedRateAddressRel commerceTaxFixedRateAddressRel =
			_addCommerceTaxFixedRateAddressRel(
				commerceTaxMethodId, countryId, regionId, zip);

		_assertFetchCommerceTaxFixedRateAddressRel(
			commerceTaxFixedRateAddressRel, commerceTaxMethodId, countryId,
			regionId, zip);

		_assertFetchCommerceTaxFixedRateAddressRel(
			null, commerceTaxMethodId, countryId, regionId,
			RandomTestUtil.randomString());
		_assertFetchCommerceTaxFixedRateAddressRel(
			null, commerceTaxMethodId, countryId, regionId, StringPool.BLANK);
		_assertFetchCommerceTaxFixedRateAddressRel(
			null, commerceTaxMethodId, countryId, RandomTestUtil.randomLong(),
			zip);

		commerceTaxMethodId = RandomTestUtil.randomLong();

		commerceTaxFixedRateAddressRel = _addCommerceTaxFixedRateAddressRel(
			commerceTaxMethodId, countryId, 0, zip);

		_assertFetchCommerceTaxFixedRateAddressRel(
			commerceTaxFixedRateAddressRel, commerceTaxMethodId, countryId,
			RandomTestUtil.randomLong(), zip);
	}

	@Inject
	private CommerceTaxFixedRateAddressRelLocalService
		_commerceTaxFixedRateAddressRelLocalService;

	private long _cpTaxCategoryId;

}