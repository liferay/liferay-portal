/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.layout.tab.screen.navigation.entry;

import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.object.constants.ObjectWebKeys;
import com.liferay.object.internal.layout.tab.screen.navigation.category.ObjectLayoutTabScreenNavigationCategory;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectLayout;
import com.liferay.object.model.ObjectLayoutTab;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectLayoutLocalServiceUtil;
import com.liferay.object.service.ObjectLayoutTabLocalServiceUtil;
import com.liferay.portal.kernel.model.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;

/**
 * @author Shuyang Zhou
 */
public class ObjectLayoutTabScreenNavigationEntry
	implements ScreenNavigationEntry<ObjectLayoutTab> {

	public ObjectLayoutTabScreenNavigationEntry(
		ObjectDefinition objectDefinition, ObjectLayoutTab objectLayoutTab,
		ObjectLayoutTabScreenNavigationCategory
			objectLayoutTabScreenNavigationCategory,
		ObjectRelationship objectRelationship) {

		_objectDefinition = objectDefinition;
		_objectLayoutTab = objectLayoutTab;
		_objectLayoutTabScreenNavigationCategory =
			objectLayoutTabScreenNavigationCategory;
		_objectRelationship = objectRelationship;
	}

	@Override
	public String getCategoryKey() {
		return _objectLayoutTabScreenNavigationCategory.getCategoryKey();
	}

	@Override
	public String getEntryKey() {
		return _objectLayoutTabScreenNavigationCategory.getCategoryKey();
	}

	@Override
	public String getLabel(Locale locale) {
		return _objectLayoutTabScreenNavigationCategory.getLabel(locale);
	}

	@Override
	public String getScreenNavigationKey() {
		return _objectLayoutTabScreenNavigationCategory.
			getScreenNavigationKey();
	}

	@Override
	public boolean isVisible(User user, ObjectLayoutTab objectLayoutTab) {
		ObjectLayout objectLayout =
			ObjectLayoutLocalServiceUtil.fetchDefaultObjectLayout(
				_objectDefinition.getObjectDefinitionId());

		if ((objectLayout != null) && (_objectLayoutTab == null)) {
			return false;
		}

		if (_objectLayoutTab != null) {
			ObjectLayoutTab currentObjectLayoutTab =
				ObjectLayoutTabLocalServiceUtil.fetchObjectLayoutTab(
					_objectLayoutTab.getObjectLayoutTabId());

			if (currentObjectLayoutTab == null) {
				return false;
			}
		}

		if (_objectRelationship == null) {
			return true;
		}

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.fetchObjectDefinition(
				_objectRelationship.getObjectDefinitionId2());

		if ((objectDefinition == null) || !objectDefinition.isActive()) {
			return false;
		}

		return true;
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		httpServletRequest.setAttribute(
			ObjectWebKeys.REGULAR_OBJECT_LAYOUT_TAB, Boolean.TRUE);
	}

	private final ObjectDefinition _objectDefinition;
	private final ObjectLayoutTab _objectLayoutTab;
	private final ObjectLayoutTabScreenNavigationCategory
		_objectLayoutTabScreenNavigationCategory;
	private final ObjectRelationship _objectRelationship;

}