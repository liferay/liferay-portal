/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {Node} from 'react-flow-renderer';

import {
	checkPostalAddressUnsupportedObjectRelationship,
	getUnsupportedObjectRelationshipErrorMessage,
} from '../../components/ModelBuilder/utils';

interface BuildObjectDefinitionNodeMockProps {
	dbTableName?: string;
	linkedObjectDefinition?: boolean;
	modifiable?: boolean;
	objectDefinitionExternalReferenceCode: string;
	objectDefinitionName: string;
	objectRelationships?: ObjectRelationship[];
	storageType?: string;
}

const objectRelationshipMock = {
	deletionType: 'prevent',
	externalReferenceCode: '',
	id: 33837,
	label: {
		en_US: 'object relationship',
	},
	name: 'objectRelationship',
	objectDefinitionExternalReferenceCode1: 'L_ACCOUNT',
	objectDefinitionExternalReferenceCode2: 'customObjectDefinitionERC',
	objectDefinitionId1: 31670,
	objectDefinitionId2: 33563,
	objectDefinitionModifiable2: true,
	objectDefinitionName2: 'customObject',
	objectDefinitionSystem2: false,
	parameterObjectFieldId: 0,
	parameterObjectFieldName: '',
	reverse: false,
	system: false,
	type: 'oneToMany',
} as ObjectRelationship;

function buildObjectDefinitionNodeMock({
	dbTableName,
	linkedObjectDefinition,
	modifiable,
	objectDefinitionExternalReferenceCode,
	objectDefinitionName,
	objectRelationships,
	storageType,
}: BuildObjectDefinitionNodeMockProps): Node<ObjectDefinitionNodeData> {
	return {
		data: {
			accountEntryRestricted: false,
			accountEntryRestrictedObjectFieldId: '',
			accountEntryRestrictedObjectFieldName: '',
			actions: {},
			active: true,
			dateCreated: '',
			dateModified: '',
			dbTableName: dbTableName ?? '',
			defaultLanguageId: 'en_US',
			enableCategorization: true,
			enableComments: false,
			enableFormContainer: false,
			enableFriendlyURLCustomization: false,
			enableIndexSearch: false,
			enableLocalization: false,
			enableObjectEntryDraft: false,
			enableObjectEntryHistory: false,
			enableObjectEntrySchedule: false,
			enableObjectEntrySubscription: false,
			externalReferenceCode: objectDefinitionExternalReferenceCode,
			friendlyURLSeparator: objectDefinitionName,
			hasObjectDefinitionDeleteResourcePermission: false,
			hasObjectDefinitionManagePermissionsResourcePermission: true,
			hasObjectDefinitionUpdateResourcePermission: true,
			hasObjectDefinitionViewResourcePermission: true,
			id: 31201,
			label: {
				en_US: '',
			},
			linkedObjectDefinition: linkedObjectDefinition ?? false,
			modifiable: modifiable ?? true,
			name: objectDefinitionName,
			objectActions: [],
			objectFields: [],
			objectFolderExternalReferenceCode: 'default',
			objectLayouts: [],
			objectRelationships: objectRelationships ?? [],
			objectViews: [],
			panelCategoryKey: 'site_administration.content',
			parameterRequired: false,
			pluralLabel: {
				en_US: '',
			},
			portlet: false,
			restContextPath: '',
			rootObjectDefinitionExternalReferenceCode: '',
			scope: 'site',
			selected: false,
			showAllObjectFields: false,
			status: {
				code: 0,
				label: 'approved',
				label_i18n: 'Approved',
			},
			storageType: storageType ?? 'default',
			system: true,
			titleObjectFieldId: 1233,
			titleObjectFieldName: 'name',
		},
		id: '31201',
		position: {
			x: 260,
			y: 550,
		},
		type: 'objectDefinitionNode',
	};
}

describe('checkPostalAddressUnsupportedObjectRelationship function', () => {
	it('returns true if the target node name is Address', () => {
		const customObjectDefinition = buildObjectDefinitionNodeMock({
			objectDefinitionExternalReferenceCode: 'customObjectDefinitionERC',
			objectDefinitionName: 'customObject',
		});

		const postalAddressObjectDefinition = buildObjectDefinitionNodeMock({
			dbTableName: 'Address',
			objectDefinitionExternalReferenceCode: 'L_POSTAL_ADDRESS',
			objectDefinitionName: 'Address',
		});

		const unsupportedObjectRelationship =
			checkPostalAddressUnsupportedObjectRelationship(
				[customObjectDefinition, postalAddressObjectDefinition],
				customObjectDefinition,
				postalAddressObjectDefinition
			);

		expect(unsupportedObjectRelationship).toBe(true);
	});

	it('returns true if the source node name is Address, but the target node does not have a relationship with the Account object', () => {
		const accountObjectDefinition = buildObjectDefinitionNodeMock({
			objectDefinitionExternalReferenceCode: 'L_ACCOUNT',
			objectDefinitionName: 'AccountEntry',
		});

		const customObjectDefinition = buildObjectDefinitionNodeMock({
			objectDefinitionExternalReferenceCode: 'customObjectDefinitionERC',
			objectDefinitionName: 'customObject',
		});

		const postalAddressObjectDefinition = buildObjectDefinitionNodeMock({
			dbTableName: 'Address',
			objectDefinitionExternalReferenceCode: 'L_POSTAL_ADDRESS',
			objectDefinitionName: 'Address',
		});

		const unsupportedObjectRelationship =
			checkPostalAddressUnsupportedObjectRelationship(
				[
					accountObjectDefinition,
					customObjectDefinition,
					postalAddressObjectDefinition,
				],
				postalAddressObjectDefinition,
				customObjectDefinition
			);

		expect(unsupportedObjectRelationship).toBe(true);
	});

	it('returns false if the source node name is Address but the target node has a relationship with the Account object', () => {
		const accountObjectDefinition = buildObjectDefinitionNodeMock({
			objectDefinitionExternalReferenceCode: 'L_ACCOUNT',
			objectDefinitionName: 'AccountEntry',
			objectRelationships: [objectRelationshipMock],
		});

		const customObjectDefinition = buildObjectDefinitionNodeMock({
			objectDefinitionExternalReferenceCode: 'customObjectDefinitionERC',
			objectDefinitionName: 'customObject',
		});

		const postalAddressObjectDefinition = buildObjectDefinitionNodeMock({
			objectDefinitionExternalReferenceCode: 'L_POSTAL_ADDRESS',
			objectDefinitionName: 'Address',
		});

		const unsupportedObjectRelationship =
			checkPostalAddressUnsupportedObjectRelationship(
				[
					accountObjectDefinition,
					customObjectDefinition,
					postalAddressObjectDefinition,
				],
				postalAddressObjectDefinition,
				customObjectDefinition
			);

		expect(unsupportedObjectRelationship).toBe(false);
	});

	it('returns false if the source and target nodes are custom objects', () => {
		const customObjectDefinition = buildObjectDefinitionNodeMock({
			objectDefinitionExternalReferenceCode: 'customObjectDefinitionERC',
			objectDefinitionName: 'customObject',
		});

		const customObjectDefinition2 = buildObjectDefinitionNodeMock({
			objectDefinitionExternalReferenceCode: 'customObjectDefinitionERC2',
			objectDefinitionName: 'customObject2',
		});

		const unsupportedObjectRelationship =
			checkPostalAddressUnsupportedObjectRelationship(
				[customObjectDefinition, customObjectDefinition2],
				customObjectDefinition,
				customObjectDefinition2
			);

		expect(unsupportedObjectRelationship).toBe(false);
	});
});

