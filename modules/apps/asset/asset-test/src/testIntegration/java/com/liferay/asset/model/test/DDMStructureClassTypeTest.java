/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.model.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.ClassTypeField;
import com.liferay.asset.model.DDMStructureClassType;
import com.liferay.data.engine.rest.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.resource.v2_0.DataDefinitionResource;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Objects;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class DDMStructureClassTypeTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetClassTypeFields() throws Exception {
		_group = GroupTestUtil.addGroup();

		DataDefinitionResource.Builder dataDefinitionResourceBuilder =
			_dataDefinitionResourceFactory.create();

		DataDefinitionResource dataDefinitionResource =
			dataDefinitionResourceBuilder.user(
				TestPropsValues.getUser()
			).build();

		DataDefinition dataDefinition =
			dataDefinitionResource.postSiteDataDefinitionByContentType(
				_group.getGroupId(), "journal",
				DataDefinition.toDTO(
					StringUtil.read(
						DDMStructureClassTypeTest.class,
						"dependencies/data-definition.json")));

		DDMStructureClassType ddmStructureClassType = new DDMStructureClassType(
			dataDefinition.getId(), StringUtil.randomString(),
			_language.getLanguageId(LocaleUtil.US));

		Assert.assertTrue(
			ListUtil.exists(
				ddmStructureClassType.getClassTypeFields(),
				classTypeField -> Objects.equals(
					classTypeField.getType(), "date_time")));

		ClassTypeField classTypeField = ddmStructureClassType.getClassTypeField(
			"Text38954058");

		Assert.assertEquals(
			classTypeField.getLabel(),
			HtmlUtil.escape("<script>alert(document.cookie)</script>"));
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private DataDefinitionResource.Factory _dataDefinitionResourceFactory;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Language _language;

}