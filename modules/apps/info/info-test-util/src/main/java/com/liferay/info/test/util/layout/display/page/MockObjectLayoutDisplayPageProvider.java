/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.test.util.layout.display.page;

import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.test.util.model.MockObject;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;

/**
 * @author Lourdes Fernández Besada
 */
public class MockObjectLayoutDisplayPageProvider
	implements LayoutDisplayPageProvider<MockObject> {

	public MockObjectLayoutDisplayPageProvider(
		LayoutDisplayPageObjectProvider layoutDisplayPageObjectProvider) {

		_layoutDisplayPageObjectProvider = layoutDisplayPageObjectProvider;
	}

	@Override
	public String getClassName() {
		return MockObject.class.getName();
	}

	@Override
	public LayoutDisplayPageObjectProvider<MockObject>
		getLayoutDisplayPageObjectProvider(
			InfoItemReference infoItemReference) {

		InfoItemIdentifier infoItemIdentifier =
			infoItemReference.getInfoItemIdentifier();

		if (!(infoItemIdentifier instanceof ClassPKInfoItemIdentifier)) {
			return null;
		}

		return _layoutDisplayPageObjectProvider;
	}

	@Override
	public LayoutDisplayPageObjectProvider<MockObject>
		getLayoutDisplayPageObjectProvider(long groupId, String urlTitle) {

		return _layoutDisplayPageObjectProvider;
	}

	@Override
	public String getURLSeparator() {
		return "/test/";
	}

	private final LayoutDisplayPageObjectProvider
		_layoutDisplayPageObjectProvider;

}