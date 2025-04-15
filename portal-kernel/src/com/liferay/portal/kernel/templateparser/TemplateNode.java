/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.templateparser;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author Alexander Chow
 * @author Raymond Augé
 */
public class TemplateNode extends LinkedHashMap<String, Object> {

	public TemplateNode(
		ThemeDisplay themeDisplay, String name, String data, String type,
		Map<String, String> attributes) {

		_themeDisplay = themeDisplay;

		put("attributes", attributes);
		put("name", name);
		put("data", data);
		put("type", type);
		put("options", new ArrayList<String>());
		put("optionsMap", new LinkedHashMap<String, String>());
	}

	public void appendChild(TemplateNode templateNode) {
		_childTemplateNodes.put(templateNode.getName(), templateNode);

		if (Objects.equals(templateNode.getName(), "attributes")) {
			put(_RANDOM_ID + "Attributes", getAttributes());
		}

		if (Objects.equals(templateNode.getName(), "data")) {
			put(_RANDOM_ID + "Data", _getData());
		}

		if (Objects.equals(templateNode.getName(), "name")) {
			put(_RANDOM_ID + "Name", getName());
		}

		if (Objects.equals(templateNode.getName(), "options")) {
			put(_RANDOM_ID + "Options", getOptions());
		}

		if (Objects.equals(templateNode.getName(), "optionsMap")) {
			put(_RANDOM_ID + "OptionsMap", getOptionsMap());
		}

		if (Objects.equals(templateNode.getName(), "type")) {
			put(_RANDOM_ID + "Type", getType());
		}

		put(templateNode.getName(), templateNode);
	}

	public void appendChildren(List<TemplateNode> templateNodes) {
		for (TemplateNode templateNode : templateNodes) {
			appendChild(templateNode);
		}
	}

	public void appendOption(String option) {
		List<String> options = getOptions();

		options.add(option);
	}

	public void appendOptionMap(String value, String label) {
		Map<String, String> optionsMap = getOptionsMap();

		optionsMap.put(value, label);
	}

	public void appendOptions(List<String> options) {
		List<String> curOptions = getOptions();

		curOptions.addAll(options);
	}

	public void appendOptionsMap(Map<String, String> optionMap) {
		Map<String, String> optionsMap = getOptionsMap();

		optionsMap.putAll(optionMap);
	}

	public void appendSibling(TemplateNode templateNode) {
		_siblingTemplateNodes.add(templateNode);
	}

	@Override
	public Object clone() {
		TemplateNode templateNode = new TemplateNode(
			_themeDisplay, getName(), getData(), getType(), getAttributes());

		for (Map.Entry<String, TemplateNode> entry :
				_childTemplateNodes.entrySet()) {

			templateNode.appendChild(entry.getValue());
		}

		templateNode.appendOptions(getOptions());
		templateNode.appendOptionsMap(getOptionsMap());

		for (TemplateNode siblingTemplateNode : _siblingTemplateNodes) {
			templateNode.appendSibling(siblingTemplateNode);
		}

		for (Map.Entry<String, Object> entry : entrySet()) {
			templateNode.put(entry.getKey(), entry.getValue());
		}

		return templateNode;
	}

	public String getAttribute(String name) {
		Map<String, String> attributes = getAttributes();

		if (attributes == null) {
			return StringPool.BLANK;
		}

		return attributes.get(name);
	}

	public Map<String, String> getAttributes() {
		if (super.containsKey(_RANDOM_ID + "Attributes") ||
			MapUtil.isNotEmpty(
				(Map<String, String>)get(_RANDOM_ID + "Attributes"))) {

			return (Map<String, String>)get(_RANDOM_ID + "Attributes");
		}

		return (Map<String, String>)get("attributes");
	}

	public TemplateNode getChild(String name) {
		return _childTemplateNodes.get(name);
	}

	public List<TemplateNode> getChildren() {
		return new ArrayList<>(_childTemplateNodes.values());
	}

