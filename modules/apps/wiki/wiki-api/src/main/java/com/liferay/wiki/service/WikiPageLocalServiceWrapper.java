/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.wiki.model.WikiPage;

/**
 * Provides a wrapper for {@link WikiPageLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see WikiPageLocalService
 * @generated
 */
public class WikiPageLocalServiceWrapper
	implements ServiceWrapper<WikiPageLocalService>, WikiPageLocalService {

	public WikiPageLocalServiceWrapper() {
		this(null);
	}

	public WikiPageLocalServiceWrapper(
		WikiPageLocalService wikiPageLocalService) {

		_wikiPageLocalService = wikiPageLocalService;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link #addPage(String,
	 long, long, String, double, String, String, boolean, String,
	 boolean, String, String, ServiceContext)}
	 */
	@Deprecated
	@Override
	public WikiPage addPage(
			long userId, long nodeId, String title, double version,
			String content, String summary, boolean minorEdit, String format,
			boolean head, String parentTitle, String redirectTitle,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.addPage(
			userId, nodeId, title, version, content, summary, minorEdit, format,
			head, parentTitle, redirectTitle, serviceContext);
	}

	@Override
	public WikiPage addPage(
			long userId, long nodeId, String title, String content,
			String summary, boolean minorEdit,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.addPage(
			userId, nodeId, title, content, summary, minorEdit, serviceContext);
	}

	@Override
	public WikiPage addPage(
			String externalReferenceCode, long userId, long nodeId,
			String title, double version, String content, String summary,
			boolean minorEdit, String format, boolean head, String parentTitle,
			String redirectTitle,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.addPage(
			externalReferenceCode, userId, nodeId, title, version, content,
			summary, minorEdit, format, head, parentTitle, redirectTitle,
			serviceContext);
	}

	@Override
	public com.liferay.portal.kernel.repository.model.FileEntry
			addPageAttachment(
				long userId, long nodeId, String title, String fileName,
				java.io.File file, String mimeType)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.addPageAttachment(
			userId, nodeId, title, fileName, file, mimeType);
	}

	@Override
	public com.liferay.portal.kernel.repository.model.FileEntry
			addPageAttachment(
				long userId, long nodeId, String title, String fileName,
				java.io.InputStream inputStream, String mimeType)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.addPageAttachment(
			userId, nodeId, title, fileName, inputStream, mimeType);
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.repository.model.FileEntry>
			addPageAttachments(
				long userId, long nodeId, String title,
				java.util.List
					<com.liferay.portal.kernel.util.ObjectValuePair
						<String, java.io.InputStream>> inputStreamOVPs)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.addPageAttachments(
			userId, nodeId, title, inputStreamOVPs);
	}

	@Override
	public void addPageResources(
			long nodeId, String title, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_wikiPageLocalService.addPageResources(
			nodeId, title, addGroupPermissions, addGuestPermissions);
	}

	@Override
	public void addPageResources(
			WikiPage page, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_wikiPageLocalService.addPageResources(
			page, addGroupPermissions, addGuestPermissions);
	}

	@Override
	public void addPageResources(
			WikiPage page,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_wikiPageLocalService.addPageResources(page, modelPermissions);
	}

	@Override
	public com.liferay.portal.kernel.repository.model.FileEntry
			addTempFileEntry(
				long groupId, long userId, String folderName, String fileName,
				java.io.InputStream inputStream, String mimeType)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.addTempFileEntry(
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
	@Override
	public WikiPage addWikiPage(WikiPage wikiPage) {
		return _wikiPageLocalService.addWikiPage(wikiPage);
	}

	@Override
	public WikiPage changeParent(
			long userId, long nodeId, String title, String newParentTitle,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.changeParent(
			userId, nodeId, title, newParentTitle, serviceContext);
	}

	@Override
	public void copyPageAttachments(
			long userId, long templateNodeId, String templateTitle, long nodeId,
			String title)
		throws com.liferay.portal.kernel.exception.PortalException {

		_wikiPageLocalService.copyPageAttachments(
			userId, templateNodeId, templateTitle, nodeId, title);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new wiki page with the primary key. Does not add the wiki page to the database.
	 *
	 * @param pageId the primary key for the new wiki page
	 * @return the new wiki page
	 */
	@Override
	public WikiPage createWikiPage(long pageId) {
		return _wikiPageLocalService.createWikiPage(pageId);
	}

	@Override
	public void deletePage(long nodeId, String title)
		throws com.liferay.portal.kernel.exception.PortalException {

		_wikiPageLocalService.deletePage(nodeId, title);
	}

	@Override
	public void deletePage(WikiPage page)
		throws com.liferay.portal.kernel.exception.PortalException {

		_wikiPageLocalService.deletePage(page);
	}

	@Override
	public void deletePageAttachment(long nodeId, String title, String fileName)
		throws com.liferay.portal.kernel.exception.PortalException {

		_wikiPageLocalService.deletePageAttachment(nodeId, title, fileName);
	}

	@Override
	public void deletePageAttachments(long nodeId, String title)
		throws com.liferay.portal.kernel.exception.PortalException {

		_wikiPageLocalService.deletePageAttachments(nodeId, title);
	}

	@Override
	public void deletePages(long nodeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_wikiPageLocalService.deletePages(nodeId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public void deleteTempFileEntry(
			long groupId, long userId, String folderName, String fileName)
		throws com.liferay.portal.kernel.exception.PortalException {

		_wikiPageLocalService.deleteTempFileEntry(
			groupId, userId, folderName, fileName);
	}

	@Override
	public void deleteTrashPageAttachments(long nodeId, String title)
		throws com.liferay.portal.kernel.exception.PortalException {

		_wikiPageLocalService.deleteTrashPageAttachments(nodeId, title);
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
	@Override
	public WikiPage deleteWikiPage(long pageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.deleteWikiPage(pageId);
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
	@Override
	public WikiPage deleteWikiPage(WikiPage wikiPage) {
		return _wikiPageLocalService.deleteWikiPage(wikiPage);
	}

	@Override
	public void discardDraft(long nodeId, String title, double version)
		throws com.liferay.portal.kernel.exception.PortalException {

		_wikiPageLocalService.discardDraft(nodeId, title, version);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _wikiPageLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _wikiPageLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _wikiPageLocalService.dynamicQuery();
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

		return _wikiPageLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _wikiPageLocalService.dynamicQuery(dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _wikiPageLocalService.dynamicQuery(
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

		return _wikiPageLocalService.dynamicQueryCount(dynamicQuery);
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

		return _wikiPageLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public WikiPage fetchLatestPage(
		long resourcePrimKey, int status, boolean preferApproved) {

		return _wikiPageLocalService.fetchLatestPage(
			resourcePrimKey, status, preferApproved);
	}

	@Override
	public WikiPage fetchLatestPage(
		long resourcePrimKey, long nodeId, int status, boolean preferApproved) {

		return _wikiPageLocalService.fetchLatestPage(
			resourcePrimKey, nodeId, status, preferApproved);
	}

	@Override
	public WikiPage fetchLatestPage(
		long nodeId, String title, int status, boolean preferApproved) {

		return _wikiPageLocalService.fetchLatestPage(
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
	@Override
	public WikiPage fetchLatestPageByExternalReferenceCode(
		long groupId, String externalReferenceCode) {

		return _wikiPageLocalService.fetchLatestPageByExternalReferenceCode(
			groupId, externalReferenceCode);
	}

	@Override
	public WikiPage fetchPage(long resourcePrimKey) {
		return _wikiPageLocalService.fetchPage(resourcePrimKey);
	}

	@Override
	public WikiPage fetchPage(long nodeId, String title) {
		return _wikiPageLocalService.fetchPage(nodeId, title);
	}

	@Override
	public WikiPage fetchPage(long nodeId, String title, double version) {
		return _wikiPageLocalService.fetchPage(nodeId, title, version);
	}

	@Override
	public com.liferay.portal.kernel.model.PersistedModel fetchPersistedModel(
		java.io.Serializable primaryKeyObj) {

		return _wikiPageLocalService.fetchPersistedModel(primaryKeyObj);
	}

	@Override
	public WikiPage fetchWikiPage(long pageId) {
		return _wikiPageLocalService.fetchWikiPage(pageId);
	}

	/**
	 * Returns the wiki page matching the UUID and group.
	 *
	 * @param uuid the wiki page's UUID
	 * @param groupId the primary key of the group
	 * @return the matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Override
	public WikiPage fetchWikiPageByUuidAndGroupId(String uuid, long groupId) {
		return _wikiPageLocalService.fetchWikiPageByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _wikiPageLocalService.getActionableDynamicQuery();
	}

	@Override
	public java.util.List<WikiPage> getChildren(
		long nodeId, boolean head, String parentTitle) {

		return _wikiPageLocalService.getChildren(nodeId, head, parentTitle);
	}

	@Override
	public java.util.List<WikiPage> getChildren(
		long nodeId, boolean head, String parentTitle, int status) {

		return _wikiPageLocalService.getChildren(
			nodeId, head, parentTitle, status);
	}

	@Override
	public java.util.List<WikiPage> getChildren(
		long nodeId, boolean head, String parentTitle, int start, int end) {

		return _wikiPageLocalService.getChildren(
			nodeId, head, parentTitle, start, end);
	}

	@Override
	public java.util.List<WikiPage> getChildren(
		long nodeId, boolean head, String parentTitle, int status, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<WikiPage>
			orderByComparator) {

		return _wikiPageLocalService.getChildren(
			nodeId, head, parentTitle, status, start, end, orderByComparator);
	}

	@Override
	public int getChildrenCount(long nodeId, boolean head, String parentTitle) {
		return _wikiPageLocalService.getChildrenCount(
			nodeId, head, parentTitle);
	}

	@Override
	public int getChildrenCount(
		long nodeId, boolean head, String parentTitle, int status) {

		return _wikiPageLocalService.getChildrenCount(
			nodeId, head, parentTitle, status);
	}

	@Override
	public java.util.List<WikiPage> getDependentPages(
		long nodeId, boolean head, String title, int status) {

		return _wikiPageLocalService.getDependentPages(
			nodeId, head, title, status);
	}

	@Override
	public com.liferay.wiki.model.WikiPageDisplay getDisplay(
			long nodeId, String title, jakarta.portlet.PortletURL viewPageURL,
			java.util.function.Supplier<jakarta.portlet.PortletURL>
				editPageURLSupplier,
			String attachmentURLPrefix)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.getDisplay(
			nodeId, title, viewPageURL, editPageURLSupplier,
			attachmentURLPrefix);
	}

	@Override
	public WikiPage getDraftPage(long nodeId, String title)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.getDraftPage(nodeId, title);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _wikiPageLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public java.util.List<WikiPage> getIncomingLinks(long nodeId, String title)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.getIncomingLinks(nodeId, title);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _wikiPageLocalService.getIndexableActionableDynamicQuery();
	}

	@Override
	public WikiPage getLatestPage(
			long resourcePrimKey, int status, boolean preferApproved)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.getLatestPage(
			resourcePrimKey, status, preferApproved);
	}

	@Override
	public WikiPage getLatestPage(
			long resourcePrimKey, long nodeId, int status,
			boolean preferApproved)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.getLatestPage(
			resourcePrimKey, nodeId, status, preferApproved);
	}

	@Override
	public WikiPage getLatestPage(
			long nodeId, String title, int status, boolean preferApproved)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.getLatestPage(
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
	@Override
	public WikiPage getLatestPageByExternalReferenceCode(
			long groupId, String externalReferenceCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.getLatestPageByExternalReferenceCode(
			groupId, externalReferenceCode);
	}

	@Override
	public java.util.List<WikiPage> getOrphans(java.util.List<WikiPage> pages)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.getOrphans(pages);
	}

	@Override
	public java.util.List<WikiPage> getOrphans(long nodeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.getOrphans(nodeId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _wikiPageLocalService.getOSGiServiceIdentifier();
	}

	@Override
	public java.util.List<WikiPage> getOutgoingLinks(long nodeId, String title)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.getOutgoingLinks(nodeId, title);
	}

	@Override
	public WikiPage getPage(long resourcePrimKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.getPage(resourcePrimKey);
	}

	@Override
	public WikiPage getPage(long resourcePrimKey, Boolean head)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.getPage(resourcePrimKey, head);
	}

	@Override
	public WikiPage getPage(long nodeId, String title)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.getPage(nodeId, title);
	}

	@Override
	public WikiPage getPage(long nodeId, String title, Boolean head)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.getPage(nodeId, title, head);
	}

	@Override
	public WikiPage getPage(long nodeId, String title, double version)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.getPage(nodeId, title, version);
	}

	@Override
	public WikiPage getPageByPageId(long pageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.getPageByPageId(pageId);
	}

	@Override
	public com.liferay.wiki.model.WikiPageDisplay getPageDisplay(
			long nodeId, String title, jakarta.portlet.PortletURL viewPageURL,
			jakarta.portlet.PortletURL editPageURL, String attachmentURLPrefix)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.getPageDisplay(
			nodeId, title, viewPageURL, editPageURL, attachmentURLPrefix);
	}

	@Override
	public com.liferay.wiki.model.WikiPageDisplay getPageDisplay(
			WikiPage page, jakarta.portlet.PortletURL viewPageURL,
			jakarta.portlet.PortletURL editPageURL, String attachmentURLPrefix)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.getPageDisplay(
			page, viewPageURL, editPageURL, attachmentURLPrefix);
	}

	@Override
	public com.liferay.wiki.model.WikiPageDisplay getPageDisplay(
			WikiPage page, jakarta.portlet.PortletURL viewPageURL,
			jakarta.portlet.PortletURL editPageURL, String attachmentURLPrefix,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.getPageDisplay(
			page, viewPageURL, editPageURL, attachmentURLPrefix,
			serviceContext);
	}

	@Override
	public com.liferay.wiki.model.WikiPageDisplay getPageDisplay(
			WikiPage page, jakarta.portlet.PortletURL viewPageURL,
			java.util.function.Supplier<jakarta.portlet.PortletURL>
				editPageURLSupplier,
			String attachmentURLPrefix,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.getPageDisplay(
			page, viewPageURL, editPageURLSupplier, attachmentURLPrefix,
			serviceContext);
	}

	@Override
	public java.util.List<WikiPage> getPages(
		long nodeId, boolean head, int start, int end) {

		return _wikiPageLocalService.getPages(nodeId, head, start, end);
	}

	@Override
	public java.util.List<WikiPage> getPages(
		long nodeId, boolean head, int status, int start, int end) {

		return _wikiPageLocalService.getPages(nodeId, head, status, start, end);
	}

	@Override
	public java.util.List<WikiPage> getPages(
		long nodeId, boolean head, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<WikiPage>
			orderByComparator) {

		return _wikiPageLocalService.getPages(
			nodeId, head, status, start, end, orderByComparator);
	}

	@Override
	public java.util.List<WikiPage> getPages(
		long nodeId, boolean head, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<WikiPage>
			orderByComparator) {

		return _wikiPageLocalService.getPages(
			nodeId, head, start, end, orderByComparator);
	}

	@Override
	public java.util.List<WikiPage> getPages(long nodeId, int start, int end) {
		return _wikiPageLocalService.getPages(nodeId, start, end);
	}

	@Override
	public java.util.List<WikiPage> getPages(
		long nodeId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<WikiPage>
			orderByComparator) {

		return _wikiPageLocalService.getPages(
			nodeId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<WikiPage> getPages(
		long resourcePrimKey, long nodeId, int status) {

		return _wikiPageLocalService.getPages(resourcePrimKey, nodeId, status);
	}

	@Override
	public java.util.List<WikiPage> getPages(
		long userId, long nodeId, int status, int start, int end) {

		return _wikiPageLocalService.getPages(
			userId, nodeId, status, start, end);
	}

	@Override
	public java.util.List<WikiPage> getPages(
		long groupId, long nodeId, int status, long statusByUserId) {

		return _wikiPageLocalService.getPages(
			groupId, nodeId, status, statusByUserId);
	}

	@Override
	public java.util.List<WikiPage> getPages(
		long nodeId, String title, boolean head, int start, int end) {

		return _wikiPageLocalService.getPages(nodeId, title, head, start, end);
	}

	@Override
	public java.util.List<WikiPage> getPages(
		long nodeId, String title, int start, int end) {

		return _wikiPageLocalService.getPages(nodeId, title, start, end);
	}

	@Override
	public java.util.List<WikiPage> getPages(
		long nodeId, String title, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<WikiPage>
			orderByComparator) {

		return _wikiPageLocalService.getPages(
			nodeId, title, start, end, orderByComparator);
	}

	@Override
	public java.util.List<WikiPage> getPages(String format) {
		return _wikiPageLocalService.getPages(format);
	}

	@Override
	public int getPagesCount(long nodeId) {
		return _wikiPageLocalService.getPagesCount(nodeId);
	}

	@Override
	public int getPagesCount(long nodeId, boolean head) {
		return _wikiPageLocalService.getPagesCount(nodeId, head);
	}

	@Override
	public int getPagesCount(long nodeId, boolean head, int status) {
		return _wikiPageLocalService.getPagesCount(nodeId, head, status);
	}

	@Override
	public int getPagesCount(long nodeId, int status) {
		return _wikiPageLocalService.getPagesCount(nodeId, status);
	}

	@Override
	public int getPagesCount(long userId, long nodeId, int status) {
		return _wikiPageLocalService.getPagesCount(userId, nodeId, status);
	}

	@Override
	public int getPagesCount(long nodeId, String title) {
		return _wikiPageLocalService.getPagesCount(nodeId, title);
	}

	@Override
	public int getPagesCount(long nodeId, String title, boolean head) {
		return _wikiPageLocalService.getPagesCount(nodeId, title, head);
	}

	@Override
	public int getPagesCount(String format) {
		return _wikiPageLocalService.getPagesCount(format);
	}

	@Override
	public java.util.List
		<? extends com.liferay.portal.kernel.model.PersistedModel>
				getPersistedModel(long resourcePrimKey)
			throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.getPersistedModel(resourcePrimKey);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public WikiPage getPreviousVersionPage(WikiPage page)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.getPreviousVersionPage(page);
	}

	@Override
	public java.util.List<WikiPage> getRecentChanges(
		long groupId, long nodeId, int start, int end) {

		return _wikiPageLocalService.getRecentChanges(
			groupId, nodeId, start, end);
	}

	@Override
	public int getRecentChangesCount(long groupId, long nodeId) {
		return _wikiPageLocalService.getRecentChangesCount(groupId, nodeId);
	}

	@Override
	public java.util.List<WikiPage> getRedirectorPages(
		long nodeId, boolean head, String redirectTitle, int status) {

		return _wikiPageLocalService.getRedirectorPages(
			nodeId, head, redirectTitle, status);
	}

	@Override
	public java.util.List<WikiPage> getRedirectorPages(
		long nodeId, String redirectTitle) {

		return _wikiPageLocalService.getRedirectorPages(nodeId, redirectTitle);
	}

	@Override
	public String[] getTempFileNames(
			long groupId, long userId, String folderName)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.getTempFileNames(
			groupId, userId, folderName);
	}

	/**
	 * Returns the wiki page with the primary key.
	 *
	 * @param pageId the primary key of the wiki page
	 * @return the wiki page
	 * @throws PortalException if a wiki page with the primary key could not be found
	 */
	@Override
	public WikiPage getWikiPage(long pageId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.getWikiPage(pageId);
	}

	/**
	 * Returns the wiki page matching the UUID and group.
	 *
	 * @param uuid the wiki page's UUID
	 * @param groupId the primary key of the group
	 * @return the matching wiki page
	 * @throws PortalException if a matching wiki page could not be found
	 */
	@Override
	public WikiPage getWikiPageByUuidAndGroupId(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.getWikiPageByUuidAndGroupId(uuid, groupId);
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
	@Override
	public java.util.List<WikiPage> getWikiPages(int start, int end) {
		return _wikiPageLocalService.getWikiPages(start, end);
	}

	/**
	 * Returns all the wiki pages matching the UUID and company.
	 *
	 * @param uuid the UUID of the wiki pages
	 * @param companyId the primary key of the company
	 * @return the matching wiki pages, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<WikiPage> getWikiPagesByUuidAndCompanyId(
		String uuid, long companyId) {

		return _wikiPageLocalService.getWikiPagesByUuidAndCompanyId(
			uuid, companyId);
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
	@Override
	public java.util.List<WikiPage> getWikiPagesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<WikiPage>
			orderByComparator) {

		return _wikiPageLocalService.getWikiPagesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of wiki pages.
	 *
	 * @return the number of wiki pages
	 */
	@Override
	public int getWikiPagesCount() {
		return _wikiPageLocalService.getWikiPagesCount();
	}

	@Override
	public boolean hasDraftPage(long nodeId, String title) {
		return _wikiPageLocalService.hasDraftPage(nodeId, title);
	}

	@Override
	public void moveDependentToTrash(WikiPage page, long trashEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_wikiPageLocalService.moveDependentToTrash(page, trashEntryId);
	}

	@Override
	public com.liferay.portal.kernel.repository.model.FileEntry
			movePageAttachmentToTrash(
				long userId, long nodeId, String title, String fileName)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.movePageAttachmentToTrash(
			userId, nodeId, title, fileName);
	}

	@Override
	public WikiPage movePageFromTrash(
			long userId, long nodeId, String title, long newNodeId,
			String newParentTitle)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.movePageFromTrash(
			userId, nodeId, title, newNodeId, newParentTitle);
	}

	@Override
	public WikiPage movePageToTrash(long userId, long nodeId, String title)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.movePageToTrash(userId, nodeId, title);
	}

	@Override
	public WikiPage movePageToTrash(
			long userId, long nodeId, String title, double version)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.movePageToTrash(
			userId, nodeId, title, version);
	}

	@Override
	public WikiPage movePageToTrash(long userId, WikiPage page)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.movePageToTrash(userId, page);
	}

	@Override
	public void renamePage(
			long userId, long nodeId, String title, String newTitle,
			boolean strict,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_wikiPageLocalService.renamePage(
			userId, nodeId, title, newTitle, strict, serviceContext);
	}

	@Override
	public void renamePage(
			long userId, long nodeId, String title, String newTitle,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_wikiPageLocalService.renamePage(
			userId, nodeId, title, newTitle, serviceContext);
	}

	@Override
	public void restorePageAttachmentFromTrash(
			long userId, long nodeId, String title, String fileName)
		throws com.liferay.portal.kernel.exception.PortalException {

		_wikiPageLocalService.restorePageAttachmentFromTrash(
			userId, nodeId, title, fileName);
	}

	@Override
	public void restorePageFromTrash(long userId, WikiPage page)
		throws com.liferay.portal.kernel.exception.PortalException {

		_wikiPageLocalService.restorePageFromTrash(userId, page);
	}

	@Override
	public WikiPage revertPage(
			long userId, long nodeId, String title, double version,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.revertPage(
			userId, nodeId, title, version, serviceContext);
	}

	@Override
	public void subscribePage(long userId, long nodeId, String title)
		throws com.liferay.portal.kernel.exception.PortalException {

		_wikiPageLocalService.subscribePage(userId, nodeId, title);
	}

	@Override
	public void unsubscribePage(long userId, long nodeId, String title)
		throws com.liferay.portal.kernel.exception.PortalException {

		_wikiPageLocalService.unsubscribePage(userId, nodeId, title);
	}

	@Override
	public void updateAsset(
			long userId, WikiPage page, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds, Double priority)
		throws com.liferay.portal.kernel.exception.PortalException {

		_wikiPageLocalService.updateAsset(
			userId, page, assetCategoryIds, assetTagNames, assetLinkEntryIds,
			priority);
	}

	@Override
	public void updateLastPostDate(long nodeId, java.util.Date lastPostDate) {
		_wikiPageLocalService.updateLastPostDate(nodeId, lastPostDate);
	}

	@Override
	public WikiPage updatePage(
			long userId, long nodeId, String title, double version,
			String content, String summary, boolean minorEdit, String format,
			String parentTitle, String redirectTitle,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.updatePage(
			userId, nodeId, title, version, content, summary, minorEdit, format,
			parentTitle, redirectTitle, serviceContext);
	}

	@Override
	public WikiPage updateStatus(
			long userId, long resourcePrimKey, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.updateStatus(
			userId, resourcePrimKey, status, serviceContext);
	}

	@Override
	public WikiPage updateStatus(
			long userId, WikiPage page, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			java.util.Map<String, java.io.Serializable> workflowContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiPageLocalService.updateStatus(
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
	@Override
	public WikiPage updateWikiPage(WikiPage wikiPage) {
		return _wikiPageLocalService.updateWikiPage(wikiPage);
	}

	@Override
	public WikiPage updateWikiPage(
		WikiPage wikiPage,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _wikiPageLocalService.updateWikiPage(wikiPage, serviceContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _wikiPageLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<WikiPage> getCTPersistence() {
		return _wikiPageLocalService.getCTPersistence();
	}

	@Override
	public Class<WikiPage> getModelClass() {
		return _wikiPageLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<WikiPage>, R, E> updateUnsafeFunction)
		throws E {

		return _wikiPageLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public WikiPageLocalService getWrappedService() {
		return _wikiPageLocalService;
	}

	@Override
	public void setWrappedService(WikiPageLocalService wikiPageLocalService) {
		_wikiPageLocalService = wikiPageLocalService;
	}

	private WikiPageLocalService _wikiPageLocalService;

}