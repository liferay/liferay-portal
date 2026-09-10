import AccountDisplay from './AccountDisplay';
import BehaviorDisplay from './BehaviorDisplay';
import EventDisplay from './EventDisplay';
import IndividualDisplay from './IndividualDisplay';
import InterestDisplay from './InterestDisplay';
import OrganizationDisplay from './OrganizationDisplay';
import React from 'react';
import SearchTermDisplay from './SearchTermDisplay';
import SessionDisplay from './SessionDisplay';
import {getRemoteCriterionTypeByPropertyKey} from 'segment/segment-editor/dynamic/criterion-types/registry';
import {IDisplayComponentProps} from '../types';

const NON_REMOTE_DISPLAYS: Record<string, React.ComponentType<any>> = {
	account: AccountDisplay,
	event: EventDisplay,
	individual: IndividualDisplay,
	interest: InterestDisplay,
	organization: OrganizationDisplay,
	'search-term': SearchTermDisplay,
	session: SessionDisplay,
	web: BehaviorDisplay,
};

const DisplayComponent: React.FC<IDisplayComponentProps> = ({
	criterion,
	property,
	segmentType,
}) => {
	const Display = (getRemoteCriterionTypeByPropertyKey(property.propertyKey)
		?.DisplayComponent ??
		NON_REMOTE_DISPLAYS[property.propertyKey] ??
		IndividualDisplay) as React.FC<IDisplayComponentProps>;

	return (
		<Display
			criterion={criterion}
			property={property}
			segmentType={segmentType}
		/>
	);
};

export default DisplayComponent;
