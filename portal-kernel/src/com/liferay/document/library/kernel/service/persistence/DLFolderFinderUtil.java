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
public class DLFolderFinderUtil {

	public static int countF_FE_FS_ByG_F_M_M(
		long groupId, long folderId, String[] mimeTypes,
		boolean includeMountFolders,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition) {

		return getFinder().countF_FE_FS_ByG_F_M_M(
			groupId, folderId, mimeTypes, includeMountFolders, queryDefinition);
	}

	public static int countFE_FS_ByG_F(
		long groupId, long folderId,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition) {

		return getFinder().countFE_FS_ByG_F(groupId, folderId, queryDefinition);
	}

	public static int filterCountF_FE_FS_ByG_F_M_M(
		long groupId, long folderId, String[] mimeTypes,
		boolean includeMountFolders,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition) {

		return getFinder().filterCountF_FE_FS_ByG_F_M_M(
			groupId, folderId, mimeTypes, includeMountFolders, queryDefinition);
	}

	public static int filterCountF_FE_FS_ByG_F_M_FETI_M(
		long groupId, long folderId, String[] mimeTypes, long fileEntryTypeId,
		boolean includeMountFolders,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition) {

		return getFinder().filterCountF_FE_FS_ByG_F_M_FETI_M(
			groupId, folderId, mimeTypes, fileEntryTypeId, includeMountFolders,
			queryDefinition);
	}

	public static int filterCountFE_FS_ByG_F(
		long groupId, long folderId,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition) {

		return getFinder().filterCountFE_FS_ByG_F(
			groupId, folderId, queryDefinition);
	}

	public static int filterCountFE_FS_ByG_F_M(
		long groupId, long folderId, String[] mimeTypes,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition) {

		return getFinder().filterCountFE_FS_ByG_F_M(
			groupId, folderId, mimeTypes, queryDefinition);
	}

	public static java.util.List<Object> filterFindF_FE_FS_ByG_F_M_M(
		long groupId, long folderId, String[] mimeTypes,
		boolean includeMountFolders,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition) {

		return getFinder().filterFindF_FE_FS_ByG_F_M_M(
			groupId, folderId, mimeTypes, includeMountFolders, queryDefinition);
	}

	public static java.util.List<Object> filterFindF_FE_FS_ByG_F_M_FETI_M(
		long groupId, long folderId, String[] mimeTypes, long fileEntryTypeId,
		boolean includeMountFolders,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition) {

		return getFinder().filterFindF_FE_FS_ByG_F_M_FETI_M(
			groupId, folderId, mimeTypes, fileEntryTypeId, includeMountFolders,
			queryDefinition);
	}

	public static java.util.List<Object> filterFindFE_FS_ByG_F(
		long groupId, long folderId,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition) {

		return getFinder().filterFindFE_FS_ByG_F(
			groupId, folderId, queryDefinition);
	}

	public static java.util.List
		<com.liferay.document.library.kernel.model.DLFolder>
			findF_ByNoAssets() {

		return getFinder().findF_ByNoAssets();
	}

	public static java.util.List
		<com.liferay.document.library.kernel.model.DLFolder> findF_ByC_T(
			long classNameId, String treePath) {

		return getFinder().findF_ByC_T(classNameId, treePath);
	}

	public static java.util.List<Object> findF_FE_FS_ByG_F_M_M(
		long groupId, long folderId, String[] mimeTypes,
		boolean includeMountFolders,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition) {

		return getFinder().findF_FE_FS_ByG_F_M_M(
			groupId, folderId, mimeTypes, includeMountFolders, queryDefinition);
	}

	public static java.util.List<Object> findFE_FS_ByG_F(
		long groupId, long folderId,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition) {

		return getFinder().findFE_FS_ByG_F(groupId, folderId, queryDefinition);
	}

	public static DLFolderFinder getFinder() {
		if (_finder == null) {
			_finder = (DLFolderFinder)PortalBeanLocatorUtil.locate(
				DLFolderFinder.class.getName());
		}

		return _finder;
	}

	public void setFinder(DLFolderFinder finder) {
		_finder = finder;
	}

	private static DLFolderFinder _finder;

}
// LIFERAY-SERVICE-BUILDER-HASH:1027338108