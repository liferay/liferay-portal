/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Autocomplete from '@clayui/autocomplete';
import ClayForm from '@clayui/form';
import classNames from 'classnames';
import {openToast} from 'frontend-js-components-web';
import {debounce, sub} from 'frontend-js-web';
import React, {
	useCallback,
	useContext,
	useEffect,
	useMemo,
	useState,
} from 'react';

import ApiHelper from '../commons/ApiHelper';
import {DSRContext} from './DSRInitializer';
import {
	TDSRContext,
	TDSRDataContext,
	TDSRRoomDetailsStepProps,
} from './DSRTypes';
import FieldErrorMessage from './FieldErrorMessage';

type TAccount = {
	key: string;
	name: string;
};

type TChannel = {
	key: string;
	name: string;
};

function getAccountId(
	accountName: string | undefined,
	accounts: Array<TAccount>
) {
	return (
		accounts.find((item) => item.name === (accountName || ''))?.key || '0'
	);
}

function getChannelId(
	channelName: string | undefined,
	channels: Array<TChannel>
) {
	return (
		channels.find((item) => item.name === (channelName || ''))?.key || '0'
	);
}

async function getAccounts(accountName = '') {
	const {data, error} = await ApiHelper.get<{
		items: Array<{id: number; name: string}>;
	}>(
		`/o/headless-admin-user/v1.0/accounts?filter=contains(name, '${accountName}')`
	);

	if (error) {
		throw new Error(error);
	}

	return data || {items: []};
}

async function getChannels(channelName = '') {
	const {data, error} = await ApiHelper.get<{
		items: Array<{id: number; name: string}>;
	}>(
		`/o/headless-commerce-admin-channel/v1.0/channels?filter=contains(name, '${channelName}')`
	);

	if (error) {
		throw new Error(error);
	}

	return data || {items: []};
}

