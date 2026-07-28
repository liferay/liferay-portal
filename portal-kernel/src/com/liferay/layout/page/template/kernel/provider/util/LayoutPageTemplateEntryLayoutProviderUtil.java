/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.kernel.provider.util;

import com.liferay.layout.page.template.kernel.provider.LayoutPageTemplateEntryLayoutProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.module.service.Snapshot;

/**
 * @author Alberto Chaparro
 */
public class LayoutPageTemplateEntryLayoutProviderUtil {

	public static Layout getLayoutPageTemplateEntryLayout(
		long groupId, String externalReferenceCode, long plid) {

		LayoutPageTemplateEntryLayoutProvider
			layoutPageTemplateEntryLayoutProvider =
				_getLayoutPageTemplateEntryLayoutProvider();

		if (layoutPageTemplateEntryLayoutProvider == null) {
			return null;
		}

		return layoutPageTemplateEntryLayoutProvider.
			getLayoutPageTemplateEntryLayout(
				groupId, externalReferenceCode, plid);
	}

	public static LayoutPrototype getLayoutPageTemplateEntryLayoutPrototype(
		long companyId, String externalReferenceCode,
		String layoutPageTemplateEntryScopeERC, long scopeGroupId) {

		LayoutPageTemplateEntryLayoutProvider
			layoutPageTemplateEntryLayoutProvider =
				_getLayoutPageTemplateEntryLayoutProvider();

		if (layoutPageTemplateEntryLayoutProvider == null) {
			return null;
		}

		return layoutPageTemplateEntryLayoutProvider.
			getLayoutPageTemplateEntryLayoutPrototype(
				companyId, externalReferenceCode,
				layoutPageTemplateEntryScopeERC, scopeGroupId);
	}

	private static LayoutPageTemplateEntryLayoutProvider
		_getLayoutPageTemplateEntryLayoutProvider() {

		LayoutPageTemplateEntryLayoutProvider
			layoutPageTemplateEntryLayoutProvider =
				_layoutPageTemplateEntryLayoutProviderSnapshot.get();

		if ((layoutPageTemplateEntryLayoutProvider == null) &&
			_log.isDebugEnabled()) {

			_log.debug("Layout page template entry layout provider is null");
		}

		return layoutPageTemplateEntryLayoutProvider;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutPageTemplateEntryLayoutProviderUtil.class);

	private static final Snapshot<LayoutPageTemplateEntryLayoutProvider>
		_layoutPageTemplateEntryLayoutProviderSnapshot = new Snapshot<>(
			LayoutPageTemplateEntryLayoutProviderUtil.class,
			LayoutPageTemplateEntryLayoutProvider.class);

}