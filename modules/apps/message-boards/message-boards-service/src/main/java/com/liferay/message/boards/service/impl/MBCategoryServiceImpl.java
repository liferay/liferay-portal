/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.impl;

import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.service.base.MBCategoryServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = {
		"json.web.service.context.name=mb",
		"json.web.service.context.path=MBCategory"
	},
	service = AopService.class
)
public class MBCategoryServiceImpl extends MBCategoryServiceBaseImpl {

	@Override
	public MBCategory addCategory(
			String externalReferenceCode, long userId, long parentCategoryId,
			String name, String description, ServiceContext serviceContext)
		throws PortalException {

		ModelResourcePermissionUtil.check(
			_categoryModelResourcePermission, getPermissionChecker(),
			serviceContext.getScopeGroupId(), parentCategoryId,
			ActionKeys.ADD_CATEGORY);

		return mbCategoryLocalService.addCategory(
			externalReferenceCode, userId, parentCategoryId, name, description,
			serviceContext);
	}

	@Override
	public MBCategory addCategory(
			String externalReferenceCode, long parentCategoryId, String name,
			String description, String displayStyle, String emailAddress,
			String inProtocol, String inServerName, int inServerPort,
			boolean inUseSSL, String inUserName, String inPassword,
			int inReadInterval, String outEmailAddress, boolean outCustom,
			String outServerName, int outServerPort, boolean outUseSSL,
			String outUserName, String outPassword, boolean mailingListActive,
			boolean allowAnonymousEmail, ServiceContext serviceContext)
		throws PortalException {

		ModelResourcePermissionUtil.check(
			_categoryModelResourcePermission, getPermissionChecker(),
			serviceContext.getScopeGroupId(), parentCategoryId,
			ActionKeys.ADD_CATEGORY);

		return mbCategoryLocalService.addCategory(
			externalReferenceCode, getUserId(), parentCategoryId, name,
			description, displayStyle, emailAddress, inProtocol, inServerName,
			inServerPort, inUseSSL, inUserName, inPassword, inReadInterval,
			outEmailAddress, outCustom, outServerName, outServerPort, outUseSSL,
			outUserName, outPassword, mailingListActive, allowAnonymousEmail,
			serviceContext);
	}

	@Override
	public void deleteCategory(long categoryId, boolean includeTrashedEntries)
		throws PortalException {

		MBCategory category = mbCategoryPersistence.findByPrimaryKey(
			categoryId);

		_categoryModelResourcePermission.check(
			getPermissionChecker(), category, ActionKeys.DELETE);

		mbCategoryLocalService.deleteCategory(category, includeTrashedEntries);
	}

	@Override
	public void deleteCategory(long groupId, long categoryId)
		throws PortalException {

		ModelResourcePermissionUtil.check(
			_categoryModelResourcePermission, getPermissionChecker(), groupId,
			categoryId, ActionKeys.DELETE);

		mbCategoryLocalService.deleteCategory(categoryId);
	}

	@Override
	public void deleteCategory(String externalReferenceCode, long groupId)
		throws PortalException {

		MBCategory category = mbCategoryPersistence.findByERC_G(
			externalReferenceCode, groupId);

		_categoryModelResourcePermission.check(
			getPermissionChecker(), category, ActionKeys.DELETE);

		mbCategoryLocalService.deleteCategory(category);
	}

	@Override
	public MBCategory fetchMBCategory(long groupId, String friendlyURL)
		throws PortalException {

		MBCategory mbCategory = mbCategoryPersistence.fetchByG_F(
			groupId,
			_friendlyURLNormalizer.normalizeWithEncodingPeriodsAndSlashes(
				friendlyURL));

		if (mbCategory == null) {
			return null;
		}

		_categoryModelResourcePermission.check(
			getPermissionChecker(), mbCategory, ActionKeys.VIEW);

		return mbCategory;
	}

	@Override
	public List<MBCategory> getCategories(long groupId) {
		return mbCategoryPersistence.filterFindByGroupId(groupId);
	}

	@Override
	public List<MBCategory> getCategories(long groupId, int status) {
		return mbCategoryPersistence.filterFindByG_S(groupId, status);
	}

	@Override
	public List<MBCategory> getCategories(
		long groupId, long parentCategoryId, int start, int end) {

		return mbCategoryPersistence.filterFindByG_P(
			groupId, parentCategoryId, start, end);
	}

