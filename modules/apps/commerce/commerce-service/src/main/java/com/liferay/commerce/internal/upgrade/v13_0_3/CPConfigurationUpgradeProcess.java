/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.upgrade.v13_0_3;

import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPConfigurationEntryLocalService;
import com.liferay.commerce.product.service.CPConfigurationListLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Matyas Wollner
 */
public class CPConfigurationUpgradeProcess extends UpgradeProcess {

	public CPConfigurationUpgradeProcess(
		ClassNameLocalService classNameLocalService,
		CPConfigurationEntryLocalService cpConfigurationEntryLocalService,
		CPConfigurationListLocalService cpConfigurationListLocalService,
		Language language) {

		_classNameLocalService = classNameLocalService;
		_cpConfigurationEntryLocalService = cpConfigurationEntryLocalService;
		_cpConfigurationListLocalService = cpConfigurationListLocalService;
		_language = language;
	}

	@Override
	protected void doUpgrade() throws Exception {
		long cpDefinitionClassNameId = _classNameLocalService.getClassNameId(
			CPDefinition.class);

		try (PreparedStatement configurationListPreparedStatement =
				connection.prepareStatement(
					StringBundler.concat(
						"select CommerceCatalog.catalogDefaultLanguageId, ",
						"CommerceCatalog.name, CommerceCatalog.userId, ",
						"CPConfigurationList.CPConfigurationListId, ",
						"Group_.groupId from CommerceCatalog join Group_ on ",
						"CommerceCatalog.commerceCatalogId = Group_.classPK ",
						"and Group_.classNameId = ? left join ",
						"CPConfigurationList on Group_.groupId = ",
						"CPConfigurationList.groupId and ",
						"CPConfigurationList.master = ?"));
			PreparedStatement configurationEntryPreparedStatement =
				connection.prepareStatement(
					StringBundler.concat(
						"select CPDefinition.CPDefinitionId, ",
						"CPDefinition.CPTaxCategoryId, CPDefinition.depth, ",
						"CPDefinition.height, CPDefinition.freeShipping, ",
						"CPDefinition.shippable, ",
						"CPDefinition.shippingExtraPrice, ",
						"CPDefinition.shipSeparately, CPDefinition.taxExempt, ",
						"CPDefinition.weight, CPDefinition.width, ",
						"CPDefinitionInventory.allowedOrderQuantities, ",
						"CPDefinitionInventory.backOrders, ",
						"CPDefinitionInventory.CPDefinitionInventoryEngine, ",
						"CPDefinitionInventory.displayAvailability, ",
						"CPDefinitionInventory.displayStockQuantity, ",
						"CPDefinitionInventory.lowStockActivity, ",
						"CPDefinitionInventory.maxOrderQuantity, ",
						"CPDefinitionInventory.minOrderQuantity, ",
						"CPDefinitionInventory.minStockQuantity, ",
						"CPDefinitionInventory.multipleOrderQuantity, ",
						"CPDAvailabilityEstimate.",
						"commerceAvailabilityEstimateId from CPDefinition ",
						"join CPDefinitionInventory on ",
						"CPDefinition.CPDefinitionId = ",
						"CPDefinitionInventory.CPDefinitionId left join ",
						"CPConfigurationEntry on CPConfigurationEntry.classPK ",
						"= CPDefinition.CPDefinitionId and ",
						"CPConfigurationEntry.CPConfigurationListId = ? left ",
						"join CPDAvailabilityEstimate on ",
						"CPDefinition.CProductId = ",
						"CPDAvailabilityEstimate.CProductId where ",
						"CPDefinition.groupId = ? and ",
						"CPConfigurationEntry.CPConfigurationListId is ",
						"null"))) {

			configurationListPreparedStatement.setLong(
				1,
				_classNameLocalService.getClassNameId(
					CommerceCatalog.class.getName()));
			configurationListPreparedStatement.setBoolean(2, true);

			ResultSet configurationListResultSet =
				configurationListPreparedStatement.executeQuery();

			while (configurationListResultSet.next()) {
				long cpConfigurationListId = GetterUtil.getLong(
					configurationListResultSet.getLong(
						"CpConfigurationListId"));
				long groupId = configurationListResultSet.getLong("groupId");
				long userId = configurationListResultSet.getLong("userId");

				if (cpConfigurationListId == 0) {
					String catalogDefaultLanguageId =
						configurationListResultSet.getString(
							"catalogDefaultLanguageId");

					Date date = new Date();

					Calendar calendar = CalendarFactoryUtil.getCalendar(
						date.getTime());

					CPConfigurationList cpConfigurationList =
						_cpConfigurationListLocalService.addCPConfigurationList(
							null, userId, groupId, 0, true,
							_language.format(
								LocaleUtil.fromLanguageId(
									catalogDefaultLanguageId),
								"master-configuration-x",
								configurationListResultSet.getString("name"),
								false),
							0D, calendar.get(Calendar.MONTH),
							calendar.get(Calendar.DAY_OF_MONTH),
							calendar.get(Calendar.YEAR),
							calendar.get(Calendar.HOUR_OF_DAY),
							calendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true,
							new ServiceContext());

					cpConfigurationListId =
						cpConfigurationList.getCPConfigurationListId();
				}

				configurationEntryPreparedStatement.setLong(
					1, cpConfigurationListId);
				configurationEntryPreparedStatement.setLong(2, groupId);

				ResultSet configurationEntryResultSet =
					configurationEntryPreparedStatement.executeQuery();

				while (configurationEntryResultSet.next()) {
					_cpConfigurationEntryLocalService.addCPConfigurationEntry(
						null, userId, groupId, cpDefinitionClassNameId,
						configurationEntryResultSet.getLong("CpDefinitionId"),
						cpConfigurationListId,
						configurationEntryResultSet.getLong("CPTaxCategoryId"),
						configurationEntryResultSet.getString(
							"allowedOrderQuantities"),
						configurationEntryResultSet.getBoolean("backOrders"),
						configurationEntryResultSet.getLong(
							"commerceAvailabilityEstimateId"),
						configurationEntryResultSet.getString(
							"CPDefinitionInventoryEngine"),
						configurationEntryResultSet.getDouble("depth"),
						configurationEntryResultSet.getBoolean(
							"displayAvailability"),
						configurationEntryResultSet.getBoolean(
							"displayStockQuantity"),
						configurationEntryResultSet.getBoolean("freeShipping"),
						configurationEntryResultSet.getDouble("height"),
						configurationEntryResultSet.getString(
							"lowStockActivity"),
						configurationEntryResultSet.getBigDecimal(
							"maxOrderQuantity"),
						configurationEntryResultSet.getBigDecimal(
							"minOrderQuantity"),
						configurationEntryResultSet.getBigDecimal(
							"minStockQuantity"),
						configurationEntryResultSet.getBigDecimal(
							"multipleOrderQuantity"),
						true,
						configurationEntryResultSet.getBoolean("shippable"),
						configurationEntryResultSet.getDouble(
							"shippingExtraPrice"),
						configurationEntryResultSet.getBoolean(
							"shipSeparately"),
						configurationEntryResultSet.getBoolean("taxExempt"),
						configurationEntryResultSet.getDouble("weight"),
						configurationEntryResultSet.getDouble("width"));
				}
			}
		}
	}

	private final ClassNameLocalService _classNameLocalService;
	private final CPConfigurationEntryLocalService
		_cpConfigurationEntryLocalService;
	private final CPConfigurationListLocalService
		_cpConfigurationListLocalService;
	private final Language _language;

}