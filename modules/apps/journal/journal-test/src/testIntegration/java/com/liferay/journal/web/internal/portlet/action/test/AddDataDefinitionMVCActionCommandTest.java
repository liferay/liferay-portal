/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.data.engine.rest.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.resource.exception.DataDefinitionValidationException;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FileUtil;
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
public class AddDataDefinitionMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testProcessAction() throws Exception {
		_testProcessActionDataDefinitionValidationException();
	}

	private void _testProcessActionDataDefinitionValidationException()
		throws Exception {

		DataDefinition dataDefinition = DataDefinition.toDTO(
			new String(
				FileUtil.getBytes(
					getClass(),
					"dependencies/data_definition_with_text_field.json")));

		dataDefinition.setName(Collections.emptyMap());

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			"dataDefinition", dataDefinition.toString());
		mockLiferayPortletActionRequest.addParameter(
			"groupId", String.valueOf(_group.getGroupId()));
		mockLiferayPortletActionRequest.addParameter(
			"redirect", "/" + RandomTestUtil.randomString());
		mockLiferayPortletActionRequest.addParameter(
			"structureKey", RandomTestUtil.randomString());
		mockLiferayPortletActionRequest.addParameter(
			ActionRequest.ACTION_NAME, "/journal/add_data_definition");

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
				mockLiferayPortletActionRequest,
				DataDefinitionValidationException.MustSetValidName.class));
		Assert.assertNull(
			recordingMockLiferayPortletActionResponse.getRedirect());
		Assert.assertEquals(
			0,
			_ddmStructureLocalService.getStructuresCount(_group.getGroupId()));
	}

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(filter = "jakarta.portlet.name=" + JournalPortletKeys.JOURNAL)
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