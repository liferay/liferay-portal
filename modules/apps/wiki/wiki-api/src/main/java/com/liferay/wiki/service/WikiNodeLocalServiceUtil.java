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
import com.liferay.wiki.model.WikiNode;

import java.io.InputStream;
import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for WikiNode. This utility wraps
 * <code>com.liferay.wiki.service.impl.WikiNodeLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see WikiNodeLocalService
 * @generated
 */
public class WikiNodeLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.wiki.service.impl.WikiNodeLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static WikiNode addDefaultNode(
			long userId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addDefaultNode(userId, serviceContext);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link #addNode(String,
	 long, String, String, ServiceContext)}
	 */
	@Deprecated
	public static WikiNode addNode(
			long userId, String name, String description,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addNode(userId, name, description, serviceContext);
	}

	public static WikiNode addNode(
			String externalReferenceCode, long userId, String name,
			String description,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addNode(
			externalReferenceCode, userId, name, description, serviceContext);
	}

	public static void addNodeResources(
			long nodeId, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		getService().addNodeResources(
			nodeId, addGroupPermissions, addGuestPermissions);
	}

	public static void addNodeResources(
			WikiNode node, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		getService().addNodeResources(
			node, addGroupPermissions, addGuestPermissions);
	}

	public static void addNodeResources(
			WikiNode node,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws PortalException {

		getService().addNodeResources(node, modelPermissions);
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
	public static WikiNode addWikiNode(WikiNode wikiNode) {
		return getService().addWikiNode(wikiNode);
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
	 * Creates a new wiki node with the primary key. Does not add the wiki node to the database.
	 *
	 * @param nodeId the primary key for the new wiki node
	 * @return the new wiki node
	 */
	public static WikiNode createWikiNode(long nodeId) {
		return getService().createWikiNode(nodeId);
	}

	public static void deleteNode(long nodeId) throws PortalException {
		getService().deleteNode(nodeId);
	}

	public static void deleteNode(WikiNode node) throws PortalException {
		getService().deleteNode(node);
	}

	public static void deleteNodes(long groupId) throws PortalException {
		getService().deleteNodes(groupId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
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
	public static WikiNode deleteWikiNode(long nodeId) throws PortalException {
		return getService().deleteWikiNode(nodeId);
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
	public static WikiNode deleteWikiNode(WikiNode wikiNode) {
		return getService().deleteWikiNode(wikiNode);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.wiki.model.impl.WikiNodeModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.wiki.model.impl.WikiNodeModelImpl</code>.
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

	public static WikiNode fetchNode(long groupId, String name) {
		return getService().fetchNode(groupId, name);
	}

	public static WikiNode fetchNodeByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchNodeByUuidAndGroupId(uuid, groupId);
	}

	public static WikiNode fetchWikiNode(long nodeId) {
		return getService().fetchWikiNode(nodeId);
	}

	public static WikiNode fetchWikiNodeByExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		return getService().fetchWikiNodeByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the wiki node matching the UUID and group.
	 *
	 * @param uuid the wiki node's UUID
	 * @param groupId the primary key of the group
	 * @return the matching wiki node, or <code>null</code> if a matching wiki node could not be found
	 */
	public static WikiNode fetchWikiNodeByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchWikiNodeByUuidAndGroupId(uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	public static WikiNode getNode(long nodeId) throws PortalException {
		return getService().getNode(nodeId);
	}

	public static WikiNode getNode(long groupId, String nodeName)
		throws PortalException {

		return getService().getNode(groupId, nodeName);
	}

	public static List<WikiNode> getNodes(long groupId) throws PortalException {
		return getService().getNodes(groupId);
	}

	public static List<WikiNode> getNodes(long groupId, int status)
		throws PortalException {

		return getService().getNodes(groupId, status);
	}

	public static List<WikiNode> getNodes(long groupId, int start, int end)
		throws PortalException {

		return getService().getNodes(groupId, start, end);
	}

	public static List<WikiNode> getNodes(
			long groupId, int status, int start, int end)
		throws PortalException {

		return getService().getNodes(groupId, status, start, end);
	}

	public static int getNodesCount(long groupId) {
		return getService().getNodesCount(groupId);
	}

	public static int getNodesCount(long groupId, int status) {
		return getService().getNodesCount(groupId, status);
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

	/**
	 * Returns the wiki node with the primary key.
	 *
	 * @param nodeId the primary key of the wiki node
	 * @return the wiki node
	 * @throws PortalException if a wiki node with the primary key could not be found
	 */
	public static WikiNode getWikiNode(long nodeId) throws PortalException {
		return getService().getWikiNode(nodeId);
	}

	public static WikiNode getWikiNodeByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getWikiNodeByExternalReferenceCode(
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
	public static WikiNode getWikiNodeByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getWikiNodeByUuidAndGroupId(uuid, groupId);
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
	public static List<WikiNode> getWikiNodes(int start, int end) {
		return getService().getWikiNodes(start, end);
	}

	/**
	 * Returns all the wiki nodes matching the UUID and company.
	 *
	 * @param uuid the UUID of the wiki nodes
	 * @param companyId the primary key of the company
	 * @return the matching wiki nodes, or an empty list if no matches were found
	 */
	public static List<WikiNode> getWikiNodesByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getWikiNodesByUuidAndCompanyId(uuid, companyId);
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
	public static List<WikiNode> getWikiNodesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<WikiNode> orderByComparator) {

		return getService().getWikiNodesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of wiki nodes.
	 *
	 * @return the number of wiki nodes
	 */
	public static int getWikiNodesCount() {
		return getService().getWikiNodesCount();
	}

	public static void importPages(
			long userId, long nodeId, InputStream[] inputStreams,
			Map<String, String[]> options)
		throws PortalException {

		getService().importPages(userId, nodeId, inputStreams, options);
	}

	public static WikiNode moveNodeToTrash(long userId, long nodeId)
		throws PortalException {

		return getService().moveNodeToTrash(userId, nodeId);
	}

	public static WikiNode moveNodeToTrash(long userId, WikiNode node)
		throws PortalException {

		return getService().moveNodeToTrash(userId, node);
	}

	public static void restoreNodeFromTrash(long userId, WikiNode node)
		throws PortalException {

		getService().restoreNodeFromTrash(userId, node);
	}

	public static void subscribeNode(long userId, long nodeId)
		throws PortalException {

		getService().subscribeNode(userId, nodeId);
	}

	public static void unsubscribeNode(long userId, long nodeId)
		throws PortalException {

		getService().unsubscribeNode(userId, nodeId);
	}

	public static WikiNode updateNode(
			long nodeId, String name, String description,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateNode(
			nodeId, name, description, serviceContext);
	}

	public static WikiNode updateStatus(
			long userId, WikiNode node, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateStatus(userId, node, status, serviceContext);
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
	public static WikiNode updateWikiNode(WikiNode wikiNode) {
		return getService().updateWikiNode(wikiNode);
	}

	public static WikiNodeLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<WikiNodeLocalService> _serviceSnapshot =
		new Snapshot<>(
			WikiNodeLocalServiceUtil.class, WikiNodeLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:844953198