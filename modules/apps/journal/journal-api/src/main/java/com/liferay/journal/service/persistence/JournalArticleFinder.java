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
public interface JournalArticleFinder {

	public int countByG_F(
		long groupId, java.util.List<Long> folderIds,
		com.liferay.portal.kernel.dao.orm.QueryDefinition
			<com.liferay.journal.model.JournalArticle> queryDefinition);

	public int filterCountByG_F(
		long groupId, java.util.List<Long> folderIds,
		com.liferay.portal.kernel.dao.orm.QueryDefinition
			<com.liferay.journal.model.JournalArticle> queryDefinition);

	public int filterCountByG_ST(
		long groupId, int status,
		com.liferay.portal.kernel.dao.orm.QueryDefinition
			<com.liferay.journal.model.JournalArticle> queryDefinition);

	public int filterCountByG_F_C(
		long groupId, java.util.List<Long> folderIds, long classNameId,
		com.liferay.portal.kernel.dao.orm.QueryDefinition
			<com.liferay.journal.model.JournalArticle> queryDefinition);

	public int filterCountByG_F_C_S(
		long groupId, java.util.List<Long> folderIds, long classNameId,
		long ddmStructureId,
		com.liferay.portal.kernel.dao.orm.QueryDefinition
			<com.liferay.journal.model.JournalArticle> queryDefinition);

	public java.util.List<com.liferay.journal.model.JournalArticle>
		filterFindByG_ST_L(
			long groupId, int status, java.util.Locale locale,
			com.liferay.portal.kernel.dao.orm.QueryDefinition
				<com.liferay.journal.model.JournalArticle> queryDefinition);

	public java.util.List<com.liferay.journal.model.JournalArticle>
		filterFindByG_F_L(
			long groupId, java.util.List<Long> folderIds,
			java.util.Locale locale,
			com.liferay.portal.kernel.dao.orm.QueryDefinition
				<com.liferay.journal.model.JournalArticle> queryDefinition);

	public java.util.List<com.liferay.journal.model.JournalArticle>
		filterFindByG_F_C_L(
			long groupId, java.util.List<Long> folderIds, long classNameId,
			java.util.Locale locale,
			com.liferay.portal.kernel.dao.orm.QueryDefinition
				<com.liferay.journal.model.JournalArticle> queryDefinition);

	public java.util.List<com.liferay.journal.model.JournalArticle>
		filterFindByG_F_C_S_L(
			long groupId, java.util.List<Long> folderIds, long classNameId,
			long ddmStructureId, java.util.Locale locale,
			com.liferay.portal.kernel.dao.orm.QueryDefinition
				<com.liferay.journal.model.JournalArticle> queryDefinition);

	public java.util.List<com.liferay.journal.model.JournalArticle>
		findByG_F_C_S_L(
			long groupId, java.util.List<Long> folderIds, long classNameId,
			long ddmStructureId, java.util.Locale locale,
			com.liferay.portal.kernel.dao.orm.QueryDefinition
				<com.liferay.journal.model.JournalArticle> queryDefinition);

}
// LIFERAY-SERVICE-BUILDER-HASH:-544120426