import ClayModal from '@clayui/modal';
import ListGroup from 'shared/components/list-group';
import React, {useEffect, useState} from 'react';
import type {Observer} from '@clayui/modal/lib/types';
import {get} from 'lodash';
import {sub} from 'shared/util/lang';

interface IFieldPreviewModalProps extends React.HTMLAttributes<HTMLDivElement> {
	dataSourceFn: () => Promise<any>;
	fieldName?: string;
	observer: Observer;
	sourceName: string;
}

const FieldPreviewModal: React.FC<IFieldPreviewModalProps> = ({
	className,
	dataSourceFn,
	fieldName,
	observer,
	sourceName,
	...otherProps
}) => {
	const [fieldData, setFieldData] = useState([]);

	useEffect(() => {
		getFieldData();
	}, []);

	const getFieldData = () =>
		dataSourceFn().then((fieldData) => {
			setFieldData(get(fieldData, [0, 'values'], []));
		});

	return (
		<ClayModal
			observer={observer}
			{...otherProps}
			className={className}
			size="lg"
		>
			<ClayModal.Header>
				{sub(Liferay.Language.get('field-preview-x'), [sourceName])}
			</ClayModal.Header>

			<ListGroup>
				<ListGroup.Item flex header>
					<ListGroup.ItemField>{fieldName}</ListGroup.ItemField>
				</ListGroup.Item>

				{fieldData.map((item, index) => (
					<ListGroup.Item key={index}>
						<ListGroup.ItemField>{item}</ListGroup.ItemField>
					</ListGroup.Item>
				))}
			</ListGroup>
		</ClayModal>
	);
};

export default FieldPreviewModal;
