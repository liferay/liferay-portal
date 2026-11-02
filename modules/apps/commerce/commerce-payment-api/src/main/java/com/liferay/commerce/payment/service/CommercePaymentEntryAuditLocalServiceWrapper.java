/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CommercePaymentEntryAuditLocalService}.
 *
 * @author Luca Pellizzon
 * @see CommercePaymentEntryAuditLocalService
 * @generated
 */
public class CommercePaymentEntryAuditLocalServiceWrapper
	implements CommercePaymentEntryAuditLocalService,
			   ServiceWrapper<CommercePaymentEntryAuditLocalService> {

	public CommercePaymentEntryAuditLocalServiceWrapper() {
		this(null);
	}

	public CommercePaymentEntryAuditLocalServiceWrapper(
		CommercePaymentEntryAuditLocalService
			commercePaymentEntryAuditLocalService) {

		_commercePaymentEntryAuditLocalService =
			commercePaymentEntryAuditLocalService;
	}

	/**
	 * Adds the commerce payment entry audit to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePaymentEntryAuditLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePaymentEntryAudit the commerce payment entry audit
	 * @return the commerce payment entry audit that was added
	 */
	@Override
	public com.liferay.commerce.payment.model.CommercePaymentEntryAudit
		addCommercePaymentEntryAudit(
			com.liferay.commerce.payment.model.CommercePaymentEntryAudit
				commercePaymentEntryAudit) {

		return _commercePaymentEntryAuditLocalService.
			addCommercePaymentEntryAudit(commercePaymentEntryAudit);
	}

	@Override
	public com.liferay.commerce.payment.model.CommercePaymentEntryAudit
			addCommercePaymentEntryAudit(
				long userId, long commercePaymentEntryId,
				java.math.BigDecimal amount, String currencyCode,
				String logType, String logTypeSettings,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePaymentEntryAuditLocalService.
			addCommercePaymentEntryAudit(
				userId, commercePaymentEntryId, amount, currencyCode, logType,
				logTypeSettings, serviceContext);
	}

	/**
	 * Creates a new commerce payment entry audit with the primary key. Does not add the commerce payment entry audit to the database.
	 *
	 * @param commercePaymentEntryAuditId the primary key for the new commerce payment entry audit
	 * @return the new commerce payment entry audit
	 */
	@Override
	public com.liferay.commerce.payment.model.CommercePaymentEntryAudit
		createCommercePaymentEntryAudit(long commercePaymentEntryAuditId) {

		return _commercePaymentEntryAuditLocalService.
			createCommercePaymentEntryAudit(commercePaymentEntryAuditId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePaymentEntryAuditLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the commerce payment entry audit from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePaymentEntryAuditLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePaymentEntryAudit the commerce payment entry audit
	 * @return the commerce payment entry audit that was removed
	 */
	@Override
	public com.liferay.commerce.payment.model.CommercePaymentEntryAudit
		deleteCommercePaymentEntryAudit(
			com.liferay.commerce.payment.model.CommercePaymentEntryAudit
				commercePaymentEntryAudit) {

		return _commercePaymentEntryAuditLocalService.
			deleteCommercePaymentEntryAudit(commercePaymentEntryAudit);
	}

	/**
	 * Deletes the commerce payment entry audit with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePaymentEntryAuditLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePaymentEntryAuditId the primary key of the commerce payment entry audit
	 * @return the commerce payment entry audit that was removed
	 * @throws PortalException if a commerce payment entry audit with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.payment.model.CommercePaymentEntryAudit
			deleteCommercePaymentEntryAudit(long commercePaymentEntryAuditId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePaymentEntryAuditLocalService.
			deleteCommercePaymentEntryAudit(commercePaymentEntryAuditId);
	}

	@Override
	public void deleteCommercePaymentEntryAudits(long commercePaymentEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_commercePaymentEntryAuditLocalService.deleteCommercePaymentEntryAudits(
			commercePaymentEntryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePaymentEntryAuditLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _commercePaymentEntryAuditLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _commercePaymentEntryAuditLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _commercePaymentEntryAuditLocalService.dynamicQuery();
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

		return _commercePaymentEntryAuditLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.payment.model.impl.CommercePaymentEntryAuditModelImpl</code>.
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

		return _commercePaymentEntryAuditLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.payment.model.impl.CommercePaymentEntryAuditModelImpl</code>.
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

		return _commercePaymentEntryAuditLocalService.dynamicQuery(
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

		return _commercePaymentEntryAuditLocalService.dynamicQueryCount(
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

		return _commercePaymentEntryAuditLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.commerce.payment.model.CommercePaymentEntryAudit
		fetchCommercePaymentEntryAudit(long commercePaymentEntryAuditId) {

		return _commercePaymentEntryAuditLocalService.
			fetchCommercePaymentEntryAudit(commercePaymentEntryAuditId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _commercePaymentEntryAuditLocalService.
			getActionableDynamicQuery();
	}

	/**
	 * Returns the commerce payment entry audit with the primary key.
	 *
	 * @param commercePaymentEntryAuditId the primary key of the commerce payment entry audit
	 * @return the commerce payment entry audit
	 * @throws PortalException if a commerce payment entry audit with the primary key could not be found
	 */
	@Override
	public com.liferay.commerce.payment.model.CommercePaymentEntryAudit
			getCommercePaymentEntryAudit(long commercePaymentEntryAuditId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePaymentEntryAuditLocalService.
			getCommercePaymentEntryAudit(commercePaymentEntryAuditId);
	}

	/**
	 * Returns a range of all the commerce payment entry audits.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.payment.model.impl.CommercePaymentEntryAuditModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of commerce payment entry audits
	 * @param end the upper bound of the range of commerce payment entry audits (not inclusive)
	 * @return the range of commerce payment entry audits
	 */
	@Override
	public java.util.List
		<com.liferay.commerce.payment.model.CommercePaymentEntryAudit>
			getCommercePaymentEntryAudits(int start, int end) {

		return _commercePaymentEntryAuditLocalService.
			getCommercePaymentEntryAudits(start, end);
	}

	/**
	 * Returns the number of commerce payment entry audits.
	 *
	 * @return the number of commerce payment entry audits
	 */
	@Override
	public int getCommercePaymentEntryAuditsCount() {
		return _commercePaymentEntryAuditLocalService.
			getCommercePaymentEntryAuditsCount();
	}

	@Override
	public int getCommercePaymentEntryAuditsCount(long commercePaymentEntryId) {
		return _commercePaymentEntryAuditLocalService.
			getCommercePaymentEntryAuditsCount(commercePaymentEntryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _commercePaymentEntryAuditLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _commercePaymentEntryAuditLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _commercePaymentEntryAuditLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult
		<com.liferay.commerce.payment.model.CommercePaymentEntryAudit>
			searchCommercePaymentEntryAudits(
				long companyId, String keywords,
				java.util.LinkedHashMap<String, Object> params, int start,
				int end, String orderByField, boolean reverse) {

		return _commercePaymentEntryAuditLocalService.
			searchCommercePaymentEntryAudits(
				companyId, keywords, params, start, end, orderByField, reverse);
	}

	/**
	 * Updates the commerce payment entry audit in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CommercePaymentEntryAuditLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param commercePaymentEntryAudit the commerce payment entry audit
	 * @return the commerce payment entry audit that was updated
	 */
	@Override
	public com.liferay.commerce.payment.model.CommercePaymentEntryAudit
		updateCommercePaymentEntryAudit(
			com.liferay.commerce.payment.model.CommercePaymentEntryAudit
				commercePaymentEntryAudit) {

		return _commercePaymentEntryAuditLocalService.
			updateCommercePaymentEntryAudit(commercePaymentEntryAudit);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _commercePaymentEntryAuditLocalService.getBasePersistence();
	}

	@Override
	public CommercePaymentEntryAuditLocalService getWrappedService() {
		return _commercePaymentEntryAuditLocalService;
	}

	@Override
	public void setWrappedService(
		CommercePaymentEntryAuditLocalService
			commercePaymentEntryAuditLocalService) {

		_commercePaymentEntryAuditLocalService =
			commercePaymentEntryAuditLocalService;
	}

	private CommercePaymentEntryAuditLocalService
		_commercePaymentEntryAuditLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1958613883