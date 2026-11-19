/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CommerceOrderNoteLocalService}.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceOrderNoteLocalService
 * @generated
 */
public class CommerceOrderNoteLocalServiceWrapper
	implements CommerceOrderNoteLocalService,
			   ServiceWrapper<CommerceOrderNoteLocalService> {

	public CommerceOrderNoteLocalServiceWrapper() {
		this(null);
	}

	public CommerceOrderNoteLocalServiceWrapper(
		CommerceOrderNoteLocalService commerceOrderNoteLocalService) {

		_commerceOrderNoteLocalService = commerceOrderNoteLocalService;
	}

	/**
	 * Adds the commerce order note to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceOrderNoteLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceOrderNote the commerce order note
	 * @return the commerce order note that was added
	 */
	@Override
	public com.liferay.commerce.model.CommerceOrderNote addCommerceOrderNote(
		com.liferay.commerce.model.CommerceOrderNote commerceOrderNote) {

		return _commerceOrderNoteLocalService.addCommerceOrderNote(
			commerceOrderNote);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderNote addCommerceOrderNote(
			long commerceOrderId, String content, boolean restricted,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderNoteLocalService.addCommerceOrderNote(
			commerceOrderId, content, restricted, serviceContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderNote addCommerceOrderNote(
			String externalReferenceCode, long commerceOrderId, String content,
			boolean restricted,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderNoteLocalService.addCommerceOrderNote(
			externalReferenceCode, commerceOrderId, content, restricted,
			serviceContext);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderNote
			addOrUpdateCommerceOrderNote(
				String externalReferenceCode, long commerceOrderNoteId,
				long commerceOrderId, String content, boolean restricted,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderNoteLocalService.addOrUpdateCommerceOrderNote(
			externalReferenceCode, commerceOrderNoteId, commerceOrderId,
			content, restricted, serviceContext);
	}

	/**
	 * Creates a new commerce order note with the primary key. Does not add the commerce order note to the database.
	 *
	 * @param commerceOrderNoteId the primary key for the new commerce order note
	 * @return the new commerce order note
	 */
	@Override
	public com.liferay.commerce.model.CommerceOrderNote createCommerceOrderNote(
		long commerceOrderNoteId) {

		return _commerceOrderNoteLocalService.createCommerceOrderNote(
			commerceOrderNoteId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderNoteLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the commerce order note from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceOrderNoteLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceOrderNote the commerce order note
	 * @return the commerce order note that was removed
	 */
	@Override
	public com.liferay.commerce.model.CommerceOrderNote deleteCommerceOrderNote(
		com.liferay.commerce.model.CommerceOrderNote commerceOrderNote) {

		return _commerceOrderNoteLocalService.deleteCommerceOrderNote(
			commerceOrderNote);
	}

	/**
	 * Deletes the commerce order note with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceOrderNoteLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceOrderNoteId the primary key of the commerce order note
	 * @return the commerce order note that was removed
	 * @throws PortalException if a commerce order note with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.model.CommerceOrderNote deleteCommerceOrderNote(
			long commerceOrderNoteId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderNoteLocalService.deleteCommerceOrderNote(
			commerceOrderNoteId);
	}

	@Override
	public void deleteCommerceOrderNotes(long commerceOrderId) {
		_commerceOrderNoteLocalService.deleteCommerceOrderNotes(
			commerceOrderId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderNoteLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commerceOrderNoteLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commerceOrderNoteLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commerceOrderNoteLocalService.dynamicQuery();
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

		return _commerceOrderNoteLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderNoteModelImpl</code>.
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

		return _commerceOrderNoteLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderNoteModelImpl</code>.
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

		return _commerceOrderNoteLocalService.dynamicQuery(
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

		return _commerceOrderNoteLocalService.dynamicQueryCount(dynamicQuery);
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

		return _commerceOrderNoteLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderNote fetchCommerceOrderNote(
		long commerceOrderNoteId) {

		return _commerceOrderNoteLocalService.fetchCommerceOrderNote(
			commerceOrderNoteId);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderNote
		fetchCommerceOrderNoteByExternalReferenceCode(
			String externalReferenceCode, long companyId) {

		return _commerceOrderNoteLocalService.
			fetchCommerceOrderNoteByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce order note matching the UUID and group.
	 *
	 * @param uuid the commerce order note's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce order note, or <code>null</code> if a matching commerce order note could not be found
	 */
	@Override
	public com.liferay.commerce.model.CommerceOrderNote
		fetchCommerceOrderNoteByUuidAndGroupId(String uuid, long groupId) {

		return _commerceOrderNoteLocalService.
			fetchCommerceOrderNoteByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commerceOrderNoteLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce order note with the primary key.
	 *
	 * @param commerceOrderNoteId the primary key of the commerce order note
	 * @return the commerce order note
	 * @throws PortalException if a commerce order note with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.model.CommerceOrderNote getCommerceOrderNote(
			long commerceOrderNoteId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderNoteLocalService.getCommerceOrderNote(
			commerceOrderNoteId);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderNote
			getCommerceOrderNoteByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderNoteLocalService.
			getCommerceOrderNoteByExternalReferenceCode(
				externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce order note matching the UUID and group.
	 *
	 * @param uuid the commerce order note's UUID
	 * @param groupId the primary key of the group
	 * @return the matching commerce order note
	 * @throws PortalException if a matching commerce order note could not be found
	 */
	@Override
	public com.liferay.commerce.model.CommerceOrderNote
			getCommerceOrderNoteByUuidAndGroupId(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderNoteLocalService.
			getCommerceOrderNoteByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the commerce order notes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceOrderNoteModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce order notes
	 * @param end the upper bound of the range of commerce order notes (not inclusive)
	 * @return the range of commerce order notes
	 */
	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrderNote>
		getCommerceOrderNotes(int start, int end) {

		return _commerceOrderNoteLocalService.getCommerceOrderNotes(start, end);
	}

	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrderNote>
		getCommerceOrderNotes(long commerceOrderId, boolean restricted) {

		return _commerceOrderNoteLocalService.getCommerceOrderNotes(
			commerceOrderId, restricted);
	}

	/**
	 * Returns all the commerce order notes matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce order notes
	 * @param companyId the primary key of the company
	 * @return the matching commerce order notes, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrderNote>
		getCommerceOrderNotesByUuidAndCompanyId(String uuid, long companyId) {

		return _commerceOrderNoteLocalService.
			getCommerceOrderNotesByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of commerce order notes matching the UUID and company.
	 *
	 * @param uuid the UUID of the commerce order notes
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of commerce order notes
	 * @param end the upper bound of the range of commerce order notes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching commerce order notes, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<com.liferay.commerce.model.CommerceOrderNote>
		getCommerceOrderNotesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.commerce.model.CommerceOrderNote>
					orderByComparator) {

		return _commerceOrderNoteLocalService.
			getCommerceOrderNotesByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of commerce order notes.
	 *
	 * @return the number of commerce order notes
	 */
	@Override
	public int getCommerceOrderNotesCount() {
		return _commerceOrderNoteLocalService.getCommerceOrderNotesCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _commerceOrderNoteLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commerceOrderNoteLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceOrderNoteLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderNoteLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the commerce order note in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceOrderNoteLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceOrderNote the commerce order note
	 * @return the commerce order note that was updated
	 */
	@Override
	public com.liferay.commerce.model.CommerceOrderNote updateCommerceOrderNote(
		com.liferay.commerce.model.CommerceOrderNote commerceOrderNote) {

		return _commerceOrderNoteLocalService.updateCommerceOrderNote(
			commerceOrderNote);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderNote updateCommerceOrderNote(
			long commerceOrderNoteId, String content, boolean restricted)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderNoteLocalService.updateCommerceOrderNote(
			commerceOrderNoteId, content, restricted);
	}

	@Override
	public com.liferay.commerce.model.CommerceOrderNote updateCommerceOrderNote(
			String externalReferenceCode, long commerceOrderNoteId,
			String content, boolean restricted)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceOrderNoteLocalService.updateCommerceOrderNote(
			externalReferenceCode, commerceOrderNoteId, content, restricted);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commerceOrderNoteLocalService.getBasePersistence();
	}

	@Override
	public CommerceOrderNoteLocalService getWrappedService() {
		return _commerceOrderNoteLocalService;
	}

	@Override
	public void setWrappedService(
		CommerceOrderNoteLocalService commerceOrderNoteLocalService) {

		_commerceOrderNoteLocalService = commerceOrderNoteLocalService;
	}

	private CommerceOrderNoteLocalService _commerceOrderNoteLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-189842846