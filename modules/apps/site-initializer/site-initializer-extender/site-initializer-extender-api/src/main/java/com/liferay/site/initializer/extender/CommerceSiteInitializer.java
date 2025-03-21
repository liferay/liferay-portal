/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.initializer.extender;

import com.liferay.portal.kernel.service.ServiceContext;

import jakarta.servlet.ServletContext;

import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.Bundle;

/**
 * @author Nilton Vieira
 */
@ProviderType
public interface CommerceSiteInitializer {

	public void addAccountGroups(
			ServiceContext serviceContext, ServletContext servletContext)
		throws Exception;

	public void addCPDefinitions(
			Bundle bundle, ServiceContext serviceContext,
			ServletContext servletContext,
			Map<String, String> stringUtilReplaceValues)
		throws Exception;

	public void addPortletSettings(
			ClassLoader classLoader, ServiceContext serviceContext,
			ServletContext servletContext)
		throws Exception;

	public long getCommerceChannelGroupId(long siteGroupId);

	public String getCommerceOrderClassName();

}