	public String getData() {
		String type = getType();

		if (type.equals("color") || type.equals("ddm-color")) {
			return _getColorData();
		}
		else if (type.equals("ddm-decimal") || type.equals("ddm-number") ||
				 type.equals("numeric")) {

			return _getNumericData();
		}
		else if (type.equals("ddm-journal-article") ||
				 type.equals("journal_article")) {

			return _getLatestArticleData();
		}
		else if (type.equals("document_library") || type.equals("image")) {
			return _getFileEntryData();
		}
		else if (type.equals("geolocation")) {
			return _getGeolocationData();
		}

		return _getData();
	}

	public String getFriendlyUrl() {
		String type = getType();

		if (type.equals("ddm-journal-article") ||
			type.equals("journal_article")) {

			return _getDDMJournalArticleFriendlyURL();
		}
		else if (type.equals("ddm-link-to-page") ||
				 type.equals("link_to_layout")) {

			return getUrl();
		}

		return StringPool.BLANK;
	}

	public String getName() {
		if (super.containsKey(_RANDOM_ID + "Name")) {
			return (String)get(_RANDOM_ID + "Name");
		}

		Object name = get("name");

		if ((name == null) || (name instanceof String)) {
			return (String)name;
		}

		return "name";
	}

	public List<String> getOptions() {
		if (super.containsKey(_RANDOM_ID + "Options") ||
			ListUtil.isNotEmpty((List<String>)get(_RANDOM_ID + "Options"))) {

			return (List<String>)get(_RANDOM_ID + "Options");
		}

		return (List<String>)get("options");
	}

	public Map<String, String> getOptionsMap() {
		if (super.containsKey(_RANDOM_ID + "OptionsMap") ||
			MapUtil.isNotEmpty(
				(Map<String, String>)get(_RANDOM_ID + "OptionsMap"))) {

			return (Map<String, String>)get(_RANDOM_ID + "OptionsMap");
		}

		return (Map<String, String>)get("optionsMap");
	}

	public List<TemplateNode> getSiblings() {
		return _siblingTemplateNodes;
	}

	public String getType() {
		if (super.containsKey(_RANDOM_ID + "Type") ||
			Validator.isNotNull((String)get(_RANDOM_ID + "Type"))) {

			return (String)get(_RANDOM_ID + "Type");
		}

		Object type = get("type");

		if ((type == null) || (type instanceof String)) {
			return (String)type;
		}

		return StringPool.BLANK;
	}

