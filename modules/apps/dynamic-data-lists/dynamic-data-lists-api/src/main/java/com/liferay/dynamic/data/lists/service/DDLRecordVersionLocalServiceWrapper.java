/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.service;

import com.liferay.dynamic.data.lists.model.DDLRecordVersion;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link DDLRecordVersionLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see DDLRecordVersionLocalService
 * @generated
 */
public class DDLRecordVersionLocalServiceWrapper
	implements DDLRecordVersionLocalService,
			   ServiceWrapper<DDLRecordVersionLocalService> {

	public DDLRecordVersionLocalServiceWrapper() {
		this(null);
	}

	public DDLRecordVersionLocalServiceWrapper(
		DDLRecordVersionLocalService ddlRecordVersionLocalService) {

		_ddlRecordVersionLocalService = ddlRecordVersionLocalService;
	}

	/**
	 * Adds the ddl record version to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDLRecordVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddlRecordVersion the ddl record version
	 * @return the ddl record version that was added
	 */
	@Override
	public DDLRecordVersion addDDLRecordVersion(
		DDLRecordVersion ddlRecordVersion) {

		return _ddlRecordVersionLocalService.addDDLRecordVersion(
			ddlRecordVersion);
	}

	/**
	 * Creates a new ddl record version with the primary key. Does not add the ddl record version to the database.
	 *
	 * @param recordVersionId the primary key for the new ddl record version
	 * @return the new ddl record version
	 */
	@Override
	public DDLRecordVersion createDDLRecordVersion(long recordVersionId) {
		return _ddlRecordVersionLocalService.createDDLRecordVersion(
			recordVersionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddlRecordVersionLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the ddl record version from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDLRecordVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddlRecordVersion the ddl record version
	 * @return the ddl record version that was removed
	 */
	@Override
	public DDLRecordVersion deleteDDLRecordVersion(
		DDLRecordVersion ddlRecordVersion) {

		return _ddlRecordVersionLocalService.deleteDDLRecordVersion(
			ddlRecordVersion);
	}

	/**
	 * Deletes the ddl record version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDLRecordVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param recordVersionId the primary key of the ddl record version
	 * @return the ddl record version that was removed
	 * @throws PortalException if a ddl record version with the primary key could not be found
	 */
	@Override
	public DDLRecordVersion deleteDDLRecordVersion(long recordVersionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddlRecordVersionLocalService.deleteDDLRecordVersion(
			recordVersionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddlRecordVersionLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _ddlRecordVersionLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _ddlRecordVersionLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _ddlRecordVersionLocalService.dynamicQuery();
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

		return _ddlRecordVersionLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.lists.model.impl.DDLRecordVersionModelImpl</code>.
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

		return _ddlRecordVersionLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.lists.model.impl.DDLRecordVersionModelImpl</code>.
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

		return _ddlRecordVersionLocalService.dynamicQuery(
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

		return _ddlRecordVersionLocalService.dynamicQueryCount(dynamicQuery);
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

		return _ddlRecordVersionLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public DDLRecordVersion fetchDDLRecordVersion(long recordVersionId) {
		return _ddlRecordVersionLocalService.fetchDDLRecordVersion(
			recordVersionId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _ddlRecordVersionLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the ddl record version with the primary key.
	 *
	 * @param recordVersionId the primary key of the ddl record version
	 * @return the ddl record version
	 * @throws PortalException if a ddl record version with the primary key could not be found
	 */
	@Override
	public DDLRecordVersion getDDLRecordVersion(long recordVersionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddlRecordVersionLocalService.getDDLRecordVersion(
			recordVersionId);
	}

	/**
	 * Returns a range of all the ddl record versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.lists.model.impl.DDLRecordVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddl record versions
	 * @param end the upper bound of the range of ddl record versions (not inclusive)
	 * @return the range of ddl record versions
	 */
	@Override
	public java.util.List<DDLRecordVersion> getDDLRecordVersions(
		int start, int end) {

		return _ddlRecordVersionLocalService.getDDLRecordVersions(start, end);
	}

	/**
	 * Returns the number of ddl record versions.
	 *
	 * @return the number of ddl record versions
	 */
	@Override
	public int getDDLRecordVersionsCount() {
		return _ddlRecordVersionLocalService.getDDLRecordVersionsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _ddlRecordVersionLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the record's latest record version.
	 *
	 * @param recordId the primary key of the record
	 * @return the latest record version for the given record
	 * @throws PortalException if a portal exception occurred
	 */
	@Override
	public DDLRecordVersion getLatestRecordVersion(long recordId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddlRecordVersionLocalService.getLatestRecordVersion(recordId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _ddlRecordVersionLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddlRecordVersionLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the record version by its ID.
	 *
	 * @param recordVersionId the primary key of the record version
	 * @return the record version with the ID
	 * @throws PortalException if a matching record set could not be found
	 */
	@Override
	public DDLRecordVersion getRecordVersion(long recordVersionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddlRecordVersionLocalService.getRecordVersion(recordVersionId);
	}

	/**
	 * Returns the record version matching the record and version.
	 *
	 * @param recordId the primary key of the record
	 * @param version the record version
	 * @return the record version matching the record primary key and version
	 * @throws PortalException if a matching record set could not be found
	 */
	@Override
	public DDLRecordVersion getRecordVersion(long recordId, String version)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddlRecordVersionLocalService.getRecordVersion(
			recordId, version);
	}

	/**
	 * Returns an ordered range of record versions matching the record's ID.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to <code>QueryUtil.ALL_POS</code> will return the
	 * full result set.
	 * </p>
	 *
	 * @param recordId the primary key of the record
	 * @param start the lower bound of the range of record versions to return
	 * @param end the upper bound of the range of record versions to return
	 (not inclusive)
	 * @param orderByComparator the comparator used to order the record
	 versions
	 * @return the range of matching record versions ordered by the comparator
	 */
	@Override
	public java.util.List<DDLRecordVersion> getRecordVersions(
		long recordId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDLRecordVersion>
			orderByComparator) {

		return _ddlRecordVersionLocalService.getRecordVersions(
			recordId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of record versions matching the record ID.
	 *
	 * @param recordId the primary key of the record
	 * @return the number of matching record versions
	 */
	@Override
	public int getRecordVersionsCount(long recordId) {
		return _ddlRecordVersionLocalService.getRecordVersionsCount(recordId);
	}

	/**
	 * Updates the ddl record version in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDLRecordVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddlRecordVersion the ddl record version
	 * @return the ddl record version that was updated
	 */
	@Override
	public DDLRecordVersion updateDDLRecordVersion(
		DDLRecordVersion ddlRecordVersion) {

		return _ddlRecordVersionLocalService.updateDDLRecordVersion(
			ddlRecordVersion);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _ddlRecordVersionLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<DDLRecordVersion> getCTPersistence() {
		return _ddlRecordVersionLocalService.getCTPersistence();
	}

	@Override
	public Class<DDLRecordVersion> getModelClass() {
		return _ddlRecordVersionLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<DDLRecordVersion>, R, E>
				updateUnsafeFunction)
		throws E {

		return _ddlRecordVersionLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public DDLRecordVersionLocalService getWrappedService() {
		return _ddlRecordVersionLocalService;
	}

	@Override
	public void setWrappedService(
		DDLRecordVersionLocalService ddlRecordVersionLocalService) {

		_ddlRecordVersionLocalService = ddlRecordVersionLocalService;
	}

	private DDLRecordVersionLocalService _ddlRecordVersionLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1996526469