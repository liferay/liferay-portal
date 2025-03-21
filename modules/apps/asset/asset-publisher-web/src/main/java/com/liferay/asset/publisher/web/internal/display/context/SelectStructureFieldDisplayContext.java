/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.publisher.web.internal.display.context;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.ClassType;
import com.liferay.asset.kernel.model.ClassTypeField;
import com.liferay.asset.kernel.model.ClassTypeReader;
import com.liferay.asset.publisher.constants.AssetPublisherWebKeys;
import com.liferay.asset.publisher.web.internal.helper.AssetPublisherWebHelper;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.SelectOption;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ResourceURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class SelectStructureFieldDisplayContext {

	public SelectStructureFieldDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_httpServletRequest = httpServletRequest;
		_liferayPortletResponse = liferayPortletResponse;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getComponentContextData() {
		return HashMapBuilder.<String, Object>put(
			"assetClassName",
			() -> {
				AssetPublisherWebHelper assetPublisherWebHelper =
					(AssetPublisherWebHelper)_httpServletRequest.getAttribute(
						AssetPublisherWebKeys.ASSET_PUBLISHER_WEB_HELPER);

				AssetRendererFactory<?> assetRendererFactory =
					AssetRendererFactoryRegistryUtil.
						getAssetRendererFactoryByClassName(_getClassName());

				return assetPublisherWebHelper.getClassName(
					assetRendererFactory);
			}
		).put(
			"eventName", HtmlUtil.escapeJS(_getEventName())
		).put(
			"getFieldItemURL",
			() -> {
				ResourceURL getFieldItemURL =
					_liferayPortletResponse.createResourceURL();

				getFieldItemURL.setParameter("className", _getClassName());
				getFieldItemURL.setParameter(
					"classTypeId", String.valueOf(_getClassTypeId()));
				getFieldItemURL.setParameter(
					"ddmStructureFieldName", _getDDMStructureFieldName());
				getFieldItemURL.setParameter(
					"ddmStructureFieldValue", _getDDMStructureFieldValue());
				getFieldItemURL.setResourceID(
					"/asset_publisher/get_field_item");

				return getFieldItemURL.toString();
			}
		).build();
	}

	public String getFieldValueURL() {
		ResourceURL getFieldValueURL =
			_liferayPortletResponse.createResourceURL();

		getFieldValueURL.setParameter("portletResource", _getPortletResource());
		getFieldValueURL.setParameter("className", _getClassName());
		getFieldValueURL.setParameter(
			"classTypeId", String.valueOf(_getClassTypeId()));
		getFieldValueURL.setResourceID("getFieldValue");

		return getFieldValueURL.toString();
	}

	public List<SelectOption> getSelectOptions() throws PortalException {
		List<SelectOption> selectOptions = new ArrayList<>();

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				_getClassName());

		ClassTypeReader classTypeReader =
			assetRendererFactory.getClassTypeReader();

		ClassType classType = classTypeReader.getClassType(
			_getClassTypeId(), _themeDisplay.getLocale());

		selectOptions.add(
			new SelectOption(
				LanguageUtil.get(_themeDisplay.getLocale(), "none"),
				StringPool.BLANK));

		for (ClassTypeField classTypeField : classType.getClassTypeFields()) {
			selectOptions.add(
				new SelectOption(
					classTypeField.getLabel(), classTypeField.getName(),
					Objects.equals(
						classTypeField.getName(),
						_getDDMStructureFieldName())));
		}

		return selectOptions;
	}

	private String _getClassName() {
		if (_className != null) {
			return _className;
		}

		_className = ParamUtil.getString(_httpServletRequest, "className");

		return _className;
	}

	private long _getClassTypeId() {
		if (_classTypeId != null) {
			return _classTypeId;
		}

		_classTypeId = ParamUtil.getLong(_httpServletRequest, "classTypeId");

		return _classTypeId;
	}

	private String _getDDMStructureFieldName() {
		if (_ddmStructureFieldName != null) {
			return _ddmStructureFieldName;
		}

		_ddmStructureFieldName = ParamUtil.getString(
			_httpServletRequest, "ddmStructureFieldName");

		return _ddmStructureFieldName;
	}

	private String _getDDMStructureFieldValue() {
		if (_ddmStructureFieldValue != null) {
			return _ddmStructureFieldValue;
		}

		_ddmStructureFieldValue = ParamUtil.getString(
			_httpServletRequest, "ddmStructureFieldValue");

		return _ddmStructureFieldValue;
	}

	private String _getEventName() {
		if (_eventName != null) {
			return _eventName;
		}

		_eventName = ParamUtil.getString(
			_httpServletRequest, "eventName",
			_liferayPortletResponse.getNamespace() + "selectDDMStructureField");

		return _eventName;
	}

	private String _getPortletResource() {
		if (_portletResource != null) {
			return _portletResource;
		}

		_portletResource = ParamUtil.getString(
			_httpServletRequest, "portletResource");

		return _portletResource;
	}

	private String _className;
	private Long _classTypeId;
	private String _ddmStructureFieldName;
	private String _ddmStructureFieldValue;
	private String _eventName;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private String _portletResource;
	private final ThemeDisplay _themeDisplay;

}