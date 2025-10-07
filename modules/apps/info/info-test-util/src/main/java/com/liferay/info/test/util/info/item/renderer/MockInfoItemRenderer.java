/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.test.util.info.item.renderer;

import com.liferay.info.item.renderer.InfoItemRenderer;
import com.liferay.info.test.util.model.MockObject;
import com.liferay.portal.kernel.util.MapUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;

/**
 * @author Lourdes Fernández Besada
 */
public class MockInfoItemRenderer implements InfoItemRenderer<MockObject> {

	public MockInfoItemRenderer(MockObject mockObject) {
		_mockObject = mockObject;
	}

	@Override
	public void render(
		MockObject mockObject, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			PrintWriter printWriter = httpServletResponse.getWriter();

			printWriter.print(MapUtil.toString(mockObject.getInfoFieldsMap()));

			printWriter.flush();
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private final MockObject _mockObject;

}