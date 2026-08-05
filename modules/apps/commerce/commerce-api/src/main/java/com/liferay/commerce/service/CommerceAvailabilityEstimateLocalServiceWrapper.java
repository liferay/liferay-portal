/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CommerceAvailabilityEstimateLocalService}.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceAvailabilityEstimateLocalService
 * @generated
 */
public class CommerceAvailabilityEstimateLocalServiceWrapper
	implements CommerceAvailabilityEstimateLocalService,
			   ServiceWrapper<CommerceAvailabilityEstimateLocalService> {

	public CommerceAvailabilityEstimateLocalServiceWrapper() {
		this(null);
	}

	public CommerceAvailabilityEstimateLocalServiceWrapper(
		CommerceAvailabilityEstimateLocalService
			commerceAvailabilityEstimateLocalService) {

		_commerceAvailabilityEstimateLocalService =
			commerceAvailabilityEstimateLocalService;
	}

	/**
	 * Adds the commerce availability estimate to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceAvailabilityEstimateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceAvailabilityEstimate the commerce availability estimate
	 * @return the commerce availability estimate that was added
	 */
	@Override
	public com.liferay.commerce.model.CommerceAvailabilityEstimate
		addCommerceAvailabilityEstimate(
			com.liferay.commerce.model.CommerceAvailabilityEstimate
				commerceAvailabilityEstimate) {

		return _commerceAvailabilityEstimateLocalService.
			addCommerceAvailabilityEstimate(commerceAvailabilityEstimate);
	}

	@Override
	public com.liferay.commerce.model.CommerceAvailabilityEstimate
			addCommerceAvailabilityEstimate(
				String externalReferenceCode,
				java.util.Map<java.util.Locale, String> titleMap,
				double priority,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceAvailabilityEstimateLocalService.
			addCommerceAvailabilityEstimate(
				externalReferenceCode, titleMap, priority, serviceContext);
	}

	/**
	 * Creates a new commerce availability estimate with the primary key. Does not add the commerce availability estimate to the database.
	 *
	 * @param commerceAvailabilityEstimateId the primary key for the new commerce availability estimate
	 * @return the new commerce availability estimate
	 */
	@Override
	public com.liferay.commerce.model.CommerceAvailabilityEstimate
		createCommerceAvailabilityEstimate(
			long commerceAvailabilityEstimateId) {

		return _commerceAvailabilityEstimateLocalService.
			createCommerceAvailabilityEstimate(commerceAvailabilityEstimateId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceAvailabilityEstimateLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the commerce availability estimate from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceAvailabilityEstimateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceAvailabilityEstimate the commerce availability estimate
	 * @return the commerce availability estimate that was removed
	 * @throws PortalException
	 */
	@Override
	public com.liferay.commerce.model.CommerceAvailabilityEstimate
			deleteCommerceAvailabilityEstimate(
				com.liferay.commerce.model.CommerceAvailabilityEstimate
					commerceAvailabilityEstimate)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceAvailabilityEstimateLocalService.
			deleteCommerceAvailabilityEstimate(commerceAvailabilityEstimate);
	}

	/**
	 * Deletes the commerce availability estimate with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceAvailabilityEstimateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceAvailabilityEstimateId the primary key of the commerce availability estimate
	 * @return the commerce availability estimate that was removed
	 * @throws PortalException if a commerce availability estimate with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.model.CommerceAvailabilityEstimate
			deleteCommerceAvailabilityEstimate(
				long commerceAvailabilityEstimateId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceAvailabilityEstimateLocalService.
			deleteCommerceAvailabilityEstimate(commerceAvailabilityEstimateId);
	}

	@Override
	public void deleteCommerceAvailabilityEstimates(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceAvailabilityEstimateLocalService.
			deleteCommerceAvailabilityEstimates(companyId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceAvailabilityEstimateLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commerceAvailabilityEstimateLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commerceAvailabilityEstimateLocalService.dslQueryCount(
			dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commerceAvailabilityEstimateLocalService.dynamicQuery();
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

		return _commerceAvailabilityEstimateLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAvailabilityEstimateModelImpl</code>.
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

		return _commerceAvailabilityEstimateLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAvailabilityEstimateModelImpl</code>.
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

		return _commerceAvailabilityEstimateLocalService.dynamicQuery(
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

		return _commerceAvailabilityEstimateLocalService.dynamicQueryCount(
			dynamicQuery);
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

		return _commerceAvailabilityEstimateLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.commerce.model.CommerceAvailabilityEstimate
		fetchCommerceAvailabilityEstimate(long commerceAvailabilityEstimateId) {

		return _commerceAvailabilityEstimateLocalService.
			fetchCommerceAvailabilityEstimate(commerceAvailabilityEstimateId);
	}

	@Override
	public com.liferay.commerce.model.CommerceAvailabilityEstimate
		fetchCommerceAvailabilityEstimateByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return _commerceAvailabilityEstimateLocalService.
			fetchCommerceAvailabilityEstimateByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce availability estimate with the matching UUID and company.
	 *
	 * @param uuid the commerce availability estimate's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce availability estimate, or <code>null</code> if a matching commerce availability estimate could not be found
	 */
	@Override
	public com.liferay.commerce.model.CommerceAvailabilityEstimate
		fetchCommerceAvailabilityEstimateByUuidAndCompanyId(
			String uuid, long companyId) {

		return _commerceAvailabilityEstimateLocalService.
			fetchCommerceAvailabilityEstimateByUuidAndCompanyId(
				uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commerceAvailabilityEstimateLocalService.
			getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce availability estimate with the primary key.
	 *
	 * @param commerceAvailabilityEstimateId the primary key of the commerce availability estimate
	 * @return the commerce availability estimate
	 * @throws PortalException if a commerce availability estimate with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.model.CommerceAvailabilityEstimate
			getCommerceAvailabilityEstimate(long commerceAvailabilityEstimateId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceAvailabilityEstimateLocalService.
			getCommerceAvailabilityEstimate(commerceAvailabilityEstimateId);
	}

	@Override
	public com.liferay.commerce.model.CommerceAvailabilityEstimate
			getCommerceAvailabilityEstimateByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceAvailabilityEstimateLocalService.
			getCommerceAvailabilityEstimateByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce availability estimate with the matching UUID and company.
	 *
	 * @param uuid the commerce availability estimate's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce availability estimate
	 * @throws PortalException if a matching commerce availability estimate could not be found
	 */
	@Override
	public com.liferay.commerce.model.CommerceAvailabilityEstimate
			getCommerceAvailabilityEstimateByUuidAndCompanyId(
				String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceAvailabilityEstimateLocalService.
			getCommerceAvailabilityEstimateByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of all the commerce availability estimates.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAvailabilityEstimateModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce availability estimates
	 * @param end the upper bound of the range of commerce availability estimates (not inclusive)
	 * @return the range of commerce availability estimates
	 */
	@Override
	public java.util.List
		<com.liferay.commerce.model.CommerceAvailabilityEstimate>
			getCommerceAvailabilityEstimates(int start, int end) {

		return _commerceAvailabilityEstimateLocalService.
			getCommerceAvailabilityEstimates(start, end);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.model.CommerceAvailabilityEstimate>
			getCommerceAvailabilityEstimates(
				long companyId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.commerce.model.CommerceAvailabilityEstimate>
						orderByComparator) {

		return _commerceAvailabilityEstimateLocalService.
			getCommerceAvailabilityEstimates(
				companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce availability estimates.
	 *
	 * @return the number of commerce availability estimates
	 */
	@Override
	public int getCommerceAvailabilityEstimatesCount() {
		return _commerceAvailabilityEstimateLocalService.
			getCommerceAvailabilityEstimatesCount();
	}

	@Override
	public int getCommerceAvailabilityEstimatesCount(long companyId) {
		return _commerceAvailabilityEstimateLocalService.
			getCommerceAvailabilityEstimatesCount(companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _commerceAvailabilityEstimateLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commerceAvailabilityEstimateLocalService.
			getIndexableActionableDynamicQuery();
	}

	@Override
	public com.liferay.commerce.model.CommerceAvailabilityEstimate
			getOrAddEmptyCommerceAvailabilityEstimate(
				String externalReferenceCode, long companyId, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceAvailabilityEstimateLocalService.
			getOrAddEmptyCommerceAvailabilityEstimate(
				externalReferenceCode, companyId, userId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceAvailabilityEstimateLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceAvailabilityEstimateLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the commerce availability estimate in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceAvailabilityEstimateLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceAvailabilityEstimate the commerce availability estimate
	 * @return the commerce availability estimate that was updated
	 */
	@Override
	public com.liferay.commerce.model.CommerceAvailabilityEstimate
		updateCommerceAvailabilityEstimate(
			com.liferay.commerce.model.CommerceAvailabilityEstimate
				commerceAvailabilityEstimate) {

		return _commerceAvailabilityEstimateLocalService.
			updateCommerceAvailabilityEstimate(commerceAvailabilityEstimate);
	}

	@Override
	public com.liferay.commerce.model.CommerceAvailabilityEstimate
			updateCommerceAvailabilityEstimate(
				String externalReferenceCode,
				long commerceAvailabilityEstimateId,
				java.util.Map<java.util.Locale, String> titleMap,
				double priority,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceAvailabilityEstimateLocalService.
			updateCommerceAvailabilityEstimate(
				externalReferenceCode, commerceAvailabilityEstimateId, titleMap,
				priority, serviceContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commerceAvailabilityEstimateLocalService.getBasePersistence();
	}

	@Override
	public CommerceAvailabilityEstimateLocalService getWrappedService() {
		return _commerceAvailabilityEstimateLocalService;
	}

	@Override
	public void setWrappedService(
		CommerceAvailabilityEstimateLocalService
			commerceAvailabilityEstimateLocalService) {

		_commerceAvailabilityEstimateLocalService =
			commerceAvailabilityEstimateLocalService;
	}

	private CommerceAvailabilityEstimateLocalService
		_commerceAvailabilityEstimateLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-591351431