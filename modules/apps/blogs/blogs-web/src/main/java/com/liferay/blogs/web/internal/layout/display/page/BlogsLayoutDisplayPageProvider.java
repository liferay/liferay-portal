/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.layout.display.page;

import com.liferay.asset.util.AssetHelper;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.friendly.url.info.item.provider.InfoItemFriendlyURLProvider;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.layout.display.page.BaseLayoutDisplayPageProvider;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(service = LayoutDisplayPageProvider.class)
public class BlogsLayoutDisplayPageProvider
	extends BaseLayoutDisplayPageProvider<BlogsEntry> {

	@Override
	public String getClassName() {
		return BlogsEntry.class.getName();
	}

	@Override
	public String getDefaultURLSeparator() {
		return FriendlyURLResolverConstants.URL_SEPARATOR_BLOGS_ENTRY;
	}

	@Override
	public LayoutDisplayPageObjectProvider<BlogsEntry>
		getLayoutDisplayPageObjectProvider(BlogsEntry blogsEntry) {

		if ((blogsEntry == null) || blogsEntry.isDraft() ||
			blogsEntry.isInTrash()) {

			return null;
		}

		return new BlogsLayoutDisplayPageObjectProvider(
			_assetHelper, blogsEntry, _infoItemFriendlyURLProvider, _language);
	}

	@Override
	public LayoutDisplayPageObjectProvider<BlogsEntry>
		getLayoutDisplayPageObjectProvider(long groupId, String urlTitle) {

		if (urlTitle.contains(StringPool.SLASH)) {
			String[] urlNames = urlTitle.split(StringPool.SLASH);

			if (urlNames.length > 1) {
				Group group = _groupLocalService.fetchFriendlyURLGroup(
					CompanyThreadLocal.getCompanyId(),
					StringPool.SLASH + urlNames[0]);

				if (group != null) {
					return getLayoutDisplayPageObjectProvider(
						group.getGroupId(), urlNames[1]);
				}
			}
		}

		BlogsEntry blogsEntry = _blogsEntryLocalService.fetchEntry(
			groupId, urlTitle);

		if (blogsEntry == null) {
			blogsEntry = _blogsEntryLocalService.fetchEntry(
				groupId,
				urlTitle.substring(urlTitle.lastIndexOf(StringPool.SLASH) + 1));
		}

		if ((blogsEntry == null) || blogsEntry.isInTrash()) {
			return null;
		}

		return new BlogsLayoutDisplayPageObjectProvider(
			_assetHelper, blogsEntry, _infoItemFriendlyURLProvider, _language);
	}

	@Override
	protected BlogsLayoutDisplayPageObjectProvider
		doGetLayoutDisplayPageObjectProvider(
			long groupId, InfoItemReference infoItemReference) {

		InfoItemIdentifier infoItemIdentifier =
			infoItemReference.getInfoItemIdentifier();

		if (!(infoItemIdentifier instanceof ClassPKInfoItemIdentifier) &&
			!(infoItemIdentifier instanceof ERCInfoItemIdentifier)) {

			return null;
		}

		BlogsEntry blogsEntry = null;

		if (infoItemIdentifier instanceof ClassPKInfoItemIdentifier) {
			ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
				(ClassPKInfoItemIdentifier)
					infoItemReference.getInfoItemIdentifier();

			blogsEntry = _blogsEntryLocalService.fetchBlogsEntry(
				classPKInfoItemIdentifier.getClassPK());
		}
		else {
			ERCInfoItemIdentifier ercInfoItemIdentifier =
				(ERCInfoItemIdentifier)infoItemIdentifier;

			blogsEntry =
				_blogsEntryLocalService.fetchBlogsEntryByExternalReferenceCode(
					ercInfoItemIdentifier.getExternalReferenceCode(), groupId);
		}

		if ((blogsEntry == null) || blogsEntry.isDraft() ||
			blogsEntry.isInTrash()) {

			return null;
		}

		return new BlogsLayoutDisplayPageObjectProvider(
			_assetHelper, blogsEntry, _infoItemFriendlyURLProvider, _language);
	}

	@Reference
	private AssetHelper _assetHelper;

	@Reference
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference(target = "(item.class.name=com.liferay.blogs.model.BlogsEntry)")
	private InfoItemFriendlyURLProvider<BlogsEntry>
		_infoItemFriendlyURLProvider;

	@Reference
	private Language _language;

}