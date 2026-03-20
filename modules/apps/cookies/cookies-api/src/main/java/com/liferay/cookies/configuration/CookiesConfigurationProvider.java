/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.configuration;

import com.liferay.cookies.configuration.banner.CookiesBannerConfiguration;
import com.liferay.cookies.configuration.consent.CookiesConsentConfiguration;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Daniel Sanz
 */
@ProviderType
public interface CookiesConfigurationProvider {

	public String getCompanyConfigurationURL(
			HttpServletRequest httpServletRequest)
		throws PortalException;

	public CookiesBannerConfiguration getCookiesBannerConfiguration(
			ThemeDisplay themeDisplay)
		throws Exception;

	public CookiesConsentConfiguration getCookiesConsentConfiguration(
			ThemeDisplay themeDisplay)
		throws Exception;

	public CookiesPreferenceHandlingConfiguration
			getCookiesPreferenceHandlingConfiguration(ThemeDisplay themeDisplay)
		throws Exception;

	public int getCookiesPreferenceHandlingConsentRenewalPeriod(
		ExtendedObjectClassDefinition.Scope scope, long scopePK);

	public long getCookiesPreferenceHandlingCustomFloatingIconImageId(
		ExtendedObjectClassDefinition.Scope scope, long scopePK);

	public int getCookiesPreferenceHandlingDissentRenewalPeriod(
		ExtendedObjectClassDefinition.Scope scope, long scopePK);

	public String getCookiesPreferenceHandlingFloatingIcon(
		ExtendedObjectClassDefinition.Scope scope, long scopePK);

	public String getGroupConfigurationURL(
			HttpServletRequest httpServletRequest)
		throws PortalException;

	public String getSystemConfigurationURL(
			HttpServletRequest httpServletRequest)
		throws PortalException;

	public boolean isCookiesPreferenceHandlingConfigurationDefined(
			ExtendedObjectClassDefinition.Scope scope, long scopePK)
		throws Exception;

	public boolean isCookiesPreferenceHandlingEnabled(
		ExtendedObjectClassDefinition.Scope scope, long scopePK);

	public boolean isCookiesPreferenceHandlingExplicitConsentMode(
		ExtendedObjectClassDefinition.Scope scope, long scopePK);

	public boolean isCookiesPreferenceHandlingFloatingIconEnabled(
		ExtendedObjectClassDefinition.Scope scope, long scopePK);

	public boolean isCookiesPreferenceHandlingStoreConsent(
		ExtendedObjectClassDefinition.Scope scope, long scopePK);

	public void resetCookiesPreferenceHandlingConfiguration(
			ExtendedObjectClassDefinition.Scope scope, long scopePK)
		throws ConfigurationException;

	public void updateCookiesPreferenceHandlingConfiguration(
			int consentRenewalPeriod, boolean enabled,
			boolean explicitConsentMode,
			ExtendedObjectClassDefinition.Scope scope, long scopePK,
			boolean storeConsent)
		throws Exception;

}