/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.content.service;

import com.liferay.document.library.content.model.DLContent;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.InputStream;
import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for DLContent. This utility wraps
 * <code>com.liferay.document.library.content.service.impl.DLContentLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see DLContentLocalService
 * @generated
 */
public class DLContentLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.document.library.content.service.impl.DLContentLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 #addContent(long, long, String, String, InputStream)}
	 */
	@Deprecated
	public static DLContent addContent(
		long companyId, long repositoryId, String path, String version,
		byte[] bytes) {

		return getService().addContent(
			companyId, repositoryId, path, version, bytes);
	}

	public static DLContent addContent(
		long companyId, long repositoryId, String path, String version,
		InputStream inputStream) {

		return getService().addContent(
			companyId, repositoryId, path, version, inputStream);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 #addContent(long, long, String, String, InputStream)}
	 */
	@Deprecated
	public static DLContent addContent(
		long companyId, long repositoryId, String path, String version,
		InputStream inputStream, long size) {

		return getService().addContent(
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
	public static DLContent addDLContent(DLContent dlContent) {
		return getService().addDLContent(dlContent);
	}

	/**
	 * Creates a new document library content with the primary key. Does not add the document library content to the database.
	 *
	 * @param contentId the primary key for the new document library content
	 * @return the new document library content
	 */
	public static DLContent createDLContent(long contentId) {
		return getService().createDLContent(contentId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteContent(
		long companyId, long repositoryId, String path, String version) {

		getService().deleteContent(companyId, repositoryId, path, version);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	public static void deleteContents(
		long companyId, long repositoryId, String path) {

		getService().deleteContents(companyId, repositoryId, path);
	}

	public static void deleteContentsByDirectory(
		long companyId, long repositoryId, String dirName) {

		getService().deleteContentsByDirectory(
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
	public static DLContent deleteDLContent(DLContent dlContent) {
		return getService().deleteDLContent(dlContent);
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
	public static DLContent deleteDLContent(long contentId)
		throws PortalException {

		return getService().deleteDLContent(contentId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static <T> T dslQuery(DSLQuery dslQuery) {
		return getService().dslQuery(dslQuery);
	}

	public static int dslQueryCount(DSLQuery dslQuery) {
		return getService().dslQueryCount(dslQuery);
	}

	public static DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static DLContent fetchDLContent(long contentId) {
		return getService().fetchDLContent(contentId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 #getContent(long, long, String, String)}
	 */
	@Deprecated
	public static DLContent getContent(
			long companyId, long repositoryId, String path)
		throws com.liferay.document.library.content.exception.
			NoSuchContentException {

		return getService().getContent(companyId, repositoryId, path);
	}

	public static DLContent getContent(
			long companyId, long repositoryId, String path, String version)
		throws com.liferay.document.library.content.exception.
			NoSuchContentException {

		return getService().getContent(companyId, repositoryId, path, version);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 #getContentsByDirectory(long, long, String)}
	 */
	@Deprecated
	public static List<DLContent> getContents(
		long companyId, long repositoryId) {

		return getService().getContents(companyId, repositoryId);
	}

	public static List<DLContent> getContents(
		long companyId, long repositoryId, String path) {

		return getService().getContents(companyId, repositoryId, path);
	}

	public static List<DLContent> getContentsByDirectory(
		long companyId, long repositoryId, String dirName) {

		return getService().getContentsByDirectory(
			companyId, repositoryId, dirName);
	}

	/**
	 * Returns the document library content with the primary key.
	 *
	 * @param contentId the primary key of the document library content
	 * @return the document library content
	 * @throws PortalException if a document library content with the primary key could not be found
	 */
	public static DLContent getDLContent(long contentId)
		throws PortalException {

		return getService().getDLContent(contentId);
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
	public static List<DLContent> getDLContents(int start, int end) {
		return getService().getDLContents(start, end);
	}

	/**
	 * Returns the number of document library contents.
	 *
	 * @return the number of document library contents
	 */
	public static int getDLContentsCount() {
		return getService().getDLContentsCount();
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	public static boolean hasContent(
		long companyId, long repositoryId, String path, String version) {

		return getService().hasContent(companyId, repositoryId, path, version);
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
	public static DLContent updateDLContent(DLContent dlContent) {
		return getService().updateDLContent(dlContent);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	public static void updateDLContent(
		long companyId, long oldRepositoryId, long newRepositoryId,
		String oldPath, String newPath) {

		getService().updateDLContent(
			companyId, oldRepositoryId, newRepositoryId, oldPath, newPath);
	}

	public static DLContentLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<DLContentLocalService> _serviceSnapshot =
		new Snapshot<>(
			DLContentLocalServiceUtil.class, DLContentLocalService.class);

}