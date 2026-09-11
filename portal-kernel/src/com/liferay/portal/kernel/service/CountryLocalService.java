/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.CountryLocalization;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for Country. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see CountryLocalServiceUtil
 * @generated
 */
@CTAware
@OSGiBeanProperties(
	property = {"model.class.name=com.liferay.portal.kernel.model.Country"}
)
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface CountryLocalService
	extends BaseLocalService, CTService<Country>, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portal.service.impl.CountryLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the country local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link CountryLocalServiceUtil} if injection and service tracking are not available.
	 */

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
	@Indexable(type = IndexableType.REINDEX)
	public Country addCountry(Country country);

	@Indexable(type = IndexableType.REINDEX)
	public Country addCountry(
			String externalReferenceCode, String a2, String a3, boolean active,
			boolean billingAllowed, String idd, String name, String number,
			double position, boolean shippingAllowed, boolean subjectToVAT,
			boolean zipRequired, ServiceContext serviceContext)
		throws PortalException;

	public CountryLocalization addCountryLocalization(
			Country country, String languageId, String title)
		throws PortalException;

	/**
	 * Creates a new country with the primary key. Does not add the country to the database.
	 *
	 * @param countryId the primary key for the new country
	 * @return the new country
	 */
	@Transactional(enabled = false)
	public Country createCountry(long countryId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	public void deleteCompanyCountries(long companyId) throws PortalException;

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
	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public Country deleteCountry(Country country) throws PortalException;

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
	@Indexable(type = IndexableType.DELETE)
	public Country deleteCountry(long countryId) throws PortalException;

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.CountryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.CountryModelImpl</code>.
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

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Country fetchCountry(long countryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Country fetchCountryByA2(long companyId, String a2);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Country fetchCountryByA3(long companyId, String a3);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Country fetchCountryByExternalReferenceCode(
		String externalReferenceCode, long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Country fetchCountryByName(long companyId, String name);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Country fetchCountryByNumber(long companyId, String number);

	/**
	 * Returns the country with the matching UUID and company.
	 *
	 * @param uuid the country's UUID
	 * @param companyId the primary key of the company
	 * @return the matching country, or <code>null</code> if a matching country could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Country fetchCountryByUuidAndCompanyId(String uuid, long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CountryLocalization fetchCountryLocalization(
		long countryId, String languageId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Country> getCompanyCountries(long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Country> getCompanyCountries(long companyId, boolean active);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Country> getCompanyCountries(
		long companyId, boolean active, int start, int end,
		OrderByComparator<Country> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Country> getCompanyCountries(
		long companyId, int start, int end,
		OrderByComparator<Country> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCompanyCountriesCount(long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCompanyCountriesCount(long companyId, boolean active);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Country> getCountries(int start, int end);

	/**
	 * Returns the number of countries.
	 *
	 * @return the number of countries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCountriesCount();

	/**
	 * Returns the country with the primary key.
	 *
	 * @param countryId the primary key of the country
	 * @return the country
	 * @throws PortalException if a country with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Country getCountry(long countryId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Country getCountryByA2(long companyId, String a2)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Country getCountryByA3(long companyId, String a3)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Country getCountryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Country getCountryByName(long companyId, String name)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Country getCountryByNumber(long companyId, String number)
		throws PortalException;

	/**
	 * Returns the country with the matching UUID and company.
	 *
	 * @param uuid the country's UUID
	 * @param companyId the primary key of the company
	 * @return the matching country
	 * @throws PortalException if a matching country could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Country getCountryByUuidAndCompanyId(String uuid, long companyId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public CountryLocalization getCountryLocalization(
			long countryId, String languageId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<CountryLocalization> getCountryLocalizations(long countryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Country getOrAddEmptyCountry(
			String externalReferenceCode, String a2, String a3, long companyId,
			String name, long userId)
		throws PortalException;

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

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public BaseModelSearchResult<Country> searchCountries(
			long companyId, Boolean active, String keywords, int start, int end,
			OrderByComparator<Country> orderByComparator)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public Country updateActive(long countryId, boolean active)
		throws PortalException;

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
	@Indexable(type = IndexableType.REINDEX)
	public Country updateCountry(Country country);

	@Indexable(type = IndexableType.REINDEX)
	public Country updateCountry(
			String externalReferenceCode, long countryId, String a2, String a3,
			boolean active, boolean billingAllowed, String idd, String name,
			String number, double position, boolean shippingAllowed,
			boolean subjectToVAT)
		throws PortalException;

	public CountryLocalization updateCountryLocalization(
			Country country, String languageId, String title)
		throws PortalException;

	public List<CountryLocalization> updateCountryLocalizations(
			Country country, Map<String, String> titleMap)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public Country updateGroupFilterEnabled(
			long countryId, boolean groupFilterEnabled)
		throws PortalException;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<Country> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<Country> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<Country>, R, E> updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:987180304