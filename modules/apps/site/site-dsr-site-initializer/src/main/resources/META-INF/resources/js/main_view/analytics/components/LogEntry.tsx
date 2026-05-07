/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import React from 'react';

import {ILogEntry} from '../types/index';

const LogEntry: React.FC<ILogEntry> = (logEntry) => {
	return (
		<li className="timeline-item" key={logEntry.createDate}>
			<div className="panel">
				<div
					className={`sticker sticker-circle timeline-increment timeline-increment-${logEntry.category}`}
				>
					<span
						className={`timeline-increment-icon timeline-increment-icon-${logEntry.category}`}
					>
						<ClayIcon className="log-icon" symbol={logEntry.icon} />
					</span>
				</div>

				<div className="panel-body pl-0">
					<div className="log-time text-secondary">
						{logEntry.time}
					</div>

					<div className="fw-600 log-label">{logEntry.label}</div>

					<div className="log-title">{logEntry.title}</div>

					{logEntry.description && (
						<div className="log-description px-2 py-1">
							{logEntry.description}
						</div>
					)}
				</div>
			</div>
		</li>
	);
};

export default LogEntry;
