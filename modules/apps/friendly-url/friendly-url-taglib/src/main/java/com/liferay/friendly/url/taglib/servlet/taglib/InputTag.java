/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.friendly.url.taglib.servlet.taglib;

import com.liferay.friendly.url.exception.NoSuchFriendlyURLEntryMappingException;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalServiceUtil;
import com.liferay.friendly.url.taglib.internal.servlet.ServletContextUtil;
import com.liferay.friendly.url.taglib.util.InfoItemObjectProviderUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanPropertiesUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.service.PersistedModelLocalServiceRegistryUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

import java.util.Objects;

/**
 * @author Adolfo Pérez
 */
public class InputTag extends IncludeTag {

	public String getClassName() {
		return _className;
	}

	public long getClassPK() {
		return _classPK;
	}

	public String getDefaultLanguageId() {
		return _defaultLanguageId;
	}

	public String getHelpMessage() {
		return _helpMessage;
	}

	public String getInputAddon() {
		return _inputAddon;
	}

	public String getName() {
		return _name;
	}

	public boolean isDisabled() {
		return _disabled;
	}

	public boolean isLanguagesDropdownVisible() {
		return _languagesDropdownVisible;
	}

	public boolean isLocalizable() {
		return _localizable;
	}

	public boolean isShowHistory() {
		return _showHistory;
	}

	public boolean isShowLabel() {
		return _showLabel;
	}

	public void setClassName(String className) {
		_className = className;
	}

	public void setClassPK(long classPK) {
		_classPK = classPK;
	}

	public void setDefaultLanguageId(String defaultLanguageId) {
		_defaultLanguageId = defaultLanguageId;
	}

	public void setDisabled(boolean disabled) {
		_disabled = disabled;
	}

	public void setHelpMessage(String helpMessage) {
		_helpMessage = helpMessage;
	}

	public void setInputAddon(String inputAddon) {
		_inputAddon = inputAddon;
	}

	public void setLanguagesDropdownVisible(boolean languagesDropdownVisible) {
		_languagesDropdownVisible = languagesDropdownVisible;
	}

	public void setLocalizable(boolean localizable) {
		_localizable = localizable;
	}

