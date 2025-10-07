/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.field;

import java.util.Map;

/**
 * @author Víctor Galán
 */
public class RelatedInfoFieldValue<T> {

	public RelatedInfoFieldValue(
		Map<RelatedInfoFieldValueIdentifier, InfoFieldValue<T>>
			relatedInfoFieldValues) {

		_relatedInfoFieldValues = relatedInfoFieldValues;
	}

	public Map<RelatedInfoFieldValueIdentifier, InfoFieldValue<T>>
		getRelatedInfoFieldValues() {

		return _relatedInfoFieldValues;
	}

	public static class RelatedInfoFieldValueIdentifier {

		public RelatedInfoFieldValueIdentifier(
			String externalReferenceCode, String parentExternalReferenceCode) {

			_externalReferenceCode = externalReferenceCode;
			_parentExternalReferenceCode = parentExternalReferenceCode;
		}

		public String getExternalReferenceCode() {
			return _externalReferenceCode;
		}

		public String getParentExternalReferenceCode() {
			return _parentExternalReferenceCode;
		}

		private final String _externalReferenceCode;
		private final String _parentExternalReferenceCode;

	}

	private final Map<RelatedInfoFieldValueIdentifier, InfoFieldValue<T>>
		_relatedInfoFieldValues;

}