/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.dynamic.data.mapping.form.field.type.internal.object.relationship;

import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.test.util.BaseDDMFormFieldTemplateContextContributorTestCase;
import com.liferay.object.dynamic.data.mapping.form.field.type.constants.ObjectDDMFormFieldTypeConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.context.path.RESTContextPathResolver;
import com.liferay.object.rest.context.path.RESTContextPathResolverRegistry;
import com.liferay.object.scope.ObjectScopeProvider;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Nathaly Gomes
 */
public class ObjectRelationshipDDMFormFieldTemplateContextContributorTest
	extends BaseDDMFormFieldTemplateContextContributorTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_ddmFormField.setDDMForm(getDDMForm());

		ObjectDefinitionLocalService objectDefinitionLocalService =
			Mockito.mock(ObjectDefinitionLocalService.class);

		Mockito.when(
			objectDefinitionLocalService.fetchObjectDefinition(
				Mockito.anyLong())
		).thenReturn(
			Mockito.mock(ObjectDefinition.class)
		);

		ObjectScopeProviderRegistry objectScopeProviderRegistry = Mockito.mock(
			ObjectScopeProviderRegistry.class);
		ObjectScopeProvider objectScopeProvider = Mockito.mock(
			ObjectScopeProvider.class);

		Mockito.when(
			objectScopeProviderRegistry.getObjectScopeProvider(
				Mockito.nullable(String.class))
		).thenReturn(
			objectScopeProvider
		);

		Portal portal = Mockito.mock(Portal.class);

		Mockito.when(
			portal.getPathContext()
		).thenReturn(
			StringPool.BLANK
		);

		Mockito.when(
			portal.getPortalURL(Mockito.any(HttpServletRequest.class))
		).thenReturn(
			_PORTAL_URL
		);

		RESTContextPathResolver restContextPathResolver = Mockito.mock(
			RESTContextPathResolver.class);

		Mockito.when(
			restContextPathResolver.getRESTContextPath(Mockito.anyLong())
		).thenReturn(
			_REST_CONTEXT_PATH
		);

		RESTContextPathResolverRegistry restContextPathResolverRegistry =
			Mockito.mock(RESTContextPathResolverRegistry.class);

		Mockito.when(
			restContextPathResolverRegistry.getRESTContextPathResolver(
				Mockito.nullable(String.class))
		).thenReturn(
			restContextPathResolver
		);

		ReflectionTestUtil.setFieldValue(
			_objectRelationshipDDMFormFieldTemplateContextContributor,
			"_jsonFactory", new JSONFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			_objectRelationshipDDMFormFieldTemplateContextContributor,
			"_objectDefinitionLocalService", objectDefinitionLocalService);
		ReflectionTestUtil.setFieldValue(
			_objectRelationshipDDMFormFieldTemplateContextContributor,
			"_objectScopeProviderRegistry", objectScopeProviderRegistry);
		ReflectionTestUtil.setFieldValue(
			_objectRelationshipDDMFormFieldTemplateContextContributor,
			"_portal", portal);
		ReflectionTestUtil.setFieldValue(
			_objectRelationshipDDMFormFieldTemplateContextContributor,
			"_restContextPathResolverRegistry",
			restContextPathResolverRegistry);
		ReflectionTestUtil.setFieldValue(
			_objectRelationshipDDMFormFieldTemplateContextContributor,
			"_systemObjectDefinitionManagerRegistry",
			_systemObjectDefinitionManagerRegistry);
	}

	@Test
	public void testGetParametersURL() {
		String additionalAPIURLParameters = RandomTestUtil.randomString();

		Assert.assertEquals(
			StringBundler.concat(
				_PORTAL_URL, _REST_CONTEXT_PATH, StringPool.QUESTION,
				additionalAPIURLParameters),
			_getAPIURL(additionalAPIURLParameters));

		Assert.assertEquals(
			_PORTAL_URL + _REST_CONTEXT_PATH, _getAPIURL(StringPool.BLANK));
	}

	private String _getAPIURL(String additionalAPIURLParameters) {
		SystemObjectDefinitionManager systemObjectDefinitionManager =
			Mockito.mock(SystemObjectDefinitionManager.class);

		Mockito.when(
			systemObjectDefinitionManager.getAdditionalAPIURLParameters()
		).thenReturn(
			additionalAPIURLParameters
		);

		Mockito.when(
			_systemObjectDefinitionManagerRegistry.
				getSystemObjectDefinitionManager(Mockito.nullable(String.class))
		).thenReturn(
			systemObjectDefinitionManager
		);

		return MapUtil.getString(
			_objectRelationshipDDMFormFieldTemplateContextContributor.
				getParameters(
					_ddmFormField, createDDMFormFieldRenderingContext()),
			"apiURL");
	}

	private static final String _PORTAL_URL = RandomTestUtil.randomString();

	private static final String _REST_CONTEXT_PATH =
		RandomTestUtil.randomString();

	private final DDMFormField _ddmFormField = new DDMFormField(
		RandomTestUtil.randomString(),
		ObjectDDMFormFieldTypeConstants.OBJECT_RELATIONSHIP);
	private final ObjectRelationshipDDMFormFieldTemplateContextContributor
		_objectRelationshipDDMFormFieldTemplateContextContributor =
			new ObjectRelationshipDDMFormFieldTemplateContextContributor();
	private final SystemObjectDefinitionManagerRegistry
		_systemObjectDefinitionManagerRegistry = Mockito.mock(
			SystemObjectDefinitionManagerRegistry.class);

}