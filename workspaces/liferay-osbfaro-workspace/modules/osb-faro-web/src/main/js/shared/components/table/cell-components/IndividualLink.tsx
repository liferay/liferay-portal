import NameCell from './Name';
import React from 'react';
import {Routes, toRoute} from 'shared/util/router';

interface IIndividualLinksProps {
	channelId?: string;
	className?: string;
	data: {
		id: string;
		individualDeleted: boolean;
		individualId: string;
		individualName: string;
		name: string;
		ownerId?: string;
	};
	disabled?: boolean;
	groupId: string;
}

const IndividualLinkCell: React.FC<IIndividualLinksProps> = ({
	channelId,
	className,
	data,
	disabled,
	groupId,
}) => {
	const id = data.individualId || data.ownerId || data.id;

	const name = data.name || data.individualName || '-';

	return (
		<NameCell
			className={className}
			data={{...data, id, name}}
			disabled={data.individualDeleted || disabled}
			routeFn={({data: {id}}: {data: {id: string}}) =>
				toRoute(Routes.CONTACTS_INDIVIDUAL, {
					channelId,
					groupId,
					id,
				})
			}
		/>
	);
};

export default IndividualLinkCell;
