import ClayLink from '@clayui/link';
import React from 'react';
import {Text} from '@clayui/core';

interface IActivitySectionEmptyStateProps {
	description?: string;
	linkHref: string;
	linkLabel: string;
	title?: string;
}

/**
 * Shown inside a Day-Level or Timed Activity card that has nothing for its
 * day. The two cards fill independently, so one can carry rows while the other
 * carries this. Copy stays about the period rather than naming a data source,
 * since a card is empty whether none is connected or none reported that day.
 */
const ActivitySectionEmptyState: React.FC<IActivitySectionEmptyStateProps> = ({
	description = Liferay.Language.get(
		'check-back-later-to-verify-if-data-has-been-received-from-your-data-sources,-or-you-can-try-a-different-date-range'
	),
	linkHref,
	linkLabel,
	title = Liferay.Language.get('there-is-no-activity-on-the-selected-period'),
}) => (
	<div className="activity-section-empty-state d-flex flex-column align-items-center justify-content-center text-center">
		<div className="font-weight-semi-bold">
			<Text size={4} weight="semi-bold">
				{title}
			</Text>
		</div>

		<div className="description">
			<Text color="secondary" size={3}>
				{description}
			</Text>
		</div>

		<ClayLink decoration="underline" href={linkHref} target="_blank">
			{linkLabel}
		</ClayLink>
	</div>
);

export default ActivitySectionEmptyState;
