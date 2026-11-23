/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;

/**
 * Provides a wrapper for {@link KaleoDefinitionVersionLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see KaleoDefinitionVersionLocalService
 * @generated
 */
public class KaleoDefinitionVersionLocalServiceWrapper
	implements KaleoDefinitionVersionLocalService,
			   ServiceWrapper<KaleoDefinitionVersionLocalService> {

	public KaleoDefinitionVersionLocalServiceWrapper() {
		this(null);
	}

	public KaleoDefinitionVersionLocalServiceWrapper(
		KaleoDefinitionVersionLocalService kaleoDefinitionVersionLocalService) {

		_kaleoDefinitionVersionLocalService =
			kaleoDefinitionVersionLocalService;
	}

	/**
	 * Adds the kaleo definition version to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoDefinitionVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoDefinitionVersion the kaleo definition version
	 * @return the kaleo definition version that was added
	 */
	@Override
	public KaleoDefinitionVersion addKaleoDefinitionVersion(
		KaleoDefinitionVersion kaleoDefinitionVersion) {

		return _kaleoDefinitionVersionLocalService.addKaleoDefinitionVersion(
			kaleoDefinitionVersion);
	}

	@Override
	public KaleoDefinitionVersion addKaleoDefinitionVersion(
			long kaleoDefinitionId, String name, String title,
			String description, String content, String version,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoDefinitionVersionLocalService.addKaleoDefinitionVersion(
			kaleoDefinitionId, name, title, description, content, version,
			serviceContext);
	}

	/**
	 * Creates a new kaleo definition version with the primary key. Does not add the kaleo definition version to the database.
	 *
	 * @param kaleoDefinitionVersionId the primary key for the new kaleo definition version
	 * @return the new kaleo definition version
	 */
	@Override
	public KaleoDefinitionVersion createKaleoDefinitionVersion(
		long kaleoDefinitionVersionId) {

		return _kaleoDefinitionVersionLocalService.createKaleoDefinitionVersion(
			kaleoDefinitionVersionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoDefinitionVersionLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the kaleo definition version from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoDefinitionVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoDefinitionVersion the kaleo definition version
	 * @return the kaleo definition version that was removed
	 * @throws PortalException
	 */
	@Override
	public KaleoDefinitionVersion deleteKaleoDefinitionVersion(
			KaleoDefinitionVersion kaleoDefinitionVersion)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoDefinitionVersionLocalService.deleteKaleoDefinitionVersion(
			kaleoDefinitionVersion);
	}

	/**
	 * Deletes the kaleo definition version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoDefinitionVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoDefinitionVersionId the primary key of the kaleo definition version
	 * @return the kaleo definition version that was removed
	 * @throws PortalException if a kaleo definition version with the primary key could not be found
	 */
	@Override
	public KaleoDefinitionVersion deleteKaleoDefinitionVersion(
			long kaleoDefinitionVersionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoDefinitionVersionLocalService.deleteKaleoDefinitionVersion(
			kaleoDefinitionVersionId);
	}

	@Override
	public void deleteKaleoDefinitionVersion(
			long companyId, String name, String version)
		throws com.liferay.portal.kernel.exception.PortalException {

		_kaleoDefinitionVersionLocalService.deleteKaleoDefinitionVersion(
			companyId, name, version);
	}

	@Override
	public void deleteKaleoDefinitionVersions(
			com.liferay.portal.workflow.kaleo.model.KaleoDefinition
				kaleoDefinition)
		throws com.liferay.portal.kernel.exception.PortalException {

		_kaleoDefinitionVersionLocalService.deleteKaleoDefinitionVersions(
			kaleoDefinition);
	}

	@Override
	public void deleteKaleoDefinitionVersions(
			java.util.List<KaleoDefinitionVersion> kaleoDefinitionVersions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_kaleoDefinitionVersionLocalService.deleteKaleoDefinitionVersions(
			kaleoDefinitionVersions);
	}

	@Override
	public void deleteKaleoDefinitionVersions(long companyId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		_kaleoDefinitionVersionLocalService.deleteKaleoDefinitionVersions(
			companyId, name);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoDefinitionVersionLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _kaleoDefinitionVersionLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _kaleoDefinitionVersionLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _kaleoDefinitionVersionLocalService.dynamicQuery();
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

		return _kaleoDefinitionVersionLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoDefinitionVersionModelImpl</code>.
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

		return _kaleoDefinitionVersionLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoDefinitionVersionModelImpl</code>.
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

		return _kaleoDefinitionVersionLocalService.dynamicQuery(
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

		return _kaleoDefinitionVersionLocalService.dynamicQueryCount(
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

		return _kaleoDefinitionVersionLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public KaleoDefinitionVersion fetchKaleoDefinitionVersion(
		long kaleoDefinitionVersionId) {

		return _kaleoDefinitionVersionLocalService.fetchKaleoDefinitionVersion(
			kaleoDefinitionVersionId);
	}

	@Override
	public KaleoDefinitionVersion fetchKaleoDefinitionVersion(
		long companyId, String name, String version) {

		return _kaleoDefinitionVersionLocalService.fetchKaleoDefinitionVersion(
			companyId, name, version);
	}

	@Override
	public KaleoDefinitionVersion fetchLatestKaleoDefinitionVersion(
			long companyId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoDefinitionVersionLocalService.
			fetchLatestKaleoDefinitionVersion(companyId, name);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _kaleoDefinitionVersionLocalService.getActionableDynamicQuery();
	}

	@Override
	public KaleoDefinitionVersion getFirstKaleoDefinitionVersion(
			long companyId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoDefinitionVersionLocalService.
			getFirstKaleoDefinitionVersion(companyId, name);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _kaleoDefinitionVersionLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the kaleo definition version with the primary key.
	 *
	 * @param kaleoDefinitionVersionId the primary key of the kaleo definition version
	 * @return the kaleo definition version
	 * @throws PortalException if a kaleo definition version with the primary key could not be found
	 */
	@Override
	public KaleoDefinitionVersion getKaleoDefinitionVersion(
			long kaleoDefinitionVersionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoDefinitionVersionLocalService.getKaleoDefinitionVersion(
			kaleoDefinitionVersionId);
	}

	@Override
	public KaleoDefinitionVersion getKaleoDefinitionVersion(
			long companyId, String name, String version)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoDefinitionVersionLocalService.getKaleoDefinitionVersion(
			companyId, name, version);
	}

	/**
	 * Returns a range of all the kaleo definition versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoDefinitionVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kaleo definition versions
	 * @param end the upper bound of the range of kaleo definition versions (not inclusive)
	 * @return the range of kaleo definition versions
	 */
	@Override
	public java.util.List<KaleoDefinitionVersion> getKaleoDefinitionVersions(
		int start, int end) {

		return _kaleoDefinitionVersionLocalService.getKaleoDefinitionVersions(
			start, end);
	}

	@Override
	public java.util.List<KaleoDefinitionVersion> getKaleoDefinitionVersions(
			long companyId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoDefinitionVersionLocalService.getKaleoDefinitionVersions(
			companyId, name);
	}

	@Override
	public java.util.List<KaleoDefinitionVersion> getKaleoDefinitionVersions(
		long companyId, String name, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KaleoDefinitionVersion>
			orderByComparator) {

		return _kaleoDefinitionVersionLocalService.getKaleoDefinitionVersions(
			companyId, name, start, end, orderByComparator);
	}

	/**
	 * Returns the number of kaleo definition versions.
	 *
	 * @return the number of kaleo definition versions
	 */
	@Override
	public int getKaleoDefinitionVersionsCount() {
		return _kaleoDefinitionVersionLocalService.
			getKaleoDefinitionVersionsCount();
	}

	@Override
	public int getKaleoDefinitionVersionsCount(long companyId, String name) {
		return _kaleoDefinitionVersionLocalService.
			getKaleoDefinitionVersionsCount(companyId, name);
	}

	@Override
	public KaleoDefinitionVersion[] getKaleoDefinitionVersionsPrevAndNext(
			long companyId, String name, String version)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoDefinitionVersionLocalService.
			getKaleoDefinitionVersionsPrevAndNext(companyId, name, version);
	}

	@Override
	public KaleoDefinitionVersion getLatestKaleoDefinitionVersion(
			long companyId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoDefinitionVersionLocalService.
			getLatestKaleoDefinitionVersion(companyId, name);
	}

	@Override
	public java.util.List<KaleoDefinitionVersion>
		getLatestKaleoDefinitionVersions(
			long companyId, String keywords, int status,
			java.util.Locale locale, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<KaleoDefinitionVersion> orderByComparator) {

		return _kaleoDefinitionVersionLocalService.
			getLatestKaleoDefinitionVersions(
				companyId, keywords, status, locale, start, end,
				orderByComparator);
	}

	@Override
	public int getLatestKaleoDefinitionVersionsCount(
		long companyId, String keywords, int status) {

		return _kaleoDefinitionVersionLocalService.
			getLatestKaleoDefinitionVersionsCount(companyId, keywords, status);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _kaleoDefinitionVersionLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoDefinitionVersionLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the kaleo definition version in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoDefinitionVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoDefinitionVersion the kaleo definition version
	 * @return the kaleo definition version that was updated
	 */
	@Override
	public KaleoDefinitionVersion updateKaleoDefinitionVersion(
		KaleoDefinitionVersion kaleoDefinitionVersion) {

		return _kaleoDefinitionVersionLocalService.updateKaleoDefinitionVersion(
			kaleoDefinitionVersion);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _kaleoDefinitionVersionLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<KaleoDefinitionVersion> getCTPersistence() {
		return _kaleoDefinitionVersionLocalService.getCTPersistence();
	}

	@Override
	public Class<KaleoDefinitionVersion> getModelClass() {
		return _kaleoDefinitionVersionLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<KaleoDefinitionVersion>, R, E>
				updateUnsafeFunction)
		throws E {

		return _kaleoDefinitionVersionLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public KaleoDefinitionVersionLocalService getWrappedService() {
		return _kaleoDefinitionVersionLocalService;
	}

	@Override
	public void setWrappedService(
		KaleoDefinitionVersionLocalService kaleoDefinitionVersionLocalService) {

		_kaleoDefinitionVersionLocalService =
			kaleoDefinitionVersionLocalService;
	}

	private KaleoDefinitionVersionLocalService
		_kaleoDefinitionVersionLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1562565370