/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.rule.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link COREntryRelLocalService}.
 *
 * @author Luca Pellizzon
 * @see COREntryRelLocalService
 * @generated
 */
public class COREntryRelLocalServiceWrapper
	implements COREntryRelLocalService,
			   ServiceWrapper<COREntryRelLocalService> {

	public COREntryRelLocalServiceWrapper() {
		this(null);
	}

	public COREntryRelLocalServiceWrapper(
		COREntryRelLocalService corEntryRelLocalService) {

		_corEntryRelLocalService = corEntryRelLocalService;
	}

	/**
	 * Adds the cor entry rel to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect COREntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param corEntryRel the cor entry rel
	 * @return the cor entry rel that was added
	 */
	@Override
	public com.liferay.commerce.order.rule.model.COREntryRel addCOREntryRel(
		com.liferay.commerce.order.rule.model.COREntryRel corEntryRel) {

		return _corEntryRelLocalService.addCOREntryRel(corEntryRel);
	}

	@Override
	public com.liferay.commerce.order.rule.model.COREntryRel addCOREntryRel(
			long userId, String className, long classPK, long corEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _corEntryRelLocalService.addCOREntryRel(
			userId, className, classPK, corEntryId);
	}

	/**
	 * Creates a new cor entry rel with the primary key. Does not add the cor entry rel to the database.
	 *
	 * @param COREntryRelId the primary key for the new cor entry rel
	 * @return the new cor entry rel
	 */
	@Override
	public com.liferay.commerce.order.rule.model.COREntryRel createCOREntryRel(
		long COREntryRelId) {

		return _corEntryRelLocalService.createCOREntryRel(COREntryRelId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _corEntryRelLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the cor entry rel from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect COREntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param corEntryRel the cor entry rel
	 * @return the cor entry rel that was removed
	 * @throws PortalException
	 */
	@Override
	public com.liferay.commerce.order.rule.model.COREntryRel deleteCOREntryRel(
			com.liferay.commerce.order.rule.model.COREntryRel corEntryRel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _corEntryRelLocalService.deleteCOREntryRel(corEntryRel);
	}

	/**
	 * Deletes the cor entry rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect COREntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param COREntryRelId the primary key of the cor entry rel
	 * @return the cor entry rel that was removed
	 * @throws PortalException if a cor entry rel with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.order.rule.model.COREntryRel deleteCOREntryRel(
			long COREntryRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _corEntryRelLocalService.deleteCOREntryRel(COREntryRelId);
	}

	@Override
	public void deleteCOREntryRels(long corEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_corEntryRelLocalService.deleteCOREntryRels(corEntryId);
	}

	@Override
	public void deleteCOREntryRels(String className, long corEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_corEntryRelLocalService.deleteCOREntryRels(className, corEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _corEntryRelLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _corEntryRelLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _corEntryRelLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _corEntryRelLocalService.dynamicQuery();
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

		return _corEntryRelLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.order.rule.model.impl.COREntryRelModelImpl</code>.
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

		return _corEntryRelLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.order.rule.model.impl.COREntryRelModelImpl</code>.
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

		return _corEntryRelLocalService.dynamicQuery(
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

		return _corEntryRelLocalService.dynamicQueryCount(dynamicQuery);
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

		return _corEntryRelLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.commerce.order.rule.model.COREntryRel fetchCOREntryRel(
		long COREntryRelId) {

		return _corEntryRelLocalService.fetchCOREntryRel(COREntryRelId);
	}

	@Override
	public java.util.List<com.liferay.commerce.order.rule.model.COREntryRel>
		getAccountEntryCOREntryRels(
			long corEntryId, String keywords, int start, int end) {

		return _corEntryRelLocalService.getAccountEntryCOREntryRels(
			corEntryId, keywords, start, end);
	}

	@Override
	public int getAccountEntryCOREntryRelsCount(
		long corEntryId, String keywords) {

		return _corEntryRelLocalService.getAccountEntryCOREntryRelsCount(
			corEntryId, keywords);
	}

	@Override
	public java.util.List<com.liferay.commerce.order.rule.model.COREntryRel>
		getAccountGroupCOREntryRels(
			long corEntryId, String keywords, int start, int end) {

		return _corEntryRelLocalService.getAccountGroupCOREntryRels(
			corEntryId, keywords, start, end);
	}

	@Override
	public int getAccountGroupCOREntryRelsCount(
		long corEntryId, String keywords) {

		return _corEntryRelLocalService.getAccountGroupCOREntryRelsCount(
			corEntryId, keywords);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _corEntryRelLocalService.getActionableDynamicQuery();
	}

	@Override
	public java.util.List<com.liferay.commerce.order.rule.model.COREntryRel>
		getCommerceChannelCOREntryRels(
			long corEntryId, String keywords, int start, int end) {

		return _corEntryRelLocalService.getCommerceChannelCOREntryRels(
			corEntryId, keywords, start, end);
	}

	@Override
	public int getCommerceChannelCOREntryRelsCount(
		long corEntryId, String keywords) {

		return _corEntryRelLocalService.getCommerceChannelCOREntryRelsCount(
			corEntryId, keywords);
	}

	@Override
	public java.util.List<com.liferay.commerce.order.rule.model.COREntryRel>
		getCommerceOrderTypeCOREntryRels(
			long corEntryId, String keywords, int start, int end) {

		return _corEntryRelLocalService.getCommerceOrderTypeCOREntryRels(
			corEntryId, keywords, start, end);
	}

	@Override
	public int getCommerceOrderTypeCOREntryRelsCount(
		long corEntryId, String keywords) {

		return _corEntryRelLocalService.getCommerceOrderTypeCOREntryRelsCount(
			corEntryId, keywords);
	}

	/**
	 * Returns the cor entry rel with the primary key.
	 *
	 * @param COREntryRelId the primary key of the cor entry rel
	 * @return the cor entry rel
	 * @throws PortalException if a cor entry rel with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.order.rule.model.COREntryRel getCOREntryRel(
			long COREntryRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _corEntryRelLocalService.getCOREntryRel(COREntryRelId);
	}

	/**
	 * Returns a range of all the cor entry rels.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.order.rule.model.impl.COREntryRelModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cor entry rels
	 * @param end the upper bound of the range of cor entry rels (not inclusive)
	 * @return the range of cor entry rels
	 */
	@Override
	public java.util.List<com.liferay.commerce.order.rule.model.COREntryRel>
		getCOREntryRels(int start, int end) {

		return _corEntryRelLocalService.getCOREntryRels(start, end);
	}

	@Override
	public java.util.List<com.liferay.commerce.order.rule.model.COREntryRel>
		getCOREntryRels(long corEntryId) {

		return _corEntryRelLocalService.getCOREntryRels(corEntryId);
	}

	@Override
	public java.util.List<com.liferay.commerce.order.rule.model.COREntryRel>
		getCOREntryRels(
			long corEntryId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.commerce.order.rule.model.COREntryRel>
					orderByComparator) {

		return _corEntryRelLocalService.getCOREntryRels(
			corEntryId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of cor entry rels.
	 *
	 * @return the number of cor entry rels
	 */
	@Override
	public int getCOREntryRelsCount() {
		return _corEntryRelLocalService.getCOREntryRelsCount();
	}

	@Override
	public int getCOREntryRelsCount(long corEntryId) {
		return _corEntryRelLocalService.getCOREntryRelsCount(corEntryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _corEntryRelLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _corEntryRelLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _corEntryRelLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the cor entry rel in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect COREntryRelLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param corEntryRel the cor entry rel
	 * @return the cor entry rel that was updated
	 */
	@Override
	public com.liferay.commerce.order.rule.model.COREntryRel updateCOREntryRel(
		com.liferay.commerce.order.rule.model.COREntryRel corEntryRel) {

		return _corEntryRelLocalService.updateCOREntryRel(corEntryRel);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _corEntryRelLocalService.getBasePersistence();
	}

	@Override
	public COREntryRelLocalService getWrappedService() {
		return _corEntryRelLocalService;
	}

	@Override
	public void setWrappedService(
		COREntryRelLocalService corEntryRelLocalService) {

		_corEntryRelLocalService = corEntryRelLocalService;
	}

	private COREntryRelLocalService _corEntryRelLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-490964889