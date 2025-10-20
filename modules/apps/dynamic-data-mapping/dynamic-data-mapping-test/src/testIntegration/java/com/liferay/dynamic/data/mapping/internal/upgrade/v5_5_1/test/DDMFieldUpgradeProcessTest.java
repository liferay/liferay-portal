/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.upgrade.v5_5_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMField;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.service.DDMFieldLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureVersionLocalService;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.util.DDM;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CompanyProviderClassTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alicia García
 */
@RunWith(Arquillian.class)
public class DDMFieldUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule() {
				{
					skipTestRule(CompanyProviderClassTestRule.INSTANCE);
				}
			},
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@After
	public void tearDown() throws Exception {
		_ddmFieldLocalService.deleteDDMFormValues(_STORAGE_ID);
	}

	@Test
	public void testUpgradeProcess() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			RandomTestUtil.randomString());

		DDMStructure ddmStructure = _ddmStructureLocalService.addStructure(
			null, _group.getCreatorUserId(), _group.getGroupId(), 0,
			PortalUtil.getClassNameId(DDLRecordSet.class.getName()),
			"CUSTOM-META-TAGS", RandomTestUtil.randomLocaleStringMap(), null,
			ddmForm, _ddm.getDefaultDDMFormLayout(ddmForm),
			StorageType.DEFAULT.toString(), DDMStructureConstants.TYPE_DEFAULT,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		List<DDMStructureVersion> ddmStructureVersions =
			_ddmStructureVersionLocalService.getStructureVersions(
				ddmStructure.getStructureId());

		DDMStructureVersion ddmStructureVersion = ddmStructureVersions.get(0);

		DDMField ddmField = _addDDMField(
			ddmStructureVersion.getStructureVersionId());

		Assert.assertEquals(CompanyConstants.SYSTEM, ddmField.getCompanyId());

		_runUpgrade();

		ddmField = _ddmFieldLocalService.getDDMField(ddmField.getFieldId());

		Assert.assertEquals(
			ddmStructureVersion.getCompanyId(), ddmField.getCompanyId());
	}

	@Test
	public void testUpgradeProcessManyCompanies() throws Exception {
		Company company = CompanyTestUtil.addCompany();

		try {
			testUpgradeProcess();
		}
		finally {
			_companyLocalService.deleteCompany(company);
		}
	}

	@Test
	public void testUpgradeProcessWithDatabasePartitionEnabled()
		throws Exception {

		Assume.assumeTrue(PropsValues.DATABASE_PARTITION_ENABLED);

		testUpgradeProcess();
	}

	private DDMField _addDDMField(long structureVersionId) {
		DDMField ddmField = _ddmFieldLocalService.createDDMField(
			_counterLocalService.increment());

		ddmField.setParentFieldId(0);
		ddmField.setStorageId(_STORAGE_ID);
		ddmField.setStructureVersionId(structureVersionId);
		ddmField.setFieldName(RandomTestUtil.randomString());
		ddmField.setFieldType(DDMFormFieldTypeConstants.TEXT);
		ddmField.setInstanceId(RandomTestUtil.randomString(8));
		ddmField.setLocalizable(false);
		ddmField.setPriority(RandomTestUtil.randomInt());

		return _ddmFieldLocalService.addDDMField(ddmField);
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();

		_entityCache.clearCache();
		_multiVMPool.clear();
	}

	private static final String _CLASS_NAME =
		"com.liferay.dynamic.data.mapping.internal.upgrade.v5_5_1." +
			"DDMFieldUpgradeProcess";

	private static final long _STORAGE_ID = 0;

	@Inject(
		filter = "(&(component.name=com.liferay.dynamic.data.mapping.internal.upgrade.registry.DDMServiceUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private CounterLocalService _counterLocalService;

	@Inject
	private DDM _ddm;

	@Inject
	private DDMFieldLocalService _ddmFieldLocalService;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@Inject
	private DDMStructureVersionLocalService _ddmStructureVersionLocalService;

	@Inject
	private EntityCache _entityCache;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private MultiVMPool _multiVMPool;

}