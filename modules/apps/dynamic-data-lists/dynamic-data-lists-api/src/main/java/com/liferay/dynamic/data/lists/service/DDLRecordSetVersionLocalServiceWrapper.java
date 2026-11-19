/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.service;

import com.liferay.dynamic.data.lists.model.DDLRecordSetVersion;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link DDLRecordSetVersionLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see DDLRecordSetVersionLocalService
 * @generated
 */
public class DDLRecordSetVersionLocalServiceWrapper
	implements DDLRecordSetVersionLocalService,
			   ServiceWrapper<DDLRecordSetVersionLocalService> {

	public DDLRecordSetVersionLocalServiceWrapper() {
		this(null);
	}

	public DDLRecordSetVersionLocalServiceWrapper(
		DDLRecordSetVersionLocalService ddlRecordSetVersionLocalService) {

		_ddlRecordSetVersionLocalService = ddlRecordSetVersionLocalService;
	}

	/**
	 * Adds the ddl record set version to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDLRecordSetVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddlRecordSetVersion the ddl record set version
	 * @return the ddl record set version that was added
	 */
	@Override
	public DDLRecordSetVersion addDDLRecordSetVersion(
		DDLRecordSetVersion ddlRecordSetVersion) {

		return _ddlRecordSetVersionLocalService.addDDLRecordSetVersion(
			ddlRecordSetVersion);
	}

	/**
	 * Creates a new ddl record set version with the primary key. Does not add the ddl record set version to the database.
	 *
	 * @param recordSetVersionId the primary key for the new ddl record set version
	 * @return the new ddl record set version
	 */
	@Override
	public DDLRecordSetVersion createDDLRecordSetVersion(
		long recordSetVersionId) {

		return _ddlRecordSetVersionLocalService.createDDLRecordSetVersion(
			recordSetVersionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddlRecordSetVersionLocalService.createPersistedModel(
			primaryKeyObj);
	}

	@Override
	public void deleteByRecordSetId(long recordSetId) {
		_ddlRecordSetVersionLocalService.deleteByRecordSetId(recordSetId);
	}

	/**
	 * Deletes the ddl record set version from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDLRecordSetVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddlRecordSetVersion the ddl record set version
	 * @return the ddl record set version that was removed
	 */
	@Override
	public DDLRecordSetVersion deleteDDLRecordSetVersion(
		DDLRecordSetVersion ddlRecordSetVersion) {

		return _ddlRecordSetVersionLocalService.deleteDDLRecordSetVersion(
			ddlRecordSetVersion);
	}

	/**
	 * Deletes the ddl record set version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDLRecordSetVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param recordSetVersionId the primary key of the ddl record set version
	 * @return the ddl record set version that was removed
	 * @throws PortalException if a ddl record set version with the primary key could not be found
	 */
	@Override
	public DDLRecordSetVersion deleteDDLRecordSetVersion(
			long recordSetVersionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddlRecordSetVersionLocalService.deleteDDLRecordSetVersion(
			recordSetVersionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddlRecordSetVersionLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _ddlRecordSetVersionLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _ddlRecordSetVersionLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _ddlRecordSetVersionLocalService.dynamicQuery();
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

		return _ddlRecordSetVersionLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.lists.model.impl.DDLRecordSetVersionModelImpl</code>.
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

		return _ddlRecordSetVersionLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.lists.model.impl.DDLRecordSetVersionModelImpl</code>.
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

		return _ddlRecordSetVersionLocalService.dynamicQuery(
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

		return _ddlRecordSetVersionLocalService.dynamicQueryCount(dynamicQuery);
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

		return _ddlRecordSetVersionLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public DDLRecordSetVersion fetchDDLRecordSetVersion(
		long recordSetVersionId) {

		return _ddlRecordSetVersionLocalService.fetchDDLRecordSetVersion(
			recordSetVersionId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _ddlRecordSetVersionLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the ddl record set version with the primary key.
	 *
	 * @param recordSetVersionId the primary key of the ddl record set version
	 * @return the ddl record set version
	 * @throws PortalException if a ddl record set version with the primary key could not be found
	 */
	@Override
	public DDLRecordSetVersion getDDLRecordSetVersion(long recordSetVersionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddlRecordSetVersionLocalService.getDDLRecordSetVersion(
			recordSetVersionId);
	}

	/**
	 * Returns a range of all the ddl record set versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.lists.model.impl.DDLRecordSetVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddl record set versions
	 * @param end the upper bound of the range of ddl record set versions (not inclusive)
	 * @return the range of ddl record set versions
	 */
	@Override
	public java.util.List<DDLRecordSetVersion> getDDLRecordSetVersions(
		int start, int end) {

		return _ddlRecordSetVersionLocalService.getDDLRecordSetVersions(
			start, end);
	}

	/**
	 * Returns the number of ddl record set versions.
	 *
	 * @return the number of ddl record set versions
	 */
	@Override
	public int getDDLRecordSetVersionsCount() {
		return _ddlRecordSetVersionLocalService.getDDLRecordSetVersionsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _ddlRecordSetVersionLocalService.
			getIndexableActionableDynamicQuery();
	}

	@Override
	public DDLRecordSetVersion getLatestRecordSetVersion(long recordSetId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddlRecordSetVersionLocalService.getLatestRecordSetVersion(
			recordSetId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _ddlRecordSetVersionLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddlRecordSetVersionLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public DDLRecordSetVersion getRecordSetVersion(
			long recordSetId, String version)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddlRecordSetVersionLocalService.getRecordSetVersion(
			recordSetId, version);
	}

	/**
	 * Updates the ddl record set version in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDLRecordSetVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddlRecordSetVersion the ddl record set version
	 * @return the ddl record set version that was updated
	 */
	@Override
	public DDLRecordSetVersion updateDDLRecordSetVersion(
		DDLRecordSetVersion ddlRecordSetVersion) {

		return _ddlRecordSetVersionLocalService.updateDDLRecordSetVersion(
			ddlRecordSetVersion);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _ddlRecordSetVersionLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<DDLRecordSetVersion> getCTPersistence() {
		return _ddlRecordSetVersionLocalService.getCTPersistence();
	}

	@Override
	public Class<DDLRecordSetVersion> getModelClass() {
		return _ddlRecordSetVersionLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<DDLRecordSetVersion>, R, E>
				updateUnsafeFunction)
		throws E {

		return _ddlRecordSetVersionLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public DDLRecordSetVersionLocalService getWrappedService() {
		return _ddlRecordSetVersionLocalService;
	}

	@Override
	public void setWrappedService(
		DDLRecordSetVersionLocalService ddlRecordSetVersionLocalService) {

		_ddlRecordSetVersionLocalService = ddlRecordSetVersionLocalService;
	}

	private DDLRecordSetVersionLocalService _ddlRecordSetVersionLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:139834149