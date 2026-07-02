/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.langchain4j.observability.api.listener;

import dev.langchain4j.observability.api.event.AiServiceErrorEvent;
import dev.langchain4j.observability.api.listener.AiServiceErrorListener;

import java.util.function.Consumer;

/**
 * @author Feliphe Marinho
 */
public class AiServiceErrorListenerImpl implements AiServiceErrorListener {

	public AiServiceErrorListenerImpl(Consumer<Throwable> onErrorConsumer) {
		_onErrorConsumer = onErrorConsumer;
	}

	@Override
	public void onEvent(AiServiceErrorEvent aiServiceErrorEvent) {
		_onErrorConsumer.accept(aiServiceErrorEvent.error());
	}

	private final Consumer<Throwable> _onErrorConsumer;

}