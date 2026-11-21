/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.markdown.converter.internal;

import com.liferay.knowledge.base.markdown.converter.MarkdownConverter;
import com.liferay.knowledge.base.markdown.converter.factory.MarkdownConverterFactory;

import org.osgi.service.component.annotations.Component;

/**
 * @author Adolfo Pérez
 */
@Component(service = MarkdownConverterFactory.class)
public class FlexmarkMarkdownConverterFactory
	implements MarkdownConverterFactory {

	@Override
	public MarkdownConverter create() {
		return _markdownConverter;
	}

	private final MarkdownConverter _markdownConverter =
		new FlexmarkMarkdownConverter();

}