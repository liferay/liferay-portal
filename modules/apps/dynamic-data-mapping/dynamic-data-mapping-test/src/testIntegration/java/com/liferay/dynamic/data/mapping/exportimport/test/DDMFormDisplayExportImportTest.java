/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalServiceUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormInstanceRecordTestUtil;
import com.liferay.exportimport.test.util.lar.BasePortletExportImportTestCase;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
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
 * @author Pedro Queiroz
 * @author Tamas Molnar
 */
@RunWith(Arquillian.class)
@Sync
public class DDMFormDisplayExportImportTest
	extends BasePortletExportImportTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			SynchronousDestinationTestRule.INSTANCE);

	@Override
	public String getPortletId() {
		return DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM;
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		UserTestUtil.setUser(TestPropsValues.getUser());
	}

	@Test
	public void testExportImport() throws Exception {
		DDMFormInstanceRecord ddmFormInstanceRecord =
			DDMFormInstanceRecordTestUtil.
				addDDMFormInstanceRecordWithRandomValues(
					group, TestPropsValues.getUserId());

		DDMFormInstanceRecord importedDDMFormInstanceRecord =
			DDMFormInstanceRecordLocalServiceUtil.
				fetchDDMFormInstanceRecordByUuidAndGroupId(
					ddmFormInstanceRecord.getUuid(),
					importedGroup.getGroupId());

		Assert.assertNull(importedDDMFormInstanceRecord);

		DDMFormInstance ddmFormInstance =
			ddmFormInstanceRecord.getFormInstance();

		PortletPreferences importedPortletPreferences =
			getImportedPortletPreferences(
				HashMapBuilder.put(
					"formInstanceId",
					new String[] {
						String.valueOf(ddmFormInstance.getFormInstanceId())
					}
				).put(
					"groupId",
					new String[] {String.valueOf(ddmFormInstance.getGroupId())}
				).build(),
				false);

		DDMFormInstance importedDDMFormInstance =
			DDMFormInstanceLocalServiceUtil.
				fetchDDMFormInstanceByUuidAndGroupId(
					ddmFormInstance.getUuid(), importedGroup.getGroupId());

		Assert.assertEquals(
			String.valueOf(importedDDMFormInstance.getFormInstanceId()),
			importedPortletPreferences.getValue(
				"formInstanceId", StringPool.BLANK));
		Assert.assertEquals(
			String.valueOf(importedDDMFormInstance.getGroupId()),
			importedPortletPreferences.getValue("groupId", StringPool.BLANK));
	}

	@Override
	@Test
	public void testExportImportAssetLinks() throws Exception {
	}

}