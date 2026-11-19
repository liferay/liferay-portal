/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.service;

import com.liferay.document.library.model.DLFileVersionPreview;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link DLFileVersionPreviewLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see DLFileVersionPreviewLocalService
 * @generated
 */
public class DLFileVersionPreviewLocalServiceWrapper
	implements DLFileVersionPreviewLocalService,
			   ServiceWrapper<DLFileVersionPreviewLocalService> {

	public DLFileVersionPreviewLocalServiceWrapper() {
		this(null);
	}

	public DLFileVersionPreviewLocalServiceWrapper(
		DLFileVersionPreviewLocalService dlFileVersionPreviewLocalService) {

		_dlFileVersionPreviewLocalService = dlFileVersionPreviewLocalService;
	}

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
	@Override
	public DLFileVersionPreview addDLFileVersionPreview(
		DLFileVersionPreview dlFileVersionPreview) {

		return _dlFileVersionPreviewLocalService.addDLFileVersionPreview(
			dlFileVersionPreview);
	}

	@Override
	public void addDLFileVersionPreview(
			long fileEntryId, long fileVersionId, int previewStatus)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileVersionPreviewLocalService.addDLFileVersionPreview(
			fileEntryId, fileVersionId, previewStatus);
	}

	/**
	 * Creates a new dl file version preview with the primary key. Does not add the dl file version preview to the database.
	 *
	 * @param dlFileVersionPreviewId the primary key for the new dl file version preview
	 * @return the new dl file version preview
	 */
	@Override
	public DLFileVersionPreview createDLFileVersionPreview(
		long dlFileVersionPreviewId) {

		return _dlFileVersionPreviewLocalService.createDLFileVersionPreview(
			dlFileVersionPreviewId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileVersionPreviewLocalService.createPersistedModel(
			primaryKeyObj);
	}

	@Override
	public void deleteDLFileEntryFileVersionPreviews(long fileEntryId) {
		_dlFileVersionPreviewLocalService.deleteDLFileEntryFileVersionPreviews(
			fileEntryId);
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
	@Override
	public DLFileVersionPreview deleteDLFileVersionPreview(
		DLFileVersionPreview dlFileVersionPreview) {

		return _dlFileVersionPreviewLocalService.deleteDLFileVersionPreview(
			dlFileVersionPreview);
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
	@Override
	public DLFileVersionPreview deleteDLFileVersionPreview(
			long dlFileVersionPreviewId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileVersionPreviewLocalService.deleteDLFileVersionPreview(
			dlFileVersionPreviewId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileVersionPreviewLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _dlFileVersionPreviewLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _dlFileVersionPreviewLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _dlFileVersionPreviewLocalService.dynamicQuery();
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

		return _dlFileVersionPreviewLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _dlFileVersionPreviewLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _dlFileVersionPreviewLocalService.dynamicQuery(
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

		return _dlFileVersionPreviewLocalService.dynamicQueryCount(
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

		return _dlFileVersionPreviewLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public DLFileVersionPreview fetchDLFileVersionPreview(
		long dlFileVersionPreviewId) {

		return _dlFileVersionPreviewLocalService.fetchDLFileVersionPreview(
			dlFileVersionPreviewId);
	}

	@Override
	public DLFileVersionPreview fetchDLFileVersionPreview(
		long fileEntryId, long fileVersionId) {

		return _dlFileVersionPreviewLocalService.fetchDLFileVersionPreview(
			fileEntryId, fileVersionId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _dlFileVersionPreviewLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the dl file version preview with the primary key.
	 *
	 * @param dlFileVersionPreviewId the primary key of the dl file version preview
	 * @return the dl file version preview
	 * @throws PortalException if a dl file version preview with the primary key could not be found
	 */
	@Override
	public DLFileVersionPreview getDLFileVersionPreview(
			long dlFileVersionPreviewId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileVersionPreviewLocalService.getDLFileVersionPreview(
			dlFileVersionPreviewId);
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
	@Override
	public java.util.List<DLFileVersionPreview> getDLFileVersionPreviews(
		int start, int end) {

		return _dlFileVersionPreviewLocalService.getDLFileVersionPreviews(
			start, end);
	}

	/**
	 * Returns the number of dl file version previews.
	 *
	 * @return the number of dl file version previews
	 */
	@Override
	public int getDLFileVersionPreviewsCount() {
		return _dlFileVersionPreviewLocalService.
			getDLFileVersionPreviewsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _dlFileVersionPreviewLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _dlFileVersionPreviewLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileVersionPreviewLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public boolean hasDLFileVersionPreview(
		long fileEntryId, long fileVersionId, int previewStatus) {

		return _dlFileVersionPreviewLocalService.hasDLFileVersionPreview(
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
	@Override
	public DLFileVersionPreview updateDLFileVersionPreview(
		DLFileVersionPreview dlFileVersionPreview) {

		return _dlFileVersionPreviewLocalService.updateDLFileVersionPreview(
			dlFileVersionPreview);
	}

	@Override
	public void updateDLFileVersionPreview(
			long dlFileVersionPreviewId, int previewStatus)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileVersionPreviewLocalService.updateDLFileVersionPreview(
			dlFileVersionPreviewId, previewStatus);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _dlFileVersionPreviewLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<DLFileVersionPreview> getCTPersistence() {
		return _dlFileVersionPreviewLocalService.getCTPersistence();
	}

	@Override
	public Class<DLFileVersionPreview> getModelClass() {
		return _dlFileVersionPreviewLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<DLFileVersionPreview>, R, E>
				updateUnsafeFunction)
		throws E {

		return _dlFileVersionPreviewLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public DLFileVersionPreviewLocalService getWrappedService() {
		return _dlFileVersionPreviewLocalService;
	}

	@Override
	public void setWrappedService(
		DLFileVersionPreviewLocalService dlFileVersionPreviewLocalService) {

		_dlFileVersionPreviewLocalService = dlFileVersionPreviewLocalService;
	}

	private DLFileVersionPreviewLocalService _dlFileVersionPreviewLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-728275339