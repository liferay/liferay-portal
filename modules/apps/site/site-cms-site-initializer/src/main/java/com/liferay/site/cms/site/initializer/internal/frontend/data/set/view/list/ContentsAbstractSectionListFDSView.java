/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.frontend.data.set.view.list;

import com.liferay.frontend.data.set.view.FDSView;
import com.liferay.frontend.data.set.view.list.BaseListFDSView;
import com.liferay.petra.string.StringPool;
import com.liferay.site.cms.site.initializer.internal.constants.CMSSiteInitializerFDSNames;

import org.osgi.service.component.annotations.Component;

/**
 * @author Roberto Díaz
 */
@Component(
	property = {
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.SPACE_CONTENTS_ABSTRACT_SECTION,
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.SPACE_CONTENTS_SECTION
	},
	service = FDSView.class
)
public class ContentsAbstractSectionListFDSView extends BaseListFDSView {

	@Override
	public String getDescription() {
		return StringPool.BLANK;
	}

	@Override
	public String getTitle() {
		return "embedded.title";
	}

}