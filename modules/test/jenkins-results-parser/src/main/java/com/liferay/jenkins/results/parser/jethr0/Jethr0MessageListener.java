/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.jethr0;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.Jethr0BuildUpdater;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;

/**
 * @author Michael Hashimoto
 */
public class Jethr0MessageListener implements MessageListener {

	public static Jethr0MessageListener getInstance(
		Jethr0Client jethr0Client, long jethr0JobId) {

		synchronized (_jethr0MessageOperators) {
			Jethr0MessageListener jethr0MessageListener =
				_jethr0MessageOperators.get(jethr0JobId);

			if (jethr0MessageListener == null) {
				_jethr0MessageOperators.put(
					jethr0JobId,
					new Jethr0MessageListener(jethr0Client, jethr0JobId));
			}

			return _jethr0MessageOperators.get(jethr0JobId);
		}
	}

	@Override
	public void onMessage(Message message) {
		for (Jethr0BuildUpdater jethr0BuildUpdater : _jethr0BuildUpdaters) {
			try {
				jethr0BuildUpdater.processMessage(message);
			}
			catch (JMSException | JSONException exception) {
				throw new RuntimeException(exception);
			}
		}
	}

	public void subscribe(Jethr0BuildUpdater jethr0BuildUpdater)
		throws JMSException {

		synchronized (_jethr0BuildUpdaters) {
			if (_jethr0BuildUpdaters.isEmpty()) {
				_jethr0Client.subscribe(this, _getMessageSelector());
			}

			_jethr0BuildUpdaters.add(jethr0BuildUpdater);
		}
	}

	public void unsubscribe(Jethr0BuildUpdater jethr0BuildUpdater)
		throws JMSException {

		synchronized (_jethr0BuildUpdaters) {
			_jethr0BuildUpdaters.remove(jethr0BuildUpdater);

			if (_jethr0BuildUpdaters.isEmpty()) {
				_jethr0Client.unsubscribe(this, _getMessageSelector());
			}
		}
	}

	protected Jethr0MessageListener(
		Jethr0Client jethr0Client, long jethr0JobId) {

		_jethr0Client = jethr0Client;
		_jethr0JobId = jethr0JobId;
	}

	private String _getMessageSelector() {
		return JenkinsResultsParserUtil.combine(
			"(jethr0JobId = '", String.valueOf(_jethr0JobId), "')");
	}

	private static final Map<Long, Jethr0MessageListener>
		_jethr0MessageOperators = new HashMap<>();

	private final Set<Jethr0BuildUpdater> _jethr0BuildUpdaters =
		new HashSet<>();
	private final Jethr0Client _jethr0Client;
	private final long _jethr0JobId;

}