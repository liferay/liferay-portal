/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.messaging.async.Async;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.UnicodeProperties;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for Company. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see CompanyLocalServiceUtil
 * @generated
 */
@OSGiBeanProperties(
	property = {"model.class.name=com.liferay.portal.kernel.model.Company"}
)
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface CompanyLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portal.service.impl.CompanyLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the company local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CompanyLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the company to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CompanyLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param company the company
	 * @return the company that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public Company addCompany(Company company);

	/**
	 * Adds a company with the primary key.
	 *
	 * @param companyId the primary key of the company (optionally <code>null</code> or
	 <code>0</code> to generate a key automatically)
	 * @param webId the the company's web domain
	 * @param virtualHostname the company's virtual host name
	 * @param mx the company's mail domain
	 * @param maxUsers the max number of company users (optionally
	 <code>0</code>)
	 * @param active whether the company is active
	 * @return the company
	 */
	public Company addCompany(
			Long companyId, String webId, String virtualHostname, String mx,
			int maxUsers, boolean active, boolean addDefaultAdminUser,
			String defaultAdminPassword, String defaultAdminScreenName,
			String defaultAdminEmailAddress, String defaultAdminFirstName,
			String defaultAdminMiddleName, String defaultAdminLastName)
		throws PortalException;

	public Company addDBPartitionCompany(
			long companyId, String name, String virtualHostname, String webId)
		throws PortalException;

	public Company checkCompany(Company company, boolean newCompany)
		throws PortalException;

	/**
	 * Returns the company with the web domain.
	 *
	 * The method sets mail domain to the web domain to the default name set in
	 * portal.properties
	 *
	 * @param webId the company's web domain
	 * @return the company with the web domain
	 */
	public Company checkCompany(String webId) throws PortalException;

	/**
	 * Checks if the company has an encryption key. It will create a key if one
	 * does not exist.
	 *
	 * @param companyId the primary key of the company
	 */
	public void checkCompanyKey(long companyId) throws PortalException;

	public Company copyDBPartitionCompany(
			long fromCompanyId, Long toCompanyId, String name,
			String virtualHostname, String webId)
		throws PortalException;

	/**
	 * Creates a new company with the primary key. Does not add the company to the database.
	 *
	 * @param companyId the primary key for the new company
	 * @return the new company
	 */
	@Transactional(enabled = false)
	public Company createCompany(long companyId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the company from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CompanyLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param company the company
	 * @return the company that was removed
	 * @throws PortalException
	 */
	@Indexable(type = IndexableType.DELETE)
	public Company deleteCompany(Company company) throws PortalException;

	/**
	 * Deletes the company with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CompanyLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param companyId the primary key of the company
	 * @return the company that was removed
	 * @throws PortalException if a company with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public Company deleteCompany(long companyId) throws PortalException;

	/**
	 * Deletes the company's logo.
	 *
	 * @param companyId the primary key of the company
	 * @return the deleted logo's company
	 */
	public Company deleteLogo(long companyId) throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> T dslQuery(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int dslQueryCount(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DynamicQuery dynamicQuery();

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery);

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.CompanyModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end);

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.CompanyModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(DynamicQuery dynamicQuery);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(
		DynamicQuery dynamicQuery, Projection projection);

	public Company exportCompany(long companyId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Company fetchCompany(long companyId);

	/**
	 * Returns the company with the primary key.
	 *
	 * @param companyId the primary key of the company
	 * @return the company with the primary key, <code>null</code> if a company
	 with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Company fetchCompanyById(long companyId);

	/**
	 * Returns the company with the virtual host name.
	 *
	 * @param virtualHostname the virtual host name
	 * @return the company with the virtual host name, <code>null</code> if a
	 company with the virtual host could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Company fetchCompanyByVirtualHost(String virtualHostname);

	@Transactional(enabled = false)
	public <E extends Exception> void forEachCompany(
			UnsafeConsumer<Company, E> unsafeConsumer)
		throws E;

	@Transactional(enabled = false)
	public <E extends Exception> void forEachCompany(
			UnsafeConsumer<Company, E> unsafeConsumer, List<Company> companies)
		throws E;

	@Transactional(enabled = false)
	public <E extends Exception> void forEachCompanyId(
			UnsafeConsumer<Long, E> unsafeConsumer)
		throws E;

	@Transactional(enabled = false)
	public <E extends Exception> void forEachCompanyId(
			UnsafeConsumer<Long, E> unsafeConsumer, long[] companyIds)
		throws E;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	/**
	 * Returns all the companies.
	 *
	 * @return the companies
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Company> getCompanies();

	/**
	 * Returns a range of all the companies.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.CompanyModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of companies
	 * @param end the upper bound of the range of companies (not inclusive)
	 * @return the range of companies
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Company> getCompanies(int start, int end);

	/**
	 * Returns the number of companies.
	 *
	 * @return the number of companies
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCompaniesCount();

	/**
	 * Returns the company with the primary key.
	 *
	 * @param companyId the primary key of the company
	 * @return the company
	 * @throws PortalException if a company with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Company getCompany(long companyId) throws PortalException;

	/**
	 * Returns the company with the primary key.
	 *
	 * @param companyId the primary key of the company
	 * @return the company with the primary key
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Company getCompanyById(long companyId) throws PortalException;

	/**
	 * Returns the company with the virtual host name.
	 *
	 * @param virtualHostname the company's virtual host name
	 * @return the company with the virtual host name
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Company getCompanyByVirtualHost(String virtualHostname)
		throws PortalException;

	/**
	 * Returns the company with the web domain.
	 *
	 * @param webId the company's web domain
	 * @return the company with the web domain
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Company getCompanyByWebId(String webId) throws PortalException;

	/**
	 * Returns the user's company.
	 *
	 * @param userId the primary key of the user
	 * @return Returns the first company if there is only one company or the
	 user's company if there are more than one company; <code>0</code>
	 otherwise
	 * @throws Exception if a user with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long getCompanyIdByUserId(long userId) throws Exception;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

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
	public void removePreferences(long companyId, String[] keys);

	/**
	 * Returns an ordered range of all assets that match the keywords in the
	 * company.
	 *
	 * The method is called in {@link
	 * com.liferay.portal.search.PortalOpenSearchImpl} which is not longer used
	 * by the Search portlet.
	 *
	 * @param companyId the primary key of the company
	 * @param userId the primary key of the user
	 * @param keywords the keywords (space separated),which may occur in assets
	 in the company (optionally <code>null</code>)
	 * @param start the lower bound of the range of assets to return
	 * @param end the upper bound of the range of assets to return (not
	 inclusive)
	 * @return the matching assets in the company
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Hits search(
		long companyId, long userId, String keywords, int start, int end);

	/**
	 * Returns an ordered range of all assets that match the keywords in the
	 * portlet within the company.
	 *
	 * @param companyId the primary key of the company
	 * @param userId the primary key of the user
	 * @param portletId the primary key of the portlet (optionally
	 <code>null</code>)
	 * @param groupId the primary key of the group (optionally <code>0</code>)
	 * @param type the mime type of assets to return(optionally
	 <code>null</code>)
	 * @param keywords the keywords (space separated), which may occur in any
	 assets in the portlet (optionally <code>null</code>)
	 * @param start the lower bound of the range of assets to return
	 * @param end the upper bound of the range of assets to return (not
	 inclusive)
	 * @return the matching assets in the portlet within the company
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Hits search(
		long companyId, long userId, String portletId, long groupId,
		String type, String keywords, int start, int end);

	/**
	 * Updates the company in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CompanyLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param company the company
	 * @return the company that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public Company updateCompany(Company company);

	/**
	 * Updates the company.
	 *
	 * @param companyId the primary key of the company
	 * @param virtualHostname the company's virtual host name
	 * @param mx the company's mail domain
	 * @param maxUsers the max number of company users (optionally
	 <code>0</code>)
	 * @param active whether the company is active
	 * @return the company with the primary key
	 */
	public Company updateCompany(
			long companyId, String virtualHostname, String mx, int maxUsers,
			boolean active)
		throws PortalException;

	/**
	 * Update the company with additional account information.
	 *
	 * @param companyId the primary key of the company
	 * @param virtualHostname the company's virtual host name
	 * @param mx the company's mail domain
	 * @param homeURL the company's home URL (optionally <code>null</code>)
	 * @param hasLogo if the company has a custom logo
	 * @param logoBytes the new logo image data
	 * @param name the company's account name(optionally <code>null</code>)
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
	 * @param industry the company's account industry (optionally
	 <code>null</code>)
	 * @param type the company's account type (optionally <code>null</code>)
	 * @param size the company's account size (optionally <code>null</code>)
	 * @return the company with the primary key
	 */
	public Company updateCompany(
			long companyId, String virtualHostname, String mx, String homeURL,
			boolean hasLogo, byte[] logoBytes, String name, String legalName,
			String legalId, String legalType, String sicCode,
			String tickerSymbol, String industry, String type, String size)
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

	@Async
	public void updateDisplayGroupNames(long companyId) throws PortalException;

	public Company updateIndexNameNext(long companyId, String indexNameNext)
		throws PortalException;

	public Company updateIndexNames(
			long companyId, String indexNameCurrent, String indexNameNext)
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
	 * @param file the file of the company's logo image
	 * @return the company with the primary key
	 */
	public Company updateLogo(long companyId, File file) throws PortalException;

	/**
	 * Update the company's logo.
	 *
	 * @param companyId the primary key of the company
	 * @param inputStream the input stream of the company's logo image
	 * @return the company with the primary key
	 */
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
	 their password
	 * @param strangers whether to allow strangers to create accounts register
	 themselves in the company
	 * @param strangersWithMx whether to allow strangers to create accounts with
	 email addresses that match the company mail suffix
	 * @param strangersVerify whether to require strangers who create accounts
	 to be verified via email
	 * @param siteLogo whether to allow site administrators to use their own
	 logo instead of the enterprise logo
	 */
	public void updateSecurity(
		long companyId, String authType, boolean autoLogin,
		boolean sendPassword, boolean strangers, boolean strangersWithMx,
		boolean strangersVerify, boolean siteLogo);

}
// LIFERAY-SERVICE-BUILDER-HASH:247372254