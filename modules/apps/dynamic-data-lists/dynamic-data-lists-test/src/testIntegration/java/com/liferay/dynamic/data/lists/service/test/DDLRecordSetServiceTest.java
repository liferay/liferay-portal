/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.lists.constants.DDLRecordSetConstants;
import com.liferay.dynamic.data.lists.helper.DDLRecordSetTestHelper;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.model.DDLRecordSetVersion;
import com.liferay.dynamic.data.lists.service.DDLRecordSetLocalServiceUtil;
import com.liferay.dynamic.data.lists.service.persistence.DDLRecordSetVersionUtil;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestHelper;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.TransactionTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rafael Praxedes
 */
@RunWith(Arquillian.class)
public class DDLRecordSetServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_ddmStructureTestHelper = new DDMStructureTestHelper(
			PortalUtil.getClassNameId(DDLRecordSet.class), _group);

		_ddlRecordSetTestHelper = new DDLRecordSetTestHelper(_group);
	}

	@Test
	public void testAddRecordSetCreatesRecordSetVersion() throws Exception {
		DDMForm ddmStructureDDMForm = DDMFormTestUtil.createDDMForm("Field");

		DDLRecordSet ddlRecordSet = addRecordSet(
			ddmStructureDDMForm, StorageType.DEFAULT.toString());

		Assert.assertEquals(
			DDLRecordSetConstants.VERSION_DEFAULT, ddlRecordSet.getVersion());

		DDLRecordSetVersion ddlRecordSetVersion =
			ddlRecordSet.getRecordSetVersion();

		Assert.assertEquals(
			DDLRecordSetConstants.VERSION_DEFAULT,
			ddlRecordSetVersion.getVersion());
	}

	@Test
	public void testAddRecordSetWithFailStorage() throws Exception {
		DDMForm ddmStructureDDMForm = DDMFormTestUtil.createDDMForm("Field");

		DDLRecordSet ddlRecordSet = addRecordSet(
			ddmStructureDDMForm, StorageType.DEFAULT.toString());

		DDMStructure ddmStructure = ddlRecordSet.getDDMStructure();

		Assert.assertEquals(
			ddmStructure.getStorageType(), StorageType.DEFAULT.toString());
	}

	@Test
	public void testDeleteRecordSetDeletesRecordSetVersions() throws Exception {
		DDMForm ddmStructureDDMForm = DDMFormTestUtil.createDDMForm("Field");

		DDLRecordSet ddlRecordSet = addRecordSet(
			ddmStructureDDMForm, StorageType.DEFAULT.toString());

		DDLRecordSetLocalServiceUtil.deleteRecordSet(
			ddlRecordSet.getRecordSetId());

		int actualCount = TransactionTestUtil.invoke(
			DDLRecordSetVersionUtil.getPersistence(),
			ddlRecordSetVersionPersistence ->
				ddlRecordSetVersionPersistence.countByRecordSetId(
					ddlRecordSet.getRecordSetId()));

		Assert.assertEquals(0, actualCount);
	}

	@Test
	public void testDraftVersionCombinedWithApprovedVersion() throws Exception {
		DDMForm ddmStructureDDMForm = DDMFormTestUtil.createDDMForm("Field");

		DDMStructure ddmStructure = _ddmStructureTestHelper.addStructure(
			ddmStructureDDMForm, StorageType.DEFAULT.toString());

		Map<Locale, String> nameMap = HashMapBuilder.put(
			LocaleUtil.US, RandomTestUtil.randomString()
		).build();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAttribute("status", WorkflowConstants.STATUS_DRAFT);

		DDLRecordSet recordSet = DDLRecordSetLocalServiceUtil.addRecordSet(
			TestPropsValues.getUserId(), _group.getGroupId(),
			ddmStructure.getStructureId(), null, nameMap, null,
			DDLRecordSetConstants.MIN_DISPLAY_ROWS_DEFAULT,
			DDLRecordSetConstants.SCOPE_FORMS, serviceContext);

		long recordSetId = recordSet.getRecordSetId();

		DDLRecordSetVersion recordSetVersion = recordSet.getRecordSetVersion();

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, recordSetVersion.getStatus());
		Assert.assertEquals(
			recordSet.getVersion(), recordSetVersion.getVersion());

		serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());

		recordSet = DDLRecordSetLocalServiceUtil.updateRecordSet(
			recordSetId, ddmStructure.getStructureId(), nameMap, null,
			DDLRecordSetConstants.MIN_DISPLAY_ROWS_DEFAULT, serviceContext);

		recordSetVersion = recordSet.getRecordSetVersion();

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, recordSetVersion.getStatus());
		Assert.assertEquals(
			recordSet.getVersion(), recordSetVersion.getVersion());

		List<DDLRecordSetVersion> ddlRecordSetVersions = _getRecordSetVersions(
			recordSetId);

		Assert.assertEquals(
			ddlRecordSetVersions.toString(), 2, ddlRecordSetVersions.size());

		serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());

		serviceContext.setAttribute("status", WorkflowConstants.STATUS_DRAFT);

		recordSet = DDLRecordSetLocalServiceUtil.updateRecordSet(
			recordSetId, ddmStructure.getStructureId(), nameMap, null,
			DDLRecordSetConstants.MIN_DISPLAY_ROWS_DEFAULT, serviceContext);

		recordSetVersion = recordSet.getRecordSetVersion();

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, recordSetVersion.getStatus());
		Assert.assertEquals(
			recordSet.getVersion(), recordSetVersion.getVersion());

		ddlRecordSetVersions = _getRecordSetVersions(recordSetId);

		Assert.assertEquals(
			ddlRecordSetVersions.toString(), 3, ddlRecordSetVersions.size());
	}

	@Test
	public void testUpdateDraftVersion() throws Exception {
		DDMForm ddmStructureDDMForm = DDMFormTestUtil.createDDMForm("Field");

		DDMStructure ddmStructure = _ddmStructureTestHelper.addStructure(
			ddmStructureDDMForm, StorageType.DEFAULT.toString());

		Map<Locale, String> nameMap = HashMapBuilder.put(
			LocaleUtil.US, RandomTestUtil.randomString()
		).build();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAttribute("status", WorkflowConstants.STATUS_DRAFT);

		DDLRecordSet recordSet = DDLRecordSetLocalServiceUtil.addRecordSet(
			TestPropsValues.getUserId(), _group.getGroupId(),
			ddmStructure.getStructureId(), null, nameMap, null,
			DDLRecordSetConstants.MIN_DISPLAY_ROWS_DEFAULT,
			DDLRecordSetConstants.SCOPE_FORMS, serviceContext);

		long recordSetId = recordSet.getRecordSetId();

		DDLRecordSetVersion recordSetVersion = recordSet.getRecordSetVersion();

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, recordSetVersion.getStatus());
		Assert.assertEquals(
			recordSet.getVersion(), recordSetVersion.getVersion());

		recordSet = DDLRecordSetLocalServiceUtil.updateRecordSet(
			recordSetId, ddmStructure.getStructureId(), nameMap, null,
			DDLRecordSetConstants.MIN_DISPLAY_ROWS_DEFAULT, serviceContext);

		recordSetVersion = recordSet.getRecordSetVersion();

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, recordSetVersion.getStatus());
		Assert.assertEquals(
			recordSet.getVersion(), recordSetVersion.getVersion());

		List<DDLRecordSetVersion> ddlRecordSetVersions = _getRecordSetVersions(
			recordSetId);

		Assert.assertEquals(
			ddlRecordSetVersions.toString(), 1, ddlRecordSetVersions.size());
	}

	@Test
	public void testUpdateRecordSetCreatesMajorRecordSetVersion()
		throws Exception {

		DDMForm ddmStructureDDMForm = DDMFormTestUtil.createDDMForm("Field");

		DDLRecordSet ddlRecordSet = addRecordSet(
			ddmStructureDDMForm, StorageType.DEFAULT.toString());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAttribute("majorVersion", Boolean.TRUE);

		DDLRecordSet updatedDDLRecordSet =
			DDLRecordSetLocalServiceUtil.updateRecordSet(
				ddlRecordSet.getRecordSetId(), ddlRecordSet.getDDMStructureId(),
				ddlRecordSet.getNameMap(), null,
				DDLRecordSetConstants.MIN_DISPLAY_ROWS_DEFAULT, serviceContext);

		Assert.assertEquals("2.0", updatedDDLRecordSet.getVersion());
	}

	@Test
	public void testUpdateRecordSetCreatesRecordSetVersion() throws Exception {
		DDMForm ddmStructureDDMForm = DDMFormTestUtil.createDDMForm("Field");

		DDLRecordSet ddlRecordSet = addRecordSet(
			ddmStructureDDMForm, StorageType.DEFAULT.toString());

		DDLRecordSet updatedDDLRecordSet =
			_ddlRecordSetTestHelper.updateRecordSet(
				ddlRecordSet.getRecordSetId(), ddlRecordSet.getDDMStructure());

		Assert.assertEquals("1.1", updatedDDLRecordSet.getVersion());

		DDLRecordSetVersion ddlRecordSetVersion =
			updatedDDLRecordSet.getRecordSetVersion();

		Assert.assertEquals(
			updatedDDLRecordSet.getVersion(), ddlRecordSetVersion.getVersion());
	}

	@Test
	public void testUpdateRecordSetWithFailStorage() throws Exception {
		DDMForm ddmStructureDDMForm = DDMFormTestUtil.createDDMForm("Field");

		DDLRecordSet ddlRecordSet = addRecordSet(
			ddmStructureDDMForm, StorageType.DEFAULT.toString());

		DDMStructure ddmStructure = ddlRecordSet.getDDMStructure();

		String storageAdpater = ddmStructure.getStorageType();

		DDMFormTestUtil.addTextDDMFormFields(ddmStructureDDMForm, "Name");

		ddlRecordSet = updateRecordSet(ddlRecordSet, ddmStructureDDMForm);

		ddmStructure = ddlRecordSet.getDDMStructure();

		Assert.assertEquals(storageAdpater, ddmStructure.getStorageType());
	}

	protected DDLRecordSet addRecordSet(
			DDMForm ddmStructureDDMForm, String storageType)
		throws Exception {

		DDMStructure ddmStructure = _ddmStructureTestHelper.addStructure(
			ddmStructureDDMForm, storageType);

		return _ddlRecordSetTestHelper.addRecordSet(ddmStructure);
	}

	protected DDLRecordSet updateRecordSet(
			DDLRecordSet ddlRecordSet, DDMForm ddmStructureDDMForm)
		throws Exception {

		DDMStructure ddmStructure = ddlRecordSet.getDDMStructure();

		ddmStructure = _ddmStructureTestHelper.updateStructure(
			ddmStructure.getStructureId(), ddmStructureDDMForm);

		return _ddlRecordSetTestHelper.updateRecordSet(
			ddlRecordSet.getRecordSetId(), ddmStructure);
	}

	private List<DDLRecordSetVersion> _getRecordSetVersions(long recordSetId)
		throws Exception {

		return TransactionTestUtil.invoke(
			DDLRecordSetVersionUtil.getPersistence(),
			ddlRecordSetVersionPersistence ->
				ddlRecordSetVersionPersistence.findByRecordSetId(recordSetId));
	}

	private DDLRecordSetTestHelper _ddlRecordSetTestHelper;
	private DDMStructureTestHelper _ddmStructureTestHelper;

	@DeleteAfterTestRun
	private Group _group;

}