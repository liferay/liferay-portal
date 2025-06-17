/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPConfigurationEntry;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CPConfigurationEntryLocalService}.
 *
 * @author Marco Leo
 * @see CPConfigurationEntryLocalService
 * @generated
 */
public class CPConfigurationEntryLocalServiceWrapper
	implements CPConfigurationEntryLocalService,
			   ServiceWrapper<CPConfigurationEntryLocalService> {

	public CPConfigurationEntryLocalServiceWrapper() {
		this(null);
	}

	public CPConfigurationEntryLocalServiceWrapper(
		CPConfigurationEntryLocalService cpConfigurationEntryLocalService) {

		_cpConfigurationEntryLocalService = cpConfigurationEntryLocalService;
	}

	/**
	 * Adds the cp configuration entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPConfigurationEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpConfigurationEntry the cp configuration entry
	 * @return the cp configuration entry that was added
	 */
	@Override
	public CPConfigurationEntry addCPConfigurationEntry(
		CPConfigurationEntry cpConfigurationEntry) {

		return _cpConfigurationEntryLocalService.addCPConfigurationEntry(
			cpConfigurationEntry);
	}

	@Override
	public CPConfigurationEntry addCPConfigurationEntry(
			String externalReferenceCode, long userId, long groupId,
			long classNameId, long classPK, long cpConfigurationListId,
			long cpTaxCategoryId, String allowedOrderQuantities,
			boolean backOrders, long commerceAvailabilityEstimateId,
			String cpDefinitionInventoryEngine, double depth,
			boolean displayAvailability, boolean displayStockQuantity,
			boolean freeShipping, double height, String lowStockActivity,
			java.math.BigDecimal maxOrderQuantity,
			java.math.BigDecimal minOrderQuantity,
			java.math.BigDecimal minStockQuantity,
			java.math.BigDecimal multipleOrderQuantity, boolean purchasable,
			boolean shippable, double shippingExtraPrice,
			boolean shipSeparately, boolean taxExempt, boolean visible,
			double weight, double width)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationEntryLocalService.addCPConfigurationEntry(
			externalReferenceCode, userId, groupId, classNameId, classPK,
			cpConfigurationListId, cpTaxCategoryId, allowedOrderQuantities,
			backOrders, commerceAvailabilityEstimateId,
			cpDefinitionInventoryEngine, depth, displayAvailability,
			displayStockQuantity, freeShipping, height, lowStockActivity,
			maxOrderQuantity, minOrderQuantity, minStockQuantity,
			multipleOrderQuantity, purchasable, shippable, shippingExtraPrice,
			shipSeparately, taxExempt, visible, weight, width);
	}

	/**
	 * Creates a new cp configuration entry with the primary key. Does not add the cp configuration entry to the database.
	 *
	 * @param CPConfigurationEntryId the primary key for the new cp configuration entry
	 * @return the new cp configuration entry
	 */
	@Override
	public CPConfigurationEntry createCPConfigurationEntry(
		long CPConfigurationEntryId) {

		return _cpConfigurationEntryLocalService.createCPConfigurationEntry(
			CPConfigurationEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationEntryLocalService.createPersistedModel(
			primaryKeyObj);
	}

	@Override
	public void deleteCPConfigurationEntries(long cpConfigurationListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpConfigurationEntryLocalService.deleteCPConfigurationEntries(
			cpConfigurationListId);
	}

	@Override
	public void deleteCPConfigurationEntries(
			long classNameId, long classPK, boolean force)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpConfigurationEntryLocalService.deleteCPConfigurationEntries(
			classNameId, classPK, force);
	}

	/**
	 * Deletes the cp configuration entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPConfigurationEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpConfigurationEntry the cp configuration entry
	 * @return the cp configuration entry that was removed
	 * @throws PortalException
	 */
	@Override
	public CPConfigurationEntry deleteCPConfigurationEntry(
			CPConfigurationEntry cpConfigurationEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationEntryLocalService.deleteCPConfigurationEntry(
			cpConfigurationEntry);
	}

	@Override
	public CPConfigurationEntry deleteCPConfigurationEntry(
			CPConfigurationEntry cpConfigurationEntry, boolean force)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationEntryLocalService.deleteCPConfigurationEntry(
			cpConfigurationEntry, force);
	}

	/**
	 * Deletes the cp configuration entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPConfigurationEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param CPConfigurationEntryId the primary key of the cp configuration entry
	 * @return the cp configuration entry that was removed
	 * @throws PortalException if a cp configuration entry with the primary key could not be found
	 */
	@Override
	public CPConfigurationEntry deleteCPConfigurationEntry(
			long CPConfigurationEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationEntryLocalService.deleteCPConfigurationEntry(
			CPConfigurationEntryId);
	}

	@Override
	public CPConfigurationEntry deleteCPConfigurationEntry(
			long cpConfigurationEntryId, boolean force)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationEntryLocalService.deleteCPConfigurationEntry(
			cpConfigurationEntryId, force);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationEntryLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _cpConfigurationEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _cpConfigurationEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _cpConfigurationEntryLocalService.dynamicQuery();
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

		return _cpConfigurationEntryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationEntryModelImpl</code>.
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

		return _cpConfigurationEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationEntryModelImpl</code>.
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

		return _cpConfigurationEntryLocalService.dynamicQuery(
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

		return _cpConfigurationEntryLocalService.dynamicQueryCount(
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

		return _cpConfigurationEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public CPConfigurationEntry fetchCPConfigurationEntry(
		long CPConfigurationEntryId) {

		return _cpConfigurationEntryLocalService.fetchCPConfigurationEntry(
			CPConfigurationEntryId);
	}

	@Override
	public CPConfigurationEntry fetchCPConfigurationEntry(
		long classNameId, long classPK, long cpConfigurationListId) {

		return _cpConfigurationEntryLocalService.fetchCPConfigurationEntry(
			classNameId, classPK, cpConfigurationListId);
	}

	@Override
	public CPConfigurationEntry
		fetchCPConfigurationEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return _cpConfigurationEntryLocalService.
			fetchCPConfigurationEntryByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp configuration entry matching the UUID and group.
	 *
	 * @param uuid the cp configuration entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry fetchCPConfigurationEntryByUuidAndGroupId(
		String uuid, long groupId) {

		return _cpConfigurationEntryLocalService.
			fetchCPConfigurationEntryByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _cpConfigurationEntryLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the cp configuration entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @return the range of cp configuration entries
	 */
	@Override
	public java.util.List<CPConfigurationEntry> getCPConfigurationEntries(
		int start, int end) {

		return _cpConfigurationEntryLocalService.getCPConfigurationEntries(
			start, end);
	}

	@Override
	public java.util.List<CPConfigurationEntry> getCPConfigurationEntries(
		long cpConfigurationListId) {

		return _cpConfigurationEntryLocalService.getCPConfigurationEntries(
			cpConfigurationListId);
	}

	@Override
	public java.util.List<CPConfigurationEntry> getCPConfigurationEntries(
		long classNameId, long classPK) {

		return _cpConfigurationEntryLocalService.getCPConfigurationEntries(
			classNameId, classPK);
	}

	@Override
	public java.util.List<CPConfigurationEntry> getCPConfigurationEntries(
		long classNameId, long classPK, boolean visible) {

		return _cpConfigurationEntryLocalService.getCPConfigurationEntries(
			classNameId, classPK, visible);
	}

	/**
	 * Returns all the cp configuration entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp configuration entries
	 * @param companyId the primary key of the company
	 * @return the matching cp configuration entries, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<CPConfigurationEntry>
		getCPConfigurationEntriesByUuidAndCompanyId(
			String uuid, long companyId) {

		return _cpConfigurationEntryLocalService.
			getCPConfigurationEntriesByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of cp configuration entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp configuration entries
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of cp configuration entries
	 * @param end the upper bound of the range of cp configuration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching cp configuration entries, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<CPConfigurationEntry>
		getCPConfigurationEntriesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPConfigurationEntry> orderByComparator) {

		return _cpConfigurationEntryLocalService.
			getCPConfigurationEntriesByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of cp configuration entries.
	 *
	 * @return the number of cp configuration entries
	 */
	@Override
	public int getCPConfigurationEntriesCount() {
		return _cpConfigurationEntryLocalService.
			getCPConfigurationEntriesCount();
	}

	/**
	 * Returns the cp configuration entry with the primary key.
	 *
	 * @param CPConfigurationEntryId the primary key of the cp configuration entry
	 * @return the cp configuration entry
	 * @throws PortalException if a cp configuration entry with the primary key could not be found
	 */
	@Override
	public CPConfigurationEntry getCPConfigurationEntry(
			long CPConfigurationEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationEntryLocalService.getCPConfigurationEntry(
			CPConfigurationEntryId);
	}

	@Override
	public CPConfigurationEntry getCPConfigurationEntry(
			long classNameId, long classPK, long cpConfigurationListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationEntryLocalService.getCPConfigurationEntry(
			classNameId, classPK, cpConfigurationListId);
	}

	@Override
	public CPConfigurationEntry getCPConfigurationEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationEntryLocalService.
			getCPConfigurationEntryByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp configuration entry matching the UUID and group.
	 *
	 * @param uuid the cp configuration entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp configuration entry
	 * @throws PortalException if a matching cp configuration entry could not be found
	 */
	@Override
	public CPConfigurationEntry getCPConfigurationEntryByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationEntryLocalService.
			getCPConfigurationEntryByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _cpConfigurationEntryLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _cpConfigurationEntryLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _cpConfigurationEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationEntryLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the cp configuration entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPConfigurationEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpConfigurationEntry the cp configuration entry
	 * @return the cp configuration entry that was updated
	 */
	@Override
	public CPConfigurationEntry updateCPConfigurationEntry(
		CPConfigurationEntry cpConfigurationEntry) {

		return _cpConfigurationEntryLocalService.updateCPConfigurationEntry(
			cpConfigurationEntry);
	}

	@Override
	public CPConfigurationEntry updateCPConfigurationEntry(
			String externalReferenceCode, long cpConfigurationEntryId,
			long cpTaxCategoryId, String allowedOrderQuantities,
			boolean backOrders, long commerceAvailabilityEstimateId,
			String cpDefinitionInventoryEngine, double depth,
			boolean displayAvailability, boolean displayStockQuantity,
			boolean freeShipping, double height, String lowStockActivity,
			java.math.BigDecimal maxOrderQuantity,
			java.math.BigDecimal minOrderQuantity,
			java.math.BigDecimal minStockQuantity,
			java.math.BigDecimal multipleOrderQuantity, boolean purchasable,
			boolean shippable, double shippingExtraPrice,
			boolean shipSeparately, boolean taxExempt, boolean visible,
			double weight, double width)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationEntryLocalService.updateCPConfigurationEntry(
			externalReferenceCode, cpConfigurationEntryId, cpTaxCategoryId,
			allowedOrderQuantities, backOrders, commerceAvailabilityEstimateId,
			cpDefinitionInventoryEngine, depth, displayAvailability,
			displayStockQuantity, freeShipping, height, lowStockActivity,
			maxOrderQuantity, minOrderQuantity, minStockQuantity,
			multipleOrderQuantity, purchasable, shippable, shippingExtraPrice,
			shipSeparately, taxExempt, visible, weight, width);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _cpConfigurationEntryLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<CPConfigurationEntry> getCTPersistence() {
		return _cpConfigurationEntryLocalService.getCTPersistence();
	}

	@Override
	public Class<CPConfigurationEntry> getModelClass() {
		return _cpConfigurationEntryLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CPConfigurationEntry>, R, E>
				updateUnsafeFunction)
		throws E {

		return _cpConfigurationEntryLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public CPConfigurationEntryLocalService getWrappedService() {
		return _cpConfigurationEntryLocalService;
	}

	@Override
	public void setWrappedService(
		CPConfigurationEntryLocalService cpConfigurationEntryLocalService) {

		_cpConfigurationEntryLocalService = cpConfigurationEntryLocalService;
	}

	private CPConfigurationEntryLocalService _cpConfigurationEntryLocalService;

}