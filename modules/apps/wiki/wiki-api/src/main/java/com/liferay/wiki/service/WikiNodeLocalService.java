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
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.wiki.model.WikiNode;

import java.io.InputStream;
import java.io.Serializable;

import java.util.List;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for WikiNode. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see WikiNodeLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface WikiNodeLocalService
	extends BaseLocalService, CTService<WikiNode>, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.wiki.service.impl.WikiNodeLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the wiki node local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link WikiNodeLocalServiceUtil} if injection and service tracking are not available.
	 */
	public WikiNode addDefaultNode(long userId, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link #addNode(String,
	 long, String, String, ServiceContext)}
	 */
	@Deprecated
	@Indexable(type = IndexableType.REINDEX)
	public WikiNode addNode(
			long userId, String name, String description,
			ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public WikiNode addNode(
			String externalReferenceCode, long userId, String name,
			String description, ServiceContext serviceContext)
		throws PortalException;

	public void addNodeResources(
			long nodeId, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException;

	public void addNodeResources(
			WikiNode node, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException;

	public void addNodeResources(
			WikiNode node, ModelPermissions modelPermissions)
		throws PortalException;

	/**
	 * Adds the wiki node to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect WikiNodeLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param wikiNode the wiki node
	 * @return the wiki node that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public WikiNode addWikiNode(WikiNode wikiNode);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Creates a new wiki node with the primary key. Does not add the wiki node to the database.
	 *
	 * @param nodeId the primary key for the new wiki node
	 * @return the new wiki node
	 */
	@Transactional(enabled = false)
	public WikiNode createWikiNode(long nodeId);

	public void deleteNode(long nodeId) throws PortalException;

	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public void deleteNode(WikiNode node) throws PortalException;

	public void deleteNodes(long groupId) throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	/**
	 * Deletes the wiki node with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect WikiNodeLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param nodeId the primary key of the wiki node
	 * @return the wiki node that was removed
	 * @throws PortalException if a wiki node with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public WikiNode deleteWikiNode(long nodeId) throws PortalException;

	/**
	 * Deletes the wiki node from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect WikiNodeLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param wikiNode the wiki node
	 * @return the wiki node that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public WikiNode deleteWikiNode(WikiNode wikiNode);

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.wiki.model.impl.WikiNodeModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.wiki.model.impl.WikiNodeModelImpl</code>.
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
	public WikiNode fetchNode(long groupId, String name);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WikiNode fetchNodeByUuidAndGroupId(String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WikiNode fetchWikiNode(long nodeId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WikiNode fetchWikiNodeByExternalReferenceCode(
		String externalReferenceCode, long groupId);

	/**
	 * Returns the wiki node matching the UUID and group.
	 *
	 * @param uuid the wiki node's UUID
	 * @param groupId the primary key of the group
	 * @return the matching wiki node, or <code>null</code> if a matching wiki node could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WikiNode fetchWikiNodeByUuidAndGroupId(String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WikiNode getNode(long nodeId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WikiNode getNode(long groupId, String nodeName)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WikiNode> getNodes(long groupId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WikiNode> getNodes(long groupId, int status)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WikiNode> getNodes(long groupId, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WikiNode> getNodes(long groupId, int status, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getNodesCount(long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getNodesCount(long groupId, int status);

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Returns the wiki node with the primary key.
	 *
	 * @param nodeId the primary key of the wiki node
	 * @return the wiki node
	 * @throws PortalException if a wiki node with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WikiNode getWikiNode(long nodeId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WikiNode getWikiNodeByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	/**
	 * Returns the wiki node matching the UUID and group.
	 *
	 * @param uuid the wiki node's UUID
	 * @param groupId the primary key of the group
	 * @return the matching wiki node
	 * @throws PortalException if a matching wiki node could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public WikiNode getWikiNodeByUuidAndGroupId(String uuid, long groupId)
		throws PortalException;

	/**
	 * Returns a range of all the wiki nodes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.wiki.model.impl.WikiNodeModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of wiki nodes
	 * @param end the upper bound of the range of wiki nodes (not inclusive)
	 * @return the range of wiki nodes
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WikiNode> getWikiNodes(int start, int end);

	/**
	 * Returns all the wiki nodes matching the UUID and company.
	 *
	 * @param uuid the UUID of the wiki nodes
	 * @param companyId the primary key of the company
	 * @return the matching wiki nodes, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WikiNode> getWikiNodesByUuidAndCompanyId(
		String uuid, long companyId);

	/**
	 * Returns a range of wiki nodes matching the UUID and company.
	 *
	 * @param uuid the UUID of the wiki nodes
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of wiki nodes
	 * @param end the upper bound of the range of wiki nodes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching wiki nodes, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<WikiNode> getWikiNodesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<WikiNode> orderByComparator);

	/**
	 * Returns the number of wiki nodes.
	 *
	 * @return the number of wiki nodes
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getWikiNodesCount();

	public void importPages(
			long userId, long nodeId, InputStream[] inputStreams,
			Map<String, String[]> options)
		throws PortalException;

	public WikiNode moveNodeToTrash(long userId, long nodeId)
		throws PortalException;

	public WikiNode moveNodeToTrash(long userId, WikiNode node)
		throws PortalException;

	public void restoreNodeFromTrash(long userId, WikiNode node)
		throws PortalException;

	public void subscribeNode(long userId, long nodeId) throws PortalException;

	public void unsubscribeNode(long userId, long nodeId)
		throws PortalException;

	public WikiNode updateNode(
			long nodeId, String name, String description,
			ServiceContext serviceContext)
		throws PortalException;

	public WikiNode updateStatus(
			long userId, WikiNode node, int status,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Updates the wiki node in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect WikiNodeLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param wikiNode the wiki node
	 * @return the wiki node that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public WikiNode updateWikiNode(WikiNode wikiNode);

	@Override
	@Transactional(enabled = false)
	public CTPersistence<WikiNode> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<WikiNode> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<WikiNode>, R, E> updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1315847947