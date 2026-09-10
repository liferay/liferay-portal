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
import com.liferay.notification.rest.client.permission.Permission;
import com.liferay.notification.rest.client.serdes.v1_0.NotificationTemplateSerDes;
import com.liferay.notification.rest.resource.v1_0.NotificationTemplateResource;
import com.liferay.notification.service.NotificationTemplateLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.constants.FeatureFlagConstants;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.props.test.util.PropsTemporarySwapper;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
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

	@Override
	@Test
	public void testDeleteNotificationTemplateByExternalReferenceCode()
		throws Exception {

		super.testDeleteNotificationTemplateByExternalReferenceCode();

		_testDeleteNotificationTemplateByExternalReferenceCodeNotFound();
	}

	@Override
	@Test
	public void testGetNotificationTemplatesPage() throws Exception {
		super.testGetNotificationTemplatesPage();

		_testGetNotificationTemplatesPageObjectDefinitionFilter();
		_testGetNotificationTemplatesPageSystemFilter();
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

		NotificationTemplate notificationTemplate =
			randomNotificationTemplate();

		notificationTemplate.setRecipientType(
			NotificationRecipientConstants.TYPE_EMAIL);
		notificationTemplate.setRecipients(
			new Object[] {
				HashMapBuilder.<String, Object>put(
					"from", RandomTestUtil.randomString()
				).put(
					"fromName",
					Collections.singletonMap(
						"en_US", RandomTestUtil.randomString())
				).put(
					"to",
					Collections.singletonMap(
						"en_US", RandomTestUtil.randomString())
				).put(
					"toType", NotificationRecipientConstants.TYPE_EMAIL
				).build()
			});
		notificationTemplate.setType(NotificationConstants.TYPE_EMAIL);

		notificationTemplate = _addNotificationTemplate(notificationTemplate);

		JSONObject recipientsJSONObject = JSONUtil.put(
			"from", RandomTestUtil.randomString()
		).put(
			"fromName", JSONUtil.put("en_US", RandomTestUtil.randomString())
		).put(
			"to", JSONUtil.put("en_US", RandomTestUtil.randomString())
		).put(
			"toType", NotificationRecipientConstants.TYPE_EMAIL
		);

		JSONAssert.assertEquals(
			recipientsJSONObject.toString(),
			JSONUtil.getValueAsString(
				HTTPTestUtil.invokeToJSONObject(
					JSONUtil.put(
						"recipients", JSONUtil.put(recipientsJSONObject)
					).toString(),
					"notification/v1.0/notification-templates/" +
						notificationTemplate.getId(),
					Http.Method.PATCH),
				"JSONArray/recipients", "JSONObject/0"),
			JSONCompareMode.LENIENT);

		_testPatchNotificationTemplateName();
	}

	@Override
	@Test
	public void testPostNotificationTemplate() throws Exception {
		super.testPostNotificationTemplate();

		// Notification template recipient type email

		_testPostNotificationTemplate(
			JSONUtil.put(
				"to", JSONUtil.put("en_US", RandomTestUtil.randomString())
			).put(
				"toType", NotificationRecipientConstants.TYPE_EMAIL
			));

		// Notification template recipient type role

		_testPostNotificationTemplate(
			JSONUtil.put(
				"to",
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
						RoleConstants.ORGANIZATION_OWNER))
			).put(
				"toType", NotificationRecipientConstants.TYPE_ROLE
			));

		// Notification template recipient type subscribers

		_testPostNotificationTemplate(
			JSONUtil.put(
				"toType", NotificationRecipientConstants.TYPE_SUBSCRIBERS));

		_testPostNotificationTemplateCreator();
		_testPostNotificationTemplateNameWithoutDefaultLanguage();
		_testPostNotificationTemplateWithFeatureFlagDisabled();
		_testPostNotificationTemplateWithPermissions();
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

		_testPutNotificationTemplateNameTranslations();
		_testPutNotificationTemplateNameTranslationsWithoutI18nMap();
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

	private void _assertRoleNames(
			NotificationTemplate notificationTemplate,
			String... expectedRoleNames)
		throws Exception {

		Assert.assertEquals(
			ListUtil.sort(Arrays.asList(expectedRoleNames)),
			ListUtil.sort(_getRoleNames(notificationTemplate.getId())));
	}

	private List<String> _getRoleNames(Long notificationTemplateId)
		throws Exception {

		return JSONUtil.toList(
			JSONUtil.getValueAsJSONArray(
				HTTPTestUtil.invokeToJSONObject(
					null,
					"notification/v1.0/notification-templates/" +
						notificationTemplateId + "?nestedFields=permissions",
					Http.Method.GET),
				"JSONArray/permissions"),
			permissionJSONObject -> permissionJSONObject.getString("roleName"));
	}

	private NotificationTemplate _randomNotificationTemplateWithPermissions(
			String roleName)
		throws Exception {

		NotificationTemplate notificationTemplate =
			randomNotificationTemplate();

		notificationTemplate.setSystem(false);

		Permission permission = new Permission();

		permission.setActionIds(new Object[] {ActionKeys.VIEW});
		permission.setRoleName(roleName);

		notificationTemplate.setPermissions(new Permission[] {permission});

		return notificationTemplate;
	}

	private void _testDeleteNotificationTemplateByExternalReferenceCodeNotFound()
		throws Exception {

		assertHttpResponseStatusCode(
			404,
			notificationTemplateResource.
				deleteNotificationTemplateByExternalReferenceCodeHttpResponse(
					RandomTestUtil.randomString()));
	}

	private void _testGetNotificationTemplatesPageObjectDefinitionFilter()
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

		List<Long> ids = TransformUtil.transform(
			page.getItems(), NotificationTemplate::getId);

		Assert.assertTrue(
			ids.toString(), ids.contains(notificationTemplate.getId()));
		Assert.assertFalse(
			ids.toString(), ids.contains(objectNotificationTemplate.getId()));
	}

	private void _testGetNotificationTemplatesPageSystemFilter()
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

		List<Long> ids = TransformUtil.transform(
			page.getItems(), NotificationTemplate::getId);

		Assert.assertTrue(
			ids.toString(), ids.contains(notificationTemplate.getId()));
		Assert.assertFalse(
			ids.toString(), ids.contains(systemNotificationTemplate.getId()));
	}

	private void _testPatchNotificationTemplateName() throws Exception {
		NotificationTemplate notificationTemplate =
			randomNotificationTemplate();

		String defaultLanguageId = LocaleUtil.toLanguageId(
			LocaleUtil.getSiteDefault());
		String translatedName = RandomTestUtil.randomString();

		notificationTemplate.setName_i18n(
			HashMapBuilder.put(
				defaultLanguageId, notificationTemplate.getName()
			).put(
				"pt_BR", translatedName
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

		Map<String, String> nameI18n = notificationTemplate.getName_i18n();

		Assert.assertEquals(name, nameI18n.get(defaultLanguageId));
		Assert.assertEquals(translatedName, nameI18n.get("pt_BR"));
	}

	private void _testPostNotificationTemplate(JSONObject recipientJSONObject)
		throws Exception {

		recipientJSONObject.put(
			"from", RandomTestUtil.randomString()
		).put(
			"fromName", JSONUtil.put("en_US", RandomTestUtil.randomString())
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
			recipientJSONObject.toString(),
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

	private void _testPostNotificationTemplateCreator() throws Exception {
		NotificationTemplate notificationTemplate = _addNotificationTemplate(
			randomNotificationTemplate());

		Creator creator = notificationTemplate.getCreator();

		User user = TestPropsValues.getUser();

		Assert.assertEquals(
			user.getExternalReferenceCode(),
			creator.getExternalReferenceCode());
	}

	private void _testPostNotificationTemplateNameWithoutDefaultLanguage()
		throws Exception {

		NotificationTemplate notificationTemplate =
			randomNotificationTemplate();

		notificationTemplate.setName(() -> null);
		notificationTemplate.setName_i18n(
			Collections.singletonMap("pt_BR", RandomTestUtil.randomString()));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.ERROR)) {

			Assert.assertEquals(
				400,
				HTTPTestUtil.invokeToHttpCode(
					NotificationTemplateSerDes.toJSON(notificationTemplate),
					"notification/v1.0/notification-templates",
					Http.Method.POST));
		}
	}

	private void _testPostNotificationTemplateWithFeatureFlagDisabled()
		throws Exception {

		try (PropsTemporarySwapper propsTemporarySwapper =
				new PropsTemporarySwapper(
					FeatureFlagConstants.getKey("LPD-49854"),
					Boolean.FALSE.toString())) {

			Assert.assertEquals(
				400,
				HTTPTestUtil.invokeToHttpCode(
					NotificationTemplateSerDes.toJSON(
						_randomNotificationTemplateWithPermissions(
							RoleConstants.ADMINISTRATOR)),
					"notification/v1.0/notification-templates",
					Http.Method.POST));
		}
	}

	private void _testPostNotificationTemplateWithPermissions()
		throws Exception {

		NotificationTemplate notificationTemplate = _addNotificationTemplate(
			_randomNotificationTemplateWithPermissions(
				RoleConstants.ADMINISTRATOR));

		_assertRoleNames(notificationTemplate, RoleConstants.ADMINISTRATOR);
	}

	private void _testPutNotificationTemplateNameTranslations()
		throws Exception {

		NotificationTemplate notificationTemplate = _addNotificationTemplate(
			randomNotificationTemplate());

		String defaultLanguageId = LocaleUtil.toLanguageId(
			LocaleUtil.getSiteDefault());
		String name = RandomTestUtil.randomString();
		String translatedName = RandomTestUtil.randomString();

		notificationTemplate.setName(() -> name);
		notificationTemplate.setName_i18n(
			HashMapBuilder.put(
				defaultLanguageId, name
			).put(
				"pt_BR", translatedName
			).build());

		notificationTemplate =
			notificationTemplateResource.putNotificationTemplate(
				notificationTemplate.getId(), notificationTemplate);

		Assert.assertEquals(name, notificationTemplate.getName());

		Map<String, String> nameI18n = notificationTemplate.getName_i18n();

		Assert.assertEquals(name, nameI18n.get(defaultLanguageId));
		Assert.assertEquals(translatedName, nameI18n.get("pt_BR"));
	}

	private void _testPutNotificationTemplateNameTranslationsWithoutI18nMap()
		throws Exception {

		NotificationTemplate notificationTemplate = _addNotificationTemplate(
			randomNotificationTemplate());

		String defaultLanguageId = LocaleUtil.toLanguageId(
			LocaleUtil.getSiteDefault());

		String name = RandomTestUtil.randomString();

		notificationTemplate.setName(() -> name);

		notificationTemplate.setName_i18n(() -> null);

		notificationTemplate =
			notificationTemplateResource.putNotificationTemplate(
				notificationTemplate.getId(), notificationTemplate);

		Assert.assertEquals(name, notificationTemplate.getName());

		Map<String, String> nameI18n = notificationTemplate.getName_i18n();

		Assert.assertEquals(name, nameI18n.get(defaultLanguageId));
	}

	private void _testPutNotificationTemplateWithPermissions()
		throws Exception {

		NotificationTemplate notificationTemplate = _addNotificationTemplate(
			_randomNotificationTemplateWithPermissions(
				RoleConstants.ADMINISTRATOR));

		notificationTemplateResource.putNotificationTemplate(
			notificationTemplate.getId(),
			_randomNotificationTemplateWithPermissions(RoleConstants.GUEST));

		_assertRoleNames(notificationTemplate, RoleConstants.GUEST);
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

}