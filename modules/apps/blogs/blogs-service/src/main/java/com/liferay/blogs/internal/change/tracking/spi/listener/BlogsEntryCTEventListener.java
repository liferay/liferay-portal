/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.internal.change.tracking.spi.listener;

import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.spi.listener.CTEventListener;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.Portal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wulbern
 */
@Component(service = CTEventListener.class)
public class BlogsEntryCTEventListener implements CTEventListener {

	@Override
	public void onAfterPublish(long ctCollectionId) {
		Pattern pattern = _getPattern(ctCollectionId);

		for (CTEntry ctEntry :
				_ctEntryLocalService.getCTEntries(
					ctCollectionId,
					_portal.getClassNameId(BlogsEntry.class.getName()))) {

			BlogsEntry blogsEntry = _blogsEntryLocalService.fetchBlogsEntry(
				ctEntry.getModelClassPK());

			if ((blogsEntry == null) || (blogsEntry.getContent() == null)) {
				continue;
			}

			Matcher matcher = pattern.matcher(blogsEntry.getContent());

			String content = matcher.replaceAll(StringPool.BLANK);

			if (content.equals(blogsEntry.getContent())) {
				continue;
			}

			blogsEntry.setModifiedDate(blogsEntry.getModifiedDate());
			blogsEntry.setContent(content);

			_blogsEntryLocalService.updateBlogsEntry(blogsEntry);
		}
	}

	private Pattern _getPattern(long ctCollectionId) {
		String parameter =
			"previewCTCollectionId=" + ctCollectionId + "(?!\\d)";

		return Pattern.compile(
			StringBundler.concat(
				"(?<=\\?|&amp;)", parameter, "&amp;|(?:\\?|&amp;)", parameter,
				"(?!&amp;)"));
	}

	@Reference
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Reference
	private CTEntryLocalService _ctEntryLocalService;

	@Reference
	private Portal _portal;

}