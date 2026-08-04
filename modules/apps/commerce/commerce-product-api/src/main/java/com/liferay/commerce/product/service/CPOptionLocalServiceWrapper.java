/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPOption;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CPOptionLocalService}.
 *
 * @author Marco Leo
 * @see CPOptionLocalService
 * @generated
 */
public class CPOptionLocalServiceWrapper
	implements CPOptionLocalService, ServiceWrapper<CPOptionLocalService> {

	public CPOptionLocalServiceWrapper() {
		this(null);
	}

	public CPOptionLocalServiceWrapper(
		CPOptionLocalService cpOptionLocalService) {

		_cpOptionLocalService = cpOptionLocalService;
	}

	/**
	 * Adds the cp option to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPOptionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpOption the cp option
	 * @return the cp option that was added
	 */
	@Override
	public CPOption addCPOption(CPOption cpOption) {
		return _cpOptionLocalService.addCPOption(cpOption);
	}

	@Override
	public CPOption addCPOption(
			String externalReferenceCode, long userId,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String commerceOptionTypeKey, boolean facetable, boolean required,
			boolean skuContributor, String key,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionLocalService.addCPOption(
			externalReferenceCode, userId, nameMap, descriptionMap,
			commerceOptionTypeKey, facetable, required, skuContributor, key,
			serviceContext);
	}

	@Override
	public CPOption addOrUpdateCPOption(
			String externalReferenceCode, long userId,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String commerceOptionTypeKey, boolean facetable, boolean required,
			boolean skuContributor, String key,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionLocalService.addOrUpdateCPOption(
			externalReferenceCode, userId, nameMap, descriptionMap,
			commerceOptionTypeKey, facetable, required, skuContributor, key,
			serviceContext);
	}

	/**
	 * Creates a new cp option with the primary key. Does not add the cp option to the database.
	 *
	 * @param CPOptionId the primary key for the new cp option
	 * @return the new cp option
	 */
	@Override
	public CPOption createCPOption(long CPOptionId) {
		return _cpOptionLocalService.createCPOption(CPOptionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the cp option from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPOptionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpOption the cp option
	 * @return the cp option that was removed
	 * @throws PortalException
	 */
	@Override
	public CPOption deleteCPOption(CPOption cpOption)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionLocalService.deleteCPOption(cpOption);
	}

	/**
	 * Deletes the cp option with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPOptionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param CPOptionId the primary key of the cp option
	 * @return the cp option that was removed
	 * @throws PortalException if a cp option with the primary key could not be found
	 */
	@Override
	public CPOption deleteCPOption(long CPOptionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionLocalService.deleteCPOption(CPOptionId);
	}

	@Override
	public void deleteCPOptions(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpOptionLocalService.deleteCPOptions(companyId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _cpOptionLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _cpOptionLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _cpOptionLocalService.dynamicQuery();
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

		return _cpOptionLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPOptionModelImpl</code>.
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

		return _cpOptionLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPOptionModelImpl</code>.
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

		return _cpOptionLocalService.dynamicQuery(
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

		return _cpOptionLocalService.dynamicQueryCount(dynamicQuery);
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

		return _cpOptionLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public CPOption fetchCPOption(long CPOptionId) {
		return _cpOptionLocalService.fetchCPOption(CPOptionId);
	}

	@Override
	public CPOption fetchCPOption(long companyId, String key) {
		return _cpOptionLocalService.fetchCPOption(companyId, key);
	}

	@Override
	public CPOption fetchCPOptionByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return _cpOptionLocalService.fetchCPOptionByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp option with the matching UUID and company.
	 *
	 * @param uuid the cp option's UUID
	 * @param companyId the primary key of the company
	 * @return the matching cp option, or <code>null</code> if a matching cp option could not be found
	 */
	@Override
	public CPOption fetchCPOptionByUuidAndCompanyId(
		String uuid, long companyId) {

		return _cpOptionLocalService.fetchCPOptionByUuidAndCompanyId(
			uuid, companyId);
	}

	@Override
	public java.util.List<CPOption> findCPOptionByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPOption>
			orderByComparator) {

		return _cpOptionLocalService.findCPOptionByCompanyId(
			companyId, start, end, orderByComparator);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _cpOptionLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the cp option with the primary key.
	 *
	 * @param CPOptionId the primary key of the cp option
	 * @return the cp option
	 * @throws PortalException if a cp option with the primary key could not be found
	 */
	@Override
	public CPOption getCPOption(long CPOptionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionLocalService.getCPOption(CPOptionId);
	}

	@Override
	public CPOption getCPOption(long companyId, String key)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionLocalService.getCPOption(companyId, key);
	}

	@Override
	public CPOption getCPOptionByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionLocalService.getCPOptionByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp option with the matching UUID and company.
	 *
	 * @param uuid the cp option's UUID
	 * @param companyId the primary key of the company
	 * @return the matching cp option
	 * @throws PortalException if a matching cp option could not be found
	 */
	@Override
	public CPOption getCPOptionByUuidAndCompanyId(String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionLocalService.getCPOptionByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of all the cp options.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPOptionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp options
	 * @param end the upper bound of the range of cp options (not inclusive)
	 * @return the range of cp options
	 */
	@Override
	public java.util.List<CPOption> getCPOptions(int start, int end) {
		return _cpOptionLocalService.getCPOptions(start, end);
	}

	/**
	 * Returns the number of cp options.
	 *
	 * @return the number of cp options
	 */
	@Override
	public int getCPOptionsCount() {
		return _cpOptionLocalService.getCPOptionsCount();
	}

	@Override
	public int getCPOptionsCount(long companyId) {
		return _cpOptionLocalService.getCPOptionsCount(companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _cpOptionLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _cpOptionLocalService.getIndexableActionableDynamicQuery();
	}

	@Override
	public CPOption getOrAddEmptyCPOption(
			String externalReferenceCode, long companyId, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionLocalService.getOrAddEmptyCPOption(
			externalReferenceCode, companyId, userId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _cpOptionLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult<CPOption>
			searchCPOptions(
				long companyId, String keywords, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionLocalService.searchCPOptions(
			companyId, keywords, start, end, sort);
	}

	/**
	 * Updates the cp option in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPOptionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpOption the cp option
	 * @return the cp option that was updated
	 */
	@Override
	public CPOption updateCPOption(CPOption cpOption) {
		return _cpOptionLocalService.updateCPOption(cpOption);
	}

	@Override
	public CPOption updateCPOption(
			long cpOptionId, java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			String commerceOptionTypeKey, boolean facetable, boolean required,
			boolean skuContributor, String key,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionLocalService.updateCPOption(
			cpOptionId, nameMap, descriptionMap, commerceOptionTypeKey,
			facetable, required, skuContributor, key, serviceContext);
	}

	@Override
	public CPOption updateCPOptionExternalReferenceCode(
			String externalReferenceCode, long cpOptionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionLocalService.updateCPOptionExternalReferenceCode(
			externalReferenceCode, cpOptionId);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _cpOptionLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<CPOption> getCTPersistence() {
		return _cpOptionLocalService.getCTPersistence();
	}

	@Override
	public Class<CPOption> getModelClass() {
		return _cpOptionLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CPOption>, R, E> updateUnsafeFunction)
		throws E {

		return _cpOptionLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public CPOptionLocalService getWrappedService() {
		return _cpOptionLocalService;
	}

	@Override
	public void setWrappedService(CPOptionLocalService cpOptionLocalService) {
		_cpOptionLocalService = cpOptionLocalService;
	}

	private CPOptionLocalService _cpOptionLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1975075779