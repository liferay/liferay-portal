/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.option.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.option.CommerceOptionValue;
import com.liferay.commerce.product.option.CommerceOptionValueHelper;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPDefinitionOptionRelLocalServiceUtil;
import com.liferay.commerce.product.service.CPDefinitionOptionValueRelLocalServiceUtil;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalServiceUtil;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.frutilla.FrutillaRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Igor Beslic
 */
@RunWith(Arquillian.class)
public class CommerceOptionValueHelperTest {

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
		_commerceCatalog = CommerceCatalogLocalServiceUtil.addCommerceCatalog(
			null, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			LocaleUtil.US.getDisplayLanguage(),
			ServiceContextTestUtil.getServiceContext(_company.getGroupId()));
	}

	@Test
	public void testGetCPDefinitionCommerceOptionValues() throws Exception {
		frutillaRule.scenario(
			"Get commerce product definition commerce option values"
		).given(
			StringBundler.concat(
				"I have a product bundle definition with 5 product options.",
				"Each option has at least two option values linked to unique ",
				"valid purchasable product commerce product instance in ",
				"catalog.")
		).when(
			"commerce option values are requested for commerce product " +
				"definition ID and JSON option key option value key " +
					"combination "
		).then(
			"correct commerce option values are created"
		);

		_setupPermissionChecker();

		CPDefinition bundleCPDefinition =
			CPTestUtil.addCPDefinitionWithChildCPDefinitions(
				_commerceCatalog.getGroupId(), 5);

		String jsonString =
			_getRandomCPDefinitionOptionValueRelsCombinationJSONString(
				bundleCPDefinition.getCPDefinitionId());

		List<CommerceOptionValue> commerceOptionValues =
			_commerceOptionValueHelper.getCPDefinitionCommerceOptionValues(
				bundleCPDefinition.getCPDefinitionId(), jsonString);

		Assert.assertEquals(
			"commerce option value entries count", 5,
			commerceOptionValues.size());

		_assertCommerceOptionValuesValid(
			bundleCPDefinition.getCPDefinitionId(), commerceOptionValues);

		_testToCommerceOptionValues(jsonString, commerceOptionValues);
	}

	@Rule
	public final FrutillaRule frutillaRule = new FrutillaRule();

	private void _assertCommerceOptionValuesValid(
		long cpDefinitionId,
		List<CommerceOptionValue> expectedCommerceOptionValues) {

		List<CPDefinitionOptionRel> cpDefinitionOptionRels =
			CPDefinitionOptionRelLocalServiceUtil.getCPDefinitionOptionRels(
				cpDefinitionId);

		for (CommerceOptionValue commerceOptionValue :
				expectedCommerceOptionValues) {

			CPDefinitionOptionRel matchingCPDefinitionOptionRel = null;

			for (CPDefinitionOptionRel cpDefinitionOptionRel :
					cpDefinitionOptionRels) {

				if (Objects.equals(
						commerceOptionValue.getOptionKey(),
						cpDefinitionOptionRel.getKey())) {

					matchingCPDefinitionOptionRel = cpDefinitionOptionRel;

					break;
				}
			}

			Assert.assertNotNull(
				"Commerce option value DTO must have commerce product " +
					"definition option rel",
				matchingCPDefinitionOptionRel);

			Assert.assertEquals(
				matchingCPDefinitionOptionRel.getPriceType(),
				commerceOptionValue.getPriceType());
			Assert.assertEquals(
				matchingCPDefinitionOptionRel.getKey(),
				commerceOptionValue.getOptionKey());

			CPDefinitionOptionValueRel matchingCPDefinitionOptionValueRel =
				CPDefinitionOptionValueRelLocalServiceUtil.
					fetchCPDefinitionOptionValueRel(
						matchingCPDefinitionOptionRel.
							getCPDefinitionOptionRelId(),
						commerceOptionValue.getOptionValueKey());

			Assert.assertNotNull(
				"Commerce option value DTO must have commerce product " +
					"definition option value rel",
				matchingCPDefinitionOptionValueRel);

			long expectedCPInstanceId = 0;

			CPInstance cpInstance =
				matchingCPDefinitionOptionValueRel.fetchCPInstance();

			if (cpInstance != null) {
				expectedCPInstanceId = cpInstance.getCPInstanceId();
			}

			Assert.assertEquals(
				expectedCPInstanceId, commerceOptionValue.getCPInstanceId());

			if (Objects.equals(
					commerceOptionValue.getPriceType(),
					CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC)) {

				Assert.assertEquals(
					cpInstance.getPrice(), commerceOptionValue.getPrice());
			}
			else {
				Assert.assertEquals(
					matchingCPDefinitionOptionValueRel.getPrice(),
					commerceOptionValue.getPrice());
			}

			Assert.assertEquals(
				matchingCPDefinitionOptionValueRel.getKey(),
				commerceOptionValue.getOptionValueKey());

			Assert.assertEquals(
				matchingCPDefinitionOptionValueRel.getQuantity(),
				commerceOptionValue.getQuantity());
		}
	}

	private String _getRandomCPDefinitionOptionValueRelsCombinationJSONString(
			long cpDefinitionId)
		throws Exception {

		List<CPDefinitionOptionValueRel> randomCPDefinitionOptionValueRels =
			CPTestUtil.getRandomCPDefinitionOptionValueRels(cpDefinitionId);

		StringBundler sb = new StringBundler();

		sb.append(StringPool.OPEN_BRACKET);

		Iterator<CPDefinitionOptionValueRel> iterator =
			randomCPDefinitionOptionValueRels.iterator();

		while (iterator.hasNext()) {
			CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
				iterator.next();

			CPDefinitionOptionRel cpDefinitionOptionRel =
				cpDefinitionOptionValueRel.getCPDefinitionOptionRel();

			sb.append(
				_toDDMFormRequestContentArrayElementFormat(
					cpDefinitionOptionRel.getKey(),
					cpDefinitionOptionValueRel.getKey()));

			if (!iterator.hasNext()) {
				continue;
			}

			sb.append(StringPool.COMMA);
		}

		sb.append(StringPool.CLOSE_BRACKET);

		return sb.toString();
	}

	private void _setupPermissionChecker() throws Exception {
		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(
				UserLocalServiceUtil.getUser(_commerceCatalog.getUserId())));
	}

	private void _testToCommerceOptionValues(
			String jsonString,
			List<CommerceOptionValue> expectedCommerceOptionValues)
		throws Exception {

		List<CommerceOptionValue> commerceOptionValues =
			_commerceOptionValueHelper.toCommerceOptionValues(jsonString);

		Assert.assertEquals(
			"Commerce option value entries count",
			expectedCommerceOptionValues.size(), commerceOptionValues.size());

		for (CommerceOptionValue commerceOptionValue : commerceOptionValues) {
			CommerceOptionValue firstMatch = commerceOptionValue.getFirstMatch(
				expectedCommerceOptionValues);

			Assert.assertNotNull(
				"Commerce product definition option value must have a match",
				firstMatch);

			Assert.assertEquals(
				firstMatch.getOptionKey(), commerceOptionValue.getOptionKey());
			Assert.assertEquals(
				firstMatch.getOptionValueKey(),
				commerceOptionValue.getOptionValueKey());
		}
	}

	private String _toDDMFormRequestContentArrayElementFormat(
		String optionKey, String optionValueKey) {

		return String.format(
			"{\"key\":\"%s\", \"value\":[\"%s\"]}", optionKey, optionValueKey);
	}

	private static Company _company;

	private CommerceCatalog _commerceCatalog;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@Inject
	private CommerceOptionValueHelper _commerceOptionValueHelper;

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

}