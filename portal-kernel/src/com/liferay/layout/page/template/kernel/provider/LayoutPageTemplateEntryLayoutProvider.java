/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.kernel.provider;

import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutPrototype;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Alberto Chaparro
 */
@ProviderType
public interface LayoutPageTemplateEntryLayoutProvider {

	public Layout getLayoutPageTemplateEntryLayout(
		long companyId, String externalReferenceCode, long groupId,
		String layoutPageTemplateEntryScopeERC, long plid);

	public LayoutPrototype getLayoutPageTemplateEntryLayoutPrototype(
		long companyId, String externalReferenceCode,
		String layoutPageTemplateEntryScopeERC, long scopeGroupId);

}