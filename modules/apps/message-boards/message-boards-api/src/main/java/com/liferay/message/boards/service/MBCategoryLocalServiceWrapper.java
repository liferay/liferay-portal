/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service;

import com.liferay.message.boards.model.MBCategory;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link MBCategoryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see MBCategoryLocalService
 * @generated
 */
public class MBCategoryLocalServiceWrapper
	implements MBCategoryLocalService, ServiceWrapper<MBCategoryLocalService> {

	public MBCategoryLocalServiceWrapper() {
		this(null);
	}

	public MBCategoryLocalServiceWrapper(
		MBCategoryLocalService mbCategoryLocalService) {

		_mbCategoryLocalService = mbCategoryLocalService;
	}

	@Override
	public MBCategory addCategory(
			String externalReferenceCode, long userId, long parentCategoryId,
			String name, String description,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbCategoryLocalService.addCategory(
			externalReferenceCode, userId, parentCategoryId, name, description,
			serviceContext);
	}

	@Override
	public MBCategory addCategory(
			String externalReferenceCode, long userId, long parentCategoryId,
			String name, String description, String displayStyle,
			String emailAddress, String inProtocol, String inServerName,
			int inServerPort, boolean inUseSSL, String inUserName,
			String inPassword, int inReadInterval, String outEmailAddress,
			boolean outCustom, String outServerName, int outServerPort,
			boolean outUseSSL, String outUserName, String outPassword,
			boolean allowAnonymous, boolean mailingListActive,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbCategoryLocalService.addCategory(
			externalReferenceCode, userId, parentCategoryId, name, description,
			displayStyle, emailAddress, inProtocol, inServerName, inServerPort,
			inUseSSL, inUserName, inPassword, inReadInterval, outEmailAddress,
			outCustom, outServerName, outServerPort, outUseSSL, outUserName,
			outPassword, allowAnonymous, mailingListActive, serviceContext);
	}

	@Override
	public void addCategoryResources(
			long categoryId, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_mbCategoryLocalService.addCategoryResources(
			categoryId, addGroupPermissions, addGuestPermissions);
	}

	@Override
	public void addCategoryResources(
			long categoryId,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_mbCategoryLocalService.addCategoryResources(
			categoryId, modelPermissions);
	}

	@Override
	public void addCategoryResources(
			MBCategory category, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_mbCategoryLocalService.addCategoryResources(
			category, addGroupPermissions, addGuestPermissions);
	}

	@Override
	public void addCategoryResources(
			MBCategory category,
			com.liferay.portal.kernel.service.permission.ModelPermissions
				modelPermissions)
		throws com.liferay.portal.kernel.exception.PortalException {

		_mbCategoryLocalService.addCategoryResources(
			category, modelPermissions);
	}

	/**
	 * Adds the message boards category to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBCategoryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param mbCategory the message boards category
	 * @return the message boards category that was added
	 */
	@Override
	public MBCategory addMBCategory(MBCategory mbCategory) {
		return _mbCategoryLocalService.addMBCategory(mbCategory);
	}

	/**
	 * Creates a new message boards category with the primary key. Does not add the message boards category to the database.
	 *
	 * @param categoryId the primary key for the new message boards category
	 * @return the new message boards category
	 */
	@Override
	public MBCategory createMBCategory(long categoryId) {
		return _mbCategoryLocalService.createMBCategory(categoryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbCategoryLocalService.createPersistedModel(primaryKeyObj);
	}

	@Override
	public void deleteCategories(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_mbCategoryLocalService.deleteCategories(groupId);
	}

	@Override
	public void deleteCategory(long categoryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_mbCategoryLocalService.deleteCategory(categoryId);
	}

	@Override
	public void deleteCategory(MBCategory category)
		throws com.liferay.portal.kernel.exception.PortalException {

		_mbCategoryLocalService.deleteCategory(category);
	}

	@Override
	public void deleteCategory(
			MBCategory category, boolean includeTrashedEntries)
		throws com.liferay.portal.kernel.exception.PortalException {

		_mbCategoryLocalService.deleteCategory(category, includeTrashedEntries);
	}

	@Override
	public void deleteCategoryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_mbCategoryLocalService.deleteCategoryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Deletes the message boards category with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBCategoryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param categoryId the primary key of the message boards category
	 * @return the message boards category that was removed
	 * @throws PortalException if a message boards category with the primary key could not be found
	 */
	@Override
	public MBCategory deleteMBCategory(long categoryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbCategoryLocalService.deleteMBCategory(categoryId);
	}

	/**
	 * Deletes the message boards category from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBCategoryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param mbCategory the message boards category
	 * @return the message boards category that was removed
	 */
	@Override
	public MBCategory deleteMBCategory(MBCategory mbCategory) {
		return _mbCategoryLocalService.deleteMBCategory(mbCategory);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbCategoryLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _mbCategoryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _mbCategoryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _mbCategoryLocalService.dynamicQuery();
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

		return _mbCategoryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
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

		return _mbCategoryLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
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

		return _mbCategoryLocalService.dynamicQuery(
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

		return _mbCategoryLocalService.dynamicQueryCount(dynamicQuery);
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

		return _mbCategoryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public MBCategory fetchMBCategory(long categoryId) {
		return _mbCategoryLocalService.fetchMBCategory(categoryId);
	}

	@Override
	public MBCategory fetchMBCategory(long groupId, String friendlyURL) {
		return _mbCategoryLocalService.fetchMBCategory(groupId, friendlyURL);
	}

	@Override
	public MBCategory fetchMBCategoryByExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		return _mbCategoryLocalService.fetchMBCategoryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the message boards category matching the UUID and group.
	 *
	 * @param uuid the message boards category's UUID
	 * @param groupId the primary key of the group
	 * @return the matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	@Override
	public MBCategory fetchMBCategoryByUuidAndGroupId(
		String uuid, long groupId) {

		return _mbCategoryLocalService.fetchMBCategoryByUuidAndGroupId(
			uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _mbCategoryLocalService.getActionableDynamicQuery();
	}

	@Override
	public java.util.List<MBCategory> getCategories(long groupId) {
		return _mbCategoryLocalService.getCategories(groupId);
	}

	@Override
	public java.util.List<MBCategory> getCategories(long groupId, int status) {
		return _mbCategoryLocalService.getCategories(groupId, status);
	}

	@Override
	public java.util.List<MBCategory> getCategories(
		long groupId, long parentCategoryId, int start, int end) {

		return _mbCategoryLocalService.getCategories(
			groupId, parentCategoryId, start, end);
	}

	@Override
	public java.util.List<MBCategory> getCategories(
		long groupId, long parentCategoryId, int status, int start, int end) {

		return _mbCategoryLocalService.getCategories(
			groupId, parentCategoryId, status, start, end);
	}

	@Override
	public java.util.List<MBCategory> getCategories(
		long groupId, long excludedCategoryId, long parentCategoryId,
		int status, int start, int end) {

		return _mbCategoryLocalService.getCategories(
			groupId, excludedCategoryId, parentCategoryId, status, start, end);
	}

	@Override
	public java.util.List<MBCategory> getCategories(
		long groupId, long[] parentCategoryIds, int start, int end) {

		return _mbCategoryLocalService.getCategories(
			groupId, parentCategoryIds, start, end);
	}

	@Override
	public java.util.List<MBCategory> getCategories(
		long groupId, long[] parentCategoryIds, int status, int start,
		int end) {

		return _mbCategoryLocalService.getCategories(
			groupId, parentCategoryIds, status, start, end);
	}

	@Override
	public java.util.List<MBCategory> getCategories(
		long groupId, long[] excludedCategoryIds, long[] parentCategoryIds,
		int status, int start, int end) {

		return _mbCategoryLocalService.getCategories(
			groupId, excludedCategoryIds, parentCategoryIds, status, start,
			end);
	}

	@Override
	public java.util.List<Object> getCategoriesAndThreads(
		long groupId, long categoryId) {

		return _mbCategoryLocalService.getCategoriesAndThreads(
			groupId, categoryId);
	}

	@Override
	public java.util.List<Object> getCategoriesAndThreads(
		long groupId, long categoryId, int status) {

		return _mbCategoryLocalService.getCategoriesAndThreads(
			groupId, categoryId, status);
	}

	@Override
	public java.util.List<Object> getCategoriesAndThreads(
		long groupId, long categoryId, int status, int start, int end) {

		return _mbCategoryLocalService.getCategoriesAndThreads(
			groupId, categoryId, status, start, end);
	}

	@Override
	public int getCategoriesAndThreadsCount(
		long groupId, long categoryId, int status) {

		return _mbCategoryLocalService.getCategoriesAndThreadsCount(
			groupId, categoryId, status);
	}

	@Override
	public int getCategoriesCount(long groupId) {
		return _mbCategoryLocalService.getCategoriesCount(groupId);
	}

	@Override
	public int getCategoriesCount(long groupId, int status) {
		return _mbCategoryLocalService.getCategoriesCount(groupId, status);
	}

	@Override
	public int getCategoriesCount(long groupId, long parentCategoryId) {
		return _mbCategoryLocalService.getCategoriesCount(
			groupId, parentCategoryId);
	}

	@Override
	public int getCategoriesCount(
		long groupId, long parentCategoryId, int status) {

		return _mbCategoryLocalService.getCategoriesCount(
			groupId, parentCategoryId, status);
	}

	@Override
	public int getCategoriesCount(
		long groupId, long excludedCategoryId, long parentCategoryId,
		int status) {

		return _mbCategoryLocalService.getCategoriesCount(
			groupId, excludedCategoryId, parentCategoryId, status);
	}

	@Override
	public int getCategoriesCount(long groupId, long[] parentCategoryIds) {
		return _mbCategoryLocalService.getCategoriesCount(
			groupId, parentCategoryIds);
	}

	@Override
	public int getCategoriesCount(
		long groupId, long[] parentCategoryIds, int status) {

		return _mbCategoryLocalService.getCategoriesCount(
			groupId, parentCategoryIds, status);
	}

	@Override
	public int getCategoriesCount(
		long groupId, long[] excludedCategoryIds, long[] parentCategoryIds,
		int status) {

		return _mbCategoryLocalService.getCategoriesCount(
			groupId, excludedCategoryIds, parentCategoryIds, status);
	}

	@Override
	public MBCategory getCategory(long categoryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbCategoryLocalService.getCategory(categoryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _mbCategoryLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _mbCategoryLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns a range of all the message boards categories.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of message boards categories
	 */
	@Override
	public java.util.List<MBCategory> getMBCategories(int start, int end) {
		return _mbCategoryLocalService.getMBCategories(start, end);
	}

	/**
	 * Returns all the message boards categories matching the UUID and company.
	 *
	 * @param uuid the UUID of the message boards categories
	 * @param companyId the primary key of the company
	 * @return the matching message boards categories, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<MBCategory> getMBCategoriesByUuidAndCompanyId(
		String uuid, long companyId) {

		return _mbCategoryLocalService.getMBCategoriesByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of message boards categories matching the UUID and company.
	 *
	 * @param uuid the UUID of the message boards categories
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching message boards categories, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<MBCategory> getMBCategoriesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<MBCategory>
			orderByComparator) {

		return _mbCategoryLocalService.getMBCategoriesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of message boards categories.
	 *
	 * @return the number of message boards categories
	 */
	@Override
	public int getMBCategoriesCount() {
		return _mbCategoryLocalService.getMBCategoriesCount();
	}

	/**
	 * Returns the message boards category with the primary key.
	 *
	 * @param categoryId the primary key of the message boards category
	 * @return the message boards category
	 * @throws PortalException if a message boards category with the primary key could not be found
	 */
	@Override
	public MBCategory getMBCategory(long categoryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbCategoryLocalService.getMBCategory(categoryId);
	}

	@Override
	public MBCategory getMBCategory(long groupId, String friendlyURL)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbCategoryLocalService.getMBCategory(groupId, friendlyURL);
	}

	@Override
	public MBCategory getMBCategoryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbCategoryLocalService.getMBCategoryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the message boards category matching the UUID and group.
	 *
	 * @param uuid the message boards category's UUID
	 * @param groupId the primary key of the group
	 * @return the matching message boards category
	 * @throws PortalException if a matching message boards category could not be found
	 */
	@Override
	public MBCategory getMBCategoryByUuidAndGroupId(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbCategoryLocalService.getMBCategoryByUuidAndGroupId(
			uuid, groupId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _mbCategoryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbCategoryLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public java.util.List<Long> getSubcategoryIds(
		java.util.List<Long> categoryIds, long groupId, long categoryId) {

		return _mbCategoryLocalService.getSubcategoryIds(
			categoryIds, groupId, categoryId);
	}

	@Override
	public void moveCategoriesToTrash(long groupId, long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_mbCategoryLocalService.moveCategoriesToTrash(groupId, userId);
	}

	@Override
	public MBCategory moveCategory(
			long categoryId, long parentCategoryId,
			boolean mergeWithParentCategory)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbCategoryLocalService.moveCategory(
			categoryId, parentCategoryId, mergeWithParentCategory);
	}

	@Override
	public MBCategory moveCategoryFromTrash(
			long userId, long categoryId, long newCategoryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbCategoryLocalService.moveCategoryFromTrash(
			userId, categoryId, newCategoryId);
	}

	@Override
	public MBCategory moveCategoryToTrash(long userId, long categoryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbCategoryLocalService.moveCategoryToTrash(userId, categoryId);
	}

	@Override
	public void restoreCategoryFromTrash(long userId, long categoryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_mbCategoryLocalService.restoreCategoryFromTrash(userId, categoryId);
	}

	@Override
	public void subscribeCategory(long userId, long groupId, long categoryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_mbCategoryLocalService.subscribeCategory(userId, groupId, categoryId);
	}

	@Override
	public void unsubscribeCategory(long userId, long groupId, long categoryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_mbCategoryLocalService.unsubscribeCategory(
			userId, groupId, categoryId);
	}

	@Override
	public MBCategory updateCategory(
			long categoryId, long parentCategoryId, String name,
			String description, String displayStyle, String emailAddress,
			String inProtocol, String inServerName, int inServerPort,
			boolean inUseSSL, String inUserName, String inPassword,
			int inReadInterval, String outEmailAddress, boolean outCustom,
			String outServerName, int outServerPort, boolean outUseSSL,
			String outUserName, String outPassword, boolean allowAnonymous,
			boolean mailingListActive, boolean mergeWithParentCategory,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbCategoryLocalService.updateCategory(
			categoryId, parentCategoryId, name, description, displayStyle,
			emailAddress, inProtocol, inServerName, inServerPort, inUseSSL,
			inUserName, inPassword, inReadInterval, outEmailAddress, outCustom,
			outServerName, outServerPort, outUseSSL, outUserName, outPassword,
			allowAnonymous, mailingListActive, mergeWithParentCategory,
			serviceContext);
	}

	/**
	 * Updates the message boards category in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect MBCategoryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param mbCategory the message boards category
	 * @return the message boards category that was updated
	 */
	@Override
	public MBCategory updateMBCategory(MBCategory mbCategory) {
		return _mbCategoryLocalService.updateMBCategory(mbCategory);
	}

	@Override
	public MBCategory updateStatus(long userId, long categoryId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _mbCategoryLocalService.updateStatus(userId, categoryId, status);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _mbCategoryLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<MBCategory> getCTPersistence() {
		return _mbCategoryLocalService.getCTPersistence();
	}

	@Override
	public Class<MBCategory> getModelClass() {
		return _mbCategoryLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<MBCategory>, R, E>
				updateUnsafeFunction)
		throws E {

		return _mbCategoryLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public MBCategoryLocalService getWrappedService() {
		return _mbCategoryLocalService;
	}

	@Override
	public void setWrappedService(
		MBCategoryLocalService mbCategoryLocalService) {

		_mbCategoryLocalService = mbCategoryLocalService;
	}

	private MBCategoryLocalService _mbCategoryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1855561538