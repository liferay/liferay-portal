/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.web.internal.display.context;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.ClassType;
import com.liferay.asset.kernel.model.ClassTypeField;
import com.liferay.asset.kernel.model.ClassTypeReader;
import com.liferay.asset.util.AssetRendererFactoryClassProvider;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.SelectOption;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class SelectStructureFieldDisplayContext {

	public SelectStructureFieldDisplayContext(
		AssetRendererFactoryClassProvider assetRendererFactoryClassProvider,
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_assetRendererFactoryClassProvider = assetRendererFactoryClassProvider;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getComponentContextData() {
		return HashMapBuilder.<String, Object>put(
			"assetClassName",
			() -> {
				AssetRendererFactory<?> assetRendererFactory =
					AssetRendererFactoryRegistryUtil.
						getAssetRendererFactoryByClassName(_getClassName());

				Class<? extends AssetRendererFactory<?>> clazz =
					_assetRendererFactoryClassProvider.getClass(
						assetRendererFactory);

				String className = clazz.getName();

				return className.substring(
					className.lastIndexOf(StringPool.PERIOD) + 1);
			}
		).put(
			"eventName",
			() -> {
				String eventName = ParamUtil.getString(
					_renderRequest, "eventName",
					_renderResponse.getNamespace() + "selectDDMStructureField");

				return HtmlUtil.escapeJS(eventName);
			}
		).put(
			"getFieldItemURL",
			() -> {
				LiferayPortletURL getFieldItemURL =
					(LiferayPortletURL)_renderResponse.createResourceURL();

				getFieldItemURL.setCopyCurrentRenderParameters(false);
				getFieldItemURL.setParameter("className", _getClassName());
				getFieldItemURL.setParameter(
					"classTypeId", String.valueOf(_getClassTypeId()));
				getFieldItemURL.setParameter(
					"ddmStructureFieldName", _getDDMStructureFieldName());
				getFieldItemURL.setParameter(
					"ddmStructureFieldValue", _getDDMStructureFieldValue());
				getFieldItemURL.setResourceID("/asset_list/get_field_item");

				return getFieldItemURL.toString();
			}
		).build();
	}

	public String getFieldValueURL() {
		LiferayPortletURL getFieldValueURL =
			(LiferayPortletURL)_renderResponse.createResourceURL();

		getFieldValueURL.setCopyCurrentRenderParameters(false);
		getFieldValueURL.setParameter("className", _getClassName());
		getFieldValueURL.setParameter(
			"classTypeId", String.valueOf(_getClassTypeId()));
		getFieldValueURL.setResourceID("/asset_list/get_field_value");

		return getFieldValueURL.toString();
	}

	public List<SelectOption> getSelectOptions() throws Exception {
		List<SelectOption> selectOptions = new ArrayList<>();

		selectOptions.add(
			new SelectOption(
				LanguageUtil.get(_themeDisplay.getLocale(), "none"),
				StringPool.BLANK));

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				_getClassName());

		ClassTypeReader classTypeReader =
			assetRendererFactory.getClassTypeReader();

		ClassType classType = classTypeReader.getClassType(
			_getClassTypeId(), _themeDisplay.getLocale());

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

		_className = ParamUtil.getString(_renderRequest, "className");

		return _className;
	}

	private long _getClassTypeId() {
		if (_classTypeId != null) {
			return _classTypeId;
		}

		_classTypeId = ParamUtil.getLong(_renderRequest, "classTypeId");

		return _classTypeId;
	}

	private String _getDDMStructureFieldName() {
		if (_ddmStructureFieldName != null) {
			return _ddmStructureFieldName;
		}

		_ddmStructureFieldName = ParamUtil.getString(
			_renderRequest, "ddmStructureFieldName");

		return _ddmStructureFieldName;
	}

	private String _getDDMStructureFieldValue() {
		if (_ddmStructureFieldValue != null) {
			return _ddmStructureFieldValue;
		}

		_ddmStructureFieldValue = ParamUtil.getString(
			_renderRequest, "ddmStructureFieldValue");

		return _ddmStructureFieldValue;
	}

	private final AssetRendererFactoryClassProvider
		_assetRendererFactoryClassProvider;
	private String _className;
	private Long _classTypeId;
	private String _ddmStructureFieldName;
	private String _ddmStructureFieldValue;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final ThemeDisplay _themeDisplay;

}