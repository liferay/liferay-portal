/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.wiki.model.WikiNode;

/**
 * Provides a wrapper for {@link WikiNodeLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see WikiNodeLocalService
 * @generated
 */
public class WikiNodeLocalServiceWrapper
	implements ServiceWrapper<WikiNodeLocalService>, WikiNodeLocalService {

	public WikiNodeLocalServiceWrapper() {
		this(null);
	}

	public WikiNodeLocalServiceWrapper(
		WikiNodeLocalService wikiNodeLocalService) {

		_wikiNodeLocalService = wikiNodeLocalService;
	}

	@Override
	public WikiNode addDefaultNode(
			long userId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiNodeLocalService.addDefaultNode(userId, serviceContext);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link #addNode(String,
	 long, String, String, ServiceContext)}
	 */
	@Deprecated
	@Override
	public WikiNode addNode(
			long userId, String name, String description,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiNodeLocalService.addNode(
			userId, name, description, serviceContext);
	}

	@Override
	public WikiNode addNode(
			String externalReferenceCode, long userId, String name,
			String description,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiNodeLocalService.addNode(
			externalReferenceCode, userId, name, description, serviceContext);
	}

	@Override
	public void addNodeResources(
			long nodeId, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_wikiNodeLocalService.addNodeResources(
			nodeId, addGroupPermissions, addGuestPermissions);
	}

	@Override
	public void addNodeResources(
			WikiNode node, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_wikiNodeLocalService.addNodeResources(
			node, addGroupPermissions, addGuestPermissions);
	}

	@Override
	public void addNodeResources(
			WikiNode node,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_wikiNodeLocalService.addNodeResources(node, modelPermissions);
	}

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
	@Override
	public WikiNode addWikiNode(WikiNode wikiNode) {
		return _wikiNodeLocalService.addWikiNode(wikiNode);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiNodeLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new wiki node with the primary key. Does not add the wiki node to the database.
	 *
	 * @param nodeId the primary key for the new wiki node
	 * @return the new wiki node
	 */
	@Override
	public WikiNode createWikiNode(long nodeId) {
		return _wikiNodeLocalService.createWikiNode(nodeId);
	}

	@Override
	public void deleteNode(long nodeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_wikiNodeLocalService.deleteNode(nodeId);
	}

	@Override
	public void deleteNode(WikiNode node)
		throws com.liferay.portal.kernel.exception.PortalException {

		_wikiNodeLocalService.deleteNode(node);
	}

	@Override
	public void deleteNodes(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_wikiNodeLocalService.deleteNodes(groupId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiNodeLocalService.deletePersistedModel(persistedModel);
	}

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
	@Override
	public WikiNode deleteWikiNode(long nodeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiNodeLocalService.deleteWikiNode(nodeId);
	}

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
	@Override
	public WikiNode deleteWikiNode(WikiNode wikiNode) {
		return _wikiNodeLocalService.deleteWikiNode(wikiNode);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _wikiNodeLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _wikiNodeLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _wikiNodeLocalService.dynamicQuery();
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

		return _wikiNodeLocalService.dynamicQuery(dynamicQuery);
	}

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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _wikiNodeLocalService.dynamicQuery(dynamicQuery, start, end);
	}

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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _wikiNodeLocalService.dynamicQuery(
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

		return _wikiNodeLocalService.dynamicQueryCount(dynamicQuery);
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

		return _wikiNodeLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public WikiNode fetchNode(long groupId, String name) {
		return _wikiNodeLocalService.fetchNode(groupId, name);
	}

	@Override
	public WikiNode fetchNodeByUuidAndGroupId(String uuid, long groupId) {
		return _wikiNodeLocalService.fetchNodeByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public WikiNode fetchWikiNode(long nodeId) {
		return _wikiNodeLocalService.fetchWikiNode(nodeId);
	}

	@Override
	public WikiNode fetchWikiNodeByExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		return _wikiNodeLocalService.fetchWikiNodeByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the wiki node matching the UUID and group.
	 *
	 * @param uuid the wiki node's UUID
	 * @param groupId the primary key of the group
	 * @return the matching wiki node, or <code>null</code> if a matching wiki node could not be found
	 */
	@Override
	public WikiNode fetchWikiNodeByUuidAndGroupId(String uuid, long groupId) {
		return _wikiNodeLocalService.fetchWikiNodeByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _wikiNodeLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _wikiNodeLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _wikiNodeLocalService.getIndexableActionableDynamicQuery();
	}

	@Override
	public WikiNode getNode(long nodeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiNodeLocalService.getNode(nodeId);
	}

	@Override
	public WikiNode getNode(long groupId, String nodeName)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiNodeLocalService.getNode(groupId, nodeName);
	}

	@Override
	public java.util.List<WikiNode> getNodes(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiNodeLocalService.getNodes(groupId);
	}

	@Override
	public java.util.List<WikiNode> getNodes(long groupId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiNodeLocalService.getNodes(groupId, status);
	}

	@Override
	public java.util.List<WikiNode> getNodes(long groupId, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiNodeLocalService.getNodes(groupId, start, end);
	}

	@Override
	public java.util.List<WikiNode> getNodes(
			long groupId, int status, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiNodeLocalService.getNodes(groupId, status, start, end);
	}

	@Override
	public int getNodesCount(long groupId) {
		return _wikiNodeLocalService.getNodesCount(groupId);
	}

	@Override
	public int getNodesCount(long groupId, int status) {
		return _wikiNodeLocalService.getNodesCount(groupId, status);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _wikiNodeLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiNodeLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the wiki node with the primary key.
	 *
	 * @param nodeId the primary key of the wiki node
	 * @return the wiki node
	 * @throws PortalException if a wiki node with the primary key could not be found
	 */
	@Override
	public WikiNode getWikiNode(long nodeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiNodeLocalService.getWikiNode(nodeId);
	}

	@Override
	public WikiNode getWikiNodeByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiNodeLocalService.getWikiNodeByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the wiki node matching the UUID and group.
	 *
	 * @param uuid the wiki node's UUID
	 * @param groupId the primary key of the group
	 * @return the matching wiki node
	 * @throws PortalException if a matching wiki node could not be found
	 */
	@Override
	public WikiNode getWikiNodeByUuidAndGroupId(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiNodeLocalService.getWikiNodeByUuidAndGroupId(uuid, groupId);
	}

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
	@Override
	public java.util.List<WikiNode> getWikiNodes(int start, int end) {
		return _wikiNodeLocalService.getWikiNodes(start, end);
	}

	/**
	 * Returns all the wiki nodes matching the UUID and company.
	 *
	 * @param uuid the UUID of the wiki nodes
	 * @param companyId the primary key of the company
	 * @return the matching wiki nodes, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<WikiNode> getWikiNodesByUuidAndCompanyId(
		String uuid, long companyId) {

		return _wikiNodeLocalService.getWikiNodesByUuidAndCompanyId(
			uuid, companyId);
	}

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
	@Override
	public java.util.List<WikiNode> getWikiNodesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<WikiNode>
			orderByComparator) {

		return _wikiNodeLocalService.getWikiNodesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of wiki nodes.
	 *
	 * @return the number of wiki nodes
	 */
	@Override
	public int getWikiNodesCount() {
		return _wikiNodeLocalService.getWikiNodesCount();
	}

	@Override
	public void importPages(
			long userId, long nodeId, java.io.InputStream[] inputStreams,
			java.util.Map<String, String[]> options)
		throws com.liferay.portal.kernel.exception.PortalException {

		_wikiNodeLocalService.importPages(
			userId, nodeId, inputStreams, options);
	}

	@Override
	public WikiNode moveNodeToTrash(long userId, long nodeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiNodeLocalService.moveNodeToTrash(userId, nodeId);
	}

	@Override
	public WikiNode moveNodeToTrash(long userId, WikiNode node)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiNodeLocalService.moveNodeToTrash(userId, node);
	}

	@Override
	public void restoreNodeFromTrash(long userId, WikiNode node)
		throws com.liferay.portal.kernel.exception.PortalException {

		_wikiNodeLocalService.restoreNodeFromTrash(userId, node);
	}

	@Override
	public void subscribeNode(long userId, long nodeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_wikiNodeLocalService.subscribeNode(userId, nodeId);
	}

	@Override
	public void unsubscribeNode(long userId, long nodeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_wikiNodeLocalService.unsubscribeNode(userId, nodeId);
	}

	@Override
	public WikiNode updateNode(
			long nodeId, String name, String description,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiNodeLocalService.updateNode(
			nodeId, name, description, serviceContext);
	}

	@Override
	public WikiNode updateStatus(
			long userId, WikiNode node, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _wikiNodeLocalService.updateStatus(
			userId, node, status, serviceContext);
	}

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
	@Override
	public WikiNode updateWikiNode(WikiNode wikiNode) {
		return _wikiNodeLocalService.updateWikiNode(wikiNode);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _wikiNodeLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<WikiNode> getCTPersistence() {
		return _wikiNodeLocalService.getCTPersistence();
	}

	@Override
	public Class<WikiNode> getModelClass() {
		return _wikiNodeLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<WikiNode>, R, E> updateUnsafeFunction)
		throws E {

		return _wikiNodeLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public WikiNodeLocalService getWrappedService() {
		return _wikiNodeLocalService;
	}

	@Override
	public void setWrappedService(WikiNodeLocalService wikiNodeLocalService) {
		_wikiNodeLocalService = wikiNodeLocalService;
	}

	private WikiNodeLocalService _wikiNodeLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-491314651