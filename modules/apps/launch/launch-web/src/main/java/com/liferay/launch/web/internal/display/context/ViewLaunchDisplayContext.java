/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.web.internal.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.URLItemSelectorReturnType;
import com.liferay.launch.entry.manager.LaunchEntryManager;
import com.liferay.launch.entry.manager.LaunchEntryManagerRegistry;
import com.liferay.launch.web.internal.constants.LaunchFDSNames;
import com.liferay.launch.web.internal.constants.LaunchPortletKeys;
import com.liferay.layout.item.selector.LayoutItemSelectorCriterion;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.time.Instant;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Alejandro Tardín
 */
public class ViewLaunchDisplayContext {

	public ViewLaunchDisplayContext(
		HttpServletRequest httpServletRequest, ItemSelector itemSelector,
		LaunchEntryManagerRegistry launchEntryManagerRegistry,
		LiferayPortletResponse liferayPortletResponse,
		ObjectEntryLocalService objectEntryLocalService) {

		_httpServletRequest = httpServletRequest;
		_itemSelector = itemSelector;
		_launchEntryManagerRegistry = launchEntryManagerRegistry;
		_liferayPortletResponse = liferayPortletResponse;
		_objectEntryLocalService = objectEntryLocalService;
	}

	public String getAPIURL() {
		String filterString = URLCodec.encodeURL(
			StringBundler.concat(
				"r_launchSetToLaunchEntries_c_launchSetId eq '",
				getLaunchSetId(), "'"));

		return "/o/launch-entries?filter=" + filterString;
	}

	public Map<String, Object> getAdditionalProps() {
		return HashMapBuilder.<String, Object>put(
			"getLaunchEntrySummaryURL",
			_getResourceURL("/launch/get_launch_entry_summary")
		).put(
			"getLaunchEntryVersionsURL",
			_getResourceURL("/launch/get_launch_entry_versions")
		).put(
			"itemSelectedEventName", _getItemSelectedEventName()
		).put(
			"launchEntryManagers", _getLaunchEntryManagerMaps()
		).put(
			"launchSetId", getLaunchSetId()
		).put(
			"published", isPublished()
		).put(
			"resolveLaunchEntryURL",
			_getResourceURL("/launch/resolve_launch_entry")
		).build();
	}

