/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export async function getCommonLicenseKey(
	accountKey: string,
	dateEnd: Date,
	dateStart: Date,
	environment: string,
	oAuthToken: string,
	provisioningServerAPI: string,
	productName: string
) {

	// eslint-disable-next-line @liferay/portal/no-global-fetch
	const response = await fetch(
		`${provisioningServerAPI}/accounts/${accountKey}/product-groups/${productName}/product-environment/${environment}/common-license-key?dateEnd=${dateEnd}&dateStart=${dateStart}`,
		{
			headers: {
				'OAuth-Token': oAuthToken,
			},
		}
	);

	return response;
}

export async function getDevelopmentLicenseKey(
	accountKey: string,
	oAuthToken: string,
	provisioningServerAPI: string,
	selectedVersion: string,
	productName: string
) {

	// eslint-disable-next-line @liferay/portal/no-global-fetch
	const response = await fetch(
		`${provisioningServerAPI}/accounts/${accountKey}/product-groups/${productName}/product-version/${selectedVersion}/development-license-key`,

		{
			headers: {
				'OAuth-Token': oAuthToken,
			},
		}
	);

	return response;
}

export async function getActivationDownloadKey(
	licenseKey: string,
	oAuthToken: string,
	provisioningServerAPI: string
) {

	// eslint-disable-next-line @liferay/portal/no-global-fetch
	const response = await fetch(
		`${provisioningServerAPI}/license-keys/${licenseKey}/download`,

		{
			headers: {
				'OAuth-Token': oAuthToken,
			},
		}
	);

	return response;
}

export async function getAggregatedActivationDownloadKey(
	selectedKeysIDs: string,
	oAuthToken: string,
	provisioningServerAPI: string
) {

	// eslint-disable-next-line @liferay/portal/no-global-fetch
	const response = await fetch(
		`${provisioningServerAPI}/license-keys/download?${selectedKeysIDs}`,

		{
			headers: {
				'OAuth-Token': oAuthToken,
			},
		}
	);

	return response;
}

export async function getMultipleActivationDownloadKey(
	selectedKeysIDs: string,
	oAuthToken: string,
	provisioningServerAPI: string
) {

	// eslint-disable-next-line @liferay/portal/no-global-fetch
	const response = await fetch(
		`${provisioningServerAPI}/license-keys/download-zip?${selectedKeysIDs}`,

		{
			headers: {
				'OAuth-Token': oAuthToken,
			},
		}
	);

	return response;
}

export async function getExportedLicenseKeys(
	accountKey: string,
	oAuthToken: string,
	provisioningServerAPI: string,
	productName: string
) {

	// eslint-disable-next-line @liferay/portal/no-global-fetch
	const response = await fetch(
		`${provisioningServerAPI}/accounts/${accountKey}/license-keys/export?filter=active+eq+true+and+startswith(productName,'${productName}')`,

		{
			headers: {
				'OAuth-Token': oAuthToken,
			},
		}
	);

	return response;
}

export async function getExportedSelectedLicenseKeys(
	selectedKeysIDs: string,
	oAuthToken: string,
	provisioningServerAPI: string
) {

	// eslint-disable-next-line @liferay/portal/no-global-fetch
	const response = await fetch(
		`${provisioningServerAPI}/license-keys/export?${selectedKeysIDs}`,

		{
			headers: {
				'OAuth-Token': oAuthToken,
			},
		}
	);

	return response;
}

export async function putDeactivateKeys(
	oAuthToken: string,
	provisioningServerAPI: string,
	licenseKeyIds: string
) {

	// eslint-disable-next-line @liferay/portal/no-global-fetch
	const response = await fetch(
		`${provisioningServerAPI}/license-keys/deactivate?${licenseKeyIds}`,

		{
			headers: {
				'OAuth-Token': oAuthToken,
			},
			method: 'PUT',
		}
	);

	return response;
}

export async function getNewGenerateKeyFormValues(
	accountKey: string,
	oAuthToken: string,
	provisioningServerAPI: string,
	productGroupName: string
) {

	// eslint-disable-next-line @liferay/portal/no-global-fetch
	const response = await fetch(
		`${provisioningServerAPI}/accounts/${accountKey}/product-groups/${productGroupName}/generate-form`,
		{
			headers: {
				'OAuth-Token': oAuthToken,
			},
		}
	);

	return response.json();
}

export async function createNewGenerateKey(
	accountKey: string,
	oAuthToken: string,
	provisioningServerAPI: string,
	licenseKey: string
) {

	// eslint-disable-next-line @liferay/portal/no-global-fetch
	const response = await fetch(
		`${provisioningServerAPI}/accounts/${accountKey}/license-keys`,
		{
			body: JSON.stringify([licenseKey]),
			headers: {
				'Content-Type': 'application/json',
				'OAuth-Token': oAuthToken,
			},
			method: 'POST',
		}
	);

	return response.json();
}

export async function putSubscriptionInKey(
	oAuthToken: string,
	provisioningServerAPI: string,
	licenseKeyIds: string
) {

	// eslint-disable-next-line @liferay/portal/no-global-fetch
	const response = await fetch(
		`${provisioningServerAPI}/license-keys/subscriptions?licenseKeyIds=${licenseKeyIds}`,

		{
			headers: {
				'OAuth-Token': oAuthToken,
			},
			method: 'PUT',
		}
	);

	return response;
}

export async function deleteSubscriptionInKey(
	oAuthToken: string,
	provisioningServerAPI: string,
	licenseKeyIds: string
) {

	// eslint-disable-next-line @liferay/portal/no-global-fetch
	const response = await fetch(
		`${provisioningServerAPI}/license-keys/subscriptions?licenseKeyIds=${licenseKeyIds}`,

		{
			headers: {
				'OAuth-Token': oAuthToken,
			},
			method: 'DELETE',
		}
	);

	return response;
}

export async function getSubscriptionInKey(
	oAuthToken: string,
	provisioningServerAPI: string,
	licenseKeyIds: string
) {

	// eslint-disable-next-line @liferay/portal/no-global-fetch
	const response = await fetch(
		`${provisioningServerAPI}/license-keys/subscriptions?licenseKeyId=${licenseKeyIds}`,
		{
			headers: {
				'OAuth-Token': oAuthToken,
			},
			method: 'GET',
		}
	);

	return response.json();
}
