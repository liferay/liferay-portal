/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayManagementToolbar from '@clayui/management-toolbar';
import classNames from 'classnames';
import {navigate, sub} from 'frontend-js-web';
import React, {useState} from 'react';

import './index.scss';
import {NotificationTemplate} from '../../utils/api';
import {ModalEditObjectDefinitionExternalReferenceCode} from './ModalEditObjectDefinitionExternalReferenceCode';

export type Entity = NotificationTemplate | ObjectDefinition;

interface ManagementToolbarProps {
	backURL: string;
	badgeClassName?: string;
	badgeLabel?: string;
	className?: string;
	enableBoxShadow?: boolean;
	entityId: number;
	hasPublishPermission: boolean;
	hasUpdatePermission: boolean;
	helpMessage: string;
	inheritanceClassName?: string;
	inheritanceLabel?: string;
	isApproved?: boolean;
	isRootDescendantNode?: boolean;
	label: string;
	loading?: boolean;
	objectDefinitionExternalReferenceCode: string;
	objectDefinitionExternalReferenceCodeSaveURL: string;
	onExternalReferenceCodeChange?: (value: string) => void;
	onGetEntity: () => Promise<Entity>;
	onSubmit: (props: boolean) => void;
	portletNamespace: string;
	screenNavigationCategoryKey?: string;
	showEntityDetails?: boolean;
}

export function ManagementToolbar({
	backURL,
	badgeClassName,
	badgeLabel,
	className,
	enableBoxShadow = true,
	entityId,
	hasPublishPermission,
	hasUpdatePermission,
	helpMessage,
	inheritanceClassName,
	inheritanceLabel,
	isApproved,
	isRootDescendantNode,
	label,
	loading,
	objectDefinitionExternalReferenceCode:
		initialObjectDefinitionExternalReferenceCode,
	objectDefinitionExternalReferenceCodeSaveURL,
	onExternalReferenceCodeChange,
	onGetEntity,
	onSubmit,
	portletNamespace,
	screenNavigationCategoryKey,
	showEntityDetails = true,
}: ManagementToolbarProps) {
	const [
		objectDefinitionExternalReferenceCode,
		setObjectDefinitionExternalReferenceCode,
	] = useState(initialObjectDefinitionExternalReferenceCode);
	const [visibleModal, setVisibleModal] = useState<boolean>(false);

	return (
		<>
			<ClayManagementToolbar
				className={classNames(
					`lfr__management-toolbar ${className}`,
					enableBoxShadow && 'lfr__management-toolbar--box-shadow'
				)}
				fluidSize="xxxl"
			>
				<ClayManagementToolbar.ItemList>
					<div className="border-right ml-sm-2 mr-3 pr-3">
						<h3 className="mb-0 text-truncate">{label}</h3>

						{Liferay.FeatureFlags['LPD-34594'] &&
							inheritanceLabel && (
								<strong
									className={`${inheritanceClassName} label`}
								>
									{inheritanceLabel}
								</strong>
							)}

						{badgeLabel && (
							<strong className={`${badgeClassName} label`}>
								{badgeLabel}
							</strong>
						)}
					</div>

					{showEntityDetails && (
						<div>
							<div>
								<span className="text-secondary">
									{`${Liferay.Language.get('id')}:`}
								</span>

								<strong className="ml-2">{entityId}</strong>
							</div>

							<div className="mt-1">
								<span className="text-secondary">
									{`${Liferay.Language.get('erc')}:`}
								</span>

								<strong className="ml-2">
									{objectDefinitionExternalReferenceCode}
								</strong>

								<span
									className="ml-3 text-secondary"
									title={helpMessage}
								>
									<ClayIcon symbol="question-circle" />
								</span>

								<ClayButton
									aria-label={sub(
										Liferay.Language.get('edit-x'),
										Liferay.Language.get(
											'external-reference-code'
										)
									)}
									className="ml-3 p-0 text-secondary"
									displayType="unstyled"
									onClick={() => setVisibleModal(true)}
								>
									<ClayIcon symbol="pencil" />
								</ClayButton>
							</div>
						</div>
					)}
				</ClayManagementToolbar.ItemList>

				{(!screenNavigationCategoryKey ||
					screenNavigationCategoryKey === 'details') && (
					<ClayManagementToolbar.ItemList>
						<ClayButton.Group key={1} spaced>
							<ClayButton
								displayType="secondary"
								id={`${portletNamespace}cancel`}
								name="cancel"
								onClick={() => navigate(backURL)}
							>
								{Liferay.Language.get('cancel')}
							</ClayButton>

							<ClayButton
								disabled={!hasUpdatePermission || loading}
								displayType={
									isApproved ||
									isApproved === undefined ||
									isRootDescendantNode
										? 'primary'
										: 'secondary'
								}
								id={`${portletNamespace}save`}
								name="save"
								onClick={() => onSubmit(true)}
							>
								{Liferay.Language.get('save')}
							</ClayButton>

							{isApproved !== undefined && !isApproved && (
								<ClayButton
									disabled={!hasPublishPermission || loading}
									id={`${portletNamespace}publish`}
									name="publish"
									onClick={() => onSubmit(false)}
								>
									{Liferay.Language.get('publish')}
								</ClayButton>
							)}
						</ClayButton.Group>
					</ClayManagementToolbar.ItemList>
				)}
			</ClayManagementToolbar>

			{visibleModal && (
				<ModalEditObjectDefinitionExternalReferenceCode
					handleOnClose={() => setVisibleModal(false)}
					helpMessage={helpMessage}
					objectDefinitionExternalReferenceCode={
						objectDefinitionExternalReferenceCode
					}
					onGetEntity={onGetEntity}
					onObjectDefinitionExternalReferenceCodeChange={
						onExternalReferenceCodeChange
					}
					saveURL={objectDefinitionExternalReferenceCodeSaveURL}
					setObjectDefinitionExternalReferenceCode={
						setObjectDefinitionExternalReferenceCode
					}
				/>
			)}
		</>
	);
}
