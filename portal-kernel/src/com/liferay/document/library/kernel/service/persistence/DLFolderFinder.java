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
public interface DLFolderFinder {

	public int countF_FE_FS_ByG_F_M_M(
		long groupId, long folderId, String[] mimeTypes,
		boolean includeMountFolders,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition);

	public int countFE_FS_ByG_F(
		long groupId, long folderId,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition);

	public int filterCountF_FE_FS_ByG_F_M_M(
		long groupId, long folderId, String[] mimeTypes,
		boolean includeMountFolders,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition);

	public int filterCountF_FE_FS_ByG_F_M_FETI_M(
		long groupId, long folderId, String[] mimeTypes, long fileEntryTypeId,
		boolean includeMountFolders,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition);

	public int filterCountFE_FS_ByG_F(
		long groupId, long folderId,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition);

	public int filterCountFE_FS_ByG_F_M(
		long groupId, long folderId, String[] mimeTypes,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition);

	public java.util.List<Object> filterFindF_FE_FS_ByG_F_M_M(
		long groupId, long folderId, String[] mimeTypes,
		boolean includeMountFolders,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition);

	public java.util.List<Object> filterFindF_FE_FS_ByG_F_M_FETI_M(
		long groupId, long folderId, String[] mimeTypes, long fileEntryTypeId,
		boolean includeMountFolders,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition);

	public java.util.List<Object> filterFindFE_FS_ByG_F(
		long groupId, long folderId,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition);

	public java.util.List<com.liferay.document.library.kernel.model.DLFolder>
		findF_ByNoAssets();

	public java.util.List<com.liferay.document.library.kernel.model.DLFolder>
		findF_ByC_T(long classNameId, String treePath);

	public java.util.List<Object> findF_FE_FS_ByG_F_M_M(
		long groupId, long folderId, String[] mimeTypes,
		boolean includeMountFolders,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition);

	public java.util.List<Object> findFE_FS_ByG_F(
		long groupId, long folderId,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition);

}
// LIFERAY-SERVICE-BUILDER-HASH:-694877477