import * as API from 'shared/api';
import * as breadcrumbs from 'shared/util/breadcrumbs';
import BasePage from 'settings/components/BasePage';
import Card from 'shared/components/Card';
import ClayButton from '@clayui/button';
import Constants from 'shared/util/constants';
import EmailReports from '../components/EmailReports';
import Form, {
	validateMaxLength,
	validateMinLength,
	validateRequired
} from 'shared/components/form';
import HelpBlock from 'shared/components/form/HelpBlock';
import RadioGroup from 'shared/components/RadioGroup';
import React, {useState} from 'react';
import StatesRenderer from 'shared/components/states-renderer/StatesRenderer';
import SyncedStripe from '../components/SyncedStripe';
import TitleEditor from 'shared/components/TitleEditor';
import UserList from '../components/UserList';
import {addAlert} from 'shared/actions/alerts';
import {Alert, IPaginationUnsorted} from 'shared/types';
import {close, modalTypes, open} from 'shared/actions/modals';
import {compose} from 'shared/hoc';
import {connect, ConnectedProps} from 'react-redux';
import {RootState} from 'shared/store';
import {Routes, toRoute} from 'shared/util/router';
import {SafeResults} from 'shared/hoc/util';
import {sequence} from 'shared/util/promise';
import {setBackURL} from 'shared/actions/settings';
import {sub} from 'shared/util/lang';
import {UNAUTHORIZED_ACCESS} from 'shared/util/request';
import {updateDefaultChannelId} from 'shared/actions/preferences';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useRequest} from 'shared/hooks/useRequest';

const {channelPermissionTypes} = Constants;

type Channel = {
	commerceChannelsCount: number;
	createTime: number;
	groupsCount: number;
	id: string;
	name: string;
	permissionType: number;
};

export const ViewContainer: React.FC<Omit<IViewProps, 'channel'>> = ({
	groupId,
	id,
	...otherProps
}) => {
	const {data, error, loading, refetch} = useRequest({
		dataSourceFn: API.channels.fetch,
		variables: {
			channelId: id,
			groupId
		}
	});

	return (
		<SafeResults
			data={data}
			error={error}
			errorProps={{
				href: toRoute(Routes.SETTINGS_CHANNELS, {groupId}),
				linkLabel: Liferay.Language.get('go-to-properties'),
				message: Liferay.Language.get(
					'the-property-you-are-looking-for-does-not-exist'
				),
				subtitle: Liferay.Language.get('property-not-found')
			}}
			loading={loading}
			onReload={refetch}
			pageDisplay
			spacer
		>
			{(channel: Channel) => (
				<View
					{...otherProps}
					channel={channel}
					groupId={groupId}
					id={id}
				/>
			)}
		</SafeResults>
	);
};

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

interface IViewProps
	extends React.HTMLAttributes<HTMLElement>,
		PropsFromRedux,
		IPaginationUnsorted {
	channel?: Channel;
	groupId: string;
	history: {
		push: (value: string) => void;
	};
	id: string;
}

