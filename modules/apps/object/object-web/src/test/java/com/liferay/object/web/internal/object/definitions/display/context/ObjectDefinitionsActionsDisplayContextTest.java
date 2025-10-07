/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.definitions.display.context;

import com.liferay.notification.service.NotificationTemplateLocalService;
import com.liferay.object.action.executor.ObjectActionExecutorRegistry;
import com.liferay.object.action.trigger.ObjectActionTrigger;
import com.liferay.object.action.trigger.ObjectActionTriggerRegistry;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectWebKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.script.management.configuration.helper.ScriptManagementConfigurationHelper;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Nathaly Gomes
 */
@FeatureFlag("LPD-59081")
public class ObjectDefinitionsActionsDisplayContextTest {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_setUpLanguageUtil();
	}

	@Test
	public void testGetObjectActionTriggersJSONArray() {
		ObjectActionTriggerRegistry objectActionTriggerRegistry = Mockito.mock(
			ObjectActionTriggerRegistry.class);

		ObjectActionTrigger objectActionTrigger1 = Mockito.mock(
			ObjectActionTrigger.class);
		ObjectActionTrigger objectActionTrigger2 = Mockito.mock(
			ObjectActionTrigger.class);

		Mockito.when(
			_objectDefinition.getClassName()
		).thenReturn(
			User.class.getName()
		);

		Mockito.when(
			objectActionTriggerRegistry.getObjectActionTriggers(
				_objectDefinition.getClassName())
		).thenReturn(
			Arrays.asList(objectActionTrigger1, objectActionTrigger2)
		);

		Mockito.when(
			objectActionTrigger1.getKey()
		).thenReturn(
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD
		);

		Mockito.when(
			objectActionTrigger2.getKey()
		).thenReturn(
			ObjectActionTriggerConstants.KEY_ON_AFTER_LOGIN
		);

		ObjectDefinitionsActionsDisplayContext
			objectDefinitionsActionsDisplayContext =
				new ObjectDefinitionsActionsDisplayContext(
					_getHttpServletRequest(), new JSONFactoryImpl(),
					Mockito.mock(NotificationTemplateLocalService.class),
					Mockito.mock(ObjectActionExecutorRegistry.class),
					objectActionTriggerRegistry,
					Mockito.mock(ObjectDefinitionLocalService.class),
					Mockito.mock(ModelResourcePermission.class),
					Mockito.mock(ObjectFieldLocalService.class),
					Mockito.mock(ObjectFolderLocalService.class),
					Mockito.mock(ScriptManagementConfigurationHelper.class));

		JSONArray jsonArray =
			objectDefinitionsActionsDisplayContext.
				getObjectActionTriggersJSONArray();

		Assert.assertEquals(
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
			JSONUtil.getValue(jsonArray, "JSONObject/0", "Object/value"));
		Assert.assertEquals(
			ObjectActionTriggerConstants.KEY_ON_AFTER_LOGIN,
			JSONUtil.getValue(jsonArray, "JSONObject/1", "Object/value"));

		Mockito.when(
			_objectDefinition.getClassName()
		).thenReturn(
			Organization.class.getName()
		);

		Mockito.when(
			objectActionTriggerRegistry.getObjectActionTriggers(
				_objectDefinition.getClassName())
		).thenReturn(
			Arrays.asList(objectActionTrigger1, objectActionTrigger2)
		);

		jsonArray =
			objectDefinitionsActionsDisplayContext.
				getObjectActionTriggersJSONArray();

		Assert.assertEquals(
			ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
			JSONUtil.getValue(jsonArray, "JSONObject/0", "Object/value"));
		Assert.assertNull(
			JSONUtil.getValue(jsonArray, "JSONObject/1", "Object/value"));
	}

	private HttpServletRequest _getHttpServletRequest() {
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(
			ObjectWebKeys.OBJECT_DEFINITION, _objectDefinition);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setLocale(LocaleUtil.US);

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		return httpServletRequest;
	}

	private void _setUpLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(Mockito.mock(Language.class));
	}

	private final ObjectDefinition _objectDefinition = Mockito.mock(
		ObjectDefinition.class);

}