/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.data.engine.rest.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.dto.v2_0.DataDefinitionField;
import com.liferay.data.engine.rest.dto.v2_0.DataLayout;
import com.liferay.data.engine.rest.test.util.DataDefinitionTestUtil;
import com.liferay.dynamic.data.mapping.model.DDMField;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMFieldLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.journal.configuration.JournalServiceConfiguration;
import com.liferay.journal.constants.JournalArticleConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mariano Álvaro Sáiz
 */
@RunWith(Arquillian.class)
public class ImportAndOverrideDataDefinitionMVCActionCommandTest
	extends BaseDataDefinitionMVCActionCommandTestCase {

	public MVCActionCommand getMVCActionCommand() {
		return _mvcActionCommand;
	}

	@Test
	@TestInfo("LPD-113664")
	public void testProcessAction() throws Exception {
		_testProcessActionWithAddedTextField();
		_testProcessActionWithModifiedDataDefinitionKey();
		_testProcessActionWithRepeatableTextField();
		_testProcessActionWithValidFields();
	}

	private void _assertDDMFormFieldReferences(
			String[] expectedDDMFormFieldReference, long ddmStructureId)
		throws Exception {

		DDMStructure ddmStructure = _ddmStructureLocalService.getStructure(
			ddmStructureId);

		List<DDMFormField> ddmFormFields = ddmStructure.getDDMFormFields(false);

		Assert.assertEquals(
			ddmFormFields.toString(), expectedDDMFormFieldReference.length,
			ddmFormFields.size());

		for (int i = 0; i < expectedDDMFormFieldReference.length; i++) {
			DDMFormField formField = ddmFormFields.get(i);

			Assert.assertEquals(
				expectedDDMFormFieldReference[i],
				formField.getFieldReference());
		}
	}

	private void _assertJournalArticleContent(long resourcePrimKey)
		throws Exception {

		JournalArticle journalArticle =
			_journalArticleLocalService.getLatestArticle(resourcePrimKey);

		DDMFormValues ddmFormValues = journalArticle.getDDMFormValues();

		ddmFormValues.getDDMFormFieldValuesMap(true);

		String journalArticleContent = journalArticle.getContent();

		Assert.assertTrue(journalArticleContent.contains("DDD"));
	}

	private void _processAction(
			Long dataDefinitionId, String fileName, boolean hasErrorMessage,
			String name)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			createMockLiferayPortletActionRequest(
				fileName, name, dataDefinitionId);

		setUpUploadPortletRequest(mockLiferayPortletActionRequest);

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		if (!hasErrorMessage) {
			Assert.assertNull(
				SessionMessages.get(
					mockLiferayPortletActionRequest,
					portal.getPortletId(mockLiferayPortletActionRequest) +
						SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE));
			Assert.assertNull(
				SessionErrors.get(
					mockLiferayPortletActionRequest,
					"importDataDefinitionErrorMessage"));
		}
		else {
			Assert.assertNotNull(
				SessionMessages.get(
					mockLiferayPortletActionRequest,
					portal.getPortletId(mockLiferayPortletActionRequest) +
						SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE));
			Assert.assertNotNull(
				SessionErrors.get(
					mockLiferayPortletActionRequest,
					"importDataDefinitionErrorMessage"));
		}
	}

	private String _read(String fileName) throws Exception {
		return new String(
			FileUtil.getBytes(getClass(), "dependencies/" + fileName));
	}

	private void _testProcessActionWithAddedTextField() throws Exception {
		DataDefinition dataDefinition =
			DataDefinitionTestUtil.addDataDefinition(
				"journal", dataDefinitionResourceFactory, group.getGroupId(),
				_read("data_definition_with_text_field.json"),
				TestPropsValues.getUser());

		JournalArticle journalArticle =
			JournalTestUtil.addArticleWithXMLContent(
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				JournalArticleConstants.CLASS_NAME_ID_DEFAULT, 0,
				_read("journal_article_content_with_text_field.xml"),
				dataDefinition.getDataDefinitionKey(), null, LocaleUtil.US,
				null,
				ServiceContextTestUtil.getServiceContext(
					group.getCompanyId(), group.getGroupId(),
					TestPropsValues.getUserId()));

		_assertDDMFormFieldReferences(
			new String[] {"Text1"}, journalArticle.getDDMStructureId());
		_assertJournalArticleContent(journalArticle.getResourcePrimKey());

		_processAction(
			dataDefinition.getId(), "data_definition_with_two_text_fields.json",
			false, "Simple");

		_assertDDMFormFieldReferences(
			new String[] {"Text1", "Text2"},
			journalArticle.getDDMStructureId());
		_assertJournalArticleContent(journalArticle.getResourcePrimKey());
	}

	private void _testProcessActionWithModifiedDataDefinitionKey()
		throws Exception {

		DataDefinition dataDefinition =
			DataDefinitionTestUtil.addDataDefinition(
				"journal", dataDefinitionResourceFactory, group.getGroupId(),
				_read("data_definition_with_text_field.json"),
				TestPropsValues.getUser());

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						JournalServiceConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"autogenerateDDMStructureKey", true
						).build());
			LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.OFF)) {

			_processAction(
				dataDefinition.getId(),
				"data_definition_with_valid_fields.json", true, "Simple");
		}

		DDMStructure ddmStructure = _ddmStructureLocalService.getStructure(
			dataDefinition.getId());

		Assert.assertEquals(
			dataDefinition.getDataDefinitionKey(),
			ddmStructure.getStructureKey());

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						JournalServiceConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"autogenerateDDMStructureKey", false
						).build())) {

			_processAction(
				dataDefinition.getId(),
				"data_definition_with_valid_fields.json", false, "Simple");
		}

		ddmStructure = _ddmStructureLocalService.getStructure(
			dataDefinition.getId());

		Assert.assertEquals(
			"VALID-DATA-DEFINITION", ddmStructure.getStructureKey());
	}

	private void _testProcessActionWithRepeatableTextField() throws Exception {
		DataDefinition dataDefinition =
			DataDefinitionTestUtil.addDataDefinition(
				"journal", dataDefinitionResourceFactory, group.getGroupId(),
				_read("data_definition_with_repeatable_text_field.json"),
				TestPropsValues.getUser());

		JournalArticle journalArticle1 =
			JournalTestUtil.addArticleWithXMLContent(
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				JournalArticleConstants.CLASS_NAME_ID_DEFAULT, 0,
				_read("journal_article_content_with_repeatable_content.xml"),
				dataDefinition.getDataDefinitionKey(), null, LocaleUtil.SPAIN,
				null,
				ServiceContextTestUtil.getServiceContext(
					group.getCompanyId(), group.getGroupId(),
					TestPropsValues.getUserId()));

		List<DDMField> ddmFields = _ddmFieldLocalService.getDDMFields(
			journalArticle1.getId(), "CopyOfCajaDeTexto9fap");

		Assert.assertEquals(ddmFields.toString(), 2, ddmFields.size());

		_processAction(
			dataDefinition.getId(),
			"data_definition_with_repeatable_text_field.json", false, "Simple");

		JournalArticle journalArticle2 = _journalArticleLocalService.getArticle(
			journalArticle1.getId());

		ddmFields = _ddmFieldLocalService.getDDMFields(
			journalArticle1.getId(), "CopyOfCajaDeTexto9fap");

		Assert.assertEquals(ddmFields.toString(), 2, ddmFields.size());

		String journalArticleContent = journalArticle2.getContent();

		Assert.assertTrue(journalArticleContent.contains("CCC1"));
		Assert.assertTrue(journalArticleContent.contains("CCC2"));
	}

	private void _testProcessActionWithValidFields() throws Exception {
		DataDefinition dataDefinition =
			DataDefinitionTestUtil.addDataDefinition(
				"journal", dataDefinitionResourceFactory, group.getGroupId(),
				_read("data_definition_with_text_field.json"),
				TestPropsValues.getUser());

		_processAction(
			dataDefinition.getId(), "data_definition_with_text_field.json",
			false, "Imported Structure");

		dataDefinition = getImportedDataDefinition();

		DataDefinitionField[] dataDefinitionFields =
			dataDefinition.getDataDefinitionFields();

		String previousTextFieldName = "Text1";

		Assert.assertTrue(
			StringUtil.startsWith(
				dataDefinitionFields[0].getName(), previousTextFieldName));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.OFF)) {

			_processAction(
				dataDefinition.getId(),
				"data_definition_with_invalid_fields.json", true,
				"Imported Structure");
		}

		DataLayout previousDataLayout = dataDefinition.getDefaultDataLayout();

		dataDefinition = getImportedDataDefinition();

		Assert.assertEquals(
			dataDefinition.getDefaultDataLayout(), previousDataLayout);
	}

	private static final String _CLASS_NAME =
		"com.liferay.journal.web.internal.portlet.action." +
			"ImportAndOverrideDataDefinitionMVCActionCommand";

	@Inject
	private DDMFieldLocalService _ddmFieldLocalService;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject(
		filter = "mvc.command.name=/journal/import_and_override_data_definition"
	)
	private MVCActionCommand _mvcActionCommand;

}