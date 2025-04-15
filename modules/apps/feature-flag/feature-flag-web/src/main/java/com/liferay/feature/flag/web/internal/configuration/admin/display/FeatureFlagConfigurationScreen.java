/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.feature.flag.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.feature.flag.web.internal.configuration.admin.category.FeatureFlagConfigurationCategory;
import com.liferay.feature.flag.web.internal.display.FeatureFlagsDisplayContextFactory;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.feature.flag.FeatureFlagType;
import com.liferay.portal.kernel.feature.flag.constants.FeatureFlagConstants;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;
import java.util.Objects;

/**
 * @author Drew Brokke
 */
public class FeatureFlagConfigurationScreen implements ConfigurationScreen {

	public FeatureFlagConfigurationScreen(
		FeatureFlagType featureFlagType,
		FeatureFlagsDisplayContextFactory featureFlagsDisplayContextFactory,
		int index, String scope, ServletContext servletContext) {

		_featureFlagType = featureFlagType;
		_featureFlagsDisplayContextFactory = featureFlagsDisplayContextFactory;
		_index = index;
		_scope = scope;
		_servletContext = servletContext;
	}

	@Override
	public String getCategoryKey() {
		return FeatureFlagConfigurationCategory.CATEGORY_KEY;
	}

	@Override
	public String getKey() {
		return FeatureFlagConstants.getKey(
			String.valueOf(_index), _featureFlagType.toString(), getScope());
	}

	@Override
	public String getName(Locale locale) {
		return _featureFlagType.getTitle(locale);
	}

	@Override
	public String getScope() {
		return _scope;
	}

	@Override
	public boolean isVisible() {
		return _featureFlagType.isUIEnabled();
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		httpServletRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			_featureFlagsDisplayContextFactory.create(
				_featureFlagType, httpServletRequest,
				Objects.equals(
					getScope(),
					ExtendedObjectClassDefinition.Scope.SYSTEM.getValue())));

		try {
			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher("/feature_flags.jsp");

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new IOException(
				"Unable to render feature_flags.jsp", exception);
		}
	}

	private final FeatureFlagsDisplayContextFactory
		_featureFlagsDisplayContextFactory;
	private final FeatureFlagType _featureFlagType;
	private final int _index;
	private final String _scope;
	private final ServletContext _servletContext;

}