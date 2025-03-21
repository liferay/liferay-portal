/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashHandlerRegistryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.trash.constants.TrashPortletKeys;
import com.liferay.trash.model.TrashEntry;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Pavel Savinov
 */
public class TrashManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public TrashManagementToolbarDisplayContext(
			HttpServletRequest httpServletRequest,
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			TrashDisplayContext trashDisplayContext)
		throws PortalException {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			trashDisplayContext.getEntrySearch());

		_trashDisplayContext = trashDisplayContext;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						dropdownItem -> {
							dropdownItem.putData(
								"action", "restoreSelectedEntries");
							dropdownItem.setIcon("restore");
							dropdownItem.setLabel(
								LanguageUtil.get(
									httpServletRequest, "restore"));
							dropdownItem.setQuickAction(true);
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						dropdownItem -> {
							dropdownItem.putData(
								"action", "deleteSelectedEntries");
							dropdownItem.setIcon("trash");
							dropdownItem.setLabel(
								LanguageUtil.get(httpServletRequest, "delete"));
							dropdownItem.setQuickAction(true);
						}
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).build();
	}

	@Override
	public Map<String, Object> getAdditionalProps() {
		return HashMapBuilder.<String, Object>put(
			"restoreEntriesURL",
			PortletURLBuilder.createActionURL(
				liferayPortletResponse, TrashPortletKeys.TRASH
			).setActionName(
				"restoreEntries"
			).setRedirect(
				_themeDisplay.getURLCurrent()
			).buildString()
		).build();
	}

	public String getAvailableActions(TrashEntry trashEntry)
		throws PortalException {

		if (_isDeletable(trashEntry)) {
			if (CTCollectionThreadLocal.isProductionMode()) {
				return "deleteSelectedEntries,restoreSelectedEntries";
			}

			return "restoreSelectedEntries";
		}

		return StringPool.BLANK;
	}

	@Override
	public String getClearResultsURL() {
		return PortletURLBuilder.create(
			getPortletURL()
		).setKeywords(
			StringPool.BLANK
		).setNavigation(
			StringPool.BLANK
		).buildString();
	}

	@Override
	public String getComponentId() {
		return "trashWebManagementToolbar";
	}

	@Override
	public List<LabelItem> getFilterLabelItems() {
		return LabelItemListBuilder.add(
			() ->
				Validator.isNotNull(getNavigation()) &&
				!Objects.equals(getNavigation(), "all"),
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						PortletURLUtil.clone(
							currentURLObj, liferayPortletResponse)
					).setNavigation(
						(String)null
					).buildString());

				labelItem.setCloseable(true);
				labelItem.setLabel(
					ResourceActionsUtil.getModelResource(
						_themeDisplay.getLocale(), getNavigation()));
			}
		).build();
	}

	@Override
	public String getInfoPanelId() {
		return "infoPanelId";
	}

	@Override
	public String getSearchContainerId() {
		return "trash";
	}

	@Override
	protected String getDisplayStyle() {
		return _trashDisplayContext.getDisplayStyle();
	}

	@Override
	protected String[] getDisplayViews() {
		return new String[] {"list", "descriptive", "icon"};
	}

	@Override
	protected List<DropdownItem> getFilterNavigationDropdownItems() {
		return new DropdownItemList() {
			{
				add(
					dropdownItem -> {
						dropdownItem.setActive(
							Objects.equals(getNavigation(), "all"));
						dropdownItem.setHref(
							getPortletURL(), "navigation", "all");
						dropdownItem.setLabel(
							LanguageUtil.get(httpServletRequest, "all"));
					});

				for (TrashHandler trashHandler :
						TrashHandlerRegistryUtil.getTrashHandlers()) {

					add(
						dropdownItem -> {
							dropdownItem.setActive(
								Objects.equals(
									getNavigation(),
									trashHandler.getClassName()));
							dropdownItem.setHref(
								getPortletURL(), "navigation",
								trashHandler.getClassName());
							dropdownItem.setLabel(
								ResourceActionsUtil.getModelResource(
									_themeDisplay.getLocale(),
									trashHandler.getClassName()));
						});
				}
			}
		};
	}

	@Override
	protected String[] getOrderByKeys() {
		String[] orderColumns = {"removed-date"};

		if (_trashDisplayContext.isSearch()) {
			orderColumns = ArrayUtil.append(orderColumns, "relevance");
		}

		return orderColumns;
	}

	private boolean _isDeletable(TrashEntry trashEntry) throws PortalException {
		TrashHandler trashHandler = TrashHandlerRegistryUtil.getTrashHandler(
			trashEntry.getClassName());

		return trashHandler.isDeletable(trashEntry.getClassPK());
	}

	private final ThemeDisplay _themeDisplay;
	private final TrashDisplayContext _trashDisplayContext;

}