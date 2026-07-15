/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ApiHelper} from '@liferay/site-cms-site-initializer';

import {ROOM_STATUS} from '../utils/roomStatus';
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
	userAccount: {
		emailAddress: string;
		membershipExpirationDate?: string;
		roleKey?: string;
	}
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

async function archiveRoom(roomId: number): Promise<IRoomObjectEntry> {
	const {data, error} = await ApiHelper.patch<IRoomObjectEntry>(
		{
			archiveDate: new Date().toISOString().slice(0, 10),
			roomStatus: ROOM_STATUS.INACTIVE,
		},
		`${BASE_PATH}/${roomId}`
	);

	if (data) {
		return data;
	}

	throw new Error(error);
}

async function checkSitePages(
	siteExternalReferenceCode: string
): Promise<{items: Array<any>}> {
	const url = `/o/headless-admin-site/v1.0/sites/${siteExternalReferenceCode}/site-pages`;

	const {data, error} = await ApiHelper.get<{items: Array<any>}>(url);

	if (data) {
		return data || {items: []};
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

async function duplicateRoom(
	roomId: number,
	{fileEntryIds, name}: {fileEntryIds: number[]; name: string}
): Promise<IRoomObjectEntry> {
	const {data, error} = await ApiHelper.post<IRoomObjectEntry>(
		`${DSR_PATH}/${roomId}/duplicate`,
		{
			fileEntryIds,
			name,
		}
	);

	if (data) {
		return data;
	}

	throw new Error(error);
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

async function getDocumentsFolderId(
	siteId: number,
	folderExternalReferenceCode: string
): Promise<number> {
	const {data, error} = await ApiHelper.get<{id: number}>(
		`/o/headless-delivery/v1.0/sites/${siteId}` +
			`/documents-folder/by-external-reference-code/${folderExternalReferenceCode}`
	);

	if (data) {
		return data.id;
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

async function getRooms(): Promise<{items: IRoomObjectEntry[]}> {
	const {data, error} = await ApiHelper.get<{items: IRoomObjectEntry[]}>(
		`${BASE_PATH}`
	);

	if (data) {
		return data;
	}

	throw new Error(error);
}

async function restoreRoom(roomId: number): Promise<IRoomObjectEntry> {
	const {data, error} = await ApiHelper.patch<IRoomObjectEntry>(
		{
			archiveDate: null,
			roomStatus: ROOM_STATUS.ACTIVE,
		},
		`${BASE_PATH}/${roomId}`
	);

	if (data) {
		return data;
	}

	throw new Error(error);
}

async function updateRoom(
	roomId: number,
	{
		trend,
	}: {
		trend: number;
	}
): Promise<IRoomObjectEntry> {
	const {data, error} = await ApiHelper.patch<IRoomObjectEntry>(
		{
			trend,
		},
		`${BASE_PATH}/${roomId}`
	);

	if (data) {
		return data;
	}

	throw new Error(error);
}

async function updateRoomSettings(
	roomId: number,
	{
		externalReferenceCode,
		friendlyURL,
		name,
	}: {
		externalReferenceCode: string;
		friendlyURL: string;
		name: string;
	}
): Promise<IRoomObjectEntry> {
	const {data, error} = await ApiHelper.patch<IRoomObjectEntry>(
		{
			externalReferenceCode,
			friendlyURL,
			name,
		},
		`${BASE_PATH}/${roomId}`
	);

	if (data) {
		return data;
	}

	throw new Error(error);
}

async function updateRoomInvitedMember(
	roomId: number,
	invitedMemberId: number,
	invitedMember: {membershipExpirationDate?: string; roleKey?: string}
): Promise<IInvitedMember> {
	const {data, error} = await ApiHelper.patch<IInvitedMember>(
		invitedMember,
		`${DSR_PATH}/${roomId}/invited-members/${invitedMemberId}`
	);

	if (data) {
		return data;
	}

	throw new Error(error);
}

async function updateRoomUserAccount(
	roomId: number,
	userId: number,
	userAccount: {membershipExpirationDate?: string; roleKey?: string}
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
	archiveRoom,
	checkSitePages,
	deleteRoomInvitedMember,
	deleteRoomUserAccount,
	duplicateRoom,
	getAccounts,
	getDocumentsFolderId,
	getRoom,
	getRoomInvitedMembers,
	getRoomUserAccounts,
	getRooms,
	restoreRoom,
	updateRoom,
	updateRoomInvitedMember,
	updateRoomSettings,
	updateRoomUserAccount,
};
