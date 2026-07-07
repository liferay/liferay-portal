/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.criteria;

import java.util.Collections;
import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class AudiencesCriteriaType {

	public AudiencesCriteriaType(
		List<AudiencesCriteria> audiencesCriterias, String key, String label) {

		_audiencesCriterias = Collections.unmodifiableList(audiencesCriterias);
		_key = key;
		_label = label;
	}

	public List<AudiencesCriteria> getAudiencesCriterias() {
		return _audiencesCriterias;
	}

	public String getKey() {
		return _key;
	}

	public String getLabel() {
		return _label;
	}

	private final List<AudiencesCriteria> _audiencesCriterias;
	private final String _key;
	private final String _label;

}