import ClayModal from '@clayui/modal';
import React from 'react';
import type {Observer} from '@clayui/modal/lib/types';
import Table from 'shared/components/table';

interface IIndividualAttributesModalInterface
	extends React.HTMLAttributes<HTMLElement> {
	dataSources: {
		dataSourceFieldName: string;
		dataSourceName: string;
	}[];
	fieldName: string;
	observer: Observer;
}

const IndividualAttributesModal: React.FC<
	IIndividualAttributesModalInterface
> = ({dataSources, fieldName, observer}) => (
	<ClayModal observer={observer}>
		<ClayModal.Header>{fieldName}</ClayModal.Header>

		<ClayModal.Body>
			<div className="h5">{Liferay.Language.get('data-sources')}</div>

			<Table
				columns={[
					{
						accessor: 'dataSourceName',
						label: Liferay.Language.get('source'),
						sortable: false,
					},
					{
						accessor: 'dataSourceFieldName',
						label: Liferay.Language.get('attribute[noun]'),
						sortable: false,
					},
				]}
				items={dataSources}
				rowIdentifier="dataSourceName"
			/>
		</ClayModal.Body>
	</ClayModal>
);

export default IndividualAttributesModal;
