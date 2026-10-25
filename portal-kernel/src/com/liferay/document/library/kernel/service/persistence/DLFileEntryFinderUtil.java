/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.kernel.service.persistence;

import com.liferay.portal.kernel.bean.PortalBeanLocatorUtil;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
public class DLFileEntryFinderUtil {

	public static int countByExtraSettings() {
		return getFinder().countByExtraSettings();
	}

	public static int countByG_F(
		long groupId, java.util.List<Long> folderIds,
		com.liferay.portal.kernel.dao.orm.QueryDefinition
			<com.liferay.document.library.kernel.model.DLFileEntry>
				queryDefinition) {

		return getFinder().countByG_F(groupId, folderIds, queryDefinition);
	}

	public static int countByG_U_R_F_M(
		long groupId, long userId, java.util.List<Long> repositoryIds,
		java.util.List<Long> folderIds, String[] mimeTypes,
		com.liferay.portal.kernel.dao.orm.QueryDefinition
			<com.liferay.document.library.kernel.model.DLFileEntry>
				queryDefinition) {

		return getFinder().countByG_U_R_F_M(
			groupId, userId, repositoryIds, folderIds, mimeTypes,
			queryDefinition);
	}

	public static int filterCountByG_F(
		long groupId, java.util.List<Long> folderIds,
		com.liferay.portal.kernel.dao.orm.QueryDefinition
			<com.liferay.document.library.kernel.model.DLFileEntry>
				queryDefinition) {

		return getFinder().filterCountByG_F(
			groupId, folderIds, queryDefinition);
	}

	public static int filterCountByG_U_F_M(
		long groupId, long userId, java.util.List<Long> folderIds,
		String[] mimeTypes,
		com.liferay.portal.kernel.dao.orm.QueryDefinition
			<com.liferay.document.library.kernel.model.DLFileEntry>
				queryDefinition) {

		return getFinder().filterCountByG_U_F_M(
			groupId, userId, folderIds, mimeTypes, queryDefinition);
	}

	public static int filterCountByG_U_R_F_M(
		long groupId, long userId, java.util.List<Long> repositoryIds,
		java.util.List<Long> folderIds, String[] mimeTypes,
		com.liferay.portal.kernel.dao.orm.QueryDefinition
			<com.liferay.document.library.kernel.model.DLFileEntry>
				queryDefinition) {

		return getFinder().filterCountByG_U_R_F_M(
			groupId, userId, repositoryIds, folderIds, mimeTypes,
			queryDefinition);
	}

	public static java.util.List
		<com.liferay.document.library.kernel.model.DLFileEntry> filterFindByG_F(
			long groupId, java.util.List<Long> folderIds,
			com.liferay.portal.kernel.dao.orm.QueryDefinition
				<com.liferay.document.library.kernel.model.DLFileEntry>
					queryDefinition) {

		return getFinder().filterFindByG_F(groupId, folderIds, queryDefinition);
	}

	public static java.util.List
		<com.liferay.document.library.kernel.model.DLFileEntry>
			filterFindByG_U_F_M(
				long groupId, long userId, java.util.List<Long> folderIds,
				String[] mimeTypes,
				com.liferay.portal.kernel.dao.orm.QueryDefinition
					<com.liferay.document.library.kernel.model.DLFileEntry>
						queryDefinition) {

		return getFinder().filterFindByG_U_F_M(
			groupId, userId, folderIds, mimeTypes, queryDefinition);
	}

	public static java.util.List
		<com.liferay.document.library.kernel.model.DLFileEntry>
			filterFindByG_U_R_F_M(
				long groupId, long userId, java.util.List<Long> repositoryIds,
				java.util.List<Long> folderIds, String[] mimeTypes,
				com.liferay.portal.kernel.dao.orm.QueryDefinition
					<com.liferay.document.library.kernel.model.DLFileEntry>
						queryDefinition) {

		return getFinder().filterFindByG_U_R_F_M(
			groupId, userId, repositoryIds, folderIds, mimeTypes,
			queryDefinition);
	}

	public static java.util.List
		<com.liferay.document.library.kernel.model.DLFileEntry>
			findByDDMStructureIds(
				long groupId, long[] ddmStructureIds, int start, int end) {

		return getFinder().findByDDMStructureIds(
			groupId, ddmStructureIds, start, end);
	}

