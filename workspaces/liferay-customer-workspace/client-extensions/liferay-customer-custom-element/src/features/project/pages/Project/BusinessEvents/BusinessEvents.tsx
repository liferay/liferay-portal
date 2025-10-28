/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import i18n from '~/utils/I18n';

import './BusinessEvents.css';

import Button from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {useModal} from '@clayui/modal';
import {useCallback, useMemo, useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {ButtonDropDown} from '~/components';
import Table, {IRow} from '~/components/Table';
import TableHeader from '~/components/Table/TableHeader';
import {useAppPropertiesContext} from '~/contexts/AppPropertiesContext';
import {useAppContext} from '~/features/project/context';
import {Liferay} from '~/services/liferay';
import {getFormattedDate} from '~/utils/getFormattedDate';
import {getFormattedTime} from '~/utils/getFormattedTime';
import getKebabCase from '~/utils/getKebabCase';
import {IBusinessEvent} from '~/utils/types';

import ManageEventModal from './components/ManageEventModal';
import useFilters from './hooks/useFilters';
import useGetBusinessEvents from './hooks/useGetBusinessEvents';
import useHasAllEventsPermissions from './hooks/useHasAllEventsPermissions';
import useIsSaasOnly from './utils/useIsSaasOnly';

const columns = [
	{
		columnKey: 'eventName',
		label: i18n.translate('event-name'),
		subLabel: i18n.translate('type'),
	},
	{
		columnKey: 'status',
		label: i18n.translate('status'),
	},
	{
		columnKey: 'details',
		label: i18n.translate('details'),
	},
	{
		columnKey: 'associatedTickets',
		label: i18n.translate('associated-tickets'),
	},
	{
		columnKey: 'targetGoLiveDate',
		label: i18n.translate('target-go-live-date'),
	},
	{
		columnKey: 'actions',
		label: '',
	},
];

const BusinessEvents = () => {
	const [{project, subscriptionGroups}] = useAppContext();

	const {filterQuery, filters, handleFilterChange, handleSearchChange} =
		useFilters(project);

	const {businessEvents, fetchBusinessEvents, loading} =
		useGetBusinessEvents(filterQuery);
	const [modalType, setModalType] = useState('');

	const {client} = useAppPropertiesContext();

	const {hasAllEventsPermissions} = useHasAllEventsPermissions();

	const {isSaasOnly} = useIsSaasOnly(subscriptionGroups);

	const navigate = useNavigate();

	const [selectedBusinessEvent, setSelectedBusinessEvent] = useState<
		IBusinessEvent | undefined
	>(undefined);

	const {observer, onOpenChange, open} = useModal({
		onClose: () => {
			setSelectedBusinessEvent(undefined);
		},
	});

	const handleOnCancel = useCallback(() => {
		fetchBusinessEvents();

		Liferay.Util.openToast({
			message: i18n.translate('business-event-canceled-successfully'),
			type: 'success',
		});
	}, [fetchBusinessEvents]);

	const handleOnCompleted = useCallback(() => {
		fetchBusinessEvents();

		Liferay.Util.openToast({
			message: i18n.translate(
				'business-event-actual-go-live-date-recorded-successfully'
			),
			type: 'success',
		});
	}, [fetchBusinessEvents]);

	const rows = useMemo(() => {
		if (businessEvents?.length > 0) {
			return businessEvents.map((businessEvent) => {
				const associatedTicketsCount = JSON.parse(
					businessEvent.associatedTickets!
				).length;

				const associatedTicketsString =
					associatedTicketsCount > 0
						? associatedTicketsCount > 1
							? `${associatedTicketsCount} Tickets`
							: `1 Ticket`
						: '';

				const isGoLiveType = businessEvent?.eventType?.key === 'goLive';

				const isOtherEventType =
					businessEvent?.eventType?.key === 'otherEvent';

				const DetailsColumn = () => {
					if (isGoLiveType) {
						return (
							<div className="text-neutral-10">
								{businessEvent?.currentLiferayVersion?.name}
							</div>
						);
					}

					if (isOtherEventType) {
						return (
							<div className="be-details text-neutral-10">
								{businessEvent?.description}
							</div>
						);
					}

					return (
						<div className="align-items-center d-flex">
							{!isSaasOnly && (
								<>
									<div className="text-neutral-10">
										{
											businessEvent?.currentLiferayVersion
												?.name
										}
									</div>

									<ClayIcon
										className="mx-2 text-neutral-4"
										symbol="order-arrow-right"
									/>
								</>
							)}

							<div className="text-neutral-10">
								{businessEvent?.newLiferayVersion?.name}
							</div>
						</div>
					);
				};

				const userOptions = [
					{
						customOptionStyle: 'pr-5',
						label: i18n.translate('view-details'),
						onClick: () => {
							navigate(
								`/${project?.accountKey}/business-events/${businessEvent.id}`
							);
						},
					},
				];

				if (
					hasAllEventsPermissions &&
					!['canceled', 'completed'].includes(
						businessEvent.eventStatus?.key!
					)
				) {
					userOptions.push(
						{
							customOptionStyle: 'pr-5',
							label: i18n.translate('edit-event'),
							onClick: () => {
								navigate(
									`/${project?.accountKey}/business-events/${businessEvent.id}/edit`
								);
							},
						},
						{
							customOptionStyle: 'pr-5',
							label: i18n.translate('record-actual-go-live'),
							onClick: () => {
								setModalType('goLiveEvent');
								onOpenChange(true);
								setSelectedBusinessEvent(businessEvent);
							},
						},
						{
							customOptionStyle: 'be-cancel-event-option pr-5',
							label: i18n.translate('cancel-event'),
							onClick: () => {
								setModalType('cancelEvent');
								onOpenChange(true);
								setSelectedBusinessEvent(businessEvent);
							},
						}
					);
				}

				return {
					actions: (
						<div className="d-flex justify-content-center">
							<ButtonDropDown
								customDropDownButton={
									<Button
										aria-label={i18n.translate(
											'manage-user-options'
										)}
										borderless
										className="text-neutral-5"
									>
										<span>
											<ClayIcon symbol="ellipsis-v" />
										</span>
									</Button>
								}
								items={userOptions}
								label="Options"
								menuElementAttrs={{
									className: 'p-0',
								}}
							/>
						</div>
					),
					associatedTickets: (
						<div className="text-neutral-10">
							{associatedTicketsString}
						</div>
					),
					details: <DetailsColumn />,
					eventName: (
						<div>
							<div className="be-event-name font-weight-semi-bold text-neutral-10">
								{businessEvent?.name}
							</div>

							<div className="be-subtitle text-neutral-7">
								{i18n.translate(
									getKebabCase(
										businessEvent?.eventType?.key as string
									) as string
								)}
							</div>
						</div>
					),
					status: (
						<div className="align-items-center d-flex">
							<div
								className={`align-items-center font-weight-semi-bold be-status be-status-${businessEvent?.eventStatus?.key} px-2 py-1`}
							>
								{i18n.translate(
									getKebabCase(
										businessEvent?.eventStatus
											?.key as string
									) as string
								)}
							</div>
						</div>
					),
					targetGoLiveDate: (
						<div>
							<div className="text-neutral-10">
								{getFormattedDate(
									businessEvent?.targetGoLiveDateTime,
									'day2DMonthSYearN',
									'UTC'
								)}
							</div>

							<div className="be-subtitle text-neutral-7">
								{getFormattedTime(
									businessEvent?.targetGoLiveDateTime,
									'UTC'
								)}
							</div>
						</div>
					),
				};
			});
		}

		return [];
	}, [
		businessEvents,
		hasAllEventsPermissions,
		isSaasOnly,
		navigate,
		onOpenChange,
		project?.accountKey,
	]);

	return loading ? (
		<div className="mx-auto">
			<ClayLoadingIndicator size="sm" />
		</div>
	) : (
		<div className="py-4">
			<div>
				<h1 className="font-weight-bold text-neutral-10">
					{i18n.translate('business-events')}
				</h1>

				<h6 className="font-weight-normal text-neutral-7">
					{i18n.translate(
						'this-table-allows-you-to-create-manage-and-track-your-business-events-please-note-that-business-events-closed-for-more-than-a-year-will-not-be-displayed-here'
					)}
				</h6>
			</div>

			<div className="mb-1">
				<TableHeader
					availableFilters={filters.availableFilters || []}
					hasCreatePermissions={hasAllEventsPermissions}
					onFilterChange={handleFilterChange}
					onSearchChange={handleSearchChange}
					searchResultsCount={businessEvents.length}
					searchTerm={filters.searchTerm || ''}
					selectedFilters={filters.selectedFilters || []}
				/>
			</div>

			<div>
				{businessEvents.length ? (
					<>
						<Table
							columns={columns}
							rows={rows as unknown as IRow[]}
						/>

						{selectedBusinessEvent && open && (
							<ManageEventModal
								accountExternalReferenceCode={
									project?.accountKey || ''
								}
								businessEvent={selectedBusinessEvent}
								client={client}
								closeFunction={onOpenChange}
								modalType={modalType}
								observer={observer}
								onCancel={handleOnCancel}
								onCompleted={handleOnCompleted}
							/>
						)}
					</>
				) : (
					<div className="p-3">
						{i18n.translate('no-business-events-were-found')}
					</div>
				)}
			</div>
		</div>
	);
};

export default BusinessEvents;