const View: React.FC<IViewProps> = ({
	addAlert,
	channel,
	close,
	defaultChannelId,
	groupId,
	history,
	id,
	open,
	setBackURL,
	updateDefaultChannelId,
	...otherProps
}) => {
	const currentUser = useCurrentUser();

	const [name, setName] = useState(channel.name);
	const [permissionType, setPermissionType] = useState(
		channel.permissionType
	);

	const updatePermissions = permissionType =>
		API.channels
			.update({
				groupId,
				id,
				permissionType
			})
			.then(response => setPermissionType(response.permissionType))
			.catch(() =>
				addAlert({
					alertType: Alert.Types.Error,
					message: Liferay.Language.get('error'),
					timeout: false
				})
			);

	const authorized = currentUser.isAdmin();

	const handleUnableToDeleteProperty = () => {
		open(modalTypes.UNABLE_DELETE_PROPERTY_MODAL, {
			onClose: close
		});
	};

	return (
		<BasePage
			breadcrumbItems={[
				breadcrumbs.getChannels({groupId}),
				breadcrumbs.getChannelName({
					active: true,
					label: name
				})
			]}
			documentTitle={`${name} - ${Liferay.Language.get('properties')}`}
		>
			<div className='content-header has-page-actions'>
				<div className='header-text w-100'>
					<Form
						initialValues={{
							name: channel.name
						}}
						onSubmit={({name}) =>
							API.channels
								.update({
									groupId,
									id,
									name: encodeURIComponent(name)
								})
								.then(({name}) => setName(name))
								.catch(() =>
									addAlert({
										alertType: Alert.Types.Error,
										message: Liferay.Language.get('error'),
										timeout: false
									})
								)
						}
					>
						{({errors, handleSubmit, values: {name}}) => (
							<>
								<TitleEditor
									editable={authorized}
									name='name'
									onBlur={() => {
										handleSubmit();

										if (!errors.name) {
											setName(name);
										}
									}}
									validate={sequence([
										validateMaxLength(75),
										validateMinLength(3),
										validateRequired
									])}
								/>

								<HelpBlock
									className='text-danger'
									name='name'
								/>
							</>
						)}
					</Form>

					<div className='description'>
						{sub(Liferay.Language.get('property-id-x'), [
							channel.id
						])}
					</div>
				</div>

				<div className='d-flex'>
					<EmailReports
						channelId={id}
						className='align-items-center d-flex'
						sitesSynced={!!channel.groupsCount}
					/>

					{authorized && (
						<span className='header-action-buttons pl-3'>
							<ClayButton
								className='button-root mr-3'
								data-testid='clear-data'
								displayType='secondary'
								onClick={() =>
									open(modalTypes.DELETE_CONFIRMATION_MODAL, {
										children: (
											<>
												<p>
													<strong>
														{sub(
															Liferay.Language.get(
																'to-clear-data-from-x,-copy-the-sentence-below-to-confirm-your-intention-to-clear-data-from-this-property'
															),
															[name]
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
										deleteButtonLabel: Liferay.Language.get(
											'clear-data'
										),
										deleteConfirmationText: sub(
											Liferay.Language.get('clear-x'),
											[name]
										),
										onClose: close,
										onSubmit: () => {
											API.channels
												.clear({
													groupId,
													ids: [id]
												})
												.then(() => {
													const clearedMessage = Liferay.Language.get(
														'data-from-x-has-been-cleared'
													);

													addAlert({
														alertType:
															Alert.Types.Success,
														message: sub(
															clearedMessage,
															[name]
														) as string
													});

													close();
												})
												.catch(err =>
													addAlert({
														alertType:
															Alert.Types.Error,
														message:
															err.message ===
															UNAUTHORIZED_ACCESS
																? Liferay.Language.get(
																		'unauthorized-access'
																  )
																: Liferay.Language.get(
																		'error'
																  ),
														timeout: false
													})
												);
										},
										title: sub(
											Liferay.Language.get(
												'clear-x-data?'
											),
											[name]
										)
									})
								}
							>
								{Liferay.Language.get('clear-data')}
							</ClayButton>

							<ClayButton
								className='button-root'
								data-testid='delete'
								displayType='secondary'
								onClick={() => {
									if (
										channel.commerceChannelsCount ||
										channel.groupsCount
									) {
										handleUnableToDeleteProperty();

										return;
									}

									open(modalTypes.DELETE_CONFIRMATION_MODAL, {
										children: (
											<>
												<p>
													<strong>
														{sub(
															Liferay.Language.get(
																'to-delete-x,-copy-the-sentence-below-to-confirm-your-intention-to-delete-property'
															),
															[name]
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
										deleteButtonLabel: Liferay.Language.get(
											'delete'
										),
										deleteConfirmationText: sub(
											Liferay.Language.get('delete-x'),
											[name]
										),
										onClose: close,
										onSubmit: () => {
											API.channels
												.delete({
													groupId,
													ids: [id]
												})
												.then(() => {
													const deletedMessage = Liferay.Language.get(
														'x-has-been-deleted'
													);

													close();

													history.push(
														toRoute(
															Routes.SETTINGS_CHANNELS,
															{
																groupId,
																id
															}
														)
													);

													addAlert({
														alertType:
															Alert.Types.Success,
														message: sub(
															deletedMessage,
															[name]
														) as string
													});

													if (
														defaultChannelId === id
													) {
														updateDefaultChannelId({
															defaultChannelId: null,
															groupId
														});

														setBackURL(
															toRoute(
																Routes.WORKSPACE_WITH_ID,
																{
																	groupId
																}
															)
														);
													}
												})
												.catch(err =>
													addAlert({
														alertType:
															Alert.Types.Error,
														message:
															err.message ===
															UNAUTHORIZED_ACCESS
																? Liferay.Language.get(
																		'unauthorized-access'
																  )
																: Liferay.Language.get(
																		'error'
																  ),
														timeout: false
													})
												);
										},
										title: sub(
											Liferay.Language.get('delete-x?'),
											[name]
										)
									});
								}}
							>
								{Liferay.Language.get('delete')}
							</ClayButton>
						</span>
					)}
				</div>
			</div>

			<Card pageDisplay>
				<SyncedStripe
					channelsSyncedCount={channel.commerceChannelsCount}
					sitesSyncedCount={channel.groupsCount}
				/>

				<Card.Body className='flex-grow-0'>
					<RadioGroup
						checked={permissionType}
						disabled={!authorized}
						inline
						name='permissionType'
						onChange={val => {
							if (val === channelPermissionTypes.selectUsers) {
								open(modalTypes.CONFIRMATION_MODAL, {
									message: (
										<div className='text-secondary'>
											{Liferay.Language.get(
												'property-permissions-will-be-changed-if-you-proceed-to-select-users.-add-users-from-your-workspace-to-give-access-to-this-property'
											)}
										</div>
									),
									modalVariant: 'modal-warning',
									onClose: close,
									onSubmit: () => {
										updatePermissions(val);

										close();
									},
									submitButtonDisplay: 'warning',
									submitMessage: Liferay.Language.get('okay'),
									title: Liferay.Language.get(
										'permissions-change'
									),
									titleIcon: 'warning-full'
								});
							} else {
								updatePermissions(val);
							}
						}}
					>
						<RadioGroup.Option
							label={<b>{Liferay.Language.get('all-users')}</b>}
							value={channelPermissionTypes.allUsers}
						/>
						<RadioGroup.Option
							label={
								<b>{Liferay.Language.get('select-users')}</b>
							}
							value={channelPermissionTypes.selectUsers}
						/>
					</RadioGroup>
				</Card.Body>

				<Card.Body noPadding>
					<StatesRenderer
						empty={
							permissionType === channelPermissionTypes.allUsers
						}
					>
						<StatesRenderer.Empty
							description={Liferay.Language.get(
								'all-users-from-this-workspace-have-access-to-this-property'
							)}
							icon={{
								symbol: 'ac_no_sites'
							}}
							title={Liferay.Language.get('all-aboard')}
						/>
						<StatesRenderer.Success>
							<UserList
								{...otherProps}
								authorized={currentUser.isAdmin()}
								groupId={groupId}
								id={channel.id}
								propertyName={channel.name}
							/>
						</StatesRenderer.Success>
					</StatesRenderer>
				</Card.Body>
			</Card>
		</BasePage>
	);
};

export default compose<any>(connector)(ViewContainer);
