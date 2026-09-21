/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.frontend.data.set.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.frontend.data.set.SystemFDSEntry;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.site.cms.site.initializer.internal.constants.CMSSiteInitializerFDSNames;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Jan Brychta
 */
@RunWith(Arquillian.class)
public class ViewFilesSectionSystemFDSEntryTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void test() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setUser(TestPropsValues.getUser());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		String additionalAPIURLParameters =
			_filesSectionSystemFDSEntry.getAdditionalAPIURLParameters(
				mockHttpServletRequest);

		Assert.assertTrue(
			additionalAPIURLParameters.contains(
				"cmsRoot eq true and cmsSection eq 'files' and " +
					"rootDescendantNode eq false"));

		Assert.assertEquals(
			20, _filesSectionSystemFDSEntry.getDefaultItemsPerPage());
		Assert.assertTrue(
			_filesSectionSystemFDSEntry.getHideManagementBarInEmptyState());
		Assert.assertEquals(
			"/search/v1.0", _filesSectionSystemFDSEntry.getRESTApplication());
		Assert.assertEquals(
			"/v1.0/search", _filesSectionSystemFDSEntry.getRESTEndpoint());
		Assert.assertEquals(
			"SearchResult", _filesSectionSystemFDSEntry.getRESTSchema());
		Assert.assertTrue(_filesSectionSystemFDSEntry.getSnapshotsEnabled());
		Assert.assertEquals(
			"documents-and-media", _filesSectionSystemFDSEntry.getSymbol());
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "frontend.data.set.name=" + CMSSiteInitializerFDSNames.FILES_SECTION
	)
	private SystemFDSEntry _filesSectionSystemFDSEntry;

}