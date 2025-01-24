/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.rest.resource.v1_0.test;

import com.liferay.account.constants.AccountRoleConstants;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.notification.constants.NotificationConstants;
import com.liferay.notification.constants.NotificationRecipientConstants;
import com.liferay.notification.constants.NotificationTemplateConstants;
import com.liferay.notification.rest.client.dto.v1_0.NotificationTemplate;
import com.liferay.notification.rest.resource.v1_0.NotificationTemplateResource;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Gabriel Albuquerque
 */
@RunWith(Arquillian.class)
public class NotificationTemplateResourceTest
	extends BaseNotificationTemplateResourceTestCase {

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
						"roleName", RoleConstants.ORGANIZATION_ADMINISTRATOR),
					JSONUtil.put("roleName", RoleConstants.ORGANIZATION_OWNER),
					JSONUtil.put(
						"roleName",
						AccountRoleConstants.
							REQUIRED_ROLE_NAME_ACCOUNT_ADMINISTRATOR),
					JSONUtil.put(
						"roleName",
						AccountRoleConstants.REQUIRED_ROLE_NAME_ACCOUNT_MEMBER))
			).put(
				"toType", NotificationRecipientConstants.TYPE_ROLE
			));
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

		return notificationTemplateResource.postNotificationTemplate(
			notificationTemplate);
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

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private NotificationTemplateResource.Factory
		_notificationTemplateResourceFactory;

}