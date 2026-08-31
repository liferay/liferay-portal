/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.randomizerbumpers;

import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.MimeTypesUtil;

import java.util.Objects;

/**
 * @author Shuyang Zhou
 */
public class TikaRandomizerBumper implements RandomizerBumper<String> {

	public static final TikaRandomizerBumper INSTANCE =
		new TikaRandomizerBumper(ContentTypes.TEXT_PLAIN);

	public TikaRandomizerBumper(String contentType) {
		_contentType = contentType;
	}

	@Override
	public boolean accept(String randomValue) {
		return Objects.equals(
			_contentType,
			MimeTypesUtil.getContentType(
				new UnsyncByteArrayInputStream(randomValue.getBytes()), null));
	}

	private final String _contentType;

}