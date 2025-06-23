/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.headless.batch.engine.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.batch.engine.client.http.HttpInvoker;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.test.util.ObjectEntryTestUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.util.PropsValues;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Mauricio Valdivia
 */
@RunWith(Arquillian.class)
public class ImportTaskResourceTest extends BaseTaskResourceTestCase {

	@FeatureFlag("LPD-45945")
	@Test
	@TestInfo("LPD-46121")
	public void testDeleteImportTask() throws Exception {
		_testDeleteImportTask(objectDefinition);
		_testDeleteImportTask(siteObjectDefinition);
	}

	@Test
	public void testPostImportTask() throws Exception {

		// With "batchRestrictFields" query parameter

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			objectDefinition, OBJECT_FIELD_NAME_TEXT, "TestObject");

		JSONObject beforeImportJSONObject = _getJSONObject(
			objectEntry.getExternalReferenceCode());

		waitForFinish(
			"COMPLETED", true,
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.putAll(
					beforeImportJSONObject.put(
						"permissions",
						beforeImportJSONObject.getJSONArray(
							"permissions"
						).put(
							JSONUtil.put(
								"actionIds", JSONUtil.putAll("VIEW")
							).put(
								"roleName", role.getName()
							)
						))
				).toString(),
				StringBundler.concat(
					"headless-batch-engine/v1.0/import-task",
					"/com.liferay.object.rest.dto.v1_0.ObjectEntry",
					"?batchRestrictFields=permissions,", OBJECT_FIELD_NAME_TEXT,
					"&createStrategy=UPSERT&taskItemDelegateName=",
					objectDefinition.getName()),
				Http.Method.POST));

		JSONAssert.assertEquals(
			JSONUtil.put(
				"permissions",
				JSONUtil.putAll(
					JSONUtil.put(
						"actionIds",
						JSONUtil.putAll(
							"DELETE", "PERMISSIONS", "UPDATE", "VIEW")
					).put(
						"roleName", "Owner"
					))
			).toString(),
			_getJSONObject(
				objectEntry.getExternalReferenceCode()
			).toString(),
			JSONCompareMode.LENIENT);

		// With "permissions" and "createStrategy" INSERT

		beforeImportJSONObject = JSONUtil.put(
			OBJECT_FIELD_NAME_TEXT, RandomTestUtil.randomString()
		).put(
			"externalReferenceCode", RandomTestUtil.randomString()
		).put(
			"permissions",
			JSONUtil.putAll(
				JSONUtil.put(
					"actionIds", JSONUtil.putAll("VIEW")
				).put(
					"roleName", role.getName()
				))
		);

		waitForFinish(
			"COMPLETED", true,
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.putAll(
					beforeImportJSONObject
				).toString(),
				StringBundler.concat(
					"headless-batch-engine/v1.0/import-task",
					"/com.liferay.object.rest.dto.v1_0.ObjectEntry",
					"?createStrategy=INSERT&taskItemDelegateName=",
					objectDefinition.getName()),
				Http.Method.POST));

		JSONAssert.assertEquals(
			JSONUtil.merge(
				beforeImportJSONObject,
				JSONUtil.put(
					"permissions",
					JSONUtil.putAll(
						JSONUtil.put(
							"actionIds", JSONUtil.putAll("VIEW")
						).put(
							"roleName", role.getName()
						)))
			).toString(),
			_getJSONObject(
				beforeImportJSONObject.getString("externalReferenceCode")
			).toString(),
			JSONCompareMode.LENIENT);

		// With "permissions" and "createStrategy" UPSERT

		beforeImportJSONObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_TEXT, RandomTestUtil.randomString()
			).put(
				"externalReferenceCode", RandomTestUtil.randomString()
			).toString(),
			StringBundler.concat(
				objectDefinition.getRESTContextPath(),
				"?nestedFields=permissions",
				"&restrictFields=dateCreated,dateModified"),
			Http.Method.POST);

		beforeImportJSONObject = beforeImportJSONObject.put(
			"permissions",
			beforeImportJSONObject.getJSONArray(
				"permissions"
			).put(
				JSONUtil.put(
					"actionIds", JSONUtil.putAll("VIEW")
				).put(
					"roleName", role.getName()
				)
			));

		waitForFinish(
			"COMPLETED", true,
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.putAll(
					beforeImportJSONObject
				).toString(),
				StringBundler.concat(
					"headless-batch-engine/v1.0/import-task",
					"/com.liferay.object.rest.dto.v1_0.ObjectEntry",
					"?createStrategy=UPSERT&taskItemDelegateName=",
					objectDefinition.getName()),
				Http.Method.POST));

		JSONAssert.assertEquals(
			JSONUtil.merge(
				beforeImportJSONObject,
				JSONUtil.put(
					"permissions",
					JSONUtil.putAll(
						JSONUtil.put(
							"actionIds",
							JSONUtil.putAll(
								"DELETE", "PERMISSIONS", "UPDATE", "VIEW")
						).put(
							"roleName", "Owner"
						),
						JSONUtil.put(
							"actionIds", JSONUtil.putAll("VIEW")
						).put(
							"roleName", role.getName()
						)))
			).toString(),
			_getJSONObject(
				beforeImportJSONObject.getString("externalReferenceCode")
			).toString(),
			JSONCompareMode.LENIENT);

		// With empty "permissions" and "createStrategy" INSERT

		beforeImportJSONObject = JSONUtil.put(
			OBJECT_FIELD_NAME_TEXT, RandomTestUtil.randomString()
		).put(
			"externalReferenceCode", RandomTestUtil.randomString()
		).put(
			"permissions", JSONFactoryUtil.createJSONArray()
		);

		waitForFinish(
			"COMPLETED", true,
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.putAll(
					beforeImportJSONObject
				).toString(),
				StringBundler.concat(
					"headless-batch-engine/v1.0/import-task",
					"/com.liferay.object.rest.dto.v1_0.ObjectEntry",
					"?createStrategy=INSERT&taskItemDelegateName=",
					objectDefinition.getName()),
				Http.Method.POST));

		JSONAssert.assertEquals(
			JSONUtil.merge(
				beforeImportJSONObject,
				JSONUtil.put("permissions", JSONFactoryUtil.createJSONArray())
			).toString(),
			_getJSONObject(
				beforeImportJSONObject.getString("externalReferenceCode")
			).toString(),
			JSONCompareMode.LENIENT);

		// With empty "permissions" and "createStrategy" UPSERT

		beforeImportJSONObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_TEXT, RandomTestUtil.randomString()
			).put(
				"externalReferenceCode", RandomTestUtil.randomString()
			).toString(),
			StringBundler.concat(
				objectDefinition.getRESTContextPath(),
				"?nestedFields=permissions",
				"&restrictFields=dateCreated,dateModified"),
			Http.Method.POST
		).put(
			"permissions", JSONFactoryUtil.createJSONArray()
		);

		waitForFinish(
			"COMPLETED", true,
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.putAll(
					beforeImportJSONObject
				).toString(),
				StringBundler.concat(
					"headless-batch-engine/v1.0/import-task",
					"/com.liferay.object.rest.dto.v1_0.ObjectEntry",
					"?createStrategy=UPSERT&taskItemDelegateName=",
					objectDefinition.getName()),
				Http.Method.POST));

		JSONAssert.assertEquals(
			JSONUtil.merge(
				beforeImportJSONObject,
				JSONUtil.put("permissions", JSONFactoryUtil.createJSONArray())
			).toString(),
			_getJSONObject(
				beforeImportJSONObject.getString("externalReferenceCode")
			).toString(),
			JSONCompareMode.LENIENT);

		// With no "permissions" and "createStrategy" INSERT

		beforeImportJSONObject = JSONUtil.put(
			OBJECT_FIELD_NAME_TEXT, RandomTestUtil.randomString()
		).put(
			"externalReferenceCode", RandomTestUtil.randomString()
		);

		waitForFinish(
			"COMPLETED", true,
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.putAll(
					beforeImportJSONObject
				).toString(),
				StringBundler.concat(
					"headless-batch-engine/v1.0/import-task",
					"/com.liferay.object.rest.dto.v1_0.ObjectEntry",
					"?createStrategy=INSERT&taskItemDelegateName=",
					objectDefinition.getName()),
				Http.Method.POST));

		JSONAssert.assertEquals(
			JSONUtil.merge(
				beforeImportJSONObject,
				JSONUtil.put(
					"permissions",
					JSONUtil.putAll(
						JSONUtil.put(
							"actionIds",
							JSONUtil.putAll(
								"DELETE", "PERMISSIONS", "UPDATE", "VIEW")
						).put(
							"roleName", "Owner"
						)))
			).toString(),
			_getJSONObject(
				beforeImportJSONObject.getString("externalReferenceCode")
			).toString(),
			JSONCompareMode.LENIENT);

		// With no "permissions" and "createStrategy" UPSERT

		beforeImportJSONObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				OBJECT_FIELD_NAME_TEXT, RandomTestUtil.randomString()
			).put(
				"externalReferenceCode", RandomTestUtil.randomString()
			).toString(),
			StringBundler.concat(
				objectDefinition.getRESTContextPath(),
				"?nestedFields=permissions",
				"&restrictFields=dateCreated,dateModified"),
			Http.Method.POST
		).put(
			"permissions", (JSONObject)null
		);

		waitForFinish(
			"COMPLETED", true,
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.putAll(
					beforeImportJSONObject
				).toString(),
				StringBundler.concat(
					"headless-batch-engine/v1.0/import-task",
					"/com.liferay.object.rest.dto.v1_0.ObjectEntry",
					"?createStrategy=UPSERT&taskItemDelegateName=",
					objectDefinition.getName()),
				Http.Method.POST));

		JSONAssert.assertEquals(
			JSONUtil.merge(
				beforeImportJSONObject,
				JSONUtil.put(
					"permissions",
					JSONUtil.putAll(
						JSONUtil.put(
							"actionIds",
							JSONUtil.putAll(
								"DELETE", "PERMISSIONS", "UPDATE", "VIEW")
						).put(
							"roleName", "Owner"
						)))
			).toString(),
			_getJSONObject(
				beforeImportJSONObject.getString("externalReferenceCode")
			).toString(),
			JSONCompareMode.LENIENT);

		// Without "batchRestrictFields" query parameter

		objectEntry = ObjectEntryTestUtil.addObjectEntry(
			objectDefinition, OBJECT_FIELD_NAME_TEXT, "TestObject");

		beforeImportJSONObject = _getJSONObject(
			objectEntry.getExternalReferenceCode());

		waitForFinish(
			"COMPLETED", true,
			HTTPTestUtil.invokeToJSONObject(
				JSONUtil.putAll(
					beforeImportJSONObject.put(
						"permissions",
						beforeImportJSONObject.getJSONArray(
							"permissions"
						).put(
							JSONUtil.put(
								"actionIds", JSONUtil.putAll("VIEW")
							).put(
								"roleName", role.getName()
							)
						))
				).toString(),
				StringBundler.concat(
					"headless-batch-engine/v1.0/import-task",
					"/com.liferay.object.rest.dto.v1_0.ObjectEntry",
					"?createStrategy=UPSERT&taskItemDelegateName=",
					objectDefinition.getName()),
				Http.Method.POST));

		JSONAssert.assertEquals(
			JSONUtil.put(
				"permissions",
				JSONUtil.putAll(
					JSONUtil.put(
						"actionIds",
						JSONUtil.putAll(
							"DELETE", "PERMISSIONS", "UPDATE", "VIEW")
					).put(
						"roleName", "Owner"
					),
					JSONUtil.put(
						"actionIds", JSONUtil.putAll("VIEW")
					).put(
						"roleName", role.getName()
					))
			).toString(),
			_getJSONObject(
				objectEntry.getExternalReferenceCode()
			).toString(),
			JSONCompareMode.LENIENT);
	}

	private int _getHttpCode(ObjectEntry objectEntry) throws Exception {
		String endpoint = StringBundler.concat(
			objectDefinition.getRESTContextPath(),
			"/by-external-reference-code/",
			objectEntry.getExternalReferenceCode());

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectEntry.getObjectDefinitionId());

		if (StringUtil.equals(
				objectDefinition.getScope(),
				ObjectDefinitionConstants.SCOPE_SITE)) {

			endpoint = StringBundler.concat(
				objectDefinition.getRESTContextPath(), "/scopes/",
				testGroup.getExternalReferenceCode(),
				"/by-external-reference-code/",
				objectEntry.getExternalReferenceCode());
		}

		return HTTPTestUtil.invokeToHttpCode(null, endpoint, Http.Method.GET);
	}

	private JSONObject _getJSONObject(String externalReferenceCode)
		throws Exception {

		return HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				objectDefinition.getRESTContextPath(),
				"/by-external-reference-code/", externalReferenceCode,
				"?nestedFields=permissions"),
			Http.Method.GET);
	}

	private void _testDeleteImportTask(ObjectDefinition objectDefinition)
		throws Exception {

		long groupId = 0;

		if (StringUtil.equals(
				objectDefinition.getScope(),
				ObjectDefinitionConstants.SCOPE_SITE)) {

			groupId = testGroup.getGroupId();

			importTaskResource = ImportTaskResource.builder(
			).authentication(
				testCompanyAdminUser.getEmailAddress(),
				PropsValues.DEFAULT_ADMIN_PASSWORD
			).endpoint(
				testCompany.getVirtualHostname(), 8080, "http"
			).locale(
				LocaleUtil.getDefault()
			).parameters(
				"siteExternalReferenceCode",
				testGroup.getExternalReferenceCode()
			).build();
		}

		ObjectEntry objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			groupId, objectDefinition,
			Collections.singletonMap(
				OBJECT_FIELD_NAME_TEXT, RandomTestUtil.randomString()));
		ObjectEntry objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			groupId, objectDefinition,
			Collections.singletonMap(
				OBJECT_FIELD_NAME_TEXT, RandomTestUtil.randomString()));

		HttpInvoker.HttpResponse httpResponse =
			importTaskResource.deleteImportTaskHttpResponse(
				"com.liferay.object.rest.dto.v1_0.ObjectEntry", null, null,
				null, objectDefinition.getName(),
				JSONUtil.putAll(
					JSONUtil.put("id", objectEntry1.getObjectEntryId())
				).toString());

		waitForFinish(
			"COMPLETED", true,
			JSONFactoryUtil.createJSONObject(httpResponse.getContent()));

		Assert.assertEquals(404, _getHttpCode(objectEntry1));

		httpResponse = importTaskResource.deleteImportTaskHttpResponse(
			"com.liferay.object.rest.dto.v1_0.ObjectEntry", null, null, null,
			objectDefinition.getName(),
			JSONUtil.putAll(
				JSONUtil.put(
					"externalReferenceCode",
					objectEntry2.getExternalReferenceCode())
			).toString());

		waitForFinish(
			"COMPLETED", true,
			JSONFactoryUtil.createJSONObject(httpResponse.getContent()));

		Assert.assertEquals(404, _getHttpCode(objectEntry2));

		objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			groupId, objectDefinition,
			Collections.singletonMap(
				OBJECT_FIELD_NAME_TEXT, RandomTestUtil.randomString()));

		objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			groupId, objectDefinition,
			Collections.singletonMap(
				OBJECT_FIELD_NAME_TEXT, RandomTestUtil.randomString()));

		httpResponse = importTaskResource.deleteImportTaskHttpResponse(
			"com.liferay.object.rest.dto.v1_0.ObjectEntry", null, null, null,
			objectDefinition.getName(),
			JSONUtil.putAll(
				JSONUtil.put(
					"externalReferenceCode",
					objectEntry2.getExternalReferenceCode()
				).put(
					"id", objectEntry1.getObjectEntryId()
				)
			).toString());

		waitForFinish(
			"COMPLETED", true,
			JSONFactoryUtil.createJSONObject(httpResponse.getContent()));

		Assert.assertEquals(404, _getHttpCode(objectEntry1));

		Assert.assertEquals(200, _getHttpCode(objectEntry2));
	}

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}