/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.io.unsync.UnsyncBufferedReader;
import com.liferay.petra.io.unsync.UnsyncStringReader;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.json.JSONObjectImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.source.formatter.util.PortalJSONObjectUtil;
import com.liferay.source.formatter.util.SourceFormatterUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class JSPUpgradeRemovedTagsCheck extends BaseTagAttributesCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws Exception {

		String upgradeFromVersion = getAttributeValue(
			SourceFormatterUtil.UPGRADE_FROM_VERSION, absolutePath);
		String upgradeToVersion = getAttributeValue(
			SourceFormatterUtil.UPGRADE_TO_RELEASE_VERSION, absolutePath);

		if ((upgradeFromVersion == null) || (upgradeToVersion == null)) {
			return content;
		}

		JSONObject upgradeFromTaglibsJSONObject = _getTaglibsJSONObject(
			upgradeFromVersion);
		JSONObject upgradeToTaglibsJSONObject = _getTaglibsJSONObject(
			upgradeToVersion);

		_checkMultiLineTagAttributes(
			fileName, content, upgradeFromTaglibsJSONObject,
			upgradeToTaglibsJSONObject, upgradeToVersion);
		_checkSingleLineTagAttributes(
			fileName, content, upgradeFromTaglibsJSONObject,
			upgradeToTaglibsJSONObject, upgradeToVersion);

		return content;
	}

	private void _checkMultiLineTagAttributes(
			String fileName, String content,
			JSONObject upgradeFromTaglibsJSONObject,
			JSONObject upgradeToTaglibsJSONObject, String upgradeToVersion)
		throws Exception {

		Matcher matcher = _multilineTagPattern.matcher(content);

		while (matcher.find()) {
			if (matcher.start() != 0) {
				char c = content.charAt(matcher.start() - 1);

				if (c != CharPool.NEW_LINE) {
					continue;
				}
			}

			_checkTag(
				fileName, parseTag(matcher.group(1), false),
				upgradeFromTaglibsJSONObject, upgradeToTaglibsJSONObject,
				upgradeToVersion, getLineNumber(content, matcher.start()));
		}
	}

	private void _checkSingleLineTagAttributes(
			String fileName, String content,
			JSONObject upgradeFromTaglibsJSONObject,
			JSONObject upgradeToTaglibsJSONObject, String upgradeToVersion)
		throws Exception {

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(content))) {

			String line = null;
			int lineNumber = 0;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				lineNumber++;

				for (String jspTag : getJSPTags(line)) {
					_checkTag(
						fileName, parseTag(jspTag, false),
						upgradeFromTaglibsJSONObject,
						upgradeToTaglibsJSONObject, upgradeToVersion,
						lineNumber);
				}
			}
		}
	}

	private void _checkTag(
		String fileName, Tag tag, JSONObject upgradeFromTaglibsJSONObject,
		JSONObject upgradeToTaglibsJSONObject, String upgradeToVersion,
		int lineNumber) {

		if (tag == null) {
			return;
		}

		String taglibName = tag.getTaglibName();

		if (taglibName == null) {
			return;
		}

		Tuple upgradeFromTagStatusTuple = _getTagStatusTuple(
			upgradeFromTaglibsJSONObject, tag);

		TagStatus upgradeFromTagStatus =
			(TagStatus)upgradeFromTagStatusTuple.getObject(0);

		Tuple upgradeToTagStatusTuple = _getTagStatusTuple(
			upgradeToTaglibsJSONObject, tag);

		TagStatus upgradeToTagStatus =
			(TagStatus)upgradeToTagStatusTuple.getObject(0);

		if (!upgradeFromTagStatus.equals(TagStatus.NO_TAGLIB_FOUND) &&
			upgradeToTagStatus.equals(TagStatus.NO_TAGLIB_FOUND)) {

			addMessage(
				fileName,
				StringBundler.concat(
					"Taglib \"", taglibName,
					"\" no longer exists in version \"", upgradeToVersion,
					"\""),
				lineNumber);
		}

		if (upgradeFromTagStatus.equals(TagStatus.ATTRIBUTES_FOUND) &&
			upgradeToTagStatus.equals(TagStatus.NO_TAG_FOUND)) {

			addMessage(
				fileName,
				StringBundler.concat(
					"Tag \"", tag.getFullName(),
					"\" no longer exists in version \"", upgradeToVersion,
					"\""),
				lineNumber);
		}

		if (upgradeFromTagStatus.equals(TagStatus.ATTRIBUTES_FOUND) &&
			upgradeToTagStatus.equals(TagStatus.ATTRIBUTES_FOUND)) {

			List<String> upgradeFromTagAttributes =
				(List<String>)upgradeFromTagStatusTuple.getObject(1);
			List<String> upgradeToTagAttributes =
				(List<String>)upgradeToTagStatusTuple.getObject(1);

			for (String upgradeFromTagAttribute : upgradeFromTagAttributes) {
				if (!upgradeToTagAttributes.contains(upgradeFromTagAttribute)) {
					addMessage(
						fileName,
						StringBundler.concat(
							"Attribute \"", upgradeFromTagAttribute,
							"\" no longer exists for tag \"", tag.getFullName(),
							"\" in version \"", upgradeToVersion, "\""),
						lineNumber);
				}
			}
		}
	}

	private List<String> _getAttributeNames(JSONObject tagJSONObject) {
		List<String> attributeNames = new ArrayList<>();

		JSONArray tagAttributeNamesJSONArray = tagJSONObject.getJSONArray(
			"attributes");

		if (tagAttributeNamesJSONArray == null) {
			return attributeNames;
		}

		Iterator<JSONObject> iterator = tagAttributeNamesJSONArray.iterator();

		while (iterator.hasNext()) {
			JSONObject attributeJSONObject = iterator.next();

			attributeNames.add(attributeJSONObject.getString("name"));
		}

		return attributeNames;
	}

	private synchronized JSONObject _getTaglibsJSONObject(String version)
		throws Exception {

		JSONObject taglibsJSONObject = _taglibsJSONObjectMap.get(version);

		if (taglibsJSONObject != null) {
			return taglibsJSONObject;
		}

		JSONObject portalJSONObject =
			PortalJSONObjectUtil.getPortalJSONObjectByVersion(version);

		if (portalJSONObject.has("taglibs")) {
			taglibsJSONObject = portalJSONObject.getJSONObject("taglibs");
		}
		else {
			taglibsJSONObject = new JSONObjectImpl();
		}

		_taglibsJSONObjectMap.put(version, taglibsJSONObject);

		return taglibsJSONObject;
	}

	private Tuple _getTagStatusTuple(JSONObject taglibsJSONObject, Tag tag) {
		JSONObject taglibJSONObject = taglibsJSONObject.getJSONObject(
			tag.getTaglibName());

		if (taglibJSONObject == null) {
			return new Tuple(TagStatus.NO_TAGLIB_FOUND);
		}

		String tagName = tag.getName();

		JSONObject tagJSONObject = taglibJSONObject.getJSONObject(tagName);

		if (tagJSONObject == null) {
			return new Tuple(TagStatus.NO_TAG_FOUND);
		}

		List<String> matchingAttributeNames = new ArrayList<>();

		Map<String, String> attributesMap = tag.getAttributesMap();

		List<String> attributeNames = _getAttributeNames(tagJSONObject);

		for (Map.Entry<String, String> entry : attributesMap.entrySet()) {
			String attributeName = entry.getKey();

			if (attributeNames.contains(attributeName)) {
				matchingAttributeNames.add(attributeName);
			}
		}

		return new Tuple(TagStatus.ATTRIBUTES_FOUND, matchingAttributeNames);
	}

	private static final Pattern _multilineTagPattern = Pattern.compile(
		"(([ \t]*)<[-\\w:]+\n.*?([^%])(/?>))(\n|$)", Pattern.DOTALL);

	private final Map<String, JSONObject> _taglibsJSONObjectMap =
		new HashMap<>();

	private enum TagStatus {

		ATTRIBUTES_FOUND, NO_TAG_FOUND, NO_TAGLIB_FOUND

	}

}