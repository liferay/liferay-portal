/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '../../../css/components/DefaultPermission.scss';

import ClayButton from '@clayui/button';
import {ClayCheckbox} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayModal from '@clayui/modal';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useCallback, useEffect, useState} from 'react';

import CMSDefaultPermissionService from '../../common/services/CMSDefaultPermissionService';
import {triggerAssetBulkAction} from '../props_transformer/actions/triggerAssetBulkAction';
import DefaultPermissionFormContainer from './DefaultPermissionFormContainer';
import {
	AssetRoleSelectedActions,
	CMSDefaultPermissionObjectEntryDTO,
	DefaultPermissionModalContentProps,
} from './DefaultPermissionTypes';

export default function DefaultPermissionModalContent({
	actions,
	apiURL,
	classExternalReferenceCode,
	className,
	closeModal,
	roles,
}: DefaultPermissionModalContentProps & {apiURL?: string}) {
	const [currentObjectEntry, setCurrentObjectEntry] =
		useState<CMSDefaultPermissionObjectEntryDTO | null>(null);
	const [currentValues, setCurrentValues] =
		useState<AssetRoleSelectedActions>({});
	const [loading, setLoading] = useState(false);
	const [propagate, setPropagate] = useState(false);

	const saveHandler = useCallback(
		async (event: any) => {
			event.preventDefault();

			setLoading(true);

			try {
				if (!currentObjectEntry) {
					throw new Error();
				}

				const response =
					await CMSDefaultPermissionService.updateObjectEntry({
						defaultPermissions: JSON.stringify(currentValues),
						externalReferenceCode:
							currentObjectEntry.externalReferenceCode,
					});

				if (response.error) {
					throw new Error();
				}

				if (propagate) {
					triggerAssetBulkAction({
						apiURL,
						keyValues: {
							defaultPermissions: JSON.stringify(currentValues),
							depotGroupId: currentObjectEntry?.depotGroupId || 0,
							treePath: currentObjectEntry?.treePath || '',
						},
						onCreateError: ({error}) => {
							setLoading(false);

							throw new Error(error as unknown as any);
						},
						onCreateSuccess: () => {
							closeModal();

							setLoading(false);
						},
						overrideDefaultErrorToast: true,
						selectedData: {
							selectAll: true,
						},
						type: 'DefaultPermissionBulkAction',
					});
				}
				else {
					openToast({
						message: Liferay.Language.get(
							'your-request-completed-successfully'
						),
						type: 'success',
					});

					closeModal();

					setLoading(false);
				}
			}
			catch (_error) {
				openToast({
					message: Liferay.Language.get(
						'an-unexpected-system-error-occurred'
					),
					type: 'danger',
				});

				setLoading(false);
			}
		},
		[apiURL, closeModal, currentObjectEntry, currentValues, propagate]
	);

	const onChangeHandler = useCallback((data: any) => {
		setCurrentValues(data);
	}, []);

	useEffect(() => {
		setLoading(true);

		CMSDefaultPermissionService.getObjectEntry({
			classExternalReferenceCode,
			className,
		})
			.then((value) => {
				setCurrentObjectEntry(value);
				setCurrentValues(JSON.parse(value.defaultPermissions));
			})
			.catch((error) => {
				console.error(error);
			})
			.finally(() => {
				setLoading(false);
			});
	}, [classExternalReferenceCode, className]);

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
				first={
					<div className="d-flex">
						<ClayCheckbox
							checked={propagate}
							data-testid="checkbox-propagate"
							disabled={loading}
							inline
							label={Liferay.Language.get(
								'propagate-default-permissions-to-new-and-existing-subfolders'
							)}
							onChange={() => {
								setPropagate(!propagate);
							}}
						/>

						<ClayTooltipProvider>
							<span
								className="pl-2"
								data-tooltip-align="top"
								title={Liferay.Language.get(
									'enabling-this-setting-will-apply-the-permissions-configuration-to-all-current-subfolders'
								)}
							>
								<ClayIcon
									aria-label="Info"
									symbol="info-circle"
								/>
							</span>
						</ClayTooltipProvider>
					</div>
				}
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
