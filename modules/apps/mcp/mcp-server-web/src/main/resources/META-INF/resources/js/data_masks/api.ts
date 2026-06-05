/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ApiHelper from '../shared/api';
import {
	API_BASE,
	DATA_MASK_FK_FIELD,
	PROFILES_API,
	PROFILE_DATA_MASKS_API,
	VALIDATE_API,
} from './constants';
import {DataMask} from './types';

interface SaveResult {
	detail?: string;
	ok: boolean;
	saved?: DataMask;
}

export interface ValidationResult {
	error?: string;
	output: string;
}

interface ProfileDataMaskAssociation {
	mcpServerProfileId?: number;
	r_dataMaskToProfileDataMasks_mcpServerDataMaskId?: number;
}

export async function validateDataMask(request: {
	detectionRegex: string;
	replacementRegex: string;
	replacementValue: string;
	sampleText: string;
}): Promise<ValidationResult> {
	const {data, error} = await ApiHelper.post<ValidationResult>(
		VALIDATE_API,
		request
	);

	return data ?? {error: error ?? '', output: ''};
}

export async function deleteDataMask(id: number): Promise<boolean> {
	const {error} = await ApiHelper.del(`${API_BASE}/${id}`);

	return error === null;
}

export async function saveDataMask(
	dataMask: DataMask | null,
	payload: Record<string, unknown>
): Promise<SaveResult> {
	const result = dataMask?.id
		? await ApiHelper.patch<DataMask>(`${API_BASE}/${dataMask.id}`, payload)
		: await ApiHelper.post<DataMask>(API_BASE, payload);

	if (result.error !== null) {
		return {detail: result.error, ok: false};
	}

	return {ok: true, saved: result.data};
}

export async function fetchProfileNames(dataMaskId: number): Promise<string[]> {
	const {data} = await ApiHelper.get<{items: ProfileDataMaskAssociation[]}>(
		`${PROFILE_DATA_MASKS_API}?pageSize=200`
	);

	const profileIds = (data?.items ?? [])
		.filter((item) => item[DATA_MASK_FK_FIELD] === dataMaskId)
		.map((item) => item.mcpServerProfileId)
		.filter((profileId): profileId is number => Boolean(profileId));

	if (!profileIds.length) {
		return [];
	}

	const names = await Promise.all(
		profileIds.map(async (profileId) => {
			const {data: profile} = await ApiHelper.get<{name?: string}>(
				`${PROFILES_API}/${profileId}`
			);

			return profile?.name ?? null;
		})
	);

	return names.filter((name): name is string => Boolean(name));
}
