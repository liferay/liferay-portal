/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '../../../css/components/DefaultPermission.scss';

import ClayButton from '@clayui/button';
import ClayForm, {ClaySelectWithOption} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayModal from '@clayui/modal';
import {sub} from 'frontend-js-web';
import React, {useCallback, useEffect, useRef, useState} from 'react';

import HelpTooltipIcon from '../../common/components/forms/HelpTooltipIcon';
import CMSDefaultPermissionService from '../../common/services/CMSDefaultPermissionService';
import SpaceService from '../../common/services/SpaceService';
import {OBJECT_ENTRY_FOLDER_CLASS_NAME} from '../../common/utils/constants';
import {getScopeExternalReferenceCode} from '../../common/utils/getScopeExternalReferenceCode';
import {openCMSModal} from '../../common/utils/openCMSModal';
import {triggerAssetBulkAction} from '../props_transformer/actions/triggerAssetBulkAction';
import {
	DEFAULT_PERMISSIONS,
	DEPOT_CLASS_NAME,
	OBJECT_DEFINITION_CLASS_NAME,
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
	section,
	selectedData,
	singleRoleMode,
}: {
	apiURL?: string;
	className: string;
	defaultPermissionAdditionalProps: any;
	section?: string;
	selectedData: any;
	singleRoleMode?: boolean;
}) {
	return openCMSModal({
		contentComponent: ({closeModal}: {closeModal: () => void}) =>
			BulkPermissionModalContent({
				...defaultPermissionAdditionalProps,
				apiURL,
				className,
				closeModal,
				section,
				selectedData,
				singleRoleMode,
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
	section,
	selectedData,
	singleRoleMode,
}: BulkPermissionModalContentProps & {
	apiURL: string;
	section?: string;
	singleRoleMode?: boolean;
}) {
	const [currentValues, setCurrentValues] =
		useState<AssetRoleSelectedActions>({});
	const [loading, setLoading] = useState(false);
	const [selectedRole, setSelectedRole] = useState<string>('');
	const [tabs, setTabs] = useState<Array<AssetType> | undefined>(undefined);
	const modalRef = useRef<HTMLDivElement>(null);

	useEffect(() => {
		if (modalRef.current) {
			const modalContainer = modalRef.current.closest(
				'.modal-dialog'
			) as HTMLElement;

			if (singleRoleMode) {
				modalContainer.classList.add(
					'permissions-by-role-modal-height-auto'
				);
			}
		}
	}, [singleRoleMode]);

	const handleRoleChange = useCallback(
		(event: React.ChangeEvent<HTMLSelectElement>) => {
			setSelectedRole(event.target.value);
		},
		[]
	);

	const saveHandler = useCallback(
		(event: any) => {
			event.preventDefault();

			setLoading(true);

			triggerAssetBulkAction({
				apiURL,
				keyValues: {
					configuration: JSON.stringify(currentValues),
					...(singleRoleMode && selectedRole
						? {roleKey: selectedRole}
						: {}),
				},
				onCreateError: () => {
					setLoading(false);
				},
				onCreateSuccess: (_response) => {
					if (!singleRoleMode) {
						closeModal();
					}

					setLoading(false);
				},
				selectedData,
				type: 'PermissionObjectBulkSelectionAction',
			});
		},
		[
			apiURL,
			closeModal,
			currentValues,
			selectedData,
			selectedRole,
			singleRoleMode,
		]
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
							const space = await SpaceService.getSpace(
								getScopeExternalReferenceCode(firstItem)
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
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{singleRoleMode ? (
					<>
						{Liferay.Language.get('edit-permissions-by-role')}
						<HelpTooltipIcon
							className="ml-2 text-4"
							message={Liferay.Language.get(
								'these-permissions-will-apply-only-to-the-selected-entities-themselves-and-will-not-affect-their-default-permissions'
							)}
						/>
					</>
				) : (
					sub(
						Liferay.Language.get('edit-x'),
						Liferay.Language.get('permissions')
					)
				)}

				<span
					className="pl-2 text-4 text-secondary text-weight-normal"
					ref={modalRef}
				>
					{`(${sub(Liferay.Language.get('x-x-selected'), [
						selectedData.items.length,
						className === DEPOT_CLASS_NAME
							? Liferay.Language.get('spaces')
							: Liferay.Language.get('items'),
					])})`}
				</span>
			</ClayModal.Header>

			<ClayModal.Body className="p-0">
				{singleRoleMode && (
					<div className="border-bottom p-4">
						<div className="alert alert-info mb-3" role="alert">
							<span className="alert-indicator">
								<ClayIcon symbol="info-circle" />
							</span>

							<strong className="lead">
								{Liferay.Language.get('info')}:
							</strong>{' '}

							{Liferay.Language.get(
								'please-note-that-the-configuration-shown-at-the-top-corresponds-to-the-default-settings-of-the-parent-level'
							)}
						</div>

						<ClayForm.Group>
							<label htmlFor="roleSelect">
								{Liferay.Language.get('select-role')}{' '}

								<span className="text-danger">*</span>
							</label>

							<ClaySelectWithOption
								aria-label="Select Role"
								disabled={loading}
								id="roleSelect"
								onChange={handleRoleChange}
								options={[
									{
										disabled: true,
										label: Liferay.Language.get(
											'choose-an-option'
										),
										value: '',
									},
									...roles.map((role) => ({
										label: role.name,
										value: role.key,
									})),
								]}
								required={true}
								value={selectedRole}
							/>
						</ClayForm.Group>
					</div>
				)}

				<DefaultPermissionFormContainer
					actions={actions}
					disabled={loading}
					infoBoxMessage={
						singleRoleMode
							? undefined
							: Liferay.Language.get(
									'please-be-aware-that-the-configuration-shown-at-the-beginning-is-the-default-of-the-parent-level'
								)
					}
					onChange={onChangeHandler}
					roles={roles}
					section={section}
					selectedRole={singleRoleMode ? selectedRole : undefined}
					singleRoleMode={singleRoleMode}
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
