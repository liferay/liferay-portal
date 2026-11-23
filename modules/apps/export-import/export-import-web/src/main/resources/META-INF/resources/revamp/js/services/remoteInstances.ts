/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {RemoteInstance, RemoteInstanceRequest} from '../types/remoteInstance';
import ApiHelper, {RequestResult} from './ApiHelper';

const REMOTE_INSTANCES_URL = '/o/remote-instances';

const REMOTE_INSTANCE_FIELDS = [
	'clientId',
	'hostname',
	'id',
	'name',
	'port',
	'secureConnection',
].join(',');

export const REMOTE_INSTANCES_API_URL = `${REMOTE_INSTANCES_URL}?fields=actions,${REMOTE_INSTANCE_FIELDS}`;

export async function addRemoteInstance(
	remoteInstanceRequest: RemoteInstanceRequest
): Promise<RequestResult<RemoteInstance>> {
	return ApiHelper.post<RemoteInstance>(
		`${REMOTE_INSTANCES_URL}?fields=${REMOTE_INSTANCE_FIELDS}`,
		remoteInstanceRequest
	);
}

export async function deleteRemoteInstance(
	remoteInstanceId: number
): Promise<RequestResult<void>> {
	return ApiHelper.delete(`${REMOTE_INSTANCES_URL}/${remoteInstanceId}`);
}

export async function updateRemoteInstance(
	remoteInstanceId: number,
	remoteInstanceRequest: RemoteInstanceRequest
): Promise<RequestResult<RemoteInstance>> {
	return ApiHelper.patch<RemoteInstance>(
		`${REMOTE_INSTANCES_URL}/${remoteInstanceId}?fields=${REMOTE_INSTANCE_FIELDS}`,
		remoteInstanceRequest
	);
}
