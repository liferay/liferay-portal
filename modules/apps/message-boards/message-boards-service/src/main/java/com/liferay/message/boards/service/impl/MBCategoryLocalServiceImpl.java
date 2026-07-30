/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.impl;

import com.liferay.expando.kernel.service.ExpandoRowLocalService;
import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.exception.CategoryNameException;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBMailingList;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.model.impl.MBCategoryImpl;
import com.liferay.message.boards.service.MBMailingListLocalService;
import com.liferay.message.boards.service.MBThreadLocalService;
import com.liferay.message.boards.service.base.MBCategoryLocalServiceBaseImpl;
import com.liferay.message.boards.service.persistence.MBMailingListPersistence;
import com.liferay.message.boards.service.persistence.MBMessagePersistence;
import com.liferay.message.boards.service.persistence.MBThreadPersistence;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.ratings.kernel.service.RatingsStatsLocalService;
import com.liferay.subscription.service.SubscriptionLocalService;
import com.liferay.trash.TrashHelper;
import com.liferay.trash.exception.RestoreEntryException;
import com.liferay.trash.exception.TrashEntryException;
import com.liferay.trash.model.TrashEntry;
import com.liferay.trash.model.TrashVersion;
import com.liferay.trash.service.TrashEntryLocalService;
import com.liferay.trash.service.TrashVersionLocalService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Wesley Gong
 */
@Component(
	property = "model.class.name=com.liferay.message.boards.model.MBCategory",
	service = AopService.class
)
public class MBCategoryLocalServiceImpl extends MBCategoryLocalServiceBaseImpl {

	@Override
	public MBCategory addCategory(
			String externalReferenceCode, long userId, long parentCategoryId,
			String name, String description, ServiceContext serviceContext)
		throws PortalException {

		return mbCategoryLocalService.addCategory(
			externalReferenceCode, userId, parentCategoryId, name, description,
			MBCategoryConstants.DEFAULT_DISPLAY_STYLE, null, null, null, 0,
			false, null, null, 0, null, false, null, 0, false, null, null,
			false, false, serviceContext);
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
			ServiceContext serviceContext)
		throws PortalException {

		// Category

		User user = _userLocalService.getUser(userId);

		long groupId = serviceContext.getScopeGroupId();

		parentCategoryId = _getParentCategoryId(groupId, parentCategoryId);

		_validate(name);

		long categoryId = counterLocalService.increment();

		MBCategory category = mbCategoryPersistence.create(categoryId);

		category.setUuid(serviceContext.getUuid());
		category.setExternalReferenceCode(externalReferenceCode);
		category.setGroupId(groupId);
		category.setCompanyId(user.getCompanyId());
		category.setUserId(user.getUserId());
		category.setUserName(user.getFullName());
		category.setParentCategoryId(parentCategoryId);
		category.setName(name);
		category.setDescription(description);
		category.setDisplayStyle(displayStyle);
		category.setFriendlyURL(
			_getUniqueFriendlyURL(groupId, categoryId, name));
		category.setExpandoBridgeAttributes(serviceContext);

		category = mbCategoryPersistence.update(category);

		// Resources

		if (serviceContext.isAddGroupPermissions() ||
			serviceContext.isAddGuestPermissions()) {

			addCategoryResources(
				category, serviceContext.isAddGroupPermissions(),
				serviceContext.isAddGuestPermissions());
		}
		else {
			addCategoryResources(
				category, serviceContext.getModelPermissions());
		}

		// Mailing list

		_mbMailingListLocalService.addMailingList(
			userId, groupId, category.getCategoryId(), emailAddress, inProtocol,
			inServerName, inServerPort, inUseSSL, inUserName, inPassword,
			inReadInterval, outEmailAddress, outCustom, outServerName,
			outServerPort, outUseSSL, outUserName, outPassword, allowAnonymous,
			mailingListActive, serviceContext);

		return category;
	}

	@Override
	public void addCategoryResources(
			long categoryId, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		if ((categoryId == MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) ||
			(categoryId == MBCategoryConstants.DISCUSSION_CATEGORY_ID)) {

			return;
		}

		MBCategory category = mbCategoryPersistence.findByPrimaryKey(
			categoryId);

		addCategoryResources(
			category, addGroupPermissions, addGuestPermissions);
	}