	public void setName(String name) {
		_name = name;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setShowHistory(boolean showHistory) {
		_showHistory = showHistory;
	}

	public void setShowLabel(boolean showLabel) {
		_showLabel = showLabel;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_className = null;
		_classPK = 0;
		_defaultLanguageId = null;
		_disabled = false;
		_helpMessage = null;
		_inputAddon = null;
		_languagesDropdownVisible = true;
		_localizable = true;
		_name = _DEFAULT_NAME;
		_showHistory = true;
		_showLabel = true;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		super.setAttributes(httpServletRequest);

		httpServletRequest.setAttribute(
			"liferay-friendly-url:input:className", getClassName());
		httpServletRequest.setAttribute(
			"liferay-friendly-url:input:classPK", getClassPK());
		httpServletRequest.setAttribute(
			"liferay-friendly-url:input:defaultLanguageId",
			getDefaultLanguageId());
		httpServletRequest.setAttribute(
			"liferay-friendly-url:input:disabled", isDisabled());
		httpServletRequest.setAttribute(
			"liferay-friendly-url:input:friendlyURLMaxLength",
			_FRIENDLY_URL_MAX_LENGTH);
		httpServletRequest.setAttribute(
			"liferay-friendly-url:input:helpMessage", getHelpMessage());
		httpServletRequest.setAttribute(
			"liferay-friendly-url:input:inputAddon", getInputAddon());
		httpServletRequest.setAttribute(
			"liferay-friendly-url:input:languagesDropdownVisible",
			isLanguagesDropdownVisible());
		httpServletRequest.setAttribute(
			"liferay-friendly-url:input:localizable", isLocalizable());
		httpServletRequest.setAttribute(
			"liferay-friendly-url:input:name", getName());
		httpServletRequest.setAttribute(
			"liferay-friendly-url:input:showHistory", _isShowHistory());
		httpServletRequest.setAttribute(
			"liferay-friendly-url:input:showLabel", isShowLabel());
		httpServletRequest.setAttribute(
			"liferay-friendly-url:input:value", _getValue());
	}

	private String _getActualClassName() throws PortalException {
		if (!Objects.equals(getClassName(), Layout.class.getName())) {
			return getClassName();
		}

		Layout layout = LayoutLocalServiceUtil.getLayout(getClassPK());

		return getClassName() + StringPool.DASH + layout.isPrivateLayout();
	}

	private String _getFallbackValue() {
		try {
			if (Objects.equals(getClassName(), FileEntry.class.getName())) {
				return StringPool.BLANK;
			}

			if (Objects.equals(getClassName(), Layout.class.getName())) {
				Layout layout = LayoutLocalServiceUtil.fetchLayout(
					getClassPK());

				if (layout == null) {
					return StringPool.BLANK;
				}

				if (isLocalizable()) {
					return layout.getFriendlyURLsXML();
				}

				return layout.getFriendlyURL();
			}

			String urlTitle = BeanPropertiesUtil.getString(
				_getModel(), "urlTitle");

			if (Validator.isNull(urlTitle)) {
				return StringPool.BLANK;
			}

			if (!isLocalizable()) {
				return urlTitle;
			}

			String languageId = BeanPropertiesUtil.getString(
				_getModel(), "defaultLanguageId");

			if (languageId == null) {
				languageId = LanguageUtil.getLanguageId(
					LocaleUtil.getDefault());
			}

			return LocalizationUtil.getXml(
				HashMapBuilder.put(
					languageId, urlTitle
				).build(),
				languageId, "UrlTitle");
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return StringPool.BLANK;
		}
	}

	private Object _getModel() throws PortalException {
		Object model = InfoItemObjectProviderUtil.getInfoItem(
			getClassName(), getClassPK());

		if (model != null) {
			return model;
		}

		PersistedModelLocalService persistedModelLocalService =
			PersistedModelLocalServiceRegistryUtil.
				getPersistedModelLocalService(getClassName());

		return persistedModelLocalService.getPersistedModel(getClassPK());
	}

	private String _getValue() {
		try {
			if (getClassPK() == 0) {
				return StringPool.BLANK;
			}

			if (Objects.equals(getClassName(), Layout.class.getName())) {
				return _getFallbackValue();
			}

			FriendlyURLEntry mainFriendlyURLEntry =
				FriendlyURLEntryLocalServiceUtil.getMainFriendlyURLEntry(
					PortalUtil.getClassNameId(_getActualClassName()),
					getClassPK());

			if (isLocalizable()) {
				return mainFriendlyURLEntry.getUrlTitleMapAsXML();
			}

			return mainFriendlyURLEntry.getUrlTitle();
		}
		catch (NoSuchFriendlyURLEntryMappingException
					noSuchFriendlyURLEntryMappingException) {

			if (_log.isDebugEnabled()) {
				_log.debug(noSuchFriendlyURLEntryMappingException);
			}

			return _getFallbackValue();
		}
		catch (PortalException portalException) {
			return ReflectionUtil.throwException(portalException);
		}
	}

	private boolean _isShowHistory() {
		if (isShowHistory() && (getClassPK() != 0)) {
			return true;
		}

		return false;
	}

	private static final String _DEFAULT_NAME = "friendlyURL";

	private static final int _FRIENDLY_URL_MAX_LENGTH = 255;

	private static final String _PAGE = "/input/page.jsp";

	private static final Log _log = LogFactoryUtil.getLog(InputTag.class);

	private String _className;
	private long _classPK;
	private String _defaultLanguageId;
	private boolean _disabled;
	private String _helpMessage;
	private String _inputAddon;
	private boolean _languagesDropdownVisible = true;
	private boolean _localizable = true;
	private String _name = _DEFAULT_NAME;
	private boolean _showHistory = true;
	private boolean _showLabel = true;

}