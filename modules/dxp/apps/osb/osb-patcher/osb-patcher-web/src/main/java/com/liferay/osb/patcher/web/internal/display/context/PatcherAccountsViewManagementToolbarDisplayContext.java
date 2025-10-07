/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.osb.patcher.constants.PatcherActionKeys;
import com.liferay.osb.patcher.constants.PatcherBuildConstants;
import com.liferay.osb.patcher.constants.WorkflowConstants;
import com.liferay.osb.patcher.model.PatcherBuild;
import com.liferay.osb.patcher.model.PatcherProductVersion;
import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.osb.patcher.permission.resource.PatcherPermission;
import com.liferay.osb.patcher.service.PatcherProductVersionLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherProjectVersionLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Eudaldo Alonso
 */
public class PatcherAccountsViewManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public PatcherAccountsViewManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SearchContainer<PatcherBuild> searchContainer) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			searchContainer);
	}

	@Override
	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setKeywords(
			StringPool.BLANK
		).setParameter(
			"patcherProductVersionId", (String)null
		).setParameter(
			"patcherProjectVersionId", (String)null
		).setParameter(
			"status", (String)null
		).setParameter(
			"type", (String)null
		).buildString();
	}

	@Override
	public CreationMenu getCreationMenu() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!PatcherPermission.contains(
				themeDisplay.getPermissionChecker(), "builds",
				PatcherActionKeys.CREATE)) {

			return null;
		}

		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					liferayPortletResponse.createRenderURL(),
					"mvcRenderCommandName", "/patcher/add_builds",
					"patcherProductVersionId", _getPatcherProductVersionId(),
					"redirect", themeDisplay.getURLCurrent());
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "create-build"));
			}
		).build();
	}

	@Override
	public List<DropdownItem> getFilterDropdownItems() {
		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					_getFilterStatusDropdownItems());
				dropdownGroupItem.setLabel(
					LanguageUtil.get(httpServletRequest, "filter-by-status"));
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					_getFilterTypeDropdownItems());
				dropdownGroupItem.setLabel(
					LanguageUtil.get(httpServletRequest, "type"));
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					_getFilterProductVersionDropdownItems());
				dropdownGroupItem.setLabel(
					LanguageUtil.get(httpServletRequest, "product-version"));
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					getFilterNavigationDropdownItems());
				dropdownGroupItem.setLabel(
					LanguageUtil.get(httpServletRequest, "project-version"));
			}
		).build();
	}

	@Override
	public List<LabelItem> getFilterLabelItems() {
		return LabelItemListBuilder.add(
			() -> _getStatus() != WorkflowConstants.STATUS_ANY,
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						getPortletURL()
					).setParameter(
						"status", (String)null
					).buildString());

				labelItem.setDismissible(true);

				labelItem.setLabel(
					LanguageUtil.get(
						httpServletRequest,
						WorkflowConstants.getStatusLabel(_getStatus())));
			}
		).add(
			() -> _getType() != PatcherBuildConstants.TYPE_ANY,
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						getPortletURL()
					).setParameter(
						"type", (String)null
					).buildString());
				labelItem.setDismissible(true);
				labelItem.setLabel(
					PatcherBuildConstants.getTypeLabel(_getType()));
			}
		).add(
			() -> _getPatcherProductVersionId() > 0,
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						getPortletURL()
					).setParameter(
						"patcherProductVersionId", (String)null
					).buildString());

				labelItem.setDismissible(true);

				PatcherProductVersion patcherProductVersion =
					PatcherProductVersionLocalServiceUtil.
						fetchPatcherProductVersion(
							_getPatcherProductVersionId());

				labelItem.setLabel(patcherProductVersion.getName());
			}
		).add(
			() -> _getPatcherProjectVersionId() > 0,
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						getPortletURL()
					).setParameter(
						"patcherProjectVersionId", (String)null
					).buildString());

				labelItem.setDismissible(true);

				PatcherProjectVersion patcherProjectVersion =
					PatcherProjectVersionLocalServiceUtil.
						fetchPatcherProjectVersion(
							_getPatcherProjectVersionId());

				labelItem.setLabel(patcherProjectVersion.getName());
			}
		).build();
	}

	@Override
	public String getSearchActionURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setParameter(
			"patcherProductVersionId", _getPatcherProductVersionId()
		).setParameter(
			"patcherProjectVersionId", _getPatcherProjectVersionId()
		).setParameter(
			"status", _getStatus()
		).setParameter(
			"type", _getType()
		).buildString();
	}

	@Override
	public String getSortingURL() {
		return null;
	}

	@Override
	public Boolean isSelectable() {
		return false;
	}

	@Override
	protected List<DropdownItem> getFilterNavigationDropdownItems() {
		return new DropdownItemList() {
			{
				add(
					dropdownItem -> {
						dropdownItem.setActive(
							_getPatcherProjectVersionId() == 0);
						dropdownItem.setHref(
							getPortletURL(), "patcherProjectVersionId", 0);
						dropdownItem.setLabel(
							LanguageUtil.get(httpServletRequest, "any"));
					});

				for (PatcherProjectVersion patcherProjectVersion :
						PatcherProjectVersionLocalServiceUtil.
							getPatcherProjectVersions()) {

					add(
						dropdownItem -> {
							dropdownItem.setActive(
								_getPatcherProjectVersionId() ==
									patcherProjectVersion.
										getPatcherProjectVersionId());
							dropdownItem.setHref(
								getPortletURL(), "patcherProjectVersionId",
								patcherProjectVersion.
									getPatcherProjectVersionId());
							dropdownItem.setLabel(
								patcherProjectVersion.getName());
						});
				}
			}
		};
	}

	private List<DropdownItem> _getDropdownItems(
		Map<String, Integer> items, String parameter, int value) {

		return new DropdownItemList() {
			{
				for (Map.Entry<String, Integer> item : items.entrySet()) {
					add(
						dropdownItem -> {
							dropdownItem.setActive(value == item.getValue());
							dropdownItem.setHref(
								getPortletURL(), parameter, item.getValue());
							dropdownItem.setLabel(
								LanguageUtil.get(
									httpServletRequest, item.getKey()));
						});
				}
			}
		};
	}

	private List<DropdownItem> _getFilterProductVersionDropdownItems() {
		return new DropdownItemList() {
			{
				add(
					dropdownItem -> {
						dropdownItem.setActive(
							_getPatcherProductVersionId() == 0);
						dropdownItem.setHref(
							getPortletURL(), "patcherProductVersionId", 0);
						dropdownItem.setLabel(
							LanguageUtil.get(httpServletRequest, "any"));
					});

				for (PatcherProductVersion patcherProductVersion :
						PatcherProductVersionLocalServiceUtil.
							getPatcherProductVersions()) {

					add(
						dropdownItem -> {
							dropdownItem.setActive(
								_getPatcherProductVersionId() ==
									patcherProductVersion.
										getPatcherProductVersionId());
							dropdownItem.setHref(
								getPortletURL(), "patcherProductVersionId",
								patcherProductVersion.
									getPatcherProductVersionId());
							dropdownItem.setLabel(
								patcherProductVersion.getName());
						});
				}
			}
		};
	}

	private List<DropdownItem> _getFilterStatusDropdownItems() {
		return _getDropdownItems(
			HashMapBuilder.put(
				WorkflowConstants.LABEL_ANY, WorkflowConstants.STATUS_ANY
			).put(
				WorkflowConstants.LABEL_BUILD_COMPILING,
				WorkflowConstants.STATUS_BUILD_COMPILING
			).put(
				WorkflowConstants.LABEL_BUILD_COMPLETE,
				WorkflowConstants.STATUS_BUILD_COMPLETE
			).put(
				WorkflowConstants.LABEL_BUILD_CONFLICT,
				WorkflowConstants.STATUS_BUILD_CONFLICT
			).put(
				WorkflowConstants.LABEL_BUILD_FAILED,
				WorkflowConstants.STATUS_BUILD_FAILED
			).put(
				WorkflowConstants.LABEL_BUILD_MERGING,
				WorkflowConstants.STATUS_BUILD_MERGING
			).put(
				WorkflowConstants.LABEL_BUILD_READY_TO_RELEASE,
				WorkflowConstants.STATUS_BUILD_READY_TO_RELEASE
			).put(
				WorkflowConstants.LABEL_BUILD_RELEASED,
				WorkflowConstants.STATUS_BUILD_RELEASED
			).build(),
			"status", _getStatus());
	}

	private List<DropdownItem> _getFilterTypeDropdownItems() {
		return _getDropdownItems(
			HashMapBuilder.put(
				PatcherBuildConstants.LABEL_ANY, PatcherBuildConstants.TYPE_ANY
			).put(
				PatcherBuildConstants.LABEL_DEBUG,
				PatcherBuildConstants.TYPE_DEBUG
			).put(
				PatcherBuildConstants.LABEL_FIX_PACK,
				PatcherBuildConstants.TYPE_FIX_PACK
			).put(
				PatcherBuildConstants.LABEL_IGNORE,
				PatcherBuildConstants.TYPE_IGNORE
			).put(
				PatcherBuildConstants.LABEL_OFFICIAL,
				PatcherBuildConstants.TYPE_OFFICIAL
			).build(),
			"type", _getType());
	}

	private long _getPatcherProductVersionId() {
		if (_patcherProductVersionId != null) {
			return _patcherProductVersionId;
		}

		_patcherProductVersionId = ParamUtil.getLong(
			httpServletRequest, "patcherProductVersionId");

		return _patcherProductVersionId;
	}

	private long _getPatcherProjectVersionId() {
		if (_patcherProjectVersionId != null) {
			return _patcherProjectVersionId;
		}

		_patcherProjectVersionId = ParamUtil.getLong(
			httpServletRequest, "patcherProjectVersionId");

		return _patcherProjectVersionId;
	}

	private int _getStatus() {
		if (_status != null) {
			return _status;
		}

		_status = ParamUtil.getInteger(
			httpServletRequest, "status", WorkflowConstants.STATUS_ANY);

		return _status;
	}

	private int _getType() {
		if (_type != null) {
			return _type;
		}

		_type = ParamUtil.getInteger(
			httpServletRequest, "type", PatcherBuildConstants.TYPE_ANY);

		return _type;
	}

	private Long _patcherProductVersionId;
	private Long _patcherProjectVersionId;
	private Integer _status;
	private Integer _type;

}