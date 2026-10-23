/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.service.persistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@ProviderType
public interface WikiPageFinder {

	public int countByModifiedDate(
		long groupId, long nodeId, java.util.Date modifiedDate, boolean before);

	public int filterCountByG_N_H_S(
		long groupId, long nodeId, boolean head,
		com.liferay.portal.kernel.dao.orm.QueryDefinition
			<com.liferay.wiki.model.WikiPage> queryDefinition);

	public java.util.List<com.liferay.wiki.model.WikiPage> filterFindByG_N_H_S(
		long groupId, long nodeId, boolean head,
		com.liferay.portal.kernel.dao.orm.QueryDefinition
			<com.liferay.wiki.model.WikiPage> queryDefinition);

	public java.util.List<com.liferay.wiki.model.WikiPage> findByModifiedDate(
		long groupId, long nodeId, java.sql.Timestamp modifiedDate,
		boolean before, int start, int end);

}
// LIFERAY-SERVICE-BUILDER-HASH:648800648