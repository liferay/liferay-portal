/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.layout.tab.screen.navigation.category;

import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationCategory;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.object.constants.ObjectWebKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectLayout;
import com.liferay.object.model.ObjectLayoutTab;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectLayoutLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;

/**
 * @author Feliphe Marinho
 */
public class ObjectLayoutTabScreenNavigationCategory
	implements ScreenNavigationCategory,
			   ScreenNavigationEntry<ObjectLayoutTab> {

	public ObjectLayoutTabScreenNavigationCategory(
		ObjectDefinition objectDefinition, ObjectLayoutTab objectLayoutTab,
		ObjectRelationship objectRelationship) {

		_objectDefinition = objectDefinition;
		_objectLayoutTab = objectLayoutTab;
		_objectRelationship = objectRelationship;
	}

	@Override
	public String getCategoryKey() {
		return _getKey();
	}

	@Override
	public String getEntryKey() {
		return _getKey();
	}

	@Override
	public String getLabel(Locale locale) {
		if (_objectLayoutTab != null) {
			return _objectLayoutTab.getName(locale);
		}

		if (_objectRelationship == null) {
			return LanguageUtil.get(locale, "basic-details");
		}

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.fetchObjectDefinition(
				_objectRelationship.getObjectDefinitionId2());

		if (objectDefinition == null) {
			return StringPool.BLANK;
		}

		return objectDefinition.getLabel(locale);
	}

	@Override
	public String getScreenNavigationKey() {
		return _objectDefinition.getClassName();
	}

	@Override
	public boolean isVisible(User user, ObjectLayoutTab objectLayoutTab) {
		ObjectLayout objectLayout =
			ObjectLayoutLocalServiceUtil.fetchDefaultObjectLayout(
				_objectDefinition.getObjectDefinitionId());

		if ((objectLayout != null) && (_objectLayoutTab == null)) {
			return false;
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

	private String _getKey() {
		if (_objectLayoutTab != null) {
			return String.valueOf(_objectLayoutTab.getObjectLayoutTabId());
		}

		if (_objectRelationship != null) {
			return String.valueOf(
				_objectRelationship.getObjectRelationshipId());
		}

		return String.valueOf(_objectDefinition.getObjectDefinitionId());
	}

	private final ObjectDefinition _objectDefinition;
	private final ObjectLayoutTab _objectLayoutTab;
	private final ObjectRelationship _objectRelationship;

}