/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.sample.service;

import com.liferay.change.tracking.sample.model.CTSChild;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CTSChildLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see CTSChildLocalService
 * @generated
 */
public class CTSChildLocalServiceWrapper
	implements CTSChildLocalService, ServiceWrapper<CTSChildLocalService> {

	public CTSChildLocalServiceWrapper() {
		this(null);
	}

	public CTSChildLocalServiceWrapper(
		CTSChildLocalService ctsChildLocalService) {

		_ctsChildLocalService = ctsChildLocalService;
	}

	/**
	 * Adds the cts child to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTSChildLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctsChild the cts child
	 * @return the cts child that was added
	 */
	@Override
	public CTSChild addCTSChild(CTSChild ctsChild) {
		return _ctsChildLocalService.addCTSChild(ctsChild);
	}

	@Override
	public CTSChild addCTSChild(long companyId) {
		return _ctsChildLocalService.addCTSChild(companyId);
	}

	@Override
	public CTSChild addCTSChild(
		long companyId, long ctsGrandParentId, long parentCTSChildId,
		String ctsParentName) {

		return _ctsChildLocalService.addCTSChild(
			companyId, ctsGrandParentId, parentCTSChildId, ctsParentName);
	}

	/**
	 * Creates a new cts child with the primary key. Does not add the cts child to the database.
	 *
	 * @param ctsChildId the primary key for the new cts child
	 * @return the new cts child
	 */
	@Override
	public CTSChild createCTSChild(long ctsChildId) {
		return _ctsChildLocalService.createCTSChild(ctsChildId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctsChildLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the cts child from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTSChildLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctsChild the cts child
	 * @return the cts child that was removed
	 */
	@Override
	public CTSChild deleteCTSChild(CTSChild ctsChild) {
		return _ctsChildLocalService.deleteCTSChild(ctsChild);
	}

	/**
	 * Deletes the cts child with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTSChildLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctsChildId the primary key of the cts child
	 * @return the cts child that was removed
	 * @throws PortalException if a cts child with the primary key could not be found
	 */
	@Override
	public CTSChild deleteCTSChild(long ctsChildId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctsChildLocalService.deleteCTSChild(ctsChildId);
	}

	@Override
	public void deleteCTSChildren(long companyId) {
		_ctsChildLocalService.deleteCTSChildren(companyId);
	}

	@Override
	public void deleteCTSChildrenByCTSGrandParentId(
		long companyId, long ctsGrandParentId) {

		_ctsChildLocalService.deleteCTSChildrenByCTSGrandParentId(
			companyId, ctsGrandParentId);
	}

	@Override
	public void deleteCTSChildrenByParentCTSChildId(
		long companyId, long parentCTSChildId) {

		_ctsChildLocalService.deleteCTSChildrenByParentCTSChildId(
			companyId, parentCTSChildId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctsChildLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _ctsChildLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _ctsChildLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _ctsChildLocalService.dynamicQuery();
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

		return _ctsChildLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.sample.model.impl.CTSChildModelImpl</code>.
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

		return _ctsChildLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.sample.model.impl.CTSChildModelImpl</code>.
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

		return _ctsChildLocalService.dynamicQuery(
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

		return _ctsChildLocalService.dynamicQueryCount(dynamicQuery);
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

		return _ctsChildLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public CTSChild fetchCTSChild(long ctsChildId) {
		return _ctsChildLocalService.fetchCTSChild(ctsChildId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _ctsChildLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the cts child with the primary key.
	 *
	 * @param ctsChildId the primary key of the cts child
	 * @return the cts child
	 * @throws PortalException if a cts child with the primary key could not be found
	 */
	@Override
	public CTSChild getCTSChild(long ctsChildId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctsChildLocalService.getCTSChild(ctsChildId);
	}

	@Override
	public java.util.List<CTSChild> getCTSChildren(long companyId) {
		return _ctsChildLocalService.getCTSChildren(companyId);
	}

	@Override
	public java.util.List<CTSChild> getCTSChildrenByCTSGrandParentId(
		long ctsGrandParentId) {

		return _ctsChildLocalService.getCTSChildrenByCTSGrandParentId(
			ctsGrandParentId);
	}

	@Override
	public java.util.List<CTSChild> getCTSChildrenByParentCTSChildId(
			long parentCTSChildId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctsChildLocalService.getCTSChildrenByParentCTSChildId(
			parentCTSChildId);
	}

	/**
	 * Returns a range of all the cts childs.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.sample.model.impl.CTSChildModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cts childs
	 * @param end the upper bound of the range of cts childs (not inclusive)
	 * @return the range of cts childs
	 */
	@Override
	public java.util.List<CTSChild> getCTSChilds(int start, int end) {
		return _ctsChildLocalService.getCTSChilds(start, end);
	}

	/**
	 * Returns the number of cts childs.
	 *
	 * @return the number of cts childs
	 */
	@Override
	public int getCTSChildsCount() {
		return _ctsChildLocalService.getCTSChildsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _ctsChildLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _ctsChildLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctsChildLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the cts child in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTSChildLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctsChild the cts child
	 * @return the cts child that was updated
	 */
	@Override
	public CTSChild updateCTSChild(CTSChild ctsChild) {
		return _ctsChildLocalService.updateCTSChild(ctsChild);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _ctsChildLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<CTSChild> getCTPersistence() {
		return _ctsChildLocalService.getCTPersistence();
	}

	@Override
	public Class<CTSChild> getModelClass() {
		return _ctsChildLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CTSChild>, R, E> updateUnsafeFunction)
		throws E {

		return _ctsChildLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public CTSChildLocalService getWrappedService() {
		return _ctsChildLocalService;
	}

	@Override
	public void setWrappedService(CTSChildLocalService ctsChildLocalService) {
		_ctsChildLocalService = ctsChildLocalService;
	}

	private CTSChildLocalService _ctsChildLocalService;

}