function DSRRoomSettingsStep({
	numberOfSteps,
	setHandleStepSubmit,
	showHeader = true,
	step = 2,
}: TDSRRoomDetailsStepProps) {
	const {dataContext, loading, setDataContext} =
		useContext<TDSRContext>(DSRContext);

	const [accountName, setAccountName] = useState(dataContext.accountName);
	const [accounts, setAccounts] = useState<Array<TAccount>>([]);
	const [channelName, setChannelName] = useState(dataContext.channelName);
	const [channels, setChannels] = useState<Array<TChannel>>([]);
	const [currentAccountName, setCurrentAccountName] = useState(
		dataContext.accountName
	);
	const [currentChannelName, setCurrentChannelName] = useState(
		dataContext.channelName
	);

	const debouncedSetAccountName = useMemo(
		() =>
			debounce((currentValue) => {
				setAccountName(currentValue);
			}, 250),
		[]
	);

	const debouncedSetChannelName = useMemo(
		() =>
			debounce((currentValue) => {
				setChannelName(currentValue);
			}, 250),
		[]
	);

	const handleFieldChange = useCallback(
		({
			target: {id, name, value},
		}: {
			target: {
				id: string;
				name: string;
				value: string;
			};
		}) => {
			const data: Partial<TDSRDataContext> = {};

			if (name === 'accountId') {
				data['accountId'] =
					parseInt(id || getAccountId(value, accounts), 10) || 0;
				data['accountName'] = value || '';
			}
			else if (name === 'channelId') {
				data['channelId'] =
					parseInt(id || getChannelId(value, channels), 10) || 0;
				data['channelName'] = value || '';
			}

			setDataContext((prevState) => ({
				...prevState,
				...data,
			}));
		},
		[accounts, channels, setDataContext]
	);

	const handleAccountIdFieldChange = useCallback(
		(value: string) => {
			setCurrentAccountName(value);
			debouncedSetAccountName(value);

			const accountId = getAccountId(value, accounts);

			if (parseInt(accountId, 10)) {
				handleFieldChange({
					target: {id: accountId, name: 'accountId', value},
				});
			}
		},
		[accounts, debouncedSetAccountName, handleFieldChange]
	);

	const handleChannelIdFieldChange = useCallback(
		(value: string) => {
			setCurrentChannelName(value);
			debouncedSetChannelName(value);

			const channelId = getChannelId(value, channels);

			if (parseInt(channelId, 10)) {
				handleFieldChange({
					target: {id: channelId, name: 'channelId', value},
				});
			}
		},
		[channels, debouncedSetChannelName, handleFieldChange]
	);

	useEffect(() => {
		setCurrentAccountName(dataContext.accountName || '');
	}, [dataContext.accountName]);

	useEffect(() => {
		setCurrentChannelName(dataContext.channelName || '');
	}, [dataContext.channelName]);

	useEffect(() => {
		getAccounts(accountName)
			.then((data) => {
				setAccounts(
					data.items.map((item) => {
						return {
							key: String(item.id),
							name: item.name,
						};
					})
				);
			})
			.catch((error) => {
				openToast({
					message: (error as Error).message,
					type: 'danger',
				});
			});
	}, [accountName]);

	useEffect(() => {
		getChannels(channelName)
			.then((data) => {
				setChannels(
					data.items.map((item) => {
						return {
							key: String(item.id),
							name: item.name,
						};
					})
				);
			})
			.catch((error) => {
				openToast({
					message: (error as Error).message,
					type: 'danger',
				});
			});
	}, [channelName]);

	useEffect(() => {
		setHandleStepSubmit(() => async (event: Event): Promise<boolean> => {
			event.preventDefault();

			return Promise.resolve(true);
		});
	}, [setHandleStepSubmit]);

	return (
		<>
			{showHeader ? (
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
						{Liferay.Language.get('room-settings')}
					</div>

					<div className="text-secondary">
						{Liferay.Language.get(
							'select-the-channel-associated-with-the-digital-sales-room'
						)}
					</div>
				</div>
			) : (
				<></>
			)}
			<div className="mt-4 row">
				<ClayForm.Group
					className={classNames('col-12', {
						'has-error': !!dataContext.errors.channelId,
					})}
				>
					<label className="d-block" htmlFor="dsr-channel-id">
						{Liferay.Language.get('select-channel')}
					</label>

					<Autocomplete
						aria-label={Liferay.Language.get('select-channel')}
						className="mb-3"
						data-qa-id="selectChannelInput"
						defaultValue={String(dataContext.channelId || '')}
						disabled={loading}
						filterKey="name"
						id="channelId"
						items={channels}
						menuTrigger="focus"
						name="dsr-channel-id"
						onChange={(value: string) => {
							if (!value) {
								handleFieldChange({
									target: {
										id: '0',
										name: 'channelId',
										value: '',
									},
								});
							}

							handleChannelIdFieldChange(value);
						}}
						onItemsChange={() => {}}
						placeholder=""
						value={currentChannelName}
					>
						{(item: TChannel) => (
							<Autocomplete.Item
								key={item.key}
								onClick={() => {
									handleFieldChange({
										target: {
											id: item.key,
											name: 'channelId',
											value: item.name,
										},
									});
								}}
								textValue={item.name}
							>
								<div>{item.name}</div>
							</Autocomplete.Item>
						)}
					</Autocomplete>

					<FieldErrorMessage
						error={dataContext.errors.channelId}
						name="channelId"
					/>
				</ClayForm.Group>
			</div>
			<div className="row">
				<ClayForm.Group
					className={classNames('col-12', {
						'has-error': !!dataContext.errors.accountId,
					})}
				>
					<label className="d-block" htmlFor="dsr-account-id">
						{Liferay.Language.get('select-account')}
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
						onItemsChange={() => {}}
						placeholder=""
						value={currentAccountName}
					>
						{(item: TAccount) => (
							<Autocomplete.Item
								key={item.key}
								onClick={() => {
									handleFieldChange({
										target: {
											id: item.key,
											name: 'accountId',
											value: item.name,
										},
									});
								}}
								textValue={item.name}
							>
								<div>{item.name}</div>
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

export default DSRRoomSettingsStep;
