/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.exception.CPOptionValueKeyException;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.model.CPOptionValue;
import com.liferay.commerce.product.service.CPOptionLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.frutilla.FrutillaRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Andrea Sbarra
 */
@RunWith(Arquillian.class)
public class CPOptionValueLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user = UserTestUtil.addUser();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), _user.getUserId());
	}

	@After
	public void tearDown() throws Exception {
		_cpOptionLocalService.deleteCPOptions(_serviceContext.getCompanyId());
	}

	@Test
	public void testValidateCPOptionValueKey() throws Exception {
		frutillaRule.scenario(
			"Verify the validity of the Option Value Key"
		).given(
			"An Option of type select date"
		).when(
			"Configuring the Option Value"
		).then(
			"Only valid key is accepted"
		);

		CPOptionValue cpOptionValue = _addCPOptionWitCPOptionValue(
			"03-18-2024-16-45-1-hours-europe-paris");

		Assert.assertNotNull("Option value was not created", cpOptionValue);
	}

	@Test(expected = CPOptionValueKeyException.class)
	public void testValidateCPOptionValueWithInvalidDate() throws Exception {
		frutillaRule.scenario(
			"Verify the validity of the Option Value Key"
		).given(
			"A Product Option of type select date"
		).when(
			"Configuring the Option Value"
		).and(
			"the key is not the right format"
		).then(
			"An exception is thrown"
		);

		_addCPOptionWitCPOptionValue("2024-03-32-16-45-1-hours-europe-paris");
	}

	@Test(expected = CPOptionValueKeyException.class)
	public void testValidateCPOptionValueWithInvalidDateValue()
		throws Exception {

		frutillaRule.scenario(
			"Verify the validity of the Option Value Key"
		).given(
			"A Product Option of type select date"
		).when(
			"Configuring the Option Value"
		).and(
			"the key is not the right format"
		).then(
			"An exception is thrown"
		);

		_addCPOptionWitCPOptionValue("03-18-aa-16-45-1-hours-europe-paris");
	}

	@Test(expected = CPOptionValueKeyException.class)
	public void testValidateCPOptionValueWithInvalidDuration()
		throws Exception {

		frutillaRule.scenario(
			"Verify the validity of the Option Value Key"
		).given(
			"A Product Option of type select date"
		).when(
			"Configuring the Option Value"
		).and(
			"the key is not the right format"
		).then(
			"An exception is thrown"
		);

		_addCPOptionWitCPOptionValue("03-18-2024-16-45-1-xyz-europe-paris");
	}

	@Test(expected = CPOptionValueKeyException.class)
	public void testValidateCPOptionValueWithInvalidKey() throws Exception {
		frutillaRule.scenario(
			"Verify the validity of the Option Value Key"
		).given(
			"A Product Option of type select date"
		).when(
			"Configuring the Option Value"
		).and(
			"the key is not the right format"
		).then(
			"An exception is thrown"
		);

		_addCPOptionWitCPOptionValue("03-18-2024_16-45-1-hours-europe-paris");
	}

	@Test(expected = CPOptionValueKeyException.class)
	public void testValidateCPOptionValueWithNullKey() throws Exception {
		frutillaRule.scenario(
			"Verify the validity of the Option Value Key"
		).given(
			"A Product Option of type select date"
		).when(
			"Configuring the Option Value"
		).and(
			"the key is null"
		).then(
			"An exception is thrown"
		);

		_addCPOptionWitCPOptionValue(null);
	}

	@Rule
	public final FrutillaRule frutillaRule = new FrutillaRule();

	private CPOptionValue _addCPOptionWitCPOptionValue(String key)
		throws Exception {

		CPOption cpOption = CPTestUtil.addCPOption(
			_group.getGroupId(), CPConstants.PRODUCT_OPTION_SELECT_DATE_KEY,
			false);

		return CPTestUtil.addCPOptionValue(cpOption, key);
	}

	@Inject
	private CPOptionLocalService _cpOptionLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private ServiceContext _serviceContext;
	private User _user;

}