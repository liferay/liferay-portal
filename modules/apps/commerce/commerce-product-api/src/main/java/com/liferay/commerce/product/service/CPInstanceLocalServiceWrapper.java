/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPInstance;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CPInstanceLocalService}.
 *
 * @author Marco Leo
 * @see CPInstanceLocalService
 * @generated
 */
public class CPInstanceLocalServiceWrapper
	implements CPInstanceLocalService, ServiceWrapper<CPInstanceLocalService> {

	public CPInstanceLocalServiceWrapper() {
		this(null);
	}

	public CPInstanceLocalServiceWrapper(
		CPInstanceLocalService cpInstanceLocalService) {

		_cpInstanceLocalService = cpInstanceLocalService;
	}

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
	@Override
	public CPInstance addCPInstance(CPInstance cpInstance) {
		return _cpInstanceLocalService.addCPInstance(cpInstance);
	}

	@Override
	public CPInstance addCPInstance(
			String externalReferenceCode, long cpDefinitionId, long groupId,
			String sku, String gtin, String manufacturerPartNumber,
			boolean purchasable,
			java.util.Map<Long, java.util.List<Long>>
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
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceLocalService.addCPInstance(
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

	@Override
	public CPInstance addOrUpdateCPInstance(
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
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceLocalService.addOrUpdateCPInstance(
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

	@Override
	public java.util.List<CPInstance> buildCPInstances(
			long cpDefinitionId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceLocalService.buildCPInstances(
			cpDefinitionId, serviceContext);
	}

	@Override
	public void checkCPInstances(long cpDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpInstanceLocalService.checkCPInstances(cpDefinitionId);
	}

	@Override
	public void checkCPInstancesByDisplayDate(long cpDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpInstanceLocalService.checkCPInstancesByDisplayDate(cpDefinitionId);
	}

	/**
	 * Creates a new cp instance with the primary key. Does not add the cp instance to the database.
	 *
	 * @param CPInstanceId the primary key for the new cp instance
	 * @return the new cp instance
	 */
	@Override
	public CPInstance createCPInstance(long CPInstanceId) {
		return _cpInstanceLocalService.createCPInstance(CPInstanceId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceLocalService.createPersistedModel(primaryKeyObj);
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
	@Override
	public CPInstance deleteCPInstance(CPInstance cpInstance)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceLocalService.deleteCPInstance(cpInstance);
	}

	@Override
	public CPInstance deleteCPInstance(CPInstance cpInstance, boolean makeCopy)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceLocalService.deleteCPInstance(cpInstance, makeCopy);
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
	@Override
	public CPInstance deleteCPInstance(long CPInstanceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceLocalService.deleteCPInstance(CPInstanceId);
	}

	@Override
	public void deleteCPInstances(long cpDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpInstanceLocalService.deleteCPInstances(cpDefinitionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _cpInstanceLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _cpInstanceLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _cpInstanceLocalService.dynamicQuery();
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

		return _cpInstanceLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _cpInstanceLocalService.dynamicQuery(dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _cpInstanceLocalService.dynamicQuery(
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

		return _cpInstanceLocalService.dynamicQueryCount(dynamicQuery);
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

		return _cpInstanceLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public CPInstance fetchCPInstance(long CPInstanceId) {
		return _cpInstanceLocalService.fetchCPInstance(CPInstanceId);
	}

	@Override
	public CPInstance fetchCPInstance(long cProductId, String cpInstanceUuid) {
		return _cpInstanceLocalService.fetchCPInstance(
			cProductId, cpInstanceUuid);
	}

	@Override
	public CPInstance fetchCPInstanceByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return _cpInstanceLocalService.fetchCPInstanceByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp instance matching the UUID and group.
	 *
	 * @param uuid the cp instance's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp instance, or <code>null</code> if a matching cp instance could not be found
	 */
	@Override
	public CPInstance fetchCPInstanceByUuidAndGroupId(
		String uuid, long groupId) {

		return _cpInstanceLocalService.fetchCPInstanceByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public CPInstance fetchCProductInstance(
		long cProductId, String cpInstanceUuid) {

		return _cpInstanceLocalService.fetchCProductInstance(
			cProductId, cpInstanceUuid);
	}

	@Override
	public CPInstance fetchDefaultCPInstance(long cpDefinitionId) {
		return _cpInstanceLocalService.fetchDefaultCPInstance(cpDefinitionId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _cpInstanceLocalService.getActionableDynamicQuery();
	}

	@Override
	public java.util.List<CPInstance> getCPDefinitionApprovedCPInstances(
		long cpDefinitionId) {

		return _cpInstanceLocalService.getCPDefinitionApprovedCPInstances(
			cpDefinitionId);
	}

	@Override
	public java.util.List<CPInstance> getCPDefinitionInstances(
		long cpDefinitionId, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPInstance>
			orderByComparator) {

		return _cpInstanceLocalService.getCPDefinitionInstances(
			cpDefinitionId, status, start, end, orderByComparator);
	}

	@Override
	public int getCPDefinitionInstancesCount(long cpDefinitionId, int status) {
		return _cpInstanceLocalService.getCPDefinitionInstancesCount(
			cpDefinitionId, status);
	}

	/**
	 * Returns the cp instance with the primary key.
	 *
	 * @param CPInstanceId the primary key of the cp instance
	 * @return the cp instance
	 * @throws PortalException if a cp instance with the primary key could not be found
	 */
	@Override
	public CPInstance getCPInstance(long CPInstanceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceLocalService.getCPInstance(CPInstanceId);
	}

	@Override
	public CPInstance getCPInstance(long cpDefinitionId, String sku)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceLocalService.getCPInstance(cpDefinitionId, sku);
	}

	@Override
	public CPInstance getCPInstanceByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceLocalService.getCPInstanceByExternalReferenceCode(
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
	@Override
	public CPInstance getCPInstanceByUuidAndGroupId(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceLocalService.getCPInstanceByUuidAndGroupId(
			uuid, groupId);
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
	@Override
	public java.util.List<CPInstance> getCPInstances(int start, int end) {
		return _cpInstanceLocalService.getCPInstances(start, end);
	}

	@Override
	public java.util.List<CPInstance> getCPInstances(
			long groupId, int status, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<CPInstance>
				orderByComparator)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceLocalService.getCPInstances(
			groupId, status, start, end, orderByComparator);
	}

	@Override
	public java.util.List<CPInstance> getCPInstances(
		long companyId, String sku) {

		return _cpInstanceLocalService.getCPInstances(companyId, sku);
	}

	@Override
	public java.util.List<CPInstance> getCPInstances(
		String replacementCPInstanceUuid, long replacementCProductId,
		int status) {

		return _cpInstanceLocalService.getCPInstances(
			replacementCPInstanceUuid, replacementCProductId, status);
	}

	/**
	 * Returns all the cp instances matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp instances
	 * @param companyId the primary key of the company
	 * @return the matching cp instances, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<CPInstance> getCPInstancesByUuidAndCompanyId(
		String uuid, long companyId) {

		return _cpInstanceLocalService.getCPInstancesByUuidAndCompanyId(
			uuid, companyId);
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
	@Override
	public java.util.List<CPInstance> getCPInstancesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<CPInstance>
			orderByComparator) {

		return _cpInstanceLocalService.getCPInstancesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of cp instances.
	 *
	 * @return the number of cp instances
	 */
	@Override
	public int getCPInstancesCount() {
		return _cpInstanceLocalService.getCPInstancesCount();
	}

	@Override
	public int getCPInstancesCount(long groupId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceLocalService.getCPInstancesCount(groupId, status);
	}

	@Override
	public int getCPInstancesCount(String cpInstanceUuid) {
		return _cpInstanceLocalService.getCPInstancesCount(cpInstanceUuid);
	}

	@Override
	public CPInstance getCProductInstance(
			long cProductId, String cpInstanceUuid)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceLocalService.getCProductInstance(
			cProductId, cpInstanceUuid);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _cpInstanceLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _cpInstanceLocalService.getIndexableActionableDynamicQuery();
	}

	@Override
	public CPInstance getOrAddEmptyCPInstance(
			String externalReferenceCode, long cpDefinitionId, long groupId,
			long companyId, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceLocalService.getOrAddEmptyCPInstance(
			externalReferenceCode, cpDefinitionId, groupId, companyId, userId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _cpInstanceLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public String[] getSKUs(long cpDefinitionId) {
		return _cpInstanceLocalService.getSKUs(cpDefinitionId);
	}

	@Override
	public void inactivateCPDefinitionOptionRelCPInstances(
			long userId, long cpDefinitionId, long cpDefinitionOptionRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpInstanceLocalService.inactivateCPDefinitionOptionRelCPInstances(
			userId, cpDefinitionId, cpDefinitionOptionRelId);
	}

	@Override
	public void inactivateCPDefinitionOptionValueRelCPInstances(
			long userId, long cpDefinitionId, long cpDefinitionOptionValueRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpInstanceLocalService.inactivateCPDefinitionOptionValueRelCPInstances(
			userId, cpDefinitionId, cpDefinitionOptionValueRelId);
	}

	@Override
	public void inactivateIncompatibleCPInstances(
			long userId, long cpDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpInstanceLocalService.inactivateIncompatibleCPInstances(
			userId, cpDefinitionId);
	}

	@Override
	public com.liferay.portal.kernel.search.Hits search(
		com.liferay.portal.kernel.search.SearchContext searchContext) {

		return _cpInstanceLocalService.search(searchContext);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult<CPInstance>
			searchCPDefinitionInstances(
				long companyId, long cpDefinitionId, String keywords,
				int status, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceLocalService.searchCPDefinitionInstances(
			companyId, cpDefinitionId, keywords, status, start, end, sort);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult<CPInstance>
			searchCPDefinitionInstances(
				long companyId, long cpDefinitionId, String keywords,
				int status, com.liferay.portal.kernel.search.Sort sort)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceLocalService.searchCPDefinitionInstances(
			companyId, cpDefinitionId, keywords, status, sort);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult<CPInstance>
			searchCPInstances(
				long companyId, long[] groupIds, String keywords, int status,
				int start, int end, com.liferay.portal.kernel.search.Sort sort)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceLocalService.searchCPInstances(
			companyId, groupIds, keywords, status, start, end, sort);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult<CPInstance>
			searchCPInstances(
				long companyId, String keywords, int status, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceLocalService.searchCPInstances(
			companyId, keywords, status, start, end, sort);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult<CPInstance>
			searchCPInstances(
				long companyId, String cpInstanceUuid, long cProductId,
				String keywords, int status, int start, int end,
				com.liferay.portal.kernel.search.Sort sort)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceLocalService.searchCPInstances(
			companyId, cpInstanceUuid, cProductId, keywords, status, start, end,
			sort);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult<CPInstance>
			searchCPInstances(
				com.liferay.portal.kernel.search.SearchContext searchContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceLocalService.searchCPInstances(searchContext);
	}

	@Override
	public int searchCPInstancesCount(
			long companyId, String cpInstanceUuid, long cProductId,
			String keywords, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceLocalService.searchCPInstancesCount(
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
	@Override
	public CPInstance updateCPInstance(CPInstance cpInstance) {
		return _cpInstanceLocalService.updateCPInstance(cpInstance);
	}

	@Override
	public CPInstance updateCPInstance(
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
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceLocalService.updateCPInstance(
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

	@Override
	public CPInstance updateExternalReferenceCode(
			long cpInstanceId, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceLocalService.updateExternalReferenceCode(
			cpInstanceId, externalReferenceCode);
	}

	@Override
	public CPInstance updatePricingInfo(
			long cpInstanceId, java.math.BigDecimal price,
			java.math.BigDecimal promoPrice, java.math.BigDecimal cost,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceLocalService.updatePricingInfo(
			cpInstanceId, price, promoPrice, cost, serviceContext);
	}

	@Override
	public CPInstance updateShippingInfo(
			long cpInstanceId, double width, double height, double depth,
			double weight,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceLocalService.updateShippingInfo(
			cpInstanceId, width, height, depth, weight, serviceContext);
	}

	@Override
	public CPInstance updateStatus(long userId, long cpInstanceId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceLocalService.updateStatus(
			userId, cpInstanceId, status);
	}

	@Override
	public CPInstance updateSubscriptionInfo(
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
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpInstanceLocalService.updateSubscriptionInfo(
			cpInstanceId, overrideSubscriptionInfo, subscriptionEnabled,
			subscriptionLength, subscriptionType,
			subscriptionTypeSettingsUnicodeProperties, maxSubscriptionCycles,
			deliverySubscriptionEnabled, deliverySubscriptionLength,
			deliverySubscriptionType,
			deliverySubscriptionTypeSettingsUnicodeProperties,
			deliveryMaxSubscriptionCycles);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _cpInstanceLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<CPInstance> getCTPersistence() {
		return _cpInstanceLocalService.getCTPersistence();
	}

	@Override
	public Class<CPInstance> getModelClass() {
		return _cpInstanceLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CPInstance>, R, E>
				updateUnsafeFunction)
		throws E {

		return _cpInstanceLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public CPInstanceLocalService getWrappedService() {
		return _cpInstanceLocalService;
	}

	@Override
	public void setWrappedService(
		CPInstanceLocalService cpInstanceLocalService) {

		_cpInstanceLocalService = cpInstanceLocalService;
	}

	private CPInstanceLocalService _cpInstanceLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:210732494