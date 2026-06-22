/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLabel from '@clayui/label';
import ClayLink from '@clayui/link';
import {
	FrontendDataSet,

	// @ts-ignore

} from '@liferay/frontend-data-set-web';
import classNames from 'classnames';
import {ILearnResourceContext, openToast} from 'frontend-js-components-web';
import {sessionStorage} from 'frontend-js-web';
import React, {useEffect, useMemo, useState} from 'react';

import {defaultFDSDataSetProps, formatActionURL} from '../../utils/fds';
import {getEditObjectRelationshipURL} from '../../utils/url';
import LabelRenderer from '../LabelRenderer';
import ModalDeletionNotAllowed from '../ModalDeletionNotAllowed';
import {deleteRelationship} from '../ViewObjectDefinitions/objectDefinitionUtil';
import {ModalAddObjectRelationship} from './ModalAddObjectRelationship';
import {ModalDeleteObjectRelationship} from './ModalDeleteObjectRelationship';

import type {FDSItem, IFDSTableProps} from '../../utils/fds';

interface ItemData {
	edge?: boolean;
	id: number;
	reverse: boolean;
	system?: boolean;
}

interface RelationshipsProps extends IFDSTableProps {
	baseResourceURL: string;
	isApproved: boolean;
	learnResources: ILearnResourceContext;
	objectDefinitionExternalReferenceCode: string;
	objectDefinitionId: string;
	objectRelationshipTypes: string[];
	parameterRequired: boolean;
}

const tableFields = [
	{
		contentRenderer: 'ObjectFieldLabelDataRenderer',
		expand: false,
		fieldName: 'label',
		label: Liferay.Language.get('label'),
		localizeLabel: true,
		sortable: true,
	},
	{
		expand: false,
		fieldName: 'objectDefinitionName2',
		label: Liferay.Language.get('related-object'),
		localizeLabel: true,
		sortable: false,
	},
	{
		expand: false,
		fieldName: 'type',
		label: Liferay.Language.get('type'),
		localizeLabel: true,
		sortable: false,
	},
	{
		contentRenderer: 'ObjectFieldHierarchyDataRenderer',
		expand: false,
		fieldName: 'hierarchy',
		label: Liferay.Language.get('hierarchy'),
		localizeLabel: true,
		sortable: false,
	},
	{
		contentRenderer: 'ObjectRelationshipSourceDataRenderer',
		expand: false,
		fieldName: 'source',
		label: Liferay.Language.get('source'),
		localizeLabel: true,
		sortable: false,
	},
];

function ObjectFieldHierarchyDataRenderer({itemData}: {itemData: ItemData}) {
	return (
		<strong
			className={classNames(
				itemData.reverse ? 'label-info' : 'label-success',
				'label'
			)}
		>
			{itemData.reverse
				? Liferay.Language.get('child')
				: Liferay.Language.get('parent')}
		</strong>
	);
}

function ObjectRelationshipInheritanceDataRenderer({
	itemData,
}: {
	itemData: ItemData;
}) {
	return (
		<ClayLabel
			className={classNames('label-inverse-secondary', {
				'label-inverse-info': itemData.edge,
			})}
		>
			{itemData.edge
				? Liferay.Language.get('inherited')
				: Liferay.Language.get('standard')}
		</ClayLabel>
	);
}

function ObjectRelationshipSourceDataRenderer({
	itemData,
}: {
	itemData: ItemData;
}) {
	return (
		<strong
			className={classNames(
				itemData.system ? 'label-info' : 'label-warning',
				'label'
			)}
		>
			{itemData.system
				? Liferay.Language.get('system')
				: Liferay.Language.get('custom')}
		</strong>
	);
}