	@Override
	public List<MBCategory> getCategories(
		long groupId, long parentCategoryId, int status, int start, int end) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return mbCategoryPersistence.filterFindByG_P(
				groupId, parentCategoryId, start, end);
		}

		return mbCategoryPersistence.filterFindByG_P_S(
			groupId, parentCategoryId, status, start, end);
	}

	@Override
	public List<MBCategory> getCategories(
		long groupId, long excludedCategoryId, long parentCategoryId,
		int status, int start, int end) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return mbCategoryPersistence.filterFindByNotC_G_P(
				excludedCategoryId, groupId, parentCategoryId, start, end);
		}

		return mbCategoryPersistence.filterFindByNotC_G_P_S(
			excludedCategoryId, groupId, parentCategoryId, status, start, end);
	}

	@Override
	public List<MBCategory> getCategories(
			long groupId, long parentCategoryId,
			QueryDefinition<MBCategory> queryDefinition)
		throws PortalException {

		if (queryDefinition.isIncludeOwner() &&
			(queryDefinition.getOwnerUserId() != 0)) {

			queryDefinition.setOwnerUserId(getUserId());
		}

		return mbCategoryFinder.filterFindC_ByG_P(
			groupId, parentCategoryId, queryDefinition);
	}

	@Override
	public List<MBCategory> getCategories(
		long groupId, long[] parentCategoryIds, int start, int end) {

		return mbCategoryPersistence.filterFindByG_P(
			groupId, parentCategoryIds, start, end);
	}

	@Override
	public List<MBCategory> getCategories(
		long groupId, long[] parentCategoryIds, int status, int start,
		int end) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return mbCategoryPersistence.filterFindByG_P(
				groupId, parentCategoryIds, start, end);
		}

		return mbCategoryPersistence.filterFindByG_P_S(
			groupId, parentCategoryIds, status, start, end);
	}

	@Override
	public List<MBCategory> getCategories(
		long groupId, long[] excludedCategoryIds, long[] parentCategoryIds,
		int status, int start, int end) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return mbCategoryPersistence.filterFindByNotC_G_P(
				excludedCategoryIds, groupId, parentCategoryIds, start, end);
		}

		return mbCategoryPersistence.filterFindByNotC_G_P_S(
			excludedCategoryIds, groupId, parentCategoryIds, status, start,
			end);
	}

	@Override
	public List<Object> getCategoriesAndThreads(long groupId, long categoryId) {
		QueryDefinition<?> queryDefinition = new QueryDefinition<>(
			WorkflowConstants.STATUS_ANY);

		return mbCategoryFinder.filterFindC_T_ByG_C(
			groupId, categoryId, queryDefinition);
	}

	@Override
	public List<Object> getCategoriesAndThreads(
		long groupId, long categoryId, int status) {

		QueryDefinition<?> queryDefinition = new QueryDefinition<>(status);

		return mbCategoryFinder.filterFindC_T_ByG_C(
			groupId, categoryId, queryDefinition);
	}

	@Override
	public List<Object> getCategoriesAndThreads(
		long groupId, long categoryId, int status, int start, int end) {

		QueryDefinition<?> queryDefinition = new QueryDefinition<>(
			status, start, end, null);

		return mbCategoryFinder.filterFindC_T_ByG_C(
			groupId, categoryId, queryDefinition);
	}

	@Override
	public List<Object> getCategoriesAndThreads(
		long groupId, long categoryId, int status, int start, int end,
		OrderByComparator<?> orderByComparator) {

		QueryDefinition<?> queryDefinition = new QueryDefinition<>(
			status, start, end, orderByComparator);

		return mbCategoryFinder.filterFindC_T_ByG_C(
			groupId, categoryId, queryDefinition);
	}

	@Override
	public List<Object> getCategoriesAndThreads(
			long groupId, long categoryId, QueryDefinition<?> queryDefinition)
		throws PortalException {

		if (queryDefinition.isIncludeOwner() &&
			(queryDefinition.getOwnerUserId() != 0)) {

			queryDefinition.setOwnerUserId(getUserId());
		}

		return mbCategoryFinder.filterFindC_T_ByG_C(
			groupId, categoryId, queryDefinition);
	}

	@Override
	public int getCategoriesAndThreadsCount(long groupId, long categoryId) {
		QueryDefinition<?> queryDefinition = new QueryDefinition<>(
			WorkflowConstants.STATUS_ANY);

		return mbCategoryFinder.filterCountC_T_ByG_C(
			groupId, categoryId, queryDefinition);
	}

	@Override
	public int getCategoriesAndThreadsCount(
		long groupId, long categoryId, int status) {

		QueryDefinition<?> queryDefinition = new QueryDefinition<>(status);

		return mbCategoryFinder.filterCountC_T_ByG_C(
			groupId, categoryId, queryDefinition);
	}

	@Override
	public int getCategoriesAndThreadsCount(
			long groupId, long categoryId, QueryDefinition<?> queryDefinition)
		throws PortalException {

		if (queryDefinition.isIncludeOwner() &&
			(queryDefinition.getOwnerUserId() != 0)) {

			queryDefinition.setOwnerUserId(getUserId());
		}

		return mbCategoryFinder.filterCountC_T_ByG_C(
			groupId, categoryId, queryDefinition);
	}

	@Override
	public int getCategoriesCount(long groupId, long parentCategoryId) {
		return mbCategoryPersistence.filterCountByG_P(
			groupId, parentCategoryId);
	}

	@Override
	public int getCategoriesCount(
		long groupId, long parentCategoryId, int status) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return mbCategoryPersistence.filterCountByG_P(
				groupId, parentCategoryId);
		}

		return mbCategoryPersistence.filterCountByG_P_S(
			groupId, parentCategoryId, status);
	}

	@Override
	public int getCategoriesCount(
		long groupId, long excludedCategoryId, long parentCategoryId,
		int status) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return mbCategoryPersistence.filterCountByNotC_G_P(
				excludedCategoryId, groupId, parentCategoryId);
		}

		return mbCategoryPersistence.filterCountByNotC_G_P_S(
			excludedCategoryId, groupId, parentCategoryId, status);
	}

	@Override
	public int getCategoriesCount(
			long groupId, long parentCategoryId,
			QueryDefinition<?> queryDefinition)
		throws PortalException {

		if (queryDefinition.isIncludeOwner() &&
			(queryDefinition.getOwnerUserId() != 0)) {

			queryDefinition.setOwnerUserId(getUserId());
		}

		return mbCategoryFinder.filterCountC_ByG_P(
			groupId, parentCategoryId, queryDefinition);
	}

	@Override
	public int getCategoriesCount(long groupId, long[] parentCategoryIds) {
		return mbCategoryPersistence.filterCountByG_P(
			groupId, parentCategoryIds);
	}

	@Override
	public int getCategoriesCount(
		long groupId, long[] parentCategoryIds, int status) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return mbCategoryPersistence.filterCountByG_P(
				groupId, parentCategoryIds);
		}

		return mbCategoryPersistence.filterCountByG_P_S(
			groupId, parentCategoryIds, status);
	}

	@Override
	public int getCategoriesCount(
		long groupId, long[] excludedCategoryIds, long[] parentCategoryIds,
		int status) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return mbCategoryPersistence.filterCountByNotC_G_P(
				excludedCategoryIds, groupId, parentCategoryIds);
		}

		return mbCategoryPersistence.filterCountByNotC_G_P_S(
			excludedCategoryIds, groupId, parentCategoryIds, status);
	}

	@Override
	public MBCategory getCategory(long categoryId) throws PortalException {
		MBCategory category = mbCategoryPersistence.findByPrimaryKey(
			categoryId);

		_categoryModelResourcePermission.check(
			getPermissionChecker(), category, ActionKeys.VIEW);

		return category;
	}

	@Override
	public long[] getCategoryIds(long groupId, long categoryId) {
		List<Long> categoryIds = new ArrayList<>();

		categoryIds.add(categoryId);

		getSubcategoryIds(categoryIds, groupId, categoryId);

		return ArrayUtil.toArray(categoryIds.toArray(new Long[0]));
	}

	@Override
	public MBCategory getMBCategory(long groupId, String friendlyURL)
		throws PortalException {

		MBCategory mbCategory = mbCategoryPersistence.findByG_F(
			groupId,
			_friendlyURLNormalizer.normalizeWithEncodingPeriodsAndSlashes(
				friendlyURL));

		_categoryModelResourcePermission.check(
			getPermissionChecker(), mbCategory, ActionKeys.VIEW);

		return mbCategory;
	}

	@Override
	public List<Long> getSubcategoryIds(
		List<Long> categoryIds, long groupId, long categoryId) {

		List<MBCategory> categories = mbCategoryPersistence.filterFindByG_P(
			groupId, categoryId);

		for (MBCategory category : categories) {
			if (category.isInTrash()) {
				continue;
			}

			categoryIds.add(category.getCategoryId());

			getSubcategoryIds(
				categoryIds, category.getGroupId(), category.getCategoryId());
		}

		return categoryIds;
	}

	@Override
	public List<MBCategory> getSubscribedCategories(
		long groupId, long userId, int start, int end) {

		long[] categoryIds = getCategoryIds(
			groupId, MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID);

		if (categoryIds.length == 0) {
			return Collections.emptyList();
		}

		QueryDefinition<MBCategory> queryDefinition = new QueryDefinition<>(
			WorkflowConstants.STATUS_ANY, start, end, null);

		return mbCategoryFinder.filterFindC_ByS_G_U_P(
			groupId, userId, categoryIds, queryDefinition);
	}

	@Override
	public int getSubscribedCategoriesCount(long groupId, long userId) {
		long[] categoryIds = getCategoryIds(
			groupId, MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID);

		if (categoryIds.length == 0) {
			return 0;
		}

		QueryDefinition<MBCategory> queryDefinition = new QueryDefinition<>(
			WorkflowConstants.STATUS_ANY);

		return mbCategoryFinder.filterCountC_ByS_G_U_P(
			groupId, userId, categoryIds, queryDefinition);
	}

	@Override
	public MBCategory moveCategory(
			long categoryId, long parentCategoryId,
			boolean mergeWithParentCategory)
		throws PortalException {

		MBCategory category = mbCategoryPersistence.findByPrimaryKey(
			categoryId);

		_categoryModelResourcePermission.check(
			getPermissionChecker(), category, ActionKeys.UPDATE);

		return mbCategoryLocalService.moveCategory(
			categoryId, parentCategoryId, mergeWithParentCategory);
	}

	@Override
	public MBCategory moveCategoryFromTrash(long categoryId, long newCategoryId)
		throws PortalException {

		MBCategory category = mbCategoryPersistence.findByPrimaryKey(
			categoryId);

		_categoryModelResourcePermission.check(
			getPermissionChecker(), category, ActionKeys.UPDATE);

		return mbCategoryLocalService.moveCategoryFromTrash(
			getUserId(), categoryId, newCategoryId);
	}

	@Override
	public MBCategory moveCategoryToTrash(long categoryId)
		throws PortalException {

		MBCategory category = mbCategoryPersistence.findByPrimaryKey(
			categoryId);

		_categoryModelResourcePermission.check(
			getPermissionChecker(), category, ActionKeys.DELETE);

		return mbCategoryLocalService.moveCategoryToTrash(
			getUserId(), categoryId);
	}

	@Override
	public void restoreCategoryFromTrash(long categoryId)
		throws PortalException {

		MBCategory category = mbCategoryPersistence.findByPrimaryKey(
			categoryId);

		_categoryModelResourcePermission.check(
			getPermissionChecker(), category, ActionKeys.DELETE);

		mbCategoryLocalService.restoreCategoryFromTrash(
			getUserId(), categoryId);
	}

	@Override
	public void subscribeCategory(long groupId, long categoryId)
		throws PortalException {

		ModelResourcePermissionUtil.check(
			_categoryModelResourcePermission, getPermissionChecker(), groupId,
			categoryId, ActionKeys.SUBSCRIBE);

		mbCategoryLocalService.subscribeCategory(
			getUserId(), groupId, categoryId);
	}

	@Override
	public void unsubscribeCategory(long groupId, long categoryId)
		throws PortalException {

		ModelResourcePermissionUtil.check(
			_categoryModelResourcePermission, getPermissionChecker(), groupId,
			categoryId, ActionKeys.SUBSCRIBE);

		mbCategoryLocalService.unsubscribeCategory(
			getUserId(), groupId, categoryId);
	}

	@Override
	public MBCategory updateCategory(
			long categoryId, long parentCategoryId, String name,
			String description, String displayStyle, String emailAddress,
			String inProtocol, String inServerName, int inServerPort,
			boolean inUseSSL, String inUserName, String inPassword,
			int inReadInterval, String outEmailAddress, boolean outCustom,
			String outServerName, int outServerPort, boolean outUseSSL,
			String outUserName, String outPassword, boolean mailingListActive,
			boolean allowAnonymousEmail, boolean mergeWithParentCategory,
			ServiceContext serviceContext)
		throws PortalException {

		MBCategory category = mbCategoryPersistence.findByPrimaryKey(
			categoryId);

		_categoryModelResourcePermission.check(
			getPermissionChecker(), category, ActionKeys.UPDATE);

		return mbCategoryLocalService.updateCategory(
			categoryId, parentCategoryId, name, description, displayStyle,
			emailAddress, inProtocol, inServerName, inServerPort, inUseSSL,
			inUserName, inPassword, inReadInterval, outEmailAddress, outCustom,
			outServerName, outServerPort, outUseSSL, outUserName, outPassword,
			mailingListActive, allowAnonymousEmail, mergeWithParentCategory,
			serviceContext);
	}

	@Reference(
		target = "(model.class.name=com.liferay.message.boards.model.MBCategory)"
	)
	private ModelResourcePermission<MBCategory>
		_categoryModelResourcePermission;

	@Reference
	private FriendlyURLNormalizer _friendlyURLNormalizer;

}