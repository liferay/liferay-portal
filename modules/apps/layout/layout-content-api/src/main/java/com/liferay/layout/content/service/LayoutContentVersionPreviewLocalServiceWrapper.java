/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link LayoutContentVersionPreviewLocalService}.
 *
 * @author Lourdes Fernández Besada
 * @see LayoutContentVersionPreviewLocalService
 * @generated
 */
public class LayoutContentVersionPreviewLocalServiceWrapper
	implements LayoutContentVersionPreviewLocalService,
			   ServiceWrapper<LayoutContentVersionPreviewLocalService> {

	public LayoutContentVersionPreviewLocalServiceWrapper() {
		this(null);
	}

	public LayoutContentVersionPreviewLocalServiceWrapper(
		LayoutContentVersionPreviewLocalService
			layoutContentVersionPreviewLocalService) {

		_layoutContentVersionPreviewLocalService =
			layoutContentVersionPreviewLocalService;
	}

	/**
	 * Adds the layout content version preview to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutContentVersionPreviewLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutContentVersionPreview the layout content version preview
	 * @return the layout content version preview that was added
	 */
	@Override
	public com.liferay.layout.content.model.LayoutContentVersionPreview
		addLayoutContentVersionPreview(
			com.liferay.layout.content.model.LayoutContentVersionPreview
				layoutContentVersionPreview) {

		return _layoutContentVersionPreviewLocalService.
			addLayoutContentVersionPreview(layoutContentVersionPreview);
	}

	@Override
	public com.liferay.layout.content.model.LayoutContentVersionPreview
			addLayoutContentVersionPreview(
				long userId, long layoutContentVersionId, String html,
				String languageId, String segmentsExperienceERC)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutContentVersionPreviewLocalService.
			addLayoutContentVersionPreview(
				userId, layoutContentVersionId, html, languageId,
				segmentsExperienceERC);
	}

	/**
	 * Creates a new layout content version preview with the primary key. Does not add the layout content version preview to the database.
	 *
	 * @param layoutContentVersionPreviewId the primary key for the new layout content version preview
	 * @return the new layout content version preview
	 */
	@Override
	public com.liferay.layout.content.model.LayoutContentVersionPreview
		createLayoutContentVersionPreview(long layoutContentVersionPreviewId) {

		return _layoutContentVersionPreviewLocalService.
			createLayoutContentVersionPreview(layoutContentVersionPreviewId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutContentVersionPreviewLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the layout content version preview from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutContentVersionPreviewLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutContentVersionPreview the layout content version preview
	 * @return the layout content version preview that was removed
	 */
	@Override
	public com.liferay.layout.content.model.LayoutContentVersionPreview
		deleteLayoutContentVersionPreview(
			com.liferay.layout.content.model.LayoutContentVersionPreview
				layoutContentVersionPreview) {

		return _layoutContentVersionPreviewLocalService.
			deleteLayoutContentVersionPreview(layoutContentVersionPreview);
	}

	/**
	 * Deletes the layout content version preview with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutContentVersionPreviewLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutContentVersionPreviewId the primary key of the layout content version preview
	 * @return the layout content version preview that was removed
	 * @throws PortalException if a layout content version preview with the primary key could not be found
	 */
	@Override
	public com.liferay.layout.content.model.LayoutContentVersionPreview
			deleteLayoutContentVersionPreview(
				long layoutContentVersionPreviewId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutContentVersionPreviewLocalService.
			deleteLayoutContentVersionPreview(layoutContentVersionPreviewId);
	}

	@Override
	public void deleteLayoutContentVersionPreviews(long layoutContentVersionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_layoutContentVersionPreviewLocalService.
			deleteLayoutContentVersionPreviews(layoutContentVersionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutContentVersionPreviewLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _layoutContentVersionPreviewLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _layoutContentVersionPreviewLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _layoutContentVersionPreviewLocalService.dynamicQuery();
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

		return _layoutContentVersionPreviewLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.content.model.impl.LayoutContentVersionPreviewModelImpl</code>.
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

		return _layoutContentVersionPreviewLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.content.model.impl.LayoutContentVersionPreviewModelImpl</code>.
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

		return _layoutContentVersionPreviewLocalService.dynamicQuery(
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

		return _layoutContentVersionPreviewLocalService.dynamicQueryCount(
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

		return _layoutContentVersionPreviewLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.layout.content.model.LayoutContentVersionPreview
		fetchLayoutContentVersionPreview(long layoutContentVersionPreviewId) {

		return _layoutContentVersionPreviewLocalService.
			fetchLayoutContentVersionPreview(layoutContentVersionPreviewId);
	}

	@Override
	public com.liferay.layout.content.model.LayoutContentVersionPreview
			fetchLayoutContentVersionPreview(
				long layoutContentVersionId, String languageId,
				String segmentsExperienceERC)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutContentVersionPreviewLocalService.
			fetchLayoutContentVersionPreview(
				layoutContentVersionId, languageId, segmentsExperienceERC);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _layoutContentVersionPreviewLocalService.
			getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _layoutContentVersionPreviewLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the layout content version preview with the primary key.
	 *
	 * @param layoutContentVersionPreviewId the primary key of the layout content version preview
	 * @return the layout content version preview
	 * @throws PortalException if a layout content version preview with the primary key could not be found
	 */
	@Override
	public com.liferay.layout.content.model.LayoutContentVersionPreview
			getLayoutContentVersionPreview(long layoutContentVersionPreviewId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutContentVersionPreviewLocalService.
			getLayoutContentVersionPreview(layoutContentVersionPreviewId);
	}

	/**
	 * Returns a range of all the layout content version previews.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.content.model.impl.LayoutContentVersionPreviewModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout content version previews
	 * @param end the upper bound of the range of layout content version previews (not inclusive)
	 * @return the range of layout content version previews
	 */
	@Override
	public java.util.List
		<com.liferay.layout.content.model.LayoutContentVersionPreview>
			getLayoutContentVersionPreviews(int start, int end) {

		return _layoutContentVersionPreviewLocalService.
			getLayoutContentVersionPreviews(start, end);
	}

	@Override
	public java.util.List
		<com.liferay.layout.content.model.LayoutContentVersionPreview>
				getLayoutContentVersionPreviews(long layoutContentVersionId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutContentVersionPreviewLocalService.
			getLayoutContentVersionPreviews(layoutContentVersionId);
	}

	/**
	 * Returns the number of layout content version previews.
	 *
	 * @return the number of layout content version previews
	 */
	@Override
	public int getLayoutContentVersionPreviewsCount() {
		return _layoutContentVersionPreviewLocalService.
			getLayoutContentVersionPreviewsCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _layoutContentVersionPreviewLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutContentVersionPreviewLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public java.util.Map<String, java.util.List<String>>
			getSegmentsExperienceERCsLanguageIds(long layoutContentVersionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutContentVersionPreviewLocalService.
			getSegmentsExperienceERCsLanguageIds(layoutContentVersionId);
	}

	/**
	 * Updates the layout content version preview in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LayoutContentVersionPreviewLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param layoutContentVersionPreview the layout content version preview
	 * @return the layout content version preview that was updated
	 */
	@Override
	public com.liferay.layout.content.model.LayoutContentVersionPreview
		updateLayoutContentVersionPreview(
			com.liferay.layout.content.model.LayoutContentVersionPreview
				layoutContentVersionPreview) {

		return _layoutContentVersionPreviewLocalService.
			updateLayoutContentVersionPreview(layoutContentVersionPreview);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _layoutContentVersionPreviewLocalService.getBasePersistence();
	}

	@Override
	public LayoutContentVersionPreviewLocalService getWrappedService() {
		return _layoutContentVersionPreviewLocalService;
	}

	@Override
	public void setWrappedService(
		LayoutContentVersionPreviewLocalService
			layoutContentVersionPreviewLocalService) {

		_layoutContentVersionPreviewLocalService =
			layoutContentVersionPreviewLocalService;
	}

	private LayoutContentVersionPreviewLocalService
		_layoutContentVersionPreviewLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1528943148