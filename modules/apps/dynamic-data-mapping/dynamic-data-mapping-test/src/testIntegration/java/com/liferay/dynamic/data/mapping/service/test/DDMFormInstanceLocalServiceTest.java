/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesSerializer;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceSettings;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceVersion;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalServiceUtil;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.dynamic.data.mapping.test.util.DDMFormInstanceTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.dynamic.data.mapping.util.DDMFormInstanceFactory;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesSerializeUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rafael Praxedes
 */
@RunWith(Arquillian.class)
public class DDMFormInstanceLocalServiceTest extends BaseDDMServiceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		Class<?> formInstanceClass = DDMFormInstance.class;

		_classNameId = PortalUtil.getClassNameId(formInstanceClass.getName());
	}

	@Test
	public void testAddFormInstanceShouldCreateApprovedFormInstanceVersion()
		throws Exception {

		DDMStructure structure = addStructure(_classNameId, "Test Structure");

		DDMForm settingsDDMForm = DDMFormTestUtil.createDDMForm();

		DDMFormValues settingsDDMFormValues =
			DDMFormValuesTestUtil.createDDMFormValues(settingsDDMForm);

		DDMFormInstance formInstance =
			DDMFormInstanceLocalServiceUtil.addFormInstance(
				structure.getUserId(), structure.getGroupId(),
				structure.getStructureId(), structure.getNameMap(),
				structure.getNameMap(), settingsDDMFormValues,
				ServiceContextTestUtil.getServiceContext(
					group, TestPropsValues.getUserId()));

		DDMFormInstanceVersion latestFormInstanceVersion =
			formInstance.getFormInstanceVersion(formInstance.getVersion());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED,
			latestFormInstanceVersion.getStatus());
	}

	@Test
	public void testAddFormInstanceShouldCreateDraftFormInstanceVersion()
		throws Exception {

		DDMStructure structure = addStructure(_classNameId, "Test Structure");

		DDMForm settingsDDMForm = DDMFormTestUtil.createDDMForm();

		DDMFormValues settingsDDMFormValues =
			DDMFormValuesTestUtil.createDDMFormValues(settingsDDMForm);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group, TestPropsValues.getUserId());

		serviceContext.setAttribute("status", WorkflowConstants.STATUS_DRAFT);

		DDMFormInstance formInstance =
			DDMFormInstanceLocalServiceUtil.addFormInstance(
				structure.getUserId(), structure.getGroupId(),
				structure.getStructureId(), structure.getNameMap(),
				structure.getNameMap(), settingsDDMFormValues, serviceContext);

		DDMFormInstanceVersion latestFormInstanceVersion =
			formInstance.getFormInstanceVersion(formInstance.getVersion());

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT,
			latestFormInstanceVersion.getStatus());
	}

	@Test
	public void testGetSettingsDDMFormValues() throws Exception {
		DDMFormInstance formInstance =
			DDMFormInstanceTestUtil.addDDMFormInstance(
				group, TestPropsValues.getUserId());

		DDMFormInstance cachedFormInstance1 =
			DDMFormInstanceLocalServiceUtil.getFormInstance(
				formInstance.getFormInstanceId());

		Assert.assertEquals(
			StorageType.DEFAULT.getValue(),
			cachedFormInstance1.getStorageType());

		DDMFormValues settingsDDMFormValues =
			cachedFormInstance1.getSettingsDDMFormValues();

		DDMFormFieldValue ddmFormFieldValue =
			settingsDDMFormValues.getDDMFormFieldValue("storageType", false);

		ddmFormFieldValue.setValue(
			new UnlocalizedValue(RandomTestUtil.randomString()));

		DDMFormInstance cachedFormInstance2 =
			DDMFormInstanceLocalServiceUtil.getFormInstance(
				formInstance.getFormInstanceId());

		DDMFormInstanceSettings ddmFormInstanceSettings =
			DDMFormInstanceFactory.create(
				DDMFormInstanceSettings.class,
				cachedFormInstance2.getSettingsDDMFormValues());

		Assert.assertEquals(
			StorageType.DEFAULT.getValue(),
			ddmFormInstanceSettings.storageType());

		Assert.assertEquals(
			StorageType.DEFAULT.getValue(),
			cachedFormInstance2.getStorageType());
	}

	@Test
	public void testSetSettings() throws Exception {
		DDMFormInstance formInstance =
			DDMFormInstanceTestUtil.addDDMFormInstance(
				group, TestPropsValues.getUserId());

		DDMFormInstance cachedFormInstance =
			DDMFormInstanceLocalServiceUtil.getFormInstance(
				formInstance.getFormInstanceId());

		cachedFormInstance.setMvccVersion(
			cachedFormInstance.getMvccVersion() - 1);

		cachedFormInstance.getObjectDefinitionId();
		cachedFormInstance.getSettingsDDMFormValues();
		cachedFormInstance.getStorageType();

		long objectDefinitionId = RandomTestUtil.randomLong();
		String storageType = RandomTestUtil.randomString();

		cachedFormInstance.setSettings(
			_createSettings(objectDefinitionId, storageType));

		DDMFormInstanceSettings ddmFormInstanceSettings =
			DDMFormInstanceFactory.create(
				DDMFormInstanceSettings.class,
				cachedFormInstance.getSettingsDDMFormValues());

		Assert.assertFalse(
			cachedFormInstance.getSettings(),
			ddmFormInstanceSettings.requireCaptcha());

		Assert.assertEquals(storageType, cachedFormInstance.getStorageType());
		Assert.assertEquals(
			objectDefinitionId, cachedFormInstance.getObjectDefinitionId());

		objectDefinitionId = RandomTestUtil.randomLong();
		storageType = RandomTestUtil.randomString();

		cachedFormInstance.setSettings(
			_createSettings(objectDefinitionId, storageType));

		Assert.assertEquals(
			objectDefinitionId, cachedFormInstance.getObjectDefinitionId());
		Assert.assertEquals(storageType, cachedFormInstance.getStorageType());
	}

	@Test
	public void testUpdateFormInstanceShouldCreateNewFormInstanceVersion()
		throws Exception {

		DDMStructure structure = addStructure(_classNameId, "Test Structure");

		DDMForm settingsDDMForm = DDMFormTestUtil.createDDMForm();

		DDMFormValues settingsDDMFormValues =
			DDMFormValuesTestUtil.createDDMFormValues(settingsDDMForm);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group, TestPropsValues.getUserId());

		DDMFormInstance formInstance =
			DDMFormInstanceLocalServiceUtil.addFormInstance(
				structure.getUserId(), structure.getGroupId(),
				structure.getStructureId(), structure.getNameMap(),
				structure.getNameMap(), settingsDDMFormValues, serviceContext);

		DDMFormInstanceVersion firstFormInstanceVersion =
			formInstance.getFormInstanceVersion(formInstance.getVersion());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED,
			firstFormInstanceVersion.getStatus());

		formInstance = DDMFormInstanceLocalServiceUtil.updateFormInstance(
			formInstance.getFormInstanceId(), formInstance.getStructureId(),
			formInstance.getNameMap(), formInstance.getDescriptionMap(),
			settingsDDMFormValues, serviceContext);

		DDMFormInstanceVersion secondFormInstanceVersion =
			formInstance.getFormInstanceVersion(formInstance.getVersion());

		Assert.assertNotEquals(
			firstFormInstanceVersion, secondFormInstanceVersion);
		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED,
			secondFormInstanceVersion.getStatus());
	}

	@Test
	public void testUpdateFormInstanceShouldKeepFormInstanceVersion()
		throws Exception {

		DDMStructure structure = addStructure(_classNameId, "Test Structure");

		DDMForm settingsDDMForm = DDMFormTestUtil.createDDMForm();

		DDMFormValues settingsDDMFormValues =
			DDMFormValuesTestUtil.createDDMFormValues(settingsDDMForm);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group, TestPropsValues.getUserId());

		serviceContext.setAttribute("status", WorkflowConstants.STATUS_DRAFT);

		DDMFormInstance formInstance =
			DDMFormInstanceLocalServiceUtil.addFormInstance(
				structure.getUserId(), structure.getGroupId(),
				structure.getStructureId(), structure.getNameMap(),
				structure.getNameMap(), settingsDDMFormValues, serviceContext);

		DDMFormInstanceVersion firstFormInstanceVersion =
			formInstance.getFormInstanceVersion(formInstance.getVersion());

		formInstance = DDMFormInstanceLocalServiceUtil.updateFormInstance(
			formInstance.getFormInstanceId(), formInstance.getStructureId(),
			formInstance.getNameMap(), formInstance.getDescriptionMap(),
			settingsDDMFormValues, serviceContext);

		DDMFormInstanceVersion secondFormInstanceVersion =
			formInstance.getFormInstanceVersion(formInstance.getVersion());

		Assert.assertEquals(
			firstFormInstanceVersion, secondFormInstanceVersion);

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT,
			firstFormInstanceVersion.getStatus());

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT,
			secondFormInstanceVersion.getStatus());
	}

	@Test
	public void testUpdateFormInstanceShouldUpdateFormInstanceVersion()
		throws Exception {

		DDMStructure structure = addStructure(_classNameId, "Test Structure");

		DDMForm settingsDDMForm = DDMFormTestUtil.createDDMForm();

		DDMFormValues settingsDDMFormValues =
			DDMFormValuesTestUtil.createDDMFormValues(settingsDDMForm);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group, TestPropsValues.getUserId());

		serviceContext.setAttribute("status", WorkflowConstants.STATUS_DRAFT);

		DDMFormInstance formInstance =
			DDMFormInstanceLocalServiceUtil.addFormInstance(
				structure.getUserId(), structure.getGroupId(),
				structure.getStructureId(), structure.getNameMap(),
				structure.getNameMap(), settingsDDMFormValues, serviceContext);

		DDMFormInstanceVersion firstFormInstanceVersion =
			formInstance.getFormInstanceVersion(formInstance.getVersion());

		serviceContext.setAttribute(
			"status", WorkflowConstants.STATUS_APPROVED);

		formInstance = DDMFormInstanceLocalServiceUtil.updateFormInstance(
			formInstance.getFormInstanceId(), formInstance.getStructureId(),
			formInstance.getNameMap(), formInstance.getDescriptionMap(),
			settingsDDMFormValues, serviceContext);

		DDMFormInstanceVersion secondFormInstanceVersion =
			formInstance.getFormInstanceVersion(formInstance.getVersion());

		Assert.assertEquals(
			firstFormInstanceVersion.getFormInstanceVersionId(),
			secondFormInstanceVersion.getFormInstanceVersionId());
		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED,
			secondFormInstanceVersion.getStatus());
	}

	private String _createSettings(
		long objectDefinitionId, String storageType) {

		DDMFormValues settingsDDMFormValues =
			DDMFormInstanceTestUtil.createSettingsDDMFormValues(false);

		settingsDDMFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"objectDefinitionId", String.valueOf(objectDefinitionId)));

		DDMFormFieldValue ddmFormFieldValue =
			settingsDDMFormValues.getDDMFormFieldValue("storageType", false);

		ddmFormFieldValue.setValue(new UnlocalizedValue(storageType));

		return DDMFormValuesSerializeUtil.serialize(
			settingsDDMFormValues, _jsonDDMFormValuesSerializer);
	}

	private static long _classNameId;

	@Inject(filter = "ddm.form.values.serializer.type=json")
	private DDMFormValuesSerializer _jsonDDMFormValuesSerializer;

}