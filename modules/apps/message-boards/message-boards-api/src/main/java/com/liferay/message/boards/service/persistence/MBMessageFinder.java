/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.persistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@ProviderType
public interface MBMessageFinder {

	public int countByParentMessageId(
		long parentMessageId, boolean flatten,
		com.liferay.portal.kernel.dao.orm.QueryDefinition
			<com.liferay.message.boards.model.MBMessage> queryDefinition);

	public int filterCountByG_U_C_S(
		long groupId, long userId, long[] categoryIds, int status);

	public int filterCountByG_U_C_A_S(
		long groupId, long userId, long[] categoryIds, boolean anonymous,
		int status);

	public int filterCountByG_U_MD_C_S(
		long groupId, long userId, java.util.Date modifiedDate,
		long[] categoryIds, int status);

	public int filterCountByG_U_MD_C_A_S(
		long groupId, long userId, java.util.Date modifiedDate,
		long[] categoryIds, boolean anonymous, int status);

	public java.util.List<Long> filterFindByG_U_C_S(
		long groupId, long userId, long[] categoryIds, int status, int start,
		int end);

	public java.util.List<Long> filterFindByG_U_C_A_S(
		long groupId, long userId, long[] categoryIds, boolean anonymous,
		int status, int start, int end);

	public java.util.List<Long> filterFindByG_U_MD_C_S(
		long groupId, long userId, java.util.Date modifiedDate,
		long[] categoryIds, int status, int start, int end);

	public java.util.List<Long> filterFindByG_U_MD_C_A_S(
		long groupId, long userId, java.util.Date modifiedDate,
		long[] categoryIds, boolean anonymous, int status, int start, int end);

	public java.util.List<com.liferay.message.boards.model.MBMessage>
		findByParentMessageId(
			long parentMessageId, boolean flatten,
			com.liferay.portal.kernel.dao.orm.QueryDefinition
				<com.liferay.message.boards.model.MBMessage> queryDefinition);

	public java.util.List<com.liferay.message.boards.model.MBMessage>
		findByThreadId(
			long threadId,
			com.liferay.portal.kernel.dao.orm.QueryDefinition
				<com.liferay.message.boards.model.MBMessage> queryDefinition);

}
// LIFERAY-SERVICE-BUILDER-HASH:-971677681