/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.store.service;

import com.liferay.change.tracking.store.model.CTSContent;
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
 * Provides the local service utility for CTSContent. This utility wraps
 * <code>com.liferay.change.tracking.store.service.impl.CTSContentLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Shuyang Zhou
 * @see CTSContentLocalService
 * @generated
 */
public class CTSContentLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.change.tracking.store.service.impl.CTSContentLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

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
	public static CTSContent addCTSContent(CTSContent ctsContent) {
		return getService().addCTSContent(ctsContent);
	}

	public static CTSContent addCTSContent(
		long companyId, long repositoryId, String path, String version,
		String storeType, InputStream inputStream) {

		return getService().addCTSContent(
			companyId, repositoryId, path, version, storeType, inputStream);
	}

	/**
	 * Creates a new cts content with the primary key. Does not add the cts content to the database.
	 *
	 * @param ctsContentId the primary key for the new cts content
	 * @return the new cts content
	 */
	public static CTSContent createCTSContent(long ctsContentId) {
		return getService().createCTSContent(ctsContentId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
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
	public static CTSContent deleteCTSContent(CTSContent ctsContent) {
		return getService().deleteCTSContent(ctsContent);
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
	public static CTSContent deleteCTSContent(long ctsContentId)
		throws PortalException {

		return getService().deleteCTSContent(ctsContentId);
	}

	public static void deleteCTSContent(
		long companyId, long repositoryId, String path, String version,
		String storeType) {

		getService().deleteCTSContent(
			companyId, repositoryId, path, version, storeType);
	}

	public static void deleteCTSContentsByDirectory(
		long companyId, long repositoryId, String dirName, String storeType) {

		getService().deleteCTSContentsByDirectory(
			companyId, repositoryId, dirName, storeType);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.store.model.impl.CTSContentModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.store.model.impl.CTSContentModelImpl</code>.
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

	public static CTSContent fetchCTSContent(long ctsContentId) {
		return getService().fetchCTSContent(ctsContentId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the cts content with the primary key.
	 *
	 * @param ctsContentId the primary key of the cts content
	 * @return the cts content
	 * @throws PortalException if a cts content with the primary key could not be found
	 */
	public static CTSContent getCTSContent(long ctsContentId)
		throws PortalException {

		return getService().getCTSContent(ctsContentId);
	}

	public static CTSContent getCTSContent(
			long companyId, long repositoryId, String path, String version,
			String storeType)
		throws com.liferay.change.tracking.store.exception.
			NoSuchContentException {

		return getService().getCTSContent(
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
	public static List<CTSContent> getCTSContents(int start, int end) {
		return getService().getCTSContents(start, end);
	}

	public static List<CTSContent> getCTSContents(
		long companyId, long repositoryId, String path, String storeType) {

		return getService().getCTSContents(
			companyId, repositoryId, path, storeType);
	}

	public static List<CTSContent> getCTSContentsByDirectory(
		long companyId, long repositoryId, String dirName, String storeType) {

		return getService().getCTSContentsByDirectory(
			companyId, repositoryId, dirName, storeType);
	}

	/**
	 * Returns the number of cts contents.
	 *
	 * @return the number of cts contents
	 */
	public static int getCTSContentsCount() {
		return getService().getCTSContentsCount();
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

	public static boolean hasCTSContent(
		long companyId, long repositoryId, String path, String version,
		String storeType) {

		return getService().hasCTSContent(
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
	public static CTSContent updateCTSContent(CTSContent ctsContent) {
		return getService().updateCTSContent(ctsContent);
	}

	public static CTSContentLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<CTSContentLocalService> _serviceSnapshot =
		new Snapshot<>(
			CTSContentLocalServiceUtil.class, CTSContentLocalService.class);

}