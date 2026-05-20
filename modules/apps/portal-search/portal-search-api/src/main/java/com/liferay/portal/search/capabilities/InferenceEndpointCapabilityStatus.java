/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.capabilities;

import com.liferay.petra.string.StringPool;

/**
 * Immutable result of an inference endpoint capability check.
 *
 * <p>
 * When {@link #isAvailable()} is {@code true}, {@link #getReason()} returns an
 * empty string. When the capability is unavailable, {@link #getReason()}
 * returns the i18n key identifying the first failed precondition.
 * </p>
 *
 * @author Rodrigo Guedes de Souza
 */
public class InferenceEndpointCapabilityStatus {

	public static InferenceEndpointCapabilityStatus available() {
		return _AVAILABLE;
	}

	public static InferenceEndpointCapabilityStatus unavailable(String reason) {
		return new InferenceEndpointCapabilityStatus(reason);
	}

	public String getReason() {
		return _reason;
	}

	public boolean isAvailable() {
		return _reason.isEmpty();
	}

	private InferenceEndpointCapabilityStatus(String reason) {
		_reason = reason;
	}

	private static final InferenceEndpointCapabilityStatus _AVAILABLE =
		new InferenceEndpointCapabilityStatus(StringPool.BLANK);

	private final String _reason;

}