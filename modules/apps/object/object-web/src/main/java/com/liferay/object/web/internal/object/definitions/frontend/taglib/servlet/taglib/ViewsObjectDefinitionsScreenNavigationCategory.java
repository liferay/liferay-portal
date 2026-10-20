/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.definitions.frontend.taglib.servlet.taglib;

import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationCategory;
import com.liferay.object.web.internal.object.definitions.constants.ObjectDefinitionsScreenNavigationEntryConstants;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gabriel Albuquerque
 */
@Component(
	property = "screen.navigation.category.order:Integer=60",
	service = ScreenNavigationCategory.class
)
public class ViewsObjectDefinitionsScreenNavigationCategory
	extends BaseObjectDefinitionsScreenNavigationCategory {

	@Override
	public String getCategoryKey() {
		return ObjectDefinitionsScreenNavigationEntryConstants.
			CATEGORY_KEY_VIEWS;
	}

	@Override
	public String getLabel(Locale locale) {
		return language.get(locale, "views[saved]");
	}

}