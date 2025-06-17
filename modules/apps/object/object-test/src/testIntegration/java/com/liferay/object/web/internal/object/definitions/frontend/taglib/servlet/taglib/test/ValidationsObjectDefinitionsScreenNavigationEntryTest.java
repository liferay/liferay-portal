/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.definitions.frontend.taglib.servlet.taglib.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.object.constants.ObjectPortletKeys;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.constants.ObjectValidationRuleConstants;
import com.liferay.object.constants.ObjectWebKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectRelationshipTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
public class ValidationsObjectDefinitionsScreenNavigationEntryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			LanguageUtil.class, "_language",
			ProxyUtil.newProxyInstance(
				Language.class.getClassLoader(),
				new Class<?>[] {Language.class},
				(proxy, method, args) -> {
					if (Objects.equals(method.getName(), "format")) {
						return args[2];
					}

					return method.invoke(_language, args);
				}));
	}

	@After
	public void tearDown() throws Exception {
		ReflectionTestUtil.setFieldValue(
			LanguageUtil.class, "_language", _language);
	}

	@Test
	public void testCreateObjectValidationRuleElements() throws Exception {
		ObjectDefinition objectDefinition1 =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();
		ObjectDefinition objectDefinition2 =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();

		ObjectRelationshipTestUtil.addObjectRelationship(
			_objectRelationshipLocalService, objectDefinition1,
			objectDefinition2,
			ObjectRelationshipConstants.DELETION_TYPE_CASCADE,
			StringUtil.randomId(),
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY);

		ObjectRelationship objectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				_objectRelationshipLocalService, objectDefinition1,
				objectDefinition2);

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(objectDefinition2);

		_screenNavigationEntry.render(
			mockHttpServletRequest, new MockHttpServletResponse());

		List<Map<String, Object>> objectValidationRuleElements =
			ReflectionTestUtil.invoke(
				mockHttpServletRequest.getAttribute(
					WebKeys.PORTLET_DISPLAY_CONTEXT),
				"_createObjectValidationRuleElements",
				new Class<?>[] {String.class},
				ObjectValidationRuleConstants.ENGINE_TYPE_DDM);

		_assertDefaultObjectValidationRuleElements(
			5, objectValidationRuleElements);

		Assert.assertEquals(
			objectDefinition1.getLabel(LocaleUtil.US),
			MapUtil.getString(objectValidationRuleElements.get(4), "label"));

		_assertItems(
			TransformUtil.transform(
				_objectFieldLocalService.getObjectFields(
					objectDefinition2.getObjectDefinitionId()),
				objectField -> _getItem(objectField.getName(), objectField)),
			objectValidationRuleElements.get(0));
		_assertItems(
			TransformUtil.transform(
				_objectFieldLocalService.getObjectFields(
					objectDefinition1.getObjectDefinitionId()),
				objectField -> {
					ObjectField relationshipObjectField =
						_objectFieldLocalService.fetchObjectField(
							objectRelationship.getObjectFieldId2());

					return _getItem(
						StringBundler.concat(
							relationshipObjectField.getName(),
							StringPool.UNDERLINE, objectField.getName()),
						objectField);
				}),
			objectValidationRuleElements.get(4));

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(UserTestUtil.addUser()));

			_assertDefaultObjectValidationRuleElements(
				4,
				ReflectionTestUtil.invoke(
					mockHttpServletRequest.getAttribute(
						WebKeys.PORTLET_DISPLAY_CONTEXT),
					"_createObjectValidationRuleElements",
					new Class<?>[] {String.class},
					ObjectValidationRuleConstants.ENGINE_TYPE_DDM));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}
	}

	private void _assertDefaultObjectValidationRuleElements(
		int expectedSize,
		List<Map<String, Object>> objectValidationRuleElements) {

		Assert.assertEquals(
			objectValidationRuleElements.toString(), expectedSize,
			objectValidationRuleElements.size());

		Assert.assertEquals(
			"Fields",
			MapUtil.getString(objectValidationRuleElements.get(0), "label"));
		Assert.assertEquals(
			"General Variables",
			MapUtil.getString(objectValidationRuleElements.get(1), "label"));
		Assert.assertEquals(
			"Operators",
			MapUtil.getString(objectValidationRuleElements.get(2), "label"));
		Assert.assertEquals(
			"Functions",
			MapUtil.getString(objectValidationRuleElements.get(3), "label"));
	}

	private void _assertItems(
		List<Map<String, String>> expectedItems,
		Map<String, Object> objectValidationRuleElementsMap) {

		List<Map<String, String>> actualItems =
			(List<Map<String, String>>)objectValidationRuleElementsMap.get(
				"items");

		Assert.assertEquals(expectedItems, actualItems);
	}

	private Map<String, String> _getItem(
		String content, ObjectField objectField) {

		return HashMapBuilder.put(
			"content", content
		).put(
			"helpText", StringPool.BLANK
		).put(
			"label", objectField.getLabel(LocaleUtil.US)
		).build();
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
		ObjectDefinition objectDefinition) {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG,
			(LiferayPortletConfig)PortletConfigFactoryUtil.create(
				_portletLocalService.getPortletById(
					ObjectPortletKeys.OBJECT_DEFINITIONS),
				null));
		mockHttpServletRequest.setAttribute(
			ObjectWebKeys.OBJECT_DEFINITION, objectDefinition);
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY,
			new ThemeDisplay() {

				@Override
				public Locale getLocale() {
					return LocaleUtil.US;
				}

			});

		return mockHttpServletRequest;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private Language _language;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Inject
	private PortletLocalService _portletLocalService;

	@Inject(
		filter = "component.name=com.liferay.object.web.internal.object.definitions.frontend.taglib.servlet.taglib.ValidationsObjectDefinitionsScreeNavigationEntry"
	)
	private ScreenNavigationEntry<ObjectDefinition> _screenNavigationEntry;

}