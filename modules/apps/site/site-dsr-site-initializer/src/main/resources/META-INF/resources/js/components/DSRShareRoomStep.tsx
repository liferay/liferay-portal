/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Button from '@clayui/button';
import DropDown from '@clayui/drop-down';
import ClayForm from '@clayui/form';
import MultiSelect from '@clayui/multi-select';
import classNames from 'classnames';
import {sub} from 'frontend-js-web';
import React, {useCallback, useContext, useEffect, useState} from 'react';

import {DSRContext} from './DSRInitializer';
import {TDSRContext, TDSRRoomDetailsStepProps} from './DSRTypes';
import FieldErrorMessage from './FieldErrorMessage';

export const DSR_SITE_ROLES = [
	{
		description: Liferay.Language.get(
			'users-can-view-content-leave-comments-and-upload-documents'
		),
		key: 'Site Administrator',
		label: Liferay.Language.get('contributor'),
	},
	{
		description: Liferay.Language.get(
			'users-can-view-documents-and-leave-comments-but-cannot-upload-files'
		),
		key: 'Site Member',
		label: Liferay.Language.get('viewer'),
	},
];

export function isEmailAddressValid(email: string) {
	const emailRegex = /.+@.+\..+/i;

	return emailRegex.test(email);
}

function DSRShareRoomStep({
	numberOfSteps,
	setHandleStepSubmit,
	step = 3,
}: TDSRRoomDetailsStepProps) {
	const {dataContext, loading, setDataContext} =
		useContext<TDSRContext>(DSRContext);

	const [emailAddresses, setEmailAddresses] = useState<
		Array<{label: string; value: string}>
	>(
		(dataContext.share?.emailAddresses || []).map((email) => ({
			label: email,
			value: email,
		}))
	);
	const [roleKey, setRoleKey] = useState<string>(
		dataContext.share?.roleKey || 'Site Member'
	);

	const handleEmailsFieldChange = useCallback(
		({emails}: {emails: Array<{label: string; value: string}>}) => {
			emails = emails.filter((email) => isEmailAddressValid(email.value));

			setDataContext((prevState) => ({
				...prevState,
				share: {
					emailAddresses: emails.map((email) => email.value.trim()),
					roleKey: prevState.share?.roleKey || '',
				},
			}));
			setEmailAddresses(emails);
		},
		[setDataContext]
	);

	const handleRoleKeyChange = useCallback(
		(key: string) => {
			setDataContext((prevState) => ({
				...prevState,
				share: {
					emailAddresses: prevState.share?.emailAddresses || [],
					roleKey: key,
				},
			}));
			setRoleKey(key);
		},
		[setDataContext]
	);

	useEffect(() => {
		setHandleStepSubmit(() => async (event: Event): Promise<boolean> => {
			event.preventDefault();

			return Promise.resolve(true);
		});
	}, [setHandleStepSubmit]);

	return (
		<>
			<div>
				<div className="mb-1 text-secondary" data-qa-id="stepLocator">
					{sub(
						Liferay.Language.get('step-x-of-x'),
						step,
						numberOfSteps
					)}
				</div>

				<div
					className="mb-1 text-6 text-weight-bold"
					data-qa-id="stepTitle"
				>
					{Liferay.Language.get('share-the-room')}
				</div>

				<div className="text-secondary">
					{Liferay.Language.get('share-the-room-with-your-teammates')}
				</div>
			</div>
			<div className="mt-4 row">
				<ClayForm.Group
					className={classNames('col-12', {
						'has-error': !!dataContext.errors.share,
					})}
				>
					<label
						className="d-block"
						htmlFor="dsr-users-email-addresses"
					>
						{Liferay.Language.get('emails')}
					</label>

					<div className="dsr-site-role-input position-relative">
						<MultiSelect
							allowDuplicateValues={false}
							autoFocus={true}
							data-qa-id="emailAddressesInput"
							disabled={loading}
							inputName="dsr-users-email-addresses"
							items={emailAddresses}
							onItemsChange={(emails: Array<any>) => {
								handleEmailsFieldChange({emails});
							}}
							placeholder={Liferay.Language.get(
								'type-a-comma-or-press-enter-to-input-email-addresses'
							)}
						/>

						<DropDown
							closeOnClick={true}
							trigger={
								<Button
									className="dsr-site-role-trigger-button"
									data-qa-id="roleKeyButton"
									disabled={loading}
									displayType="secondary"
									size="xs"
								>
									{DSR_SITE_ROLES.find(
										(item) => item.key === roleKey
									)?.label || Liferay.Language.get('viewer')}
								</Button>
							}
							triggerIcon="caret-bottom"
						>
							<DropDown.ItemList items={DSR_SITE_ROLES}>
								{(item: any) => (
									<DropDown.Item
										data-qa-id={`roleKeyItem_${item.label}`}
										key={item.key}
										onClick={() => {
											handleRoleKeyChange(item.key);
										}}
									>
										{item.label}
									</DropDown.Item>
								)}
							</DropDown.ItemList>
						</DropDown>
					</div>

					<FieldErrorMessage
						error={dataContext.errors.share}
						name="usersEmailAddresses"
					/>
				</ClayForm.Group>
			</div>
		</>
	);
}

export default DSRShareRoomStep;
