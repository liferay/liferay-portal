/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action;

import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockActionRequest;
import com.liferay.portal.kernel.test.portlet.MockActionResponse;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletConfig;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Carolina Barbosa
 */
public class DDMFormConfigurationActionTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_setUpDDMFormInstanceLocalService();
		_setUpGroupLocalService();
	}

	@Test
	public void testProcessAction() throws Exception {
		Map<String, String[]> portletPreferencesMap = new HashMap<>();

		_ddmFormConfigurationAction.processAction(
			Mockito.mock(PortletConfig.class),
			_getMockActionRequest(portletPreferencesMap),
			new MockActionResponse());

		Assert.assertEquals(
			_DDM_STRUCTURE_EXTERNAL_REFERENCE_CODE,
			portletPreferencesMap.get("ddmStructureExternalReferenceCode")[0]);
		Assert.assertEquals(
			_GROUP_EXTERNAL_REFERENCE_CODE,
			portletPreferencesMap.get("groupExternalReferenceCode")[0]);
	}

	private MockActionRequest _getMockActionRequest(
		Map<String, String[]> portletPreferencesMap) {

		MockActionRequest mockActionRequest = new MockActionRequest();

		mockActionRequest.setAttribute(
			WebKeys.PORTLET_PREFERENCES_MAP, portletPreferencesMap);
		mockActionRequest.setParameter(
			"preferences--formInstanceId--",
			String.valueOf(_DDM_FORM_INSTANCE_ID));
		mockActionRequest.setParameter(
			"preferences--groupId--", String.valueOf(_GROUP_ID));

		return mockActionRequest;
	}

	private void _setUpDDMFormInstanceLocalService() throws Exception {
		DDMFormInstance ddmFormInstance = Mockito.mock(DDMFormInstance.class);

		DDMStructure ddmStructure = Mockito.mock(DDMStructure.class);

		Mockito.when(
			ddmStructure.getExternalReferenceCode()
		).thenReturn(
			_DDM_STRUCTURE_EXTERNAL_REFERENCE_CODE
		);

		Mockito.when(
			ddmFormInstance.getStructure()
		).thenReturn(
			ddmStructure
		);

		Mockito.when(
			_ddmFormInstanceLocalService.getDDMFormInstance(
				_DDM_FORM_INSTANCE_ID)
		).thenReturn(
			ddmFormInstance
		);

		ReflectionTestUtil.setFieldValue(
			_ddmFormConfigurationAction, "_ddmFormInstanceLocalService",
			_ddmFormInstanceLocalService);
	}

	private void _setUpGroupLocalService() throws Exception {
		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getExternalReferenceCode()
		).thenReturn(
			_GROUP_EXTERNAL_REFERENCE_CODE
		);

		Mockito.when(
			_groupLocalService.getGroup(_GROUP_ID)
		).thenReturn(
			group
		);

		ReflectionTestUtil.setFieldValue(
			_ddmFormConfigurationAction, "_groupLocalService",
			_groupLocalService);
	}

	private static final long _DDM_FORM_INSTANCE_ID =
		RandomTestUtil.randomLong();

	private static final String _DDM_STRUCTURE_EXTERNAL_REFERENCE_CODE =
		RandomTestUtil.randomString();

	private static final String _GROUP_EXTERNAL_REFERENCE_CODE =
		RandomTestUtil.randomString();

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private final DDMFormConfigurationAction _ddmFormConfigurationAction =
		new DDMFormConfigurationAction();
	private final DDMFormInstanceLocalService _ddmFormInstanceLocalService =
		Mockito.mock(DDMFormInstanceLocalService.class);
	private final GroupLocalService _groupLocalService = Mockito.mock(
		GroupLocalService.class);

}