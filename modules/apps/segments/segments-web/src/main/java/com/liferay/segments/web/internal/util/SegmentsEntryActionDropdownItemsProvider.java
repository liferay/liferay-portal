/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.web.internal.util;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.web.internal.display.context.SegmentsDisplayContext;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Diego Hu
 */
public class SegmentsEntryActionDropdownItemsProvider {

	public SegmentsEntryActionDropdownItemsProvider(
		HttpServletRequest httpServletRequest,
		SegmentsDisplayContext segmentsDisplayContext,
		SegmentsEntry segmentsEntry) {

		_httpServletRequest = httpServletRequest;
		_segmentsDisplayContext = segmentsDisplayContext;
		_segmentsEntry = segmentsEntry;
	}

	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> dropdownGroupItem.setDropdownItems(
				DropdownItemListBuilder.add(
					() -> _segmentsDisplayContext.isShowUpdateAction(
						_segmentsEntry),
					dropdownItem -> {
						dropdownItem.setHref(
							_segmentsDisplayContext.getEditURL(_segmentsEntry));
						dropdownItem.setIcon("pencil");
						dropdownItem.setLabel(
							LanguageUtil.get(_httpServletRequest, "edit"));
					}
				).build())
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> _segmentsDisplayContext.isShowViewAction(
							_segmentsEntry),
						dropdownItem -> {
							dropdownItem.putData(
								"action", "viewMembersSegmentsEntry");
							dropdownItem.putData(
								"viewMembersSegmentsEntryURL",
								_segmentsDisplayContext.getPreviewMembersURL(
									_segmentsEntry));
							dropdownItem.setIcon("users");
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "view-members"));
						}
					).add(
						() ->
							_segmentsDisplayContext.isShowAssignUserRolesAction(
								_segmentsEntry),
						dropdownItem -> {
							Map<String, Object> assignUserRolesDataMap =
								_segmentsDisplayContext.
									getAssignUserRolesDataMap(_segmentsEntry);

							dropdownItem.putData(
								"action", "assignSiteRolesSegmentsEntry");
							dropdownItem.putData(
								"itemSelectorURL",
								String.valueOf(
									assignUserRolesDataMap.get(
										"itemSelectorURL")));
							dropdownItem.putData(
								"segmentsEntryId",
								String.valueOf(
									assignUserRolesDataMap.get(
										"segmentsEntryId")));
							dropdownItem.setDisabled(
								!_segmentsDisplayContext.
									isRoleSegmentationEnabled(
										_segmentsEntry.getCompanyId()));
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "assign-site-roles"));
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> _segmentsDisplayContext.isShowPermissionAction(
							_segmentsEntry),
						dropdownItem -> {
							dropdownItem.putData(
								"action", "permissionsSegmentsEntry");
							dropdownItem.putData(
								"permissionsSegmentsEntryURL",
								_segmentsDisplayContext.getPermissionURL(
									_segmentsEntry));
							dropdownItem.setIcon("password-policies");
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "permissions"));
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> _segmentsDisplayContext.isShowDeleteAction(
							_segmentsEntry),
						dropdownItem -> {
							dropdownItem.putData(
								"action", "deleteSegmentEntry");
							dropdownItem.putData(
								"deleteSegmentEntryURL",
								_segmentsDisplayContext.getDeleteURL(
									_segmentsEntry));
							dropdownItem.setIcon("trash");
							dropdownItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, "delete"));
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).build();
	}

	private final HttpServletRequest _httpServletRequest;
	private final SegmentsDisplayContext _segmentsDisplayContext;
	private final SegmentsEntry _segmentsEntry;

}