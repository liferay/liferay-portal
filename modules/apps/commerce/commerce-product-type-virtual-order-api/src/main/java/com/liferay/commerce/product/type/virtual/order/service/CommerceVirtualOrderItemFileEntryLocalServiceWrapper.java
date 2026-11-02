/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.type.virtual.order.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CommerceVirtualOrderItemFileEntryLocalService}.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceVirtualOrderItemFileEntryLocalService
 * @generated
 */
public class CommerceVirtualOrderItemFileEntryLocalServiceWrapper
	implements CommerceVirtualOrderItemFileEntryLocalService,
			   ServiceWrapper<CommerceVirtualOrderItemFileEntryLocalService> {

	public CommerceVirtualOrderItemFileEntryLocalServiceWrapper() {
		this(null);
	}

	public CommerceVirtualOrderItemFileEntryLocalServiceWrapper(
		CommerceVirtualOrderItemFileEntryLocalService
			commerceVirtualOrderItemFileEntryLocalService) {

		_commerceVirtualOrderItemFileEntryLocalService =
			commerceVirtualOrderItemFileEntryLocalService;
	}

	/**
	 * Adds the commerce virtual order item file entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceVirtualOrderItemFileEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceVirtualOrderItemFileEntry the commerce virtual order item file entry
	 * @return the commerce virtual order item file entry that was added
	 */
	@Override
	public com.liferay.commerce.product.type.virtual.order.model.
		CommerceVirtualOrderItemFileEntry addCommerceVirtualOrderItemFileEntry(
			com.liferay.commerce.product.type.virtual.order.model.
				CommerceVirtualOrderItemFileEntry
					commerceVirtualOrderItemFileEntry) {

		return _commerceVirtualOrderItemFileEntryLocalService.
			addCommerceVirtualOrderItemFileEntry(
				commerceVirtualOrderItemFileEntry);
	}

	@Override
	public com.liferay.commerce.product.type.virtual.order.model.
		CommerceVirtualOrderItemFileEntry addCommerceVirtualOrderItemFileEntry(
				long userId, long groupId, long commerceOrderItemId,
				long fileEntryId, String url, int usages, String version)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceVirtualOrderItemFileEntryLocalService.
			addCommerceVirtualOrderItemFileEntry(
				userId, groupId, commerceOrderItemId, fileEntryId, url, usages,
				version);
	}

	/**
	 * Creates a new commerce virtual order item file entry with the primary key. Does not add the commerce virtual order item file entry to the database.
	 *
	 * @param commerceVirtualOrderItemFileEntryId the primary key for the new commerce virtual order item file entry
	 * @return the new commerce virtual order item file entry
	 */
	@Override
	public com.liferay.commerce.product.type.virtual.order.model.
		CommerceVirtualOrderItemFileEntry
			createCommerceVirtualOrderItemFileEntry(
				long commerceVirtualOrderItemFileEntryId) {

		return _commerceVirtualOrderItemFileEntryLocalService.
			createCommerceVirtualOrderItemFileEntry(
				commerceVirtualOrderItemFileEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceVirtualOrderItemFileEntryLocalService.
			createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the commerce virtual order item file entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceVirtualOrderItemFileEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceVirtualOrderItemFileEntry the commerce virtual order item file entry
	 * @return the commerce virtual order item file entry that was removed
	 */
	@Override
	public com.liferay.commerce.product.type.virtual.order.model.
		CommerceVirtualOrderItemFileEntry
			deleteCommerceVirtualOrderItemFileEntry(
				com.liferay.commerce.product.type.virtual.order.model.
					CommerceVirtualOrderItemFileEntry
						commerceVirtualOrderItemFileEntry) {

		return _commerceVirtualOrderItemFileEntryLocalService.
			deleteCommerceVirtualOrderItemFileEntry(
				commerceVirtualOrderItemFileEntry);
	}

	/**
	 * Deletes the commerce virtual order item file entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceVirtualOrderItemFileEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceVirtualOrderItemFileEntryId the primary key of the commerce virtual order item file entry
	 * @return the commerce virtual order item file entry that was removed
	 * @throws PortalException if a commerce virtual order item file entry with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.product.type.virtual.order.model.
		CommerceVirtualOrderItemFileEntry
				deleteCommerceVirtualOrderItemFileEntry(
					long commerceVirtualOrderItemFileEntryId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceVirtualOrderItemFileEntryLocalService.
			deleteCommerceVirtualOrderItemFileEntry(
				commerceVirtualOrderItemFileEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceVirtualOrderItemFileEntryLocalService.
			deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commerceVirtualOrderItemFileEntryLocalService.dslQuery(
			dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commerceVirtualOrderItemFileEntryLocalService.dslQueryCount(
			dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commerceVirtualOrderItemFileEntryLocalService.dynamicQuery();
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

		return _commerceVirtualOrderItemFileEntryLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.type.virtual.order.model.impl.CommerceVirtualOrderItemFileEntryModelImpl</code>.
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

		return _commerceVirtualOrderItemFileEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.type.virtual.order.model.impl.CommerceVirtualOrderItemFileEntryModelImpl</code>.
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

		return _commerceVirtualOrderItemFileEntryLocalService.dynamicQuery(
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

		return _commerceVirtualOrderItemFileEntryLocalService.dynamicQueryCount(
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

		return _commerceVirtualOrderItemFileEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.commerce.product.type.virtual.order.model.
		CommerceVirtualOrderItemFileEntry
			fetchCommerceVirtualOrderItemFileEntry(
				long commerceVirtualOrderItemFileEntryId) {

		return _commerceVirtualOrderItemFileEntryLocalService.
			fetchCommerceVirtualOrderItemFileEntry(
				commerceVirtualOrderItemFileEntryId);
	}

	@Override
	public com.liferay.commerce.product.type.virtual.order.model.
		CommerceVirtualOrderItemFileEntry
			fetchCommerceVirtualOrderItemFileEntry(
				long commerceVirtualOrderItemId, long fileEntryId) {

		return _commerceVirtualOrderItemFileEntryLocalService.
			fetchCommerceVirtualOrderItemFileEntry(
				commerceVirtualOrderItemId, fileEntryId);
	}

	/**
	 * Returns the commerce virtual order item file entry matching the UUID and group.
	 *
	 * @param uuid the commerce virtual order item file entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce virtual order item file entry, or <code>null</code> if a matching commerce virtual order item file entry could not be found
	 */
	@Override
	public com.liferay.commerce.product.type.virtual.order.model.
		CommerceVirtualOrderItemFileEntry
			fetchCommerceVirtualOrderItemFileEntryByUuidAndGroupId(
				String uuid, long groupId) {

		return _commerceVirtualOrderItemFileEntryLocalService.
			fetchCommerceVirtualOrderItemFileEntryByUuidAndGroupId(
				uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commerceVirtualOrderItemFileEntryLocalService.
			getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the commerce virtual order item file entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.type.virtual.order.model.impl.CommerceVirtualOrderItemFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce virtual order item file entries
	 * @param end the upper bound of the range of commerce virtual order item file entries (not inclusive)
	 * @return the range of commerce virtual order item file entries
	 */
	@Override
	public java.util.List
		<com.liferay.commerce.product.type.virtual.order.model.
			CommerceVirtualOrderItemFileEntry>
				getCommerceVirtualOrderItemFileEntries(int start, int end) {

		return _commerceVirtualOrderItemFileEntryLocalService.
			getCommerceVirtualOrderItemFileEntries(start, end);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.product.type.virtual.order.model.
			CommerceVirtualOrderItemFileEntry>
				getCommerceVirtualOrderItemFileEntries(
					long commerceVirtualOrderItemId) {

		return _commerceVirtualOrderItemFileEntryLocalService.
			getCommerceVirtualOrderItemFileEntries(commerceVirtualOrderItemId);
	}

	/**
	 * Returns all the commerce virtual order item file entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce virtual order item file entries
	 * @param companyId the primary key of the company
	 * @return the matching commerce virtual order item file entries, or an empty list if no matches were found
	 */
	@Override
	public java.util.List
		<com.liferay.commerce.product.type.virtual.order.model.
			CommerceVirtualOrderItemFileEntry>
				getCommerceVirtualOrderItemFileEntriesByUuidAndCompanyId(
					String uuid, long companyId) {

		return _commerceVirtualOrderItemFileEntryLocalService.
			getCommerceVirtualOrderItemFileEntriesByUuidAndCompanyId(
				uuid, companyId);
	}

	/**
	 * Returns a range of commerce virtual order item file entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce virtual order item file entries
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of commerce virtual order item file entries
	 * @param end the upper bound of the range of commerce virtual order item file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching commerce virtual order item file entries, or an empty list if no matches were found
	 */
	@Override
	public java.util.List
		<com.liferay.commerce.product.type.virtual.order.model.
			CommerceVirtualOrderItemFileEntry>
				getCommerceVirtualOrderItemFileEntriesByUuidAndCompanyId(
					String uuid, long companyId, int start, int end,
					com.liferay.portal.kernel.util.OrderByComparator
						<com.liferay.commerce.product.type.virtual.order.model.
							CommerceVirtualOrderItemFileEntry>
								orderByComparator) {

		return _commerceVirtualOrderItemFileEntryLocalService.
			getCommerceVirtualOrderItemFileEntriesByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce virtual order item file entries.
	 *
	 * @return the number of commerce virtual order item file entries
	 */
	@Override
	public int getCommerceVirtualOrderItemFileEntriesCount() {
		return _commerceVirtualOrderItemFileEntryLocalService.
			getCommerceVirtualOrderItemFileEntriesCount();
	}

	@Override
	public int getCommerceVirtualOrderItemFileEntriesCount(
		long commerceVirtualOrderItemId) {

		return _commerceVirtualOrderItemFileEntryLocalService.
			getCommerceVirtualOrderItemFileEntriesCount(
				commerceVirtualOrderItemId);
	}

	/**
	 * Returns the commerce virtual order item file entry with the primary key.
	 *
	 * @param commerceVirtualOrderItemFileEntryId the primary key of the commerce virtual order item file entry
	 * @return the commerce virtual order item file entry
	 * @throws PortalException if a commerce virtual order item file entry with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.product.type.virtual.order.model.
		CommerceVirtualOrderItemFileEntry getCommerceVirtualOrderItemFileEntry(
				long commerceVirtualOrderItemFileEntryId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceVirtualOrderItemFileEntryLocalService.
			getCommerceVirtualOrderItemFileEntry(
				commerceVirtualOrderItemFileEntryId);
	}

	/**
	 * Returns the commerce virtual order item file entry matching the UUID and group.
	 *
	 * @param uuid the commerce virtual order item file entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce virtual order item file entry
	 * @throws PortalException if a matching commerce virtual order item file entry could not be found
	 */
	@Override
	public com.liferay.commerce.product.type.virtual.order.model.
		CommerceVirtualOrderItemFileEntry
				getCommerceVirtualOrderItemFileEntryByUuidAndGroupId(
					String uuid, long groupId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceVirtualOrderItemFileEntryLocalService.
			getCommerceVirtualOrderItemFileEntryByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _commerceVirtualOrderItemFileEntryLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commerceVirtualOrderItemFileEntryLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceVirtualOrderItemFileEntryLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceVirtualOrderItemFileEntryLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public com.liferay.commerce.product.type.virtual.order.model.
		CommerceVirtualOrderItemFileEntry incrementUsages(
				long commerceVirtualOrderItemFileEntryId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceVirtualOrderItemFileEntryLocalService.incrementUsages(
			commerceVirtualOrderItemFileEntryId);
	}

	/**
	 * Updates the commerce virtual order item file entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceVirtualOrderItemFileEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceVirtualOrderItemFileEntry the commerce virtual order item file entry
	 * @return the commerce virtual order item file entry that was updated
	 */
	@Override
	public com.liferay.commerce.product.type.virtual.order.model.
		CommerceVirtualOrderItemFileEntry
			updateCommerceVirtualOrderItemFileEntry(
				com.liferay.commerce.product.type.virtual.order.model.
					CommerceVirtualOrderItemFileEntry
						commerceVirtualOrderItemFileEntry) {

		return _commerceVirtualOrderItemFileEntryLocalService.
			updateCommerceVirtualOrderItemFileEntry(
				commerceVirtualOrderItemFileEntry);
	}

	@Override
	public com.liferay.commerce.product.type.virtual.order.model.
		CommerceVirtualOrderItemFileEntry
				updateCommerceVirtualOrderItemFileEntry(
					long commerceVirtualOrderItemFileEntryId, long fileEntryId,
					String url, int usages, String version)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceVirtualOrderItemFileEntryLocalService.
			updateCommerceVirtualOrderItemFileEntry(
				commerceVirtualOrderItemFileEntryId, fileEntryId, url, usages,
				version);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commerceVirtualOrderItemFileEntryLocalService.
			getBasePersistence();
	}

	@Override
	public CommerceVirtualOrderItemFileEntryLocalService getWrappedService() {
		return _commerceVirtualOrderItemFileEntryLocalService;
	}

	@Override
	public void setWrappedService(
		CommerceVirtualOrderItemFileEntryLocalService
			commerceVirtualOrderItemFileEntryLocalService) {

		_commerceVirtualOrderItemFileEntryLocalService =
			commerceVirtualOrderItemFileEntryLocalService;
	}

	private CommerceVirtualOrderItemFileEntryLocalService
		_commerceVirtualOrderItemFileEntryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:224005869