/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.store.service;

import com.liferay.change.tracking.store.model.CTSContent;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link CTSContentLocalService}.
 *
 * @author Shuyang Zhou
 * @see CTSContentLocalService
 * @generated
 */
public class CTSContentLocalServiceWrapper
	implements CTSContentLocalService, ServiceWrapper<CTSContentLocalService> {

	public CTSContentLocalServiceWrapper() {
		this(null);
	}

	public CTSContentLocalServiceWrapper(
		CTSContentLocalService ctsContentLocalService) {

		_ctsContentLocalService = ctsContentLocalService;
	}

	/**
	 * Adds the cts content to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTSContentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctsContent the cts content
	 * @return the cts content that was added
	 */
	@Override
	public CTSContent addCTSContent(CTSContent ctsContent) {
		return _ctsContentLocalService.addCTSContent(ctsContent);
	}

	@Override
	public CTSContent addCTSContent(
		long companyId, long repositoryId, String path, String version,
		String storeType, java.io.InputStream inputStream) {

		return _ctsContentLocalService.addCTSContent(
			companyId, repositoryId, path, version, storeType, inputStream);
	}

	/**
	 * Creates a new cts content with the primary key. Does not add the cts content to the database.
	 *
	 * @param ctsContentId the primary key for the new cts content
	 * @return the new cts content
	 */
	@Override
	public CTSContent createCTSContent(long ctsContentId) {
		return _ctsContentLocalService.createCTSContent(ctsContentId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctsContentLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the cts content from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTSContentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctsContent the cts content
	 * @return the cts content that was removed
	 */
	@Override
	public CTSContent deleteCTSContent(CTSContent ctsContent) {
		return _ctsContentLocalService.deleteCTSContent(ctsContent);
	}

	/**
	 * Deletes the cts content with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTSContentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctsContentId the primary key of the cts content
	 * @return the cts content that was removed
	 * @throws PortalException if a cts content with the primary key could not be found
	 */
	@Override
	public CTSContent deleteCTSContent(long ctsContentId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctsContentLocalService.deleteCTSContent(ctsContentId);
	}

	@Override
	public void deleteCTSContent(
		long companyId, long repositoryId, String path, String version,
		String storeType) {

		_ctsContentLocalService.deleteCTSContent(
			companyId, repositoryId, path, version, storeType);
	}

	@Override
	public void deleteCTSContentsByDirectory(
		long companyId, long repositoryId, String dirName, String storeType) {

		_ctsContentLocalService.deleteCTSContentsByDirectory(
			companyId, repositoryId, dirName, storeType);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctsContentLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _ctsContentLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _ctsContentLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _ctsContentLocalService.dynamicQuery();
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

		return _ctsContentLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.store.model.impl.CTSContentModelImpl</code>.
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

		return _ctsContentLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.store.model.impl.CTSContentModelImpl</code>.
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

		return _ctsContentLocalService.dynamicQuery(
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

		return _ctsContentLocalService.dynamicQueryCount(dynamicQuery);
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

		return _ctsContentLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public CTSContent fetchCTSContent(long ctsContentId) {
		return _ctsContentLocalService.fetchCTSContent(ctsContentId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _ctsContentLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the cts content with the primary key.
	 *
	 * @param ctsContentId the primary key of the cts content
	 * @return the cts content
	 * @throws PortalException if a cts content with the primary key could not be found
	 */
	@Override
	public CTSContent getCTSContent(long ctsContentId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctsContentLocalService.getCTSContent(ctsContentId);
	}

	@Override
	public CTSContent getCTSContent(
			long companyId, long repositoryId, String path, String version,
			String storeType)
		throws com.liferay.change.tracking.store.exception.
			NoSuchContentException {

		return _ctsContentLocalService.getCTSContent(
			companyId, repositoryId, path, version, storeType);
	}

	/**
	 * Returns a range of all the cts contents.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.store.model.impl.CTSContentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cts contents
	 * @param end the upper bound of the range of cts contents (not inclusive)
	 * @return the range of cts contents
	 */
	@Override
	public java.util.List<CTSContent> getCTSContents(int start, int end) {
		return _ctsContentLocalService.getCTSContents(start, end);
	}

	@Override
	public java.util.List<CTSContent> getCTSContents(
		long companyId, long repositoryId, String path, String storeType) {

		return _ctsContentLocalService.getCTSContents(
			companyId, repositoryId, path, storeType);
	}

	@Override
	public java.util.List<CTSContent> getCTSContentsByDirectory(
		long companyId, long repositoryId, String dirName, String storeType) {

		return _ctsContentLocalService.getCTSContentsByDirectory(
			companyId, repositoryId, dirName, storeType);
	}

	/**
	 * Returns the number of cts contents.
	 *
	 * @return the number of cts contents
	 */
	@Override
	public int getCTSContentsCount() {
		return _ctsContentLocalService.getCTSContentsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _ctsContentLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _ctsContentLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctsContentLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public boolean hasCTSContent(
		long companyId, long repositoryId, String path, String version,
		String storeType) {

		return _ctsContentLocalService.hasCTSContent(
			companyId, repositoryId, path, version, storeType);
	}

	/**
	 * Updates the cts content in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTSContentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctsContent the cts content
	 * @return the cts content that was updated
	 */
	@Override
	public CTSContent updateCTSContent(CTSContent ctsContent) {
		return _ctsContentLocalService.updateCTSContent(ctsContent);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _ctsContentLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<CTSContent> getCTPersistence() {
		return _ctsContentLocalService.getCTPersistence();
	}

	@Override
	public Class<CTSContent> getModelClass() {
		return _ctsContentLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<CTSContent>, R, E>
				updateUnsafeFunction)
		throws E {

		return _ctsContentLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public CTSContentLocalService getWrappedService() {
		return _ctsContentLocalService;
	}

	@Override
	public void setWrappedService(
		CTSContentLocalService ctsContentLocalService) {

		_ctsContentLocalService = ctsContentLocalService;
	}

	private CTSContentLocalService _ctsContentLocalService;

}