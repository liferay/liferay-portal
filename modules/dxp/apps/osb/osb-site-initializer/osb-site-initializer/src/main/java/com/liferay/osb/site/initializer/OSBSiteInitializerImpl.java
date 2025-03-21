/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.site.initializer;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.search.experiences.rest.dto.v1_0.SXPBlueprint;
import com.liferay.search.experiences.rest.resource.v1_0.SXPBlueprintResource;
import com.liferay.site.initializer.extender.OSBSiteInitializer;
import com.liferay.site.initializer.extender.SiteInitializerUtil;

import jakarta.servlet.ServletContext;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Nilton Vieira
 */
@Component(service = OSBSiteInitializer.class)
public class OSBSiteInitializerImpl implements OSBSiteInitializer {

	@Override
	public void addOrUpdateSXPBlueprint(
			Map<String, String> classNameIdStringUtilReplaceValues,
			Map<String, String> releaseInfoStringUtilReplaceValues,
			ServiceContext serviceContext, ServletContext servletContext,
			Map<String, String> stringUtilReplaceValues)
		throws Exception {

		String json = SiteInitializerUtil.read(
			"/site-initializer/sxp-blueprints.json", servletContext);

		if (json == null) {
			return;
		}

		SXPBlueprintResource.Builder builder =
			_sxpBlueprintResourceFactory.create();

		SXPBlueprintResource sxpBlueprintResource = builder.httpServletRequest(
			serviceContext.getRequest()
		).user(
			serviceContext.fetchUser()
		).build();

		JSONArray jsonArray = _jsonFactory.createJSONArray(
			SiteInitializerUtil.replace(
				classNameIdStringUtilReplaceValues,
				releaseInfoStringUtilReplaceValues, json,
				stringUtilReplaceValues));

		for (int i = 0; i < jsonArray.length(); i++) {
			SXPBlueprint sxpBlueprint = SXPBlueprint.toDTO(
				String.valueOf(jsonArray.getJSONObject(i)));

			if (sxpBlueprint == null) {
				_log.error(
					"Unable to transform search experiences blueprint from " +
						"JSON: " + json);

				continue;
			}

			sxpBlueprint =
				sxpBlueprintResource.putSXPBlueprintByExternalReferenceCode(
					sxpBlueprint.getExternalReferenceCode(), sxpBlueprint);

			stringUtilReplaceValues.put(
				"SXP_BLUEPRINT_ID:" + sxpBlueprint.getExternalReferenceCode(),
				String.valueOf(sxpBlueprint.getId()));
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OSBSiteInitializerImpl.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private SXPBlueprintResource.Factory _sxpBlueprintResourceFactory;

}