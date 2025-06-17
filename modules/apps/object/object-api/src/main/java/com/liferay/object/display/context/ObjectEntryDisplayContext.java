/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectLayout;
import com.liferay.object.model.ObjectLayoutBox;
import com.liferay.object.model.ObjectLayoutTab;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;

import jakarta.servlet.jsp.PageContext;

import java.util.Map;

/**
 * @author Gabriel Albuquerque
 */
public interface ObjectEntryDisplayContext {

	public String getBackURL() throws PortalException;

	public ObjectDefinition getObjectDefinition1();

	public ObjectDefinition getObjectDefinition2() throws PortalException;

	public ObjectEntry getObjectEntry() throws PortalException;

	public ObjectLayout getObjectLayout() throws PortalException;

	public ObjectLayoutBox getObjectLayoutBox(String type)
		throws PortalException;

	public ObjectLayoutTab getObjectLayoutTab() throws PortalException;

	public ObjectRelationship getObjectRelationship() throws PortalException;

	public String getObjectRelationshipERCObjectFieldName();

	public String getParentObjectEntryId();

	public CreationMenu getRelatedModelCreationMenu(
			ObjectRelationship objectRelationship)
		throws PortalException;

	public String getRelatedObjectEntryItemSelectorURL(
			ObjectRelationship objectRelationship)
		throws PortalException;

	public Map<String, String> getRelationshipContextParams()
		throws PortalException;

	public Map<String, Object> getScheduleProperties() throws PortalException;

	public default String getURLSeparator() {
		return FriendlyURLResolverConstants.URL_SEPARATOR_OBJECT_ENTRY;
	}

	public boolean isGuestUser();

	public boolean isReadOnly();

	public boolean isShowObjectEntryForm() throws PortalException;

	public String renderDDMForm(PageContext pageContext) throws PortalException;

}