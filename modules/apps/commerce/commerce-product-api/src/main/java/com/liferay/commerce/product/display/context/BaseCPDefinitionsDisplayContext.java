/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.display.context;

import com.liferay.commerce.product.display.context.helper.CPRequestHelper;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.portlet.action.ActionHelper;
import com.liferay.commerce.product.servlet.taglib.ui.constants.CPDefinitionScreenNavigationConstants;
import com.liferay.commerce.product.type.CPType;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Locale;

/**
 * @author Alessio Antonio Rendina
 * @author Marco Leo
 */
public abstract class BaseCPDefinitionsDisplayContext {

	public BaseCPDefinitionsDisplayContext(
		ActionHelper actionHelper, HttpServletRequest httpServletRequest) {

		this.actionHelper = actionHelper;
		this.httpServletRequest = httpServletRequest;

		portalPreferences = PortletPreferencesFactoryUtil.getPortalPreferences(
			this.httpServletRequest);

		cpRequestHelper = new CPRequestHelper(httpServletRequest);

		liferayPortletRequest = cpRequestHelper.getLiferayPortletRequest();
		liferayPortletResponse = cpRequestHelper.getLiferayPortletResponse();
	}

	public String getCatalogDefaultLanguageId() throws PortalException {
		CommerceCatalog commerceCatalog = getCommerceCatalog();

		if (commerceCatalog == null) {
			ThemeDisplay themeDisplay = cpRequestHelper.getThemeDisplay();

			return LocaleUtil.toLanguageId(themeDisplay.getSiteDefaultLocale());
		}

		return commerceCatalog.getCatalogDefaultLanguageId();
	}

	public CommerceCatalog getCommerceCatalog() throws PortalException {
		if (_commerceCatalog != null) {
			return _commerceCatalog;
		}

		CPDefinition cpDefinition = getCPDefinition();

		if (cpDefinition == null) {
			return null;
		}

		_commerceCatalog = _cpDefinition.getCommerceCatalog();

		return _commerceCatalog;
	}

	public long getCommerceCatalogId() throws PortalException {
		CommerceCatalog commerceCatalog = getCommerceCatalog();

		if (commerceCatalog == null) {
			return 0;
		}

		return commerceCatalog.getCommerceCatalogId();
	}

	public CPDefinition getCPDefinition() throws PortalException {
		if (_cpDefinition != null) {
			return _cpDefinition;
		}

		_cpDefinition = actionHelper.getCPDefinition(
			cpRequestHelper.getRenderRequest());

		return _cpDefinition;
	}

	public long getCPDefinitionId() throws PortalException {
		CPDefinition cpDefinition = getCPDefinition();

		if (cpDefinition == null) {
			return 0;
		}

		return cpDefinition.getCPDefinitionId();
	}

	public CPType getCPType() throws PortalException {
		CPDefinition cpDefinition = getCPDefinition();

		if (cpDefinition == null) {
			return actionHelper.getCPType(
				ParamUtil.getString(httpServletRequest, "productTypeName"));
		}

		return actionHelper.getCPType(cpDefinition.getProductTypeName());
	}

	public CPType getCPType(String name) {
		return actionHelper.getCPType(name);
	}

	public List<CPType> getCPTypes() {
		return actionHelper.getCPTypes();
	}

	public PortletURL getEditProductDefinitionURL() throws PortalException {
		return PortletURLBuilder.createRenderURL(
			liferayPortletResponse
		).setMVCRenderCommandName(
			"/cp_definitions/edit_cp_definition"
		).setParameter(
			"cpDefinitionId", getCPDefinitionId()
		).buildPortletURL();
	}

	public PortletURL getPortletURL() throws PortalException {
		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		String redirect = ParamUtil.getString(httpServletRequest, "redirect");

		if (Validator.isNotNull(redirect)) {
			portletURL.setParameter("redirect", redirect);
		}

		CPDefinition cpDefinition = getCPDefinition();

		if (cpDefinition != null) {
			portletURL.setParameter(
				"cpDefinitionId", String.valueOf(getCPDefinitionId()));
		}

		return portletURL;
	}

	public long getScopeGroupId() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return themeDisplay.getScopeGroupId();
	}

	public String getScreenNavigationCategoryKey() {
		return CPDefinitionScreenNavigationConstants.CATEGORY_KEY_DETAILS;
	}

	public String getSelectedScreenNavigationCategoryKey() {
		return ParamUtil.getString(
			httpServletRequest, "screenNavigationCategoryKey",
			getScreenNavigationCategoryKey());
	}

	public String getSku(CPDefinition cpDefinition, Locale locale) {
		List<CPInstance> cpInstances = cpDefinition.getCPInstances();

		if (cpInstances.isEmpty()) {
			return StringPool.BLANK;
		}

		if (cpInstances.size() > 1) {
			return LanguageUtil.get(locale, "multiple-skus");
		}

		CPInstance cpInstance = cpInstances.get(0);

		return cpInstance.getSku();
	}

	protected final ActionHelper actionHelper;
	protected final CPRequestHelper cpRequestHelper;
	protected final HttpServletRequest httpServletRequest;
	protected final LiferayPortletRequest liferayPortletRequest;
	protected final LiferayPortletResponse liferayPortletResponse;
	protected final PortalPreferences portalPreferences;

	private CommerceCatalog _commerceCatalog;
	private CPDefinition _cpDefinition;

}