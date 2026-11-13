/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.sample.web.internal.frontend.data.set.action;

import com.liferay.frontend.data.set.FDSEntryItemImportPolicy;
import com.liferay.frontend.data.set.action.FDSBulkActions;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.model.FDSActionDropdownItemBuilder;
import com.liferay.frontend.data.set.model.FDSActionDropdownItemList;
import com.liferay.frontend.data.set.sample.web.internal.constants.FDSSampleFDSNames;
import com.liferay.portal.kernel.language.LanguageUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;

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

		return FDSActionDropdownItemList.of(
			FDSActionDropdownItemBuilder.setHighlighted(
				true
			).setHref(
				"#"
			).setIcon(
				"document"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "label")
			).setModalSize(
				"lg"
			).setTarget(
				"modal"
			).build(
				"sampleBulkAction"
			),
			FDSActionDropdownItemBuilder.setHighlighted(
				false
			).setHref(
				"#"
			).setIcon(
				"trash"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "delete")
			).setModalSize(
				"lg"
			).setTarget(
				"modal"
			).build(
				"delete"
			),
			FDSActionDropdownItemBuilder.setHref(
				"/o/c/fdssamples/"
			).setIcon(
				"check"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "test")
			).setMethod(
				"POST"
			).build(
				"testBulkAction"
			));
	}

	@Override
	public FDSEntryItemImportPolicy getFDSEntryItemImportPolicy() {
		return FDSEntryItemImportPolicy.DETACHED;
	}

}