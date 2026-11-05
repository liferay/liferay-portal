/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service;

import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link DDMStructureVersionLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see DDMStructureVersionLocalService
 * @generated
 */
public class DDMStructureVersionLocalServiceWrapper
	implements DDMStructureVersionLocalService,
			   ServiceWrapper<DDMStructureVersionLocalService> {

	public DDMStructureVersionLocalServiceWrapper() {
		this(null);
	}

	public DDMStructureVersionLocalServiceWrapper(
		DDMStructureVersionLocalService ddmStructureVersionLocalService) {

		_ddmStructureVersionLocalService = ddmStructureVersionLocalService;
	}

	/**
	 * Adds the ddm structure version to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMStructureVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmStructureVersion the ddm structure version
	 * @return the ddm structure version that was added
	 */
	@Override
	public DDMStructureVersion addDDMStructureVersion(
		DDMStructureVersion ddmStructureVersion) {

		return _ddmStructureVersionLocalService.addDDMStructureVersion(
			ddmStructureVersion);
	}

	/**
	 * Creates a new ddm structure version with the primary key. Does not add the ddm structure version to the database.
	 *
	 * @param structureVersionId the primary key for the new ddm structure version
	 * @return the new ddm structure version
	 */
	@Override
	public DDMStructureVersion createDDMStructureVersion(
		long structureVersionId) {

		return _ddmStructureVersionLocalService.createDDMStructureVersion(
			structureVersionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureVersionLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the ddm structure version from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMStructureVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmStructureVersion the ddm structure version
	 * @return the ddm structure version that was removed
	 */
	@Override
	public DDMStructureVersion deleteDDMStructureVersion(
		DDMStructureVersion ddmStructureVersion) {

		return _ddmStructureVersionLocalService.deleteDDMStructureVersion(
			ddmStructureVersion);
	}

	/**
	 * Deletes the ddm structure version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMStructureVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param structureVersionId the primary key of the ddm structure version
	 * @return the ddm structure version that was removed
	 * @throws PortalException if a ddm structure version with the primary key could not be found
	 */
	@Override
	public DDMStructureVersion deleteDDMStructureVersion(
			long structureVersionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureVersionLocalService.deleteDDMStructureVersion(
			structureVersionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureVersionLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _ddmStructureVersionLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _ddmStructureVersionLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _ddmStructureVersionLocalService.dynamicQuery();
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

		return _ddmStructureVersionLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMStructureVersionModelImpl</code>.
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

		return _ddmStructureVersionLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMStructureVersionModelImpl</code>.
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

		return _ddmStructureVersionLocalService.dynamicQuery(
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

		return _ddmStructureVersionLocalService.dynamicQueryCount(dynamicQuery);
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

		return _ddmStructureVersionLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public DDMStructureVersion fetchDDMStructureVersion(
		long structureVersionId) {

		return _ddmStructureVersionLocalService.fetchDDMStructureVersion(
			structureVersionId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _ddmStructureVersionLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the ddm structure version with the primary key.
	 *
	 * @param structureVersionId the primary key of the ddm structure version
	 * @return the ddm structure version
	 * @throws PortalException if a ddm structure version with the primary key could not be found
	 */
	@Override
	public DDMStructureVersion getDDMStructureVersion(long structureVersionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureVersionLocalService.getDDMStructureVersion(
			structureVersionId);
	}

	/**
	 * Returns a range of all the ddm structure versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMStructureVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddm structure versions
	 * @param end the upper bound of the range of ddm structure versions (not inclusive)
	 * @return the range of ddm structure versions
	 */
	@Override
	public java.util.List<DDMStructureVersion> getDDMStructureVersions(
		int start, int end) {

		return _ddmStructureVersionLocalService.getDDMStructureVersions(
			start, end);
	}

	/**
	 * Returns the number of ddm structure versions.
	 *
	 * @return the number of ddm structure versions
	 */
	@Override
	public int getDDMStructureVersionsCount() {
		return _ddmStructureVersionLocalService.getDDMStructureVersionsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _ddmStructureVersionLocalService.
			getIndexableActionableDynamicQuery();
	}

	@Override
	public DDMStructureVersion getLatestStructureVersion(long structureId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureVersionLocalService.getLatestStructureVersion(
			structureId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _ddmStructureVersionLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureVersionLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public DDMStructureVersion getStructureVersion(long structureVersionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureVersionLocalService.getStructureVersion(
			structureVersionId);
	}

	@Override
	public DDMStructureVersion getStructureVersion(
			long structureId, String version)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureVersionLocalService.getStructureVersion(
			structureId, version);
	}

	@Override
	public com.liferay.dynamic.data.mapping.model.DDMForm
			getStructureVersionDDMForm(DDMStructureVersion structureVersion)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmStructureVersionLocalService.getStructureVersionDDMForm(
			structureVersion);
	}

	@Override
	public java.util.List<DDMStructureVersion> getStructureVersions(
		long structureId) {

		return _ddmStructureVersionLocalService.getStructureVersions(
			structureId);
	}

	/**
	 * Updates the ddm structure version in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMStructureVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmStructureVersion the ddm structure version
	 * @return the ddm structure version that was updated
	 */
	@Override
	public DDMStructureVersion updateDDMStructureVersion(
		DDMStructureVersion ddmStructureVersion) {

		return _ddmStructureVersionLocalService.updateDDMStructureVersion(
			ddmStructureVersion);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _ddmStructureVersionLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<DDMStructureVersion> getCTPersistence() {
		return _ddmStructureVersionLocalService.getCTPersistence();
	}

	@Override
	public Class<DDMStructureVersion> getModelClass() {
		return _ddmStructureVersionLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<DDMStructureVersion>, R, E>
				updateUnsafeFunction)
		throws E {

		return _ddmStructureVersionLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public DDMStructureVersionLocalService getWrappedService() {
		return _ddmStructureVersionLocalService;
	}

	@Override
	public void setWrappedService(
		DDMStructureVersionLocalService ddmStructureVersionLocalService) {

		_ddmStructureVersionLocalService = ddmStructureVersionLocalService;
	}

	private DDMStructureVersionLocalService _ddmStructureVersionLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1520005006