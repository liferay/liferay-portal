/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '../../../css/components/DefaultPermission.scss';

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayModal from '@clayui/modal';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {openModal} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useCallback, useEffect, useState} from 'react';

import CMSDefaultPermissionService from '../../common/services/CMSDefaultPermissionService';
import {triggerAssetBulkAction} from '../props_transformer/actions/triggerAssetBulkAction';
import DefaultPermissionFormContainer from './DefaultPermissionFormContainer';
import {
	ActionsMap,
	AssetRoleSelectedActions,
	BulkDefaultPermissionModalContentProps,
} from './DefaultPermissionTypes';

export function DEFAULT_PERMISSIONS(actions: ActionsMap) {
	return {
		L_CONTENTS: {
			'CMS Administrator': actions.L_CONTENTS.map((item) => item.key),
		},
		L_FILES: {
			'CMS Administrator': actions.L_FILES.map((item) => item.key),
		},
		OBJECT_ENTRY_FOLDERS: {
			'CMS Administrator': actions.OBJECT_ENTRY_FOLDERS.map(
				(item) => item.key
			),
		},
	};
}
export const DEPOT_CLASS_NAME = 'com.liferay.depot.model.DepotEntry';
export const OBJECT_DEFINITION_CLASS_NAME =
	'com.liferay.object.model.ObjectDefinition';
export const OBJECT_ENTRY_FOLDER_CLASS_NAME =
	'com.liferay.object.model.ObjectEntryFolder';

export function defaultPermissionsBulkAction({
	apiURL,
	className,
	defaultPermissionAdditionalProps,
	selectedData,
}: {
	apiURL?: string;
	className: string;
	defaultPermissionAdditionalProps: any;
	selectedData: any;
}) {
	if (
		selectedData?.selectAll ||
		!selectedData?.items?.length ||
		(className !== DEPOT_CLASS_NAME &&
			selectedData.items.find(
				(item: any) => item.entryClassName !== className
			))
	) {
		return openModal({
			bodyHTML: Liferay.Language.get(
				'this-action-is-not-available-for-the-item-you-have-selected'
			),
			buttons: [
				{
					autoFocus: true,
					displayType: 'warning',
					label: Liferay.Language.get('ok'),
					type: 'cancel',
				},
			],
			center: true,
			status: 'warning',
			title: Liferay.Language.get('action-not-allowed'),
		});
	}

	return openModal({
		containerProps: {
			className: '',
		},
		contentComponent: ({closeModal}: {closeModal: () => void}) =>
			BulkDefaultPermissionModalContent({
				...defaultPermissionAdditionalProps,
				apiURL,
				className,
				closeModal,
				selectedData,
			}),
		size: 'full-screen',
	});
}

export default function BulkDefaultPermissionModalContent({
	actions,
	apiURL,
	className,
	closeModal,
	roles,
	selectedData,
}: BulkDefaultPermissionModalContentProps & {apiURL?: string}) {
	const [currentValues, setCurrentValues] =
		useState<AssetRoleSelectedActions>({});
	const [loading, setLoading] = useState(false);

	const saveHandler = useCallback(() => {
		setLoading(true);

		triggerAssetBulkAction({
			apiURL,
			keyValues: {
				defaultPermissions: JSON.stringify(currentValues),
			},
			onCreateError: () => {
				setLoading(false);
			},
			onCreateSuccess: (_response) => {
				closeModal();

				setLoading(false);
			},
			selectedData:
				className !== DEPOT_CLASS_NAME
					? selectedData
					: {
							...selectedData,
							items: selectedData.items.map((item: any) => {
								return {
									...item,
									embedded: {
										externalReferenceCode:
											item.externalReferenceCode,
										id: item.siteId,
									},
									entryClassName: DEPOT_CLASS_NAME,
								};
							}),
						},
			type: 'DefaultPermissionBulkAction',
		});
	}, [apiURL, className, closeModal, currentValues, selectedData]);

	const onChangeHandler = useCallback((data: any) => {
		setCurrentValues(data);
	}, []);

	useEffect(() => {
		let isMounted = true;

		const getDefaultPermissions = async () => {
			if (!isMounted) {
				return;
			}

			setLoading(true);

			try {
				if (className === DEPOT_CLASS_NAME) {
					setCurrentValues(DEFAULT_PERMISSIONS(actions));
				}
				else {
					let entryClassExternalReferenceCode = '';
					let entryClassName = '';
					const firstItem = selectedData.items[0];

					if (
						['L_CONTENTS', 'L_FILES'].includes(
							firstItem.embedded
								.parentObjectEntryFolderExternalReferenceCode
						) ||
						selectedData.items.some((item: any) => {
							return (
								item.embedded
									.parentObjectEntryFolderExternalReferenceCode !==
								firstItem.embedded
									.parentObjectEntryFolderExternalReferenceCode
							);
						})
					) {
						if (
							selectedData.items.some((item: any) => {
								return (
									item.embedded.scopeKey !==
									firstItem.embedded.scopeKey
								);
							})
						) {
							setCurrentValues(DEFAULT_PERMISSIONS(actions));

							return;
						}
						else {
							const space =
								await CMSDefaultPermissionService.getSpace(
									firstItem.embedded.scopeId
								);

							entryClassExternalReferenceCode =
								space.externalReferenceCode;
							entryClassName = DEPOT_CLASS_NAME;
						}
					}
					else {
						entryClassExternalReferenceCode =
							firstItem.embedded
								.parentObjectEntryFolderExternalReferenceCode;
						entryClassName = firstItem.entryClassName;
					}

					const objectEntry =
						await CMSDefaultPermissionService.getObjectEntry({
							classExternalReferenceCode:
								entryClassExternalReferenceCode,
							className: entryClassName,
						});

					if (isMounted) {
						setCurrentValues(
							JSON.parse(objectEntry.defaultPermissions)
						);
					}
				}
			}
			catch (error) {
				console.error(error);

				setCurrentValues(DEFAULT_PERMISSIONS(actions));
			}
			finally {
				setLoading(false);
			}
		};

		getDefaultPermissions();

		return () => {
			isMounted = false;
		};
	}, [actions, className, selectedData.items]);

	return (
		<>
			<ClayModal.Header>
				{sub(
					Liferay.Language.get('edit-x'),
					Liferay.Language.get('default-permissions')
				)}

				<span className="pl-2 text-4 text-secondary text-weight-normal">
					{`(${sub(Liferay.Language.get('x-x-selected'), [
						selectedData.items.length,
						className === DEPOT_CLASS_NAME
							? Liferay.Language.get('spaces')
							: Liferay.Language.get('folders'),
					])})`}
				</span>

				<ClayTooltipProvider>
					<span
						className="pl-2 text-3"
						data-tooltip-align="bottom"
						title={Liferay.Language.get(
							'setting-default-permissions-for-this-folder-will-automatically-apply-them-to-all-newly-created-items'
						)}
					>
						<ClayIcon aria-label="Info" symbol="info-circle" />
					</span>
				</ClayTooltipProvider>
			</ClayModal.Header>

			<ClayModal.Body className="p-0">
				<DefaultPermissionFormContainer
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
