/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link DLStorageQuotaLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see DLStorageQuotaLocalService
 * @generated
 */
public class DLStorageQuotaLocalServiceWrapper
	implements DLStorageQuotaLocalService,
			   ServiceWrapper<DLStorageQuotaLocalService> {

	public DLStorageQuotaLocalServiceWrapper() {
		this(null);
	}

	public DLStorageQuotaLocalServiceWrapper(
		DLStorageQuotaLocalService dlStorageQuotaLocalService) {

		_dlStorageQuotaLocalService = dlStorageQuotaLocalService;
	}

	/**
	 * Adds the dl storage quota to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLStorageQuotaLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlStorageQuota the dl storage quota
	 * @return the dl storage quota that was added
	 */
	@Override
	public com.liferay.document.library.model.DLStorageQuota addDLStorageQuota(
		com.liferay.document.library.model.DLStorageQuota dlStorageQuota) {

		return _dlStorageQuotaLocalService.addDLStorageQuota(dlStorageQuota);
	}

	/**
	 * Creates a new dl storage quota with the primary key. Does not add the dl storage quota to the database.
	 *
	 * @param dlStorageQuotaId the primary key for the new dl storage quota
	 * @return the new dl storage quota
	 */
	@Override
	public com.liferay.document.library.model.DLStorageQuota
		createDLStorageQuota(long dlStorageQuotaId) {

		return _dlStorageQuotaLocalService.createDLStorageQuota(
			dlStorageQuotaId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlStorageQuotaLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the dl storage quota from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLStorageQuotaLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlStorageQuota the dl storage quota
	 * @return the dl storage quota that was removed
	 */
	@Override
	public com.liferay.document.library.model.DLStorageQuota
		deleteDLStorageQuota(
			com.liferay.document.library.model.DLStorageQuota dlStorageQuota) {

		return _dlStorageQuotaLocalService.deleteDLStorageQuota(dlStorageQuota);
	}

	/**
	 * Deletes the dl storage quota with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLStorageQuotaLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlStorageQuotaId the primary key of the dl storage quota
	 * @return the dl storage quota that was removed
	 * @throws PortalException if a dl storage quota with the primary key could not be found
	 */
	@Override
	public com.liferay.document.library.model.DLStorageQuota
			deleteDLStorageQuota(long dlStorageQuotaId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlStorageQuotaLocalService.deleteDLStorageQuota(
			dlStorageQuotaId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlStorageQuotaLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _dlStorageQuotaLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _dlStorageQuotaLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _dlStorageQuotaLocalService.dynamicQuery();
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

		return _dlStorageQuotaLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.document.library.model.impl.DLStorageQuotaModelImpl</code>.
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

		return _dlStorageQuotaLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.document.library.model.impl.DLStorageQuotaModelImpl</code>.
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

		return _dlStorageQuotaLocalService.dynamicQuery(
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

		return _dlStorageQuotaLocalService.dynamicQueryCount(dynamicQuery);
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

		return _dlStorageQuotaLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.document.library.model.DLStorageQuota
		fetchDLStorageQuota(long dlStorageQuotaId) {

		return _dlStorageQuotaLocalService.fetchDLStorageQuota(
			dlStorageQuotaId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _dlStorageQuotaLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the dl storage quota with the primary key.
	 *
	 * @param dlStorageQuotaId the primary key of the dl storage quota
	 * @return the dl storage quota
	 * @throws PortalException if a dl storage quota with the primary key could not be found
	 */
	@Override
	public com.liferay.document.library.model.DLStorageQuota getDLStorageQuota(
			long dlStorageQuotaId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlStorageQuotaLocalService.getDLStorageQuota(dlStorageQuotaId);
	}

	/**
	 * Returns a range of all the dl storage quotas.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.document.library.model.impl.DLStorageQuotaModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of dl storage quotas
	 * @param end the upper bound of the range of dl storage quotas (not inclusive)
	 * @return the range of dl storage quotas
	 */
	@Override
	public java.util.List<com.liferay.document.library.model.DLStorageQuota>
		getDLStorageQuotas(int start, int end) {

		return _dlStorageQuotaLocalService.getDLStorageQuotas(start, end);
	}

	/**
	 * Returns the number of dl storage quotas.
	 *
	 * @return the number of dl storage quotas
	 */
	@Override
	public int getDLStorageQuotasCount() {
		return _dlStorageQuotaLocalService.getDLStorageQuotasCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _dlStorageQuotaLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _dlStorageQuotaLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlStorageQuotaLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public void incrementStorageSize(long companyId, long increment) {
		_dlStorageQuotaLocalService.incrementStorageSize(companyId, increment);
	}

	/**
	 * Updates the dl storage quota in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLStorageQuotaLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlStorageQuota the dl storage quota
	 * @return the dl storage quota that was updated
	 */
	@Override
	public com.liferay.document.library.model.DLStorageQuota
		updateDLStorageQuota(
			com.liferay.document.library.model.DLStorageQuota dlStorageQuota) {

		return _dlStorageQuotaLocalService.updateDLStorageQuota(dlStorageQuota);
	}

	@Override
	public void updateStorageSize(long companyId) {
		_dlStorageQuotaLocalService.updateStorageSize(companyId);
	}

	@Override
	public void validateStorageQuota(long companyId, long increment)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlStorageQuotaLocalService.validateStorageQuota(companyId, increment);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _dlStorageQuotaLocalService.getBasePersistence();
	}

	@Override
	public DLStorageQuotaLocalService getWrappedService() {
		return _dlStorageQuotaLocalService;
	}

	@Override
	public void setWrappedService(
		DLStorageQuotaLocalService dlStorageQuotaLocalService) {

		_dlStorageQuotaLocalService = dlStorageQuotaLocalService;
	}

	private DLStorageQuotaLocalService _dlStorageQuotaLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-614569874