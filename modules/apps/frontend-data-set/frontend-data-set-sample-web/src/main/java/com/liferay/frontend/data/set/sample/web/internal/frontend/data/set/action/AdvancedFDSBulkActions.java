/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.sample.web.internal.frontend.data.set.action;

import com.liferay.frontend.data.set.FDSEntryItemImportPolicy;
import com.liferay.frontend.data.set.action.FDSBulkActions;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.sample.web.internal.constants.FDSSampleFDSNames;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marko Cikos
 */
@Component(
	property = "frontend.data.set.name=" + FDSSampleFDSNames.ADVANCED,
	service = FDSBulkActions.class
)
public class AdvancedFDSBulkActions implements FDSBulkActions {

	@Override
	public List<FDSActionDropdownItem> getFDSActionDropdownItems(
		HttpServletRequest httpServletRequest) {

		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				null, null, null, true, "#", "document", "sampleBulkAction",
				LanguageUtil.get(httpServletRequest, "label"), null, "lg", null,
				null, null, "modal", null, null, null),
			new FDSActionDropdownItem(
				null, null, null, false, "#", "trash", "delete",
				LanguageUtil.get(httpServletRequest, "delete"), null, "lg",
				null, null, null, "modal", null, null, null),
			new FDSActionDropdownItem(
				"/o/c/fdssamples/", "check", "testBulkAction",
				LanguageUtil.get(httpServletRequest, "test"), "POST", null,
				null));
	}

	@Override
	public FDSEntryItemImportPolicy getFDSEntryItemImportPolicy() {
		return FDSEntryItemImportPolicy.DETACHED;
	}

	@Reference
	private Language _language;

}