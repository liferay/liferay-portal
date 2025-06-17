/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.option;

import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Alessio Antonio Rendina
 */
public class CheckboxCommerceOptionTypeImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		Mockito.when(
			_cpDefinitionOptionRel.getKey()
		).thenReturn(
			"testKey"
		);
	}

	@Test
	public void testIsValid() {
		Assert.assertFalse(
			_checkboxCommerceOptionTypeImpl.isValid(
				_cpDefinitionOptionRel, new String[] {"invalidKey"}));
		Assert.assertTrue(
			_checkboxCommerceOptionTypeImpl.isValid(
				_cpDefinitionOptionRel, new String[] {"testKey"}));
	}

	@InjectMocks
	private CheckboxCommerceOptionTypeImpl _checkboxCommerceOptionTypeImpl;

	@Mock
	private CPDefinitionOptionRel _cpDefinitionOptionRel;

}