/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.layout.page.template.info.item.capability.EditPageInfoItemCapability;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.definition.setting.builder.ObjectDefinitionSettingBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import jakarta.portlet.Portlet;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Víctor Galán
 */
@RunWith(Arquillian.class)
public class MappingTypesUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	@TestInfo("LPD-101984")
	public void testGetMappingTypeJSONObject() throws Exception {
		_group = GroupTestUtil.addGroup();

		ObjectFolder objectFolder =
			_objectFolderLocalService.getOrAddEmptyObjectFolder(
				ObjectFolderConstants.
					EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES,
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId());

		ObjectDefinition cmsObjectDefinition = _publishObjectDefinition(
			_addCMSObjectDefinition(objectFolder.getObjectFolderId()));

		ObjectDefinition objectDefinition = _publishObjectDefinition(
			ObjectDefinitionTestUtil.addCustomObjectDefinition(
				ObjectDefinitionTestUtil.getRandomName()));

		Assert.assertNull(
			_getMappingTypeJSONObject(RandomTestUtil.randomString()));

		JSONArray mappingTypesJSONArray = _getMappingTypesJSONArray();

		Assert.assertFalse(
			_hasClassName(
				cmsObjectDefinition.getClassName(), mappingTypesJSONArray));
		Assert.assertTrue(
			_hasClassName(
				objectDefinition.getClassName(), mappingTypesJSONArray));
	}

	private ObjectDefinition _addCMSObjectDefinition(long objectFolderId)
		throws Exception {

		return _objectDefinitionLocalService.addCustomObjectDefinition(
			null, TestPropsValues.getUserId(), objectFolderId, null, null, true,
			false, true, false, true, false, false, false, false, null,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			ObjectDefinitionTestUtil.getRandomName(), null, null,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			true, ObjectDefinitionConstants.SCOPE_DEPOT,
			ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
			Collections.singletonList(
				new ObjectDefinitionSettingBuilder(
				).name(
					ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS
				).value(
					StringPool.TRUE
				).build()),
			Collections.singletonList(
				new TextObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"a" + RandomTestUtil.randomString()
				).build()),
			Collections.emptyList(), new ServiceContext());
	}

	private JSONObject _getMappingTypeJSONObject(String className)
		throws Exception {

		return ReflectionTestUtil.invoke(
			_getMappingTypesUtilClass(), "getMappingTypeJSONObject",
			new Class<?>[] {
				String.class, InfoItemServiceRegistry.class, String.class,
				ThemeDisplay.class
			},
			className, _infoItemServiceRegistry, EditPageInfoItemCapability.KEY,
			_getThemeDisplay());
	}

	private JSONArray _getMappingTypesJSONArray() throws Exception {
		return ReflectionTestUtil.invoke(
			_getMappingTypesUtilClass(), "getMappingTypesJSONArray",
			new Class<?>[] {
				InfoItemServiceRegistry.class, String.class, ThemeDisplay.class
			},
			_infoItemServiceRegistry, EditPageInfoItemCapability.KEY,
			_getThemeDisplay());
	}

	private Class<?> _getMappingTypesUtilClass() throws Exception {
		Class<?> portletClass = _portlet.getClass();

		return Class.forName(
			"com.liferay.layout.content.page.editor.web.internal.util." +
				"MappingTypesUtil",
			true, portletClass.getClassLoader());
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.getSiteDefault());
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private boolean _hasClassName(
		String className, JSONArray mappingTypesJSONArray) {

		for (int i = 0; i < mappingTypesJSONArray.length(); i++) {
			JSONObject mappingTypeJSONObject =
				mappingTypesJSONArray.getJSONObject(i);

			if (className.equals(
					mappingTypeJSONObject.getString("className"))) {

				return true;
			}
		}

		return false;
	}

	private ObjectDefinition _publishObjectDefinition(
			ObjectDefinition objectDefinition)
		throws Exception {

		return _objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	private Group _group;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectFolderLocalService _objectFolderLocalService;

	@Inject(
		filter = "component.name=com.liferay.layout.content.page.editor.web.internal.portlet.ContentPageEditorPortlet"
	)
	private Portlet _portlet;

}