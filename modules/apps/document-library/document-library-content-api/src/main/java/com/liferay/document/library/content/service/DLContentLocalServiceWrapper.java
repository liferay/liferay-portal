/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.content.service;

import com.liferay.document.library.content.model.DLContent;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link DLContentLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see DLContentLocalService
 * @generated
 */
public class DLContentLocalServiceWrapper
	implements DLContentLocalService, ServiceWrapper<DLContentLocalService> {

	public DLContentLocalServiceWrapper() {
		this(null);
	}

	public DLContentLocalServiceWrapper(
		DLContentLocalService dlContentLocalService) {

		_dlContentLocalService = dlContentLocalService;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 #addContent(long, long, String, String, InputStream)}
	 */
	@Deprecated
	@Override
	public DLContent addContent(
		long companyId, long repositoryId, String path, String version,
		byte[] bytes) {

		return _dlContentLocalService.addContent(
			companyId, repositoryId, path, version, bytes);
	}

	@Override
	public DLContent addContent(
		long companyId, long repositoryId, String path, String version,
		java.io.InputStream inputStream) {

		return _dlContentLocalService.addContent(
			companyId, repositoryId, path, version, inputStream);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 #addContent(long, long, String, String, InputStream)}
	 */
	@Deprecated
	@Override
	public DLContent addContent(
		long companyId, long repositoryId, String path, String version,
		java.io.InputStream inputStream, long size) {

		return _dlContentLocalService.addContent(
			companyId, repositoryId, path, version, inputStream, size);
	}

	/**
	 * Adds the document library content to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLContentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlContent the document library content
	 * @return the document library content that was added
	 */
	@Override
	public DLContent addDLContent(DLContent dlContent) {
		return _dlContentLocalService.addDLContent(dlContent);
	}

	/**
	 * Creates a new document library content with the primary key. Does not add the document library content to the database.
	 *
	 * @param contentId the primary key for the new document library content
	 * @return the new document library content
	 */
	@Override
	public DLContent createDLContent(long contentId) {
		return _dlContentLocalService.createDLContent(contentId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlContentLocalService.createPersistedModel(primaryKeyObj);
	}

	@Override
	public void deleteContent(
		long companyId, long repositoryId, String path, String version) {

		_dlContentLocalService.deleteContent(
			companyId, repositoryId, path, version);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	@Override
	public void deleteContents(long companyId, long repositoryId, String path) {
		_dlContentLocalService.deleteContents(companyId, repositoryId, path);
	}

	@Override
	public void deleteContentsByDirectory(
		long companyId, long repositoryId, String dirName) {

		_dlContentLocalService.deleteContentsByDirectory(
			companyId, repositoryId, dirName);
	}

	/**
	 * Deletes the document library content from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLContentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlContent the document library content
	 * @return the document library content that was removed
	 */
	@Override
	public DLContent deleteDLContent(DLContent dlContent) {
		return _dlContentLocalService.deleteDLContent(dlContent);
	}

	/**
	 * Deletes the document library content with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLContentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param contentId the primary key of the document library content
	 * @return the document library content that was removed
	 * @throws PortalException if a document library content with the primary key could not be found
	 */
	@Override
	public DLContent deleteDLContent(long contentId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlContentLocalService.deleteDLContent(contentId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlContentLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _dlContentLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _dlContentLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _dlContentLocalService.dynamicQuery();
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

		return _dlContentLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.document.library.content.model.impl.DLContentModelImpl</code>.
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

		return _dlContentLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.document.library.content.model.impl.DLContentModelImpl</code>.
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

		return _dlContentLocalService.dynamicQuery(
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

		return _dlContentLocalService.dynamicQueryCount(dynamicQuery);
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

		return _dlContentLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public DLContent fetchDLContent(long contentId) {
		return _dlContentLocalService.fetchDLContent(contentId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _dlContentLocalService.getActionableDynamicQuery();
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 #getContent(long, long, String, String)}
	 */
	@Deprecated
	@Override
	public DLContent getContent(long companyId, long repositoryId, String path)
		throws com.liferay.document.library.content.exception.
			NoSuchContentException {

		return _dlContentLocalService.getContent(companyId, repositoryId, path);
	}

	@Override
	public DLContent getContent(
			long companyId, long repositoryId, String path, String version)
		throws com.liferay.document.library.content.exception.
			NoSuchContentException {

		return _dlContentLocalService.getContent(
			companyId, repositoryId, path, version);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 #getContentsByDirectory(long, long, String)}
	 */
	@Deprecated
	@Override
	public java.util.List<DLContent> getContents(
		long companyId, long repositoryId) {

		return _dlContentLocalService.getContents(companyId, repositoryId);
	}

	@Override
	public java.util.List<DLContent> getContents(
		long companyId, long repositoryId, String path) {

		return _dlContentLocalService.getContents(
			companyId, repositoryId, path);
	}

	@Override
	public java.util.List<DLContent> getContentsByDirectory(
		long companyId, long repositoryId, String dirName) {

		return _dlContentLocalService.getContentsByDirectory(
			companyId, repositoryId, dirName);
	}

	/**
	 * Returns the document library content with the primary key.
	 *
	 * @param contentId the primary key of the document library content
	 * @return the document library content
	 * @throws PortalException if a document library content with the primary key could not be found
	 */
	@Override
	public DLContent getDLContent(long contentId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlContentLocalService.getDLContent(contentId);
	}

	/**
	 * Returns a range of all the document library contents.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.document.library.content.model.impl.DLContentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of document library contents
	 * @param end the upper bound of the range of document library contents (not inclusive)
	 * @return the range of document library contents
	 */
	@Override
	public java.util.List<DLContent> getDLContents(int start, int end) {
		return _dlContentLocalService.getDLContents(start, end);
	}

	/**
	 * Returns the number of document library contents.
	 *
	 * @return the number of document library contents
	 */
	@Override
	public int getDLContentsCount() {
		return _dlContentLocalService.getDLContentsCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _dlContentLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _dlContentLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlContentLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public boolean hasContent(
		long companyId, long repositoryId, String path, String version) {

		return _dlContentLocalService.hasContent(
			companyId, repositoryId, path, version);
	}

	/**
	 * Updates the document library content in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLContentLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlContent the document library content
	 * @return the document library content that was updated
	 */
	@Override
	public DLContent updateDLContent(DLContent dlContent) {
		return _dlContentLocalService.updateDLContent(dlContent);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	@Override
	public void updateDLContent(
		long companyId, long oldRepositoryId, long newRepositoryId,
		String oldPath, String newPath) {

		_dlContentLocalService.updateDLContent(
			companyId, oldRepositoryId, newRepositoryId, oldPath, newPath);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _dlContentLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<DLContent> getCTPersistence() {
		return _dlContentLocalService.getCTPersistence();
	}

	@Override
	public Class<DLContent> getModelClass() {
		return _dlContentLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<DLContent>, R, E> updateUnsafeFunction)
		throws E {

		return _dlContentLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public DLContentLocalService getWrappedService() {
		return _dlContentLocalService;
	}

	@Override
	public void setWrappedService(DLContentLocalService dlContentLocalService) {
		_dlContentLocalService = dlContentLocalService;
	}

	private DLContentLocalService _dlContentLocalService;

}