	public String getBackURL() {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).buildString();
	}

	public CreationMenu getCreationMenu() {
		if (isPublished()) {
			return null;
		}

		CreationMenu creationMenu = new CreationMenu();

		Locale locale = _getLocale();

		for (LaunchEntryManager launchEntryManager :
				_getLaunchEntryManagers()) {

			creationMenu.addPrimaryDropdownItem(
				dropdownItem -> dropdownItem.setLabel(
					launchEntryManager.getLabel(locale)));
		}

		return creationMenu;
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		if (isPublished()) {
			return List.of();
		}

		return List.of(
			new FDSActionDropdownItem(
				"#", "trash", "delete",
				LanguageUtil.get(_httpServletRequest, "remove"), "get", null,
				null));
	}

	public String getFDSName() {
		return LaunchFDSNames.LAUNCH_ENTRIES;
	}

	public long getLaunchSetId() {
		if (_launchSetId == null) {
			_launchSetId = ParamUtil.getLong(
				_httpServletRequest, "launchSetId");
		}

		return _launchSetId;
	}

	public String getLaunchSetName() {
		return ParamUtil.getString(_httpServletRequest, "launchSetName");
	}

	public String getPreviewPageItemSelectedEventName() {
		String portletNamespace = PortalUtil.getPortletNamespace(
			LaunchPortletKeys.LAUNCH);

		return portletNamespace + "selectPreviewPage";
	}

	public String getPreviewPageItemSelectorURL() {
		ThemeDisplay themeDisplay = _getThemeDisplay();

		LayoutItemSelectorCriterion layoutItemSelectorCriterion =
			new LayoutItemSelectorCriterion();

		layoutItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new URLItemSelectorReturnType());
		layoutItemSelectorCriterion.setShowBreadcrumb(true);
		layoutItemSelectorCriterion.setShowPrivatePages(true);
		layoutItemSelectorCriterion.setShowPublicPages(true);

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(_httpServletRequest),
				themeDisplay.getSiteGroup(), themeDisplay.getSiteGroupId(),
				getPreviewPageItemSelectedEventName(),
				layoutItemSelectorCriterion));
	}

	public String getPublishLaunchURL() {
		return PortletURLBuilder.createActionURL(
			_liferayPortletResponse
		).setActionName(
			"/launch/publish_launch"
		).setMVCRenderCommandName(
			"/launch/view_launch"
		).setRedirect(
			PortalUtil.getCurrentCompleteURL(_httpServletRequest)
		).setParameter(
			"launchSetId", getLaunchSetId()
		).setParameter(
			"launchSetName", getLaunchSetName()
		).buildString();
	}

	public String getScheduleLaunchURL() {
		return PortletURLBuilder.createActionURL(
			_liferayPortletResponse
		).setActionName(
			"/launch/schedule_launch"
		).setRedirect(
			PortalUtil.getCurrentCompleteURL(_httpServletRequest)
		).setParameter(
			"launchSetId", getLaunchSetId()
		).buildString();
	}

	public String getScheduledDate() {
		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
			getLaunchSetId());

		if ((objectEntry == null) ||
			(objectEntry.getStatus() != WorkflowConstants.STATUS_SCHEDULED)) {

			return null;
		}

		Map<String, Serializable> values = objectEntry.getValues();

		Date scheduledDate = (Date)values.get("scheduledDate");

		if (scheduledDate == null) {
			return null;
		}

		Instant instant = scheduledDate.toInstant();

		return instant.toString();
	}

	public String getUnscheduleLaunchURL() {
		return PortletURLBuilder.createActionURL(
			_liferayPortletResponse
		).setActionName(
			"/launch/unschedule_launch"
		).setRedirect(
			PortalUtil.getCurrentCompleteURL(_httpServletRequest)
		).setParameter(
			"launchSetId", getLaunchSetId()
		).buildString();
	}

	public boolean isPublished() {
		if (_published == null) {
			ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
				getLaunchSetId());

			_published =
				(objectEntry != null) &&
				(objectEntry.getStatus() == WorkflowConstants.STATUS_APPROVED);
		}

		return _published;
	}

	private String _getClassName(LaunchEntryManager launchEntryManager) {
		Class<?> modelClass = launchEntryManager.getModelClass();

		return modelClass.getName();
	}

	private String _getItemSelectedEventName() {
		String portletNamespace = PortalUtil.getPortletNamespace(
			LaunchPortletKeys.LAUNCH);

		return portletNamespace + "selectLaunchEntry";
	}

	private List<Map<String, Object>> _getLaunchEntryManagerMaps() {
		ThemeDisplay themeDisplay = _getThemeDisplay();

		return TransformUtil.transform(
			_getLaunchEntryManagers(),
			launchEntryManager -> HashMapBuilder.<String, Object>put(
				"className", _getClassName(launchEntryManager)
			).put(
				"itemSelectorURL",
				String.valueOf(
					_itemSelector.getItemSelectorURL(
						RequestBackedPortletURLFactoryUtil.create(
							_httpServletRequest),
						themeDisplay.getScopeGroup(),
						themeDisplay.getScopeGroupId(),
						_getItemSelectedEventName(),
						launchEntryManager.getItemSelectorCriterion()))
			).put(
				"label", launchEntryManager.getLabel(themeDisplay.getLocale())
			).build());
	}

	private List<LaunchEntryManager> _getLaunchEntryManagers() {
		Locale locale = _getLocale();

		List<LaunchEntryManager> launchEntryManagers =
			_launchEntryManagerRegistry.getLaunchEntryManagers();

		ListUtil.sort(
			launchEntryManagers,
			(launchEntryManager1, launchEntryManager2) -> {
				String label1 = launchEntryManager1.getLabel(locale);
				String label2 = launchEntryManager2.getLabel(locale);

				return label1.compareTo(label2);
			});

		return launchEntryManagers;
	}

	private Locale _getLocale() {
		ThemeDisplay themeDisplay = _getThemeDisplay();

		return themeDisplay.getLocale();
	}

	private String _getResourceURL(String resourceID) {
		return ResourceURLBuilder.createResourceURL(
			_liferayPortletResponse
		).setResourceID(
			resourceID
		).buildString();
	}

	private ThemeDisplay _getThemeDisplay() {
		return (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	private final HttpServletRequest _httpServletRequest;
	private final ItemSelector _itemSelector;
	private final LaunchEntryManagerRegistry _launchEntryManagerRegistry;
	private Long _launchSetId;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private Boolean _published;

}