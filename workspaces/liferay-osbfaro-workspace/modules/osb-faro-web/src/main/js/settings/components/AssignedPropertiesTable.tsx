import ClayButton from '@clayui/button';
import ClayLink from '@clayui/link';
import CrossPageSelect from 'shared/hoc/CrossPageSelect';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React, {useEffect, useRef, useState} from 'react';
import TextTruncate from 'shared/components/TextTruncate';
import URLConstants from 'shared/util/url-constants';
import {Alert} from 'shared/types';
import {Card} from 'shared/components/revamping/Card';
import {ClayRadio, ClayRadioGroup, ClayToggle} from '@clayui/form';
import {createOrderIOMap, NAME} from 'shared/util/pagination';
import {DataSourceStatuses, Sizes} from 'shared/util/constants';
import {fetchChannelDatasources} from 'shared/api/data-source';
import {getChannelsAutoSelectFilter} from 'shared/util/data-sources';
import {Link, useParams} from 'react-router-dom';
import {modalTypes} from 'shared/actions/modals';
import {Routes, toRoute} from 'shared/util/router';
import {Text} from '@clayui/core';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useQueryPagination} from 'shared/hooks/useQueryPagination';
import {useRequest} from 'shared/hooks/useRequest';

interface IChannel {
	channelId: string;
	enabled: boolean;
}

interface IAssignedPropertiesTableProps {
	addAlert: (alert: {alertType: string; message: string}) => void;
	close: (...args: any[]) => void;
	customColumns?: any[];
	dataSource: any;
	handleUpdateDataSource: (...args: any[]) => void;
	loading?: boolean;
	open: (...args: any[]) => void;
	updateDataSourceFn: (params: {[key: string]: any}) => Promise<any>;
}

