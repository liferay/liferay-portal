/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v6_2_0;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletPreferences;

/**
 * @author Alexander Chow
 */
public class UpgradeSearch extends BaseUpgradePortletPreferences {

	@Override
	protected String[] getPortletIds() {
		return new String[] {"3"};
	}

	protected JSONObject upgradeDataJSONObject(JSONObject dataJSONObject)
		throws Exception {

		JSONArray valuesJSONArray = dataJSONObject.getJSONArray("values");

		boolean hasBookmarksEntry = false;
		boolean hasDLFileEntry = false;
		boolean hasJournalArticle = false;

		for (int i = 0; i < valuesJSONArray.length(); i++) {
			String value = valuesJSONArray.getString(i);

			if (value.equals(
					"com.liferay.portlet.bookmarks.model.BookmarksEntry")) {

				hasBookmarksEntry = true;
			}

			if (value.equals(
					"com.liferay.portlet.documentlibrary.model.DLFileEntry")) {

				hasDLFileEntry = true;
			}

			if (value.equals(
					"com.liferay.portlet.journal.model.JournalArticle")) {

				hasJournalArticle = true;
			}
		}

		if (!hasBookmarksEntry && !hasDLFileEntry && !hasJournalArticle) {
			return null;
		}

		if (hasBookmarksEntry) {
			valuesJSONArray.put(
				"com.liferay.portlet.bookmarks.model.BookmarksFolder");
		}

		if (hasDLFileEntry) {
			valuesJSONArray.put(
				"com.liferay.portlet.documentlibrary.model.DLFolder");
		}

		if (hasJournalArticle) {
			valuesJSONArray.put(
				"com.liferay.portlet.journal.model.JournalFolder");
		}

		dataJSONObject.put("values", valuesJSONArray);

		return dataJSONObject;
	}

	@Override
	protected String upgradePreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId, String xml)
		throws Exception {

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.fromXML(
				companyId, ownerId, ownerType, plid, portletId, xml);

		String searchConfiguration = portletPreferences.getValue(
			"searchConfiguration", null);

		if (Validator.isNull(searchConfiguration)) {
			return null;
		}

		JSONObject searchConfigurationJSONObject =
			JSONFactoryUtil.createJSONObject(searchConfiguration);

		JSONArray oldFacetsJSONArray =
			searchConfigurationJSONObject.getJSONArray("facets");

		if (oldFacetsJSONArray == null) {
			return null;
		}

		JSONArray newFacetsJSONArray = JSONFactoryUtil.createJSONArray();

		for (int i = 0; i < oldFacetsJSONArray.length(); i++) {
			JSONObject oldFacetJSONObject = oldFacetsJSONArray.getJSONObject(i);

			String fieldName = oldFacetJSONObject.getString("fieldName");

			if (fieldName.equals("entryClassName")) {
				JSONObject oldDataJSONObject = oldFacetJSONObject.getJSONObject(
					"data");

				JSONObject newDataJSONObject = upgradeDataJSONObject(
					oldDataJSONObject);

				if (newDataJSONObject == null) {
					return null;
				}

				oldFacetJSONObject.put("data", newDataJSONObject);
			}

			newFacetsJSONArray.put(oldFacetJSONObject);
		}

		searchConfigurationJSONObject.put("facets", newFacetsJSONArray);

		portletPreferences.setValue(
			"searchConfiguration", searchConfigurationJSONObject.toString());

		return PortletPreferencesFactoryUtil.toXML(portletPreferences);
	}

}