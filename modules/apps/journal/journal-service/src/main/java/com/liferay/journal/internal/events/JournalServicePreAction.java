/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.events;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.util.LinkedAssetEntryIdsUtil;
import com.liferay.journal.exception.NoSuchArticleException;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Julio Camarero
 */
@Component(
	property = "key=servlet.service.events.pre", service = LifecycleAction.class
)
public class JournalServicePreAction extends Action {

	@Override
	public void run(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws ActionException {

		try {
			servicePre(httpServletRequest);
		}
		catch (Exception exception) {
			throw new ActionException(exception);
		}
	}

	public void servicePre(HttpServletRequest httpServletRequest)
		throws PortalException {

		String strutsAction = _portal.getStrutsAction(httpServletRequest);

		if (!strutsAction.equals(_PATH_PORTAL_LAYOUT)) {
			return;
		}

		long mainJournalArticleId = ParamUtil.getLong(
			httpServletRequest, "p_j_a_id");

		if (mainJournalArticleId <= 0) {
			return;
		}

		try {
			JournalArticle mainJournalArticle =
				_journalArticleService.getArticle(mainJournalArticleId);

			AssetEntry layoutAssetEntry = _assetEntryLocalService.getEntry(
				JournalArticle.class.getName(),
				mainJournalArticle.getResourcePrimKey());

			httpServletRequest.setAttribute(
				WebKeys.LAYOUT_ASSET_ENTRY, layoutAssetEntry);

			LinkedAssetEntryIdsUtil.addLinkedAssetEntryId(
				httpServletRequest, layoutAssetEntry.getEntryId());
		}
		catch (NoSuchArticleException noSuchArticleException) {
			if (_log.isWarnEnabled()) {
				_log.warn(noSuchArticleException);
			}
		}
	}

	private static final String _PATH_PORTAL_LAYOUT = "/portal/layout";

	private static final Log _log = LogFactoryUtil.getLog(
		JournalServicePreAction.class);

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private JournalArticleService _journalArticleService;

	@Reference
	private Portal _portal;

}