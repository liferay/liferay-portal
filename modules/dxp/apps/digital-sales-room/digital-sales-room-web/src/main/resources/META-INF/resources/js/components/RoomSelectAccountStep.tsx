/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Autocomplete from '@clayui/autocomplete';
import ClayForm from '@clayui/form';
import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import {debounce, sub} from 'frontend-js-web';
import React, {
	useCallback,
	useContext,
	useEffect,
	useMemo,
	useState,
} from 'react';

import AccountSticker from '../common/components/AccountSticker';
import RoomService from '../common/services/RoomService';
import {displayErrorToast} from '../common/utils/toastUtil';
import {IAccount, IRoomContext, IRoomStepProps} from '../common/utils/types';
import FieldErrorMessage from './FieldErrorMessage';
import {RoomContext} from './RoomInitializer';

function getAccountId(
	accountName: string | undefined,
	accounts: Array<IAccount>
) {
	return String(
		accounts.find((item) => item.name === (accountName || ''))?.id || 0
	);
}

function RoomSelectAccountStep({
	numberOfSteps,
	setHandleStepSubmit,
	showHeader = true,
	step = 1,
}: IRoomStepProps) {
	const {dataContext, loading, setDataContext} =
		useContext<IRoomContext>(RoomContext);
	const [accountName, setAccountName] = useState(dataContext.accountName);
	const [accounts, setAccounts] = useState<Array<IAccount>>([]);
	const [currentAccountName, setCurrentAccountName] = useState(
		dataContext.accountName
	);

	const debouncedSetAccountName = useMemo(
		() =>
			debounce((currentValue) => {
				setAccountName(currentValue);
			}, 250),
		[]
	);

	const handleFieldChange = useCallback(
		({
			target: {id, value},
		}: {
			target: {
				id: string;
				name: string;
				value: string;
			};
		}) => {
			const accountId =
				parseInt(id || getAccountId(value, accounts), 10) || 0;

			setDataContext((prevState) => ({
				...prevState,
				accountId,
				accountName: value || '',
				errors: {
					...prevState.errors,
					accountId: accountId
						? ''
						: Liferay.Language.get('this-field-is-mandatory'),
				},
			}));
		},
		[accounts, setDataContext]
	);

	const handleAccountIdFieldChange = useCallback(
		(value: string) => {
			setCurrentAccountName(value);
			debouncedSetAccountName(value);

			const accountId = getAccountId(value, accounts);

			if (accounts.length && parseInt(accountId, 10) >= 0) {
				handleFieldChange({
					target: {id: accountId, name: 'accountId', value},
				});
			}
		},
		[accounts, debouncedSetAccountName, handleFieldChange]
	);

	useEffect(() => {
		setCurrentAccountName(dataContext.accountName || '');
	}, [dataContext.accountName]);

	useEffect(() => {
		RoomService.getAccounts(accountName)
			.then((data) => {
				setAccounts(
					data.items.map((item) => {
						const hasLogo =
							item.logoURL && !item.logoURL.includes('img_id=0');

						return {
							...item,
							logoURL: hasLogo ? item.logoURL : undefined,
						};
					})
				);
			})
			.catch((error) => {
				displayErrorToast((error as Error).message);
			});
	}, [accountName]);

	useEffect(() => {
		setHandleStepSubmit(() => async (event: Event): Promise<boolean> => {
			event.preventDefault();

			if (!dataContext.accountId) {
				setDataContext((prevState) => ({
					...prevState,
					errors: {
						...prevState.errors,
						accountId: Liferay.Language.get(
							'this-field-is-mandatory'
						),
					},
				}));

				return Promise.resolve(false);
			}

			return Promise.resolve(true);
		});
	}, [dataContext, setDataContext, setHandleStepSubmit]);

	return (
		<>
			{showHeader && (
				<div>
					<div
						className="mb-1 text-secondary"
						data-qa-id="stepLocator"
					>
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
						{Liferay.Language.get('account-association')}
					</div>

					<div className="text-secondary">
						{Liferay.Language.get(
							'choose-the-account-you-want-to-associate-with-this-digital-sales-room'
						)}
					</div>
				</div>
			)}

			<div className="mt-4 row">
				<ClayForm.Group
					className={classNames('col-12', {
						'has-error': !!dataContext.errors.accountId,
					})}
				>
					<label className="d-block" htmlFor="dsr-account-id">
						{Liferay.Language.get('select-account')}

						<span className="c-ml-2 reference-mark">
							<ClayIcon symbol="asterisk" />

							<span className="hide-accessible sr-only">
								{Liferay.Language.get('required')}
							</span>
						</span>
					</label>

					<Autocomplete
						aria-label={Liferay.Language.get('select-account')}
						className="mb-3"
						data-qa-id="selectAccountInput"
						defaultValue={String(dataContext.accountId || '')}
						disabled={loading}
						filterKey="name"
						id="accountId"
						items={accounts}
						menuTrigger="focus"
						name="dsr-account-id"
						onChange={(value: string) => {
							if (!value) {
								handleFieldChange({
									target: {
										id: '0',
										name: 'accountId',
										value: '',
									},
								});
							}

							handleAccountIdFieldChange(value);
						}}
						placeholder=""
						value={currentAccountName}
					>
						{(item: IAccount) => (
							<Autocomplete.Item
								key={item.id}
								onClick={() => {
									handleFieldChange({
										target: {
											id: String(item.id),
											name: 'accountId',
											value: item.name,
										},
									});
								}}
								textValue={item.name}
							>
								<div className="align-items-center d-flex">
									<AccountSticker
										className="c-mr-2 flex-shrink-0"
										initialColor="#FFF"
										logoURL={item.logoURL}
										name={item.name}
										shape="circle"
										size="sm"
									/>

									<span>{item.name}</span>
								</div>
							</Autocomplete.Item>
						)}
					</Autocomplete>

					<FieldErrorMessage
						error={dataContext.errors.accountId}
						name="accountId"
					/>
				</ClayForm.Group>
			</div>
		</>
	);
}

export default RoomSelectAccountStep;
