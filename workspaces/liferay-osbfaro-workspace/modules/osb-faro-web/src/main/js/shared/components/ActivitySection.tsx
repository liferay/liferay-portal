import React from 'react';
import {Text} from '@clayui/core';

interface IActivitySectionProps {
	children: React.ReactNode;
	label: string;
}

const ActivitySection: React.FC<IActivitySectionProps> = ({
	children,
	label,
}) => (
	<div className="activity-section">
		<div className="activity-section-header">
			<Text color="secondary" size={3} weight="semi-bold">
				{label}
			</Text>
		</div>

		{children}
	</div>
);

export default ActivitySection;
