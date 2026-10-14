/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import {fetch} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useEffect, useState} from 'react';

import DSRequestStatusLabel, {
	getDSRequestStatusDisplayType,
	getDSRequestStatusLabel,
} from './DSRequestStatusLabel';

import './ds_request_details.scss';

const SEPARATOR = ` ${String.fromCharCode(183)} `;

function DSRequestDetailsContent({details}) {
	const ordered =
		new Set(details.recipients.map(({signingOrder}) => signingOrder)).size >
		1;

	return (
		<div className="ds-request-details">
			<div className="bg-light border mb-4 p-3 rounded">
				<div className="small text-secondary text-uppercase">
					{Liferay.Language.get('status')}
				</div>

				<div className="mb-3">
					<DSRequestStatusLabel status={details.status} />
				</div>

				<div className="small text-secondary text-uppercase">
					{Liferay.Language.get('sender')}
				</div>

				<div className="font-weight-semi-bold mb-3">
					{details.userName}
				</div>

				<div className="small text-secondary text-uppercase">
					{Liferay.Language.get('envelope-id')}
				</div>

				<div className={details.expirationDate ? 'mb-3' : ''}>
					{details.externalReferenceCode}
				</div>

				{details.expirationDate ? (
					<>
						<div className="small text-secondary text-uppercase">
							{Liferay.Language.get('expiration-date')}
						</div>

						<div>{formatDate(details.expirationDate)}</div>
					</>
				) : null}
			</div>

			<h5>{Liferay.Language.get('recipients')}</h5>

			<table className="mb-4 table table-list">
				<thead>
					<tr>
						{ordered && <th>{Liferay.Language.get('order')}</th>}

						<th>{Liferay.Language.get('name')}</th>

						<th>{Liferay.Language.get('email')}</th>

						<th>{Liferay.Language.get('status')}</th>

						<th>{Liferay.Language.get('date')}</th>
					</tr>
				</thead>

				<tbody>
					{details.recipients.map((recipient, index) => (
						<tr key={index}>
							{ordered && <td>{recipient.signingOrder}</td>}

							<td>{recipient.name}</td>

							<td>{recipient.emailAddress}</td>

							<td>
								<DSRequestStatusLabel
									status={getRecipientStatus(recipient)}
								/>
							</td>

							<td>
								{formatDate(
									recipient.statusDate || recipient.sentDate
								)}
							</td>
						</tr>
					))}
				</tbody>
			</table>

			<h5>{Liferay.Language.get('activity')}</h5>

			<ul className="timeline">
				{getActivities(details).map((activity, index) => (
					<li className="timeline-item" key={index}>
						<div className="panel panel-secondary">
							<div className="timeline-increment">
								<span
									className={`timeline-icon bg-${activity.type}`}
								/>
							</div>

							<div className="panel-body">
								<div className="font-weight-semi-bold">
									{activity.title}
								</div>

								<div className="text-secondary">
									{[
										activity.detail,
										formatDate(activity.time),
									]
										.filter(Boolean)
										.join(SEPARATOR)}
								</div>
							</div>
						</div>
					</li>
				))}
			</ul>

			{details.history?.length ? (
				<>
					<h5>{Liferay.Language.get('history')}</h5>

					<table className="table table-list">
						<thead>
							<tr>
								<th>{Liferay.Language.get('envelope-id')}</th>

								<th>{Liferay.Language.get('status')}</th>

								<th>{Liferay.Language.get('create-date')}</th>

								<th>{Liferay.Language.get('date')}</th>
							</tr>
						</thead>

						<tbody>
							{details.history.map((historyEntry) => (
								<tr key={historyEntry.externalReferenceCode}>
									<td>
										{historyEntry.externalReferenceCode}
									</td>

									<td>
										<DSRequestStatusLabel
											status={historyEntry.status}
										/>
									</td>

									<td>
										{formatDate(historyEntry.createDate)}
									</td>

									<td>
										{formatDate(historyEntry.statusDate)}
									</td>
								</tr>
							))}
						</tbody>
					</table>
				</>
			) : null}
		</div>
	);
}

DSRequestDetailsContent.propTypes = {
	details: PropTypes.object.isRequired,
};

function formatDate(time) {
	if (!time) {
		return '';
	}

	return new Date(time).toLocaleString();
}

function getActivities(details) {
	const activities = [];

	details.recipients.forEach((recipient) => {
		if (recipient.sentDate) {
			activities.push({
				detail: recipient.name,
				time: recipient.sentDate,
				title: Liferay.Language.get('sent'),
				type: 'info',
			});
		}

		if (recipient.statusDate) {
			const status = getRecipientStatus(recipient);

			activities.push({
				detail: recipient.name,
				time: recipient.statusDate,
				title: getDSRequestStatusLabel(status),
				type: getDSRequestStatusDisplayType(status),
			});
		}
	});

	if (
		(details.status === 'expired' || details.status === 'voided') &&
		details.statusDate
	) {
		activities.push({
			detail: '',
			time: details.statusDate,
			title: getDSRequestStatusLabel(details.status),
			type: getDSRequestStatusDisplayType(details.status),
		});
	}

	activities.sort((a, b) => (a.time || 0) - (b.time || 0));

	return activities;
}

function getRecipientStatus(recipient) {
	if (recipient.status === 'completed') {
		return 'signed';
	}

	return recipient.status;
}

export default function DSRequestDetails({url}) {
	const [details, setDetails] = useState(null);
	const [error, setError] = useState(false);
	const [loading, setLoading] = useState(true);

	useEffect(() => {
		let mounted = true;

		fetch(url)
			.then((response) => {
				if (!response.ok) {
					throw new Error(response.statusText);
				}

				return response.json();
			})
			.then((data) => {
				if (mounted) {
					setDetails(data);
					setLoading(false);
				}
			})
			.catch(() => {
				if (mounted) {
					setError(true);
					setLoading(false);
				}
			});

		return () => {
			mounted = false;
		};
	}, [url]);

	if (loading) {
		return <ClayLoadingIndicator />;
	}

	if (error) {
		return (
			<div className="text-danger">
				{Liferay.Language.get('an-unexpected-error-occurred')}
			</div>
		);
	}

	if (details && details.externalReferenceCode) {
		return <DSRequestDetailsContent details={details} />;
	}

	return (
		<div className="text-secondary">
			{Liferay.Language.get(
				'no-signature-request-was-found-for-this-document'
			)}
		</div>
	);
}

DSRequestDetails.propTypes = {
	url: PropTypes.string.isRequired,
};
