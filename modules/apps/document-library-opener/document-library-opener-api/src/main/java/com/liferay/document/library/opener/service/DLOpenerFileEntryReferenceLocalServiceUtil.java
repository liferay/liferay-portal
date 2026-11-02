/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.opener.service;

import com.liferay.document.library.opener.model.DLOpenerFileEntryReference;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for DLOpenerFileEntryReference. This utility wraps
 * <code>com.liferay.document.library.opener.service.impl.DLOpenerFileEntryReferenceLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see DLOpenerFileEntryReferenceLocalService
 * @generated
 */
public class DLOpenerFileEntryReferenceLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.document.library.opener.service.impl.DLOpenerFileEntryReferenceLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

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
	public static DLOpenerFileEntryReference addDLOpenerFileEntryReference(
		DLOpenerFileEntryReference dlOpenerFileEntryReference) {

		return getService().addDLOpenerFileEntryReference(
			dlOpenerFileEntryReference);
	}

	public static DLOpenerFileEntryReference
			addPlaceholderDLOpenerFileEntryReference(
				long userId, String referenceType,
				com.liferay.portal.kernel.repository.model.FileEntry fileEntry,
				int type)
		throws PortalException {

		return getService().addPlaceholderDLOpenerFileEntryReference(
			userId, referenceType, fileEntry, type);
	}

	/**
	 * Creates a new dl opener file entry reference with the primary key. Does not add the dl opener file entry reference to the database.
	 *
	 * @param dlOpenerFileEntryReferenceId the primary key for the new dl opener file entry reference
	 * @return the new dl opener file entry reference
	 */
	public static DLOpenerFileEntryReference createDLOpenerFileEntryReference(
		long dlOpenerFileEntryReferenceId) {

		return getService().createDLOpenerFileEntryReference(
			dlOpenerFileEntryReferenceId);
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
	 * Deletes the dl opener file entry reference from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLOpenerFileEntryReferenceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlOpenerFileEntryReference the dl opener file entry reference
	 * @return the dl opener file entry reference that was removed
	 */
	public static DLOpenerFileEntryReference deleteDLOpenerFileEntryReference(
		DLOpenerFileEntryReference dlOpenerFileEntryReference) {

		return getService().deleteDLOpenerFileEntryReference(
			dlOpenerFileEntryReference);
	}

	/**
	 * @deprecated As of Mueller (7.2.x), replaced by {@link
	 #deleteDLOpenerFileEntryReference(String, FileEntry)}
	 */
	@Deprecated
	public static void deleteDLOpenerFileEntryReference(
			com.liferay.portal.kernel.repository.model.FileEntry fileEntry)
		throws PortalException {

		getService().deleteDLOpenerFileEntryReference(fileEntry);
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
	public static DLOpenerFileEntryReference deleteDLOpenerFileEntryReference(
			long dlOpenerFileEntryReferenceId)
		throws PortalException {

		return getService().deleteDLOpenerFileEntryReference(
			dlOpenerFileEntryReferenceId);
	}

	public static void deleteDLOpenerFileEntryReference(
			String referenceType,
			com.liferay.portal.kernel.repository.model.FileEntry fileEntry)
		throws PortalException {

		getService().deleteDLOpenerFileEntryReference(referenceType, fileEntry);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.document.library.opener.model.impl.DLOpenerFileEntryReferenceModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.document.library.opener.model.impl.DLOpenerFileEntryReferenceModelImpl</code>.
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

	/**
	 * @deprecated As of Mueller (7.2.x), replaced by {@link
	 #fetchDLOpenerFileEntryReference(String, FileEntry)}
	 */
	@Deprecated
	public static DLOpenerFileEntryReference fetchDLOpenerFileEntryReference(
		com.liferay.portal.kernel.repository.model.FileEntry fileEntry) {

		return getService().fetchDLOpenerFileEntryReference(fileEntry);
	}

	public static DLOpenerFileEntryReference fetchDLOpenerFileEntryReference(
		long dlOpenerFileEntryReferenceId) {

		return getService().fetchDLOpenerFileEntryReference(
			dlOpenerFileEntryReferenceId);
	}

	public static DLOpenerFileEntryReference fetchDLOpenerFileEntryReference(
		String referenceKey,
		com.liferay.portal.kernel.repository.model.FileEntry fileEntry) {

		return getService().fetchDLOpenerFileEntryReference(
			referenceKey, fileEntry);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * @deprecated As of Mueller (7.2.x), replaced by {@link
	 #getDLOpenerFileEntryReference(String, FileEntry)}
	 */
	@Deprecated
	public static DLOpenerFileEntryReference getDLOpenerFileEntryReference(
			com.liferay.portal.kernel.repository.model.FileEntry fileEntry)
		throws PortalException {

		return getService().getDLOpenerFileEntryReference(fileEntry);
	}

	/**
	 * Returns the dl opener file entry reference with the primary key.
	 *
	 * @param dlOpenerFileEntryReferenceId the primary key of the dl opener file entry reference
	 * @return the dl opener file entry reference
	 * @throws PortalException if a dl opener file entry reference with the primary key could not be found
	 */
	public static DLOpenerFileEntryReference getDLOpenerFileEntryReference(
			long dlOpenerFileEntryReferenceId)
		throws PortalException {

		return getService().getDLOpenerFileEntryReference(
			dlOpenerFileEntryReferenceId);
	}

	public static DLOpenerFileEntryReference getDLOpenerFileEntryReference(
			String referenceType,
			com.liferay.portal.kernel.repository.model.FileEntry fileEntry)
		throws PortalException {

		return getService().getDLOpenerFileEntryReference(
			referenceType, fileEntry);
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
	public static List<DLOpenerFileEntryReference>
		getDLOpenerFileEntryReferences(int start, int end) {

		return getService().getDLOpenerFileEntryReferences(start, end);
	}

	/**
	 * Returns the number of dl opener file entry references.
	 *
	 * @return the number of dl opener file entry references
	 */
	public static int getDLOpenerFileEntryReferencesCount() {
		return getService().getDLOpenerFileEntryReferencesCount();
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
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
	 * Updates the dl opener file entry reference in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLOpenerFileEntryReferenceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlOpenerFileEntryReference the dl opener file entry reference
	 * @return the dl opener file entry reference that was updated
	 */
	public static DLOpenerFileEntryReference updateDLOpenerFileEntryReference(
		DLOpenerFileEntryReference dlOpenerFileEntryReference) {

		return getService().updateDLOpenerFileEntryReference(
			dlOpenerFileEntryReference);
	}

	public static DLOpenerFileEntryReference updateDLOpenerFileEntryReference(
		String referenceKey, String referenceType,
		com.liferay.portal.kernel.repository.model.FileEntry fileEntry) {

		return getService().updateDLOpenerFileEntryReference(
			referenceKey, referenceType, fileEntry);
	}

	public static DLOpenerFileEntryReferenceLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<DLOpenerFileEntryReferenceLocalService>
		_serviceSnapshot = new Snapshot<>(
			DLOpenerFileEntryReferenceLocalServiceUtil.class,
			DLOpenerFileEntryReferenceLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-2116853186