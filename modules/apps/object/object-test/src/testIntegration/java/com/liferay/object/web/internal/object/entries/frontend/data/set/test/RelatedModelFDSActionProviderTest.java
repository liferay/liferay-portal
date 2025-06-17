/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.entries.frontend.data.set.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.frontend.data.set.provider.FDSActionProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectPortletKeys;
import com.liferay.object.entries.frontend.data.set.data.model.RelatedModel;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Feliphe Marinho
 */
@RunWith(Arquillian.class)
public class RelatedModelFDSActionProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testGetDropdownItems() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			TestPropsValues.getUserId(), 0,
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null, Collections.emptyMap(),
			ServiceContextTestUtil.getServiceContext());

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), objectDefinition.getClassName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), role.getRoleId(),
			new String[] {ActionKeys.UPDATE});

		User user = UserTestUtil.addUser();

		_userLocalService.addRoleUser(role.getRoleId(), user);

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));

		PrincipalThreadLocal.setName(user.getUserId());

		_assertDropdownItems(
			new String[] {"delete", "view"},
			_fdsActionProvider.getDropdownItems(
				TestPropsValues.getGroupId(),
				_getMockHttpServletRequest(objectEntry.getObjectEntryId()),
				new RelatedModel(
					objectDefinition.getClassName(),
					objectEntry.getObjectEntryId(), objectEntry.getTitleValue(),
					false)));

		_resourcePermissionLocalService.removeResourcePermission(
			TestPropsValues.getCompanyId(), objectDefinition.getClassName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), role.getRoleId(),
			ActionKeys.UPDATE);

		_assertDropdownItems(
			new String[] {"view"},
			_fdsActionProvider.getDropdownItems(
				TestPropsValues.getGroupId(),
				_getMockHttpServletRequest(objectEntry.getObjectEntryId()),
				new RelatedModel(
					objectDefinition.getClassName(),
					objectEntry.getObjectEntryId(), objectEntry.getTitleValue(),
					false)));

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(objectEntry.getObjectEntryId());

		mockHttpServletRequest.setParameter(
			"template", AssetRenderer.TEMPLATE_ABSTRACT);

		_assertDropdownItems(
			new String[0],
			_fdsActionProvider.getDropdownItems(
				TestPropsValues.getGroupId(), mockHttpServletRequest,
				new RelatedModel(
					objectDefinition.getClassName(),
					objectEntry.getObjectEntryId(), objectEntry.getTitleValue(),
					false)));

		AssetEntry assetEntry = _assetEntryLocalService.getEntry(
			objectDefinition.getClassName(), objectEntry.getObjectEntryId());

		String portletId = PortletKeys.MY_WORKFLOW_TASK;
		String redirect = RandomTestUtil.randomString();
		String workflowTaskId = StringUtil.randomId();

		mockHttpServletRequest.setParameter("portletId", portletId);
		mockHttpServletRequest.setParameter("redirect", redirect);
		mockHttpServletRequest.setParameter(
			"template", AssetRenderer.TEMPLATE_FULL_CONTENT);
		mockHttpServletRequest.setParameter("workflowTaskId", workflowTaskId);

		List<DropdownItem> fdsActionProviderDropdownItems =
			_fdsActionProvider.getDropdownItems(
				TestPropsValues.getGroupId(), mockHttpServletRequest,
				new RelatedModel(
					objectDefinition.getClassName(),
					objectEntry.getObjectEntryId(), objectEntry.getTitleValue(),
					false));

		DropdownItem dropdownItem = fdsActionProviderDropdownItems.get(0);

		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(mockHttpServletRequest);

		Assert.assertEquals(
			PortletURLBuilder.create(
				requestBackedPortletURLFactory.createRenderURL(portletId)
			).setMVCPath(
				"/view_content.jsp"
			).setRedirect(
				redirect
			).setParameter(
				"assetEntryClassPK", objectEntry.getObjectEntryId()
			).setParameter(
				"assetEntryId", assetEntry.getEntryId()
			).setParameter(
				"externalReferenceCode", objectEntry.getExternalReferenceCode()
			).setParameter(
				"languageId",
				LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault())
			).setParameter(
				"type", objectDefinition.getClassName()
			).setParameter(
				"workflowTaskId", workflowTaskId
			).buildString(),
			dropdownItem.get("href"));
	}

	private void _assertDropdownItems(
		String[] expectedDropdownItemLabels,
		List<DropdownItem> actualDropdownItems) {

		Assert.assertEquals(
			actualDropdownItems.toString(), expectedDropdownItemLabels.length,
			actualDropdownItems.size());

		for (int i = 0; i < expectedDropdownItemLabels.length; i++) {
			DropdownItem actualDropdownItem = actualDropdownItems.get(i);

			Assert.assertEquals(
				expectedDropdownItemLabels[i], actualDropdownItem.get("label"));

			if (StringUtil.equals(expectedDropdownItemLabels[i], "view")) {
				Map<String, Object> data =
					(Map<String, Object>)actualDropdownItem.get("data");

				Assert.assertEquals("view", data.get("id"));
			}
		}
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
			long objectEntryId)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addParameter(
			"objectEntryId", String.valueOf(objectEntryId));
		mockHttpServletRequest.setAttribute(
			WebKeys.CURRENT_URL, "http://localhost:8080/currentURL");

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setScopeGroupId(TestPropsValues.getGroupId());
		themeDisplay.setSiteGroupId(TestPropsValues.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockHttpServletRequest;
	}

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "fds.data.provider.key=" + ObjectPortletKeys.OBJECT_ENTRIES + "-relatedModels"
	)
	private FDSActionProvider _fdsActionProvider;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private UserLocalService _userLocalService;

}