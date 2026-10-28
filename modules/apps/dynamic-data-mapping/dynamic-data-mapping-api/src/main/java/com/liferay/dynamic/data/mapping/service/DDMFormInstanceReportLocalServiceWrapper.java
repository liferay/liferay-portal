/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service;

import com.liferay.dynamic.data.mapping.model.DDMFormInstanceReport;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link DDMFormInstanceReportLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see DDMFormInstanceReportLocalService
 * @generated
 */
public class DDMFormInstanceReportLocalServiceWrapper
	implements DDMFormInstanceReportLocalService,
			   ServiceWrapper<DDMFormInstanceReportLocalService> {

	public DDMFormInstanceReportLocalServiceWrapper() {
		this(null);
	}

	public DDMFormInstanceReportLocalServiceWrapper(
		DDMFormInstanceReportLocalService ddmFormInstanceReportLocalService) {

		_ddmFormInstanceReportLocalService = ddmFormInstanceReportLocalService;
	}

	/**
	 * Adds the ddm form instance report to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMFormInstanceReportLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmFormInstanceReport the ddm form instance report
	 * @return the ddm form instance report that was added
	 */
	@Override
	public DDMFormInstanceReport addDDMFormInstanceReport(
		DDMFormInstanceReport ddmFormInstanceReport) {

		return _ddmFormInstanceReportLocalService.addDDMFormInstanceReport(
			ddmFormInstanceReport);
	}

	@Override
	public DDMFormInstanceReport addFormInstanceReport(long formInstanceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmFormInstanceReportLocalService.addFormInstanceReport(
			formInstanceId);
	}

	/**
	 * Creates a new ddm form instance report with the primary key. Does not add the ddm form instance report to the database.
	 *
	 * @param formInstanceReportId the primary key for the new ddm form instance report
	 * @return the new ddm form instance report
	 */
	@Override
	public DDMFormInstanceReport createDDMFormInstanceReport(
		long formInstanceReportId) {

		return _ddmFormInstanceReportLocalService.createDDMFormInstanceReport(
			formInstanceReportId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmFormInstanceReportLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the ddm form instance report from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMFormInstanceReportLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmFormInstanceReport the ddm form instance report
	 * @return the ddm form instance report that was removed
	 */
	@Override
	public DDMFormInstanceReport deleteDDMFormInstanceReport(
		DDMFormInstanceReport ddmFormInstanceReport) {

		return _ddmFormInstanceReportLocalService.deleteDDMFormInstanceReport(
			ddmFormInstanceReport);
	}

	/**
	 * Deletes the ddm form instance report with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMFormInstanceReportLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param formInstanceReportId the primary key of the ddm form instance report
	 * @return the ddm form instance report that was removed
	 * @throws PortalException if a ddm form instance report with the primary key could not be found
	 */
	@Override
	public DDMFormInstanceReport deleteDDMFormInstanceReport(
			long formInstanceReportId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmFormInstanceReportLocalService.deleteDDMFormInstanceReport(
			formInstanceReportId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmFormInstanceReportLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _ddmFormInstanceReportLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _ddmFormInstanceReportLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _ddmFormInstanceReportLocalService.dynamicQuery();
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

		return _ddmFormInstanceReportLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceReportModelImpl</code>.
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

		return _ddmFormInstanceReportLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceReportModelImpl</code>.
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

		return _ddmFormInstanceReportLocalService.dynamicQuery(
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

		return _ddmFormInstanceReportLocalService.dynamicQueryCount(
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

		return _ddmFormInstanceReportLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public DDMFormInstanceReport fetchDDMFormInstanceReport(
		long formInstanceReportId) {

		return _ddmFormInstanceReportLocalService.fetchDDMFormInstanceReport(
			formInstanceReportId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _ddmFormInstanceReportLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the ddm form instance report with the primary key.
	 *
	 * @param formInstanceReportId the primary key of the ddm form instance report
	 * @return the ddm form instance report
	 * @throws PortalException if a ddm form instance report with the primary key could not be found
	 */
	@Override
	public DDMFormInstanceReport getDDMFormInstanceReport(
			long formInstanceReportId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmFormInstanceReportLocalService.getDDMFormInstanceReport(
			formInstanceReportId);
	}

	/**
	 * Returns a range of all the ddm form instance reports.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMFormInstanceReportModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddm form instance reports
	 * @param end the upper bound of the range of ddm form instance reports (not inclusive)
	 * @return the range of ddm form instance reports
	 */
	@Override
	public java.util.List<DDMFormInstanceReport> getDDMFormInstanceReports(
		int start, int end) {

		return _ddmFormInstanceReportLocalService.getDDMFormInstanceReports(
			start, end);
	}

	/**
	 * Returns the number of ddm form instance reports.
	 *
	 * @return the number of ddm form instance reports
	 */
	@Override
	public int getDDMFormInstanceReportsCount() {
		return _ddmFormInstanceReportLocalService.
			getDDMFormInstanceReportsCount();
	}

	@Override
	public DDMFormInstanceReport getFormInstanceReportByFormInstanceId(
			long formInstanceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmFormInstanceReportLocalService.
			getFormInstanceReportByFormInstanceId(formInstanceId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _ddmFormInstanceReportLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _ddmFormInstanceReportLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmFormInstanceReportLocalService.getPersistedModel(
			primaryKeyObj);
	}

	@Override
	public void processFormInstanceReportEvent(
		long formInstanceReportId, long formInstanceRecordVersionId,
		String ddmFormInstanceReportEvent) {

		_ddmFormInstanceReportLocalService.processFormInstanceReportEvent(
			formInstanceReportId, formInstanceRecordVersionId,
			ddmFormInstanceReportEvent);
	}

	/**
	 * Updates the ddm form instance report in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMFormInstanceReportLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmFormInstanceReport the ddm form instance report
	 * @return the ddm form instance report that was updated
	 */
	@Override
	public DDMFormInstanceReport updateDDMFormInstanceReport(
		DDMFormInstanceReport ddmFormInstanceReport) {

		return _ddmFormInstanceReportLocalService.updateDDMFormInstanceReport(
			ddmFormInstanceReport);
	}

	@Override
	public DDMFormInstanceReport updateFormInstanceReport(
			long formInstanceReportId, long formInstanceRecordVersionId,
			String ddmFormInstanceReportEvent)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmFormInstanceReportLocalService.updateFormInstanceReport(
			formInstanceReportId, formInstanceRecordVersionId,
			ddmFormInstanceReportEvent);
	}

	@Override
	public DDMFormInstanceReport updateFormInstanceReportByFormInstanceId(
			long formInstanceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmFormInstanceReportLocalService.
			updateFormInstanceReportByFormInstanceId(formInstanceId);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _ddmFormInstanceReportLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<DDMFormInstanceReport> getCTPersistence() {
		return _ddmFormInstanceReportLocalService.getCTPersistence();
	}

	@Override
	public Class<DDMFormInstanceReport> getModelClass() {
		return _ddmFormInstanceReportLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<DDMFormInstanceReport>, R, E>
				updateUnsafeFunction)
		throws E {

		return _ddmFormInstanceReportLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public DDMFormInstanceReportLocalService getWrappedService() {
		return _ddmFormInstanceReportLocalService;
	}

	@Override
	public void setWrappedService(
		DDMFormInstanceReportLocalService ddmFormInstanceReportLocalService) {

		_ddmFormInstanceReportLocalService = ddmFormInstanceReportLocalService;
	}

	private DDMFormInstanceReportLocalService
		_ddmFormInstanceReportLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:180111363