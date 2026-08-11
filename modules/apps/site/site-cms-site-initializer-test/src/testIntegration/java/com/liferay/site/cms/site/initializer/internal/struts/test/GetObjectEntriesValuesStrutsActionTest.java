/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.struts.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.field.builder.BooleanObjectFieldBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectRelationshipTestUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Víctor Galán Grande
 */
@RunWith(Arquillian.class)
public class GetObjectEntriesValuesStrutsActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testExecute() throws Exception {
		String booleanFieldName = "booleanField";
		String localizedFieldName = "localizedField";
		String textFieldName = "textField";

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition(
				Arrays.asList(
					new BooleanObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(booleanFieldName)
					).name(
						booleanFieldName
					).build(),
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(localizedFieldName)
					).localized(
						true
					).name(
						localizedFieldName
					).build(),
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(textFieldName)
					).name(
						textFieldName
					).build()));

		objectDefinition =
			ObjectDefinitionLocalServiceUtil.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId());

		Map<String, String> localizedFieldValueMap = HashMapBuilder.put(
			LocaleUtil.toLanguageId(LocaleUtil.US),
			RandomTestUtil.randomString()
		).build();
		String textFieldValue = RandomTestUtil.randomString();

		String externalReferenceCode = PortalUUIDUtil.generate();

		ObjectEntry objectEntry = ObjectEntryLocalServiceUtil.addObjectEntry(
			0L, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				booleanFieldName, true
			).put(
				localizedFieldName + "_i18n",
				(Serializable)localizedFieldValueMap
			).put(
				textFieldName, textFieldValue
			).build(),
			ServiceContextTestUtil.getServiceContext());

		objectEntry.setExternalReferenceCode(externalReferenceCode);

		objectEntry = ObjectEntryLocalServiceUtil.updateObjectEntry(
			objectEntry);

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(TestPropsValues.getUser());

		JSONArray objectEntriesJSONArray = JSONUtil.put(
			JSONUtil.put(
				"className", objectDefinition.getClassName()
			).put(
				"objectEntryId", objectEntry.getObjectEntryId()
			));

		mockHttpServletRequest.setParameter(
			"objectEntries", objectEntriesJSONArray.toString());

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_getObjectEntriesValuesStrutsAction.execute(
			mockHttpServletRequest, mockHttpServletResponse);

		JSONArray resultJSONArray = _jsonFactory.createJSONArray(
			mockHttpServletResponse.getContentAsString());

		JSONObject objectEntryValuesJSONObject = resultJSONArray.getJSONObject(
			0);

		Assert.assertNotNull(objectEntryValuesJSONObject);

		Assert.assertEquals(
			externalReferenceCode,
			objectEntryValuesJSONObject.getString("externalReferenceCode"));

		JSONArray fieldsJSONArray = objectEntryValuesJSONObject.getJSONArray(
			"fields");

		Assert.assertNotNull(fieldsJSONArray);

		Assert.assertNull(_findField(fieldsJSONArray, booleanFieldName));

		JSONObject localizedFieldJSONObject = _findField(
			fieldsJSONArray, localizedFieldName);

		Assert.assertNotNull(localizedFieldJSONObject);

		JSONObject valueI18nJSONObject = localizedFieldJSONObject.getJSONObject(
			"value_i18n");

		Assert.assertNotNull(valueI18nJSONObject);
		Assert.assertEquals(
			localizedFieldValueMap.get(LocaleUtil.toLanguageId(LocaleUtil.US)),
			valueI18nJSONObject.getString(
				LocaleUtil.toLanguageId(LocaleUtil.US)));

		JSONObject textFieldJSONObject = _findField(
			fieldsJSONArray, textFieldName);

		Assert.assertNotNull(textFieldJSONObject);
		Assert.assertEquals(
			textFieldName, textFieldJSONObject.getString("label"));
		Assert.assertEquals(
			textFieldValue, textFieldJSONObject.getString("value"));
	}

	@Test
	public void testExecuteWithObjectRelationship() throws Exception {
		String parentTextFieldName = "parentTextField";

		ObjectDefinition parentObjectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition(
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(parentTextFieldName)
					).name(
						parentTextFieldName
					).build()));

		parentObjectDefinition =
			ObjectDefinitionLocalServiceUtil.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				parentObjectDefinition.getObjectDefinitionId());

		String childTextFieldName = "childTextField";

		ObjectDefinition childObjectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition(
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(childTextFieldName)
					).name(
						childTextFieldName
					).build()));

		childObjectDefinition =
			ObjectDefinitionLocalServiceUtil.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				childObjectDefinition.getObjectDefinitionId());

		ObjectRelationship objectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				_objectRelationshipLocalService, parentObjectDefinition,
				childObjectDefinition);

		objectRelationship.setDeletionType(
			ObjectRelationshipConstants.DELETION_TYPE_CASCADE);
		objectRelationship.setType(
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		objectRelationship =
			_objectRelationshipLocalService.updateObjectRelationship(
				objectRelationship);

		String parentExternalReferenceCode = PortalUUIDUtil.generate();

		ObjectEntry parentObjectEntry =
			ObjectEntryLocalServiceUtil.addObjectEntry(
				0L, TestPropsValues.getUserId(),
				parentObjectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					parentTextFieldName, RandomTestUtil.randomString()
				).build(),
				ServiceContextTestUtil.getServiceContext());

		parentObjectEntry.setExternalReferenceCode(parentExternalReferenceCode);

		parentObjectEntry = ObjectEntryLocalServiceUtil.updateObjectEntry(
			parentObjectEntry);

		String childTextFieldValue = RandomTestUtil.randomString();
		String childExternalReferenceCode = PortalUUIDUtil.generate();

		ObjectEntry childObjectEntry =
			ObjectEntryLocalServiceUtil.addObjectEntry(
				0L, TestPropsValues.getUserId(),
				childObjectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					childTextFieldName, childTextFieldValue
				).build(),
				ServiceContextTestUtil.getServiceContext());

		childObjectEntry.setExternalReferenceCode(childExternalReferenceCode);

		childObjectEntry = ObjectEntryLocalServiceUtil.updateObjectEntry(
			childObjectEntry);

		ObjectRelationshipTestUtil.relateObjectEntries(
			parentObjectEntry.getObjectEntryId(),
			childObjectEntry.getObjectEntryId(), objectRelationship,
			TestPropsValues.getUserId());

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(TestPropsValues.getUser());

		JSONArray objectEntriesJSONArray = JSONUtil.put(
			JSONUtil.put(
				"className", parentObjectDefinition.getClassName()
			).put(
				"objectEntryId", parentObjectEntry.getObjectEntryId()
			));

		mockHttpServletRequest.setParameter(
			"objectEntries", objectEntriesJSONArray.toString());

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_getObjectEntriesValuesStrutsAction.execute(
			mockHttpServletRequest, mockHttpServletResponse);

		JSONArray resultJSONArray = _jsonFactory.createJSONArray(
			mockHttpServletResponse.getContentAsString());

		JSONObject parentObjectEntryValuesJSONObject =
			resultJSONArray.getJSONObject(0);

		Assert.assertNotNull(parentObjectEntryValuesJSONObject);

		JSONArray relatedJSONArray =
			parentObjectEntryValuesJSONObject.getJSONArray("related");

		Assert.assertNotNull(relatedJSONArray);
		Assert.assertEquals(1, relatedJSONArray.length());

		JSONObject childObjectEntryValuesJSONObject =
			relatedJSONArray.getJSONObject(0);

		Assert.assertEquals(
			childExternalReferenceCode,
			childObjectEntryValuesJSONObject.getString(
				"externalReferenceCode"));

		Assert.assertEquals(
			objectRelationship.getName(),
			childObjectEntryValuesJSONObject.getString("name"));

		JSONArray childFieldsJSONArray =
			childObjectEntryValuesJSONObject.getJSONArray("fields");

		JSONObject childTextFieldJSONObject = _findField(
			childFieldsJSONArray, childTextFieldName);

		Assert.assertNotNull(childTextFieldJSONObject);
		Assert.assertEquals(
			childTextFieldValue, childTextFieldJSONObject.getString("value"));
	}

	@Test
	public void testExecuteWithPermissions() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition(
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap("textField")
					).name(
						"textField"
					).build()));

		objectDefinition =
			ObjectDefinitionLocalServiceUtil.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId());

		ObjectEntry objectEntry1 = ObjectEntryLocalServiceUtil.addObjectEntry(
			0L, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"textField", "Value 1"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		ObjectEntry objectEntry2 = ObjectEntryLocalServiceUtil.addObjectEntry(
			0L, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"textField", "Value 2"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		User user = UserTestUtil.addUser();

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		RoleLocalServiceUtil.addUserRoles(
			user.getUserId(), new long[] {role.getRoleId()});

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), objectDefinition.getClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntry1.getObjectEntryId()), role.getRoleId(),
			new String[] {ActionKeys.VIEW});

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			MockHttpServletRequest mockHttpServletRequest =
				_getMockHttpServletRequest(user);

			mockHttpServletRequest.setParameter(
				"objectEntries",
				JSONUtil.putAll(
					JSONUtil.put(
						"className", objectDefinition.getClassName()
					).put(
						"objectEntryId", objectEntry1.getObjectEntryId()
					),
					JSONUtil.put(
						"className", objectDefinition.getClassName()
					).put(
						"objectEntryId", objectEntry2.getObjectEntryId()
					)
				).toString());

			MockHttpServletResponse mockHttpServletResponse =
				new MockHttpServletResponse();

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					"com.liferay.site.cms.site.initializer.internal.struts." +
						"GetObjectEntriesValuesStrutsAction",
					LoggerTestUtil.OFF)) {

				_getObjectEntriesValuesStrutsAction.execute(
					mockHttpServletRequest, mockHttpServletResponse);
			}

			JSONArray resultJSONArray = _jsonFactory.createJSONArray(
				mockHttpServletResponse.getContentAsString());

			Assert.assertEquals(1, resultJSONArray.length());

			JSONObject objectEntryValuesJSONObject =
				resultJSONArray.getJSONObject(0);

			Assert.assertEquals(
				String.valueOf(objectEntry1.getObjectEntryId()),
				objectEntryValuesJSONObject.getString("id"));
		}
	}

	private JSONObject _findField(JSONArray fieldsJSONArray, String fieldName) {
		for (int i = 0; i < fieldsJSONArray.length(); i++) {
			JSONObject fieldJSONObject = fieldsJSONArray.getJSONObject(i);

			if (Objects.equals(fieldName, fieldJSONObject.getString("name"))) {
				return fieldJSONObject;
			}
		}

		return null;
	}

	private MockHttpServletRequest _getMockHttpServletRequest(User user)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.US);
		themeDisplay.setUser(user);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockHttpServletRequest;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(filter = "path=/cms/get_object_entries_values")
	private StrutsAction _getObjectEntriesValuesStrutsAction;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private UserLocalService _userLocalService;

}