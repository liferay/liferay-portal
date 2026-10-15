import ClayLabel from '@clayui/label';
import InputWithEditToggle from 'shared/components/InputWithEditToggle';
import React, {useCallback, useRef} from 'react';
import {DataSource} from 'shared/util/records';
import {sequence} from 'shared/util/promise';
import {Text} from '@clayui/core';
import {
	toPromise,
	validateMaxLength,
	validateRequired,
} from 'shared/util/validators';
import {validateUniqueName} from 'shared/util/data-sources';

interface IDataSourceEditableTitleProps {
	dataSource: DataSource;
	description?: React.ReactNode;
	displayType?: string;
	editable?: boolean;
	groupId: string;
	label: React.ReactNode;
	onUpdateName: (name: string) => Promise<any> | void;
}

const DataSourceEditableTitle = ({
	dataSource,
	description,
	displayType,
	editable,
	groupId,
	label,
	onUpdateName,
}: IDataSourceEditableTitleProps) => {
	const cachedNameValues = useRef(new Map());

	const handleUpdateName = useCallback(onUpdateName, [
		groupId,
		dataSource.id,
	]);

	const handleValidate = useCallback(
		(value: string) => {
			let error = null;

			if (value !== dataSource.name) {
				if (cachedNameValues.current.has(value)) {
					error = cachedNameValues.current.get(value);
				}
				else {
					error = validateUniqueName({groupId, value});

					cachedNameValues.current.set(value, error);
				}
			}

			return toPromise(error);
		},
		[dataSource.name, groupId]
	);

	return (
		<div className="mb-5">
			<ClayLabel
				className="mb-2"
				displayType={
					displayType as
						| 'secondary'
						| 'info'
						| 'warning'
						| 'danger'
						| 'success'
						| 'unstyled'
						| undefined
				}
				inverse
			>
				{label}
			</ClayLabel>

			<InputWithEditToggle
				editable={!!editable}
				inputWidth={30}
				name="dataSourceName"
				onSubmit={(name) => toPromise(handleUpdateName(name))}
				required
				validate={sequence([
					validateRequired,
					validateMaxLength(75),
					handleValidate,
				])}
				value={dataSource.name || ''}
			/>

			{description && (
				<div className="mt-1">
					<Text color="secondary" size={2}>
						{description}
					</Text>
				</div>
			)}
		</div>
	);
};

export {DataSourceEditableTitle};
