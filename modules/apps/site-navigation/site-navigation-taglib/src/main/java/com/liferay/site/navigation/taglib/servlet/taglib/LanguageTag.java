/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.taglib.servlet.taglib;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portletdisplaytemplate.PortletDisplayTemplateManagerUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.servlet.taglib.ui.LanguageEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.navigation.taglib.internal.servlet.ServletContextUtil;
import com.liferay.taglib.aui.AUIUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Brian Wing Shun Chan
 */
public class LanguageTag extends IncludeTag {

	public String getDdmTemplateGroupKey() {
		return _ddmTemplateGroupKey;
	}

	public String getDdmTemplateKey() {
		return _ddmTemplateKey;
	}

	public String getFormAction() {
		return _formAction;
	}

	public String getFormName() {
		return _formName;
	}

	public String getLanguageId() {
		return _languageId;
	}

	public String[] getLanguageIds() {
		return _languageIds;
	}

	public String getName() {
		return _name;
	}

	public boolean isDisplayCurrentLocale() {
		return _displayCurrentLocale;
	}

	public boolean isUseNamespace() {
		return _useNamespace;
	}

	public void setDdmTemplateGroupKey(String ddmTemplateGroupKey) {
		_ddmTemplateGroupKey = ddmTemplateGroupKey;
	}

	public void setDdmTemplateKey(String ddmTemplateKey) {
		_ddmTemplateKey = ddmTemplateKey;
	}

	public void setDisplayCurrentLocale(boolean displayCurrentLocale) {
		_displayCurrentLocale = displayCurrentLocale;
	}

	public void setFormAction(String formAction) {
		_formAction = formAction;
	}

	public void setFormName(String formName) {
		_formName = formName;
	}

	public void setLanguageId(String languageId) {
		_languageId = languageId;
	}

	public void setLanguageIds(String[] languageIds) {
		_languageIds = languageIds;
	}

	public void setName(String name) {
		_name = name;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setUseNamespace(boolean useNamespace) {
		_useNamespace = useNamespace;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_ddmTemplateGroupKey = null;
		_ddmTemplateKey = null;
		_displayCurrentLocale = true;
		_formAction = null;
		_formName = "fm";
		_languageId = null;
		_languageIds = null;
		_name = "languageId";
		_useNamespace = true;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected int processStartTag() throws Exception {
		String formAction = _getFormAction();
		String namespace = _getNamespacedName();

		List<LanguageEntry> languageEntries = _getLanguageEntries(
			_getLocales(), _displayCurrentLocale, formAction, namespace);

		if (!languageEntries.isEmpty()) {
			JspWriter jspWriter = pageContext.getOut();

			jspWriter.write(
				PortletDisplayTemplateManagerUtil.renderDDMTemplate(
					PortalUtil.getClassNameId(LanguageEntry.class),
					HashMapBuilder.<String, Object>put(
						"formAction", formAction
					).put(
						"formName", getFormName()
					).put(
						"languageId",
						() -> {
							HttpServletRequest httpServletRequest =
								getRequest();

							ThemeDisplay themeDisplay =
								(ThemeDisplay)httpServletRequest.getAttribute(
									WebKeys.THEME_DISPLAY);

							return GetterUtil.getString(
								_languageId, themeDisplay.getLanguageId());
						}
					).put(
						"name", _name
					).put(
						"namespace", namespace
					).build(),
					_ddmTemplateKey, languageEntries, _getDisplayStyleGroupId(),
					getRequest(),
					(HttpServletResponse)pageContext.getResponse(), true));
		}

		return SKIP_BODY;
	}

	private long _getDisplayStyleGroupId() {
		HttpServletRequest httpServletRequest = getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (Validator.isNotNull(_ddmTemplateGroupKey)) {
			Group group = GroupLocalServiceUtil.fetchGroup(
				themeDisplay.getCompanyId(), _ddmTemplateGroupKey);

			if (group != null) {
				return group.getGroupId();
			}
		}

		return themeDisplay.getScopeGroupId();
	}

	private String _getFormAction() {
		String formAction = _formAction;

		if (Validator.isNotNull(formAction)) {
			return formAction;
		}

		HttpServletRequest httpServletRequest = getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		formAction = themeDisplay.getPathMain() + "/portal/update_language";

		formAction = HttpComponentsUtil.setParameter(
			formAction, "redirect",
			PortalUtil.getCurrentURL(httpServletRequest));
		formAction = HttpComponentsUtil.setParameter(
			formAction, "groupId", themeDisplay.getScopeGroupId());

		Layout layout = themeDisplay.getLayout();

		formAction = HttpComponentsUtil.setParameter(
			formAction, "privateLayout", layout.isPrivateLayout());

		return HttpComponentsUtil.setParameter(
			formAction, "layoutId", layout.getLayoutId());
	}

	private List<LanguageEntry> _getLanguageEntries(
		Collection<Locale> locales, boolean displayCurrentLocale,
		String formAction, String parameterName) {

		List<LanguageEntry> languageEntries = new ArrayList<>();

		Map<String, Integer> counts = new HashMap<>();

		for (Locale locale : locales) {
			Integer count = counts.get(locale.getLanguage());

			if (count == null) {
				count = Integer.valueOf(1);
			}
			else {
				count = Integer.valueOf(count.intValue() + 1);
			}

			counts.put(locale.getLanguage(), count);
		}

		Set<String> duplicateLanguages = new HashSet<>();

		for (Locale locale : locales) {
			Integer count = counts.get(locale.getLanguage());

			if (count.intValue() != 1) {
				duplicateLanguages.add(locale.getLanguage());
			}
		}

		Locale currentLocale = null;

		if (Validator.isNotNull(_languageId)) {
			currentLocale = LocaleUtil.fromLanguageId(_languageId);
		}
		else {
			HttpServletRequest httpServletRequest = getRequest();

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			currentLocale = themeDisplay.getLocale();
		}

		for (Locale locale : locales) {
			boolean disabled = false;
			String url = null;

			if (!LocaleUtil.equals(locale, currentLocale)) {
				url = HttpComponentsUtil.setParameter(
					formAction, parameterName, LocaleUtil.toLanguageId(locale));
			}
			else if (!displayCurrentLocale) {
				disabled = true;
			}

			LanguageEntry languageEntry = new LanguageEntry(
				duplicateLanguages, currentLocale, locale, url, disabled);

			languageEntries.add(languageEntry);
		}

		return languageEntries;
	}

	private Collection<Locale> _getLocales() {
		if (ArrayUtil.isNotEmpty(_languageIds)) {
			return Arrays.asList(LocaleUtil.fromLanguageIds(_languageIds));
		}

		HttpServletRequest httpServletRequest = getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return LanguageUtil.getAvailableLocales(themeDisplay.getSiteGroupId());
	}

	private String _getNamespacedName() {
		String name = _name;

		if (!_useNamespace) {
			return name;
		}

		HttpServletRequest httpServletRequest = getRequest();

		PortletRequest portletRequest =
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST);
		PortletResponse portletResponse =
			(PortletResponse)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE);

		String namespace = AUIUtil.getNamespace(
			portletRequest, portletResponse);

		if (Validator.isNotNull(namespace)) {
			name = namespace.concat(name);
		}

		return name;
	}

	private static final String _PAGE = "/language/page.jsp";

	private String _ddmTemplateGroupKey;
	private String _ddmTemplateKey;
	private boolean _displayCurrentLocale = true;
	private String _formAction;
	private String _formName = "fm";
	private String _languageId;
	private String[] _languageIds;
	private String _name = "languageId";
	private boolean _useNamespace = true;

}