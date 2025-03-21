/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.display.context;

import com.liferay.knowledge.base.constants.KBFolderConstants;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.service.KBArticleLocalServiceUtil;
import com.liferay.knowledge.base.service.KBArticleServiceUtil;
import com.liferay.knowledge.base.service.KBFolderServiceUtil;
import com.liferay.knowledge.base.util.KnowledgeBaseUtil;
import com.liferay.knowledge.base.util.comparator.KBArticlePriorityComparator;
import com.liferay.knowledge.base.web.internal.KBUtil;
import com.liferay.knowledge.base.web.internal.configuration.KBDisplayPortletInstanceConfiguration;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletRequest;

import java.util.Collections;
import java.util.List;

/**
 * @author Adolfo Pérez
 */
public class KBNavigationDisplayContext {

	public KBNavigationDisplayContext(
		PortletRequest portletRequest, PortalPreferences portalPreferences,
		KBDisplayPortletInstanceConfiguration
			kbDisplayPortletInstanceConfiguration,
		KBArticle kbArticle) {

		_portletRequest = portletRequest;
		_portalPreferences = portalPreferences;
		_kbDisplayPortletInstanceConfiguration =
			kbDisplayPortletInstanceConfiguration;
		_kbArticle = kbArticle;
	}

	public List<Long> getAncestorResourcePrimaryKeys() throws PortalException {
		if (_kbArticle == null) {
			return Collections.singletonList(
				KBFolderConstants.DEFAULT_PARENT_FOLDER_ID);
		}

		KBArticle latestKBArticle =
			KBArticleLocalServiceUtil.getLatestKBArticle(
				_kbArticle.getResourcePrimKey(),
				WorkflowConstants.STATUS_APPROVED);

		List<Long> ancestorResourcePrimaryKeys =
			latestKBArticle.getAncestorResourcePrimaryKeys();

		Collections.reverse(ancestorResourcePrimaryKeys);

		return ancestorResourcePrimaryKeys;
	}

	public List<KBArticle> getChildKBArticles(
			long groupId, long parentResourcePrimKey, int level)
		throws PortalException {

		if ((parentResourcePrimKey == getResourcePrimKey()) && (level == 0) &&
			!isFolderResource()) {

			return Collections.singletonList(
				KBArticleServiceUtil.getLatestKBArticle(
					getResourcePrimKey(), WorkflowConstants.STATUS_APPROVED));
		}

		if (isMaxNestingLevelReached(level)) {
			return KBArticleServiceUtil.getAllDescendantKBArticles(
				groupId, parentResourcePrimKey,
				WorkflowConstants.STATUS_APPROVED,
				KBArticlePriorityComparator.getInstance(true));
		}

		return KBArticleServiceUtil.getKBArticles(
			groupId, parentResourcePrimKey, WorkflowConstants.STATUS_APPROVED,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			KBArticlePriorityComparator.getInstance(true));
	}

	public String getCurrentKBFolderURLTitle() throws PortalException {
		long rootResourcePrimKey = getRootResourcePrimKey();

		if (rootResourcePrimKey == KBFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			return StringPool.BLANK;
		}

		KBFolder kbFolder = KBFolderServiceUtil.getKBFolder(
			rootResourcePrimKey);

		return kbFolder.getUrlTitle();
	}

	public String getLabel() {
		if (KBFolderConstants.DEFAULT_PARENT_FOLDER_ID !=
				getResourcePrimKey()) {

			return "the-selected-knowledge-base-is-empty";
		}

		return "please-configure-this-portlet-to-make-it-visible-to-all-users";
	}

	public String getPageTitle() throws PortalException {
		long rootResourcePrimKey = getRootResourcePrimKey();

		if (isFolderResource() &&
			(rootResourcePrimKey !=
				KBFolderConstants.DEFAULT_PARENT_FOLDER_ID)) {

			KBFolder kbFolder = KBFolderServiceUtil.getKBFolder(
				rootResourcePrimKey);

			String pageTitle =
				_kbDisplayPortletInstanceConfiguration.contentRootPrefix() +
					" " + kbFolder.getName();

			if (_kbArticle == null) {
				return pageTitle;
			}

			return _kbArticle.getTitle() + " - " + pageTitle;
		}

		if (_kbArticle != null) {
			return _kbArticle.getTitle();
		}

		return null;
	}

