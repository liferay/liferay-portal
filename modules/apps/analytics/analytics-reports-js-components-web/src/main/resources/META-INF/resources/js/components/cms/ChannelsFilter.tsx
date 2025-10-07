/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useContext, useEffect, useState} from 'react';

import {Context} from '../../Context';
import ApiHelper from '../../apis/ApiHelper';
import {buildQueryString} from '../../utils/buildQueryString';
import Filter from '../Filter';

const initialChannel = {
	label: Liferay.Language.get('all-channels'),
	value: '',
};

const fetchChannels = async (keywords: string = '') => {
	const queryParams = buildQueryString({
		keywords,
	});
	const endpoint = `/o/analytics-cms-rest/v1.0/channels${queryParams}`;

	const {data, error} = await ApiHelper.get<{
		items: {groupId: string; name: string}[];
	}>(endpoint);

	if (data) {
		return data.items.map(({groupId, name}) => ({
			label: name,
			value: String(groupId),
		}));
	}

	if (error) {
		console.error(error);
	}

	return [];
};

const ChannelsFilter = () => {
	const {changeChannelFilter, filters} = useContext(Context);
	const [channels, setChannels] = useState([initialChannel]);
	const [loading, setLoading] = useState<boolean>(true);

	const triggerLabel =
		channels.find((channel) => channel.value === filters.channel)?.label ||
		Liferay.Language.get('all-channels');

	useEffect(() => {
		const fetchInitialData = async () => {
			setLoading(true);

			try {
				const channels = await fetchChannels();

				setChannels([initialChannel, ...channels]);
			}
			catch (error) {
				console.error('Failed to fetch channels:', error);
			}
			finally {
				setLoading(false);
			}
		};

		fetchInitialData();
	}, []);

	return (
		<Filter
			active={filters.channel}
			className="mr-3"
			filterByValue="channels"
			icon="sites"
			items={channels}
			loading={loading}
			onSearch={async (value) => {
				setLoading(true);

				try {
					const channels = await fetchChannels(value);

					setChannels(
						value ? channels : [initialChannel, ...channels]
					);
				}
				catch (error) {
					console.error('Failed to search channels:', error);
				}
				finally {
					setLoading(false);
				}
			}}
			onSelectItem={(item) => changeChannelFilter(item.value)}
			title={Liferay.Language.get('filter-by-channels')}
			triggerLabel={triggerLabel}
		/>
	);
};

export {ChannelsFilter};
