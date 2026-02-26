/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.site.initializer.internal.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.model.FDSActionDropdownItemBuilder;
import com.liferay.frontend.data.set.model.FDSActionDropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalServiceUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.site.dsr.site.initializer.internal.constants.DSRConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Stefano Motta
 */
public class ViewRoomsSectionDisplayContext extends BaseSectionDisplayContext {

	public ViewRoomsSectionDisplayContext(
		HttpServletRequest httpServletRequest,
		ObjectDefinition objectDefinition,
		ObjectEntryService objectEntryService) {

		super(httpServletRequest, objectDefinition, objectEntryService);
	}

	public Map<String, Object> getAdditionalProps() {
		return HashMapBuilder.<String, Object>put(
			"createRedirectURL",
			() -> StringBundler.concat(
				themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
				DSRConstants.DSR_FRIENDLY_URL, "/edit_room?siteId={siteId}")
		).put(
			"siteTemplates",
			() -> TransformUtil.transform(
				LayoutSetPrototypeLocalServiceUtil.getLayoutSetPrototypes(
					objectDefinition.getCompanyId()),
				this::_getLayoutSetPrototypeJSONObject)
		).build();
	}

	@Override
	public String getAPIURL() {
		StringBundler sb = new StringBundler(4);

		sb.append("/o/search/v1.0/search?emptySearch=true&");
		sb.append("filter=objectDefinitionId eq ");
		sb.append(objectDefinition.getObjectDefinitionId());
		sb.append("&nestedFields=embedded,r_accountToDSRRooms_accountEntryId");

		return sb.toString();
	}

	@Override
	public CreationMenu getCreationMenu() throws Exception {
		if (!hasAddObjectEntryPortletResourcePermission()) {
			return null;
		}

		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.putData("action", "createDigitalSalesRoom");
				dropdownItem.putData(
					"objectDefinitionId",
					String.valueOf(objectDefinition.getObjectDefinitionId()));
				dropdownItem.putData(
					"title",
					objectDefinition.getLabel(themeDisplay.getLocale()));
				dropdownItem.setIcon("forms");
				dropdownItem.setLabel(
					LanguageUtil.get(
						httpServletRequest, "new-digital-sales-room"));
			}
		).build();
	}

	@Override
	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		return FDSActionDropdownItemList.of(
			FDSActionDropdownItemBuilder.setHref(
				() -> StringBundler.concat(
					themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
					DSRConstants.DSR_FRIENDLY_URL, "/edit_room?siteId=",
					"{embedded.siteId}")
			).setIcon(
				"pencil"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "edit")
			).setPermissionKey(
				"update"
			).build(
				"edit"
			),
			FDSActionDropdownItemBuilder.setHref(
				"#"
			).setIcon(
				"share"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "share")
			).setPermissionKey(
				"update"
			).build(
				"share"
			),
			FDSActionDropdownItemBuilder.setHref(
				"#"
			).setIcon(
				"trash"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "delete")
			).setMethod(
				"delete"
			).setPermissionKey(
				"delete"
			).build(
				"delete"
			));
	}

	private JSONObject _getLayoutSetPrototypeJSONObject(
		LayoutSetPrototype layoutSetPrototype) {

		return JSONUtil.put(
			"description",
			() -> layoutSetPrototype.getDescription(themeDisplay.getLocale())
		).put(
			"friendlyURL",
			() -> {
				Group group = layoutSetPrototype.getGroup();

				return group.getDisplayURL(themeDisplay, true);
			}
		).put(
			"name", () -> layoutSetPrototype.getName(themeDisplay.getLocale())
		).put(
			"uuid", layoutSetPrototype.getUuid()
		);
	}

}