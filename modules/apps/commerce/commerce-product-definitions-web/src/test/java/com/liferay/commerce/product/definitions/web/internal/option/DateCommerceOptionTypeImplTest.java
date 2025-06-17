/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.option;

import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Alessio Antonio Rendina
 */
public class DateCommerceOptionTypeImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testIsValid() {
		Assert.assertFalse(
			_dateCommerceOptionTypeImpl.isValid(
				_cpDefinitionOptionRel,
				new String[] {RandomTestUtil.randomString()}));
		Assert.assertTrue(
			_dateCommerceOptionTypeImpl.isValid(
				_cpDefinitionOptionRel, new String[] {"2025-05-30"}));
	}

	@Mock
	private CPDefinitionOptionRel _cpDefinitionOptionRel;

	@InjectMocks
	private DateCommerceOptionTypeImpl _dateCommerceOptionTypeImpl;

}