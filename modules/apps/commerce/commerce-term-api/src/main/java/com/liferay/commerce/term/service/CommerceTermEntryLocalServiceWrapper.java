/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.term.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CommerceTermEntryLocalService}.
 *
 * @author Luca Pellizzon
 * @see CommerceTermEntryLocalService
 * @generated
 */
public class CommerceTermEntryLocalServiceWrapper
	implements CommerceTermEntryLocalService,
			   ServiceWrapper<CommerceTermEntryLocalService> {

	public CommerceTermEntryLocalServiceWrapper() {
		this(null);
	}

	public CommerceTermEntryLocalServiceWrapper(
		CommerceTermEntryLocalService commerceTermEntryLocalService) {

		_commerceTermEntryLocalService = commerceTermEntryLocalService;
	}

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
	@Override
	public com.liferay.commerce.term.model.CommerceTermEntry
		addCommerceTermEntry(
			com.liferay.commerce.term.model.CommerceTermEntry
				commerceTermEntry) {

		return _commerceTermEntryLocalService.addCommerceTermEntry(
			commerceTermEntry);
	}

	@Override
	public com.liferay.commerce.term.model.CommerceTermEntry
			addCommerceTermEntry(
				String externalReferenceCode, long userId, boolean active,
				java.util.Map<java.util.Locale, String> descriptionMap,
				int displayDateMonth, int displayDateDay, int displayDateYear,
				int displayDateHour, int displayDateMinute,
				int expirationDateMonth, int expirationDateDay,
				int expirationDateYear, int expirationDateHour,
				int expirationDateMinute, boolean neverExpire,
				java.util.Map<java.util.Locale, String> labelMap, String name,
				double priority, String type, String typeSettings,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTermEntryLocalService.addCommerceTermEntry(
			externalReferenceCode, userId, active, descriptionMap,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, labelMap, name, priority, type, typeSettings,
			serviceContext);
	}

	@Override
	public com.liferay.commerce.term.model.CTermEntryLocalization
			addCTermEntryLocalization(
				com.liferay.commerce.term.model.CommerceTermEntry
					commerceTermEntry,
				String languageId, String description, String label)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTermEntryLocalService.addCTermEntryLocalization(
			commerceTermEntry, languageId, description, label);
	}

	@Override
	public void checkCommerceTermEntries()
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceTermEntryLocalService.checkCommerceTermEntries();
	}

	/**
	 * Creates a new commerce term entry with the primary key. Does not add the commerce term entry to the database.
	 *
	 * @param commerceTermEntryId the primary key for the new commerce term entry
	 * @return the new commerce term entry
	 */
	@Override
	public com.liferay.commerce.term.model.CommerceTermEntry
		createCommerceTermEntry(long commerceTermEntryId) {

		return _commerceTermEntryLocalService.createCommerceTermEntry(
			commerceTermEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTermEntryLocalService.createPersistedModel(
			primaryKeyObj);
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
	@Override
	public com.liferay.commerce.term.model.CommerceTermEntry
			deleteCommerceTermEntry(
				com.liferay.commerce.term.model.CommerceTermEntry
					commerceTermEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTermEntryLocalService.deleteCommerceTermEntry(
			commerceTermEntry);
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
	@Override
	public com.liferay.commerce.term.model.CommerceTermEntry
			deleteCommerceTermEntry(long commerceTermEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTermEntryLocalService.deleteCommerceTermEntry(
			commerceTermEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTermEntryLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commerceTermEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commerceTermEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commerceTermEntryLocalService.dynamicQuery();
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

		return _commerceTermEntryLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _commerceTermEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _commerceTermEntryLocalService.dynamicQuery(
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

		return _commerceTermEntryLocalService.dynamicQueryCount(dynamicQuery);
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

		return _commerceTermEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.commerce.term.model.CommerceTermEntry
		fetchCommerceTermEntry(long commerceTermEntryId) {

		return _commerceTermEntryLocalService.fetchCommerceTermEntry(
			commerceTermEntryId);
	}

	@Override
	public com.liferay.commerce.term.model.CommerceTermEntry
		fetchCommerceTermEntryByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return _commerceTermEntryLocalService.
			fetchCommerceTermEntryByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce term entry with the matching UUID and company.
	 *
	 * @param uuid the commerce term entry's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce term entry, or <code>null</code> if a matching commerce term entry could not be found
	 */
	@Override
	public com.liferay.commerce.term.model.CommerceTermEntry
		fetchCommerceTermEntryByUuidAndCompanyId(String uuid, long companyId) {

		return _commerceTermEntryLocalService.
			fetchCommerceTermEntryByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.commerce.term.model.CTermEntryLocalization
		fetchCTermEntryLocalization(
			long commerceTermEntryId, String languageId) {

		return _commerceTermEntryLocalService.fetchCTermEntryLocalization(
			commerceTermEntryId, languageId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commerceTermEntryLocalService.getActionableDynamicQuery();
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
	@Override
	public java.util.List<com.liferay.commerce.term.model.CommerceTermEntry>
		getCommerceTermEntries(int start, int end) {

		return _commerceTermEntryLocalService.getCommerceTermEntries(
			start, end);
	}

	@Override
	public java.util.List<com.liferay.commerce.term.model.CommerceTermEntry>
		getCommerceTermEntries(long companyId, String type) {

		return _commerceTermEntryLocalService.getCommerceTermEntries(
			companyId, type);
	}

	/**
	 * Returns the number of commerce term entries.
	 *
	 * @return the number of commerce term entries
	 */
	@Override
	public int getCommerceTermEntriesCount() {
		return _commerceTermEntryLocalService.getCommerceTermEntriesCount();
	}

	/**
	 * Returns the commerce term entry with the primary key.
	 *
	 * @param commerceTermEntryId the primary key of the commerce term entry
	 * @return the commerce term entry
	 * @throws PortalException if a commerce term entry with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.term.model.CommerceTermEntry
			getCommerceTermEntry(long commerceTermEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTermEntryLocalService.getCommerceTermEntry(
			commerceTermEntryId);
	}

	@Override
	public com.liferay.commerce.term.model.CommerceTermEntry
			getCommerceTermEntryByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTermEntryLocalService.
			getCommerceTermEntryByExternalReferenceCode(
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
	@Override
	public com.liferay.commerce.term.model.CommerceTermEntry
			getCommerceTermEntryByUuidAndCompanyId(String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTermEntryLocalService.
			getCommerceTermEntryByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.commerce.term.model.CTermEntryLocalization
			getCTermEntryLocalization(
				long commerceTermEntryId, String languageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTermEntryLocalService.getCTermEntryLocalization(
			commerceTermEntryId, languageId);
	}

	@Override
	public java.util.List<String> getCTermEntryLocalizationLanguageIds(
		long commerceTermEntryId) {

		return _commerceTermEntryLocalService.
			getCTermEntryLocalizationLanguageIds(commerceTermEntryId);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.term.model.CTermEntryLocalization>
			getCTermEntryLocalizations(long commerceTermEntryId) {

		return _commerceTermEntryLocalService.getCTermEntryLocalizations(
			commerceTermEntryId);
	}

	@Override
	public java.util.List<com.liferay.commerce.term.model.CommerceTermEntry>
		getDeliveryCommerceTermEntries(
			long companyId, long commerceOrderTypeId,
			long commerceShippingOptionId) {

		return _commerceTermEntryLocalService.getDeliveryCommerceTermEntries(
			companyId, commerceOrderTypeId, commerceShippingOptionId);
	}

	@Override
	public int getDeliveryCommerceTermEntriesCount(
		long companyId, long commerceOrderTypeId,
		long commerceShippingOptionId) {

		return _commerceTermEntryLocalService.
			getDeliveryCommerceTermEntriesCount(
				companyId, commerceOrderTypeId, commerceShippingOptionId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _commerceTermEntryLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commerceTermEntryLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceTermEntryLocalService.getOSGiServiceIdentifier();
	}

	@Override
	public java.util.List<com.liferay.commerce.term.model.CommerceTermEntry>
		getPaymentCommerceTermEntries(
			long companyId, long commerceOrderTypeId,
			long commercePaymentMethodGroupRelId) {

		return _commerceTermEntryLocalService.getPaymentCommerceTermEntries(
			companyId, commerceOrderTypeId, commercePaymentMethodGroupRelId);
	}

	@Override
	public int getPaymentCommerceTermEntriesCount(
		long companyId, long commerceOrderTypeId,
		long commercePaymentMethodGroupRelId) {

		return _commerceTermEntryLocalService.
			getPaymentCommerceTermEntriesCount(
				companyId, commerceOrderTypeId,
				commercePaymentMethodGroupRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTermEntryLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public com.liferay.portal.kernel.search.Hits search(
		com.liferay.portal.kernel.search.SearchContext searchContext) {

		return _commerceTermEntryLocalService.search(searchContext);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.commerce.term.model.CommerceTermEntry>
				searchCommerceTermEntries(
					long companyId, long accountEntryId, String type,
					String keywords, int start, int end,
					com.liferay.portal.kernel.search.Sort sort)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTermEntryLocalService.searchCommerceTermEntries(
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
	@Override
	public com.liferay.commerce.term.model.CommerceTermEntry
		updateCommerceTermEntry(
			com.liferay.commerce.term.model.CommerceTermEntry
				commerceTermEntry) {

		return _commerceTermEntryLocalService.updateCommerceTermEntry(
			commerceTermEntry);
	}

	@Override
	public com.liferay.commerce.term.model.CommerceTermEntry
			updateCommerceTermEntry(
				long userId, long commerceTermEntryId, boolean active,
				java.util.Map<java.util.Locale, String> descriptionMap,
				int displayDateMonth, int displayDateDay, int displayDateYear,
				int displayDateHour, int displayDateMinute,
				int expirationDateMonth, int expirationDateDay,
				int expirationDateYear, int expirationDateHour,
				int expirationDateMinute, boolean neverExpire,
				java.util.Map<java.util.Locale, String> labelMap, String name,
				double priority, String typeSettings,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTermEntryLocalService.updateCommerceTermEntry(
			userId, commerceTermEntryId, active, descriptionMap,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire, labelMap, name, priority, typeSettings,
			serviceContext);
	}

	@Override
	public com.liferay.commerce.term.model.CommerceTermEntry
			updateCommerceTermEntryExternalReferenceCode(
				String externalReferenceCode, long commerceTermEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTermEntryLocalService.
			updateCommerceTermEntryExternalReferenceCode(
				externalReferenceCode, commerceTermEntryId);
	}

	@Override
	public com.liferay.commerce.term.model.CTermEntryLocalization
			updateCTermEntryLocalization(
				com.liferay.commerce.term.model.CommerceTermEntry
					commerceTermEntry,
				String languageId, String description, String label)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTermEntryLocalService.updateCTermEntryLocalization(
			commerceTermEntry, languageId, description, label);
	}

	@Override
	public java.util.List
		<com.liferay.commerce.term.model.CTermEntryLocalization>
				updateCTermEntryLocalizations(
					com.liferay.commerce.term.model.CommerceTermEntry
						commerceTermEntry,
					java.util.Map<String, String> descriptionMap,
					java.util.Map<String, String> labelMap)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTermEntryLocalService.updateCTermEntryLocalizations(
			commerceTermEntry, descriptionMap, labelMap);
	}

	@Override
	public com.liferay.commerce.term.model.CommerceTermEntry updateStatus(
			long userId, long commerceTermEntryId, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTermEntryLocalService.updateStatus(
			userId, commerceTermEntryId, status, serviceContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commerceTermEntryLocalService.getBasePersistence();
	}

	@Override
	public CommerceTermEntryLocalService getWrappedService() {
		return _commerceTermEntryLocalService;
	}

	@Override
	public void setWrappedService(
		CommerceTermEntryLocalService commerceTermEntryLocalService) {

		_commerceTermEntryLocalService = commerceTermEntryLocalService;
	}

	private CommerceTermEntryLocalService _commerceTermEntryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-2145785400