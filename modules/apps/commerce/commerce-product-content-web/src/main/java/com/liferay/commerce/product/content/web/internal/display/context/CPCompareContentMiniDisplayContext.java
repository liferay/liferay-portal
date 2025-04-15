/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.web.internal.display.context;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.content.render.list.CPContentListRenderer;
import com.liferay.commerce.product.content.render.list.CPContentListRendererRegistry;
import com.liferay.commerce.product.content.render.list.entry.CPContentListEntryRenderer;
import com.liferay.commerce.product.content.render.list.entry.CPContentListEntryRendererRegistry;
import com.liferay.commerce.product.content.web.internal.configuration.CPCompareContentMiniPortletInstanceConfiguration;
import com.liferay.commerce.product.data.source.CPDataSourceResult;
import com.liferay.commerce.product.display.context.helper.CPRequestHelper;
import com.liferay.commerce.product.type.CPType;
import com.liferay.commerce.product.type.CPTypeRegistry;
import com.liferay.commerce.product.util.CPCompareHelper;
import com.liferay.commerce.product.util.CPDefinitionHelper;
import com.liferay.commerce.util.CommerceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alessio Antonio Rendina
 */
public class CPCompareContentMiniDisplayContext {

	public CPCompareContentMiniDisplayContext(
			ConfigurationProvider configurationProvider,
			CPCompareHelper cpCompareHelper,
			CPContentListEntryRendererRegistry
				cpContentListEntryRendererRegistry,
			CPContentListRendererRegistry cpContentListRendererRegistry,
			CPDefinitionHelper cpDefinitionHelper,
			CPTypeRegistry cpTypeRegistry, GroupLocalService groupLocalService,
			HttpServletRequest httpServletRequest)
		throws PortalException {

		_configurationProvider = configurationProvider;
		_cpCompareHelper = cpCompareHelper;
		_cpContentListEntryRendererRegistry =
			cpContentListEntryRendererRegistry;
		_cpContentListRendererRegistry = cpContentListRendererRegistry;
		_cpDefinitionHelper = cpDefinitionHelper;
		_cpTypeRegistry = cpTypeRegistry;
		_groupLocalService = groupLocalService;

		_cpRequestHelper = new CPRequestHelper(httpServletRequest);

		_cpCompareContentMiniPortletInstanceConfiguration =
			configurationProvider.getPortletInstanceConfiguration(
				CPCompareContentMiniPortletInstanceConfiguration.class,
				_cpRequestHelper.getThemeDisplay());

		AccountEntry accountEntry = null;

		CommerceContext commerceContext =
			(CommerceContext)httpServletRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		if (commerceContext != null) {
			accountEntry = commerceContext.getAccountEntry();
		}

		if (accountEntry != null) {
			HttpServletRequest originalHttpServletRequest =
				PortalUtil.getOriginalServletRequest(httpServletRequest);

			_cpDefinitionIds = new ArrayList<>(
				cpCompareHelper.getCPDefinitionIds(
					commerceContext.getCommerceChannelGroupId(),
					accountEntry.getAccountEntryId(),
					CookiesManagerUtil.getCookieValue(
						cpCompareHelper.getCPDefinitionIdsCookieKey(
							commerceContext.getCommerceChannelGroupId()),
						originalHttpServletRequest)));
		}
		else {
			_cpDefinitionIds = new ArrayList<>();
		}
	}

	public Map<String, String> getCPContentListEntryRendererKeys() {
		Map<String, String> cpContentListEntryRendererKeys = new HashMap<>();

		for (CPType cpType : getCPTypes()) {
			String cpTypeName = cpType.getName();

			cpContentListEntryRendererKeys.put(
				cpTypeName, getCPTypeListEntryRendererKey(cpTypeName));
		}

		return cpContentListEntryRendererKeys;
	}

	public List<CPContentListEntryRenderer> getCPContentListEntryRenderers(
		String cpType) {

		return _cpContentListEntryRendererRegistry.
			getCPContentListEntryRenderers(
				CPPortletKeys.CP_COMPARE_CONTENT_MINI_WEB, cpType);
	}

	public String getCPContentListRendererKey() {
		RenderRequest renderRequest = _cpRequestHelper.getRenderRequest();

		PortletPreferences portletPreferences = renderRequest.getPreferences();

		String value = portletPreferences.getValue(
			"cpContentListRendererKey", null);

		if (Validator.isNotNull(value)) {
			return value;
		}

		List<CPContentListRenderer> cpContentListRenderers =
			getCPContentListRenderers();

		if (cpContentListRenderers.isEmpty()) {
			return StringPool.BLANK;
		}

		CPContentListRenderer cpContentListRenderer =
			cpContentListRenderers.get(0);

		if (cpContentListRenderer == null) {
			return StringPool.BLANK;
		}

		return cpContentListRenderer.getKey();
	}

	public List<CPContentListRenderer> getCPContentListRenderers() {
		return _cpContentListRendererRegistry.getCPContentListRenderers(
			CPPortletKeys.CP_COMPARE_CONTENT_MINI_WEB);
	}

