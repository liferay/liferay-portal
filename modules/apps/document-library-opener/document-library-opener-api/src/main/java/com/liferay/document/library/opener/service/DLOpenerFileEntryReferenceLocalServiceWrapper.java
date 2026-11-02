/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.opener.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link DLOpenerFileEntryReferenceLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see DLOpenerFileEntryReferenceLocalService
 * @generated
 */
public class DLOpenerFileEntryReferenceLocalServiceWrapper
	implements DLOpenerFileEntryReferenceLocalService,
			   ServiceWrapper<DLOpenerFileEntryReferenceLocalService> {

	public DLOpenerFileEntryReferenceLocalServiceWrapper() {
		this(null);
	}

	public DLOpenerFileEntryReferenceLocalServiceWrapper(
		DLOpenerFileEntryReferenceLocalService
			dlOpenerFileEntryReferenceLocalService) {

		_dlOpenerFileEntryReferenceLocalService =
			dlOpenerFileEntryReferenceLocalService;
	}

	/**
	 * Adds the dl opener file entry reference to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLOpenerFileEntryReferenceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlOpenerFileEntryReference the dl opener file entry reference
	 * @return the dl opener file entry reference that was added
	 */
	@Override
	public com.liferay.document.library.opener.model.DLOpenerFileEntryReference
		addDLOpenerFileEntryReference(
			com.liferay.document.library.opener.model.DLOpenerFileEntryReference
				dlOpenerFileEntryReference) {

		return _dlOpenerFileEntryReferenceLocalService.
			addDLOpenerFileEntryReference(dlOpenerFileEntryReference);
	}

	@Override
	public com.liferay.document.library.opener.model.DLOpenerFileEntryReference
			addPlaceholderDLOpenerFileEntryReference(
				long userId, String referenceType,
				com.liferay.portal.kernel.repository.model.FileEntry fileEntry,
				int type)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlOpenerFileEntryReferenceLocalService.
			addPlaceholderDLOpenerFileEntryReference(
				userId, referenceType, fileEntry, type);
	}

	/**
	 * Creates a new dl opener file entry reference with the primary key. Does not add the dl opener file entry reference to the database.
	 *
	 * @param dlOpenerFileEntryReferenceId the primary key for the new dl opener file entry reference
	 * @return the new dl opener file entry reference
	 */
	@Override
	public com.liferay.document.library.opener.model.DLOpenerFileEntryReference
		createDLOpenerFileEntryReference(long dlOpenerFileEntryReferenceId) {

		return _dlOpenerFileEntryReferenceLocalService.
			createDLOpenerFileEntryReference(dlOpenerFileEntryReferenceId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlOpenerFileEntryReferenceLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the dl opener file entry reference from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLOpenerFileEntryReferenceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlOpenerFileEntryReference the dl opener file entry reference
	 * @return the dl opener file entry reference that was removed
	 */
	@Override
	public com.liferay.document.library.opener.model.DLOpenerFileEntryReference
		deleteDLOpenerFileEntryReference(
			com.liferay.document.library.opener.model.DLOpenerFileEntryReference
				dlOpenerFileEntryReference) {

		return _dlOpenerFileEntryReferenceLocalService.
			deleteDLOpenerFileEntryReference(dlOpenerFileEntryReference);
	}

	/**
	 * @deprecated As of Mueller (7.2.x), replaced by {@link
	 #deleteDLOpenerFileEntryReference(String, FileEntry)}
	 */
	@Deprecated
	@Override
	public void deleteDLOpenerFileEntryReference(
			com.liferay.portal.kernel.repository.model.FileEntry fileEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlOpenerFileEntryReferenceLocalService.
			deleteDLOpenerFileEntryReference(fileEntry);
	}

	/**
	 * Deletes the dl opener file entry reference with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLOpenerFileEntryReferenceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlOpenerFileEntryReferenceId the primary key of the dl opener file entry reference
	 * @return the dl opener file entry reference that was removed
	 * @throws PortalException if a dl opener file entry reference with the primary key could not be found
	 */
	@Override
	public com.liferay.document.library.opener.model.DLOpenerFileEntryReference
			deleteDLOpenerFileEntryReference(long dlOpenerFileEntryReferenceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlOpenerFileEntryReferenceLocalService.
			deleteDLOpenerFileEntryReference(dlOpenerFileEntryReferenceId);
	}

	@Override
	public void deleteDLOpenerFileEntryReference(
			String referenceType,
			com.liferay.portal.kernel.repository.model.FileEntry fileEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlOpenerFileEntryReferenceLocalService.
			deleteDLOpenerFileEntryReference(referenceType, fileEntry);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlOpenerFileEntryReferenceLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _dlOpenerFileEntryReferenceLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _dlOpenerFileEntryReferenceLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _dlOpenerFileEntryReferenceLocalService.dynamicQuery();
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

		return _dlOpenerFileEntryReferenceLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.document.library.opener.model.impl.DLOpenerFileEntryReferenceModelImpl</code>.
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

		return _dlOpenerFileEntryReferenceLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.document.library.opener.model.impl.DLOpenerFileEntryReferenceModelImpl</code>.
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

		return _dlOpenerFileEntryReferenceLocalService.dynamicQuery(
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

		return _dlOpenerFileEntryReferenceLocalService.dynamicQueryCount(
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

		return _dlOpenerFileEntryReferenceLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	/**
	 * @deprecated As of Mueller (7.2.x), replaced by {@link
	 #fetchDLOpenerFileEntryReference(String, FileEntry)}
	 */
	@Deprecated
	@Override
	public com.liferay.document.library.opener.model.DLOpenerFileEntryReference
		fetchDLOpenerFileEntryReference(
			com.liferay.portal.kernel.repository.model.FileEntry fileEntry) {

		return _dlOpenerFileEntryReferenceLocalService.
			fetchDLOpenerFileEntryReference(fileEntry);
	}

	@Override
	public com.liferay.document.library.opener.model.DLOpenerFileEntryReference
		fetchDLOpenerFileEntryReference(long dlOpenerFileEntryReferenceId) {

		return _dlOpenerFileEntryReferenceLocalService.
			fetchDLOpenerFileEntryReference(dlOpenerFileEntryReferenceId);
	}

	@Override
	public com.liferay.document.library.opener.model.DLOpenerFileEntryReference
		fetchDLOpenerFileEntryReference(
			String referenceKey,
			com.liferay.portal.kernel.repository.model.FileEntry fileEntry) {

		return _dlOpenerFileEntryReferenceLocalService.
			fetchDLOpenerFileEntryReference(referenceKey, fileEntry);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _dlOpenerFileEntryReferenceLocalService.
			getActionableDynamicQuery();
	}

	/**
	 * @deprecated As of Mueller (7.2.x), replaced by {@link
	 #getDLOpenerFileEntryReference(String, FileEntry)}
	 */
	@Deprecated
	@Override
	public com.liferay.document.library.opener.model.DLOpenerFileEntryReference
			getDLOpenerFileEntryReference(
				com.liferay.portal.kernel.repository.model.FileEntry fileEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlOpenerFileEntryReferenceLocalService.
			getDLOpenerFileEntryReference(fileEntry);
	}

	/**
	 * Returns the dl opener file entry reference with the primary key.
	 *
	 * @param dlOpenerFileEntryReferenceId the primary key of the dl opener file entry reference
	 * @return the dl opener file entry reference
	 * @throws PortalException if a dl opener file entry reference with the primary key could not be found
	 */
	@Override
	public com.liferay.document.library.opener.model.DLOpenerFileEntryReference
			getDLOpenerFileEntryReference(long dlOpenerFileEntryReferenceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlOpenerFileEntryReferenceLocalService.
			getDLOpenerFileEntryReference(dlOpenerFileEntryReferenceId);
	}

	@Override
	public com.liferay.document.library.opener.model.DLOpenerFileEntryReference
			getDLOpenerFileEntryReference(
				String referenceType,
				com.liferay.portal.kernel.repository.model.FileEntry fileEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlOpenerFileEntryReferenceLocalService.
			getDLOpenerFileEntryReference(referenceType, fileEntry);
	}

	/**
	 * Returns a range of all the dl opener file entry references.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.document.library.opener.model.impl.DLOpenerFileEntryReferenceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of dl opener file entry references
	 * @param end the upper bound of the range of dl opener file entry references (not inclusive)
	 * @return the range of dl opener file entry references
	 */
	@Override
	public java.util.List
		<com.liferay.document.library.opener.model.DLOpenerFileEntryReference>
			getDLOpenerFileEntryReferences(int start, int end) {

		return _dlOpenerFileEntryReferenceLocalService.
			getDLOpenerFileEntryReferences(start, end);
	}

	/**
	 * Returns the number of dl opener file entry references.
	 *
	 * @return the number of dl opener file entry references
	 */
	@Override
	public int getDLOpenerFileEntryReferencesCount() {
		return _dlOpenerFileEntryReferenceLocalService.
			getDLOpenerFileEntryReferencesCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _dlOpenerFileEntryReferenceLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _dlOpenerFileEntryReferenceLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlOpenerFileEntryReferenceLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the dl opener file entry reference in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLOpenerFileEntryReferenceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlOpenerFileEntryReference the dl opener file entry reference
	 * @return the dl opener file entry reference that was updated
	 */
	@Override
	public com.liferay.document.library.opener.model.DLOpenerFileEntryReference
		updateDLOpenerFileEntryReference(
			com.liferay.document.library.opener.model.DLOpenerFileEntryReference
				dlOpenerFileEntryReference) {

		return _dlOpenerFileEntryReferenceLocalService.
			updateDLOpenerFileEntryReference(dlOpenerFileEntryReference);
	}

	@Override
	public com.liferay.document.library.opener.model.DLOpenerFileEntryReference
		updateDLOpenerFileEntryReference(
			String referenceKey, String referenceType,
			com.liferay.portal.kernel.repository.model.FileEntry fileEntry) {

		return _dlOpenerFileEntryReferenceLocalService.
			updateDLOpenerFileEntryReference(
				referenceKey, referenceType, fileEntry);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _dlOpenerFileEntryReferenceLocalService.getBasePersistence();
	}

	@Override
	public DLOpenerFileEntryReferenceLocalService getWrappedService() {
		return _dlOpenerFileEntryReferenceLocalService;
	}

	@Override
	public void setWrappedService(
		DLOpenerFileEntryReferenceLocalService
			dlOpenerFileEntryReferenceLocalService) {

		_dlOpenerFileEntryReferenceLocalService =
			dlOpenerFileEntryReferenceLocalService;
	}

	private DLOpenerFileEntryReferenceLocalService
		_dlOpenerFileEntryReferenceLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1924432079