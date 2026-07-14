/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;

import java.io.InputStream;

import java.util.List;

/**
 * Provides the remote service utility for Company. This utility wraps
 * <code>com.liferay.portal.service.impl.CompanyServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see CompanyService
 * @generated
 */
public class CompanyServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.service.impl.CompanyServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds a company.
	 *
	 * @param companyId the primary key of the company (optionally <code>null</code> or
	 <code>0</code> to generate a key automatically)
	 * @param webId the company's web domain
	 * @param virtualHost the company's virtual host name
	 * @param mx the company's mail domain
	 * @param maxUsers the max number of company users (optionally
	 <code>0</code>)
	 * @param active whether the company is active
	 * @return the company
	 */
	public static Company addCompany(
			long companyId, String webId, String virtualHost, String mx,
			int maxUsers, boolean active)
		throws PortalException {

		return getService().addCompany(
			companyId, webId, virtualHost, mx, maxUsers, active);
	}

	/**
	 * Adds a company.
	 *
	 * @param webId the company's web domain
	 * @param virtualHost the company's virtual host name
	 * @param mx the company's mail domain
	 * @param maxUsers the max number of company users (optionally
	 <code>0</code>)
	 * @param active whether the company is active
	 * @return the company
	 */
	public static Company addCompany(
			Long companyId, String webId, String virtualHost, String mx,
			int maxUsers, boolean active, String defaultAdminPassword,
			String defaultAdminScreenName, String defaultAdminEmailAddress,
			String defaultAdminFirstName, String defaultAdminMiddleName,
			String defaultAdminLastName)
		throws PortalException {

		return getService().addCompany(
			companyId, webId, virtualHost, mx, maxUsers, active,
			defaultAdminPassword, defaultAdminScreenName,
			defaultAdminEmailAddress, defaultAdminFirstName,
			defaultAdminMiddleName, defaultAdminLastName);
	}

	public static Company addDBPartitionCompany(
			String schemaName, String name, String virtualHost, String webId)
		throws PortalException {

		return getService().addDBPartitionCompany(
			schemaName, name, virtualHost, webId);
	}

	public static Company deleteCompany(long companyId) throws PortalException {
		return getService().deleteCompany(companyId);
	}

	/**
	 * Deletes the company's logo.
	 *
	 * @param companyId the primary key of the company
	 */
	public static void deleteLogo(long companyId) throws PortalException {
		getService().deleteLogo(companyId);
	}

	public static void forEachCompany(
			com.liferay.petra.function.UnsafeConsumer<Company, Exception>
				unsafeConsumer)
		throws Exception {

		getService().forEachCompany(unsafeConsumer);
	}

	/**
	 * Returns all the companies.
	 *
	 * @return the companies
	 */
	public static List<Company> getCompanies() {
		return getService().getCompanies();
	}

	/**
	 * Returns the company with the primary key.
	 *
	 * @param companyId the primary key of the company
	 * @return Returns the company with the primary key
	 */
	public static Company getCompanyById(long companyId)
		throws PortalException {

		return getService().getCompanyById(companyId);
	}

	/**
	 * Returns the company with the virtual host name.
	 *
	 * @param virtualHost the company's virtual host name
	 * @return Returns the company with the virtual host name
	 */
	public static Company getCompanyByVirtualHost(String virtualHost)
		throws PortalException {

		return getService().getCompanyByVirtualHost(virtualHost);
	}

	/**
	 * Returns the company with the web domain.
	 *
	 * @param webId the company's web domain
	 * @return Returns the company with the web domain
	 */
	public static Company getCompanyByWebId(String webId)
		throws PortalException {

		return getService().getCompanyByWebId(webId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * Removes the values that match the keys of the company's preferences.
	 *
	 * This method is called by {@link
	 * com.liferay.portlet.portalsettings.action.EditLDAPServerAction} remotely
	 * through {@link CompanyService}.
	 *
	 * @param companyId the primary key of the company
	 * @param keys the company's preferences keys to be remove
	 */
	public static void removePreferences(long companyId, String[] keys)
		throws PortalException {

		getService().removePreferences(companyId, keys);
	}

	/**
	 * Updates the company
	 *
	 * @param companyId the primary key of the company
	 * @param virtualHost the company's virtual host name
	 * @param mx the company's mail domain
	 * @param maxUsers the max number of company users (optionally
	 <code>0</code>)
	 * @param active whether the company is active
	 * @return the company with the primary key
	 */
	public static Company updateCompany(
			long companyId, String virtualHost, String mx, int maxUsers,
			boolean active)
		throws PortalException {

		return getService().updateCompany(
			companyId, virtualHost, mx, maxUsers, active);
	}

	/**
	 * Updates the company with additional account information.
	 *
	 * @param companyId the primary key of the company
	 * @param virtualHost the company's virtual host name
	 * @param mx the company's mail domain
	 * @param homeURL the company's home URL (optionally <code>null</code>)
	 * @param hasLogo if the company has a custom logo
	 * @param logoBytes the new logo image data
	 * @param name the company's account name (optionally <code>null</code>)
	 * @param legalName the company's account legal name (optionally
	 <code>null</code>)
	 * @param legalId the company's account legal ID (optionally
	 <code>null</code>)
	 * @param legalType the company's account legal type (optionally
	 <code>null</code>)
	 * @param sicCode the company's account SIC code (optionally
	 <code>null</code>)
	 * @param tickerSymbol the company's account ticker symbol (optionally
	 <code>null</code>)
	 * @param industry the the company's account industry (optionally
	 <code>null</code>)
	 * @param type the company's account type (optionally <code>null</code>)
	 * @param size the company's account size (optionally <code>null</code>)
	 * @return the the company with the primary key
	 */
	public static Company updateCompany(
			long companyId, String virtualHost, String mx, String homeURL,
			boolean hasLogo, byte[] logoBytes, String name, String legalName,
			String legalId, String legalType, String sicCode,
			String tickerSymbol, String industry, String type, String size)
		throws PortalException {

		return getService().updateCompany(
			companyId, virtualHost, mx, homeURL, hasLogo, logoBytes, name,
			legalName, legalId, legalType, sicCode, tickerSymbol, industry,
			type, size);
	}

	/**
	 * Updates the company with addition information.
	 *
	 * @param companyId the primary key of the company
	 * @param virtualHost the company's virtual host name
	 * @param mx the company's mail domain
	 * @param homeURL the company's home URL (optionally <code>null</code>)
	 * @param hasLogo if the company has a custom logo
	 * @param logoBytes the new logo image data
	 * @param name the company's account name (optionally <code>null</code>)
	 * @param legalName the company's account legal name (optionally
	 <code>null</code>)
	 * @param legalId the company's accout legal ID (optionally
	 <code>null</code>)
	 * @param legalType the company's account legal type (optionally
	 <code>null</code>)
	 * @param sicCode the company's account SIC code (optionally
	 <code>null</code>)
	 * @param tickerSymbol the company's account ticker symbol (optionally
	 <code>null</code>)
	 * @param industry the the company's account industry (optionally
	 <code>null</code>)
	 * @param type the company's account type (optionally <code>null</code>)
	 * @param size the company's account size (optionally <code>null</code>)
	 * @param languageId the ID of the company's default user's language
	 * @param timeZoneId the ID of the company's default user's time zone
	 * @param addresses the company's addresses
	 * @param emailAddresses the company's email addresses
	 * @param phones the company's phone numbers
	 * @param websites the company's websites
	 * @param unicodeProperties the company's properties
	 * @return the company with the primary key
	 */
	public static Company updateCompany(
			long companyId, String virtualHost, String mx, String homeURL,
			boolean hasLogo, byte[] logoBytes, String name, String legalName,
			String legalId, String legalType, String sicCode,
			String tickerSymbol, String industry, String type, String size,
			String languageId, String timeZoneId,
			List<com.liferay.portal.kernel.model.Address> addresses,
			List<com.liferay.portal.kernel.model.EmailAddress> emailAddresses,
			List<com.liferay.portal.kernel.model.Phone> phones,
			List<com.liferay.portal.kernel.model.Website> websites,
			com.liferay.portal.kernel.util.UnicodeProperties unicodeProperties)
		throws PortalException {

		return getService().updateCompany(
			companyId, virtualHost, mx, homeURL, hasLogo, logoBytes, name,
			legalName, legalId, legalType, sicCode, tickerSymbol, industry,
			type, size, languageId, timeZoneId, addresses, emailAddresses,
			phones, websites, unicodeProperties);
	}

	/**
	 * Update the company's display.
	 *
	 * @param companyId the primary key of the company
	 * @param languageId the ID of the company's default user's language
	 * @param timeZoneId the ID of the company's default user's time zone
	 */
	public static void updateDisplay(
			long companyId, String languageId, String timeZoneId)
		throws PortalException {

		getService().updateDisplay(companyId, languageId, timeZoneId);
	}

	/**
	 * Updates the company's logo.
	 *
	 * @param companyId the primary key of the company
	 * @param bytes the bytes of the company's logo image
	 * @return the company with the primary key
	 */
	public static Company updateLogo(long companyId, byte[] bytes)
		throws PortalException {

		return getService().updateLogo(companyId, bytes);
	}

	/**
	 * Updates the company's logo.
	 *
	 * @param companyId the primary key of the company
	 * @param inputStream the input stream of the company's logo image
	 * @return the company with the primary key
	 */
	public static Company updateLogo(long companyId, InputStream inputStream)
		throws PortalException {

		return getService().updateLogo(companyId, inputStream);
	}

	/**
	 * Updates the company's preferences. The company's default properties are
	 * found in portal.properties.
	 *
	 * @param companyId the primary key of the company
	 * @param unicodeProperties the company's properties. See {@link
	 UnicodeProperties}
	 */
	public static void updatePreferences(
			long companyId,
			com.liferay.portal.kernel.util.UnicodeProperties unicodeProperties)
		throws PortalException {

		getService().updatePreferences(companyId, unicodeProperties);
	}

	/**
	 * Updates the company's security properties.
	 *
	 * @param companyId the primary key of the company
	 * @param authType the company's method of authenticating users
	 * @param autoLogin whether to allow users to select the "remember me"
	 feature
	 * @param sendPassword whether to allow users to ask the company to send
	 their passwords
	 * @param strangers whether to allow strangers to create accounts to
	 register themselves in the company
	 * @param strangersWithMx whether to allow strangers to create accounts with
	 email addresses that match the company mail suffix
	 * @param strangersVerify whether to require strangers who create accounts
	 to be verified via email
	 * @param siteLogo whether to to allow site administrators to use their own
	 logo instead of the enterprise logo
	 */
	public static void updateSecurity(
			long companyId, String authType, boolean autoLogin,
			boolean sendPassword, boolean strangers, boolean strangersWithMx,
			boolean strangersVerify, boolean siteLogo)
		throws PortalException {

		getService().updateSecurity(
			companyId, authType, autoLogin, sendPassword, strangers,
			strangersWithMx, strangersVerify, siteLogo);
	}

	public static CompanyService getService() {
		return _service;
	}

	public static void setService(CompanyService service) {
		_service = service;
	}

	private static volatile CompanyService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:-2125220250