/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CompanyLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see CompanyLocalService
 * @generated
 */
public class CompanyLocalServiceWrapper
	implements CompanyLocalService, ServiceWrapper<CompanyLocalService> {

	public CompanyLocalServiceWrapper() {
		this(null);
	}

	public CompanyLocalServiceWrapper(CompanyLocalService companyLocalService) {
		_companyLocalService = companyLocalService;
	}

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
	@Override
	public com.liferay.portal.kernel.model.Company addCompany(
		com.liferay.portal.kernel.model.Company company) {

		return _companyLocalService.addCompany(company);
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
	@Override
	public com.liferay.portal.kernel.model.Company addCompany(
			Long companyId, String webId, String virtualHostname, String mx,
			int maxUsers, boolean active, boolean addDefaultAdminUser,
			String defaultAdminPassword, String defaultAdminScreenName,
			String defaultAdminEmailAddress, String defaultAdminFirstName,
			String defaultAdminMiddleName, String defaultAdminLastName)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _companyLocalService.addCompany(
			companyId, webId, virtualHostname, mx, maxUsers, active,
			addDefaultAdminUser, defaultAdminPassword, defaultAdminScreenName,
			defaultAdminEmailAddress, defaultAdminFirstName,
			defaultAdminMiddleName, defaultAdminLastName);
	}

	@Override
	public com.liferay.portal.kernel.model.Company addDBPartitionCompany(
			long companyId, String name, String virtualHostname, String webId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _companyLocalService.addDBPartitionCompany(
			companyId, name, virtualHostname, webId);
	}

	@Override
	public com.liferay.portal.kernel.model.Company checkCompany(
			com.liferay.portal.kernel.model.Company company, boolean newCompany)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _companyLocalService.checkCompany(company, newCompany);
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
	@Override
	public com.liferay.portal.kernel.model.Company checkCompany(String webId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _companyLocalService.checkCompany(webId);
	}

	/**
	 * Checks if the company has an encryption key. It will create a key if one
	 * does not exist.
	 *
	 * @param companyId the primary key of the company
	 */
	@Override
	public void checkCompanyKey(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_companyLocalService.checkCompanyKey(companyId);
	}

	@Override
	public com.liferay.portal.kernel.model.Company copyDBPartitionCompany(
			long fromCompanyId, Long toCompanyId, String name,
			String virtualHostname, String webId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _companyLocalService.copyDBPartitionCompany(
			fromCompanyId, toCompanyId, name, virtualHostname, webId);
	}

	/**
	 * Creates a new company with the primary key. Does not add the company to the database.
	 *
	 * @param companyId the primary key for the new company
	 * @return the new company
	 */
	@Override
	public com.liferay.portal.kernel.model.Company createCompany(
		long companyId) {

		return _companyLocalService.createCompany(companyId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _companyLocalService.createPersistedModel(primaryKeyObj);
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
	@Override
	public com.liferay.portal.kernel.model.Company deleteCompany(
			com.liferay.portal.kernel.model.Company company)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _companyLocalService.deleteCompany(company);
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
	@Override
	public com.liferay.portal.kernel.model.Company deleteCompany(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _companyLocalService.deleteCompany(companyId);
	}

	/**
	 * Deletes the company's logo.
	 *
	 * @param companyId the primary key of the company
	 * @return the deleted logo's company
	 */
	@Override
	public com.liferay.portal.kernel.model.Company deleteLogo(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _companyLocalService.deleteLogo(companyId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _companyLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _companyLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _companyLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _companyLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _companyLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _companyLocalService.dynamicQuery(dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _companyLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _companyLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _companyLocalService.dynamicQueryCount(dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.kernel.model.Company exportCompany(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _companyLocalService.exportCompany(companyId);
	}

	@Override
	public com.liferay.portal.kernel.model.Company fetchCompany(
		long companyId) {

		return _companyLocalService.fetchCompany(companyId);
	}

	/**
	 * Returns the company with the primary key.
	 *
	 * @param companyId the primary key of the company
	 * @return the company with the primary key, <code>null</code> if a company
	 with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.kernel.model.Company fetchCompanyById(
		long companyId) {

		return _companyLocalService.fetchCompanyById(companyId);
	}

	/**
	 * Returns the company with the virtual host name.
	 *
	 * @param virtualHostname the virtual host name
	 * @return the company with the virtual host name, <code>null</code> if a
	 company with the virtual host could not be found
	 */
	@Override
	public com.liferay.portal.kernel.model.Company fetchCompanyByVirtualHost(
		String virtualHostname) {

		return _companyLocalService.fetchCompanyByVirtualHost(virtualHostname);
	}

	@Override
	public <E extends Exception> void forEachCompany(
			com.liferay.petra.function.UnsafeConsumer
				<com.liferay.portal.kernel.model.Company, E> unsafeConsumer)
		throws E {

		_companyLocalService.forEachCompany(unsafeConsumer);
	}

	@Override
	public <E extends Exception> void forEachCompany(
			com.liferay.petra.function.UnsafeConsumer
				<com.liferay.portal.kernel.model.Company, E> unsafeConsumer,
			java.util.List<com.liferay.portal.kernel.model.Company> companies)
		throws E {

		_companyLocalService.forEachCompany(unsafeConsumer, companies);
	}

	@Override
	public <E extends Exception> void forEachCompanyId(
			com.liferay.petra.function.UnsafeConsumer<Long, E> unsafeConsumer)
		throws E {

		_companyLocalService.forEachCompanyId(unsafeConsumer);
	}

	@Override
	public <E extends Exception> void forEachCompanyId(
			com.liferay.petra.function.UnsafeConsumer<Long, E> unsafeConsumer,
			long[] companyIds)
		throws E {

		_companyLocalService.forEachCompanyId(unsafeConsumer, companyIds);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _companyLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns all the companies.
	 *
	 * @return the companies
	 */
	@Override
	public java.util.List<com.liferay.portal.kernel.model.Company>
		getCompanies() {

		return _companyLocalService.getCompanies();
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
	@Override
	public java.util.List<com.liferay.portal.kernel.model.Company> getCompanies(
		int start, int end) {

		return _companyLocalService.getCompanies(start, end);
	}

	/**
	 * Returns the number of companies.
	 *
	 * @return the number of companies
	 */
	@Override
	public int getCompaniesCount() {
		return _companyLocalService.getCompaniesCount();
	}

	/**
	 * Returns the company with the primary key.
	 *
	 * @param companyId the primary key of the company
	 * @return the company
	 * @throws PortalException if a company with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.kernel.model.Company getCompany(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _companyLocalService.getCompany(companyId);
	}

	/**
	 * Returns the company with the primary key.
	 *
	 * @param companyId the primary key of the company
	 * @return the company with the primary key
	 */
	@Override
	public com.liferay.portal.kernel.model.Company getCompanyById(
			long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _companyLocalService.getCompanyById(companyId);
	}

	/**
	 * Returns the company with the virtual host name.
	 *
	 * @param virtualHostname the company's virtual host name
	 * @return the company with the virtual host name
	 */
	@Override
	public com.liferay.portal.kernel.model.Company getCompanyByVirtualHost(
			String virtualHostname)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _companyLocalService.getCompanyByVirtualHost(virtualHostname);
	}

	/**
	 * Returns the company with the web domain.
	 *
	 * @param webId the company's web domain
	 * @return the company with the web domain
	 */
	@Override
	public com.liferay.portal.kernel.model.Company getCompanyByWebId(
			String webId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _companyLocalService.getCompanyByWebId(webId);
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
	@Override
	public long getCompanyIdByUserId(long userId) throws Exception {
		return _companyLocalService.getCompanyIdByUserId(userId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _companyLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _companyLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _companyLocalService.getPersistedModel(primaryKeyObj);
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
	@Override
	public void removePreferences(long companyId, String[] keys) {
		_companyLocalService.removePreferences(companyId, keys);
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
	@Override
	public com.liferay.portal.kernel.search.Hits search(
		long companyId, long userId, String keywords, int start, int end) {

		return _companyLocalService.search(
			companyId, userId, keywords, start, end);
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
	@Override
	public com.liferay.portal.kernel.search.Hits search(
		long companyId, long userId, String portletId, long groupId,
		String type, String keywords, int start, int end) {

		return _companyLocalService.search(
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
	@Override
	public com.liferay.portal.kernel.model.Company updateCompany(
		com.liferay.portal.kernel.model.Company company) {

		return _companyLocalService.updateCompany(company);
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
	@Override
	public com.liferay.portal.kernel.model.Company updateCompany(
			long companyId, String virtualHostname, String mx, int maxUsers,
			boolean active)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _companyLocalService.updateCompany(
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
	@Override
	public com.liferay.portal.kernel.model.Company updateCompany(
			long companyId, String virtualHostname, String mx, String homeURL,
			boolean hasLogo, byte[] logoBytes, String name, String legalName,
			String legalId, String legalType, String sicCode,
			String tickerSymbol, String industry, String type, String size)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _companyLocalService.updateCompany(
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
	@Override
	public void updateDisplay(
			long companyId, String languageId, String timeZoneId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_companyLocalService.updateDisplay(companyId, languageId, timeZoneId);
	}

	@Override
	public void updateDisplayGroupNames(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_companyLocalService.updateDisplayGroupNames(companyId);
	}

	@Override
	public com.liferay.portal.kernel.model.Company updateIndexNameNext(
			long companyId, String indexNameNext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _companyLocalService.updateIndexNameNext(
			companyId, indexNameNext);
	}

	@Override
	public com.liferay.portal.kernel.model.Company updateIndexNames(
			long companyId, String indexNameCurrent, String indexNameNext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _companyLocalService.updateIndexNames(
			companyId, indexNameCurrent, indexNameNext);
	}

	/**
	 * Updates the company's logo.
	 *
	 * @param companyId the primary key of the company
	 * @param bytes the bytes of the company's logo image
	 * @return the company with the primary key
	 */
	@Override
	public com.liferay.portal.kernel.model.Company updateLogo(
			long companyId, byte[] bytes)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _companyLocalService.updateLogo(companyId, bytes);
	}

	/**
	 * Updates the company's logo.
	 *
	 * @param companyId the primary key of the company
	 * @param file the file of the company's logo image
	 * @return the company with the primary key
	 */
	@Override
	public com.liferay.portal.kernel.model.Company updateLogo(
			long companyId, java.io.File file)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _companyLocalService.updateLogo(companyId, file);
	}

	/**
	 * Update the company's logo.
	 *
	 * @param companyId the primary key of the company
	 * @param inputStream the input stream of the company's logo image
	 * @return the company with the primary key
	 */
	@Override
	public com.liferay.portal.kernel.model.Company updateLogo(
			long companyId, java.io.InputStream inputStream)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _companyLocalService.updateLogo(companyId, inputStream);
	}

	/**
	 * Updates the company's preferences. The company's default properties are
	 * found in portal.properties.
	 *
	 * @param companyId the primary key of the company
	 * @param unicodeProperties the company's properties. See {@link
	 UnicodeProperties}
	 */
	@Override
	public void updatePreferences(
			long companyId,
			com.liferay.portal.kernel.util.UnicodeProperties unicodeProperties)
		throws com.liferay.portal.kernel.exception.PortalException {

		_companyLocalService.updatePreferences(companyId, unicodeProperties);
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
	@Override
	public void updateSecurity(
		long companyId, String authType, boolean autoLogin,
		boolean sendPassword, boolean strangers, boolean strangersWithMx,
		boolean strangersVerify, boolean siteLogo) {

		_companyLocalService.updateSecurity(
			companyId, authType, autoLogin, sendPassword, strangers,
			strangersWithMx, strangersVerify, siteLogo);
	}

	@Override
	public void validateCompany(
			String webId, String virtualHostname, String mx, int maxUsers)
		throws com.liferay.portal.kernel.exception.PortalException {

		_companyLocalService.validateCompany(
			webId, virtualHostname, mx, maxUsers);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _companyLocalService.getBasePersistence();
	}

	@Override
	public CompanyLocalService getWrappedService() {
		return _companyLocalService;
	}

	@Override
	public void setWrappedService(CompanyLocalService companyLocalService) {
		_companyLocalService = companyLocalService;
	}

	private CompanyLocalService _companyLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-699430741