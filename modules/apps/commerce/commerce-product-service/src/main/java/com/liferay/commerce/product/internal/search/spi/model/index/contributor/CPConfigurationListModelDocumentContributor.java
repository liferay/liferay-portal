/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.internal.search.spi.model.index.contributor;

import com.liferay.commerce.product.constants.CPField;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CPConfigurationListRel;
import com.liferay.commerce.product.model.CommerceChannelRel;
import com.liferay.commerce.product.service.CPConfigurationListRelLocalService;
import com.liferay.commerce.product.service.CommerceChannelRelService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = "indexer.class.name=com.liferay.commerce.product.model.CPConfigurationList",
	service = ModelDocumentContributor.class
)
public class CPConfigurationListModelDocumentContributor
	implements ModelDocumentContributor<CPConfigurationList> {

	@Override
	public void contribute(
		Document document, CPConfigurationList cpConfigurationList) {

		try {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Indexing commerce product configuration list " +
						cpConfigurationList);
			}

			document.addKeyword(
				CPField.CP_CONFIGURATION_LIST_ID,
				cpConfigurationList.getCPConfigurationListId());
			document.addKeyword(
				CPField.EXTERNAL_REFERENCE_CODE,
				cpConfigurationList.getExternalReferenceCode(), true);
			document.addKeyword(
				Field.GROUP_ID, cpConfigurationList.getGroupId());
			document.addText(Field.NAME, cpConfigurationList.getName());
			document.addNumber(
				Field.PRIORITY, cpConfigurationList.getPriority());
			document.addNumber(
				"commerceAccountGroupIds",
				TransformUtil.transformToLongArray(
					_cpConfigurationListRelLocalService.
						getAccountGroupCPConfigurationListRels(
							cpConfigurationList.getCPConfigurationListId(),
							null, QueryUtil.ALL_POS, QueryUtil.ALL_POS),
					CPConfigurationListRel::getClassPK));
			document.addNumber(
				"commerceAccountId",
				TransformUtil.transformToLongArray(
					_cpConfigurationListRelLocalService.
						getAccountEntryCPConfigurationListRels(
							cpConfigurationList.getCPConfigurationListId(),
							null, QueryUtil.ALL_POS, QueryUtil.ALL_POS),
					CPConfigurationListRel::getClassPK));
			document.addNumber(
				"commerceChannelId",
				TransformUtil.transformToLongArray(
					_commerceChannelRelService.getCommerceChannelRels(
						CPConfigurationList.class.getName(),
						cpConfigurationList.getCPConfigurationListId(), null,
						QueryUtil.ALL_POS, QueryUtil.ALL_POS),
					CommerceChannelRel::getCommerceChannelId));
			document.addNumber(
				"commerceOrderTypeId",
				TransformUtil.transformToLongArray(
					_cpConfigurationListRelLocalService.
						getCommerceOrderTypeCPConfigurationListRels(
							cpConfigurationList.getCPConfigurationListId(),
							null, QueryUtil.ALL_POS, QueryUtil.ALL_POS),
					CPConfigurationListRel::getClassPK));

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Commerce product configuration list " +
						cpConfigurationList + " indexed successfully");
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to index commerce product configuration list" +
						cpConfigurationList,
					exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPConfigurationListModelDocumentContributor.class);

	@Reference
	private CommerceChannelRelService _commerceChannelRelService;

	@Reference
	private CPConfigurationListRelLocalService
		_cpConfigurationListRelLocalService;

}