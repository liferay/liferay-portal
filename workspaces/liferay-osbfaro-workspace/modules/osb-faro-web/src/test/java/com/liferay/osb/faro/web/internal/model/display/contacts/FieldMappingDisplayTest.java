/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.model.display.contacts;

import com.liferay.osb.faro.engine.client.constants.FieldMappingConstants;
import com.liferay.osb.faro.engine.client.model.FieldMapping;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.AdditionalAnswers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Thiago Buarque
 */
public class FieldMappingDisplayTest {

	@Test
	public void testGetDisplayName() {
		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.get(Mockito.any(Locale.class), Mockito.anyString())
		).thenAnswer(
			AdditionalAnswers.returnsArgAt(1)
		);

		ReflectionTestUtil.setFieldValue(
			LanguageUtil.class, "_language", language);

		MockedStatic<PermissionThreadLocal> permissionThreadLocalMockedStatic =
			Mockito.mockStatic(PermissionThreadLocal.class);

		PermissionChecker permissionChecker = Mockito.mock(
			PermissionChecker.class);

		User user = Mockito.mock(User.class);

		Mockito.when(
			user.getLocale()
		).thenReturn(
			LocaleUtil.US
		);

		Mockito.when(
			permissionChecker.getUser()
		).thenReturn(
			user
		);

		permissionThreadLocalMockedStatic.when(
			PermissionThreadLocal::getPermissionChecker
		).thenReturn(
			permissionChecker
		);

		_testGetDisplayName("account", "customer-since", "yearStarted");
		_testGetDisplayName("demographics", "field.birth-date", "birthday");
		_testGetDisplayName(
			"demographics", "field.email-address", "emailAddress");
		_testGetDisplayName("demographics", "field.first-name", "firstName");
		_testGetDisplayName("demographics", "field.job-title", "jobTitle");
		_testGetDisplayName("demographics", "selected-language", "languageId");
		_testGetDisplayName("demographics", "field.last-name", "lastName");
		_testGetDisplayName("demographics", "middle-name", "middleName");
		_testGetDisplayName(
			"demographics", "field.date-modified", "modifiedDate");
		_testGetDisplayName(
			"demographics", "randomFieldName", "randomFieldName");
		_testGetDisplayName("demographics", "field.screen-name", "screenName");
	}

	private void _testGetDisplayName(
		String context, String expectedDisplayName, String fieldName) {

		FieldMapping fieldMapping = new FieldMapping();

		fieldMapping.setContext(context);
		fieldMapping.setDisplayName(fieldName);
		fieldMapping.setDisplayType(FieldMappingConstants.TYPE_TEXT);
		fieldMapping.setFieldName(fieldName);
		fieldMapping.setFieldType(FieldMappingConstants.TYPE_TEXT);

		FieldMappingDisplay fieldMappingDisplay = new FieldMappingDisplay(
			fieldMapping);

		Assert.assertEquals(
			expectedDisplayName,
			ReflectionTestUtil.getFieldValue(
				fieldMappingDisplay, "_displayName"));
	}

}