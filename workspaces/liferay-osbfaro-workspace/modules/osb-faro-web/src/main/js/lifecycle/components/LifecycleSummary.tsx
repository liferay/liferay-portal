import BaseEditPage from 'shared/components/base-edit-page';
import LifecycleFieldRow from 'lifecycle/components/LifecycleFieldRow';
import React from 'react';
import {ClayInput} from '@clayui/form';
import {formatDateToTimeZone} from 'shared/util/date';
import {getFieldErrorMessage} from 'lifecycle/utils/lifecycleValidation';
import {getStageCountLabel} from 'lifecycle/utils/lifecycleLabels';
import {
	LIFECYCLE_DESCRIPTION_MAX_LENGTH,
	LIFECYCLE_NAME_MAX_LENGTH,
} from 'lifecycle/utils/constants';
import {noop} from 'lodash';
import {useTimeZone} from 'shared/hooks/useTimeZone';

interface ILifecycleSummaryProps {
	authorName: string;
	description: string;
	isFieldErrorVisible?: (id: string) => boolean;
	modifiedDate?: number | null;
	name: string;
	onDescriptionChange: (description: string) => void;
	onFieldBlur?: (id: string) => void;
	onNameChange: (name: string) => void;
	stageCount: number;
}

const LifecycleSummary: React.FC<ILifecycleSummaryProps> = ({
	authorName,
	description,
	isFieldErrorVisible = () => false,
	modifiedDate,
	name,
	onDescriptionChange,
	onFieldBlur = noop,
	onNameChange,
	stageCount,
}) => {
	const {timeZoneId} = useTimeZone();

	const descriptionErrorMessage = getFieldErrorMessage({
		maxLength: LIFECYCLE_DESCRIPTION_MAX_LENGTH,
		requiredMessage: Liferay.Language.get(
			'the-description-field-is-required'
		),
		showErrors: isFieldErrorVisible('lifecycleDescription'),
		value: description,
	});

	const nameErrorMessage = getFieldErrorMessage({
		maxLength: LIFECYCLE_NAME_MAX_LENGTH,
		requiredMessage: Liferay.Language.get('the-name-field-is-required'),
		showErrors: isFieldErrorVisible('lifecycleName'),
		value: name,
	});

	return (
		<>
			<div className="mb-5">
				<BaseEditPage.Title
					errorMessage={nameErrorMessage}
					id="lifecycleName"
					label={Liferay.Language.get('name')}
					maxLength={LIFECYCLE_NAME_MAX_LENGTH}
					onBlur={() => onFieldBlur('lifecycleName')}
					onChange={(event) => onNameChange(event.target.value)}
					placeholder={Liferay.Language.get('new-lifecycle')}
					required
					value={name}
				/>
			</div>

			<LifecycleFieldRow
				errorMessage={descriptionErrorMessage}
				icon="align-left"
				id="lifecycleDescription"
				label={Liferay.Language.get('description')}
				required
			>
				<ClayInput
					aria-describedby={
						descriptionErrorMessage
							? 'lifecycleDescriptionError'
							: undefined
					}
					id="lifecycleDescription"
					maxLength={LIFECYCLE_DESCRIPTION_MAX_LENGTH}
					onBlur={() => onFieldBlur('lifecycleDescription')}
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
					value={getStageCountLabel(stageCount)}
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
