/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.web.internal.util;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.segments.web.internal.display.context.SegmentsCompanyConfigurationDisplayContext;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Diego Hu
 */
public class SegmentsCompanyConfigurationActionDropdownItemsProvider {

	public SegmentsCompanyConfigurationActionDropdownItemsProvider(
		HttpServletRequest httpServletRequest,
		SegmentsCompanyConfigurationDisplayContext
			segmentsCompanyConfigurationDisplayContext) {

		_httpServletRequest = httpServletRequest;
		_segmentsCompanyConfigurationDisplayContext =
			segmentsCompanyConfigurationDisplayContext;
	}

	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> dropdownGroupItem.setDropdownItems(
				DropdownItemListBuilder.add(
					dropdownItem -> {
						dropdownItem.setHref(
							_segmentsCompanyConfigurationDisplayContext.
								getDeleteConfigurationActionURL());
						dropdownItem.setLabel(
							LanguageUtil.get(
								_httpServletRequest, "reset-default-values"));
					}
				).add(
					dropdownItem -> {
						dropdownItem.setHref(
							_segmentsCompanyConfigurationDisplayContext.
								getExportConfigurationActionURL());
						dropdownItem.setLabel(
							LanguageUtil.get(_httpServletRequest, "export"));
					}
				).build())
		).build();
	}

	private final HttpServletRequest _httpServletRequest;
	private final SegmentsCompanyConfigurationDisplayContext
		_segmentsCompanyConfigurationDisplayContext;

}