/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.sample.web.internal.frontend.data.set;

import com.liferay.frontend.data.set.SystemFDSEntry;
import com.liferay.frontend.data.set.sample.web.internal.constants.FDSSampleFDSNames;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Antonio Ortega
 */
@Component(
	property = "frontend.data.set.name=" + FDSSampleFDSNames.DELEGATED_FILTERS,
	service = SystemFDSEntry.class
)
public class DelegatedFiltersSystemFDSEntry implements SystemFDSEntry {

	@Override
	public String getAdditionalAPIURLParameters(
		HttpServletRequest httpServletRequest) {

		return "sort=title:asc";
	}

	@Override
	public String getDescription() {
		return "This is the \"Delegated Filters\" sample of a frontend data " +
			"set.";
	}

	@Override
	public String getName() {
		return FDSSampleFDSNames.DELEGATED_FILTERS;
	}

	@Override
	public String getRESTApplication() {
		return "/c/fdssamples";
	}

	@Override
	public String getRESTEndpoint() {
		return "/";
	}

	@Override
	public String getRESTSchema() {
		return "FDSSample";
	}

	@Override
	public boolean getSnapshotsEnabled() {
		return true;
	}

	@Override
	public String getSymbol() {
		return "filter";
	}

	@Override
	public String getTitle() {
		return "Delegated Filters Sample";
	}

}