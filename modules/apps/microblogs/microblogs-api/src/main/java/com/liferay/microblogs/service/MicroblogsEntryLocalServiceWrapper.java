/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.microblogs.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link MicroblogsEntryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see MicroblogsEntryLocalService
 * @generated
 */
public class MicroblogsEntryLocalServiceWrapper
	implements MicroblogsEntryLocalService,
			   ServiceWrapper<MicroblogsEntryLocalService> {

	public MicroblogsEntryLocalServiceWrapper() {
		this(null);
	}

	public MicroblogsEntryLocalServiceWrapper(
		MicroblogsEntryLocalService microblogsEntryLocalService) {

		_microblogsEntryLocalService = microblogsEntryLocalService;
	}

	@Override
	public com.liferay.microblogs.model.MicroblogsEntry addMicroblogsEntry(
			long userId, long creatorClassNameId, long creatorClassPK,
			String content, int type, long parentMicroblogsEntryId,
			int socialRelationType,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _microblogsEntryLocalService.addMicroblogsEntry(
			userId, creatorClassNameId, creatorClassPK, content, type,
			parentMicroblogsEntryId, socialRelationType, serviceContext);
	}

	@Override
	public com.liferay.microblogs.model.MicroblogsEntry addMicroblogsEntry(
			long userId, String content, int type, long parentMicroblogsEntryId,
			int socialRelationType,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _microblogsEntryLocalService.addMicroblogsEntry(
			userId, content, type, parentMicroblogsEntryId, socialRelationType,
			serviceContext);
	}

	/**
	 * Adds the microblogs entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MicroblogsEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param microblogsEntry the microblogs entry
	 * @return the microblogs entry that was added
	 */
	@Override
	public com.liferay.microblogs.model.MicroblogsEntry addMicroblogsEntry(
		com.liferay.microblogs.model.MicroblogsEntry microblogsEntry) {

		return _microblogsEntryLocalService.addMicroblogsEntry(microblogsEntry);
	}

	/**
	 * Creates a new microblogs entry with the primary key. Does not add the microblogs entry to the database.
	 *
	 * @param microblogsEntryId the primary key for the new microblogs entry
	 * @return the new microblogs entry
	 */
	@Override
	public com.liferay.microblogs.model.MicroblogsEntry createMicroblogsEntry(
		long microblogsEntryId) {

		return _microblogsEntryLocalService.createMicroblogsEntry(
			microblogsEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _microblogsEntryLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the microblogs entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MicroblogsEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param microblogsEntryId the primary key of the microblogs entry
	 * @return the microblogs entry that was removed
	 * @throws PortalException if a microblogs entry with the primary key could not be found
	 */
	@Override
	public com.liferay.microblogs.model.MicroblogsEntry deleteMicroblogsEntry(
			long microblogsEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _microblogsEntryLocalService.deleteMicroblogsEntry(
			microblogsEntryId);
	}

	/**
	 * Deletes the microblogs entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MicroblogsEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param microblogsEntry the microblogs entry
	 * @return the microblogs entry that was removed
	 * @throws PortalException
	 */
	@Override
	public com.liferay.microblogs.model.MicroblogsEntry deleteMicroblogsEntry(
			com.liferay.microblogs.model.MicroblogsEntry microblogsEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _microblogsEntryLocalService.deleteMicroblogsEntry(
			microblogsEntry);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _microblogsEntryLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public void deleteUserMicroblogsEntries(long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_microblogsEntryLocalService.deleteUserMicroblogsEntries(userId);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _microblogsEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _microblogsEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _microblogsEntryLocalService.dynamicQuery();
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

		return _microblogsEntryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
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

		return _microblogsEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
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

		return _microblogsEntryLocalService.dynamicQuery(
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

		return _microblogsEntryLocalService.dynamicQueryCount(dynamicQuery);
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

		return _microblogsEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.microblogs.model.MicroblogsEntry fetchMicroblogsEntry(
		long microblogsEntryId) {

		return _microblogsEntryLocalService.fetchMicroblogsEntry(
			microblogsEntryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _microblogsEntryLocalService.getActionableDynamicQuery();
	}

	@Override
	public java.util.List<com.liferay.microblogs.model.MicroblogsEntry>
		getCompanyMicroblogsEntries(long companyId, int start, int end) {

		return _microblogsEntryLocalService.getCompanyMicroblogsEntries(
			companyId, start, end);
	}

	@Override
	public int getCompanyMicroblogsEntriesCount(long companyId) {
		return _microblogsEntryLocalService.getCompanyMicroblogsEntriesCount(
			companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _microblogsEntryLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the microblogs entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.microblogs.model.impl.MicroblogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of microblogs entries
	 * @param end the upper bound of the range of microblogs entries (not inclusive)
	 * @return the range of microblogs entries
	 */
	@Override
	public java.util.List<com.liferay.microblogs.model.MicroblogsEntry>
		getMicroblogsEntries(int start, int end) {

		return _microblogsEntryLocalService.getMicroblogsEntries(start, end);
	}

	/**
	 * Returns the number of microblogs entries.
	 *
	 * @return the number of microblogs entries
	 */
	@Override
	public int getMicroblogsEntriesCount() {
		return _microblogsEntryLocalService.getMicroblogsEntriesCount();
	}

	/**
	 * Returns the microblogs entry with the primary key.
	 *
	 * @param microblogsEntryId the primary key of the microblogs entry
	 * @return the microblogs entry
	 * @throws PortalException if a microblogs entry with the primary key could not be found
	 */
	@Override
	public com.liferay.microblogs.model.MicroblogsEntry getMicroblogsEntry(
			long microblogsEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _microblogsEntryLocalService.getMicroblogsEntry(
			microblogsEntryId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _microblogsEntryLocalService.getOSGiServiceIdentifier();
	}

	@Override
	public java.util.List<com.liferay.microblogs.model.MicroblogsEntry>
		getParentMicroblogsEntryMicroblogsEntries(
			int type, long parentMicroblogsEntryId, int start, int end) {

		return _microblogsEntryLocalService.
			getParentMicroblogsEntryMicroblogsEntries(
				type, parentMicroblogsEntryId, start, end);
	}

	@Override
	public java.util.List<com.liferay.microblogs.model.MicroblogsEntry>
		getParentMicroblogsEntryMicroblogsEntries(
			int type, long parentMicroblogsEntryId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.microblogs.model.MicroblogsEntry>
					orderByComparator) {

		return _microblogsEntryLocalService.
			getParentMicroblogsEntryMicroblogsEntries(
				type, parentMicroblogsEntryId, start, end, orderByComparator);
	}

	@Override
	public int getParentMicroblogsEntryMicroblogsEntriesCount(
		int type, long parentMicroblogsEntryId) {

		return _microblogsEntryLocalService.
			getParentMicroblogsEntryMicroblogsEntriesCount(
				type, parentMicroblogsEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _microblogsEntryLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public java.util.List<com.liferay.microblogs.model.MicroblogsEntry>
		getUserMicroblogsEntries(long userId, int start, int end) {

		return _microblogsEntryLocalService.getUserMicroblogsEntries(
			userId, start, end);
	}

	@Override
	public java.util.List<com.liferay.microblogs.model.MicroblogsEntry>
		getUserMicroblogsEntries(long userId, int type, int start, int end) {

		return _microblogsEntryLocalService.getUserMicroblogsEntries(
			userId, type, start, end);
	}

	@Override
	public int getUserMicroblogsEntriesCount(long userId) {
		return _microblogsEntryLocalService.getUserMicroblogsEntriesCount(
			userId);
	}

	@Override
	public int getUserMicroblogsEntriesCount(long userId, int type) {
		return _microblogsEntryLocalService.getUserMicroblogsEntriesCount(
			userId, type);
	}

	@Override
	public void updateAsset(
			com.liferay.microblogs.model.MicroblogsEntry microblogsEntry,
			long[] assetCategoryIds, String[] assetTagNames)
		throws com.liferay.portal.kernel.exception.PortalException {

		_microblogsEntryLocalService.updateAsset(
			microblogsEntry, assetCategoryIds, assetTagNames);
	}

	@Override
	public com.liferay.microblogs.model.MicroblogsEntry updateMicroblogsEntry(
			long microblogsEntryId, String content, int socialRelationType,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _microblogsEntryLocalService.updateMicroblogsEntry(
			microblogsEntryId, content, socialRelationType, serviceContext);
	}

	/**
	 * Updates the microblogs entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MicroblogsEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param microblogsEntry the microblogs entry
	 * @return the microblogs entry that was updated
	 */
	@Override
	public com.liferay.microblogs.model.MicroblogsEntry updateMicroblogsEntry(
		com.liferay.microblogs.model.MicroblogsEntry microblogsEntry) {

		return _microblogsEntryLocalService.updateMicroblogsEntry(
			microblogsEntry);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _microblogsEntryLocalService.getBasePersistence();
	}

	@Override
	public MicroblogsEntryLocalService getWrappedService() {
		return _microblogsEntryLocalService;
	}

	@Override
	public void setWrappedService(
		MicroblogsEntryLocalService microblogsEntryLocalService) {

		_microblogsEntryLocalService = microblogsEntryLocalService;
	}

	private MicroblogsEntryLocalService _microblogsEntryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1891286322