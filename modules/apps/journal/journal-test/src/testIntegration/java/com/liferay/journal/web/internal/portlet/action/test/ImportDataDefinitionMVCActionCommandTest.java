/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.data.engine.rest.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.dto.v2_0.DataDefinitionField;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rodrigo Paulino
 */
@RunWith(Arquillian.class)
public class ImportDataDefinitionMVCActionCommandTest
	extends BaseDataDefinitionMVCActionCommandTestCase {

	public MVCActionCommand getMVCActionCommand() {
		return _mvcActionCommand;
	}

	@Test
	public void testProcessActionWithDataDefinitionFromPreviousVersion()
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			createMockLiferayPortletActionRequest(
				"data_definition_with_text_field.json", "Imported Structure");

		setUpUploadPortletRequest(mockLiferayPortletActionRequest);

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		Assert.assertNull(
			SessionMessages.get(
				mockLiferayPortletActionRequest,
				portal.getPortletId(mockLiferayPortletActionRequest) +
					SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE));
		Assert.assertNull(
			SessionErrors.get(
				mockLiferayPortletActionRequest,
				"importDataDefinitionErrorMessage"));

		DataDefinition dataDefinition = getImportedDataDefinition();

		DataDefinitionField[] dataDefinitionFields =
			dataDefinition.getDataDefinitionFields();

		String previousTextFieldName = "Text1";

		Assert.assertNotEquals(
			previousTextFieldName, dataDefinitionFields[0].getName());
	}

	@Test
	public void testProcessActionWithFieldNamesWithoutRandomDigits()
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			createMockLiferayPortletActionRequest(
				"data_definition_with_field_names_without_random_digits.json",
				"Imported Structure");

		setUpUploadPortletRequest(mockLiferayPortletActionRequest);

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		Assert.assertNotNull(
			SessionMessages.get(
				mockLiferayPortletActionRequest,
				portal.getPortletId(mockLiferayPortletActionRequest) +
					SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_SUCCESS_MESSAGE));
		Assert.assertNotNull(
			SessionMessages.get(
				mockLiferayPortletActionRequest,
				"importDataDefinitionSuccessMessage"));
	}

	@Test
	public void testProcessActionWithInvalidDataDefinition() throws Exception {
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			createMockLiferayPortletActionRequest(
				"data_definition_with_invalid_fields.json",
				"Imported Structure");

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.journal.web.internal.portlet.action." +
					"ImportDataDefinitionMVCActionCommand",
				LoggerTestUtil.ERROR)) {

			setUpUploadPortletRequest(mockLiferayPortletActionRequest);

			_mvcActionCommand.processAction(
				mockLiferayPortletActionRequest,
				new MockLiferayPortletActionResponse());

			_assertFailure(mockLiferayPortletActionRequest);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(LoggerTestUtil.ERROR, logEntry.getPriority());

			Throwable throwable = logEntry.getThrowable();

			Assert.assertEquals(
				"The sum of all column sizes of a row must be less than the " +
					"maximum row size of 12",
				throwable.getMessage());
		}
	}

	@Test
	public void testProcessActionWithMultipleImportOfSameDataDefinition()
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			createMockLiferayPortletActionRequest(
				"data_definition_with_valid_fields.json", "Imported Structure");

		setUpUploadPortletRequest(mockLiferayPortletActionRequest);

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		Assert.assertNotNull(
			SessionMessages.get(
				mockLiferayPortletActionRequest,
				portal.getPortletId(mockLiferayPortletActionRequest) +
					SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_SUCCESS_MESSAGE));
		Assert.assertNotNull(
			SessionMessages.get(
				mockLiferayPortletActionRequest,
				"importDataDefinitionSuccessMessage"));

		mockLiferayPortletActionRequest = createMockLiferayPortletActionRequest(
			"data_definition_with_valid_fields.json", "Imported Structure");

		setUpUploadPortletRequest(mockLiferayPortletActionRequest);

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		Assert.assertNotNull(
			SessionMessages.get(
				mockLiferayPortletActionRequest,
				portal.getPortletId(mockLiferayPortletActionRequest) +
					SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_SUCCESS_MESSAGE));
		Assert.assertNotNull(
			SessionMessages.get(
				mockLiferayPortletActionRequest,
				"importDataDefinitionSuccessMessage"));
	}

	@Test
	public void testProcessActionWithoutName() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.journal.web.internal.portlet.action." +
					"ImportDataDefinitionMVCActionCommand",
				LoggerTestUtil.ERROR)) {

			MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
				createMockLiferayPortletActionRequest(
					"data_definition_with_valid_fields.json", null);

			setUpUploadPortletRequest(mockLiferayPortletActionRequest);

			_mvcActionCommand.processAction(
				mockLiferayPortletActionRequest,
				new MockLiferayPortletActionResponse());

			_assertFailure(mockLiferayPortletActionRequest);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(LoggerTestUtil.ERROR, logEntry.getPriority());

			Throwable throwable = logEntry.getThrowable();

			Assert.assertTrue(
				StringUtil.startsWith(
					throwable.getMessage(), "Name is null for locale"));
		}
	}

	@Test
	public void testProcessActionWithValidDataDefinitionAndName()
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			createMockLiferayPortletActionRequest(
				"data_definition_with_valid_fields.json", "Imported Structure");

		setUpUploadPortletRequest(mockLiferayPortletActionRequest);

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		Assert.assertNotNull(
			SessionMessages.get(
				mockLiferayPortletActionRequest,
				portal.getPortletId(mockLiferayPortletActionRequest) +
					SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_SUCCESS_MESSAGE));
		Assert.assertNotNull(
			SessionMessages.get(
				mockLiferayPortletActionRequest,
				"importDataDefinitionSuccessMessage"));

		DataDefinition dataDefinition = getImportedDataDefinition();

		DataDefinitionField[] dataDefinitionFields =
			dataDefinition.getDataDefinitionFields();

		Assert.assertEquals("Text32861154", dataDefinitionFields[0].getName());
	}

	private void _assertFailure(
		MockLiferayPortletActionRequest mockLiferayPortletActionRequest) {

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

	@Inject(filter = "mvc.command.name=/journal/import_data_definition")
	private MVCActionCommand _mvcActionCommand;

}