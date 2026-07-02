/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.langchain4j.rag.content.injector;

import com.liferay.ai.hub.internal.exception.ContentInjectorException;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.rag.content.Content;

import java.util.List;

/**
 * @author Feliphe Marinho
 */
public class DefaultContentInjector
	extends dev.langchain4j.rag.content.injector.DefaultContentInjector {

	public DefaultContentInjector(List<String> metadataKeysToInclude) {
		super(metadataKeysToInclude);
	}

	@Override
	public ChatMessage inject(List<Content> contents, ChatMessage chatMessage) {
		if (contents.isEmpty()) {
			throw new ContentInjectorException(
				"No content to be injected", "there-is-no-content");
		}

		return super.inject(contents, chatMessage);
	}

}