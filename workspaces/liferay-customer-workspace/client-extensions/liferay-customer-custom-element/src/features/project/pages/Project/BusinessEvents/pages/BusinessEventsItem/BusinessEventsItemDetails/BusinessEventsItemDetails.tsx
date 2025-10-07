/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Nav} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {useModal} from '@clayui/modal';
import NavigationBar from '@clayui/navigation-bar';
import {useCallback, useEffect, useState} from 'react';
import {Link, useLocation, useNavigate, useParams} from 'react-router-dom';
import {ButtonDropDown} from '~/components';
import {useAppPropertiesContext} from '~/contexts/AppPropertiesContext';
import {Liferay} from '~/services/liferay';
import i18n from '~/utils/I18n';
import {getFormattedDate} from '~/utils/getFormattedDate';
import {getFormattedTime} from '~/utils/getFormattedTime';
import {ITicket} from '~/utils/types';

import './BusinessEventsItemDetails.css';

import getKebabCase from '~/utils/getKebabCase';

import AssociatedTicketsContainer from '../../../components/AssociatedTicketsContainer';
import ManageEventModal from '../../../components/ManageEventModal';
import useAccountsTickets from '../../../hooks/useAccountsTickets';
import useGetBusinessEvent from '../../../hooks/useGetBusinessEvent';
import useHasAllEventsPermissions from '../../../hooks/useHasAllEventsPermissions';

