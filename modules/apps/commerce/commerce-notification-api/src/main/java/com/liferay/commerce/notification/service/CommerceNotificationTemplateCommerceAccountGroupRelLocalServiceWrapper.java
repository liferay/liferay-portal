/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.notification.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CommerceNotificationTemplateCommerceAccountGroupRelLocalService}.
 *
 * @author Alessio Antonio Rendina
 * @see CommerceNotificationTemplateCommerceAccountGroupRelLocalService
 * @deprecated As of Cavanaugh (7.4.x)
 * @generated
 */
@Deprecated
public class
	CommerceNotificationTemplateCommerceAccountGroupRelLocalServiceWrapper
		implements CommerceNotificationTemplateCommerceAccountGroupRelLocalService,
				   ServiceWrapper
					   <CommerceNotificationTemplateCommerceAccountGroupRelLocalService> {

	public CommerceNotificationTemplateCommerceAccountGroupRelLocalServiceWrapper() {
		this(null);
	}

	public CommerceNotificationTemplateCommerceAccountGroupRelLocalServiceWrapper(
		CommerceNotificationTemplateCommerceAccountGroupRelLocalService
			commerceNotificationTemplateCommerceAccountGroupRelLocalService) {

		_commerceNotificationTemplateCommerceAccountGroupRelLocalService =
			commerceNotificationTemplateCommerceAccountGroupRelLocalService;
	}

	/**
	 * Adds the commerce notification template commerce account group rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceNotificationTemplateCommerceAccountGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceNotificationTemplateCommerceAccountGroupRel the commerce notification template commerce account group rel
	 * @return the commerce notification template commerce account group rel that was added
	 */
	@Override
	public com.liferay.commerce.notification.model.
		CommerceNotificationTemplateCommerceAccountGroupRel
			addCommerceNotificationTemplateCommerceAccountGroupRel(
				com.liferay.commerce.notification.model.
					CommerceNotificationTemplateCommerceAccountGroupRel
						commerceNotificationTemplateCommerceAccountGroupRel) {

		return _commerceNotificationTemplateCommerceAccountGroupRelLocalService.
			addCommerceNotificationTemplateCommerceAccountGroupRel(
				commerceNotificationTemplateCommerceAccountGroupRel);
	}

	@Override
	public com.liferay.commerce.notification.model.
		CommerceNotificationTemplateCommerceAccountGroupRel
				addCommerceNotificationTemplateCommerceAccountGroupRel(
					long commerceNotificationTemplateId,
					long commerceAccountGroupId,
					com.liferay.portal.kernel.service.ServiceContext
						serviceContext)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceNotificationTemplateCommerceAccountGroupRelLocalService.
			addCommerceNotificationTemplateCommerceAccountGroupRel(
				commerceNotificationTemplateId, commerceAccountGroupId,
				serviceContext);
	}

	/**
	 * Creates a new commerce notification template commerce account group rel with the primary key. Does not add the commerce notification template commerce account group rel to the database.
	 *
	 * @param commerceNotificationTemplateCommerceAccountGroupRelId the primary key for the new commerce notification template commerce account group rel
	 * @return the new commerce notification template commerce account group rel
	 */
	@Override
	public com.liferay.commerce.notification.model.
		CommerceNotificationTemplateCommerceAccountGroupRel
			createCommerceNotificationTemplateCommerceAccountGroupRel(
				long commerceNotificationTemplateCommerceAccountGroupRelId) {

		return _commerceNotificationTemplateCommerceAccountGroupRelLocalService.
			createCommerceNotificationTemplateCommerceAccountGroupRel(
				commerceNotificationTemplateCommerceAccountGroupRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceNotificationTemplateCommerceAccountGroupRelLocalService.
			createPersistedModel(primaryKeyObj);
	}

	@Override
	public void
		deleteCNTemplateCommerceAccountGroupRelsByCommerceNotificationTemplateId(
			long commerceNotificationTemplateId) {

		_commerceNotificationTemplateCommerceAccountGroupRelLocalService.
			deleteCNTemplateCommerceAccountGroupRelsByCommerceNotificationTemplateId(
				commerceNotificationTemplateId);
	}

	/**
	 * Deletes the commerce notification template commerce account group rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceNotificationTemplateCommerceAccountGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceNotificationTemplateCommerceAccountGroupRel the commerce notification template commerce account group rel
	 * @return the commerce notification template commerce account group rel that was removed
	 */
	@Override
	public com.liferay.commerce.notification.model.
		CommerceNotificationTemplateCommerceAccountGroupRel
			deleteCommerceNotificationTemplateCommerceAccountGroupRel(
				com.liferay.commerce.notification.model.
					CommerceNotificationTemplateCommerceAccountGroupRel
						commerceNotificationTemplateCommerceAccountGroupRel) {

		return _commerceNotificationTemplateCommerceAccountGroupRelLocalService.
			deleteCommerceNotificationTemplateCommerceAccountGroupRel(
				commerceNotificationTemplateCommerceAccountGroupRel);
	}

	/**
	 * Deletes the commerce notification template commerce account group rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceNotificationTemplateCommerceAccountGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceNotificationTemplateCommerceAccountGroupRelId the primary key of the commerce notification template commerce account group rel
	 * @return the commerce notification template commerce account group rel that was removed
	 * @throws PortalException if a commerce notification template commerce account group rel with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.notification.model.
		CommerceNotificationTemplateCommerceAccountGroupRel
				deleteCommerceNotificationTemplateCommerceAccountGroupRel(
					long commerceNotificationTemplateCommerceAccountGroupRelId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceNotificationTemplateCommerceAccountGroupRelLocalService.
			deleteCommerceNotificationTemplateCommerceAccountGroupRel(
				commerceNotificationTemplateCommerceAccountGroupRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceNotificationTemplateCommerceAccountGroupRelLocalService.
			deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commerceNotificationTemplateCommerceAccountGroupRelLocalService.
			dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commerceNotificationTemplateCommerceAccountGroupRelLocalService.
			dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commerceNotificationTemplateCommerceAccountGroupRelLocalService.
			dynamicQuery();
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

		return _commerceNotificationTemplateCommerceAccountGroupRelLocalService.
			dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.notification.model.impl.CommerceNotificationTemplateCommerceAccountGroupRelModelImpl</code>.
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

		return _commerceNotificationTemplateCommerceAccountGroupRelLocalService.
			dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.notification.model.impl.CommerceNotificationTemplateCommerceAccountGroupRelModelImpl</code>.
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

		return _commerceNotificationTemplateCommerceAccountGroupRelLocalService.
			dynamicQuery(dynamicQuery, start, end, orderByComparator);
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

		return _commerceNotificationTemplateCommerceAccountGroupRelLocalService.
			dynamicQueryCount(dynamicQuery);
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

		return _commerceNotificationTemplateCommerceAccountGroupRelLocalService.
			dynamicQueryCount(dynamicQuery, projection);
	}

	@Override
	public com.liferay.commerce.notification.model.
		CommerceNotificationTemplateCommerceAccountGroupRel
			fetchCommerceNotificationTemplateCommerceAccountGroupRel(
				long commerceNotificationTemplateCommerceAccountGroupRelId) {

		return _commerceNotificationTemplateCommerceAccountGroupRelLocalService.
			fetchCommerceNotificationTemplateCommerceAccountGroupRel(
				commerceNotificationTemplateCommerceAccountGroupRelId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commerceNotificationTemplateCommerceAccountGroupRelLocalService.
			getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce notification template commerce account group rel with the primary key.
	 *
	 * @param commerceNotificationTemplateCommerceAccountGroupRelId the primary key of the commerce notification template commerce account group rel
	 * @return the commerce notification template commerce account group rel
	 * @throws PortalException if a commerce notification template commerce account group rel with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.notification.model.
		CommerceNotificationTemplateCommerceAccountGroupRel
				getCommerceNotificationTemplateCommerceAccountGroupRel(
					long commerceNotificationTemplateCommerceAccountGroupRelId)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceNotificationTemplateCommerceAccountGroupRelLocalService.
			getCommerceNotificationTemplateCommerceAccountGroupRel(
				commerceNotificationTemplateCommerceAccountGroupRelId);
	}

	/**
	 * Returns a range of all the commerce notification template commerce account group rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.notification.model.impl.CommerceNotificationTemplateCommerceAccountGroupRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce notification template commerce account group rels
	 * @param end the upper bound of the range of commerce notification template commerce account group rels (not inclusive)
	 * @return the range of commerce notification template commerce account group rels
	 */
	@Override
	public java.util.List
		<com.liferay.commerce.notification.model.
			CommerceNotificationTemplateCommerceAccountGroupRel>
				getCommerceNotificationTemplateCommerceAccountGroupRels(
					int start, int end) {

		return _commerceNotificationTemplateCommerceAccountGroupRelLocalService.
			getCommerceNotificationTemplateCommerceAccountGroupRels(start, end);
	}

	/**
	 * Returns the number of commerce notification template commerce account group rels.
	 *
	 * @return the number of commerce notification template commerce account group rels
	 */
	@Override
	public int getCommerceNotificationTemplateCommerceAccountGroupRelsCount() {
		return _commerceNotificationTemplateCommerceAccountGroupRelLocalService.
			getCommerceNotificationTemplateCommerceAccountGroupRelsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commerceNotificationTemplateCommerceAccountGroupRelLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceNotificationTemplateCommerceAccountGroupRelLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceNotificationTemplateCommerceAccountGroupRelLocalService.
			getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the commerce notification template commerce account group rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceNotificationTemplateCommerceAccountGroupRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceNotificationTemplateCommerceAccountGroupRel the commerce notification template commerce account group rel
	 * @return the commerce notification template commerce account group rel that was updated
	 */
	@Override
	public com.liferay.commerce.notification.model.
		CommerceNotificationTemplateCommerceAccountGroupRel
			updateCommerceNotificationTemplateCommerceAccountGroupRel(
				com.liferay.commerce.notification.model.
					CommerceNotificationTemplateCommerceAccountGroupRel
						commerceNotificationTemplateCommerceAccountGroupRel) {

		return _commerceNotificationTemplateCommerceAccountGroupRelLocalService.
			updateCommerceNotificationTemplateCommerceAccountGroupRel(
				commerceNotificationTemplateCommerceAccountGroupRel);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commerceNotificationTemplateCommerceAccountGroupRelLocalService.
			getBasePersistence();
	}

	@Override
	public CommerceNotificationTemplateCommerceAccountGroupRelLocalService
		getWrappedService() {

		return _commerceNotificationTemplateCommerceAccountGroupRelLocalService;
	}

	@Override
	public void setWrappedService(
		CommerceNotificationTemplateCommerceAccountGroupRelLocalService
			commerceNotificationTemplateCommerceAccountGroupRelLocalService) {

		_commerceNotificationTemplateCommerceAccountGroupRelLocalService =
			commerceNotificationTemplateCommerceAccountGroupRelLocalService;
	}

	private CommerceNotificationTemplateCommerceAccountGroupRelLocalService
		_commerceNotificationTemplateCommerceAccountGroupRelLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1012912460