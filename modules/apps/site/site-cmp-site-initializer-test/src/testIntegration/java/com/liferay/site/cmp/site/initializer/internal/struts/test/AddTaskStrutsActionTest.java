/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.struts.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cmp.site.initializer.test.util.CMPTestUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Carolina Barbosa
 */
@FeatureFlags(featureFlags = @FeatureFlag("LPD-58677"))
@RunWith(Arquillian.class)
public class AddTaskStrutsActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		CMPTestUtil.getOrAddGroup(AddTaskStrutsActionTest.class);

		_cmpProjectObjectEntry = CMPTestUtil.addCMPProjectObjectEntry();
		_cmpTaskObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMP_TASK", TestPropsValues.getCompanyId());
	}

	@Test
	public void testExecute() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay() {
			{
				setSiteDefaultLocale(LocaleUtil.US);
				setUser(TestPropsValues.getUser());
			}
		};

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setParameter(
			"objectDefinitionId",
			String.valueOf(_cmpTaskObjectDefinition.getObjectDefinitionId()));
		mockHttpServletRequest.setParameter(
			"projectGroupId",
			String.valueOf(_cmpProjectObjectEntry.getGroupId()));
		mockHttpServletRequest.setParameter(
			"projectId",
			String.valueOf(_cmpProjectObjectEntry.getObjectEntryId()));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_addTaskStrutsAction.execute(
			mockHttpServletRequest, mockHttpServletResponse);

		String objectEntryId = StringUtil.removeFirst(
			mockHttpServletResponse.getRedirectedUrl(),
			StringBundler.concat(
				themeDisplay.getPathFriendlyURLPublic(),
				GroupConstants.CMS_FRIENDLY_URL, "/e/edit-task/",
				_portal.getClassNameId(_cmpTaskObjectDefinition.getClassName()),
				StringPool.SLASH));

		ObjectEntry cmpTaskObjectEntry =
			_objectEntryLocalService.fetchObjectEntry(
				GetterUtil.getLong(objectEntryId));

		Assert.assertEquals(
			_cmpProjectObjectEntry.getGroupId(),
			cmpTaskObjectEntry.getGroupId());
		Assert.assertEquals(
			_cmpTaskObjectDefinition.getObjectDefinitionId(),
			cmpTaskObjectEntry.getObjectDefinitionId());
		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, cmpTaskObjectEntry.getStatus());
		Assert.assertEquals(
			_cmpProjectObjectEntry.getObjectEntryId(),
			MapUtil.getLong(
				cmpTaskObjectEntry.getValues(),
				"r_cmpProjectToCMPTasks_c_cmpProjectId"));
	}

	@Inject(filter = "path=/cms/add_task")
	private StrutsAction _addTaskStrutsAction;

	private ObjectEntry _cmpProjectObjectEntry;
	private ObjectDefinition _cmpTaskObjectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private Portal _portal;

}