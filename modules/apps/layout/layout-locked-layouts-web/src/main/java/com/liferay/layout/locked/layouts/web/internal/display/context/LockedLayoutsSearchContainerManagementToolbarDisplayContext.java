/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.locked.layouts.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.layout.model.LockedLayoutType;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ListUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

/**
 * @author Lourdes Fernández Besada
 */
public class LockedLayoutsSearchContainerManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public LockedLayoutsSearchContainerManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		LockedLayoutsDisplayContext lockedLayoutsDisplayContext) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			lockedLayoutsDisplayContext.getSearchContainer());

		_lockedLayoutsDisplayContext = lockedLayoutsDisplayContext;
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData("action", "unlockLockedLayouts");
				dropdownItem.setIcon("unlock");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "unlock"));
				dropdownItem.setQuickAction(true);
			}
		).build();
	}

	@Override
	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setKeywords(
			StringPool.BLANK
		).setParameter(
			"orderByCol",
			LockedLayoutsDisplayContext.LockedLayoutOrder.LockedLayoutOrderType.
				LAST_AUTOSAVE.getValue()
		).setParameter(
			"orderByType", "desc"
		).setParameter(
			"type", StringPool.BLANK
		).buildString();
	}

	@Override
	public List<DropdownItem> getFilterDropdownItems() {
		DropdownItemList dropdownItemList = DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(_getFilterDropdownItems());
				dropdownGroupItem.setLabel(
					LanguageUtil.get(httpServletRequest, "filter-by-type"));
			}
		).build();

		List<DropdownItem> filterDropdownItems = super.getFilterDropdownItems();

		if (ListUtil.isNotEmpty(filterDropdownItems)) {
			dropdownItemList.addAll(filterDropdownItems);
		}

		return dropdownItemList;
	}

	@Override
	public List<LabelItem> getFilterLabelItems() {
		LockedLayoutType lockedLayoutType =
			_lockedLayoutsDisplayContext.getLockedLayoutType();

		if (lockedLayoutType == null) {
			return null;
		}

		return LabelItemListBuilder.add(
			() -> lockedLayoutType != null,
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						getPortletURL()
					).setParameter(
						"type", StringPool.BLANK
					).buildString());
				labelItem.setDismissible(true);
				labelItem.setLabel(
					LanguageUtil.get(
						httpServletRequest, lockedLayoutType.getValue()));
			}
		).build();
	}

	@Override
	public List<DropdownItem> getOrderDropdownItems() {
		return _getOrderDropdownItems();
	}

	@Override
	public String getSearchContainerId() {
		return "lockedLayoutsSearchContainer";
	}

	@Override
	public String getSortingOrder() {
		return _lockedLayoutsDisplayContext.getOrderByType();
	}

	@Override
	public String getSortingURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setParameter(
			"orderByCol", _lockedLayoutsDisplayContext.getOrderByCol()
		).setParameter(
			"orderByType", _getReverseOrderByType()
		).buildString();
	}

	@Override
	public Boolean isDisabled() {
		return !_lockedLayoutsDisplayContext.hasLockedLayouts() &&
			   (_lockedLayoutsDisplayContext.getLockedLayoutType() == null);
	}

	private List<DropdownItem> _getFilterDropdownItems() {
		List<DropdownItem> dropdownItems = DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.setHref(getPortletURL(), "type", StringPool.BLANK);
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "all"));
			}
		).build();

		for (LockedLayoutType lockedLayoutType : LockedLayoutType.values()) {
			dropdownItems.add(
				DropdownItemBuilder.setActive(
					Objects.equals(
						_lockedLayoutsDisplayContext.getLockedLayoutType(),
						lockedLayoutType)
				).setHref(
					getPortletURL(), "type", lockedLayoutType.getValue()
				).setLabel(
					LanguageUtil.get(
						httpServletRequest, lockedLayoutType.getValue())
				).build());
		}

		return dropdownItems;
	}

	private List<DropdownItem> _getOrderDropdownItems() {
		LockedLayoutsDisplayContext.LockedLayoutOrder lockedLayoutOrder =
			_lockedLayoutsDisplayContext.getLockedLayoutOrder();

		return new DropdownItemList() {
			{
				for (LockedLayoutsDisplayContext.LockedLayoutOrder.
						LockedLayoutOrderType lockedLayoutOrderType :
							LockedLayoutsDisplayContext.LockedLayoutOrder.
								LockedLayoutOrderType.values()) {

					add(
						dropdownItem -> {
							dropdownItem.setActive(
								Objects.equals(
									lockedLayoutOrderType,
									lockedLayoutOrder.
										getLockedLayoutOrderType()));
							dropdownItem.setHref(
								getPortletURL(), "orderByCol",
								lockedLayoutOrderType.getValue(), "orderByType",
								_lockedLayoutsDisplayContext.getOrderByType());
							dropdownItem.setLabel(
								LanguageUtil.get(
									httpServletRequest,
									lockedLayoutOrderType.getValue()));
						});
				}
			}
		};
	}

	private String _getReverseOrderByType() {
		if (Objects.equals(
				_lockedLayoutsDisplayContext.getOrderByType(), "asc")) {

			return "desc";
		}

		return "asc";
	}

	private final LockedLayoutsDisplayContext _lockedLayoutsDisplayContext;

}