	public String getUrl() {
		if (_themeDisplay == null) {
			return StringPool.BLANK;
		}

		String data = _getData();

		if (!JSONUtil.isJSONObject(data)) {
			return StringPool.BLANK;
		}

		try {
			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(data);

			Layout layout = LayoutLocalServiceUtil.fetchLayout(
				jsonObject.getLong("groupId"),
				jsonObject.getBoolean("privateLayout"),
				jsonObject.getLong("layoutId"));

			if (layout == null) {
				return StringPool.BLANK;
			}

			return PortalUtil.getLayoutRelativeURL(layout, _themeDisplay);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to parse JSON from data: " + data);
			}

			return StringPool.BLANK;
		}
	}

	private String _getColorData() {
		String data = _getData();

		if (data.startsWith(StringPool.POUND)) {
			return data;
		}

		return StringPool.POUND + data;
	}

	private String _getData() {
		if (super.containsKey(_RANDOM_ID + "Data") ||
			Validator.isNotNull((String)get(_RANDOM_ID + "Data"))) {

			return (String)get(_RANDOM_ID + "Data");
		}

		return (String)get("data");
	}

	private String _getDDMJournalArticleFriendlyURL() {
		if (_themeDisplay == null) {
			return StringPool.BLANK;
		}

		String data = _getData();

		try {
			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(data);

			AssetRendererFactory<?> assetRendererFactory =
				AssetRendererFactoryRegistryUtil.
					getAssetRendererFactoryByClassName(
						jsonObject.getString("className"));

			if (assetRendererFactory == null) {
				return StringPool.BLANK;
			}

			long classPK = GetterUtil.getLong(jsonObject.getLong("classPK"));

			AssetRenderer<?> assetRenderer =
				assetRendererFactory.getAssetRenderer(classPK);

			if (assetRenderer == null) {
				return StringPool.BLANK;
			}

			HttpServletRequest httpServletRequest = _themeDisplay.getRequest();

			PortletRequest portletRequest =
				(PortletRequest)httpServletRequest.getAttribute(
					JavaConstants.JAVAX_PORTLET_REQUEST);
			PortletResponse portletResponse =
				(PortletResponse)httpServletRequest.getAttribute(
					JavaConstants.JAVAX_PORTLET_RESPONSE);

			return assetRenderer.getURLViewInContext(
				PortalUtil.getLiferayPortletRequest(portletRequest),
				PortalUtil.getLiferayPortletResponse(portletResponse),
				StringPool.BLANK);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return StringPool.BLANK;
	}

	private String _getFileEntryData() {
		String data = _getData();

		try {
			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(data);

			String uuid = jsonObject.getString("uuid");
			long groupId = jsonObject.getLong("groupId");

			if (Validator.isNull(uuid) && (groupId == 0)) {
				return StringPool.BLANK;
			}

			FileEntry fileEntry =
				DLAppLocalServiceUtil.getFileEntryByUuidAndGroupId(
					uuid, groupId);

			return DLUtil.getPreviewURL(
				fileEntry, fileEntry.getFileVersion(), _themeDisplay,
				StringPool.BLANK, false, false);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return StringPool.BLANK;
	}

	private String _getGeolocationData() {
		String data = _getData();

		if (Validator.isNull(data)) {
			return StringPool.BLANK;
		}

		try {
			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(data);

			if (jsonObject.has("latitude") && jsonObject.has("longitude")) {
				return data;
			}

			if (!jsonObject.has("lat") || !jsonObject.has("lng")) {
				return data;
			}

			jsonObject.put(
				"latitude", jsonObject.get("lat")
			).put(
				"longitude", jsonObject.get("lng")
			);

			return jsonObject.toString();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return StringPool.BLANK;
	}

	private String _getLatestArticleData() {
		String data = _getData();

		try {
			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(data);

			AssetRendererFactory<?> assetRendererFactory =
				AssetRendererFactoryRegistryUtil.
					getAssetRendererFactoryByClassName(
						jsonObject.getString("className"));

			if (assetRendererFactory == null) {
				return StringPool.BLANK;
			}

			long classPK = GetterUtil.getLong(jsonObject.getLong("classPK"));

			AssetRenderer<?> assetRenderer =
				assetRendererFactory.getAssetRenderer(classPK);

			if (assetRenderer == null) {
				return StringPool.BLANK;
			}

			if (Objects.equals(
					jsonObject.getString("uuid"), assetRenderer.getUuid())) {

				return data;
			}

			String updatedTitle = assetRenderer.getTitle(
				LocaleUtil.fromLanguageId(
					assetRenderer.getDefaultLanguageId()));

			jsonObject.put("title", updatedTitle);

			Map<Locale, String> titleMap = new HashMap<>();

			for (String languageId : assetRenderer.getAvailableLanguageIds()) {
				Locale locale = LocaleUtil.fromLanguageId(languageId);

				if (locale != null) {
					titleMap.put(locale, assetRenderer.getTitle(locale));
				}
			}

			jsonObject.put(
				"titleMap", titleMap
			).put(
				"uuid", assetRenderer.getUuid()
			);

			return jsonObject.toString();
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to parse JSON from data: " + data);
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return _getData();
	}

	private String _getNumericData() {
		String data = _getData();

		DecimalFormat decimalFormat = (DecimalFormat)DecimalFormat.getInstance(
			LocaleUtil.getMostRelevantLocale());

		DecimalFormatSymbols decimalFormatSymbols =
			decimalFormat.getDecimalFormatSymbols();

		decimalFormatSymbols.setZeroDigit('0');

		decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);

		decimalFormat.setGroupingUsed(false);
		decimalFormat.setMaximumFractionDigits(Integer.MAX_VALUE);
		decimalFormat.setParseBigDecimal(true);

		return decimalFormat.format(GetterUtil.getDouble(data));
	}

	private static final String _RANDOM_ID = StringUtil.randomId();

	private static final Log _log = LogFactoryUtil.getLog(TemplateNode.class);

	private final Map<String, TemplateNode> _childTemplateNodes =
		new LinkedHashMap<>();
	private final List<TemplateNode> _siblingTemplateNodes = new ArrayList<>();
	private final ThemeDisplay _themeDisplay;

}