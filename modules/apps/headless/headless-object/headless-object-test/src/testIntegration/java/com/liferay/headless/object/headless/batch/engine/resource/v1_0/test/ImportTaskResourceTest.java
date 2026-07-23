/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.object.headless.batch.engine.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
import com.liferay.headless.object.client.dto.v1_0.ObjectEntryFolder;
import com.liferay.headless.object.client.resource.v1_0.ObjectEntryFolderResource;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Jhosseph Gonzalez
 */
@RunWith(Arquillian.class)
public class ImportTaskResourceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_testGroup = GroupTestUtil.addGroup();

		_testCompany = CompanyLocalServiceUtil.getCompany(
			_testGroup.getCompanyId());

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			_testCompany.getCompanyId());

		_objectEntryFolderResource = ObjectEntryFolderResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			_testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	@After
	public void tearDown() throws Exception {
		GroupTestUtil.deleteGroup(_testGroup);
	}

	@Test
	public void testPostImportTask() throws Exception {

		// Create strategy "INSERT", execute status "COMPLETED", update strategy
		// null

		JSONObject jsonObject = JSONUtil.put(
			"externalReferenceCode", RandomTestUtil.randomString()
		).put(
			"label", RandomTestUtil.randomString()
		).put(
			"title", RandomTestUtil.randomString()
		);

		ObjectEntryFolder objectEntryFolder = _postImportTask(
			"INSERT", "COMPLETED", jsonObject, null);

		JSONAssert.assertEquals(
			jsonObject.toString(), objectEntryFolder.toString(),
			JSONCompareMode.LENIENT);

		// Create strategy "INSERT", execute status "FAILED", update strategy
		// null

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.batch.engine.internal." +
					"BatchEngineImportTaskExecutorImpl",
				LoggerTestUtil.ERROR)) {

			objectEntryFolder = _postImportTask(
				"INSERT", "FAILED",
				JSONUtil.put(
					"externalReferenceCode",
					objectEntryFolder.getExternalReferenceCode()
				).put(
					"label", RandomTestUtil.randomString()
				).put(
					"title", RandomTestUtil.randomString()
				),
				null);

			JSONAssert.assertEquals(
				jsonObject.toString(), objectEntryFolder.toString(),
				JSONCompareMode.LENIENT);
		}

		// Create strategy "UPSERT", execute status "COMPLETED", update strategy

		jsonObject = JSONUtil.put(
			"description", RandomTestUtil.randomString()
		).put(
			"externalReferenceCode", RandomTestUtil.randomString()
		).put(
			"label", RandomTestUtil.randomString()
		).put(
			"title", RandomTestUtil.randomString()
		);

		objectEntryFolder = _postImportTask(
			"UPSERT", "COMPLETED", jsonObject, null);

		JSONAssert.assertEquals(
			jsonObject.toString(), objectEntryFolder.toString(),
			JSONCompareMode.LENIENT);

		// Create strategy "UPSERT", execute status "COMPLETED", update strategy
		// "PARTIAL_UPDATE"

		jsonObject = JSONUtil.put(
			"externalReferenceCode",
			objectEntryFolder.getExternalReferenceCode()
		).put(
			"label", RandomTestUtil.randomString()
		).put(
			"title", RandomTestUtil.randomString()
		);

		String expectedDescriptionValue = objectEntryFolder.getDescription();

		objectEntryFolder = _postImportTask(
			"UPSERT", "COMPLETED", jsonObject, "PARTIAL_UPDATE");

		Assert.assertEquals(
			expectedDescriptionValue, objectEntryFolder.getDescription());

		JSONAssert.assertEquals(
			jsonObject.toString(), objectEntryFolder.toString(),
			JSONCompareMode.LENIENT);

		// Create strategy "UPSERT", execute status "COMPLETED", update strategy
		// "UPDATE"

		jsonObject = JSONUtil.put(
			"externalReferenceCode",
			objectEntryFolder.getExternalReferenceCode()
		).put(
			"label", RandomTestUtil.randomString()
		).put(
			"title", RandomTestUtil.randomString()
		);

		objectEntryFolder = _postImportTask(
			"UPSERT", "COMPLETED", jsonObject, "UPDATE");

		Assert.assertTrue(Validator.isNull(objectEntryFolder.getDescription()));

		JSONAssert.assertEquals(
			jsonObject.toString(), objectEntryFolder.toString(),
			JSONCompareMode.LENIENT);
	}

	private ObjectEntryFolder _postImportTask(
			String createStrategy, String expectedExecuteStatus,
			JSONObject jsonObject, String updateStrategy)
		throws Exception {

		ImportTaskResource.Builder builder = ImportTaskResource.builder(
		).authentication(
			"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			_testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).parameter(
			"siteExternalReferenceCode", _testGroup.getExternalReferenceCode()
		);

		if (updateStrategy != null) {
			builder.parameter("updateStrategy", updateStrategy);
		}

		ImportTaskResource importTaskResource = builder.build();

		ImportTask importTask = importTaskResource.postImportTask(
			"com.liferay.headless.object.dto.v1_0.ObjectEntryFolder", null,
			null, null, createStrategy, null, null, "ON_ERROR_FAIL", null,
			JSONUtil.putAll(
				jsonObject
			).toString());

		while (true) {
			importTask = importTaskResource.getImportTask(importTask.getId());

			ImportTask.ExecuteStatus executeStatus =
				importTask.getExecuteStatus();

			if (StringUtil.equals(executeStatus.getValue(), "COMPLETED") ||
				StringUtil.equals(executeStatus.getValue(), "FAILED")) {

				Assert.assertEquals(
					expectedExecuteStatus, executeStatus.getValue());

				return _objectEntryFolderResource.
					getScopeScopeKeyObjectEntryFolderByExternalReferenceCode(
						String.valueOf(_testGroup.getGroupId()),
						jsonObject.getString("externalReferenceCode"));
			}
		}
	}

	private ObjectEntryFolderResource _objectEntryFolderResource;
	private Company _testCompany;
	private User _testCompanyAdminUser;
	private Group _testGroup;

}