	@Override
	public void addCategoryResources(
			long categoryId, ModelPermissions modelPermissions)
		throws PortalException {

		if ((categoryId == MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) ||
			(categoryId == MBCategoryConstants.DISCUSSION_CATEGORY_ID)) {

			return;
		}

		MBCategory category = mbCategoryPersistence.findByPrimaryKey(
			categoryId);

		addCategoryResources(category, modelPermissions);
	}

	@Override
	public void addCategoryResources(
			MBCategory category, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException {

		_resourceLocalService.addResources(
			category.getCompanyId(), category.getGroupId(),
			category.getUserId(), MBCategory.class.getName(),
			category.getCategoryId(), false, addGroupPermissions,
			addGuestPermissions);
	}

	@Override
	public void addCategoryResources(
			MBCategory category, ModelPermissions modelPermissions)
		throws PortalException {

		_resourceLocalService.addModelResources(
			category.getCompanyId(), category.getGroupId(),
			category.getUserId(), MBCategory.class.getName(),
			category.getCategoryId(), modelPermissions);
	}

	@Override
	public void deleteCategories(long groupId) throws PortalException {
		List<MBCategory> categories = mbCategoryPersistence.findByGroupId(
			groupId);

		for (MBCategory category : categories) {
			mbCategoryLocalService.deleteCategory(category);
		}
	}

	@Override
	public void deleteCategory(long categoryId) throws PortalException {
		MBCategory category = mbCategoryPersistence.findByPrimaryKey(
			categoryId);

		mbCategoryLocalService.deleteCategory(category);
	}

	@Override
	public void deleteCategory(MBCategory category) throws PortalException {
		mbCategoryLocalService.deleteCategory(category, true);
	}

	@Override
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public void deleteCategory(
			MBCategory category, boolean includeTrashedEntries)
		throws PortalException {

		// Categories

		List<MBCategory> categories = mbCategoryPersistence.findByG_P(
			category.getGroupId(), category.getCategoryId());

		for (MBCategory curCategory : categories) {
			if (includeTrashedEntries ||
				!_trashHelper.isInTrashExplicitly(curCategory)) {

				mbCategoryLocalService.deleteCategory(
					curCategory, includeTrashedEntries);
			}
		}

		// Threads

		_mbThreadLocalService.deleteThreads(
			category.getGroupId(), category.getCategoryId(),
			includeTrashedEntries);

		// Mailing list

		MBMailingList mbMailingList = _mbMailingListPersistence.fetchByG_C(
			category.getGroupId(), category.getCategoryId());

		if (mbMailingList != null) {
			_mbMailingListLocalService.deleteMailingList(mbMailingList);
		}

		// Subscriptions

		_subscriptionLocalService.deleteSubscriptions(
			category.getCompanyId(), MBCategory.class.getName(),
			category.getCategoryId());

		// Expando

		_expandoRowLocalService.deleteRows(category.getCategoryId());

		// Ratings

		_ratingsStatsLocalService.deleteStats(
			MBCategory.class.getName(), category.getCategoryId());

		// Resources

		_resourceLocalService.deleteResource(
			category.getCompanyId(), MBCategory.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL, category.getCategoryId());

		// Trash

		if (_trashHelper.isInTrashExplicitly(category)) {
			_trashEntryLocalService.deleteEntry(
				MBCategory.class.getName(), category.getCategoryId());
		}
		else {
			_trashVersionLocalService.deleteTrashVersion(
				MBCategory.class.getName(), category.getCategoryId());
		}

		// Category

		mbCategoryLocalService.deleteMBCategory(category);
	}

	@Override
	public void deleteCategoryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		mbCategoryLocalService.deleteCategory(
			mbCategoryPersistence.findByERC_G(externalReferenceCode, groupId));
	}

	@Override
	public MBCategory fetchMBCategory(long groupId, String friendlyURL) {
		return mbCategoryPersistence.fetchByG_F(
			groupId,
			_friendlyURLNormalizer.normalizeWithEncodingPeriodsAndSlashes(
				friendlyURL));
	}

