/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.display.context.test;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.site.cmp.site.initializer.test.util.CMPTestUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Pedro Leite
 */
public abstract class BaseSectionDisplayContextTestCase {

	@Before
	public void setUp() throws Exception {
		Group group = CMPTestUtil.getOrAddGroup(
			BaseSectionDisplayContextTestCase.class);

		objectDefinition =
			objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					getObjectDefinitionExternalReferenceCode(),
					TestPropsValues.getCompanyId());
		themeDisplay = new ThemeDisplay() {
			{
				setCompany(
					_companyLocalService.getCompany(
						TestPropsValues.getCompanyId()));
				setLocale(LocaleUtil.getDefault());
				setScopeGroupId(group.getGroupId());
				setURLCurrent(
					"http://localhost:" +
						PortalUtil.getPortalServerPort(false) + "/currentURL");
				setUser(TestPropsValues.getUser());
			}
		};
	}

	protected void assertFDSFilter(
		String expectedEntityFieldType, String expectedId, String expectedLabel,
		FDSFilter fdsFilter) {

		Assert.assertEquals(
			expectedEntityFieldType, fdsFilter.getEntityFieldType());
		Assert.assertEquals(expectedId, fdsFilter.getId());
		Assert.assertEquals(expectedLabel, fdsFilter.getLabel());
	}

	protected Map<String, Object> getAdditionalProps(AssetEntry assetEntry)
		throws Exception {

		return ReflectionTestUtil.invoke(
			getSectionDisplayContext(_getHttpServletRequest(assetEntry)),
			"getAdditionalProps", new Class<?>[0]);
	}

	protected String getAPIURL(AssetEntry assetEntry) throws Exception {
		return ReflectionTestUtil.invoke(
			getSectionDisplayContext(_getHttpServletRequest(assetEntry)),
			"getAPIURL", new Class<?>[0]);
	}

	protected List<DropdownItem> getBulkActionDropdownItems(
			AssetEntry assetEntry)
		throws Exception {

		return ReflectionTestUtil.invoke(
			getSectionDisplayContext(_getHttpServletRequest(assetEntry)),
			"getBulkActionDropdownItems", new Class<?>[0]);
	}

	protected CreationMenu getCreationMenu(AssetEntry assetEntry)
		throws Exception {

		return ReflectionTestUtil.invoke(
			getSectionDisplayContext(_getHttpServletRequest(assetEntry)),
			"getCreationMenu", new Class<?>[0]);
	}

	protected Map<String, Object> getEmptyState(AssetEntry assetEntry)
		throws Exception {

		return ReflectionTestUtil.invoke(
			getSectionDisplayContext(_getHttpServletRequest(assetEntry)),
			"getEmptyState", new Class<?>[0]);
	}

	protected List<FDSActionDropdownItem> getFDSActionDropdownItems(
			AssetEntry assetEntry)
		throws Exception {

		return ReflectionTestUtil.invoke(
			getSectionDisplayContext(_getHttpServletRequest(assetEntry)),
			"getFDSActionDropdownItems", new Class<?>[0]);
	}

	protected List<FDSFilter> getFDSFilters(AssetEntry assetEntry)
		throws Exception {

		return ReflectionTestUtil.invoke(
			getSectionDisplayContext(_getHttpServletRequest(assetEntry)),
			"getFDSFilters", new Class<?>[0]);
	}

	protected Map<String, Object> getHeaderProps(AssetEntry assetEntry)
		throws Exception {

		return ReflectionTestUtil.invoke(
			getSectionDisplayContext(_getHttpServletRequest(assetEntry)),
			"getHeaderProps", new Class<?>[0]);
	}

	protected abstract String getObjectDefinitionExternalReferenceCode();

	protected abstract Object getSectionDisplayContext(
			HttpServletRequest httpServletRequest)
		throws Exception;

	protected String getValue(DropdownItem dropdownItem, String key) {
		return MapUtil.getString(
			(HashMap<String, Object>)dropdownItem.get("data"), key);
	}

	protected ObjectDefinition objectDefinition;

	@Inject
	protected ObjectDefinitionLocalService objectDefinitionLocalService;

	protected ThemeDisplay themeDisplay;

	private HttpServletRequest _getHttpServletRequest(AssetEntry assetEntry) {
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		if (assetEntry != null) {
			httpServletRequest.setAttribute(
				WebKeys.LAYOUT_ASSET_ENTRY, assetEntry);
		}

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		return httpServletRequest;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

}