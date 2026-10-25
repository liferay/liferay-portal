/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.social.kernel.service.persistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@ProviderType
public interface SocialActivitySetFinder {

	public int countByOrganizationId(long organizationId);

	public int countByRelationType(long userId, int type);

	public int countByUser(long userId);

	public int countByUserGroups(long userId);

	public java.util.List<com.liferay.social.kernel.model.SocialActivitySet>
		findByOrganizationId(long organizationId, int start, int end);

	public java.util.List<com.liferay.social.kernel.model.SocialActivitySet>
		findByRelationType(long userId, int type, int start, int end);

	public java.util.List<com.liferay.social.kernel.model.SocialActivitySet>
		findByUser(long userId, int start, int end);

	public java.util.List<com.liferay.social.kernel.model.SocialActivitySet>
		findByUserGroups(long userId, int start, int end);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1335779846