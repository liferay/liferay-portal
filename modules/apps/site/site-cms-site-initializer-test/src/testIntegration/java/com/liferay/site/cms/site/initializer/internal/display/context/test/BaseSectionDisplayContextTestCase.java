/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context.test;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.initializer.SiteInitializerRegistry;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public abstract class BaseSectionDisplayContextTestCase {

	@Before
	public void setUp() throws Exception {
		group = GroupTestUtil.addGroup();

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		try {
			SiteInitializer siteInitializer =
				_siteInitializerRegistry.getSiteInitializer(
					"com.liferay.site.initializer.cms");

			siteInitializer.initialize(group.getGroupId());
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	protected ObjectDefinition addCustomObjectDefinition(
			long objectFolderId, boolean active, boolean enableObjectEntryDraft,
			String scope, int status)
		throws Exception {

		ObjectDefinition objectDefinition =
			objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), objectFolderId, null, false, false,
				true, true, enableObjectEntryDraft, false,
				Collections.singletonMap(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				Collections.singletonMap(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()),
				true, scope, ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(),
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						"Text", "String", true, true, null,
						RandomTestUtil.randomString(),
						"x" + RandomTestUtil.randomString(), false)));

		if (status == WorkflowConstants.STATUS_DRAFT) {
			return objectDefinition;
		}

		objectDefinition =
			objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId());

		return objectDefinitionLocalService.updateCustomObjectDefinition(
			objectDefinition.getExternalReferenceCode(),
			objectDefinition.getObjectDefinitionId(),
			objectDefinition.getAccountEntryRestrictedObjectFieldId(),
			objectDefinition.getDescriptionObjectFieldId(),
			objectDefinition.getObjectFolderId(),
			objectDefinition.getTitleObjectFieldId(),
			objectDefinition.isAccountEntryRestricted(), active,
			objectDefinition.getClassName(),
			objectDefinition.isEnableCategorization(),
			objectDefinition.isEnableComments(),
			objectDefinition.isEnableFriendlyURLCustomization(),
			objectDefinition.isEnableIndexSearch(),
			objectDefinition.isEnableLocalization(),
			objectDefinition.isEnableObjectEntryDraft(),
			objectDefinition.isEnableObjectEntryHistory(),
			objectDefinition.isEnableObjectEntryVersioning(),
			objectDefinition.getLabelMap(), objectDefinition.getName(),
			objectDefinition.getPanelAppOrder(),
			objectDefinition.getPanelCategoryKey(),
			objectDefinition.isPortlet(), objectDefinition.getPluralLabelMap(),
			objectDefinition.getScope(), objectDefinition.getStatus(),
			Collections.emptyList());
	}

	protected String getHref(ObjectDefinition objectDefinition) {
		StringBundler sb = new StringBundler(4);

		sb.append("/cms/add_structured_content_item?groupId=");
		sb.append(group.getGroupId());
		sb.append("&objectDefinitionId=");
		sb.append(objectDefinition.getObjectDefinitionId());

		return sb.toString();
	}

	protected HttpServletRequest getMockHttpServletRequest() throws Exception {
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, getThemeDisplay(httpServletRequest));

		return httpServletRequest;
	}

	protected ThemeDisplay getThemeDisplay(
			HttpServletRequest httpServletRequest)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setRequest(httpServletRequest);
		themeDisplay.setScopeGroupId(group.getGroupId());
		themeDisplay.setSiteGroupId(group.getGroupId());
		themeDisplay.setURLCurrent("http://localhost:8080/currentURL");
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	protected void testGetCreationMenu(
		CreationMenu creationMenu, Map<String, String> expectedResultMap) {

		List<DropdownItem> dropdownItems = (List<DropdownItem>)creationMenu.get(
			"primaryItems");

		Assert.assertEquals(
			dropdownItems.toString(), expectedResultMap.size(),
			dropdownItems.size());

		int index = 0;

		for (Map.Entry<String, String> entry : expectedResultMap.entrySet()) {
			DropdownItem dropdownItem = dropdownItems.get(index);

			Assert.assertEquals(entry.getKey(), dropdownItem.get("label"));

			if (Validator.isNull(entry.getValue())) {
				Assert.assertNull(dropdownItem.get("href"));
			}
			else {
				Assert.assertEquals(entry.getValue(), dropdownItem.get("href"));
			}

			index++;
		}
	}

	@Inject
	protected CompanyLocalService companyLocalService;

	@DeleteAfterTestRun
	protected Group group;

	@Inject
	protected ObjectDefinitionLocalService objectDefinitionLocalService;

	@Inject
	protected ObjectFolderLocalService objectFolderLocalService;

	@Inject
	private SiteInitializerRegistry _siteInitializerRegistry;

}