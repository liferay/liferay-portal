/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPConfigurationEntry;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CPConfigurationEntry. This utility wraps
 * <code>com.liferay.commerce.product.service.impl.CPConfigurationEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see CPConfigurationEntryLocalService
 * @generated
 */
public class CPConfigurationEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.product.service.impl.CPConfigurationEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

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
	public static CPConfigurationEntry addCPConfigurationEntry(
		CPConfigurationEntry cpConfigurationEntry) {

		return getService().addCPConfigurationEntry(cpConfigurationEntry);
	}

	public static CPConfigurationEntry addCPConfigurationEntry(
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
		throws PortalException {

		return getService().addCPConfigurationEntry(
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
	public static CPConfigurationEntry createCPConfigurationEntry(
		long CPConfigurationEntryId) {

		return getService().createCPConfigurationEntry(CPConfigurationEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteCPConfigurationEntries(long cpConfigurationListId)
		throws PortalException {

		getService().deleteCPConfigurationEntries(cpConfigurationListId);
	}

	public static void deleteCPConfigurationEntries(
			long classNameId, long classPK, boolean force)
		throws PortalException {

		getService().deleteCPConfigurationEntries(classNameId, classPK, force);
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
	public static CPConfigurationEntry deleteCPConfigurationEntry(
			CPConfigurationEntry cpConfigurationEntry)
		throws PortalException {

		return getService().deleteCPConfigurationEntry(cpConfigurationEntry);
	}

	public static CPConfigurationEntry deleteCPConfigurationEntry(
			CPConfigurationEntry cpConfigurationEntry, boolean force)
		throws PortalException {

		return getService().deleteCPConfigurationEntry(
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
	public static CPConfigurationEntry deleteCPConfigurationEntry(
			long CPConfigurationEntryId)
		throws PortalException {

		return getService().deleteCPConfigurationEntry(CPConfigurationEntryId);
	}

	public static CPConfigurationEntry deleteCPConfigurationEntry(
			long cpConfigurationEntryId, boolean force)
		throws PortalException {

		return getService().deleteCPConfigurationEntry(
			cpConfigurationEntryId, force);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationEntryModelImpl</code>.
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

	public static CPConfigurationEntry fetchCPConfigurationEntry(
		long CPConfigurationEntryId) {

		return getService().fetchCPConfigurationEntry(CPConfigurationEntryId);
	}

	public static CPConfigurationEntry fetchCPConfigurationEntry(
		long classNameId, long classPK, long cpConfigurationListId) {

		return getService().fetchCPConfigurationEntry(
			classNameId, classPK, cpConfigurationListId);
	}

	public static CPConfigurationEntry
		fetchCPConfigurationEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return getService().fetchCPConfigurationEntryByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp configuration entry matching the UUID and group.
	 *
	 * @param uuid the cp configuration entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp configuration entry, or <code>null</code> if a matching cp configuration entry could not be found
	 */
	public static CPConfigurationEntry
		fetchCPConfigurationEntryByUuidAndGroupId(String uuid, long groupId) {

		return getService().fetchCPConfigurationEntryByUuidAndGroupId(
			uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
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
	public static List<CPConfigurationEntry> getCPConfigurationEntries(
		int start, int end) {

		return getService().getCPConfigurationEntries(start, end);
	}

	public static List<CPConfigurationEntry> getCPConfigurationEntries(
		long cpConfigurationListId) {

		return getService().getCPConfigurationEntries(cpConfigurationListId);
	}

	public static List<CPConfigurationEntry> getCPConfigurationEntries(
		long classNameId, long classPK) {

		return getService().getCPConfigurationEntries(classNameId, classPK);
	}

	public static List<CPConfigurationEntry> getCPConfigurationEntries(
		long classNameId, long classPK, boolean visible) {

		return getService().getCPConfigurationEntries(
			classNameId, classPK, visible);
	}

	/**
	 * Returns all the cp configuration entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp configuration entries
	 * @param companyId the primary key of the company
	 * @return the matching cp configuration entries, or an empty list if no matches were found
	 */
	public static List<CPConfigurationEntry>
		getCPConfigurationEntriesByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().getCPConfigurationEntriesByUuidAndCompanyId(
			uuid, companyId);
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
	public static List<CPConfigurationEntry>
		getCPConfigurationEntriesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<CPConfigurationEntry> orderByComparator) {

		return getService().getCPConfigurationEntriesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of cp configuration entries.
	 *
	 * @return the number of cp configuration entries
	 */
	public static int getCPConfigurationEntriesCount() {
		return getService().getCPConfigurationEntriesCount();
	}

	/**
	 * Returns the cp configuration entry with the primary key.
	 *
	 * @param CPConfigurationEntryId the primary key of the cp configuration entry
	 * @return the cp configuration entry
	 * @throws PortalException if a cp configuration entry with the primary key could not be found
	 */
	public static CPConfigurationEntry getCPConfigurationEntry(
			long CPConfigurationEntryId)
		throws PortalException {

		return getService().getCPConfigurationEntry(CPConfigurationEntryId);
	}

	public static CPConfigurationEntry getCPConfigurationEntry(
			long classNameId, long classPK, long cpConfigurationListId)
		throws PortalException {

		return getService().getCPConfigurationEntry(
			classNameId, classPK, cpConfigurationListId);
	}

	public static CPConfigurationEntry
			getCPConfigurationEntryByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getCPConfigurationEntryByExternalReferenceCode(
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
	public static CPConfigurationEntry getCPConfigurationEntryByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getCPConfigurationEntryByUuidAndGroupId(
			uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
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
	 * Updates the cp configuration entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPConfigurationEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpConfigurationEntry the cp configuration entry
	 * @return the cp configuration entry that was updated
	 */
	public static CPConfigurationEntry updateCPConfigurationEntry(
		CPConfigurationEntry cpConfigurationEntry) {

		return getService().updateCPConfigurationEntry(cpConfigurationEntry);
	}

	public static CPConfigurationEntry updateCPConfigurationEntry(
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
		throws PortalException {

		return getService().updateCPConfigurationEntry(
			externalReferenceCode, cpConfigurationEntryId, cpTaxCategoryId,
			allowedOrderQuantities, backOrders, commerceAvailabilityEstimateId,
			cpDefinitionInventoryEngine, depth, displayAvailability,
			displayStockQuantity, freeShipping, height, lowStockActivity,
			maxOrderQuantity, minOrderQuantity, minStockQuantity,
			multipleOrderQuantity, purchasable, shippable, shippingExtraPrice,
			shipSeparately, taxExempt, visible, weight, width);
	}

	public static CPConfigurationEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CPConfigurationEntryLocalService>
		_serviceSnapshot = new Snapshot<>(
			CPConfigurationEntryLocalServiceUtil.class,
			CPConfigurationEntryLocalService.class);

}