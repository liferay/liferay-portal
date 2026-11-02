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
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.Portal;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wulbern
 */
@Component(service = CTEventListener.class)
public class BlogsEntryCTEventListener implements CTEventListener {

	@Override
	public void onAfterPublish(long ctCollectionId) {
		for (CTEntry ctEntry :
				_ctEntryLocalService.getCTEntries(
					ctCollectionId,
					_portal.getClassNameId(BlogsEntry.class.getName()))) {

			BlogsEntry blogsEntry = _blogsEntryLocalService.fetchBlogsEntry(
				ctEntry.getModelClassPK());

			if (blogsEntry == null) {
				continue;
			}

			String content = _removePreviewCTCollectionId(
				blogsEntry.getContent(), ctCollectionId);

			if (Objects.equals(content, blogsEntry.getContent())) {
				continue;
			}

			blogsEntry.setContent(content);

			_blogsEntryLocalService.updateBlogsEntry(blogsEntry);
		}
	}

	private String _removePreviewCTCollectionId(
		String content, long ctCollectionId) {

		if (content == null) {
			return null;
		}

		String parameter = "previewCTCollectionId=" + ctCollectionId;

		int index = content.indexOf(parameter);

		while (index >= 0) {
			int end = index + parameter.length();

			if ((end < content.length()) &&
				Character.isDigit(content.charAt(end))) {

				index = content.indexOf(parameter, end);

				continue;
			}

			if (content.startsWith(StringPool.AMPERSAND_ENCODED, end)) {
				end += StringPool.AMPERSAND_ENCODED.length();
			}
			else if (content.startsWith(StringPool.AMPERSAND, end)) {
				end++;
			}
			else if (content.startsWith(
						StringPool.AMPERSAND_ENCODED,
						index - StringPool.AMPERSAND_ENCODED.length())) {

				index -= StringPool.AMPERSAND_ENCODED.length();
			}
			else if ((index > 0) &&
					 ((content.charAt(index - 1) == CharPool.AMPERSAND) ||
					  (content.charAt(index - 1) == CharPool.QUESTION))) {

				index--;
			}

			content = content.substring(0, index) + content.substring(end);

			index = content.indexOf(parameter, index);
		}

		return content;
	}

	@Reference
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Reference
	private CTEntryLocalService _ctEntryLocalService;

	@Reference
	private Portal _portal;

}