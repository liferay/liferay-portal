/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.web.internal.display.context;

import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.content.render.list.CPContentListRenderer;
import com.liferay.commerce.product.content.render.list.CPContentListRendererRegistry;
import com.liferay.commerce.product.content.render.list.entry.CPContentListEntryRenderer;
import com.liferay.commerce.product.content.render.list.entry.CPContentListEntryRendererRegistry;
import com.liferay.commerce.product.content.web.internal.configuration.CPPublisherPortletInstanceConfiguration;
import com.liferay.commerce.product.content.web.internal.display.context.helper.CPContentRequestHelper;
import com.liferay.commerce.product.content.web.internal.helper.CPPublisherWebHelper;
import com.liferay.commerce.product.type.CPType;
import com.liferay.commerce.product.type.CPTypeRegistry;
import com.liferay.commerce.util.CommerceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Marco Leo
 * @author Alessio Antonio Rendina
 */
public class BaseCPPublisherDisplayContext {

	public BaseCPPublisherDisplayContext(
			ConfigurationProvider configurationProvider,
			CPContentListEntryRendererRegistry contentListEntryRendererRegistry,
			CPContentListRendererRegistry cpContentListRendererRegistry,
			CPPublisherWebHelper cpPublisherWebHelper,
			CPTypeRegistry cpTypeRegistry, GroupLocalService groupLocalService,
			HttpServletRequest httpServletRequest)
		throws PortalException {

		this.configurationProvider = configurationProvider;
		this.contentListEntryRendererRegistry =
			contentListEntryRendererRegistry;
		this.cpContentListRendererRegistry = cpContentListRendererRegistry;
		this.cpPublisherWebHelper = cpPublisherWebHelper;
		this.cpTypeRegistry = cpTypeRegistry;
		this.groupLocalService = groupLocalService;

		cpContentRequestHelper = new CPContentRequestHelper(httpServletRequest);

		cpPublisherPortletInstanceConfiguration =
			this.configurationProvider.getPortletInstanceConfiguration(
				CPPublisherPortletInstanceConfiguration.class,
				cpContentRequestHelper.getThemeDisplay());
	}

	public List<CPCatalogEntry> getCPCatalogEntries() throws Exception {
		HttpServletRequest httpServletRequest =
			cpContentRequestHelper.getRequest();

		CommerceContext commerceContext =
			(CommerceContext)httpServletRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		if (commerceContext == null) {
			return null;
		}

		return cpPublisherWebHelper.getCPCatalogEntries(
			CommerceUtil.getCommerceAccountId(commerceContext),
			commerceContext.getCommerceChannelGroupId(),
			cpContentRequestHelper.getPortletPreferences(),
			cpContentRequestHelper.getThemeDisplay());
	}

	public List<CPContentListEntryRenderer> getCPContentListEntryRenderers(
		String cpType) {

		return contentListEntryRendererRegistry.getCPContentListEntryRenderers(
			CPPortletKeys.CP_PUBLISHER_WEB, cpType);
	}

	public String getCPContentListRendererKey() {
		RenderRequest renderRequest = cpContentRequestHelper.getRenderRequest();

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
		return cpContentListRendererRegistry.getCPContentListRenderers(
			CPPortletKeys.CP_PUBLISHER_WEB);
	}

	public String getCPTypeListEntryRendererKey(String cpType) {
		RenderRequest renderRequest = cpContentRequestHelper.getRenderRequest();

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
		return cpTypeRegistry.getCPTypes();
	}

	public String getDataSource() {
		if (dataSource != null) {
			return dataSource;
		}

		PortletPreferences portletPreferences =
			cpContentRequestHelper.getPortletPreferences();

		dataSource = GetterUtil.getString(
			portletPreferences.getValue("dataSource", null));

		return dataSource;
	}

	public String getDisplayStyle() {
		return cpPublisherPortletInstanceConfiguration.displayStyle();
	}

