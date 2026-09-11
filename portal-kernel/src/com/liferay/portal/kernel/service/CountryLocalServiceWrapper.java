/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CountryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see CountryLocalService
 * @generated
 */
public class CountryLocalServiceWrapper
	implements CountryLocalService, ServiceWrapper<CountryLocalService> {

	public CountryLocalServiceWrapper() {
		this(null);
	}

	public CountryLocalServiceWrapper(CountryLocalService countryLocalService) {
		_countryLocalService = countryLocalService;
	}

	/**
	 * Adds the country to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CountryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param country the country
	 * @return the country that was added
	 */
	@Override
	public Country addCountry(Country country) {
		return _countryLocalService.addCountry(country);
	}

	@Override
	public Country addCountry(
			String externalReferenceCode, String a2, String a3, boolean active,
			boolean billingAllowed, String idd, String name, String number,
			double position, boolean shippingAllowed, boolean subjectToVAT,
			boolean zipRequired, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _countryLocalService.addCountry(
			externalReferenceCode, a2, a3, active, billingAllowed, idd, name,
			number, position, shippingAllowed, subjectToVAT, zipRequired,
			serviceContext);
	}

	@Override
	public com.liferay.portal.kernel.model.CountryLocalization
			addCountryLocalization(
				Country country, String languageId, String title)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _countryLocalService.addCountryLocalization(
			country, languageId, title);
	}

	/**
	 * Creates a new country with the primary key. Does not add the country to the database.
	 *
	 * @param countryId the primary key for the new country
	 * @return the new country
	 */
	@Override
	public Country createCountry(long countryId) {
		return _countryLocalService.createCountry(countryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _countryLocalService.createPersistedModel(primaryKeyObj);
	}

	@Override
	public void deleteCompanyCountries(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_countryLocalService.deleteCompanyCountries(companyId);
	}

	/**
	 * Deletes the country from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CountryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param country the country
	 * @return the country that was removed
	 * @throws PortalException
	 */
	@Override
	public Country deleteCountry(Country country)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _countryLocalService.deleteCountry(country);
	}

	/**
	 * Deletes the country with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CountryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param countryId the primary key of the country
	 * @return the country that was removed
	 * @throws PortalException if a country with the primary key could not be found
	 */
	@Override
	public Country deleteCountry(long countryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _countryLocalService.deleteCountry(countryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _countryLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _countryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _countryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _countryLocalService.dynamicQuery();
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

		return _countryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.CountryModelImpl</code>.
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

		return _countryLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.CountryModelImpl</code>.
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

		return _countryLocalService.dynamicQuery(
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

		return _countryLocalService.dynamicQueryCount(dynamicQuery);
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

		return _countryLocalService.dynamicQueryCount(dynamicQuery, projection);
	}

	@Override
	public Country fetchCountry(long countryId) {
		return _countryLocalService.fetchCountry(countryId);
	}

	@Override
	public Country fetchCountryByA2(long companyId, String a2) {
		return _countryLocalService.fetchCountryByA2(companyId, a2);
	}

	@Override
	public Country fetchCountryByA3(long companyId, String a3) {
		return _countryLocalService.fetchCountryByA3(companyId, a3);
	}

	@Override
	public Country fetchCountryByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return _countryLocalService.fetchCountryByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	@Override
	public Country fetchCountryByName(long companyId, String name) {
		return _countryLocalService.fetchCountryByName(companyId, name);
	}

	@Override
	public Country fetchCountryByNumber(long companyId, String number) {
		return _countryLocalService.fetchCountryByNumber(companyId, number);
	}

	/**
	 * Returns the country with the matching UUID and company.
	 *
	 * @param uuid the country's UUID
	 * @param companyId the primary key of the company
	 * @return the matching country, or <code>null</code> if a matching country could not be found
	 */
	@Override
	public Country fetchCountryByUuidAndCompanyId(String uuid, long companyId) {
		return _countryLocalService.fetchCountryByUuidAndCompanyId(
			uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.model.CountryLocalization
		fetchCountryLocalization(long countryId, String languageId) {

		return _countryLocalService.fetchCountryLocalization(
			countryId, languageId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _countryLocalService.getActionableDynamicQuery();
	}

	@Override
	public java.util.List<Country> getCompanyCountries(long companyId) {
		return _countryLocalService.getCompanyCountries(companyId);
	}

	@Override
	public java.util.List<Country> getCompanyCountries(
		long companyId, boolean active) {

		return _countryLocalService.getCompanyCountries(companyId, active);
	}

	@Override
	public java.util.List<Country> getCompanyCountries(
		long companyId, boolean active, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Country>
			orderByComparator) {

		return _countryLocalService.getCompanyCountries(
			companyId, active, start, end, orderByComparator);
	}

	@Override
	public java.util.List<Country> getCompanyCountries(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<Country>
			orderByComparator) {

		return _countryLocalService.getCompanyCountries(
			companyId, start, end, orderByComparator);
	}

	@Override
	public int getCompanyCountriesCount(long companyId) {
		return _countryLocalService.getCompanyCountriesCount(companyId);
	}

	@Override
	public int getCompanyCountriesCount(long companyId, boolean active) {
		return _countryLocalService.getCompanyCountriesCount(companyId, active);
	}

	/**
	 * Returns a range of all the countries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.CountryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of countries
	 * @param end the upper bound of the range of countries (not inclusive)
	 * @return the range of countries
	 */
	@Override
	public java.util.List<Country> getCountries(int start, int end) {
		return _countryLocalService.getCountries(start, end);
	}

	/**
	 * Returns the number of countries.
	 *
	 * @return the number of countries
	 */
	@Override
	public int getCountriesCount() {
		return _countryLocalService.getCountriesCount();
	}

	/**
	 * Returns the country with the primary key.
	 *
	 * @param countryId the primary key of the country
	 * @return the country
	 * @throws PortalException if a country with the primary key could not be found
	 */
	@Override
	public Country getCountry(long countryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _countryLocalService.getCountry(countryId);
	}

	@Override
	public Country getCountryByA2(long companyId, String a2)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _countryLocalService.getCountryByA2(companyId, a2);
	}

	@Override
	public Country getCountryByA3(long companyId, String a3)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _countryLocalService.getCountryByA3(companyId, a3);
	}

	@Override
	public Country getCountryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _countryLocalService.getCountryByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	@Override
	public Country getCountryByName(long companyId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _countryLocalService.getCountryByName(companyId, name);
	}

	@Override
	public Country getCountryByNumber(long companyId, String number)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _countryLocalService.getCountryByNumber(companyId, number);
	}

	/**
	 * Returns the country with the matching UUID and company.
	 *
	 * @param uuid the country's UUID
	 * @param companyId the primary key of the company
	 * @return the matching country
	 * @throws PortalException if a matching country could not be found
	 */
	@Override
	public Country getCountryByUuidAndCompanyId(String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _countryLocalService.getCountryByUuidAndCompanyId(
			uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.model.CountryLocalization
			getCountryLocalization(long countryId, String languageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _countryLocalService.getCountryLocalization(
			countryId, languageId);
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.model.CountryLocalization>
		getCountryLocalizations(long countryId) {

		return _countryLocalService.getCountryLocalizations(countryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _countryLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _countryLocalService.getIndexableActionableDynamicQuery();
	}

	@Override
	public Country getOrAddEmptyCountry(
			String externalReferenceCode, String a2, String a3, long companyId,
			String name, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _countryLocalService.getOrAddEmptyCountry(
			externalReferenceCode, a2, a3, companyId, name, userId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _countryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _countryLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult<Country>
			searchCountries(
				long companyId, Boolean active, String keywords, int start,
				int end,
				com.liferay.portal.kernel.util.OrderByComparator<Country>
					orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _countryLocalService.searchCountries(
			companyId, active, keywords, start, end, orderByComparator);
	}

	@Override
	public Country updateActive(long countryId, boolean active)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _countryLocalService.updateActive(countryId, active);
	}

	/**
	 * Updates the country in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CountryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param country the country
	 * @return the country that was updated
	 */
	@Override
	public Country updateCountry(Country country) {
		return _countryLocalService.updateCountry(country);
	}

	@Override
	public Country updateCountry(
			String externalReferenceCode, long countryId, String a2, String a3,
			boolean active, boolean billingAllowed, String idd, String name,
			String number, double position, boolean shippingAllowed,
			boolean subjectToVAT)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _countryLocalService.updateCountry(
			externalReferenceCode, countryId, a2, a3, active, billingAllowed,
			idd, name, number, position, shippingAllowed, subjectToVAT);
	}

	@Override
	public com.liferay.portal.kernel.model.CountryLocalization
			updateCountryLocalization(
				Country country, String languageId, String title)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _countryLocalService.updateCountryLocalization(
			country, languageId, title);
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.model.CountryLocalization>
			updateCountryLocalizations(
				Country country, java.util.Map<String, String> titleMap)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _countryLocalService.updateCountryLocalizations(
			country, titleMap);
	}

	@Override
	public Country updateGroupFilterEnabled(
			long countryId, boolean groupFilterEnabled)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _countryLocalService.updateGroupFilterEnabled(
			countryId, groupFilterEnabled);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _countryLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<Country> getCTPersistence() {
		return _countryLocalService.getCTPersistence();
	}

	@Override
	public Class<Country> getModelClass() {
		return _countryLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<Country>, R, E> updateUnsafeFunction)
		throws E {

		return _countryLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public CountryLocalService getWrappedService() {
		return _countryLocalService;
	}

	@Override
	public void setWrappedService(CountryLocalService countryLocalService) {
		_countryLocalService = countryLocalService;
	}

	private CountryLocalService _countryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:668921470