/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context.test;

import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectDefinitionSetting;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

import org.junit.Before;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public abstract class BaseDisplayContextTestCase {

	@Before
	public void setUp() throws Exception {
		group = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.CMS);

		mockHttpServletRequest = getMockHttpServletRequest();

		themeDisplay = getThemeDisplay(mockHttpServletRequest);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);
	}

	protected ObjectDefinition addCustomObjectDefinition(
			long objectFolderId, boolean active, boolean enableComments,
			boolean enableObjectEntryDraft,
			List<ObjectDefinitionSetting> objectDefinitionSettings,
			String scope, int status)
		throws Exception {

		ObjectDefinition objectDefinition =
			objectDefinitionLocalService.addCustomObjectDefinition(
				null, TestPropsValues.getUserId(), objectFolderId, null, true,
				enableComments, true, false, true, enableObjectEntryDraft,
				false, false, false, null,
				Collections.singletonMap(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				Collections.singletonMap(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()),
				true, scope, ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				objectDefinitionSettings,
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						"Text", "String", true, true, null,
						RandomTestUtil.randomString(), "text", false)),
				Collections.emptyList(), new ServiceContext());

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
			objectDefinition.isEnableFormContainer(),
			objectDefinition.isEnableFriendlyURLCustomization(),
			objectDefinition.isEnableIndexSearch(),
			objectDefinition.isEnableObjectEntryDraft(),
			objectDefinition.isEnableObjectEntryHistory(),
			objectDefinition.isEnableObjectEntrySchedule(),
			objectDefinition.isEnableObjectEntrySubscription(),
			objectDefinition.isEnableObjectEntryVersioning(),
			objectDefinition.getFriendlyURLSeparator(),
			objectDefinition.getLabelMap(), objectDefinition.getName(),
			objectDefinition.getPanelAppOrder(),
			objectDefinition.getPanelCategoryKey(),
			objectDefinition.isPortlet(), objectDefinition.getPluralLabelMap(),
			objectDefinition.getScope(), objectDefinition.getStatus(),
			objectDefinition.getObjectDefinitionSettings(),
			Collections.emptyList(), Collections.emptyList(),
			new ServiceContext());
	}

	protected ObjectDefinition addCustomObjectDefinition(
			long objectFolderId, boolean active, boolean enableObjectEntryDraft,
			List<ObjectDefinitionSetting> objectDefinitionSettings,
			String scope, int status)
		throws Exception {

		return addCustomObjectDefinition(
			objectFolderId, active, false, enableObjectEntryDraft,
			objectDefinitionSettings, scope, status);
	}

	protected ObjectDefinition addCustomObjectDefinition(
			long objectFolderId, boolean active, boolean enableObjectEntryDraft,
			String scope, int status)
		throws Exception {

		return addCustomObjectDefinition(
			objectFolderId, active, enableObjectEntryDraft,
			Collections.emptyList(), scope, status);
	}

	protected MockHttpServletRequest getMockHttpServletRequest()
		throws Exception {

		return getMockHttpServletRequest(null, TestPropsValues.getUser());
	}

	protected MockHttpServletRequest getMockHttpServletRequest(
			ObjectEntryFolder objectEntryFolder)
		throws Exception {

		return getMockHttpServletRequest(
			objectEntryFolder, TestPropsValues.getUser());
	}

	protected MockHttpServletRequest getMockHttpServletRequest(
			ObjectEntryFolder objectEntryFolder, User user)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		if (objectEntryFolder != null) {
			mockHttpServletRequest.setAttribute(
				InfoDisplayWebKeys.INFO_ITEM, objectEntryFolder);
		}

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY,
			getThemeDisplay(mockHttpServletRequest, user));

		return mockHttpServletRequest;
	}

	protected MockHttpServletRequest getMockHttpServletRequest(User user)
		throws Exception {

		return getMockHttpServletRequest(null, user);
	}

	protected ThemeDisplay getThemeDisplay(
			HttpServletRequest httpServletRequest)
		throws Exception {

		return getThemeDisplay(httpServletRequest, TestPropsValues.getUser());
	}

	protected ThemeDisplay getThemeDisplay(
			HttpServletRequest httpServletRequest, User user)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLanguageId(group.getDefaultLanguageId());

		Layout layout = LayoutTestUtil.addTypeContentLayout(group, "test");

		themeDisplay.setLayout(layout);
		themeDisplay.setLayoutSet(layout.getLayoutSet());

		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setPathMain(portal.getPathMain());
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setPortalURL(
			"http://localhost:" + PortalUtil.getPortalServerPort(false));
		themeDisplay.setRealUser(user);
		themeDisplay.setRequest(httpServletRequest);
		themeDisplay.setScopeGroupId(group.getGroupId());
		themeDisplay.setSiteGroupId(group.getGroupId());
		themeDisplay.setURLCurrent(
			"http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/currentURL");
		themeDisplay.setUser(user);

		return themeDisplay;
	}

	@Inject
	protected CompanyLocalService companyLocalService;

	protected Group group;

	@Inject
	protected Language language;

	protected MockHttpServletRequest mockHttpServletRequest;

	@Inject
	protected ObjectDefinitionLocalService objectDefinitionLocalService;

	@Inject
	protected ObjectFolderLocalService objectFolderLocalService;

	@Inject
	protected Portal portal;

	protected ThemeDisplay themeDisplay;

	@Inject
	private GroupLocalService _groupLocalService;

}