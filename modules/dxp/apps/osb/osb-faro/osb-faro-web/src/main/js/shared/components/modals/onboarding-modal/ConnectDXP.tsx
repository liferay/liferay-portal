import * as API from 'shared/api';
import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import ClayLink from '@clayui/link';
import ClayList from '@clayui/list';
import CopyButton from 'shared/components/CopyButton';
import DataSourceQuery, {
	DataSource,
	DataSourceData,
	DataSourceSyncData
} from 'shared/queries/DataSourceQuery';
import getCN from 'classnames';
import InfoPopover from 'shared/components/InfoPopover';
import Input from 'shared/components/Input';
import Label from 'shared/components/form/Label';
import Modal from 'shared/components/modal';
import React, {FC, useEffect, useRef, useState} from 'react';
import Select from 'shared/components/Select';
import URLConstants from 'shared/util/url-constants';
import {ActionType, useChannelContext} from 'shared/context/channel';
import {compose} from 'redux';
import {connect} from 'react-redux';
import {CREATE_DATE} from 'shared/util/pagination';
import {
	CredentialTypes,
	DataSourceTypes,
	OrderByDirections
} from 'shared/util/constants';
import {fetchDataSource} from 'shared/actions/data-sources';
import {get, noop, upperFirst} from 'lodash';
import {getDefaultChannel} from 'shared/components/channels-menu';
import {Routes, toRoute} from 'shared/util/router';
import {sub} from 'shared/util/lang';
import {Text} from '@clayui/core';
import {useInterval} from 'shared/hooks/useInterval';
import {useLazyQuery} from '@apollo/react-hooks';
import {withHistory} from 'shared/hoc';

const TIMEOUT_INTERVAL = 5000;

const DXP_VERSIONS = {
	'dxp-2024-q-1-1': {
		label: 'DXP 2024.Q1.1 Quarterly Release',
		url: URLConstants.DownloadDXP2024Q11
	},
	'dxp-73-u30': {
		label: 'DXP Version 7.3 U30 + and above with hotfix',
		url: URLConstants.DownloadDXP73U30
	}
};

const DATA_SOURCE_STATUSES = {
	CONFIGURED: {
		display: 'success',
		label: Liferay.Language.get('configured')
	},
	UNCONFIGURED: {
		display: 'secondary',
		label: Liferay.Language.get('unconfigured')
	}
};

interface IConnectDXPProps {
	dxpConnected: boolean;
	groupId: string;
	onboarding?: boolean;
	onClose: () => void;
	onNext?: (increment?: number) => void;
}

interface IConnectDXPWrapperProps {
	dataSourceId?: string;
	fetchDataSource: ({
		groupId,
		id
	}: {
		groupId: string;
		id: string;
	}) => DataSource;
	history: {
		push: (path: string) => void;
	};
	isUpgrading: boolean;
	onDxpConnected: (dxpConnected: boolean) => void;
	onPrevious?: () => void;
}

interface ITokenInputProps {
	token: string;
}

