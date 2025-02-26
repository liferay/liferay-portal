/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import i18n from '~/utils/I18n';
import {IBusinessEvent} from '~/utils/types';

import './BusinessEventsItemDetails.css';

interface IProps {
	businessEvent: IBusinessEvent;
}

const BusinessEventsItemDetails = ({businessEvent}: IProps) => {
	return (
		<div className="event-detail-container">
			{businessEvent?.eventType?.name && (
				<div className="event-detail-item mb-4">
					<div className="event-detail-title mb-1 text-neutral-8">
						{i18n.translate('event-type')}
					</div>

					<div className="d-inline-block event-detail-value font-weight-semi-bold rounded text-neutral-9">
						{businessEvent?.eventType.name}
					</div>
				</div>
			)}

			{businessEvent?.currentLiferayVersion?.name && (
				<div className="event-detail-item mb-4">
					<div className="event-detail-title mb-1 text-neutral-8">
						{i18n.translate('current-version')}
					</div>

					<div className="d-inline-block event-detail-value font-weight-semi-bold rounded text-neutral-9">
						{businessEvent?.currentLiferayVersion.name}
					</div>
				</div>
			)}

			{businessEvent?.newLiferayVersion?.name && (
				<div className="event-detail-item mb-4">
					<div className="event-detail-title mb-1 text-neutral-8">
						{i18n.translate('new-version')}
					</div>

					<div className="d-inline-block event-detail-value font-weight-semi-bold rounded text-neutral-9">
						{businessEvent?.newLiferayVersion.name}
					</div>
				</div>
			)}

			{businessEvent?.targetGoLiveDateTime && (
				<div className="event-detail-item mb-4">
					<div className="event-detail-title mb-1 text-neutral-8">
						{i18n.translate('target-go-live-date')}
					</div>

					<div className="d-inline-block event-detail-value font-weight-semi-bold rounded text-neutral-9">
						{businessEvent?.targetGoLiveDateTime?.toString()}
					</div>
				</div>
			)}

			{businessEvent?.associatedTickets && (
				<div className="event-detail-item mb-4">
					<div className="event-detail-title mb-1 text-neutral-8">
						{i18n.translate('associated-tickets')}
					</div>

					<div className="d-inline-block event-detail-value font-weight-semi-bold rounded text-neutral-9">
						{businessEvent?.associatedTickets}
					</div>
				</div>
			)}
		</div>
	);
};

export default BusinessEventsItemDetails;
