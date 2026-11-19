/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CommercePaymentEntryLocalService}.
 *
 * @author Luca Pellizzon
 * @see CommercePaymentEntryLocalService
 * @generated
 */
public class CommercePaymentEntryLocalServiceWrapper
	implements CommercePaymentEntryLocalService,
			   ServiceWrapper<CommercePaymentEntryLocalService> {

	public CommercePaymentEntryLocalServiceWrapper() {
		this(null);
	}

	public CommercePaymentEntryLocalServiceWrapper(
		CommercePaymentEntryLocalService commercePaymentEntryLocalService) {

		_commercePaymentEntryLocalService = commercePaymentEntryLocalService;
	}

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
	@Override
	public com.liferay.commerce.payment.model.CommercePaymentEntry
		addCommercePaymentEntry(
			com.liferay.commerce.payment.model.CommercePaymentEntry
				commercePaymentEntry) {

		return _commercePaymentEntryLocalService.addCommercePaymentEntry(
			commercePaymentEntry);
	}

	@Override
	public com.liferay.commerce.payment.model.CommercePaymentEntry
			addCommercePaymentEntry(
				long userId, long classNameId, long classPK,
				long commerceChannelId, java.math.BigDecimal amount,
				String callbackURL, String cancelURL, String currencyCode,
				String languageId, String note, String payload,
				String paymentIntegrationKey, int paymentIntegrationType,
				String reasonKey, String transactionCode, int type,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePaymentEntryLocalService.addCommercePaymentEntry(
			userId, classNameId, classPK, commerceChannelId, amount,
			callbackURL, cancelURL, currencyCode, languageId, note, payload,
			paymentIntegrationKey, paymentIntegrationType, reasonKey,
			transactionCode, type, serviceContext);
	}

	@Override
	public com.liferay.commerce.payment.model.CommercePaymentEntry
			addOrUpdateCommercePaymentEntry(
				String externalReferenceCode, long userId, long classNameId,
				long classPK, long commerceChannelId,
				java.math.BigDecimal amount, String callbackURL,
				String cancelURL, String currencyCode, String errorMessages,
				String languageId, String note, String payload,
				String paymentIntegrationKey, int paymentIntegrationType,
				int paymentStatus, String reasonKey, String redirectURL,
				String transactionCode, int type,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePaymentEntryLocalService.
			addOrUpdateCommercePaymentEntry(
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
	@Override
	public com.liferay.commerce.payment.model.CommercePaymentEntry
		createCommercePaymentEntry(long commercePaymentEntryId) {

		return _commercePaymentEntryLocalService.createCommercePaymentEntry(
			commercePaymentEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePaymentEntryLocalService.createPersistedModel(
			primaryKeyObj);
	}

	@Override
	public void deleteCommercePaymentEntries(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commercePaymentEntryLocalService.deleteCommercePaymentEntries(
			companyId);
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
	@Override
	public com.liferay.commerce.payment.model.CommercePaymentEntry
			deleteCommercePaymentEntry(
				com.liferay.commerce.payment.model.CommercePaymentEntry
					commercePaymentEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePaymentEntryLocalService.deleteCommercePaymentEntry(
			commercePaymentEntry);
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
	@Override
	public com.liferay.commerce.payment.model.CommercePaymentEntry
			deleteCommercePaymentEntry(long commercePaymentEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePaymentEntryLocalService.deleteCommercePaymentEntry(
			commercePaymentEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePaymentEntryLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commercePaymentEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commercePaymentEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commercePaymentEntryLocalService.dynamicQuery();
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

		return _commercePaymentEntryLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _commercePaymentEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _commercePaymentEntryLocalService.dynamicQuery(
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

		return _commercePaymentEntryLocalService.dynamicQueryCount(
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

		return _commercePaymentEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.commerce.payment.model.CommercePaymentEntry
		fetchCommercePaymentEntry(long commercePaymentEntryId) {

		return _commercePaymentEntryLocalService.fetchCommercePaymentEntry(
			commercePaymentEntryId);
	}

	@Override
	public com.liferay.commerce.payment.model.CommercePaymentEntry
		fetchCommercePaymentEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return _commercePaymentEntryLocalService.
			fetchCommercePaymentEntryByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commercePaymentEntryLocalService.getActionableDynamicQuery();
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
	@Override
	public java.util.List
		<com.liferay.commerce.payment.model.CommercePaymentEntry>
			getCommercePaymentEntries(int start, int end) {

		return _commercePaymentEntryLocalService.getCommercePaymentEntries(
			start, end);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.payment.model.CommercePaymentEntry>
			getCommercePaymentEntries(
				long companyId, long classNameId, long classPK, int type,
				int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.commerce.payment.model.CommercePaymentEntry>
						orderByComparator) {

		return _commercePaymentEntryLocalService.getCommercePaymentEntries(
			companyId, classNameId, classPK, type, start, end,
			orderByComparator);
	}

	/**
	 * Returns the number of commerce payment entries.
	 *
	 * @return the number of commerce payment entries
	 */
	@Override
	public int getCommercePaymentEntriesCount() {
		return _commercePaymentEntryLocalService.
			getCommercePaymentEntriesCount();
	}

	@Override
	public int getCommercePaymentEntriesCount(
		long companyId, long classNameId, long classPK, int type) {

		return _commercePaymentEntryLocalService.getCommercePaymentEntriesCount(
			companyId, classNameId, classPK, type);
	}

	/**
	 * Returns the commerce payment entry with the primary key.
	 *
	 * @param commercePaymentEntryId the primary key of the commerce payment entry
	 * @return the commerce payment entry
	 * @throws PortalException if a commerce payment entry with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.payment.model.CommercePaymentEntry
			getCommercePaymentEntry(long commercePaymentEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePaymentEntryLocalService.getCommercePaymentEntry(
			commercePaymentEntryId);
	}

	@Override
	public com.liferay.commerce.payment.model.CommercePaymentEntry
			getCommercePaymentEntryByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePaymentEntryLocalService.
			getCommercePaymentEntryByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commercePaymentEntryLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commercePaymentEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePaymentEntryLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.payment.model.CommercePaymentEntry>
			getRefundCommercePaymentEntries(
				long companyId, long classNameId, long classPK, int start,
				int end) {

		return _commercePaymentEntryLocalService.
			getRefundCommercePaymentEntries(
				companyId, classNameId, classPK, start, end);
	}

	@Override
	public int getRefundCommercePaymentEntriesCount(
		long companyId, long classNameId, long classPK) {

		return _commercePaymentEntryLocalService.
			getRefundCommercePaymentEntriesCount(
				companyId, classNameId, classPK);
	}

	@Override
	public java.math.BigDecimal getRefundedAmount(
		long companyId, long classNameId, long classPK) {

		return _commercePaymentEntryLocalService.getRefundedAmount(
			companyId, classNameId, classPK);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.commerce.payment.model.CommercePaymentEntry>
			searchCommercePaymentEntries(
				long companyId, String keywords,
				java.util.LinkedHashMap<String, Object> params, int start,
				int end, com.liferay.portal.kernel.search.Sort sort) {

		return _commercePaymentEntryLocalService.searchCommercePaymentEntries(
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
	@Override
	public com.liferay.commerce.payment.model.CommercePaymentEntry
		updateCommercePaymentEntry(
			com.liferay.commerce.payment.model.CommercePaymentEntry
				commercePaymentEntry) {

		return _commercePaymentEntryLocalService.updateCommercePaymentEntry(
			commercePaymentEntry);
	}

	@Override
	public com.liferay.commerce.payment.model.CommercePaymentEntry
			updateCommercePaymentEntry(
				String externalReferenceCode, long commercePaymentEntryId,
				long commerceChannelId, java.math.BigDecimal amount,
				String callbackURL, String cancelURL, String currencyCode,
				String errorMessages, String languageId, String note,
				String payload, String paymentIntegrationKey,
				int paymentIntegrationType, int paymentStatus, String reasonKey,
				String redirectURL, String transactionCode, int type)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePaymentEntryLocalService.updateCommercePaymentEntry(
			externalReferenceCode, commercePaymentEntryId, commerceChannelId,
			amount, callbackURL, cancelURL, currencyCode, errorMessages,
			languageId, note, payload, paymentIntegrationKey,
			paymentIntegrationType, paymentStatus, reasonKey, redirectURL,
			transactionCode, type);
	}

	@Override
	public com.liferay.commerce.payment.model.CommercePaymentEntry
			updateExternalReferenceCode(
				long commercePaymentEntryId, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePaymentEntryLocalService.updateExternalReferenceCode(
			commercePaymentEntryId, externalReferenceCode);
	}

	@Override
	public com.liferay.commerce.payment.model.CommercePaymentEntry updateNote(
			long commercePaymentEntryId, String note)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePaymentEntryLocalService.updateNote(
			commercePaymentEntryId, note);
	}

	@Override
	public com.liferay.commerce.payment.model.CommercePaymentEntry
			updateReasonKey(long commercePaymentEntryId, String reasonKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePaymentEntryLocalService.updateReasonKey(
			commercePaymentEntryId, reasonKey);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commercePaymentEntryLocalService.getBasePersistence();
	}

	@Override
	public CommercePaymentEntryLocalService getWrappedService() {
		return _commercePaymentEntryLocalService;
	}

	@Override
	public void setWrappedService(
		CommercePaymentEntryLocalService commercePaymentEntryLocalService) {

		_commercePaymentEntryLocalService = commercePaymentEntryLocalService;
	}

	private CommercePaymentEntryLocalService _commercePaymentEntryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1819655817