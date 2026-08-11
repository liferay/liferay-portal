/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '../../../css/components/DefaultPermission.scss';

import ClayButton from '@clayui/button';
import ClayForm, {ClaySelectWithOption} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayModal from '@clayui/modal';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {sub} from 'frontend-js-web';
import React, {useCallback, useEffect, useRef, useState} from 'react';

import HelpTooltipIcon from '../../common/components/forms/HelpTooltipIcon';
import CMSDefaultPermissionService from '../../common/services/CMSDefaultPermissionService';
import SpaceService from '../../common/services/SpaceService';
import {getScopeExternalReferenceCode} from '../../common/utils/getScopeExternalReferenceCode';
import {openActionNotAllowedModal} from '../../common/utils/openActionNotAllowedModal';
import {openCMSModal} from '../../common/utils/openCMSModal';
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

export function defaultPermissionsBulkAction({
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
	if (
		selectedData?.selectAll ||
		!selectedData?.items?.length ||
		(className !== DEPOT_CLASS_NAME &&
			selectedData.items.find(
				(item: any) => item.entryClassName !== className
			))
	) {
		return openActionNotAllowedModal();
	}

	return openCMSModal({
		contentComponent: ({closeModal}: {closeModal: () => void}) =>
			BulkDefaultPermissionModalContent({
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

export default function BulkDefaultPermissionModalContent({
	actions,
	apiURL,
	className,
	closeModal,
	roles,
	section,
	selectedData,
	singleRoleMode,
}: BulkDefaultPermissionModalContentProps & {
	apiURL?: string;
	section?: string;
	singleRoleMode?: boolean;
}) {
	const [currentValues, setCurrentValues] =
		useState<AssetRoleSelectedActions>({});
	const [loading, setLoading] = useState(false);
	const [selectedRole, setSelectedRole] = useState<string>('');
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

	const saveHandler = useCallback(() => {
		setLoading(true);

		triggerAssetBulkAction({
			apiURL,
			keyValues: {
				defaultPermissions: JSON.stringify(currentValues),
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
			type: 'DefaultPermissionObjectBulkSelectionAction',
		});
	}, [
		apiURL,
		className,
		closeModal,
		currentValues,
		selectedData,
		selectedRole,
		singleRoleMode,
	]);

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
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{singleRoleMode ? (
					<>
						{Liferay.Language.get(
							'edit-default-permissions-by-role'
						)}
						<HelpTooltipIcon
							className="ml-2 text-4"
							message={Liferay.Language.get(
								'these-default-permissions-will-apply-to-all-newly-created-items-within-the-selected-folders-or-spaces'
							)}
						/>
					</>
				) : (
					sub(
						Liferay.Language.get('edit-x'),
						Liferay.Language.get('default-permissions')
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
							: Liferay.Language.get('folders'),
					])})`}
				</span>

				{!singleRoleMode && (
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
				)}
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
