/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.option;

import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
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
public class DocumentLibraryCommerceOptionTypeImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		Mockito.when(
			_jsonFactory.createJSONObject()
		).thenReturn(
			_jsonObject
		);
	}

	@Test
	public void testIsValid() throws PortalException {
		Assert.assertTrue(
			_documentLibraryCommerceOptionTypeImpl.isValid(
				_cpDefinitionOptionRel, new String[] {"{}"}));
	}

	@Mock
	private CPDefinitionOptionRel _cpDefinitionOptionRel;

	@InjectMocks
	private DocumentLibraryCommerceOptionTypeImpl
		_documentLibraryCommerceOptionTypeImpl;

	@Mock
	private JSONFactory _jsonFactory;

	@Mock
	private JSONObject _jsonObject;

}