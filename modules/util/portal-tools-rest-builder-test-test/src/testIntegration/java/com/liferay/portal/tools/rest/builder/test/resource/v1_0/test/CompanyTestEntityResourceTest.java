/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.CompanyTestEntity;
import com.liferay.portal.tools.rest.builder.test.client.resource.v1_0.CompanyTestEntityResource;
import com.liferay.portal.util.PropsValues;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class CompanyTestEntityResourceTest
	extends BaseCompanyTestEntityResourceTestCase {

	@Override
	@Test
	public void testPostCompanyTestEntity() throws Exception {
		super.testPostCompanyTestEntity();

		_testPostCompanyTestEntityBatch();
	}

	@Override
	@Test
	public void testPutCompanyTestEntity() throws Exception {
		super.testPutCompanyTestEntity();

		_testPutCompanyTestEntityBatch();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"description"};
	}

	@Override
	protected CompanyTestEntity
			testGetCompanyTestEntitiesPage_addCompanyTestEntity(
				CompanyTestEntity companyTestEntity)
		throws Exception {

		return companyTestEntityResource.postCompanyTestEntity(
			companyTestEntity);
	}

	@Override
	protected CompanyTestEntity testGetCompanyTestEntity_addCompanyTestEntity()
		throws Exception {

		return companyTestEntityResource.postCompanyTestEntity(
			randomCompanyTestEntity());
	}

	@Override
	protected CompanyTestEntity
			testGetCompanyTestEntityByExternalReferenceCode_addCompanyTestEntity()
		throws Exception {

		return companyTestEntityResource.postCompanyTestEntity(
			randomCompanyTestEntity());
	}

	@Override
	protected CompanyTestEntity
			testGetCompanyTestEntityPermissionsPage_addCompanyTestEntity()
		throws Exception {

		return companyTestEntityResource.postCompanyTestEntity(
			randomCompanyTestEntity());
	}

	@Override
	protected CompanyTestEntity
			testPatchCompanyTestEntity_addCompanyTestEntity()
		throws Exception {

		return companyTestEntityResource.postCompanyTestEntity(
			randomCompanyTestEntity());
	}

	@Override
	protected CompanyTestEntity testPostCompanyTestEntity_addCompanyTestEntity(
			CompanyTestEntity companyTestEntity)
		throws Exception {

		return companyTestEntityResource.postCompanyTestEntity(
			companyTestEntity);
	}

	@Override
	protected CompanyTestEntity
			testPostCompanyTestEntity_addPermissionsCompanyTestEntity(
				CompanyTestEntity companyTestEntity)
		throws Exception {

		return permissionsCompanyTestEntityResource.postCompanyTestEntity(
			companyTestEntity);
	}

	@Override
	protected CompanyTestEntity testPutCompanyTestEntity_addCompanyTestEntity()
		throws Exception {

		return companyTestEntityResource.postCompanyTestEntity(
			randomCompanyTestEntity());
	}

	@Override
	protected CompanyTestEntity
			testPutCompanyTestEntityByExternalReferenceCode_addCompanyTestEntity()
		throws Exception {

		return companyTestEntityResource.postCompanyTestEntity(
			randomCompanyTestEntity());
	}

	@Override
	protected CompanyTestEntity
			testPutCompanyTestEntityPermissionsPage_addCompanyTestEntity()
		throws Exception {

		return companyTestEntityResource.postCompanyTestEntity(
			randomCompanyTestEntity());
	}

	private CompanyTestEntityResource _createCompanyTestEntityResource(
			String importStrategy, String updateStrategy)
		throws Exception {

		User testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		return CompanyTestEntityResource.builder(
		).authentication(
			testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).locale(
			LocaleUtil.getDefault()
		).parameter(
			"importStrategy", importStrategy
		).parameter(
			"updateStrategy", updateStrategy
		).build();
	}

	private void _testPostCompanyTestEntityBatch() throws Exception {
		CompanyTestEntity companyTestEntity =
			companyTestEntityResource.postCompanyTestEntity(
				randomCompanyTestEntity());

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.batch.engine.internal." +
					"BatchEngineImportTaskExecutorImpl",
				LoggerTestUtil.ERROR)) {

			_waitForFinish(
				"FAILED", true,
				JSONFactoryUtil.createJSONObject(
					companyTestEntityResource.
						postCompanyTestEntityBatchHttpResponse(
							null,
							JSONUtil.putAll(
								JSONFactoryUtil.createJSONObject(
									companyTestEntity.toString()))
						).getContent()));
		}
	}

	private void _testPutCompanyTestEntityBatch() throws Exception {
		CompanyTestEntity postCompanyTestEntity =
			companyTestEntityResource.postCompanyTestEntity(
				randomCompanyTestEntity());

		String description = RandomTestUtil.randomString();

		postCompanyTestEntity.setDescription(description);

		CompanyTestEntity randomCompanyTestEntity = randomCompanyTestEntity();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.batch.engine.internal.strategy." +
					"OnErrorContinueBatchEngineImportStrategy",
				LoggerTestUtil.ERROR)) {

			CompanyTestEntityResource companyTestEntityResource =
				_createCompanyTestEntityResource("ON_ERROR_CONTINUE", "UPDATE");

			_waitForFinish(
				"COMPLETED", true,
				JSONFactoryUtil.createJSONObject(
					companyTestEntityResource.
						putCompanyTestEntityBatchHttpResponse(
							null,
							JSONUtil.putAll(
								JSONFactoryUtil.createJSONObject(
									postCompanyTestEntity.toString()),
								JSONFactoryUtil.createJSONObject(
									randomCompanyTestEntity.toString()))
						).getContent()));
		}

		CompanyTestEntity actualCompanyTestEntity =
			companyTestEntityResource.getCompanyTestEntity(
				postCompanyTestEntity.getId());

		Assert.assertEquals(
			description, actualCompanyTestEntity.getDescription());

		assertHttpResponseStatusCode(
			404,
			companyTestEntityResource.
				getCompanyTestEntityByExternalReferenceCodeHttpResponse(
					randomCompanyTestEntity.getExternalReferenceCode()));
	}

	private JSONObject _waitForFinish(
			String expectedExecuteStatus, boolean importTask,
			JSONObject jsonObject)
		throws Exception {

		String endpoint = StringBundler.concat(
			"headless-batch-engine/v1.0/",
			importTask ? "import-task" : "export-task",
			"/by-external-reference-code/");

		while (true) {
			jsonObject = HTTPTestUtil.invokeToJSONObject(
				null, endpoint + jsonObject.getString("externalReferenceCode"),
				Http.Method.GET);

			String executeStatus = jsonObject.getString("executeStatus");

			if (StringUtil.equals(executeStatus, "COMPLETED") ||
				StringUtil.equals(executeStatus, "FAILED")) {

				Assert.assertEquals(expectedExecuteStatus, executeStatus);

				return jsonObject;
			}
		}
	}

}