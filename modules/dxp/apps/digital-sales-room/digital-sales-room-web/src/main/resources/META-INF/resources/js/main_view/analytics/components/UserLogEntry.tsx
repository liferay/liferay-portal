/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import AccountSticker from '../../../common/components/AccountSticker';
import {ILogEntry, IUserLogsEntry} from '../types/index';
import LogEntry from './LogEntry';

const UserLogEntry: React.FC<IUserLogsEntry> = (userLogsEntry) => {
	const userName =
		userLogsEntry.userName || Liferay.Language.get('anonymous');

	return (
		<>
			<div className="d-flex inline-item pl-3">
				<AccountSticker
					className="sticker-user-icon"
					name={userName}
					shape="circle"
					size="lg"
				/>

				<span className="fw-600 ml-2">{userName}</span>
			</div>
			<ul className="pl-5 timeline">
				{userLogsEntry.logs.map(
					(logEntry: ILogEntry, index: number) => (
						<LogEntry key={index} {...logEntry} />
					)
				)}
			</ul>
		</>
	);
};

export default UserLogEntry;
