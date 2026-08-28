/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.marketing.platform.web.internal.fragment.renderer.test;

import com.liferay.content.marketing.platform.test.util.CMPTestUtil;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.info.item.InfoItemReference;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Carolina Barbosa
 */
public abstract class BaseComponentSectionFragmentRendererTestCase {

	@Before
	public void setUp() throws Exception {
		CMPTestUtil.getOrAddGroup(
			BaseComponentSectionFragmentRendererTestCase.class);

		cmpProjectObjectDefinition =
			objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMP_PROJECT", TestPropsValues.getCompanyId());

		cmpProjectObjectEntry = CMPTestUtil.addCMPProjectObjectEntry();

		cmpProjectObjectEntryTitle = MapUtil.getString(
			cmpProjectObjectEntry.getValues(), "title");

		cmpTaskObjectDefinition =
			objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMP_TASK", TestPropsValues.getCompanyId());

		cmpTaskObjectEntry = CMPTestUtil.addCMPTaskObjectEntry(
			cmpProjectObjectEntry);

		themeDisplay = new ThemeDisplay() {
			{
				setCompany(
					_companyLocalService.fetchCompany(
						TestPropsValues.getCompanyId()));
				setLocale(LocaleUtil.US);
				setPermissionChecker(
					PermissionCheckerFactoryUtil.create(
						TestPropsValues.getUser()));
				setScopeGroupId(TestPropsValues.getGroupId());
				setSiteGroupId(TestPropsValues.getGroupId());
				setUser(TestPropsValues.getUser());
			}
		};

		mockHttpServletRequest = getMockHttpServletRequest(
			cmpProjectObjectDefinition, cmpProjectObjectEntry);
	}

	protected abstract FragmentRenderer getFragmentRenderer();

	protected MockHttpServletRequest getMockHttpServletRequest(
			ObjectDefinition objectDefinition, ObjectEntry objectEntry)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			InfoDisplayWebKeys.INFO_ITEM, objectEntry);

		LayoutDisplayPageProvider<?> layoutDisplayPageProvider =
			_layoutDisplayPageProviderRegistry.
				getLayoutDisplayPageProviderByClassName(
					objectDefinition.getCompanyId(),
					objectDefinition.getClassName());

		mockHttpServletRequest.setAttribute(
			LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER,
			layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				new InfoItemReference(
					layoutDisplayPageProvider.getClassName(),
					objectEntry.getObjectEntryId())));

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setParameter("redirect", "/redirect-url");

		return mockHttpServletRequest;
	}

	protected Map<String, Object> getProps() {
		return ReflectionTestUtil.invoke(
			getFragmentRenderer(), "getProps",
			new Class<?>[] {
				FragmentRendererContext.class, HttpServletRequest.class
			},
			null, mockHttpServletRequest);
	}

	protected void testGetPropsWithCMSObjectEntry() throws Exception {
		_depotEntry = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext());

		mockHttpServletRequest = getMockHttpServletRequest(
			objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_WEB_CONTENT", TestPropsValues.getCompanyId()),
			CMPTestUtil.addCMSBasicWebContentObjectEntry(
				_depotEntry, RandomTestUtil.randomString()));

		Map<String, Object> props = getProps();

		Assert.assertEquals(
			GroupConstants.DEFAULT_PARENT_GROUP_ID,
			props.get("projectGroupId"));
	}

	protected void testGetPropsWithTaskObjectEntry() throws Exception {
		mockHttpServletRequest = getMockHttpServletRequest(
			cmpTaskObjectDefinition, cmpTaskObjectEntry);

		Map<String, Object> props = getProps();

		Assert.assertEquals(
			cmpProjectObjectEntry.getGroupId(), props.get("projectGroupId"));
	}

	protected ObjectDefinition cmpProjectObjectDefinition;
	protected ObjectEntry cmpProjectObjectEntry;
	protected String cmpProjectObjectEntryTitle;
	protected ObjectDefinition cmpTaskObjectDefinition;
	protected ObjectEntry cmpTaskObjectEntry;
	protected MockHttpServletRequest mockHttpServletRequest;

	@Inject
	protected ObjectDefinitionLocalService objectDefinitionLocalService;

	protected ThemeDisplay themeDisplay;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private LayoutDisplayPageProviderRegistry
		_layoutDisplayPageProviderRegistry;

}