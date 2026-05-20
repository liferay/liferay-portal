/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.capabilities;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Evaluates whether the inference endpoint capability required by Bring Your
 * Own LLM (BYO-LLM) is available in the current runtime.
 *
 * <p>
 * Consumers should call {@link #check()} once per decision point and inspect
 * the returned {@link InferenceEndpointCapabilityStatus}. The status carries
 * both the boolean availability and, when unavailable, the i18n key for the
 * first failed precondition, so callers can render a user-facing reason
 * without re-running the underlying checks.
 * </p>
 *
 * @author Rodrigo Guedes de Souza
 */
@ProviderType
public interface InferenceEndpointCapabilityChecker {

	public InferenceEndpointCapabilityStatus check();

}