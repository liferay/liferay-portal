/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal;

import com.liferay.knowledge.base.constants.KBCommentConstants;
import com.liferay.knowledge.base.constants.KBFolderConstants;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBComment;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.model.KBTemplate;
import com.liferay.knowledge.base.service.KBFolderServiceUtil;
import com.liferay.knowledge.base.util.KnowledgeBaseUtil;
import com.liferay.knowledge.base.util.comparator.KBArticleCreateDateComparator;
import com.liferay.knowledge.base.util.comparator.KBArticleModifiedDateComparator;
import com.liferay.knowledge.base.util.comparator.KBArticlePriorityComparator;
import com.liferay.knowledge.base.util.comparator.KBArticleStatusComparator;
import com.liferay.knowledge.base.util.comparator.KBArticleTitleComparator;
import com.liferay.knowledge.base.util.comparator.KBArticleUserNameComparator;
import com.liferay.knowledge.base.util.comparator.KBArticleVersionComparator;
import com.liferay.knowledge.base.util.comparator.KBArticleViewCountComparator;
import com.liferay.knowledge.base.util.comparator.KBCommentCreateDateComparator;
import com.liferay.knowledge.base.util.comparator.KBCommentModifiedDateComparator;
import com.liferay.knowledge.base.util.comparator.KBCommentStatusComparator;
import com.liferay.knowledge.base.util.comparator.KBCommentUserNameComparator;
import com.liferay.knowledge.base.util.comparator.KBFolderNameComparator;
import com.liferay.knowledge.base.util.comparator.KBObjectsModifiedDateComparator;
import com.liferay.knowledge.base.util.comparator.KBObjectsPriorityComparator;
import com.liferay.knowledge.base.util.comparator.KBObjectsTitleComparator;
import com.liferay.knowledge.base.util.comparator.KBObjectsViewCountComparator;
import com.liferay.knowledge.base.util.comparator.KBTemplateCreateDateComparator;
import com.liferay.knowledge.base.util.comparator.KBTemplateModifiedDateComparator;
import com.liferay.knowledge.base.util.comparator.KBTemplateTitleComparator;
import com.liferay.knowledge.base.util.comparator.KBTemplateUserNameComparator;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.SortFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Roberto Díaz
 */
public class KBUtil {

