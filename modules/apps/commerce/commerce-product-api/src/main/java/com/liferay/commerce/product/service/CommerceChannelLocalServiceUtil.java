/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CommerceChannel. This utility wraps
 * <code>com.liferay.commerce.product.service.impl.CommerceChannelLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see CommerceChannelLocalService
 * @generated
 */
public class CommerceChannelLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.product.service.impl.CommerceChannelLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

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
	public static CommerceChannel addCommerceChannel(
		CommerceChannel commerceChannel) {

		return getService().addCommerceChannel(commerceChannel);
	}

	public static CommerceChannel addCommerceChannel(
			String externalReferenceCode, long accountEntryId, long siteGroupId,
			String name, String type,
			com.liferay.portal.kernel.util.UnicodeProperties
				typeSettingsUnicodeProperties,
			String commerceCurrencyCode,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCommerceChannel(
			externalReferenceCode, accountEntryId, siteGroupId, name, type,
			typeSettingsUnicodeProperties, commerceCurrencyCode,
			serviceContext);
	}

	public static CommerceChannel addOrUpdateCommerceChannel(
			long userId, String externalReferenceCode, long accountEntryId,
			long siteGroupId, String name, String type,
			com.liferay.portal.kernel.util.UnicodeProperties
				typeSettingsUnicodeProperties,
			String commerceCurrencyCode,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateCommerceChannel(
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
	public static CommerceChannel createCommerceChannel(
		long commerceChannelId) {

		return getService().createCommerceChannel(commerceChannelId);
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
	public static CommerceChannel deleteCommerceChannel(
			CommerceChannel commerceChannel)
		throws PortalException {

		return getService().deleteCommerceChannel(commerceChannel);
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
	public static CommerceChannel deleteCommerceChannel(long commerceChannelId)
		throws PortalException {

		return getService().deleteCommerceChannel(commerceChannelId);
	}

	public static void deleteCommerceChannels(long companyId)
		throws PortalException {

		getService().deleteCommerceChannels(companyId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CommerceChannelModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CommerceChannelModelImpl</code>.
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

	public static CommerceChannel fetchCommerceChannel(long commerceChannelId) {
		return getService().fetchCommerceChannel(commerceChannelId);
	}

	public static CommerceChannel fetchCommerceChannelByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return getService().fetchCommerceChannelByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static CommerceChannel fetchCommerceChannelByGroupClassPK(
		long groupId) {

		return getService().fetchCommerceChannelByGroupClassPK(groupId);
	}

	public static CommerceChannel fetchCommerceChannelBySiteGroupId(
		long siteGroupId) {

		return getService().fetchCommerceChannelBySiteGroupId(siteGroupId);
	}

	/**
	 * Returns the commerce channel with the matching UUID and company.
	 *
	 * @param uuid the commerce channel's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce channel, or <code>null</code> if a matching commerce channel could not be found
	 */
	public static CommerceChannel fetchCommerceChannelByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().fetchCommerceChannelByUuidAndCompanyId(
			uuid, companyId);
	}

	public static com.liferay.portal.kernel.model.Group
			fetchCommerceChannelGroup(long commerceChannelId)
		throws PortalException {

		return getService().fetchCommerceChannelGroup(commerceChannelId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce channel with the primary key.
	 *
	 * @param commerceChannelId the primary key of the commerce channel
	 * @return the commerce channel
	 * @throws PortalException if a commerce channel with the primary key could not be found
	 */
	public static CommerceChannel getCommerceChannel(long commerceChannelId)
		throws PortalException {

		return getService().getCommerceChannel(commerceChannelId);
	}

	public static CommerceChannel getCommerceChannelByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getCommerceChannelByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	public static CommerceChannel getCommerceChannelByGroupId(long groupId)
		throws PortalException {

		return getService().getCommerceChannelByGroupId(groupId);
	}

	public static CommerceChannel getCommerceChannelByOrderGroupId(
			long orderGroupId)
		throws PortalException {

		return getService().getCommerceChannelByOrderGroupId(orderGroupId);
	}

	/**
	 * Returns the commerce channel with the matching UUID and company.
	 *
	 * @param uuid the commerce channel's UUID
	 * @param companyId the primary key of the company
	 * @return the matching commerce channel
	 * @throws PortalException if a matching commerce channel could not be found
	 */
	public static CommerceChannel getCommerceChannelByUuidAndCompanyId(
			String uuid, long companyId)
		throws PortalException {

		return getService().getCommerceChannelByUuidAndCompanyId(
			uuid, companyId);
	}

	public static com.liferay.portal.kernel.model.Group getCommerceChannelGroup(
			long commerceChannelId)
		throws PortalException {

		return getService().getCommerceChannelGroup(commerceChannelId);
	}

	public static long getCommerceChannelGroupIdBySiteGroupId(long siteGroupId)
		throws PortalException {

		return getService().getCommerceChannelGroupIdBySiteGroupId(siteGroupId);
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
	public static List<CommerceChannel> getCommerceChannels(
		int start, int end) {

		return getService().getCommerceChannels(start, end);
	}

	public static List<CommerceChannel> getCommerceChannels(long companyId) {
		return getService().getCommerceChannels(companyId);
	}

	public static List<CommerceChannel> getCommerceChannels(
			long companyId, String keywords, int start, int end)
		throws PortalException {

		return getService().getCommerceChannels(
			companyId, keywords, start, end);
	}

	public static List<CommerceChannel> getCommerceChannelsByAccountEntryId(
		long accountEntryId) {

		return getService().getCommerceChannelsByAccountEntryId(accountEntryId);
	}

	/**
	 * Returns the number of commerce channels.
	 *
	 * @return the number of commerce channels
	 */
	public static int getCommerceChannelsCount() {
		return getService().getCommerceChannelsCount();
	}

	public static int getCommerceChannelsCount(long companyId, String keywords)
		throws PortalException {

		return getService().getCommerceChannelsCount(companyId, keywords);
	}

	public static List<CommerceChannel> getEligibleCommerceChannels(
			long accountEntryId, String name, int start, int end)
		throws PortalException {

		return getService().getEligibleCommerceChannels(
			accountEntryId, name, start, end);
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

	public static CommerceChannel getOrAddEmptyCommerceChannel(
			String externalReferenceCode, long companyId, long userId)
		throws PortalException {

		return getService().getOrAddEmptyCommerceChannel(
			externalReferenceCode, companyId, userId);
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

	public static List<CommerceChannel> search(long companyId)
		throws PortalException {

		return getService().search(companyId);
	}

	public static List<CommerceChannel> search(
			long companyId, String keywords, int start, int end,
			com.liferay.portal.kernel.search.Sort sort)
		throws PortalException {

		return getService().search(companyId, keywords, start, end, sort);
	}

	public static int searchCommerceChannelsCount(
			long companyId, String keywords)
		throws PortalException {

		return getService().searchCommerceChannelsCount(companyId, keywords);
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
	public static CommerceChannel updateCommerceChannel(
		CommerceChannel commerceChannel) {

		return getService().updateCommerceChannel(commerceChannel);
	}

	public static CommerceChannel updateCommerceChannel(
			long commerceChannelId, long accountEntryId, long siteGroupId,
			String name, String type,
			com.liferay.portal.kernel.util.UnicodeProperties
				typeSettingsUnicodeProperties,
			String commerceCurrencyCode, String priceDisplayType,
			boolean discountsTargetNetPrice)
		throws PortalException {

		return getService().updateCommerceChannel(
			commerceChannelId, accountEntryId, siteGroupId, name, type,
			typeSettingsUnicodeProperties, commerceCurrencyCode,
			priceDisplayType, discountsTargetNetPrice);
	}

	public static CommerceChannel updateCommerceChannelExternalReferenceCode(
			String externalReferenceCode, long commerceChannelId)
		throws PortalException {

		return getService().updateCommerceChannelExternalReferenceCode(
			externalReferenceCode, commerceChannelId);
	}

	public static CommerceChannelLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CommerceChannelLocalService>
		_serviceSnapshot = new Snapshot<>(
			CommerceChannelLocalServiceUtil.class,
			CommerceChannelLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1363936458