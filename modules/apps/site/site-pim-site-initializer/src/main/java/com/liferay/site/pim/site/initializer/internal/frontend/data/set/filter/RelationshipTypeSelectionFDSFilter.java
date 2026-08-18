/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.frontend.data.set.filter;

import com.liferay.frontend.data.set.filter.BaseSelectionFDSFilter;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.site.pim.site.initializer.internal.constants.PIMFDSNames;
import com.liferay.site.pim.site.initializer.link.PIMLinkTypeRegistry;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(
	property = {
		"frontend.data.set.name=" + PIMFDSNames.PRODUCT_RELATIONSHIPS,
		"service.ranking:Integer=100"
	},
	service = FDSFilter.class
)
public class RelationshipTypeSelectionFDSFilter extends BaseSelectionFDSFilter {

	@Override
	public String getId() {
		return "type";
	}

	@Override
	public String getLabel() {
		return "type";
	}

	@Override
	public List<SelectionFDSFilterItem> getSelectionFDSFilterItems(
		Locale locale) {

		return TransformUtil.transform(
			_pimLinkTypeRegistry.getPIMLinkTypes(),
			pimLinkType -> new SelectionFDSFilterItem(
				pimLinkType.getLabel(locale), pimLinkType.getType()));
	}

	@Reference
	private PIMLinkTypeRegistry _pimLinkTypeRegistry;

}