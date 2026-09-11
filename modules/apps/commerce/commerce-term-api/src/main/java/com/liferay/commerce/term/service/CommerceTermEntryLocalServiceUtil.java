/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.term.service;

import com.liferay.commerce.term.model.CommerceTermEntry;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for CommerceTermEntry. This utility wraps
 * <code>com.liferay.commerce.term.service.impl.CommerceTermEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Luca Pellizzon
 * @see CommerceTermEntryLocalService
 * @generated
 */
public class CommerceTermEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.term.service.impl.CommerceTermEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce term entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTermEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTermEntry the commerce term entry
	 * @return the commerce term entry that was added
	 */
	public static CommerceTermEntry addCommerceTermEntry(
		CommerceTermEntry commerceTermEntry) {

		return getService().addCommerceTermEntry(commerceTermEntry);
	}

	public static CommerceTermEntry addCommerceTermEntry(
			String externalReferenceCode, long userId, boolean active,
			Map<java.util.Locale, String> descriptionMap, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, Map<java.util.Locale, String> labelMap,
			String name, double priority, String type, String typeSettings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceTermEntry(
			externalReferenceCode, userId, active, descriptionMap,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, labelMap, name, priority, type, typeSettings,
			serviceContext);
	}

	public static com.liferay.commerce.term.model.CTermEntryLocalization
			addCTermEntryLocalization(
				CommerceTermEntry commerceTermEntry, String languageId,
				String description, String label)
		throws PortalException {

		return getService().addCTermEntryLocalization(
			commerceTermEntry, languageId, description, label);
	}

	public static void checkCommerceTermEntries() throws PortalException {
		getService().checkCommerceTermEntries();
	}

	/**
	 * Creates a new commerce term entry with the primary key. Does not add the commerce term entry to the database.
	 *
	 * @param commerceTermEntryId the primary key for the new commerce term entry
	 * @return the new commerce term entry
	 */
	public static CommerceTermEntry createCommerceTermEntry(
		long commerceTermEntryId) {

		return getService().createCommerceTermEntry(commerceTermEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the commerce term entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTermEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTermEntry the commerce term entry
	 * @return the commerce term entry that was removed
	 * @throws PortalException
	 */
	public static CommerceTermEntry deleteCommerceTermEntry(
			CommerceTermEntry commerceTermEntry)
		throws PortalException {

		return getService().deleteCommerceTermEntry(commerceTermEntry);
	}

	/**
	 * Deletes the commerce term entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTermEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTermEntryId the primary key of the commerce term entry
	 * @return the commerce term entry that was removed
	 * @throws PortalException if a commerce term entry with the primary key could not be found
	 */
	public static CommerceTermEntry deleteCommerceTermEntry(
			long commerceTermEntryId)
		throws PortalException {

		return getService().deleteCommerceTermEntry(commerceTermEntryId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.term.model.impl.CommerceTermEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.term.model.impl.CommerceTermEntryModelImpl</code>.
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

	public static CommerceTermEntry fetchCommerceTermEntry(
		long commerceTermEntryId) {

		return getService().fetchCommerceTermEntry(commerceTermEntryId);
	}

	public static CommerceTermEntry
		fetchCommerceTermEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return getService().fetchCommerceTermEntryByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce term entry with the matching UUID and company.
	 *
	 * @param uuid the commerce term entry's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce term entry, or <code>null</code> if a matching commerce term entry could not be found
	 */
	public static CommerceTermEntry fetchCommerceTermEntryByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().fetchCommerceTermEntryByUuidAndCompanyId(
			uuid, companyId);
	}

	public static com.liferay.commerce.term.model.CTermEntryLocalization
		fetchCTermEntryLocalization(
			long commerceTermEntryId, String languageId) {

		return getService().fetchCTermEntryLocalization(
			commerceTermEntryId, languageId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the commerce term entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.term.model.impl.CommerceTermEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce term entries
	 * @param end the upper bound of the range of commerce term entries (not inclusive)
	 * @return the range of commerce term entries
	 */
	public static List<CommerceTermEntry> getCommerceTermEntries(
		int start, int end) {

		return getService().getCommerceTermEntries(start, end);
	}

	public static List<CommerceTermEntry> getCommerceTermEntries(
		long companyId, String type) {

		return getService().getCommerceTermEntries(companyId, type);
	}

	/**
	 * Returns the number of commerce term entries.
	 *
	 * @return the number of commerce term entries
	 */
	public static int getCommerceTermEntriesCount() {
		return getService().getCommerceTermEntriesCount();
	}

	/**
	 * Returns the commerce term entry with the primary key.
	 *
	 * @param commerceTermEntryId the primary key of the commerce term entry
	 * @return the commerce term entry
	 * @throws PortalException if a commerce term entry with the primary key could not be found
	 */
	public static CommerceTermEntry getCommerceTermEntry(
			long commerceTermEntryId)
		throws PortalException {

		return getService().getCommerceTermEntry(commerceTermEntryId);
	}

	public static CommerceTermEntry getCommerceTermEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getCommerceTermEntryByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce term entry with the matching UUID and company.
	 *
	 * @param uuid the commerce term entry's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce term entry
	 * @throws PortalException if a matching commerce term entry could not be found
	 */
	public static CommerceTermEntry getCommerceTermEntryByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException {

		return getService().getCommerceTermEntryByUuidAndCompanyId(
			uuid, companyId);
	}

	public static com.liferay.commerce.term.model.CTermEntryLocalization
			getCTermEntryLocalization(
				long commerceTermEntryId, String languageId)
		throws PortalException {

		return getService().getCTermEntryLocalization(
			commerceTermEntryId, languageId);
	}

	public static List<String> getCTermEntryLocalizationLanguageIds(
		long commerceTermEntryId) {

		return getService().getCTermEntryLocalizationLanguageIds(
			commerceTermEntryId);
	}

	public static List<com.liferay.commerce.term.model.CTermEntryLocalization>
		getCTermEntryLocalizations(long commerceTermEntryId) {

		return getService().getCTermEntryLocalizations(commerceTermEntryId);
	}

	public static List<CommerceTermEntry> getDeliveryCommerceTermEntries(
		long companyId, long commerceOrderTypeId,
		long commerceShippingOptionId) {

		return getService().getDeliveryCommerceTermEntries(
			companyId, commerceOrderTypeId, commerceShippingOptionId);
	}

	public static int getDeliveryCommerceTermEntriesCount(
		long companyId, long commerceOrderTypeId,
		long commerceShippingOptionId) {

		return getService().getDeliveryCommerceTermEntriesCount(
			companyId, commerceOrderTypeId, commerceShippingOptionId);
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

	public static List<CommerceTermEntry> getPaymentCommerceTermEntries(
		long companyId, long commerceOrderTypeId,
		long commercePaymentMethodGroupRelId) {

		return getService().getPaymentCommerceTermEntries(
			companyId, commerceOrderTypeId, commercePaymentMethodGroupRelId);
	}

	public static int getPaymentCommerceTermEntriesCount(
		long companyId, long commerceOrderTypeId,
		long commercePaymentMethodGroupRelId) {

		return getService().getPaymentCommerceTermEntriesCount(
			companyId, commerceOrderTypeId, commercePaymentMethodGroupRelId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	public static com.liferay.portal.kernel.search.Hits search(
		com.liferay.portal.kernel.search.SearchContext searchContext) {

		return getService().search(searchContext);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CommerceTermEntry> searchCommerceTermEntries(
				long companyId, long accountEntryId, String type,
				String keywords, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchCommerceTermEntries(
			companyId, accountEntryId, type, keywords, start, end, sort);
	}

	/**
	 * Updates the commerce term entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTermEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTermEntry the commerce term entry
	 * @return the commerce term entry that was updated
	 */
	public static CommerceTermEntry updateCommerceTermEntry(
		CommerceTermEntry commerceTermEntry) {

		return getService().updateCommerceTermEntry(commerceTermEntry);
	}

	public static CommerceTermEntry updateCommerceTermEntry(
			long userId, long commerceTermEntryId, boolean active,
			Map<java.util.Locale, String> descriptionMap, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, Map<java.util.Locale, String> labelMap,
			String name, double priority, String typeSettings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCommerceTermEntry(
			userId, commerceTermEntryId, active, descriptionMap,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, labelMap, name, priority, typeSettings,
			serviceContext);
	}

	public static CommerceTermEntry
			updateCommerceTermEntryExternalReferenceCode(
				String externalReferenceCode, long commerceTermEntryId)
		throws PortalException {

		return getService().updateCommerceTermEntryExternalReferenceCode(
			externalReferenceCode, commerceTermEntryId);
	}

	public static com.liferay.commerce.term.model.CTermEntryLocalization
			updateCTermEntryLocalization(
				CommerceTermEntry commerceTermEntry, String languageId,
				String description, String label)
		throws PortalException {

		return getService().updateCTermEntryLocalization(
			commerceTermEntry, languageId, description, label);
	}

	public static List<com.liferay.commerce.term.model.CTermEntryLocalization>
			updateCTermEntryLocalizations(
				CommerceTermEntry commerceTermEntry,
				Map<String, String> descriptionMap,
				Map<String, String> labelMap)
		throws PortalException {

		return getService().updateCTermEntryLocalizations(
			commerceTermEntry, descriptionMap, labelMap);
	}

	public static CommerceTermEntry updateStatus(
			long userId, long commerceTermEntryId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateStatus(
			userId, commerceTermEntryId, status, serviceContext);
	}

	public static CommerceTermEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceTermEntryLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceTermEntryLocalServiceUtil.class,
			CommerceTermEntryLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-965785376