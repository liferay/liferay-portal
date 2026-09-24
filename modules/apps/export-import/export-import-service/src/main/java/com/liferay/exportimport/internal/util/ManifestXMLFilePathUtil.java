/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.util;

import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;

/**
 * @author Petteri Karttunen
 */
public class ManifestXMLFilePathUtil {

	public static final String MANIFEST_XML_FILE_PATH = "/manifest.xml";

	public static String getExportManifestXmlFilePath(
		PortletDataContext portletDataContext) {

		if (!GroupExportImportParameterUtil.isGroupScoped(portletDataContext)) {
			return MANIFEST_XML_FILE_PATH;
		}

		return ExportImportPathUtil.getRootPath(portletDataContext) +
			MANIFEST_XML_FILE_PATH;
	}

	public static String getImportManifestXMLFilePath(long sourceGroupId) {
		return StringBundler.concat(
			StringPool.FORWARD_SLASH, ExportImportPathUtil.PATH_PREFIX_GROUP,
			StringPool.FORWARD_SLASH, sourceGroupId, MANIFEST_XML_FILE_PATH);
	}

	public static String getImportManifestXMLFilePath(
		PortletDataContext portletDataContext) {

		if (!GroupExportImportParameterUtil.isGroupScoped(portletDataContext)) {
			return MANIFEST_XML_FILE_PATH;
		}

		return ExportImportPathUtil.getSourceRootPath(portletDataContext) +
			MANIFEST_XML_FILE_PATH;
	}

}