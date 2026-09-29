/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.contributor;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.ServiceContext;

/**
 * @author Stefano Motta
 */
public interface CMSObjectEntryFormContributor {

	public void contribute(
			Layout layout, ObjectDefinition objectDefinition,
			ServiceContext serviceContext)
		throws Exception;

}