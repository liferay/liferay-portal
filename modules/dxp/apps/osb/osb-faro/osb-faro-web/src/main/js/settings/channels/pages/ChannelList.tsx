import * as API from 'shared/api';
import BasePage from 'settings/components/BasePage';
import Card from 'shared/components/Card';
import ClayButton from '@clayui/button';
import ClayLink from '@clayui/link';
import CrossPageSelect from 'shared/hoc/CrossPageSelect';
import ListComponent from 'shared/hoc/ListComponent';
import Nav from 'shared/components/Nav';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React from 'react';
import RowActions from 'shared/components/RowActions';
import TextTruncate from 'shared/components/TextTruncate';
import URLConstants from 'shared/util/url-constants';
import {
	ACTION_TYPES,
	ActionTypes,
	useSelectionContext,
	withSelectionProvider
} from 'shared/context/selection';
import {addAlert} from 'shared/actions/alerts';
import {Alert} from 'shared/types';
import {close, modalTypes, open} from 'shared/actions/modals';
import {compose} from 'shared/hoc';
import {connect, ConnectedProps} from 'react-redux';
import {CREATE_TIME, createOrderIOMap} from 'shared/util/pagination';
import {formatDateToTimeZone} from 'shared/util/date';
import {FormikActions} from 'formik';
import {getPluralMessage, sub} from 'shared/util/lang';
import {IPagination} from 'shared/types';
import {Link} from 'react-router-dom';
import {RootState} from 'shared/store';
import {Routes, toRoute} from 'shared/util/router';
import {setBackURL} from 'shared/actions/settings';
import {Sizes} from 'shared/util/constants';
import {UNAUTHORIZED_ACCESS} from 'shared/util/request';
import {updateDefaultChannelId} from 'shared/actions/preferences';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useQueryPagination} from 'shared/hooks/useQueryPagination';
import {useRequest} from 'shared/hooks/useRequest';
import {useTimeZone} from 'shared/hooks/useTimeZone';

type ChannelNameFn = (attrs: {
	data: {id: string; name: string};
	hrefFormatter: (data: object) => string;
}) => React.ReactNode;

type FormValues = {
	name: string;
};

const ChannelName: ChannelNameFn = ({data, hrefFormatter}) => (
	<td className='table-cell-expand' key={data.id}>
		<div className='table-title text-truncate'>
			<Link to={hrefFormatter(data)}>
				<TextTruncate title={data.name} />
			</Link>
		</div>
	</td>
);

const connector = connect(
	(state: RootState) => ({
		defaultChannelId: state.getIn([
			'preferences',
			'user',
			'defaultChannelId',
			'data'
		])
	}),
	{addAlert, close, open, setBackURL, updateDefaultChannelId}
);

type PropsFromRedux = ConnectedProps<typeof connector>;

interface IChannelListProps extends IPagination, PropsFromRedux {
	groupId: string;
	history: {
		push: (href: string) => void;
	};
}

