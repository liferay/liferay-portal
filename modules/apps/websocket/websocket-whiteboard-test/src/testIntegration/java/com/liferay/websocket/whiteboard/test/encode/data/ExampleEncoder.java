/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.websocket.whiteboard.test.encode.data;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;

import jakarta.websocket.Encoder;
import jakarta.websocket.EndpointConfig;

/**
 * @author Cristina González
 */
public class ExampleEncoder implements Encoder.Text<Example> {

	@Override
	public void destroy() {
	}

	@Override
	public String encode(Example example) {
		return StringBundler.concat(
			example.getNumber(), StringPool.SPACE, example.getData());
	}

	@Override
	public void init(EndpointConfig endpointConfig) {
	}

}