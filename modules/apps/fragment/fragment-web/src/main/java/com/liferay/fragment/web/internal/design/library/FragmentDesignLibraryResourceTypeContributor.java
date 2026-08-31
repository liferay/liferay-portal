/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.design.library;

import com.liferay.depot.model.DepotEntry;
import com.liferay.design.library.resource.type.DesignLibraryResourceCreationItem;
import com.liferay.design.library.resource.type.DesignLibraryResourceTypeContributor;
import com.liferay.fragment.constants.FragmentActionKeys;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 * @author Thiago Buarque
 */
@Component(
	property = "service.ranking:Integer=200",
	service = DesignLibraryResourceTypeContributor.class
)
public class FragmentDesignLibraryResourceTypeContributor
	implements DesignLibraryResourceTypeContributor {

	@Override
	public String getColor() {
		return "pink";
	}

	@Override
	public List<DesignLibraryResourceCreationItem> getCreationItems(
			HttpServletRequest httpServletRequest, DepotEntry depotEntry,
			String backURL)
		throws PortalException {

		Group depotGroup = depotEntry.getGroup();

		Map<String, Object> baseModuleProps = _getBaseModuleProps(
			httpServletRequest, depotGroup, backURL);

		return ListUtil.fromArray(
			_newCreationItem(
				httpServletRequest, "add-basic-fragment", "new-basic-fragment",
				"fragment", FragmentConstants.TYPE_COMPONENT, baseModuleProps),
			_newCreationItem(
				httpServletRequest, "add-form-fragment", "new-form-fragment",
				"fragment", FragmentConstants.TYPE_INPUT, baseModuleProps),
			_newCreationItem(
				httpServletRequest, "add-fragment-set", "new-fragment-set",
				"set", 0, baseModuleProps));
	}

	@Override
	public String getDefaultActionId() {
		return "view";
	}

	@Override
	public String getEntryClassName() {
		return FragmentCollection.class.getName();
	}

	@Override
	public List<FDSActionDropdownItem> getFDSActionDropdownItems(
			HttpServletRequest httpServletRequest, DepotEntry depotEntry,
			String backURL)
		throws PortalException {

		Group depotGroup = depotEntry.getGroup();

		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					PortalUtil.getControlPanelPortletURL(
						httpServletRequest, depotGroup,
						FragmentPortletKeys.FRAGMENT, 0, 0,
						PortletRequest.RENDER_PHASE)
				).setBackURL(
					backURL
				).setParameter(
					"fragmentCollectionExternalReferenceCode",
					"{embedded.externalReferenceCode}"
				).buildString(),
				"view", "view", LanguageUtil.get(httpServletRequest, "view"),
				null, null, "link"),
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					PortalUtil.getControlPanelPortletURL(
						httpServletRequest, depotGroup,
						FragmentPortletKeys.FRAGMENT, 0, 0,
						PortletRequest.RENDER_PHASE)
				).setMVCRenderCommandName(
					"/fragment/edit_fragment_collection"
				).setRedirect(
					backURL
				).setParameter(
					"fragmentCollectionExternalReferenceCode",
					"{embedded.externalReferenceCode}"
				).buildString(),
				"pencil", "edit", LanguageUtil.get(httpServletRequest, "edit"),
				null, null, "link"),
			new FDSActionDropdownItem(
				"{actions.delete.href}", "trash", "delete",
				LanguageUtil.get(httpServletRequest, "delete"), "delete",
				"delete", "async"));
	}

	@Override
	public String getIcon() {
		return "squares";
	}

	@Override
	public String getKey() {
		return "fragment-set";
	}

	@Override
	public String getLabel(Locale locale) {
		return LanguageUtil.get(locale, "fragment-set");
	}

	@Override
	public boolean hasAddPermission(
		PermissionChecker permissionChecker, DepotEntry depotEntry) {

		return _portletResourcePermission.contains(
			permissionChecker, depotEntry.getGroupId(),
			FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);
	}

	@Override
	public boolean hasViewPermission(
		PermissionChecker permissionChecker, DepotEntry depotEntry) {

		return hasAddPermission(permissionChecker, depotEntry);
	}

	private Map<String, Object> _getBaseModuleProps(
		HttpServletRequest httpServletRequest, Group depotGroup,
		String backURL) {

		LiferayPortletURL addFragmentCollectionPortletURL =
			(LiferayPortletURL)PortalUtil.getControlPanelPortletURL(
				httpServletRequest, depotGroup, FragmentPortletKeys.FRAGMENT, 0,
				0, PortletRequest.RESOURCE_PHASE);

		addFragmentCollectionPortletURL.setResourceID(
			"/fragment/add_fragment_collection");

		return HashMapBuilder.<String, Object>put(
			"addFragmentCollectionURL",
			addFragmentCollectionPortletURL.toString()
		).put(
			"addFragmentEntryURL",
			PortletURLBuilder.create(
				PortalUtil.getControlPanelPortletURL(
					httpServletRequest, depotGroup,
					FragmentPortletKeys.FRAGMENT, 0, 0,
					PortletRequest.ACTION_PHASE)
			).setActionName(
				"/fragment/add_fragment_entry"
			).setRedirect(
				backURL
			).buildString()
		).put(
			"fragmentCollections",
			JSONUtil.toJSONArray(
				_fragmentCollectionLocalService.getFragmentCollections(
					depotGroup.getGroupId(), QueryUtil.ALL_POS,
					QueryUtil.ALL_POS),
				fragmentCollection -> JSONUtil.put(
					"fragmentCollectionId",
					fragmentCollection.getFragmentCollectionId()
				).put(
					"name", fragmentCollection.getName()
				),
				_log)
		).put(
			"namespace",
			PortalUtil.getPortletNamespace(FragmentPortletKeys.FRAGMENT)
		).build();
	}

	private DesignLibraryResourceCreationItem _newCreationItem(
		HttpServletRequest httpServletRequest, String id, String languageKey,
		String mode, int fragmentType, Map<String, Object> baseModuleProps) {

		return new DesignLibraryResourceCreationItem(
			id, LanguageUtil.get(httpServletRequest, languageKey),
			"{AddFragmentDesignLibraryModalContent} from fragment-web",
			HashMapBuilder.<String, Object>putAll(
				baseModuleProps
			).put(
				"fragmentType", fragmentType
			).put(
				"mode", mode
			).build());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FragmentDesignLibraryResourceTypeContributor.class);

	@Reference
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Reference(
		target = "(resource.name=" + FragmentConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}