/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.sample.web.internal.frontend.data.set.action;

import com.liferay.frontend.data.set.action.FDSItemsActions;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.sample.web.internal.constants.FDSSampleFDSNames;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Miguel Arroyo
 */
@Component(
	property = "frontend.data.set.name=" + FDSSampleFDSNames.SINGLE_SELECTION,
	service = FDSItemsActions.class
)
public class SingleSelectionFDSItemsActions implements FDSItemsActions {

	@Override
	public List<FDSActionDropdownItem> getFDSActionDropdownItems(
		HttpServletRequest httpServletRequest) {

		return Arrays.asList(
			new FDSActionDropdownItem(
				null, "info-circle-open", "showDetails", "Show Details", null,
				null, "infoPanel"));
	}

}