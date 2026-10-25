/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.kernel.service.persistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@ProviderType
public interface DLFileEntryFinder {

	public int countByExtraSettings();

	public int countByG_F(
		long groupId, java.util.List<Long> folderIds,
		com.liferay.portal.kernel.dao.orm.QueryDefinition
			<com.liferay.document.library.kernel.model.DLFileEntry>
				queryDefinition);

	public int countByG_U_R_F_M(
		long groupId, long userId, java.util.List<Long> repositoryIds,
		java.util.List<Long> folderIds, String[] mimeTypes,
		com.liferay.portal.kernel.dao.orm.QueryDefinition
			<com.liferay.document.library.kernel.model.DLFileEntry>
				queryDefinition);

	public int filterCountByG_F(
		long groupId, java.util.List<Long> folderIds,
		com.liferay.portal.kernel.dao.orm.QueryDefinition
			<com.liferay.document.library.kernel.model.DLFileEntry>
				queryDefinition);

	public int filterCountByG_U_F_M(
		long groupId, long userId, java.util.List<Long> folderIds,
		String[] mimeTypes,
		com.liferay.portal.kernel.dao.orm.QueryDefinition
			<com.liferay.document.library.kernel.model.DLFileEntry>
				queryDefinition);

	public int filterCountByG_U_R_F_M(
		long groupId, long userId, java.util.List<Long> repositoryIds,
		java.util.List<Long> folderIds, String[] mimeTypes,
		com.liferay.portal.kernel.dao.orm.QueryDefinition
			<com.liferay.document.library.kernel.model.DLFileEntry>
				queryDefinition);

	public java.util.List<com.liferay.document.library.kernel.model.DLFileEntry>
		filterFindByG_F(
			long groupId, java.util.List<Long> folderIds,
			com.liferay.portal.kernel.dao.orm.QueryDefinition
				<com.liferay.document.library.kernel.model.DLFileEntry>
					queryDefinition);

	public java.util.List<com.liferay.document.library.kernel.model.DLFileEntry>
		filterFindByG_U_F_M(
			long groupId, long userId, java.util.List<Long> folderIds,
			String[] mimeTypes,
			com.liferay.portal.kernel.dao.orm.QueryDefinition
				<com.liferay.document.library.kernel.model.DLFileEntry>
					queryDefinition);

	public java.util.List<com.liferay.document.library.kernel.model.DLFileEntry>
		filterFindByG_U_R_F_M(
			long groupId, long userId, java.util.List<Long> repositoryIds,
			java.util.List<Long> folderIds, String[] mimeTypes,
			com.liferay.portal.kernel.dao.orm.QueryDefinition
				<com.liferay.document.library.kernel.model.DLFileEntry>
					queryDefinition);

	public java.util.List<com.liferay.document.library.kernel.model.DLFileEntry>
		findByDDMStructureIds(
			long groupId, long[] ddmStructureIds, int start, int end);

	public java.util.List<com.liferay.document.library.kernel.model.DLFileEntry>
		findByDDMStructureIds(long[] ddmStructureIds, int start, int end);

	public java.util.List<com.liferay.document.library.kernel.model.DLFileEntry>
		findByNoAssets();

	public java.util.List<com.liferay.document.library.kernel.model.DLFileEntry>
		findByExtraSettings(int start, int end);

	public java.util.List<com.liferay.document.library.kernel.model.DLFileEntry>
		findByC_T(long classNameId, String treePath);

	public java.util.List<com.liferay.document.library.kernel.model.DLFileEntry>
		findByG_F(
			long groupId, java.util.List<Long> folderIds,
			com.liferay.portal.kernel.dao.orm.QueryDefinition
				<com.liferay.document.library.kernel.model.DLFileEntry>
					queryDefinition);

	public java.util.List<com.liferay.document.library.kernel.model.DLFileEntry>
		findByG_R_F(
			long groupId, java.util.List<Long> repositoryIds,
			java.util.List<Long> folderIds,
			com.liferay.portal.kernel.dao.orm.QueryDefinition
				<com.liferay.document.library.kernel.model.DLFileEntry>
					queryDefinition);

	public java.util.List<com.liferay.document.library.kernel.model.DLFileEntry>
		findByG_U_F(
			long groupId, long userId, java.util.List<Long> folderIds,
			com.liferay.portal.kernel.dao.orm.QueryDefinition
				<com.liferay.document.library.kernel.model.DLFileEntry>
					queryDefinition);

	public java.util.List<com.liferay.document.library.kernel.model.DLFileEntry>
		findByG_U_F_M(
			long groupId, long userId, java.util.List<Long> folderIds,
			String[] mimeTypes,
			com.liferay.portal.kernel.dao.orm.QueryDefinition
				<com.liferay.document.library.kernel.model.DLFileEntry>
					queryDefinition);

	public java.util.List<com.liferay.document.library.kernel.model.DLFileEntry>
		findByG_U_R_F(
			long groupId, long userId, java.util.List<Long> repositoryIds,
			java.util.List<Long> folderIds,
			com.liferay.portal.kernel.dao.orm.QueryDefinition
				<com.liferay.document.library.kernel.model.DLFileEntry>
					queryDefinition);

	public java.util.List<com.liferay.document.library.kernel.model.DLFileEntry>
		findByG_U_R_F_M(
			long groupId, long userId, java.util.List<Long> repositoryIds,
			java.util.List<Long> folderIds, String[] mimeTypes,
			com.liferay.portal.kernel.dao.orm.QueryDefinition
				<com.liferay.document.library.kernel.model.DLFileEntry>
					queryDefinition);

}
// LIFERAY-SERVICE-BUILDER-HASH:-2075059817