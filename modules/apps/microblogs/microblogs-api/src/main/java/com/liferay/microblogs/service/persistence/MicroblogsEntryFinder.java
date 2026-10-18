/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.microblogs.service.persistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@ProviderType
public interface MicroblogsEntryFinder {

	public int countByC_U(long companyId, long userId);

	public int countByU_MU(long userId, long microblogsEntryUserId);

	public int countByC_U_ATN(long companyId, long userId, String assetTagName);

	public int countByU_T_MU(long userId, int type, long microblogsEntryUserId);

	public java.util.List<com.liferay.microblogs.model.MicroblogsEntry>
		findByC_U(long companyId, long userId, int start, int end);

	public java.util.List<com.liferay.microblogs.model.MicroblogsEntry>
		findByU_MU(long userId, long microblogsEntryUserId, int start, int end);

	public java.util.List<com.liferay.microblogs.model.MicroblogsEntry>
		findByC_U_ATN(
			long companyId, long userId, String assetTagName, int start,
			int end);

	public java.util.List<com.liferay.microblogs.model.MicroblogsEntry>
		findByU_T_MU(
			long userId, int type, long microblogsEntryUserId, int start,
			int end);

}
// LIFERAY-SERVICE-BUILDER-HASH:-42330980