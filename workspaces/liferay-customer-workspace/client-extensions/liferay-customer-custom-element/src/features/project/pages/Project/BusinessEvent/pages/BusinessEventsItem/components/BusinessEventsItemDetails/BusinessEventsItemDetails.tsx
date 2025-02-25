/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import i18n from '~/utils/I18n';

import './BusinessEventsItemDetails.css';
import {IBusinessEventTicket} from '../../../../BusinessEvents';

interface BusinessEventsItemDetailsProps {
	eventTicket: IBusinessEventTicket;
}

const BusinessEventsItemDetails = ({
	eventTicket,
}: BusinessEventsItemDetailsProps) => {
	return (
		<div className="event-detail-container">
			{eventTicket?.eventType?.name && (
				<div className="event-detail-item mb-4">
					<div className="event-detail-title mb-1 text-neutral-8">
						{i18n.translate('event-type')}
					</div>

					<div className="d-inline-block event-detail-value font-weight-semi-bold rounded text-neutral-9">
						{eventTicket?.eventType.name}
					</div>
				</div>
			)}

			{eventTicket?.currentLiferayVersion?.name && (
				<div className="event-detail-item mb-4">
					<div className="event-detail-title mb-1 text-neutral-8">
						{i18n.translate('current-version')}
					</div>

					<div className="d-inline-block event-detail-value font-weight-semi-bold rounded text-neutral-9">
						{eventTicket?.currentLiferayVersion.name}
					</div>
				</div>
			)}

			{eventTicket?.newLiferayVersion?.name && (
				<div className="event-detail-item mb-4">
					<div className="event-detail-title mb-1 text-neutral-8">
						{i18n.translate('new-version')}
					</div>

					<div className="d-inline-block event-detail-value font-weight-semi-bold rounded text-neutral-9">
						{eventTicket?.newLiferayVersion.name}
					</div>
				</div>
			)}

			{eventTicket?.targetGoLiveDateTime && (
				<div className="event-detail-item mb-4">
					<div className="event-detail-title mb-1 text-neutral-8">
						{i18n.translate('target-go-live-date')}
					</div>

					<div className="d-inline-block event-detail-value font-weight-semi-bold rounded text-neutral-9">
						{eventTicket?.targetGoLiveDateTime}
					</div>
				</div>
			)}

			{eventTicket?.associatedTickets && (
				<div className="event-detail-item mb-4">
					<div className="event-detail-title mb-1 text-neutral-8">
						{i18n.translate('associated-tickets')}
					</div>

					<div className="d-inline-block event-detail-value font-weight-semi-bold rounded text-neutral-9">
						{eventTicket?.associatedTickets}
					</div>
				</div>
			)}
		</div>
	);
};

export default BusinessEventsItemDetails;
