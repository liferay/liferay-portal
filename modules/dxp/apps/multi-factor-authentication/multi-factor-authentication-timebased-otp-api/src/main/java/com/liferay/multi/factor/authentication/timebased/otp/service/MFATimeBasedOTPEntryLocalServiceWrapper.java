/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.timebased.otp.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link MFATimeBasedOTPEntryLocalService}.
 *
 * @author Arthur Chan
 * @see MFATimeBasedOTPEntryLocalService
 * @generated
 */
public class MFATimeBasedOTPEntryLocalServiceWrapper
	implements MFATimeBasedOTPEntryLocalService,
			   ServiceWrapper<MFATimeBasedOTPEntryLocalService> {

	public MFATimeBasedOTPEntryLocalServiceWrapper() {
		this(null);
	}

	public MFATimeBasedOTPEntryLocalServiceWrapper(
		MFATimeBasedOTPEntryLocalService mfaTimeBasedOTPEntryLocalService) {

		_mfaTimeBasedOTPEntryLocalService = mfaTimeBasedOTPEntryLocalService;
	}

	/**
	 * Adds the mfa time based otp entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MFATimeBasedOTPEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param mfaTimeBasedOTPEntry the mfa time based otp entry
	 * @return the mfa time based otp entry that was added
	 */
	@Override
	public com.liferay.multi.factor.authentication.timebased.otp.model.
		MFATimeBasedOTPEntry addMFATimeBasedOTPEntry(
			com.liferay.multi.factor.authentication.timebased.otp.model.
				MFATimeBasedOTPEntry mfaTimeBasedOTPEntry) {

		return _mfaTimeBasedOTPEntryLocalService.addMFATimeBasedOTPEntry(
			mfaTimeBasedOTPEntry);
	}

	@Override
	public com.liferay.multi.factor.authentication.timebased.otp.model.
		MFATimeBasedOTPEntry addTimeBasedOTPEntry(
				long userId, String sharedSecret)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _mfaTimeBasedOTPEntryLocalService.addTimeBasedOTPEntry(
			userId, sharedSecret);
	}

	/**
	 * Creates a new mfa time based otp entry with the primary key. Does not add the mfa time based otp entry to the database.
	 *
	 * @param mfaTimeBasedOTPEntryId the primary key for the new mfa time based otp entry
	 * @return the new mfa time based otp entry
	 */
	@Override
	public com.liferay.multi.factor.authentication.timebased.otp.model.
		MFATimeBasedOTPEntry createMFATimeBasedOTPEntry(
			long mfaTimeBasedOTPEntryId) {

		return _mfaTimeBasedOTPEntryLocalService.createMFATimeBasedOTPEntry(
			mfaTimeBasedOTPEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mfaTimeBasedOTPEntryLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the mfa time based otp entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MFATimeBasedOTPEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param mfaTimeBasedOTPEntryId the primary key of the mfa time based otp entry
	 * @return the mfa time based otp entry that was removed
	 * @throws PortalException if a mfa time based otp entry with the primary key could not be found
	 */
	@Override
	public com.liferay.multi.factor.authentication.timebased.otp.model.
		MFATimeBasedOTPEntry deleteMFATimeBasedOTPEntry(
				long mfaTimeBasedOTPEntryId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _mfaTimeBasedOTPEntryLocalService.deleteMFATimeBasedOTPEntry(
			mfaTimeBasedOTPEntryId);
	}

	/**
	 * Deletes the mfa time based otp entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MFATimeBasedOTPEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param mfaTimeBasedOTPEntry the mfa time based otp entry
	 * @return the mfa time based otp entry that was removed
	 */
	@Override
	public com.liferay.multi.factor.authentication.timebased.otp.model.
		MFATimeBasedOTPEntry deleteMFATimeBasedOTPEntry(
			com.liferay.multi.factor.authentication.timebased.otp.model.
				MFATimeBasedOTPEntry mfaTimeBasedOTPEntry) {

		return _mfaTimeBasedOTPEntryLocalService.deleteMFATimeBasedOTPEntry(
			mfaTimeBasedOTPEntry);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mfaTimeBasedOTPEntryLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _mfaTimeBasedOTPEntryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _mfaTimeBasedOTPEntryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _mfaTimeBasedOTPEntryLocalService.dynamicQuery();
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

		return _mfaTimeBasedOTPEntryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.multi.factor.authentication.timebased.otp.model.impl.MFATimeBasedOTPEntryModelImpl</code>.
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

		return _mfaTimeBasedOTPEntryLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.multi.factor.authentication.timebased.otp.model.impl.MFATimeBasedOTPEntryModelImpl</code>.
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

		return _mfaTimeBasedOTPEntryLocalService.dynamicQuery(
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

		return _mfaTimeBasedOTPEntryLocalService.dynamicQueryCount(
			dynamicQuery);
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

		return _mfaTimeBasedOTPEntryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.multi.factor.authentication.timebased.otp.model.
		MFATimeBasedOTPEntry fetchMFATimeBasedOTPEntry(
			long mfaTimeBasedOTPEntryId) {

		return _mfaTimeBasedOTPEntryLocalService.fetchMFATimeBasedOTPEntry(
			mfaTimeBasedOTPEntryId);
	}

	@Override
	public com.liferay.multi.factor.authentication.timebased.otp.model.
		MFATimeBasedOTPEntry fetchMFATimeBasedOTPEntryByUserId(long userId) {

		return _mfaTimeBasedOTPEntryLocalService.
			fetchMFATimeBasedOTPEntryByUserId(userId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _mfaTimeBasedOTPEntryLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _mfaTimeBasedOTPEntryLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the mfa time based otp entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.multi.factor.authentication.timebased.otp.model.impl.MFATimeBasedOTPEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of mfa time based otp entries
	 * @param end the upper bound of the range of mfa time based otp entries (not inclusive)
	 * @return the range of mfa time based otp entries
	 */
	@Override
	public java.util.List
		<com.liferay.multi.factor.authentication.timebased.otp.model.
			MFATimeBasedOTPEntry> getMFATimeBasedOTPEntries(
				int start, int end) {

		return _mfaTimeBasedOTPEntryLocalService.getMFATimeBasedOTPEntries(
			start, end);
	}

	/**
	 * Returns the number of mfa time based otp entries.
	 *
	 * @return the number of mfa time based otp entries
	 */
	@Override
	public int getMFATimeBasedOTPEntriesCount() {
		return _mfaTimeBasedOTPEntryLocalService.
			getMFATimeBasedOTPEntriesCount();
	}

	/**
	 * Returns the mfa time based otp entry with the primary key.
	 *
	 * @param mfaTimeBasedOTPEntryId the primary key of the mfa time based otp entry
	 * @return the mfa time based otp entry
	 * @throws PortalException if a mfa time based otp entry with the primary key could not be found
	 */
	@Override
	public com.liferay.multi.factor.authentication.timebased.otp.model.
		MFATimeBasedOTPEntry getMFATimeBasedOTPEntry(
				long mfaTimeBasedOTPEntryId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _mfaTimeBasedOTPEntryLocalService.getMFATimeBasedOTPEntry(
			mfaTimeBasedOTPEntryId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _mfaTimeBasedOTPEntryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mfaTimeBasedOTPEntryLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public String getPlaintextSharedSecret(
			com.liferay.multi.factor.authentication.timebased.otp.model.
				MFATimeBasedOTPEntry mfaTimeBasedOTPEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mfaTimeBasedOTPEntryLocalService.getPlaintextSharedSecret(
			mfaTimeBasedOTPEntry);
	}

	@Override
	public com.liferay.multi.factor.authentication.timebased.otp.model.
		MFATimeBasedOTPEntry resetFailedAttempts(long userId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _mfaTimeBasedOTPEntryLocalService.resetFailedAttempts(userId);
	}

	@Override
	public com.liferay.multi.factor.authentication.timebased.otp.model.
		MFATimeBasedOTPEntry updateAttempts(
				long userId, String ipAddress, boolean success)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _mfaTimeBasedOTPEntryLocalService.updateAttempts(
			userId, ipAddress, success);
	}

	@Override
	public com.liferay.multi.factor.authentication.timebased.otp.model.
		MFATimeBasedOTPEntry updateLastTOTP(long userId, String lastValidTOTP)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _mfaTimeBasedOTPEntryLocalService.updateLastTOTP(
			userId, lastValidTOTP);
	}

	/**
	 * Updates the mfa time based otp entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MFATimeBasedOTPEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param mfaTimeBasedOTPEntry the mfa time based otp entry
	 * @return the mfa time based otp entry that was updated
	 */
	@Override
	public com.liferay.multi.factor.authentication.timebased.otp.model.
		MFATimeBasedOTPEntry updateMFATimeBasedOTPEntry(
			com.liferay.multi.factor.authentication.timebased.otp.model.
				MFATimeBasedOTPEntry mfaTimeBasedOTPEntry) {

		return _mfaTimeBasedOTPEntryLocalService.updateMFATimeBasedOTPEntry(
			mfaTimeBasedOTPEntry);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _mfaTimeBasedOTPEntryLocalService.getBasePersistence();
	}

	@Override
	public MFATimeBasedOTPEntryLocalService getWrappedService() {
		return _mfaTimeBasedOTPEntryLocalService;
	}

	@Override
	public void setWrappedService(
		MFATimeBasedOTPEntryLocalService mfaTimeBasedOTPEntryLocalService) {

		_mfaTimeBasedOTPEntryLocalService = mfaTimeBasedOTPEntryLocalService;
	}

	private MFATimeBasedOTPEntryLocalService _mfaTimeBasedOTPEntryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1886379628