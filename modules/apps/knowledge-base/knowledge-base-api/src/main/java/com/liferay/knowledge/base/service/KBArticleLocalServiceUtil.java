/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.service;

import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.InputStream;
import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for KBArticle. This utility wraps
 * <code>com.liferay.knowledge.base.service.impl.KBArticleLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see KBArticleLocalService
 * @generated
 */
public class KBArticleLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.knowledge.base.service.impl.KBArticleLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static com.liferay.portal.kernel.repository.model.FileEntry
			addAttachment(
				long userId, long resourcePrimKey, String fileName,
				InputStream inputStream, String mimeType)
		throws PortalException {

		return getService().addAttachment(
			userId, resourcePrimKey, fileName, inputStream, mimeType);
	}

	/**
	 * Adds the kb article to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KBArticleLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kbArticle the kb article
	 * @return the kb article that was added
	 */
	public static KBArticle addKBArticle(KBArticle kbArticle) {
		return getService().addKBArticle(kbArticle);
	}

	public static KBArticle addKBArticle(
			String externalReferenceCode, long userId,
			long parentResourceClassNameId, long parentResourcePrimKey,
			String title, String urlTitle, String content, String description,
			String[] sections, String sourceURL, java.util.Date displayDate,
			java.util.Date expirationDate, java.util.Date reviewDate,
			String[] selectedFileNames,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addKBArticle(
			externalReferenceCode, userId, parentResourceClassNameId,
			parentResourcePrimKey, title, urlTitle, content, description,
			sections, sourceURL, displayDate, expirationDate, reviewDate,
			selectedFileNames, serviceContext);
	}

	public static void addKBArticleResources(
			KBArticle kbArticle, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		getService().addKBArticleResources(
			kbArticle, addGroupPermissions, addGuestPermissions);
	}

	public static void addKBArticleResources(
			KBArticle kbArticle,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws PortalException {

		getService().addKBArticleResources(kbArticle, modelPermissions);
	}

	public static void addKBArticleResources(
			long kbArticleId, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		getService().addKBArticleResources(
			kbArticleId, addGroupPermissions, addGuestPermissions);
	}

	public static int addKBArticlesMarkdown(
			long userId, long groupId, long parentKbFolderId, String fileName,
			boolean prioritizeByNumericalPrefix, InputStream inputStream,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addKBArticlesMarkdown(
			userId, groupId, parentKbFolderId, fileName,
			prioritizeByNumericalPrefix, inputStream, serviceContext);
	}

	public static void addTempAttachment(
			long groupId, long userId, String fileName, String tempFolderName,
			InputStream inputStream, String mimeType)
		throws PortalException {

		getService().addTempAttachment(
			groupId, userId, fileName, tempFolderName, inputStream, mimeType);
	}

	public static void checkKBArticles(long companyId) throws PortalException {
		getService().checkKBArticles(companyId);
	}

	/**
	 * Creates a new kb article with the primary key. Does not add the kb article to the database.
	 *
	 * @param kbArticleId the primary key for the new kb article
	 * @return the new kb article
	 */
	public static KBArticle createKBArticle(long kbArticleId) {
		return getService().createKBArticle(kbArticleId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteGroupKBArticles(long groupId)
		throws PortalException {

		getService().deleteGroupKBArticles(groupId);
	}

	/**
	 * Deletes the kb article from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KBArticleLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kbArticle the kb article
	 * @return the kb article that was removed
	 * @throws PortalException
	 */
	public static KBArticle deleteKBArticle(KBArticle kbArticle)
		throws PortalException {

		return getService().deleteKBArticle(kbArticle);
	}

	/**
	 * Deletes the kb article with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KBArticleLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kbArticleId the primary key of the kb article
	 * @return the kb article that was removed
	 * @throws PortalException if a kb article with the primary key could not be found
	 */
	public static KBArticle deleteKBArticle(long kbArticleId)
		throws PortalException {

		return getService().deleteKBArticle(kbArticleId);
	}

	public static KBArticle deleteKBArticle(
			long userId, long resourcePrimKey, int version)
		throws PortalException {

		return getService().deleteKBArticle(userId, resourcePrimKey, version);
	}

	public static void deleteKBArticles(
			long groupId, long parentResourcePrimKey)
		throws PortalException {

		getService().deleteKBArticles(groupId, parentResourcePrimKey);
	}

	public static void deleteKBArticles(
			long groupId, long parentResourcePrimKey,
			boolean includeTrashedEntries)
		throws PortalException {

		getService().deleteKBArticles(
			groupId, parentResourcePrimKey, includeTrashedEntries);
	}

	public static void deleteKBArticles(long[] resourcePrimKeys)
		throws PortalException {

		getService().deleteKBArticles(resourcePrimKeys);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static void deleteTempAttachment(
			long groupId, long userId, String fileName, String tempFolderName)
		throws PortalException {

		getService().deleteTempAttachment(
			groupId, userId, fileName, tempFolderName);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBArticleModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBArticleModelImpl</code>.
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

	public static KBArticle expireKBArticle(
			long userId, long resourcePrimKey,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().expireKBArticle(
			userId, resourcePrimKey, serviceContext);
	}

	public static KBArticle fetchFirstChildKBArticle(
		long groupId, long parentResourcePrimKey) {

		return getService().fetchFirstChildKBArticle(
			groupId, parentResourcePrimKey);
	}

	public static KBArticle fetchKBArticle(long kbArticleId) {
		return getService().fetchKBArticle(kbArticleId);
	}

	public static KBArticle fetchKBArticle(
		long resourcePrimKey, long groupId, int version) {

		return getService().fetchKBArticle(resourcePrimKey, groupId, version);
	}

	public static KBArticle fetchKBArticleByExternalReferenceCode(
		long groupId, String externalReferenceCode, int version) {

		return getService().fetchKBArticleByExternalReferenceCode(
			groupId, externalReferenceCode, version);
	}

	public static KBArticle fetchKBArticleByUrlTitle(
		long groupId, long kbFolderId, String urlTitle) {

		return getService().fetchKBArticleByUrlTitle(
			groupId, kbFolderId, urlTitle);
	}

	public static KBArticle fetchKBArticleByUrlTitle(
		long groupId, String kbFolderUrlTitle, String urlTitle) {

		return getService().fetchKBArticleByUrlTitle(
			groupId, kbFolderUrlTitle, urlTitle);
	}

	/**
	 * Returns the kb article matching the UUID and group.
	 *
	 * @param uuid the kb article's UUID
	 * @param groupId the primary key of the group
	 * @return the matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchKBArticleByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchKBArticleByUuidAndGroupId(uuid, groupId);
	}

	public static KBArticle fetchLatestKBArticle(
		long resourcePrimKey, int status) {

		return getService().fetchLatestKBArticle(resourcePrimKey, status);
	}

	public static KBArticle fetchLatestKBArticle(
		long resourcePrimKey, long groupId) {

		return getService().fetchLatestKBArticle(resourcePrimKey, groupId);
	}

	public static KBArticle fetchLatestKBArticleByExternalReferenceCode(
		long groupId, String externalReferenceCode) {

		return getService().fetchLatestKBArticleByExternalReferenceCode(
			groupId, externalReferenceCode);
	}

	public static KBArticle fetchLatestKBArticleByExternalReferenceCode(
		long groupId, String externalReferenceCode, int status) {

		return getService().fetchLatestKBArticleByExternalReferenceCode(
			groupId, externalReferenceCode, status);
	}

	public static KBArticle fetchLatestKBArticleByUrlTitle(
		long groupId, long kbFolderId, String urlTitle, int status) {

		return getService().fetchLatestKBArticleByUrlTitle(
			groupId, kbFolderId, urlTitle, status);
	}

	public static PersistedModel fetchPersistedModel(
		Serializable primaryKeyObj) {

		return getService().fetchPersistedModel(primaryKeyObj);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<KBArticle> getAllDescendantKBArticles(
		long resourcePrimKey, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getService().getAllDescendantKBArticles(
			resourcePrimKey, status, orderByComparator);
	}

	public static List<KBArticle> getCompanyKBArticles(
		long companyId, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getService().getCompanyKBArticles(
			companyId, status, start, end, orderByComparator);
	}

	public static int getCompanyKBArticlesCount(long companyId, int status) {
		return getService().getCompanyKBArticlesCount(companyId, status);
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static List<KBArticle> getGroupKBArticles(
		long groupId, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getService().getGroupKBArticles(
			groupId, status, start, end, orderByComparator);
	}

	public static int getGroupKBArticlesCount(long groupId, int status) {
		return getService().getGroupKBArticlesCount(groupId, status);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the kb article with the primary key.
	 *
	 * @param kbArticleId the primary key of the kb article
	 * @return the kb article
	 * @throws PortalException if a kb article with the primary key could not be found
	 */
	public static KBArticle getKBArticle(long kbArticleId)
		throws PortalException {

		return getService().getKBArticle(kbArticleId);
	}

	public static KBArticle getKBArticle(long resourcePrimKey, int version)
		throws PortalException {

		return getService().getKBArticle(resourcePrimKey, version);
	}

	public static List<KBArticle> getKBArticleAndAllDescendantKBArticles(
		long resourcePrimKey, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getService().getKBArticleAndAllDescendantKBArticles(
			resourcePrimKey, status, orderByComparator);
	}

	public static KBArticle getKBArticleByUrlTitle(
			long groupId, long kbFolderId, String urlTitle)
		throws PortalException {

		return getService().getKBArticleByUrlTitle(
			groupId, kbFolderId, urlTitle);
	}

	public static KBArticle getKBArticleByUrlTitle(
			long groupId, String kbFolderUrlTitle, String urlTitle)
		throws PortalException {

		return getService().getKBArticleByUrlTitle(
			groupId, kbFolderUrlTitle, urlTitle);
	}

	/**
	 * Returns the kb article matching the UUID and group.
	 *
	 * @param uuid the kb article's UUID
	 * @param groupId the primary key of the group
	 * @return the matching kb article
	 * @throws PortalException if a matching kb article could not be found
	 */
	public static KBArticle getKBArticleByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getKBArticleByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the kb articles.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of kb articles
	 */
	public static List<KBArticle> getKBArticles(int start, int end) {
		return getService().getKBArticles(start, end);
	}

	public static List<KBArticle> getKBArticles(
		long groupId, long parentResourcePrimKey, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return getService().getKBArticles(
			groupId, parentResourcePrimKey, status, start, end,
			orderByComparator);
	}

	public static List<KBArticle> getKBArticles(
		long[] resourcePrimKeys, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getService().getKBArticles(
			resourcePrimKeys, status, orderByComparator);
	}

	/**
	 * Returns all the kb articles matching the UUID and company.
	 *
	 * @param uuid the UUID of the kb articles
	 * @param companyId the primary key of the company
	 * @return the matching kb articles, or an empty list if no matches were found
	 */
	public static List<KBArticle> getKBArticlesByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getKBArticlesByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of kb articles matching the UUID and company.
	 *
	 * @param uuid the UUID of the kb articles
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching kb articles, or an empty list if no matches were found
	 */
	public static List<KBArticle> getKBArticlesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getService().getKBArticlesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of kb articles.
	 *
	 * @return the number of kb articles
	 */
	public static int getKBArticlesCount() {
		return getService().getKBArticlesCount();
	}

	public static int getKBArticlesCount(
		long groupId, long parentResourcePrimKey, int status) {

		return getService().getKBArticlesCount(
			groupId, parentResourcePrimKey, status);
	}

	public static List<KBArticle> getKBArticleVersions(
		long resourcePrimKey, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getService().getKBArticleVersions(
			resourcePrimKey, status, start, end, orderByComparator);
	}

	public static int getKBArticleVersionsCount(
		long resourcePrimKey, int status) {

		return getService().getKBArticleVersionsCount(resourcePrimKey, status);
	}

	public static List<KBArticle> getKBFolderKBArticles(
		long groupId, long kbFolderId) {

		return getService().getKBFolderKBArticles(groupId, kbFolderId);
	}

	public static int getKBFolderKBArticlesCount(
		long groupId, long kbFolderId, int status) {

		return getService().getKBFolderKBArticlesCount(
			groupId, kbFolderId, status);
	}

	public static KBArticle getLatestKBArticle(long resourcePrimKey)
		throws PortalException {

		return getService().getLatestKBArticle(resourcePrimKey);
	}

	public static KBArticle getLatestKBArticle(long resourcePrimKey, int status)
		throws PortalException {

		return getService().getLatestKBArticle(resourcePrimKey, status);
	}

	public static KBArticle getLatestKBArticle(
			long resourcePrimKey, int[] statuses)
		throws PortalException {

		return getService().getLatestKBArticle(resourcePrimKey, statuses);
	}

	public static KBArticle getLatestKBArticleByExternalReferenceCode(
			long groupId, String externalReferenceCode)
		throws PortalException {

		return getService().getLatestKBArticleByExternalReferenceCode(
			groupId, externalReferenceCode);
	}

	public static KBArticle getLatestKBArticleByUrlTitle(
			long groupId, long kbFolderId, String urlTitle, int status)
		throws PortalException {

		return getService().getLatestKBArticleByUrlTitle(
			groupId, kbFolderId, urlTitle, status);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static List<? extends PersistedModel> getPersistedModel(
			long resourcePrimKey)
		throws PortalException {

		return getService().getPersistedModel(resourcePrimKey);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	public static KBArticle[] getPreviousAndNextKBArticles(long kbArticleId)
		throws PortalException {

		return getService().getPreviousAndNextKBArticles(kbArticleId);
	}

	public static List<KBArticle> getSectionsKBArticles(
		long groupId, String[] sections, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getService().getSectionsKBArticles(
			groupId, sections, status, start, end, orderByComparator);
	}

	public static int getSectionsKBArticlesCount(
		long groupId, String[] sections, int status) {

		return getService().getSectionsKBArticlesCount(
			groupId, sections, status);
	}

	public static String[] getTempAttachmentNames(
			long groupId, long userId, String tempFolderName)
		throws PortalException {

		return getService().getTempAttachmentNames(
			groupId, userId, tempFolderName);
	}

	public static boolean hasKBArticleLock(long userId, long resourcePrimKey) {
		return getService().hasKBArticleLock(userId, resourcePrimKey);
	}

	public static void incrementViewCount(
			long userId, long resourcePrimKey, int increment)
		throws PortalException {

		getService().incrementViewCount(userId, resourcePrimKey, increment);
	}

	public static com.liferay.portal.kernel.lock.Lock lockKBArticle(
			long userId, long resourcePrimKey)
		throws PortalException {

		return getService().lockKBArticle(userId, resourcePrimKey);
	}

	public static void moveDependentKBArticlesToTrash(
			long parentResourcePrimKey, long trashEntryId)
		throws PortalException {

		getService().moveDependentKBArticlesToTrash(
			parentResourcePrimKey, trashEntryId);
	}

	public static void moveDependentKBArticleToTrash(
			KBArticle kbArticle, long trashEntryId)
		throws PortalException {

		getService().moveDependentKBArticleToTrash(kbArticle, trashEntryId);
	}

	public static void moveKBArticle(
			long userId, long resourcePrimKey, long parentResourceClassNameId,
			long parentResourcePrimKey, double priority)
		throws PortalException {

		getService().moveKBArticle(
			userId, resourcePrimKey, parentResourceClassNameId,
			parentResourcePrimKey, priority);
	}

	public static void moveKBArticleFromTrash(
			long userId, long resourcePrimKey, long parentResourceClassNameId,
			long parentResourcePrimKey)
		throws PortalException {

		getService().moveKBArticleFromTrash(
			userId, resourcePrimKey, parentResourceClassNameId,
			parentResourcePrimKey);
	}

	public static KBArticle moveKBArticleToTrash(
			long userId, long resourcePrimKey)
		throws PortalException {

		return getService().moveKBArticleToTrash(userId, resourcePrimKey);
	}

	public static void restoreDependentKBArticleFromTrash(KBArticle kbArticle)
		throws PortalException {

		getService().restoreDependentKBArticleFromTrash(kbArticle);
	}

	public static void restoreDependentKBArticlesFromTrash(
			long parentResourcePrimKey)
		throws PortalException {

		getService().restoreDependentKBArticlesFromTrash(parentResourcePrimKey);
	}

	public static void restoreKBArticleFromTrash(
			long userId, long resourcePrimKey)
		throws PortalException {

		getService().restoreKBArticleFromTrash(userId, resourcePrimKey);
	}

	public static KBArticle revertKBArticle(
			long userId, long resourcePrimKey, int version,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().revertKBArticle(
			userId, resourcePrimKey, version, serviceContext);
	}

	public static List<KBArticle> search(
		long groupId, String title, String content, int status,
		java.util.Date startDate, java.util.Date endDate, boolean andOperator,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return getService().search(
			groupId, title, content, status, startDate, endDate, andOperator,
			start, end, orderByComparator);
	}

	public static void subscribeGroupKBArticles(long userId, long groupId)
		throws PortalException {

		getService().subscribeGroupKBArticles(userId, groupId);
	}

	public static void subscribeKBArticle(
			long userId, long groupId, long resourcePrimKey)
		throws PortalException {

		getService().subscribeKBArticle(userId, groupId, resourcePrimKey);
	}

	public static void unlockKBArticle(long userId, long resourcePrimKey) {
		getService().unlockKBArticle(userId, resourcePrimKey);
	}

	public static void unlockKBArticle(
		long userId, long resourcePrimKey, boolean force) {

		getService().unlockKBArticle(userId, resourcePrimKey, force);
	}

	public static void unsubscribeGroupKBArticles(long userId, long groupId)
		throws PortalException {

		getService().unsubscribeGroupKBArticles(userId, groupId);
	}

	public static void unsubscribeKBArticle(long userId, long resourcePrimKey)
		throws PortalException {

		getService().unsubscribeKBArticle(userId, resourcePrimKey);
	}

	public static KBArticle updateAndUnlockKBArticle(
			long userId, long resourcePrimKey, String title, String content,
			String description, String[] sections, String sourceURL,
			java.util.Date displayDate, java.util.Date expirationDate,
			java.util.Date reviewDate, String[] selectedFileNames,
			long[] removeFileEntryIds,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateAndUnlockKBArticle(
			userId, resourcePrimKey, title, content, description, sections,
			sourceURL, displayDate, expirationDate, reviewDate,
			selectedFileNames, removeFileEntryIds, serviceContext);
	}

	/**
	 * Updates the kb article in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KBArticleLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kbArticle the kb article
	 * @return the kb article that was updated
	 */
	public static KBArticle updateKBArticle(KBArticle kbArticle) {
		return getService().updateKBArticle(kbArticle);
	}

	public static KBArticle updateKBArticle(
			long userId, long resourcePrimKey, String title, String content,
			String description, String[] sections, String sourceURL,
			java.util.Date displayDate, java.util.Date expirationDate,
			java.util.Date reviewDate, String[] selectedFileNames,
			long[] removeFileEntryIds,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateKBArticle(
			userId, resourcePrimKey, title, content, description, sections,
			sourceURL, displayDate, expirationDate, reviewDate,
			selectedFileNames, removeFileEntryIds, serviceContext);
	}

	public static void updateKBArticleAsset(
			long userId, KBArticle kbArticle, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds)
		throws PortalException {

		getService().updateKBArticleAsset(
			userId, kbArticle, assetCategoryIds, assetTagNames,
			assetLinkEntryIds);
	}

	public static void updateKBArticleResources(
			KBArticle kbArticle, String[] groupPermissions,
			String[] guestPermissions)
		throws PortalException {

		getService().updateKBArticleResources(
			kbArticle, groupPermissions, guestPermissions);
	}

	public static void updateKBArticlesPriorities(
			Map<Long, Double> resourcePrimKeyToPriorityMap)
		throws PortalException {

		getService().updateKBArticlesPriorities(resourcePrimKeyToPriorityMap);
	}

	public static void updatePriority(long resourcePrimKey, double priority) {
		getService().updatePriority(resourcePrimKey, priority);
	}

	public static KBArticle updateStatus(
			long userId, long resourcePrimKey, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateStatus(
			userId, resourcePrimKey, status, serviceContext);
	}

	public static KBArticleLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<KBArticleLocalService> _serviceSnapshot =
		new Snapshot<>(
			KBArticleLocalServiceUtil.class, KBArticleLocalService.class);

}