/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.info.item.provider;

import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.GroupKeyInfoItemIdentifier;
import com.liferay.info.item.GroupUrlTitleInfoItemIdentifier;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.provider.BaseInfoItemObjectProvider;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.journal.exception.NoSuchArticleException;
import com.liferay.journal.exception.NoSuchArticleResourceException;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleResource;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalArticleResourceLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 */
@Component(
	property = {
		"info.item.identifier=com.liferay.info.item.ClassPKInfoItemIdentifier",
		"info.item.identifier=com.liferay.info.item.ERCInfoItemIdentifier",
		"info.item.identifier=com.liferay.info.item.GroupKeyInfoItemIdentifier",
		"info.item.identifier=com.liferay.info.item.GroupUrlTitleInfoItemIdentifier",
		"item.class.name=com.liferay.journal.model.JournalArticle",
		"service.ranking:Integer=100"
	},
	service = InfoItemObjectProvider.class
)
public class JournalArticleInfoItemObjectProvider
	extends BaseInfoItemObjectProvider<JournalArticle> {

	@Override
	protected JournalArticle doGetInfoItem(
			long groupId, InfoItemIdentifier infoItemIdentifier)
		throws NoSuchInfoItemException {

		if (!(infoItemIdentifier instanceof ClassPKInfoItemIdentifier) &&
			!(infoItemIdentifier instanceof ERCInfoItemIdentifier) &&
			!(infoItemIdentifier instanceof GroupKeyInfoItemIdentifier) &&
			!(infoItemIdentifier instanceof GroupUrlTitleInfoItemIdentifier)) {

			throw new NoSuchInfoItemException(
				"Unsupported info item identifier " + infoItemIdentifier);
		}

		JournalArticle article = null;

		try {
			if (infoItemIdentifier instanceof ClassPKInfoItemIdentifier) {
				ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
					(ClassPKInfoItemIdentifier)infoItemIdentifier;

				article = _getArticle(
					classPKInfoItemIdentifier.getClassPK(),
					infoItemIdentifier.getVersion());
			}
			else if (infoItemIdentifier instanceof ERCInfoItemIdentifier) {
				ERCInfoItemIdentifier ercInfoItemIdentifier =
					(ERCInfoItemIdentifier)infoItemIdentifier;

				article = _getArticle(
					ercInfoItemIdentifier.getExternalReferenceCode(), groupId,
					infoItemIdentifier.getVersion());
			}
			else if (infoItemIdentifier instanceof GroupKeyInfoItemIdentifier) {
				GroupKeyInfoItemIdentifier groupKeyInfoItemIdentifier =
					(GroupKeyInfoItemIdentifier)infoItemIdentifier;

				article = _getArticle(
					groupKeyInfoItemIdentifier.getGroupId(),
					groupKeyInfoItemIdentifier.getKey(),
					infoItemIdentifier.getVersion());
			}
			else if (infoItemIdentifier instanceof
						GroupUrlTitleInfoItemIdentifier) {

				GroupUrlTitleInfoItemIdentifier
					groupURLTitleInfoItemIdentifier =
						(GroupUrlTitleInfoItemIdentifier)infoItemIdentifier;

				article = _getArticleByUrlTitle(
					groupURLTitleInfoItemIdentifier.getGroupId(),
					groupURLTitleInfoItemIdentifier.getUrlTitle(),
					infoItemIdentifier.getVersion());
			}

			if ((article != null) &&
				!Objects.equals(
					infoItemIdentifier.getVersion(),
					InfoItemIdentifier.VERSION_LATEST_APPROVED) &&
				_isSignedIn() && !_hasPermission(article)) {

				article = _getArticle(
					article.getResourcePrimKey(),
					InfoItemIdentifier.VERSION_LATEST_APPROVED);
			}
		}
		catch (NoSuchArticleException | NoSuchArticleResourceException
					exception) {

			throw new NoSuchInfoItemException(
				"Unable to get journal article with identifier " +
					infoItemIdentifier,
				exception);
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}

		if (article == null) {
			throw new NoSuchInfoItemException(
				"Unable to get journal article " + infoItemIdentifier);
		}

		if (article.isInTrash()) {
			return null;
		}

		return article;
	}

	private JournalArticle _getArticle(long classPK, String version)
		throws PortalException {

		if (Validator.isNull(version) ||
			Objects.equals(
				version, InfoItemIdentifier.VERSION_LATEST_APPROVED)) {

			return _journalArticleLocalService.fetchLatestArticle(classPK);
		}
		else if (Objects.equals(version, InfoItemIdentifier.VERSION_LATEST)) {
			JournalArticleResource articleResource =
				_journalArticleResourceLocalService.getArticleResource(classPK);

			return _journalArticleLocalService.fetchLatestArticle(
				articleResource.getGroupId(), articleResource.getArticleId(),
				WorkflowConstants.STATUS_ANY);
		}

		JournalArticleResource articleResource =
			_journalArticleResourceLocalService.getArticleResource(classPK);

		return _journalArticleLocalService.getArticle(
			articleResource.getGroupId(), articleResource.getArticleId(),
			GetterUtil.getDouble(version));
	}

	private JournalArticle _getArticle(
			long groupId, String articleId, String version)
		throws PortalException {

		if (Validator.isNull(version) ||
			Objects.equals(
				version, InfoItemIdentifier.VERSION_LATEST_APPROVED)) {

			return _journalArticleLocalService.fetchLatestArticle(
				groupId, articleId, WorkflowConstants.STATUS_APPROVED);
		}
		else if (Objects.equals(version, InfoItemIdentifier.VERSION_LATEST)) {
			return _journalArticleLocalService.fetchLatestArticle(
				groupId, articleId, WorkflowConstants.STATUS_ANY);
		}

		return _journalArticleLocalService.getArticle(
			groupId, articleId, GetterUtil.getDouble(version));
	}

	private JournalArticle _getArticle(
			String externalReferenceCode, long groupId, String version)
		throws PortalException {

		if (Validator.isNull(version) ||
			Objects.equals(
				version, InfoItemIdentifier.VERSION_LATEST_APPROVED)) {

			return _journalArticleLocalService.
				fetchLatestArticleByExternalReferenceCode(
					groupId, externalReferenceCode,
					WorkflowConstants.STATUS_APPROVED, true);
		}

		if (Objects.equals(version, InfoItemIdentifier.VERSION_LATEST)) {
			return _journalArticleLocalService.
				fetchLatestArticleByExternalReferenceCode(
					groupId, externalReferenceCode);
		}

		JournalArticle journalArticle =
			_journalArticleLocalService.
				fetchLatestArticleByExternalReferenceCode(
					groupId, externalReferenceCode);

		return _journalArticleLocalService.getArticle(
			journalArticle.getGroupId(), journalArticle.getArticleId(),
			GetterUtil.getDouble(version));
	}

	private JournalArticle _getArticleByUrlTitle(
			long groupId, String urlTitle, String version)
		throws PortalException {

		if (Validator.isNull(version) ||
			Objects.equals(
				version, InfoItemIdentifier.VERSION_LATEST_APPROVED)) {

			return _journalArticleLocalService.fetchLatestArticleByUrlTitle(
				groupId, urlTitle, WorkflowConstants.STATUS_APPROVED);
		}
		else if (Objects.equals(version, InfoItemIdentifier.VERSION_LATEST)) {
			return _journalArticleLocalService.fetchLatestArticleByUrlTitle(
				groupId, urlTitle, WorkflowConstants.STATUS_ANY);
		}

		JournalArticle journalArticle =
			_journalArticleLocalService.fetchLatestArticleByUrlTitle(
				groupId, urlTitle, WorkflowConstants.STATUS_ANY);

		return _journalArticleLocalService.getArticle(
			groupId, journalArticle.getArticleId(),
			GetterUtil.getDouble(version));
	}

	private boolean _hasPermission(JournalArticle article) {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker == null) {
			return false;
		}

		try {
			_journalArticleModelResourcePermission.check(
				permissionChecker, article, ActionKeys.VIEW);

			return true;
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return false;
	}

	private boolean _isSignedIn() {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker == null) {
			return false;
		}

		return permissionChecker.isSignedIn();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalArticleInfoItemObjectProvider.class);

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.journal.model.JournalArticle)"
	)
	private ModelResourcePermission<JournalArticle>
		_journalArticleModelResourcePermission;

	@Reference
	private JournalArticleResourceLocalService
		_journalArticleResourceLocalService;

}