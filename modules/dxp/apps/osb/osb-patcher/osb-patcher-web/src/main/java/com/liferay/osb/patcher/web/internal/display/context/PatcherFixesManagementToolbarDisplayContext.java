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
import com.liferay.osb.patcher.constants.PatcherFixConstants;
import com.liferay.osb.patcher.constants.WorkflowConstants;
import com.liferay.osb.patcher.model.PatcherFix;
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
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class PatcherFixesManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public PatcherFixesManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SearchContainer<PatcherFix> searchContainer) {

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
			"patcherProductVersionId", (String)null
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
				themeDisplay.getPermissionChecker(), "fixes",
				PatcherActionKeys.CREATE)) {

			return null;
		}

		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					liferayPortletResponse.createRenderURL(),
					"mvcRenderCommandName", "/patcher/add_fixes",
					"patcherProductVersionId", _getPatcherProductVersionId(),
					"redirect", themeDisplay.getURLCurrent());
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "create-fix"));
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
					LanguageUtil.get(httpServletRequest, "filter-by-type"));
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
			() -> _getType() >= 0,
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
					PatcherFixConstants.getTypeLabel(_getType()));
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
					_getStatus() == WorkflowConstants.STATUS_FIX_ADDING);
				dropdownItem.setHref(
					getPortletURL(), "status",
					WorkflowConstants.STATUS_FIX_ADDING);
				dropdownItem.setLabel(
					LanguageUtil.get(
						httpServletRequest,
						WorkflowConstants.LABEL_FIX_ADDING));
			}
		).add(
			dropdownItem -> {
				dropdownItem.setActive(
					_getStatus() == WorkflowConstants.STATUS_FIX_CONFLICT);
				dropdownItem.setHref(
					getPortletURL(), "status",
					WorkflowConstants.STATUS_FIX_CONFLICT);
				dropdownItem.setLabel(
					LanguageUtil.get(
						httpServletRequest,
						WorkflowConstants.LABEL_FIX_CONFLICT));
			}
		).add(
			dropdownItem -> {
				dropdownItem.setActive(
					_getStatus() == WorkflowConstants.STATUS_FIX_COMPLETE);
				dropdownItem.setHref(
					getPortletURL(), "status",
					WorkflowConstants.STATUS_FIX_COMPLETE);
				dropdownItem.setLabel(
					LanguageUtil.get(
						httpServletRequest,
						WorkflowConstants.LABEL_FIX_COMPLETE));
			}
		).add(
			dropdownItem -> {
				dropdownItem.setActive(
					_getStatus() == WorkflowConstants.STATUS_FIX_FAILED);
				dropdownItem.setHref(
					getPortletURL(), "status",
					WorkflowConstants.STATUS_FIX_FAILED);
				dropdownItem.setLabel(
					LanguageUtil.get(
						httpServletRequest,
						WorkflowConstants.LABEL_FIX_FAILED));
			}
		).add(
			dropdownItem -> {
				dropdownItem.setActive(
					_getStatus() ==
						WorkflowConstants.STATUS_FIX_REBASE_CONFLICT);
				dropdownItem.setHref(
					getPortletURL(), "status",
					WorkflowConstants.STATUS_FIX_REBASE_CONFLICT);
				dropdownItem.setLabel(
					LanguageUtil.get(
						httpServletRequest,
						WorkflowConstants.LABEL_FIX_REBASE_CONFLICT));
			}
		).add(
			dropdownItem -> {
				dropdownItem.setActive(
					_getStatus() == WorkflowConstants.STATUS_FIX_REBASING);
				dropdownItem.setHref(
					getPortletURL(), "status",
					WorkflowConstants.STATUS_FIX_REBASING);
				dropdownItem.setLabel(
					LanguageUtil.get(
						httpServletRequest,
						WorkflowConstants.LABEL_FIX_REBASING));
			}
		).build();
	}

	private List<DropdownItem> _getFilterTypeDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setActive(_getType() == -1);
				dropdownItem.setHref(getPortletURL(), "type", -1);
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "any"));
			}
		).add(
			dropdownItem -> {
				dropdownItem.setActive(
					_getType() == PatcherFixConstants.TYPE_PATCH);
				dropdownItem.setHref(
					getPortletURL(), "type", PatcherFixConstants.TYPE_PATCH);
				dropdownItem.setLabel(
					LanguageUtil.get(
						httpServletRequest, PatcherFixConstants.LABEL_PATCH));
			}
		).add(
			dropdownItem -> {
				dropdownItem.setActive(
					_getType() == PatcherFixConstants.TYPE_WORKAROUND);
				dropdownItem.setHref(
					getPortletURL(), "type",
					PatcherFixConstants.TYPE_WORKAROUND);
				dropdownItem.setLabel(
					LanguageUtil.get(
						httpServletRequest,
						PatcherFixConstants.LABEL_WORKAROUND));
			}
		).add(
			dropdownItem -> {
				dropdownItem.setActive(
					_getType() == PatcherFixConstants.TYPE_FIX_PACK);
				dropdownItem.setHref(
					getPortletURL(), "type", PatcherFixConstants.TYPE_FIX_PACK);
				dropdownItem.setLabel(
					LanguageUtil.get(
						httpServletRequest,
						PatcherFixConstants.LABEL_FIX_PACK));
			}
		).add(
			dropdownItem -> {
				dropdownItem.setActive(
					_getType() == PatcherFixConstants.TYPE_GENERATED);
				dropdownItem.setHref(
					getPortletURL(), "type",
					PatcherFixConstants.TYPE_GENERATED);
				dropdownItem.setLabel(
					LanguageUtil.get(
						httpServletRequest,
						PatcherFixConstants.LABEL_GENERATED));
			}
		).add(
			dropdownItem -> {
				dropdownItem.setActive(
					_getType() ==
						PatcherFixConstants.TYPE_GENERATED_PRIVATE_PUBLIC);
				dropdownItem.setHref(
					getPortletURL(), "type",
					PatcherFixConstants.TYPE_GENERATED_PRIVATE_PUBLIC);
				dropdownItem.setLabel(
					LanguageUtil.get(
						httpServletRequest,
						PatcherFixConstants.LABEL_GENERATED_PRIVATE_PUBLIC));
			}
		).add(
			dropdownItem -> {
				dropdownItem.setActive(
					_getType() == PatcherFixConstants.TYPE_EXCLUDED);
				dropdownItem.setHref(
					getPortletURL(), "type", PatcherFixConstants.TYPE_EXCLUDED);
				dropdownItem.setLabel(
					LanguageUtil.get(
						httpServletRequest,
						PatcherFixConstants.LABEL_EXCLUDED));
			}
		).add(
			dropdownItem -> {
				dropdownItem.setActive(
					_getType() == PatcherFixConstants.TYPE_REBASE);
				dropdownItem.setHref(
					getPortletURL(), "type", PatcherFixConstants.TYPE_REBASE);
				dropdownItem.setLabel(
					LanguageUtil.get(
						httpServletRequest, PatcherFixConstants.LABEL_REBASE));
			}
		).build();
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

		_type = ParamUtil.getInteger(httpServletRequest, "type", -1);

		return _type;
	}

	private Long _patcherProductVersionId;
	private Long _patcherProjectVersionId;
	private Integer _status;
	private Integer _type;

}