describe('getUnsupportedObjectRelationshipErrorMessage function', () => {
	it('returns errorMessage when source and target object definition nodes are unmodifiable', () => {
		const unmodifiableObjectDefinition = buildObjectDefinitionNodeMock({
			modifiable: false,
			objectDefinitionExternalReferenceCode: 'customObjectDefinitionERC',
			objectDefinitionName: 'customObject',
		});

		const unmodifiableObjectDefinition2 = buildObjectDefinitionNodeMock({
			modifiable: false,
			objectDefinitionExternalReferenceCode: 'customObjectDefinitionERC2',
			objectDefinitionName: 'customObject2',
		});

		const errorMessage = getUnsupportedObjectRelationshipErrorMessage(
			[unmodifiableObjectDefinition, unmodifiableObjectDefinition2],
			unmodifiableObjectDefinition,
			unmodifiableObjectDefinition2
		);

		expect(errorMessage).toMatchObject({
			errorMessage: 'unmodifiable-system-objects-cannot-be-related',
		});
	});

	it('returns errorMessage when the source node is a linked object definition', () => {
		const linkedObjectDefinition = buildObjectDefinitionNodeMock({
			linkedObjectDefinition: true,
			objectDefinitionExternalReferenceCode: 'customObjectDefinitionERC',
			objectDefinitionName: 'customObject',
		});

		const customObjectDefinition2 = buildObjectDefinitionNodeMock({
			objectDefinitionExternalReferenceCode: 'customObjectDefinitionERC2',
			objectDefinitionName: 'customObject2',
		});

		const errorMessage = getUnsupportedObjectRelationshipErrorMessage(
			[linkedObjectDefinition, customObjectDefinition2],
			linkedObjectDefinition,
			customObjectDefinition2
		);

		expect(errorMessage).toMatchObject({
			errorMessage:
				'adding-relationships-directly-to-linked-objects-is-not-allowed',
		});
	});

	it('returns errorMessage when source and target object definition nodes have Salesforce storageType', () => {
		const salesforceObjectDefinition = buildObjectDefinitionNodeMock({
			objectDefinitionExternalReferenceCode: 'customObjectDefinitionERC',
			objectDefinitionName: 'customObject',
			storageType: 'salesforce',
		});

		const salesforceObjectDefinition2 = buildObjectDefinitionNodeMock({
			objectDefinitionExternalReferenceCode: 'customObjectDefinitionERC2',
			objectDefinitionName: 'customObject2',
			storageType: 'salesforce',
		});

		const errorMessage = getUnsupportedObjectRelationshipErrorMessage(
			[salesforceObjectDefinition, salesforceObjectDefinition2],
			salesforceObjectDefinition,
			salesforceObjectDefinition2
		);

		expect(errorMessage).toMatchObject({
			errorMessage: 'salesforce-objects-do-not-support-relationships',
		});
	});

	it('returns errorMessage when there is an unsupported postal address object relationship', () => {
		const customObjectDefinition = buildObjectDefinitionNodeMock({
			objectDefinitionExternalReferenceCode: 'customObjectDefinitionERC',
			objectDefinitionName: 'customObject',
		});

		const postalAddressObjectDefinition = buildObjectDefinitionNodeMock({
			dbTableName: 'Address',
			objectDefinitionExternalReferenceCode: 'L_POSTAL_ADDRESS',
			objectDefinitionName: 'Address',
		});

		const errorMessage = getUnsupportedObjectRelationshipErrorMessage(
			[customObjectDefinition, postalAddressObjectDefinition],
			customObjectDefinition,
			postalAddressObjectDefinition
		);

		expect(errorMessage).toMatchObject({
			errorMessage:
				'postal-address-can-only-have-a-relationship-with-the-account-object',
			learnMessage: 'accessing-accounts-data-from-custom-objects',
		});
	});
});