	public long getDisplayStyleGroupId() {
		if (displayStyleGroupId != null) {
			return displayStyleGroupId;
		}

		String displayStyleGroupExternalReferenceCode =
			cpPublisherPortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode();

		ThemeDisplay themeDisplay = cpContentRequestHelper.getThemeDisplay();

		Group group = themeDisplay.getScopeGroup();

		if (Validator.isNotNull(displayStyleGroupExternalReferenceCode)) {
			group = groupLocalService.fetchGroupByExternalReferenceCode(
				displayStyleGroupExternalReferenceCode,
				themeDisplay.getCompanyId());
		}

		if (group != null) {
			displayStyleGroupId = group.getGroupId();
		}
		else {
			displayStyleGroupId = themeDisplay.getScopeGroupId();
		}

		return displayStyleGroupId;
	}

	public String getDisplayStyleGroupKey() {
		if (Validator.isNotNull(displayStyleGroupKey)) {
			return displayStyleGroupKey;
		}

		String displayStyleGroupExternalReferenceCode =
			cpPublisherPortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode();

		ThemeDisplay themeDisplay = cpContentRequestHelper.getThemeDisplay();

		Group group = themeDisplay.getScopeGroup();

		if (Validator.isNotNull(displayStyleGroupExternalReferenceCode)) {
			group = groupLocalService.fetchGroupByExternalReferenceCode(
				displayStyleGroupExternalReferenceCode,
				themeDisplay.getCompanyId());
		}

		if (group != null) {
			displayStyleGroupKey = group.getGroupKey();
		}
		else {
			displayStyleGroupKey = StringPool.BLANK;
		}

		return displayStyleGroupKey;
	}

	public int getPaginationDelta() {
		return cpPublisherPortletInstanceConfiguration.paginationDelta();
	}

	public String getRenderSelection() {
		if (renderSelection != null) {
			return renderSelection;
		}

		PortletPreferences portletPreferences =
			cpContentRequestHelper.getPortletPreferences();

		renderSelection = GetterUtil.getString(
			portletPreferences.getValue("renderSelection", null), "custom");

		return renderSelection;
	}

	public String getSelectionStyle() {
		if (selectionStyle != null) {
			return selectionStyle;
		}

		PortletPreferences portletPreferences =
			cpContentRequestHelper.getPortletPreferences();

		selectionStyle = GetterUtil.getString(
			portletPreferences.getValue("selectionStyle", null), "dynamic");

		return selectionStyle;
	}

	public boolean isPaginate() {
		return cpPublisherPortletInstanceConfiguration.paginate();
	}

	public boolean isRenderSelectionADT() {
		String renderSelection = getRenderSelection();

		return renderSelection.equals("adt");
	}

	public boolean isRenderSelectionCustomRenderer() {
		String renderSelection = getRenderSelection();

		return renderSelection.equals("custom");
	}

	public boolean isSelectionStyleDataSource() {
		String selectionStyle = getSelectionStyle();

		return selectionStyle.equals("dataSource");
	}

	public boolean isSelectionStyleDynamic() {
		String selectionStyle = getSelectionStyle();

		return selectionStyle.equals("dynamic");
	}

	public boolean isSelectionStyleManual() {
		String selectionStyle = getSelectionStyle();

		return selectionStyle.equals("manual");
	}

	protected ConfigurationProvider configurationProvider;
	protected final CPContentListEntryRendererRegistry
		contentListEntryRendererRegistry;
	protected final CPContentListRendererRegistry cpContentListRendererRegistry;
	protected final CPContentRequestHelper cpContentRequestHelper;
	protected final CPPublisherPortletInstanceConfiguration
		cpPublisherPortletInstanceConfiguration;
	protected final CPPublisherWebHelper cpPublisherWebHelper;
	protected final CPTypeRegistry cpTypeRegistry;
	protected String dataSource;
	protected Long displayStyleGroupId;
	protected String displayStyleGroupKey;
	protected final GroupLocalService groupLocalService;
	protected String renderSelection;
	protected String selectionStyle;

}