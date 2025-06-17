/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.processor;

import com.liferay.source.formatter.SourceFormatterArgs;
import com.liferay.source.formatter.util.FileUtil;
import com.liferay.source.formatter.util.ModulesPropertiesUtil;

import java.io.File;
import java.io.IOException;

import java.util.List;

/**
 * @author Hugo Huijser
 */
public class BNDSourceProcessor extends BaseSourceProcessor {

	@Override
	protected List<String> doGetFileNames() throws IOException {
		return getFileNames(new String[0], getIncludes());
	}

	@Override
	protected String[] doGetIncludes() {
		return _INCLUDES;
	}

	@Override
	protected void postFormat() throws IOException {
		if (!_checkModulesPropertiesFile()) {
			return;
		}

		File modulesPropertiesFile = new File(
			getPortalDir(), "modules/modules.properties");

		String newContent = ModulesPropertiesUtil.getContent(getPortalDir());
		String oldContent = FileUtil.read(modulesPropertiesFile);

		if (!oldContent.equals(newContent)) {
			FileUtil.write(modulesPropertiesFile, newContent);

			System.out.println("Updated \"modules.properties\"");
		}
	}

	private boolean _checkModulesPropertiesFile() {
		if (!isPortalSource()) {
			return false;
		}

		SourceFormatterArgs sourceFormatterArgs = getSourceFormatterArgs();

		List<String> fileExtensions = sourceFormatterArgs.getFileExtensions();

		return fileExtensions.contains("bnd");
	}

	private static final String[] _INCLUDES = {"**/*.bnd"};

}