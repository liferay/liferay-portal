/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayToggle} from '@clayui/form';
import PropTypes from 'prop-types';
import React, {useState} from 'react';

import SidebarPanel from '../../SidebarPanel';
import HelpIcon from '../shared-components/HelpIcon';
import TimerFields from './TimerFields';

const TimerDuration = ({
	duration: durationValue,
	durationScale: durationScaleValue,
	recurrence: recurrenceValue,
	recurrenceScale: recurrenceScaleValue,
	selectedItem,
	setTimerSections,
	timerIdentifier,
	timersIndex,
}) => {
	const [recurrence, setRecurrence] = useState(
		!!recurrenceValue && !!recurrenceScaleValue
	);

	const handleToggle = () => {
		if (recurrence) {
			setRecurrence(false);
			setTimerSections((previousSections) => {
				const updatedSections = [...previousSections];

				delete updatedSections[timersIndex].recurrence;
				delete updatedSections[timersIndex].recurrenceScale;

				return updatedSections;
			});
		}
		else {
			setRecurrence(true);
		}
	};

	return (
		<SidebarPanel panelTitle={Liferay.Language.get('duration')}>
			<TimerFields
				durationScaleValue={durationScaleValue}
				durationValue={durationValue}
				selectedItem={selectedItem}
				setTimerSections={setTimerSections}
				timerIdentifier={timerIdentifier}
				timersIndex={timersIndex}
			/>

			<div className="timers-duration-toggle">
				<ClayToggle
					label={Liferay.Language.get('recurrence')}
					onToggle={handleToggle}
					toggled={recurrence}
				/>

				<HelpIcon
					className="ml-2"
					message={Liferay.Language.get(
						'repeat-the-action-at-a-given-duration-until-the-workflow-task-is-completed'
					)}
				/>
			</div>

			{recurrence && (
				<TimerFields
					durationScaleValue={recurrenceScaleValue}
					durationValue={recurrenceValue}
					recurrence
					scaleHelpText={Liferay.Language.get('recurrence')}
					selectedItem={selectedItem}
					setTimerSections={setTimerSections}
					timerIdentifier={timerIdentifier}
					timersIndex={timersIndex}
				/>
			)}
		</SidebarPanel>
	);
};

TimerDuration.propTypes = {
	selectedItem: PropTypes.object.isRequired,
	setTimerSections: PropTypes.func.isRequired,
	timerIdentifier: PropTypes.string.isRequired,
	timersIndex: PropTypes.number.isRequired,
};

export default TimerDuration;
