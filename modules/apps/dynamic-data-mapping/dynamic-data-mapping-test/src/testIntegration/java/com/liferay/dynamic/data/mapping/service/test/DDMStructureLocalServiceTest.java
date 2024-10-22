/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.exception.DuplicateDDMStructureExternalReferenceCodeException;
import com.liferay.dynamic.data.mapping.exception.InvalidParentStructureException;
import com.liferay.dynamic.data.mapping.exception.NoSuchStructureException;
import com.liferay.dynamic.data.mapping.exception.RequiredStructureException;
import com.liferay.dynamic.data.mapping.exception.StructureDefinitionException;
import com.liferay.dynamic.data.mapping.exception.StructureDuplicateElementException;
import com.liferay.dynamic.data.mapping.exception.StructureDuplicateStructureKeyException;
import com.liferay.dynamic.data.mapping.exception.StructureNameException;
import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance;
import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstanceLink;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMFormRule;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureLayout;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMDataProviderInstanceLinkLocalService;
import com.liferay.dynamic.data.mapping.service.DDMDataProviderInstanceLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLayoutLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureVersionLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.util.DDMUtil;
import com.liferay.dynamic.data.mapping.util.comparator.StructureIdComparator;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;

/**
 * @author Eduardo García
 */
@RunWith(Arquillian.class)
public class DDMStructureLocalServiceTest extends BaseDDMServiceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		_classNameId = PortalUtil.getClassNameId(DDL_RECORD_SET_CLASS_NAME);
	}

	@Test
	public void testAddStructure() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();

		DDMStructure ddmStructure = _addStructure(
			externalReferenceCode, RandomTestUtil.randomString());

		Assert.assertTrue(
			Validator.isNotNull(ddmStructure.getExternalReferenceCode()));

		AssertUtils.assertFailure(
			DuplicateDDMStructureExternalReferenceCodeException.class,
			StringBundler.concat(
				"Duplicate structure with the external reference code \"",
				externalReferenceCode, "\" and class name ID \"",
				ddmStructure.getClassNameId(), "\" on site ID \"",
				ddmStructure.getGroupId(), "\""),
			() -> _addStructure(
				externalReferenceCode, RandomTestUtil.randomString()));
	}

	@Test(expected = StructureDefinitionException.class)
	public void testAddStructureMissingRequiredElementAttribute()
		throws Exception {

		addStructure(
			_classNameId, null, "Test Structure",
			read("ddm-structure-required-element-attribute.xsd"),
			StorageType.DEFAULT.getValue(), DDMStructureConstants.TYPE_DEFAULT);
	}

	@Test(expected = StructureDuplicateElementException.class)
	public void testAddStructureWithDuplicateElementName() throws Exception {
		addStructure(
			_classNameId, null, "Test Structure",
			read("ddm-structure-duplicate-element-name.xsd"),
			StorageType.DEFAULT.getValue(), DDMStructureConstants.TYPE_DEFAULT);
	}

	@Test(expected = StructureDuplicateElementException.class)
	public void testAddStructureWithDuplicateElementNameInParent()
		throws Exception {

		DDMStructure parentStructure = addStructure(
			_classNameId, null, "Test Parent Structure",
			read("ddm-structure-duplicate-element-name.xsd"),
			StorageType.DEFAULT.getValue(), DDMStructureConstants.TYPE_DEFAULT);

		addStructure(
			parentStructure.getStructureId(), _classNameId, null,
			"Test Structure", read("ddm-structure-duplicate-element-name.xsd"),
			StorageType.DEFAULT.getValue(), DDMStructureConstants.TYPE_DEFAULT);
	}

	@Test(expected = StructureDuplicateStructureKeyException.class)
	public void testAddStructureWithDuplicateKey() throws Exception {
		String structureKey = RandomTestUtil.randomString();

		addStructure(
			_classNameId, structureKey, "Test Structure 1",
			read("test-structure.xsd"), StorageType.DEFAULT.getValue(),
			DDMStructureConstants.TYPE_DEFAULT);

		addStructure(
			_classNameId, structureKey, "Test Structure 2",
			read("test-structure.xsd"), StorageType.DEFAULT.getValue(),
			DDMStructureConstants.TYPE_DEFAULT);
	}

	@Test(expected = StructureDefinitionException.class)
	public void testAddStructureWithInvalidElementAttribute() throws Exception {
		addStructure(
			_classNameId, null, "Test Structure",
			read("ddm-structure-invalid-element-attribute.xsd"),
			StorageType.DEFAULT.getValue(), DDMStructureConstants.TYPE_DEFAULT);
	}

	@Test(expected = StructureDefinitionException.class)
	public void testAddStructureWithoutDefinition() throws Exception {
		addStructure(
			_classNameId, null, "Test Structure", StringPool.BLANK,
			StorageType.DEFAULT.getValue(), DDMStructureConstants.TYPE_DEFAULT);
	}

	@Test(expected = StructureNameException.class)
	public void testAddStructureWithoutName() throws Exception {
		addStructure(
			_classNameId, null, StringPool.BLANK, read("test-structure.xsd"),
			StorageType.DEFAULT.getValue(), DDMStructureConstants.TYPE_DEFAULT);
	}

	@Test
	public void testAddStructureWithReferencedDataProviderInstance1()
		throws Exception {

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		DDMFormField ddmFormField = new DDMFormField("Field", "select");

		ddmFormField.setDataType("string");

		long ddmDataProviderInstanceId = RandomTestUtil.randomLong();

		ddmFormField.setProperty("dataSourceType", "data-provider");
		ddmFormField.setProperty(
			"ddmDataProviderInstanceId", ddmDataProviderInstanceId);

		ddmForm.addDDMFormField(ddmFormField);

		DDMStructure structure = ddmStructureTestHelper.addStructure(
			ddmForm, StorageType.DEFAULT.getValue());

		DDMDataProviderInstanceLink dataProviderInstanceLink =
			_ddmDataProviderInstanceLinkLocalService.
				fetchDataProviderInstanceLink(
					ddmDataProviderInstanceId, structure.getStructureId());

		Assert.assertNotNull(dataProviderInstanceLink);

		_ddmStructureLocalService.deleteStructure(structure);

		Assert.assertNull(
			_ddmDataProviderInstanceLinkLocalService.
				fetchDataProviderInstanceLink(
					ddmDataProviderInstanceId, structure.getStructureId()));
	}

	@Test
	public void testAddStructureWithReferencedDataProviderInstance2()
		throws Exception {

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		DDMFormField ddmFormField = new DDMFormField("Field", "select");

		ddmFormField.setDataType("string");

		long ddmDataProviderInstanceId = RandomTestUtil.randomLong();

		ddmFormField.setProperty("dataSourceType", "data-provider");

		JSONArray dataProviderInstanceIdJSONArray =
			_jsonFactory.createJSONArray();

		dataProviderInstanceIdJSONArray.put(ddmDataProviderInstanceId);

		ddmFormField.setProperty(
			"ddmDataProviderInstanceId", dataProviderInstanceIdJSONArray);

		ddmForm.addDDMFormField(ddmFormField);

		DDMStructure structure = ddmStructureTestHelper.addStructure(
			ddmForm, StorageType.DEFAULT.getValue());

		DDMDataProviderInstanceLink dataProviderInstanceLink =
			_ddmDataProviderInstanceLinkLocalService.
				fetchDataProviderInstanceLink(
					ddmDataProviderInstanceId, structure.getStructureId());

		Assert.assertNotNull(dataProviderInstanceLink);

		_ddmStructureLocalService.deleteStructure(structure);

		Assert.assertNull(
			_ddmDataProviderInstanceLinkLocalService.
				fetchDataProviderInstanceLink(
					ddmDataProviderInstanceId, structure.getStructureId()));
	}

	@Test
	public void testAddStructureWithReferencedDataProviderInstance3()
		throws Exception {

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm("Field1");

		DDMDataProviderInstance dataProviderInstance1 =
			createDDMDataProviderInstance();

		List<String> actions = new ArrayList<>();

		String action = String.format(
			"call('%s','','')", dataProviderInstance1.getUuid());

		actions.add(action);

		DDMFormRule ddmFormRule = new DDMFormRule(actions, "TRUE");

		ddmForm.addDDMFormRule(ddmFormRule);

		DDMStructure structure = ddmStructureTestHelper.addStructure(
			ddmForm, StorageType.DEFAULT.getValue());

		List<DDMDataProviderInstanceLink> dataProviderInstanceLinks =
			_ddmDataProviderInstanceLinkLocalService.
				getDataProviderInstanceLinks(structure.getStructureId());

		Assert.assertEquals(
			dataProviderInstanceLinks.toString(), 1,
			dataProviderInstanceLinks.size());
	}

	@Test
	public void testAddStructureWithSameExternalReferenceCodeAndDifferentClassNameId()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		DDMStructure structure1 = _addStructure(
			externalReferenceCode,
			PortalUtil.getClassNameId(_CLASS_NAME_DL_FILE_ENTRY_METADATA),
			TestPropsValues.getGroupId(), RandomTestUtil.randomString(),
			TestPropsValues.getUserId());
		DDMStructure structure2 = _addStructure(
			externalReferenceCode,
			PortalUtil.getClassNameId(_CLASS_NAME_JOURNAL_ARTICLE),
			TestPropsValues.getGroupId(), RandomTestUtil.randomString(),
			TestPropsValues.getUserId());

		Assert.assertNotEquals(structure1, structure2);

		Assert.assertEquals(
			2,
			_ddmStructureLocalService.getStructures(
				TestPropsValues.getGroupId()
			).size());
	}

	@Test(expected = DuplicateDDMStructureExternalReferenceCodeException.class)
	public void testAddStructureWithSameExternalReferenceCodeAndSameClassNameId()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();
		long classNameId = PortalUtil.getClassNameId(
			_CLASS_NAME_DL_FILE_ENTRY_METADATA);

		_addStructure(
			externalReferenceCode, classNameId, TestPropsValues.getGroupId(),
			RandomTestUtil.randomString(), TestPropsValues.getUserId());
		_addStructure(
			externalReferenceCode, classNameId, TestPropsValues.getGroupId(),
			RandomTestUtil.randomString(), TestPropsValues.getUserId());
	}

	@Test
	public void testAddStructureWithSameExternalReferenceCodeBetweenDifferentSites()
		throws Exception {

		DDMStructure structure1 = _addStructure(RandomTestUtil.randomString());

		User user = UserTestUtil.addGroupAdminUser(GroupTestUtil.addGroup());

		DDMStructure structure2 = _addStructure(
			structure1.getExternalReferenceCode(), structure1.getClassNameId(),
			user.getGroupId(), RandomTestUtil.randomString(), user.getUserId());

		Assert.assertNotNull(
			_ddmStructureLocalService.fetchStructure(
				structure2.getExternalReferenceCode(), structure2.getGroupId(),
				structure2.getClassNameId()));

		Assert.assertNotEquals(structure1, structure2);

		Assert.assertEquals(
			1,
			_ddmStructureLocalService.getStructuresCount(
				structure1.getGroupId()));

		Assert.assertEquals(
			1,
			_ddmStructureLocalService.getStructuresCount(
				structure2.getGroupId()));

		_ddmStructureLocalService.deleteStructure(
			structure1.getExternalReferenceCode(), structure1.getGroupId(),
			structure1.getClassNameId());

		Assert.assertEquals(
			0,
			_ddmStructureLocalService.getStructuresCount(
				structure1.getGroupId()));

		Assert.assertEquals(
			1,
			_ddmStructureLocalService.getStructuresCount(
				structure2.getGroupId()));
	}

	@Test
	public void testCopyStructure() throws Exception {
		DDMStructure structure = addStructure(_classNameId, "Test Structure");

		DDMStructure copyStructure = copyStructure(structure);

		Assert.assertEquals(structure.getGroupId(), copyStructure.getGroupId());
		JSONAssert.assertEquals(
			structure.getDefinition(), copyStructure.getDefinition(), false);
		Assert.assertEquals(
			structure.getStorageType(), copyStructure.getStorageType());
		Assert.assertEquals(structure.getType(), copyStructure.getType());
	}

	@Test
	public void testDeleteStructure() throws Exception {
		DDMStructure structure = addStructure(_classNameId, "Test Structure");

		_ddmStructureLocalService.deleteStructure(structure.getStructureId());

		Assert.assertNull(
			_ddmStructureLocalService.fetchDDMStructure(
				structure.getStructureId()));
	}

	@Test
	public void testDeleteStructureByExternalReferenceCode() throws Exception {
		DDMStructure ddmStructure = _addStructure(
			RandomTestUtil.randomString());

		_ddmStructureLocalService.deleteStructure(
			ddmStructure.getExternalReferenceCode(), ddmStructure.getGroupId(),
			ddmStructure.getClassNameId());

		Assert.assertNull(
			_ddmStructureLocalService.fetchStructure(
				ddmStructure.getExternalReferenceCode(),
				ddmStructure.getGroupId(), ddmStructure.getClassNameId()));

		AssertUtils.assertFailure(
			NoSuchStructureException.class,
			StringBundler.concat(
				"No DDMStructure exists with the key {externalReferenceCode=",
				ddmStructure.getExternalReferenceCode(), ", groupId=",
				ddmStructure.getGroupId(), ", classNameId=",
				ddmStructure.getClassNameId(), "}"),
			() -> _ddmStructureLocalService.deleteStructure(
				ddmStructure.getExternalReferenceCode(),
				ddmStructure.getGroupId(), ddmStructure.getClassNameId()));
	}

	@Test(
		expected = RequiredStructureException.MustNotDeleteStructureReferencedByTemplates.class
	)
	public void testDeleteStructureReferencedByTemplates() throws Exception {
		DDMStructure structure = addStructure(_classNameId, "Test Structure");

		addDisplayTemplate(
			structure.getPrimaryKey(), "Test Display Template",
			WorkflowConstants.STATUS_APPROVED);
		addFormTemplate(
			structure.getPrimaryKey(), "Test Form Template",
			WorkflowConstants.STATUS_APPROVED);

		_ddmStructureLocalService.deleteStructure(structure.getStructureId());
	}

	@Test
	public void testDeleteStructureWithLayoutRemoved() throws Exception {
		DDMStructure ddmStructure = ddmStructureTestHelper.addStructure(
			DDMFormTestUtil.createDDMForm("Field1"),
			StorageType.DEFAULT.getValue());

		DDMStructureLayout ddmStructureLayout =
			ddmStructure.fetchDDMStructureLayout();

		Assert.assertNotNull(ddmStructureLayout);

		_ddmStructureLayoutLocalService.deleteDDMStructureLayout(
			ddmStructureLayout.getStructureLayoutId());

		Assert.assertNull(ddmStructure.fetchDDMStructureLayout());

		Assert.assertTrue(
			ListUtil.isNotEmpty(
				_ddmStructureVersionLocalService.getStructureVersions(
					ddmStructure.getStructureId())));

		_ddmStructureLocalService.deleteStructure(
			ddmStructure.getStructureId());

		Assert.assertTrue(
			ListUtil.isEmpty(
				_ddmStructureVersionLocalService.getStructureVersions(
					ddmStructure.getStructureId())));
	}

	@Test
	public void testFetchStructure() throws Exception {
		DDMStructure structure = addStructure(_classNameId, "Test Structure");

		Assert.assertNotNull(
			_ddmStructureLocalService.fetchStructure(
				structure.getGroupId(), _classNameId,
				structure.getStructureKey()));
	}

	@Test
	public void testGetFullHierarchyDDMFormFieldsMap() throws Exception {
		DDMForm parentDDMForm = DDMFormTestUtil.createDDMForm();

		DDMFormField nameDDMFormField = DDMFormTestUtil.createTextDDMFormField(
			"Name", true, false, false);

		nameDDMFormField.addNestedDDMFormField(
			DDMFormTestUtil.createTextDDMFormField("Age", true, false, false));

		parentDDMForm.addDDMFormField(nameDDMFormField);

		DDMStructure parentStructure = ddmStructureTestHelper.addStructure(
			parentDDMForm, StorageType.DEFAULT.toString());

		DDMForm childDDMForm = DDMFormTestUtil.createDDMForm();

		DDMFormField descriptionDDMFormField =
			DDMFormTestUtil.createTextDDMFormField(
				"Description", true, false, false);

		childDDMForm.addDDMFormField(descriptionDDMFormField);

		DDMStructure childStructure = ddmStructureTestHelper.addStructure(
			parentStructure.getStructureId(), parentStructure.getClassNameId(),
			null, "Child Structure", StringPool.BLANK, childDDMForm,
			DDMUtil.getDefaultDDMFormLayout(childDDMForm),
			StorageType.DEFAULT.toString(), DDMStructureConstants.TYPE_DEFAULT);

		Map<String, DDMFormField> childFullHierarchyDDMFormFieldsMap =
			childStructure.getFullHierarchyDDMFormFieldsMap(true);

		Assert.assertTrue(
			childFullHierarchyDDMFormFieldsMap.containsKey("Name"));
		Assert.assertTrue(
			childFullHierarchyDDMFormFieldsMap.containsKey("Age"));
		Assert.assertTrue(
			childFullHierarchyDDMFormFieldsMap.containsKey("Description"));

		// Update parent DDM form to have just the Name field

		DDMForm parentDDMFormUpdated = DDMFormTestUtil.createDDMForm();

		parentDDMFormUpdated.addDDMFormField(nameDDMFormField);

		parentStructure.setDDMForm(parentDDMFormUpdated);

		_ddmStructureLocalService.updateDDMStructure(parentStructure);

		// Assert that the child DDM form has the full hierarchy updated

		childStructure = _ddmStructureLocalService.getStructure(
			childStructure.getStructureId());

		childFullHierarchyDDMFormFieldsMap =
			childStructure.getFullHierarchyDDMFormFieldsMap(true);

		Assert.assertTrue(
			childFullHierarchyDDMFormFieldsMap.containsKey("Name"));
		Assert.assertTrue(
			childFullHierarchyDDMFormFieldsMap.containsKey("Description"));
	}

	@Test
	public void testGetStructureByExternalReferenceCode() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();

		DDMStructure ddmStructure = _addStructure(
			externalReferenceCode, RandomTestUtil.randomString());

		Assert.assertEquals(
			externalReferenceCode, ddmStructure.getExternalReferenceCode());

		Assert.assertNotNull(
			_ddmStructureLocalService.getStructure(
				externalReferenceCode, ddmStructure.getGroupId(),
				ddmStructure.getClassNameId()));

		long classNameId = PortalUtil.getClassNameId(
			_CLASS_NAME_JOURNAL_ARTICLE);

		AssertUtils.assertFailure(
			NoSuchStructureException.class,
			StringBundler.concat(
				"No DDMStructure exists with the key {externalReferenceCode=",
				ddmStructure.getExternalReferenceCode(), ", groupId=",
				ddmStructure.getGroupId(), ", classNameId=", classNameId, "}"),
			() -> _ddmStructureLocalService.deleteStructure(
				ddmStructure.getExternalReferenceCode(),
				ddmStructure.getGroupId(), classNameId));
	}

	@Test
	public void testGetStructures() throws Exception {
		DDMStructure structure = addStructure(_classNameId, "Test Structure");

		List<DDMStructure> structures = _ddmStructureLocalService.getStructures(
			structure.getGroupId());

		Assert.assertTrue(
			structures.toString(), structures.contains(structure));
	}

	@Test
	public void testGetStructuresCountWithKeywords() throws Exception {
		DDMStructure ddmStructure1 = _addStructure("Basic Structure");
		DDMStructure ddmStructure2 = _addStructure("Blank Structure");
		DDMStructure ddmStructure3 = _addStructure("Sample Structure");

		Assert.assertEquals(
			1,
			_ddmStructureLocalService.getStructuresCount(
				group.getCompanyId(), new long[] {group.getGroupId()},
				_classNameId, "Basic", WorkflowConstants.STATUS_ANY));
		Assert.assertEquals(
			1,
			_ddmStructureLocalService.getStructuresCount(
				group.getCompanyId(), new long[] {group.getGroupId()},
				_classNameId, "Blank", WorkflowConstants.STATUS_ANY));
		Assert.assertEquals(
			3,
			_ddmStructureLocalService.getStructuresCount(
				group.getCompanyId(), new long[] {group.getGroupId()},
				_classNameId, "Structure", WorkflowConstants.STATUS_ANY));

		_updateStructure(ddmStructure1, "Test Structure");
		updateStructure(ddmStructure2);
		updateStructure(ddmStructure2);
		updateStructure(ddmStructure3);
		updateStructure(ddmStructure3);
		updateStructure(ddmStructure3);

		Assert.assertEquals(
			1,
			_ddmStructureLocalService.getStructuresCount(
				group.getCompanyId(), new long[] {group.getGroupId()},
				_classNameId, "Test", WorkflowConstants.STATUS_ANY));
		Assert.assertEquals(
			0,
			_ddmStructureLocalService.getStructuresCount(
				group.getCompanyId(), new long[] {group.getGroupId()},
				_classNameId, "Basic", WorkflowConstants.STATUS_ANY));
	}

	@Test
	public void testGetStructuresCountWithoutKeywords() throws Exception {
		DDMStructure ddmStructure1 = _addStructure(StringUtil.randomString());
		DDMStructure ddmStructure2 = _addStructure(StringUtil.randomString());
		DDMStructure ddmStructure3 = _addStructure(StringUtil.randomString());

		Assert.assertEquals(
			3,
			_ddmStructureLocalService.getStructuresCount(
				group.getCompanyId(), new long[] {group.getGroupId()},
				_classNameId, StringPool.BLANK, WorkflowConstants.STATUS_ANY));

		updateStructure(ddmStructure1);
		updateStructure(ddmStructure2);
		updateStructure(ddmStructure2);
		updateStructure(ddmStructure3);
		updateStructure(ddmStructure3);
		updateStructure(ddmStructure3);

		Assert.assertEquals(
			3,
			_ddmStructureLocalService.getStructuresCount(
				group.getCompanyId(), new long[] {group.getGroupId()},
				_classNameId, StringPool.BLANK, WorkflowConstants.STATUS_ANY));
	}

	@Test
	public void testGetStructuresWithKeywords() throws Exception {
		DDMStructure ddmStructure1 = _addStructure("Basic Structure");
		DDMStructure ddmStructure2 = _addStructure("Blank Structure");
		DDMStructure ddmStructure3 = _addStructure("Sample Structure");

		List<DDMStructure> ddmStructures =
			_ddmStructureLocalService.getStructures(
				group.getCompanyId(), new long[] {group.getGroupId()},
				_classNameId, "Basic", WorkflowConstants.STATUS_ANY,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(ddmStructures.toString(), 1, ddmStructures.size());

		ddmStructures = _ddmStructureLocalService.getStructures(
			group.getCompanyId(), new long[] {group.getGroupId()}, _classNameId,
			"Blank", WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertEquals(ddmStructures.toString(), 1, ddmStructures.size());

		ddmStructures = _ddmStructureLocalService.getStructures(
			group.getCompanyId(), new long[] {group.getGroupId()}, _classNameId,
			"Structure", WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertEquals(ddmStructures.toString(), 3, ddmStructures.size());

		_updateStructure(ddmStructure1, "Test Structure");
		updateStructure(ddmStructure2);
		updateStructure(ddmStructure2);
		updateStructure(ddmStructure3);
		updateStructure(ddmStructure3);
		updateStructure(ddmStructure3);

		ddmStructures = _ddmStructureLocalService.getStructures(
			group.getCompanyId(), new long[] {group.getGroupId()}, _classNameId,
			"Test", WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertEquals(ddmStructures.toString(), 1, ddmStructures.size());

		ddmStructures = _ddmStructureLocalService.getStructures(
			group.getCompanyId(), new long[] {group.getGroupId()}, _classNameId,
			"Basic", WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertEquals(ddmStructures.toString(), 0, ddmStructures.size());
	}

	@Test
	public void testGetStructuresWithoutKeywords() throws Exception {
		DDMStructure ddmStructure1 = _addStructure(StringUtil.randomString());
		DDMStructure ddmStructure2 = _addStructure(StringUtil.randomString());
		DDMStructure ddmStructure3 = _addStructure(StringUtil.randomString());

		List<DDMStructure> ddmStructures =
			_ddmStructureLocalService.getStructures(
				group.getCompanyId(), new long[] {group.getGroupId()},
				_classNameId, StringPool.BLANK, WorkflowConstants.STATUS_ANY,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(ddmStructures.toString(), 3, ddmStructures.size());

		updateStructure(ddmStructure1);
		updateStructure(ddmStructure2);
		updateStructure(ddmStructure2);
		updateStructure(ddmStructure3);
		updateStructure(ddmStructure3);
		updateStructure(ddmStructure3);

		ddmStructures = _ddmStructureLocalService.getStructures(
			group.getCompanyId(), new long[] {group.getGroupId()}, _classNameId,
			StringPool.BLANK, WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertEquals(ddmStructures.toString(), 3, ddmStructures.size());
	}

	@Test
	public void testGetTemplates() throws Exception {
		DDMStructure structure = addStructure(_classNameId, "Test Structure");

		addDisplayTemplate(
			structure.getStructureId(), "Test Display Template",
			WorkflowConstants.STATUS_APPROVED);
		addFormTemplate(
			structure.getStructureId(), "Test Form Template",
			WorkflowConstants.STATUS_APPROVED);

		List<DDMTemplate> templates = structure.getTemplates();

		Assert.assertEquals(templates.toString(), 2, templates.size());
	}

	@Test
	public void testSearchByAnyStatus() throws Exception {
		addStructure(
			0, _classNameId, null, StringUtil.randomString(), StringPool.BLANK,
			read("test-structure.xsd"), StorageType.DEFAULT.getValue(),
			DDMStructureConstants.TYPE_DEFAULT,
			WorkflowConstants.STATUS_APPROVED);

		addStructure(
			0, _classNameId, null, StringUtil.randomString(), StringPool.BLANK,
			read("test-structure.xsd"), StorageType.DEFAULT.getValue(),
			DDMStructureConstants.TYPE_DEFAULT, WorkflowConstants.STATUS_DRAFT);

		List<DDMStructure> structures = _ddmStructureLocalService.search(
			TestPropsValues.getCompanyId(), new long[] {group.getGroupId()},
			_classNameId, null, null, null, DDMStructureConstants.TYPE_DEFAULT,
			WorkflowConstants.STATUS_ANY, true, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertEquals(structures.toString(), 2, structures.size());
	}

	@Test
	public void testSearchByClassNameId() throws Exception {
		addStructure(_classNameId, StringUtil.randomString());
		addStructure(_classNameId, StringUtil.randomString());
		addStructure(_classNameId, StringUtil.randomString());

		List<DDMStructure> structures = _ddmStructureLocalService.search(
			TestPropsValues.getCompanyId(), new long[] {group.getGroupId()},
			_classNameId, null, null, null, DDMStructureConstants.TYPE_DEFAULT,
			WorkflowConstants.STATUS_APPROVED, true, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertEquals(structures.toString(), 3, structures.size());
	}

	@Test
	public void testSearchByDescription() throws Exception {
		addStructure(_classNameId, StringUtil.randomString(), "Contact");
		addStructure(_classNameId, StringUtil.randomString(), "Event");

		List<DDMStructure> structures = _ddmStructureLocalService.search(
			TestPropsValues.getCompanyId(), new long[] {group.getGroupId()},
			_classNameId, null, "Contact", null,
			DDMStructureConstants.TYPE_DEFAULT,
			WorkflowConstants.STATUS_APPROVED, true, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		DDMStructure structure = structures.get(0);

		Assert.assertEquals(
			"Contact", structure.getDescription(group.getDefaultLanguageId()));
	}

	@Test
	public void testSearchByDraftStatus() throws Exception {
		addStructure(
			0, _classNameId, null, StringUtil.randomString(), StringPool.BLANK,
			read("test-structure.xsd"), StorageType.DEFAULT.getValue(),
			DDMStructureConstants.TYPE_DEFAULT,
			WorkflowConstants.STATUS_APPROVED);

		addStructure(
			0, _classNameId, null, StringUtil.randomString(), StringPool.BLANK,
			read("test-structure.xsd"), StorageType.DEFAULT.getValue(),
			DDMStructureConstants.TYPE_DEFAULT, WorkflowConstants.STATUS_DRAFT);

		List<DDMStructure> structures = _ddmStructureLocalService.search(
			TestPropsValues.getCompanyId(), new long[] {group.getGroupId()},
			_classNameId, null, null, null, DDMStructureConstants.TYPE_DEFAULT,
			WorkflowConstants.STATUS_DRAFT, true, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertEquals(structures.toString(), 1, structures.size());
	}

	@Test
	public void testSearchByKeywords1() throws Exception {
		DDMStructure structure = addStructure(_classNameId, "Events");

		addStructure(_classNameId, "Event");

		List<DDMStructure> structures = _ddmStructureLocalService.search(
			structure.getCompanyId(), new long[] {structure.getGroupId()},
			structure.getClassNameId(), "Event",
			WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, StructureIdComparator.getInstance(true));

		Assert.assertEquals(structures.toString(), 2, structures.size());
		Assert.assertEquals("Events", getStructureName(structures.get(0)));
		Assert.assertEquals("Event", getStructureName(structures.get(1)));
	}

	@Test
	public void testSearchByKeywords2() throws Exception {
		DDMStructure structure = addStructure(_classNameId, "To Do");

		addStructure(_classNameId, "To Doing");

		List<DDMStructure> structures = _ddmStructureLocalService.search(
			structure.getCompanyId(), new long[] {structure.getGroupId()},
			structure.getClassNameId(), "To Do",
			WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, StructureIdComparator.getInstance(true));

		Assert.assertEquals(structures.toString(), 2, structures.size());
		Assert.assertEquals("To Do", getStructureName(structures.get(0)));
		Assert.assertEquals("To Doing", getStructureName(structures.get(1)));
	}

	@Test
	public void testSearchByName() throws Exception {
		addStructure(_classNameId, "Contact");
		addStructure(_classNameId, "Event");

		List<DDMStructure> structures = _ddmStructureLocalService.search(
			TestPropsValues.getCompanyId(), new long[] {group.getGroupId()},
			_classNameId, "Contact", null, null,
			DDMStructureConstants.TYPE_DEFAULT,
			WorkflowConstants.STATUS_APPROVED, true, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertEquals("Contact", getStructureName(structures.get(0)));
	}

	@Test
	public void testSearchByNameAndDescription() throws Exception {
		addStructure(_classNameId, "Contact", "Contact");
		addStructure(_classNameId, "Event", "Event");

		List<DDMStructure> structures = _ddmStructureLocalService.search(
			TestPropsValues.getCompanyId(), new long[] {group.getGroupId()},
			_classNameId, "Contact", "Event", null,
			DDMStructureConstants.TYPE_DEFAULT,
			WorkflowConstants.STATUS_APPROVED, true, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertEquals(structures.toString(), 2, structures.size());
	}

	@Test
	public void testSearchByNameAndDescriptionOrdered() throws Exception {
		addStructure(_classNameId, "Contact", "Contact");
		addStructure(_classNameId, "Event", "Event");

		List<DDMStructure> structures = _ddmStructureLocalService.search(
			TestPropsValues.getCompanyId(), new long[] {group.getGroupId()},
			_classNameId, "Contact", "Event", null,
			DDMStructureConstants.TYPE_DEFAULT,
			WorkflowConstants.STATUS_APPROVED, false, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, StructureIdComparator.getInstance(true));

		Assert.assertEquals("Contact", getStructureName(structures.get(0)));
		Assert.assertEquals("Event", getStructureName(structures.get(1)));
	}

	@Test
	public void testSearchByNonexistingStorageType() throws Exception {
		addStructure(_classNameId, StringUtil.randomString());

		List<DDMStructure> structures = _ddmStructureLocalService.search(
			TestPropsValues.getCompanyId(), new long[] {group.getGroupId()},
			_classNameId, null, null, "NonExistingStorageType",
			DDMStructureConstants.TYPE_DEFAULT,
			WorkflowConstants.STATUS_APPROVED, true, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertEquals(structures.toString(), 0, structures.size());
	}

	@Test
	public void testSearchByStorageType() throws Exception {
		addStructure(_classNameId, StringUtil.randomString());

		List<DDMStructure> structures = _ddmStructureLocalService.search(
			TestPropsValues.getCompanyId(), new long[] {group.getGroupId()},
			_classNameId, null, null, StorageType.DEFAULT.toString(),
			DDMStructureConstants.TYPE_DEFAULT,
			WorkflowConstants.STATUS_APPROVED, true, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertEquals(structures.toString(), 1, structures.size());
	}

	@Test
	public void testSearchByType() throws Exception {
		addStructure(
			0, _classNameId, null, StringUtil.randomString(), StringPool.BLANK,
			read("test-structure.xsd"), StorageType.DEFAULT.getValue(),
			DDMStructureConstants.TYPE_DEFAULT,
			WorkflowConstants.STATUS_APPROVED);

		addStructure(
			0, _classNameId, null, StringUtil.randomString(), StringPool.BLANK,
			read("test-structure.xsd"), StorageType.DEFAULT.getValue(),
			DDMStructureConstants.TYPE_FRAGMENT,
			WorkflowConstants.STATUS_APPROVED);

		List<DDMStructure> structures = _ddmStructureLocalService.search(
			TestPropsValues.getCompanyId(), new long[] {group.getGroupId()},
			_classNameId, null, null, null, DDMStructureConstants.TYPE_DEFAULT,
			WorkflowConstants.STATUS_APPROVED, true, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertEquals(structures.toString(), 1, structures.size());

		structures = _ddmStructureLocalService.search(
			TestPropsValues.getCompanyId(), new long[] {group.getGroupId()},
			_classNameId, null, null, null, DDMStructureConstants.TYPE_FRAGMENT,
			WorkflowConstants.STATUS_APPROVED, true, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertEquals(structures.toString(), 1, structures.size());
	}

	@Test
	public void testSearchCount() throws Exception {
		int initialCount = _ddmStructureLocalService.searchCount(
			TestPropsValues.getCompanyId(), new long[] {group.getGroupId()},
			_classNameId, "Test Structure", null, null,
			DDMStructureConstants.TYPE_DEFAULT,
			WorkflowConstants.STATUS_APPROVED, false);

		addStructure(_classNameId, "Test Structure");

		int count = _ddmStructureLocalService.searchCount(
			TestPropsValues.getCompanyId(), new long[] {group.getGroupId()},
			_classNameId, "Test Structure", null, null,
			DDMStructureConstants.TYPE_DEFAULT,
			WorkflowConstants.STATUS_APPROVED, false);

		Assert.assertEquals(initialCount + 1, count);
	}

	@Test
	public void testSearchCountByKeywords() throws Exception {
		int initialCount = _ddmStructureLocalService.searchCount(
			TestPropsValues.getCompanyId(), new long[] {group.getGroupId()},
			_classNameId, null, WorkflowConstants.STATUS_APPROVED);

		addStructure(_classNameId, "Test Structure");

		int count = _ddmStructureLocalService.searchCount(
			TestPropsValues.getCompanyId(), new long[] {group.getGroupId()},
			_classNameId, null, WorkflowConstants.STATUS_APPROVED);

		Assert.assertEquals(initialCount + 1, count);
	}

	@Test
	public void testSearchCountByType() throws Exception {
		int initialCount = _ddmStructureLocalService.searchCount(
			TestPropsValues.getCompanyId(), new long[] {group.getGroupId()},
			_classNameId, null, null, null, DDMStructureConstants.TYPE_FRAGMENT,
			WorkflowConstants.STATUS_ANY, true);

		addStructure(
			0, _classNameId, null, StringUtil.randomString(), StringPool.BLANK,
			read("test-structure.xsd"), StorageType.DEFAULT.getValue(),
			DDMStructureConstants.TYPE_FRAGMENT,
			WorkflowConstants.STATUS_APPROVED);

		int count = _ddmStructureLocalService.searchCount(
			TestPropsValues.getCompanyId(), new long[] {group.getGroupId()},
			_classNameId, null, null, null, DDMStructureConstants.TYPE_FRAGMENT,
			WorkflowConstants.STATUS_ANY, true);

		Assert.assertEquals(initialCount + 1, count);
	}

	@Test
	public void testSearchGlobalSiteStructure() throws Exception {
		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		DDMStructure structure = addStructure(
			company.getGroup(), _classNameId, "Global Structure");

		User user = UserTestUtil.addGroupAdminUser(group);

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			permissionCheckerFactory.create(user));

		List<DDMStructure> structures = _ddmStructureLocalService.search(
			structure.getCompanyId(),
			PortalUtil.getCurrentAndAncestorSiteGroupIds(group.getGroupId()),
			structure.getClassNameId(), "Global", WorkflowConstants.STATUS_ANY,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			StructureIdComparator.getInstance(true));

		Assert.assertEquals(structures.toString(), 0, structures.size());

		PermissionThreadLocal.setPermissionChecker(originalPermissionChecker);

		_ddmStructureLocalService.deleteStructure(structure);

		_userLocalService.deleteUser(user);
	}

	@Test
	public void testUpdateStructure() throws Exception {
		DDMStructure structure1 = _addStructure(RandomTestUtil.randomString());
		String externalReferenceCode = RandomTestUtil.randomString();

		structure1 = _updateStructure(
			externalReferenceCode, structure1.getClassNameId(),
			structure1.getGroupId(), RandomTestUtil.randomString(), structure1);

		Assert.assertEquals(
			externalReferenceCode, structure1.getExternalReferenceCode());

		structure1 = _addStructure(
			externalReferenceCode,
			PortalUtil.getClassNameId(_CLASS_NAME_JOURNAL_ARTICLE),
			group.getGroupId(), RandomTestUtil.randomString(),
			TestPropsValues.getUserId());

		DDMStructure structure2 = _addStructure(
			externalReferenceCode,
			PortalUtil.getClassNameId(_CLASS_NAME_DL_FILE_ENTRY_METADATA),
			group.getGroupId(), RandomTestUtil.randomString(),
			TestPropsValues.getUserId());

		DDMStructure finalStructure = structure1;

		AssertUtils.assertFailure(
			DuplicateDDMStructureExternalReferenceCodeException.class,
			StringBundler.concat(
				"Duplicate structure with the external reference code \"",
				externalReferenceCode, "\" and class name ID \"",
				PortalUtil.getClassNameId(_CLASS_NAME_JOURNAL_ARTICLE),
				"\" on site ID \"", finalStructure.getGroupId(), "\""),
			() -> _updateStructure(
				externalReferenceCode,
				PortalUtil.getClassNameId(_CLASS_NAME_JOURNAL_ARTICLE),
				finalStructure.getGroupId(), finalStructure.getName(),
				structure2));
	}

	@Test
	public void testUpdateStructureWithReferencedDataProviderInstance()
		throws Exception {

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		List<DDMFormField> ddmFormFields = new ArrayList<>();

		DDMFormField ddmFormField1 = new DDMFormField("Field1", "select");

		ddmFormField1.setDataType("string");

		long ddmDataProviderInstanceId1 = RandomTestUtil.randomLong();

		ddmFormField1.setProperty("dataSourceType", "data-provider");
		ddmFormField1.setProperty(
			"ddmDataProviderInstanceId", ddmDataProviderInstanceId1);

		ddmFormFields.add(ddmFormField1);

		DDMFormField ddmFormField2 = new DDMFormField("Field2", "select");

		ddmFormField2.setDataType("string");

		long ddmDataProviderInstanceId2 = RandomTestUtil.randomLong();

		ddmFormField2.setProperty("dataSourceType", "data-provider");
		ddmFormField2.setProperty(
			"ddmDataProviderInstanceId", ddmDataProviderInstanceId2);

		ddmFormFields.add(ddmFormField2);

		ddmForm.setDDMFormFields(ddmFormFields);

		DDMStructure structure = ddmStructureTestHelper.addStructure(
			ddmForm, StorageType.DEFAULT.getValue());

		List<DDMDataProviderInstanceLink> dataProviderInstanceLinks =
			_ddmDataProviderInstanceLinkLocalService.
				getDataProviderInstanceLinks(structure.getStructureId());

		Assert.assertEquals(
			dataProviderInstanceLinks.toString(), 2,
			dataProviderInstanceLinks.size());

		// Remove one of the data provider instance links

		ddmFormFields.remove(ddmFormField2);

		ddmForm.setDDMFormFields(ddmFormFields);

		ddmStructureTestHelper.updateStructure(
			structure.getStructureId(), ddmForm);

		dataProviderInstanceLinks =
			_ddmDataProviderInstanceLinkLocalService.
				getDataProviderInstanceLinks(structure.getStructureId());

		Assert.assertEquals(
			dataProviderInstanceLinks.toString(), 1,
			dataProviderInstanceLinks.size());

		_ddmStructureLocalService.deleteStructure(structure);

		dataProviderInstanceLinks =
			_ddmDataProviderInstanceLinkLocalService.
				getDataProviderInstanceLinks(structure.getStructureId());

		Assert.assertEquals(
			dataProviderInstanceLinks.toString(), 0,
			dataProviderInstanceLinks.size());
	}

	@Test
	public void testUpdateStructureWithReferencedDataProviderInstance2()
		throws Exception {

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm("Field1");

		DDMDataProviderInstance dataProviderInstance1 =
			createDDMDataProviderInstance();

		DDMDataProviderInstance dataProviderInstance2 =
			createDDMDataProviderInstance();

		List<String> actions = new ArrayList<>();

		String action1 = String.format(
			"call('%s','','')", dataProviderInstance1.getUuid());

		String action2 = String.format(
			"call('%s','','')", dataProviderInstance2.getUuid());

		actions.add(action1);
		actions.add(action2);

		DDMFormRule ddmFormRule1 = new DDMFormRule(actions, "TRUE");

		ddmForm.addDDMFormRule(ddmFormRule1);

		actions = new ArrayList<>();

		actions.add(action1);

		DDMFormRule ddmFormRule2 = new DDMFormRule(actions, "FALSE");

		ddmForm.addDDMFormRule(ddmFormRule2);

		DDMStructure structure = ddmStructureTestHelper.addStructure(
			ddmForm, StorageType.DEFAULT.getValue());

		List<DDMDataProviderInstanceLink> dataProviderInstanceLinks =
			_ddmDataProviderInstanceLinkLocalService.
				getDataProviderInstanceLinks(structure.getStructureId());

		Assert.assertEquals(
			dataProviderInstanceLinks.toString(), 2,
			dataProviderInstanceLinks.size());

		// Remove one of the data provider instance links

		List<DDMFormRule> ddmFormRules = ddmForm.getDDMFormRules();

		ddmFormRules.remove(ddmFormRule1);

		ddmForm.setDDMFormRules(ddmFormRules);

		ddmStructureTestHelper.updateStructure(
			structure.getStructureId(), ddmForm);

		dataProviderInstanceLinks =
			_ddmDataProviderInstanceLinkLocalService.
				getDataProviderInstanceLinks(structure.getStructureId());

		Assert.assertEquals(
			dataProviderInstanceLinks.toString(), 1,
			dataProviderInstanceLinks.size());

		_ddmStructureLocalService.deleteStructure(structure);

		dataProviderInstanceLinks =
			_ddmDataProviderInstanceLinkLocalService.
				getDataProviderInstanceLinks(structure.getStructureId());

		Assert.assertEquals(
			dataProviderInstanceLinks.toString(), 0,
			dataProviderInstanceLinks.size());
	}

	@Test
	public void testValidateIndexTypePropertyDefaultValue() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm("Field1");

		DDMStructure structure = ddmStructureTestHelper.addStructure(
			ddmForm, StorageType.DEFAULT.getValue());

		DDMStructure structureAfterUpdate = updateStructure(structure);

		DDMForm ddmFormAfterUpdate = structureAfterUpdate.getDDMForm();

		List<DDMFormField> ddmFormField = ddmFormAfterUpdate.getDDMFormFields();

		DDMFormField textField = ddmFormField.get(0);

		Assert.assertEquals(StringPool.BLANK, textField.getIndexType());
	}

	@Test
	public void testValidateIndexTypePropertyValue1() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm("Field1");

		DDMFormTestUtil.setIndexTypeProperty(ddmForm, "text");

		DDMStructure structure = ddmStructureTestHelper.addStructure(
			ddmForm, StorageType.DEFAULT.getValue());

		DDMStructure structureAfterUpdate = updateStructure(structure);

		DDMForm ddmFormAfterUpdate = structureAfterUpdate.getDDMForm();

		List<DDMFormField> ddmFormFieldAfterUpdate =
			ddmFormAfterUpdate.getDDMFormFields();

		DDMFormField textFieldAfterUpdate = ddmFormFieldAfterUpdate.get(0);

		Assert.assertEquals("text", textFieldAfterUpdate.getIndexType());
	}

	@Test
	public void testValidateIndexTypePropertyValue2() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm("Field1");

		DDMFormTestUtil.setIndexTypeProperty(ddmForm, "none");

		DDMStructure structure = ddmStructureTestHelper.addStructure(
			ddmForm, StorageType.DEFAULT.getValue());

		DDMStructure structureAfterUpdate = updateStructure(structure);

		DDMForm ddmFormAfterUpdate = structureAfterUpdate.getDDMForm();

		List<DDMFormField> ddmFormFieldAfterUpdate =
			ddmFormAfterUpdate.getDDMFormFields();

		DDMFormField textFieldAfterUpdate = ddmFormFieldAfterUpdate.get(0);

		Assert.assertEquals("none", textFieldAfterUpdate.getIndexType());
	}

	@Test(expected = InvalidParentStructureException.class)
	public void testValidateParentStructure() throws Exception {
		DDMStructure structure1 = addStructure(
			0, _classNameId, null, "Test Structure 1", null,
			read("ddm-structure-text-field.xsd"),
			StorageType.DEFAULT.getValue(), DDMStructureConstants.TYPE_DEFAULT);

		DDMStructure structure2 = addStructure(
			structure1.getStructureId(), _classNameId, null, "Test Structure 2",
			null, read("ddm-structure-radio-field.xsd"),
			StorageType.DEFAULT.getValue(), DDMStructureConstants.TYPE_DEFAULT);

		DDMStructure structure3 = addStructure(
			structure2.getStructureId(), _classNameId, null, "Test Structure 3",
			null, read("ddm-structure-select-field.xsd"),
			StorageType.DEFAULT.getValue(), DDMStructureConstants.TYPE_DEFAULT);

		structure1.setParentStructureId(structure3.getStructureId());

		updateStructure(structure1);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected DDMStructure copyStructure(DDMStructure structure)
		throws Exception {

		return _ddmStructureLocalService.copyStructure(
			structure.getUserId(), structure.getStructureId(),
			structure.getNameMap(), structure.getDescriptionMap(),
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));
	}

	protected DDMDataProviderInstance createDDMDataProviderInstance()
		throws Exception {

		Map<Locale, String> nameMap = HashMapBuilder.put(
			LocaleUtil.getSiteDefault(), StringUtil.randomString()
		).build();

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm("dataProviderName");

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		return _ddmDataProviderInstanceLocalService.addDataProviderInstance(
			TestPropsValues.getUserId(), group.getGroupId(), nameMap, nameMap,
			ddmFormValues, "rest",
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId()));
	}

	protected String getStructureName(DDMStructure structure) {
		return structure.getName(group.getDefaultLanguageId());
	}

	protected DDMStructure updateStructure(DDMStructure structure)
		throws Exception {

		return _updateStructure(
			structure, structure.getName(LocaleUtil.getSiteDefault()));
	}

	@Inject
	protected static PermissionCheckerFactory permissionCheckerFactory;

	private DDMStructure _addStructure(String name) throws Exception {
		return _addStructure(null, name);
	}

	private DDMStructure _addStructure(
			String externalReferenceCode, long classNameId, long groupId,
			String name, long userId)
		throws Exception {

		DDMForm ddmForm = DDMStructureTestUtil.getSampleDDMForm();

		DDMFormLayout ddmFormLayout = DDMUtil.getDefaultDDMFormLayout(ddmForm);

		return _ddmStructureLocalService.addStructure(
			externalReferenceCode, userId, groupId, 0, classNameId, null,
			Collections.singletonMap(LocaleUtil.getSiteDefault(), name), null,
			ddmForm, ddmFormLayout, StorageType.DEFAULT.toString(),
			DDMStructureConstants.TYPE_DEFAULT,
			ServiceContextTestUtil.getServiceContext(groupId, userId));
	}

	private DDMStructure _addStructure(
			String externalReferenceCode, String name)
		throws Exception {

		return _addStructure(
			externalReferenceCode, _classNameId, group.getGroupId(), name,
			TestPropsValues.getUserId());
	}

	private DDMStructure _updateStructure(DDMStructure structure, String name)
		throws Exception {

		return _ddmStructureLocalService.updateStructure(
			structure.getUserId(), structure.getStructureId(),
			structure.getParentStructureId(),
			Collections.singletonMap(LocaleUtil.getSiteDefault(), name),
			structure.getDescriptionMap(), structure.getDDMForm(),
			structure.getDDMFormLayout(),
			ServiceContextTestUtil.getServiceContext(structure.getGroupId()));
	}

	private DDMStructure _updateStructure(
			String externalReferenceCode, long classNameId, long groupId,
			String name, DDMStructure structure)
		throws Exception {

		return _ddmStructureLocalService.updateStructure(
			externalReferenceCode, structure.getUserId(), groupId,
			structure.getStructureId(), structure.getParentStructureId(),
			classNameId,
			Collections.singletonMap(LocaleUtil.getSiteDefault(), name),
			structure.getDescriptionMap(), structure.getDDMForm(),
			structure.getDDMFormLayout(),
			ServiceContextTestUtil.getServiceContext(groupId));
	}

	private static final String _CLASS_NAME_DL_FILE_ENTRY_METADATA =
		"com.liferay.document.library.kernel.model.DLFileEntryMetadata";

	private static final String _CLASS_NAME_JOURNAL_ARTICLE =
		"com.liferay.journal.model.JournalArticle";

	private static long _classNameId;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DDMDataProviderInstanceLinkLocalService
		_ddmDataProviderInstanceLinkLocalService;

	@Inject
	private DDMDataProviderInstanceLocalService
		_ddmDataProviderInstanceLocalService;

	@Inject
	private DDMStructureLayoutLocalService _ddmStructureLayoutLocalService;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@Inject
	private DDMStructureVersionLocalService _ddmStructureVersionLocalService;

	private final JSONFactory _jsonFactory = new JSONFactoryImpl();

	@Inject
	private UserLocalService _userLocalService;

}