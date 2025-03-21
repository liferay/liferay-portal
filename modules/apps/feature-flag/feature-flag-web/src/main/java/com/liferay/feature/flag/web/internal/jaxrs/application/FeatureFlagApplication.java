/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.feature.flag.web.internal.jaxrs.application;

import com.liferay.feature.flag.web.internal.feature.flag.FeatureFlagsBag;
import com.liferay.feature.flag.web.internal.feature.flag.FeatureFlagsBagProvider;
import com.liferay.feature.flag.web.internal.model.FeatureFlagDisplay;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.feature.flag.FeatureFlag;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants;

/**
 * @author Drew Brokke
 */
@Component(
	property = {
		JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE + "=/com-liferay-feature-flag-web",
		JaxrsWhiteboardConstants.JAX_RS_NAME + "=com.liferay.feature.flag.web.internal.jaxrs.application.FeatureFlagApplication",
		"auth.verifier.auth.verifier.PortalSessionAuthVerifier.urls.includes=/*",
		"auth.verifier.guest.allowed=false", "liferay.oauth2=false"
	},
	service = Application.class
)
public class FeatureFlagApplication extends Application {

	@Path("/set-enabled")
	@POST
	public Response confirm(
		@Context HttpServletRequest httpServletRequest,
		@Context HttpServletResponse httpServletResponse,
		@FormParam("companyId") long companyId,
		@FormParam("enabled") boolean enabled, @FormParam("key") String key) {

		_featureFlagsBagProvider.setEnabled(companyId, key, enabled);

		FeatureFlagsBag featureFlagsBag =
			_featureFlagsBagProvider.getOrCreateFeatureFlagsBag(companyId);

		return Response.ok(
			HashMapBuilder.put(
				"dependentFeatureFlags",
				TransformUtil.transform(
					_getDependentFeatureFlags(featureFlagsBag, key),
					featureFlag -> _toMap(
						companyId, featureFlag, featureFlagsBag,
						_portal.getLocale(httpServletRequest)))
			).build(),
			MediaType.APPLICATION_JSON
		).build();
	}

	public Set<Object> getSingletons() {
		return Collections.singleton(this);
	}

	@Path("/is-enabled")
	@POST
	public Response isEnabled(
		@Context HttpServletRequest httpServletRequest,
		@Context HttpServletResponse httpServletResponse,
		@FormParam("companyId") long companyId, @FormParam("key") String key) {

		try {
			FeatureFlagsBag featureFlagsBag =
				_featureFlagsBagProvider.getOrCreateFeatureFlagsBag(companyId);

			FeatureFlag featureFlag = featureFlagsBag.getFeatureFlag(key);

			if (featureFlag == null) {
				return Response.status(
					Response.Status.NOT_FOUND
				).build();
			}

			Locale locale = _portal.getLocale(httpServletRequest);

			return Response.ok(
				HashMapBuilder.<String, Object>put(
					"dependentFeatureFlags",
					TransformUtil.transform(
						_getDependentFeatureFlags(featureFlagsBag, key),
						dependentFeatureFlag -> _toMap(
							companyId, dependentFeatureFlag, featureFlagsBag,
							locale))
				).put(
					"featureFlag",
					_toMap(companyId, featureFlag, featureFlagsBag, locale)
				).build(),
				MediaType.APPLICATION_JSON
			).build();
		}
		catch (Exception exception) {
			_log.error(exception);

			return Response.status(
				Response.Status.INTERNAL_SERVER_ERROR
			).build();
		}
	}

	private List<FeatureFlag> _getDependencyFeatureFlags(
		long companyId, FeatureFlagsBag featureFlagsBag, String key) {

		FeatureFlag featureFlag = featureFlagsBag.getFeatureFlag(key);

		if (featureFlag == null) {
			_log.error(
				StringBundler.concat(
					"Feature flag ", HtmlUtil.escape(key),
					" does not exist for company ", companyId));

			return new ArrayList<>();
		}

		return featureFlagsBag.getFeatureFlags(
			maybeDependencyFeatureFlag -> ArrayUtil.contains(
				featureFlag.getDependencyKeys(),
				maybeDependencyFeatureFlag.getKey()));
	}

	private List<FeatureFlag> _getDependentFeatureFlags(
		FeatureFlagsBag featureFlagsBag, String key) {

		return featureFlagsBag.getFeatureFlags(
			maybeDependentFeatureFlag -> ArrayUtil.contains(
				maybeDependentFeatureFlag.getDependencyKeys(), key));
	}

	private Map<String, Object> _toMap(
		long companyId, FeatureFlag featureFlag,
		FeatureFlagsBag featureFlagsBag, Locale locale) {

		FeatureFlagDisplay featureFlagDisplay = new FeatureFlagDisplay(
			companyId,
			_getDependencyFeatureFlags(
				companyId, featureFlagsBag, featureFlag.getKey()),
			featureFlag, locale);

		return HashMapBuilder.<String, Object>put(
			"companyId", featureFlagDisplay.getCompanyId()
		).put(
			"dependenciesFulfilled",
			featureFlagDisplay.isDependenciesFulfilled()
		).put(
			"dependencyKeys", featureFlagDisplay.getDependencyKeys()
		).put(
			"description", featureFlagDisplay.getDescription()
		).put(
			"enabled", featureFlagDisplay.isEnabled()
		).put(
			"featureFlagType", featureFlagDisplay.getFeatureFlagType()
		).put(
			"key", featureFlagDisplay.getKey()
		).put(
			"title", featureFlagDisplay.getTitle()
		).build();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FeatureFlagApplication.class);

	@Reference
	private FeatureFlagsBagProvider _featureFlagsBagProvider;

	@Reference
	private Portal _portal;

}