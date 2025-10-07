/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayList from '@clayui/list';
import classNames from 'classnames';
import PropTypes from 'prop-types';
import React from 'react';

import {IFrontendDataSetContext} from '../../FrontendDataSetContext';

function TimelineEntry({
	date,
	description,
	title,
}: {
	date: string;
	description: string;
	title: string;
}) {
	return (
		<li className="timeline-item">
			<div className="panel panel-secondary">
				<div className="timeline-increment">
					<span className="timeline-icon"></span>
				</div>

				<div className="panel-body">
					<div className="mb-2 row">
						<div className="col">
							<div className="h4 mb-0">{title}</div>
						</div>

						<div className="col-auto">{description}</div>
					</div>

					<small>{date}</small>
				</div>
			</div>
		</li>
	);
}

TimelineEntry.propTypes = {
	date: PropTypes.string.isRequired,
	description: PropTypes.string.isRequired,
	title: PropTypes.string.isRequired,
};

TimelineEntry.defaultProps = {};

function Timeline({
	frontendDataSetContext,
	items,
}: {
	frontendDataSetContext: IFrontendDataSetContext;
	items: any[];
}) {
	if (!items?.length) {
		return null;
	}

	return (
		<ClayList className={classNames('mb-0', 'timeline')}>
			{items.map((item: any, i: number) => (
				<TimelineEntry
					key={i}
					{...item}
					borderBottom={i !== items.length - 1}
					frontendDataSetContext={frontendDataSetContext}
				/>
			))}
		</ClayList>
	);
}

export default Timeline;
