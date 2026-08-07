/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.design.library;

import com.liferay.design.library.resource.type.DesignLibraryResourceType;
import com.liferay.fragment.constants.FragmentActionKeys;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
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
 * @author Thiago Buarque
 */
@Component(
	property = "service.ranking:Integer=200",
	service = DesignLibraryResourceType.class
)
public class FragmentDesignLibraryResourceType
	implements DesignLibraryResourceType {

	@Override
	public String getColor() {
		return "--pink";
	}

	@Override
	public String getCreationItemsModule() {
		return "{getFragmentCreationItems} from fragment-web";
	}

	@Override
	public Map<String, Object> getCreationItemsProps(
		HttpServletRequest httpServletRequest, Group depotGroup,
		String backURL) {

		JSONArray fragmentCollectionsJSONArray = _jsonFactory.createJSONArray();

		for (FragmentCollection fragmentCollection :
				_fragmentCollectionLocalService.getFragmentCollections(
					depotGroup.getGroupId(), QueryUtil.ALL_POS,
					QueryUtil.ALL_POS)) {

			fragmentCollectionsJSONArray.put(
				JSONUtil.put(
					"fragmentCollectionId",
					fragmentCollection.getFragmentCollectionId()
				).put(
					"name", fragmentCollection.getName()
				));
		}

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
			).setParameter(
				"type", FragmentConstants.TYPE_COMPONENT
			).buildString()
		).put(
			"fragmentCollections", fragmentCollectionsJSONArray
		).put(
			"namespace",
			PortalUtil.getPortletNamespace(FragmentPortletKeys.FRAGMENT)
		).build();
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
		HttpServletRequest httpServletRequest, Group depotGroup,
		String backURL) {

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
	public String getLabel(Locale locale) {
		return LanguageUtil.get(locale, "fragment-set");
	}

	@Override
	public String getSymbol() {
		return "squares";
	}

	@Override
	public boolean hasAddPermission(
		PermissionChecker permissionChecker, Group depotGroup) {

		return _portletResourcePermission.contains(
			permissionChecker, depotGroup.getGroupId(),
			FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);
	}

	@Override
	public boolean hasViewPermission(
		PermissionChecker permissionChecker, Group depotGroup) {

		return hasAddPermission(permissionChecker, depotGroup);
	}

	@Reference
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference(
		target = "(resource.name=" + FragmentConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}