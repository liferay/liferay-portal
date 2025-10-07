/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context.test;

import com.liferay.batch.engine.unit.BatchEngineUnitProcessor;
import com.liferay.batch.engine.unit.BatchEngineUnitReader;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectDefinitionSetting;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectFolder;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFolderLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
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
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.initializer.SiteInitializerRegistry;

import jakarta.servlet.http.HttpServletRequest;

import java.io.File;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.junit.Before;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public abstract class BaseDisplayContextTestCase {

	@Before
	public void setUp() throws Exception {
		group = GroupTestUtil.addGroup();

		mockHttpServletRequest = getMockHttpServletRequest();

		themeDisplay = getThemeDisplay(mockHttpServletRequest);

		if (_isCMSSiteInitialized()) {
			return;
		}

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		try {

			// These tests require the instance to be created with the feature
			// flag LPD-17564 enabled. On CI, feature flags are enabled on
			// demand for each test, but not during instance initialization.
			// Until the feature flag LPD-17564 is removed, run the instance
			// lifecycle initializer manually so that the role is created.

			SiteInitializer siteInitializer =
				_siteInitializerRegistry.getSiteInitializer(
					"com.liferay.site.initializer.cms");

			siteInitializer.initialize(group.getGroupId());

			Bundle testBundle = FrameworkUtil.getBundle(
				BaseDisplayContextTestCase.class);

			BundleContext bundleContext = testBundle.getBundleContext();

			for (Bundle bundle : bundleContext.getBundles()) {
				if (Objects.equals(
						bundle.getSymbolicName(),
						"com.liferay.site.initializer.cms")) {

					_deleteFile(bundle, "00.list.type.definition");
					_deleteFile(bundle, "01.object.folder");
					_deleteFile(bundle, "02.object.definition");

					CompletableFuture<Void> completableFuture =
						_batchEngineUnitProcessor.processBatchEngineUnits(
							_batchEngineUnitReader.getBatchEngineUnits(bundle));

					completableFuture.join();
				}
			}
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	protected ObjectDefinition addCustomObjectDefinition(
			long objectFolderId, boolean active, boolean enableObjectEntryDraft,
			List<ObjectDefinitionSetting> objectDefinitionSettings,
			String scope, int status)
		throws Exception {

		ObjectDefinition objectDefinition =
			objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), objectFolderId, null, false, true,
				false, true, true, enableObjectEntryDraft, false, false, false,
				null,
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
				Collections.emptyList());

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
			objectDefinition.isEnableLocalization(),
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
			Collections.emptyList());
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

		return getMockHttpServletRequest(null);
	}

	protected MockHttpServletRequest getMockHttpServletRequest(
			ObjectEntryFolder objectEntryFolder)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		if (objectEntryFolder != null) {
			mockHttpServletRequest.setAttribute(
				InfoDisplayWebKeys.INFO_ITEM, objectEntryFolder);
		}

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, getThemeDisplay(mockHttpServletRequest));

		return mockHttpServletRequest;
	}

	protected ThemeDisplay getThemeDisplay(
			HttpServletRequest httpServletRequest)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLanguageId(group.getDefaultLanguageId());
		themeDisplay.setLayout(
			LayoutTestUtil.addTypeContentLayout(group, "test"));
		themeDisplay.setPathMain(portal.getPathMain());
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setPortalURL("http://localhost:8080");
		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setRequest(httpServletRequest);
		themeDisplay.setScopeGroupId(group.getGroupId());
		themeDisplay.setSiteGroupId(group.getGroupId());
		themeDisplay.setURLCurrent("http://localhost:8080/currentURL");
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	@Inject
	protected CompanyLocalService companyLocalService;

	@DeleteAfterTestRun
	protected Group group;

	protected MockHttpServletRequest mockHttpServletRequest;

	@Inject
	protected ObjectDefinitionLocalService objectDefinitionLocalService;

	@Inject
	protected ObjectFolderLocalService objectFolderLocalService;

	@Inject
	protected Portal portal;

	protected ThemeDisplay themeDisplay;

	private void _deleteFile(Bundle bundle, String fileName) {
		File file = bundle.getDataFile(
			".com.liferay.site.initializer.cms.internal.batch." + fileName +
				".batch.engine.data.json.0.processed");

		if ((file != null) && file.exists()) {
			file.delete();
		}
	}

	private boolean _isCMSSiteInitialized() {
		ObjectFolder objectFolder =
			objectFolderLocalService.fetchObjectFolderByExternalReferenceCode(
				ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES,
				group.getCompanyId());

		if (objectFolder != null) {
			return true;
		}

		return false;
	}

	@Inject
	private BatchEngineUnitProcessor _batchEngineUnitProcessor;

	@Inject
	private BatchEngineUnitReader _batchEngineUnitReader;

	@Inject
	private SiteInitializerRegistry _siteInitializerRegistry;

}