const AssignedPropertiesTable = ({
	addAlert,
	close,
	customColumns = [],
	dataSource,
	handleUpdateDataSource,
	loading: initialLoading = false,
	open,
	updateDataSourceFn,
}: IAssignedPropertiesTableProps) => {
	const [loading, setLoading] = useState(initialLoading);
	const {groupId} = useParams();

	const currentUser = useCurrentUser();

	const {delta, orderIOMap, page, query} = useQueryPagination({
		initialOrderIOMap: createOrderIOMap(NAME),
	});

	const enableAllChannels = dataSource.provider.getIn(
		['channelsConfiguration', 'enableAllChannels'],
		false
	);

	const channelDatasources = useRequest({
		dataSourceFn: fetchChannelDatasources,
		variables: {delta, groupId, id: dataSource.id, orderIOMap, page, query},
	});

	const channelsConfigurationRef = useRef<{
		channels: IChannel[];
		enableAllChannels: boolean;
	}>({
		channels: [],
		enableAllChannels: false,
	});

	const dataSourceActive = dataSource.status === DataSourceStatuses.Active;

	const handleToggleChannel = async (item: IChannel) => {
		const selectedChannelIndex =
			channelsConfigurationRef.current.channels.findIndex(
				({channelId}) => channelId === item.channelId
			);

		if (selectedChannelIndex !== -1) {
			const updatedChannelsConfiguration = {
				...channelsConfigurationRef.current,
				channels: channelsConfigurationRef.current.channels.map(
					(channel, index) =>
						index === selectedChannelIndex
							? {...channel, enabled: !channel.enabled}
							: channel
				),
			};

			try {
				await updateDataSourceFn({
					channelsConfiguration: updatedChannelsConfiguration,
					groupId,
					id: dataSource.id,
				} as any);
			}
			catch (error) {
				addAlert({
					alertType: Alert.Types.Error,
					message: Liferay.Language.get(
						'there-was-an-error-processing-your-request.-try-again.-if-the-problem-persists,-please-contact-support'
					),
				});
			}
			finally {
				channelsConfigurationRef.current = updatedChannelsConfiguration;
			}
		}
	};

	useEffect(() => {
		if (dataSource.provider?.get('channelsConfiguration')) {
			channelsConfigurationRef.current = {
				channels: dataSource.provider
					.getIn(['channelsConfiguration', 'channels'])
					.toJS(),
				enableAllChannels: dataSource.provider.get(
					'channelsConfiguration'
				).enableAllChannels,
			};
		}
	}, [dataSource]);

	return (
		<>
			<div className="p-4">
				<Text as="p" color="secondary" size={4}>
					{Liferay.Language.get(
						'properties-let-you-consolidate-data-from-individuals,-accounts,-campaigns,-sites,-and-commerce-channels-in-one-place.-an-individuals-data-is-available-in-every-property-they-are-assigned-to'
					)}
				</Text>

				<Card.SubHeader
					title={Liferay.Language.get('data-availability')}
				/>

				<Text as="p" color="secondary" size={4}>
					{Liferay.Language.get(
						'choose-the-properties-that-will-have-access-to-this-data-source'
					)}
				</Text>

				<ClayRadioGroup
					defaultValue={enableAllChannels ? 'all' : 'custom'}
					inline
					onChange={async (value) => {
						await updateDataSourceFn({
							channelsConfiguration: {
								channels:
									channelsConfigurationRef.current.channels,
								enableAllChannels: value === 'all',
							},
							groupId,
							id: dataSource.id,
						} as any);

						await handleUpdateDataSource();
					}}
				>
					<ClayRadio
						disabled={!currentUser.isAdmin() || !dataSourceActive}
						label={Liferay.Language.get(
							'make-individual-data-from-this-data-source-available-in-all-properties,-including-those-not-yet-created'
						)}
						value="all"
					/>

					<ClayRadio
						disabled={!currentUser.isAdmin() || !dataSourceActive}
						label={Liferay.Language.get('select-properties')}
						value="custom"
					/>
				</ClayRadioGroup>
			</div>

			{enableAllChannels ? (
				<div className="d-flex justify-content-center text-center my-6">
					<NoResultsDisplay
						description={Liferay.Language.get(
							'all-properties-from-this-workspace-have-access-to-this-data-source'
						)}
						icon={{
							border: false,
							size: Sizes.XXXLarge,
							symbol: 'ac_no_sites',
						}}
						title={Liferay.Language.get('all-aboard')}
					/>
				</div>
			) : (
				<CrossPageSelect
					columns={[
						{
							accessor: 'name',
							cellRenderer: ({
								data,
								hrefFormatter,
							}: {
								data: {channelId: string; name: string};
								hrefFormatter: (data: {
									channelId: string;
								}) => string;
							}) => (
								<td
									className="table-cell-expand"
									key={data.channelId}
								>
									<div className="table-title text-truncate">
										<Link to={hrefFormatter(data)}>
											<TextTruncate title={data.name} />
										</Link>
									</div>
								</td>
							),
							cellRendererProps: {
								hrefFormatter: ({
									channelId,
								}: {
									channelId: string;
								}) =>
									toRoute(Routes.SETTINGS_CHANNELS_VIEW, {
										groupId,
										id: channelId,
									}),
							},
							className: 'table-cell-expand',
							label: Liferay.Language.get('property-name'),
						},
						...customColumns,
						{
							accessor: 'enabled',
							cellRenderer: ({data}: {data: IChannel}) => (
								<ToggleRenderer
									addAlert={addAlert}
									close={close}
									data={data}
									disabled={
										loading ||
										!currentUser.isAdmin() ||
										!dataSourceActive
									}
									key={`${data.channelId}-toggle`}
									onChange={handleToggleChannel}
									open={open}
								/>
							),
							label: '',
							sortable: false,
						},
					]}
					delta={delta}
					entityLabel={Liferay.Language.get('properties')}
					error={channelDatasources.error}
					items={channelDatasources.data?.items}
					loading={loading}
					noResultsRenderer={
						<NoResultsDisplay
							className="py-6"
							description={
								<>
									{Liferay.Language.get(
										'go-to-properties-under-workspace-settings-to-create-a-property'
									)}

									<ClayLink
										className="d-block mb-3"
										href={URLConstants.CreateProperty}
										key="DOCUMENTATION"
										target="_blank"
									>
										{Liferay.Language.get(
											'access-our-documentation-to-learn-more'
										)}
									</ClayLink>
								</>
							}
							icon={{
								border: false,
								size: Sizes.XXXLarge,
								symbol: 'ac_satellite',
							}}
							title={Liferay.Language.get(
								'no-properties-were-found'
							)}
						/>
					}
					orderByOptions={[
						{
							label: Liferay.Language.get('property-name'),
							value: NAME,
						},
					]}
					ordered
					orderIOMap={orderIOMap}
					page={page}
					query={query}
					renderNav={() => {
						if (currentUser.isAdmin()) {
							return (
								<ClayButton
									disabled={!dataSourceActive}
									onClick={() =>
										open(modalTypes.SELECT_CHANNELS_MODAL, {
											autoSelectFilter:
												getChannelsAutoSelectFilter(
													dataSource
												),
											groupId,
											initialItems:
												channelsConfigurationRef.current?.channels?.map(
													({channelId}) => channelId
												),
											onClose: close,
											onSelect: async (
												items: string[]
											) => {
												setLoading(true);

												await updateDataSourceFn({
													channelsConfiguration: {
														channels: items.map(
															(
																channelId: string
															) => ({
																channelId,
																enabled: true,
															})
														),
														enableAllChannels:
															false,
													},
													groupId,
													id: dataSource.id,
												} as any);

												await handleUpdateDataSource();

												channelDatasources.refetch?.();

												setLoading(false);
											},
										})
									}
								>
									{Liferay.Language.get('select-property')}
								</ClayButton>
							);
						}

						return null;
					}}
					rowIdentifier="channelId"
					showCheckbox={false}
					total={channelDatasources.data?.total}
				/>
			)}
		</>
	);
};

const ToggleRenderer = ({
	addAlert,
	close,
	data,
	disabled,
	onChange,
	open,
}: {
	addAlert: (alert: {alertType: string; message: string}) => void;
	close: (...args: any[]) => void;
	data: IChannel;
	disabled: boolean;
	onChange: (channel: IChannel) => void;
	open: (...args: any[]) => void;
}) => {
	const [state, setState] = useState(data.enabled);

	const handleChange = (newState: boolean) => {
		setState(newState);

		onChange({
			channelId: data.channelId,
			enabled: newState,
		});
	};

	return (
		<td className="text-center">
			<ClayToggle
				defaultChecked={state}
				disabled={disabled}
				onToggle={() => {
					if (state) {
						open(modalTypes.CONFIRMATION_MODAL, {
							cancelMessage: Liferay.Language.get('cancel'),
							message: Liferay.Language.get(
								'this-action-will-stop-syncing-new-data-from-your-liferay-dxp-instance-to-this-property.-data-that-was-already-synced-will-remain-available.-are-you-sure-you-want-to-continue'
							),
							modalVariant: 'modal-warning',
							onClose: close,
							onSubmit: () => {
								handleChange(!state);

								addAlert({
									alertType: Alert.Types.Success,
									message: Liferay.Language.get(
										'properties-settings-have-been-saved'
									),
								});
							},
							submitButtonDisplay: 'warning',
							submitMessage: Liferay.Language.get('stop-syncing'),
							title: Liferay.Language.get('stop-syncing-data'),
							titleIcon: 'warning-full',
						});
					}
					else {
						handleChange(!state);
					}
				}}
				sizing="sm"
				toggled={state}
			/>
		</td>
	);
};

export {AssignedPropertiesTable};
