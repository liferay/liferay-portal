/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.configuration.DLConfiguration;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.site.cms.site.initializer.internal.util.ActionUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Marco Galluzzi
 */
public class ViewFolderSectionDisplayContext extends BaseSectionDisplayContext {

	public ViewFolderSectionDisplayContext(
		DepotEntryLocalService depotEntryLocalService,
		DLConfiguration dlConfiguration, GroupLocalService groupLocalService,
		HttpServletRequest httpServletRequest, Language language,
		ObjectDefinitionService objectDefinitionService,
		ObjectDefinitionSettingLocalService objectDefinitionSettingLocalService,
		ObjectEntryFolderLocalService objectEntryFolderLocalService,
		ModelResourcePermission<ObjectEntryFolder>
			objectEntryFolderModelResourcePermission,
		Portal portal) {

		super(
			depotEntryLocalService, dlConfiguration, groupLocalService,
			httpServletRequest, language, objectDefinitionService,
			objectDefinitionSettingLocalService,
			objectEntryFolderModelResourcePermission, portal);

		_objectEntryFolderLocalService = objectEntryFolderLocalService;
	}

	@Override
	public Map<String, Object> getAdditionalProps() {
		return new HashMapBuilder<>().putAll(
			super.getAdditionalProps()
		).put(
			"rootObjectEntryFolderExternalReferenceCode",
			getRootObjectEntryFolderExternalReferenceCode()
		).build();
	}

	public Map<String, Object> getBreadcrumbProps() {
		if (objectEntryFolder == null) {
			return Collections.emptyMap();
		}

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		Group group = groupLocalService.fetchGroup(
			objectEntryFolder.getGroupId());

		addBreadcrumbItem(
			jsonArray, false,
			ActionUtil.getSpaceURL(group.getClassPK(), themeDisplay),
			group.getName(themeDisplay.getLocale()));

		String[] parts = StringUtil.split(
			objectEntryFolder.getTreePath(), CharPool.SLASH);

		if (parts.length > 2) {
			for (int i = 1; i < (parts.length - 1); i++) {
				ObjectEntryFolder objectEntryFolder =
					_objectEntryFolderLocalService.fetchObjectEntryFolder(
						GetterUtil.getLong(parts[i]));

				addBreadcrumbItem(
					jsonArray, false,
					ActionUtil.getViewFolderURL(
						objectEntryFolder.getObjectEntryFolderId(),
						themeDisplay),
					objectEntryFolder.getName());
			}
		}

		addBreadcrumbItem(jsonArray, true, null, objectEntryFolder.getName());

		return HashMapBuilder.<String, Object>put(
			"breadcrumbItems", jsonArray
		).put(
			"displayType",
			() -> {
				UnicodeProperties unicodeProperties =
					group.getTypeSettingsProperties();

				return GetterUtil.get(
					unicodeProperties.get("logoColor"), "outline-0");
			}
		).put(
			"size", "sm"
		).build();
	}

	@Override
	public List<DropdownItem> getBulkActionDropdownItems() {
		List<DropdownItem> fdsBulkActionDropdownItems =
			super.getBulkActionDropdownItems();

		fdsBulkActionDropdownItems.add(
			new FDSActionDropdownItem(
				"#", "password-policies", "permissions",
				LanguageUtil.get(httpServletRequest, "permissions"), null, null,
				null));
		fdsBulkActionDropdownItems.add(
			new FDSActionDropdownItem(
				"#", "password-policies", "default-permissions",
				LanguageUtil.get(httpServletRequest, "default-permissions"),
				null, null, null));

		return fdsBulkActionDropdownItems;
	}

	@Override
	public List<DropdownItem> getCreationMenuDropdownItems() {
		if (Objects.equals(
				getRootObjectEntryFolderExternalReferenceCode(),
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS)) {

			return ActionUtil.getContentsSectionCreationMenuDropdownItems(
				httpServletRequest, objectEntryFolder);
		}

		return ActionUtil.getFilesSectionCreationMenuDropdownItems(
			httpServletRequest, objectEntryFolder);
	}

