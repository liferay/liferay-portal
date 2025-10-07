/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const baseAttributes = [
	'accountExternalReferenceCode',
	'accountId',
	'analyticsCloudURL',
	'cloudConsoleURL',
	'contactSupportURL',
	'eulaBaseURL',
	'featureFlags',
	'marketoFormId',
	'productId',
	'ssaProjectPrefix',
	'trialAccountCheck',
	'trialEulaURL',
] as const;

const baseKPIAttributes = [
	'kpiConnectorQuartelyRelease',
	'kpiLowCodePublishedApps',
	'kpiPartnershipIntegration',
	'kpiProjectUsingMarketplaceApps',
	'kpiQuartelyReleaseApps',
] as const;

function getAttribute<T extends readonly string[]>(
	element: HTMLElement,
	attrs: T
): Record<T[number], string> {
	return Object.fromEntries(
		attrs.map((key) => [key, element.getAttribute(key) ?? ''])
	) as Record<T[number], string>;
}

export function getAttributes(element: HTMLElement) {
	return {
		...getAttribute(element, baseAttributes),
		featureFlags: (element.getAttribute('featureFlags') ?? '')
			.split(',')
			.map((f) => f.trim())
			.filter(Boolean),
		kpi: getAttribute(element, baseKPIAttributes),
	};
}

export type MarketplaceProperties = ReturnType<typeof getAttributes>;
