/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.service;

import com.liferay.commerce.helper.CommerceRoleHelper;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Stefano Motta
 */
public class CommerceReturnsRolesObjectDefinitionLocalServiceWrapperTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testPublishSystemObjectDefinition() throws Exception {
		CommerceReturnsRolesObjectDefinitionLocalServiceWrapper
			commerceReturnsRolesObjectDefinitionLocalServiceWrapper =
				new CommerceReturnsRolesObjectDefinitionLocalServiceWrapper();

		CommerceRoleHelper commerceRoleHelper = Mockito.mock(
			CommerceRoleHelper.class);

		ReflectionTestUtil.setFieldValue(
			commerceReturnsRolesObjectDefinitionLocalServiceWrapper,
			"_commerceRoleHelper", commerceRoleHelper);

		ObjectDefinitionLocalService objectDefinitionLocalService =
			Mockito.mock(ObjectDefinitionLocalService.class);

		commerceReturnsRolesObjectDefinitionLocalServiceWrapper.
			setWrappedService(objectDefinitionLocalService);

		ObjectDefinition objectDefinition = _mockObjectDefinition(
			RandomTestUtil.randomLong(), RandomTestUtil.randomString());
		long objectDefinitionId = RandomTestUtil.randomLong();
		long userId = RandomTestUtil.randomLong();

		Mockito.when(
			objectDefinitionLocalService.publishSystemObjectDefinition(
				userId, objectDefinitionId)
		).thenReturn(
			objectDefinition
		);

		Assert.assertSame(
			objectDefinition,
			commerceReturnsRolesObjectDefinitionLocalServiceWrapper.
				publishSystemObjectDefinition(userId, objectDefinitionId));

		Mockito.verifyNoInteractions(commerceRoleHelper);

		long companyId = RandomTestUtil.randomLong();

		objectDefinition = _mockObjectDefinition(companyId, "CommerceReturn");

		Mockito.when(
			objectDefinitionLocalService.publishSystemObjectDefinition(
				userId, objectDefinitionId)
		).thenReturn(
			objectDefinition
		);

		Assert.assertSame(
			objectDefinition,
			commerceReturnsRolesObjectDefinitionLocalServiceWrapper.
				publishSystemObjectDefinition(userId, objectDefinitionId));

		Mockito.verify(
			commerceRoleHelper
		).checkCommerceReturnsRoles(
			Mockito.argThat(
				serviceContext -> serviceContext.getCompanyId() == companyId)
		);
	}

	private ObjectDefinition _mockObjectDefinition(
		long companyId, String name) {

		ObjectDefinition objectDefinition = Mockito.mock(
			ObjectDefinition.class);

		Mockito.when(
			objectDefinition.getCompanyId()
		).thenReturn(
			companyId
		);

		Mockito.when(
			objectDefinition.getName()
		).thenReturn(
			name
		);

		return objectDefinition;
	}

}