export default function Relationships({
	apiURL,
	baseResourceURL,
	creationMenu,
	formName,
	id,
	isApproved,
	items,
	learnResources,
	objectDefinitionExternalReferenceCode,
	objectDefinitionId,
	parameterRequired,
	style,
	url,
}: RelationshipsProps) {
	const [objectRelationship, setObjectRelationship] =
		useState<ObjectRelationship | null>();
	const [showAddModal, setShowAddModal] = useState(false);
	const [showDeleteModal, setShowDeleteModal] = useState(false);
	const [showDeletionNotAllowedModal, setShowDeletionNotAllowedModal] =
		useState(false);

	const fields = useMemo(() => {
		const updatedTableFields = [...tableFields];

		const inheritanceField = {
			contentRenderer: 'ObjectRelationshipInheritanceDataRenderer',
			expand: false,
			fieldName: 'relationshipInheritance',
			label: Liferay.Language.get('permission-inheritance'),
			localizeLabel: true,
			sortable: false,
		};

		updatedTableFields.splice(4, 0, inheritanceField);

		return updatedTableFields;
	}, []);

	function ObjectFieldLabelDataRenderer({
		itemData,
		openSidePanel,
		value,
	}: FDSItem<ItemData>) {
		return (
			<LabelRenderer
				onClick={() => {
					openSidePanel({
						url: formatActionURL(url, itemData.id),
					});
				}}
				value={value}
			/>
		);
	}

	const frontendDataSetProps = {
		...defaultFDSDataSetProps,
		apiURL,
		creationMenu,
		customDataRenderers: {
			ObjectFieldHierarchyDataRenderer,
			ObjectFieldLabelDataRenderer,
			ObjectRelationshipInheritanceDataRenderer,
			ObjectRelationshipSourceDataRenderer,
		},
		formName,
		id,
		itemsActions: items,
		namespace:
			'_com_liferay_object_web_internal_object_definitions_portlet_ObjectDefinitionsPortlet_',
		onActionDropdownItemClick({
			action,
			itemData,
		}: {
			action: {data: {id: string}};
			itemData: ObjectRelationship;
		}) {
			if (action.data.id === 'deleteObjectRelationship') {
				if (itemData.edge) {
					setShowDeletionNotAllowedModal(true);

					return;
				}

				if (isApproved || itemData.reverse) {
					setObjectRelationship(itemData);
					setShowDeleteModal(true);
				}
				else {
					deleteRelationship(itemData.id, true);
				}
			}
		},
		portletId:
			'com_liferay_object_web_internal_object_definitions_portlet_ObjectDefinitionsPortlet',
		style,
		views: [
			{
				contentRenderer: 'table',
				label: 'Table',
				name: 'table',
				schema: {
					fields,
				},
				thumbnail: 'table',
			},
		],
	};

	const onAfterAddObjectRelationship = async ({
		objectDefinitionId1,
	}: ObjectRelationship) => {
		const toastMessage = Liferay.Language.get(
			'relationship-was-created-successfully'
		);

		let toastAction;

		if (objectDefinitionId !== objectDefinitionId1.toString()) {
			toastAction = {
				linkHref: await getEditObjectRelationshipURL(
					baseResourceURL,
					objectDefinitionId1
				),
				linkLabel: Liferay.Language.get('view-relationship'),
			};
		}

		sessionStorage.setItem(
			'addObjectRelationshipSuccessToast',
			JSON.stringify({
				toastAction,
				toastMessage,
			}),
			sessionStorage.TYPES.NECESSARY
		);
	};

	useEffect(() => {
		Liferay.on('addObjectRelationship', () => setShowAddModal(true));

		const addObjectRelationshipSuccessToast = sessionStorage.getItem(
			'addObjectRelationshipSuccessToast',
			sessionStorage.TYPES.NECESSARY
		);

		if (addObjectRelationshipSuccessToast) {
			const {toastAction, toastMessage} = JSON.parse(
				addObjectRelationshipSuccessToast
			);

			openToast({
				message: toastMessage,
				toastProps: toastAction && {
					actions: (
						<ClayLink
							decoration="underline"
							href={toastAction.linkHref}
							style={{fontWeight: 'bold'}}
						>
							{toastAction.linkLabel}
						</ClayLink>
					),
				},
			});

			sessionStorage.removeItem('addObjectRelationshipSuccessToast');
		}

		return () => {
			Liferay.detach('addObjectRelationship');
		};
	}, []);

	return (
		<>
			<FrontendDataSet {...frontendDataSetProps} />

			{showAddModal && (
				<ModalAddObjectRelationship
					baseResourceURL={baseResourceURL}
					handleOnClose={() => setShowAddModal(false)}
					learnResources={learnResources}
					objectDefinitionExternalReferenceCode1={
						objectDefinitionExternalReferenceCode
					}
					objectRelationshipParameterRequired={parameterRequired}
					onAfterAddObjectRelationship={onAfterAddObjectRelationship}
				/>
			)}

			{showDeleteModal && objectRelationship && (
				<ModalDeleteObjectRelationship
					handleOnClose={() => setShowDeleteModal(false)}
					objectRelationship={
						objectRelationship as ObjectRelationship
					}
					setObjectRelationship={setObjectRelationship}
				/>
			)}

			{showDeletionNotAllowedModal && (
				<ModalDeletionNotAllowed
					content={
						<span
							dangerouslySetInnerHTML={{
								__html: Liferay.Language.get(
									'you-cannot-delete-a-relationship-with-inheritance-enabled.-disable-inheritance-before-deleting-the-relationship'
								),
							}}
						/>
					}
					onModalClose={() => setShowDeletionNotAllowedModal(false)}
				/>
			)}
		</>
	);
}
