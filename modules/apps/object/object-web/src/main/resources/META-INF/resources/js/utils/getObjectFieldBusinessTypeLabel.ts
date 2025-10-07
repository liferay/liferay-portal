/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const objectFieldBusinessTypeLabels = {
	Aggregation: Liferay.Language.get('aggregation'),
	Assignee: Liferay.Language.get('assignee'),
	Attachment: Liferay.Language.get('attachment'),
	AutoIncrement: Liferay.Language.get('auto-increment'),
	Boolean: Liferay.Language.get('boolean'),
	Date: Liferay.Language.get('date'),
	DateTime: Liferay.Language.get('date-time'),
	Decimal: Liferay.Language.get('decimal'),
	Encrypted: Liferay.Language.get('encrypted'),
	Formula: Liferay.Language.get('formula'),
	Integer: Liferay.Language.get('integer'),
	LongInteger: Liferay.Language.get('long-integer'),
	LongText: Liferay.Language.get('long-text'),
	MultiselectPicklist: Liferay.Language.get('multiselect-picklist'),
	Picklist: Liferay.Language.get('picklist'),
	PrecisionDecimal: Liferay.Language.get('precision-decimal'),
	Relationship: Liferay.Language.get('relationship'),
	RichText: Liferay.Language.get('rich-text'),
	Text: Liferay.Language.get('text'),
};

export function getObjectFieldBusinessTypeLabel(
	objectFieldBusinessType: ObjectFieldBusinessTypeName
) {
	return (
		objectFieldBusinessTypeLabels[objectFieldBusinessType] ??
		objectFieldBusinessType
	);
}
