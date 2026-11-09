import ClayLabel from '@clayui/label';
import ClayTable from '@clayui/table';
import React from 'react';
import {uniq} from 'lodash';
import {IUtmParameterRow} from 'campaigns/utils/types';

interface IUtmParametersTableProps {
	rows: IUtmParameterRow[];
}

const UtmParametersTable: React.FC<IUtmParametersTableProps> = ({rows}) => (
	<ClayTable className="table-sm">
		<ClayTable.Head>
			<ClayTable.Row>
				<ClayTable.Cell headingCell>
					{Liferay.Language.get('parameter')}
				</ClayTable.Cell>

				<ClayTable.Cell headingCell>
					{Liferay.Language.get('value')}
				</ClayTable.Cell>
			</ClayTable.Row>
		</ClayTable.Head>

		<ClayTable.Body>
			{rows.map(({name, values: allValues}) => {
				const values = uniq(allValues);

				return (
					<ClayTable.Row key={name}>
						<ClayTable.Cell className="text-dark" headingCell>
							{name}
						</ClayTable.Cell>

						<ClayTable.Cell>
							{values.length
								? values.map((value) => (
										<ClayLabel
											displayType="secondary"
											key={value}
										>
											{value}
										</ClayLabel>
									))
								: '-'}
						</ClayTable.Cell>
					</ClayTable.Row>
				);
			})}
		</ClayTable.Body>
	</ClayTable>
);

export default UtmParametersTable;
