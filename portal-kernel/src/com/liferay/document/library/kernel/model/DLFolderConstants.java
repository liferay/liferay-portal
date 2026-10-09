/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.kernel.model;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeFormatter;

/**
 * <p>
 * This contains several utility methods for the purpose of determining folder
 * IDs and data repository IDs as used by back-end data systems like search and
 * Document Library stores. These repository IDs should not be confused with the
 * repository ID used by {@link
 * com.liferay.portal.service.impl.RepositoryServiceImpl}.
 * </p>
 *
 * @author Samuel Kong
 * @author Alexander Chow
 */
public class DLFolderConstants {

	public static final long DEFAULT_PARENT_FOLDER_ID = 0;

	public static final String NAME_GENERAL_RESTRICTIONS = "blank";

	public static final int RESTRICTION_TYPE_FILE_ENTRY_TYPES_AND_WORKFLOW = 1;

	public static final int RESTRICTION_TYPE_INHERIT = 0;

	public static final int RESTRICTION_TYPE_WORKFLOW = 2;

	public static String getClassName() {
		return DLFolder.class.getName();
	}

	/**
	 * Determine the data repository ID from the repository ID and folder ID.
	 * The folder ID may be zero, implying that it is the root folder for the
	 * given repository.
	 */
	public static long getDataRepositoryId(long repositoryId, long folderId) {
		if (folderId != DEFAULT_PARENT_FOLDER_ID) {
			return folderId;
		}

		return repositoryId;
	}

	/**
	 * Determine the folder ID when no knowledge of it currently exists.
	 */
	public static long getFolderId(long groupId, long dataRepositoryId) {
		if (groupId != dataRepositoryId) {
			return dataRepositoryId;
		}

		return DEFAULT_PARENT_FOLDER_ID;
	}

	public static String getNameInvalidCharacters(String[] charBlacklist) {
		return StringUtil.merge(charBlacklist, StringPool.SPACE);
	}

	public static String getNameInvalidEndCharacters(
		String[] charLastBlacklist) {

		StringBundler sb = new StringBundler(charLastBlacklist.length * 2);

		sb.append(StringPool.BLANK);

		for (int i = 0; i < charLastBlacklist.length; i++) {
			if (charLastBlacklist[i].startsWith("\\u")) {
				sb.append(UnicodeFormatter.parseString(charLastBlacklist[i]));
			}
			else {
				sb.append(charLastBlacklist[i]);
			}

			if ((i + 1) >= charLastBlacklist.length) {
				continue;
			}

			sb.append(StringPool.SPACE);
		}

		return sb.toString();
	}

	public static String getNameReservedWords(String[] nameBlacklist) {
		return StringPool.NULL + StringPool.COMMA_AND_SPACE +
			StringUtil.merge(nameBlacklist, StringPool.COMMA_AND_SPACE);
	}

}