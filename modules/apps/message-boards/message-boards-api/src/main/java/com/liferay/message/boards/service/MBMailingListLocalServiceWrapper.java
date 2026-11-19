/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service;

import com.liferay.message.boards.model.MBMailingList;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link MBMailingListLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see MBMailingListLocalService
 * @generated
 */
public class MBMailingListLocalServiceWrapper
	implements MBMailingListLocalService,
			   ServiceWrapper<MBMailingListLocalService> {

	public MBMailingListLocalServiceWrapper() {
		this(null);
	}

	public MBMailingListLocalServiceWrapper(
		MBMailingListLocalService mbMailingListLocalService) {

		_mbMailingListLocalService = mbMailingListLocalService;
	}

	@Override
	public MBMailingList addMailingList(
			long userId, long groupId, long categoryId, String emailAddress,
			String inProtocol, String inServerName, int inServerPort,
			boolean inUseSSL, String inUserName, String inPassword,
			int inReadInterval, String outEmailAddress, boolean outCustom,
			String outServerName, int outServerPort, boolean outUseSSL,
			String outUserName, String outPassword, boolean allowAnonymous,
			boolean active,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMailingListLocalService.addMailingList(
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
	@Override
	public MBMailingList addMBMailingList(MBMailingList mbMailingList) {
		return _mbMailingListLocalService.addMBMailingList(mbMailingList);
	}

	/**
	 * Creates a new message boards mailing list with the primary key. Does not add the message boards mailing list to the database.
	 *
	 * @param mailingListId the primary key for the new message boards mailing list
	 * @return the new message boards mailing list
	 */
	@Override
	public MBMailingList createMBMailingList(long mailingListId) {
		return _mbMailingListLocalService.createMBMailingList(mailingListId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMailingListLocalService.createPersistedModel(primaryKeyObj);
	}

	@Override
	public void deleteMailingList(long mailingListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_mbMailingListLocalService.deleteMailingList(mailingListId);
	}

	@Override
	public void deleteMailingList(MBMailingList mailingList)
		throws com.liferay.portal.kernel.exception.PortalException {

		_mbMailingListLocalService.deleteMailingList(mailingList);
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
	@Override
	public MBMailingList deleteMBMailingList(long mailingListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMailingListLocalService.deleteMBMailingList(mailingListId);
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
	@Override
	public MBMailingList deleteMBMailingList(MBMailingList mbMailingList) {
		return _mbMailingListLocalService.deleteMBMailingList(mbMailingList);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMailingListLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _mbMailingListLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _mbMailingListLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _mbMailingListLocalService.dynamicQuery();
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

		return _mbMailingListLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _mbMailingListLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _mbMailingListLocalService.dynamicQuery(
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

		return _mbMailingListLocalService.dynamicQueryCount(dynamicQuery);
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

		return _mbMailingListLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public MBMailingList fetchCategoryMailingList(
		long groupId, long categoryId) {

		return _mbMailingListLocalService.fetchCategoryMailingList(
			groupId, categoryId);
	}

	@Override
	public MBMailingList fetchMBMailingList(long mailingListId) {
		return _mbMailingListLocalService.fetchMBMailingList(mailingListId);
	}

	/**
	 * Returns the message boards mailing list matching the UUID and group.
	 *
	 * @param uuid the message boards mailing list's UUID
	 * @param groupId the primary key of the group
	 * @return the matching message boards mailing list, or <code>null</code> if a matching message boards mailing list could not be found
	 */
	@Override
	public MBMailingList fetchMBMailingListByUuidAndGroupId(
		String uuid, long groupId) {

		return _mbMailingListLocalService.fetchMBMailingListByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _mbMailingListLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _mbMailingListLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _mbMailingListLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the message boards mailing list with the primary key.
	 *
	 * @param mailingListId the primary key of the message boards mailing list
	 * @return the message boards mailing list
	 * @throws PortalException if a message boards mailing list with the primary key could not be found
	 */
	@Override
	public MBMailingList getMBMailingList(long mailingListId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMailingListLocalService.getMBMailingList(mailingListId);
	}

	/**
	 * Returns the message boards mailing list matching the UUID and group.
	 *
	 * @param uuid the message boards mailing list's UUID
	 * @param groupId the primary key of the group
	 * @return the matching message boards mailing list
	 * @throws PortalException if a matching message boards mailing list could not be found
	 */
	@Override
	public MBMailingList getMBMailingListByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMailingListLocalService.getMBMailingListByUuidAndGroupId(
			uuid, groupId);
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
	@Override
	public java.util.List<MBMailingList> getMBMailingLists(int start, int end) {
		return _mbMailingListLocalService.getMBMailingLists(start, end);
	}

	/**
	 * Returns all the message boards mailing lists matching the UUID and company.
	 *
	 * @param uuid the UUID of the message boards mailing lists
	 * @param companyId the primary key of the company
	 * @return the matching message boards mailing lists, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<MBMailingList> getMBMailingListsByUuidAndCompanyId(
		String uuid, long companyId) {

		return _mbMailingListLocalService.getMBMailingListsByUuidAndCompanyId(
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
	@Override
	public java.util.List<MBMailingList> getMBMailingListsByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBMailingList>
			orderByComparator) {

		return _mbMailingListLocalService.getMBMailingListsByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of message boards mailing lists.
	 *
	 * @return the number of message boards mailing lists
	 */
	@Override
	public int getMBMailingListsCount() {
		return _mbMailingListLocalService.getMBMailingListsCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _mbMailingListLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMailingListLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public MBMailingList updateMailingList(
			long mailingListId, String emailAddress, String inProtocol,
			String inServerName, int inServerPort, boolean inUseSSL,
			String inUserName, String inPassword, int inReadInterval,
			String outEmailAddress, boolean outCustom, String outServerName,
			int outServerPort, boolean outUseSSL, String outUserName,
			String outPassword, boolean allowAnonymous, boolean active,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbMailingListLocalService.updateMailingList(
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
	@Override
	public MBMailingList updateMBMailingList(MBMailingList mbMailingList) {
		return _mbMailingListLocalService.updateMBMailingList(mbMailingList);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _mbMailingListLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<MBMailingList> getCTPersistence() {
		return _mbMailingListLocalService.getCTPersistence();
	}

	@Override
	public Class<MBMailingList> getModelClass() {
		return _mbMailingListLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<MBMailingList>, R, E>
				updateUnsafeFunction)
		throws E {

		return _mbMailingListLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public MBMailingListLocalService getWrappedService() {
		return _mbMailingListLocalService;
	}

	@Override
	public void setWrappedService(
		MBMailingListLocalService mbMailingListLocalService) {

		_mbMailingListLocalService = mbMailingListLocalService;
	}

	private MBMailingListLocalService _mbMailingListLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:826995424