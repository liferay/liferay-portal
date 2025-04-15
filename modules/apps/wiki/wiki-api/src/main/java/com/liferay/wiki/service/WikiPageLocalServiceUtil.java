/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.wiki.model.WikiPage;

import java.io.InputStream;
import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for WikiPage. This utility wraps
 * <code>com.liferay.wiki.service.impl.WikiPageLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see WikiPageLocalService
 * @generated
 */
public class WikiPageLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.wiki.service.impl.WikiPageLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link #addPage(String,
	 long, long, String, double, String, String, boolean, String,
	 boolean, String, String, ServiceContext)}
	 */
	@Deprecated
	public static WikiPage addPage(
			long userId, long nodeId, String title, double version,
			String content, String summary, boolean minorEdit, String format,
			boolean head, String parentTitle, String redirectTitle,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addPage(
			userId, nodeId, title, version, content, summary, minorEdit, format,
			head, parentTitle, redirectTitle, serviceContext);
	}

	public static WikiPage addPage(
			long userId, long nodeId, String title, String content,
			String summary, boolean minorEdit,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addPage(
			userId, nodeId, title, content, summary, minorEdit, serviceContext);
	}

	public static WikiPage addPage(
			String externalReferenceCode, long userId, long nodeId,
			String title, double version, String content, String summary,
			boolean minorEdit, String format, boolean head, String parentTitle,
			String redirectTitle,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addPage(
			externalReferenceCode, userId, nodeId, title, version, content,
			summary, minorEdit, format, head, parentTitle, redirectTitle,
			serviceContext);
	}

	public static com.liferay.portal.kernel.repository.model.FileEntry
			addPageAttachment(
				long userId, long nodeId, String title, String fileName,
				java.io.File file, String mimeType)
		throws PortalException {

		return getService().addPageAttachment(
			userId, nodeId, title, fileName, file, mimeType);
	}

	public static com.liferay.portal.kernel.repository.model.FileEntry
			addPageAttachment(
				long userId, long nodeId, String title, String fileName,
				InputStream inputStream, String mimeType)
		throws PortalException {

		return getService().addPageAttachment(
			userId, nodeId, title, fileName, inputStream, mimeType);
	}

	public static List<com.liferay.portal.kernel.repository.model.FileEntry>
			addPageAttachments(
				long userId, long nodeId, String title,
				List
					<com.liferay.portal.kernel.util.ObjectValuePair
						<String, InputStream>> inputStreamOVPs)
		throws PortalException {

		return getService().addPageAttachments(
			userId, nodeId, title, inputStreamOVPs);
	}

	public static void addPageResources(
			long nodeId, String title, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		getService().addPageResources(
			nodeId, title, addGroupPermissions, addGuestPermissions);
	}

	public static void addPageResources(
			WikiPage page, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		getService().addPageResources(
			page, addGroupPermissions, addGuestPermissions);
	}

	public static void addPageResources(
			WikiPage page,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws PortalException {

		getService().addPageResources(page, modelPermissions);
	}

	public static com.liferay.portal.kernel.repository.model.FileEntry
			addTempFileEntry(
				long groupId, long userId, String folderName, String fileName,
				InputStream inputStream, String mimeType)
		throws PortalException {

		return getService().addTempFileEntry(
			groupId, userId, folderName, fileName, inputStream, mimeType);
	}

	/**
	 * Adds the wiki page to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect WikiPageLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param wikiPage the wiki page
	 * @return the wiki page that was added
	 */
	public static WikiPage addWikiPage(WikiPage wikiPage) {
		return getService().addWikiPage(wikiPage);
	}

	public static WikiPage changeParent(
			long userId, long nodeId, String title, String newParentTitle,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().changeParent(
			userId, nodeId, title, newParentTitle, serviceContext);
	}

	public static void copyPageAttachments(
			long userId, long templateNodeId, String templateTitle, long nodeId,
			String title)
		throws PortalException {

		getService().copyPageAttachments(
			userId, templateNodeId, templateTitle, nodeId, title);
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
	 * Creates a new wiki page with the primary key. Does not add the wiki page to the database.
	 *
	 * @param pageId the primary key for the new wiki page
	 * @return the new wiki page
	 */
	public static WikiPage createWikiPage(long pageId) {
		return getService().createWikiPage(pageId);
	}

	public static void deletePage(long nodeId, String title)
		throws PortalException {

		getService().deletePage(nodeId, title);
	}

	public static void deletePage(WikiPage page) throws PortalException {
		getService().deletePage(page);
	}

	public static void deletePageAttachment(
			long nodeId, String title, String fileName)
		throws PortalException {

		getService().deletePageAttachment(nodeId, title, fileName);
	}

	public static void deletePageAttachments(long nodeId, String title)
		throws PortalException {

		getService().deletePageAttachments(nodeId, title);
	}

	public static void deletePages(long nodeId) throws PortalException {
		getService().deletePages(nodeId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static void deleteTempFileEntry(
			long groupId, long userId, String folderName, String fileName)
		throws PortalException {

		getService().deleteTempFileEntry(groupId, userId, folderName, fileName);
	}

	public static void deleteTrashPageAttachments(long nodeId, String title)
		throws PortalException {

		getService().deleteTrashPageAttachments(nodeId, title);
	}

	/**
	 * Deletes the wiki page with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect WikiPageLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param pageId the primary key of the wiki page
	 * @return the wiki page that was removed
	 * @throws PortalException if a wiki page with the primary key could not be found
	 */
	public static WikiPage deleteWikiPage(long pageId) throws PortalException {
		return getService().deleteWikiPage(pageId);
	}

	/**
	 * Deletes the wiki page from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect WikiPageLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param wikiPage the wiki page
	 * @return the wiki page that was removed
	 */
	public static WikiPage deleteWikiPage(WikiPage wikiPage) {
		return getService().deleteWikiPage(wikiPage);
	}

	public static void discardDraft(long nodeId, String title, double version)
		throws PortalException {

		getService().discardDraft(nodeId, title, version);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.wiki.model.impl.WikiPageModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.wiki.model.impl.WikiPageModelImpl</code>.
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

	public static WikiPage fetchLatestPage(
		long resourcePrimKey, int status, boolean preferApproved) {

		return getService().fetchLatestPage(
			resourcePrimKey, status, preferApproved);
	}

	public static WikiPage fetchLatestPage(
		long resourcePrimKey, long nodeId, int status, boolean preferApproved) {

		return getService().fetchLatestPage(
			resourcePrimKey, nodeId, status, preferApproved);
	}

	public static WikiPage fetchLatestPage(
		long nodeId, String title, int status, boolean preferApproved) {

		return getService().fetchLatestPage(
			nodeId, title, status, preferApproved);
	}

	/**
	 * Returns the latest wiki page matching the group and the external
	 * reference code
	 *
	 * @param groupId the primary key of the group
	 * @param externalReferenceCode the wiki page's external reference code
	 * @return the latest matching wiki page, or <code>null</code> if no
	 matching wiki page could be found
	 */
	public static WikiPage fetchLatestPageByExternalReferenceCode(
		long groupId, String externalReferenceCode) {

		return getService().fetchLatestPageByExternalReferenceCode(
			groupId, externalReferenceCode);
	}

	public static WikiPage fetchPage(long resourcePrimKey) {
		return getService().fetchPage(resourcePrimKey);
	}

	public static WikiPage fetchPage(long nodeId, String title) {
		return getService().fetchPage(nodeId, title);
	}

	public static WikiPage fetchPage(
		long nodeId, String title, double version) {

		return getService().fetchPage(nodeId, title, version);
	}

	public static PersistedModel fetchPersistedModel(
		Serializable primaryKeyObj) {

		return getService().fetchPersistedModel(primaryKeyObj);
	}

	public static WikiPage fetchWikiPage(long pageId) {
		return getService().fetchWikiPage(pageId);
	}

	/**
	 * Returns the wiki page matching the UUID and group.
	 *
	 * @param uuid the wiki page's UUID
	 * @param groupId the primary key of the group
	 * @return the matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	public static WikiPage fetchWikiPageByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchWikiPageByUuidAndGroupId(uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static List<WikiPage> getChildren(
		long nodeId, boolean head, String parentTitle) {

		return getService().getChildren(nodeId, head, parentTitle);
	}

	public static List<WikiPage> getChildren(
		long nodeId, boolean head, String parentTitle, int status) {

		return getService().getChildren(nodeId, head, parentTitle, status);
	}

	public static List<WikiPage> getChildren(
		long nodeId, boolean head, String parentTitle, int start, int end) {

		return getService().getChildren(nodeId, head, parentTitle, start, end);
	}

	public static List<WikiPage> getChildren(
		long nodeId, boolean head, String parentTitle, int status, int start,
		int end, OrderByComparator<WikiPage> orderByComparator) {

		return getService().getChildren(
			nodeId, head, parentTitle, status, start, end, orderByComparator);
	}

	public static int getChildrenCount(
		long nodeId, boolean head, String parentTitle) {

		return getService().getChildrenCount(nodeId, head, parentTitle);
	}

	public static int getChildrenCount(
		long nodeId, boolean head, String parentTitle, int status) {

		return getService().getChildrenCount(nodeId, head, parentTitle, status);
	}

	public static List<WikiPage> getDependentPages(
		long nodeId, boolean head, String title, int status) {

		return getService().getDependentPages(nodeId, head, title, status);
	}

	public static com.liferay.wiki.model.WikiPageDisplay getDisplay(
			long nodeId, String title, jakarta.portlet.PortletURL viewPageURL,
			java.util.function.Supplier<jakarta.portlet.PortletURL>
				editPageURLSupplier,
			String attachmentURLPrefix)
		throws PortalException {

		return getService().getDisplay(
			nodeId, title, viewPageURL, editPageURLSupplier,
			attachmentURLPrefix);
	}

	public static WikiPage getDraftPage(long nodeId, String title)
		throws PortalException {

		return getService().getDraftPage(nodeId, title);
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static List<WikiPage> getIncomingLinks(long nodeId, String title)
		throws PortalException {

		return getService().getIncomingLinks(nodeId, title);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	public static WikiPage getLatestPage(
			long resourcePrimKey, int status, boolean preferApproved)
		throws PortalException {

		return getService().getLatestPage(
			resourcePrimKey, status, preferApproved);
	}

	public static WikiPage getLatestPage(
			long resourcePrimKey, long nodeId, int status,
			boolean preferApproved)
		throws PortalException {

		return getService().getLatestPage(
			resourcePrimKey, nodeId, status, preferApproved);
	}

	public static WikiPage getLatestPage(
			long nodeId, String title, int status, boolean preferApproved)
		throws PortalException {

		return getService().getLatestPage(
			nodeId, title, status, preferApproved);
	}

	/**
	 * Returns the latest wiki page matching the group and the external
	 * reference code
	 *
	 * @param groupId the primary key of the group
	 * @param externalReferenceCode the wiki page's external reference code
	 * @return the latest matching wiki page
	 * @throws PortalException if a portal exception occurred
	 */
	public static WikiPage getLatestPageByExternalReferenceCode(
			long groupId, String externalReferenceCode)
		throws PortalException {

		return getService().getLatestPageByExternalReferenceCode(
			groupId, externalReferenceCode);
	}

	public static List<WikiPage> getOrphans(List<WikiPage> pages)
		throws PortalException {

		return getService().getOrphans(pages);
	}

	public static List<WikiPage> getOrphans(long nodeId)
		throws PortalException {

		return getService().getOrphans(nodeId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static List<WikiPage> getOutgoingLinks(long nodeId, String title)
		throws PortalException {

		return getService().getOutgoingLinks(nodeId, title);
	}

	public static WikiPage getPage(long resourcePrimKey)
		throws PortalException {

		return getService().getPage(resourcePrimKey);
	}

	public static WikiPage getPage(long resourcePrimKey, Boolean head)
		throws PortalException {

		return getService().getPage(resourcePrimKey, head);
	}

	public static WikiPage getPage(long nodeId, String title)
		throws PortalException {

		return getService().getPage(nodeId, title);
	}

	public static WikiPage getPage(long nodeId, String title, Boolean head)
		throws PortalException {

		return getService().getPage(nodeId, title, head);
	}

	public static WikiPage getPage(long nodeId, String title, double version)
		throws PortalException {

		return getService().getPage(nodeId, title, version);
	}

	public static WikiPage getPageByPageId(long pageId) throws PortalException {
		return getService().getPageByPageId(pageId);
	}

	public static com.liferay.wiki.model.WikiPageDisplay getPageDisplay(
			long nodeId, String title, jakarta.portlet.PortletURL viewPageURL,
			jakarta.portlet.PortletURL editPageURL, String attachmentURLPrefix)
		throws PortalException {

		return getService().getPageDisplay(
			nodeId, title, viewPageURL, editPageURL, attachmentURLPrefix);
	}

	public static com.liferay.wiki.model.WikiPageDisplay getPageDisplay(
			WikiPage page, jakarta.portlet.PortletURL viewPageURL,
			jakarta.portlet.PortletURL editPageURL, String attachmentURLPrefix)
		throws PortalException {

		return getService().getPageDisplay(
			page, viewPageURL, editPageURL, attachmentURLPrefix);
	}

	public static com.liferay.wiki.model.WikiPageDisplay getPageDisplay(
			WikiPage page, jakarta.portlet.PortletURL viewPageURL,
			jakarta.portlet.PortletURL editPageURL, String attachmentURLPrefix,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().getPageDisplay(
			page, viewPageURL, editPageURL, attachmentURLPrefix,
			serviceContext);
	}

	public static com.liferay.wiki.model.WikiPageDisplay getPageDisplay(
			WikiPage page, jakarta.portlet.PortletURL viewPageURL,
			java.util.function.Supplier<jakarta.portlet.PortletURL>
				editPageURLSupplier,
			String attachmentURLPrefix,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().getPageDisplay(
			page, viewPageURL, editPageURLSupplier, attachmentURLPrefix,
			serviceContext);
	}

	public static List<WikiPage> getPages(
		long nodeId, boolean head, int start, int end) {

		return getService().getPages(nodeId, head, start, end);
	}

	public static List<WikiPage> getPages(
		long nodeId, boolean head, int status, int start, int end) {

		return getService().getPages(nodeId, head, status, start, end);
	}

	public static List<WikiPage> getPages(
		long nodeId, boolean head, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return getService().getPages(
			nodeId, head, status, start, end, orderByComparator);
	}

	public static List<WikiPage> getPages(
		long nodeId, boolean head, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return getService().getPages(
			nodeId, head, start, end, orderByComparator);
	}

	public static List<WikiPage> getPages(long nodeId, int start, int end) {
		return getService().getPages(nodeId, start, end);
	}

	public static List<WikiPage> getPages(
		long nodeId, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return getService().getPages(nodeId, start, end, orderByComparator);
	}

	public static List<WikiPage> getPages(
		long resourcePrimKey, long nodeId, int status) {

		return getService().getPages(resourcePrimKey, nodeId, status);
	}

	public static List<WikiPage> getPages(
		long userId, long nodeId, int status, int start, int end) {

		return getService().getPages(userId, nodeId, status, start, end);
	}

	public static List<WikiPage> getPages(
		long groupId, long nodeId, int status, long statusByUserId) {

		return getService().getPages(groupId, nodeId, status, statusByUserId);
	}

	public static List<WikiPage> getPages(
		long nodeId, String title, boolean head, int start, int end) {

		return getService().getPages(nodeId, title, head, start, end);
	}

	public static List<WikiPage> getPages(
		long nodeId, String title, int start, int end) {

		return getService().getPages(nodeId, title, start, end);
	}

	public static List<WikiPage> getPages(
		long nodeId, String title, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return getService().getPages(
			nodeId, title, start, end, orderByComparator);
	}

	public static List<WikiPage> getPages(String format) {
		return getService().getPages(format);
	}

	public static int getPagesCount(long nodeId) {
		return getService().getPagesCount(nodeId);
	}

	public static int getPagesCount(long nodeId, boolean head) {
		return getService().getPagesCount(nodeId, head);
	}

	public static int getPagesCount(long nodeId, boolean head, int status) {
		return getService().getPagesCount(nodeId, head, status);
	}

	public static int getPagesCount(long nodeId, int status) {
		return getService().getPagesCount(nodeId, status);
	}

	public static int getPagesCount(long userId, long nodeId, int status) {
		return getService().getPagesCount(userId, nodeId, status);
	}

	public static int getPagesCount(long nodeId, String title) {
		return getService().getPagesCount(nodeId, title);
	}

	public static int getPagesCount(long nodeId, String title, boolean head) {
		return getService().getPagesCount(nodeId, title, head);
	}

	public static int getPagesCount(String format) {
		return getService().getPagesCount(format);
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

	public static WikiPage getPreviousVersionPage(WikiPage page)
		throws PortalException {

		return getService().getPreviousVersionPage(page);
	}

	public static List<WikiPage> getRecentChanges(
		long groupId, long nodeId, int start, int end) {

		return getService().getRecentChanges(groupId, nodeId, start, end);
	}

	public static int getRecentChangesCount(long groupId, long nodeId) {
		return getService().getRecentChangesCount(groupId, nodeId);
	}

	public static List<WikiPage> getRedirectorPages(
		long nodeId, boolean head, String redirectTitle, int status) {

		return getService().getRedirectorPages(
			nodeId, head, redirectTitle, status);
	}

	public static List<WikiPage> getRedirectorPages(
		long nodeId, String redirectTitle) {

		return getService().getRedirectorPages(nodeId, redirectTitle);
	}

	public static String[] getTempFileNames(
			long groupId, long userId, String folderName)
		throws PortalException {

		return getService().getTempFileNames(groupId, userId, folderName);
	}

	/**
	 * Returns the wiki page with the primary key.
	 *
	 * @param pageId the primary key of the wiki page
	 * @return the wiki page
	 * @throws PortalException if a wiki page with the primary key could not be found
	 */
	public static WikiPage getWikiPage(long pageId) throws PortalException {
		return getService().getWikiPage(pageId);
	}

	/**
	 * Returns the wiki page matching the UUID and group.
	 *
	 * @param uuid the wiki page's UUID
	 * @param groupId the primary key of the group
	 * @return the matching wiki page
	 * @throws PortalException if a matching wiki page could not be found
	 */
	public static WikiPage getWikiPageByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getWikiPageByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the wiki pages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.wiki.model.impl.WikiPageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @return the range of wiki pages
	 */
	public static List<WikiPage> getWikiPages(int start, int end) {
		return getService().getWikiPages(start, end);
	}

	/**
	 * Returns all the wiki pages matching the UUID and company.
	 *
	 * @param uuid the UUID of the wiki pages
	 * @param companyId the primary key of the company
	 * @return the matching wiki pages, or an empty list if no matches were found
	 */
	public static List<WikiPage> getWikiPagesByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getWikiPagesByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of wiki pages matching the UUID and company.
	 *
	 * @param uuid the UUID of the wiki pages
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of wiki pages
	 * @param end the upper bound of the range of wiki pages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching wiki pages, or an empty list if no matches were found
	 */
	public static List<WikiPage> getWikiPagesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<WikiPage> orderByComparator) {

		return getService().getWikiPagesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of wiki pages.
	 *
	 * @return the number of wiki pages
	 */
	public static int getWikiPagesCount() {
		return getService().getWikiPagesCount();
	}

	public static boolean hasDraftPage(long nodeId, String title) {
		return getService().hasDraftPage(nodeId, title);
	}

	public static void moveDependentToTrash(WikiPage page, long trashEntryId)
		throws PortalException {

		getService().moveDependentToTrash(page, trashEntryId);
	}

	public static com.liferay.portal.kernel.repository.model.FileEntry
			movePageAttachmentToTrash(
				long userId, long nodeId, String title, String fileName)
		throws PortalException {

		return getService().movePageAttachmentToTrash(
			userId, nodeId, title, fileName);
	}

	public static WikiPage movePageFromTrash(
			long userId, long nodeId, String title, long newNodeId,
			String newParentTitle)
		throws PortalException {

		return getService().movePageFromTrash(
			userId, nodeId, title, newNodeId, newParentTitle);
	}

	public static WikiPage movePageToTrash(
			long userId, long nodeId, String title)
		throws PortalException {

		return getService().movePageToTrash(userId, nodeId, title);
	}

	public static WikiPage movePageToTrash(
			long userId, long nodeId, String title, double version)
		throws PortalException {

		return getService().movePageToTrash(userId, nodeId, title, version);
	}

	public static WikiPage movePageToTrash(long userId, WikiPage page)
		throws PortalException {

		return getService().movePageToTrash(userId, page);
	}

	public static void renamePage(
			long userId, long nodeId, String title, String newTitle,
			boolean strict,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().renamePage(
			userId, nodeId, title, newTitle, strict, serviceContext);
	}

	public static void renamePage(
			long userId, long nodeId, String title, String newTitle,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().renamePage(
			userId, nodeId, title, newTitle, serviceContext);
	}

	public static void restorePageAttachmentFromTrash(
			long userId, long nodeId, String title, String fileName)
		throws PortalException {

		getService().restorePageAttachmentFromTrash(
			userId, nodeId, title, fileName);
	}

	public static void restorePageFromTrash(long userId, WikiPage page)
		throws PortalException {

		getService().restorePageFromTrash(userId, page);
	}

	public static WikiPage revertPage(
			long userId, long nodeId, String title, double version,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().revertPage(
			userId, nodeId, title, version, serviceContext);
	}

	public static void subscribePage(long userId, long nodeId, String title)
		throws PortalException {

		getService().subscribePage(userId, nodeId, title);
	}

	public static void unsubscribePage(long userId, long nodeId, String title)
		throws PortalException {

		getService().unsubscribePage(userId, nodeId, title);
	}

	public static void updateAsset(
			long userId, WikiPage page, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds, Double priority)
		throws PortalException {

		getService().updateAsset(
			userId, page, assetCategoryIds, assetTagNames, assetLinkEntryIds,
			priority);
	}

	public static void updateLastPostDate(
		long nodeId, java.util.Date lastPostDate) {

		getService().updateLastPostDate(nodeId, lastPostDate);
	}

	public static WikiPage updatePage(
			long userId, long nodeId, String title, double version,
			String content, String summary, boolean minorEdit, String format,
			String parentTitle, String redirectTitle,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updatePage(
			userId, nodeId, title, version, content, summary, minorEdit, format,
			parentTitle, redirectTitle, serviceContext);
	}

	public static WikiPage updateStatus(
			long userId, long resourcePrimKey, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateStatus(
			userId, resourcePrimKey, status, serviceContext);
	}

	public static WikiPage updateStatus(
			long userId, WikiPage page, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		return getService().updateStatus(
			userId, page, status, serviceContext, workflowContext);
	}

	/**
	 * Updates the wiki page in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect WikiPageLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param wikiPage the wiki page
	 * @return the wiki page that was updated
	 */
	public static WikiPage updateWikiPage(WikiPage wikiPage) {
		return getService().updateWikiPage(wikiPage);
	}

	public static WikiPage updateWikiPage(
		WikiPage wikiPage,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return getService().updateWikiPage(wikiPage, serviceContext);
	}

	public static WikiPageLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<WikiPageLocalService> _serviceSnapshot =
		new Snapshot<>(
			WikiPageLocalServiceUtil.class, WikiPageLocalService.class);

}