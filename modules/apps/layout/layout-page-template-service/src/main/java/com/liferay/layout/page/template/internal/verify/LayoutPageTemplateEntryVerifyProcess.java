/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.verify;

import com.liferay.info.item.InfoItemFormVariation;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormVariationsProvider;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.verify.VerifyProcess;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mikel Lorza
 */
@Component(service = VerifyProcess.class)
public class LayoutPageTemplateEntryVerifyProcess extends VerifyProcess {

	@Override
	protected void doVerify() throws Exception {
		if (!FeatureFlagManagerUtil.isEnabled(
				CompanyThreadLocal.getCompanyId(), "LPD-35443")) {

			return;
		}

		updateClassTypeKey();
	}

	protected void updateClassTypeKey() throws PortalException {
		ActionableDynamicQuery layoutPageTemplateEntryActionableDynamicQuery =
			_layoutPageTemplateEntryLocalService.getActionableDynamicQuery();

		layoutPageTemplateEntryActionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> dynamicQuery.add(
				RestrictionsFactoryUtil.and(
					RestrictionsFactoryUtil.isNotNull("classTypeId"),
					RestrictionsFactoryUtil.isNull("classTypeKey"))));

		layoutPageTemplateEntryActionableDynamicQuery.setPerformActionMethod(
			(ActionableDynamicQuery.PerformActionMethod
				<LayoutPageTemplateEntry>)layoutPageTemplateEntry -> {
					InfoItemFormVariationsProvider<?>
						infoItemFormVariationsProvider =
							_infoItemServiceRegistry.getFirstInfoItemService(
								InfoItemFormVariationsProvider.class,
								layoutPageTemplateEntry.getClassName());

					if (infoItemFormVariationsProvider == null) {
						return;
					}

					InfoItemFormVariation infoItemFormVariation =
						infoItemFormVariationsProvider.getInfoItemFormVariation(
							layoutPageTemplateEntry.getGroupId(),
							String.valueOf(
								layoutPageTemplateEntry.getClassTypeId()));

					if (infoItemFormVariation == null) {
						return;
					}

					layoutPageTemplateEntry.setClassTypeKey(
						infoItemFormVariation.getExternalReferenceCode());

					_layoutPageTemplateEntryLocalService.
						updateLayoutPageTemplateEntry(layoutPageTemplateEntry);
				});

		layoutPageTemplateEntryActionableDynamicQuery.performActions();
	}

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

}