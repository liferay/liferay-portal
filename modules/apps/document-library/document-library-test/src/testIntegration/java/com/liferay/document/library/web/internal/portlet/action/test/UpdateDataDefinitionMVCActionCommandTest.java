/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.data.engine.rest.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.dto.v2_0.DataDefinitionField;
import com.liferay.data.engine.rest.resource.exception.DataDefinitionValidationException;
import com.liferay.data.engine.rest.resource.v2_0.DataDefinitionResource;
import com.liferay.data.engine.rest.test.util.DataDefinitionTestUtil;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.Portlet;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
public class UpdateDataDefinitionMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_dataDefinitionJSON = new String(
			FileUtil.getBytes(getClass(), "dependencies/data_definition.json"));
		_group = GroupTestUtil.addGroup();

		_dataDefinition = DataDefinitionTestUtil.addDataDefinition(
			"document-library", _dataDefinitionResourceFactory,
			_group.getGroupId(), _dataDefinitionJSON,
			TestPropsValues.getUser());
	}

	@Test
	public void testProcessAction() throws Exception {
		_testProcessActionDataDefinitionValidationExceptionMustSetFields();
		_testProcessActionDataDefinitionValidationExceptionMustSetValidName();
	}

	private void _assertProcessAction(
			Class<?> exceptionClass, DataDefinition dataDefinition)
		throws Exception {

		dataDefinition.setDescription(
			HashMapBuilder.<String, Object>put(
				LocaleUtil.toLanguageId(LocaleUtil.US),
				RandomTestUtil.randomString()
			).build());

		DDMStructure ddmStructure = _ddmStructureLocalService.getStructure(
			_dataDefinition.getId());

		String description = ddmStructure.getDescription(LocaleUtil.US);

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			"dataDefinition", dataDefinition.toString());
		mockLiferayPortletActionRequest.addParameter(
			"dataDefinitionId", String.valueOf(_dataDefinition.getId()));
		mockLiferayPortletActionRequest.addParameter(
			"dataLayout",
			String.valueOf(_dataDefinition.getDefaultDataLayout()));
		mockLiferayPortletActionRequest.addParameter(
			"redirect", "/" + RandomTestUtil.randomString());
		mockLiferayPortletActionRequest.addParameter(
			ActionRequest.ACTION_NAME,
			"/document_library/update_data_definition");

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setUser(TestPropsValues.getUser());

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		RecordingMockLiferayPortletActionResponse
			recordingMockLiferayPortletActionResponse =
				new RecordingMockLiferayPortletActionResponse();

		_portlet.processAction(
			mockLiferayPortletActionRequest,
			recordingMockLiferayPortletActionResponse);

		Assert.assertTrue(
			SessionErrors.contains(
				mockLiferayPortletActionRequest, exceptionClass));
		Assert.assertTrue(
			SessionMessages.contains(
				mockLiferayPortletActionRequest,
				_portal.getPortletId(mockLiferayPortletActionRequest) +
					SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE));

		Assert.assertNull(
			recordingMockLiferayPortletActionResponse.getRedirect());

		ddmStructure = _ddmStructureLocalService.getStructure(
			_dataDefinition.getId());

		Assert.assertEquals(
			description, ddmStructure.getDescription(LocaleUtil.US));
	}

	private void _testProcessActionDataDefinitionValidationExceptionMustSetFields()
		throws Exception {

		DataDefinition dataDefinition = DataDefinition.toDTO(
			_dataDefinitionJSON);

		dataDefinition.setDataDefinitionFields(new DataDefinitionField[0]);

		_assertProcessAction(
			DataDefinitionValidationException.MustSetFields.class,
			dataDefinition);
	}

	private void _testProcessActionDataDefinitionValidationExceptionMustSetValidName()
		throws Exception {

		DataDefinition dataDefinition = DataDefinition.toDTO(
			_dataDefinitionJSON);

		dataDefinition.setName(Collections.emptyMap());

		_assertProcessAction(
			DataDefinitionValidationException.MustSetValidName.class,
			dataDefinition);
	}

	private DataDefinition _dataDefinition;
	private String _dataDefinitionJSON;

	@Inject
	private DataDefinitionResource.Factory _dataDefinitionResourceFactory;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Portal _portal;

	@Inject(
		filter = "jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY_ADMIN
	)
	private Portlet _portlet;

	private static class RecordingMockLiferayPortletActionResponse
		extends MockLiferayPortletActionResponse {

		public String getRedirect() {
			return _redirect;
		}

		@Override
		public Map<String, String[]> getRenderParameterMap() {
			return _renderParameterMap;
		}

		@Override
		public void sendRedirect(String location) {
			_redirect = location;
		}

		@Override
		public void setRenderParameter(String name, String... values) {
			_renderParameterMap.put(name, values);
		}

		private String _redirect;
		private final Map<String, String[]> _renderParameterMap =
			new HashMap<>();

	}

}