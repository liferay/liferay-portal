/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.service;

import com.liferay.document.library.model.DLFileVersionPreview;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for DLFileVersionPreview. This utility wraps
 * <code>com.liferay.document.library.service.impl.DLFileVersionPreviewLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see DLFileVersionPreviewLocalService
 * @generated
 */
public class DLFileVersionPreviewLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.document.library.service.impl.DLFileVersionPreviewLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the dl file version preview to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLFileVersionPreviewLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlFileVersionPreview the dl file version preview
	 * @return the dl file version preview that was added
	 */
	public static DLFileVersionPreview addDLFileVersionPreview(
		DLFileVersionPreview dlFileVersionPreview) {

		return getService().addDLFileVersionPreview(dlFileVersionPreview);
	}

	public static void addDLFileVersionPreview(
			long fileEntryId, long fileVersionId, int previewStatus)
		throws PortalException {

		getService().addDLFileVersionPreview(
			fileEntryId, fileVersionId, previewStatus);
	}

	/**
	 * Creates a new dl file version preview with the primary key. Does not add the dl file version preview to the database.
	 *
	 * @param dlFileVersionPreviewId the primary key for the new dl file version preview
	 * @return the new dl file version preview
	 */
	public static DLFileVersionPreview createDLFileVersionPreview(
		long dlFileVersionPreviewId) {

		return getService().createDLFileVersionPreview(dlFileVersionPreviewId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteDLFileEntryFileVersionPreviews(long fileEntryId) {
		getService().deleteDLFileEntryFileVersionPreviews(fileEntryId);
	}

	/**
	 * Deletes the dl file version preview from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLFileVersionPreviewLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlFileVersionPreview the dl file version preview
	 * @return the dl file version preview that was removed
	 */
	public static DLFileVersionPreview deleteDLFileVersionPreview(
		DLFileVersionPreview dlFileVersionPreview) {

		return getService().deleteDLFileVersionPreview(dlFileVersionPreview);
	}

	/**
	 * Deletes the dl file version preview with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLFileVersionPreviewLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlFileVersionPreviewId the primary key of the dl file version preview
	 * @return the dl file version preview that was removed
	 * @throws PortalException if a dl file version preview with the primary key could not be found
	 */
	public static DLFileVersionPreview deleteDLFileVersionPreview(
			long dlFileVersionPreviewId)
		throws PortalException {

		return getService().deleteDLFileVersionPreview(dlFileVersionPreviewId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.document.library.model.impl.DLFileVersionPreviewModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.document.library.model.impl.DLFileVersionPreviewModelImpl</code>.
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

	public static DLFileVersionPreview fetchDLFileVersionPreview(
		long dlFileVersionPreviewId) {

		return getService().fetchDLFileVersionPreview(dlFileVersionPreviewId);
	}

	public static DLFileVersionPreview fetchDLFileVersionPreview(
		long fileEntryId, long fileVersionId) {

		return getService().fetchDLFileVersionPreview(
			fileEntryId, fileVersionId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the dl file version preview with the primary key.
	 *
	 * @param dlFileVersionPreviewId the primary key of the dl file version preview
	 * @return the dl file version preview
	 * @throws PortalException if a dl file version preview with the primary key could not be found
	 */
	public static DLFileVersionPreview getDLFileVersionPreview(
			long dlFileVersionPreviewId)
		throws PortalException {

		return getService().getDLFileVersionPreview(dlFileVersionPreviewId);
	}

	/**
	 * Returns a range of all the dl file version previews.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.document.library.model.impl.DLFileVersionPreviewModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of dl file version previews
	 * @param end the upper bound of the range of dl file version previews (not inclusive)
	 * @return the range of dl file version previews
	 */
	public static List<DLFileVersionPreview> getDLFileVersionPreviews(
		int start, int end) {

		return getService().getDLFileVersionPreviews(start, end);
	}

	/**
	 * Returns the number of dl file version previews.
	 *
	 * @return the number of dl file version previews
	 */
	public static int getDLFileVersionPreviewsCount() {
		return getService().getDLFileVersionPreviewsCount();
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

	public static boolean hasDLFileVersionPreview(
		long fileEntryId, long fileVersionId, int previewStatus) {

		return getService().hasDLFileVersionPreview(
			fileEntryId, fileVersionId, previewStatus);
	}

	/**
	 * Updates the dl file version preview in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLFileVersionPreviewLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlFileVersionPreview the dl file version preview
	 * @return the dl file version preview that was updated
	 */
	public static DLFileVersionPreview updateDLFileVersionPreview(
		DLFileVersionPreview dlFileVersionPreview) {

		return getService().updateDLFileVersionPreview(dlFileVersionPreview);
	}

	public static void updateDLFileVersionPreview(
			long dlFileVersionPreviewId, int previewStatus)
		throws PortalException {

		getService().updateDLFileVersionPreview(
			dlFileVersionPreviewId, previewStatus);
	}

	public static DLFileVersionPreviewLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<DLFileVersionPreviewLocalService>
		_serviceSnapshot = new Snapshot<>(
			DLFileVersionPreviewLocalServiceUtil.class,
			DLFileVersionPreviewLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1985113166