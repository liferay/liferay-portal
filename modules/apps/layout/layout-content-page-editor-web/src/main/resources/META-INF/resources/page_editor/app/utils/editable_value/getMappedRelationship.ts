/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export default function getInitialRelationship(field: string | undefined) {
	if (
		!field ||
		!field.includes('ObjectRelationship') ||
		!Liferay.FeatureFlags['LPD-60546']
	) {
		return null;
	}

	const relationshipField = field.split('#').pop();

	if (!relationshipField) {
		return null;
	}

	const [relationship] = relationshipField.split('_');

	return relationship;
}
