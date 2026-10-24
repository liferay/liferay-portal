/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service;

import com.liferay.commerce.model.CommerceSubscriptionEntry;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommerceSubscriptionEntry. This utility wraps
 * <code>com.liferay.commerce.service.impl.CommerceSubscriptionEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceSubscriptionEntryLocalService
 * @generated
 */
public class CommerceSubscriptionEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.service.impl.CommerceSubscriptionEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce subscription entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceSubscriptionEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceSubscriptionEntry the commerce subscription entry
	 * @return the commerce subscription entry that was added
	 */
	public static CommerceSubscriptionEntry addCommerceSubscriptionEntry(
		CommerceSubscriptionEntry commerceSubscriptionEntry) {

		return getService().addCommerceSubscriptionEntry(
			commerceSubscriptionEntry);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	public static CommerceSubscriptionEntry addCommerceSubscriptionEntry(
			long userId, long groupId, long commerceOrderItemId,
			int subscriptionLength, String subscriptionType,
			long maxSubscriptionCycles,
			com.liferay.portal.kernel.util.UnicodeProperties
				subscriptionTypeSettingsUnicodeProperties)
		throws PortalException {

		return getService().addCommerceSubscriptionEntry(
			userId, groupId, commerceOrderItemId, subscriptionLength,
			subscriptionType, maxSubscriptionCycles,
			subscriptionTypeSettingsUnicodeProperties);
	}

	public static CommerceSubscriptionEntry addCommerceSubscriptionEntry(
			long userId, long groupId, long commerceOrderItemId,
			int subscriptionLength, String subscriptionType,
			long maxSubscriptionCycles,
			com.liferay.portal.kernel.util.UnicodeProperties
				subscriptionTypeSettingsUnicodeProperties,
			int deliverySubscriptionLength, String deliverySubscriptionType,
			long deliveryMaxSubscriptionCycles,
			com.liferay.portal.kernel.util.UnicodeProperties
				deliverySubscriptionTypeSettingsUnicodeProperties)
		throws PortalException {

		return getService().addCommerceSubscriptionEntry(
			userId, groupId, commerceOrderItemId, subscriptionLength,
			subscriptionType, maxSubscriptionCycles,
			subscriptionTypeSettingsUnicodeProperties,
			deliverySubscriptionLength, deliverySubscriptionType,
			deliveryMaxSubscriptionCycles,
			deliverySubscriptionTypeSettingsUnicodeProperties);
	}

	/**
	 * Creates a new commerce subscription entry with the primary key. Does not add the commerce subscription entry to the database.
	 *
	 * @param commerceSubscriptionEntryId the primary key for the new commerce subscription entry
	 * @return the new commerce subscription entry
	 */
	public static CommerceSubscriptionEntry createCommerceSubscriptionEntry(
		long commerceSubscriptionEntryId) {

		return getService().createCommerceSubscriptionEntry(
			commerceSubscriptionEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteCommerceSubscriptionEntries(long groupId) {
		getService().deleteCommerceSubscriptionEntries(groupId);
	}

	/**
	 * Deletes the commerce subscription entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceSubscriptionEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceSubscriptionEntry the commerce subscription entry
	 * @return the commerce subscription entry that was removed
	 */
	public static CommerceSubscriptionEntry deleteCommerceSubscriptionEntry(
		CommerceSubscriptionEntry commerceSubscriptionEntry) {

		return getService().deleteCommerceSubscriptionEntry(
			commerceSubscriptionEntry);
	}

	/**
	 * Deletes the commerce subscription entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceSubscriptionEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceSubscriptionEntryId the primary key of the commerce subscription entry
	 * @return the commerce subscription entry that was removed
	 * @throws PortalException if a commerce subscription entry with the primary key could not be found
	 */
	public static CommerceSubscriptionEntry deleteCommerceSubscriptionEntry(
			long commerceSubscriptionEntryId)
		throws PortalException {

		return getService().deleteCommerceSubscriptionEntry(
			commerceSubscriptionEntryId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceSubscriptionEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceSubscriptionEntryModelImpl</code>.
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

	public static CommerceSubscriptionEntry fetchCommerceSubscriptionEntry(
		long commerceSubscriptionEntryId) {

		return getService().fetchCommerceSubscriptionEntry(
			commerceSubscriptionEntryId);
	}

	public static CommerceSubscriptionEntry
		fetchCommerceSubscriptionEntryByCommerceOrderItemId(
			long commerceOrderItemId) {

		return getService().fetchCommerceSubscriptionEntryByCommerceOrderItemId(
			commerceOrderItemId);
	}

	/**
	 * Returns the commerce subscription entry matching the UUID and group.
	 *
	 * @param uuid the commerce subscription entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce subscription entry, or <code>null</code> if a matching commerce subscription entry could not be found
	 */
	public static CommerceSubscriptionEntry
		fetchCommerceSubscriptionEntryByUuidAndGroupId(
			String uuid, long groupId) {

		return getService().fetchCommerceSubscriptionEntryByUuidAndGroupId(
			uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<CommerceSubscriptionEntry>
		getActiveCommerceSubscriptionEntries() {

		return getService().getActiveCommerceSubscriptionEntries();
	}

	public static List<CommerceSubscriptionEntry>
		getCommerceDeliverySubscriptionEntriesToRenew() {

		return getService().getCommerceDeliverySubscriptionEntriesToRenew();
	}

	/**
	 * Returns a range of all the commerce subscription entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceSubscriptionEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce subscription entries
	 * @param end the upper bound of the range of commerce subscription entries (not inclusive)
	 * @return the range of commerce subscription entries
	 */
	public static List<CommerceSubscriptionEntry>
		getCommerceSubscriptionEntries(int start, int end) {

		return getService().getCommerceSubscriptionEntries(start, end);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	public static List<CommerceSubscriptionEntry>
		getCommerceSubscriptionEntries(
			long companyId, long userId, int start, int end,
			OrderByComparator<CommerceSubscriptionEntry> orderByComparator) {

		return getService().getCommerceSubscriptionEntries(
			companyId, userId, start, end, orderByComparator);
	}

	public static List<CommerceSubscriptionEntry>
		getCommerceSubscriptionEntries(
			long companyId, long groupId, long userId, int start, int end,
			OrderByComparator<CommerceSubscriptionEntry> orderByComparator) {

		return getService().getCommerceSubscriptionEntries(
			companyId, groupId, userId, start, end, orderByComparator);
	}

	/**
	 * Returns all the commerce subscription entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce subscription entries
	 * @param companyId the primary key of the company
	 * @return the matching commerce subscription entries, or an empty list if no matches were found
	 */
	public static List<CommerceSubscriptionEntry>
		getCommerceSubscriptionEntriesByUuidAndCompanyId(
			String uuid, long companyId) {

		return getService().getCommerceSubscriptionEntriesByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of commerce subscription entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce subscription entries
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of commerce subscription entries
	 * @param end the upper bound of the range of commerce subscription entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching commerce subscription entries, or an empty list if no matches were found
	 */
	public static List<CommerceSubscriptionEntry>
		getCommerceSubscriptionEntriesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<CommerceSubscriptionEntry> orderByComparator) {

		return getService().getCommerceSubscriptionEntriesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce subscription entries.
	 *
	 * @return the number of commerce subscription entries
	 */
	public static int getCommerceSubscriptionEntriesCount() {
		return getService().getCommerceSubscriptionEntriesCount();
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	public static int getCommerceSubscriptionEntriesCount(
		long companyId, long userId) {

		return getService().getCommerceSubscriptionEntriesCount(
			companyId, userId);
	}

	public static int getCommerceSubscriptionEntriesCount(
		long companyId, long groupId, long userId) {

		return getService().getCommerceSubscriptionEntriesCount(
			companyId, groupId, userId);
	}

	public static List<CommerceSubscriptionEntry>
		getCommerceSubscriptionEntriesToRenew() {

		return getService().getCommerceSubscriptionEntriesToRenew();
	}

	/**
	 * Returns the commerce subscription entry with the primary key.
	 *
	 * @param commerceSubscriptionEntryId the primary key of the commerce subscription entry
	 * @return the commerce subscription entry
	 * @throws PortalException if a commerce subscription entry with the primary key could not be found
	 */
	public static CommerceSubscriptionEntry getCommerceSubscriptionEntry(
			long commerceSubscriptionEntryId)
		throws PortalException {

		return getService().getCommerceSubscriptionEntry(
			commerceSubscriptionEntryId);
	}

	/**
	 * Returns the commerce subscription entry matching the UUID and group.
	 *
	 * @param uuid the commerce subscription entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce subscription entry
	 * @throws PortalException if a matching commerce subscription entry could not be found
	 */
	public static CommerceSubscriptionEntry
			getCommerceSubscriptionEntryByUuidAndGroupId(
				String uuid, long groupId)
		throws PortalException {

		return getService().getCommerceSubscriptionEntryByUuidAndGroupId(
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

	public static CommerceSubscriptionEntry
			incrementCommerceDeliverySubscriptionEntryCycle(
				long commerceSubscriptionEntryId)
		throws PortalException {

		return getService().incrementCommerceDeliverySubscriptionEntryCycle(
			commerceSubscriptionEntryId);
	}

	public static CommerceSubscriptionEntry
			incrementCommerceSubscriptionEntryCycle(
				long commerceSubscriptionEntryId)
		throws PortalException {

		return getService().incrementCommerceSubscriptionEntryCycle(
			commerceSubscriptionEntryId);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CommerceSubscriptionEntry> searchCommerceSubscriptionEntries(
				long companyId, Long maxSubscriptionCycles,
				Integer subscriptionStatus, String keywords, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchCommerceSubscriptionEntries(
			companyId, maxSubscriptionCycles, subscriptionStatus, keywords,
			start, end, sort);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CommerceSubscriptionEntry> searchCommerceSubscriptionEntries(
				long companyId, long[] groupIds, Long maxSubscriptionCycles,
				Integer subscriptionStatus, String keywords, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchCommerceSubscriptionEntries(
			companyId, groupIds, maxSubscriptionCycles, subscriptionStatus,
			keywords, start, end, sort);
	}

	/**
	 * Updates the commerce subscription entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceSubscriptionEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceSubscriptionEntry the commerce subscription entry
	 * @return the commerce subscription entry that was updated
	 */
	public static CommerceSubscriptionEntry updateCommerceSubscriptionEntry(
		CommerceSubscriptionEntry commerceSubscriptionEntry) {

		return getService().updateCommerceSubscriptionEntry(
			commerceSubscriptionEntry);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	public static CommerceSubscriptionEntry updateCommerceSubscriptionEntry(
			long commerceSubscriptionEntryId, int subscriptionLength,
			String subscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				subscriptionTypeSettingsUnicodeProperties,
			long maxSubscriptionCycles, int subscriptionStatus,
			int nextIterationDateMonth, int nextIterationDateDay,
			int nextIterationDateYear, int nextIterationDateHour,
			int nextIterationDateMinute)
		throws PortalException {

		return getService().updateCommerceSubscriptionEntry(
			commerceSubscriptionEntryId, subscriptionLength, subscriptionType,
			subscriptionTypeSettingsUnicodeProperties, maxSubscriptionCycles,
			subscriptionStatus, nextIterationDateMonth, nextIterationDateDay,
			nextIterationDateYear, nextIterationDateHour,
			nextIterationDateMinute);
	}

	public static CommerceSubscriptionEntry updateCommerceSubscriptionEntry(
			long commerceSubscriptionEntryId, int subscriptionLength,
			String subscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				subscriptionTypeSettingsUnicodeProperties,
			long maxSubscriptionCycles, int subscriptionStatus,
			int nextIterationDateMonth, int nextIterationDateDay,
			int nextIterationDateYear, int nextIterationDateHour,
			int nextIterationDateMinute, int deliverySubscriptionLength,
			String deliverySubscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				deliverySubscriptionTypeSettingsUnicodeProperties,
			long deliveryMaxSubscriptionCycles, int deliverySubscriptionStatus,
			int deliveryNextIterationDateMonth,
			int deliveryNextIterationDateDay, int deliveryNextIterationDateYear,
			int deliveryNextIterationDateHour,
			int deliveryNextIterationDateMinute)
		throws PortalException {

		return getService().updateCommerceSubscriptionEntry(
			commerceSubscriptionEntryId, subscriptionLength, subscriptionType,
			subscriptionTypeSettingsUnicodeProperties, maxSubscriptionCycles,
			subscriptionStatus, nextIterationDateMonth, nextIterationDateDay,
			nextIterationDateYear, nextIterationDateHour,
			nextIterationDateMinute, deliverySubscriptionLength,
			deliverySubscriptionType,
			deliverySubscriptionTypeSettingsUnicodeProperties,
			deliveryMaxSubscriptionCycles, deliverySubscriptionStatus,
			deliveryNextIterationDateMonth, deliveryNextIterationDateDay,
			deliveryNextIterationDateYear, deliveryNextIterationDateHour,
			deliveryNextIterationDateMinute);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x)
	 */
	@Deprecated
	public static CommerceSubscriptionEntry
			updateCommerceSubscriptionEntryIterationDates(
				long commerceSubscriptionEntryId,
				java.util.Date lastIterationDate)
		throws PortalException {

		return getService().updateCommerceSubscriptionEntryIterationDates(
			commerceSubscriptionEntryId, lastIterationDate);
	}

	public static CommerceSubscriptionEntry updateDeliverySubscriptionStatus(
			long commerceSubscriptionEntryId, int subscriptionStatus)
		throws PortalException {

		return getService().updateDeliverySubscriptionStatus(
			commerceSubscriptionEntryId, subscriptionStatus);
	}

	public static CommerceSubscriptionEntry updateSubscriptionStatus(
			long commerceSubscriptionEntryId, int subscriptionStatus)
		throws PortalException {

		return getService().updateSubscriptionStatus(
			commerceSubscriptionEntryId, subscriptionStatus);
	}

	public static CommerceSubscriptionEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceSubscriptionEntryLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceSubscriptionEntryLocalServiceUtil.class,
			CommerceSubscriptionEntryLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-993845869