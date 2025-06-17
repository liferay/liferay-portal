/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.entry.processor.resources.util;

import com.liferay.document.library.util.DLURLHelperUtil;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentCollectionLocalServiceUtil;
import com.liferay.fragment.service.FragmentEntryLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Eudaldo Alonso
 */
public class ResourcesFragmentEntryProcessorUtil {

	public static String processResources(
			FragmentEntryLink fragmentEntryLink, String code)
		throws PortalException {

		FragmentEntry fragmentEntry =
			FragmentEntryLocalServiceUtil.fetchFragmentEntry(
				fragmentEntryLink.getFragmentEntryId());

		if (fragmentEntry == null) {
			return code;
		}

		FragmentCollection fragmentCollection =
			FragmentCollectionLocalServiceUtil.fetchFragmentCollection(
				fragmentEntry.getFragmentCollectionId());

		Matcher matcher = _pattern.matcher(code);

		while (matcher.find()) {
			if (fragmentEntry.getGroupId() <= 0) {
				continue;
			}

			String fileName = matcher.group(1);

			FileEntry fileEntry = fragmentCollection.getResource(fileName);

			if ((fileEntry == null) &&
				Validator.isNotNull(FileUtil.getExtension(fileName))) {

				fileEntry = fragmentCollection.getResource(
					FileUtil.stripExtension(fileName));
			}

			String fileEntryURL = StringPool.BLANK;

			if (fileEntry != null) {
				fileEntryURL = DLURLHelperUtil.getDownloadURL(
					fileEntry, fileEntry.getFileVersion(), null,
					StringPool.BLANK, false, false);
			}

			code = StringUtil.replace(code, matcher.group(), fileEntryURL);
		}

		return code;
	}

	private static final Pattern _pattern = Pattern.compile(
		"\\[resources:(.+?)\\]");

}