	@Override
	public Map<String, Object> getEmptyState() {
		String rootObjectEntryFolderExternalReferenceCode =
			getRootObjectEntryFolderExternalReferenceCode();

		String description = "click-new-or-drag-and-drop-your-files-here";
		String image = "/states/cms_empty_state.svg";
		String title = "no-assets-yet";

		if (Objects.equals(
				rootObjectEntryFolderExternalReferenceCode,
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS)) {

			description = "click-new-to-create-your-first-piece-of-content";
			image = "/states/cms_empty_state_content.svg";
			title = "no-content-yet";
		}
		else if (Objects.equals(
					rootObjectEntryFolderExternalReferenceCode,
					ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES)) {

			description = "click-new-or-drag-and-drop-your-files-here";
			image = "/states/cms_empty_state_files.svg";
			title = "no-files-yet";
		}

		return HashMapBuilder.<String, Object>put(
			"description", LanguageUtil.get(httpServletRequest, description)
		).put(
			"image", image
		).put(
			"title", LanguageUtil.get(httpServletRequest, title)
		).build();
	}

	@Override
	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		List<FDSActionDropdownItem> fdsActionDropdownItems =
			super.getFDSActionDropdownItems();

		fdsActionDropdownItems.add(
			new FDSActionDropdownItem(
				"{embedded.file.link.href}", "download", "download",
				LanguageUtil.get(httpServletRequest, "download"), "get", null,
				"link"));

		if (!Objects.equals(
				getRootObjectEntryFolderExternalReferenceCode(),
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS)) {

			fdsActionDropdownItems.add(
				new FDSActionDropdownItem(
					StringBundler.concat(
						"/o", GroupConstants.CMS_FRIENDLY_URL,
						"/download-folder/",
						portal.getClassNameId(ObjectEntryFolder.class),
						"/{embedded.id}"),
					"download", "download-folder",
					LanguageUtil.get(httpServletRequest, "download"), "get",
					null, "link",
					HashMapBuilder.<String, Object>put(
						"entryClassName", ObjectEntryFolder.class.getName()
					).build()));
		}

		fdsActionDropdownItems.add(
			new FDSActionDropdownItem(
				StringPool.BLANK, "info-circle-open", "show-details",
				LanguageUtil.get(httpServletRequest, "show-details"), null,
				null, "infoPanel"));

		return fdsActionDropdownItems;
	}

	@Override
	public String[] getObjectFolderExternalReferenceCodes() {
		if (_objectFolderExternalReferenceCode != null) {
			return new String[] {_objectFolderExternalReferenceCode};
		}

		String rootObjectEntryFolderExternalReferenceCode =
			getRootObjectEntryFolderExternalReferenceCode();

		if (rootObjectEntryFolderExternalReferenceCode == null) {
			return new String[0];
		}

		if (rootObjectEntryFolderExternalReferenceCode.equals(
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS)) {

			_objectFolderExternalReferenceCode =
				ObjectFolderConstants.
					EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES;
		}

		if (rootObjectEntryFolderExternalReferenceCode.equals(
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES)) {

			_objectFolderExternalReferenceCode =
				ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES;
		}

		return new String[] {_objectFolderExternalReferenceCode};
	}

	@Override
	public String getRootObjectEntryFolderExternalReferenceCode() {
		if (_rootObjectEntryFolderExternalReferenceCode != null) {
			return _rootObjectEntryFolderExternalReferenceCode;
		}

		if (objectEntryFolder == null) {
			return null;
		}

		String[] parts = StringUtil.split(
			objectEntryFolder.getTreePath(), CharPool.SLASH);

		if (parts.length <= 2) {
			_rootObjectEntryFolderExternalReferenceCode =
				objectEntryFolder.getExternalReferenceCode();

			return _rootObjectEntryFolderExternalReferenceCode;
		}

		ObjectEntryFolder rootObjectEntryFolder =
			_objectEntryFolderLocalService.fetchObjectEntryFolder(
				GetterUtil.getLong(parts[1]));

		if (rootObjectEntryFolder == null) {
			return null;
		}

		_rootObjectEntryFolderExternalReferenceCode =
			rootObjectEntryFolder.getExternalReferenceCode();

		return _rootObjectEntryFolderExternalReferenceCode;
	}

	@Override
	protected String getCMSSectionFilterString() {
		return null;
	}

	private final ObjectEntryFolderLocalService _objectEntryFolderLocalService;
	private String _objectFolderExternalReferenceCode;
	private String _rootObjectEntryFolderExternalReferenceCode;

}