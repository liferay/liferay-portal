/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.omni.search.web.internal.portlet.action;

import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.product.navigation.omni.search.OmniSearchResult;
import com.liferay.product.navigation.omni.search.OmniSearchResultProvider;
import com.liferay.product.navigation.omni.search.web.internal.constants.ProductNavigationOmniSearchPortletKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Marcos Castro
 * @author Thiago Buarque
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ProductNavigationOmniSearchPortletKeys.PRODUCT_NAVIGATION_OMNI_SEARCH,
		"mvc.command.name=/omni_search/omni_search_results"
	},
	service = MVCResourceCommand.class
)
public class OmniSearchResultsMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (!themeDisplay.isSignedIn() ||
			!FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-78171")) {

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				_toJSONArray(Collections.emptyList()));

			return;
		}

		HttpServletRequest httpServletRequest =
			_portal.getOriginalServletRequest(
				_portal.getHttpServletRequest(
					_portal.getLiferayPortletRequest(resourceRequest)));

		List<OmniSearchResult> omniSearchResults = new ArrayList<>();

		for (OmniSearchResultProvider omniSearchResultProvider :
				_omniSearchResultProviders) {

			try {
				omniSearchResults.addAll(
					omniSearchResultProvider.getOmniSearchResults(
						httpServletRequest, themeDisplay));
			}
			catch (Exception exception) {
				_log.error(
					"Unable to get results from omni search provider",
					exception);
			}
		}

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse, _toJSONArray(omniSearchResults));
	}

	private JSONArray _toJSONArray(List<OmniSearchResult> omniSearchResults)
		throws Exception {

		return JSONUtil.toJSONArray(
			omniSearchResults,
			omniSearchResult -> JSONUtil.put(
				"description", omniSearchResult.getDescription()
			).put(
				"icon", omniSearchResult.getIcon()
			).put(
				"omniSearchResults",
				_toJSONArray(omniSearchResult.getOmniSearchResults())
			).put(
				"title", omniSearchResult.getTitle()
			).put(
				"type", String.valueOf(omniSearchResult.getType())
			).put(
				"url", omniSearchResult.getURL()
			));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OmniSearchResultsMVCResourceCommand.class);

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile List<OmniSearchResultProvider> _omniSearchResultProviders;

	@Reference
	private Portal _portal;

}