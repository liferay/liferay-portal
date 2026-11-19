/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service;

import com.liferay.message.boards.model.MBMailingList;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for MBMailingList. This utility wraps
 * <code>com.liferay.message.boards.service.impl.MBMailingListLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see MBMailingListLocalService
 * @generated
 */
public class MBMailingListLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.message.boards.service.impl.MBMailingListLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static MBMailingList addMailingList(
			long userId, long groupId, long categoryId, String emailAddress,
			String inProtocol, String inServerName, int inServerPort,
			boolean inUseSSL, String inUserName, String inPassword,
			int inReadInterval, String outEmailAddress, boolean outCustom,
			String outServerName, int outServerPort, boolean outUseSSL,
			String outUserName, String outPassword, boolean allowAnonymous,
			boolean active,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addMailingList(
			userId, groupId, categoryId, emailAddress, inProtocol, inServerName,
			inServerPort, inUseSSL, inUserName, inPassword, inReadInterval,
			outEmailAddress, outCustom, outServerName, outServerPort, outUseSSL,
			outUserName, outPassword, allowAnonymous, active, serviceContext);
	}

	/**
	 * Adds the message boards mailing list to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBMailingListLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param mbMailingList the message boards mailing list
	 * @return the message boards mailing list that was added
	 */
	public static MBMailingList addMBMailingList(MBMailingList mbMailingList) {
		return getService().addMBMailingList(mbMailingList);
	}

	/**
	 * Creates a new message boards mailing list with the primary key. Does not add the message boards mailing list to the database.
	 *
	 * @param mailingListId the primary key for the new message boards mailing list
	 * @return the new message boards mailing list
	 */
	public static MBMailingList createMBMailingList(long mailingListId) {
		return getService().createMBMailingList(mailingListId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteMailingList(long mailingListId)
		throws PortalException {

		getService().deleteMailingList(mailingListId);
	}

	public static void deleteMailingList(MBMailingList mailingList)
		throws PortalException {

		getService().deleteMailingList(mailingList);
	}

	/**
	 * Deletes the message boards mailing list with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBMailingListLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param mailingListId the primary key of the message boards mailing list
	 * @return the message boards mailing list that was removed
	 * @throws PortalException if a message boards mailing list with the primary key could not be found
	 */
	public static MBMailingList deleteMBMailingList(long mailingListId)
		throws PortalException {

		return getService().deleteMBMailingList(mailingListId);
	}

	/**
	 * Deletes the message boards mailing list from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBMailingListLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param mbMailingList the message boards mailing list
	 * @return the message boards mailing list that was removed
	 */
	public static MBMailingList deleteMBMailingList(
		MBMailingList mbMailingList) {

		return getService().deleteMBMailingList(mbMailingList);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMailingListModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMailingListModelImpl</code>.
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

	public static MBMailingList fetchCategoryMailingList(
		long groupId, long categoryId) {

		return getService().fetchCategoryMailingList(groupId, categoryId);
	}

	public static MBMailingList fetchMBMailingList(long mailingListId) {
		return getService().fetchMBMailingList(mailingListId);
	}

	/**
	 * Returns the message boards mailing list matching the UUID and group.
	 *
	 * @param uuid the message boards mailing list's UUID
	 * @param groupId the primary key of the group
	 * @return the matching message boards mailing list, or <code>null</code> if a matching message boards mailing list could not be found
	 */
	public static MBMailingList fetchMBMailingListByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchMBMailingListByUuidAndGroupId(uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
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
	 * Returns the message boards mailing list with the primary key.
	 *
	 * @param mailingListId the primary key of the message boards mailing list
	 * @return the message boards mailing list
	 * @throws PortalException if a message boards mailing list with the primary key could not be found
	 */
	public static MBMailingList getMBMailingList(long mailingListId)
		throws PortalException {

		return getService().getMBMailingList(mailingListId);
	}

	/**
	 * Returns the message boards mailing list matching the UUID and group.
	 *
	 * @param uuid the message boards mailing list's UUID
	 * @param groupId the primary key of the group
	 * @return the matching message boards mailing list
	 * @throws PortalException if a matching message boards mailing list could not be found
	 */
	public static MBMailingList getMBMailingListByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getMBMailingListByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the message boards mailing lists.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMailingListModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of message boards mailing lists
	 * @param end the upper bound of the range of message boards mailing lists (not inclusive)
	 * @return the range of message boards mailing lists
	 */
	public static List<MBMailingList> getMBMailingLists(int start, int end) {
		return getService().getMBMailingLists(start, end);
	}

	/**
	 * Returns all the message boards mailing lists matching the UUID and company.
	 *
	 * @param uuid the UUID of the message boards mailing lists
	 * @param companyId the primary key of the company
	 * @return the matching message boards mailing lists, or an empty list if no matches were found
	 */
	public static List<MBMailingList> getMBMailingListsByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getMBMailingListsByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of message boards mailing lists matching the UUID and company.
	 *
	 * @param uuid the UUID of the message boards mailing lists
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of message boards mailing lists
	 * @param end the upper bound of the range of message boards mailing lists (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching message boards mailing lists, or an empty list if no matches were found
	 */
	public static List<MBMailingList> getMBMailingListsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<MBMailingList> orderByComparator) {

		return getService().getMBMailingListsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of message boards mailing lists.
	 *
	 * @return the number of message boards mailing lists
	 */
	public static int getMBMailingListsCount() {
		return getService().getMBMailingListsCount();
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

	public static MBMailingList updateMailingList(
			long mailingListId, String emailAddress, String inProtocol,
			String inServerName, int inServerPort, boolean inUseSSL,
			String inUserName, String inPassword, int inReadInterval,
			String outEmailAddress, boolean outCustom, String outServerName,
			int outServerPort, boolean outUseSSL, String outUserName,
			String outPassword, boolean allowAnonymous, boolean active,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateMailingList(
			mailingListId, emailAddress, inProtocol, inServerName, inServerPort,
			inUseSSL, inUserName, inPassword, inReadInterval, outEmailAddress,
			outCustom, outServerName, outServerPort, outUseSSL, outUserName,
			outPassword, allowAnonymous, active, serviceContext);
	}

	/**
	 * Updates the message boards mailing list in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBMailingListLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param mbMailingList the message boards mailing list
	 * @return the message boards mailing list that was updated
	 */
	public static MBMailingList updateMBMailingList(
		MBMailingList mbMailingList) {

		return getService().updateMBMailingList(mbMailingList);
	}

	public static MBMailingListLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<MBMailingListLocalService> _serviceSnapshot =
		new Snapshot<>(
			MBMailingListLocalServiceUtil.class,
			MBMailingListLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:225513219