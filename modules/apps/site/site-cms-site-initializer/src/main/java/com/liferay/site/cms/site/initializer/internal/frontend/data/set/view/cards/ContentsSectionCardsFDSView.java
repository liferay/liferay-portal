/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.frontend.data.set.view.cards;

import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.petra.string.StringPool;
import com.liferay.site.cms.site.initializer.internal.constants.CMSSiteInitializerFDSNames;

import org.osgi.service.component.annotations.Component;

/**
 * @author Mikel Lorza
 */
@Component(
	property = "frontend.data.set.name=" + CMSSiteInitializerFDSNames.CONTENTS_SECTION,
	service = FDSView.class
)
public class ContentsSectionCardsFDSView extends BaseSectionCardsFDSView {

	@Override
	public String getDescription() {
		return StringPool.BLANK;
	}

	@Override
	public String getTitle() {
		return "embedded.title";
	}

}