const BusinessEventsItemDetails = () => {
	const {accountKey, id} = useParams<{accountKey: string; id: string}>();

	const {businessEvent, fetchBusinessEvent, loading} = useGetBusinessEvent(
		id || ''
	);

	const {client} = useAppPropertiesContext();

	const [modalType, setModalType] = useState('');
	const {hasAllEventsPermissions} = useHasAllEventsPermissions();

	const {loading: loadingTickets, tickets} = useAccountsTickets(
		businessEvent,
		accountKey,
		loading
	);

	const location = useLocation();
	const navigate = useNavigate();

	const {observer, onOpenChange, open} = useModal();

	const userOptions = [
		{
			customOptionStyle: 'pr-5',
			icon: <ClayIcon symbol="pencil" />,
			label: i18n.translate('edit-event'),
			onClick: () => {
				navigate(`/${accountKey}/business-events/${id}/edit`);
			},
		},
		{
			customOptionStyle: 'pr-5',
			icon: <ClayIcon symbol="check-circle" />,
			label: i18n.translate('record-actual-go-live'),
			onClick: () => {
				setModalType('goLiveEvent');
				onOpenChange(true);
			},
		},
		{
			customOptionStyle: 'cancel-event-option pr-5',
			icon: <ClayIcon symbol="trash" />,
			label: i18n.translate('cancel-event'),
			onClick: () => {
				setModalType('cancelEvent');
				onOpenChange(true);
			},
		},
	];

	const [ticketOptions, setTicketOptions] = useState<ITicket[]>([]);

	const handleOnCancel = useCallback(() => {
		fetchBusinessEvent();

		Liferay.Util.openToast({
			message: i18n.translate('business-event-canceled-successfully'),
			type: 'success',
		});
	}, [fetchBusinessEvent]);

	const handleOnCompleted = useCallback(() => {
		fetchBusinessEvent();

		Liferay.Util.openToast({
			message: i18n.translate(
				'business-event-actual-go-live-date-recorded-successfully'
			),
			type: 'success',
		});
	}, [fetchBusinessEvent]);

	useEffect(() => {
		if (businessEvent && tickets) {
			const associatedTickets = JSON.parse(
				businessEvent.associatedTickets!
			);

			setTicketOptions([
				...(tickets?.filter((ticket) =>
					associatedTickets.includes(ticket.ticketId)
				) || []),
			]);
		}

		const params = new URLSearchParams(location.search);
		const modalType = params.get('openModal');

		if (modalType === 'goLiveEvent') {
			setModalType('goLiveEvent');

			onOpenChange(true);
		}
	}, [businessEvent, location.search, onOpenChange, tickets]);

	if (loading) {
		return (
			<div className="mx-auto">
				<ClayLoadingIndicator size="sm" />
			</div>
		);
	}

	if (!businessEvent) {
		return <div>{i18n.translate('no-data-found')}</div>;
	}

	return (
		<div>
			<div className="be-breadcrumbs font-weight-semi-bold mb-4">
				<span className="mx-2">
					<Link to={`/${accountKey}/business-events/`}>
						<ClayIcon className="mr-1" symbol="order-arrow-left" />

						{i18n.translate('back-to-business-events')}
					</Link>
				</span>
			</div>

			<div>
				<div
					className={`align-items-center font-weight-semi-bold be-status be-status-${businessEvent?.eventStatus?.key} mb-1 d-inline px-2 py-1`}
				>
					{i18n.translate(
						getKebabCase(
							businessEvent?.eventStatus?.key as string
						) as string
					)}
				</div>

				<div className="alight-items-center d-flex justify-content-between mb-4 mt-2">
					<div className="font-weight-bold text-neutral-10">
						<h3>{businessEvent.name}</h3>
					</div>

					{hasAllEventsPermissions &&
						!['canceled', 'completed'].includes(
							businessEvent.eventStatus?.key!
						) && (
							<div className="be-actions">
								<ButtonDropDown
									items={userOptions}
									label={i18n.translate('actions')}
									menuElementAttrs={{
										className: 'p-0',
									}}
								/>
							</div>
						)}
				</div>
			</div>

			<div className="mb-4">
				<NavigationBar
					fluidSize={false}
					triggerLabel={i18n.translate('event-details')}
				>
					<Nav.Item>
						<Nav.Link
							active={true}
							aria-label={`Switch to ${i18n.translate('event-details')}`}
							className="be-nav-link text-neutral-10"
						>
							{i18n.translate('event-details')}
						</Nav.Link>
					</Nav.Item>
					<Nav.Item
						onClick={() =>
							navigate(
								`/${accountKey}/business-events/${id}/activity-history`
							)
						}
					>
						<Nav.Link
							active={false}
							aria-label={`Switch to ${i18n.translate('activity-history')}`}
							className="be-nav-link text-neutral-10"
						>
							{i18n.translate('activity-history')}
						</Nav.Link>
					</Nav.Item>
				</NavigationBar>
			</div>

			<div className="mt-4">
				<div className="event-detail-container">
					{businessEvent?.eventType && (
						<div className="event-detail-item mb-4">
							<div className="event-detail-title font-weight-semi-bold mb-1 text-neutral-8">
								{i18n.translate('event-type')}
							</div>

							<div className="d-inline-block event-detail-value font-weight-semi-bold rounded text-neutral-9">
								{i18n.translate(
									getKebabCase(
										businessEvent?.eventType?.key as string
									) as string
								)}
							</div>
						</div>
					)}

					{businessEvent?.currentLiferayVersion?.key && (
						<div className="event-detail-item mb-4">
							<div className="event-detail-title font-weight-semi-bold mb-1 text-neutral-8">
								{i18n.translate('current-version')}
							</div>

							<div className="d-inline-block event-detail-value font-weight-semi-bold rounded text-neutral-9">
								{businessEvent?.currentLiferayVersion?.name}
							</div>
						</div>
					)}

					{businessEvent?.newLiferayVersion?.key && (
						<div className="event-detail-item mb-4">
							<div className="event-detail-title font-weight-semi-bold mb-1 text-neutral-8">
								{i18n.translate('new-version')}
							</div>

							<div className="d-inline-block event-detail-value font-weight-semi-bold rounded text-neutral-9">
								{businessEvent?.newLiferayVersion?.name}
							</div>
						</div>
					)}

					{businessEvent?.description && (
						<div className="event-detail-item mb-4">
							<div className="event-detail-title font-weight-semi-bold mb-2 text-neutral-8">
								{i18n.translate('details')}
							</div>

							<div className="d-inline-block text-neutral-9">
								{businessEvent?.description}
							</div>
						</div>
					)}

					{businessEvent?.targetGoLiveDateTime && (
						<div className="event-detail-item mb-4">
							<div className="event-detail-title font-weight-semi-bold mb-1 text-neutral-8">
								{i18n.translate('target-go-live-date')}
							</div>

							<div className="d-inline-block event-detail-value font-weight-semi-bold rounded text-neutral-9">
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
						</div>
					)}

					{businessEvent?.actualGoLiveDateTime && (
						<div className="event-detail-item mb-4">
							<div className="event-detail-title font-weight-semi-bold mb-1 text-neutral-8">
								{i18n.translate('actual-go-live-date')}
							</div>

							<div className="d-inline-block event-detail-value font-weight-semi-bold rounded text-neutral-9">
								<div className="text-neutral-10">
									{getFormattedDate(
										businessEvent?.actualGoLiveDateTime,
										'day2DMonthSYearN',
										'UTC'
									)}
								</div>

								<div className="be-subtitle text-neutral-7">
									{getFormattedTime(
										businessEvent?.actualGoLiveDateTime,
										'UTC'
									)}
								</div>
							</div>
						</div>
					)}

					{!loadingTickets ? (
						!tickets ? (
							<p
								dangerouslySetInnerHTML={{
									__html: i18n.sub(
										'we-apologize-for-the-inconvenience-but-we-ve-detected-a-system-error-with-this-project',
										[
											'<a href="https://liferay.atlassian.net/servicedesk/customer/portals">',
											'</a>',
										]
									),
								}}
							/>
						) : (
							Boolean(ticketOptions.length) && (
								<div className="event-detail-item mb-4">
									<div className="event-detail-title font-weight-semi-bold mb-1 text-neutral-8">
										{i18n.translate('associated-tickets')}
									</div>

									<div className="w-50">
										<AssociatedTicketsContainer
											ticketOptions={ticketOptions}
										/>
									</div>
								</div>
							)
						)
					) : (
						<div className="w-25">
							<ClayLoadingIndicator size="sm" />
						</div>
					)}
				</div>
			</div>

			{businessEvent && open && (
				<ManageEventModal
					accountExternalReferenceCode={accountKey || ''}
					businessEvent={businessEvent}
					client={client}
					closeFunction={onOpenChange}
					modalType={modalType}
					observer={observer}
					onCancel={handleOnCancel}
					onCompleted={handleOnCompleted}
				/>
			)}
		</div>
	);
};

export default BusinessEventsItemDetails;
