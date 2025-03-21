/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.util;

import com.liferay.dynamic.data.mapping.constants.DDMConstants;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateHandler;
import com.liferay.portal.kernel.template.TemplateHandlerRegistryUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Eduardo García
 */
@ProviderType
public abstract class BaseDDMDisplay implements DDMDisplay {

	@Override
	public String getAvailableFields() {
		return DDMConstants.AVAILABLE_FIELDS;
	}

	@Override
	public String getConfirmSelectStructureMessage(Locale locale) {
		return StringPool.BLANK;
	}

	@Override
	public String getConfirmSelectTemplateMessage(Locale locale) {
		return StringPool.BLANK;
	}

	@Override
	public DDMNavigationHelper getDDMNavigationHelper() {
		return new DDMNavigationHelperImpl();
	}

	@Override
	public DDMDisplayTabItem getDefaultTabItem() {
		return new DDMDisplayTabItem() {

			@Override
			public String getTitle(
				LiferayPortletRequest liferayPortletRequest,
				LiferayPortletResponse liferayPortletResponse) {

				String scopeTitle = ParamUtil.getString(
					liferayPortletRequest, "scopeTitle");

				if (Validator.isNull(scopeTitle)) {
					return BaseDDMDisplay.this.getTitle(
						liferayPortletRequest.getLocale());
				}

				return scopeTitle;
			}

		};
	}

	@Override
	public String getDefaultTemplateLanguage() {
		return TemplateConstants.LANG_TYPE_FTL;
	}

	@Override
	public String getDescription(Locale locale) {
		return null;
	}

	@Override
	public String getEditStructureDefaultValuesURL(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			DDMStructure structure, String redirectURL)
		throws Exception {

		return null;
	}

	@Override
	public String getEditTemplateBackURL(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse, long classNameId,
			long classPK, long resourceClassNameId, String portletResource)
		throws Exception {

		return getViewTemplatesURL(
			liferayPortletRequest, liferayPortletResponse, classNameId, classPK,
			resourceClassNameId);
	}

	@Override
	public String getEditTemplateTitle(
		DDMStructure structure, DDMTemplate template, Locale locale) {

		if ((structure != null) && (template != null)) {
			return StringUtil.appendParentheticalSuffix(
				template.getName(locale), structure.getName(locale));
		}
		else if (structure != null) {
			return LanguageUtil.format(
				getResourceBundle(locale), "new-template-for-structure-x",
				structure.getName(locale), false);
		}
		else if (template != null) {
			return template.getName(locale);
		}

		return getDefaultEditTemplateTitle(locale);
	}

	@Override
	public String getEditTemplateTitle(long classNameId, Locale locale) {
		if (classNameId > 0) {
			TemplateHandler templateHandler =
				TemplateHandlerRegistryUtil.getTemplateHandler(classNameId);

			if (templateHandler != null) {
				return LanguageUtil.format(
					locale, "new-x", templateHandler.getName(locale), false);
			}
		}

		return getDefaultEditTemplateTitle(locale);
	}

	public abstract String getPortletId();

	@Override
	public String getStorageType() {
		return StringPool.BLANK;
	}

	@Override
	public String getStructureName(Locale locale) {
		return LanguageUtil.get(getResourceBundle(locale), "structure");
	}

	@Override
	public String getStructureType() {
		return StringPool.BLANK;
	}

	@Override
	public List<DDMDisplayTabItem> getTabItems() {
		return Arrays.asList(getDefaultTabItem());
	}

	@Override
	public long[] getTemplateClassNameIds(long classNameId) {
		if (classNameId > 0) {
			return new long[] {classNameId};
		}

		return TemplateHandlerRegistryUtil.getClassNameIds();
	}

	@Override
	public long[] getTemplateClassPKs(
			long companyId, long classNameId, long classPK)
		throws Exception {

		if (classPK > 0) {
			return new long[] {classPK};
		}

		List<Long> classPKs = new ArrayList<>();

		classPKs.add(0L);

		List<DDMStructure> structures =
			DDMStructureLocalServiceUtil.getClassStructures(
				companyId, PortalUtil.getClassNameId(getStructureType()));

		for (DDMStructure structure : structures) {
			classPKs.add(structure.getPrimaryKey());
		}

		return ArrayUtil.toLongArray(classPKs);
	}

	@Override
	public long[] getTemplateGroupIds(
			ThemeDisplay themeDisplay, boolean includeAncestorTemplates)
		throws Exception {

		HttpServletRequest httpServletRequest = themeDisplay.getRequest();

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		long groupId = themeDisplay.getScopeGroupId();

		String refererPortletName = ParamUtil.getString(
			httpServletRequest,
			portletDisplay.getNamespace() + "refererPortletName");

		if (Validator.isNotNull(refererPortletName)) {
			groupId = PortalUtil.getScopeGroupId(
				httpServletRequest, refererPortletName, true);
		}

		if (includeAncestorTemplates) {
			return PortalUtil.getCurrentAndAncestorSiteGroupIds(groupId);
		}

		return new long[] {groupId};
	}

