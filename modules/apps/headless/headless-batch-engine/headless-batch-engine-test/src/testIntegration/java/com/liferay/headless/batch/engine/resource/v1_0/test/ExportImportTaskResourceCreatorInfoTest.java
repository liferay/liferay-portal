/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.batch.engine.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.batch.engine.client.dto.v1_0.ExportTask;
import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.http.HttpInvoker;
import com.liferay.headless.batch.engine.client.serdes.v1_0.ExportTaskSerDes;
import com.liferay.headless.batch.engine.client.serdes.v1_0.ImportTaskSerDes;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.InputStream;
import java.io.Serializable;

import java.util.Arrays;
import java.util.Objects;
import java.util.zip.ZipInputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Vendel Toreki
 */
@RunWith(Arquillian.class)
public class ExportImportTaskResourceCreatorInfoTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_objectDefinition1 = ObjectDefinitionTestUtil.publishObjectDefinition(
			ObjectDefinitionTestUtil.getRandomName(),
			Arrays.asList(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
					RandomTestUtil.randomString(), _OBJECT_FIELD_NAME_TEXT,
					false)),
			ObjectDefinitionConstants.SCOPE_COMPANY);

		_objectEntry1 = _addObjectEntry(
			_objectDefinition1, _OBJECT_FIELD_NAME_TEXT,
			RandomTestUtil.randomString(), TestPropsValues.getUser());

		_user = UserTestUtil.addUser();

		_objectEntry2 = _addObjectEntry(
			_objectDefinition1, _OBJECT_FIELD_NAME_TEXT,
			RandomTestUtil.randomString(), _user);

		_executeExportTask();

		_objectEntryLocalService.deleteObjectEntry(_objectEntry1);
		_objectEntryLocalService.deleteObjectEntry(_objectEntry2);
	}

	@Test
	public void testImportWithInsertAndKeepCreator() throws Exception {
		_executeImportTask("INSERT", "KEEP_CREATOR");

		_objectEntry1 = _objectEntryLocalService.getObjectEntry(
			_objectEntry1.getExternalReferenceCode(),
			_objectEntry1.getGroupId(),
			_objectDefinition1.getObjectDefinitionId());
		_objectEntry2 = _objectEntryLocalService.getObjectEntry(
			_objectEntry2.getExternalReferenceCode(),
			_objectEntry2.getGroupId(),
			_objectDefinition1.getObjectDefinitionId());

		Assert.assertEquals(
			TestPropsValues.getUserId(), _objectEntry1.getUserId());
		Assert.assertEquals(_user.getUserId(), _objectEntry2.getUserId());
	}

	@Test
	public void testImportWithInsertAndKeepCreatorUserDoesNotExist()
		throws Exception {

		_userLocalService.deleteUser(_user);

		_executeImportTask("INSERT", "KEEP_CREATOR");

		_objectEntry1 = _objectEntryLocalService.getObjectEntry(
			_objectEntry1.getExternalReferenceCode(),
			_objectEntry1.getGroupId(),
			_objectDefinition1.getObjectDefinitionId());
		_objectEntry2 = _objectEntryLocalService.getObjectEntry(
			_objectEntry2.getExternalReferenceCode(),
			_objectEntry2.getGroupId(),
			_objectDefinition1.getObjectDefinitionId());

		Assert.assertEquals(
			TestPropsValues.getUserId(), _objectEntry1.getUserId());
		Assert.assertEquals(
			TestPropsValues.getUserId(), _objectEntry2.getUserId());
	}

	@Test
	public void testImportWithInsertAndKeepCreatorUserExistByExternalReferenceCode()
		throws Exception {

		String externalReferenceCode = _user.getExternalReferenceCode();

		_userLocalService.deleteUser(_user);

		_user = UserTestUtil.addUser();

		_user.setExternalReferenceCode(externalReferenceCode);

		_user = _userLocalService.updateUser(_user);

		_executeImportTask("INSERT", "KEEP_CREATOR");

		_objectEntry1 = _objectEntryLocalService.getObjectEntry(
			_objectEntry1.getExternalReferenceCode(),
			_objectEntry1.getGroupId(),
			_objectDefinition1.getObjectDefinitionId());
		_objectEntry2 = _objectEntryLocalService.getObjectEntry(
			_objectEntry2.getExternalReferenceCode(),
			_objectEntry2.getGroupId(),
			_objectDefinition1.getObjectDefinitionId());

		Assert.assertEquals(
			TestPropsValues.getUserId(), _objectEntry1.getUserId());
		Assert.assertEquals(_user.getUserId(), _objectEntry2.getUserId());
	}

	@Test
	public void testImportWithInsertAndOverwriteCreator() throws Exception {
		_executeImportTask("INSERT", "OVERWRITE_CREATOR");

		_objectEntry1 = _objectEntryLocalService.getObjectEntry(
			_objectEntry1.getExternalReferenceCode(),
			_objectEntry1.getGroupId(),
			_objectDefinition1.getObjectDefinitionId());
		_objectEntry2 = _objectEntryLocalService.getObjectEntry(
			_objectEntry2.getExternalReferenceCode(),
			_objectEntry2.getGroupId(),
			_objectDefinition1.getObjectDefinitionId());

		Assert.assertEquals(
			TestPropsValues.getUserId(), _objectEntry1.getUserId());
		Assert.assertEquals(
			TestPropsValues.getUserId(), _objectEntry2.getUserId());
	}

	@Test
	public void testImportWithUpsertAndKeepCreator() throws Exception {
		_objectEntry1 = _addObjectEntry(
			_objectEntry1.getExternalReferenceCode(), _objectDefinition1,
			_OBJECT_FIELD_NAME_TEXT, RandomTestUtil.randomString(),
			TestPropsValues.getUser());

		_objectEntry2 = _addObjectEntry(
			_objectEntry2.getExternalReferenceCode(), _objectDefinition1,
			_OBJECT_FIELD_NAME_TEXT, RandomTestUtil.randomString(),
			TestPropsValues.getUser());

		_executeImportTask("UPSERT", "KEEP_CREATOR");

		_objectEntry1 = _objectEntryLocalService.getObjectEntry(
			_objectEntry1.getExternalReferenceCode(),
			_objectEntry1.getGroupId(),
			_objectDefinition1.getObjectDefinitionId());
		_objectEntry2 = _objectEntryLocalService.getObjectEntry(
			_objectEntry2.getExternalReferenceCode(),
			_objectEntry2.getGroupId(),
			_objectDefinition1.getObjectDefinitionId());

		Assert.assertEquals(
			TestPropsValues.getUserId(), _objectEntry1.getUserId());
		Assert.assertEquals(
			TestPropsValues.getUserId(), _objectEntry2.getUserId());
	}

	@Test
	public void testImportWithUpsertAndKeepCreatorAndObjectEntriesDoNotExist()
		throws Exception {

		_executeImportTask("UPSERT", "KEEP_CREATOR");

		_objectEntry1 = _objectEntryLocalService.getObjectEntry(
			_objectEntry1.getExternalReferenceCode(),
			_objectEntry1.getGroupId(),
			_objectDefinition1.getObjectDefinitionId());
		_objectEntry2 = _objectEntryLocalService.getObjectEntry(
			_objectEntry2.getExternalReferenceCode(),
			_objectEntry2.getGroupId(),
			_objectDefinition1.getObjectDefinitionId());

		Assert.assertEquals(
			TestPropsValues.getUserId(), _objectEntry1.getUserId());
		Assert.assertEquals(_user.getUserId(), _objectEntry2.getUserId());
	}

	@Test
	public void testImportWithUpsertAndOverwriteCreator() throws Exception {
		_objectEntry1 = _addObjectEntry(
			_objectEntry1.getExternalReferenceCode(), _objectDefinition1,
			_OBJECT_FIELD_NAME_TEXT, RandomTestUtil.randomString(),
			TestPropsValues.getUser());

		_objectEntry2 = _addObjectEntry(
			_objectEntry2.getExternalReferenceCode(), _objectDefinition1,
			_OBJECT_FIELD_NAME_TEXT, RandomTestUtil.randomString(),
			TestPropsValues.getUser());

		_executeImportTask("UPSERT", "OVERWRITE_CREATOR");

		_objectEntry1 = _objectEntryLocalService.getObjectEntry(
			_objectEntry1.getExternalReferenceCode(),
			_objectEntry1.getGroupId(),
			_objectDefinition1.getObjectDefinitionId());
		_objectEntry2 = _objectEntryLocalService.getObjectEntry(
			_objectEntry2.getExternalReferenceCode(),
			_objectEntry2.getGroupId(),
			_objectDefinition1.getObjectDefinitionId());

		Assert.assertEquals(
			TestPropsValues.getUserId(), _objectEntry1.getUserId());
		Assert.assertEquals(
			TestPropsValues.getUserId(), _objectEntry2.getUserId());
	}

	private ObjectEntry _addObjectEntry(
			ObjectDefinition objectDefinition, String objectFieldName,
			Serializable objectFieldValue, User user)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			0L, user.getUserId(), objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				objectFieldName, objectFieldValue
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private ObjectEntry _addObjectEntry(
			String externalReferenceCode, ObjectDefinition objectDefinition,
			String objectFieldName, Serializable objectFieldValue, User user)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			0L, user.getUserId(), objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				objectFieldName, objectFieldValue
			).put(
				"externalReferenceCode", externalReferenceCode
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private void _executeExportTask() throws Exception {
		HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

		httpInvoker.header(HttpHeaders.ACCEPT, ContentTypes.APPLICATION_JSON);
		httpInvoker.httpMethod(HttpInvoker.HttpMethod.POST);
		httpInvoker.path(
			StringBundler.concat(
				"http://localhost:8080/o/headless-batch-engine/v1.0",
				"/export-task/com.liferay.object.rest.dto.v1_0.ObjectEntry",
				"/JSON?taskItemDelegateName=", _objectDefinition1.getName()));
		httpInvoker.userNameAndPassword(
			"test@liferay.com:" + PropsValues.DEFAULT_ADMIN_PASSWORD);

		HttpInvoker.HttpResponse httpResponse = httpInvoker.invoke();

		ExportTask exportTask = ExportTaskSerDes.toDTO(
			httpResponse.getContent());

		String externalReferenceCode = exportTask.getExternalReferenceCode();

		while (true) {
			exportTask = ExportTaskSerDes.toDTO(
				_invoke(
					"http://localhost:8080/o/headless-batch-engine/v1.0" +
						"/export-task/by-external-reference-code/" +
							externalReferenceCode));

			if (Objects.equals(
					exportTask.getExecuteStatusAsString(), "COMPLETED")) {

				break;
			}
			else if (Objects.equals(
						exportTask.getExecuteStatusAsString(), "FAILED")) {

				throw new AssertionError(exportTask.getErrorMessage());
			}
		}

		httpInvoker = HttpInvoker.newHttpInvoker();

		httpInvoker.header(
			HttpHeaders.ACCEPT, ContentTypes.APPLICATION_OCTET_STREAM);
		httpInvoker.httpMethod(HttpInvoker.HttpMethod.GET);
		httpInvoker.path(
			StringBundler.concat(
				"http://localhost:8080/o/headless-batch-engine/v1.0",
				"/export-task/by-external-reference-code/",
				externalReferenceCode, "/content"));
		httpInvoker.userNameAndPassword(
			"test@liferay.com:" + PropsValues.DEFAULT_ADMIN_PASSWORD);

		httpResponse = httpInvoker.invoke();

		try (InputStream inputStream = new UnsyncByteArrayInputStream(
				httpResponse.getBinaryContent())) {

			ZipInputStream zipInputStream = new ZipInputStream(inputStream);

			zipInputStream.getNextEntry();

			_json = StringUtil.read(zipInputStream);
		}
	}

	private void _executeImportTask(
			String createStrategy, String importCreatorStrategy)
		throws Exception {

		HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

		httpInvoker.body(_json, "application/json");
		httpInvoker.httpMethod(HttpInvoker.HttpMethod.POST);

		httpInvoker.path(
			StringBundler.concat(
				"http://localhost:8080/o/headless-batch-engine/v1.0",
				"/import-task/com.liferay.object.rest.dto.v1_0.ObjectEntry",
				"?createStrategy=", createStrategy, "&importCreatorStrategy=",
				importCreatorStrategy, "&taskItemDelegateName=",
				_objectDefinition1.getName()));

		httpInvoker.userNameAndPassword(
			"test@liferay.com:" + PropsValues.DEFAULT_ADMIN_PASSWORD);

		HttpInvoker.HttpResponse httpResponse = httpInvoker.invoke();

		ImportTask importTask = ImportTaskSerDes.toDTO(
			httpResponse.getContent());

		String externalReferenceCode = importTask.getExternalReferenceCode();

		while (true) {
			importTask = ImportTaskSerDes.toDTO(
				_invoke(
					"http://localhost:8080/o/headless-batch-engine/v1.0" +
						"/import-task/by-external-reference-code/" +
							externalReferenceCode));

			if (Objects.equals(
					importTask.getExecuteStatusAsString(), "COMPLETED")) {

				break;
			}
			else if (Objects.equals(
						importTask.getExecuteStatusAsString(), "FAILED")) {

				throw new AssertionError(importTask.getErrorMessage());
			}
		}
	}

	private String _invoke(String url) throws Exception {
		HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

		httpInvoker.httpMethod(HttpInvoker.HttpMethod.GET);
		httpInvoker.path(url);
		httpInvoker.userNameAndPassword(
			"test@liferay.com:" + PropsValues.DEFAULT_ADMIN_PASSWORD);

		HttpInvoker.HttpResponse httpResponse = httpInvoker.invoke();

		Assert.assertEquals(200, httpResponse.getStatusCode());

		return httpResponse.getContent();
	}

	private static final String _OBJECT_FIELD_NAME_TEXT = "testFieldName";

	private String _json;
	private ObjectDefinition _objectDefinition1;
	private ObjectEntry _objectEntry1;
	private ObjectEntry _objectEntry2;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}