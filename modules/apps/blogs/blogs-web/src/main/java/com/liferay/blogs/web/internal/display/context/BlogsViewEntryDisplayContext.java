/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.display.context;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.blogs.constants.BlogsPortletKeys;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryServiceUtil;
import com.liferay.blogs.web.internal.configuration.BlogsPortletInstanceConfiguration;
import com.liferay.blogs.web.internal.util.BlogsEntryAssetEntryUtil;
import com.liferay.blogs.web.internal.util.BlogsEntryUtil;
import com.liferay.blogs.web.internal.util.BlogsPortletInstanceConfigurationUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.comment.CommentManagerUtil;
import com.liferay.portal.kernel.comment.Discussion;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.ServiceContextFunction;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.ratings.kernel.model.RatingsEntry;
import com.liferay.ratings.kernel.model.RatingsStats;
import com.liferay.ratings.kernel.service.RatingsEntryLocalServiceUtil;
import com.liferay.ratings.kernel.service.RatingsStatsLocalServiceUtil;

import java.util.Objects;

/**
 * @author Adolfo Pérez
 */
public class BlogsViewEntryDisplayContext {

	public BlogsViewEntryDisplayContext(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;

		_themeDisplay = (ThemeDisplay)liferayPortletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getBlogEntryExternalReferenceCode() {
		BlogsEntry blogsEntry = getBlogsEntry();

		return blogsEntry.getExternalReferenceCode();
	}

	public BlogsEntry getBlogsEntry() {
		if (_blogsEntry != null) {
			return _blogsEntry;
		}

		_blogsEntry = (BlogsEntry)_liferayPortletRequest.getAttribute(
			WebKeys.BLOGS_ENTRY);

		return _blogsEntry;
	}

	public AssetEntry getBlogsEntryAssetEntry() throws PortalException {
		if (_assetEntry != null) {
			return _assetEntry;
		}

		_assetEntry = BlogsEntryAssetEntryUtil.getAssetEntry(
			_liferayPortletRequest.getHttpServletRequest(), getBlogsEntry());

		return _assetEntry;
	}

	public long getBlogsEntryAssetEntryId() throws PortalException {
		AssetEntry blogsEntryAssetEntry = getBlogsEntryAssetEntry();

		return blogsEntryAssetEntry.getEntryId();
	}

	public String getBlogsEntryDescription() {
		BlogsEntry blogsEntry = getBlogsEntry();

		String description = blogsEntry.getDescription();

		if (Validator.isNotNull(description)) {
			return description;
		}

		return HtmlUtil.stripHtml(
			StringUtil.shorten(
				blogsEntry.getContent(),
				PropsValues.BLOGS_PAGE_ABSTRACT_LENGTH));
	}

	public long getBlogsEntryId() {
		BlogsEntry blogsEntry = getBlogsEntry();

		return ParamUtil.getLong(
			_liferayPortletRequest, "entryId", blogsEntry.getEntryId());
	}

	public RatingsEntry getBlogsEntryRatingsEntry() {
		RatingsStats ratingsStats = getBlogsEntryRatingsStats();

		if (ratingsStats == null) {
			return null;
		}

		return RatingsEntryLocalServiceUtil.fetchEntry(
			_themeDisplay.getUserId(), BlogsEntry.class.getName(),
			getBlogsEntryId());
	}

	public RatingsStats getBlogsEntryRatingsStats() {
		return RatingsStatsLocalServiceUtil.fetchStats(
			BlogsEntry.class.getName(), getBlogsEntryId());
	}

	public String getBlogsEntryTitle() {
		if (_blogsEntryTitle != null) {
			return _blogsEntryTitle;
		}

		_blogsEntryTitle = BlogsEntryUtil.getDisplayTitle(
			ResourceBundleUtil.getBundle(
				_liferayPortletRequest.getLocale(), getClass()),
			getBlogsEntry());

		return _blogsEntryTitle;
	}

	public long getBlogsEntryUserId() {
		BlogsEntry blogsEntry = getBlogsEntry();

		return blogsEntry.getUserId();
	}

	public Discussion getDiscussion() throws PortalException {
		if (_discussion != null) {
			return _discussion;
		}

		_discussion = CommentManagerUtil.getDiscussion(
			_themeDisplay.getUserId(), _themeDisplay.getScopeGroupId(),
			BlogsEntry.class.getName(), getBlogsEntryId(),
			new ServiceContextFunction(_liferayPortletRequest));

		return _discussion;
	}

	public BlogsEntry getNextBlogsEntry() throws PortalException {
		BlogsEntry[] previousAndNextBlogsEntries =
			_getPreviousAndNextBlogsEntries();

		return previousAndNextBlogsEntries[2];
	}

	public BlogsEntry getPreviousBlogsEntry() throws PortalException {
		BlogsEntry[] previousAndNextBlogsEntries =
			_getPreviousAndNextBlogsEntries();

		return previousAndNextBlogsEntries[0];
	}

	public String getRedirect() {
		String redirect = ParamUtil.getString(
			_liferayPortletRequest, "redirect");

		if (Validator.isNotNull(redirect)) {
			return redirect;
		}

		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCRenderCommandName(
			_getRedirectRenderCommandName()
		).buildString();
	}

	public String getTrackbackURL() throws PortalException {
		BlogsEntry entry = getBlogsEntry();

		return StringBundler.concat(
			PortalUtil.getLayoutFullURL(
				_themeDisplay.getLayout(), _themeDisplay, false),
			Portal.FRIENDLY_URL_SEPARATOR, "blogs/trackback/",
			entry.getUrlTitle());
	}

	public boolean isBlogsEntryPreviousAndNextNavigationEnabled()
		throws PortalException {

		if (!PropsValues.BLOGS_ENTRY_PREVIOUS_AND_NEXT_NAVIGATION_ENABLED) {
			return false;
		}

		if ((getPreviousBlogsEntry() != null) ||
			(getNextBlogsEntry() != null)) {

			return true;
		}

		return false;
	}

	public boolean isCommentRatingsEnabled() throws ConfigurationException {
		BlogsPortletInstanceConfiguration blogsPortletInstanceConfiguration =
			_getBlogsPortletInstanceConfiguration();

		return blogsPortletInstanceConfiguration.enableCommentRatings();
	}

	public boolean isCommentsEnabled() throws PortalException {
		Layout layout = _themeDisplay.getLayout();

		if (layout.isTypeAssetDisplay() || (getDiscussion() == null)) {
			return false;
		}

		BlogsPortletInstanceConfiguration blogsPortletInstanceConfiguration =
			_getBlogsPortletInstanceConfiguration();

		return blogsPortletInstanceConfiguration.enableComments();
	}

	public boolean isTrackbackEnabled() {
		BlogsEntry blogsEntry = getBlogsEntry();

		if (PropsValues.BLOGS_TRACKBACK_ENABLED &&
			blogsEntry.isAllowTrackbacks() &&
			Validator.isNotNull(blogsEntry.getUrlTitle())) {

			return true;
		}

		return false;
	}

	private BlogsPortletInstanceConfiguration
			_getBlogsPortletInstanceConfiguration()
		throws ConfigurationException {

		if (_blogsPortletInstanceConfiguration != null) {
			return _blogsPortletInstanceConfiguration;
		}

		_blogsPortletInstanceConfiguration =
			BlogsPortletInstanceConfigurationUtil.
				getBlogsPortletInstanceConfiguration(_themeDisplay);

		return _blogsPortletInstanceConfiguration;
	}

	private BlogsEntry[] _getPreviousAndNextBlogsEntries()
		throws PortalException {

		if (_previousAndNextBlogsEntries != null) {
			return _previousAndNextBlogsEntries;
		}

		_previousAndNextBlogsEntries =
			BlogsEntryServiceUtil.getEntriesPrevAndNext(getBlogsEntryId());

		return _previousAndNextBlogsEntries;
	}

	private String _getRedirectRenderCommandName() {
		String mvcRenderCommandName = "/blogs/view";

		if (Objects.equals(
				_liferayPortletRequest.getPortletName(),
				BlogsPortletKeys.BLOGS_AGGREGATOR)) {

			mvcRenderCommandName = "/blogs_aggregator/view";
		}

		return mvcRenderCommandName;
	}

	private AssetEntry _assetEntry;
	private BlogsEntry _blogsEntry;
	private String _blogsEntryTitle;
	private BlogsPortletInstanceConfiguration
		_blogsPortletInstanceConfiguration;
	private Discussion _discussion;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private BlogsEntry[] _previousAndNextBlogsEntries;
	private final ThemeDisplay _themeDisplay;

}