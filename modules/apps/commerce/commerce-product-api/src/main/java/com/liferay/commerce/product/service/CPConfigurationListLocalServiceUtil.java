/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service;

import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for CPConfigurationList. This utility wraps
 * <code>com.liferay.commerce.product.service.impl.CPConfigurationListLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see CPConfigurationListLocalService
 * @generated
 */
public class CPConfigurationListLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.commerce.product.service.impl.CPConfigurationListLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

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
	public static CPConfigurationList addCPConfigurationList(
		CPConfigurationList cpConfigurationList) {

		return getService().addCPConfigurationList(cpConfigurationList);
	}

	public static CPConfigurationList addCPConfigurationList(
			String externalReferenceCode, long userId, long groupId,
			long parentCPConfigurationListId, boolean master, String name,
			double priority, int displayDateMonth, int displayDateDay,
			int displayDateYear, int displayDateHour, int displayDateMinute,
			int expirationDateMonth, int expirationDateDay,
			int expirationDateYear, int expirationDateHour,
			int expirationDateMinute, boolean neverExpire)
		throws PortalException {

		return getService().addCPConfigurationList(
			externalReferenceCode, userId, groupId, parentCPConfigurationListId,
			master, name, priority, displayDateMonth, displayDateDay,
			displayDateYear, displayDateHour, displayDateMinute,
			expirationDateMonth, expirationDateDay, expirationDateYear,
			expirationDateHour, expirationDateMinute, neverExpire);
	}

	public static CPConfigurationList addOrUpdateCPConfigurationList(
			String externalReferenceCode, long companyId, long userId,
			long groupId, long parentCPConfigurationListId, boolean master,
			String name, double priority, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire)
		throws PortalException {

		return getService().addOrUpdateCPConfigurationList(
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
	public static CPConfigurationList createCPConfigurationList(
		long CPConfigurationListId) {

		return getService().createCPConfigurationList(CPConfigurationListId);
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
	public static CPConfigurationList deleteCPConfigurationList(
			CPConfigurationList cpConfigurationList)
		throws PortalException {

		return getService().deleteCPConfigurationList(cpConfigurationList);
	}

	public static CPConfigurationList deleteCPConfigurationList(
			CPConfigurationList cpConfigurationList, boolean force)
		throws PortalException {

		return getService().deleteCPConfigurationList(
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
	public static CPConfigurationList deleteCPConfigurationList(
			long CPConfigurationListId)
		throws PortalException {

		return getService().deleteCPConfigurationList(CPConfigurationListId);
	}

	public static CPConfigurationList deleteCPConfigurationList(
			long cpConfigurationListId, boolean force)
		throws PortalException {

		return getService().deleteCPConfigurationList(
			cpConfigurationListId, force);
	}

	public static void deleteCPConfigurationLists(long companyId)
		throws PortalException {

		getService().deleteCPConfigurationLists(companyId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationListModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPConfigurationListModelImpl</code>.
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

	public static CPConfigurationList fetchCPConfigurationList(
		long CPConfigurationListId) {

		return getService().fetchCPConfigurationList(CPConfigurationListId);
	}

	public static CPConfigurationList
		fetchCPConfigurationListByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return getService().fetchCPConfigurationListByExternalReferenceCode(
			externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp configuration list matching the UUID and group.
	 *
	 * @param uuid the cp configuration list's UUID
	 * @param groupId the primary key of the group
	 * @return the matching cp configuration list, or <code>null</code> if a matching cp configuration list could not be found
	 */
	public static CPConfigurationList fetchCPConfigurationListByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchCPConfigurationListByUuidAndGroupId(
			uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the cp configuration list with the primary key.
	 *
	 * @param CPConfigurationListId the primary key of the cp configuration list
	 * @return the cp configuration list
	 * @throws PortalException if a cp configuration list with the primary key could not be found
	 */
	public static CPConfigurationList getCPConfigurationList(
			long CPConfigurationListId)
		throws PortalException {

		return getService().getCPConfigurationList(CPConfigurationListId);
	}

	public static CPConfigurationList
			getCPConfigurationListByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		return getService().getCPConfigurationListByExternalReferenceCode(
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
	public static CPConfigurationList getCPConfigurationListByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getCPConfigurationListByUuidAndGroupId(
			uuid, groupId);
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
	public static List<CPConfigurationList> getCPConfigurationLists(
		int start, int end) {

		return getService().getCPConfigurationLists(start, end);
	}

	public static List<CPConfigurationList> getCPConfigurationLists(
		long groupId, long companyId) {

		return getService().getCPConfigurationLists(groupId, companyId);
	}

	public static List<CPConfigurationList> getCPConfigurationLists(
		long companyId, long groupId, long accountEntryId,
		long[] accountGroupIds, long commerceChannelId,
		long commerceOrderTypeId) {

		return getService().getCPConfigurationLists(
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
	public static List<CPConfigurationList>
		getCPConfigurationListsByUuidAndCompanyId(String uuid, long companyId) {

		return getService().getCPConfigurationListsByUuidAndCompanyId(
			uuid, companyId);
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
	public static List<CPConfigurationList>
		getCPConfigurationListsByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<CPConfigurationList> orderByComparator) {

		return getService().getCPConfigurationListsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of cp configuration lists.
	 *
	 * @return the number of cp configuration lists
	 */
	public static int getCPConfigurationListsCount() {
		return getService().getCPConfigurationListsCount();
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

	public static CPConfigurationList getMasterCPConfigurationList(long groupId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPConfigurationListException {

		return getService().getMasterCPConfigurationList(groupId);
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
	public static CPConfigurationList updateCPConfigurationList(
		CPConfigurationList cpConfigurationList) {

		return getService().updateCPConfigurationList(cpConfigurationList);
	}

	public static CPConfigurationList updateCPConfigurationList(
			String externalReferenceCode, long cpConfigurationListId,
			long userId, long groupId, long parentCPConfigurationListId,
			boolean master, String name, double priority, int displayDateMonth,
			int displayDateDay, int displayDateYear, int displayDateHour,
			int displayDateMinute, int expirationDateMonth,
			int expirationDateDay, int expirationDateYear,
			int expirationDateHour, int expirationDateMinute,
			boolean neverExpire)
		throws PortalException {

		return getService().updateCPConfigurationList(
			externalReferenceCode, cpConfigurationListId, userId, groupId,
			parentCPConfigurationListId, master, name, priority,
			displayDateMonth, displayDateDay, displayDateYear, displayDateHour,
			displayDateMinute, expirationDateMonth, expirationDateDay,
			expirationDateYear, expirationDateHour, expirationDateMinute,
			neverExpire);
	}

	public static CPConfigurationListLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CPConfigurationListLocalService>
		_serviceSnapshot = new Snapshot<>(
			CPConfigurationListLocalServiceUtil.class,
			CPConfigurationListLocalService.class);

}