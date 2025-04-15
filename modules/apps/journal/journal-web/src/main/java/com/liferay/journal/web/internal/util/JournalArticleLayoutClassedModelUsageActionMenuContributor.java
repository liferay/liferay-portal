/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.util;

import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.web.internal.security.permission.resource.JournalArticlePermission;
import com.liferay.layout.model.LayoutClassedModelUsage;
import com.liferay.layout.util.LayoutClassedModelUsageActionMenuContributor;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(
	property = "model.class.name=com.liferay.journal.model.JournalArticle",
	service = LayoutClassedModelUsageActionMenuContributor.class
)
public class JournalArticleLayoutClassedModelUsageActionMenuContributor
	implements LayoutClassedModelUsageActionMenuContributor {

	@Override
	public List<DropdownItem> getLayoutClassedModelUsageActionDropdownItems(
		HttpServletRequest httpServletRequest,
		LayoutClassedModelUsage layoutClassedModelUsage) {

		JournalArticle article = _journalArticleLocalService.fetchLatestArticle(
			layoutClassedModelUsage.getClassPK(), WorkflowConstants.STATUS_ANY,
			false);

		return new DropdownItemList() {
			{
				JournalArticle approvedArticle =
					_journalArticleLocalService.fetchLatestArticle(
						layoutClassedModelUsage.getClassPK(),
						WorkflowConstants.STATUS_APPROVED);

				ThemeDisplay themeDisplay =
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				if (approvedArticle != null) {
					add(
						dropdownItem -> {
							dropdownItem.setHref(
								_getURL(
									article, layoutClassedModelUsage,
									AssetRendererFactory.TYPE_LATEST_APPROVED,
									InfoItemIdentifier.VERSION_LATEST_APPROVED,
									httpServletRequest));
							dropdownItem.setLabel(
								_language.get(
									themeDisplay.getLocale(), "view-in-page"));
						});
				}

				if (article.isDraft() || article.isPending() ||
					article.isScheduled()) {

					try {
						if (JournalArticlePermission.contains(
								themeDisplay.getPermissionChecker(), article,
								ActionKeys.UPDATE)) {

							String key = "preview-draft-in-page";

							if (article.isPending()) {
								key = "preview-pending-in-page";
							}
							else if (article.isScheduled()) {
								key = "preview-scheduled-in-page";
							}

							String label = _language.get(
								themeDisplay.getLocale(), key);

							add(
								dropdownItem -> {
									dropdownItem.setHref(
										_getURL(
											article, layoutClassedModelUsage,
											AssetRendererFactory.TYPE_LATEST,
											InfoItemIdentifier.VERSION_LATEST,
											httpServletRequest));
									dropdownItem.setLabel(label);
								});
						}
					}
					catch (PortalException portalException) {
						_log.error(
							"Unable to check article permission",
							portalException);
					}
				}
			}
		};
	}

	private String _getURL(
			JournalArticle article,
			LayoutClassedModelUsage layoutClassedModelUsage, int previewType,
			String previewVersion, HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String layoutURL = null;

		if (layoutClassedModelUsage.getContainerType() ==
				_portal.getClassNameId(FragmentEntryLink.class)) {

			layoutURL = _portal.getLayoutFriendlyURL(
				_layoutLocalService.fetchLayout(
					layoutClassedModelUsage.getPlid()),
				themeDisplay);

			layoutURL = HttpComponentsUtil.setParameter(
				layoutURL, "previewClassNameId",
				String.valueOf(layoutClassedModelUsage.getClassNameId()));
			layoutURL = HttpComponentsUtil.setParameter(
				layoutURL, "previewClassPK",
				String.valueOf(layoutClassedModelUsage.getClassPK()));
			layoutURL = HttpComponentsUtil.setParameter(
				layoutURL, "previewType", String.valueOf(previewType));
			layoutURL = HttpComponentsUtil.setParameter(
				layoutURL, "previewVersion", previewVersion);
		}
		else {
			layoutURL = PortletURLBuilder.create(
				PortletURLFactoryUtil.create(
					httpServletRequest,
					layoutClassedModelUsage.getContainerKey(),
					layoutClassedModelUsage.getPlid(),
					PortletRequest.RENDER_PHASE)
			).setParameter(
				"previewClassNameId", layoutClassedModelUsage.getClassNameId()
			).setParameter(
				"previewClassPK", layoutClassedModelUsage.getClassPK()
			).setParameter(
				"previewType", previewType
			).setParameter(
				"previewVersion", previewVersion
			).buildString();
		}

		String portletURLString = HttpComponentsUtil.addParameters(
			layoutURL, "p_l_back_url", themeDisplay.getURLCurrent(),
			"p_l_back_url_title", article.getTitle(themeDisplay.getLocale()));

		return portletURLString + "#portlet_" +
			layoutClassedModelUsage.getContainerKey();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalArticleLayoutClassedModelUsageActionMenuContributor.class);

	@Reference
	private JournalArticleLocalService _journalArticleLocalService;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

}