	public static List<KBFolder> getAlternateRootKBFolders(
			long groupId, long kbFolderId)
		throws PortalException {

		List<KBFolder> kbFolders = KBFolderServiceUtil.getKBFolders(
			groupId, kbFolderId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		kbFolders = new ArrayList<>(kbFolders);

		Iterator<KBFolder> iterator = kbFolders.iterator();

		while (iterator.hasNext()) {
			KBFolder kbFolder = iterator.next();

			if (kbFolder.isEmpty()) {
				iterator.remove();
			}
		}

		return ListUtil.sort(
			kbFolders, KBFolderNameComparator.getInstance(false));
	}

	public static OrderByComparator<KBArticle> getKBArticleOrderByComparator(
		String orderByCol, String orderByType) {

		if (Validator.isNull(orderByCol) || Validator.isNull(orderByType)) {
			return null;
		}

		boolean ascending = false;

		if (orderByType.equals("asc")) {
			ascending = true;
		}

		if (orderByCol.equals("create-date")) {
			return KBArticleCreateDateComparator.getInstance(ascending);
		}
		else if (orderByCol.equals("modified-date")) {
			return KBArticleModifiedDateComparator.getInstance(ascending);
		}
		else if (orderByCol.equals("priority")) {
			return KBArticlePriorityComparator.getInstance(ascending);
		}
		else if (orderByCol.equals("status")) {
			return KBArticleStatusComparator.getInstance(ascending);
		}
		else if (orderByCol.equals("title")) {
			return KBArticleTitleComparator.getInstance(ascending);
		}
		else if (orderByCol.equals("user-name")) {
			return KBArticleUserNameComparator.getInstance(ascending);
		}
		else if (orderByCol.equals("version")) {
			return KBArticleVersionComparator.getInstance(ascending);
		}
		else if (orderByCol.equals("view-count")) {
			return KBArticleViewCountComparator.getInstance(ascending);
		}

		return null;
	}

	public static Sort[] getKBArticleSorts(
		String orderByCol, String orderByType) {

		if (Validator.isNull(orderByCol) || Validator.isNull(orderByType)) {
			return SortFactoryUtil.getDefaultSorts();
		}

		boolean reverse = true;

		if (orderByType.equals("asc")) {
			reverse = false;
		}

		if (orderByCol.equals("create-date")) {
			String fieldName = Field.CREATE_DATE;

			return new Sort[] {
				SortFactoryUtil.create(fieldName, Sort.LONG_TYPE, reverse),
				SortFactoryUtil.create(null, Sort.SCORE_TYPE, false)
			};
		}
		else if (orderByCol.equals("modified-date")) {
			String fieldName = Field.MODIFIED_DATE;

			return new Sort[] {
				SortFactoryUtil.create(fieldName, Sort.LONG_TYPE, reverse),
				SortFactoryUtil.create(null, Sort.SCORE_TYPE, false)
			};
		}
		else if (orderByCol.equals("score")) {
			String fieldName = null;

			return new Sort[] {
				SortFactoryUtil.create(fieldName, Sort.SCORE_TYPE, !reverse),
				SortFactoryUtil.create(
					Field.MODIFIED_DATE, Sort.LONG_TYPE, true)
			};
		}
		else if (orderByCol.equals("title")) {
			String fieldName = "titleKeyword";

			return new Sort[] {
				SortFactoryUtil.create(fieldName, Sort.STRING_TYPE, reverse),
				SortFactoryUtil.create(null, Sort.SCORE_TYPE, false)
			};
		}
		else if (orderByCol.equals("user-name")) {
			String fieldName = Field.USER_NAME;

			return new Sort[] {
				SortFactoryUtil.create(fieldName, Sort.STRING_TYPE, reverse),
				SortFactoryUtil.create(null, Sort.SCORE_TYPE, false)
			};
		}

		return SortFactoryUtil.getDefaultSorts();
	}

	public static OrderByComparator<KBComment> getKBCommentOrderByComparator(
		String orderByCol, String orderByType) {

		if (Validator.isNull(orderByCol) || Validator.isNull(orderByType)) {
			return KBCommentStatusComparator.getInstance(false);
		}

		boolean ascending = false;

		if (orderByType.equals("asc")) {
			ascending = true;
		}

		if (orderByCol.equals("create-date")) {
			return KBCommentCreateDateComparator.getInstance(ascending);
		}
		else if (orderByCol.equals("modified-date")) {
			return KBCommentModifiedDateComparator.getInstance(ascending);
		}
		else if (orderByCol.equals("status")) {
			return KBCommentStatusComparator.getInstance(ascending);
		}
		else if (orderByCol.equals("user-name")) {
			return KBCommentUserNameComparator.getInstance(ascending);
		}

		return null;
	}

	public static OrderByComparator<Object> getKBObjectsOrderByComparator(
		String orderByCol, String orderByType) {

		if (Validator.isNull(orderByCol) || Validator.isNull(orderByType)) {
			return null;
		}

		boolean ascending = false;

		if (orderByType.equals("asc")) {
			ascending = true;
		}

		if (orderByCol.equals("modified-date")) {
			return new KBObjectsModifiedDateComparator<>(ascending, true);
		}
		else if (orderByCol.equals("priority")) {
			return KBObjectsPriorityComparator.getInstance(ascending);
		}
		else if (orderByCol.equals("title")) {
			return new KBObjectsTitleComparator<>(ascending, true);
		}
		else if (orderByCol.equals("view-count")) {
			return KBObjectsViewCountComparator.getInstance(ascending);
		}

		return null;
	}

	public static OrderByComparator<KBTemplate> getKBTemplateOrderByComparator(
		String orderByCol, String orderByType) {

		if (Validator.isNull(orderByCol) || Validator.isNull(orderByType)) {
			return null;
		}

		boolean ascending = false;

		if (orderByType.equals("asc")) {
			ascending = true;
		}

		if (orderByCol.equals("create-date")) {
			return KBTemplateCreateDateComparator.getInstance(ascending);
		}
		else if (orderByCol.equals("modified-date")) {
			return KBTemplateModifiedDateComparator.getInstance(ascending);
		}
		else if (orderByCol.equals("title")) {
			return KBTemplateTitleComparator.getInstance(ascending);
		}
		else if (orderByCol.equals("user-name")) {
			return KBTemplateUserNameComparator.getInstance(ascending);
		}

		return null;
	}

	public static int getNextStatus(int status) {
		if (status == KBCommentConstants.STATUS_IN_PROGRESS) {
			return KBCommentConstants.STATUS_COMPLETED;
		}
		else if (status == KBCommentConstants.STATUS_NEW) {
			return KBCommentConstants.STATUS_IN_PROGRESS;
		}

		return KBCommentConstants.STATUS_NONE;
	}

	public static String getPreferredKBFolderURLTitle(
			PortalPreferences portalPreferences, String contentRootPrefix)
		throws JSONException {

		String preferredKBFolderURLTitle = portalPreferences.getValue(
			KBPortletKeys.KNOWLEDGE_BASE_DISPLAY, "preferredKBFolderURLTitle",
			"{}");

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			preferredKBFolderURLTitle);

		return jsonObject.getString(contentRootPrefix, StringPool.BLANK);
	}

