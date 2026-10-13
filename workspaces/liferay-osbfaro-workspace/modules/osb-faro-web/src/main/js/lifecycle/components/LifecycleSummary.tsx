import BaseEditPage from 'shared/components/base-edit-page';
import LifecycleFieldRow from 'lifecycle/components/LifecycleFieldRow';
import React from 'react';
import {ClayInput} from '@clayui/form';
import {formatDateToTimeZone} from 'shared/util/date';
import {sub} from 'shared/util/lang';
import {useTimeZone} from 'shared/hooks/useTimeZone';

interface ILifecycleSummaryProps {
	authorName: string;
	description: string;
	modifiedDate?: number | null;
	name: string;
	onDescriptionChange: (description: string) => void;
	onNameChange: (name: string) => void;
	stageCount: number;
}

const LifecycleSummary: React.FC<ILifecycleSummaryProps> = ({
	authorName,
	description,
	modifiedDate,
	name,
	onDescriptionChange,
	onNameChange,
	stageCount,
}) => {
	const {timeZoneId} = useTimeZone();

	return (
		<>
			<div className="mb-5">
				<BaseEditPage.Title
					id="lifecycleName"
					label={Liferay.Language.get('name')}
					onChange={(event) => onNameChange(event.target.value)}
					placeholder={Liferay.Language.get('new-lifecycle')}
					required
					value={name}
				/>
			</div>

			<LifecycleFieldRow
				icon="align-left"
				id="lifecycleDescription"
				label={Liferay.Language.get('description')}
				required
			>
				<ClayInput
					id="lifecycleDescription"
					onChange={(event) =>
						onDescriptionChange(event.target.value)
					}
					required
					value={description}
				/>
			</LifecycleFieldRow>

			<LifecycleFieldRow
				icon="check-circle"
				id="lifecycleStages"
				label={Liferay.Language.get('stages')}
				narrow
			>
				<ClayInput
					className="bg-white"
					id="lifecycleStages"
					readOnly
					value={
						stageCount === 1
							? Liferay.Language.get('1-stage')
							: (sub(Liferay.Language.get('x-stages'), [
									stageCount,
								]) as string)
					}
				/>
			</LifecycleFieldRow>

			<LifecycleFieldRow
				icon="user"
				id="lifecycleAuthor"
				label={Liferay.Language.get('last-modified-by')}
				narrow
			>
				<ClayInput
					className="bg-white"
					id="lifecycleAuthor"
					readOnly
					value={authorName}
				/>
			</LifecycleFieldRow>

			<LifecycleFieldRow
				icon="calendar"
				id="lifecycleModifiedDate"
				label={Liferay.Language.get('modified-date')}
				narrow
			>
				<ClayInput
					className="bg-white"
					id="lifecycleModifiedDate"
					readOnly
					value={
						modifiedDate
							? formatDateToTimeZone(
									modifiedDate,
									undefined,
									timeZoneId
								)
							: ''
					}
				/>
			</LifecycleFieldRow>
		</>
	);
};

export default LifecycleSummary;
