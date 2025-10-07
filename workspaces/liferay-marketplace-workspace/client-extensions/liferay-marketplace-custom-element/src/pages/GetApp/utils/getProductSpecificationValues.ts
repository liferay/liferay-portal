/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export function getProductSpecificationValues(
	productSpecifications: DeliveryProductSpecification[]
) {
	const validSpecificationValues = [
		'client-extension',
		'cloud',
		'composite-app',
		'dxp',
		'low-code-configuration',
		'other',
		'ssa-saas',
	];

	const productSpecification = productSpecifications.find(({value}) => {
		return validSpecificationValues.includes(value.toLowerCase());
	}) as DeliveryProductSpecification;

	return productSpecification?.value ?? '';
}