	public static int getPreviousStatus(int status) {
		if (status == KBCommentConstants.STATUS_COMPLETED) {
			return KBCommentConstants.STATUS_IN_PROGRESS;
		}
		else if (status == KBCommentConstants.STATUS_IN_PROGRESS) {
			return KBCommentConstants.STATUS_NEW;
		}

		return KBCommentConstants.STATUS_NONE;
	}

	public static long getRootResourcePrimKey(
			PortletRequest portletRequest, long groupId,
			long resourceClassNameId, long resourcePrimKey)
		throws PortalException {

		long kbFolderClassNameId = PortalUtil.getClassNameId(
			KBFolderConstants.getClassName());

		if (resourceClassNameId == kbFolderClassNameId) {
			return _getCurrentRootKBFolder(
				portletRequest, groupId, resourcePrimKey);
		}

		return KnowledgeBaseUtil.getKBFolderId(
			resourceClassNameId, resourcePrimKey);
	}

	public static String getStatusLabel(int status) {
		if (status == KBCommentConstants.STATUS_COMPLETED) {
			return "resolved";
		}
		else if (status == KBCommentConstants.STATUS_IN_PROGRESS) {
			return "in-progress";
		}
		else if (status == KBCommentConstants.STATUS_NEW) {
			return "new";
		}

		throw new IllegalArgumentException(
			String.format("Invalid suggestion status %s", status));
	}

	public static String getStatusTransitionLabel(int status) {
		if (status == KBCommentConstants.STATUS_COMPLETED) {
			return "resolve";
		}
		else if (status == KBCommentConstants.STATUS_IN_PROGRESS) {
			return "move-to-in-progress";
		}
		else if (status == KBCommentConstants.STATUS_NEW) {
			return "move-to-new";
		}

		throw new IllegalArgumentException(
			String.format("Invalid suggestion status %s", status));
	}

	private static long _getCurrentRootKBFolder(
			PortletRequest portletRequest, long groupId, long kbFolderId)
		throws PortalException {

		PortalPreferences portalPreferences =
			PortletPreferencesFactoryUtil.getPortalPreferences(portletRequest);

		PortletPreferences portletPreferences = portletRequest.getPreferences();

		String contentRootPrefix = GetterUtil.getString(
			portletPreferences.getValue("contentRootPrefix", null));

		String kbFolderURLTitle = getPreferredKBFolderURLTitle(
			portalPreferences, contentRootPrefix);

		long childKbFolderId = KBFolderConstants.DEFAULT_PARENT_FOLDER_ID;

		if (kbFolderURLTitle == null) {
			List<KBFolder> kbFolders = getAlternateRootKBFolders(
				groupId, kbFolderId);

			if (!kbFolders.isEmpty()) {
				KBFolder kbFolder = kbFolders.get(0);

				childKbFolderId = kbFolder.getKbFolderId();
			}
		}
		else {
			KBFolder kbFolder = KBFolderServiceUtil.fetchKBFolderByUrlTitle(
				groupId, kbFolderId, kbFolderURLTitle);

			if (kbFolder != null) {
				childKbFolderId = kbFolder.getKbFolderId();
			}
		}

		return childKbFolderId;
	}

}