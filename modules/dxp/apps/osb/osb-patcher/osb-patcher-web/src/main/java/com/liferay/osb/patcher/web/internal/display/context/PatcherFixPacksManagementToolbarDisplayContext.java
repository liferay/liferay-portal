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
import com.liferay.osb.patcher.constants.WorkflowConstants;
import com.liferay.osb.patcher.model.PatcherFixComponent;
import com.liferay.osb.patcher.model.PatcherFixPack;
import com.liferay.osb.patcher.model.PatcherProjectVersion;
import com.liferay.osb.patcher.permission.resource.PatcherPermission;
import com.liferay.osb.patcher.service.PatcherFixComponentLocalServiceUtil;
import com.liferay.osb.patcher.service.PatcherProjectVersionLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class PatcherFixPacksManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public PatcherFixPacksManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SearchContainer<PatcherFixPack> searchContainer) {

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
			"patcherFixComponentId", (String)null
		).setParameter(
			"patcherProjectVersionId", (String)null
		).setParameter(
			"status", (String)null
		).buildString();
	}

	@Override
	public CreationMenu getCreationMenu() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!PatcherPermission.contains(
				themeDisplay.getPermissionChecker(), "fix_packs",
				PatcherActionKeys.CREATE)) {

			return null;
		}

		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					liferayPortletResponse.createRenderURL(),
					"mvcRenderCommandName", "/patcher/add_fix_packs",
					"redirect", themeDisplay.getURLCurrent());
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "create-fix-pack"));
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
					_getFilterComponentDropdownItems());
				dropdownGroupItem.setLabel(
					LanguageUtil.get(httpServletRequest, "component"));
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
			() -> _getPatcherFixComponentId() > 0,
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						getPortletURL()
					).setParameter(
						"patcherFixComponentId", (String)null
					).buildString());

				labelItem.setDismissible(true);

				PatcherFixComponent patcherFixComponent =
					PatcherFixComponentLocalServiceUtil.
						fetchPatcherFixComponent(_getPatcherFixComponentId());

				labelItem.setLabel(patcherFixComponent.getName());
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
			"patcherFixComponentId", _getPatcherFixComponentId()
		).setParameter(
			"patcherProjectVersionId", _getPatcherProjectVersionId()
		).setParameter(
			"status", _getStatus()
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

	private List<DropdownItem> _getFilterComponentDropdownItems() {
		return new DropdownItemList() {
			{
				add(
					dropdownItem -> {
						dropdownItem.setActive(
							Validator.isNull(_getPatcherFixComponentId()));
						dropdownItem.setHref(
							getPortletURL(), "patcherFixComponentId",
							StringPool.BLANK);
						dropdownItem.setLabel(
							LanguageUtil.get(httpServletRequest, "any"));
					});

				for (PatcherFixComponent patcherFixComponent :
						PatcherFixComponentLocalServiceUtil.
							getPatcherFixComponents()) {

					add(
						dropdownItem -> {
							dropdownItem.setActive(
								_getPatcherFixComponentId() ==
									patcherFixComponent.
										getPatcherFixComponentId());
							dropdownItem.setHref(
								getPortletURL(), "patcherFixComponentId",
								patcherFixComponent.getPatcherFixComponentId());
							dropdownItem.setLabel(
								patcherFixComponent.getName());
						});
				}
			}
		};
	}

	private List<DropdownItem> _getFilterStatusDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setActive(
					_getStatus() == WorkflowConstants.STATUS_ANY);
				dropdownItem.setHref(
					getPortletURL(), "status", WorkflowConstants.STATUS_ANY);
				dropdownItem.setLabel(
					LanguageUtil.get(
						httpServletRequest, WorkflowConstants.LABEL_ANY));
			}
		).add(
			dropdownItem -> {
				dropdownItem.setActive(
					_getStatus() == WorkflowConstants.STATUS_FIX_PACK_FROZEN);
				dropdownItem.setHref(
					getPortletURL(), "status",
					WorkflowConstants.STATUS_FIX_PACK_FROZEN);
				dropdownItem.setLabel(
					LanguageUtil.get(
						httpServletRequest,
						WorkflowConstants.LABEL_FIX_PACK_FROZEN));
			}
		).add(
			dropdownItem -> {
				dropdownItem.setActive(
					_getStatus() == WorkflowConstants.STATUS_FIX_PACK_RELEASED);
				dropdownItem.setHref(
					getPortletURL(), "status",
					WorkflowConstants.STATUS_FIX_PACK_RELEASED);
				dropdownItem.setLabel(
					LanguageUtil.get(
						httpServletRequest,
						WorkflowConstants.LABEL_FIX_PACK_RELEASED));
			}
		).add(
			dropdownItem -> {
				dropdownItem.setActive(
					_getStatus() ==
						WorkflowConstants.STATUS_FIX_PACK_UNDER_DEVELOPMENT);
				dropdownItem.setHref(
					getPortletURL(), "status",
					WorkflowConstants.STATUS_FIX_PACK_UNDER_DEVELOPMENT);
				dropdownItem.setLabel(
					LanguageUtil.get(
						httpServletRequest,
						WorkflowConstants.LABEL_FIX_PACK_UNDER_DEVELOPMENT));
			}
		).build();
	}

	private long _getPatcherFixComponentId() {
		if (_patcherFixComponentId != null) {
			return _patcherFixComponentId;
		}

		_patcherFixComponentId = ParamUtil.getLong(
			httpServletRequest, "patcherFixComponentId");

		return _patcherFixComponentId;
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

	private Long _patcherFixComponentId;
	private Long _patcherProjectVersionId;
	private Integer _status;

}