import AddConditionButton from 'shared/components/conditions-panel/AddConditionButton';
import ConditionsSection from 'shared/components/conditions-panel/ConditionsSection';
import EventChip from './EventChip';
import EventDropdown from './EventDropdown';
import React, {useRef} from 'react';
import {Align} from '@clayui/drop-down';
import {Event} from 'event-analysis/utils/types';
import {useAttributes} from '../context/attributes';

interface IEventSectionProps {
	event?: Event;
	onEventChange: (event: Event | null) => void;
}

const EventSection: React.FC<IEventSectionProps> = ({event, onEventChange}) => {
	const {deleteAllAttributes} = useAttributes();

	const sectionRef = useRef<HTMLElement>(null);

	const handleEventChange = (event: Event | null): void => {
		onEventChange(event);

		deleteAllAttributes();
	};

	return (
		<ConditionsSection
			action={
				!event && (
					<EventDropdown
						alignmentPosition={Align.RightTop}
						onEventChange={handleEventChange}
						trigger={
							<AddConditionButton
								label={Liferay.Language.get('add-event')}
							/>
						}
					/>
				)
			}
			className="event-section-root"
			ref={sectionRef}
			title={Liferay.Language.get('analyze')}
		>
			{event && (
				<div className="event-container event-list mt-3">
					<EventChip
						event={event}
						onEventChange={(event) => {
							handleEventChange(event);

							setTimeout(() => sectionRef.current?.focus());
						}}
					/>
				</div>
			)}
		</ConditionsSection>
	);
};

export default EventSection;
