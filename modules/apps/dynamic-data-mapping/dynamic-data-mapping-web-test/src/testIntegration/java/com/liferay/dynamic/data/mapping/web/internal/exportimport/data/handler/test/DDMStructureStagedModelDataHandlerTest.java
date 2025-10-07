/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.web.internal.exportimport.data.handler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProvider;
import com.liferay.dynamic.data.mapping.data.provider.configuration.DDMDataProviderConfiguration;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializerDeserializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance;
import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstanceLink;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMDataProviderInstanceLinkLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMDataProviderInstanceLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormFactory;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.test.util.lar.BaseStagedModelDataHandlerTestCase;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Daniel Kocsis
 */
@RunWith(Arquillian.class)
public class DDMStructureStagedModelDataHandlerTest
	extends BaseStagedModelDataHandlerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		ConfigurationTestUtil.saveConfiguration(
			DDMDataProviderConfiguration.class.getName(),
			HashMapDictionaryBuilder.<String, Object>put(
				"accessLocalNetwork", true
			).build());
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		ConfigurationTestUtil.saveConfiguration(
			DDMDataProviderConfiguration.class.getName(),
			HashMapDictionaryBuilder.<String, Object>put(
				"accessLocalNetwork", false
			).build());
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_availableLocales = LanguageUtil.getAvailableLocales(
			TestPropsValues.getCompanyId());
		_defaultLocale = LocaleUtil.getDefault();
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		LanguageUtil.init();

		CompanyTestUtil.resetCompanyLocales(
			TestPropsValues.getCompanyId(), _availableLocales, _defaultLocale);
	}

	@Test
	public void testImportStructureToGlobalSite() throws Exception {
		Company company = CompanyLocalServiceUtil.getCompany(
			stagingGroup.getCompanyId());

		Group companyGroup = company.getGroup();

		DDMStructure structure = DDMStructureTestUtil.addStructure(
			companyGroup.getGroupId(), _CLASS_NAME);

		initExport(companyGroup);

		try {
			ExportImportThreadLocal.setPortletExportInProcess(true);

			StagedModelDataHandlerUtil.exportStagedModel(
				portletDataContext, structure);
		}
		finally {
			ExportImportThreadLocal.setPortletExportInProcess(false);
		}

		_targetCompany = CompanyTestUtil.addCompany();

		User targetGuestUser = _targetCompany.getGuestUser();

		initImport(companyGroup, _targetCompany.getGroup());

		portletDataContext.setUserIdStrategy(
			new TestUserIdStrategy(targetGuestUser));

		StagedModel exportedStagedModel = readExportedStagedModel(structure);

		Assert.assertNotNull(exportedStagedModel);

		try {
			ExportImportThreadLocal.setPortletImportInProcess(true);

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, exportedStagedModel);
		}
		finally {
			ExportImportThreadLocal.setPortletImportInProcess(false);
		}

		StagedModel importedStagedModel = getStagedModel(
			exportedStagedModel.getUuid(), _targetCompany.getGroup());

		Assert.assertNotNull(importedStagedModel);
	}

	@Test
	public void testImportStructureWithDisabledDefaultLocale()
		throws Exception {

		DDMStructure structure = DDMStructureTestUtil.addStructure(
			stagingGroup.getGroupId(), _CLASS_NAME);

		Locale newLocale = LocaleUtil.BRAZIL;

		if (newLocale.equals(_defaultLocale)) {
			newLocale = LocaleUtil.CHINA;
		}

		LanguageUtil.init();

		CompanyTestUtil.resetCompanyLocales(
			stagingGroup.getCompanyId(), Arrays.asList(newLocale), newLocale);

		initExport();

		try {
			ExportImportThreadLocal.setPortletExportInProcess(true);

			StagedModelDataHandlerUtil.exportStagedModel(
				portletDataContext, structure);
		}
		finally {
			ExportImportThreadLocal.setPortletExportInProcess(false);
		}

		initImport();

		StagedModel exportedStagedModel = readExportedStagedModel(structure);

		Assert.assertNotNull(exportedStagedModel);

		try {
			ExportImportThreadLocal.setPortletImportInProcess(true);

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, exportedStagedModel);
		}
		finally {
			ExportImportThreadLocal.setPortletImportInProcess(false);
		}

		StagedModel importedStagedModel = getStagedModel(
			exportedStagedModel.getUuid(), liveGroup);

		Assert.assertNotNull(importedStagedModel);
	}

	@Test
	public void testPublishStructureWithParentGroups() throws Exception {
		Group parentGroup = GroupTestUtil.addGroup();

		Group childGroup = GroupTestUtil.addGroup(parentGroup.getGroupId());

		DDMStructure parentStructure = DDMStructureTestUtil.addStructure(
			parentGroup.getGroupId(), _CLASS_NAME);

		DDMStructure childStructure = DDMStructureTestUtil.addStructure(
			childGroup.getGroupId(), _CLASS_NAME,
			parentStructure.getStructureId(),
			DDMStructureTestUtil.getSampleDDMForm("childname"),
			LocaleUtil.getSiteDefault(),
			ServiceContextTestUtil.getServiceContext());

		_exportStructure(parentGroup, parentStructure);

		Group newParentGroup = GroupTestUtil.addGroup();

		_importStructure(parentGroup, newParentGroup, parentStructure);

		_exportStructure(childGroup, childStructure);

		childGroup = GroupTestUtil.deleteGroup(childGroup);

		GroupTestUtil.deleteGroup(parentGroup);

		Group newChildGroup = GroupTestUtil.addGroup(
			newParentGroup.getGroupId());

		_importStructure(childGroup, newChildGroup, childStructure);

		DDMStructure importedParentStructure =
			DDMStructureLocalServiceUtil.fetchDDMStructureByUuidAndGroupId(
				parentStructure.getUuid(), newParentGroup.getGroupId());

		DDMStructure importedChildStructure =
			DDMStructureLocalServiceUtil.fetchDDMStructureByUuidAndGroupId(
				childStructure.getUuid(), newChildGroup.getGroupId());

		Assert.assertNotNull(importedParentStructure);
		Assert.assertNotNull(importedChildStructure);
		Assert.assertEquals(
			importedParentStructure.getStructureId(),
			importedChildStructure.getParentStructureId());
	}

	@Override
	protected Map<String, List<StagedModel>> addDependentStagedModelsMap(
			Group group)
		throws Exception {

		Map<String, List<StagedModel>> dependentStagedModelsMap =
			new HashMap<>();

		// Parent structure

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			group.getGroupId(), _CLASS_NAME,
			DDMStructureTestUtil.getSampleDDMForm(
				RandomTestUtil.randomString()));

		addDependentStagedModel(
			dependentStagedModelsMap, DDMStructure.class, ddmStructure);

		// Data provider instance

		Map<Locale, String> nameMap = HashMapBuilder.put(
			LocaleUtil.getSiteDefault(), "Data provider"
		).build();

		DDMFormValues ddmFormValues = _getDDMDataProviderInstanceFormValues();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group, TestPropsValues.getUserId());

		DDMDataProviderInstance ddmDataProviderInstance =
			DDMDataProviderInstanceLocalServiceUtil.addDataProviderInstance(
				TestPropsValues.getUserId(), group.getGroupId(), nameMap,
				nameMap, ddmFormValues, "rest", serviceContext);

		addDependentStagedModel(
			dependentStagedModelsMap, DDMDataProviderInstance.class,
			ddmDataProviderInstance);

		return dependentStagedModelsMap;
	}

	@Override
	protected StagedModel addStagedModel(
			Group group,
			Map<String, List<StagedModel>> dependentStagedModelsMap)
		throws Exception {

		// Parent structure

		List<StagedModel> dependentStagedModels = dependentStagedModelsMap.get(
			DDMStructure.class.getSimpleName());

		DDMStructure parentStructure = (DDMStructure)dependentStagedModels.get(
			0);

		// Data provider instance

		dependentStagedModels = dependentStagedModelsMap.get(
			DDMDataProviderInstance.class.getSimpleName());

		DDMDataProviderInstance ddmDataProviderInstance =
			(DDMDataProviderInstance)dependentStagedModels.get(0);

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm("Name");

		DDMFormField selectDDMFormField = DDMFormTestUtil.createDDMFormField(
			"Country", "Country", "select", "string", true, false, true);

		selectDDMFormField.setProperty("dataSourceType", "data-provider");
		selectDDMFormField.setProperty(
			"ddmDataProviderInstanceId",
			ddmDataProviderInstance.getDataProviderInstanceId());

		ddmForm.addDDMFormField(selectDDMFormField);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group, TestPropsValues.getUserId());

		return DDMStructureTestUtil.addStructure(
			group.getGroupId(), _CLASS_NAME, parentStructure.getStructureId(),
			ddmForm, LocaleUtil.getSiteDefault(), serviceContext);
	}

	@Override
	protected StagedModel getStagedModel(String uuid, Group group)
		throws PortalException {

		return DDMStructureLocalServiceUtil.getDDMStructureByUuidAndGroupId(
			uuid, group.getGroupId());
	}

	@Override
	protected Class<? extends StagedModel> getStagedModelClass() {
		return DDMStructure.class;
	}

	@Override
	protected void validateImport(
			Map<String, List<StagedModel>> dependentStagedModelsMap,
			Group group)
		throws Exception {

		List<StagedModel> ddmStructureDependentStagedModels =
			dependentStagedModelsMap.get(DDMStructure.class.getSimpleName());

		Assert.assertEquals(
			ddmStructureDependentStagedModels.toString(), 1,
			ddmStructureDependentStagedModels.size());

		DDMStructure ddmStructure =
			(DDMStructure)ddmStructureDependentStagedModels.get(0);

		DDMStructureLocalServiceUtil.getDDMStructureByUuidAndGroupId(
			ddmStructure.getUuid(), group.getGroupId());

		List<StagedModel> ddmDataProviderInstanceDependentStagedModels =
			dependentStagedModelsMap.get(
				DDMDataProviderInstance.class.getSimpleName());

		Assert.assertEquals(
			ddmDataProviderInstanceDependentStagedModels.toString(), 1,
			ddmDataProviderInstanceDependentStagedModels.size());

		DDMDataProviderInstance dataProviderInstance =
			(DDMDataProviderInstance)
				ddmDataProviderInstanceDependentStagedModels.get(0);

		DDMDataProviderInstanceLocalServiceUtil.
			getDDMDataProviderInstanceByUuidAndGroupId(
				dataProviderInstance.getUuid(), group.getGroupId());
	}

	@Override
	protected void validateImportedStagedModel(
			StagedModel stagedModel, StagedModel importedStagedModel)
		throws Exception {

		super.validateImportedStagedModel(stagedModel, importedStagedModel);

		// Structure

		DDMStructure structure = (DDMStructure)stagedModel;
		DDMStructure importedStructure = (DDMStructure)importedStagedModel;

		Assert.assertEquals(
			structure.getStructureKey(), importedStructure.getStructureKey());
		Assert.assertEquals(structure.getName(), importedStructure.getName());
		Assert.assertEquals(
			structure.getDescription(), importedStructure.getDescription());
		Assert.assertEquals(
			structure.getStructureKey(), importedStructure.getStructureKey());
		Assert.assertEquals(
			structure.getStorageType(), importedStructure.getStorageType());
		Assert.assertEquals(structure.getType(), importedStructure.getType());

		structure = DDMStructureLocalServiceUtil.fetchDDMStructure(
			structure.getStructureId());

		if (structure == null) {
			return;
		}

		// Data provider instance

		List<DDMDataProviderInstanceLink> ddmDataProviderInstanceLinks =
			DDMDataProviderInstanceLinkLocalServiceUtil.
				getDataProviderInstanceLinks(structure.getStructureId());

		List<DDMDataProviderInstanceLink> importedDDMDataProviderInstanceLinks =
			DDMDataProviderInstanceLinkLocalServiceUtil.
				getDataProviderInstanceLinks(
					importedStructure.getStructureId());

		Assert.assertEquals(
			ddmDataProviderInstanceLinks.toString(), 1,
			ddmDataProviderInstanceLinks.size());
		Assert.assertEquals(
			importedDDMDataProviderInstanceLinks.toString(), 1,
			importedDDMDataProviderInstanceLinks.size());

		DDMDataProviderInstanceLink dataProviderInstanceLink =
			ddmDataProviderInstanceLinks.get(0);

		DDMDataProviderInstance dataProviderInstance =
			DDMDataProviderInstanceLocalServiceUtil.getDataProviderInstance(
				dataProviderInstanceLink.getDataProviderInstanceId());

		DDMDataProviderInstanceLink importedDataProviderInstanceLink =
			importedDDMDataProviderInstanceLinks.get(0);

		long importedDataProviderInstanceId =
			importedDataProviderInstanceLink.getDataProviderInstanceId();

		DDMDataProviderInstance importedDataProviderInstance =
			DDMDataProviderInstanceLocalServiceUtil.getDataProviderInstance(
				importedDataProviderInstanceId);

		Assert.assertEquals(
			_getDDMDataProviderInstanceFormValues(dataProviderInstance),
			_getDDMDataProviderInstanceFormValues(
				importedDataProviderInstance));
	}

	private void _exportStructure(Group exportGroup, DDMStructure structure)
		throws Exception {

		initExport(exportGroup);

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, structure);
	}

	private DDMFormValues _getDDMDataProviderInstanceFormValues() {
		Class<?> ddmDataProviderSettings = _ddmDataProvider.getSettings();

		DDMForm ddmForm = DDMFormFactory.create(ddmDataProviderSettings);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"cacheable", Boolean.FALSE.toString()));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"key", "countryId"));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"password", TestPropsValues.USER_PASSWORD));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"url",
				"http://localhost:8080/api/jsonws/country/get-countries"));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"username", "test@liferay.com"));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"timeout", "1000"));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"value", "nameCurrentValue"));

		return ddmFormValues;
	}

	private DDMFormValues _getDDMDataProviderInstanceFormValues(
		DDMDataProviderInstance ddmDataProviderInstance) {

		Class<?> ddmDataProviderSettings = _ddmDataProvider.getSettings();

		DDMForm ddmForm = DDMFormFactory.create(ddmDataProviderSettings);

		DDMFormValuesDeserializerDeserializeRequest.Builder builder =
			DDMFormValuesDeserializerDeserializeRequest.Builder.newBuilder(
				ddmDataProviderInstance.getDefinition(), ddmForm);

		DDMFormValuesDeserializerDeserializeResponse
			ddmFormValuesDeserializerDeserializeResponse =
				_jsonDDMFormValuesDeserializer.deserialize(builder.build());

		return ddmFormValuesDeserializerDeserializeResponse.getDDMFormValues();
	}

	private void _importStructure(
			Group exportGroup, Group importGroup, DDMStructure structure)
		throws Exception {

		initImport(exportGroup, importGroup);

		if (Objects.nonNull(structure)) {
			DDMStructure exportedStructure =
				(DDMStructure)readExportedStagedModel(structure);

			StagedModelDataHandlerUtil.importStagedModel(
				portletDataContext, exportedStructure);
		}
	}

	private static final String _CLASS_NAME =
		"com.liferay.dynamic.data.lists.model.DDLRecordSet";

	private static Set<Locale> _availableLocales;
	private static Locale _defaultLocale;

	@Inject(filter = "ddm.form.values.deserializer.type=json")
	private static DDMFormValuesDeserializer _jsonDDMFormValuesDeserializer;

	@Inject(filter = "ddm.data.provider.type=rest")
	private DDMDataProvider _ddmDataProvider;

	@DeleteAfterTestRun
	private Company _targetCompany;

}