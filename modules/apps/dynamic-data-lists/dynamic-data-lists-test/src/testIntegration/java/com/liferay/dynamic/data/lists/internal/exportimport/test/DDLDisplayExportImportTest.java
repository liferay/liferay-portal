/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.internal.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.lists.constants.DDLPortletKeys;
import com.liferay.dynamic.data.lists.helper.DDLRecordSetTestHelper;
import com.liferay.dynamic.data.lists.helper.DDLRecordTestHelper;
import com.liferay.dynamic.data.lists.model.DDLRecord;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.service.DDLRecordLocalServiceUtil;
import com.liferay.dynamic.data.lists.service.DDLRecordSetLocalServiceUtil;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.exportimport.test.util.lar.BasePortletExportImportTestCase;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.PortletPreferences;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tamas Molnar
 */
@RunWith(Arquillian.class)
public class DDLDisplayExportImportTest
	extends BasePortletExportImportTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Override
	public String getPortletId() {
		return DDLPortletKeys.DYNAMIC_DATA_LISTS_DISPLAY;
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		UserTestUtil.setUser(TestPropsValues.getUser());

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			group.getGroupId(), DDLRecordSet.class.getName());

		_ddlRecordSetTestHelper = new DDLRecordSetTestHelper(group);

		DDLRecordSet recordSet = _ddlRecordSetTestHelper.addRecordSet(
			ddmStructure);

		_ddlRecordTestHelper = new DDLRecordTestHelper(group, recordSet);
	}

	@Test
	public void testExportImport() throws Exception {
		DDLRecord record = _ddlRecordTestHelper.addRecord();

		DDLRecordSet recordSet = record.getRecordSet();

		PortletPreferences importedPortletPreferences =
			getImportedPortletPreferences(
				HashMapBuilder.put(
					"groupId",
					new String[] {String.valueOf(recordSet.getGroupId())}
				).put(
					"recordSetId",
					new String[] {String.valueOf(recordSet.getRecordSetId())}
				).put(
					"recordSetKey",
					new String[] {String.valueOf(recordSet.getRecordSetKey())}
				).build(),
				false);

		DDLRecord importedRecord =
			DDLRecordLocalServiceUtil.fetchDDLRecordByUuidAndGroupId(
				record.getUuid(), importedGroup.getGroupId());

		Assert.assertNotNull(importedRecord);

		DDLRecordSet importedRecordSet =
			DDLRecordSetLocalServiceUtil.fetchDDLRecordSetByUuidAndGroupId(
				recordSet.getUuid(), importedGroup.getGroupId());

		Assert.assertNotNull(importedRecordSet);

		Assert.assertEquals(
			String.valueOf(importedRecordSet.getRecordSetId()),
			importedPortletPreferences.getValue(
				"recordSetId", StringPool.BLANK));
	}

	@Override
	@Test
	public void testExportImportAssetLinks() throws Exception {
	}

	private DDLRecordSetTestHelper _ddlRecordSetTestHelper;
	private DDLRecordTestHelper _ddlRecordTestHelper;

}