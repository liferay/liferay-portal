/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.initializer.extender;

import com.liferay.portal.kernel.service.ServiceContext;

import jakarta.servlet.ServletContext;

import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Nilton Vieira
 */
@ProviderType
public interface OSBSiteInitializer {

	public void addOrUpdateSXPBlueprint(
			Map<String, String> classNameIdStringUtilReplaceValues,
			Map<String, String> releaseInfoStringUtilReplaceValues,
			ServiceContext serviceContext, ServletContext servletContext,
			Map<String, String> stringUtilReplaceValues)
		throws Exception;

}