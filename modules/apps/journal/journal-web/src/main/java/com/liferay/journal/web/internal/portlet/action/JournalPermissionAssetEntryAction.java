/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.portlet.action;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.publisher.action.AssetEntryAction;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.taglib.security.PermissionsURLTag;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = {
		"jakarta.portlet.name=" + JournalPortletKeys.JOURNAL,
		"model.class.name=com.liferay.journal.model.JournalArticle"
	},
	service = AssetEntryAction.class
)
public class JournalPermissionAssetEntryAction
	implements AssetEntryAction<JournalArticle> {

	@Override
	public String getDialogTitle(Locale locale) {
		return _language.get(locale, "permissions");
	}

	@Override
	public String getDialogURL(
			HttpServletRequest httpServletRequest,
			AssetRenderer<JournalArticle> assetRenderer)
		throws PortalException {

		JournalArticle article = (JournalArticle)assetRenderer.getAssetObject();

		String permissionURL = null;

		try {
			permissionURL = PermissionsURLTag.doTag(
				StringPool.BLANK, JournalArticle.class.getName(),
				article.getTitle(httpServletRequest.getLocale()), null,
				String.valueOf(article.getResourcePrimKey()),
				LiferayWindowState.POP_UP.toString(), null, httpServletRequest);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return permissionURL;
	}

	@Override
	public String getIcon() {
		return "cog";
	}

	@Override
	public String getMessage(Locale locale) {
		return _language.get(locale, "permissions");
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker,
			AssetRenderer<JournalArticle> assetRenderer)
		throws PortalException {

		JournalArticle article = (JournalArticle)assetRenderer.getAssetObject();

		return _journalArticleModelResourcePermission.contains(
			permissionChecker, article, ActionKeys.PERMISSIONS);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalPermissionAssetEntryAction.class);

	@Reference(
		target = "(model.class.name=com.liferay.journal.model.JournalArticle)"
	)
	private ModelResourcePermission<JournalArticle>
		_journalArticleModelResourcePermission;

	@Reference
	private Language _language;

}