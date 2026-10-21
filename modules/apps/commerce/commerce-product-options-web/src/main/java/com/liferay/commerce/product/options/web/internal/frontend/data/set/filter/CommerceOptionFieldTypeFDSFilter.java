/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.options.web.internal.frontend.data.set.filter;

import com.liferay.commerce.product.configuration.CPOptionConfiguration;
import com.liferay.commerce.product.option.CommerceOptionType;
import com.liferay.commerce.product.option.CommerceOptionTypeRegistry;
import com.liferay.commerce.product.options.web.internal.constants.CommerceOptionFDSNames;
import com.liferay.commerce.product.util.CommerceOptionTypeUtil;
import com.liferay.frontend.data.set.filter.BaseSelectionFDSFilter;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = "frontend.data.set.name=" + CommerceOptionFDSNames.OPTIONS,
	service = FDSFilter.class
)
public class CommerceOptionFieldTypeFDSFilter extends BaseSelectionFDSFilter {

	public List<CommerceOptionType> getCommerceOptionTypes() {
		List<CommerceOptionType> commerceOptionTypes =
			_commerceOptionTypeRegistry.getCommerceOptionTypes();

		CPOptionConfiguration cpOptionConfiguration = null;

		try {
			cpOptionConfiguration =
				_configurationProvider.getSystemConfiguration(
					CPOptionConfiguration.class);
		}
		catch (ConfigurationException configurationException) {
			_log.error(configurationException);
		}

		String[] allowedCommerceOptionTypes =
			cpOptionConfiguration.allowedCommerceOptionTypes();

		return CommerceOptionTypeUtil.getAllowedCommerceOptionTypes(
			commerceOptionTypes, allowedCommerceOptionTypes);
	}

	@Override
	public String getId() {
		return "fieldType";
	}

	@Override
	public String getLabel() {
		return "type";
	}

	@Override
	public List<SelectionFDSFilterItem> getSelectionFDSFilterItems(
		Locale locale) {

		List<SelectionFDSFilterItem> selectionFDSFilterItems =
			new ArrayList<>();

		for (CommerceOptionType commerceOptionType : getCommerceOptionTypes()) {
			selectionFDSFilterItems.add(
				new SelectionFDSFilterItem(
					commerceOptionType.getLabel(locale),
					commerceOptionType.getKey()));
		}

		return selectionFDSFilterItems;
	}

	@Override
	public boolean isMultiple() {
		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceOptionFieldTypeFDSFilter.class);

	@Reference
	private CommerceOptionTypeRegistry _commerceOptionTypeRegistry;

	@Reference
	private ConfigurationProvider _configurationProvider;

}