/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.web.internal.constants.BlogsWebConstants;
import com.liferay.blogs.web.internal.security.permission.resource.BlogsEntryPermission;
import com.liferay.blogs.web.internal.servlet.taglib.util.BlogsEntryActionDropdownItemsProvider;
import com.liferay.blogs.web.internal.util.BlogsEntryUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.BaseVerticalCard;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.HtmlParser;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.trash.TrashHelper;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Adolfo Pérez
 */
public class BlogsEntryVerticalCard extends BaseVerticalCard {

	public BlogsEntryVerticalCard(
		BlogsEntry blogsEntry, String blogsEntryURL, HtmlParser htmlParser,
		PermissionChecker permissionChecker, RenderRequest renderRequest,
		RenderResponse renderResponse, ResourceBundle resourceBundle,
		RowChecker rowChecker, TrashHelper trashHelper) {

		super(blogsEntry, renderRequest, rowChecker);

		_blogsEntry = blogsEntry;
		_blogsEntryURL = blogsEntryURL;
		_htmlParser = htmlParser;
		_permissionChecker = permissionChecker;
		_renderResponse = renderResponse;
		_resourceBundle = resourceBundle;
		_trashHelper = trashHelper;
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		try {
			BlogsEntryActionDropdownItemsProvider
				blogsEntryActionDropdownItemsProvider =
					new BlogsEntryActionDropdownItemsProvider(
						renderRequest, _renderResponse, _permissionChecker,
						_resourceBundle, _trashHelper);

			return blogsEntryActionDropdownItemsProvider.getActionDropdownItems(
				_blogsEntry);
		}
		catch (PortalException portalException) {
			return ReflectionUtil.throwException(portalException);
		}
	}

	@Override
	public String getDefaultEventHandler() {
		return BlogsWebConstants.BLOGS_ELEMENTS_DEFAULT_EVENT_HANDLER;
	}

	@Override
	public String getHref() {
		try {
			if (!BlogsEntryPermission.contains(
					_permissionChecker, _blogsEntry, ActionKeys.UPDATE)) {

				return null;
			}

			return _blogsEntryURL;
		}
		catch (PortalException portalException) {
			return ReflectionUtil.throwException(portalException);
		}
	}

	@Override
	public String getIcon() {
		return "blogs";
	}

	@Override
	public String getImageSrc() {
		try {
			String coverImageURL = _blogsEntry.getCoverImageURL(themeDisplay);

			if (Validator.isNull(coverImageURL)) {
				return _blogsEntry.getSmallImageURL(themeDisplay);
			}

			return coverImageURL;
		}
		catch (PortalException portalException) {
			return ReflectionUtil.throwException(portalException);
		}
	}

	@Override
	public String getSubtitle() {
		Date modifiedDate = _blogsEntry.getModifiedDate();

		String modifiedDateDescription = LanguageUtil.getTimeDescription(
			PortalUtil.getHttpServletRequest(renderRequest),
			System.currentTimeMillis() - modifiedDate.getTime(), true);

		return LanguageUtil.format(
			_resourceBundle, "x-ago-by-x",
			new Object[] {
				modifiedDateDescription,
				HtmlUtil.escape(_blogsEntry.getStatusByUserName())
			});
	}

	@Override
	public String getTitle() {
		return _htmlParser.extractText(
			BlogsEntryUtil.getDisplayTitle(_resourceBundle, _blogsEntry));
	}

	@Override
	public Boolean isFlushHorizontal() {
		return true;
	}

	private final BlogsEntry _blogsEntry;
	private final String _blogsEntryURL;
	private final HtmlParser _htmlParser;
	private final PermissionChecker _permissionChecker;
	private final RenderResponse _renderResponse;
	private final ResourceBundle _resourceBundle;
	private final TrashHelper _trashHelper;

}