const ChannelList: React.FC<IChannelListProps> = ({
	addAlert,
	close,
	defaultChannelId,
	groupId,
	history,
	open,
	setBackURL,
	updateDefaultChannelId
}) => {
	const {selectedItems, selectionDispatch} = useSelectionContext();

	const {delta, orderIOMap, page, query} = useQueryPagination({
		initialOrderIOMap: createOrderIOMap(CREATE_TIME)
	});

	const {data, error, loading, refetch: refetchChannels} = useRequest({
		dataSourceFn: API.channels.search,
		variables: {
			cur: page,
			delta,
			groupId,
			orderIOMap,
			query
		}
	});

	const currentUser = useCurrentUser();

	const {timeZoneId} = useTimeZone();

	const handleAddChannel = () => {
		open(modalTypes.ADD_CHANNEL_MODAL, {
			onClose: close,
			onSubmit: handleSubmit
		});
	};

	const handleUnableToDeleteProperty = () => {
		open(modalTypes.UNABLE_DELETE_PROPERTY_MODAL, {
			onClose: close
		});
	};

	const handleClearData = (ids: string[], name: string) => {
		const message: string = getPluralMessage(
			name,
			Liferay.Language.get('x-properties'),
			ids.length
		) as string;

		open(modalTypes.DELETE_CONFIRMATION_MODAL, {
			children: (
				<>
					<p>
						<strong>
							{sub(
								Liferay.Language.get(
									'to-clear-data-from-x,-copy-the-sentence-below-to-confirm-your-intention-to-clear-data-from-this-property'
								),
								[message]
							)}
						</strong>
					</p>

					<p>
						{Liferay.Language.get(
							'this-will-result-in-the-complete-removal-of-this-propertys-historical-events.-you-will-not-be-able-to-undo-this-operation'
						)}
					</p>
				</>
			),
			deleteButtonLabel: Liferay.Language.get('clear-data'),
			deleteConfirmationText: sub(Liferay.Language.get('clear-x'), [
				message
			]),
			onClose: close,
			onSubmit: () =>
				API.channels
					.clear({
						groupId,
						ids
					})
					.then(() => {
						const clearedMessage: string = getPluralMessage(
							Liferay.Language.get('x-property-has-been-cleared'),
							Liferay.Language.get(
								'x-properties-have-been-cleared'
							),
							ids.length
						) as string;

						addAlert({
							alertType: Alert.Types.Success,
							message: sub(
								clearedMessage,
								[<b key='clearedCount'>{ids.length}</b>],
								false
							) as string
						});

						selectionDispatch({type: ActionTypes.ClearAll});

						refetchChannels();

						close();
					})
					.catch(err =>
						addAlert({
							alertType: Alert.Types.Error,
							message:
								err.message === UNAUTHORIZED_ACCESS
									? Liferay.Language.get(
											'unauthorized-access'
									  )
									: Liferay.Language.get('error'),
							timeout: false
						})
					),
			title: sub(Liferay.Language.get('clear-x-data?'), [message])
		});
	};

	const handleDeleteChannel = (ids: string[], name: string) => {
		const message: string = getPluralMessage(
			name,
			Liferay.Language.get('x-properties'),
			ids.length
		) as string;

		open(modalTypes.DELETE_CONFIRMATION_MODAL, {
			children: (
				<>
					<p>
						<strong>
							{sub(
								Liferay.Language.get(
									'to-delete-x,-copy-the-sentence-below-to-confirm-your-intention-to-delete-property'
								),
								[message]
							)}
						</strong>
					</p>

					<p>
						{Liferay.Language.get(
							'this-will-result-in-the-complete-removal-of-this-propertys-historical-events.-you-will-not-be-able-to-undo-this-operation'
						)}
					</p>
				</>
			),
			deleteButtonLabel: Liferay.Language.get('delete'),
			deleteConfirmationText: sub(Liferay.Language.get('delete-x'), [
				message
			]),
			onClose: close,
			onSubmit: () =>
				API.channels
					.delete({
						groupId,
						ids
					})
					.then(() => {
						const deletedMessage: string = getPluralMessage(
							Liferay.Language.get('x-property-has-been-deleted'),
							Liferay.Language.get(
								'x-properties-have-been-deleted'
							),
							ids.length
						) as string;

						addAlert({
							alertType: Alert.Types.Success,
							message: sub(
								deletedMessage,
								[<b key='deleteCount'>{ids.length}</b>],
								false
							) as string
						});

						if (ids.includes(defaultChannelId)) {
							updateDefaultChannelId({
								defaultChannelId: null,
								groupId
							});

							setBackURL(
								toRoute(Routes.WORKSPACE_WITH_ID, {
									groupId
								})
							);
						}

						selectionDispatch({type: ACTION_TYPES.clearAll});

						refetchChannels();

						close();
					})
					.catch(err =>
						addAlert({
							alertType: Alert.Types.Error,
							message:
								err.message === UNAUTHORIZED_ACCESS
									? Liferay.Language.get(
											'unauthorized-access'
									  )
									: Liferay.Language.get('error'),
							timeout: false
						})
					),
			title: sub(Liferay.Language.get('delete-x?'), [message])
		});
	};

	const handleSubmit = (
		{name}: FormValues,
		{setFieldError, setSubmitting}: FormikActions<FormValues>
	) => {
		API.channels
			.create({groupId, name: encodeURIComponent(name).trim()})
			.then(({id, name}) => {
				addAlert({
					alertType: Alert.Types.Success,
					message: sub(Liferay.Language.get('x-has-been-created'), [
						name
					]) as string
				});

				close();

				history.push(
					toRoute(Routes.SETTINGS_CHANNELS_VIEW, {
						groupId,
						id
					})
				);
			})
			.catch(({field, message}) => {
				setSubmitting(false);

				if (field) {
					setFieldError(field, message);
				}
			});
	};

	const renderNav = () => {
		if (selectedItems.isEmpty()) {
			return (
				<Nav>
					<Nav.Item>
						<ClayButton
							className='button-root nav-btn p-2'
							data-testid='addproperty-button'
							displayType='primary'
							onClick={handleAddChannel}
						>
							{Liferay.Language.get('new-property')}
						</ClayButton>
					</Nav.Item>
				</Nav>
			);
		} else {
			return (
				<Nav>
					<ClayButton
						borderless
						className='button-root'
						displayType='secondary'
						onClick={() =>
							handleClearData(
								selectedItems.keySeq().toArray(),
								selectedItems.first().name
							)
						}
						outline
					>
						{Liferay.Language.get('clear-data')}
					</ClayButton>

					<ClayButton
						borderless
						className='button-root'
						displayType='secondary'
						onClick={() => {
							const ableToDeleteChannel = !selectedItems.some(
								({commerceChannelsCount, groupsCount}) =>
									commerceChannelsCount || groupsCount
							);

							if (ableToDeleteChannel) {
								handleDeleteChannel(
									selectedItems.keySeq().toArray(),
									selectedItems.first().name
								);
							} else {
								handleUnableToDeleteProperty();
							}
						}}
						outline
					>
						{Liferay.Language.get('delete')}
					</ClayButton>
				</Nav>
			);
		}
	};

	const authorized: boolean = currentUser.isAdmin();

	const renderRowActions = ({
		data: {commerceChannelsCount, groupsCount, id, name}
	}) => {
		const actions = [
			{
				iconSymbol: 'magic',
				label: Liferay.Language.get('clear-data'),
				onClick: () => handleClearData([id], name)
			},
			{
				iconSymbol: 'trash',
				label: Liferay.Language.get('delete'),
				onClick: () => {
					if (!commerceChannelsCount && !groupsCount) {
						handleDeleteChannel([id], name);
					} else {
						handleUnableToDeleteProperty();
					}
				}
			}
		];

		return (
			<RowActions
				actions={actions.map(({label, onClick}) => ({
					label,
					onClick
				}))}
				quickActions={actions}
			/>
		);
	};

	return (
		<BasePage
			key='sitesListPage'
			pageDescription={
				<>
					<div>
						{Liferay.Language.get(
							'analytics-cloud-allows-for-customized-user-access-settings-per-property-managed'
						)}
					</div>
					<div>
						{Liferay.Language.get(
							'by-default-property-access-settings-will-be-set-to-all-users'
						)}
					</div>
				</>
			}
			pageTitle={Liferay.Language.get('properties')}
		>
			<Card pageDisplay>
				<CrossPageSelect
					columns={[
						{
							accessor: 'name',
							cellRenderer: ChannelName,
							cellRendererProps: {
								hrefFormatter: ({id}: {id: string}) =>
									toRoute(Routes.SETTINGS_CHANNELS_VIEW, {
										groupId,
										id
									})
							},
							className: 'table-cell-expand',
							label: Liferay.Language.get('property-name')
						},
						{
							accessor: 'groupsCount',
							className: 'text-right',
							label: Liferay.Language.get('sites'),
							sortable: false
						},
						{
							accessor: 'commerceChannelsCount',
							className: 'text-right',
							label: Liferay.Language.get('channels'),
							sortable: false
						},
						{
							accessor: 'id',
							className: 'text-right',
							label: Liferay.Language.get('property-id'),
							sortable: false
						},
						{
							accessor: 'permissionType',
							dataFormatter: value =>
								value === 0
									? Liferay.Language.get('all-users')
									: Liferay.Language.get('select-users'),
							label: Liferay.Language.get('access-setting'),
							sortable: false
						},
						{
							accessor: 'createTime',
							dataFormatter: date =>
								formatDateToTimeZone(date, 'll', timeZoneId),
							label: Liferay.Language.get('date-added')
						}
					]}
					currentUser={currentUser}
					delta={delta}
					entityLabel={Liferay.Language.get('properties')}
					error={error}
					items={data?.items}
					loading={loading}
					noResultsRenderer={
						<NoResultsDisplay
							description={
								<>
									{Liferay.Language.get(
										'create-a-property-to-get-started'
									)}

									<ClayLink
										className='d-block mb-3'
										href={URLConstants.CreateProperty}
										key='DOCUMENTATION'
										target='_blank'
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
								symbol: 'ac_satellite'
							}}
							title={Liferay.Language.get('no-properties-found')}
						/>
					}
					orderIOMap={orderIOMap}
					page={page}
					query={query}
					renderNav={authorized ? renderNav : null}
					renderRowActions={authorized ? renderRowActions : null}
					rowIdentifier='id'
					showCheckbox={authorized}
					total={data?.total}
				>
					{props => <ListComponent {...props} />}
				</CrossPageSelect>
			</Card>
		</BasePage>
	);
};

export default compose(connector, withSelectionProvider)(ChannelList);
