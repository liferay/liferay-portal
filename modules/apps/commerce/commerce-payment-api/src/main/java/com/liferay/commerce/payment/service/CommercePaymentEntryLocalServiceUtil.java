/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.service;

import com.liferay.commerce.payment.model.CommercePaymentEntry;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommercePaymentEntry. This utility wraps
 * <code>com.liferay.commerce.payment.service.impl.CommercePaymentEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Luca Pellizzon
 * @see CommercePaymentEntryLocalService
 * @generated
 */
public class CommercePaymentEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.payment.service.impl.CommercePaymentEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the commerce payment entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePaymentEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePaymentEntry the commerce payment entry
	 * @return the commerce payment entry that was added
	 */
	public static CommercePaymentEntry addCommercePaymentEntry(
		CommercePaymentEntry commercePaymentEntry) {

		return getService().addCommercePaymentEntry(commercePaymentEntry);
	}

	public static CommercePaymentEntry addCommercePaymentEntry(
			long userId, long classNameId, long classPK, long commerceChannelId,
			java.math.BigDecimal amount, String callbackURL, String cancelURL,
			String currencyCode, String languageId, String note, String payload,
			String paymentIntegrationKey, int paymentIntegrationType,
			String reasonKey, String transactionCode, int type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommercePaymentEntry(
			userId, classNameId, classPK, commerceChannelId, amount,
			callbackURL, cancelURL, currencyCode, languageId, note, payload,
			paymentIntegrationKey, paymentIntegrationType, reasonKey,
			transactionCode, type, serviceContext);
	}

	public static CommercePaymentEntry addOrUpdateCommercePaymentEntry(
			String externalReferenceCode, long userId, long classNameId,
			long classPK, long commerceChannelId, java.math.BigDecimal amount,
			String callbackURL, String cancelURL, String currencyCode,
			String errorMessages, String languageId, String note,
			String payload, String paymentIntegrationKey,
			int paymentIntegrationType, int paymentStatus, String reasonKey,
			String redirectURL, String transactionCode, int type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateCommercePaymentEntry(
			externalReferenceCode, userId, classNameId, classPK,
			commerceChannelId, amount, callbackURL, cancelURL, currencyCode,
			errorMessages, languageId, note, payload, paymentIntegrationKey,
			paymentIntegrationType, paymentStatus, reasonKey, redirectURL,
			transactionCode, type, serviceContext);
	}

	/**
	 * Creates a new commerce payment entry with the primary key. Does not add the commerce payment entry to the database.
	 *
	 * @param commercePaymentEntryId the primary key for the new commerce payment entry
	 * @return the new commerce payment entry
	 */
	public static CommercePaymentEntry createCommercePaymentEntry(
		long commercePaymentEntryId) {

		return getService().createCommercePaymentEntry(commercePaymentEntryId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteCommercePaymentEntries(long companyId)
		throws PortalException {

		getService().deleteCommercePaymentEntries(companyId);
	}

	/**
	 * Deletes the commerce payment entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePaymentEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePaymentEntry the commerce payment entry
	 * @return the commerce payment entry that was removed
	 * @throws PortalException
	 */
	public static CommercePaymentEntry deleteCommercePaymentEntry(
			CommercePaymentEntry commercePaymentEntry)
		throws PortalException {

		return getService().deleteCommercePaymentEntry(commercePaymentEntry);
	}

	/**
	 * Deletes the commerce payment entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePaymentEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePaymentEntryId the primary key of the commerce payment entry
	 * @return the commerce payment entry that was removed
	 * @throws PortalException if a commerce payment entry with the primary key could not be found
	 */
	public static CommercePaymentEntry deleteCommercePaymentEntry(
			long commercePaymentEntryId)
		throws PortalException {

		return getService().deleteCommercePaymentEntry(commercePaymentEntryId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.payment.model.impl.CommercePaymentEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.payment.model.impl.CommercePaymentEntryModelImpl</code>.
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

	public static CommercePaymentEntry fetchCommercePaymentEntry(
		long commercePaymentEntryId) {

		return getService().fetchCommercePaymentEntry(commercePaymentEntryId);
	}

	public static CommercePaymentEntry
		fetchCommercePaymentEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return getService().fetchCommercePaymentEntryByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the commerce payment entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.payment.model.impl.CommercePaymentEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce payment entries
	 * @param end the upper bound of the range of commerce payment entries (not inclusive)
	 * @return the range of commerce payment entries
	 */
	public static List<CommercePaymentEntry> getCommercePaymentEntries(
		int start, int end) {

		return getService().getCommercePaymentEntries(start, end);
	}

	public static List<CommercePaymentEntry> getCommercePaymentEntries(
		long companyId, long classNameId, long classPK, int type, int start,
		int end, OrderByComparator<CommercePaymentEntry> orderByComparator) {

		return getService().getCommercePaymentEntries(
			companyId, classNameId, classPK, type, start, end,
			orderByComparator);
	}

	/**
	 * Returns the number of commerce payment entries.
	 *
	 * @return the number of commerce payment entries
	 */
	public static int getCommercePaymentEntriesCount() {
		return getService().getCommercePaymentEntriesCount();
	}

	public static int getCommercePaymentEntriesCount(
		long companyId, long classNameId, long classPK, int type) {

		return getService().getCommercePaymentEntriesCount(
			companyId, classNameId, classPK, type);
	}

	/**
	 * Returns the commerce payment entry with the primary key.
	 *
	 * @param commercePaymentEntryId the primary key of the commerce payment entry
	 * @return the commerce payment entry
	 * @throws PortalException if a commerce payment entry with the primary key could not be found
	 */
	public static CommercePaymentEntry getCommercePaymentEntry(
			long commercePaymentEntryId)
		throws PortalException {

		return getService().getCommercePaymentEntry(commercePaymentEntryId);
	}

	public static CommercePaymentEntry
			getCommercePaymentEntryByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getCommercePaymentEntryByExternalReferenceCode(
			externalReferenceCode, companyId);
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

	public static List<CommercePaymentEntry> getRefundCommercePaymentEntries(
		long companyId, long classNameId, long classPK, int start, int end) {

		return getService().getRefundCommercePaymentEntries(
			companyId, classNameId, classPK, start, end);
	}

	public static int getRefundCommercePaymentEntriesCount(
		long companyId, long classNameId, long classPK) {

		return getService().getRefundCommercePaymentEntriesCount(
			companyId, classNameId, classPK);
	}

	public static java.math.BigDecimal getRefundedAmount(
		long companyId, long classNameId, long classPK) {

		return getService().getRefundedAmount(companyId, classNameId, classPK);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CommercePaymentEntry> searchCommercePaymentEntries(
			long companyId, String keywords,
			java.util.LinkedHashMap<String, Object> params, int start, int end,
			com.liferay.portal.kernel.search.Sort sort) {

		return getService().searchCommercePaymentEntries(
			companyId, keywords, params, start, end, sort);
	}

	/**
	 * Updates the commerce payment entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePaymentEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePaymentEntry the commerce payment entry
	 * @return the commerce payment entry that was updated
	 */
	public static CommercePaymentEntry updateCommercePaymentEntry(
		CommercePaymentEntry commercePaymentEntry) {

		return getService().updateCommercePaymentEntry(commercePaymentEntry);
	}

	public static CommercePaymentEntry updateCommercePaymentEntry(
			String externalReferenceCode, long commercePaymentEntryId,
			long commerceChannelId, java.math.BigDecimal amount,
			String callbackURL, String cancelURL, String currencyCode,
			String errorMessages, String languageId, String note,
			String payload, String paymentIntegrationKey,
			int paymentIntegrationType, int paymentStatus, String reasonKey,
			String redirectURL, String transactionCode, int type)
		throws PortalException {

		return getService().updateCommercePaymentEntry(
			externalReferenceCode, commercePaymentEntryId, commerceChannelId,
			amount, callbackURL, cancelURL, currencyCode, errorMessages,
			languageId, note, payload, paymentIntegrationKey,
			paymentIntegrationType, paymentStatus, reasonKey, redirectURL,
			transactionCode, type);
	}

	public static CommercePaymentEntry updateExternalReferenceCode(
			long commercePaymentEntryId, String externalReferenceCode)
		throws PortalException {

		return getService().updateExternalReferenceCode(
			commercePaymentEntryId, externalReferenceCode);
	}

	public static CommercePaymentEntry updateNote(
			long commercePaymentEntryId, String note)
		throws PortalException {

		return getService().updateNote(commercePaymentEntryId, note);
	}

	public static CommercePaymentEntry updateReasonKey(
			long commercePaymentEntryId, String reasonKey)
		throws PortalException {

		return getService().updateReasonKey(commercePaymentEntryId, reasonKey);
	}

	public static CommercePaymentEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommercePaymentEntryLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommercePaymentEntryLocalServiceUtil.class,
			CommercePaymentEntryLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1958188718