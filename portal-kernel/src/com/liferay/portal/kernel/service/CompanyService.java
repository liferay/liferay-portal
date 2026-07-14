/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.jsonwebservice.JSONWebServiceMode;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.EmailAddress;
import com.liferay.portal.kernel.model.Phone;
import com.liferay.portal.kernel.model.Website;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.UnicodeProperties;

import java.io.InputStream;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for Company. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see CompanyServiceUtil
 * @generated
 */
@AccessControlled
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface CompanyService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portal.service.impl.CompanyServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the company remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CompanyServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds a company.
	 *
	 * @param companyId the primary key of the company (optionally <code>null</code> or
	 *         <code>0</code> to generate a key automatically)
	 * @param webId the company's web domain
	 * @param virtualHost the company's virtual host name
	 * @param mx the company's mail domain
	 * @param maxUsers the max number of company users (optionally
	 <code>0</code>)
	 * @param active whether the company is active
	 * @return the company
	 */
	@JSONWebService(mode = JSONWebServiceMode.IGNORE)
	public Company addCompany(
			long companyId, String webId, String virtualHost, String mx,
			int maxUsers, boolean active)
		throws PortalException;

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
	@JSONWebService(mode = JSONWebServiceMode.IGNORE)
	public Company addCompany(
			Long companyId, String webId, String virtualHost, String mx,
			int maxUsers, boolean active, String defaultAdminPassword,
			String defaultAdminScreenName, String defaultAdminEmailAddress,
			String defaultAdminFirstName, String defaultAdminMiddleName,
			String defaultAdminLastName)
		throws PortalException;

	@JSONWebService(mode = JSONWebServiceMode.IGNORE)
	public Company addDBPartitionCompany(
			String schemaName, String name, String virtualHost, String webId)
		throws PortalException;

	@JSONWebService(mode = JSONWebServiceMode.IGNORE)
	public Company deleteCompany(long companyId) throws PortalException;

	/**
	 * Deletes the company's logo.
	 *
	 * @param companyId the primary key of the company
	 */
	public void deleteLogo(long companyId) throws PortalException;

	public void forEachCompany(
			UnsafeConsumer<Company, Exception> unsafeConsumer)
		throws Exception;

	/**
	 * Returns all the companies.
	 *
	 * @return the companies
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Company> getCompanies();

	/**
	 * Returns the company with the primary key.
	 *
	 * @param companyId the primary key of the company
	 * @return Returns the company with the primary key
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Company getCompanyById(long companyId) throws PortalException;

	/**
	 * Returns the company with the virtual host name.
	 *
	 * @param virtualHost the company's virtual host name
	 * @return Returns the company with the virtual host name
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Company getCompanyByVirtualHost(String virtualHost)
		throws PortalException;

	/**
	 * Returns the company with the web domain.
	 *
	 * @param webId the company's web domain
	 * @return Returns the company with the web domain
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Company getCompanyByWebId(String webId) throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

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
	public void removePreferences(long companyId, String[] keys)
		throws PortalException;

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
	public Company updateCompany(
			long companyId, String virtualHost, String mx, int maxUsers,
			boolean active)
		throws PortalException;

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
	public Company updateCompany(
			long companyId, String virtualHost, String mx, String homeURL,
			boolean hasLogo, byte[] logoBytes, String name, String legalName,
			String legalId, String legalType, String sicCode,
			String tickerSymbol, String industry, String type, String size)
		throws PortalException;

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
	@JSONWebService(mode = JSONWebServiceMode.IGNORE)
	public Company updateCompany(
			long companyId, String virtualHost, String mx, String homeURL,
			boolean hasLogo, byte[] logoBytes, String name, String legalName,
			String legalId, String legalType, String sicCode,
			String tickerSymbol, String industry, String type, String size,
			String languageId, String timeZoneId, List<Address> addresses,
			List<EmailAddress> emailAddresses, List<Phone> phones,
			List<Website> websites, UnicodeProperties unicodeProperties)
		throws PortalException;

	/**
	 * Update the company's display.
	 *
	 * @param companyId the primary key of the company
	 * @param languageId the ID of the company's default user's language
	 * @param timeZoneId the ID of the company's default user's time zone
	 */
	public void updateDisplay(
			long companyId, String languageId, String timeZoneId)
		throws PortalException;

	/**
	 * Updates the company's logo.
	 *
	 * @param companyId the primary key of the company
	 * @param bytes the bytes of the company's logo image
	 * @return the company with the primary key
	 */
	public Company updateLogo(long companyId, byte[] bytes)
		throws PortalException;

	/**
	 * Updates the company's logo.
	 *
	 * @param companyId the primary key of the company
	 * @param inputStream the input stream of the company's logo image
	 * @return the company with the primary key
	 */
	@JSONWebService(mode = JSONWebServiceMode.IGNORE)
	public Company updateLogo(long companyId, InputStream inputStream)
		throws PortalException;

	/**
	 * Updates the company's preferences. The company's default properties are
	 * found in portal.properties.
	 *
	 * @param companyId the primary key of the company
	 * @param unicodeProperties the company's properties. See {@link
	 UnicodeProperties}
	 */
	public void updatePreferences(
			long companyId, UnicodeProperties unicodeProperties)
		throws PortalException;

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
	@JSONWebService(mode = JSONWebServiceMode.IGNORE)
	public void updateSecurity(
			long companyId, String authType, boolean autoLogin,
			boolean sendPassword, boolean strangers, boolean strangersWithMx,
			boolean strangersVerify, boolean siteLogo)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:1229729915