const ConnectDXP: React.FC<IConnectDXPWrapperProps & IConnectDXPProps> = ({
	dataSourceId,
	dxpConnected,
	fetchDataSource,
	groupId,
	history,
	onboarding,
	onClose,
	onDxpConnected,
	onNext
}) => {
	const {channelDispatch} = useChannelContext();
	const [token, setToken] = useState<string>('');

	const [getDataSources, {data}] = useLazyQuery<DataSourceData>(
		DataSourceQuery,
		{
			fetchPolicy: 'network-only',
			onCompleted: () => {
				onDxpConnected(true);
			},
			variables: {
				credentialsType: CredentialTypes.Token,
				size: 1,
				sort: {
					column: CREATE_DATE,
					type: OrderByDirections.Descending
				},
				type: DataSourceTypes.Liferay
			}
		}
	);

	let _tokenRequest;

	const getNextToken: (prevToken?: string) => Promise<any> = prevToken =>
		API.dataSource
			.fetchToken(groupId, dataSourceId)
			.then(nextToken => {
				if (!prevToken || prevToken === nextToken) {
					_tokenRequest = setTimeout(
						() => getNextToken(nextToken),
						TIMEOUT_INTERVAL
					);
				} else {
					if (onboarding) {
						onDxpConnected(true);

						updateChannels();
						// if it's an upgrade from oauth to token, we need to fetch the DataSource
					} else if (dataSourceId) {
						fetchDataSource({groupId, id: dataSourceId});
					}

					getDataSources();
				}

				return nextToken;
			})
			.catch(err => {
				if (!err.IS_CANCELLATION_ERROR) {
					_tokenRequest = setTimeout(
						() => getNextToken(prevToken),
						TIMEOUT_INTERVAL
					);
				}

				return prevToken;
			});

	const updateChannels = () => {
		API.channels.fetchAll({groupId}).then(({items}) => {
			const channelId = get(items, [0, 'id']);

			history.push(toRoute(Routes.SITES, {channelId, groupId}));

			channelDispatch({
				payload: getDefaultChannel(channelId, items),
				type: ActionType.setSelectedChannel
			});

			channelDispatch({
				payload: items,
				type: ActionType.setChannels
			});
		});
	};

	useEffect(() => {
		_tokenRequest = getNextToken().then(setToken);

		return () => {
			clearTimeout(_tokenRequest);
		};
	}, []);

	const getNavHref = () => {
		const id = get(data, ['dataSources', 0, 'id'], null);

		if (id) {
			return toRoute(Routes.SETTINGS_DATA_SOURCE, {groupId, id});
		}

		return toRoute(Routes.SETTINGS_DATA_SOURCE_LIST, {groupId});
	};

	return (
		<>
			<Modal.Header onClose={onClose} />

			<Modal.Body>
				<div className='analytics-to-dxp-container'>
					<ClayIcon
						className='icon-root icon-size-xl'
						symbol='dxp_icon'
					/>

					<ClayIcon
						className={getCN('arrows icon-root icon-size-lg', {
							connected: dxpConnected
						})}
						symbol='ac_horizontal_arrows'
					/>

					<ClayIcon
						className='icon-root icon-size-xl'
						symbol={dxpConnected ? 'ac_logo' : 'ac_logo_grayscale'}
					/>
				</div>

				<div className='text-center mb-4'>
					<Text size={10} weight='bold'>
						{dxpConnected
							? Liferay.Language.get(
									'your-dxp-instance-is-connected-to-analytics-cloud'
							  )
							: Liferay.Language.get(
									'connect-your-dxp-analytics'
							  )}
					</Text>
				</div>

				{!dxpConnected && (
					<>
						<TokenInput token={token} />

						<FixPackSelect />
					</>
				)}

				{dxpConnected && <DxpSyncTable />}
			</Modal.Body>

			<Modal.Footer>
				<div>
					{!(dxpConnected && onboarding) && (
						<ClayButton
							className='button-root'
							disabled={dxpConnected}
							displayType='secondary'
							onClick={onboarding ? () => onNext() : onClose}
						>
							{onboarding
								? Liferay.Language.get('skip')
								: Liferay.Language.get('cancel')}
						</ClayButton>
					)}

					<ClayLink
						button
						className='button-root ml-2'
						displayType='primary'
						href={getNavHref()}
						onClick={() => (onboarding ? onNext() : onClose())}
					>
						{onboarding
							? Liferay.Language.get('next')
							: Liferay.Language.get('done')}
					</ClayLink>
				</div>
			</Modal.Footer>
		</>
	);
};

const DxpSyncTable: FC<React.HTMLAttributes<HTMLElement>> = () => {
	const [dataSource, setDataSources] = useState<DataSource>({
		contactsSyncDetails: {selected: false},
		sitesSyncDetails: {selected: false}
	});
	const [getDataSources, {data}] = useLazyQuery<DataSourceSyncData>(
		DataSourceQuery,
		{
			fetchPolicy: 'network-only',
			variables: {
				credentialsType: CredentialTypes.Token,
				size: 1,
				sort: {
					column: CREATE_DATE,
					type: OrderByDirections.Descending
				},
				type: DataSourceTypes.Liferay
			}
		}
	);
	useInterval<void>(getDataSources, TIMEOUT_INTERVAL);

	const getLabelProps = (selected: boolean) =>
		selected
			? DATA_SOURCE_STATUSES.CONFIGURED
			: DATA_SOURCE_STATUSES.UNCONFIGURED;

	const {display: contactsDisplay, label: contactslabel} = getLabelProps(
		dataSource?.contactsSyncDetails?.selected
	);

	const {display: sitesDisplay, label: sitesLabel} = getLabelProps(
		dataSource?.sitesSyncDetails?.selected
	);

	useEffect(() => {
		if (data) {
			setDataSources(data.dataSources[0]);
		}
	}, [data]);

	return (
		<div>
			<ClayList>
				<ClayList.Item flex>
					<ClayList.ItemField expand>
						<Text size={3} weight='bold'>
							{Liferay.Language.get('sites')}
						</Text>
					</ClayList.ItemField>

					<ClayList.ItemField>
						<ClayLabel
							className='text-uppercase'
							displayType={sitesDisplay as any}
							large
						>
							{sitesLabel}
						</ClayLabel>
					</ClayList.ItemField>
				</ClayList.Item>

				<ClayList.Item flex>
					<ClayList.ItemField expand>
						<Text size={3} weight='bold'>
							{Liferay.Language.get('contacts')}
						</Text>
					</ClayList.ItemField>

					<ClayList.ItemField>
						<ClayLabel
							className='text-uppercase'
							displayType={contactsDisplay as any}
							large
						>
							{contactslabel}
						</ClayLabel>
					</ClayList.ItemField>
				</ClayList.Item>
			</ClayList>

			<div className='mt-2'>
				<Text color='secondary' size={3}>
					{Liferay.Language.get(
						'you-can-check-your-data-source-syncing-status-under-settings-in-data-sources,-or-continue-configuring-your-data-source-on-your-dxp-instance'
					)}
				</Text>
			</div>
		</div>
	);
};

