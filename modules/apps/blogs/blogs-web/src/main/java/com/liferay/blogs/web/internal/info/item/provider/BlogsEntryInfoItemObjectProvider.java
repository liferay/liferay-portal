/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.info.item.provider;

import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.GroupUrlTitleInfoItemIdentifier;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.provider.BaseInfoItemObjectProvider;
import com.liferay.info.item.provider.InfoItemObjectProvider;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 */
@Component(
	property = {
		"info.item.identifier=com.liferay.info.item.ClassPKInfoItemIdentifier",
		"info.item.identifier=com.liferay.info.item.ERCInfoItemIdentifier",
		"info.item.identifier=com.liferay.info.item.GroupUrlTitleInfoItemIdentifier",
		"item.class.name=com.liferay.blogs.model.BlogsEntry",
		"service.ranking:Integer=100"
	},
	service = InfoItemObjectProvider.class
)
public class BlogsEntryInfoItemObjectProvider
	extends BaseInfoItemObjectProvider<BlogsEntry> {

	@Override
	protected BlogsEntry doGetInfoItem(
			long groupId, InfoItemIdentifier infoItemIdentifier)
		throws NoSuchInfoItemException {

		if (!(infoItemIdentifier instanceof ClassPKInfoItemIdentifier) &&
			!(infoItemIdentifier instanceof ERCInfoItemIdentifier) &&
			!(infoItemIdentifier instanceof GroupUrlTitleInfoItemIdentifier)) {

			throw new NoSuchInfoItemException(
				"Unsupported info item identifier " + infoItemIdentifier);
		}

		BlogsEntry blogsEntry = null;

		if (infoItemIdentifier instanceof ClassPKInfoItemIdentifier) {
			ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
				(ClassPKInfoItemIdentifier)infoItemIdentifier;

			blogsEntry = _blogsEntryLocalService.fetchBlogsEntry(
				classPKInfoItemIdentifier.getClassPK());
		}
		else if (infoItemIdentifier instanceof ERCInfoItemIdentifier) {
			ERCInfoItemIdentifier ercInfoItemIdentifier =
				(ERCInfoItemIdentifier)infoItemIdentifier;

			blogsEntry =
				_blogsEntryLocalService.fetchBlogsEntryByExternalReferenceCode(
					ercInfoItemIdentifier.getExternalReferenceCode(), groupId);
		}
		else if (infoItemIdentifier instanceof
					GroupUrlTitleInfoItemIdentifier) {

			GroupUrlTitleInfoItemIdentifier groupURLTitleInfoItemIdentifier =
				(GroupUrlTitleInfoItemIdentifier)infoItemIdentifier;

			blogsEntry = _blogsEntryLocalService.fetchEntry(
				groupURLTitleInfoItemIdentifier.getGroupId(),
				groupURLTitleInfoItemIdentifier.getUrlTitle());
		}

		if ((blogsEntry == null) || blogsEntry.isDraft() ||
			blogsEntry.isInTrash()) {

			throw new NoSuchInfoItemException(
				"Unable to get blogs entry with info item identifier " +
					infoItemIdentifier);
		}

		return blogsEntry;
	}

	@Reference
	private BlogsEntryLocalService _blogsEntryLocalService;

}