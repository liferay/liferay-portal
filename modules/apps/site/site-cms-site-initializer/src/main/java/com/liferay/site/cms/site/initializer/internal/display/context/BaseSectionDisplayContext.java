/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.cms.site.initializer.internal.configuration.CMSSiteInitializerConfiguration;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Marco Galluzzi
 */
public abstract class BaseSectionDisplayContext {

	public BaseSectionDisplayContext(
		CMSSiteInitializerConfiguration cmsSiteInitializerConfiguration,
		HttpServletRequest httpServletRequest) {

		this.cmsSiteInitializerConfiguration = cmsSiteInitializerConfiguration;
		this.httpServletRequest = httpServletRequest;

		Object object = httpServletRequest.getAttribute(
			InfoDisplayWebKeys.INFO_ITEM);

		if (object instanceof ObjectEntryFolder) {
			objectEntryFolder = (ObjectEntryFolder)object;
		}

		themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getAPIURL() {
		String[] objectDefinitionFolderExternalReferenceCodes =
			getObjectDefinitionFolderExternalReferenceCodes();

		StringBundler sb = new StringBundler(9);

		sb.append("/o/search/v1.0/search?emptySearch=true&");
		sb.append("filter=objectDefinitionFolder in ('");
		sb.append(
			StringUtil.merge(
				objectDefinitionFolderExternalReferenceCodes, "','"));
		sb.append("')");

		String cmsSectionFilterString = getCMSSectionFilterString();

		if (Validator.isNotNull(cmsSectionFilterString)) {
			sb.append(" or ");
			sb.append(cmsSectionFilterString);
		}

		if (objectEntryFolder != null) {
			sb.append(" and folderId eq");
			sb.append(objectEntryFolder.getObjectEntryFolderId());
		}

		sb.append("&nestedFields=embedded");

		return sb.toString();
	}

	public List<DropdownItem> getBulkActionDropdownItems() {
		return new ArrayList<>();
	}

	public CreationMenu getCreationMenu() {
		return new CreationMenu();
	}

	public Map<String, Object> getEmptyState() {
		return Collections.emptyMap();
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems()
		throws Exception {

		return new ArrayList<>();
	}

	public String[] getObjectDefinitionFolderExternalReferenceCodes() {
		return new String[0];
	}

	protected String getAddStructuredContentItemURL(long objectDefinitionId) {
		StringBundler sb = new StringBundler(6);

		sb.append(themeDisplay.getPortalURL());
		sb.append(themeDisplay.getPathMain());
		sb.append("/cms/add_structured_content_item?groupId=");
		sb.append(themeDisplay.getScopeGroupId());
		sb.append("&objectDefinitionId=");
		sb.append(objectDefinitionId);

		return sb.toString();
	}

	protected abstract String getCMSSectionFilterString();

	protected final CMSSiteInitializerConfiguration
		cmsSiteInitializerConfiguration;
	protected final HttpServletRequest httpServletRequest;
	protected ObjectEntryFolder objectEntryFolder;
	protected final ThemeDisplay themeDisplay;

}