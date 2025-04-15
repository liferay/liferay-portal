/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.sample.web.internal.frontend.data.set.sort;

import com.liferay.frontend.data.set.FDSEntryItemImportPolicy;
import com.liferay.frontend.data.set.model.FDSSortItem;
import com.liferay.frontend.data.set.model.FDSSortItemBuilder;
import com.liferay.frontend.data.set.model.FDSSortItemListBuilder;
import com.liferay.frontend.data.set.sample.web.internal.constants.FDSSampleFDSNames;
import com.liferay.frontend.data.set.sort.FDSSorts;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * @author Daniel Sanz
 */
@Component(
	property = "frontend.data.set.name=" + FDSSampleFDSNames.CUSTOM_INTERNAL_VIEW,
	service = FDSSorts.class
)
public class CustomInternalViewFDSSorts implements FDSSorts {

	public FDSEntryItemImportPolicy getFDSEntryItemImportPolicy() {
		return FDSEntryItemImportPolicy.GROUP_PROXY;
	}

	@Override
	public List<FDSSortItem> getFDSSortItems(
		HttpServletRequest httpServletRequest) {

		return FDSSortItemListBuilder.add(
			FDSSortItemBuilder.setActive(
				true
			).setDirection(
				"asc"
			).setKey(
				"color"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "color")
			).build()
		).add(
			() -> {
				Locale locale = PortalUtil.getLocale(httpServletRequest);

				if (locale.equals(LocaleUtil.US)) {
					return false;
				}

				return true;
			},
			FDSSortItemBuilder.setActive(
				false
			).setDirection(
				"desc"
			).setKey(
				"title"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "title")
			).build()
		).build();
	}

}