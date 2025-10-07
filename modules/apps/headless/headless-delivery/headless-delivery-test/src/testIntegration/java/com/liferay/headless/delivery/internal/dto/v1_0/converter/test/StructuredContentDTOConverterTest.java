/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.dto.v1_0.converter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.data.engine.rest.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.resource.v2_0.DataDefinitionResource;
import com.liferay.data.engine.rest.test.util.DataDefinitionTestUtil;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.headless.delivery.dto.v1_0.ContentField;
import com.liferay.headless.delivery.dto.v1_0.ContentFieldValue;
import com.liferay.headless.delivery.dto.v1_0.StructuredContent;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.RandomUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import java.io.InputStream;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alicia García
 */
@RunWith(Arquillian.class)
public class StructuredContentDTOConverterTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testToDTO() throws Exception {
		_testSelectedFromList(false, JSONUtil.putAll("one", "two", "three"));
		_testSelectedFromList(true, JSONUtil.putAll("one", "three", "two"));
	}

	private String _read(String fileName) throws Exception {
		Class<?> clazz = getClass();

		InputStream inputStream = clazz.getResourceAsStream(
			"dependencies/" + fileName);

		return StringUtil.read(inputStream);
	}

	private void _testSelectedFromList(
			boolean alphabeticalOrder, JSONArray expectedDataJSONArray)
		throws Exception {

		int nextInt = RandomUtil.nextInt(Integer.MAX_VALUE);

		String json = StringUtil.replace(
			StringUtil.replace(
				_read("test-data-definition-select-from-list.json"),
				"[$ALPHABETICAL_ORDER$]", String.valueOf(alphabeticalOrder)),
			"[$RANDOM_INT$]", String.valueOf(nextInt));

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(json);

		DataDefinition dataDefinition =
			DataDefinitionTestUtil.addDataDefinition(
				"journal", _dataDefinitionResourceFactory, _group.getGroupId(),
				jsonObject.toString(), TestPropsValues.getUser());

		DDMStructure ddmStructure =
			DDMStructureLocalServiceUtil.getDDMStructure(
				dataDefinition.getId());

		JournalArticle journalArticle =
			JournalTestUtil.addArticleWithXMLContent(
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				JournalArticleConstants.CLASS_NAME_ID_DEFAULT, 0,
				StringUtil.replace(
					_read("test-journal-article-select-from-list.xml"),
					"[$RANDOM_INT$]", String.valueOf(nextInt)),
				ddmStructure.getStructureKey(), null, LocaleUtil.US, null,
				ServiceContextTestUtil.getServiceContext(
					_group.getCompanyId(), _group.getGroupId(),
					TestPropsValues.getUserId()));

		StructuredContent structuredContent = _toDTO(journalArticle);

		ContentField[] contentFields = structuredContent.getContentFields();

		Assert.assertEquals(
			Arrays.toString(contentFields), 1, contentFields.length);

		ContentField contentField = contentFields[0];

		ContentFieldValue contentFieldValue =
			contentField.getContentFieldValue();

		Assert.assertTrue(
			JSONUtil.equals(
				expectedDataJSONArray,
				JSONFactoryUtil.createJSONArray(contentFieldValue.getData())));
	}

	private StructuredContent _toDTO(JournalArticle journalArticle)
		throws Exception {

		DTOConverter<JournalArticle, StructuredContent> dtoConverter =
			(DTOConverter<JournalArticle, StructuredContent>)
				_dtoConverterRegistry.getDTOConverter(
					JournalArticle.class.getName());

		DefaultDTOConverterContext dtoConverterContext =
			new DefaultDTOConverterContext(
				_dtoConverterRegistry, journalArticle.getResourcePrimKey(),
				LocaleUtil.getDefault(), null, null);

		return dtoConverter.toDTO(dtoConverterContext, journalArticle);
	}

	@Inject
	private DataDefinitionResource.Factory _dataDefinitionResourceFactory;

	@Inject
	private DTOConverterRegistry _dtoConverterRegistry;

	@DeleteAfterTestRun
	private Group _group;

}