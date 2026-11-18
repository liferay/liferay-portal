import Card from 'shared/components/Card';
import Panel from '@clayui/panel';
import React from 'react';
import UtmParametersEmptyState from './UtmParametersEmptyState';
import UtmParametersTable from './UtmParametersTable';
import {ICustomUtmParameter, UtmParameters} from 'campaigns/utils/types';
import {UTM_PARAMETERS} from 'campaigns/utils/constants';

interface IUtmParametersCardProps {
	customParameters?: ICustomUtmParameter[];
	utmParameters: UtmParameters;
}

const UtmParametersCard: React.FC<IUtmParametersCardProps> = ({
	customParameters = [],
	utmParameters,
}) => {
	const rows = [
		...UTM_PARAMETERS.map((parameter) => ({
			name: parameter as string,
			values: utmParameters[parameter] ?? [],
		})),
		...customParameters,
	];

	const isEmpty = rows.every(({values}) => !values.length);

	return (
		<Card>
			<Panel
				className="card-root mb-0"
				collapsable
				collapseHeaderClassNames="p-3"
				displayTitle={
					<Panel.Title className="card-title">
						{Liferay.Language.get('utm-parameters')}
					</Panel.Title>
				}
			>
				<Panel.Body className="pb-3 pt-2 px-3">
					{isEmpty ? (
						<UtmParametersEmptyState />
					) : (
						<UtmParametersTable rows={rows} />
					)}
				</Panel.Body>
			</Panel>
		</Card>
	);
};

export default UtmParametersCard;
