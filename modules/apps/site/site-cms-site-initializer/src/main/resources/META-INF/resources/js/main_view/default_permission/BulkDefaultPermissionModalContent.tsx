/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '../../../css/components/DefaultPermission.scss';

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayModal from '@clayui/modal';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {openModal, openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useCallback, useEffect, useState} from 'react';

import CMSDefaultPermissionService from '../../common/services/CMSDefaultPermissionService';
import DefaultPermissionFormContainer from './DefaultPermissionFormContainer';
import {
	AssetRoleSelectedActions,
	BulkDefaultPermissionModalContentProps,
} from './DefaultPermissionTypes';

const DEFAULT_PERMISSIONS = {
	L_CONTENTS: {
		'CMS Administrator': [
			'UPDATE_DISCUSSION',
			'DELETE',
			'PERMISSIONS',
			'OBJECT_ENTRY_HISTORY',
			'DELETE_DISCUSSION',
			'UPDATE',
			'VIEW',
			'ADD_DISCUSSION',
		],
	},
	L_FILES: {
		'CMS Administrator': [
			'UPDATE_DISCUSSION',
			'DELETE',
			'PERMISSIONS',
			'OBJECT_ENTRY_HISTORY',
			'DELETE_DISCUSSION',
			'UPDATE',
			'VIEW',
			'ADD_DISCUSSION',
		],
	},
	OBJECT_ENTRY_FOLDERS: {
		'CMS Administrator': [
			'DELETE',
			'PERMISSIONS',
			'UPDATE',
			'SUBSCRIBE',
			'VIEW',
			'ADD_ENTRY',
		],
	},
};
const DEPOT_CLASS_NAME = 'com.liferay.depot.model.DepotEntry';

export function defaultPermissionsBulkAction({
	className,
	defaultPermissionAdditionalProps,
	selectedData,
}: {
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
				className,
				closeModal,
				selectedData,
			}),
		size: 'full-screen',
	});
}

export default function BulkDefaultPermissionModalContent({
	actions,
	className,
	closeModal,
	roles,
	selectedData,
}: BulkDefaultPermissionModalContentProps) {
	const [currentValues, setCurrentValues] =
		useState<AssetRoleSelectedActions>({});
	const [loading, setLoading] = useState(false);

	const saveHandler = useCallback(() => {
		setLoading(true);

		return CMSDefaultPermissionService.batchUpdateObjectEntry({
			bulkActionItems: selectedData.items.map((item: any) => {
				return {
					classExternalReferenceCode:
						item.externalReferenceCode ||
						item.embedded?.externalReferenceCode,
					className: item.entryClassName || className,
				};
			}),
			defaultPermissions: JSON.stringify(currentValues),
			selectAll: false,
		})
			.then(() => {
				openToast({
					message: Liferay.Language.get(
						'your-request-completed-successfully'
					),
					type: 'success',
				});

				closeModal();
			})
			.catch(() => {
				openToast({
					message: Liferay.Language.get(
						'an-unexpected-system-error-occurred'
					),
					type: 'danger',
				});
			})
			.finally(() => {
				setLoading(false);
			});
	}, [className, closeModal, currentValues, selectedData.items]);

	const onChangeHandler = useCallback((data: any) => {
		setCurrentValues(data);
	}, []);

	useEffect(() => {
		setLoading(true);

		try {
			if (className === DEPOT_CLASS_NAME) {
				setCurrentValues(DEFAULT_PERMISSIONS);
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
						setCurrentValues(DEFAULT_PERMISSIONS);

						return;
					}
					else {
						entryClassExternalReferenceCode =
							firstItem.embedded.scopeExternalReferenceCode;
						entryClassName = DEPOT_CLASS_NAME;
					}
				}
				else {
					entryClassExternalReferenceCode =
						firstItem.embedded
							.parentObjectEntryFolderExternalReferenceCode;
					entryClassName = firstItem.entryClassName;
				}

				CMSDefaultPermissionService.getObjectEntry({
					classExternalReferenceCode: entryClassExternalReferenceCode,
					className: entryClassName,
				})
					.then((value) => {
						setCurrentValues(JSON.parse(value.defaultPermissions));
					})
					.catch((error) => {
						console.error(error);
					})
					.finally(() => {
						setLoading(false);
					});
			}
		}
		catch (error) {
			console.error(error);
		}
		finally {
			setLoading(false);
		}
	}, [className, selectedData.items]);

	return (
		<>
			<ClayModal.Header>
				{sub(
					Liferay.Language.get('edit-x'),
					Liferay.Language.get('default-permissions')
				)}

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
