/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.rest.resource.v1_0.test;

import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.notification.constants.NotificationConstants;
import com.liferay.notification.constants.NotificationRecipientConstants;
import com.liferay.notification.constants.NotificationRecipientSettingConstants;
import com.liferay.notification.constants.NotificationTemplateConstants;
import com.liferay.notification.rest.client.dto.v1_0.Creator;
import com.liferay.notification.rest.client.dto.v1_0.NotificationTemplate;
import com.liferay.notification.rest.client.pagination.Page;
import com.liferay.notification.rest.client.pagination.Pagination;
import com.liferay.notification.rest.resource.v1_0.NotificationTemplateResource;
import com.liferay.notification.service.NotificationTemplateLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.constants.FeatureFlagConstants;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.props.test.util.PropsTemporarySwapper;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Gabriel Albuquerque
 */
@FeatureFlags(featureFlags = @FeatureFlag(value = "LPD-49854"))
@RunWith(Arquillian.class)
public class NotificationTemplateResourceTest
	extends BaseNotificationTemplateResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);
		_user = UserTestUtil.addUser();
		_userGroup = UserGroupTestUtil.addUserGroup();
	}

	@Override
	@Test
	public void testDeleteNotificationTemplateByExternalReferenceCode()
		throws Exception {

		super.testDeleteNotificationTemplateByExternalReferenceCode();

		_testDeleteNotificationTemplateByExternalReferenceCodeNotFound();
	}

	@Override
	@Test
	public void testGetNotificationTemplate() throws Exception {
		super.testGetNotificationTemplate();

		_testGetNotificationTemplateWithRecipientsAndEmailType();
		_testGetNotificationTemplateWithRecipientsAndFeatureFlagDisabled();
		_testGetNotificationTemplateWithRecipientsAndUserNotificationType();
	}

	@Override
	@Test
	public void testGetNotificationTemplatesPage() throws Exception {
		super.testGetNotificationTemplatesPage();

		_testGetNotificationTemplatesPageWithObjectDefinitionIdFilter();
		_testGetNotificationTemplatesPageWithSystemFilter();
	}

	@Override
	@Test
	public void testGetNotificationTemplatesPageWithSortInteger()
		throws Exception {

		testGetNotificationTemplatesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, notificationTemplate1, notificationTemplate2) -> {
				if (BeanTestUtil.hasProperty(
						notificationTemplate1, entityField.getName())) {

					BeanTestUtil.setProperty(
						notificationTemplate1, entityField.getName(), 0);
				}

				if (BeanTestUtil.hasProperty(
						notificationTemplate2, entityField.getName())) {

					BeanTestUtil.setProperty(
						notificationTemplate2, entityField.getName(), 1);
				}
			});
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetNotificationTemplate() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetNotificationTemplateByExternalReferenceCode()
		throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetNotificationTemplateByExternalReferenceCodeNotFound() {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetNotificationTemplateNotFound() {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetNotificationTemplatesPage() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLPostNotificationTemplate() throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLPostNotificationTemplateCopy() throws Exception {
	}

	@Override
	@Test
	public void testPatchNotificationTemplate() throws Exception {
		super.testPatchNotificationTemplate();

		_testPatchNotificationTemplateWithName();
		_testPatchNotificationTemplateWithoutRecipients();
		_testPatchPutNotificationTemplateWithRecipientsAndEmailType(
			Http.Method.PATCH);
		_testPatchPutNotificationTemplateWithRecipientsAndUserNotificationType(
			Http.Method.PATCH);
	}

	@Override
	@Test
	public void testPostNotificationTemplate() throws Exception {
		super.testPostNotificationTemplate();

		_testPostNotificationTemplateWithCreator();
		_testPostNotificationTemplateWithRecipientsAndEmailType();
		_testPostNotificationTemplateWithNameWithoutDefaultLanguage();
		_testPostNotificationTemplateWithPermissions();
		_testPostNotificationTemplateWithPermissionsAndFeatureFlagDisabled();
		_testPostNotificationTemplateWithRecipientsAndFeatureFlagDisabled();
		_testPostNotificationTemplateWithRecipientsAndUserNotificationType();
	}

	@Override
	@Test
	public void testPostNotificationTemplateCopy() throws Exception {
		super.testPostNotificationTemplateCopy();

		NotificationTemplate systemNotificationTemplate =
			randomNotificationTemplate();

		systemNotificationTemplate.setSystem(true);

		systemNotificationTemplate = _addNotificationTemplate(
			systemNotificationTemplate);

		Assert.assertTrue(systemNotificationTemplate.getSystem());

		NotificationTemplate notificationTemplate =
			notificationTemplateResource.postNotificationTemplateCopy(
				systemNotificationTemplate.getId());

		Assert.assertEquals(
			systemNotificationTemplate.getName() + " (copy)",
			notificationTemplate.getName());
		Assert.assertFalse(notificationTemplate.getSystem());
	}

	@Override
	@Test
	public void testPutNotificationTemplate() throws Exception {
		super.testPutNotificationTemplate();

		_testPatchPutNotificationTemplateWithRecipientsAndEmailType(
			Http.Method.PUT);
		_testPatchPutNotificationTemplateWithRecipientsAndUserNotificationType(
			Http.Method.PUT);
		_testPutNotificationTemplateWithNameTranslations();
		_testPutNotificationTemplateWithPermissions();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"description", "name"};
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {"objectDefinitionId"};
	}

	@Override
	protected NotificationTemplate randomNotificationTemplate()
		throws Exception {

		NotificationTemplate notificationTemplate =
			super.randomNotificationTemplate();

		notificationTemplate.setBody(
			LocalizedMapUtil.getI18nMap(
				RandomTestUtil.randomLocaleStringMap()));
		notificationTemplate.setEditorType(
			NotificationTemplate.EditorType.RICH_TEXT);
		notificationTemplate.setObjectDefinitionExternalReferenceCode(
			StringPool.BLANK);
		notificationTemplate.setObjectDefinitionId(0L);
		notificationTemplate.setRecipients(new Object[0]);
		notificationTemplate.setRecipientType(
			NotificationRecipientConstants.TYPE_USER);
		notificationTemplate.setSubject(
			LocalizedMapUtil.getI18nMap(
				RandomTestUtil.randomLocaleStringMap()));
		notificationTemplate.setSystem(false);
		notificationTemplate.setType(
			NotificationConstants.TYPE_USER_NOTIFICATION);

		return notificationTemplate;
	}

	@Override
	protected NotificationTemplate
			testDeleteNotificationTemplate_addNotificationTemplate()
		throws Exception {

		return _addNotificationTemplate(randomNotificationTemplate());
	}

	@Override
	protected NotificationTemplate
			testDeleteNotificationTemplateByExternalReferenceCode_addNotificationTemplate()
		throws Exception {

		return _addNotificationTemplate(randomNotificationTemplate());
	}

	@Override
	protected NotificationTemplate
			testGetNotificationTemplate_addNotificationTemplate()
		throws Exception {

		return _addNotificationTemplate(randomNotificationTemplate());
	}

	@Override
	protected NotificationTemplate
			testGetNotificationTemplateByExternalReferenceCode_addNotificationTemplate()
		throws Exception {

		return _addNotificationTemplate(randomNotificationTemplate());
	}

	@Override
	protected NotificationTemplate
			testGetNotificationTemplatesPage_addNotificationTemplate(
				NotificationTemplate notificationTemplate)
		throws Exception {

		return _addNotificationTemplate(notificationTemplate);
	}

	@Override
	protected NotificationTemplate
			testGraphQLNotificationTemplate_addNotificationTemplate()
		throws Exception {

		return _addNotificationTemplate(randomNotificationTemplate());
	}

	@Override
	protected NotificationTemplate
			testPatchNotificationTemplate_addNotificationTemplate()
		throws Exception {

		return _addNotificationTemplate(randomNotificationTemplate());
	}

	@Override
	protected NotificationTemplate
			testPostNotificationTemplate_addNotificationTemplate(
				NotificationTemplate notificationTemplate)
		throws Exception {

		return _addNotificationTemplate(notificationTemplate);
	}

	@Override
	protected NotificationTemplate
			testPostNotificationTemplateCopy_addNotificationTemplate(
				NotificationTemplate notificationTemplate)
		throws Exception {

		return _addNotificationTemplate(notificationTemplate);
	}

	@Override
	protected NotificationTemplate
			testPutNotificationTemplate_addNotificationTemplate()
		throws Exception {

		return _addNotificationTemplate(randomNotificationTemplate());
	}

	@Override
	protected NotificationTemplate
			testPutNotificationTemplateByExternalReferenceCode_addNotificationTemplate()
		throws Exception {

		return _addNotificationTemplate(randomNotificationTemplate());
	}

	private NotificationTemplate _addNotificationTemplate(
			NotificationTemplate notificationTemplate)
		throws Exception {

		notificationTemplate =
			notificationTemplateResource.postNotificationTemplate(
				notificationTemplate);

		_notificationTemplates.add(
			_notificationTemplateLocalService.fetchNotificationTemplate(
				notificationTemplate.getId()));

		return notificationTemplate;
	}

	private void _assertFailureNotificationTemplate(
		String expectedTitle, JSONObject jsonObject) {

		Assert.assertEquals(
			jsonObject.toString(), Response.Status.BAD_REQUEST.name(),
			jsonObject.getString("status"));
		Assert.assertEquals(
			jsonObject.toString(), expectedTitle,
			jsonObject.getString("title"));
	}

	private void _assertNotificationTemplateRecipients(
			JSONArray expectedRecipientsJSONArray,
			JSONObject notificationTemplateJSONObject)
		throws Exception {

		JSONAssert.assertEquals(
			expectedRecipientsJSONArray.toString(),
			JSONUtil.getValueAsString(
				notificationTemplateJSONObject, "JSONArray/recipients"),
			JSONCompareMode.NON_EXTENSIBLE);
	}

	private void _assertPermissions(JSONObject jsonObject, String roleName)
		throws Exception {

		Assert.assertEquals(
			Collections.singletonList(roleName),
			JSONUtil.toList(
				jsonObject.getJSONArray("permissions"),
				permissionJSONObject -> permissionJSONObject.getString(
					"roleName")));
	}

	private JSONObject _getNotificationTemplateJSONObject(String roleName) {
		return JSONUtil.put(
			"description", RandomTestUtil.randomString()
		).put(
			"editorType", NotificationTemplateConstants.EDITOR_TYPE_RICH_TEXT
		).put(
			"name", RandomTestUtil.randomString()
		).put(
			"permissions", JSONUtil.putAll(_getPermissionJSONObject(roleName))
		).put(
			"recipients", JSONUtil.putAll()
		).put(
			"subject",
			JSONUtil.put(
				LocaleUtil.toLanguageId(LocaleUtil.getDefault()),
				RandomTestUtil.randomString())
		).put(
			"type", NotificationConstants.TYPE_USER_NOTIFICATION
		);
	}

	private JSONObject _getPermissionJSONObject(String roleName) {
		return JSONUtil.put(
			"actionIds", new String[] {ActionKeys.VIEW}
		).put(
			"roleName", roleName
		);
	}

	private JSONObject _patchPutNotificationTemplateJSONObject(
			Http.Method httpMethod, long notificationTemplateId,
			JSONArray recipientsJSONArray, String recipientType, String type)
		throws Exception {

		return HTTPTestUtil.invokeToJSONObject(
			_toNotificationTemplateJSONObject(
				recipientsJSONArray, recipientType, type
			).toString(),
			"notification/v1.0/notification-templates/" +
				notificationTemplateId,
			httpMethod);
	}

	private JSONObject _postNotificationTemplateJSONObject(
			JSONArray recipientsJSONArray, String recipientType, String type)
		throws Exception {

		JSONObject notificationTemplateJSONObject =
			HTTPTestUtil.invokeToJSONObject(
				_toNotificationTemplateJSONObject(
					recipientsJSONArray, recipientType, type
				).toString(),
				"notification/v1.0/notification-templates", Http.Method.POST);

		com.liferay.notification.model.NotificationTemplate
			notificationTemplate =
				_notificationTemplateLocalService.fetchNotificationTemplate(
					notificationTemplateJSONObject.getLong("id"));

		if (notificationTemplate != null) {
			_notificationTemplates.add(notificationTemplate);
		}

		return notificationTemplateJSONObject;
	}

	private JSONObject _postNotificationTemplateWithPermissions(String roleName)
		throws Exception {

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			_getNotificationTemplateJSONObject(
				roleName
			).toString(),
			"notification/v1.0/notification-templates?nestedFields=permissions",
			Http.Method.POST);

		_notificationTemplates.add(
			_notificationTemplateLocalService.fetchNotificationTemplate(
				jsonObject.getLong("id")));

		return jsonObject;
	}

	private JSONObject _putNotificationTemplateWithPermissions(
			long notificationTemplateId, String roleName)
		throws Exception {

		return HTTPTestUtil.invokeToJSONObject(
			_getNotificationTemplateJSONObject(
				roleName
			).toString(),
			"notification/v1.0/notification-templates/" +
				notificationTemplateId + "?nestedFields=permissions",
			Http.Method.PUT);
	}

	private void _testDeleteNotificationTemplateByExternalReferenceCodeNotFound()
		throws Exception {

		assertHttpResponseStatusCode(
			404,
			notificationTemplateResource.
				deleteNotificationTemplateByExternalReferenceCodeHttpResponse(
					RandomTestUtil.randomString()));
	}

	private void _testGetNotificationTemplate(
			JSONArray recipientsJSONArray, String recipientType, String type)
		throws Exception {

		JSONObject notificationTemplateJSONObject =
			_postNotificationTemplateJSONObject(
				recipientsJSONArray, recipientType, type);

		_assertNotificationTemplateRecipients(
			recipientsJSONArray,
			HTTPTestUtil.invokeToJSONObject(
				null,
				"notification/v1.0/notification-templates/" +
					notificationTemplateJSONObject.getLong("id"),
				Http.Method.GET));
	}

	private void _testGetNotificationTemplateWithRecipientsAndEmailType()
		throws Exception {

		_testGetNotificationTemplate(
			JSONUtil.putAll(
				_toEmailRecipientJSONObject(
					RandomTestUtil.randomString() + "@liferay.com",
					JSONUtil.put("en_US", RandomTestUtil.randomString()),
					JSONUtil.putAll(_toRoleJSONObject(_role.getName())),
					NotificationRecipientConstants.TYPE_ROLE
				).put(
					NotificationRecipientSettingConstants.NAME_BCC,
					JSONUtil.putAll(_toUserGroupJSONObject(_userGroup))
				).put(
					NotificationRecipientSettingConstants.NAME_BCC_TYPE,
					NotificationRecipientConstants.TYPE_USER_GROUP
				)),
			NotificationRecipientConstants.TYPE_EMAIL,
			NotificationConstants.TYPE_EMAIL);
	}

	private void _testGetNotificationTemplateWithRecipientsAndFeatureFlagDisabled()
		throws Exception {

		try (PropsTemporarySwapper propsTemporarySwapper =
				new PropsTemporarySwapper(
					FeatureFlagConstants.getKey("LPD-49854"),
					Boolean.FALSE.toString())) {

			_testGetNotificationTemplate(
				JSONUtil.putAll(
					_toEmailRecipientJSONObject(
						RandomTestUtil.randomString() + "@liferay.com",
						JSONUtil.put("en_US", RandomTestUtil.randomString()),
						JSONUtil.putAll(
							JSONUtil.put(
								NotificationRecipientSettingConstants.
									NAME_ROLE_NAME,
								_role.getName())),
						NotificationRecipientConstants.TYPE_ROLE
					).put(
						NotificationRecipientSettingConstants.NAME_BCC,
						JSONUtil.putAll(
							JSONUtil.put(
								NotificationRecipientSettingConstants.
									NAME_USER_GROUP_NAME,
								_userGroup.getName()))
					).put(
						NotificationRecipientSettingConstants.NAME_BCC_TYPE,
						NotificationRecipientConstants.TYPE_USER_GROUP
					)),
				NotificationRecipientConstants.TYPE_EMAIL,
				NotificationConstants.TYPE_EMAIL);
			_testGetNotificationTemplate(
				JSONUtil.putAll(
					JSONUtil.put(
						NotificationRecipientSettingConstants.
							NAME_USER_SCREEN_NAME,
						_user.getScreenName())),
				NotificationRecipientConstants.TYPE_USER,
				NotificationConstants.TYPE_USER_NOTIFICATION);
		}
	}

	private void _testGetNotificationTemplateWithRecipientsAndUserNotificationType()
		throws Exception {

		_testGetNotificationTemplate(
			JSONUtil.putAll(_toRoleJSONObject(_role.getName())),
			NotificationRecipientConstants.TYPE_ROLE,
			NotificationConstants.TYPE_USER_NOTIFICATION);
		_testGetNotificationTemplate(
			JSONUtil.putAll(_toUserJSONObject(_user)),
			NotificationRecipientConstants.TYPE_USER,
			NotificationConstants.TYPE_USER_NOTIFICATION);
		_testGetNotificationTemplate(
			JSONUtil.putAll(_toUserGroupJSONObject(_userGroup)),
			NotificationRecipientConstants.TYPE_USER_GROUP,
			NotificationConstants.TYPE_USER_NOTIFICATION);
	}

	private void _testGetNotificationTemplatesPageWithObjectDefinitionIdFilter()
		throws Exception {

		NotificationTemplate notificationTemplate = _addNotificationTemplate(
			randomNotificationTemplate());

		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition();

		NotificationTemplate objectNotificationTemplate =
			randomNotificationTemplate();

		objectNotificationTemplate.setObjectDefinitionId(
			_objectDefinition.getObjectDefinitionId());

		objectNotificationTemplate = _addNotificationTemplate(
			objectNotificationTemplate);

		Page<NotificationTemplate> page =
			notificationTemplateResource.getNotificationTemplatesPage(
				null, null, "objectDefinitionId eq 0", Pagination.of(1, 100),
				null);

		List<Long> notificationTemplateIds = TransformUtil.transform(
			page.getItems(), NotificationTemplate::getId);

		Assert.assertTrue(
			notificationTemplateIds.contains(notificationTemplate.getId()));
		Assert.assertFalse(
			notificationTemplateIds.contains(
				objectNotificationTemplate.getId()));
	}

	private void _testGetNotificationTemplatesPageWithSystemFilter()
		throws Exception {

		NotificationTemplate notificationTemplate = _addNotificationTemplate(
			randomNotificationTemplate());

		NotificationTemplate systemNotificationTemplate =
			randomNotificationTemplate();

		systemNotificationTemplate.setSystem(true);

		systemNotificationTemplate = _addNotificationTemplate(
			systemNotificationTemplate);

		Page<NotificationTemplate> page =
			notificationTemplateResource.getNotificationTemplatesPage(
				null, null, "system eq false", Pagination.of(1, 100), null);

		List<Long> notificationTemplateIds = TransformUtil.transform(
			page.getItems(), NotificationTemplate::getId);

		Assert.assertTrue(
			notificationTemplateIds.contains(notificationTemplate.getId()));
		Assert.assertFalse(
			notificationTemplateIds.contains(
				systemNotificationTemplate.getId()));
	}

	private void _testPatchNotificationTemplateWithName() throws Exception {
		NotificationTemplate notificationTemplate =
			randomNotificationTemplate();

		String defaultLanguageId = LocaleUtil.toLanguageId(
			LocaleUtil.getSiteDefault());
		String translatedLanguageId = LocaleUtil.toLanguageId(
			LocaleUtil.BRAZIL);
		String translatedName = RandomTestUtil.randomString();

		notificationTemplate.setName_i18n(
			HashMapBuilder.put(
				defaultLanguageId, notificationTemplate.getName()
			).put(
				translatedLanguageId, translatedName
			).build());

		notificationTemplate = _addNotificationTemplate(notificationTemplate);

		String name = RandomTestUtil.randomString();

		HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"name", name
			).toString(),
			"notification/v1.0/notification-templates/" +
				notificationTemplate.getId(),
			Http.Method.PATCH);

		notificationTemplate =
			notificationTemplateResource.getNotificationTemplate(
				notificationTemplate.getId());

		Assert.assertEquals(name, notificationTemplate.getName());

		Map<String, String> nameI18nMap = notificationTemplate.getName_i18n();

		Assert.assertEquals(name, nameI18nMap.get(defaultLanguageId));
		Assert.assertEquals(
			translatedName, nameI18nMap.get(translatedLanguageId));
	}

	private void _testPatchNotificationTemplateWithoutRecipients()
		throws Exception {

		_testPatchNotificationTemplateWithoutRecipients(
			JSONUtil.putAll(
				_toEmailRecipientJSONObject(
					RandomTestUtil.randomString() + "@liferay.com",
					JSONUtil.put("en_US", RandomTestUtil.randomString()),
					JSONUtil.putAll(_toRoleJSONObject(_role.getName())),
					NotificationRecipientConstants.TYPE_ROLE
				).put(
					NotificationRecipientSettingConstants.NAME_BCC,
					JSONUtil.putAll(_toUserGroupJSONObject(_userGroup))
				).put(
					NotificationRecipientSettingConstants.NAME_BCC_TYPE,
					NotificationRecipientConstants.TYPE_USER_GROUP
				)),
			NotificationRecipientConstants.TYPE_EMAIL,
			NotificationConstants.TYPE_EMAIL);
		_testPatchNotificationTemplateWithoutRecipients(
			JSONUtil.putAll(_toUserJSONObject(_user)),
			NotificationRecipientConstants.TYPE_USER,
			NotificationConstants.TYPE_USER_NOTIFICATION);
	}

	private void _testPatchNotificationTemplateWithoutRecipients(
			JSONArray recipientsJSONArray, String recipientType, String type)
		throws Exception {

		JSONObject notificationTemplateJSONObject =
			_postNotificationTemplateJSONObject(
				recipientsJSONArray, recipientType, type);

		String name = RandomTestUtil.randomString();

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				"name", name
			).toString(),
			"notification/v1.0/notification-templates/" +
				notificationTemplateJSONObject.getLong("id"),
			Http.Method.PATCH);

		Assert.assertEquals(name, jsonObject.getString("name"));

		_assertNotificationTemplateRecipients(recipientsJSONArray, jsonObject);
	}

	private void _testPatchPutNotificationTemplateWithRecipientsAndEmailType(
			Http.Method httpMethod)
		throws Exception {

		// Notification template recipient type email

		JSONObject notificationTemplateJSONObject =
			_postNotificationTemplateJSONObject(
				JSONUtil.putAll(
					_toEmailRecipientJSONObject(
						RandomTestUtil.randomString() + "@liferay.com",
						JSONUtil.put("en_US", RandomTestUtil.randomString()),
						JSONUtil.put("en_US", RandomTestUtil.randomString()),
						NotificationRecipientConstants.TYPE_EMAIL)),
				NotificationRecipientConstants.TYPE_EMAIL,
				NotificationConstants.TYPE_EMAIL);

		long notificationTemplateId = notificationTemplateJSONObject.getLong(
			"id");

		String from = RandomTestUtil.randomString() + "@liferay.com";
		JSONObject fromNameJSONObject = JSONUtil.put(
			"en_US", RandomTestUtil.randomString());

		JSONArray recipientsJSONArray = JSONUtil.putAll(
			_toEmailRecipientJSONObject(
				from, fromNameJSONObject,
				JSONUtil.put("en_US", RandomTestUtil.randomString()),
				NotificationRecipientConstants.TYPE_EMAIL));

		_assertNotificationTemplateRecipients(
			recipientsJSONArray,
			_patchPutNotificationTemplateJSONObject(
				httpMethod, notificationTemplateId, recipientsJSONArray,
				NotificationRecipientConstants.TYPE_EMAIL,
				NotificationConstants.TYPE_EMAIL));

		// Notification template recipient type role

		_assertFailureNotificationTemplate(
			"The role recipient does not exist.",
			_patchPutNotificationTemplateJSONObject(
				httpMethod, notificationTemplateId,
				JSONUtil.putAll(
					_toEmailRecipientJSONObject(
						from, fromNameJSONObject,
						JSONUtil.putAll(
							_toRoleJSONObject(
								RandomTestUtil.randomString(),
								_role.getName())),
						NotificationRecipientConstants.TYPE_ROLE)),
				NotificationRecipientConstants.TYPE_EMAIL,
				NotificationConstants.TYPE_EMAIL));

		_assertNotificationTemplateRecipients(
			JSONUtil.putAll(
				_toEmailRecipientJSONObject(
					from, fromNameJSONObject,
					JSONUtil.putAll(_toRoleJSONObject(_role.getName())),
					NotificationRecipientConstants.TYPE_ROLE)),
			_patchPutNotificationTemplateJSONObject(
				httpMethod, notificationTemplateId,
				JSONUtil.putAll(
					_toEmailRecipientJSONObject(
						from, fromNameJSONObject,
						JSONUtil.putAll(
							_toRoleJSONObject(
								_role.getExternalReferenceCode(),
								RandomTestUtil.randomString())),
						NotificationRecipientConstants.TYPE_ROLE)),
				NotificationRecipientConstants.TYPE_EMAIL,
				NotificationConstants.TYPE_EMAIL));

		// Notification template recipient type user group

		_assertFailureNotificationTemplate(
			"The user group recipient does not exist.",
			_patchPutNotificationTemplateJSONObject(
				httpMethod, notificationTemplateId,
				JSONUtil.putAll(
					_toEmailRecipientJSONObject(
						from, fromNameJSONObject,
						JSONUtil.putAll(
							JSONUtil.put(
								NotificationRecipientSettingConstants.
									NAME_ROLE_NAME,
								_role.getName())),
						NotificationRecipientConstants.TYPE_ROLE
					).put(
						NotificationRecipientSettingConstants.NAME_BCC,
						JSONUtil.putAll(
							_toUserGroupJSONObject(
								RandomTestUtil.randomString(),
								_userGroup.getName()))
					).put(
						NotificationRecipientSettingConstants.NAME_BCC_TYPE,
						NotificationRecipientConstants.TYPE_USER_GROUP
					)),
				NotificationRecipientConstants.TYPE_EMAIL,
				NotificationConstants.TYPE_EMAIL));

		_assertNotificationTemplateRecipients(
			JSONUtil.putAll(
				_toEmailRecipientJSONObject(
					from, fromNameJSONObject,
					JSONUtil.putAll(_toRoleJSONObject(_role.getName())),
					NotificationRecipientConstants.TYPE_ROLE
				).put(
					NotificationRecipientSettingConstants.NAME_BCC,
					JSONUtil.putAll(_toUserGroupJSONObject(_userGroup))
				).put(
					NotificationRecipientSettingConstants.NAME_BCC_TYPE,
					NotificationRecipientConstants.TYPE_USER_GROUP
				)),
			_patchPutNotificationTemplateJSONObject(
				httpMethod, notificationTemplateId,
				JSONUtil.putAll(
					_toEmailRecipientJSONObject(
						from, fromNameJSONObject,
						JSONUtil.putAll(
							JSONUtil.put(
								NotificationRecipientSettingConstants.
									NAME_ROLE_NAME,
								_role.getName())),
						NotificationRecipientConstants.TYPE_ROLE
					).put(
						NotificationRecipientSettingConstants.NAME_BCC,
						JSONUtil.putAll(
							_toUserGroupJSONObject(
								_userGroup.getExternalReferenceCode(),
								RandomTestUtil.randomString()))
					).put(
						NotificationRecipientSettingConstants.NAME_BCC_TYPE,
						NotificationRecipientConstants.TYPE_USER_GROUP
					)),
				NotificationRecipientConstants.TYPE_EMAIL,
				NotificationConstants.TYPE_EMAIL));
	}

	private void
			_testPatchPutNotificationTemplateWithRecipientsAndUserNotificationType(
				Http.Method httpMethod)
		throws Exception {

		// Notification template recipient type role

		JSONObject notificationTemplateJSONObject =
			_postNotificationTemplateJSONObject(
				JSONUtil.putAll(
					JSONUtil.put(
						NotificationRecipientSettingConstants.
							NAME_USER_SCREEN_NAME,
						"[%OBJECT_AUTHOR%]")),
				NotificationRecipientConstants.TYPE_TERM,
				NotificationConstants.TYPE_USER_NOTIFICATION);

		long notificationTemplateId = notificationTemplateJSONObject.getLong(
			"id");

		_assertFailureNotificationTemplate(
			"The role recipient does not exist.",
			_patchPutNotificationTemplateJSONObject(
				httpMethod, notificationTemplateId,
				JSONUtil.putAll(
					_toRoleJSONObject(
						RandomTestUtil.randomString(), _role.getName())),
				NotificationRecipientConstants.TYPE_ROLE,
				NotificationConstants.TYPE_USER_NOTIFICATION));

		_assertNotificationTemplateRecipients(
			JSONUtil.putAll(_toRoleJSONObject(_role.getName())),
			_patchPutNotificationTemplateJSONObject(
				httpMethod, notificationTemplateId,
				JSONUtil.putAll(
					_toRoleJSONObject(
						_role.getExternalReferenceCode(),
						RandomTestUtil.randomString())),
				NotificationRecipientConstants.TYPE_ROLE,
				NotificationConstants.TYPE_USER_NOTIFICATION));

		// Notification template recipient type user

		_assertNotificationTemplateRecipients(
			JSONUtil.putAll(_toUserJSONObject(_user)),
			_patchPutNotificationTemplateJSONObject(
				httpMethod, notificationTemplateId,
				JSONUtil.putAll(
					_toUserJSONObject(
						_user.getExternalReferenceCode(),
						RandomTestUtil.randomString())),
				NotificationRecipientConstants.TYPE_USER,
				NotificationConstants.TYPE_USER_NOTIFICATION));

		// Notification template recipient type user group

		_assertFailureNotificationTemplate(
			"The user group recipient does not exist.",
			_patchPutNotificationTemplateJSONObject(
				httpMethod, notificationTemplateId,
				JSONUtil.putAll(
					_toUserGroupJSONObject(
						RandomTestUtil.randomString(), _userGroup.getName())),
				NotificationRecipientConstants.TYPE_USER_GROUP,
				NotificationConstants.TYPE_USER_NOTIFICATION));

		_assertNotificationTemplateRecipients(
			JSONUtil.putAll(_toUserGroupJSONObject(_userGroup)),
			_patchPutNotificationTemplateJSONObject(
				httpMethod, notificationTemplateId,
				JSONUtil.putAll(
					_toUserGroupJSONObject(
						_userGroup.getExternalReferenceCode(),
						RandomTestUtil.randomString())),
				NotificationRecipientConstants.TYPE_USER_GROUP,
				NotificationConstants.TYPE_USER_NOTIFICATION));
	}

	private void _testPostNotificationTemplate(JSONObject recipientJSONObject)
		throws Exception {

		_testPostNotificationTemplate(
			JSONFactoryUtil.createJSONObject(recipientJSONObject.toString()),
			recipientJSONObject);
	}

	private void _testPostNotificationTemplate(
			JSONObject expectedRecipientJSONObject,
			JSONObject recipientJSONObject)
		throws Exception {

		String from = RandomTestUtil.randomString() + "@liferay.com";
		JSONObject fromNameJSONObject = JSONUtil.put(
			"en_US", RandomTestUtil.randomString());

		expectedRecipientJSONObject.put(
			NotificationRecipientSettingConstants.NAME_FROM, from
		).put(
			NotificationRecipientSettingConstants.NAME_FROM_NAME,
			fromNameJSONObject
		);

		recipientJSONObject.put(
			NotificationRecipientSettingConstants.NAME_FROM, from
		).put(
			NotificationRecipientSettingConstants.NAME_FROM_NAME,
			fromNameJSONObject
		);

		JSONObject notificationTemplateJSONObject = JSONUtil.put(
			"editorType", NotificationTemplateConstants.EDITOR_TYPE_RICH_TEXT
		).put(
			"name", RandomTestUtil.randomString()
		).put(
			"recipients", JSONUtil.putAll(recipientJSONObject)
		).put(
			"subject",
			JSONUtil.put(
				LocaleUtil.toLanguageId(LocaleUtil.getDefault()),
				RandomTestUtil.randomString())
		).put(
			"type", NotificationConstants.TYPE_EMAIL
		);

		JSONAssert.assertEquals(
			expectedRecipientJSONObject.toString(),
			JSONUtil.getValueAsString(
				HTTPTestUtil.invokeToJSONObject(
					notificationTemplateJSONObject.toString(),
					"notification/v1.0/notification-templates",
					Http.Method.POST),
				"JSONArray/recipients", "JSONObject/0"),
			JSONCompareMode.NON_EXTENSIBLE);

		NotificationTemplateResource.Builder
			notificationTemplateResourceBuilder =
				_notificationTemplateResourceFactory.create();

		NotificationTemplateResource notificationTemplateResource =
			notificationTemplateResourceBuilder.user(
				TestPropsValues.getUser()
			).build();

		Assert.assertNotNull(
			notificationTemplateResource.postNotificationTemplate(
				com.liferay.notification.rest.dto.v1_0.NotificationTemplate.
					toDTO(notificationTemplateJSONObject.toString())));
	}

	private void _testPostNotificationTemplateWithCreator() throws Exception {
		NotificationTemplate notificationTemplate = _addNotificationTemplate(
			randomNotificationTemplate());

		Creator creator = notificationTemplate.getCreator();

		User user = TestPropsValues.getUser();

		Assert.assertEquals(
			user.getExternalReferenceCode(),
			creator.getExternalReferenceCode());

		com.liferay.notification.model.NotificationTemplate
			serviceBuilderNotificationTemplate =
				_notificationTemplateLocalService.addNotificationTemplate(
					RandomTestUtil.randomString(), _user.getUserId(),
					NotificationConstants.TYPE_EMAIL);

		_notificationTemplates.add(serviceBuilderNotificationTemplate);

		notificationTemplate =
			notificationTemplateResource.getNotificationTemplate(
				serviceBuilderNotificationTemplate.getNotificationTemplateId());

		creator = notificationTemplate.getCreator();

		Assert.assertEquals(
			_user.getExternalReferenceCode(),
			creator.getExternalReferenceCode());
	}

	private void _testPostNotificationTemplateWithNameWithoutDefaultLanguage()
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.ERROR)) {

			Assert.assertEquals(
				400,
				HTTPTestUtil.invokeToHttpCode(
					JSONUtil.put(
						"name_i18n",
						JSONUtil.put(
							LocaleUtil.toLanguageId(LocaleUtil.BRAZIL),
							RandomTestUtil.randomString())
					).put(
						"recipients", JSONUtil.putAll()
					).toString(),
					"notification/v1.0/notification-templates",
					Http.Method.POST));
		}
	}

	private void _testPostNotificationTemplateWithPermissions()
		throws Exception {

		_assertPermissions(
			_postNotificationTemplateWithPermissions(
				RoleConstants.ADMINISTRATOR),
			RoleConstants.ADMINISTRATOR);
	}

	private void _testPostNotificationTemplateWithPermissionsAndFeatureFlagDisabled()
		throws Exception {

		try (PropsTemporarySwapper propsTemporarySwapper =
				new PropsTemporarySwapper(
					FeatureFlagConstants.getKey("LPD-49854"),
					Boolean.FALSE.toString())) {

			Assert.assertEquals(
				400,
				HTTPTestUtil.invokeToHttpCode(
					JSONUtil.put(
						"permissions",
						JSONUtil.putAll(
							_getPermissionJSONObject(
								RoleConstants.ADMINISTRATOR))
					).toString(),
					"notification/v1.0/notification-templates",
					Http.Method.POST));
		}
	}

	private void _testPostNotificationTemplateWithRecipient(
			JSONArray expectedRecipientsJSONArray,
			JSONObject recipientJSONObject, String recipientType)
		throws Exception {

		_assertNotificationTemplateRecipients(
			expectedRecipientsJSONArray,
			_postNotificationTemplateJSONObject(
				JSONUtil.putAll(recipientJSONObject), recipientType,
				NotificationConstants.TYPE_USER_NOTIFICATION));
	}

	private void _testPostNotificationTemplateWithRecipientsAndEmailType()
		throws Exception {

		// Notification template recipient type email

		_testPostNotificationTemplate(
			JSONUtil.put(
				NotificationRecipientSettingConstants.NAME_TO,
				JSONUtil.put("en_US", RandomTestUtil.randomString())
			).put(
				NotificationRecipientSettingConstants.NAME_TO_TYPE,
				NotificationRecipientConstants.TYPE_EMAIL
			));

		// Notification template recipient type role

		_assertFailureNotificationTemplate(
			"The role recipient does not exist.",
			_postNotificationTemplateJSONObject(
				JSONUtil.putAll(
					JSONUtil.put(
						NotificationRecipientSettingConstants.NAME_TO,
						JSONUtil.putAll(
							_toRoleJSONObject(
								RandomTestUtil.randomString(), _role.getName()))
					).put(
						NotificationRecipientSettingConstants.NAME_TO_TYPE,
						NotificationRecipientConstants.TYPE_ROLE
					)),
				NotificationRecipientConstants.TYPE_EMAIL,
				NotificationConstants.TYPE_EMAIL));

		_testPostNotificationTemplate(
			JSONUtil.put(
				NotificationRecipientSettingConstants.NAME_TO,
				JSONUtil.putAll(
					_toRoleJSONObject(
						AccountRoleConstants.
							REQUIRED_ROLE_NAME_ACCOUNT_ADMINISTRATOR),
					_toRoleJSONObject(
						AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_MEMBER),
					_toRoleJSONObject(RoleConstants.ORGANIZATION_ADMINISTRATOR),
					_toRoleJSONObject(RoleConstants.ORGANIZATION_OWNER),
					_toRoleJSONObject(_role.getName()))
			).put(
				NotificationRecipientSettingConstants.NAME_TO_TYPE,
				NotificationRecipientConstants.TYPE_ROLE
			),
			JSONUtil.put(
				NotificationRecipientSettingConstants.NAME_TO,
				JSONUtil.putAll(
					JSONUtil.put(
						NotificationRecipientSettingConstants.NAME_ROLE_NAME,
						AccountRoleConstants.
							REQUIRED_ROLE_NAME_ACCOUNT_ADMINISTRATOR),
					JSONUtil.put(
						NotificationRecipientSettingConstants.NAME_ROLE_NAME,
						AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_MEMBER),
					JSONUtil.put(
						NotificationRecipientSettingConstants.NAME_ROLE_NAME,
						RoleConstants.ORGANIZATION_ADMINISTRATOR),
					JSONUtil.put(
						NotificationRecipientSettingConstants.NAME_ROLE_NAME,
						RoleConstants.ORGANIZATION_OWNER),
					_toRoleJSONObject(
						_role.getExternalReferenceCode(),
						RandomTestUtil.randomString()))
			).put(
				NotificationRecipientSettingConstants.NAME_TO_TYPE,
				NotificationRecipientConstants.TYPE_ROLE
			));

		// Notification template recipient type subscribers

		_testPostNotificationTemplate(
			JSONUtil.put(
				NotificationRecipientSettingConstants.NAME_TO_TYPE,
				NotificationRecipientConstants.TYPE_SUBSCRIBERS));

		// Notification template recipient type user group

		_assertFailureNotificationTemplate(
			"The user group recipient does not exist.",
			_postNotificationTemplateJSONObject(
				JSONUtil.putAll(
					JSONUtil.put(
						NotificationRecipientSettingConstants.NAME_TO,
						JSONUtil.putAll(
							_toUserGroupJSONObject(
								RandomTestUtil.randomString(),
								_userGroup.getName()))
					).put(
						NotificationRecipientSettingConstants.NAME_TO_TYPE,
						NotificationRecipientConstants.TYPE_USER_GROUP
					)),
				NotificationRecipientConstants.TYPE_EMAIL,
				NotificationConstants.TYPE_EMAIL));

		_testPostNotificationTemplate(
			JSONUtil.put(
				NotificationRecipientSettingConstants.NAME_TO,
				JSONUtil.putAll(_toUserGroupJSONObject(_userGroup))
			).put(
				NotificationRecipientSettingConstants.NAME_TO_TYPE,
				NotificationRecipientConstants.TYPE_USER_GROUP
			),
			JSONUtil.put(
				NotificationRecipientSettingConstants.NAME_TO,
				JSONUtil.putAll(
					_toUserGroupJSONObject(
						_userGroup.getExternalReferenceCode(),
						RandomTestUtil.randomString()))
			).put(
				NotificationRecipientSettingConstants.NAME_TO_TYPE,
				NotificationRecipientConstants.TYPE_USER_GROUP
			));
	}

	private void _testPostNotificationTemplateWithRecipientsAndFeatureFlagDisabled()
		throws Exception {

		try (PropsTemporarySwapper propsTemporarySwapper =
				new PropsTemporarySwapper(
					FeatureFlagConstants.getKey("LPD-49854"),
					Boolean.FALSE.toString())) {

			_testPostNotificationTemplateWithRecipient(
				JSONUtil.putAll(
					JSONUtil.put(
						NotificationRecipientSettingConstants.NAME_ROLE_NAME,
						_role.getName())),
				_toRoleJSONObject(
					RandomTestUtil.randomString(), _role.getName()),
				NotificationRecipientConstants.TYPE_ROLE);

			String screenName = RandomTestUtil.randomString();

			_testPostNotificationTemplateWithRecipient(
				JSONUtil.putAll(
					JSONUtil.put(
						NotificationRecipientSettingConstants.
							NAME_USER_SCREEN_NAME,
						screenName)),
				_toUserJSONObject(RandomTestUtil.randomString(), screenName),
				NotificationRecipientConstants.TYPE_USER);

			_testPostNotificationTemplateWithRecipient(
				JSONUtil.putAll(
					JSONUtil.put(
						NotificationRecipientSettingConstants.
							NAME_USER_GROUP_NAME,
						_userGroup.getName())),
				_toUserGroupJSONObject(
					RandomTestUtil.randomString(), _userGroup.getName()),
				NotificationRecipientConstants.TYPE_USER_GROUP);
		}
	}

	private void _testPostNotificationTemplateWithRecipientsAndUserNotificationType()
		throws Exception {

		// Notification template recipient type role

		_assertFailureNotificationTemplate(
			"The role recipient does not exist.",
			_postNotificationTemplateJSONObject(
				JSONUtil.putAll(
					_toRoleJSONObject(
						RandomTestUtil.randomString(), _role.getName())),
				NotificationRecipientConstants.TYPE_ROLE,
				NotificationConstants.TYPE_USER_NOTIFICATION));

		_testPostNotificationTemplateWithRecipient(
			JSONUtil.putAll(_toRoleJSONObject(_role.getName())),
			_toRoleJSONObject(
				_role.getExternalReferenceCode(),
				RandomTestUtil.randomString()),
			NotificationRecipientConstants.TYPE_ROLE);

		_testPostNotificationTemplateWithRecipient(
			JSONUtil.putAll(),
			JSONUtil.put(
				NotificationRecipientSettingConstants.NAME_ROLE_NAME,
				RandomTestUtil.randomString()),
			NotificationRecipientConstants.TYPE_ROLE);

		// Notification template recipient type term

		_testPostNotificationTemplateWithRecipient(
			JSONUtil.putAll(
				JSONUtil.put(
					NotificationRecipientSettingConstants.NAME_USER_SCREEN_NAME,
					"[%OBJECT_AUTHOR%]")),
			JSONUtil.put(
				NotificationRecipientSettingConstants.NAME_USER_SCREEN_NAME,
				"[%OBJECT_AUTHOR%]"),
			NotificationRecipientConstants.TYPE_TERM);

		// Notification template recipient type user

		JSONObject userJSONObject = _toUserJSONObject(_user);

		_testPostNotificationTemplateWithRecipient(
			JSONUtil.putAll(userJSONObject),
			_toUserJSONObject(
				_user.getExternalReferenceCode(),
				RandomTestUtil.randomString()),
			NotificationRecipientConstants.TYPE_USER);

		_testPostNotificationTemplateWithRecipient(
			JSONUtil.putAll(userJSONObject),
			_toUserJSONObject(
				RandomTestUtil.randomString(), _user.getScreenName()),
			NotificationRecipientConstants.TYPE_USER);

		_testPostNotificationTemplateWithRecipient(
			JSONUtil.putAll(),
			_toUserJSONObject(
				RandomTestUtil.randomString(), RandomTestUtil.randomString()),
			NotificationRecipientConstants.TYPE_USER);

		// Notification template recipient type user group

		_assertFailureNotificationTemplate(
			"The user group recipient does not exist.",
			_postNotificationTemplateJSONObject(
				JSONUtil.putAll(
					_toUserGroupJSONObject(
						RandomTestUtil.randomString(), _userGroup.getName())),
				NotificationRecipientConstants.TYPE_USER_GROUP,
				NotificationConstants.TYPE_USER_NOTIFICATION));

		_testPostNotificationTemplateWithRecipient(
			JSONUtil.putAll(_toUserGroupJSONObject(_userGroup)),
			_toUserGroupJSONObject(
				_userGroup.getExternalReferenceCode(),
				RandomTestUtil.randomString()),
			NotificationRecipientConstants.TYPE_USER_GROUP);

		_testPostNotificationTemplateWithRecipient(
			JSONUtil.putAll(),
			JSONUtil.put(
				NotificationRecipientSettingConstants.NAME_USER_GROUP_NAME,
				RandomTestUtil.randomString()),
			NotificationRecipientConstants.TYPE_USER_GROUP);
	}

	private void _testPutNotificationTemplateWithNameTranslations()
		throws Exception {

		// With a name internationalization map

		String defaultLanguageId = LocaleUtil.toLanguageId(
			LocaleUtil.getSiteDefault());
		String translatedLanguageId = LocaleUtil.toLanguageId(
			LocaleUtil.BRAZIL);

		NotificationTemplate notificationTemplate1 = _addNotificationTemplate(
			randomNotificationTemplate());

		String name1 = RandomTestUtil.randomString();
		String translatedName = RandomTestUtil.randomString();

		notificationTemplate1.setName(() -> name1);
		notificationTemplate1.setName_i18n(
			HashMapBuilder.put(
				defaultLanguageId, name1
			).put(
				translatedLanguageId, translatedName
			).build());

		notificationTemplate1 =
			notificationTemplateResource.putNotificationTemplate(
				notificationTemplate1.getId(), notificationTemplate1);

		Assert.assertEquals(name1, notificationTemplate1.getName());

		Map<String, String> nameI18nMap1 = notificationTemplate1.getName_i18n();

		Assert.assertEquals(name1, nameI18nMap1.get(defaultLanguageId));
		Assert.assertEquals(
			translatedName, nameI18nMap1.get(translatedLanguageId));

		// Without a name internationalization map

		NotificationTemplate notificationTemplate2 = _addNotificationTemplate(
			randomNotificationTemplate());

		String name2 = RandomTestUtil.randomString();

		notificationTemplate2.setName(() -> name2);

		notificationTemplate2.setName_i18n(() -> null);

		notificationTemplate2 =
			notificationTemplateResource.putNotificationTemplate(
				notificationTemplate2.getId(), notificationTemplate2);

		Assert.assertEquals(name2, notificationTemplate2.getName());

		Map<String, String> nameI18nMap2 = notificationTemplate2.getName_i18n();

		Assert.assertEquals(name2, nameI18nMap2.get(defaultLanguageId));
	}

	private void _testPutNotificationTemplateWithPermissions()
		throws Exception {

		JSONObject jsonObject = _postNotificationTemplateWithPermissions(
			RoleConstants.ADMINISTRATOR);

		_assertPermissions(
			_putNotificationTemplateWithPermissions(
				jsonObject.getLong("id"), RoleConstants.GUEST),
			RoleConstants.GUEST);
	}

	private JSONObject _toEmailRecipientJSONObject(
		String from, JSONObject fromNameJSONObject, Object to, String toType) {

		return JSONUtil.put(
			NotificationRecipientSettingConstants.NAME_FROM, from
		).put(
			NotificationRecipientSettingConstants.NAME_FROM_NAME,
			fromNameJSONObject
		).put(
			NotificationRecipientSettingConstants.NAME_TO, to
		).put(
			NotificationRecipientSettingConstants.NAME_TO_TYPE, toType
		);
	}

	private JSONObject _toNotificationTemplateJSONObject(
		JSONArray recipientsJSONArray, String recipientType, String type) {

		return JSONUtil.put(
			"editorType", NotificationTemplateConstants.EDITOR_TYPE_RICH_TEXT
		).put(
			"name", RandomTestUtil.randomString()
		).put(
			"recipients", recipientsJSONArray
		).put(
			"recipientType", recipientType
		).put(
			"subject",
			JSONUtil.put(
				LocaleUtil.toLanguageId(LocaleUtil.getDefault()),
				RandomTestUtil.randomString())
		).put(
			"type", type
		);
	}

	private JSONObject _toRoleJSONObject(String roleName) throws Exception {
		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), roleName);

		return _toRoleJSONObject(
			role.getExternalReferenceCode(), role.getName()
		).put(
			NotificationRecipientSettingConstants.NAME_ROLE_TYPE,
			RoleConstants.getTypeLabel(role.getType())
		);
	}

	private JSONObject _toRoleJSONObject(
		String externalReferenceCode, String roleName) {

		return JSONUtil.put(
			NotificationRecipientSettingConstants.
				NAME_ROLE_EXTERNAL_REFERENCE_CODE,
			externalReferenceCode
		).put(
			NotificationRecipientSettingConstants.NAME_ROLE_NAME, roleName
		);
	}

	private JSONObject _toUserGroupJSONObject(
		String externalReferenceCode, String userGroupName) {

		return JSONUtil.put(
			NotificationRecipientSettingConstants.
				NAME_USER_GROUP_EXTERNAL_REFERENCE_CODE,
			externalReferenceCode
		).put(
			NotificationRecipientSettingConstants.NAME_USER_GROUP_NAME,
			userGroupName
		);
	}

	private JSONObject _toUserGroupJSONObject(UserGroup userGroup) {
		return _toUserGroupJSONObject(
			userGroup.getExternalReferenceCode(), userGroup.getName());
	}

	private JSONObject _toUserJSONObject(
		String externalReferenceCode, String screenName) {

		return JSONUtil.put(
			NotificationRecipientSettingConstants.
				NAME_USER_EXTERNAL_REFERENCE_CODE,
			externalReferenceCode
		).put(
			NotificationRecipientSettingConstants.NAME_USER_SCREEN_NAME,
			screenName
		);
	}

	private JSONObject _toUserJSONObject(User user) {
		return _toUserJSONObject(
			user.getExternalReferenceCode(), user.getScreenName());
	}

	@Inject
	private NotificationTemplateLocalService _notificationTemplateLocalService;

	@Inject
	private NotificationTemplateResource.Factory
		_notificationTemplateResourceFactory;

	@DeleteAfterTestRun
	private List<com.liferay.notification.model.NotificationTemplate>
		_notificationTemplates = new ArrayList<>();

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@DeleteAfterTestRun
	private Role _role;

	@Inject
	private RoleLocalService _roleLocalService;

	@DeleteAfterTestRun
	private User _user;

	@DeleteAfterTestRun
	private UserGroup _userGroup;

}