	public long getParentResourcePrimKey() throws PortalException {
		if (_kbArticle != null) {
			return _kbArticle.getParentResourcePrimKey();
		}

		return getRootResourcePrimKey();
	}

	public long getRootResourcePrimKey() throws PortalException {
		if (_rootResourcePrimKey != null) {
			return _rootResourcePrimKey;
		}

		if (!isFolderResource()) {
			_rootResourcePrimKey = getResourcePrimKey();
		}
		else if (_kbArticle != null) {
			_rootResourcePrimKey = KnowledgeBaseUtil.getKBFolderId(
				_kbArticle.getParentResourceClassNameId(),
				_kbArticle.getParentResourcePrimKey());
		}
		else {
			_rootResourcePrimKey = KBUtil.getRootResourcePrimKey(
				_portletRequest, PortalUtil.getScopeGroupId(_portletRequest),
				_getResourceClassNameId(), getResourcePrimKey());
		}

		return _rootResourcePrimKey;
	}

	public boolean isChildKBArticleExpanded(KBArticle childKBArticle, int level)
		throws PortalException {

		List<Long> ancestorResourcePrimaryKeys =
			getAncestorResourcePrimaryKeys();

		if ((ancestorResourcePrimaryKeys.size() > 1) &&
			(level < ancestorResourcePrimaryKeys.size()) &&
			(childKBArticle.getResourcePrimKey() ==
				ancestorResourcePrimaryKeys.get(level))) {

			return true;
		}

		return false;
	}

	public boolean isFolderResource() {
		long kbFolderClassNameId = PortalUtil.getClassNameId(
			KBFolderConstants.getClassName());

		if (kbFolderClassNameId == _getResourceClassNameId()) {
			return true;
		}

		return false;
	}

	public boolean isFurtherExpansionRequired(
			long parentResourcePrimKey, KBArticle childKBArticle, int level)
		throws PortalException {

		List<Long> ancestorResourcePrimaryKeys =
			getAncestorResourcePrimaryKeys();

		if (!isMaxNestingLevelReached(level) &&
			ancestorResourcePrimaryKeys.contains(
				childKBArticle.getResourcePrimKey())) {

			return true;
		}

		return false;
	}

	public boolean isLeftNavigationVisible() throws PortalException {
		if (_leftNavigationVisible == null) {
			_leftNavigationVisible = isFolderResource();
		}

		return _leftNavigationVisible;
	}

	public boolean isMaxNestingLevelReached(int level) {
		int maxNestingLevel =
			_kbDisplayPortletInstanceConfiguration.maxNestingLevel();

		if ((maxNestingLevel - level) <= 1) {
			return true;
		}

		return false;
	}

	protected long getResourcePrimKey() {
		if (_resourcePrimKey == null) {
			_resourcePrimKey = GetterUtil.getLong(
				_kbDisplayPortletInstanceConfiguration.resourcePrimKey());
		}

		return _resourcePrimKey;
	}

	private long _getResourceClassNameId() {
		if (_resourceClassNameId != null) {
			return _resourceClassNameId;
		}

		if (_kbDisplayPortletInstanceConfiguration.resourceClassNameId() != 0) {
			_resourceClassNameId =
				_kbDisplayPortletInstanceConfiguration.resourceClassNameId();
		}
		else {
			_resourceClassNameId = PortalUtil.getClassNameId(
				KBFolderConstants.getClassName());
		}

		return _resourceClassNameId;
	}

	private final KBArticle _kbArticle;
	private final KBDisplayPortletInstanceConfiguration
		_kbDisplayPortletInstanceConfiguration;
	private Boolean _leftNavigationVisible;
	private final PortalPreferences _portalPreferences;
	private final PortletRequest _portletRequest;
	private Long _resourceClassNameId;
	private Long _resourcePrimKey;
	private Long _rootResourcePrimKey;

}