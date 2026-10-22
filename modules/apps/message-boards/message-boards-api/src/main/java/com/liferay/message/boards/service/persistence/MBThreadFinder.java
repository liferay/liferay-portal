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
public interface MBThreadFinder {

	public int countByG_U(
		long groupId, long userId,
		com.liferay.portal.kernel.dao.orm.QueryDefinition
			<com.liferay.message.boards.model.MBThread> queryDefinition);

	public int countByG_U_LPD(
		long groupId, long userId, java.util.Date lastPostDate,
		com.liferay.portal.kernel.dao.orm.QueryDefinition
			<com.liferay.message.boards.model.MBThread> queryDefinition);

	public int countByG_U_A(
		long groupId, long userId, boolean anonymous,
		com.liferay.portal.kernel.dao.orm.QueryDefinition
			<com.liferay.message.boards.model.MBThread> queryDefinition);

	public int countByS_G_U(
		long groupId, long userId,
		com.liferay.portal.kernel.dao.orm.QueryDefinition
			<com.liferay.message.boards.model.MBThread> queryDefinition);

	public int countByG_U_LPD_A(
		long groupId, long userId, java.util.Date lastPostDate,
		boolean includeAnonymous,
		com.liferay.portal.kernel.dao.orm.QueryDefinition
			<com.liferay.message.boards.model.MBThread> queryDefinition);

	public int filterCountByG_C(long groupId, long categoryId);

	public int filterCountByG_C(
		long groupId, long categoryId,
		com.liferay.portal.kernel.dao.orm.QueryDefinition
			<com.liferay.message.boards.model.MBThread> queryDefinition);

	public int filterCountByS_G_U_C(
		long groupId, long userId, long[] categoryIds,
		com.liferay.portal.kernel.dao.orm.QueryDefinition
			<com.liferay.message.boards.model.MBThread> queryDefinition);

	public java.util.List<com.liferay.message.boards.model.MBThread>
		filterFindByG_C(
			long groupId, long categoryId,
			com.liferay.portal.kernel.dao.orm.QueryDefinition
				<com.liferay.message.boards.model.MBThread> queryDefinition);

	public java.util.List<com.liferay.message.boards.model.MBThread>
		filterFindByS_G_U_C(
			long groupId, long userId, long[] categoryIds,
			com.liferay.portal.kernel.dao.orm.QueryDefinition
				<com.liferay.message.boards.model.MBThread> queryDefinition);

	public java.util.List<com.liferay.message.boards.model.MBThread> findByG_U(
		long groupId, long userId,
		com.liferay.portal.kernel.dao.orm.QueryDefinition
			<com.liferay.message.boards.model.MBThread> queryDefinition);

	public java.util.List<com.liferay.message.boards.model.MBThread>
		findByG_U_LPD(
			long groupId, long userId, java.util.Date lastPostDate,
			com.liferay.portal.kernel.dao.orm.QueryDefinition
				<com.liferay.message.boards.model.MBThread> queryDefinition);

	public java.util.List<com.liferay.message.boards.model.MBThread>
		findByG_U_A(
			long groupId, long userId, boolean anonymous,
			com.liferay.portal.kernel.dao.orm.QueryDefinition
				<com.liferay.message.boards.model.MBThread> queryDefinition);

	public java.util.List<com.liferay.message.boards.model.MBThread>
		findByS_G_U(
			long groupId, long userId,
			com.liferay.portal.kernel.dao.orm.QueryDefinition
				<com.liferay.message.boards.model.MBThread> queryDefinition);

	public java.util.List<com.liferay.message.boards.model.MBThread>
		findByG_U_LPD_A(
			long groupId, long userId, java.util.Date lastPostDate,
			boolean includeAnonymous,
			com.liferay.portal.kernel.dao.orm.QueryDefinition
				<com.liferay.message.boards.model.MBThread> queryDefinition);

}
// LIFERAY-SERVICE-BUILDER-HASH:-34698296