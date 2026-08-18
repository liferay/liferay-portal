/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.model.FDSActionDropdownItemBuilder;
import com.liferay.frontend.data.set.serializer.FDSSerializer;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemBuilder;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.pim.site.initializer.internal.constants.PIMFDSNames;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Stefano Motta
 */
public class ProductRelationshipsDisplayContext {

	public ProductRelationshipsDisplayContext(
		FDSSerializer fdsSerializer, HttpServletRequest httpServletRequest,
		ObjectEntryLocalService objectEntryLocalService) {

		_fdsSerializer = fdsSerializer;
		_httpServletRequest = httpServletRequest;
		_objectEntryLocalService = objectEntryLocalService;
	}

	public String getAPIURL() {
		ObjectEntry objectEntry = _getObjectEntry();

		return StringBundler.concat(
			"/o/headless-pim/v1.0/scopes/", objectEntry.getGroupId(),
			"/links?className=",
			URLCodec.encodeURL(objectEntry.getModelClassName()),
			"&externalReferenceCode=",
			URLCodec.encodeURL(objectEntry.getExternalReferenceCode()));
	}

	public CreationMenu getCreationMenu() {
		CreationMenu creationMenu = new CreationMenu();

		ThemeDisplay themeDisplay = _getThemeDisplay();

		if (!FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-96666")) {

			return creationMenu;
		}

		ObjectEntry objectEntry = _getObjectEntry();

		if (!_hasUpdatePermission(objectEntry, themeDisplay)) {
			return creationMenu;
		}

		creationMenu.addPrimaryDropdownItem(
			DropdownItemBuilder.putData(
				"action", "createProductRelationship"
			).putData(
				"className", objectEntry.getModelClassName()
			).putData(
				"externalReferenceCode", objectEntry.getExternalReferenceCode()
			).putData(
				"filters",
				String.valueOf(
					_fdsSerializer.serializeFilters(
						PIMFDSNames.PRODUCT_RELATIONSHIP_SELECTOR,
						_httpServletRequest))
			).putData(
				"name", _getName(objectEntry)
			).putData(
				"scopeKey", String.valueOf(objectEntry.getGroupId())
			).putData(
				"searchAPIURL", _getSearchAPIURL(objectEntry)
			).setLabel(
				LanguageUtil.get(_httpServletRequest, "add-relationship")
			).build());

		return creationMenu;
	}

	public Map<String, Object> getEmptyState() {
		return HashMapBuilder.<String, Object>put(
			"image", "/states/cms_empty_state_preview.svg"
		).put(
			"title",
			LanguageUtil.get(_httpServletRequest, "no-relationships-were-found")
		).build();
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		ThemeDisplay themeDisplay = _getThemeDisplay();

		if (!FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-96666")) {

			return Collections.emptyList();
		}

		return ListUtil.fromArray(
			FDSActionDropdownItemBuilder.setConfirmationMessage(
				LanguageUtil.get(
					_httpServletRequest, "are-you-sure-you-want-to-delete-this")
			).setHref(
				"{actions.delete.href}"
			).setIcon(
				"trash"
			).setLabel(
				LanguageUtil.get(_httpServletRequest, "delete")
			).setMethod(
				"delete"
			).setPermissionKey(
				"delete"
			).setTarget(
				"headless"
			).build(
				"delete"
			));
	}

	private String _getName(ObjectEntry objectEntry) {
		try {
			return MapUtil.getString(
				_objectEntryLocalService.getValues(objectEntry), "name");
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return objectEntry.getExternalReferenceCode();
		}
	}

	private ObjectEntry _getObjectEntry() {
		return (ObjectEntry)_httpServletRequest.getAttribute(
			InfoDisplayWebKeys.INFO_ITEM);
	}

	private String _getSearchAPIURL(ObjectEntry objectEntry) {
		return StringBundler.concat(
			"/o/search/v1.0/search?emptySearch=true&filter=",
			URLCodec.encodeURL(
				StringBundler.concat(
					"cmsSection eq 'products' and groupIds/any(g:g eq ",
					objectEntry.getGroupId(), ") and not (entryClassPK in (",
					objectEntry.getObjectEntryId())),
			"{relatedObjectEntryIds}", URLCodec.encodeURL("))"),
			"&nestedFields=embedded,systemProperties.objectDefinitionBrief");
	}

	private ThemeDisplay _getThemeDisplay() {
		return (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	private boolean _hasUpdatePermission(
		ObjectEntry objectEntry, ThemeDisplay themeDisplay) {

		try {
			ModelResourcePermission<ObjectEntry> modelResourcePermission =
				ObjectEntryServiceUtil.getModelResourcePermission(
					objectEntry.getObjectDefinitionId());

			return modelResourcePermission.contains(
				themeDisplay.getPermissionChecker(),
				objectEntry.getObjectEntryId(), ActionKeys.UPDATE);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ProductRelationshipsDisplayContext.class);

	private final FDSSerializer _fdsSerializer;
	private final HttpServletRequest _httpServletRequest;
	private final ObjectEntryLocalService _objectEntryLocalService;

}