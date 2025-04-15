/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.BaseVerticalCard;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.web.internal.display.context.JournalDisplayContext;
import com.liferay.journal.web.internal.security.permission.resource.JournalArticlePermission;
import com.liferay.journal.web.internal.servlet.taglib.util.JournalArticleActionDropdownItemsProvider;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.trash.TrashHelper;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Locale;

/**
 * @author Eudaldo Alonso
 */
public class JournalArticleVerticalCard extends BaseVerticalCard {

	public JournalArticleVerticalCard(
		BaseModel<?> baseModel, RenderRequest renderRequest,
		RenderResponse renderResponse, RowChecker rowChecker,
		AssetDisplayPageFriendlyURLProvider assetDisplayPageFriendlyURLProvider,
		TrashHelper trashHelper, JournalDisplayContext journalDisplayContext) {

		super(baseModel, renderRequest, rowChecker);

		_renderResponse = renderResponse;
		_assetDisplayPageFriendlyURLProvider =
			assetDisplayPageFriendlyURLProvider;
		_trashHelper = trashHelper;
		_journalDisplayContext = journalDisplayContext;

		_article = (JournalArticle)baseModel;
		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		JournalArticleActionDropdownItemsProvider
			articleActionDropdownItemsProvider =
				new JournalArticleActionDropdownItemsProvider(
					_article,
					PortalUtil.getLiferayPortletRequest(renderRequest),
					PortalUtil.getLiferayPortletResponse(_renderResponse),
					_assetDisplayPageFriendlyURLProvider, _trashHelper);

		try {
			return articleActionDropdownItemsProvider.getActionDropdownItems();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	@Override
	public String getHref() {
		try {
			if (!JournalArticlePermission.contains(
					themeDisplay.getPermissionChecker(), _article,
					ActionKeys.UPDATE)) {

				return StringPool.BLANK;
			}

			return PortletURLBuilder.createRenderURL(
				_renderResponse
			).setMVCRenderCommandName(
				"/journal/edit_article"
			).setRedirect(
				themeDisplay.getURLCurrent()
			).setParameter(
				"articleId", _article.getArticleId()
			).setParameter(
				"backURLTitle",
				() -> {
					PortletDisplay portletDisplay =
						themeDisplay.getPortletDisplay();

					return portletDisplay.getPortletDisplayName();
				}
			).setParameter(
				"folderId", _article.getFolderId()
			).setParameter(
				"groupId", _article.getGroupId()
			).setParameter(
				"referringPortletResource",
				ParamUtil.getString(
					_httpServletRequest, "referringPortletResource")
			).setParameter(
				"version", _article.getVersion()
			).buildString();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return null;
	}

	@Override
	public String getIcon() {
		return "web-content";
	}

	@Override
	public String getImageSrc() {
		return HtmlUtil.escape(_article.getArticleImageURL(themeDisplay));
	}

	@Override
	public String getInputName() {
		return rowChecker.getRowIds() + JournalArticle.class.getSimpleName();
	}

	@Override
	public String getInputValue() {
		return HtmlUtil.escape(_article.getArticleId());
	}

	@Override
	public List<LabelItem> getLabels() {
		return LabelItemListBuilder.add(
			() -> !_article.isApproved() && _article.hasApprovedVersion(),
			labelItem -> labelItem.setStatus(WorkflowConstants.STATUS_APPROVED)
		).add(
			labelItem -> labelItem.setStatus(_article.getStatus())
		).build();
	}

	@Override
	public String getStickerCssClass() {
		return "sticker-bottom-left ";
	}

	@Override
	public String getStickerIcon() {
		return "web-content";
	}

	@Override
	public String getStickerImageSrc() {
		try {
			User user = UserLocalServiceUtil.fetchUser(
				_article.getStatusByUserId());

			if (user == null) {
				return StringPool.BLANK;
			}

			if (user.getPortraitId() <= 0) {
				return null;
			}

			return user.getPortraitURL(themeDisplay);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return StringPool.BLANK;
		}
	}

	@Override
	public String getSubtitle() {
		return _journalDisplayContext.getArticleSubtitle(_article);
	}

	@Override
	public String getTitle() {
		String title = _article.getTitle(themeDisplay.getLocale());

		if (Validator.isNotNull(title)) {
			return title;
		}

		Locale defaultLanguage = LocaleUtil.fromLanguageId(
			_article.getDefaultLanguageId());

		return _article.getTitle(defaultLanguage);
	}

	@Override
	public boolean isTranslated() {
		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalArticleVerticalCard.class);

	private final JournalArticle _article;
	private final AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;
	private final HttpServletRequest _httpServletRequest;
	private final JournalDisplayContext _journalDisplayContext;
	private final RenderResponse _renderResponse;
	private final TrashHelper _trashHelper;

}