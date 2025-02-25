/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import i18n from '~/utils/I18n';

import './BusinessEvents.css';

import {ButtonWithIcon} from '@clayui/core';
import {useCallback, useEffect, useMemo, useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {ButtonDropDown} from '~/components';
import {IFilterOption} from '~/components/Filter/Filter';
import Table, {IRow} from '~/components/Table';
import TableHeader from '~/components/Table/TableHeader';
import {hasAdminUserAccount} from '~/features/project/containers/ActivationKeysTable/utils/hasAdminUserAccount';
import {useCustomerPortal} from '~/features/project/context';
import {getFormattedDate} from '~/features/project/utils/getFormattedDate';
import useCurrentKoroneikiAccount from '~/hooks/useCurrentKoroneikiAccount';
import {getBusinessEvents} from '~/services/liferay/api';
import {getFormattedTime} from '~/utils/getFormattedTime';
import {IOrganizationBrief} from '~/utils/types';

import useMyUserAccountByAccountExternalReferenceCode from '../TeamMembers/components/TeamMembersTable/hooks/useMyUserAccountByAccountExternalReferenceCode';
import {INITIAL_FILTER} from './utils/constants/initialFilter';

export interface IBusinessEventTicket {
	associatedTickets: string;
	currentLiferayVersion: Record<string, string>;
	description: string;
	eventStatus: Record<string, string>;
	eventType: Record<string, string>;
	id: number;
	name: string;
	newLiferayVersion: Record<string, string>;
	targetGoLiveDateTime: string;
}

export interface IState {
	availableFilters?: IFilterOption[];
	searchTerm?: string;
	selectedFilters?: IFilterOption[];
}

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
	const [{project}] = useCustomerPortal();

	const [filters, setFilters] = useState<IState>({
		availableFilters: INITIAL_FILTER,
		searchTerm: '',
		selectedFilters: [],
	});
	const {data, loading} = useCurrentKoroneikiAccount();
	const koroneikiAccount = data?.koroneikiAccountByExternalReferenceCode;

	const {data: myUserAccountData} =
		useMyUserAccountByAccountExternalReferenceCode(
			koroneikiAccount?.accountKey,
			loading
		);
	const loggedUserAccount = myUserAccountData?.myUserAccount;

	const [businessEventsTickets, setBusinessEventsTickets] = useState<
		IBusinessEventTicket[]
	>([]);

	const isAdminUserAccount = hasAdminUserAccount(myUserAccountData);
	const hasProjectAdminOrRequesterRole =
		loggedUserAccount?.selectedAccountSummary?.hasSupportSeatRole;
	const isLiferayStaff = loggedUserAccount?.isLiferayStaff;

	const hasFLSOrganizationAssociated = useMemo<boolean>(
		() =>
			loggedUserAccount?.organizationBriefs?.some(
				(orgBrief: IOrganizationBrief) => orgBrief.name.includes('FLS')
			) ?? false,
		[loggedUserAccount?.organizationBriefs]
	);

	const hasAllEventsPermissions =
		isAdminUserAccount ||
		hasProjectAdminOrRequesterRole ||
		isLiferayStaff ||
		hasFLSOrganizationAssociated;

	const navigate = useNavigate();

	const handleEditEvent = useCallback(
		(eventTicketId: number) => {
			if (eventTicketId) {
				navigate(
					`/${project?.accountKey}/business-events/${eventTicketId}`
				);
			}
		},
		[navigate, project?.accountKey]
	);

	const generateFilterQuery = (filters: IState) => {
		const queryParams = Object.entries(filters)
			.map(([key, {value}]) => {
				if (Array.isArray(value) && !!value.length) {
					return `(${value
						.map((item) => `${key} eq '${item}'`)
						.join(' or ')})`;
				}

				return '';
			})
			.filter(Boolean);

		if (filters.searchTerm?.trim()) {
			queryParams.push(`(contains(name, '${filters.searchTerm}'))`);
		}

		return queryParams.length ? `filter=${queryParams.join(' and ')}` : '';
	};

	const filterQuery = generateFilterQuery(filters);

	const handleFilterChange = (newFilterOptions: IFilterOption[]) => {
		setFilters((prevFilters) => {
			let updatedFilters: IFilterOption[] = prevFilters.selectedFilters
				? [...prevFilters.selectedFilters]
				: [];

			if (newFilterOptions && !!newFilterOptions.length) {
				newFilterOptions.forEach((newOption) => {
					updatedFilters = updatedFilters.filter(
						(filter) => filter.name !== newOption.name
					);
					updatedFilters.push(newOption);
				});
			}
			else {
				updatedFilters = updatedFilters.filter(
					(filter) =>
						filter.name !==
						(prevFilters.selectedFilters?.length &&
							prevFilters.selectedFilters[0].name)
				);
			}

			return {
				...prevFilters,
				selectedFilters: updatedFilters,
			};
		});
	};

	const handleSearchChange = (searchTerm: string) => {
		setFilters((prevFilters) => ({
			...prevFilters,
			searchTerm,
		}));
	};

	useEffect(() => {
		const fetchBusinessEvents = async () => {
			try {
				const businessEventsResponse =
					await getBusinessEvents(filterQuery);

				setBusinessEventsTickets(businessEventsResponse.items);
			}
			catch (error) {
				console.error('Error', error);
			}
		};

		fetchBusinessEvents();
	}, [filterQuery]);

	const rows = useMemo(() => {
		if (businessEventsTickets?.length > 0) {
			return businessEventsTickets.map((eventTicket) => {
				const userOptions = [
					{
						customOptionStyle: 'pr-5',
						label: i18n.translate('view-details'),
						onClick: () => {
							handleEditEvent(eventTicket?.id);
						},
					},
				];

				if (hasAllEventsPermissions) {
					userOptions.push(
						{
							customOptionStyle: 'pr-5',
							label: i18n.translate('edit-event'),
							onClick: () => {
								handleEditEvent(eventTicket?.id);
							},
						},
						{
							customOptionStyle: 'pr-5',
							label: i18n.translate('record-actual-go-live'),
							onClick: () => {},
						},
						{
							customOptionStyle: 'pr-5 be-cancel-event-option',
							label: i18n.translate('cancel-event'),
							onClick: () => {},
						}
					);
				}

				return {
					actions: (
						<div className="d-flex justify-content-center">
							<ButtonDropDown
								customDropDownButton={
									<ButtonWithIcon
										aria-label={i18n.translate(
											'manage-user-options'
										)}
										borderless
										className="text-neutral-5"
										onPointerEnterCapture={undefined}
										onPointerLeaveCapture={undefined}
										placeholder={undefined}
										symbol="ellipsis-v"
									/>
								}
								items={userOptions}
								label="Options"
							/>
						</div>
					),
					associatedTickets: (
						<div className="text-neutral-10">
							{eventTicket?.associatedTickets}
						</div>
					),
					details: (
						<div className="text-neutral-10">
							{eventTicket?.description}
						</div>
					),
					eventName: (
						<div>
							<div className="font-weight-semi-bold text-neutral-10">
								{eventTicket?.name}
							</div>

							<div className="be-subtitle text-neutral-7">
								{eventTicket?.eventType?.name}
							</div>
						</div>
					),
					status: (
						<div className="align-items-center d-flex">
							<div
								className={`align-items-center font-weight-semi-bold be-status be-status-${eventTicket?.eventStatus?.name.toLowerCase()} px-2 py-1`}
							>
								{eventTicket?.eventStatus?.name}
							</div>
						</div>
					),
					targetGoLiveDate: (
						<div>
							<div className="text-neutral-10">
								{getFormattedDate(
									eventTicket?.targetGoLiveDateTime,
									'day2DMonthSYearN'
								)}
							</div>

							<div className="be-subtitle text-neutral-7">
								{getFormattedTime(
									eventTicket?.targetGoLiveDateTime
								)}
							</div>
						</div>
					),
				};
			});
		}

		return [];
	}, [businessEventsTickets, hasAllEventsPermissions, handleEditEvent]);

	return (
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
					searchResultsCount={businessEventsTickets.length}
					searchTerm={filters.searchTerm || ''}
					selectedFilters={filters.selectedFilters || []}
				/>
			</div>

			<div>
				<Table columns={columns} rows={rows as unknown as IRow[]} />
			</div>
		</div>
	);
};

export default BusinessEvents;
