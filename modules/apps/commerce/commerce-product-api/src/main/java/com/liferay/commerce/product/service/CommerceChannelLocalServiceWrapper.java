/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CommerceChannelLocalService}.
 *
 * @author Marco Leo
 * @see CommerceChannelLocalService
 * @generated
 */
public class CommerceChannelLocalServiceWrapper
	implements CommerceChannelLocalService,
			   ServiceWrapper<CommerceChannelLocalService> {

	public CommerceChannelLocalServiceWrapper() {
		this(null);
	}

	public CommerceChannelLocalServiceWrapper(
		CommerceChannelLocalService commerceChannelLocalService) {

		_commerceChannelLocalService = commerceChannelLocalService;
	}

	/**
	 * Adds the commerce channel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceChannelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceChannel the commerce channel
	 * @return the commerce channel that was added
	 */
	@Override
	public CommerceChannel addCommerceChannel(CommerceChannel commerceChannel) {
		return _commerceChannelLocalService.addCommerceChannel(commerceChannel);
	}

	@Override
	public CommerceChannel addCommerceChannel(
			String externalReferenceCode, long accountEntryId, long siteGroupId,
			String name, String type,
			com.liferay.portal.kernel.util.UnicodeProperties
				typeSettingsUnicodeProperties,
			String commerceCurrencyCode,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceChannelLocalService.addCommerceChannel(
			externalReferenceCode, accountEntryId, siteGroupId, name, type,
			typeSettingsUnicodeProperties, commerceCurrencyCode,
			serviceContext);
	}

	@Override
	public CommerceChannel addOrUpdateCommerceChannel(
			long userId, String externalReferenceCode, long accountEntryId,
			long siteGroupId, String name, String type,
			com.liferay.portal.kernel.util.UnicodeProperties
				typeSettingsUnicodeProperties,
			String commerceCurrencyCode,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceChannelLocalService.addOrUpdateCommerceChannel(
			userId, externalReferenceCode, accountEntryId, siteGroupId, name,
			type, typeSettingsUnicodeProperties, commerceCurrencyCode,
			serviceContext);
	}

	/**
	 * Creates a new commerce channel with the primary key. Does not add the commerce channel to the database.
	 *
	 * @param commerceChannelId the primary key for the new commerce channel
	 * @return the new commerce channel
	 */
	@Override
	public CommerceChannel createCommerceChannel(long commerceChannelId) {
		return _commerceChannelLocalService.createCommerceChannel(
			commerceChannelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceChannelLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the commerce channel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceChannelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceChannel the commerce channel
	 * @return the commerce channel that was removed
	 * @throws PortalException
	 */
	@Override
	public CommerceChannel deleteCommerceChannel(
			CommerceChannel commerceChannel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceChannelLocalService.deleteCommerceChannel(
			commerceChannel);
	}

	/**
	 * Deletes the commerce channel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceChannelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceChannelId the primary key of the commerce channel
	 * @return the commerce channel that was removed
	 * @throws PortalException if a commerce channel with the primary key could not be found
	 */
	@Override
	public CommerceChannel deleteCommerceChannel(long commerceChannelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceChannelLocalService.deleteCommerceChannel(
			commerceChannelId);
	}

	@Override
	public void deleteCommerceChannels(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceChannelLocalService.deleteCommerceChannels(companyId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceChannelLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commerceChannelLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commerceChannelLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commerceChannelLocalService.dynamicQuery();
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

		return _commerceChannelLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CommerceChannelModelImpl</code>.
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

		return _commerceChannelLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CommerceChannelModelImpl</code>.
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

		return _commerceChannelLocalService.dynamicQuery(
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

		return _commerceChannelLocalService.dynamicQueryCount(dynamicQuery);
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

		return _commerceChannelLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public CommerceChannel fetchCommerceChannel(long commerceChannelId) {
		return _commerceChannelLocalService.fetchCommerceChannel(
			commerceChannelId);
	}

	@Override
	public CommerceChannel fetchCommerceChannelByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return _commerceChannelLocalService.
			fetchCommerceChannelByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public CommerceChannel fetchCommerceChannelByGroupClassPK(long groupId) {
		return _commerceChannelLocalService.fetchCommerceChannelByGroupClassPK(
			groupId);
	}

	@Override
	public CommerceChannel fetchCommerceChannelBySiteGroupId(long siteGroupId) {
		return _commerceChannelLocalService.fetchCommerceChannelBySiteGroupId(
			siteGroupId);
	}

	/**
	 * Returns the commerce channel with the matching UUID and company.
	 *
	 * @param uuid the commerce channel's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce channel, or <code>null</code> if a matching commerce channel could not be found
	 */
	@Override
	public CommerceChannel fetchCommerceChannelByUuidAndCompanyId(
		String uuid, long companyId) {

		return _commerceChannelLocalService.
			fetchCommerceChannelByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.model.Group fetchCommerceChannelGroup(
			long commerceChannelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceChannelLocalService.fetchCommerceChannelGroup(
			commerceChannelId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commerceChannelLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce channel with the primary key.
	 *
	 * @param commerceChannelId the primary key of the commerce channel
	 * @return the commerce channel
	 * @throws PortalException if a commerce channel with the primary key could not be found
	 */
	@Override
	public CommerceChannel getCommerceChannel(long commerceChannelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceChannelLocalService.getCommerceChannel(
			commerceChannelId);
	}

	@Override
	public CommerceChannel getCommerceChannelByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceChannelLocalService.
			getCommerceChannelByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	@Override
	public CommerceChannel getCommerceChannelByGroupId(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceChannelLocalService.getCommerceChannelByGroupId(
			groupId);
	}

	@Override
	public CommerceChannel getCommerceChannelByOrderGroupId(long orderGroupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceChannelLocalService.getCommerceChannelByOrderGroupId(
			orderGroupId);
	}

	/**
	 * Returns the commerce channel with the matching UUID and company.
	 *
	 * @param uuid the commerce channel's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce channel
	 * @throws PortalException if a matching commerce channel could not be found
	 */
	@Override
	public CommerceChannel getCommerceChannelByUuidAndCompanyId(
			String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceChannelLocalService.
			getCommerceChannelByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.model.Group getCommerceChannelGroup(
			long commerceChannelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceChannelLocalService.getCommerceChannelGroup(
			commerceChannelId);
	}

	@Override
	public long getCommerceChannelGroupIdBySiteGroupId(long siteGroupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceChannelLocalService.
			getCommerceChannelGroupIdBySiteGroupId(siteGroupId);
	}

	/**
	 * Returns a range of all the commerce channels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CommerceChannelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce channels
	 * @param end the upper bound of the range of commerce channels (not inclusive)
	 * @return the range of commerce channels
	 */
	@Override
	public java.util.List<CommerceChannel> getCommerceChannels(
		int start, int end) {

		return _commerceChannelLocalService.getCommerceChannels(start, end);
	}

	@Override
	public java.util.List<CommerceChannel> getCommerceChannels(long companyId) {
		return _commerceChannelLocalService.getCommerceChannels(companyId);
	}

	@Override
	public java.util.List<CommerceChannel> getCommerceChannels(
			long companyId, String keywords, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceChannelLocalService.getCommerceChannels(
			companyId, keywords, start, end);
	}

	@Override
	public java.util.List<CommerceChannel> getCommerceChannelsByAccountEntryId(
		long accountEntryId) {

		return _commerceChannelLocalService.getCommerceChannelsByAccountEntryId(
			accountEntryId);
	}

	/**
	 * Returns the number of commerce channels.
	 *
	 * @return the number of commerce channels
	 */
	@Override
	public int getCommerceChannelsCount() {
		return _commerceChannelLocalService.getCommerceChannelsCount();
	}

	@Override
	public int getCommerceChannelsCount(long companyId, String keywords)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceChannelLocalService.getCommerceChannelsCount(
			companyId, keywords);
	}

	@Override
	public java.util.List<CommerceChannel> getEligibleCommerceChannels(
			long accountEntryId, String name, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceChannelLocalService.getEligibleCommerceChannels(
			accountEntryId, name, start, end);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _commerceChannelLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commerceChannelLocalService.
			getIndexableActionableDynamicQuery();
	}

	@Override
	public CommerceChannel getOrAddEmptyCommerceChannel(
			String externalReferenceCode, long companyId, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceChannelLocalService.getOrAddEmptyCommerceChannel(
			externalReferenceCode, companyId, userId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceChannelLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceChannelLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public java.util.List<CommerceChannel> search(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceChannelLocalService.search(companyId);
	}

	@Override
	public java.util.List<CommerceChannel> search(
			long companyId, String keywords, int start, int end,
			com.liferay.portal.kernel.search.Sort sort)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceChannelLocalService.search(
			companyId, keywords, start, end, sort);
	}

	@Override
	public int searchCommerceChannelsCount(long companyId, String keywords)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceChannelLocalService.searchCommerceChannelsCount(
			companyId, keywords);
	}

	/**
	 * Updates the commerce channel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceChannelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceChannel the commerce channel
	 * @return the commerce channel that was updated
	 */
	@Override
	public CommerceChannel updateCommerceChannel(
		CommerceChannel commerceChannel) {

		return _commerceChannelLocalService.updateCommerceChannel(
			commerceChannel);
	}

	@Override
	public CommerceChannel updateCommerceChannel(
			long commerceChannelId, long accountEntryId, long siteGroupId,
			String name, String type,
			com.liferay.portal.kernel.util.UnicodeProperties
				typeSettingsUnicodeProperties,
			String commerceCurrencyCode, String priceDisplayType,
			boolean discountsTargetNetPrice)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceChannelLocalService.updateCommerceChannel(
			commerceChannelId, accountEntryId, siteGroupId, name, type,
			typeSettingsUnicodeProperties, commerceCurrencyCode,
			priceDisplayType, discountsTargetNetPrice);
	}

	@Override
	public CommerceChannel updateCommerceChannelExternalReferenceCode(
			String externalReferenceCode, long commerceChannelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceChannelLocalService.
			updateCommerceChannelExternalReferenceCode(
				externalReferenceCode, commerceChannelId);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commerceChannelLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<CommerceChannel> getCTPersistence() {
		return _commerceChannelLocalService.getCTPersistence();
	}

	@Override
	public Class<CommerceChannel> getModelClass() {
		return _commerceChannelLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CommerceChannel>, R, E>
				updateUnsafeFunction)
		throws E {

		return _commerceChannelLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public CommerceChannelLocalService getWrappedService() {
		return _commerceChannelLocalService;
	}

	@Override
	public void setWrappedService(
		CommerceChannelLocalService commerceChannelLocalService) {

		_commerceChannelLocalService = commerceChannelLocalService;
	}

	private CommerceChannelLocalService _commerceChannelLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-992223687