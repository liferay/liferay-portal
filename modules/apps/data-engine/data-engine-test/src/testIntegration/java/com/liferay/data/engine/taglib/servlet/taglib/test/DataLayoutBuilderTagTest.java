/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.taglib.servlet.taglib.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.data.engine.rest.dto.v2_0.DataLayout;
import com.liferay.data.engine.taglib.servlet.taglib.DataLayoutBuilderTag;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.util.DDM;
import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Locale;
import java.util.Set;

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
public class DataLayoutBuilderTagTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testDataLayoutDDMFormAdapterGetDDMForm() throws Exception {
		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), JournalArticle.class.getName(),
			DDMFormTestUtil.createDDMForm("Text"));

		String ddmFormJSON = _ddm.getDDMFormJSONString(
			ddmStructure.getDDMForm());

		DataLayout dataLayout = new DataLayout();

		dataLayout.setDataDefinitionId(ddmStructure.getStructureId());

		TestDataLayoutBuilderTag testDataLayoutBuilderTag =
			new TestDataLayoutBuilderTag();

		ReflectionTestUtil.invoke(
			testDataLayoutBuilderTag.createDataLayoutDDMFormAdapter(
				DDMFormTestUtil.createAvailableLocales(
					LocaleUtil.BRAZIL, LocaleUtil.US),
				dataLayout),
			"_getDDMForm", new Class<?>[0]);

		ddmStructure = DDMStructureLocalServiceUtil.getStructure(
			ddmStructure.getStructureId());

		Assert.assertEquals(
			ddmFormJSON, _ddm.getDDMFormJSONString(ddmStructure.getDDMForm()));
	}

	@Inject
	private DDM _ddm;

	@DeleteAfterTestRun
	private Group _group;

	private static class TestDataLayoutBuilderTag extends DataLayoutBuilderTag {

		public Object createDataLayoutDDMFormAdapter(
			Set<Locale> availableLocales, DataLayout dataLayout) {

			return new DataLayoutDDMFormAdapter(
				availableLocales, null, dataLayout, null, null);
		}

	}

}