/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '../../../css/components/DefaultPermission.scss';

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import {openModal} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useCallback, useEffect, useState} from 'react';

import {triggerAssetBulkAction} from '../props_transformer/actions/triggerAssetBulkAction';
import {DEPOT_CLASS_NAME} from './BulkDefaultPermissionModalContent';
import DefaultPermissionForm from './DefaultPermissionForm';
import {
	CheckedRoleActions,
	RoleSelectedActions,
	SpacesBulkPermissionModalContentProps,
} from './DefaultPermissionTypes';

export function permissionsBulkAction({
	apiURL,
	className,
	selectedData,
	spacePermissionAdditionalProps,
}: {
	apiURL?: string;
	className: string;
	selectedData: any;
	spacePermissionAdditionalProps: any;
}) {
	return openModal({
		containerProps: {
			className: '',
		},
		contentComponent: ({closeModal}: {closeModal: () => void}) =>
			SpacesBulkPermissionModalContent({
				...spacePermissionAdditionalProps,
				apiURL,
				className,
				closeModal,
				selectedData,
			}),
		size: 'full-screen',
	});
}

export default function SpacesBulkPermissionModalContent({
	actions,
	apiURL,
	closeModal,
	roles,
	selectedData,
}: SpacesBulkPermissionModalContentProps & {apiURL?: string}) {
	const [currentValues, setCurrentValues] = useState<RoleSelectedActions>({});
	const [loading, setLoading] = useState(false);

	useEffect(() => {
		setLoading(true);

		setCurrentValues(
			roles.reduce(
				(initialData, {actions, key}) => ({
					...initialData,
					[key]: actions || [],
				}),
				{}
			)
		);

		setLoading(false);
	}, [roles]);

	const saveHandler = useCallback(() => {
		setLoading(true);

		const permissions = [];

		for (const [key, value] of Object.entries(currentValues)) {
			if (value && value.length) {
				permissions.push({
					actionIds: value,
					roleName: key,
				});
			}
		}

		triggerAssetBulkAction({
			apiURL,
			keyValues: {
				permissions,
			},
			onCreateError: () => {
				setLoading(false);
			},
			onCreateSuccess: (_response) => {
				closeModal();

				setLoading(false);
			},
			selectedData: {
				...selectedData,
				items: selectedData.items.map((item: any) => {
					return {
						...item,
						embedded: {
							externalReferenceCode: item.externalReferenceCode,
							id: item.siteId,
						},
						entryClassName: DEPOT_CLASS_NAME,
					};
				}),
			},
			type: 'PermissionBulkAction',
		});
	}, [apiURL, closeModal, currentValues, selectedData]);

	const onChangeHandler = useCallback(
		(checkedRoleActions: CheckedRoleActions) => {
			const roleSelectedActions: RoleSelectedActions = {};

			for (const [key, value] of Object.entries(checkedRoleActions)) {
				const lastIndex = key.lastIndexOf('#');

				const roleKey = key.slice(0, lastIndex);
				const action = key.slice(lastIndex + 1);

				const existingData = roleSelectedActions[roleKey] || [];

				if (!value) {
					const index = existingData.indexOf(action);

					if (index >= 0) {
						existingData.splice(index, 1);
					}
				}
				else {
					existingData.push(action);
				}

				roleSelectedActions[roleKey] = existingData;
			}

			setCurrentValues({
				...currentValues,
				...roleSelectedActions,
			});
		},
		[currentValues]
	);

	return (
		<>
			<ClayModal.Header>
				{sub(
					Liferay.Language.get('edit-x'),
					Liferay.Language.get('permissions')
				)}

				<span className="pl-2 text-4 text-secondary text-weight-normal">
					{`(${sub(Liferay.Language.get('x-x-selected'), [
						selectedData.items.length
							? selectedData.items.length
							: Liferay.Language.get('all'),
						Liferay.Language.get('spaces'),
					])})`}
				</span>
			</ClayModal.Header>

			<ClayModal.Body className="p-0">
				<DefaultPermissionForm
					actions={actions}
					disabled={loading}
					infoBoxMessage={Liferay.Language.get(
						'please-be-aware-that-the-configuration-shown-at-the-beginning-is-the-default-of-the-parent-level'
					)}
					onChange={onChangeHandler}
					roles={roles}
					values={currentValues}
				/>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							data-testid="button-cancel"
							disabled={loading}
							displayType="secondary"
							onClick={closeModal}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							data-testid="button-save"
							disabled={loading}
							onClick={saveHandler}
						>
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
}
