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

import CMSDefaultPermissionService from '../../common/services/CMSDefaultPermissionService';
import {triggerAssetBulkAction} from '../props_transformer/actions/triggerAssetBulkAction';
import {
	DEFAULT_PERMISSIONS,
	DEPOT_CLASS_NAME,
	OBJECT_DEFINITION_CLASS_NAME,
	OBJECT_ENTRY_FOLDER_CLASS_NAME,
} from './BulkDefaultPermissionModalContent';
import DefaultPermissionFormContainer from './DefaultPermissionFormContainer';
import {
	AssetRoleSelectedActions,
	AssetType,
	BulkPermissionModalContentProps,
	DefaultAssetTypes,
} from './DefaultPermissionTypes';

export function permissionsBulkAction({
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
	return openModal({
		containerProps: {
			className: '',
		},
		contentComponent: ({closeModal}: {closeModal: () => void}) =>
			BulkPermissionModalContent({
				...defaultPermissionAdditionalProps,
				apiURL,
				className,
				closeModal,
				selectedData,
			}),
		size: 'full-screen',
	});
}

export default function BulkPermissionModalContent({
	actions,
	apiURL,
	className,
	closeModal,
	roles,
	selectedData,
}: BulkPermissionModalContentProps & {apiURL: string}) {
	const [currentValues, setCurrentValues] =
		useState<AssetRoleSelectedActions>({});
	const [loading, setLoading] = useState(false);
	const [tabs, setTabs] = useState<Array<AssetType> | undefined>(undefined);

	const saveHandler = useCallback(
		(event: any) => {
			event.preventDefault();

			setLoading(true);

			triggerAssetBulkAction({
				apiURL,
				keyValues: {
					configuration: JSON.stringify(currentValues),
				},
				onCreateError: () => {
					setLoading(false);
				},
				onCreateSuccess: (_response) => {
					closeModal();

					setLoading(false);
				},
				selectedData,
				type: 'PermissionBulkAction',
			});
		},
		[apiURL, closeModal, currentValues, selectedData]
	);

	const onChangeHandler = useCallback((data: any) => {
		setCurrentValues(data);
	}, []);

	useEffect(() => {
		let isMounted = true;

		const getPermissions = async () => {
			if (!isMounted) {
				return;
			}

			setLoading(true);

			try {
				if (selectedData?.selectAll) {
					setCurrentValues(DEFAULT_PERMISSIONS(actions));
				}
				else {
					let entryClassExternalReferenceCode = '';
					let entryClassName = '';
					const firstItem = selectedData.items[0];

					if (
						['L_CONTENTS', 'L_FILES'].includes(
							firstItem.embedded
								.parentObjectEntryFolderExternalReferenceCode ||
								firstItem.embedded
									.objectEntryFolderExternalReferenceCode
						) ||
						selectedData.items.some((item: any) => {
							return (
								(item.embedded
									.parentObjectEntryFolderExternalReferenceCode ||
									item.embedded
										.objectEntryFolderExternalReferenceCode) !==
									firstItem.embedded
										.parentObjectEntryFolderExternalReferenceCode ||
								firstItem.embedded
									.objectEntryFolderExternalReferenceCode
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
								.parentObjectEntryFolderExternalReferenceCode ||
							firstItem.embedded
								.objectEntryFolderExternalReferenceCode;
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

		getPermissions();

		if (!selectedData.selectAll) {
			const tabs: AssetType[] = [];

			selectedData.items.forEach((item: any) => {
				if (
					item.entryClassName === OBJECT_ENTRY_FOLDER_CLASS_NAME &&
					!tabs.find(
						(tab) =>
							tab.key === DefaultAssetTypes.OBJECT_ENTRY_FOLDERS
					)
				) {
					tabs.unshift({
						key: DefaultAssetTypes.OBJECT_ENTRY_FOLDERS,
						label: Liferay.Language.get('folder-permissions'),
					});
				}
				else if (
					item.entryClassName.startsWith(
						OBJECT_DEFINITION_CLASS_NAME
					) &&
					!tabs.find(
						(tab) => tab.key === DefaultAssetTypes.L_CONTENTS
					)
				) {
					tabs.push({
						key: DefaultAssetTypes.L_CONTENTS,
						label: Liferay.Language.get('content-permissions'),
					});
					tabs.push({
						key: DefaultAssetTypes.L_FILES,
						label: Liferay.Language.get('file-permissions'),
					});
				}
			});

			setTabs(tabs);
		}
		else {
			setTabs(undefined);
		}

		return () => {
			isMounted = false;
		};
	}, [actions, className, selectedData.items, selectedData.selectAll]);

	return (
		<>
			<ClayModal.Header>
				{sub(
					Liferay.Language.get('edit-x'),
					Liferay.Language.get('permissions')
				)}

				<span className="pl-2 text-4 text-secondary text-weight-normal">
					{`(${sub(Liferay.Language.get('x-x-selected'), [
						selectedData.items.length,
						className === DEPOT_CLASS_NAME
							? Liferay.Language.get('spaces')
							: Liferay.Language.get('items'),
					])})`}
				</span>
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
					types={tabs}
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
