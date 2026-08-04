/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPOptionValue;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CPOptionValueLocalService}.
 *
 * @author Marco Leo
 * @see CPOptionValueLocalService
 * @generated
 */
public class CPOptionValueLocalServiceWrapper
	implements CPOptionValueLocalService,
			   ServiceWrapper<CPOptionValueLocalService> {

	public CPOptionValueLocalServiceWrapper() {
		this(null);
	}

	public CPOptionValueLocalServiceWrapper(
		CPOptionValueLocalService cpOptionValueLocalService) {

		_cpOptionValueLocalService = cpOptionValueLocalService;
	}

	/**
	 * Adds the cp option value to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPOptionValueLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpOptionValue the cp option value
	 * @return the cp option value that was added
	 */
	@Override
	public CPOptionValue addCPOptionValue(CPOptionValue cpOptionValue) {
		return _cpOptionValueLocalService.addCPOptionValue(cpOptionValue);
	}

	@Override
	public CPOptionValue addCPOptionValue(
			long cpOptionId, java.util.Map<java.util.Locale, String> nameMap,
			double priority, String key,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionValueLocalService.addCPOptionValue(
			cpOptionId, nameMap, priority, key, serviceContext);
	}

	@Override
	public CPOptionValue addCPOptionValue(
			String externalReferenceCode, long cpOptionId,
			java.util.Map<java.util.Locale, String> nameMap, double priority,
			String key,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionValueLocalService.addCPOptionValue(
			externalReferenceCode, cpOptionId, nameMap, priority, key,
			serviceContext);
	}

	@Override
	public CPOptionValue addOrUpdateCPOptionValue(
			String externalReferenceCode, long cpOptionId,
			java.util.Map<java.util.Locale, String> nameMap, double priority,
			String key,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionValueLocalService.addOrUpdateCPOptionValue(
			externalReferenceCode, cpOptionId, nameMap, priority, key,
			serviceContext);
	}

	/**
	 * Creates a new cp option value with the primary key. Does not add the cp option value to the database.
	 *
	 * @param CPOptionValueId the primary key for the new cp option value
	 * @return the new cp option value
	 */
	@Override
	public CPOptionValue createCPOptionValue(long CPOptionValueId) {
		return _cpOptionValueLocalService.createCPOptionValue(CPOptionValueId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionValueLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the cp option value from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPOptionValueLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpOptionValue the cp option value
	 * @return the cp option value that was removed
	 * @throws PortalException
	 */
	@Override
	public CPOptionValue deleteCPOptionValue(CPOptionValue cpOptionValue)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionValueLocalService.deleteCPOptionValue(cpOptionValue);
	}

	/**
	 * Deletes the cp option value with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPOptionValueLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param CPOptionValueId the primary key of the cp option value
	 * @return the cp option value that was removed
	 * @throws PortalException if a cp option value with the primary key could not be found
	 */
	@Override
	public CPOptionValue deleteCPOptionValue(long CPOptionValueId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionValueLocalService.deleteCPOptionValue(CPOptionValueId);
	}

	@Override
	public void deleteCPOptionValues(long cpOptionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpOptionValueLocalService.deleteCPOptionValues(cpOptionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionValueLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _cpOptionValueLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _cpOptionValueLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _cpOptionValueLocalService.dynamicQuery();
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

		return _cpOptionValueLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPOptionValueModelImpl</code>.
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

		return _cpOptionValueLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPOptionValueModelImpl</code>.
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

		return _cpOptionValueLocalService.dynamicQuery(
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

		return _cpOptionValueLocalService.dynamicQueryCount(dynamicQuery);
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

		return _cpOptionValueLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public CPOptionValue fetchCPOptionValue(long CPOptionValueId) {
		return _cpOptionValueLocalService.fetchCPOptionValue(CPOptionValueId);
	}

	@Override
	public CPOptionValue fetchCPOptionValueByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return _cpOptionValueLocalService.
			fetchCPOptionValueByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp option value with the matching UUID and company.
	 *
	 * @param uuid the cp option value's UUID
	 * @param companyId the primary key of the company
	 * @return the matching cp option value, or <code>null</code> if a matching cp option value could not be found
	 */
	@Override
	public CPOptionValue fetchCPOptionValueByUuidAndCompanyId(
		String uuid, long companyId) {

		return _cpOptionValueLocalService.fetchCPOptionValueByUuidAndCompanyId(
			uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _cpOptionValueLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the cp option value with the primary key.
	 *
	 * @param CPOptionValueId the primary key of the cp option value
	 * @return the cp option value
	 * @throws PortalException if a cp option value with the primary key could not be found
	 */
	@Override
	public CPOptionValue getCPOptionValue(long CPOptionValueId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionValueLocalService.getCPOptionValue(CPOptionValueId);
	}

	@Override
	public CPOptionValue getCPOptionValue(long cpOptionId, String key)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionValueLocalService.getCPOptionValue(cpOptionId, key);
	}

	@Override
	public CPOptionValue getCPOptionValueByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionValueLocalService.
			getCPOptionValueByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp option value with the matching UUID and company.
	 *
	 * @param uuid the cp option value's UUID
	 * @param companyId the primary key of the company
	 * @return the matching cp option value
	 * @throws PortalException if a matching cp option value could not be found
	 */
	@Override
	public CPOptionValue getCPOptionValueByUuidAndCompanyId(
			String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionValueLocalService.getCPOptionValueByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of all the cp option values.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp option values
	 * @param end the upper bound of the range of cp option values (not inclusive)
	 * @return the range of cp option values
	 */
	@Override
	public java.util.List<CPOptionValue> getCPOptionValues(int start, int end) {
		return _cpOptionValueLocalService.getCPOptionValues(start, end);
	}

	@Override
	public java.util.List<CPOptionValue> getCPOptionValues(
		long cpOptionId, int start, int end) {

		return _cpOptionValueLocalService.getCPOptionValues(
			cpOptionId, start, end);
	}

	@Override
	public java.util.List<CPOptionValue> getCPOptionValues(
		long cpOptionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPOptionValue>
			orderByComparator) {

		return _cpOptionValueLocalService.getCPOptionValues(
			cpOptionId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of cp option values.
	 *
	 * @return the number of cp option values
	 */
	@Override
	public int getCPOptionValuesCount() {
		return _cpOptionValueLocalService.getCPOptionValuesCount();
	}

	@Override
	public int getCPOptionValuesCount(long cpOptionId) {
		return _cpOptionValueLocalService.getCPOptionValuesCount(cpOptionId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _cpOptionValueLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _cpOptionValueLocalService.getIndexableActionableDynamicQuery();
	}

	@Override
	public CPOptionValue getOrAddEmptyCPOptionValue(
			String externalReferenceCode, long cpOptionId, long companyId,
			long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionValueLocalService.getOrAddEmptyCPOptionValue(
			externalReferenceCode, cpOptionId, companyId, userId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _cpOptionValueLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionValueLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public com.liferay.portal.kernel.search.Hits search(
		com.liferay.portal.kernel.search.SearchContext searchContext) {

		return _cpOptionValueLocalService.search(searchContext);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult<CPOptionValue>
			searchCPOptionValues(
				long companyId, long cpOptionId, String keywords, int start,
				int end, com.liferay.portal.kernel.search.Sort[] sorts)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionValueLocalService.searchCPOptionValues(
			companyId, cpOptionId, keywords, start, end, sorts);
	}

	@Override
	public int searchCPOptionValuesCount(
			long companyId, long cpOptionId, String keywords)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionValueLocalService.searchCPOptionValuesCount(
			companyId, cpOptionId, keywords);
	}

	/**
	 * Updates the cp option value in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPOptionValueLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpOptionValue the cp option value
	 * @return the cp option value that was updated
	 */
	@Override
	public CPOptionValue updateCPOptionValue(CPOptionValue cpOptionValue) {
		return _cpOptionValueLocalService.updateCPOptionValue(cpOptionValue);
	}

	@Override
	public CPOptionValue updateCPOptionValue(
			long cpOptionValueId,
			java.util.Map<java.util.Locale, String> nameMap, double priority,
			String key,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpOptionValueLocalService.updateCPOptionValue(
			cpOptionValueId, nameMap, priority, key, serviceContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _cpOptionValueLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<CPOptionValue> getCTPersistence() {
		return _cpOptionValueLocalService.getCTPersistence();
	}

	@Override
	public Class<CPOptionValue> getModelClass() {
		return _cpOptionValueLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CPOptionValue>, R, E>
				updateUnsafeFunction)
		throws E {

		return _cpOptionValueLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public CPOptionValueLocalService getWrappedService() {
		return _cpOptionValueLocalService;
	}

	@Override
	public void setWrappedService(
		CPOptionValueLocalService cpOptionValueLocalService) {

		_cpOptionValueLocalService = cpOptionValueLocalService;
	}

	private CPOptionValueLocalService _cpOptionValueLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1300621940