import ClayLink from '@clayui/link';
import React from 'react';

interface IActivityChartEmptyStateProps {
	description?: string;
	linkHref: string;
	linkLabel: string;
	title: string;
}

/**
 * Overlay shown on top of the activity-stream chart when it has no data. The
 * account and individual cards render the same chrome, differing only in the
 * title, documentation link, and label. The description defaults to the shared
 * "check back later" copy.
 */
const ActivityChartEmptyState: React.FC<IActivityChartEmptyStateProps> = ({
	description = Liferay.Language.get(
		'check-back-later-to-verify-if-data-has-been-received-from-your-data-sources'
	),
	linkHref,
	linkLabel,
	title,
}) => (
	<div
		className="position-absolute d-flex flex-column align-items-center justify-content-center text-center px-3"
		style={{
			inset: 0,
			pointerEvents: 'none',
		}}
	>
		<div
			className="font-weight-semi-bold mb-2"
			style={{pointerEvents: 'auto'}}
		>
			{title}
		</div>

		<div className="text-secondary mb-2" style={{pointerEvents: 'auto'}}>
			{description}
		</div>

		<ClayLink
			decoration="underline"
			href={linkHref}
			style={{pointerEvents: 'auto'}}
			target="_blank"
		>
			{linkLabel}
		</ClayLink>
	</div>
);

export default ActivityChartEmptyState;
