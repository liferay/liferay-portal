import getCN from 'classnames';
import React from 'react';
import {IndividualTypes} from 'segment/segment-editor/dynamic/utils/constants';

interface IMembershipChanges {
	className?: string;
	data: {
		profileType: IndividualTypes;
	};
}

const INDIVIDUAL_TYPE_LABEL_MAP: Record<IndividualTypes, string> = {
	ANONYMOUS: Liferay.Language.get('anonymous'),
	KNOWN: Liferay.Language.get('known'),
};

const IndividualType: React.FC<IMembershipChanges> = ({
	className,
	data: {profileType},
}) => (
	<td className={getCN('text-capitalize', className)}>
		{INDIVIDUAL_TYPE_LABEL_MAP[profileType]}
	</td>
);

export default IndividualType;
