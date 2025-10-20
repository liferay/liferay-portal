/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.upgrade.v5_5_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.model.DDMFieldAttribute;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.service.DDMFieldLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
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
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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
public class DDMFieldAttributeUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule() {
			{
				skipTestRule(CompanyProviderClassTestRule.INSTANCE);
			}
		};

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
		_addDDMFormValues();

		_assertCompanyId(CompanyConstants.SYSTEM);

		_runUpgrade();

		_assertCompanyId(_group.getCompanyId());
	}

	@Test
	public void testUpgradeProcessSeveralCompanies() throws Exception {
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

	private void _addDDMFormValues() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			RandomTestUtil.randomString());

		DDMStructure ddmStructure = _ddmStructureLocalService.addStructure(
			null, _group.getCreatorUserId(), _group.getGroupId(), 0,
			PortalUtil.getClassNameId(DDLRecordSet.class.getName()),
			"CUSTOM-META-TAGS", RandomTestUtil.randomLocaleStringMap(), null,
			ddmForm, _ddm.getDefaultDDMFormLayout(ddmForm),
			StorageType.DEFAULT.toString(), DDMStructureConstants.TYPE_DEFAULT,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		ddmFormValues.setDefaultLocale(LocaleUtil.ENGLISH);

		Set<Locale> availableLocales = new LinkedHashSet<>(
			Arrays.asList(
				LocaleUtil.CHINA, LocaleUtil.ENGLISH, LocaleUtil.SPAIN));

		ddmFormValues.setAvailableLocales(availableLocales);

		DDMFormFieldValue ddmFormFieldValue = new DDMFormFieldValue();

		ddmFormFieldValue.setName("field1");
		ddmFormFieldValue.setInstanceId(StringUtil.randomString(8));

		Value value = new LocalizedValue(LocaleUtil.ENGLISH);

		for (Locale locale : availableLocales) {
			value.addString(locale, LocaleUtil.toLanguageId(locale) + " value");
		}

		ddmFormFieldValue.setValue(value);

		ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);

		_ddmFieldLocalService.updateDDMFormValues(
			ddmStructure.getStructureId(), _STORAGE_ID, ddmFormValues);
	}

	private void _assertCompanyId(long companyId) {
		List<DDMFieldAttribute> ddmFieldAttributes;

		for (String ddmFieldAttributeName : _ddmFieldAttributeNames) {
			ddmFieldAttributes = _ddmFieldLocalService.getDDMFieldAttributes(
				_STORAGE_ID, ddmFieldAttributeName);

			for (DDMFieldAttribute ddmFieldAttribute : ddmFieldAttributes) {
				Assert.assertEquals(
					companyId, ddmFieldAttribute.getCompanyId());
			}
		}
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
			"DDMFieldAttributeUpgradeProcess";

	private static final long _STORAGE_ID = 0;

	private static final List<String> _ddmFieldAttributeNames = Arrays.asList(
		"availableLanguageIds", "defaultLanguageId");

	@Inject(
		filter = "(&(component.name=com.liferay.dynamic.data.mapping.internal.upgrade.registry.DDMServiceUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DDM _ddm;

	@Inject
	private DDMFieldLocalService _ddmFieldLocalService;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@Inject
	private EntityCache _entityCache;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private MultiVMPool _multiVMPool;

}