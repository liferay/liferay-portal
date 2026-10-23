import ClayModal from '@clayui/modal';
import * as API from 'shared/api';
import ClayTable from '@clayui/table';
import React, {useState} from 'react';
import type {Observer} from '@clayui/modal/lib/types';
import {noop} from 'lodash';
import {sub} from 'shared/util/lang';
import {useEffect} from 'react';

const ROW_DELTA = 10;

interface ICSVPreviewModalProps extends React.HTMLAttributes<HTMLElement> {
	fieldName?: string;
	fileVersionId?: string | number;
	groupId: string;
	id: string;
	name: string;
	observer: Observer;
}

type CSVFieldData = {
	name: string;
	values: string[];
};

const CSVPreviewModal: React.FC<ICSVPreviewModalProps> = ({
	className,
	fieldName,
	fileVersionId,
	groupId,
	id,
	name,
	observer,
}) => {
	const [data, setData] = useState<CSVFieldData[]>([]);

	useEffect(() => {
		const fetchData = () => {
			API.dataSource
				.fetchFieldValues({
					count: ROW_DELTA,
					fieldName,
					fileVersionId,
					groupId,
					id,
				})
				.then(setData)
				.catch(noop);
		};

		fetchData();
	}, [fieldName, fileVersionId, groupId, id]);

	return (
		<ClayModal className={className} observer={observer} size="lg">
			<ClayModal.Header>
				{sub(Liferay.Language.get('data-preview-x'), [name])}
			</ClayModal.Header>

			<ClayModal.Body>
				<div className="table-responsive">
					<ClayTable className="table-bordered">
						<ClayTable.Head>
							<ClayTable.Row>
								{data.map(({name}, i) => (
									<ClayTable.Cell
										expanded
										headingCell
										key={i}
									>
										{name}
									</ClayTable.Cell>
								))}
							</ClayTable.Row>
						</ClayTable.Head>

						<ClayTable.Body>
							{!!data.length &&
								data[0].values.map(
									(_val: string, row: number) => (
										<ClayTable.Row key={row}>
											{data.map((_val, column) => (
												<ClayTable.Cell
													key={`${column}-${row}`}
												>
													{data[column].values[row]}
												</ClayTable.Cell>
											))}
										</ClayTable.Row>
									)
								)}
						</ClayTable.Body>
					</ClayTable>
				</div>
			</ClayModal.Body>
		</ClayModal>
	);
};

export default CSVPreviewModal;
