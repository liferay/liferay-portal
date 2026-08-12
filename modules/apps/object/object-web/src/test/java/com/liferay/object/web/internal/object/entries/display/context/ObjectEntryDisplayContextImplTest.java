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
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectWebKeys;
import com.liferay.object.field.business.type.ObjectFieldBusinessType;
import com.liferay.object.field.business.type.ObjectFieldBusinessTypeRegistry;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManagerRegistry;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectLayoutLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Aquiles Duarte
 */
public class ObjectEntryDisplayContextImplTest {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_setUpMockHttpServletRequest();
		_setUpObjectDefinition();
		_setUpObjectEntryLocalService();
		_setUpThemeDisplay();
	}

	@Test
	public void testAddFieldsetDDMFormField() {
		ObjectEntryDisplayContextImpl objectEntryDisplayContextImpl =
			_createObjectEntryDisplayContextImpl(_mockHttpServletRequest);

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
	public void testGetBackURL() throws Exception {
		_testGetBackURL(false, 0, null);
		_testGetBackURL(false, 0, _OBJECT_ENTRY_ID);
		_testGetBackURL(true, 1, null);
		_testGetBackURL(true, 1, _OBJECT_ENTRY_ID);
	}

	@Test
	public void testGetDefaultLocale() {
		ObjectEntryDisplayContextImpl objectEntryDisplayContextImpl =
			_createObjectEntryDisplayContextImpl(_mockHttpServletRequest);

		LocaleUtil.setDefault(
			LocaleUtil.BRAZIL.getLanguage(), LocaleUtil.BRAZIL.getCountry(),
			LocaleUtil.BRAZIL.getVariant());

		Mockito.when(
			_objectDefinition.getScope()
		).thenReturn(
			ObjectDefinitionConstants.SCOPE_COMPANY
		);

		Assert.assertEquals(
			LocaleUtil.BRAZIL,
			objectEntryDisplayContextImpl.getDefaultLocale(
				LocaleUtil.US, null));

		Mockito.when(
			_objectDefinition.getScope()
		).thenReturn(
			ObjectDefinitionConstants.SCOPE_SITE
		);

		Assert.assertEquals(
			LocaleUtil.US,
			objectEntryDisplayContextImpl.getDefaultLocale(
				LocaleUtil.US, null));
	}

	@Test
	public void testGetMethod() throws Exception {
		_testGetMethod("PATCH", _OBJECT_ENTRY_ID, RandomTestUtil.randomLong());
		_testGetMethod("PUT", _OBJECT_ENTRY_ID, 0);
		_testGetMethod("PUT", null, 0);
		_testGetMethod("PUT", null, RandomTestUtil.randomLong());
	}

	@Test
	@TestInfo("LPD-102111")
	public void testGetObjectDefinitionPortletId() throws Exception {
		Assert.assertEquals(
			_PORTLET_ID, _getObjectDefinitionPortletId(_PORTLET_ID));

		String portletId = PortletIdCodec.encode(
			_PORTLET_ID, RandomTestUtil.randomString());

		Assert.assertEquals(
			portletId, _getObjectDefinitionPortletId(portletId));

		Assert.assertEquals(
			StringPool.BLANK,
			_getObjectDefinitionPortletId(RandomTestUtil.randomString()));
	}

	@Test
	public void testGetObjectEntry() throws Exception {
		String groupId = String.valueOf(RandomTestUtil.randomLong());

		_mockHttpServletRequest.setAttribute(
			ObjectWebKeys.OBJECT_ENTRY_GROUP_ID, groupId);

		String externalReferenceCode = RandomTestUtil.randomString();

		_mockHttpServletRequest.setParameter(
			"externalReferenceCode", externalReferenceCode);

		long companyId = _themeDisplay.getCompanyId();

		Mockito.when(
			_objectDefinition.getCompanyId()
		).thenReturn(
			companyId
		);

		Company company = Mockito.mock(Company.class);

		Mockito.when(
			company.getCompanyId()
		).thenReturn(
			companyId
		);

		_themeDisplay.setCompany(company);

		ObjectEntryManager objectEntryManager = Mockito.mock(
			ObjectEntryManager.class);
		ObjectEntryManagerRegistry objectEntryManagerRegistry = Mockito.mock(
			ObjectEntryManagerRegistry.class);

		Mockito.when(
			objectEntryManagerRegistry.getObjectEntryManager(
				companyId, ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT)
		).thenReturn(
			objectEntryManager
		);

		ObjectEntry objectEntry = Mockito.mock(ObjectEntry.class);

		Mockito.when(
			objectEntryManager.getObjectEntry(
				Mockito.eq(companyId), Mockito.any(DTOConverterContext.class),
				Mockito.eq(externalReferenceCode),
				Mockito.eq(_objectDefinition), Mockito.eq(groupId))
		).thenReturn(
			objectEntry
		);

		ObjectEntryDisplayContextImpl objectEntryDisplayContextImpl =
			_createObjectEntryDisplayContextImpl(
				_mockHttpServletRequest, objectEntryManagerRegistry,
				Mockito.mock(ObjectRelationshipLocalService.class));

		Assert.assertSame(
			objectEntry,
			ReflectionTestUtil.invoke(
				objectEntryDisplayContextImpl, "_getObjectEntry",
				new Class<?>[0], new Object[0]));
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
			httpServletRequest, Mockito.mock(ObjectEntryManagerRegistry.class),
			Mockito.mock(ObjectFieldBusinessTypeRegistry.class),
			Mockito.mock(ObjectRelationshipLocalService.class));
	}

	private ObjectEntryDisplayContextImpl _createObjectEntryDisplayContextImpl(
		HttpServletRequest httpServletRequest, Long objectEntryId) {

		ObjectEntryDisplayContextImpl objectEntryDisplayContextImpl =
			_createObjectEntryDisplayContextImpl(
				httpServletRequest,
				Mockito.mock(ObjectEntryManagerRegistry.class),
				Mockito.mock(ObjectFieldBusinessTypeRegistry.class),
				Mockito.mock(ObjectRelationshipLocalService.class));

		ObjectEntry objectEntry = Mockito.mock(ObjectEntry.class);

		Mockito.when(
			objectEntry.getId()
		).thenReturn(
			objectEntryId
		);

		ReflectionTestUtil.setFieldValue(
			objectEntryDisplayContextImpl, "_objectEntry", objectEntry);

		return objectEntryDisplayContextImpl;
	}

	private ObjectEntryDisplayContextImpl _createObjectEntryDisplayContextImpl(
		HttpServletRequest httpServletRequest,
		ObjectEntryManagerRegistry objectEntryManagerRegistry,
		ObjectFieldBusinessTypeRegistry objectFieldBusinessTypeRegistry,
		ObjectRelationshipLocalService objectRelationshipLocalService) {

		return new ObjectEntryDisplayContextImpl(
			Mockito.mock(DDMExpressionFactory.class),
			Mockito.mock(DDMFormRenderer.class), httpServletRequest,
			Mockito.mock(ItemSelector.class),
			Mockito.mock(ObjectDefinitionLocalService.class),
			objectEntryManagerRegistry, _objectEntryLocalService,
			Mockito.mock(ObjectEntryService.class),
			objectFieldBusinessTypeRegistry,
			Mockito.mock(ObjectFieldLocalService.class),
			Mockito.mock(ObjectLayoutLocalService.class),
			objectRelationshipLocalService,
			Mockito.mock(ObjectScopeProviderRegistry.class));
	}

	private ObjectEntryDisplayContextImpl _createObjectEntryDisplayContextImpl(
		HttpServletRequest httpServletRequest,
		ObjectEntryManagerRegistry objectEntryManagerRegistry,
		ObjectRelationshipLocalService objectRelationshipLocalService) {

		return _createObjectEntryDisplayContextImpl(
			httpServletRequest, objectEntryManagerRegistry,
			Mockito.mock(ObjectFieldBusinessTypeRegistry.class),
			objectRelationshipLocalService);
	}

	private ObjectEntryDisplayContextImpl _createObjectEntryDisplayContextImpl(
		ObjectFieldBusinessTypeRegistry objectFieldBusinessTypeRegistry) {

		return _createObjectEntryDisplayContextImpl(
			_mockHttpServletRequest,
			Mockito.mock(ObjectEntryManagerRegistry.class),
			objectFieldBusinessTypeRegistry,
			Mockito.mock(ObjectRelationshipLocalService.class));
	}

	private String _getObjectDefinitionPortletId(String portletId)
		throws Exception {

		PortletDisplay portletDisplay = Mockito.mock(PortletDisplay.class);

		Mockito.when(
			portletDisplay.getId()
		).thenReturn(
			portletId
		);

		Mockito.when(
			_themeDisplay.getPortletDisplay()
		).thenReturn(
			portletDisplay
		);

		return ReflectionTestUtil.invoke(
			_createObjectEntryDisplayContextImpl(
				_mockHttpServletRequest, _OBJECT_ENTRY_ID),
			"_getObjectDefinitionPortletId", new Class<?>[0]);
	}

	private void _setUpMockHttpServletRequest() {
		_mockHttpServletRequest.addParameter("backURL", _BACK_URL);
		_mockHttpServletRequest.setAttribute(
			ObjectWebKeys.OBJECT_DEFINITION, _objectDefinition);
		_mockHttpServletRequest.setAttribute(
			ObjectWebKeys.OBJECT_ENTRY_READ_ONLY, Boolean.FALSE);
		_mockHttpServletRequest.setAttribute(WebKeys.PORTLET_ID, _PORTLET_ID);
		_mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);
	}

	private void _setUpObjectDefinition() {
		Mockito.when(
			_objectDefinition.getPortletId()
		).thenReturn(
			_PORTLET_ID
		);

		Mockito.when(
			_objectDefinition.getStorageType()
		).thenReturn(
			ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT
		);
	}

	private void _setUpObjectEntryLocalService() {
		Mockito.when(
			_objectEntryLocalService.fetchObjectEntry(_OBJECT_ENTRY_ID)
		).thenReturn(
			_serviceBuilderObjectEntry
		);
	}

	private void _setUpThemeDisplay() {
		Mockito.when(
			_themeDisplay.getCompanyId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			_themeDisplay.getLocale()
		).thenReturn(
			LocaleUtil.SPAIN
		);
	}

	private void _testGetBackURL(
			boolean defaultStorageType, int expectedTimes, Long objectEntryId)
		throws Exception {

		Mockito.when(
			_objectDefinition.isDefaultStorageType()
		).thenReturn(
			defaultStorageType
		);

		ObjectEntryDisplayContextImpl objectEntryDisplayContextImpl =
			_createObjectEntryDisplayContextImpl(
				_mockHttpServletRequest, objectEntryId);

		Assert.assertEquals(
			_BACK_URL, objectEntryDisplayContextImpl.getBackURL());

		Mockito.verify(
			_objectEntryLocalService, Mockito.times(expectedTimes)
		).fetchObjectEntry(
			GetterUtil.getLong(objectEntryId)
		);
	}

	private void _testGetMethod(
			String expectedMethod, Long objectEntryId, long rootObjectEntryId)
		throws Exception {

		Mockito.when(
			_serviceBuilderObjectEntry.getRootObjectEntryId()
		).thenReturn(
			rootObjectEntryId
		);

		ObjectEntryDisplayContextImpl objectEntryDisplayContextImpl =
			_createObjectEntryDisplayContextImpl(
				_mockHttpServletRequest, objectEntryId);

		Assert.assertEquals(
			expectedMethod, objectEntryDisplayContextImpl.getMethod());
	}

	private static final String _BACK_URL = RandomTestUtil.randomString();

	private static final long _OBJECT_ENTRY_ID = RandomTestUtil.randomLong();

	private static final String _PORTLET_ID = RandomTestUtil.randomString();

	private final MockHttpServletRequest _mockHttpServletRequest =
		new MockHttpServletRequest();
	private final ObjectDefinition _objectDefinition = Mockito.mock(
		ObjectDefinition.class);
	private final ObjectEntryLocalService _objectEntryLocalService =
		Mockito.mock(ObjectEntryLocalService.class);
	private final com.liferay.object.model.ObjectEntry
		_serviceBuilderObjectEntry = Mockito.mock(
			com.liferay.object.model.ObjectEntry.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}