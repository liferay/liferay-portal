/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.metadata;

import com.liferay.portal.kernel.exception.PortalException;

import java.io.InputStream;

import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Miguel Pastor
 */
@ProviderType
public interface RawMetadataProcessor {

	public static final String TIKA_RAW_METADATA = "TIKARAWMETADATA";

	public Map<String, String[]> getRawMetadata(
			String mimeType, InputStream inputStream)
		throws PortalException;

}