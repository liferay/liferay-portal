/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import useAnalyticsQuery from '../../../common/hooks/useAnalyticsQuery';

import './../../../../css/components/ActivityLog.scss';
import {
	IActivityLogEntry,
	ILogEntry,
	IUserLogsEntry,
	TActivityLog,
} from '../types';
import AnalyticsFrame from './AnalyticsFrame';
import Loader from './Loader';
import UserLogEntry from './UserLogEntry';

export const TYPES = [
	{
		category: 'comment',
		icon: 'comments',
		key: 'commentPosted',
		label: Liferay.Language.get('commented'),
	},
	{
		category: 'download',
		icon: 'download',
		key: 'documentDownloaded',
		label: sub(
			Liferay.Language.get('downloaded-a-x'),
			Liferay.Language.get('document')
		),
	},
	{
		category: 'view',
		icon: 'view',
		key: 'documentPreviewed',
		label: sub(
			Liferay.Language.get('viewed-a-x'),
			Liferay.Language.get('document')
		),
	},
	{
		category: 'upload',
		icon: 'upload',
		key: 'documentUploaded',
		label: sub(
			Liferay.Language.get('uploaded-a-x'),
			Liferay.Language.get('document')
		),
	},
	{
		category: 'view',
		icon: 'view',
		key: 'pageViewed',
		label: sub(
			Liferay.Language.get('viewed-a-x'),
			Liferay.Language.get('page')
		),
	},
];

interface IUserSessionEvent {
	assetTitle: string;
	createDate: string;
	emailAddressHashed: string;
	name: string;
	properties?: [{name: string; value: string}];
}

interface IUserSessionsPage {
	totalEvents?: number;
	userSessions?: Array<{
		userName?: string;
		userSessionEvents?: IUserSessionEvent[];
	}>;
}

const toActivityLogEntries = (
	response: IUserSessionsPage
): IActivityLogEntry[] => {
	const userSessions = response?.userSessions ?? [];

	return userSessions.flatMap((userSession) =>
		(userSession.userSessionEvents ?? []).map((event) => ({
			createDate: Date.parse(event.createDate),
			description: event.properties?.find(({name}) => name === 'comment')
				?.value,
			title: event.assetTitle,
			type: event.name,
			userName: userSession.userName,
		}))
	);
};

const formatData = (data: IActivityLogEntry[]) => {
	return data.reduce((activityLog: TActivityLog, item: IActivityLogEntry) => {
		const date = new Date(item.createDate);

		const dateKey = date.toISOString().split('T')[0];

		const timeString = date.toLocaleTimeString(
			Liferay.ThemeDisplay.getBCP47LanguageId(),
			{
				hour: 'numeric',
				hour12: true,
				minute: '2-digit',
			}
		);

		if (!activityLog[dateKey]) {
			activityLog[dateKey] = [];
		}

		const type = TYPES.find((type) => type.key === item.type);

		const logEntry: ILogEntry = {
			...item,
			category: type ? type.category : '',
			icon: type ? type.icon : '',
			label: type ? type.label : item.label,
			time: timeString,
		};

		const dayGroup = activityLog[dateKey];

		const lastUserBlock = dayGroup[dayGroup.length - 1];

		if (lastUserBlock && lastUserBlock.userName === item.userName) {
			lastUserBlock.logs.push(logEntry);
		}
		else {
			dayGroup.push({
				logs: [logEntry],
				userName: item.userName,
			});
		}

		return activityLog;
	}, {});
};

function ActivityLog({isAnalyticsEnabled}: {isAnalyticsEnabled: boolean}) {
	const [data, setData] = useState<TActivityLog>({});
	const [element, setElement] = useState<HTMLElement | null>(null);

	const {isLoading, response} = useAnalyticsQuery({
		element,
		query: {paths: [{key: 'userSessions', path: '/user-sessions'}]},
		settings: {isAnalyticsEnabled},
		variables: {
			entityType: 'INDIVIDUAL',
			keywords: '',
			page: 0,
			rangeEnd: null,
			rangeStart: null,
			size: 20,
		},
	});

	useEffect(() => {
		if (response) {
			setData(formatData(toActivityLogEntries(response.userSessions)));
		}

		return () => {};
	}, [response]);

	return (
		<AnalyticsFrame
			icon="box-container"
			title={Liferay.Language.get('activity-log')}
		>
			<div ref={setElement}>
				{isAnalyticsEnabled ? (
					isLoading ? (
						<Loader />
					) : !Object.keys(data).length ? (
						<p className="mt-3 text-center text-muted">
							{Liferay.Language.get('no-data-available')}
						</p>
					) : (
						Object.entries(data).map(
							([date, userLogs]: [string, IUserLogsEntry[]]) => (
								<>
									<div className="activity-logs-date fw-600 mb-3 px-3 py-2 text-secondary">
										{date}
									</div>

									{userLogs.map(
										(
											userLogsEntry: IUserLogsEntry,
											index: number
										) => (
											<UserLogEntry
												key={index}
												{...userLogsEntry}
											/>
										)
									)}
								</>
							)
						)
					)
				) : (
					<div className="dsr-analytics-empty-message">
						<p className="mb-0 text-center text-muted">
							{Liferay.Language.get(
								'analytics-cloud-is-not-configured'
							)}
						</p>
					</div>
				)}
			</div>
		</AnalyticsFrame>
	);
}

export default ActivityLog;
