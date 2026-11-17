import React from 'react';
import {ILifecycleDetail} from 'shared/api/lifecycle';
import {Option, Picker} from '@clayui/core';

interface ILifecyclePickerProps {
	lifecycles: ILifecycleDetail[];
	onSelectionChange: (lifecycleId: string) => void;
	selectedLifecycleId?: string;
}

const LifecyclePicker: React.FC<ILifecyclePickerProps> = ({
	lifecycles,
	onSelectionChange,
	selectedLifecycleId,
}) => (
	<Picker
		aria-label={Liferay.Language.get('lifecycle')}
		disabled={lifecycles.length === 0}
		items={lifecycles}
		onSelectionChange={(lifecycleId) =>
			onSelectionChange(String(lifecycleId))
		}
		placeholder={Liferay.Language.get('select')}
		selectedKey={selectedLifecycleId}
	>
		{({id, name}) => <Option key={id}>{name}</Option>}
	</Picker>
);

export default LifecyclePicker;
