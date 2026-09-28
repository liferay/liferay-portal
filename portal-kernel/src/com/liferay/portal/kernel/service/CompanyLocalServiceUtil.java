/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.InputStream;
import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for Company. This utility wraps
 * <code>com.liferay.portal.service.impl.CompanyLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see CompanyLocalService
 * @generated
 */
public class CompanyLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.service.impl.CompanyLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
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
	public static Company addCompany(Company company) {
		return getService().addCompany(company);
	}

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
	public static Company addCompany(
			Long companyId, String webId, String virtualHostname, String mx,
			int maxUsers, boolean active, boolean addDefaultAdminUser,
			String defaultAdminPassword, String defaultAdminScreenName,
			String defaultAdminEmailAddress, String defaultAdminFirstName,
			String defaultAdminMiddleName, String defaultAdminLastName)
		throws PortalException {

		return getService().addCompany(
			companyId, webId, virtualHostname, mx, maxUsers, active,
			addDefaultAdminUser, defaultAdminPassword, defaultAdminScreenName,
			defaultAdminEmailAddress, defaultAdminFirstName,
			defaultAdminMiddleName, defaultAdminLastName);
	}

	public static Company addDBPartitionCompany(
			long companyId, String name, String virtualHostname, String webId)
		throws PortalException {

		return getService().addDBPartitionCompany(
			companyId, name, virtualHostname, webId);
	}

	public static Company checkCompany(Company company, boolean newCompany)
		throws PortalException {

		return getService().checkCompany(company, newCompany);
	}

	/**
	 * Returns the company with the web domain.
	 *
	 * The method sets mail domain to the web domain to the default name set in
	 * portal.properties
	 *
	 * @param webId the company's web domain
	 * @return the company with the web domain
	 */
	public static Company checkCompany(String webId) throws PortalException {
		return getService().checkCompany(webId);
	}

	/**
	 * Checks if the company has an encryption key. It will create a key if one
	 * does not exist.
	 *
	 * @param companyId the primary key of the company
	 */
	public static void checkCompanyKey(long companyId) throws PortalException {
		getService().checkCompanyKey(companyId);
	}

	public static Company copyDBPartitionCompany(
			long fromCompanyId, Long toCompanyId, String name,
			String virtualHostname, String webId)
		throws PortalException {

		return getService().copyDBPartitionCompany(
			fromCompanyId, toCompanyId, name, virtualHostname, webId);
	}

	/**
	 * Creates a new company with the primary key. Does not add the company to the database.
	 *
	 * @param companyId the primary key for the new company
	 * @return the new company
	 */
	public static Company createCompany(long companyId) {
		return getService().createCompany(companyId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

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
	public static Company deleteCompany(Company company)
		throws PortalException {

		return getService().deleteCompany(company);
	}

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
	public static Company deleteCompany(long companyId) throws PortalException {
		return getService().deleteCompany(companyId);
	}

	/**
	 * Deletes the company's logo.
	 *
	 * @param companyId the primary key of the company
	 * @return the deleted logo's company
	 */
	public static Company deleteLogo(long companyId) throws PortalException {
		return getService().deleteLogo(companyId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static <T> T dslQuery(DSLQuery dslQuery) {
		return getService().dslQuery(dslQuery);
	}

	public static int dslQueryCount(DSLQuery dslQuery) {
		return getService().dslQueryCount(dslQuery);
	}

	public static DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
	}

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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
	}

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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static Company exportCompany(long companyId) throws PortalException {
		return getService().exportCompany(companyId);
	}

	public static Company fetchCompany(long companyId) {
		return getService().fetchCompany(companyId);
	}

	/**
	 * Returns the company with the primary key.
	 *
	 * @param companyId the primary key of the company
	 * @return the company with the primary key, <code>null</code> if a company
	 with the primary key could not be found
	 */
	public static Company fetchCompanyById(long companyId) {
		return getService().fetchCompanyById(companyId);
	}

	/**
	 * Returns the company with the virtual host name.
	 *
	 * @param virtualHostname the virtual host name
	 * @return the company with the virtual host name, <code>null</code> if a
	 company with the virtual host could not be found
	 */
	public static Company fetchCompanyByVirtualHost(String virtualHostname) {
		return getService().fetchCompanyByVirtualHost(virtualHostname);
	}

	public static <E extends Exception> void forEachCompany(
			com.liferay.petra.function.UnsafeConsumer<Company, E>
				unsafeConsumer)
		throws E {

		getService().forEachCompany(unsafeConsumer);
	}

	public static <E extends Exception> void forEachCompany(
			com.liferay.petra.function.UnsafeConsumer<Company, E>
				unsafeConsumer,
			List<Company> companies)
		throws E {

		getService().forEachCompany(unsafeConsumer, companies);
	}

	public static <E extends Exception> void forEachCompanyId(
			com.liferay.petra.function.UnsafeConsumer<Long, E> unsafeConsumer)
		throws E {

		getService().forEachCompanyId(unsafeConsumer);
	}

	public static <E extends Exception> void forEachCompanyId(
			com.liferay.petra.function.UnsafeConsumer<Long, E> unsafeConsumer,
			long[] companyIds)
		throws E {

		getService().forEachCompanyId(unsafeConsumer, companyIds);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
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
	public static List<Company> getCompanies(int start, int end) {
		return getService().getCompanies(start, end);
	}

	/**
	 * Returns the number of companies.
	 *
	 * @return the number of companies
	 */
	public static int getCompaniesCount() {
		return getService().getCompaniesCount();
	}

	/**
	 * Returns the company with the primary key.
	 *
	 * @param companyId the primary key of the company
	 * @return the company
	 * @throws PortalException if a company with the primary key could not be found
	 */
	public static Company getCompany(long companyId) throws PortalException {
		return getService().getCompany(companyId);
	}

	/**
	 * Returns the company with the primary key.
	 *
	 * @param companyId the primary key of the company
	 * @return the company with the primary key
	 */
	public static Company getCompanyById(long companyId)
		throws PortalException {

		return getService().getCompanyById(companyId);
	}

	/**
	 * Returns the company with the virtual host name.
	 *
	 * @param virtualHostname the company's virtual host name
	 * @return the company with the virtual host name
	 */
	public static Company getCompanyByVirtualHost(String virtualHostname)
		throws PortalException {

		return getService().getCompanyByVirtualHost(virtualHostname);
	}

	/**
	 * Returns the company with the web domain.
	 *
	 * @param webId the company's web domain
	 * @return the company with the web domain
	 */
	public static Company getCompanyByWebId(String webId)
		throws PortalException {

		return getService().getCompanyByWebId(webId);
	}

	/**
	 * Returns the user's company.
	 *
	 * @param userId the primary key of the user
	 * @return Returns the first company if there is only one company or the
	 user's company if there are more than one company; <code>0</code>
	 otherwise
	 * @throws Exception if a user with the primary key could not be found
	 */
	public static long getCompanyIdByUserId(long userId) throws Exception {
		return getService().getCompanyIdByUserId(userId);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
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
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
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
	public static void removePreferences(long companyId, String[] keys) {
		getService().removePreferences(companyId, keys);
	}

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
	public static com.liferay.portal.kernel.search.Hits search(
		long companyId, long userId, String keywords, int start, int end) {

		return getService().search(companyId, userId, keywords, start, end);
	}

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
	public static com.liferay.portal.kernel.search.Hits search(
		long companyId, long userId, String portletId, long groupId,
		String type, String keywords, int start, int end) {

		return getService().search(
			companyId, userId, portletId, groupId, type, keywords, start, end);
	}

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
	public static Company updateCompany(Company company) {
		return getService().updateCompany(company);
	}

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
	public static Company updateCompany(
			long companyId, String virtualHostname, String mx, int maxUsers,
			boolean active)
		throws PortalException {

		return getService().updateCompany(
			companyId, virtualHostname, mx, maxUsers, active);
	}

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
	public static Company updateCompany(
			long companyId, String virtualHostname, String mx, String homeURL,
			boolean hasLogo, byte[] logoBytes, String name, String legalName,
			String legalId, String legalType, String sicCode,
			String tickerSymbol, String industry, String type, String size)
		throws PortalException {

		return getService().updateCompany(
			companyId, virtualHostname, mx, homeURL, hasLogo, logoBytes, name,
			legalName, legalId, legalType, sicCode, tickerSymbol, industry,
			type, size);
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

	public static void updateDisplayGroupNames(long companyId)
		throws PortalException {

		getService().updateDisplayGroupNames(companyId);
	}

	public static Company updateIndexNameNext(
			long companyId, String indexNameNext)
		throws PortalException {

		return getService().updateIndexNameNext(companyId, indexNameNext);
	}

	public static Company updateIndexNames(
			long companyId, String indexNameCurrent, String indexNameNext)
		throws PortalException {

		return getService().updateIndexNames(
			companyId, indexNameCurrent, indexNameNext);
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
	 * @param file the file of the company's logo image
	 * @return the company with the primary key
	 */
	public static Company updateLogo(long companyId, java.io.File file)
		throws PortalException {

		return getService().updateLogo(companyId, file);
	}

	/**
	 * Update the company's logo.
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
	public static void updateSecurity(
		long companyId, String authType, boolean autoLogin,
		boolean sendPassword, boolean strangers, boolean strangersWithMx,
		boolean strangersVerify, boolean siteLogo) {

		getService().updateSecurity(
			companyId, authType, autoLogin, sendPassword, strangers,
			strangersWithMx, strangersVerify, siteLogo);
	}

	public static void validateCompany(
			String webId, String virtualHostname, String mx, int maxUsers)
		throws PortalException {

		getService().validateCompany(webId, virtualHostname, mx, maxUsers);
	}

	public static CompanyLocalService getService() {
		return _service;
	}

	public static void setService(CompanyLocalService service) {
		_service = service;
	}

	private static volatile CompanyLocalService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:-561410455