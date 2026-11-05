/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service;

import com.liferay.dynamic.data.mapping.model.DDMTemplateVersion;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link DDMTemplateVersionLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see DDMTemplateVersionLocalService
 * @generated
 */
public class DDMTemplateVersionLocalServiceWrapper
	implements DDMTemplateVersionLocalService,
			   ServiceWrapper<DDMTemplateVersionLocalService> {

	public DDMTemplateVersionLocalServiceWrapper() {
		this(null);
	}

	public DDMTemplateVersionLocalServiceWrapper(
		DDMTemplateVersionLocalService ddmTemplateVersionLocalService) {

		_ddmTemplateVersionLocalService = ddmTemplateVersionLocalService;
	}

	/**
	 * Adds the ddm template version to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMTemplateVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmTemplateVersion the ddm template version
	 * @return the ddm template version that was added
	 */
	@Override
	public DDMTemplateVersion addDDMTemplateVersion(
		DDMTemplateVersion ddmTemplateVersion) {

		return _ddmTemplateVersionLocalService.addDDMTemplateVersion(
			ddmTemplateVersion);
	}

	/**
	 * Creates a new ddm template version with the primary key. Does not add the ddm template version to the database.
	 *
	 * @param templateVersionId the primary key for the new ddm template version
	 * @return the new ddm template version
	 */
	@Override
	public DDMTemplateVersion createDDMTemplateVersion(long templateVersionId) {
		return _ddmTemplateVersionLocalService.createDDMTemplateVersion(
			templateVersionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmTemplateVersionLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the ddm template version from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMTemplateVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmTemplateVersion the ddm template version
	 * @return the ddm template version that was removed
	 */
	@Override
	public DDMTemplateVersion deleteDDMTemplateVersion(
		DDMTemplateVersion ddmTemplateVersion) {

		return _ddmTemplateVersionLocalService.deleteDDMTemplateVersion(
			ddmTemplateVersion);
	}

	/**
	 * Deletes the ddm template version with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMTemplateVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param templateVersionId the primary key of the ddm template version
	 * @return the ddm template version that was removed
	 * @throws PortalException if a ddm template version with the primary key could not be found
	 */
	@Override
	public DDMTemplateVersion deleteDDMTemplateVersion(long templateVersionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmTemplateVersionLocalService.deleteDDMTemplateVersion(
			templateVersionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmTemplateVersionLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public void deleteTemplateVersions(long templateId) {
		_ddmTemplateVersionLocalService.deleteTemplateVersions(templateId);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _ddmTemplateVersionLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _ddmTemplateVersionLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _ddmTemplateVersionLocalService.dynamicQuery();
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

		return _ddmTemplateVersionLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMTemplateVersionModelImpl</code>.
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

		return _ddmTemplateVersionLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMTemplateVersionModelImpl</code>.
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

		return _ddmTemplateVersionLocalService.dynamicQuery(
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

		return _ddmTemplateVersionLocalService.dynamicQueryCount(dynamicQuery);
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

		return _ddmTemplateVersionLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public DDMTemplateVersion fetchDDMTemplateVersion(long templateVersionId) {
		return _ddmTemplateVersionLocalService.fetchDDMTemplateVersion(
			templateVersionId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _ddmTemplateVersionLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the ddm template version with the primary key.
	 *
	 * @param templateVersionId the primary key of the ddm template version
	 * @return the ddm template version
	 * @throws PortalException if a ddm template version with the primary key could not be found
	 */
	@Override
	public DDMTemplateVersion getDDMTemplateVersion(long templateVersionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmTemplateVersionLocalService.getDDMTemplateVersion(
			templateVersionId);
	}

	/**
	 * Returns a range of all the ddm template versions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMTemplateVersionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddm template versions
	 * @param end the upper bound of the range of ddm template versions (not inclusive)
	 * @return the range of ddm template versions
	 */
	@Override
	public java.util.List<DDMTemplateVersion> getDDMTemplateVersions(
		int start, int end) {

		return _ddmTemplateVersionLocalService.getDDMTemplateVersions(
			start, end);
	}

	/**
	 * Returns the number of ddm template versions.
	 *
	 * @return the number of ddm template versions
	 */
	@Override
	public int getDDMTemplateVersionsCount() {
		return _ddmTemplateVersionLocalService.getDDMTemplateVersionsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _ddmTemplateVersionLocalService.
			getIndexableActionableDynamicQuery();
	}

	@Override
	public DDMTemplateVersion getLatestTemplateVersion(long templateId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmTemplateVersionLocalService.getLatestTemplateVersion(
			templateId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _ddmTemplateVersionLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmTemplateVersionLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public DDMTemplateVersion getTemplateVersion(
			long templateId, String version)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmTemplateVersionLocalService.getTemplateVersion(
			templateId, version);
	}

	@Override
	public java.util.List<DDMTemplateVersion> getTemplateVersions(
		long templateId) {

		return _ddmTemplateVersionLocalService.getTemplateVersions(templateId);
	}

	/**
	 * Updates the ddm template version in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMTemplateVersionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmTemplateVersion the ddm template version
	 * @return the ddm template version that was updated
	 */
	@Override
	public DDMTemplateVersion updateDDMTemplateVersion(
		DDMTemplateVersion ddmTemplateVersion) {

		return _ddmTemplateVersionLocalService.updateDDMTemplateVersion(
			ddmTemplateVersion);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _ddmTemplateVersionLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<DDMTemplateVersion> getCTPersistence() {
		return _ddmTemplateVersionLocalService.getCTPersistence();
	}

	@Override
	public Class<DDMTemplateVersion> getModelClass() {
		return _ddmTemplateVersionLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<DDMTemplateVersion>, R, E>
				updateUnsafeFunction)
		throws E {

		return _ddmTemplateVersionLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public DDMTemplateVersionLocalService getWrappedService() {
		return _ddmTemplateVersionLocalService;
	}

	@Override
	public void setWrappedService(
		DDMTemplateVersionLocalService ddmTemplateVersionLocalService) {

		_ddmTemplateVersionLocalService = ddmTemplateVersionLocalService;
	}

	private DDMTemplateVersionLocalService _ddmTemplateVersionLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1170926001