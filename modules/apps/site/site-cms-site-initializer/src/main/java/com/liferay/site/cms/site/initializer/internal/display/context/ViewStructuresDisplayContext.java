/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.model.FDSActionDropdownItemBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.constants.ObjectPortletKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.cms.site.initializer.contributor.CMSStructureObjectFolderContributor;
import com.liferay.site.cms.site.initializer.internal.util.ActionUtil;
import com.liferay.site.cms.site.initializer.internal.util.ExportImportUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sam Ziemer
 */
public class ViewStructuresDisplayContext {

	public ViewStructuresDisplayContext(
		List<CMSStructureObjectFolderContributor>
			cmsStructureObjectFolderContributors,
		HttpServletRequest httpServletRequest) {

		_cmsStructureObjectFolderContributors =
			cmsStructureObjectFolderContributors;
		_httpServletRequest = httpServletRequest;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getAdditionalProps() {
		Map<String, String> objectFolderTypeLabels = new HashMap<>();

		for (CMSStructureObjectFolderContributor
				cmsStructureObjectFolderContributor :
					_cmsStructureObjectFolderContributors) {

			String objectFolderExternalReferenceCode =
				cmsStructureObjectFolderContributor.
					getObjectFolderExternalReferenceCode();

			if (Validator.isNull(objectFolderExternalReferenceCode)) {
				continue;
			}

			objectFolderTypeLabels.put(
				objectFolderExternalReferenceCode,
				LanguageUtil.get(
					_httpServletRequest,
					cmsStructureObjectFolderContributor.getLabel()));
		}

		return HashMapBuilder.<String, Object>put(
			"objectFolderTypeLabels", objectFolderTypeLabels
		).build();
	}

	public String getAPIURL() {
		StringBundler sb = new StringBundler();

		sb.append("/o/object-admin/v1.0/object-definitions?filter=");
		sb.append("(objectFolderExternalReferenceCode eq '");
		sb.append(
			ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES);
		sb.append("' or objectFolderExternalReferenceCode eq '");
		sb.append(ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES);
		sb.append("'");

		for (CMSStructureObjectFolderContributor
				cmsStructureObjectFolderContributor :
					_cmsStructureObjectFolderContributors) {

			String objectFolderExternalReferenceCode =
				cmsStructureObjectFolderContributor.
					getObjectFolderExternalReferenceCode();

			if (Validator.isNull(objectFolderExternalReferenceCode)) {
				continue;
			}

			sb.append(" or objectFolderExternalReferenceCode eq '");
			sb.append(objectFolderExternalReferenceCode);
			sb.append("'");
		}

		sb.append(")");

		return sb.toString();
	}

	public Map<String, Object> getBreadcrumbProps() {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		_addBreadcrumbItem(jsonArray, false, null, _getLayoutName());

		return HashMapBuilder.<String, Object>put(
			"actionItems",
			JSONUtil.putAll(
				ExportImportUtil.getExportActionItemJSONObject(
					_httpServletRequest, ObjectPortletKeys.OBJECT_DEFINITIONS,
					"export-content-structures", _themeDisplay),
				ExportImportUtil.getImportActionItemJSONObject(
					_httpServletRequest, ObjectPortletKeys.OBJECT_DEFINITIONS,
					"import-content-structures", _themeDisplay)
			).put(
				JSONUtil.put(
					"href",
					PortletURLBuilder.create(
						PortletURLFactoryUtil.create(
							_httpServletRequest,
							ObjectPortletKeys.OBJECT_DEFINITIONS,
							PortletRequest.ACTION_PHASE)
					).setActionName(
						"/object_definitions/import_object_definition"
					).buildString()
				).put(
					"label",
					LanguageUtil.format(
						_httpServletRequest, "import-from-x", "JSON")
				).put(
					"redirect", _themeDisplay.getURLCurrent()
				).put(
					"symbolLeft", "import"
				).put(
					"target", "importStructureModal"
				)
			)
		).put(
			"breadcrumbItems", jsonArray
		).put(
			"hideSpace", true
		).build();
	}

	public List<DropdownItem> getBulkActionDropdownItems() {
		return List.of(
			FDSActionDropdownItemBuilder.setHighlighted(
				true
			).setHref(
				"#"
			).setIcon(
				"workflow"
			).setLabel(
				LanguageUtil.get(_httpServletRequest, "assign-default-workflow")
			).setModalSize(
				"lg"
			).setTarget(
				"modal"
			).build(
				"assign-default-workflow"
			));
	}

	public CreationMenu getCreationMenu() {
		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					ActionUtil.getBaseStructureBuilderURL(_themeDisplay) +
						"?objectFolderExternalReferenceCode=" +
							ObjectFolderConstants.
								EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES);
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "content"));
			}
		).addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					ActionUtil.getBaseStructureBuilderURL(_themeDisplay) +
						"?objectFolderExternalReferenceCode=" +
							ObjectFolderConstants.
								EXTERNAL_REFERENCE_CODE_FILE_TYPES);
				dropdownItem.setLabel(
					LanguageUtil.get(_httpServletRequest, "file"));
			}
		).build();
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems()
		throws Exception {

		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				ActionUtil.getBaseStructureBuilderURL(_themeDisplay) +
					"?objectDefinitionId={id}",
				"pencil", "edit", LanguageUtil.get(_httpServletRequest, "edit"),
				"get", "update", null),
			new FDSActionDropdownItem(
				ActionUtil.getBaseStructureUsagesURL(_themeDisplay) + "{id}",
				"list-ul", "viewUsages",
				LanguageUtil.get(_httpServletRequest, "view-usages"), "get",
				null, null),
			new FDSActionDropdownItem(
				ResourceURLBuilder.createResourceURL(
					PortletURLFactoryUtil.create(
						_httpServletRequest,
						ObjectPortletKeys.OBJECT_DEFINITIONS,
						PortletRequest.RESOURCE_PHASE)
				).setParameter(
					"objectDefinitionId", "{id}"
				).setResourceID(
					"/object_definitions/export_bound_object_definitions"
				).buildString(),
				"export", "exportBoundObjectDefinitions",
				LanguageUtil.get(_httpServletRequest, "export-as-json"), "get",
				"exportBoundObjectDefinitions", null),
			new FDSActionDropdownItem(
				ResourceURLBuilder.createResourceURL(
					PortletURLFactoryUtil.create(
						_httpServletRequest,
						ObjectPortletKeys.OBJECT_DEFINITIONS,
						PortletRequest.RESOURCE_PHASE)
				).setParameter(
					"objectDefinitionId", "{id}"
				).setResourceID(
					"/object_definitions/export_object_definition"
				).buildString(),
				"export", "export",
				LanguageUtil.get(_httpServletRequest, "export-as-json"), "get",
				"exportObjectDefinition", null),
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					PortalUtil.getControlPanelPortletURL(
						_httpServletRequest,
						"com_liferay_portlet_configuration_web_portlet_" +
							"PortletConfigurationPortlet",
						ActionRequest.RENDER_PHASE)
				).setMVCPath(
					"/edit_permissions.jsp"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"modelResource", ObjectDefinition.class.getName()
				).setParameter(
					"modelResourceDescription", "{name}"
				).setParameter(
					"resourcePrimKey", "{id}"
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildString(),
				"password-policies", "permissions",
				LanguageUtil.get(_httpServletRequest, "permissions"), "get",
				"permissions", "modal-permissions"),
			new FDSActionDropdownItem(
				StringBundler.concat(
					_themeDisplay.getPortalURL(), _themeDisplay.getPathMain(),
					"/cms/get_object_definition_deletion_info?",
					"objectDefinitionId={id}"),
				"trash", "delete",
				LanguageUtil.get(_httpServletRequest, "delete"), "delete",
				"delete", null, Map.of("system", false)));
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

	private String _getLayoutName() {
		Layout layout = _themeDisplay.getLayout();

		if (layout == null) {
			return null;
		}

		return layout.getName(_themeDisplay.getLocale(), true);
	}

	private final List<CMSStructureObjectFolderContributor>
		_cmsStructureObjectFolderContributors;
	private final HttpServletRequest _httpServletRequest;
	private final ThemeDisplay _themeDisplay;

}