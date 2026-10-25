/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service.persistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@ProviderType
public interface JournalFolderFinder {

	public int countF_A_ByG_F_DDMSI(
		long groupId, long folderId, long ddmStructureId,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition);

	public int filterCountF_A_ByG_F_DDMSI(
		long groupId, long folderId, long ddmStructureId,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition);

	public int filterCountF_A_ByG_F_DDMSI_NotS(
		long groupId, long folderId, long ddmStructureId,
		int[] excludedStatuses,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition);

	public java.util.List<Object> filterFindF_A_ByG_F_DDMSI(
		long groupId, long folderId, long ddmStructureId,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition);

	public java.util.List<Object> filterFindF_A_ByG_F_DDMSI_L(
		long groupId, long folderId, long ddmStructureId,
		java.util.Locale locale,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition);

	public java.util.List<Object> filterFindF_A_ByG_F_DDMSI_L_NotS(
		long groupId, long folderId, long ddmStructureId,
		java.util.Locale locale, int[] excludedStatuses,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition);

	public java.util.List<Object> findF_A_ByG_F_DDMSI(
		long groupId, long folderId, long ddmStructureId,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition);

}
// LIFERAY-SERVICE-BUILDER-HASH:1150615527