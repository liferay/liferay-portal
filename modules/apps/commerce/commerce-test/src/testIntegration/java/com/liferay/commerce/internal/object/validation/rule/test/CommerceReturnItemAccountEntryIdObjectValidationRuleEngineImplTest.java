/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.object.validation.rule.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.validation.rule.ObjectValidationRuleEngine;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Crescenzo Rega
 */
@RunWith(Arquillian.class)
public class CommerceReturnItemAccountEntryIdObjectValidationRuleEngineImplTest
	extends BaseObjectValidationRuleEngineImplTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void test() throws Exception {
		Map<String, Object> results = _objectValidationRuleEngine.execute(
			HashMapBuilder.<String, Object>put(
				"entryDTO",
				HashMapBuilder.<String, Object>put(
					"properties",
					HashMapBuilder.<String, Object>put(
						"r_accountToCommerceReturnItems_accountEntryId",
						RandomTestUtil.randomLong()
					).put(
						"r_commerceOrderItemToCommerceReturnItems_" +
							"commerceOrderItemId",
						RandomTestUtil.randomLong()
					).build()
				).build()
			).build(),
			null);

		Assert.assertFalse(
			GetterUtil.getBoolean(results.get("validationCriteriaMet")));

		results = _objectValidationRuleEngine.execute(
			HashMapBuilder.<String, Object>put(
				"entryDTO",
				HashMapBuilder.<String, Object>put(
					"properties",
					HashMapBuilder.<String, Object>put(
						"r_accountToCommerceReturnItems_accountEntryId",
						accountEntry.getAccountEntryId()
					).put(
						"r_commerceOrderItemToCommerceReturnItems_" +
							"commerceOrderItemId",
						commerceOrderItem.getCommerceOrderItemId()
					).build()
				).build()
			).build(),
			null);

		Assert.assertTrue(
			GetterUtil.getBoolean(results.get("validationCriteriaMet")));
	}

	@Inject(
		filter = "component.name=com.liferay.commerce.internal.object.validation.rule.CommerceReturnItemAccountEntryIdObjectValidationRuleEngineImpl"
	)
	private ObjectValidationRuleEngine _objectValidationRuleEngine;

}