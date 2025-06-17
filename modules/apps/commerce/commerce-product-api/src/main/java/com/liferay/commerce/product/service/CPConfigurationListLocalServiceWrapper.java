/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CPConfigurationListLocalService}.
 *
 * @author Marco Leo
 * @see CPConfigurationListLocalService
 * @generated
 */
public class CPConfigurationListLocalServiceWrapper
	implements CPConfigurationListLocalService,
			   ServiceWrapper<CPConfigurationListLocalService> {

	public CPConfigurationListLocalServiceWrapper() {
		this(null);
	}

	public CPConfigurationListLocalServiceWrapper(
		CPConfigurationListLocalService cpConfigurationListLocalService) {

		_cpConfigurationListLocalService = cpConfigurationListLocalService;
	}

	/**
	 * Adds the cp configuration list to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPConfigurationListLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpConfigurationList the cp configuration list
	 * @return the cp configuration list that was added
	 */
	@Override
	public CPConfigurationList addCPConfigurationList(
		CPConfigurationList cpConfigurationList) {

		return _cpConfigurationListLocalService.addCPConfigurationList(
			cpConfigurationList);
	}

	@Override
	public CPConfigurationList addCPConfigurationList(
			String externalReferenceCode, long userId, long groupId,
			long parentCPConfigurationListId, boolean master, String name,
			double priority, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListLocalService.addCPConfigurationList(
			externalReferenceCode, userId, groupId, parentCPConfigurationListId,
			master, name, priority, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire);
	}

	@Override
	public CPConfigurationList addOrUpdateCPConfigurationList(
			String externalReferenceCode, long companyId, long userId,
			long groupId, long parentCPConfigurationListId, boolean master,
			String name, double priority, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListLocalService.addOrUpdateCPConfigurationList(
			externalReferenceCode, companyId, userId, groupId,
			parentCPConfigurationListId, master, name, priority,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire);
	}

	/**
	 * Creates a new cp configuration list with the primary key. Does not add the cp configuration list to the database.
	 *
	 * @param CPConfigurationListId the primary key for the new cp configuration list
	 * @return the new cp configuration list
	 */
	@Override
	public CPConfigurationList createCPConfigurationList(
		long CPConfigurationListId) {

		return _cpConfigurationListLocalService.createCPConfigurationList(
			CPConfigurationListId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the cp configuration list from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPConfigurationListLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpConfigurationList the cp configuration list
	 * @return the cp configuration list that was removed
	 * @throws PortalException
	 */
	@Override
	public CPConfigurationList deleteCPConfigurationList(
			CPConfigurationList cpConfigurationList)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListLocalService.deleteCPConfigurationList(
			cpConfigurationList);
	}

	@Override
	public CPConfigurationList deleteCPConfigurationList(
			CPConfigurationList cpConfigurationList, boolean force)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListLocalService.deleteCPConfigurationList(
			cpConfigurationList, force);
	}

	/**
	 * Deletes the cp configuration list with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPConfigurationListLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param CPConfigurationListId the primary key of the cp configuration list
	 * @return the cp configuration list that was removed
	 * @throws PortalException if a cp configuration list with the primary key could not be found
	 */
	@Override
	public CPConfigurationList deleteCPConfigurationList(
			long CPConfigurationListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListLocalService.deleteCPConfigurationList(
			CPConfigurationListId);
	}

	@Override
	public CPConfigurationList deleteCPConfigurationList(
			long cpConfigurationListId, boolean force)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListLocalService.deleteCPConfigurationList(
			cpConfigurationListId, force);
	}

	@Override
	public void deleteCPConfigurationLists(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_cpConfigurationListLocalService.deleteCPConfigurationLists(companyId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _cpConfigurationListLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _cpConfigurationListLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _cpConfigurationListLocalService.dynamicQuery();
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

		return _cpConfigurationListLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationListModelImpl</code>.
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

		return _cpConfigurationListLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationListModelImpl</code>.
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

		return _cpConfigurationListLocalService.dynamicQuery(
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

		return _cpConfigurationListLocalService.dynamicQueryCount(dynamicQuery);
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

		return _cpConfigurationListLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public CPConfigurationList fetchCPConfigurationList(
		long CPConfigurationListId) {

		return _cpConfigurationListLocalService.fetchCPConfigurationList(
			CPConfigurationListId);
	}

	@Override
	public CPConfigurationList fetchCPConfigurationListByExternalReferenceCode(
		String externalReferenceCode, long companyId) {

		return _cpConfigurationListLocalService.
			fetchCPConfigurationListByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp configuration list matching the UUID and group.
	 *
	 * @param uuid the cp configuration list's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp configuration list, or <code>null</code> if a matching cp configuration list could not be found
	 */
	@Override
	public CPConfigurationList fetchCPConfigurationListByUuidAndGroupId(
		String uuid, long groupId) {

		return _cpConfigurationListLocalService.
			fetchCPConfigurationListByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _cpConfigurationListLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the cp configuration list with the primary key.
	 *
	 * @param CPConfigurationListId the primary key of the cp configuration list
	 * @return the cp configuration list
	 * @throws PortalException if a cp configuration list with the primary key could not be found
	 */
	@Override
	public CPConfigurationList getCPConfigurationList(
			long CPConfigurationListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListLocalService.getCPConfigurationList(
			CPConfigurationListId);
	}

	@Override
	public CPConfigurationList getCPConfigurationListByExternalReferenceCode(
			String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListLocalService.
			getCPConfigurationListByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp configuration list matching the UUID and group.
	 *
	 * @param uuid the cp configuration list's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp configuration list
	 * @throws PortalException if a matching cp configuration list could not be found
	 */
	@Override
	public CPConfigurationList getCPConfigurationListByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListLocalService.
			getCPConfigurationListByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the cp configuration lists.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationListModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cp configuration lists
	 * @param end the upper bound of the range of cp configuration lists (not inclusive)
	 * @return the range of cp configuration lists
	 */
	@Override
	public java.util.List<CPConfigurationList> getCPConfigurationLists(
		int start, int end) {

		return _cpConfigurationListLocalService.getCPConfigurationLists(
			start, end);
	}

	@Override
	public java.util.List<CPConfigurationList> getCPConfigurationLists(
		long groupId, long companyId) {

		return _cpConfigurationListLocalService.getCPConfigurationLists(
			groupId, companyId);
	}

	@Override
	public java.util.List<CPConfigurationList> getCPConfigurationLists(
		long companyId, long groupId, long accountEntryId,
		long[] accountGroupIds, long commerceChannelId,
		long commerceOrderTypeId) {

		return _cpConfigurationListLocalService.getCPConfigurationLists(
			companyId, groupId, accountEntryId, accountGroupIds,
			commerceChannelId, commerceOrderTypeId);
	}

	/**
	 * Returns all the cp configuration lists matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp configuration lists
	 * @param companyId the primary key of the company
	 * @return the matching cp configuration lists, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<CPConfigurationList>
		getCPConfigurationListsByUuidAndCompanyId(String uuid, long companyId) {

		return _cpConfigurationListLocalService.
			getCPConfigurationListsByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of cp configuration lists matching the UUID and company.
	 *
	 * @param uuid the UUID of the cp configuration lists
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of cp configuration lists
	 * @param end the upper bound of the range of cp configuration lists (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching cp configuration lists, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<CPConfigurationList>
		getCPConfigurationListsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<CPConfigurationList> orderByComparator) {

		return _cpConfigurationListLocalService.
			getCPConfigurationListsByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of cp configuration lists.
	 *
	 * @return the number of cp configuration lists
	 */
	@Override
	public int getCPConfigurationListsCount() {
		return _cpConfigurationListLocalService.getCPConfigurationListsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _cpConfigurationListLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _cpConfigurationListLocalService.
			getIndexableActionableDynamicQuery();
	}

	@Override
	public CPConfigurationList getMasterCPConfigurationList(long groupId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPConfigurationListException {

		return _cpConfigurationListLocalService.getMasterCPConfigurationList(
			groupId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _cpConfigurationListLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the cp configuration list in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CPConfigurationListLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param cpConfigurationList the cp configuration list
	 * @return the cp configuration list that was updated
	 */
	@Override
	public CPConfigurationList updateCPConfigurationList(
		CPConfigurationList cpConfigurationList) {

		return _cpConfigurationListLocalService.updateCPConfigurationList(
			cpConfigurationList);
	}

	@Override
	public CPConfigurationList updateCPConfigurationList(
			String externalReferenceCode, long cpConfigurationListId,
			long userId, long groupId, long parentCPConfigurationListId,
			boolean master, String name, double priority, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _cpConfigurationListLocalService.updateCPConfigurationList(
			externalReferenceCode, cpConfigurationListId, userId, groupId,
			parentCPConfigurationListId, master, name, priority,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _cpConfigurationListLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<CPConfigurationList> getCTPersistence() {
		return _cpConfigurationListLocalService.getCTPersistence();
	}

	@Override
	public Class<CPConfigurationList> getModelClass() {
		return _cpConfigurationListLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CPConfigurationList>, R, E>
				updateUnsafeFunction)
		throws E {

		return _cpConfigurationListLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public CPConfigurationListLocalService getWrappedService() {
		return _cpConfigurationListLocalService;
	}

	@Override
	public void setWrappedService(
		CPConfigurationListLocalService cpConfigurationListLocalService) {

		_cpConfigurationListLocalService = cpConfigurationListLocalService;
	}

	private CPConfigurationListLocalService _cpConfigurationListLocalService;

}