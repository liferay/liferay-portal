/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayInput} from '@clayui/form';
import ClayLink from '@clayui/link';
import ClayPopover from '@clayui/popover';
import React, {useEffect, useState} from 'react';

function compareTicket(a, b) {
	const aParts = a.split('-');
	const bParts = b.split('-');

	if (aParts[0] !== bParts[0]) {
		return aParts[0] > bParts[0] ? 1 : -1;
	}

	if (aParts.length === 1 || bParts.length === 1) {
		return bParts.length - aParts.length;
	}

	return parseInt(aParts[1], 10) - parseInt(bParts[1], 10);
}

function getTicketLink(ticket) {
	if (ticket.toUpperCase() !== ticket) {
		return ticket;
	}

	return (
		<ClayLink
			href={`https://liferay.atlassian.net/browse/${ticket}`}
			target="_blank"
		>
			{ticket}
		</ClayLink>
	);
}

function getTicketLinks(text) {
	if (!text?.trim()) {
		return null;
	}

	const tickets = text
		.split(',')
		.map((t) => t.trim())
		.filter(Boolean)
		.sort(compareTicket);

	return tickets.map((ticket, index) => (
		<React.Fragment key={ticket}>
			{index > 0 && ', '}

			{getTicketLink(ticket)}
		</React.Fragment>
	));
}

export default function PopoverTooltip({label, name, namespace, tickets = ''}) {
	const [showPopover, setShowPopover] = useState(false);
	const [value, setValue] = useState(tickets);

	useEffect(() => {
		if (!showPopover) {
			return;
		}

		const handleKeyDown = (event) => {
			if (event.key === 'Escape') {
				setShowPopover(false);
			}
		};

		window.addEventListener('keydown', handleKeyDown);

		return () => window.removeEventListener('keydown', handleKeyDown);
	}, [showPopover]);

	return (
		<ClayPopover
			alignPosition="top"
			header="JIRA Links"
			id={`${namespace}${name}_popover`}
			onShowChange={setShowPopover}
			role="tooltip"
			show={showPopover}
			style={{width: 256}}
			trigger={
				<div className="form-group">
					<label htmlFor={`${namespace}${name}`}>{label}</label>

					<ClayInput
						component="textarea"
						id={`${namespace}${name}`}
						name={`${namespace}${name}`}
						onChange={(event) => setValue(event.target.value)}
						onClick={() => {
							setShowPopover((show) => !show);
						}}
						type="text"
						value={value}
					/>
				</div>
			}
		>
			{getTicketLinks(value)}
		</ClayPopover>
	);
}
