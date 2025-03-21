/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.web.internal.power.tools.portlet.action;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.util.ExpandoBridgeFactoryUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.search.experiences.constants.SXPPortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Petteri Karttunen
 * @author André de Oliveira
 * @author Brian Wing Shun Chan
 */
@Component(
	enabled = false,
	property = {
		"jakarta.portlet.name=" + SXPPortletKeys.SXP_POWER_TOOLS,
		"mvc.command.name=/sxp_power_tools/import_google_places"
	},
	service = MVCActionCommand.class
)
public class ImportGooglePlacesMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		_setUpExpandoBridge(actionRequest);

		_import(actionRequest, "la", "Los Angeles");
		_import(actionRequest, "nashville", "Nashville");
		_import(actionRequest, "ny", "New York");
	}

	private String[] _getAssetTagNames(JSONObject jsonObject) {
		String[] assetTagNames = JSONUtil.toStringArray(
			jsonObject.getJSONArray("types"));

		for (int i = 0; i < assetTagNames.length; i++) {
			String assetTagName = assetTagNames[i];

			assetTagName = StringUtil.removeChars(
				assetTagName, _INVALID_CHARACTERS);
			assetTagName = StringUtil.replace(
				assetTagName, CharPool.UNDERLINE, CharPool.SPACE);

			assetTagNames[i] = assetTagName;
		}

		return assetTagNames;
	}

	private void _import(
			ActionRequest actionRequest, String cityName, JSONObject jsonObject)
		throws Exception {

		JSONObject geometryJSONObject = jsonObject.getJSONObject("geometry");

		JSONObject locationJSONObject = geometryJSONObject.getJSONObject(
			"location");

		double latitude = locationJSONObject.getDouble("lat");
		double longitude = locationJSONObject.getDouble("lng");

		JournalArticle journalArticle = addJournalArticle(
			actionRequest, _getAssetTagNames(jsonObject),
			StringBundler.concat(
				cityName, StringPool.COLON, latitude, StringPool.COMMA,
				longitude),
			jsonObject.getString("name"));

		ExpandoBridge expandoBridge = journalArticle.getExpandoBridge();

		expandoBridge.setAttribute(
			"location",
			JSONUtil.put(
				"latitude", latitude
			).put(
				"longitude", longitude
			),
			false);

		journalArticleLocalService.updateJournalArticle(journalArticle);
	}

	private void _import(
			ActionRequest actionRequest, String cityCode, String cityName)
		throws Exception {

		_import(actionRequest, cityCode, cityName, "restaurant");
		_import(actionRequest, cityCode, cityName, "tourist");
	}

	private void _import(
			ActionRequest actionRequest, String cityCode, String cityName,
			String type)
		throws Exception {

		JSONObject jsonObject = _read(cityCode + "_" + type);

		JSONArray jsonArray = jsonObject.getJSONArray("results");

		for (int i = 0; i < jsonArray.length(); i++) {
			_import(actionRequest, cityName, jsonArray.getJSONObject(i));
		}
	}

	private JSONObject _read(String fileName) throws Exception {
		return jsonFactory.createJSONObject(
			new String(
				FileUtil.getBytes(
					getClass(),
					"dependencies/google/places/" + fileName + ".json")));
	}

	private void _setUpExpandoBridge(ActionRequest actionRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		ExpandoBridge expandoBridge = ExpandoBridgeFactoryUtil.getExpandoBridge(
			themeDisplay.getCompanyId(), JournalArticle.class.getName());

		if (!expandoBridge.hasAttribute("location")) {
			expandoBridge.addAttribute(
				"location", ExpandoColumnConstants.GEOLOCATION,
				JSONUtil.put(
					"latitude", 0
				).put(
					"longitude", 0
				),
				false);
		}

		UnicodeProperties unicodeProperties =
			expandoBridge.getAttributeProperties("location");

		unicodeProperties.setProperty(
			ExpandoColumnConstants.INDEX_TYPE,
			String.valueOf(ExpandoColumnConstants.INDEX_TYPE_KEYWORD));
		unicodeProperties.setProperty(
			ExpandoColumnConstants.PROPERTY_LOCALIZE_FIELD_NAME, "false");

		expandoBridge.setAttributeProperties("location", unicodeProperties);
	}

	private static final char[] _INVALID_CHARACTERS = {
		CharPool.AMPERSAND, CharPool.APOSTROPHE, CharPool.AT,
		CharPool.BACK_SLASH, CharPool.CLOSE_BRACKET, CharPool.CLOSE_CURLY_BRACE,
		CharPool.COLON, CharPool.COMMA, CharPool.EQUAL, CharPool.GREATER_THAN,
		CharPool.FORWARD_SLASH, CharPool.LESS_THAN, CharPool.NEW_LINE,
		CharPool.OPEN_BRACKET, CharPool.OPEN_CURLY_BRACE, CharPool.PERCENT,
		CharPool.PIPE, CharPool.PLUS, CharPool.POUND, CharPool.PRIME,
		CharPool.QUESTION, CharPool.QUOTE, CharPool.RETURN, CharPool.SEMICOLON,
		CharPool.SLASH, CharPool.STAR, CharPool.TILDE
	};

}