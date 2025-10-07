/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getRandomInt} from '../../../../utils/getRandomInt';

export async function newScimUser(randomNumber = getRandomInt()) {
	return {
		active: true,
		addresses: [
			{
				country: 'GB',
				formatted:
					'Muffin Man\n' +
					'1234 Drury Lane\n' +
					'Great Britain, England 54321\n' +
					'United Kingdom',
				locality: 'Great Britain',
				postalCode: '54321',
				primary: false,
				region: 'England',
				streetAddress: 'Muffin Man\n' + '1234 Drury Lane',
				type: 'personal',
			},
			{
				country: 'US',
				formatted:
					'The President of the United States\n' +
					'1600 Pennsylvania Ave NW\n' +
					'Washington, District of Columbia 20500\n' +
					'United States',
				locality: 'Washington',
				postalCode: '20500',
				primary: true,
				region: 'District of Columbia',
				streetAddress:
					'The President of the United States\n' +
					'1600 Pennsylvania Ave NW',
				type: 'business',
			},
		],
		displayName: 'testDisplayName',
		emails: [
			{
				primary: false,
				type: 'default',
				value: 'emailAddress1@liferay.com',
			},
			{
				primary: true,
				type: 'default',
				value: `able${randomNumber}@liferay.com`,
			},
			{
				primary: false,
				type: 'default',
				value: 'emailAddress3@liferay.com',
			},
		],
		entitlements: [
			{
				value: 'testEntitlement1',
			},
			{
				value: 'testEntitlement2',
			},
		],
		ims: [
			{
				type: 'Jabber',
				value: 'testJabberIms',
			},
			{
				type: 'Skype',
				value: 'testSkypeIms',
			},
		],
		name: {
			familyName: `Baker ${randomNumber}`,
			givenName: `Able ${randomNumber}`,
			honorificPrefix: 'Dr',
			honorificSuffix: 'Phd',
			middleName: 'testMiddleName',
		},
		nickName: 'testNickName',
		phoneNumbers: [
			{
				primary: true,
				type: 'Business',
				value: '555-555-5555',
			},
			{
				primary: false,
				type: 'Personal',
				value: '555-555-4444',
			},
		],
		photos: [
			{
				value: 'testPhoto1',
			},
			{
				value: 'testPhoto2',
			},
		],
		preferredLanguage: 'testPreferredLanguage',
		profileUrl: 'http://testProfileUrl.com',
		roles: [
			{
				value: 'Invalid Role',
			},
			{
				value: 'Power User',
			},
			{
				value: 'Supplier',
			},
		],
		timezone: 'America/Los_Angeles',
		userName: `able${randomNumber}.baker`,
		userType: 'testUserType',
		x509Certificates: [
			{
				value: 'testx509Certificate1',
			},
			{
				value: 'testx509Certificate2',
			},
		],
	};
}
