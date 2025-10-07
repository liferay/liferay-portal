/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.service;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.knowledge.base.model.KBArticle;
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
import com.liferay.portal.kernel.lock.Lock;
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
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.InputStream;
import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for KBArticle. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see KBArticleLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface KBArticleLocalService
	extends BaseLocalService, CTService<KBArticle>, PersistedModelLocalService,
			PersistedResourcedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.knowledge.base.service.impl.KBArticleLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the kb article local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link KBArticleLocalServiceUtil} if injection and service tracking are not available.
	 */
	public FileEntry addAttachment(
			long userId, long resourcePrimKey, String fileName,
			InputStream inputStream, String mimeType)
		throws PortalException;

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
	@Indexable(type = IndexableType.REINDEX)
	public KBArticle addKBArticle(KBArticle kbArticle);

	public KBArticle addKBArticle(
			String externalReferenceCode, long userId,
			long parentResourceClassNameId, long parentResourcePrimKey,
			String title, String urlTitle, String content, String description,
			String[] sections, String sourceURL, Date displayDate,
			Date expirationDate, Date reviewDate, String[] selectedFileNames,
			ServiceContext serviceContext)
		throws PortalException;

	public void addKBArticleResources(
			KBArticle kbArticle, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException;

	public void addKBArticleResources(
			KBArticle kbArticle, ModelPermissions modelPermissions)
		throws PortalException;

	public void addKBArticleResources(
			long kbArticleId, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException;

	public int addKBArticlesMarkdown(
			long userId, long groupId, long parentKbFolderId, String fileName,
			boolean prioritizeByNumericalPrefix, InputStream inputStream,
			ServiceContext serviceContext)
		throws PortalException;

	public void addTempAttachment(
			long groupId, long userId, String fileName, String tempFolderName,
			InputStream inputStream, String mimeType)
		throws PortalException;

	public void checkKBArticles(long companyId) throws PortalException;

	/**
	 * Creates a new kb article with the primary key. Does not add the kb article to the database.
	 *
	 * @param kbArticleId the primary key for the new kb article
	 * @return the new kb article
	 */
	@Transactional(enabled = false)
	public KBArticle createKBArticle(long kbArticleId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	public void deleteGroupKBArticles(long groupId) throws PortalException;

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
	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public KBArticle deleteKBArticle(KBArticle kbArticle)
		throws PortalException;

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
	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public KBArticle deleteKBArticle(long kbArticleId) throws PortalException;

	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public KBArticle deleteKBArticle(
			long userId, long resourcePrimKey, int version)
		throws PortalException;

	public void deleteKBArticles(long groupId, long parentResourcePrimKey)
		throws PortalException;

	public void deleteKBArticles(
			long groupId, long parentResourcePrimKey,
			boolean includeTrashedEntries)
		throws PortalException;

	public void deleteKBArticles(long[] resourcePrimKeys)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	public void deleteTempAttachment(
			long groupId, long userId, String fileName, String tempFolderName)
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBArticleModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.knowledge.base.model.impl.KBArticleModelImpl</code>.
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

	public KBArticle expireKBArticle(
			long userId, long resourcePrimKey, ServiceContext serviceContext)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KBArticle fetchFirstChildKBArticle(
		long groupId, long parentResourcePrimKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KBArticle fetchKBArticle(long kbArticleId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KBArticle fetchKBArticle(
		long resourcePrimKey, long groupId, int version);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KBArticle fetchKBArticleByExternalReferenceCode(
		long groupId, String externalReferenceCode, int version);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KBArticle fetchKBArticleByUrlTitle(
		long groupId, long kbFolderId, String urlTitle);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KBArticle fetchKBArticleByUrlTitle(
		long groupId, String kbFolderUrlTitle, String urlTitle);

	/**
	 * Returns the kb article matching the UUID and group.
	 *
	 * @param uuid the kb article's UUID
	 * @param groupId the primary key of the group
	 * @return the matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KBArticle fetchKBArticleByUuidAndGroupId(String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KBArticle fetchLatestKBArticle(long resourcePrimKey, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KBArticle fetchLatestKBArticle(long resourcePrimKey, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KBArticle fetchLatestKBArticleByExternalReferenceCode(
		long groupId, String externalReferenceCode);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KBArticle fetchLatestKBArticleByExternalReferenceCode(
		long groupId, String externalReferenceCode, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KBArticle fetchLatestKBArticleByUrlTitle(
		long groupId, long kbFolderId, String urlTitle, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel fetchPersistedModel(Serializable primaryKeyObj);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KBArticle> getAllDescendantKBArticles(
		long resourcePrimKey, int status,
		OrderByComparator<KBArticle> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KBArticle> getCompanyKBArticles(
		long companyId, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCompanyKBArticlesCount(long companyId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KBArticle> getGroupKBArticles(
		long groupId, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getGroupKBArticlesCount(long groupId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns the kb article with the primary key.
	 *
	 * @param kbArticleId the primary key of the kb article
	 * @return the kb article
	 * @throws PortalException if a kb article with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KBArticle getKBArticle(long kbArticleId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KBArticle getKBArticle(long resourcePrimKey, int version)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KBArticle> getKBArticleAndAllDescendantKBArticles(
		long resourcePrimKey, int status,
		OrderByComparator<KBArticle> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KBArticle getKBArticleByUrlTitle(
			long groupId, long kbFolderId, String urlTitle)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KBArticle getKBArticleByUrlTitle(
			long groupId, String kbFolderUrlTitle, String urlTitle)
		throws PortalException;

	/**
	 * Returns the kb article matching the UUID and group.
	 *
	 * @param uuid the kb article's UUID
	 * @param groupId the primary key of the group
	 * @return the matching kb article
	 * @throws PortalException if a matching kb article could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KBArticle getKBArticleByUuidAndGroupId(String uuid, long groupId)
		throws PortalException;

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KBArticle> getKBArticles(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KBArticle> getKBArticles(
		long groupId, long parentResourcePrimKey, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KBArticle> getKBArticles(
		long[] resourcePrimKeys, int status,
		OrderByComparator<KBArticle> orderByComparator);

	/**
	 * Returns all the kb articles matching the UUID and company.
	 *
	 * @param uuid the UUID of the kb articles
	 * @param companyId the primary key of the company
	 * @return the matching kb articles, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KBArticle> getKBArticlesByUuidAndCompanyId(
		String uuid, long companyId);

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
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KBArticle> getKBArticlesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<KBArticle> orderByComparator);

	/**
	 * Returns the number of kb articles.
	 *
	 * @return the number of kb articles
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getKBArticlesCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getKBArticlesCount(
		long groupId, long parentResourcePrimKey, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KBArticle> getKBArticleVersions(
		long resourcePrimKey, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getKBArticleVersionsCount(long resourcePrimKey, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KBArticle> getKBFolderKBArticles(long groupId, long kbFolderId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getKBFolderKBArticlesCount(
		long groupId, long kbFolderId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KBArticle getLatestKBArticle(long resourcePrimKey)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KBArticle getLatestKBArticle(long resourcePrimKey, int status)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KBArticle getLatestKBArticle(long resourcePrimKey, int[] statuses)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KBArticle getLatestKBArticleByExternalReferenceCode(
			long groupId, String externalReferenceCode)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public KBArticle getLatestKBArticleByUrlTitle(
			long groupId, long kbFolderId, String urlTitle, int status)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

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
	public KBArticle[] getPreviousAndNextKBArticles(long kbArticleId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KBArticle> getSectionsKBArticles(
		long groupId, String[] sections, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getSectionsKBArticlesCount(
		long groupId, String[] sections, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public String[] getTempAttachmentNames(
			long groupId, long userId, String tempFolderName)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasKBArticleLock(long userId, long resourcePrimKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public void incrementViewCount(
			long userId, long resourcePrimKey, int increment)
		throws PortalException;

	public Lock lockKBArticle(long userId, long resourcePrimKey)
		throws PortalException;

	public void moveDependentKBArticlesToTrash(
			long parentResourcePrimKey, long trashEntryId)
		throws PortalException;

	public void moveDependentKBArticleToTrash(
			KBArticle kbArticle, long trashEntryId)
		throws PortalException;

	public void moveKBArticle(
			long userId, long resourcePrimKey, long parentResourceClassNameId,
			long parentResourcePrimKey, double priority)
		throws PortalException;

	public void moveKBArticleFromTrash(
			long userId, long resourcePrimKey, long parentResourceClassNameId,
			long parentResourcePrimKey)
		throws PortalException;

	public KBArticle moveKBArticleToTrash(long userId, long resourcePrimKey)
		throws PortalException;

	public void restoreDependentKBArticleFromTrash(KBArticle kbArticle)
		throws PortalException;

	public void restoreDependentKBArticlesFromTrash(long parentResourcePrimKey)
		throws PortalException;

	public void restoreKBArticleFromTrash(long userId, long resourcePrimKey)
		throws PortalException;

	public KBArticle revertKBArticle(
			long userId, long resourcePrimKey, int version,
			ServiceContext serviceContext)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<KBArticle> search(
		long groupId, String title, String content, int status, Date startDate,
		Date endDate, boolean andOperator, int start, int end,
		OrderByComparator<KBArticle> orderByComparator);

	public void subscribeGroupKBArticles(long userId, long groupId)
		throws PortalException;

	public void subscribeKBArticle(
			long userId, long groupId, long resourcePrimKey)
		throws PortalException;

	public void unlockKBArticle(long userId, long resourcePrimKey);

	public void unlockKBArticle(
		long userId, long resourcePrimKey, boolean force);

	public void unsubscribeGroupKBArticles(long userId, long groupId)
		throws PortalException;

	public void unsubscribeKBArticle(long userId, long resourcePrimKey)
		throws PortalException;

	public KBArticle updateAndUnlockKBArticle(
			long userId, long resourcePrimKey, String title, String content,
			String description, String[] sections, String sourceURL,
			Date displayDate, Date expirationDate, Date reviewDate,
			String[] selectedFileNames, long[] removeFileEntryIds,
			ServiceContext serviceContext)
		throws PortalException;

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
	@Indexable(type = IndexableType.REINDEX)
	public KBArticle updateKBArticle(KBArticle kbArticle);

	public KBArticle updateKBArticle(
			long userId, long resourcePrimKey, String title, String content,
			String description, String[] sections, String sourceURL,
			Date displayDate, Date expirationDate, Date reviewDate,
			String[] selectedFileNames, long[] removeFileEntryIds,
			ServiceContext serviceContext)
		throws PortalException;

	public void updateKBArticleAsset(
			long userId, KBArticle kbArticle, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds)
		throws PortalException;

	public void updateKBArticleResources(
			KBArticle kbArticle, String[] groupPermissions,
			String[] guestPermissions)
		throws PortalException;

	public void updateKBArticlesPriorities(
			Map<Long, Double> resourcePrimKeyToPriorityMap)
		throws PortalException;

	public void updatePriority(long resourcePrimKey, double priority);

	public KBArticle updateStatus(
			long userId, long resourcePrimKey, int status,
			ServiceContext serviceContext)
		throws PortalException;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<KBArticle> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<KBArticle> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<KBArticle>, R, E> updateUnsafeFunction)
		throws E;

}