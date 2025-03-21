/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.search.web.internal.portlet.shared.search;

import com.liferay.commerce.product.constants.CPField;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.content.search.web.internal.configuration.CPSortPortletInstanceConfiguration;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.SortFactoryUtil;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchContributor;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;

import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 */
@Component(
	property = "jakarta.portlet.name=" + CPPortletKeys.CP_SORT,
	service = PortletSharedSearchContributor.class
)
public class CPSortPortletSharedSearchContributor
	implements PortletSharedSearchContributor {

	@Override
	public void contribute(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		SearchContext searchContext =
			portletSharedSearchSettings.getSearchContext();

		QueryConfig queryConfig = portletSharedSearchSettings.getQueryConfig();

		queryConfig.setHighlightEnabled(false);

		RenderRequest renderRequest =
			portletSharedSearchSettings.getRenderRequest();

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		HttpServletRequest httpServletRequest =
			_portal.getOriginalServletRequest(themeDisplay.getRequest());

		String portletId = ParamUtil.getString(httpServletRequest, "p_p_id");

		String orderByCol = ParamUtil.getString(
			httpServletRequest,
			StringBundler.concat(
				StringPool.UNDERLINE, portletId, StringPool.UNDERLINE,
				SearchContainer.DEFAULT_ORDER_BY_COL_PARAM));

		if (Validator.isNull(orderByCol)) {
			List<PortletPreferences> portletPreferencesList =
				_portletPreferencesLocalService.getPortletPreferences(
					PortletKeys.PREFS_OWNER_ID_DEFAULT,
					PortletKeys.PREFS_OWNER_TYPE_LAYOUT,
					themeDisplay.getPlid());

			for (PortletPreferences portletPreferences :
					portletPreferencesList) {

				if (!StringUtil.containsIgnoreCase(
						portletPreferences.getPortletId(),
						CPPortletKeys.CP_SORT, StringPool.BLANK)) {

					continue;
				}

				try {
					CPSortPortletInstanceConfiguration
						cpSortPortletInstanceConfiguration =
							_configurationProvider.
								getPortletInstanceConfiguration(
									CPSortPortletInstanceConfiguration.class,
									themeDisplay.getLayout(),
									portletPreferences.getPortletId());

					if (Validator.isBlank(
							cpSortPortletInstanceConfiguration.defaultSort())) {

						continue;
					}

					orderByCol =
						cpSortPortletInstanceConfiguration.defaultSort();

					break;
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}
				}
			}
		}

		if (orderByCol.equals("price-low-to-high")) {
			searchContext.setSorts(
				SortFactoryUtil.create(
					CPField.BASE_PRICE, Sort.DOUBLE_TYPE, false));
		}
		else if (orderByCol.equals("price-high-to-low")) {
			searchContext.setSorts(
				SortFactoryUtil.create(
					CPField.BASE_PRICE, Sort.DOUBLE_TYPE, true));
		}
		else if (orderByCol.equals("name-ascending")) {
			searchContext.setSorts(SortFactoryUtil.create(Field.NAME, false));
		}
		else if (orderByCol.equals("name-descending")) {
			searchContext.setSorts(SortFactoryUtil.create(Field.NAME, true));
		}
		else if (orderByCol.equals("new-items")) {
			searchContext.setSorts(
				SortFactoryUtil.create(Field.CREATE_DATE + "_sortable", true));
		}
		else {
			searchContext.setSorts(SortFactoryUtil.getDefaultSorts());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPSortPortletSharedSearchContributor.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Portal _portal;

	@Reference
	private PortletPreferencesLocalService _portletPreferencesLocalService;

}