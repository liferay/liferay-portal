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

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Daniel Sanz
 */
@Component(
	property = "frontend.data.set.name=" + FDSSampleFDSNames.CLASSIC,
	service = FDSSorts.class
)
public class ClassicFDSSorts implements FDSSorts {

	public FDSEntryItemImportPolicy getFDSEntryItemImportPolicy() {
		return FDSEntryItemImportPolicy.ITEM_PROXY;
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
				"lastName"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "last-name")
			).build()
		).add(
			FDSSortItemBuilder.setActive(
				false
			).setDirection(
				"desc"
			).setKey(
				"emailAddress"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "email-address")
			).build()
		).add(
			FDSSortItemBuilder.setActive(
				false
			).setDirection(
				"desc"
			).setKey(
				"firstName"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "first-name")
			).build()
		).build();
	}

}