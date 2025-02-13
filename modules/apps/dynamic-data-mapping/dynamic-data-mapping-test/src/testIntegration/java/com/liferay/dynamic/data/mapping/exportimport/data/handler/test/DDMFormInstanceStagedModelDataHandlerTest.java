/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormInstanceTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.util.DDMUtil;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.test.util.lar.BaseStagedModelDataHandlerTestCase;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Leonardo Barros
 * @author Pedro Queiroz
 */
@RunWith(Arquillian.class)
@Sync
public class DDMFormInstanceStagedModelDataHandlerTest
	extends BaseStagedModelDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_settingsDDMFormValues =
			DDMFormInstanceTestUtil.createSettingsDDMFormValues();
	}

	@Test
	public void testExportImport() throws Exception {
		ObjectDefinition objectDefinition1 = _addObjectDefinition(
			RandomTestUtil.randomString(), TestPropsValues.getUserId());

		DDMFormInstance ddmFormInstance1 = _addDDMFormInstance(
			objectDefinition1);

		initExport();

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, ddmFormInstance1);

		ObjectDefinitionLocalServiceUtil.deleteObjectDefinition(
			objectDefinition1.getObjectDefinitionId());

		String originalName = PrincipalThreadLocal.getName();
		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		Company company = CompanyTestUtil.addCompany();

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					company.getCompanyId())) {

			User user = UserTestUtil.getAdminUser(company.getCompanyId());

			Assert.assertNotNull(user);

			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(user));

			PrincipalThreadLocal.setName(user.getUserId());

			ObjectDefinition objectDefinition2 = _addObjectDefinition(
				objectDefinition1.getExternalReferenceCode(), user.getUserId());

			initImport();

			_assertDDMFormInstanceSettings(
				objectDefinition1.getObjectDefinitionId(),
				portletDataContext.getZipEntryAsString(
					ExportImportPathUtil.getModelPath(
						ddmFormInstance1, "settings-ddm-form-values.json")));

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, ddmFormInstance1);

			DDMFormInstance ddmFormInstance2 = (DDMFormInstance)getStagedModel(
				ddmFormInstance1.getUuid(), liveGroup);

			_assertDDMFormInstanceSettings(
				objectDefinition2.getObjectDefinitionId(),
				ddmFormInstance2.getSettings());

			ObjectDefinitionLocalServiceUtil.deleteObjectDefinition(
				objectDefinition2.getObjectDefinitionId());
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
			PrincipalThreadLocal.setName(originalName);

			CompanyLocalServiceUtil.deleteCompany(company);
		}
	}

	@Override
	protected Map<String, List<StagedModel>> addDependentStagedModelsMap(
			Group group)
		throws Exception {

		Map<String, List<StagedModel>> dependentStagedModelsMap =
			new LinkedHashMap<>();

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			group.getGroupId(), DDMFormInstance.class.getName());

		addDependentStagedModel(
			dependentStagedModelsMap, DDMStructure.class, ddmStructure);

		return dependentStagedModelsMap;
	}

	@Override
	protected StagedModel addStagedModel(
			Group group,
			Map<String, List<StagedModel>> dependentStagedModelsMap)
		throws Exception {

		List<StagedModel> dependentStagedModels = dependentStagedModelsMap.get(
			DDMStructure.class.getSimpleName());

		DDMStructure ddmStructure = (DDMStructure)dependentStagedModels.get(0);

		DDMFormInstance ddmFormInstance =
			DDMFormInstanceTestUtil.addDDMFormInstance(
				ddmStructure, group, _settingsDDMFormValues,
				TestPropsValues.getUserId());

		return DDMFormInstanceTestUtil.updateDDMFormInstance(
			ddmFormInstance.getFormInstanceId(), _settingsDDMFormValues);
	}

	@Override
	protected StagedModel getStagedModel(String uuid, Group group)
		throws PortalException {

		return DDMFormInstanceLocalServiceUtil.
			getDDMFormInstanceByUuidAndGroupId(uuid, group.getGroupId());
	}

	@Override
	protected Class<? extends StagedModel> getStagedModelClass() {
		return DDMFormInstance.class;
	}

	@Override
	protected void validateImportedStagedModel(
			StagedModel stagedModel, StagedModel importedStagedModel)
		throws Exception {

		super.validateImportedStagedModel(stagedModel, importedStagedModel);

		DDMFormInstance importedFormInstance =
			(DDMFormInstance)importedStagedModel;

		Assert.assertEquals(
			_settingsDDMFormValues,
			importedFormInstance.getSettingsDDMFormValues());
	}

	private DDMFormInstance _addDDMFormInstance(
			ObjectDefinition objectDefinition)
		throws Exception {

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		DDMFormField ddmFormField = DDMFormTestUtil.createTextDDMFormField(
			"text", false, false, false);

		ddmFormField.setProperty(
			"objectFieldName",
			JSONUtil.put(
				"text"
			).toString());

		ddmForm.addDDMFormField(ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"objectDefinitionId",
				new UnlocalizedValue(
					JSONUtil.put(
						String.valueOf(objectDefinition.getObjectDefinitionId())
					).toString())));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"storageType",
				new UnlocalizedValue(
					JSONUtil.put(
						"object"
					).toString())));

		DDMStructure ddmStructure = DDMStructureLocalServiceUtil.addStructure(
			null, TestPropsValues.getUserId(), stagingGroup.getGroupId(), 0,
			PortalUtil.getClassNameId(DDMFormInstance.class.getName()), null,
			HashMapBuilder.put(
				LocaleUtil.getSiteDefault(), RandomTestUtil.randomString()
			).build(),
			null, ddmForm, DDMUtil.getDefaultDDMFormLayout(ddmForm), "object",
			DDMStructureConstants.TYPE_DEFAULT,
			ServiceContextTestUtil.getServiceContext());

		return DDMFormInstanceLocalServiceUtil.addFormInstance(
			ddmStructure.getUserId(), ddmStructure.getGroupId(),
			ddmStructure.getStructureId(), ddmStructure.getNameMap(),
			ddmStructure.getNameMap(), ddmFormValues,
			ServiceContextTestUtil.getServiceContext(
				stagingGroup, TestPropsValues.getUserId()));
	}

	private ObjectDefinition _addObjectDefinition(
			String externalReferenceCode, long userId)
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.addCustomObjectDefinition(
				userId, 0, null, false, false, true, false, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(),
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), "text")));

		objectDefinition.setExternalReferenceCode(externalReferenceCode);

		return ObjectDefinitionLocalServiceUtil.updateObjectDefinition(
			objectDefinition);
	}

	private void _assertDDMFormInstanceSettings(
			long objectDefinitionId, String settingsDDMFormValues)
		throws Exception {

		JSONObject settingsDDMFormValuesJSONObject =
			_jsonFactory.createJSONObject(settingsDDMFormValues);

		JSONArray fieldValuesJSONArray =
			settingsDDMFormValuesJSONObject.getJSONArray("fieldValues");

		for (int i = 0; i < fieldValuesJSONArray.length(); i++) {
			JSONObject fieldValueJSONObject =
				fieldValuesJSONArray.getJSONObject(i);

			if (!StringUtil.equals(
					fieldValueJSONObject.getString("name"),
					"objectDefinitionId")) {

				continue;
			}

			Assert.assertEquals(
				objectDefinitionId,
				_jsonFactory.createJSONArray(
					fieldValueJSONObject.getString("value")
				).getLong(
					0
				));
		}
	}

	private static final JSONFactory _jsonFactory = new JSONFactoryImpl();

	private DDMFormValues _settingsDDMFormValues;

}