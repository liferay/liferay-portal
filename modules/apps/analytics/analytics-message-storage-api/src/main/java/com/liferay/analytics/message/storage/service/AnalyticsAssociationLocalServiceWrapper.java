/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.message.storage.service;

import com.liferay.analytics.message.storage.model.AnalyticsAssociation;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link AnalyticsAssociationLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see AnalyticsAssociationLocalService
 * @generated
 */
public class AnalyticsAssociationLocalServiceWrapper
	implements AnalyticsAssociationLocalService,
			   ServiceWrapper<AnalyticsAssociationLocalService> {

	public AnalyticsAssociationLocalServiceWrapper() {
		this(null);
	}

	public AnalyticsAssociationLocalServiceWrapper(
		AnalyticsAssociationLocalService analyticsAssociationLocalService) {

		_analyticsAssociationLocalService = analyticsAssociationLocalService;
	}

	/**
	 * Adds the analytics association to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AnalyticsAssociationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param analyticsAssociation the analytics association
	 * @return the analytics association that was added
	 */
	@Override
	public AnalyticsAssociation addAnalyticsAssociation(
		AnalyticsAssociation analyticsAssociation) {

		return _analyticsAssociationLocalService.addAnalyticsAssociation(
			analyticsAssociation);
	}

	@Override
	public AnalyticsAssociation addAnalyticsAssociation(
		long companyId, java.util.Date createDate, long userId,
		String associationClassName, long associationClassPK, String className,
		long classPK) {

		return _analyticsAssociationLocalService.addAnalyticsAssociation(
			companyId, createDate, userId, associationClassName,
			associationClassPK, className, classPK);
	}

	/**
	 * Creates a new analytics association with the primary key. Does not add the analytics association to the database.
	 *
	 * @param analyticsAssociationId the primary key for the new analytics association
	 * @return the new analytics association
	 */
	@Override
	public AnalyticsAssociation createAnalyticsAssociation(
		long analyticsAssociationId) {

		return _analyticsAssociationLocalService.createAnalyticsAssociation(
			analyticsAssociationId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _analyticsAssociationLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the analytics association from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AnalyticsAssociationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param analyticsAssociation the analytics association
	 * @return the analytics association that was removed
	 */
	@Override
	public AnalyticsAssociation deleteAnalyticsAssociation(
		AnalyticsAssociation analyticsAssociation) {

		return _analyticsAssociationLocalService.deleteAnalyticsAssociation(
			analyticsAssociation);
	}

	/**
	 * Deletes the analytics association with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AnalyticsAssociationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param analyticsAssociationId the primary key of the analytics association
	 * @return the analytics association that was removed
	 * @throws PortalException if a analytics association with the primary key could not be found
	 */
	@Override
	public AnalyticsAssociation deleteAnalyticsAssociation(
			long analyticsAssociationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _analyticsAssociationLocalService.deleteAnalyticsAssociation(
			analyticsAssociationId);
	}

	@Override
	public void deleteAnalyticsAssociations(long companyId) {
		_analyticsAssociationLocalService.deleteAnalyticsAssociations(
			companyId);
	}

	@Override
	public void deleteAnalyticsAssociations(
		long companyId, java.util.Date modifiedDate) {

		_analyticsAssociationLocalService.deleteAnalyticsAssociations(
			companyId, modifiedDate);
	}

	@Override
	public void deleteAnalyticsAssociations(
		long companyId, String associationClassName, long associationClassPK) {

		_analyticsAssociationLocalService.deleteAnalyticsAssociations(
			companyId, associationClassName, associationClassPK);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _analyticsAssociationLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _analyticsAssociationLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _analyticsAssociationLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _analyticsAssociationLocalService.dynamicQuery();
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

		return _analyticsAssociationLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.analytics.message.storage.model.impl.AnalyticsAssociationModelImpl</code>.
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

		return _analyticsAssociationLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.analytics.message.storage.model.impl.AnalyticsAssociationModelImpl</code>.
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

		return _analyticsAssociationLocalService.dynamicQuery(
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

		return _analyticsAssociationLocalService.dynamicQueryCount(
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

		return _analyticsAssociationLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public AnalyticsAssociation fetchAnalyticsAssociation(
		long analyticsAssociationId) {

		return _analyticsAssociationLocalService.fetchAnalyticsAssociation(
			analyticsAssociationId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _analyticsAssociationLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the analytics association with the primary key.
	 *
	 * @param analyticsAssociationId the primary key of the analytics association
	 * @return the analytics association
	 * @throws PortalException if a analytics association with the primary key could not be found
	 */
	@Override
	public AnalyticsAssociation getAnalyticsAssociation(
			long analyticsAssociationId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _analyticsAssociationLocalService.getAnalyticsAssociation(
			analyticsAssociationId);
	}

	/**
	 * Returns a range of all the analytics associations.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.analytics.message.storage.model.impl.AnalyticsAssociationModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of analytics associations
	 * @param end the upper bound of the range of analytics associations (not inclusive)
	 * @return the range of analytics associations
	 */
	@Override
	public java.util.List<AnalyticsAssociation> getAnalyticsAssociations(
		int start, int end) {

		return _analyticsAssociationLocalService.getAnalyticsAssociations(
			start, end);
	}

	@Override
	public java.util.List<AnalyticsAssociation> getAnalyticsAssociations(
		long companyId, java.util.Date modifiedDate,
		String associationClassName, int start, int end) {

		return _analyticsAssociationLocalService.getAnalyticsAssociations(
			companyId, modifiedDate, associationClassName, start, end);
	}

	/**
	 * Returns the number of analytics associations.
	 *
	 * @return the number of analytics associations
	 */
	@Override
	public int getAnalyticsAssociationsCount() {
		return _analyticsAssociationLocalService.
			getAnalyticsAssociationsCount();
	}

	@Override
	public int getAnalyticsAssociationsCount(
		long companyId, java.util.Date modifiedDate,
		String associationClassName) {

		return _analyticsAssociationLocalService.getAnalyticsAssociationsCount(
			companyId, modifiedDate, associationClassName);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _analyticsAssociationLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _analyticsAssociationLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _analyticsAssociationLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the analytics association in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AnalyticsAssociationLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param analyticsAssociation the analytics association
	 * @return the analytics association that was updated
	 */
	@Override
	public AnalyticsAssociation updateAnalyticsAssociation(
		AnalyticsAssociation analyticsAssociation) {

		return _analyticsAssociationLocalService.updateAnalyticsAssociation(
			analyticsAssociation);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _analyticsAssociationLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<AnalyticsAssociation> getCTPersistence() {
		return _analyticsAssociationLocalService.getCTPersistence();
	}

	@Override
	public Class<AnalyticsAssociation> getModelClass() {
		return _analyticsAssociationLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<AnalyticsAssociation>, R, E>
				updateUnsafeFunction)
		throws E {

		return _analyticsAssociationLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public AnalyticsAssociationLocalService getWrappedService() {
		return _analyticsAssociationLocalService;
	}

	@Override
	public void setWrappedService(
		AnalyticsAssociationLocalService analyticsAssociationLocalService) {

		_analyticsAssociationLocalService = analyticsAssociationLocalService;
	}

	private AnalyticsAssociationLocalService _analyticsAssociationLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:234133749