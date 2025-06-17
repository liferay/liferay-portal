/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.util.ExpandoBridgeFactoryUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.util.PropsValues;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class ExpandoDisplayContextTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testGetSearchContainer() throws Exception {
		try {
			List<ExpandoColumn> initialExpandoColumns =
				_expandoColumnLocalService.getDefaultTableColumns(
					TestPropsValues.getCompanyId(), Layout.class.getName());

			int totalCount =
				PropsValues.SEARCH_CONTAINER_PAGE_DEFAULT_DELTA + 1;

			ExpandoBridge expandoBridge =
				ExpandoBridgeFactoryUtil.getExpandoBridge(
					TestPropsValues.getCompanyId(), Layout.class.getName());

			for (int i = 0; i < totalCount; i++) {
				expandoBridge.addAttribute(
					RandomTestUtil.randomString(),
					ExpandoColumnConstants.STRING);
			}

			MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
				_getMockLiferayPortletRenderRequest();

			mockLiferayPortletRenderRequest.setParameter(
				"modelResource", Layout.class.getName());

			_mvcRenderCommand.render(
				mockLiferayPortletRenderRequest,
				new MockLiferayPortletRenderResponse());

			SearchContainer<String> searchContainer = ReflectionTestUtil.invoke(
				mockLiferayPortletRenderRequest.getAttribute(
					"com.liferay.expando.web.internal.display.context." +
						"ExpandoDisplayContext"),
				"getSearchContainer", new Class<?>[0]);

			Assert.assertEquals(
				totalCount + initialExpandoColumns.size(),
				searchContainer.getTotal());

			List<String> results = searchContainer.getResults();

			Assert.assertEquals(
				results.toString(), totalCount + initialExpandoColumns.size(),
				results.size());
		}
		finally {
			List<ExpandoColumn> expandoColumns =
				_expandoColumnLocalService.getDefaultTableColumns(
					TestPropsValues.getCompanyId(), Layout.class.getName());

			for (ExpandoColumn expandoColumn : expandoColumns) {
				_expandoColumnLocalService.deleteColumn(expandoColumn);
			}
		}
	}

	private MockLiferayPortletRenderRequest
			_getMockLiferayPortletRenderRequest()
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG, null);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.US);

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockLiferayPortletRenderRequest;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private ExpandoColumnLocalService _expandoColumnLocalService;

	@Inject(
		filter = "mvc.command.name=/expando/view_attributes",
		type = MVCRenderCommand.class
	)
	private MVCRenderCommand _mvcRenderCommand;

	@Inject
	private Portal _portal;

}