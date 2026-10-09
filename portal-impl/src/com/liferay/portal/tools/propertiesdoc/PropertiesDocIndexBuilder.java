/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.propertiesdoc;

import com.liferay.portal.freemarker.FreeMarkerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.tools.ArgumentsUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Jesse Rao
 * @author James Hinkey
 */
public class PropertiesDocIndexBuilder {

	public static void main(String[] args) throws Exception {
		Map<String, String> arguments = ArgumentsUtil.parseArguments(args);

		try {
			new PropertiesDocIndexBuilder(arguments);
		}
		catch (Exception exception) {
			ArgumentsUtil.processMainException(arguments, exception);
		}
	}

	public PropertiesDocIndexBuilder(Map<String, String> arguments) {
		String propertiesDirName = GetterUtil.getString(
			arguments.get("properties.dir"));

		File propertiesDir = new File(propertiesDirName);

		if (!propertiesDir.exists()) {
			System.out.println(propertiesDirName + " not found");

			return;
		}

		List<String> propertiesHTMLFileNames = new ArrayList<>();

		File[] files = propertiesDir.listFiles();

		for (File file : files) {
			String fileName = file.getName();

			if (!fileName.endsWith(".properties.html")) {
				continue;
			}

			String propertiesHTMLFileName = fileName.substring(
				0, fileName.length() - 5);

			propertiesHTMLFileNames.add(propertiesHTMLFileName);
		}

		if (propertiesHTMLFileNames.isEmpty()) {
			System.out.println(
				"No properties HTML files found in " + propertiesDirName);

			return;
		}

		Map<String, Object> context = HashMapBuilder.<String, Object>put(
			"propertiesHTMLFileNames", propertiesHTMLFileNames
		).put(
			"releaseInfoVersion", ReleaseInfo.getVersion()
		).build();

		try {
			String indexHTMLFileName = propertiesDirName + "/index.html";

			File indexHTMLFile = new File(indexHTMLFileName);

			System.out.println("Writing " + indexHTMLFile);

			Writer writer = new FileWriter(indexHTMLFile);

			try {
				FreeMarkerUtil.process(
					"com/liferay/portal/tools/propertiesdoc/dependencies" +
						"/index.ftl",
					context, writer);
			}
			catch (Exception exception) {
				_log.error(exception);
			}

			writer.flush();
		}
		catch (IOException ioException) {
			_log.error(ioException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PropertiesDocIndexBuilder.class);

}