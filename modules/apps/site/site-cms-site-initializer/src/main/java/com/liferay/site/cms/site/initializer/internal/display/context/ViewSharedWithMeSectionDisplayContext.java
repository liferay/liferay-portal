/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.editor.configuration.EditorConfiguration;
import com.liferay.portal.kernel.editor.configuration.EditorConfigurationFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.cms.site.initializer.internal.util.ActionUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alicia García
 */
public class ViewSharedWithMeSectionDisplayContext {

	public ViewSharedWithMeSectionDisplayContext(
		HttpServletRequest httpServletRequest,
		ObjectDefinitionService objectDefinitionService, Portal portal) {

		_httpServletRequest = httpServletRequest;
		_objectDefinitionService = objectDefinitionService;
		_portal = portal;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getAdditionalProps() {
		return HashMapBuilder.<String, Object>put(
			"autocompleteURL",
			() -> StringBundler.concat(
				"/o/search/v1.0/search?emptySearch=",
				"true&entryClassNames=com.liferay.portal.kernel.model.User,",
				"com.liferay.portal.kernel.model.UserGroup&nestedFields=",
				"embedded")
		).put(
			"collaboratorURLs",
			() -> {
				Map<String, String> collaboratorURLs = new HashMap<>();

				for (ObjectDefinition objectDefinition :
						_objectDefinitionService.getCMSObjectDefinitions(
							_themeDisplay.getCompanyId(),
							_getObjectFolderExternalReferenceCodes())) {

					collaboratorURLs.put(
						objectDefinition.getClassName(),
						StringBundler.concat(
							"/o", objectDefinition.getRESTContextPath(),
							"/{objectEntryId}/collaborators"));
				}

				collaboratorURLs.put(
					ObjectEntryFolder.class.getName(),
					"/o/headless-object/v1.0/object-entry-folders" +
						"/{objectEntryFolderId}/collaborators");

				return collaboratorURLs;
			}
		).put(
			"commentsProps",
			HashMapBuilder.<String, Object>put(
				"addCommentURL",
				StringBundler.concat(
					_themeDisplay.getPortalURL(), _themeDisplay.getPathMain(),
					GroupConstants.CMS_FRIENDLY_URL,
					"/add_content_item_comment")
			).put(
				"deleteCommentURL",
				StringBundler.concat(
					_themeDisplay.getPortalURL(), _themeDisplay.getPathMain(),
					GroupConstants.CMS_FRIENDLY_URL,
					"/delete_content_item_comment")
			).put(
				"editCommentURL",
				StringBundler.concat(
					_themeDisplay.getPortalURL(), _themeDisplay.getPathMain(),
					GroupConstants.CMS_FRIENDLY_URL,
					"/edit_content_item_comment")
			).put(
				"editorConfig",
				() -> {
					EditorConfiguration contentItemCommentEditorConfiguration =
						EditorConfigurationFactoryUtil.getEditorConfiguration(
							StringPool.BLANK, "contentItemCommentEditor",
							StringPool.BLANK, Collections.emptyMap(),
							_themeDisplay,
							RequestBackedPortletURLFactoryUtil.create(
								_httpServletRequest));

					Map<String, Object> data =
						contentItemCommentEditorConfiguration.getData();

					return data.get("editorConfig");
				}
			).put(
				"getCommentsURL",
				StringBundler.concat(
					_themeDisplay.getPortalURL(), _themeDisplay.getPathMain(),
					GroupConstants.CMS_FRIENDLY_URL, "/get_asset_comments")
			).build()
		).put(
			"contentViewURL",
			StringBundler.concat(
				_themeDisplay.getPortalURL(), _themeDisplay.getPathMain(),
				GroupConstants.CMS_FRIENDLY_URL,
				"/edit_content_item?&p_l_mode=read&p_p_state=",
				LiferayWindowState.POP_UP, "&redirect=",
				_themeDisplay.getURLCurrent(), "&objectEntryId={embedded.id}")
		).build();
	}

	public String getAPIURL() {
		return "/o/headless-admin-user/v1.0/my-user-account/shared-assets" +
			"/shared-with-me?filter=(spaceDepotEntry eq true)" +
				"&nestedFields=file";
	}

	public Map<String, Object> getBreadcrumbProps() throws PortalException {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		_addBreadcrumbItem(jsonArray, false, null, _getLayoutName());

		return HashMapBuilder.<String, Object>put(
			"breadcrumbItems", jsonArray
		).put(
			"hideSpace", true
		).build();
	}

	public Map<String, Object> getEmptyState() {
		return HashMapBuilder.<String, Object>put(
			"description",
			LanguageUtil.get(
				_httpServletRequest,
				"items-shared-with-you-by-other-users-will-appear-here")
		).put(
			"image", "/states/empty-state-shared-with-me.svg"
		).put(
			"title",
			LanguageUtil.get(
				_httpServletRequest, "no-items-shared-with-you-yet")
		).build();
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems()
		throws PortalException {

		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				StringBundler.concat(
					_themeDisplay.getPortalURL(), _themeDisplay.getPathMain(),
					GroupConstants.CMS_FRIENDLY_URL,
					"/edit_content_item?objectEntryId={classPK}",
					"&p_l_mode=read&p_p_state=", LiferayWindowState.POP_UP,
					"&redirect=", _themeDisplay.getURLCurrent()),
				"view", "actionLink",
				LanguageUtil.get(_httpServletRequest, "view"), "get", null,
				"modal"),
			new FDSActionDropdownItem(
				null, "share", "share",
				LanguageUtil.get(_httpServletRequest, "share"), "get", null,
				"link"),
			new FDSActionDropdownItem(
				StringPool.BLANK, "view", "view-file",
				LanguageUtil.get(_httpServletRequest, "view"), null, null, null,
				HashMapBuilder.<String, Object>put(
					"className", _getBasicDocumentClassName()
				).build()),
			new FDSActionDropdownItem(
				StringBundler.concat(
					_themeDisplay.getPortalURL(), _themeDisplay.getPathMain(),
					GroupConstants.CMS_FRIENDLY_URL,
					"/edit_content_item?&p_l_mode=read&p_p_state=",
					LiferayWindowState.POP_UP, "&redirect=",
					_themeDisplay.getURLCurrent(), "&objectEntryId={classPK}"),
				"view", "view-content",
				LanguageUtil.get(_httpServletRequest, "view"), "get", null,
				null),
			new FDSActionDropdownItem(
				StringBundler.concat(
					_themeDisplay.getPortalURL(), _themeDisplay.getPathMain(),
					GroupConstants.CMS_FRIENDLY_URL,
					"/edit_content_item?objectEntryId={classPK}&redirect=",
					_themeDisplay.getURLCurrent()),
				"pencil", "actionLinkEdit",
				LanguageUtil.get(_httpServletRequest, "edit"), "get", null,
				null),
			new FDSActionDropdownItem(
				"{file.link.href}", "download", "download",
				LanguageUtil.get(_httpServletRequest, "download"), "get", null,
				"link"),
			new FDSActionDropdownItem(
				ActionUtil.getBaseViewFolderURL(_themeDisplay) + "{classPK}",
				"view", "actionLinkFolder",
				LanguageUtil.get(_httpServletRequest, "view-folder"), "get",
				null, null,
				HashMapBuilder.<String, Object>put(
					"className", ObjectEntryFolder.class.getName()
				).build()),
			new FDSActionDropdownItem(
				StringBundler.concat(
					_themeDisplay.getPathFriendlyURLPublic(),
					GroupConstants.CMS_FRIENDLY_URL, "/e/edit-folder/",
					_portal.getClassNameId(ObjectEntryFolder.class),
					"/{classPK}?redirect=", _themeDisplay.getURLCurrent()),
				"pencil", "edit-folder",
				LanguageUtil.get(_httpServletRequest, "edit"), "get", null,
				null,
				HashMapBuilder.<String, Object>put(
					"className", ObjectEntryFolder.class.getName()
				).build()),
			new FDSActionDropdownItem(
				StringBundler.concat(
					"/o", GroupConstants.CMS_FRIENDLY_URL, "/download-folder/",
					_portal.getClassNameId(ObjectEntryFolder.class),
					"/{classPK}"),
				"download", "download-folder",
				LanguageUtil.get(_httpServletRequest, "download"), "get", null,
				"link",
				HashMapBuilder.<String, Object>put(
					"className", ObjectEntryFolder.class.getName()
				).build()));
	}

	private void _addBreadcrumbItem(
		JSONArray jsonArray, boolean active, String friendlyURL, String label) {

		jsonArray.put(
			JSONUtil.put(
				"active", active
			).put(
				"href", friendlyURL
			).put(
				"label", label
			));
	}

	private String _getBasicDocumentClassName() {
		try {
			ObjectDefinition basicDocumentObjectDefinition =
				_objectDefinitionService.
					fetchObjectDefinitionByExternalReferenceCode(
						"L_BASIC_DOCUMENT", _themeDisplay.getCompanyId());

			if (basicDocumentObjectDefinition != null) {
				return basicDocumentObjectDefinition.getClassName();
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return StringPool.BLANK;
	}

	private String _getLayoutName() {
		Layout layout = _themeDisplay.getLayout();

		if (layout == null) {
			return null;
		}

		return layout.getName(_themeDisplay.getLocale(), true);
	}

	private String[] _getObjectFolderExternalReferenceCodes() {
		return new String[] {
			ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES,
			ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES
		};
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewSharedWithMeSectionDisplayContext.class);

	private final HttpServletRequest _httpServletRequest;
	private final ObjectDefinitionService _objectDefinitionService;
	private final Portal _portal;
	private final ThemeDisplay _themeDisplay;

}