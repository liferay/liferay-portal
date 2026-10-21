/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.provider;

import com.liferay.design.library.util.DesignLibraryUtil;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.kernel.provider.LayoutPageTemplateEntryLayoutProvider;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutPrototypeLocalService;
import com.liferay.portal.kernel.util.ScopeUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alberto Chaparro
 */
@Component(service = LayoutPageTemplateEntryLayoutProvider.class)
public class LayoutPageTemplateEntryLayoutProviderImpl
	implements LayoutPageTemplateEntryLayoutProvider {

	@Override
	public Layout getLayoutPageTemplateEntryLayout(
		long companyId, String externalReferenceCode, long groupId,
		String layoutPageTemplateEntryScopeERC, long plid) {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByPlid(plid);
		long scopeGroupId = groupId;

		if ((layoutPageTemplateEntry != null) &&
			(layoutPageTemplateEntry.getType() ==
				LayoutPageTemplateEntryTypeConstants.WIDGET_PAGE)) {

			scopeGroupId = layoutPageTemplateEntry.getGroupId();
		}

		Long itemGroupId = ScopeUtil.getItemGroupId(
			companyId, layoutPageTemplateEntryScopeERC, scopeGroupId);

		if ((itemGroupId == null) ||
			((itemGroupId != scopeGroupId) &&
			 !DesignLibraryUtil.isConnectedDesignLibraryGroupId(
				 itemGroupId, scopeGroupId))) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Unable to find master page with external reference ",
						"code ", externalReferenceCode,
						" in a design library with external reference code ",
						layoutPageTemplateEntryScopeERC, " connected to group ",
						scopeGroupId));
			}

			return null;
		}

		return _fetchLayoutPageTemplateEntryLayout(
			externalReferenceCode, itemGroupId);
	}

	@Override
	public LayoutPrototype getLayoutPageTemplateEntryLayoutPrototype(
		long companyId, String externalReferenceCode,
		String layoutPageTemplateEntryScopeERC, long scopeGroupId) {

		Long itemGroupId = ScopeUtil.getItemGroupId(
			companyId, layoutPageTemplateEntryScopeERC, scopeGroupId);

		if (itemGroupId == null) {
			return null;
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					externalReferenceCode, itemGroupId);

		if (layoutPageTemplateEntry != null) {
			return _layoutPrototypeLocalService.fetchLayoutPrototype(
				layoutPageTemplateEntry.getLayoutPrototypeId());
		}

		return null;
	}

	private Layout _fetchLayoutPageTemplateEntryLayout(
		String externalReferenceCode, long groupId) {

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					externalReferenceCode, groupId);

		if (layoutPageTemplateEntry == null) {
			return null;
		}

		return _layoutLocalService.fetchLayout(
			layoutPageTemplateEntry.getPlid());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutPageTemplateEntryLayoutProviderImpl.class);

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private LayoutPrototypeLocalService _layoutPrototypeLocalService;

}