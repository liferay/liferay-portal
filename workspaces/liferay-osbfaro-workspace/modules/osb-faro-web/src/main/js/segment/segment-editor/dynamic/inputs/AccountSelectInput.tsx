import Form from 'shared/components/form';
import React from 'react';
import {CustomValue} from 'shared/util/records';
import {fromJS, List} from 'immutable';
import {findLifecycleFromValue} from '../utils/custom-inputs';
import {ISegmentEditorCustomInputBase} from '../utils/types';
import {Option, Picker} from '@clayui/core';
import {RelationalOperators} from '../utils/constants';
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

	const lifecycle = findLifecycleFromValue(lifecycles, value);

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
						onSelectionChange={(selectedLifecycleERC) =>
							onChange({
								valid: true,
								value: value.setIn(
									['criterionGroup', 'items'],
									List([
										fromJS({
											operatorName:
												RelationalOperators.NE,
											propertyName: `lifecycleStatus@${selectedLifecycleERC}`,
											value: null,
										}),
									])
								) as CustomValue,
							})
						}
						placeholder={Liferay.Language.get('select')}
						selectedKey={lifecycle?.externalReferenceCode ?? ''}
					>
						{({externalReferenceCode, name}) => (
							<Option key={externalReferenceCode}>{name}</Option>
						)}
					</Picker>
				</Form.GroupItem>
			</Form.Group>
		</div>
	);
};

export default AccountSelectInput;
