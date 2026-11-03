import ClayButton from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import ClayIcon from 'shared/components/Icon';
import ClayLabel from '@clayui/label';
import Form from 'shared/components/form';
import React from 'react';
import {Conjunctions, RelationalOperators} from '../utils/constants';
import {CustomValue} from 'shared/util/records';
import {fromJS, List} from 'immutable';
import {
	findLifecycleFromValue,
	getLifecycleStageERCsFromValue,
} from '../utils/custom-inputs';
import {ISegmentEditorCustomInputBase} from '../utils/types';
import {Option, Picker} from '@clayui/core';
import {sortBy} from 'lodash';
import {useChannelLifecycles} from 'shared/hooks/useChannelLifecycles';
import {useParams} from 'react-router-dom';

interface IAccountSelectProps extends ISegmentEditorCustomInputBase {
	groupId: string;
	operatorRenderer: React.ElementType;
}

const AccountSelectInput: React.FC<IAccountSelectProps> = ({
	displayValue,
	groupId,
	onChange,
	operatorRenderer: OperatorDropdown,
	property,
	value,
}) => {
	const {channelId} = useParams();

	const {lifecycles} = useChannelLifecycles({channelId, groupId});

	const lifecycleStageERCs = getLifecycleStageERCsFromValue(value);

	const lifecycle = findLifecycleFromValue(lifecycles, value);

	const lifecycleStages = sortBy(
		lifecycle?.accountLifecycleStages ?? [],
		'position'
	);

	const selectedStages = lifecycle
		? lifecycleStageERCs.map(
				(lifecycleStageERC) =>
					lifecycleStages.find(
						({externalReferenceCode}) =>
							externalReferenceCode === lifecycleStageERC
					) ?? {
						externalReferenceCode: lifecycleStageERC,
						name: Liferay.Language.get('undefined-stage'),
					}
			)
		: [];

	const availableStages = lifecycleStages.filter(
		({externalReferenceCode}) =>
			!lifecycleStageERCs.includes(externalReferenceCode)
	);

	const lifecycleERC = lifecycle?.externalReferenceCode ?? '';

	const handleChange = (
		newLifecycleERC: string,
		newLifecycleStageERCs: string[]
	) => {
		const propertyName = `lifecycleStatus@${newLifecycleERC}`;

		const stageItems = newLifecycleStageERCs.map((lifecycleStageERC) => ({
			operatorName: RelationalOperators.EQ,
			propertyName,
			value: lifecycleStageERC,
		}));

		const items = !stageItems.length
			? [
					{
						operatorName: RelationalOperators.NE,
						propertyName,
						value: null,
					},
				]
			: stageItems.length === 1
				? stageItems
				: [{conjunctionName: Conjunctions.Or, items: stageItems}];

		onChange({
			valid: true,
			value: value.setIn(
				['criterionGroup', 'items'],
				List(items.map((item) => fromJS(item)))
			) as CustomValue,
		});
	};

	return (
		<div className="account-select-input-root criteria-statement">
			<Form.Group autoFit>
				<Form.GroupItem className="entity-name" label shrink>
					{property.entityName}
				</Form.GroupItem>

				<Form.GroupItem className="display-value" label shrink>
					{displayValue}
				</Form.GroupItem>

				<OperatorDropdown />

				<Form.GroupItem shrink>
					<Picker
						aria-label={Liferay.Language.get('lifecycle')}
						disabled={lifecycles.length === 0}
						items={lifecycles}
						onSelectionChange={(selectedLifecycleERC) => {
							if (String(selectedLifecycleERC) !== lifecycleERC) {
								handleChange(String(selectedLifecycleERC), []);
							}
						}}
						placeholder={Liferay.Language.get('select')}
						selectedKey={lifecycleERC}
					>
						{({externalReferenceCode, name}) => (
							<Option key={externalReferenceCode}>{name}</Option>
						)}
					</Picker>
				</Form.GroupItem>

				{selectedStages.length > 0 && (
					<Form.GroupItem shrink>
						<div
							className="input-group-item input-list-root"
							style={{maxWidth: '52rem', width: 'fit-content'}}
						>
							<div
								className="form-control form-control-tag-group"
								style={{
									maxWidth: '52rem',
									overflowY: 'auto',
									width: 'fit-content',
								}}
							>
								{selectedStages.map(
									({externalReferenceCode, name}) => (
										<ClayLabel
											closeButtonProps={{
												'aria-label':
													Liferay.Language.get(
														'close'
													),
												onClick: () =>
													handleChange(
														lifecycleERC,
														lifecycleStageERCs.filter(
															(
																lifecycleStageERC
															) =>
																lifecycleStageERC !==
																externalReferenceCode
														)
													),
											}}
											displayType="secondary"
											inverse
											key={externalReferenceCode}
										>
											{name}
										</ClayLabel>
									)
								)}

								<ClayButton
									aria-label={Liferay.Language.get('clear')}
									className="button-root text-secondary"
									displayType="unstyled"
									onClick={() =>
										handleChange(lifecycleERC, [])
									}
								>
									<ClayIcon
										className="icon-root"
										symbol="times-circle"
									/>
								</ClayButton>
							</div>
						</div>
					</Form.GroupItem>
				)}

				<Form.GroupItem shrink>
					<ClayDropDown
						closeOnClick
						trigger={
							<ClayButton
								className="button-root"
								disabled={!availableStages.length}
								displayType="secondary"
							>
								<ClayIcon symbol="plus" />

								<span className="ml-2">
									{Liferay.Language.get('add-stage')}
								</span>
							</ClayButton>
						}
					>
						{availableStages.map(
							({externalReferenceCode, name}) => (
								<ClayDropDown.Item
									key={externalReferenceCode}
									onClick={() =>
										handleChange(lifecycleERC, [
											...lifecycleStageERCs,
											externalReferenceCode,
										])
									}
								>
									{name}
								</ClayDropDown.Item>
							)
						)}
					</ClayDropDown>
				</Form.GroupItem>
			</Form.Group>
		</div>
	);
};

export default AccountSelectInput;
