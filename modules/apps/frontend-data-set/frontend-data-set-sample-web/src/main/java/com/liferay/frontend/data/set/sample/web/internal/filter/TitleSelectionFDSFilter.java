/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.sample.web.internal.filter;

import com.liferay.frontend.data.set.filter.BaseSelectionFDSFilter;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.sample.web.internal.constants.FDSSampleFDSNames;

import org.osgi.service.component.annotations.Component;

/**
 * @author Daniel Sanz
 */
@Component(
	property = "frontend.data.set.name=" + FDSSampleFDSNames.ADVANCED,
	service = FDSFilter.class
)
public class TitleSelectionFDSFilter extends BaseSelectionFDSFilter {

	@Override
	public String getAPIURL() {
		return "o/c/fdssamples";
	}

	@Override
	public String getId() {
		return "title";
	}

	public String getItemKey() {
		return "title";
	}

	public String getItemLabel() {
		return "title";
	}

	@Override
	public String getLabel() {
		return "title";
	}

	@Override
	public boolean isAutocompleteEnabled() {
		return true;
	}

	@Override
	public boolean isMultiple() {
		return true;
	}

}