const FixPackSelect: FC<React.HTMLAttributes<HTMLElement>> = () => {
	const dxpVersionsList = Object.keys(DXP_VERSIONS);
	const [dxpVersion, setDxpVersion] = useState<string>(dxpVersionsList[0]);

	return (
		<>
			<div className='mt-1'>
				<Text color='secondary' size={3}>
					{sub(
						Liferay.Language.get(
							'x-to-learn-how-to-connect-liferay-dxp-to-analytics-cloud'
						),
						[
							<ClayLink
								href={URLConstants.HelpConnectDxp}
								key='helpConnectDxpText'
								target='_blank'
							>
								{upperFirst(
									Liferay.Language.get(
										'click-here'
									).toLowerCase()
								)}
							</ClayLink>
						],
						false
					)}
				</Text>
			</div>

			<div className='mt-4 mb-1'>
				<Label>
					<Text size={6} weight='bold'>
						{Liferay.Language.get('dxp-requirements')}
					</Text>

					<InfoPopover
						className='ml-2'
						content={Liferay.Language.get(
							'minimum-fix-pack-version-required-for-full-functionality'
						)}
						title={Liferay.Language.get('dxp-requirements')}
					/>
				</Label>
			</div>

			<div className='d-flex align-items-center justify-content-between'>
				<div className='flex-grow-1 mr-3'>
					<Select
						onChange={({target: {value}}) => setDxpVersion(value)}
						value={dxpVersion}
					>
						{dxpVersionsList.map(key => (
							<Select.Item key={key} value={key}>
								{DXP_VERSIONS[key].label}
							</Select.Item>
						))}
					</Select>
				</div>

				<div>
					<ClayLink
						button
						className='button-root'
						displayType='secondary'
						href={DXP_VERSIONS[dxpVersion].url}
						target='_blank'
					>
						<ClayIcon
							className='icon-root mr-2'
							symbol='shortcut'
						/>

						{Liferay.Language.get('download')}
					</ClayLink>
				</div>
			</div>
		</>
	);
};

const TokenInput: FC<ITokenInputProps> = ({token}) => {
	const [tokenCopied, setTokenCopied] = useState(false);
	const copyButtonClassName = getCN('copy-button', {
		'input-success': tokenCopied
	});

	const _inputRef = useRef<any>();

	const selectAll = () => {
		_inputRef.current && _inputRef.current.selectAll();
	};

	return (
		<>
			<div className='mb-1'>
				<Text weight='bold'>
					{Liferay.Language.get(
						'copy-this-token-to-your-dxp-instance'
					)}
				</Text>
			</div>

			<Input.Group>
				<Input.GroupItem position='prepend'>
					<Input
						className={getCN('text-truncate', {
							'input-success': tokenCopied
						})}
						inset='after'
						onChange={noop}
						onClick={selectAll}
						ref={_inputRef}
						value={token}
					/>

					<Input.Inset
						className={copyButtonClassName}
						position='after'
					>
						<CopyButton
							className={copyButtonClassName}
							displayType='unstyled'
							onClick={() => {
								setTokenCopied(true);
							}}
							text={token}
						/>
					</Input.Inset>
				</Input.GroupItem>
			</Input.Group>
		</>
	);
};

export default compose<any>(
	withHistory,
	connect(null, {
		fetchDataSource
	})
)(ConnectDXP);
