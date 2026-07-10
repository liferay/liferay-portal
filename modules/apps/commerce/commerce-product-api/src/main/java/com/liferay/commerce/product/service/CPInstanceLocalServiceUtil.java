/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPInstance;
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
 * Provides the local service utility for CPInstance. This utility wraps
 * <code>com.liferay.commerce.product.service.impl.CPInstanceLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see CPInstanceLocalService
 * @generated
 */
public class CPInstanceLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.product.service.impl.CPInstanceLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the cp instance to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpInstance the cp instance
	 * @return the cp instance that was added
	 */
	public static CPInstance addCPInstance(CPInstance cpInstance) {
		return getService().addCPInstance(cpInstance);
	}

	public static CPInstance addCPInstance(
			String externalReferenceCode, long cpDefinitionId, long groupId,
			String sku, String gtin, String manufacturerPartNumber,
			boolean purchasable,
			Map<Long, List<Long>>
				cpDefinitionOptionRelIdCPDefinitionOptionValueRelIds,
			double width, double height, double depth, double weight,
			java.math.BigDecimal price, java.math.BigDecimal promoPrice,
			java.math.BigDecimal cost, boolean published, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, boolean overrideSubscriptionInfo,
			boolean subscriptionEnabled, int subscriptionLength,
			String subscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				subscriptionTypeSettingsUnicodeProperties,
			long maxSubscriptionCycles, boolean deliverySubscriptionEnabled,
			int deliverySubscriptionLength, String deliverySubscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				deliverySubscriptionTypeSettingsUnicodeProperties,
			long deliveryMaxSubscriptionCycles, String unspsc,
			boolean discontinued, String replacementCPInstanceUuid,
			long replacementCProductId, int discontinuedDateMonth,
			int discontinuedDateDay, int discontinuedDateYear,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addCPInstance(
			externalReferenceCode, cpDefinitionId, groupId, sku, gtin,
			manufacturerPartNumber, purchasable,
			cpDefinitionOptionRelIdCPDefinitionOptionValueRelIds, width, height,
			depth, weight, price, promoPrice, cost, published, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			overrideSubscriptionInfo, subscriptionEnabled, subscriptionLength,
			subscriptionType, subscriptionTypeSettingsUnicodeProperties,
			maxSubscriptionCycles, deliverySubscriptionEnabled,
			deliverySubscriptionLength, deliverySubscriptionType,
			deliverySubscriptionTypeSettingsUnicodeProperties,
			deliveryMaxSubscriptionCycles, unspsc, discontinued,
			replacementCPInstanceUuid, replacementCProductId,
			discontinuedDateMonth, discontinuedDateDay, discontinuedDateYear,
			serviceContext);
	}

	public static CPInstance addOrUpdateCPInstance(
			String externalReferenceCode, long cpDefinitionId, long groupId,
			String sku, String gtin, String manufacturerPartNumber,
			boolean purchasable, String json, double width, double height,
			double depth, double weight, java.math.BigDecimal price,
			java.math.BigDecimal promoPrice, java.math.BigDecimal cost,
			boolean published, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire,
			boolean overrideSubscriptionInfo, boolean subscriptionEnabled,
			int subscriptionLength, String subscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				subscriptionTypeSettingsUnicodeProperties,
			long maxSubscriptionCycles, boolean deliverySubscriptionEnabled,
			int deliverySubscriptionLength, String deliverySubscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				deliverySubscriptionTypeSettingsUnicodeProperties,
			long deliveryMaxSubscriptionCycles, String unspsc,
			boolean discontinued, String replacementCPInstanceUuid,
			long replacementCProductId, int discontinuedDateMonth,
			int discontinuedDateDay, int discontinuedDateYear,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateCPInstance(
			externalReferenceCode, cpDefinitionId, groupId, sku, gtin,
			manufacturerPartNumber, purchasable, json, width, height, depth,
			weight, price, promoPrice, cost, published, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			overrideSubscriptionInfo, subscriptionEnabled, subscriptionLength,
			subscriptionType, subscriptionTypeSettingsUnicodeProperties,
			maxSubscriptionCycles, deliverySubscriptionEnabled,
			deliverySubscriptionLength, deliverySubscriptionType,
			deliverySubscriptionTypeSettingsUnicodeProperties,
			deliveryMaxSubscriptionCycles, unspsc, discontinued,
			replacementCPInstanceUuid, replacementCProductId,
			discontinuedDateMonth, discontinuedDateDay, discontinuedDateYear,
			serviceContext);
	}

	public static List<CPInstance> buildCPInstances(
			long cpDefinitionId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().buildCPInstances(cpDefinitionId, serviceContext);
	}

	public static void checkCPInstances(long cpDefinitionId)
		throws PortalException {

		getService().checkCPInstances(cpDefinitionId);
	}

	public static void checkCPInstancesByDisplayDate(long cpDefinitionId)
		throws PortalException {

		getService().checkCPInstancesByDisplayDate(cpDefinitionId);
	}

	/**
	 * Creates a new cp instance with the primary key. Does not add the cp instance to the database.
	 *
	 * @param CPInstanceId the primary key for the new cp instance
	 * @return the new cp instance
	 */
	public static CPInstance createCPInstance(long CPInstanceId) {
		return getService().createCPInstance(CPInstanceId);
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
	 * Deletes the cp instance from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpInstance the cp instance
	 * @return the cp instance that was removed
	 * @throws PortalException
	 */
	public static CPInstance deleteCPInstance(CPInstance cpInstance)
		throws PortalException {

		return getService().deleteCPInstance(cpInstance);
	}

	public static CPInstance deleteCPInstance(
			CPInstance cpInstance, boolean makeCopy)
		throws PortalException {

		return getService().deleteCPInstance(cpInstance, makeCopy);
	}

	/**
	 * Deletes the cp instance with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param CPInstanceId the primary key of the cp instance
	 * @return the cp instance that was removed
	 * @throws PortalException if a cp instance with the primary key could not be found
	 */
	public static CPInstance deleteCPInstance(long CPInstanceId)
		throws PortalException {

		return getService().deleteCPInstance(CPInstanceId);
	}

	public static void deleteCPInstances(long cpDefinitionId)
		throws PortalException {

		getService().deleteCPInstances(cpDefinitionId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPInstanceModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPInstanceModelImpl</code>.
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

	public static CPInstance fetchCPInstance(long CPInstanceId) {
		return getService().fetchCPInstance(CPInstanceId);
	}

	public static CPInstance fetchCPInstance(
		long cProductId, String cpInstanceUuid) {

		return getService().fetchCPInstance(cProductId, cpInstanceUuid);
	}

	public static CPInstance fetchCPInstanceByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return getService().fetchCPInstanceByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp instance matching the UUID and group.
	 *
	 * @param uuid the cp instance's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	public static CPInstance fetchCPInstanceByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchCPInstanceByUuidAndGroupId(uuid, groupId);
	}

	public static CPInstance fetchCProductInstance(
		long cProductId, String cpInstanceUuid) {

		return getService().fetchCProductInstance(cProductId, cpInstanceUuid);
	}

	public static CPInstance fetchDefaultCPInstance(long cpDefinitionId) {
		return getService().fetchDefaultCPInstance(cpDefinitionId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<CPInstance> getCPDefinitionApprovedCPInstances(
		long cpDefinitionId) {

		return getService().getCPDefinitionApprovedCPInstances(cpDefinitionId);
	}

	public static List<CPInstance> getCPDefinitionInstances(
		long cpDefinitionId, int status, int start, int end,
		OrderByComparator<CPInstance> orderByComparator) {

		return getService().getCPDefinitionInstances(
			cpDefinitionId, status, start, end, orderByComparator);
	}

	public static int getCPDefinitionInstancesCount(
		long cpDefinitionId, int status) {

		return getService().getCPDefinitionInstancesCount(
			cpDefinitionId, status);
	}

	/**
	 * Returns the cp instance with the primary key.
	 *
	 * @param CPInstanceId the primary key of the cp instance
	 * @return the cp instance
	 * @throws PortalException if a cp instance with the primary key could not be found
	 */
	public static CPInstance getCPInstance(long CPInstanceId)
		throws PortalException {

		return getService().getCPInstance(CPInstanceId);
	}

	public static CPInstance getCPInstance(long cpDefinitionId, String sku)
		throws PortalException {

		return getService().getCPInstance(cpDefinitionId, sku);
	}

	public static CPInstance getCPInstanceByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getCPInstanceByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp instance matching the UUID and group.
	 *
	 * @param uuid the cp instance's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp instance
	 * @throws PortalException if a matching cp instance could not be found
	 */
	public static CPInstance getCPInstanceByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getCPInstanceByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the cp instances.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @return the range of cp instances
	 */
	public static List<CPInstance> getCPInstances(int start, int end) {
		return getService().getCPInstances(start, end);
	}

	public static List<CPInstance> getCPInstances(
			long groupId, int status, int start, int end,
			OrderByComparator<CPInstance> orderByComparator)
		throws PortalException {

		return getService().getCPInstances(
			groupId, status, start, end, orderByComparator);
	}

	public static List<CPInstance> getCPInstances(long companyId, String sku) {
		return getService().getCPInstances(companyId, sku);
	}

	public static List<CPInstance> getCPInstances(
		String replacementCPInstanceUuid, long replacementCProductId,
		int status) {

		return getService().getCPInstances(
			replacementCPInstanceUuid, replacementCProductId, status);
	}

	/**
	 * Returns all the cp instances matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp instances
	 * @param companyId the primary key of the company
	 * @return the matching cp instances, or an empty list if no matches were found
	 */
	public static List<CPInstance> getCPInstancesByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getCPInstancesByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of cp instances matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp instances
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of cp instances
	 * @param end the upper bound of the range of cp instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching cp instances, or an empty list if no matches were found
	 */
	public static List<CPInstance> getCPInstancesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPInstance> orderByComparator) {

		return getService().getCPInstancesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of cp instances.
	 *
	 * @return the number of cp instances
	 */
	public static int getCPInstancesCount() {
		return getService().getCPInstancesCount();
	}

	public static int getCPInstancesCount(long groupId, int status)
		throws PortalException {

		return getService().getCPInstancesCount(groupId, status);
	}

	public static int getCPInstancesCount(String cpInstanceUuid) {
		return getService().getCPInstancesCount(cpInstanceUuid);
	}

	public static CPInstance getCProductInstance(
			long cProductId, String cpInstanceUuid)
		throws PortalException {

		return getService().getCProductInstance(cProductId, cpInstanceUuid);
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

	public static CPInstance getOrAddEmptyCPInstance(
			String externalReferenceCode, long cpDefinitionId, long groupId,
			long companyId, long userId)
		throws PortalException {

		return getService().getOrAddEmptyCPInstance(
			externalReferenceCode, cpDefinitionId, groupId, companyId, userId);
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

	public static String[] getSKUs(long cpDefinitionId) {
		return getService().getSKUs(cpDefinitionId);
	}

	public static void inactivateCPDefinitionOptionRelCPInstances(
			long userId, long cpDefinitionId, long cpDefinitionOptionRelId)
		throws PortalException {

		getService().inactivateCPDefinitionOptionRelCPInstances(
			userId, cpDefinitionId, cpDefinitionOptionRelId);
	}

	public static void inactivateCPDefinitionOptionValueRelCPInstances(
			long userId, long cpDefinitionId, long cpDefinitionOptionValueRelId)
		throws PortalException {

		getService().inactivateCPDefinitionOptionValueRelCPInstances(
			userId, cpDefinitionId, cpDefinitionOptionValueRelId);
	}

	public static void inactivateIncompatibleCPInstances(
			long userId, long cpDefinitionId)
		throws PortalException {

		getService().inactivateIncompatibleCPInstances(userId, cpDefinitionId);
	}

	public static com.liferay.portal.kernel.search.Hits search(
		com.liferay.portal.kernel.search.SearchContext searchContext) {

		return getService().search(searchContext);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CPInstance> searchCPDefinitionInstances(
				long companyId, long cpDefinitionId, String keywords,
				int status, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchCPDefinitionInstances(
			companyId, cpDefinitionId, keywords, status, start, end, sort);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CPInstance> searchCPDefinitionInstances(
				long companyId, long cpDefinitionId, String keywords,
				int status, com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchCPDefinitionInstances(
			companyId, cpDefinitionId, keywords, status, sort);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CPInstance> searchCPInstances(
				long companyId, long[] groupIds, String keywords, int status,
				int start, int end, com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchCPInstances(
			companyId, groupIds, keywords, status, start, end, sort);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CPInstance> searchCPInstances(
				long companyId, String keywords, int status, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchCPInstances(
			companyId, keywords, status, start, end, sort);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CPInstance> searchCPInstances(
				long companyId, String cpInstanceUuid, long cProductId,
				String keywords, int status, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
			throws PortalException {

		return getService().searchCPInstances(
			companyId, cpInstanceUuid, cProductId, keywords, status, start, end,
			sort);
	}

	public static com.liferay.portal.kernel.search.BaseModelSearchResult
		<CPInstance> searchCPInstances(
				com.liferay.portal.kernel.search.SearchContext searchContext)
			throws PortalException {

		return getService().searchCPInstances(searchContext);
	}

	public static int searchCPInstancesCount(
			long companyId, String cpInstanceUuid, long cProductId,
			String keywords, int status)
		throws PortalException {

		return getService().searchCPInstancesCount(
			companyId, cpInstanceUuid, cProductId, keywords, status);
	}

	/**
	 * Updates the cp instance in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpInstance the cp instance
	 * @return the cp instance that was updated
	 */
	public static CPInstance updateCPInstance(CPInstance cpInstance) {
		return getService().updateCPInstance(cpInstance);
	}

	public static CPInstance updateCPInstance(
			String externalReferenceCode, long cpInstanceId, String sku,
			String gtin, String manufacturerPartNumber, boolean purchasable,
			double width, double height, double depth, double weight,
			java.math.BigDecimal price, java.math.BigDecimal promoPrice,
			java.math.BigDecimal cost, boolean published, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire, boolean overrideSubscriptionInfo,
			boolean subscriptionEnabled, int subscriptionLength,
			String subscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				subscriptionTypeSettingsUnicodeProperties,
			long maxSubscriptionCycles, boolean deliverySubscriptionEnabled,
			int deliverySubscriptionLength, String deliverySubscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				deliverySubscriptionTypeSettingsUnicodeProperties,
			long deliveryMaxSubscriptionCycles, String unspsc,
			boolean discontinued, String replacementCPInstanceUuid,
			long replacementCProductId, int discontinuedDateMonth,
			int discontinuedDateDay, int discontinuedDateYear,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateCPInstance(
			externalReferenceCode, cpInstanceId, sku, gtin,
			manufacturerPartNumber, purchasable, width, height, depth, weight,
			price, promoPrice, cost, published, displayDateMonth,
			displayDateDay, displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire,
			overrideSubscriptionInfo, subscriptionEnabled, subscriptionLength,
			subscriptionType, subscriptionTypeSettingsUnicodeProperties,
			maxSubscriptionCycles, deliverySubscriptionEnabled,
			deliverySubscriptionLength, deliverySubscriptionType,
			deliverySubscriptionTypeSettingsUnicodeProperties,
			deliveryMaxSubscriptionCycles, unspsc, discontinued,
			replacementCPInstanceUuid, replacementCProductId,
			discontinuedDateMonth, discontinuedDateDay, discontinuedDateYear,
			serviceContext);
	}

	public static CPInstance updateExternalReferenceCode(
			long cpInstanceId, String externalReferenceCode)
		throws PortalException {

		return getService().updateExternalReferenceCode(
			cpInstanceId, externalReferenceCode);
	}

	public static CPInstance updatePricingInfo(
			long cpInstanceId, java.math.BigDecimal price,
			java.math.BigDecimal promoPrice, java.math.BigDecimal cost,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updatePricingInfo(
			cpInstanceId, price, promoPrice, cost, serviceContext);
	}

	public static CPInstance updateShippingInfo(
			long cpInstanceId, double width, double height, double depth,
			double weight,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateShippingInfo(
			cpInstanceId, width, height, depth, weight, serviceContext);
	}

	public static CPInstance updateStatus(
			long userId, long cpInstanceId, int status)
		throws PortalException {

		return getService().updateStatus(userId, cpInstanceId, status);
	}

	public static CPInstance updateSubscriptionInfo(
			long cpInstanceId, boolean overrideSubscriptionInfo,
			boolean subscriptionEnabled, int subscriptionLength,
			String subscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				subscriptionTypeSettingsUnicodeProperties,
			long maxSubscriptionCycles, boolean deliverySubscriptionEnabled,
			int deliverySubscriptionLength, String deliverySubscriptionType,
			com.liferay.portal.kernel.util.UnicodeProperties
				deliverySubscriptionTypeSettingsUnicodeProperties,
			long deliveryMaxSubscriptionCycles)
		throws PortalException {

		return getService().updateSubscriptionInfo(
			cpInstanceId, overrideSubscriptionInfo, subscriptionEnabled,
			subscriptionLength, subscriptionType,
			subscriptionTypeSettingsUnicodeProperties, maxSubscriptionCycles,
			deliverySubscriptionEnabled, deliverySubscriptionLength,
			deliverySubscriptionType,
			deliverySubscriptionTypeSettingsUnicodeProperties,
			deliveryMaxSubscriptionCycles);
	}

	public static CPInstanceLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CPInstanceLocalService> _serviceSnapshot =
		new Snapshot<>(
			CPInstanceLocalServiceUtil.class, CPInstanceLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-538107981