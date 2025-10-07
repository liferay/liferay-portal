/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.kernel.json.JSONException;
import com.liferay.source.formatter.SourceFormatterExcludes;
import com.liferay.source.formatter.SourceFormatterMessage;
import com.liferay.source.formatter.processor.SourceProcessor;

import java.util.List;
import java.util.Set;

/**
 * @author Hugo Huijser
 */
public interface SourceCheck {

	public Set<SourceFormatterMessage> getSourceFormatterMessages(
		String fileName);

	public int getWeight();

	public boolean isEnabled(String absolutePath);

	public boolean isJavaSource(String content, int pos);

	public boolean isJavaSource(
		String content, int pos, boolean checkInsideTags);

	public boolean isLiferaySourceCheck();

	public boolean isModuleSourceCheck();

	public void setAllFileNames(List<String> allFileNames);

	public void setAttributes(String attributes) throws JSONException;

	public void setBaseDirName(String baseDirName);

	public void setExcludes(String excludes) throws JSONException;

	public void setFileExtensions(List<String> fileExtensions);

	public void setFilterCheckNames(List<String> filterCheckNames);

	public void setMaxDirLevel(int maxDirLevel);

	public void setMaxLineLength(int maxLineLength);

	public void setPluginsInsideModulesDirectoryNames(
		List<String> pluginsInsideModulesDirectoryNames);

	public void setPortalSource(boolean portalSource);

	public void setProjectPathPrefix(String projectPathPrefix);

	public void setSourceFormatterExcludes(
		SourceFormatterExcludes sourceFormatterExcludes);

	public void setSourceProcessor(SourceProcessor sourceProcessor);

	public void setSubrepository(boolean subrepository);

	public void setWeight(int weight);

}