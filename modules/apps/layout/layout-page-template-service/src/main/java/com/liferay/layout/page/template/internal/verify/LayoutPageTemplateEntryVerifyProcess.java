/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.verify;

import com.liferay.info.item.InfoItemFormVariation;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFormVariationsProvider;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;
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
		updateClassTypeKey();
	}

	protected void updateClassTypeKey() throws PortalException {
		ActionableDynamicQuery layoutPageTemplateEntryActionableDynamicQuery =
			_layoutPageTemplateEntryLocalService.getActionableDynamicQuery();

		layoutPageTemplateEntryActionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> dynamicQuery.add(
				RestrictionsFactoryUtil.eq(
					"type",
					LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE)));
		layoutPageTemplateEntryActionableDynamicQuery.setPerformActionMethod(
			(ActionableDynamicQuery.PerformActionMethod
				<LayoutPageTemplateEntry>)layoutPageTemplateEntry -> {
					if ((layoutPageTemplateEntry.getClassNameId() == 0) ||
						Validator.isNotNull(
							layoutPageTemplateEntry.getClassTypeKey())) {

						return;
					}

					InfoItemFormVariationsProvider<?>
						infoItemFormVariationsProvider =
							_infoItemServiceRegistry.getFirstInfoItemService(
								InfoItemFormVariationsProvider.class,
								layoutPageTemplateEntry.getClassName());

					if (infoItemFormVariationsProvider == null) {
						if ((layoutPageTemplateEntry.getClassTypeId() > 0) &&
							_log.isWarnEnabled()) {

							_log.warn(
								StringBundler.concat(
									"Unable to update the class type key for ",
									"layout page template entry ",
									layoutPageTemplateEntry.
										getLayoutPageTemplateEntryId(),
									" because no info item form variations ",
									"provider is registered for ",
									layoutPageTemplateEntry.getClassName()));
						}

						return;
					}

					InfoItemFormVariation infoItemFormVariation =
						infoItemFormVariationsProvider.getInfoItemFormVariation(
							layoutPageTemplateEntry.getGroupId(),
							layoutPageTemplateEntry.getClassTypeKey(),
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

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutPageTemplateEntryVerifyProcess.class);

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

}