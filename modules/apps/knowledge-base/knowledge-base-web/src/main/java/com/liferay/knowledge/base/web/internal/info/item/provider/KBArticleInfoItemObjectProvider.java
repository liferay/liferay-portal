/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.info.item.provider;

import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.provider.BaseInfoItemObjectProvider;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.service.KBArticleLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alicia García
 */
@Component(
	property = {
		"info.item.identifier=com.liferay.info.item.ClassPKInfoItemIdentifier",
		"info.item.identifier=com.liferay.info.item.ERCInfoItemIdentifier",
		"item.class.name=com.liferay.knowledge.base.model.KBArticle",
		"service.ranking:Integer=100"
	},
	service = InfoItemObjectProvider.class
)
public class KBArticleInfoItemObjectProvider
	extends BaseInfoItemObjectProvider<KBArticle> {

	@Override
	protected KBArticle doGetInfoItem(
			long groupId, InfoItemIdentifier infoItemIdentifier)
		throws NoSuchInfoItemException {

		if (!(infoItemIdentifier instanceof ClassPKInfoItemIdentifier) &&
			!(infoItemIdentifier instanceof ERCInfoItemIdentifier)) {

			throw new NoSuchInfoItemException(
				"Unsupported info item identifier " + infoItemIdentifier);
		}

		KBArticle kbArticle = null;

		if (infoItemIdentifier instanceof ClassPKInfoItemIdentifier) {
			ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
				(ClassPKInfoItemIdentifier)infoItemIdentifier;

			kbArticle = _getKBArticle(
				classPKInfoItemIdentifier.getClassPK(),
				classPKInfoItemIdentifier.getVersion());
		}
		else {
			ERCInfoItemIdentifier ercInfoItemIdentifier =
				(ERCInfoItemIdentifier)infoItemIdentifier;

			kbArticle = _getKBArticle(
				ercInfoItemIdentifier.getExternalReferenceCode(), groupId,
				ercInfoItemIdentifier.getVersion());
		}

		if ((kbArticle == null) ||
			(!Objects.equals(
				infoItemIdentifier.getVersion(),
				InfoItemIdentifier.VERSION_LATEST) &&
			 kbArticle.isDraft())) {

			throw new NoSuchInfoItemException(
				"Unable to get knowledge base article with info item " +
					"identifier " + infoItemIdentifier);
		}

		return kbArticle;
	}

	private KBArticle _getKBArticle(long classPK, String version) {
		if (Validator.isNull(version) ||
			Objects.equals(
				version, InfoItemIdentifier.VERSION_LATEST_APPROVED)) {

			return _kbArticleLocalService.fetchLatestKBArticle(
				classPK, WorkflowConstants.STATUS_APPROVED);
		}

		if (Objects.equals(version, InfoItemIdentifier.VERSION_LATEST)) {
			return _kbArticleLocalService.fetchLatestKBArticle(
				classPK, WorkflowConstants.STATUS_ANY);
		}

		KBArticle latestKBArticle = _kbArticleLocalService.fetchLatestKBArticle(
			classPK, WorkflowConstants.STATUS_ANY);

		return _kbArticleLocalService.fetchKBArticle(
			classPK, latestKBArticle.getGroupId(),
			GetterUtil.getInteger(version));
	}

	private KBArticle _getKBArticle(
		String externalReferenceCode, long groupId, String version) {

		if (Validator.isNull(version) ||
			Objects.equals(
				version, InfoItemIdentifier.VERSION_LATEST_APPROVED)) {

			return _kbArticleLocalService.
				fetchLatestKBArticleByExternalReferenceCode(
					groupId, externalReferenceCode,
					WorkflowConstants.STATUS_APPROVED);
		}

		if (Objects.equals(version, InfoItemIdentifier.VERSION_LATEST)) {
			return _kbArticleLocalService.
				fetchLatestKBArticleByExternalReferenceCode(
					groupId, externalReferenceCode);
		}

		return _kbArticleLocalService.fetchKBArticleByExternalReferenceCode(
			groupId, externalReferenceCode, GetterUtil.getInteger(version));
	}

	@Reference
	private KBArticleLocalService _kbArticleLocalService;

}