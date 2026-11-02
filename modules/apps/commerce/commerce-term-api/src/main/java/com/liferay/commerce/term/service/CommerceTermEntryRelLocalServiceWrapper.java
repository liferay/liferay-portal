/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.term.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CommerceTermEntryRelLocalService}.
 *
 * @author Luca Pellizzon
 * @see CommerceTermEntryRelLocalService
 * @generated
 */
public class CommerceTermEntryRelLocalServiceWrapper
	implements CommerceTermEntryRelLocalService,
			   ServiceWrapper<CommerceTermEntryRelLocalService> {

	public CommerceTermEntryRelLocalServiceWrapper() {
		this(null);
	}

	public CommerceTermEntryRelLocalServiceWrapper(
		CommerceTermEntryRelLocalService commerceTermEntryRelLocalService) {

		_commerceTermEntryRelLocalService = commerceTermEntryRelLocalService;
	}

	/**
	 * Adds the commerce term entry rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTermEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTermEntryRel the commerce term entry rel
	 * @return the commerce term entry rel that was added
	 */
	@Override
	public com.liferay.commerce.term.model.CommerceTermEntryRel
		addCommerceTermEntryRel(
			com.liferay.commerce.term.model.CommerceTermEntryRel
				commerceTermEntryRel) {

		return _commerceTermEntryRelLocalService.addCommerceTermEntryRel(
			commerceTermEntryRel);
	}

	@Override
	public com.liferay.commerce.term.model.CommerceTermEntryRel
			addCommerceTermEntryRel(
				long userId, String className, long classPK,
				long commerceTermEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTermEntryRelLocalService.addCommerceTermEntryRel(
			userId, className, classPK, commerceTermEntryId);
	}

	/**
	 * Creates a new commerce term entry rel with the primary key. Does not add the commerce term entry rel to the database.
	 *
	 * @param commerceTermEntryRelId the primary key for the new commerce term entry rel
	 * @return the new commerce term entry rel
	 */
	@Override
	public com.liferay.commerce.term.model.CommerceTermEntryRel
		createCommerceTermEntryRel(long commerceTermEntryRelId) {

		return _commerceTermEntryRelLocalService.createCommerceTermEntryRel(
			commerceTermEntryRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTermEntryRelLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the commerce term entry rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTermEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTermEntryRel the commerce term entry rel
	 * @return the commerce term entry rel that was removed
	 * @throws PortalException
	 */
	@Override
	public com.liferay.commerce.term.model.CommerceTermEntryRel
			deleteCommerceTermEntryRel(
				com.liferay.commerce.term.model.CommerceTermEntryRel
					commerceTermEntryRel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTermEntryRelLocalService.deleteCommerceTermEntryRel(
			commerceTermEntryRel);
	}

	/**
	 * Deletes the commerce term entry rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTermEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTermEntryRelId the primary key of the commerce term entry rel
	 * @return the commerce term entry rel that was removed
	 * @throws PortalException if a commerce term entry rel with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.term.model.CommerceTermEntryRel
			deleteCommerceTermEntryRel(long commerceTermEntryRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTermEntryRelLocalService.deleteCommerceTermEntryRel(
			commerceTermEntryRelId);
	}

	@Override
	public void deleteCommerceTermEntryRels(long commerceTermEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceTermEntryRelLocalService.deleteCommerceTermEntryRels(
			commerceTermEntryId);
	}

	@Override
	public void deleteCommerceTermEntryRels(
			String className, long commerceTermEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commerceTermEntryRelLocalService.deleteCommerceTermEntryRels(
			className, commerceTermEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTermEntryRelLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commerceTermEntryRelLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commerceTermEntryRelLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commerceTermEntryRelLocalService.dynamicQuery();
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

		return _commerceTermEntryRelLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.term.model.impl.CommerceTermEntryRelModelImpl</code>.
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

		return _commerceTermEntryRelLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.term.model.impl.CommerceTermEntryRelModelImpl</code>.
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

		return _commerceTermEntryRelLocalService.dynamicQuery(
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

		return _commerceTermEntryRelLocalService.dynamicQueryCount(
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

		return _commerceTermEntryRelLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.commerce.term.model.CommerceTermEntryRel
		fetchCommerceTermEntryRel(long commerceTermEntryRelId) {

		return _commerceTermEntryRelLocalService.fetchCommerceTermEntryRel(
			commerceTermEntryRelId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commerceTermEntryRelLocalService.getActionableDynamicQuery();
	}

	@Override
	public java.util.List<com.liferay.commerce.term.model.CommerceTermEntryRel>
		getCommerceOrderTypeCommerceTermEntryRels(
			long commerceTermEntryId, String keywords, int start, int end) {

		return _commerceTermEntryRelLocalService.
			getCommerceOrderTypeCommerceTermEntryRels(
				commerceTermEntryId, keywords, start, end);
	}

	@Override
	public int getCommerceOrderTypeCommerceTermEntryRelsCount(
		long commerceTermEntryId, String keywords) {

		return _commerceTermEntryRelLocalService.
			getCommerceOrderTypeCommerceTermEntryRelsCount(
				commerceTermEntryId, keywords);
	}

	/**
	 * Returns the commerce term entry rel with the primary key.
	 *
	 * @param commerceTermEntryRelId the primary key of the commerce term entry rel
	 * @return the commerce term entry rel
	 * @throws PortalException if a commerce term entry rel with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.term.model.CommerceTermEntryRel
			getCommerceTermEntryRel(long commerceTermEntryRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTermEntryRelLocalService.getCommerceTermEntryRel(
			commerceTermEntryRelId);
	}

	/**
	 * Returns a range of all the commerce term entry rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.term.model.impl.CommerceTermEntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce term entry rels
	 * @param end the upper bound of the range of commerce term entry rels (not inclusive)
	 * @return the range of commerce term entry rels
	 */
	@Override
	public java.util.List<com.liferay.commerce.term.model.CommerceTermEntryRel>
		getCommerceTermEntryRels(int start, int end) {

		return _commerceTermEntryRelLocalService.getCommerceTermEntryRels(
			start, end);
	}

	/**
	 * Returns the number of commerce term entry rels.
	 *
	 * @return the number of commerce term entry rels
	 */
	@Override
	public int getCommerceTermEntryRelsCount() {
		return _commerceTermEntryRelLocalService.
			getCommerceTermEntryRelsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commerceTermEntryRelLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commerceTermEntryRelLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commerceTermEntryRelLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the commerce term entry rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommerceTermEntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commerceTermEntryRel the commerce term entry rel
	 * @return the commerce term entry rel that was updated
	 */
	@Override
	public com.liferay.commerce.term.model.CommerceTermEntryRel
		updateCommerceTermEntryRel(
			com.liferay.commerce.term.model.CommerceTermEntryRel
				commerceTermEntryRel) {

		return _commerceTermEntryRelLocalService.updateCommerceTermEntryRel(
			commerceTermEntryRel);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commerceTermEntryRelLocalService.getBasePersistence();
	}

	@Override
	public CommerceTermEntryRelLocalService getWrappedService() {
		return _commerceTermEntryRelLocalService;
	}

	@Override
	public void setWrappedService(
		CommerceTermEntryRelLocalService commerceTermEntryRelLocalService) {

		_commerceTermEntryRelLocalService = commerceTermEntryRelLocalService;
	}

	private CommerceTermEntryRelLocalService _commerceTermEntryRelLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-320120768