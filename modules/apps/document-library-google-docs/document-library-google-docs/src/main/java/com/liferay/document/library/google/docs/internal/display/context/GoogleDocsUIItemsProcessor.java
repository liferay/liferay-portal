/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.google.docs.internal.display.context;

import com.liferay.document.library.display.context.DLUIItemKeys;
import com.liferay.document.library.google.docs.internal.helper.GoogleDocsMetadataHelper;
import com.liferay.document.library.google.docs.internal.util.constants.GoogleDocsConstants;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemBuilder;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Iván Zaera
 */
public class GoogleDocsUIItemsProcessor {

	public GoogleDocsUIItemsProcessor(
		HttpServletRequest httpServletRequest,
		GoogleDocsMetadataHelper googleDocsMetadataHelper) {

		_httpServletRequest = httpServletRequest;
		_googleDocsMetadataHelper = googleDocsMetadataHelper;
	}

	public void processDropdownItems(List<DropdownItem> dropdownItems) {
		_removeUnsupportedDropdownItems(dropdownItems);

		_insertEditInGoogleDropdownItem(dropdownItems);
	}

	private <T> int _getIndex(List<T> items, Predicate<T> predicate) {
		for (int i = 0; i < items.size(); i++) {
			if (predicate.test(items.get(i))) {
				return i;
			}
		}

		return -1;
	}

	private void _insertEditInGoogleDropdownItem(
		List<DropdownItem> dropdownItems) {

		if (!_googleDocsMetadataHelper.containsField(
				GoogleDocsConstants.DDM_FIELD_NAME_URL)) {

			return;
		}

		int index = _getIndex(
			dropdownItems,
			dropdownItem -> Objects.equals(
				dropdownItem.get("key"), DLUIItemKeys.EDIT));

		if (index == -1) {
			index = 0;
		}

		dropdownItems.add(
			index,
			DropdownItemBuilder.setHref(
				_googleDocsMetadataHelper.getFieldValue(
					GoogleDocsConstants.DDM_FIELD_NAME_URL)
			).setKey(
				GoogleDocsUIItemKeys.EDIT_IN_GOOGLE
			).setLabel(
				() -> {
					ThemeDisplay themeDisplay =
						(ThemeDisplay)_httpServletRequest.getAttribute(
							WebKeys.THEME_DISPLAY);

					return LanguageUtil.get(
						themeDisplay.getLocale(), "edit-in-google-drive");
				}
			).setTarget(
				"_blank"
			).build());
	}

	private <T> void _removeUIItems(
		List<T> items, Function<T, String> function, Set<String> keys) {

		Iterator<T> iterator = items.iterator();

		while (iterator.hasNext()) {
			T item = iterator.next();

			if (keys.contains(function.apply(item))) {
				iterator.remove();
			}
		}
	}

	private void _removeUnsupportedDropdownItems(
		List<DropdownItem> dropdownItems) {

		_removeUIItems(
			dropdownItems, dropdownItem -> (String)dropdownItem.get("key"),
			SetUtil.fromArray(
				DLUIItemKeys.CANCEL_CHECKOUT, DLUIItemKeys.CHECKIN,
				DLUIItemKeys.CHECKOUT, DLUIItemKeys.DOWNLOAD,
				DLUIItemKeys.OPEN_IN_MS_OFFICE, "#edit-in-google-drive"));
	}

	private final GoogleDocsMetadataHelper _googleDocsMetadataHelper;
	private final HttpServletRequest _httpServletRequest;

}