/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CommerceOrderTypeLocalService}.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceOrderTypeLocalService
 * @generated
 */
public class CommerceOrderTypeLocalServiceWrapper
	implements CommerceOrderTypeLocalService,
			   ServiceWrapper<CommerceOrderTypeLocalService> {

	public CommerceOrderTypeLocalServiceWrapper() {
		this(null);
	}

	public CommerceOrderTypeLocalServiceWrapper(
		CommerceOrderTypeLocalService commerceOrderTypeLocalService) {

		_commerceOrderTypeLocalService = commerceOrderTypeLocalService;
	}

	/**
	 * Adds the commerce order type to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceOrderTypeLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceOrderType the commerce order type
	 * @return the commerce order type that was added
	 */
	@Override
	public com.liferay.commerce.model.CommerceOrderType addCommerceOrderType(
		com.liferay.commerce.model.CommerceOrderType commerceOrderType) {

		return _commerceOrderTypeLocalService.addCommerceOrderType(
			commerceOrderType);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderType addCommerceOrderType(
			String externalReferenceCode, long userId,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			boolean active, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int displayOrder, int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderTypeLocalService.addCommerceOrderType(
			externalReferenceCode, userId, nameMap, descriptionMap, active,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, displayOrder, expirationDateMonth,
			expirationDateDay, expirationDateYear, expirationDateHour,
			expirationDateMinute, neverExpire, serviceContext);
	}

	@Override
	public void checkCommerceOrderTypes()
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceOrderTypeLocalService.checkCommerceOrderTypes();
	}

	/**
	 * Creates a new commerce order type with the primary key. Does not add the commerce order type to the database.
	 *
	 * @param commerceOrderTypeId the primary key for the new commerce order type
	 * @return the new commerce order type
	 */
	@Override
	public com.liferay.commerce.model.CommerceOrderType createCommerceOrderType(
		long commerceOrderTypeId) {

		return _commerceOrderTypeLocalService.createCommerceOrderType(
			commerceOrderTypeId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderTypeLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the commerce order type from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceOrderTypeLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceOrderType the commerce order type
	 * @return the commerce order type that was removed
	 * @throws PortalException
	 */
	@Override
	public com.liferay.commerce.model.CommerceOrderType deleteCommerceOrderType(
			com.liferay.commerce.model.CommerceOrderType commerceOrderType)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderTypeLocalService.deleteCommerceOrderType(
			commerceOrderType);
	}

	/**
	 * Deletes the commerce order type with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceOrderTypeLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceOrderTypeId the primary key of the commerce order type
	 * @return the commerce order type that was removed
	 * @throws PortalException if a commerce order type with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.model.CommerceOrderType deleteCommerceOrderType(
			long commerceOrderTypeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderTypeLocalService.deleteCommerceOrderType(
			commerceOrderTypeId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderTypeLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commerceOrderTypeLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commerceOrderTypeLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commerceOrderTypeLocalService.dynamicQuery();
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

		return _commerceOrderTypeLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderTypeModelImpl</code>.
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

		return _commerceOrderTypeLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderTypeModelImpl</code>.
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

		return _commerceOrderTypeLocalService.dynamicQuery(
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

		return _commerceOrderTypeLocalService.dynamicQueryCount(dynamicQuery);
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

		return _commerceOrderTypeLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderType fetchCommerceOrderType(
		long commerceOrderTypeId) {

		return _commerceOrderTypeLocalService.fetchCommerceOrderType(
			commerceOrderTypeId);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderType
		fetchCommerceOrderTypeByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return _commerceOrderTypeLocalService.
			fetchCommerceOrderTypeByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce order type with the matching UUID and company.
	 *
	 * @param uuid the commerce order type's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce order type, or <code>null</code> if a matching commerce order type could not be found
	 */
	@Override
	public com.liferay.commerce.model.CommerceOrderType
		fetchCommerceOrderTypeByUuidAndCompanyId(String uuid, long companyId) {

		return _commerceOrderTypeLocalService.
			fetchCommerceOrderTypeByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commerceOrderTypeLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce order type with the primary key.
	 *
	 * @param commerceOrderTypeId the primary key of the commerce order type
	 * @return the commerce order type
	 * @throws PortalException if a commerce order type with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.model.CommerceOrderType getCommerceOrderType(
			long commerceOrderTypeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderTypeLocalService.getCommerceOrderType(
			commerceOrderTypeId);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderType
			getCommerceOrderTypeByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderTypeLocalService.
			getCommerceOrderTypeByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce order type with the matching UUID and company.
	 *
	 * @param uuid the commerce order type's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce order type
	 * @throws PortalException if a matching commerce order type could not be found
	 */
	@Override
	public com.liferay.commerce.model.CommerceOrderType
			getCommerceOrderTypeByUuidAndCompanyId(String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderTypeLocalService.
			getCommerceOrderTypeByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of all the commerce order types.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderTypeModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce order types
	 * @param end the upper bound of the range of commerce order types (not inclusive)
	 * @return the range of commerce order types
	 */
	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrderType>
		getCommerceOrderTypes(int start, int end) {

		return _commerceOrderTypeLocalService.getCommerceOrderTypes(start, end);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrderType>
			getCommerceOrderTypes(
				long companyId, String className, long classPK, boolean active,
				int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderTypeLocalService.getCommerceOrderTypes(
			companyId, className, classPK, active, start, end);
	}

	/**
	 * Returns the number of commerce order types.
	 *
	 * @return the number of commerce order types
	 */
	@Override
	public int getCommerceOrderTypesCount() {
		return _commerceOrderTypeLocalService.getCommerceOrderTypesCount();
	}

	@Override
	public int getCommerceOrderTypesCount(long companyId, boolean active) {
		return _commerceOrderTypeLocalService.getCommerceOrderTypesCount(
			companyId, active);
	}

	@Override
	public int getCommerceOrderTypesCount(
			long companyId, String className, long classPK, boolean active)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderTypeLocalService.getCommerceOrderTypesCount(
			companyId, className, classPK, active);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _commerceOrderTypeLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commerceOrderTypeLocalService.
			getIndexableActionableDynamicQuery();
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderType
			getOrAddEmptyCommerceOrderType(
				String externalReferenceCode, long companyId, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderTypeLocalService.getOrAddEmptyCommerceOrderType(
			externalReferenceCode, companyId, userId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceOrderTypeLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderTypeLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the commerce order type in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceOrderTypeLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceOrderType the commerce order type
	 * @return the commerce order type that was updated
	 */
	@Override
	public com.liferay.commerce.model.CommerceOrderType updateCommerceOrderType(
		com.liferay.commerce.model.CommerceOrderType commerceOrderType) {

		return _commerceOrderTypeLocalService.updateCommerceOrderType(
			commerceOrderType);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderType updateCommerceOrderType(
			String externalReferenceCode, long userId, long commerceOrderTypeId,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			boolean active, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int displayOrder, int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderTypeLocalService.updateCommerceOrderType(
			externalReferenceCode, userId, commerceOrderTypeId, nameMap,
			descriptionMap, active, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute, displayOrder,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			serviceContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderType
			updateCommerceOrderTypeExternalReferenceCode(
				String externalReferenceCode, long commerceOrderTypeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderTypeLocalService.
			updateCommerceOrderTypeExternalReferenceCode(
				externalReferenceCode, commerceOrderTypeId);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderType updateStatus(
			long userId, long commerceOrderTypeId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			java.util.Map<String, java.io.Serializable> workflowContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderTypeLocalService.updateStatus(
			userId, commerceOrderTypeId, status, serviceContext,
			workflowContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commerceOrderTypeLocalService.getBasePersistence();
	}

	@Override
	public CommerceOrderTypeLocalService getWrappedService() {
		return _commerceOrderTypeLocalService;
	}

	@Override
	public void setWrappedService(
		CommerceOrderTypeLocalService commerceOrderTypeLocalService) {

		_commerceOrderTypeLocalService = commerceOrderTypeLocalService;
	}

	private CommerceOrderTypeLocalService _commerceOrderTypeLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-15495848