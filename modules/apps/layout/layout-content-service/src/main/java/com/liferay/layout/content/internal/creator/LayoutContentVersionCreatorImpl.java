/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.internal.creator;

import com.liferay.layout.content.creator.LayoutContentVersionCreator;
import com.liferay.layout.content.provider.LayoutContentVersionDataProvider;
import com.liferay.layout.content.service.LayoutContentVersionLocalService;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryLocalService;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.servlet.InitialRequestSyncUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(service = LayoutContentVersionCreator.class)
public class LayoutContentVersionCreatorImpl
	implements LayoutContentVersionCreator {

	@Override
	public void createLayoutContentVersion(Layout layout) {
		try {
			if (!CTCollectionThreadLocal.isProductionMode() ||
				!FeatureFlagManagerUtil.isEnabled(
					layout.getCompanyId(), "LPD-10622") ||
				!InitialRequestSyncUtil.isSynced() || !layout.isDraftLayout() ||
				!layout.isTypeContent()) {

				return;
			}

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_layoutPageTemplateEntryLocalService.
					fetchLayoutPageTemplateEntryByPlid(layout.getClassPK());

			if (layoutPageTemplateEntry != null) {
				return;
			}

			LayoutUtilityPageEntry layoutUtilityPageEntry =
				_layoutUtilityPageEntryLocalService.
					fetchLayoutUtilityPageEntryByPlid(layout.getClassPK());

			if (layoutUtilityPageEntry != null) {
				return;
			}

			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			if (serviceContext == null) {
				serviceContext = new ServiceContext();

				serviceContext.setCompanyId(layout.getCompanyId());
				serviceContext.setUserId(layout.getUserId());
			}

			_layoutContentVersionLocalService.addLayoutContentVersion(
				null, layout.getUserId(),
				_layoutContentVersionDataProvider.getLayoutContentVersionData(
					layout, serviceContext),
				layout.getNameMap(), layout.getPlid(),
				WorkflowConstants.STATUS_APPROVED);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to add layout content version for plid " +
					layout.getPlid(),
				exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutContentVersionCreatorImpl.class);

	@Reference
	private LayoutContentVersionDataProvider _layoutContentVersionDataProvider;

	@Reference
	private LayoutContentVersionLocalService _layoutContentVersionLocalService;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private LayoutUtilityPageEntryLocalService
		_layoutUtilityPageEntryLocalService;

}