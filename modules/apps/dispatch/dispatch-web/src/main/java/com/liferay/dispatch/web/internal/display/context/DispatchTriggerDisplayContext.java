/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.web.internal.display.context;

import com.liferay.dispatch.constants.DispatchPortletKeys;
import com.liferay.dispatch.executor.DispatchTaskExecutorRegistry;
import com.liferay.dispatch.metadata.DispatchTriggerMetadata;
import com.liferay.dispatch.metadata.DispatchTriggerMetadataProvider;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.ViewTypeItemList;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.FastDateFormatConstants;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;

import java.text.Format;

import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author guywandji
 * @author Alessio Antonio Rendina
 */
public class DispatchTriggerDisplayContext extends BaseDisplayContext {

	public DispatchTriggerDisplayContext(
		DispatchTaskExecutorRegistry dispatchTaskExecutorRegistry,
		DispatchTriggerLocalService dispatchTriggerLocalService,
		DispatchTriggerMetadataProvider dispatchTriggerMetadataProvider,
		RenderRequest renderRequest) {

		super(renderRequest);

		_dispatchTaskExecutorRegistry = dispatchTaskExecutorRegistry;
		_dispatchTriggerLocalService = dispatchTriggerLocalService;
		_dispatchTriggerMetadataProvider = dispatchTriggerMetadataProvider;
	}

	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.add(
			dropdownItem -> {
				dropdownItem.putData("action", "deleteEntries");
				dropdownItem.setIcon("trash");
				dropdownItem.setLabel(
					LanguageUtil.get(
						dispatchRequestHelper.getLocale(), "delete"));
				dropdownItem.setQuickAction(true);
			}
		).build();
	}

	public CreationMenu getCreationMenu() {
		CreationMenu creationMenu = new CreationMenu();

		for (String dispatchTaskExecutorType :
				_dispatchTaskExecutorRegistry.getDispatchTaskExecutorTypes()) {

			if (_dispatchTaskExecutorRegistry.isHiddenInUI(
					dispatchTaskExecutorType)) {

				continue;
			}

			creationMenu.addDropdownItem(
				dropdownItem -> {
					dropdownItem.setHref(
						PortletURLBuilder.createRenderURL(
							dispatchRequestHelper.getLiferayPortletResponse()
						).setMVCRenderCommandName(
							"/dispatch/edit_dispatch_trigger"
						).setCMD(
							Constants.ADD
						).setRedirect(
							dispatchRequestHelper.getCurrentURL()
						).setParameter(
							"dispatchTaskExecutorType", dispatchTaskExecutorType
						).buildRenderURL());
					dropdownItem.setLabel(
						getDispatchTaskExecutorName(
							dispatchTaskExecutorType,
							dispatchRequestHelper.getLocale()));
				});
		}

		return creationMenu;
	}

	public String getDispatchTaskExecutorName(
		String dispatchTaskExecutorType, Locale locale) {

		return LanguageUtil.get(
			locale,
			_dispatchTaskExecutorRegistry.fetchDispatchTaskExecutorName(
				dispatchTaskExecutorType));
	}

	public DispatchTrigger getDispatchTrigger() {
		return dispatchRequestHelper.getDispatchTrigger();
	}

	public DispatchTriggerMetadata getDispatchTriggerMetadata(
		long dispatchTriggerId) {

		return _dispatchTriggerMetadataProvider.getDispatchTriggerMetadata(
			dispatchTriggerId);
	}

	public String getNextFireDateString(DispatchTrigger dispatchTrigger) {
		DispatchTriggerMetadata dispatchTriggerMetadata =
			getDispatchTriggerMetadata(dispatchTrigger.getDispatchTriggerId());

		if (!dispatchTriggerMetadata.isDispatchTaskExecutorReady() ||
			(dispatchTrigger.getNextFireDate() == null)) {

			return LanguageUtil.get(
				dispatchRequestHelper.getRequest(), "not-scheduled");
		}

		TimeZone timeZone = null;

		String timeZoneId = dispatchTrigger.getTimeZoneId();

		if (Validator.isNotNull(timeZoneId)) {
			timeZone = TimeZone.getTimeZone(timeZoneId);
		}
		else {
			timeZone = TimeZoneUtil.getDefault();
		}

		Format fastDateTimeFormat = FastDateFormatFactoryUtil.getDateTime(
			FastDateFormatConstants.SHORT, FastDateFormatConstants.LONG,
			dispatchRequestHelper.getLocale(), timeZone);

		return fastDateTimeFormat.format(dispatchTrigger.getNextFireDate());
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			dispatchRequestHelper.getRequest(), DispatchPortletKeys.DISPATCH,
			"trigger-order-by-col", "modified-date");

		return _orderByCol;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			dispatchRequestHelper.getRequest(), DispatchPortletKeys.DISPATCH,
			"trigger-order-by-type", "desc");

		return _orderByType;
	}

	public PortletURL getPortletURL() {
		LiferayPortletResponse liferayPortletResponse =
			dispatchRequestHelper.getLiferayPortletResponse();

		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		String delta = ParamUtil.getString(
			dispatchRequestHelper.getRequest(), "delta");

		if (Validator.isNotNull(delta)) {
			portletURL.setParameter("delta", delta);
		}

		String deltaEntry = ParamUtil.getString(
			dispatchRequestHelper.getRequest(), "deltaEntry");

		if (Validator.isNotNull(deltaEntry)) {
			portletURL.setParameter("deltaEntry", deltaEntry);
		}

		return portletURL;
	}

	public RowChecker getRowChecker() {
		if (_rowChecker == null) {
			_rowChecker = new EmptyOnClickRowChecker(
				dispatchRequestHelper.getLiferayPortletResponse());
		}

		return _rowChecker;
	}

	public SearchContainer<DispatchTrigger> getSearchContainer() {
		if (_searchContainer != null) {
			return _searchContainer;
		}

		_searchContainer = new SearchContainer<>(
			dispatchRequestHelper.getLiferayPortletRequest(), getPortletURL(),
			null, null);

		_searchContainer.setEmptyResultsMessage("no-items-were-found");
		_searchContainer.setOrderByCol(getOrderByCol());
		_searchContainer.setOrderByComparator(null);
		_searchContainer.setOrderByType(getOrderByType());
		_searchContainer.setResultsAndTotal(
			ListUtil.filter(
				_dispatchTriggerLocalService.getDispatchTriggers(
					dispatchRequestHelper.getCompanyId(), QueryUtil.ALL_POS,
					QueryUtil.ALL_POS),
				dispatchTrigger -> {
					UnicodeProperties unicodeProperties =
						dispatchTrigger.
							getDispatchTaskSettingsUnicodeProperties();

					if (!unicodeProperties.containsKey("featureFlagKey") ||
						FeatureFlagManagerUtil.isEnabled(
							unicodeProperties.getProperty("featureFlagKey"))) {

						return true;
					}

					return false;
				}));
		_searchContainer.setRowChecker(getRowChecker());

		return _searchContainer;
	}

	public int getTotalItems() {
		SearchContainer<DispatchTrigger> searchContainer = getSearchContainer();

		return searchContainer.getTotal();
	}

	public ViewTypeItemList getViewTypeItems() {
		return new ViewTypeItemList(getPortletURL(), "list") {
			{
				addTableViewTypeItem();
			}
		};
	}

	public boolean isClusterModeSingle(String type) {
		return _dispatchTaskExecutorRegistry.isClusterModeSingle(type);
	}

	private final DispatchTaskExecutorRegistry _dispatchTaskExecutorRegistry;
	private final DispatchTriggerLocalService _dispatchTriggerLocalService;
	private final DispatchTriggerMetadataProvider
		_dispatchTriggerMetadataProvider;
	private String _orderByCol;
	private String _orderByType;
	private RowChecker _rowChecker;
	private SearchContainer<DispatchTrigger> _searchContainer;

}