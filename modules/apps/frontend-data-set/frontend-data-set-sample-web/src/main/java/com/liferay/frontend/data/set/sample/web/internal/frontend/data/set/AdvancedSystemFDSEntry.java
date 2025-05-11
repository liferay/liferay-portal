/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.sample.web.internal.frontend.data.set;

import com.liferay.frontend.data.set.SystemFDSEntry;
import com.liferay.frontend.data.set.sample.web.internal.constants.FDSSampleFDSNames;

import org.osgi.service.component.annotations.Component;

/**
 * @author Marko Cikos
 */
@Component(
	property = "frontend.data.set.name=" + FDSSampleFDSNames.ADVANCED,
	service = SystemFDSEntry.class
)
public class AdvancedSystemFDSEntry implements SystemFDSEntry {

	@Override
	public String getAdditionalAPIURLParameters() {
		return "sort=title:asc";
	}

	@Override
	public String getDescription() {
		return "This is the \"Advanced\" sample of a frontend data set.";
	}

	@Override
	public String getName() {
		return FDSSampleFDSNames.ADVANCED;
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
	public String getSymbol() {
		return "shopping-cart";
	}

	@Override
	public String getTitle() {
		return "Advanced Sample";
	}

}