/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.criteria;

import com.liferay.item.selector.ItemSelectorReturnType;

/**
 * This return type should return the following information of a file entry as a
 * JSON object:
 *
 * <ul>
 * <li>
 * <code>classNameId</code>: The class name ID of the selected file entry
 * </li>
 * <li>
 * <code>extension</code>: The extension of the selected file entry
 * </li>
 * <li>
 * <code>fileEntryId</code>: The ID of the selected file entry
 * </li>
 * <li>
 * <code>formattedSize</code>: The size of the selected file entry, formatted
 * for the user's locale
 * </li>
 * <li>
 * <code>groupId</code>: The group ID of the selected file entry
 * </li>
 * <li>
 * <code>html</code>: The html of the selected file entry
 * </li>
 * <li>
 * <code>size</code>: The size of the selected file entry
 * </li>
 * <li>
 * <code>title</code>: The title of the selected file entry
 * </li>
 * <li>
 * <code>type</code>: The type of the selected file entry
 * </li>
 * <li>
 * <code>url</code>: The URL of the selected file entry
 * </li>
 * <li>
 * <code>uuid</code>: The UUID of the selected file entry
 * </li>
 * </ul>
 *
 * @author Sergio González
 */
public class FileEntryItemSelectorReturnType implements ItemSelectorReturnType {
}