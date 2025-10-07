/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.internal.log4j;

import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;

/**
 * @author Hai Yu
 */
@Plugin(
	category = Core.CATEGORY_NAME,
	name = CompanyLogRoutingFilePattern.PLUGIN_NAME, printObject = true
)
public final class CompanyLogRoutingFilePattern {

	public static final String PLUGIN_NAME = "FilePattern";

	@PluginBuilderFactory
	public static Builder newBuilder() {
		return new Builder();
	}

	public String getFileNamePattern() {
		return _fileNamePattern;
	}

	public Layout<?> getLayout() {
		return _layout;
	}

	public static class Builder
		implements org.apache.logging.log4j.core.util.Builder
			<CompanyLogRoutingFilePattern> {

		@Override
		public CompanyLogRoutingFilePattern build() {
			return new CompanyLogRoutingFilePattern(_fileNamePattern, _layout);
		}

		@PluginBuilderAttribute("fileNamePattern")
		@Required
		private String _fileNamePattern;

		@PluginElement("Layout")
		@Required
		private Layout<?> _layout;

	}

	private CompanyLogRoutingFilePattern(
		String fileNamePattern, Layout<?> layout) {

		_fileNamePattern = fileNamePattern;
		_layout = layout;
	}

	private final String _fileNamePattern;
	private final Layout<?> _layout;

}