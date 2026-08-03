/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayModal from '@clayui/modal';
import {fetch} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

const DISPLAY_TYPES = {
	completed: 'success',
	declined: 'danger',
	expired: 'warning',
	sent: 'info',
	signed: 'success',
	voided: 'danger',
};

const STATUS_LABELS = {
	completed: Liferay.Language.get('completed'),
	declined: Liferay.Language.get('declined'),
	expired: Liferay.Language.get('expired'),
	sent: Liferay.Language.get('sent'),
	signed: Liferay.Language.get('signed'),
	voided: Liferay.Language.get('voided'),
};

const SEPARATOR = ` ${String.fromCharCode(183)} `;

function formatDate(time) {
	if (!time) {
		return '';
	}

	return new Date(time).toLocaleString();
}

function getActivities(detail) {
	const activities = [];

	detail.recipients.forEach((recipient) => {
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
				title: getStatusLabel(status),
				type: getDisplayType(status),
			});
		}
	});

	if (
		(detail.requestStatus === 'voided' ||
			detail.requestStatus === 'expired') &&
		detail.statusDate
	) {
		activities.push({
			detail: '',
			time: detail.statusDate,
			title: getStatusLabel(detail.requestStatus),
			type: getDisplayType(detail.requestStatus),
		});
	}

	activities.sort((a, b) => (a.time || 0) - (b.time || 0));

	activities.unshift({
		detail: detail.requesterName,
		time: detail.createDate,
		title: Liferay.Language.get('envelope-created'),
		type: 'info',
	});

	return activities;
}

function getDisplayType(status) {
	return DISPLAY_TYPES[status] || 'info';
}

function getStatusLabel(status) {
	return STATUS_LABELS[status] || status;
}

function getRecipientDate(recipient) {
	return recipient.statusDate || recipient.sentDate;
}

function getRecipientStatus(recipient) {
	if (recipient.requestRecipientStatus === 'completed') {
		return 'signed';
	}

	return recipient.requestRecipientStatus;
}

function StatusLabel({status}) {
	if (!status) {
		return null;
	}

	return (
		<span className={`label label-${getDisplayType(status)}`}>
			{getStatusLabel(status)}
		</span>
	);
}

function SignatureDetailsContent({detail}) {
	return (
		<div className="signature-details">
			<div className="bg-light border mb-4 p-3 rounded">
				<div className="small text-secondary text-uppercase">
					{Liferay.Language.get('status')}
				</div>

				<div className="mb-3">
					<StatusLabel status={detail.requestStatus} />
				</div>

				<div className="small text-secondary text-uppercase">
					{Liferay.Language.get('requester')}
				</div>

				<div className="font-weight-semi-bold">
					{detail.requesterName}
				</div>

				<div className="mb-3 text-secondary">
					{detail.requesterEmailAddress}
				</div>

				<div className="small text-secondary text-uppercase">
					{Liferay.Language.get('envelope-id')}
				</div>

				<div className={detail.expirationDate ? 'mb-3' : ''}>
					{detail.providerRequestId}
				</div>

				{detail.expirationDate ? (
					<>
						<div className="small text-secondary text-uppercase">
							{Liferay.Language.get('expiration-date')}
						</div>

						<div>{formatDate(detail.expirationDate)}</div>
					</>
				) : null}
			</div>

			<h5>{Liferay.Language.get('recipients')}</h5>

			<table className="mb-4 table table-list">
				<thead>
					<tr>
						<th>{Liferay.Language.get('name')}</th>

						<th>{Liferay.Language.get('email')}</th>

						<th>{Liferay.Language.get('status')}</th>

						<th>{Liferay.Language.get('date')}</th>
					</tr>
				</thead>

				<tbody>
					{detail.recipients.map((recipient, index) => (
						<tr key={index}>
							<td>{recipient.name}</td>

							<td>{recipient.emailAddress}</td>

							<td>
								<StatusLabel
									status={getRecipientStatus(recipient)}
								/>
							</td>

							<td>{formatDate(getRecipientDate(recipient))}</td>
						</tr>
					))}
				</tbody>
			</table>

			<h5>{Liferay.Language.get('activity')}</h5>

			<ul className="timeline">
				{getActivities(detail).map((activity, index) => (
					<li className="timeline-item" key={index}>
						<div className="panel panel-secondary">
							<div className="timeline-increment">
								<span
									className={`timeline-icon bg-${activity.type}`}
									style={{
										height: '1.25rem',
										minWidth: '1.25rem',
										width: '1.25rem',
									}}
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
		</div>
	);
}

export default function SignatureDetailsModal({fileEntryTitle, url}) {
	const [detail, setDetail] = useState(null);
	const [loading, setLoading] = useState(true);

	useEffect(() => {
		let mounted = true;

		fetch(url)
			.then((response) => response.json())
			.then((data) => {
				if (mounted) {
					setDetail(data);
					setLoading(false);
				}
			})
			.catch(() => {
				if (mounted) {
					setLoading(false);
				}
			});

		return () => {
			mounted = false;
		};
	}, [url]);

	return (
		<>
			<ClayModal.Header>
				{fileEntryTitle || Liferay.Language.get('signature-status')}
			</ClayModal.Header>

			<ClayModal.Body>
				{loading ? (
					<ClayLoadingIndicator />
				) : detail && detail.providerRequestId ? (
					<SignatureDetailsContent detail={detail} />
				) : (
					<div className="text-secondary">
						{Liferay.Language.get(
							'no-signature-request-was-found-for-this-document'
						)}
					</div>
				)}
			</ClayModal.Body>
		</>
	);
}
