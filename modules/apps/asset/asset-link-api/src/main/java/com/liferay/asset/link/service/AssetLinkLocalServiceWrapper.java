/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.link.service;

import com.liferay.asset.link.model.AssetLink;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link AssetLinkLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see AssetLinkLocalService
 * @generated
 */
public class AssetLinkLocalServiceWrapper
	implements AssetLinkLocalService, ServiceWrapper<AssetLinkLocalService> {

	public AssetLinkLocalServiceWrapper() {
		this(null);
	}

	public AssetLinkLocalServiceWrapper(
		AssetLinkLocalService assetLinkLocalService) {

		_assetLinkLocalService = assetLinkLocalService;
	}

	/**
	 * Adds the asset link to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetLink the asset link
	 * @return the asset link that was added
	 */
	@Override
	public AssetLink addAssetLink(AssetLink assetLink) {
		return _assetLinkLocalService.addAssetLink(assetLink);
	}

	/**
	 * Adds a new asset link.
	 *
	 * @param userId the primary key of the link's creator
	 * @param entryId1 the primary key of the first asset entry
	 * @param entryId2 the primary key of the second asset entry
	 * @param type the link type. Acceptable values include {@link
	 AssetLinkConstants#TYPE_RELATED} which is a bidirectional
	 relationship and {@link AssetLinkConstants#TYPE_CHILD} which is a
	 unidirectional relationship. For more information see {@link
	 AssetLinkConstants}
	 * @param weight the weight of the relationship, allowing precedence
	 ordering of links
	 * @return the asset link
	 */
	@Override
	public AssetLink addLink(
			long userId, long entryId1, long entryId2, int type, int weight)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetLinkLocalService.addLink(
			userId, entryId1, entryId2, type, weight);
	}

	/**
	 * Creates a new asset link with the primary key. Does not add the asset link to the database.
	 *
	 * @param linkId the primary key for the new asset link
	 * @return the new asset link
	 */
	@Override
	public AssetLink createAssetLink(long linkId) {
		return _assetLinkLocalService.createAssetLink(linkId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetLinkLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the asset link from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetLink the asset link
	 * @return the asset link that was removed
	 */
	@Override
	public AssetLink deleteAssetLink(AssetLink assetLink) {
		return _assetLinkLocalService.deleteAssetLink(assetLink);
	}

	/**
	 * Deletes the asset link with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param linkId the primary key of the asset link
	 * @return the asset link that was removed
	 * @throws PortalException if a asset link with the primary key could not be found
	 */
	@Override
	public AssetLink deleteAssetLink(long linkId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetLinkLocalService.deleteAssetLink(linkId);
	}

	@Override
	public void deleteGroupLinks(long groupId) {
		_assetLinkLocalService.deleteGroupLinks(groupId);
	}

	/**
	 * Deletes the asset link.
	 *
	 * @param link the asset link
	 */
	@Override
	public void deleteLink(AssetLink link) {
		_assetLinkLocalService.deleteLink(link);
	}

	/**
	 * Deletes the asset link.
	 *
	 * @param linkId the primary key of the asset link
	 */
	@Override
	public void deleteLink(long linkId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_assetLinkLocalService.deleteLink(linkId);
	}

	/**
	 * Deletes all links associated with the asset entry.
	 *
	 * @param entryId the primary key of the asset entry
	 */
	@Override
	public void deleteLinks(long entryId) {
		_assetLinkLocalService.deleteLinks(entryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetLinkLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _assetLinkLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _assetLinkLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _assetLinkLocalService.dynamicQuery();
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

		return _assetLinkLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.link.model.impl.AssetLinkModelImpl</code>.
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

		return _assetLinkLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.link.model.impl.AssetLinkModelImpl</code>.
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

		return _assetLinkLocalService.dynamicQuery(
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

		return _assetLinkLocalService.dynamicQueryCount(dynamicQuery);
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

		return _assetLinkLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public AssetLink fetchAssetLink(long linkId) {
		return _assetLinkLocalService.fetchAssetLink(linkId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _assetLinkLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the asset link with the primary key.
	 *
	 * @param linkId the primary key of the asset link
	 * @return the asset link
	 * @throws PortalException if a asset link with the primary key could not be found
	 */
	@Override
	public AssetLink getAssetLink(long linkId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetLinkLocalService.getAssetLink(linkId);
	}

	/**
	 * Returns a range of all the asset links.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.asset.link.model.impl.AssetLinkModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of asset links
	 * @param end the upper bound of the range of asset links (not inclusive)
	 * @return the range of asset links
	 */
	@Override
	public java.util.List<AssetLink> getAssetLinks(int start, int end) {
		return _assetLinkLocalService.getAssetLinks(start, end);
	}

	/**
	 * Returns the number of asset links.
	 *
	 * @return the number of asset links
	 */
	@Override
	public int getAssetLinksCount() {
		return _assetLinkLocalService.getAssetLinksCount();
	}

	/**
	 * Returns all the asset links whose first entry ID is the given entry ID.
	 *
	 * @param entryId the primary key of the asset entry
	 * @return the asset links whose first entry ID is the given entry ID
	 */
	@Override
	public java.util.List<AssetLink> getDirectLinks(long entryId) {
		return _assetLinkLocalService.getDirectLinks(entryId);
	}

	@Override
	public java.util.List<AssetLink> getDirectLinks(
		long entryId, boolean excludeInvisibleLinks) {

		return _assetLinkLocalService.getDirectLinks(
			entryId, excludeInvisibleLinks);
	}

	/**
	 * Returns all the asset links of the given link type whose first entry ID
	 * is the given entry ID.
	 *
	 * @param entryId the primary key of the asset entry
	 * @param typeId the link type. Acceptable values include {@link
	 AssetLinkConstants#TYPE_RELATED} which is a bidirectional
	 relationship and {@link AssetLinkConstants#TYPE_CHILD} which is a
	 unidirectional relationship. For more information see {@link
	 AssetLinkConstants}
	 * @return the asset links of the given link type whose first entry ID is
	 the given entry ID
	 */
	@Override
	public java.util.List<AssetLink> getDirectLinks(long entryId, int typeId) {
		return _assetLinkLocalService.getDirectLinks(entryId, typeId);
	}

	@Override
	public java.util.List<AssetLink> getDirectLinks(
		long entryId, int typeId, boolean excludeInvisibleLinks) {

		return _assetLinkLocalService.getDirectLinks(
			entryId, typeId, excludeInvisibleLinks);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _assetLinkLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns all the asset links whose first or second entry ID is the given
	 * entry ID.
	 *
	 * @param entryId the primary key of the asset entry
	 * @return the asset links whose first or second entry ID is the given entry
	 ID
	 */
	@Override
	public java.util.List<AssetLink> getLinks(long entryId) {
		return _assetLinkLocalService.getLinks(entryId);
	}

	@Override
	public java.util.List<AssetLink> getLinks(
		long groupId, java.util.Date startDate, java.util.Date endDate,
		int start, int end) {

		return _assetLinkLocalService.getLinks(
			groupId, startDate, endDate, start, end);
	}

	/**
	 * Returns all the asset links of the given link type whose first or second
	 * entry ID is the given entry ID.
	 *
	 * @param entryId the primary key of the asset entry
	 * @param typeId the link type. Acceptable values include {@link
	 AssetLinkConstants#TYPE_RELATED} which is a bidirectional
	 relationship and {@link AssetLinkConstants#TYPE_CHILD} which is a
	 unidirectional relationship. For more information see {@link
	 AssetLinkConstants}
	 * @return the asset links of the given link type whose first or second
	 entry ID is the given entry ID
	 */
	@Override
	public java.util.List<AssetLink> getLinks(long entryId, int typeId) {
		return _assetLinkLocalService.getLinks(entryId, typeId);
	}

	/**
	 * Returns all the asset links of an AssetEntry.
	 *
	 * @param classNameId AssetEntry's classNameId
	 * @param classPK AssetEntry's classPK
	 * @return the asset links of the given entry params
	 */
	@Override
	public java.util.List<AssetLink> getLinks(long classNameId, long classPK) {
		return _assetLinkLocalService.getLinks(classNameId, classPK);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _assetLinkLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetLinkLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns all the asset links of the given link type whose second entry ID
	 * is the given entry ID.
	 *
	 * @param entryId the primary key of the asset entry
	 * @param typeId the link type. Acceptable values include {@link
	 AssetLinkConstants#TYPE_RELATED} which is a bidirectional
	 relationship and {@link AssetLinkConstants#TYPE_CHILD} which is a
	 unidirectional relationship. For more information see {@link
	 AssetLinkConstants}
	 * @return the asset links of the given link type whose second entry ID is
	 the given entry ID
	 */
	@Override
	public java.util.List<AssetLink> getReverseLinks(long entryId, int typeId) {
		return _assetLinkLocalService.getReverseLinks(entryId, typeId);
	}

	/**
	 * Updates the asset link in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect AssetLinkLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param assetLink the asset link
	 * @return the asset link that was updated
	 */
	@Override
	public AssetLink updateAssetLink(AssetLink assetLink) {
		return _assetLinkLocalService.updateAssetLink(assetLink);
	}

	@Override
	public AssetLink updateLink(
			long userId, long entryId1, long entryId2, int typeId, int weight)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetLinkLocalService.updateLink(
			userId, entryId1, entryId2, typeId, weight);
	}

	/**
	 * Updates all links of the asset entry, replacing them with links
	 * associating the asset entry with the asset entries of the given link
	 * entry IDs.
	 *
	 * <p>
	 * If no link exists with a given link entry ID, a new link is created
	 * associating the current asset entry with the asset entry of that link
	 * entry ID. An existing link is deleted if either of its entry IDs is not
	 * contained in the given link entry IDs.
	 * </p>
	 *
	 * @param userId the primary key of the user updating the links
	 * @param entryId the primary key of the asset entry to be managed
	 * @param linkEntryIds the primary keys of the asset entries to be linked
	 with the asset entry to be managed
	 * @param typeId the type of the asset links to be created. Acceptable
	 values include {@link AssetLinkConstants#TYPE_RELATED} which is a
	 bidirectional relationship and {@link
	 AssetLinkConstants#TYPE_CHILD} which is a unidirectional
	 relationship. For more information see {@link AssetLinkConstants}
	 */
	@Override
	public void updateLinks(
			long userId, long entryId, long[] linkEntryIds, int typeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_assetLinkLocalService.updateLinks(
			userId, entryId, linkEntryIds, typeId);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _assetLinkLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<AssetLink> getCTPersistence() {
		return _assetLinkLocalService.getCTPersistence();
	}

	@Override
	public Class<AssetLink> getModelClass() {
		return _assetLinkLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<AssetLink>, R, E> updateUnsafeFunction)
		throws E {

		return _assetLinkLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public AssetLinkLocalService getWrappedService() {
		return _assetLinkLocalService;
	}

	@Override
	public void setWrappedService(AssetLinkLocalService assetLinkLocalService) {
		_assetLinkLocalService = assetLinkLocalService;
	}

	private AssetLinkLocalService _assetLinkLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1327607518