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
public interface MBCategoryFinder {

	public int countC_T_ByG_C(
		long groupId, long categoryId,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition);

	public int filterCountC_ByG_P(
		long groupId, long parentCategoryId,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition);

	public int filterCountC_ByS_G_U_P(
		long groupId, long userId, long[] parentCategoryIds,
		com.liferay.portal.kernel.dao.orm.QueryDefinition
			<com.liferay.message.boards.model.MBCategory> queryDefinition);

	public int filterCountC_T_ByG_C(
		long groupId, long categoryId,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition);

	public java.util.List<com.liferay.message.boards.model.MBCategory>
		filterFindC_ByG_P(
			long groupId, long parentCategoryId,
			com.liferay.portal.kernel.dao.orm.QueryDefinition
				<com.liferay.message.boards.model.MBCategory> queryDefinition);

	public java.util.List<com.liferay.message.boards.model.MBCategory>
		filterFindC_ByS_G_U_P(
			long groupId, long userId, long[] parentCategoryIds,
			com.liferay.portal.kernel.dao.orm.QueryDefinition
				<com.liferay.message.boards.model.MBCategory> queryDefinition);

	public java.util.List<Object> filterFindC_T_ByG_C(
		long groupId, long categoryId,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition);

	public java.util.List<Object> findC_T_ByG_C(
		long groupId, long categoryId,
		com.liferay.portal.kernel.dao.orm.QueryDefinition<?> queryDefinition);

}
// LIFERAY-SERVICE-BUILDER-HASH:1702963473