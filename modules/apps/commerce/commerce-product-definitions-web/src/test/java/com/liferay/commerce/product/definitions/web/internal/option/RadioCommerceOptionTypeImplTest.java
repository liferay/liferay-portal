/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.option;

import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;

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
public class RadioCommerceOptionTypeImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		Mockito.when(
			_cpDefinitionOptionRel.getCPDefinitionOptionValueRels()
		).thenReturn(
			Collections.singletonList(_cpDefinitionOptionValueRel)
		);

		Mockito.when(
			_cpDefinitionOptionValueRel.getKey()
		).thenReturn(
			"testKey"
		);
	}

	@Test
	public void testIsValid() {
		Assert.assertFalse(
			_radioCommerceOptionTypeImpl.isValid(
				_cpDefinitionOptionRel, new String[] {"invalidKey"}));
		Assert.assertTrue(
			_radioCommerceOptionTypeImpl.isValid(
				_cpDefinitionOptionRel, new String[] {"testKey"}));
	}

	@Mock
	private CPDefinitionOptionRel _cpDefinitionOptionRel;

	@Mock
	private CPDefinitionOptionValueRel _cpDefinitionOptionValueRel;

	@InjectMocks
	private RadioCommerceOptionTypeImpl _radioCommerceOptionTypeImpl;

}