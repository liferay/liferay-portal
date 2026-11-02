/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.test.util.search;

import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.blogs.test.util.BlogsTestUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author André de Oliveira
 */
public class BlogsEntrySearchFixture {

	public BlogsEntrySearchFixture(
		BlogsEntryLocalService blogsEntryLocalService) {

		_blogsEntryLocalService = blogsEntryLocalService;
	}

	public BlogsEntry addBlogsEntry(BlogsEntryBlueprint blogsEntryBlueprint) {
		try {
			ServiceContext serviceContext =
				blogsEntryBlueprint.getServiceContext();

			if (serviceContext == null) {
				serviceContext = ServiceContextTestUtil.getServiceContext(
					blogsEntryBlueprint.getGroupId());
			}

			BlogsEntry blogsEntry = BlogsTestUtil.addEntry(
				blogsEntryBlueprint.getUserId(), blogsEntryBlueprint.getTitle(),
				blogsEntryBlueprint.getContent(), serviceContext);

			_blogsEntries.add(blogsEntry);

			return blogsEntry;
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	public List<BlogsEntry> getBlogsEntries() {
		return _blogsEntries;
	}

	private final List<BlogsEntry> _blogsEntries = new ArrayList<>();
	private final BlogsEntryLocalService _blogsEntryLocalService;

}