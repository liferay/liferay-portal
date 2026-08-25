/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.model.listener;

import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(service = ModelListener.class)
public class GroupModelListener extends BaseModelListener<Group> {

	@Override
	public void onBeforeRemove(Group group) {
		long groupId = group.getGroupId();

		try {
			CommerceChannel commerceChannel =
				_commerceChannelLocalService.fetchCommerceChannelBySiteGroupId(
					groupId);

			if (commerceChannel != null) {
				_commerceChannelLocalService.updateCommerceChannel(
					commerceChannel.getCommerceChannelId(),
					commerceChannel.getAccountEntryId(),
					GroupConstants.DEFAULT_PARENT_GROUP_ID,
					commerceChannel.getName(), commerceChannel.getType(),
					commerceChannel.getTypeSettingsUnicodeProperties(),
					commerceChannel.getCommerceCurrencyCode(),
					commerceChannel.getPriceDisplayType(),
					commerceChannel.isDiscountsTargetNetPrice());
			}

			CommerceCatalog commerceCatalog =
				_commerceCatalogLocalService.fetchCommerceCatalogByGroupId(
					groupId);

			if ((commerceCatalog != null) && !commerceCatalog.isSystem()) {
				for (CPDefinition cpDefinition :
						_cpDefinitionLocalService.getCPDefinitions(
							groupId, WorkflowConstants.STATUS_ANY,
							QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

					_cpDefinitionLocalService.deleteCPDefinition(cpDefinition);
				}

				_commerceCatalogLocalService.deleteCommerceCatalog(
					commerceCatalog);
			}
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GroupModelListener.class);

	@Reference
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

}