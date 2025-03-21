/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.web.internal.blueprint.options.portlet.shared.search;

import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchContributor;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;
import com.liferay.search.experiences.constants.SXPPortletKeys;
import com.liferay.search.experiences.web.internal.blueprint.options.portlet.preferences.SXPBlueprintOptionsPortletPreferencesUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Kevin Tan
 */
@Component(
	enabled = false,
	property = "jakarta.portlet.name=" + SXPPortletKeys.SXP_BLUEPRINT_OPTIONS,
	service = PortletSharedSearchContributor.class
)
public class SXPBlueprintOptionsPortletSharedSearchContributor
	implements PortletSharedSearchContributor {

	@Override
	public void contribute(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		String federatedSearchKey =
			SXPBlueprintOptionsPortletPreferencesUtil.getValue(
				portletSharedSearchSettings.getPortletPreferences(),
				"federatedSearchKey");

		SearchRequestBuilder searchRequestBuilder =
			portletSharedSearchSettings.getFederatedSearchRequestBuilder(
				federatedSearchKey);

		searchRequestBuilder.withSearchContext(
			searchContext -> {
				searchContext.setAttribute(
					"federatedSearchKey", federatedSearchKey);

				String sxpBlueprintExternalReferenceCode = GetterUtil.getString(
					searchContext.getAttribute(
						"search.experiences.blueprint.external.reference." +
							"code"));

				if (Validator.isBlank(sxpBlueprintExternalReferenceCode)) {
					searchContext.setAttribute(
						"search.experiences.blueprint.external.reference.code",
						SXPBlueprintOptionsPortletPreferencesUtil.getValue(
							portletSharedSearchSettings.getPortletPreferences(),
							"sxpBlueprintExternalReferenceCode"));
				}

				if (Validator.isNull(
						searchContext.getAttribute(
							"search.experiences.ip.address"))) {

					HttpServletRequest httpServletRequest =
						_portal.getHttpServletRequest(
							portletSharedSearchSettings.getRenderRequest());

					searchContext.setAttribute(
						"search.experiences.ip.address",
						httpServletRequest.getRemoteAddr());
				}

				if (Validator.isNull(
						searchContext.getAttribute(
							"search.experiences.scope.group.id"))) {

					ThemeDisplay themeDisplay =
						portletSharedSearchSettings.getThemeDisplay();

					searchContext.setAttribute(
						"search.experiences.scope.group.id",
						themeDisplay.getScopeGroupId());
				}
			});
	}

	@Reference
	private Portal _portal;

}