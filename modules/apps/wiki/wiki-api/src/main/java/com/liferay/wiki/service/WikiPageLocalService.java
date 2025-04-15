/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.service;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.increment.BufferedIncrement;
import com.liferay.portal.kernel.increment.DateOverrideIncrement;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.PersistedResourcedModelLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.model.WikiPageDisplay;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import jakarta.portlet.PortletURL;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for WikiPage. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see WikiPageLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface WikiPageLocalService
	extends BaseLocalService, CTService<WikiPage>, PersistedModelLocalService,
			PersistedResourcedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.wiki.service.impl.WikiPageLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the wiki page local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link WikiPageLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link #addPage(String,
	 long, long, String, double, String, String, boolean, String,
	 boolean, String, String, ServiceContext)}
	 */
	@Deprecated
	public WikiPage addPage(
			long userId, long nodeId, String title, double version,
			String content, String summary, boolean minorEdit, String format,
			boolean head, String parentTitle, String redirectTitle,
			ServiceContext serviceContext)
		throws PortalException;

	public WikiPage addPage(
			long userId, long nodeId, String title, String content,
			String summary, boolean minorEdit, ServiceContext serviceContext)
		throws PortalException;

	public WikiPage addPage(
			String externalReferenceCode, long userId, long nodeId,
			String title, double version, String content, String summary,
			boolean minorEdit, String format, boolean head, String parentTitle,
			String redirectTitle, ServiceContext serviceContext)
		throws PortalException;

	public FileEntry addPageAttachment(
			long userId, long nodeId, String title, String fileName, File file,
			String mimeType)
		throws PortalException;

	public FileEntry addPageAttachment(
			long userId, long nodeId, String title, String fileName,
			InputStream inputStream, String mimeType)
		throws PortalException;

	public List<FileEntry> addPageAttachments(
			long userId, long nodeId, String title,
			List<ObjectValuePair<String, InputStream>> inputStreamOVPs)
		throws PortalException;

	public void addPageResources(
			long nodeId, String title, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException;

	public void addPageResources(
			WikiPage page, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException;

	public void addPageResources(
			WikiPage page, ModelPermissions modelPermissions)
		throws PortalException;

	public FileEntry addTempFileEntry(
			long groupId, long userId, String folderName, String fileName,
			InputStream inputStream, String mimeType)
		throws PortalException;

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
	@Indexable(type = IndexableType.REINDEX)
	public WikiPage addWikiPage(WikiPage wikiPage);

	public WikiPage changeParent(
			long userId, long nodeId, String title, String newParentTitle,
			ServiceContext serviceContext)
		throws PortalException;

	public void copyPageAttachments(
			long userId, long templateNodeId, String templateTitle, long nodeId,
			String title)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Creates a new wiki page with the primary key. Does not add the wiki page to the database.
	 *
	 * @param pageId the primary key for the new wiki page
	 * @return the new wiki page
	 */
	@Transactional(enabled = false)
	public WikiPage createWikiPage(long pageId);

	public void deletePage(long nodeId, String title) throws PortalException;

	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP, send = false,
		type = SystemEventConstants.TYPE_DELETE
	)
	public void deletePage(WikiPage page) throws PortalException;

	public void deletePageAttachment(long nodeId, String title, String fileName)
		throws PortalException;

	public void deletePageAttachments(long nodeId, String title)
		throws PortalException;

	public void deletePages(long nodeId) throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	public void deleteTempFileEntry(
			long groupId, long userId, String folderName, String fileName)
		throws PortalException;

	public void deleteTrashPageAttachments(long nodeId, String title)
		throws PortalException;

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
	@Indexable(type = IndexableType.DELETE)
	public WikiPage deleteWikiPage(long pageId) throws PortalException;

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
	@Indexable(type = IndexableType.DELETE)
	public WikiPage deleteWikiPage(WikiPage wikiPage);

	public void discardDraft(long nodeId, String title, double version)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> T dslQuery(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int dslQueryCount(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DynamicQuery dynamicQuery();

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(DynamicQuery dynamicQuery);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(
		DynamicQuery dynamicQuery, Projection projection);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WikiPage fetchLatestPage(
		long resourcePrimKey, int status, boolean preferApproved);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WikiPage fetchLatestPage(
		long resourcePrimKey, long nodeId, int status, boolean preferApproved);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WikiPage fetchLatestPage(
		long nodeId, String title, int status, boolean preferApproved);

	/**
	 * Returns the latest wiki page matching the group and the external
	 * reference code
	 *
	 * @param groupId the primary key of the group
	 * @param externalReferenceCode the wiki page's external reference code
	 * @return the latest matching wiki page, or <code>null</code> if no
	 matching wiki page could be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WikiPage fetchLatestPageByExternalReferenceCode(
		long groupId, String externalReferenceCode);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WikiPage fetchPage(long resourcePrimKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WikiPage fetchPage(long nodeId, String title);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WikiPage fetchPage(long nodeId, String title, double version);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel fetchPersistedModel(Serializable primaryKeyObj);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WikiPage fetchWikiPage(long pageId);

	/**
	 * Returns the wiki page matching the UUID and group.
	 *
	 * @param uuid the wiki page's UUID
	 * @param groupId the primary key of the group
	 * @return the matching wiki page, or <code>null</code> if a matching wiki page could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WikiPage fetchWikiPageByUuidAndGroupId(String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WikiPage> getChildren(
		long nodeId, boolean head, String parentTitle);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WikiPage> getChildren(
		long nodeId, boolean head, String parentTitle, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WikiPage> getChildren(
		long nodeId, boolean head, String parentTitle, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WikiPage> getChildren(
		long nodeId, boolean head, String parentTitle, int status, int start,
		int end, OrderByComparator<WikiPage> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getChildrenCount(long nodeId, boolean head, String parentTitle);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getChildrenCount(
		long nodeId, boolean head, String parentTitle, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WikiPage> getDependentPages(
		long nodeId, boolean head, String title, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WikiPageDisplay getDisplay(
			long nodeId, String title, PortletURL viewPageURL,
			Supplier<PortletURL> editPageURLSupplier,
			String attachmentURLPrefix)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WikiPage getDraftPage(long nodeId, String title)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WikiPage> getIncomingLinks(long nodeId, String title)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WikiPage getLatestPage(
			long resourcePrimKey, int status, boolean preferApproved)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WikiPage getLatestPage(
			long resourcePrimKey, long nodeId, int status,
			boolean preferApproved)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WikiPage getLatestPage(
			long nodeId, String title, int status, boolean preferApproved)
		throws PortalException;

	/**
	 * Returns the latest wiki page matching the group and the external
	 * reference code
	 *
	 * @param groupId the primary key of the group
	 * @param externalReferenceCode the wiki page's external reference code
	 * @return the latest matching wiki page
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WikiPage getLatestPageByExternalReferenceCode(
			long groupId, String externalReferenceCode)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WikiPage> getOrphans(List<WikiPage> pages)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WikiPage> getOrphans(long nodeId) throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WikiPage> getOutgoingLinks(long nodeId, String title)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WikiPage getPage(long resourcePrimKey) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WikiPage getPage(long resourcePrimKey, Boolean head)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WikiPage getPage(long nodeId, String title) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WikiPage getPage(long nodeId, String title, Boolean head)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WikiPage getPage(long nodeId, String title, double version)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WikiPage getPageByPageId(long pageId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WikiPageDisplay getPageDisplay(
			long nodeId, String title, PortletURL viewPageURL,
			PortletURL editPageURL, String attachmentURLPrefix)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WikiPageDisplay getPageDisplay(
			WikiPage page, PortletURL viewPageURL, PortletURL editPageURL,
			String attachmentURLPrefix)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WikiPageDisplay getPageDisplay(
			WikiPage page, PortletURL viewPageURL, PortletURL editPageURL,
			String attachmentURLPrefix, ServiceContext serviceContext)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WikiPageDisplay getPageDisplay(
			WikiPage page, PortletURL viewPageURL,
			Supplier<PortletURL> editPageURLSupplier,
			String attachmentURLPrefix, ServiceContext serviceContext)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WikiPage> getPages(
		long nodeId, boolean head, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WikiPage> getPages(
		long nodeId, boolean head, int status, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WikiPage> getPages(
		long nodeId, boolean head, int status, int start, int end,
		OrderByComparator<WikiPage> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WikiPage> getPages(
		long nodeId, boolean head, int start, int end,
		OrderByComparator<WikiPage> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WikiPage> getPages(long nodeId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WikiPage> getPages(
		long nodeId, int start, int end,
		OrderByComparator<WikiPage> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WikiPage> getPages(
		long resourcePrimKey, long nodeId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WikiPage> getPages(
		long userId, long nodeId, int status, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WikiPage> getPages(
		long groupId, long nodeId, int status, long statusByUserId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WikiPage> getPages(
		long nodeId, String title, boolean head, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WikiPage> getPages(
		long nodeId, String title, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WikiPage> getPages(
		long nodeId, String title, int start, int end,
		OrderByComparator<WikiPage> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WikiPage> getPages(String format);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getPagesCount(long nodeId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getPagesCount(long nodeId, boolean head);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getPagesCount(long nodeId, boolean head, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getPagesCount(long nodeId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getPagesCount(long userId, long nodeId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getPagesCount(long nodeId, String title);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getPagesCount(long nodeId, String title, boolean head);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getPagesCount(String format);

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<? extends PersistedModel> getPersistedModel(
			long resourcePrimKey)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WikiPage getPreviousVersionPage(WikiPage page)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WikiPage> getRecentChanges(
		long groupId, long nodeId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getRecentChangesCount(long groupId, long nodeId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WikiPage> getRedirectorPages(
		long nodeId, boolean head, String redirectTitle, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WikiPage> getRedirectorPages(long nodeId, String redirectTitle);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public String[] getTempFileNames(
			long groupId, long userId, String folderName)
		throws PortalException;

	/**
	 * Returns the wiki page with the primary key.
	 *
	 * @param pageId the primary key of the wiki page
	 * @return the wiki page
	 * @throws PortalException if a wiki page with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WikiPage getWikiPage(long pageId) throws PortalException;

	/**
	 * Returns the wiki page matching the UUID and group.
	 *
	 * @param uuid the wiki page's UUID
	 * @param groupId the primary key of the group
	 * @return the matching wiki page
	 * @throws PortalException if a matching wiki page could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WikiPage getWikiPageByUuidAndGroupId(String uuid, long groupId)
		throws PortalException;

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WikiPage> getWikiPages(int start, int end);

	/**
	 * Returns all the wiki pages matching the UUID and company.
	 *
	 * @param uuid the UUID of the wiki pages
	 * @param companyId the primary key of the company
	 * @return the matching wiki pages, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WikiPage> getWikiPagesByUuidAndCompanyId(
		String uuid, long companyId);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WikiPage> getWikiPagesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<WikiPage> orderByComparator);

	/**
	 * Returns the number of wiki pages.
	 *
	 * @return the number of wiki pages
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getWikiPagesCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasDraftPage(long nodeId, String title);

	public void moveDependentToTrash(WikiPage page, long trashEntryId)
		throws PortalException;

	public FileEntry movePageAttachmentToTrash(
			long userId, long nodeId, String title, String fileName)
		throws PortalException;

	public WikiPage movePageFromTrash(
			long userId, long nodeId, String title, long newNodeId,
			String newParentTitle)
		throws PortalException;

	public WikiPage movePageToTrash(long userId, long nodeId, String title)
		throws PortalException;

	public WikiPage movePageToTrash(
			long userId, long nodeId, String title, double version)
		throws PortalException;

	public WikiPage movePageToTrash(long userId, WikiPage page)
		throws PortalException;

	public void renamePage(
			long userId, long nodeId, String title, String newTitle,
			boolean strict, ServiceContext serviceContext)
		throws PortalException;

	public void renamePage(
			long userId, long nodeId, String title, String newTitle,
			ServiceContext serviceContext)
		throws PortalException;

	public void restorePageAttachmentFromTrash(
			long userId, long nodeId, String title, String fileName)
		throws PortalException;

	public void restorePageFromTrash(long userId, WikiPage page)
		throws PortalException;

	public WikiPage revertPage(
			long userId, long nodeId, String title, double version,
			ServiceContext serviceContext)
		throws PortalException;

	public void subscribePage(long userId, long nodeId, String title)
		throws PortalException;

	public void unsubscribePage(long userId, long nodeId, String title)
		throws PortalException;

	public void updateAsset(
			long userId, WikiPage page, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds, Double priority)
		throws PortalException;

	@BufferedIncrement(
		configuration = "WikiNode", incrementClass = DateOverrideIncrement.class
	)
	public void updateLastPostDate(long nodeId, Date lastPostDate);

	public WikiPage updatePage(
			long userId, long nodeId, String title, double version,
			String content, String summary, boolean minorEdit, String format,
			String parentTitle, String redirectTitle,
			ServiceContext serviceContext)
		throws PortalException;

	public WikiPage updateStatus(
			long userId, long resourcePrimKey, int status,
			ServiceContext serviceContext)
		throws PortalException;

	public WikiPage updateStatus(
			long userId, WikiPage page, int status,
			ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException;

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
	@Indexable(type = IndexableType.REINDEX)
	public WikiPage updateWikiPage(WikiPage wikiPage);

	public WikiPage updateWikiPage(
		WikiPage wikiPage, ServiceContext serviceContext);

	@Override
	@Transactional(enabled = false)
	public CTPersistence<WikiPage> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<WikiPage> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<WikiPage>, R, E> updateUnsafeFunction)
		throws E;

}