	public static java.util.List
		<com.liferay.document.library.kernel.model.DLFileEntry>
			findByDDMStructureIds(long[] ddmStructureIds, int start, int end) {

		return getFinder().findByDDMStructureIds(ddmStructureIds, start, end);
	}

	public static java.util.List
		<com.liferay.document.library.kernel.model.DLFileEntry>
			findByNoAssets() {

		return getFinder().findByNoAssets();
	}

	public static java.util.List
		<com.liferay.document.library.kernel.model.DLFileEntry>
			findByExtraSettings(int start, int end) {

		return getFinder().findByExtraSettings(start, end);
	}

	public static java.util.List
		<com.liferay.document.library.kernel.model.DLFileEntry> findByC_T(
			long classNameId, String treePath) {

		return getFinder().findByC_T(classNameId, treePath);
	}

	public static java.util.List
		<com.liferay.document.library.kernel.model.DLFileEntry> findByG_F(
			long groupId, java.util.List<Long> folderIds,
			com.liferay.portal.kernel.dao.orm.QueryDefinition
				<com.liferay.document.library.kernel.model.DLFileEntry>
					queryDefinition) {

		return getFinder().findByG_F(groupId, folderIds, queryDefinition);
	}

	public static java.util.List
		<com.liferay.document.library.kernel.model.DLFileEntry> findByG_R_F(
			long groupId, java.util.List<Long> repositoryIds,
			java.util.List<Long> folderIds,
			com.liferay.portal.kernel.dao.orm.QueryDefinition
				<com.liferay.document.library.kernel.model.DLFileEntry>
					queryDefinition) {

		return getFinder().findByG_R_F(
			groupId, repositoryIds, folderIds, queryDefinition);
	}

	public static java.util.List
		<com.liferay.document.library.kernel.model.DLFileEntry> findByG_U_F(
			long groupId, long userId, java.util.List<Long> folderIds,
			com.liferay.portal.kernel.dao.orm.QueryDefinition
				<com.liferay.document.library.kernel.model.DLFileEntry>
					queryDefinition) {

		return getFinder().findByG_U_F(
			groupId, userId, folderIds, queryDefinition);
	}

	public static java.util.List
		<com.liferay.document.library.kernel.model.DLFileEntry> findByG_U_F_M(
			long groupId, long userId, java.util.List<Long> folderIds,
			String[] mimeTypes,
			com.liferay.portal.kernel.dao.orm.QueryDefinition
				<com.liferay.document.library.kernel.model.DLFileEntry>
					queryDefinition) {

		return getFinder().findByG_U_F_M(
			groupId, userId, folderIds, mimeTypes, queryDefinition);
	}

	public static java.util.List
		<com.liferay.document.library.kernel.model.DLFileEntry> findByG_U_R_F(
			long groupId, long userId, java.util.List<Long> repositoryIds,
			java.util.List<Long> folderIds,
			com.liferay.portal.kernel.dao.orm.QueryDefinition
				<com.liferay.document.library.kernel.model.DLFileEntry>
					queryDefinition) {

		return getFinder().findByG_U_R_F(
			groupId, userId, repositoryIds, folderIds, queryDefinition);
	}

	public static java.util.List
		<com.liferay.document.library.kernel.model.DLFileEntry> findByG_U_R_F_M(
			long groupId, long userId, java.util.List<Long> repositoryIds,
			java.util.List<Long> folderIds, String[] mimeTypes,
			com.liferay.portal.kernel.dao.orm.QueryDefinition
				<com.liferay.document.library.kernel.model.DLFileEntry>
					queryDefinition) {

		return getFinder().findByG_U_R_F_M(
			groupId, userId, repositoryIds, folderIds, mimeTypes,
			queryDefinition);
	}

	public static DLFileEntryFinder getFinder() {
		if (_finder == null) {
			_finder = (DLFileEntryFinder)PortalBeanLocatorUtil.locate(
				DLFileEntryFinder.class.getName());
		}

		return _finder;
	}

	public void setFinder(DLFileEntryFinder finder) {
		_finder = finder;
	}

	private static DLFileEntryFinder _finder;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1009675071