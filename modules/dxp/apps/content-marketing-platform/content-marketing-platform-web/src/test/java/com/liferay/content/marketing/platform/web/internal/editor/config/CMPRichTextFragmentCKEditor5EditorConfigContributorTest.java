/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.marketing.platform.web.internal.editor.config;

import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Larissa Ribeiro
 */
public class CMPRichTextFragmentCKEditor5EditorConfigContributorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testPopulateConfigJSONObject() {
		JSONObject jsonObject = _populateConfigJSONObject(
			Mockito.mock(ThemeDisplay.class));

		Mockito.verifyNoInteractions(jsonObject);

		jsonObject = _populateConfigJSONObject(_getThemeDisplay(null));

		Mockito.verifyNoInteractions(jsonObject);

		jsonObject = _populateConfigJSONObject(
			_getThemeDisplay(RandomTestUtil.randomString()));

		Mockito.verifyNoInteractions(jsonObject);

		jsonObject = _populateConfigJSONObject(
			_getThemeDisplay("L_CMP_PROJECT"));

		Mockito.verify(
			jsonObject
		).remove(
			"groupId"
		);

		Mockito.verify(
			jsonObject
		).put(
			"itemSelectorAskForGroupId", true
		);

		jsonObject = _populateConfigJSONObject(_getThemeDisplay("L_CMP_TASK"));

		Mockito.verify(
			jsonObject
		).remove(
			"groupId"
		);

		Mockito.verify(
			jsonObject
		).put(
			"itemSelectorAskForGroupId", true
		);
	}

	private ThemeDisplay _getThemeDisplay(String externalReferenceCode) {
		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.when(
			themeDisplay.getRequest()
		).thenReturn(
			httpServletRequest
		);

		if (externalReferenceCode != null) {
			ObjectDefinition objectDefinition = Mockito.mock(
				ObjectDefinition.class);

			Mockito.when(
				objectDefinition.getExternalReferenceCode()
			).thenReturn(
				externalReferenceCode
			);

			ObjectEntry objectEntry = Mockito.mock(ObjectEntry.class);

			Mockito.when(
				objectEntry.getObjectDefinition()
			).thenReturn(
				objectDefinition
			);

			Mockito.when(
				httpServletRequest.getAttribute(InfoDisplayWebKeys.INFO_ITEM)
			).thenReturn(
				objectEntry
			);
		}

		return themeDisplay;
	}

	private JSONObject _populateConfigJSONObject(ThemeDisplay themeDisplay) {
		JSONObject jsonObject = Mockito.mock(JSONObject.class);

		_cmpRichTextFragmentCKEditor5EditorConfigContributor.
			populateConfigJSONObject(
				jsonObject, Collections.emptyMap(), themeDisplay, null);

		return jsonObject;
	}

	private final CMPRichTextFragmentCKEditor5EditorConfigContributor
		_cmpRichTextFragmentCKEditor5EditorConfigContributor =
			new CMPRichTextFragmentCKEditor5EditorConfigContributor();

}