/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter;

import com.liferay.commerce.stock.activity.CommerceLowStockActivity;
import com.liferay.commerce.stock.activity.CommerceLowStockActivityRegistry;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.LowStockAction;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Danny Situ
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.commerce.stock.activity.CommerceLowStockActivity"
	},
	service = DTOConverter.class
)
public class LowStockActionDTOConverter
	implements DTOConverter<CommerceLowStockActivity, LowStockAction> {

	@Override
	public String getContentType() {
		return LowStockAction.class.getSimpleName();
	}

	@Override
	public LowStockAction toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CommerceLowStockActivity commerceLowStockActivity =
			_commerceLowStockActivityRegistry.getCommerceLowStockActivity(
				(String)dtoConverterContext.getId());

		return new LowStockAction() {
			{
				setKey(commerceLowStockActivity::getKey);
				setLabel(
					() -> LanguageUtils.getLanguageIdMap(
						commerceLowStockActivity.getLabelMap()));
			}
		};
	}

	@Reference
	private CommerceLowStockActivityRegistry _commerceLowStockActivityRegistry;

}