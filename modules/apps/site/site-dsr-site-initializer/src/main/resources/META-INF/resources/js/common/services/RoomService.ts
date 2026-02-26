/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ApiHelper} from '@liferay/site-cms-site-initializer';

import {
	IAccount,
	IInvitedMember,
	IInvitedMembersDTO,
	IRoomObjectEntry,
	IUserAccount,
	IUserAccountsDTO,
} from '../utils/types';

const BASE_PATH = '/o/digital-sales-room/rooms';
const DSR_PATH = '/o/headless-dsr/v1.0/rooms';

async function addRoom({
	accountEntryId,
	friendlyURL,
	name,
	siteTemplateKey,
}: {
	accountEntryId: number;
	friendlyURL: string;
	name: string;
	siteTemplateKey?: string;
}): Promise<IRoomObjectEntry> {
	const {data, error} = await ApiHelper.post<IRoomObjectEntry>(BASE_PATH, {
		friendlyURL,
		name,
		r_accountToDSRRooms_accountEntryId: accountEntryId,
		siteTemplateKey: siteTemplateKey || '',
	});

	if (data) {
		return data;
	}

	throw new Error(error);
}

async function addRoomUserAccount(
	roomId: number,
	userAccount: {emailAddress: string; roleKey?: string}
): Promise<IUserAccount> {
	const {data, error} = await ApiHelper.post<IUserAccount>(
		`${DSR_PATH}/${roomId}/user-accounts`,
		userAccount
	);

	if (data) {
		return data;
	}

	throw new Error(error);
}

async function deleteRoomInvitedMember(
	roomId: number,
	invitedMemberId: number
): Promise<void> {
	const {error} = await ApiHelper.delete(
		`${DSR_PATH}/${roomId}/invited-members/${invitedMemberId}`
	);

	if (error) {
		throw new Error(error);
	}
}

async function deleteRoomUserAccount(
	roomId: number,
	userId: number
): Promise<void> {
	const {error} = await ApiHelper.delete(
		`${DSR_PATH}/${roomId}/user-accounts/${userId}`
	);

	if (error) {
		throw new Error(error);
	}
}

async function getAccounts(
	accountName?: string
): Promise<{items: Array<IAccount>}> {
	let url = '/o/headless-admin-user/v1.0/accounts?sort=name:asc';

	if (accountName) {
		url += `&search=${encodeURIComponent(accountName)}`;
	}

	const {data, error} = await ApiHelper.get<{items: Array<IAccount>}>(url);

	if (data) {
		return data || {items: []};
	}

	throw new Error(error);
}

async function getRoom(id: number): Promise<IRoomObjectEntry> {
	const {data, error} = await ApiHelper.get<IRoomObjectEntry>(
		`${BASE_PATH}/${id}`
	);

	if (data) {
		return data;
	}

	throw new Error(error);
}

async function getRoomInvitedMembers(
	roomId: number
): Promise<IInvitedMember[]> {
	const {data, error} = await ApiHelper.get<IInvitedMembersDTO>(
		`${DSR_PATH}/${roomId}/invited-members`
	);

	if (data) {
		return data.items || [];
	}

	throw new Error(error);
}

async function getRoomUserAccounts(roomId: number): Promise<IUserAccount[]> {
	const {data, error} = await ApiHelper.get<IUserAccountsDTO>(
		`${DSR_PATH}/${roomId}/user-accounts`
	);

	if (data) {
		return data.items || [];
	}

	throw new Error(error);
}

async function updateRoomUserAccount(
	roomId: number,
	userId: number,
	userAccount: {roleKey?: string}
): Promise<IUserAccount> {
	const {data, error} = await ApiHelper.patch<IUserAccount>(
		userAccount,
		`${DSR_PATH}/${roomId}/user-accounts/${userId}`
	);

	if (data) {
		return data;
	}

	throw new Error(error);
}

export default {
	addRoom,
	addRoomUserAccount,
	deleteRoomInvitedMember,
	deleteRoomUserAccount,
	getAccounts,
	getRoom,
	getRoomInvitedMembers,
	getRoomUserAccounts,
	updateRoomUserAccount,
};
