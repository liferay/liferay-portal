/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.test.util.info.item.provider;

import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.info.test.util.model.MockObject;

/**
 * @author Lourdes Fernández Besada
 */
public class MockInfoItemObjectProvider
	implements InfoItemObjectProvider<MockObject> {

	public MockInfoItemObjectProvider(MockObject mockObject) {
		_mockObject = mockObject;
	}

	@Override
	public MockObject getInfoItem(InfoItemIdentifier infoItemIdentifier)
		throws NoSuchInfoItemException {

		if (!(infoItemIdentifier instanceof ClassPKInfoItemIdentifier)) {
			throw new NoSuchInfoItemException(
				"Unsupported info item identifier " + infoItemIdentifier);
		}

		return _mockObject;
	}

	private final MockObject _mockObject;

}