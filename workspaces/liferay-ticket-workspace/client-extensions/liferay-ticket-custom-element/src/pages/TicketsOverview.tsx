/* eslint-disable quote-props */

/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Option, Picker} from '@clayui/core';
import {ClayInput} from '@clayui/form';
import ClayLayout from '@clayui/layout';
import {ClayPaginationBarWithBasicItems} from '@clayui/pagination-bar';
import React, {useState} from 'react';
import {QueryClient, useMutation, useQueryClient} from 'react-query';

import {RecentActivity} from '../components/RecentActivity';
import {TicketGrid} from '../components/TicketGrid';
import useDebounce from '../hooks/useDebounce';
import {useRecentTickets} from '../hooks/useRecentTickets';
import {useTickets} from '../hooks/useTickets';
import {Liferay} from '../services/liferay';
import {generateNewTicket} from '../services/tickets';
import {Filter} from '../types';
import {INITIAL_FILTER_STATE} from '../utils/constants';

const DEBOUNCE_DELAY = 300;

const FILTERS: Filter[] = [
	{
		field: '',
		label: 'No Filter',
		value: '',
	},
	{
		field: 'ticketStatus',
		label: 'Open Issues',
		value: 'open',
	},
	{
		field: 'ticketStatus',
		label: 'Queued Issues',
		value: 'queued',
	},
	{
		field: 'priority',
		label: 'Major Priority Issues',
		value: 'major',
	},
	{
		field: 'resolution',
		label: 'Unresolved Issues',
		value: 'unresolved',
	},
];

const TicketsOverview = () => {
	const queryClient: QueryClient = useQueryClient();

	const [filter, setFilter] = useState(INITIAL_FILTER_STATE);
	const [page, setPage] = useState(1);
	const [pageSize, setPageSize] = useState(20);
	const [search, setSearch] = useState<string>('');

	const debouncedSearch = useDebounce(search, DEBOUNCE_DELAY);
	const debouncedPage = useDebounce(page, DEBOUNCE_DELAY);

	const recentTickets = useRecentTickets();

	const {rows: tickets, totalCount} = useTickets({
		filter,
		page: debouncedPage,
		pageSize,
		search: debouncedSearch,
	});

	const generateNewTicketMutation = useMutation({
		mutationFn: generateNewTicket,
		onSuccess: () => {
			queryClient.invalidateQueries();

			setPage(1);

			Liferay.Util.openToast({
				message: 'A new ticket was added!',
				type: 'success',
			});
		},
	});

	return (
		<>
			<ClayLayout.ContentRow className="bg-neutral-1 justify-content-between mb-3 p-3 rounded">
				<ClayLayout.ContentCol className="text-11">
					Your Tickets
				</ClayLayout.ContentCol>
				<ClayLayout.ContentCol float="end">
					<ClayButton
						displayType="primary"
						onClick={() => {
							generateNewTicketMutation.mutate();
						}}
					>
						Generate a New Ticket
					</ClayButton>
				</ClayLayout.ContentCol>
			</ClayLayout.ContentRow>
			<ClayLayout.ContentRow className="mb-3" padded>
				<ClayLayout.ContentCol expand>
					<ClayInput
						onChange={(event) => {
							setSearch(event.target.value);

							setPage(1);
						}}
						placeholder="Search Tickets"
						type="text"
					/>
				</ClayLayout.ContentCol>
				<ClayLayout.ContentCol>
					<Picker
						aria-label="Select a Filter"
						items={FILTERS}
						messages={{
							itemDescribedby: Liferay.Language.get(
								'you-are-currently-on-a-text-element,-inside-of-a-list-box'
							),
							itemSelected: Liferay.Language.get('x-selected'),
							scrollToBottomAriaLabel:
								Liferay.Language.get('scroll-to-bottom'),
							scrollToTopAriaLabel:
								Liferay.Language.get('scroll-to-top'),
						}}
						onSelectionChange={(selectedFilterValue: any) => {
							setPage(1);

							const selectedFilter = FILTERS.find(
								(filter) => filter.value === selectedFilterValue
							);

							if (selectedFilter) {
								setFilter(selectedFilter);
							}
						}}
						placeholder="Select a Filter"
					>
						{(item) => (
							<Option key={item.value}>{item.label}</Option>
						)}
					</Picker>
				</ClayLayout.ContentCol>
			</ClayLayout.ContentRow>
			<ClayLayout.ContentRow padded>
				<TicketGrid tickets={tickets} />
			</ClayLayout.ContentRow>
			<ClayLayout.ContentRow className="mt-3" padded>
				<ClayPaginationBarWithBasicItems
					active={page}
					activeDelta={pageSize}
					ellipsisBuffer={3}
					onActiveChange={setPage}
					onDeltaChange={setPageSize}
					totalItems={totalCount}
				/>
			</ClayLayout.ContentRow>
			<ClayLayout.ContentRow padded>
				<RecentActivity tickets={recentTickets} />
			</ClayLayout.ContentRow>
		</>
	);
};

export default TicketsOverview;