	@Override
	public List<MBCategory> getCategories(long groupId) {
		return mbCategoryPersistence.findByGroupId(groupId);
	}

	@Override
	public List<MBCategory> getCategories(long groupId, int status) {
		return mbCategoryPersistence.findByG_S(groupId, status);
	}

	@Override
	public List<MBCategory> getCategories(
		long groupId, long parentCategoryId, int start, int end) {

		return mbCategoryPersistence.findByG_P(
			groupId, parentCategoryId, start, end);
	}

	@Override
	public List<MBCategory> getCategories(
		long groupId, long parentCategoryId, int status, int start, int end) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return mbCategoryPersistence.findByG_P(
				groupId, parentCategoryId, start, end);
		}

		return mbCategoryPersistence.findByG_P_S(
			groupId, parentCategoryId, status, start, end);
	}

	@Override
	public List<MBCategory> getCategories(
		long groupId, long excludedCategoryId, long parentCategoryId,
		int status, int start, int end) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return mbCategoryPersistence.findByNotC_G_P(
				excludedCategoryId, groupId, parentCategoryId, start, end);
		}

		return mbCategoryPersistence.findByNotC_G_P_S(
			excludedCategoryId, groupId, parentCategoryId, status, start, end);
	}

	@Override
	public List<MBCategory> getCategories(
		long groupId, long[] parentCategoryIds, int start, int end) {

		return mbCategoryPersistence.findByG_P(
			groupId, parentCategoryIds, start, end);
	}

	@Override
	public List<MBCategory> getCategories(
		long groupId, long[] parentCategoryIds, int status, int start,
		int end) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return mbCategoryPersistence.findByG_P(
				groupId, parentCategoryIds, start, end);
		}

		return mbCategoryPersistence.findByG_P_S(
			groupId, parentCategoryIds, status, start, end);
	}

	@Override
	public List<MBCategory> getCategories(
		long groupId, long[] excludedCategoryIds, long[] parentCategoryIds,
		int status, int start, int end) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return mbCategoryPersistence.findByNotC_G_P(
				excludedCategoryIds, groupId, parentCategoryIds, start, end);
		}

		return mbCategoryPersistence.findByNotC_G_P_S(
			excludedCategoryIds, groupId, parentCategoryIds, status, start,
			end);
	}

	@Override
	public List<Object> getCategoriesAndThreads(long groupId, long categoryId) {
		QueryDefinition<?> queryDefinition = new QueryDefinition<>(
			WorkflowConstants.STATUS_ANY);

		return mbCategoryFinder.findC_T_ByG_C(
			groupId, categoryId, queryDefinition);
	}

	@Override
	public List<Object> getCategoriesAndThreads(
		long groupId, long categoryId, int status) {

		QueryDefinition<?> queryDefinition = new QueryDefinition<>(status);

		return mbCategoryFinder.findC_T_ByG_C(
			groupId, categoryId, queryDefinition);
	}

	@Override
	public List<Object> getCategoriesAndThreads(
		long groupId, long categoryId, int status, int start, int end) {

		QueryDefinition<?> queryDefinition = new QueryDefinition<>(
			status, start, end, null);

		return mbCategoryFinder.findC_T_ByG_C(
			groupId, categoryId, queryDefinition);
	}

	@Override
	public int getCategoriesAndThreadsCount(long groupId, long categoryId) {
		QueryDefinition<?> queryDefinition = new QueryDefinition<>(
			WorkflowConstants.STATUS_ANY);

		return mbCategoryFinder.countC_T_ByG_C(
			groupId, categoryId, queryDefinition);
	}

	@Override
	public int getCategoriesAndThreadsCount(
		long groupId, long categoryId, int status) {

		QueryDefinition<?> queryDefinition = new QueryDefinition<>(status);

		return mbCategoryFinder.countC_T_ByG_C(
			groupId, categoryId, queryDefinition);
	}

	@Override
	public int getCategoriesCount(long groupId) {
		return mbCategoryPersistence.countByGroupId(groupId);
	}

	@Override
	public int getCategoriesCount(long groupId, int status) {
		return mbCategoryPersistence.countByG_S(groupId, status);
	}

	@Override
	public int getCategoriesCount(long groupId, long parentCategoryId) {
		return mbCategoryPersistence.countByG_P(groupId, parentCategoryId);
	}

	@Override
	public int getCategoriesCount(
		long groupId, long parentCategoryId, int status) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return mbCategoryPersistence.countByG_P(groupId, parentCategoryId);
		}

		return mbCategoryPersistence.countByG_P_S(
			groupId, parentCategoryId, status);
	}

	@Override
	public int getCategoriesCount(
		long groupId, long excludedCategoryId, long parentCategoryId,
		int status) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return mbCategoryPersistence.countByNotC_G_P(
				excludedCategoryId, groupId, parentCategoryId);
		}

		return mbCategoryPersistence.countByNotC_G_P_S(
			excludedCategoryId, groupId, parentCategoryId, status);
	}

	@Override
	public int getCategoriesCount(long groupId, long[] parentCategoryIds) {
		return mbCategoryPersistence.countByG_P(groupId, parentCategoryIds);
	}

	@Override
	public int getCategoriesCount(
		long groupId, long[] parentCategoryIds, int status) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return mbCategoryPersistence.countByG_P(groupId, parentCategoryIds);
		}

		return mbCategoryPersistence.countByG_P_S(
			groupId, parentCategoryIds, status);
	}

	@Override
	public int getCategoriesCount(
		long groupId, long[] excludedCategoryIds, long[] parentCategoryIds,
		int status) {

		if (status == WorkflowConstants.STATUS_ANY) {
			return mbCategoryPersistence.countByNotC_G_P(
				excludedCategoryIds, groupId, parentCategoryIds);
		}

		return mbCategoryPersistence.countByNotC_G_P_S(
			excludedCategoryIds, groupId, parentCategoryIds, status);
	}

	@Override
	public MBCategory getCategory(long categoryId) throws PortalException {
		MBCategory category = null;

		if ((categoryId != MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) &&
			(categoryId != MBCategoryConstants.DISCUSSION_CATEGORY_ID)) {

			category = mbCategoryPersistence.findByPrimaryKey(categoryId);
		}
		else {
			category = new MBCategoryImpl();

			category.setCategoryId(categoryId);
			category.setParentCategoryId(categoryId);
		}

		return category;
	}

	@Override
	public List<MBCategory> getCompanyCategories(
		long companyId, int start, int end) {

		return mbCategoryPersistence.findByCompanyId(companyId, start, end);
	}

	@Override
	public int getCompanyCategoriesCount(long companyId) {
		return mbCategoryPersistence.countByCompanyId(companyId);
	}

	@Override
	public MBCategory getMBCategory(long groupId, String friendlyURL)
		throws PortalException {

		return mbCategoryPersistence.findByG_F(
			groupId,
			_friendlyURLNormalizer.normalizeWithEncodingPeriodsAndSlashes(
				friendlyURL));
	}

	@Override
	public List<Long> getSubcategoryIds(
		List<Long> categoryIds, long groupId, long categoryId) {

		List<MBCategory> categories = mbCategoryPersistence.findByG_P(
			groupId, categoryId);

		for (MBCategory category : categories) {
			categoryIds.add(category.getCategoryId());

			getSubcategoryIds(
				categoryIds, category.getGroupId(), category.getCategoryId());
		}

		return categoryIds;
	}

	@Override
	public List<MBCategory> getSubscribedCategories(
		long groupId, long userId, int start, int end) {

		QueryDefinition<MBCategory> queryDefinition = new QueryDefinition<>(
			WorkflowConstants.STATUS_ANY, start, end, null);

		return mbCategoryFinder.findC_ByS_G_U_P(
			groupId, userId, null, queryDefinition);
	}

	@Override
	public int getSubscribedCategoriesCount(long groupId, long userId) {
		QueryDefinition<MBCategory> queryDefinition = new QueryDefinition<>(
			WorkflowConstants.STATUS_ANY);

		return mbCategoryFinder.countC_ByS_G_U_P(
			groupId, userId, null, queryDefinition);
	}

	@Override
	public void moveCategoriesToTrash(long groupId, long userId)
		throws PortalException {

		List<MBCategory> categories = mbCategoryPersistence.findByGroupId(
			groupId);

		for (MBCategory category : categories) {
			moveCategoryToTrash(userId, category.getCategoryId());
		}
	}

	@Override
	public MBCategory moveCategory(
			long categoryId, long parentCategoryId,
			boolean mergeWithParentCategory)
		throws PortalException {

		MBCategory category = mbCategoryPersistence.findByPrimaryKey(
			categoryId);

		parentCategoryId = _getParentCategoryId(category, parentCategoryId);

		if (mergeWithParentCategory && (categoryId != parentCategoryId) &&
			(parentCategoryId !=
				MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) &&
			(parentCategoryId != MBCategoryConstants.DISCUSSION_CATEGORY_ID)) {

			_mergeCategories(category, parentCategoryId);

			return category;
		}

		category.setParentCategoryId(parentCategoryId);

		category = mbCategoryPersistence.update(category);

		_reindex(MBCategory.class, category);

		return category;
	}

	@Override
	public MBCategory moveCategoryFromTrash(
			long userId, long categoryId, long newCategoryId)
		throws PortalException {

		MBCategory category = mbCategoryPersistence.findByPrimaryKey(
			categoryId);

		if (!category.isInTrash()) {
			throw new RestoreEntryException(
				RestoreEntryException.INVALID_STATUS);
		}

		if (_trashHelper.isInTrashExplicitly(category)) {
			restoreCategoryFromTrash(userId, categoryId);
		}
		else {

			// Category

			TrashVersion trashVersion = _trashVersionLocalService.fetchVersion(
				MBCategory.class.getName(), category.getCategoryId());

			int status = WorkflowConstants.STATUS_APPROVED;

			if (trashVersion != null) {
				status = trashVersion.getStatus();
			}

			updateStatus(userId, categoryId, status);

			// Trash

			if (trashVersion != null) {
				_trashVersionLocalService.deleteTrashVersion(trashVersion);
			}

			// Categories and threads

			_restoreDependentsFromTrash(
				_userLocalService.getUser(userId),
				getCategoriesAndThreads(
					category.getGroupId(), categoryId,
					WorkflowConstants.STATUS_IN_TRASH));
		}

		return moveCategory(categoryId, newCategoryId, false);
	}

	@Override
	public MBCategory moveCategoryToTrash(long userId, long categoryId)
		throws PortalException {

		// Category

		MBCategory category = mbCategoryPersistence.findByPrimaryKey(
			categoryId);

		if (category.isInTrash()) {
			throw new TrashEntryException();
		}

		category = updateStatus(
			userId, categoryId, WorkflowConstants.STATUS_IN_TRASH);

		// Trash

		TrashEntry trashEntry = _trashEntryLocalService.addTrashEntry(
			userId, category.getGroupId(), MBCategory.class.getName(),
			categoryId, category.getUuid(), null,
			WorkflowConstants.STATUS_APPROVED, null, null);

		// Categories and threads

		_moveDependentsToTrash(
			_userLocalService.getUser(userId),
			getCategoriesAndThreads(category.getGroupId(), categoryId),
			trashEntry.getEntryId());

		_reindex(MBCategory.class, category);

		return category;
	}

	@Override
	public void restoreCategoryFromTrash(long userId, long categoryId)
		throws PortalException {

		// Category

		MBCategory category = mbCategoryPersistence.findByPrimaryKey(
			categoryId);

		if (!category.isInTrash()) {
			throw new RestoreEntryException(
				RestoreEntryException.INVALID_STATUS);
		}

		TrashEntry trashEntry = _trashEntryLocalService.getEntry(
			MBCategory.class.getName(), categoryId);

		category = updateStatus(
			userId, categoryId, WorkflowConstants.STATUS_APPROVED);

		// Categories and threads

		_restoreDependentsFromTrash(
			_userLocalService.getUser(userId),
			getCategoriesAndThreads(
				category.getGroupId(), categoryId,
				WorkflowConstants.STATUS_IN_TRASH));

		// Trash

		_trashEntryLocalService.deleteEntry(trashEntry.getEntryId());
	}

	@Override
	public void subscribeCategory(long userId, long groupId, long categoryId)
		throws PortalException {

		if (categoryId == MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) {
			categoryId = groupId;
		}

		_subscriptionLocalService.addSubscription(
			userId, groupId, MBCategory.class.getName(), categoryId);
	}

	@Override
	public void unsubscribeCategory(long userId, long groupId, long categoryId)
		throws PortalException {

		if (categoryId == MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) {
			categoryId = groupId;
		}

		_subscriptionLocalService.deleteSubscription(
			userId, MBCategory.class.getName(), categoryId);
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
			ServiceContext serviceContext)
		throws PortalException {

		// Merge categories

		if ((categoryId == MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) ||
			(categoryId == MBCategoryConstants.DISCUSSION_CATEGORY_ID)) {

			return null;
		}

		MBCategory category = mbCategoryPersistence.findByPrimaryKey(
			categoryId);

		parentCategoryId = _getParentCategoryId(category, parentCategoryId);

		if (mergeWithParentCategory && (categoryId != parentCategoryId) &&
			(parentCategoryId !=
				MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) &&
			(parentCategoryId != MBCategoryConstants.DISCUSSION_CATEGORY_ID)) {

			_mergeCategories(category, parentCategoryId);

			return category;
		}

		// Category

		_validate(name);

		category.setParentCategoryId(parentCategoryId);
		category.setName(name);
		category.setDescription(description);

		if (!displayStyle.equals(category.getDisplayStyle())) {
			category.setDisplayStyle(displayStyle);

			_updateChildCategoriesDisplayStyle(category, displayStyle);
		}

		category.setExpandoBridgeAttributes(serviceContext);

		category = mbCategoryPersistence.update(category);

		_reindex(MBCategory.class, category);

		// Mailing list

		MBMailingList mailingList = _mbMailingListPersistence.fetchByG_C(
			category.getGroupId(), category.getCategoryId());

		if (mailingList != null) {
			_mbMailingListLocalService.updateMailingList(
				mailingList.getMailingListId(), emailAddress, inProtocol,
				inServerName, inServerPort, inUseSSL, inUserName, inPassword,
				inReadInterval, outEmailAddress, outCustom, outServerName,
				outServerPort, outUseSSL, outUserName, outPassword,
				allowAnonymous, mailingListActive, serviceContext);
		}
		else {
			_mbMailingListLocalService.addMailingList(
				category.getUserId(), category.getGroupId(),
				category.getCategoryId(), emailAddress, inProtocol,
				inServerName, inServerPort, inUseSSL, inUserName, inPassword,
				inReadInterval, outEmailAddress, outCustom, outServerName,
				outServerPort, outUseSSL, outUserName, outPassword,
				allowAnonymous, mailingListActive, serviceContext);
		}

		return category;
	}

	@Override
	public MBCategory updateStatus(long userId, long categoryId, int status)
		throws PortalException {

		// Category

		User user = _userLocalService.getUser(userId);

		MBCategory category = mbCategoryPersistence.findByPrimaryKey(
			categoryId);

		category.setStatus(status);
		category.setStatusByUserId(user.getUserId());
		category.setStatusByUserName(user.getFullName());
		category.setStatusDate(new Date());

		return mbCategoryPersistence.update(category);
	}

	private long _getParentCategoryId(long groupId, long parentCategoryId) {
		if ((parentCategoryId !=
				MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) &&
			(parentCategoryId != MBCategoryConstants.DISCUSSION_CATEGORY_ID)) {

			MBCategory parentCategory = mbCategoryPersistence.fetchByPrimaryKey(
				parentCategoryId);

			if ((parentCategory == null) ||
				(groupId != parentCategory.getGroupId())) {

				parentCategoryId =
					MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID;
			}
		}

		return parentCategoryId;
	}

	private long _getParentCategoryId(
		MBCategory category, long parentCategoryId) {

		if ((parentCategoryId ==
				MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) ||
			(parentCategoryId == MBCategoryConstants.DISCUSSION_CATEGORY_ID)) {

			return parentCategoryId;
		}

		if (category.getCategoryId() == parentCategoryId) {
			return category.getParentCategoryId();
		}

		MBCategory parentCategory = mbCategoryPersistence.fetchByPrimaryKey(
			parentCategoryId);

		if ((parentCategory == null) ||
			(category.getGroupId() != parentCategory.getGroupId())) {

			return category.getParentCategoryId();
		}

		List<Long> subcategoryIds = new ArrayList<>();

		getSubcategoryIds(
			subcategoryIds, category.getGroupId(), category.getCategoryId());

		if (subcategoryIds.contains(parentCategoryId)) {
			return category.getParentCategoryId();
		}

		return parentCategoryId;
	}

	private String _getUniqueFriendlyURL(
		long groupId, long categoryId, String name) {

		if (Validator.isNull(name)) {
			return String.valueOf(categoryId);
		}

		name = StringUtil.toLowerCase(name.trim());

		if (Validator.isNull(name) || Validator.isNumber(name)) {
			name = String.valueOf(categoryId);
		}
		else {
			name =
				_friendlyURLNormalizer.normalizeWithEncodingPeriodsAndSlashes(
					name);
		}

		name = ModelHintsUtil.trimString(
			MBCategory.class.getName(), "friendlyURL", name);

		String friendlyURL = name;

		MBCategory mbCategory = mbCategoryPersistence.fetchByG_F(
			groupId, friendlyURL);

		if (mbCategory == null) {
			return friendlyURL;
		}

		if (!StringUtil.endsWith(friendlyURL, StringPool.DASH)) {
			name = name + StringPool.DASH;
		}

		for (int i = 1; mbCategory != null; i++) {
			friendlyURL = name + i;

			mbCategory = mbCategoryPersistence.fetchByG_F(groupId, friendlyURL);
		}

		return friendlyURL;
	}

	private void _mergeCategories(MBCategory fromCategory, long toCategoryId)
		throws PortalException {

		if ((toCategoryId == MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) ||
			(toCategoryId == MBCategoryConstants.DISCUSSION_CATEGORY_ID)) {

			return;
		}

		List<MBCategory> categories = mbCategoryPersistence.findByG_P(
			fromCategory.getGroupId(), fromCategory.getCategoryId());

		for (MBCategory category : categories) {
			_mergeCategories(category, toCategoryId);
		}

		List<MBThread> threads = _mbThreadPersistence.findByG_C(
			fromCategory.getGroupId(), fromCategory.getCategoryId());

		for (MBThread thread : threads) {

			// Thread

			thread.setCategoryId(toCategoryId);

			thread = _mbThreadPersistence.update(thread);

			List<MBMessage> messages = _mbMessagePersistence.findByThreadId(
				thread.getThreadId());

			for (MBMessage message : messages) {

				// Message

				message.setCategoryId(toCategoryId);

				message = _mbMessagePersistence.update(message);

				_reindex(MBMessage.class, message);
			}
		}

		mbCategoryLocalService.deleteCategory(fromCategory);
	}

	private void _moveDependentsToTrash(
			User user, List<Object> categoriesAndThreads, long trashEntryId)
		throws PortalException {

		for (Object object : categoriesAndThreads) {
			if (object instanceof MBThread) {

				// Thread

				MBThread thread = (MBThread)object;

				int oldStatus = thread.getStatus();

				if (oldStatus == WorkflowConstants.STATUS_IN_TRASH) {
					continue;
				}

				thread.setStatus(WorkflowConstants.STATUS_IN_TRASH);

				thread = _mbThreadPersistence.update(thread);

				// Trash

				if (oldStatus != WorkflowConstants.STATUS_APPROVED) {
					_trashVersionLocalService.addTrashVersion(
						trashEntryId, MBThread.class.getName(),
						thread.getThreadId(), oldStatus, null);
				}

				// Threads

				_mbThreadLocalService.moveDependentsToTrash(
					thread.getGroupId(), thread.getThreadId(), trashEntryId);

				_reindex(MBThread.class, thread);
			}
			else if (object instanceof MBCategory) {

				// Category

				MBCategory category = (MBCategory)object;

				if (category.isInTrash()) {
					continue;
				}

				int oldStatus = category.getStatus();

				category.setStatus(WorkflowConstants.STATUS_IN_TRASH);

				category = mbCategoryPersistence.update(category);

				// Trash

				if (oldStatus != WorkflowConstants.STATUS_APPROVED) {
					_trashVersionLocalService.addTrashVersion(
						trashEntryId, MBCategory.class.getName(),
						category.getCategoryId(), oldStatus, null);
				}

				// Categories and threads

				_moveDependentsToTrash(
					user,
					getCategoriesAndThreads(
						category.getGroupId(), category.getCategoryId()),
					trashEntryId);

				_reindex(MBCategory.class, category);
			}
		}
	}

	private <T> void _reindex(Class<T> clazz, T model) throws PortalException {
		Indexer<T> indexer = IndexerRegistryUtil.nullSafeGetIndexer(clazz);

		indexer.reindex(model);
	}

	private void _restoreDependentsFromTrash(
			User user, List<Object> categoriesAndThreads)
		throws PortalException {

		for (Object object : categoriesAndThreads) {
			if (object instanceof MBThread) {

				// Thread

				MBThread thread = (MBThread)object;

				if (!_trashHelper.isInTrashImplicitly(thread)) {
					continue;
				}

				TrashVersion trashVersion =
					_trashVersionLocalService.fetchVersion(
						MBThread.class.getName(), thread.getThreadId());

				int oldStatus = WorkflowConstants.STATUS_APPROVED;

				if (trashVersion != null) {
					oldStatus = trashVersion.getStatus();
				}

				thread.setStatus(oldStatus);

				thread = _mbThreadPersistence.update(thread);

				// Threads

				_mbThreadLocalService.restoreDependentsFromTrash(
					thread.getGroupId(), thread.getThreadId());

				// Trash

				if (trashVersion != null) {
					_trashVersionLocalService.deleteTrashVersion(trashVersion);
				}

				_reindex(MBThread.class, thread);
			}
			else if (object instanceof MBCategory) {

				// Category

				MBCategory category = (MBCategory)object;

				if (!_trashHelper.isInTrashImplicitly(category)) {
					continue;
				}

				TrashVersion trashVersion =
					_trashVersionLocalService.fetchVersion(
						MBCategory.class.getName(), category.getCategoryId());

				int oldStatus = WorkflowConstants.STATUS_APPROVED;

				if (trashVersion != null) {
					oldStatus = trashVersion.getStatus();
				}

				category.setStatus(oldStatus);

				category = mbCategoryPersistence.update(category);

				// Categories and threads

				_restoreDependentsFromTrash(
					user,
					getCategoriesAndThreads(
						category.getGroupId(), category.getCategoryId(),
						WorkflowConstants.STATUS_IN_TRASH));

				// Trash

				if (trashVersion != null) {
					_trashVersionLocalService.deleteTrashVersion(trashVersion);
				}
			}
		}
	}

	private void _updateChildCategoriesDisplayStyle(
			MBCategory category, String displayStyle)
		throws PortalException {

		List<MBCategory> categories = getCategories(
			category.getGroupId(), category.getCategoryId(), QueryUtil.ALL_POS,
			QueryUtil.ALL_POS);

		for (MBCategory curCategory : categories) {
			_updateChildCategoriesDisplayStyle(curCategory, displayStyle);

			curCategory.setDisplayStyle(displayStyle);

			mbCategoryPersistence.update(curCategory);
		}
	}

	private void _validate(String name) throws PortalException {
		if (Validator.isNull(name)) {
			throw new CategoryNameException("Name is null");
		}
	}

	@Reference
	private ExpandoRowLocalService _expandoRowLocalService;

	@Reference
	private FriendlyURLNormalizer _friendlyURLNormalizer;

	@Reference
	private MBMailingListLocalService _mbMailingListLocalService;

	@Reference
	private MBMailingListPersistence _mbMailingListPersistence;

	@Reference
	private MBMessagePersistence _mbMessagePersistence;

	@Reference
	private MBThreadLocalService _mbThreadLocalService;

	@Reference
	private MBThreadPersistence _mbThreadPersistence;

	@Reference
	private RatingsStatsLocalService _ratingsStatsLocalService;

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private SubscriptionLocalService _subscriptionLocalService;

	@Reference
	private TrashEntryLocalService _trashEntryLocalService;

	@Reference
	private TrashHelper _trashHelper;

	@Reference
	private TrashVersionLocalService _trashVersionLocalService;

	@Reference
	private UserLocalService _userLocalService;

}