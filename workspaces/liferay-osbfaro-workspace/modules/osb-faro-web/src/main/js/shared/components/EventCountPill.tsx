import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import React, {FC} from 'react';

const EventCountPill: FC<{symbol?: string; totalEvents?: number}> = ({
	symbol = 'click',
	totalEvents,
}) =>
	totalEvents === undefined ? null : (
		<ClayLabel
			className="event-count-pill flex-shrink-0 m-0"
			displayType="secondary"
			withClose={false}
		>
			<ClayLabel.ItemBefore>
				<ClayIcon className="text-secondary" symbol={symbol} />
			</ClayLabel.ItemBefore>

			<ClayLabel.ItemExpand>{totalEvents}</ClayLabel.ItemExpand>
		</ClayLabel>
	);

export default EventCountPill;
