/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.dynamic.data.mapping.util.DDM;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class DDMTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testGetDDMFormValues() throws Exception {
		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), DDLRecordSet.class.getName(),
			DDMFormTestUtil.createDDMForm("Text1", "Text2"));

		DDMTemplate ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_group.getGroupId(), ddmStructure.getStructureId(),
			PortalUtil.getClassNameId(DDLRecordSet.class), "json",
			_ddm.getDDMFormJSONString(DDMFormTestUtil.createDDMForm("Text1")),
			LocaleUtil.US);

		String ddmFormJSON = _ddm.getDDMFormJSONString(
			ddmStructure.getDDMForm());

		_ddm.getDDMFormValues(
			ddmStructure.getStructureId(), ddmTemplate.getTemplateId(),
			StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		ddmStructure = DDMStructureLocalServiceUtil.getStructure(
			ddmStructure.getStructureId());

		Assert.assertEquals(
			ddmFormJSON, _ddm.getDDMFormJSONString(ddmStructure.getDDMForm()));
	}

	@Inject
	private DDM _ddm;

	@DeleteAfterTestRun
	private Group _group;

}