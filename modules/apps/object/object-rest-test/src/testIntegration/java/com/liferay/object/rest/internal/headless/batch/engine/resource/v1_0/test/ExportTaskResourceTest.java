/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.headless.batch.engine.resource.v1_0.test;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.test.util.ObjectEntryTestUtil;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalServiceUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.vulcan.jackson.databind.ObjectMapperProviderUtil;
import com.liferay.portal.vulcan.permission.PermissionUtil;

import java.util.Collections;
import java.util.zip.ZipInputStream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
public class ExportTaskResourceTest extends BaseTaskResourceTestCase {

	@Test
	public void testPostExportTask() throws Exception {
		long companyId = 0;

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.batch.engine.internal." +
					"BatchEngineExportTaskExecutorImpl",
				LoggerTestUtil.ERROR)) {

			_testPostExportTask("COMPLETED", null, objectDefinition);

			JSONObject companyJSONObject = HTTPTestUtil.invokeToJSONObject(
				JSONUtil.put(
					"domain", "able.com"
				).put(
					"portalInstanceId", "able.com"
				).put(
					"virtualHost", "www.able.com"
				).toString(),
				"headless-portal-instances/v1.0/portal-instances",
				Http.Method.POST);

			companyId = companyJSONObject.getLong("companyId");

			User user = UserTestUtil.getAdminUser(companyId);

			ObjectDefinition objectDefinition2 =
				ObjectDefinitionTestUtil.publishObjectDefinition(
					Collections.singletonList(
						ObjectFieldUtil.createObjectField(
							ObjectFieldConstants.BUSINESS_TYPE_TEXT,
							ObjectFieldConstants.DB_TYPE_STRING,
							OBJECT_FIELD_NAME_TEXT_1)),
					ObjectDefinitionConstants.SCOPE_COMPANY, user.getUserId());

			_testPostExportTask("FAILED", null, objectDefinition2);

			HTTPTestUtil.customize(
			).withBaseURL(
				"http://www.able.com:8080"
			).withCredentials(
				"test@able.com", PropsValues.DEFAULT_ADMIN_PASSWORD
			).apply(
				() -> {
					_testPostExportTask("COMPLETED", null, objectDefinition2);
					_testPostExportTask("FAILED", null, objectDefinition);
				}
			);
		}
		finally {
			if (companyId != 0) {
				_companyLocalService.deleteCompany(companyId);
			}
		}
	}

	@Test
	public void testPostExportTaskWithBatchNestedFields() throws Exception {

		// With "batchNestedFields" query parameter

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			objectDefinition, OBJECT_FIELD_NAME_TEXT_1, "TestObject");

		JSONObject jsonObject1 = _testPostExportTask(
			"COMPLETED", "batchNestedFields=permissions", objectDefinition);

		Assert.assertEquals(1, jsonObject1.getInt("processedItemsCount"));

		JSONArray contentJSONArray1 = _getExportTaskContentJSONArray(
			jsonObject1.getString("externalReferenceCode"));

		JSONArray permissionsJSONArray = JSONUtil.getValueAsJSONArray(
			contentJSONArray1, "JSONObject/0", "JSONArray/permissions");

		ObjectMapper objectMapper = ObjectMapperProviderUtil.getObjectMapper();

		JSONAssert.assertEquals(
			objectMapper.writeValueAsString(
				PermissionUtil.getPermissions(
					objectDefinition.getCompanyId(),
					ResourceActionLocalServiceUtil.getResourceActions(
						objectDefinition.getClassName()),
					objectEntry.getObjectEntryId(),
					objectDefinition.getClassName(), null)),
			permissionsJSONArray.toString(), JSONCompareMode.LENIENT);

		// Without "batchNestedFields" query parameter

		JSONObject jsonObject2 = _testPostExportTask(
			"COMPLETED", null, objectDefinition);

		Assert.assertEquals(1, jsonObject2.getInt("processedItemsCount"));

		JSONArray contentJSONArray2 = _getExportTaskContentJSONArray(
			jsonObject2.getString("externalReferenceCode"));

		Assert.assertNull(
			JSONUtil.getValueAsJSONArray(
				contentJSONArray2, "JSONObject/0", "JSONArray/permissions"));
	}

	@Test
	@TestInfo("LPD-60157")
	public void testPostExportTaskWithFieldNames() throws Exception {
		ObjectEntryTestUtil.addObjectEntry(
			objectDefinition, OBJECT_FIELD_NAME_TEXT_1, "TestObject");

		JSONObject jsonObject = _testPostExportTask(
			"COMPLETED", "fieldNames=status.code," + OBJECT_FIELD_NAME_TEXT_1,
			objectDefinition);

		Assert.assertEquals(1, jsonObject.getInt("processedItemsCount"));

		JSONAssert.assertEquals(
			JSONUtil.putAll(
				JSONUtil.put(
					OBJECT_FIELD_NAME_TEXT_1, "TestObject"
				).put(
					"status", JSONUtil.put("code", 0)
				)
			).toString(),
			_getExportTaskContentJSONArray(
				jsonObject.getString("externalReferenceCode")
			).toString(),
			JSONCompareMode.STRICT);
	}

	@Test
	public void testPostExportTaskWithFilter() throws Exception {
		ObjectEntryTestUtil.addObjectEntry(
			objectDefinition, OBJECT_FIELD_NAME_TEXT_1, "Object3");

		ObjectEntry objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			objectDefinition, OBJECT_FIELD_NAME_TEXT_1, "TestObject1");
		ObjectEntry objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			objectDefinition, OBJECT_FIELD_NAME_TEXT_1, "TestObject2");

		String filterString =
			"contains(" + OBJECT_FIELD_NAME_TEXT_1 + ", 'Test')";

		JSONObject jsonObject = _testPostExportTask(
			"COMPLETED", "filter=" + URLCodec.encodeURL(filterString),
			objectDefinition);

		Assert.assertEquals(2, jsonObject.getInt("processedItemsCount"));

		JSONAssert.assertEquals(
			JSONUtil.putAll(
				JSONUtil.put(
					"externalReferenceCode",
					objectEntry1.getExternalReferenceCode()),
				JSONUtil.put(
					"externalReferenceCode",
					objectEntry2.getExternalReferenceCode())
			).toString(),
			_getExportTaskContentJSONArray(
				jsonObject.getString("externalReferenceCode")
			).toString(),
			JSONCompareMode.LENIENT);
	}

	private JSONArray _getExportTaskContentJSONArray(
			String externalReferenceCode)
		throws Exception {

		try (ZipInputStream zipInputStream = new ZipInputStream(
				HTTPTestUtil.invokeToInputStream(
					null,
					StringBundler.concat(
						"headless-batch-engine/v1.0/export-task",
						"/by-external-reference-code/", externalReferenceCode,
						"/content"),
					Http.Method.GET))) {

			zipInputStream.getNextEntry();

			return JSONFactoryUtil.createJSONArray(
				StringUtil.read(zipInputStream));
		}
	}

	private JSONObject _testPostExportTask(
			String expectedExecuteStatus, String queryParameters,
			ObjectDefinition objectDefinition)
		throws Exception {

		String endpoint = StringBundler.concat(
			"headless-batch-engine/v1.0/export-task",
			"/com.liferay.object.rest.dto.v1_0.ObjectEntry/json?",
			"taskItemDelegateName=", objectDefinition.getName());

		if (queryParameters != null) {
			endpoint = endpoint + "&" + queryParameters;
		}

		JSONObject jsonObject = waitForFinish(
			expectedExecuteStatus, false,
			HTTPTestUtil.invokeToJSONObject(null, endpoint, Http.Method.POST));

		return HTTPTestUtil.invokeToJSONObject(
			null,
			ENDPOINT_EXPORT_TASK_BY_ERC +
				jsonObject.getString("externalReferenceCode"),
			Http.Method.GET);
	}

	@Inject
	private CompanyLocalService _companyLocalService;

}