/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.configuration.provider;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.segments.configuration.SegmentsCompanyConfiguration;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Cristina González
 */
@ProviderType
public interface SegmentsConfigurationProvider {

	public String getCompanyConfigurationURL(
			HttpServletRequest httpServletRequest)
		throws PortalException;

	public String getConfigurationURL(HttpServletRequest httpServletRequest)
		throws PortalException;

	public boolean isRoleSegmentationEnabled() throws ConfigurationException;

	public boolean isRoleSegmentationEnabled(long companyId)
		throws ConfigurationException;

	public boolean isSegmentationEnabled() throws ConfigurationException;

	public boolean isSegmentationEnabled(long companyId)
		throws ConfigurationException;

	public boolean isSegmentsCompanyConfigurationDefined(long companyId)
		throws ConfigurationException;

	public void resetSegmentsCompanyConfiguration(long companyId)
		throws ConfigurationException;

	public void updateSegmentsCompanyConfiguration(
			long companyId,
			SegmentsCompanyConfiguration segmentsCompanyConfiguration)
		throws ConfigurationException;

}