/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.entries.display.context;

import com.liferay.dynamic.data.mapping.expression.DDMExpressionFactory;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderer;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.item.selector.ItemSelector;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectWebKeys;
import com.liferay.object.field.business.type.ObjectFieldBusinessType;
import com.liferay.object.field.business.type.ObjectFieldBusinessTypeRegistry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectLayoutLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Aquiles Duarte
 */
public class ObjectEntryDisplayContextImplTest {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testAddFieldsetDDMFormField() {
		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getLocale()
		).thenReturn(
			LocaleUtil.SPAIN
		);

		Mockito.when(
			httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		ObjectEntryDisplayContextImpl objectEntryDisplayContextImpl =
			_createObjectEntryDisplayContextImpl(httpServletRequest);

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();
		String fieldName = RandomTestUtil.randomString();
		String label = RandomTestUtil.randomString();

		objectEntryDisplayContextImpl.addFieldsetDDMFormField(
			RandomTestUtil.randomBoolean(), ddmForm, fieldName, label,
			Collections.singletonList(new DDMFormField()), null);

		Map<String, DDMFormField> ddmFormFieldsMap =
			ddmForm.getDDMFormFieldsMap(true);

		DDMFormField ddmFormField = ddmFormFieldsMap.get(fieldName);

		LocalizedValue localizedValue = ddmFormField.getLabel();

		Assert.assertEquals(
			LocaleUtil.SPAIN, localizedValue.getDefaultLocale());
		Assert.assertEquals(label, localizedValue.getString(LocaleUtil.BRAZIL));
		Assert.assertEquals(label, localizedValue.getString(LocaleUtil.SPAIN));
		Assert.assertEquals(label, localizedValue.getString(LocaleUtil.US));
	}

	@Test
	public void testGetObjectFieldBusinessType() {
		ObjectField objectField = Mockito.mock(ObjectField.class);

		Mockito.when(
			objectField.getBusinessType()
		).thenReturn(
			ObjectFieldConstants.BUSINESS_TYPE_DATE
		);

		Mockito.when(
			objectField.isMetadata()
		).thenReturn(
			true
		);

		ObjectFieldBusinessType objectFieldBusinessType = Mockito.mock(
			ObjectFieldBusinessType.class);

		ObjectFieldBusinessTypeRegistry objectFieldBusinessTypeRegistry =
			Mockito.mock(ObjectFieldBusinessTypeRegistry.class);

		Mockito.when(
			objectFieldBusinessTypeRegistry.getObjectFieldBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_DATE_TIME)
		).thenReturn(
			objectFieldBusinessType
		);

		ObjectEntryDisplayContextImpl objectEntryDisplayContextImpl =
			_createObjectEntryDisplayContextImpl(
				objectFieldBusinessTypeRegistry);

		Assert.assertSame(
			objectFieldBusinessType,
			ReflectionTestUtil.invoke(
				objectEntryDisplayContextImpl, "_getObjectFieldBusinessType",
				new Class<?>[] {ObjectField.class},
				new Object[] {objectField}));
	}

	private ObjectEntryDisplayContextImpl _createObjectEntryDisplayContextImpl(
		HttpServletRequest httpServletRequest) {

		return _createObjectEntryDisplayContextImpl(
			httpServletRequest,
			Mockito.mock(ObjectFieldBusinessTypeRegistry.class));
	}

	private ObjectEntryDisplayContextImpl _createObjectEntryDisplayContextImpl(
		HttpServletRequest httpServletRequest,
		ObjectFieldBusinessTypeRegistry objectFieldBusinessTypeRegistry) {

		Mockito.when(
			httpServletRequest.getAttribute(
				ObjectWebKeys.OBJECT_ENTRY_READ_ONLY)
		).thenReturn(
			false
		);

		return new ObjectEntryDisplayContextImpl(
			Mockito.mock(DDMExpressionFactory.class),
			Mockito.mock(DDMFormRenderer.class), httpServletRequest,
			Mockito.mock(ItemSelector.class),
			Mockito.mock(ObjectDefinitionLocalService.class),
			Mockito.mock(ObjectEntryManagerRegistry.class),
			Mockito.mock(ObjectEntryLocalService.class),
			Mockito.mock(ObjectEntryService.class),
			objectFieldBusinessTypeRegistry,
			Mockito.mock(ObjectFieldLocalService.class),
			Mockito.mock(ObjectLayoutLocalService.class),
			Mockito.mock(ObjectRelationshipLocalService.class),
			Mockito.mock(ObjectScopeProviderRegistry.class));
	}

	private ObjectEntryDisplayContextImpl _createObjectEntryDisplayContextImpl(
		ObjectFieldBusinessTypeRegistry objectFieldBusinessTypeRegistry) {

		return _createObjectEntryDisplayContextImpl(
			Mockito.mock(HttpServletRequest.class),
			objectFieldBusinessTypeRegistry);
	}

}