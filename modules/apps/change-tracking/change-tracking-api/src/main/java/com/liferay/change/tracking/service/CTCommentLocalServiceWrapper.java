/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CTCommentLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see CTCommentLocalService
 * @generated
 */
public class CTCommentLocalServiceWrapper
	implements CTCommentLocalService, ServiceWrapper<CTCommentLocalService> {

	public CTCommentLocalServiceWrapper() {
		this(null);
	}

	public CTCommentLocalServiceWrapper(
		CTCommentLocalService ctCommentLocalService) {

		_ctCommentLocalService = ctCommentLocalService;
	}

	/**
	 * Adds the ct comment to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTCommentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctComment the ct comment
	 * @return the ct comment that was added
	 */
	@Override
	public com.liferay.change.tracking.model.CTComment addCTComment(
		com.liferay.change.tracking.model.CTComment ctComment) {

		return _ctCommentLocalService.addCTComment(ctComment);
	}

	@Override
	public com.liferay.change.tracking.model.CTComment addCTComment(
			long userId, long ctCollectionId, long ctEntryId, String value)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctCommentLocalService.addCTComment(
			userId, ctCollectionId, ctEntryId, value);
	}

	/**
	 * Creates a new ct comment with the primary key. Does not add the ct comment to the database.
	 *
	 * @param ctCommentId the primary key for the new ct comment
	 * @return the new ct comment
	 */
	@Override
	public com.liferay.change.tracking.model.CTComment createCTComment(
		long ctCommentId) {

		return _ctCommentLocalService.createCTComment(ctCommentId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctCommentLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the ct comment from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTCommentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctComment the ct comment
	 * @return the ct comment that was removed
	 */
	@Override
	public com.liferay.change.tracking.model.CTComment deleteCTComment(
		com.liferay.change.tracking.model.CTComment ctComment) {

		return _ctCommentLocalService.deleteCTComment(ctComment);
	}

	/**
	 * Deletes the ct comment with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTCommentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctCommentId the primary key of the ct comment
	 * @return the ct comment that was removed
	 * @throws PortalException if a ct comment with the primary key could not be found
	 */
	@Override
	public com.liferay.change.tracking.model.CTComment deleteCTComment(
			long ctCommentId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctCommentLocalService.deleteCTComment(ctCommentId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctCommentLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _ctCommentLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _ctCommentLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _ctCommentLocalService.dynamicQuery();
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

		return _ctCommentLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTCommentModelImpl</code>.
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

		return _ctCommentLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTCommentModelImpl</code>.
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

		return _ctCommentLocalService.dynamicQuery(
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

		return _ctCommentLocalService.dynamicQueryCount(dynamicQuery);
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

		return _ctCommentLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.change.tracking.model.CTComment fetchCTComment(
		long ctCommentId) {

		return _ctCommentLocalService.fetchCTComment(ctCommentId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _ctCommentLocalService.getActionableDynamicQuery();
	}

	@Override
	public java.util.Map
		<Long, java.util.List<com.liferay.change.tracking.model.CTComment>>
			getCTCollectionCTComments(long ctCollectionId) {

		return _ctCommentLocalService.getCTCollectionCTComments(ctCollectionId);
	}

	/**
	 * Returns the ct comment with the primary key.
	 *
	 * @param ctCommentId the primary key of the ct comment
	 * @return the ct comment
	 * @throws PortalException if a ct comment with the primary key could not be found
	 */
	@Override
	public com.liferay.change.tracking.model.CTComment getCTComment(
			long ctCommentId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctCommentLocalService.getCTComment(ctCommentId);
	}

	/**
	 * Returns a range of all the ct comments.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTCommentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ct comments
	 * @param end the upper bound of the range of ct comments (not inclusive)
	 * @return the range of ct comments
	 */
	@Override
	public java.util.List<com.liferay.change.tracking.model.CTComment>
		getCTComments(int start, int end) {

		return _ctCommentLocalService.getCTComments(start, end);
	}

	/**
	 * Returns the number of ct comments.
	 *
	 * @return the number of ct comments
	 */
	@Override
	public int getCTCommentsCount() {
		return _ctCommentLocalService.getCTCommentsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _ctCommentLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _ctCommentLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctCommentLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the ct comment in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTCommentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctComment the ct comment
	 * @return the ct comment that was updated
	 */
	@Override
	public com.liferay.change.tracking.model.CTComment updateCTComment(
		com.liferay.change.tracking.model.CTComment ctComment) {

		return _ctCommentLocalService.updateCTComment(ctComment);
	}

	@Override
	public com.liferay.change.tracking.model.CTComment updateCTComment(
			long ctCommentId, String value)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctCommentLocalService.updateCTComment(ctCommentId, value);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _ctCommentLocalService.getBasePersistence();
	}

	@Override
	public CTCommentLocalService getWrappedService() {
		return _ctCommentLocalService;
	}

	@Override
	public void setWrappedService(CTCommentLocalService ctCommentLocalService) {
		_ctCommentLocalService = ctCommentLocalService;
	}

	private CTCommentLocalService _ctCommentLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1375467932