	public CPDataSourceResult getCPDataSourceResult() throws PortalException {
		List<CPCatalogEntry> cpCatalogEntries = new ArrayList<>();

		HttpServletRequest httpServletRequest = _cpRequestHelper.getRequest();

		CommerceContext commerceContext =
			(CommerceContext)httpServletRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		for (Long cpDefinitionId : _cpDefinitionIds) {
			cpCatalogEntries.add(
				_cpDefinitionHelper.getCPCatalogEntry(
					CommerceUtil.getCommerceAccountId(commerceContext),
					commerceContext.getCommerceChannelGroupId(), cpDefinitionId,
					_cpRequestHelper.getLocale()));
		}

		if (cpCatalogEntries.size() > getProductsLimit()) {
			cpCatalogEntries = cpCatalogEntries.subList(0, getProductsLimit());
		}

		return new CPDataSourceResult(
			cpCatalogEntries, cpCatalogEntries.size());
	}

	public String getCPTypeListEntryRendererKey(String cpType) {
		RenderRequest renderRequest = _cpRequestHelper.getRenderRequest();

		PortletPreferences portletPreferences = renderRequest.getPreferences();

		String value = portletPreferences.getValue(
			cpType + "--cpTypeListEntryRendererKey", null);

		if (Validator.isNotNull(value)) {
			return value;
		}

		List<CPContentListEntryRenderer> cpContentListEntryRenderers =
			getCPContentListEntryRenderers(cpType);

		if (cpContentListEntryRenderers.isEmpty()) {
			return StringPool.BLANK;
		}

		CPContentListEntryRenderer cpContentListEntryRenderer =
			cpContentListEntryRenderers.get(0);

		if (cpContentListEntryRenderer == null) {
			return StringPool.BLANK;
		}

		return cpContentListEntryRenderer.getKey();
	}

	public List<CPType> getCPTypes() {
		return _cpTypeRegistry.getCPTypes();
	}

	public String getDisplayStyle() {
		return _cpCompareContentMiniPortletInstanceConfiguration.displayStyle();
	}

	public long getDisplayStyleGroupId() {
		if (_displayStyleGroupId != null) {
			return _displayStyleGroupId;
		}

		String displayStyleGroupExternalReferenceCode =
			_cpCompareContentMiniPortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode();

		ThemeDisplay themeDisplay = _cpRequestHelper.getThemeDisplay();

		Group group = themeDisplay.getScopeGroup();

		if (Validator.isNotNull(displayStyleGroupExternalReferenceCode)) {
			group = _groupLocalService.fetchGroupByExternalReferenceCode(
				displayStyleGroupExternalReferenceCode,
				themeDisplay.getCompanyId());
		}

		if (group != null) {
			_displayStyleGroupId = group.getGroupId();
		}
		else {
			_displayStyleGroupId = themeDisplay.getScopeGroupId();
		}

		return _displayStyleGroupId;
	}

	public String getDisplayStyleGroupKey() {
		if (Validator.isNotNull(_displayStyleGroupKey)) {
			return _displayStyleGroupKey;
		}

		String displayStyleGroupExternalReferenceCode =
			_cpCompareContentMiniPortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode();

		ThemeDisplay themeDisplay = _cpRequestHelper.getThemeDisplay();

		Group group = themeDisplay.getScopeGroup();

		if (Validator.isNotNull(displayStyleGroupExternalReferenceCode)) {
			group = _groupLocalService.fetchGroupByExternalReferenceCode(
				displayStyleGroupExternalReferenceCode,
				themeDisplay.getCompanyId());
		}

		if (group != null) {
			_displayStyleGroupKey = group.getGroupKey();
		}
		else {
			_displayStyleGroupKey = StringPool.BLANK;
		}

		return _displayStyleGroupKey;
	}

	public int getProductsLimit() {
		return _cpCompareContentMiniPortletInstanceConfiguration.
			productsLimit();
	}

	public String getSelectionStyle() {
		return _cpCompareContentMiniPortletInstanceConfiguration.
			selectionStyle();
	}

	public boolean hasCommerceChannel() throws PortalException {
		HttpServletRequest httpServletRequest = _cpRequestHelper.getRequest();

		CommerceContext commerceContext =
			(CommerceContext)httpServletRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		if (commerceContext == null) {
			return false;
		}

		long commerceChannelId = commerceContext.getCommerceChannelId();

		if (commerceChannelId > 0) {
			return true;
		}

		return false;
	}

	public boolean isSelectionStyleADT() {
		String selectionStyle = getSelectionStyle();

		return selectionStyle.equals("adt");
	}

	public boolean isSelectionStyleCustomRenderer() {
		String selectionStyle = getSelectionStyle();

		return selectionStyle.equals("custom");
	}

	private final ConfigurationProvider _configurationProvider;
	private final CPCompareContentMiniPortletInstanceConfiguration
		_cpCompareContentMiniPortletInstanceConfiguration;
	private final CPCompareHelper _cpCompareHelper;
	private final CPContentListEntryRendererRegistry
		_cpContentListEntryRendererRegistry;
	private final CPContentListRendererRegistry _cpContentListRendererRegistry;
	private final CPDefinitionHelper _cpDefinitionHelper;
	private final List<Long> _cpDefinitionIds;
	private final CPRequestHelper _cpRequestHelper;
	private final CPTypeRegistry _cpTypeRegistry;
	private Long _displayStyleGroupId;
	private String _displayStyleGroupKey;
	private final GroupLocalService _groupLocalService;

}