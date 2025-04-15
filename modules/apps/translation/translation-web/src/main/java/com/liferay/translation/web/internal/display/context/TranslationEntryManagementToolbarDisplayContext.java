/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.display.context.SearchContainerManagementToolbarDisplayContext;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.LabelItemListBuilder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.translation.model.TranslationEntry;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adolfo Pérez
 */
public class TranslationEntryManagementToolbarDisplayContext
	extends SearchContainerManagementToolbarDisplayContext {

	public TranslationEntryManagementToolbarDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		SearchContainer<?> searchContainer) {

		super(
			httpServletRequest, liferayPortletRequest, liferayPortletResponse,
			searchContainer);

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData(
					"action", "deleteSelectedTranslationEntries");

				dropdownItem.putData(
					"delete-translation-entries-url",
					PortletURLBuilder.createActionURL(
						liferayPortletResponse
					).setActionName(
						"/translation/delete_translation_entry"
					).buildString());

				dropdownItem.setIcon("times-circle");
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "delete"));
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
			"orderByCol", getOrderByCol()
		).setParameter(
			"orderByType", getOrderByType()
		).buildString();
	}

	@Override
	public List<LabelItem> getFilterLabelItems() {
		if (_getStatus() == WorkflowConstants.STATUS_ANY) {
			return null;
		}

		return LabelItemListBuilder.add(
			labelItem -> {
				labelItem.putData(
					"removeLabelURL",
					PortletURLBuilder.create(
						PortletURLUtil.clone(
							getPortletURL(), liferayPortletResponse)
					).setParameter(
						"status", (String)null
					).buildString());

				labelItem.setDismissible(true);
				labelItem.setLabel(
					WorkflowConstants.getStatusLabel(_getStatus()));
			}
		).build();
	}

	@Override
	public List<DropdownItem> getFilterNavigationDropdownItems() {
		return _getFilterStatusDropdownItems();
	}

	@Override
	public String getSearchActionURL() {
		return PortletURLBuilder.createRenderURL(
			liferayPortletResponse
		).setParameter(
			"orderByCol", getOrderByCol()
		).setParameter(
			"orderByType", getOrderByType()
		).buildString();
	}

	@Override
	public String getSearchContainerId() {
		return "searchContainer";
	}

	@Override
	protected String getFilterNavigationDropdownItemsLabel() {
		return LanguageUtil.get(httpServletRequest, "filter-by-status");
	}

	@Override
	protected String[] getOrderByKeys() {
		return new String[] {"modified-date", "status", "title"};
	}

	private List<DropdownItem> _getFilterStatusDropdownItems() {
		return new DropdownItemList() {
			{
				for (int status : _getStatuses()) {
					add(
						dropdownItem -> {
							dropdownItem.setActive(_getStatus() == status);
							dropdownItem.setHref(
								getPortletURL(), "status",
								String.valueOf(status));
							dropdownItem.setLabel(
								LanguageUtil.get(
									httpServletRequest,
									WorkflowConstants.getStatusLabel(status)));
						});
				}
			}
		};
	}

	private int _getStatus() {
		if (_status != null) {
			return _status;
		}

		_status = ParamUtil.getInteger(
			httpServletRequest, "status", WorkflowConstants.STATUS_ANY);

		return _status;
	}

	private List<Integer> _getStatuses() {
		List<Integer> statuses = new ArrayList<>();

		statuses.add(WorkflowConstants.STATUS_ANY);
		statuses.add(WorkflowConstants.STATUS_DRAFT);

		int workflowDefinitionLinksCount =
			WorkflowDefinitionLinkLocalServiceUtil.
				getWorkflowDefinitionLinksCount(
					_themeDisplay.getCompanyId(),
					_themeDisplay.getScopeGroupId(),
					TranslationEntry.class.getName());

		if (workflowDefinitionLinksCount > 0) {
			statuses.add(WorkflowConstants.STATUS_PENDING);
			statuses.add(WorkflowConstants.STATUS_DENIED);
		}

		statuses.add(WorkflowConstants.STATUS_APPROVED);

		return statuses;
	}

	private Integer _status;
	private final ThemeDisplay _themeDisplay;

}