	@Override
	public long getTemplateHandlerClassNameId(
		DDMTemplate template, long classNameId) {

		if (template != null) {
			return template.getClassNameId();
		}

		return classNameId;
	}

	@Override
	public Set<String> getTemplateLanguageTypes() {
		return _templateLanguageTypes;
	}

	@Override
	public String getTemplateMode() {
		return StringPool.BLANK;
	}

	@Override
	public String getTemplateType() {
		return StringPool.BLANK;
	}

	@Override
	public String getTemplateType(DDMTemplate template, Locale locale) {
		return LanguageUtil.get(locale, template.getType());
	}

	@Override
	public String getTitle(Locale locale) {
		return LanguageUtil.get(locale, "structures");
	}

	@Override
	public String getViewStructuresBackURL(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws Exception {

		return ParamUtil.getString(liferayPortletRequest, "backURL");
	}

	@Override
	public String getViewTemplatesBackURL(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse, long classPK)
		throws Exception {

		DDMNavigationHelper ddmNavigationHelper = getDDMNavigationHelper();

		if (ddmNavigationHelper.isNavigationStartsOnSelectStructure(
				liferayPortletRequest)) {

			return ParamUtil.getString(liferayPortletRequest, "redirect");
		}

		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				liferayPortletRequest,
				PortletProviderUtil.getPortletId(
					DDMStructure.class.getName(), PortletProvider.Action.VIEW),
				PortletRequest.RENDER_PHASE)
		).setMVCPath(
			"/view.jsp"
		).buildString();
	}

	@Override
	public Set<String> getViewTemplatesExcludedColumnNames() {
		return _viewTemplateExcludedColumnNames;
	}

	@Override
	public String getViewTemplatesTitle(
		DDMStructure structure, boolean controlPanel, boolean search,
		Locale locale) {

		if (structure != null) {
			return LanguageUtil.format(
				getResourceBundle(locale), "templates-for-structure-x",
				structure.getName(locale), false);
		}

		return getDefaultViewTemplateTitle(locale);
	}

	@Override
	public String getViewTemplatesTitle(DDMStructure structure, Locale locale) {
		return getViewTemplatesTitle(structure, false, false, locale);
	}

	@Override
	public boolean isEnableSelectStructureLink(
		DDMStructure structure, long classPK) {

		if (structure.getStructureId() == classPK) {
			return false;
		}

		if ((classPK == 0) || (structure.getParentStructureId() == 0)) {
			return true;
		}

		DDMStructure parentStructure =
			DDMStructureLocalServiceUtil.fetchStructure(
				structure.getParentStructureId());

		while (parentStructure != null) {
			if (parentStructure.getStructureId() == classPK) {
				return false;
			}

			parentStructure = DDMStructureLocalServiceUtil.fetchStructure(
				parentStructure.getParentStructureId());
		}

		return true;
	}

	@Override
	public boolean isShowAddButton(Group scopeGroup) {
		String portletId = getPortletId();

		String ddmStructurePortletId = PortletProviderUtil.getPortletId(
			DDMStructure.class.getName(), PortletProvider.Action.VIEW);

		if (portletId.equals(ddmStructurePortletId)) {
			return false;
		}

		if (!scopeGroup.hasLocalOrRemoteStagingGroup() ||
			!scopeGroup.isStagedPortlet(portletId)) {

			return true;
		}

		return false;
	}

	@Override
	public boolean isShowBackURLInTitleBar() {
		return false;
	}

	@Override
	public boolean isShowConfirmSelectStructure() {
		return false;
	}

	@Override
	public boolean isShowConfirmSelectTemplate() {
		return false;
	}

	@Override
	public boolean isShowStructureSelector() {
		return false;
	}

	@Override
	public boolean isVersioningEnabled() {
		return false;
	}

	protected String getDefaultEditTemplateTitle(Locale locale) {
		return LanguageUtil.get(getResourceBundle(locale), "new-template");
	}

	protected String getDefaultViewTemplateTitle(Locale locale) {
		return LanguageUtil.get(locale, "templates");
	}

	protected ResourceBundle getResourceBundle(Locale locale) {
		ResourceBundleLoader portalResourceBundleLoader =
			ResourceBundleLoaderUtil.getPortalResourceBundleLoader();

		return portalResourceBundleLoader.loadResourceBundle(locale);
	}

	protected String getViewTemplatesURL(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse, long classNameId,
			long classPK, long resourceClassNameId)
		throws Exception {

		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				liferayPortletRequest,
				PortletProviderUtil.getPortletId(
					DDMStructure.class.getName(), PortletProvider.Action.VIEW),
				PortletRequest.RENDER_PHASE)
		).setMVCPath(
			"/view_template.jsp"
		).setParameter(
			"classNameId", classNameId
		).setParameter(
			"classPK", classPK
		).setParameter(
			"resourceClassNameId", resourceClassNameId
		).buildString();
	}

	private static final Set<String> _templateLanguageTypes = SetUtil.fromArray(
		TemplateConstants.LANG_TYPE_FTL, TemplateConstants.LANG_TYPE_VM);
	private static final Set<String> _viewTemplateExcludedColumnNames =
		SetUtil.fromArray("structure");

}