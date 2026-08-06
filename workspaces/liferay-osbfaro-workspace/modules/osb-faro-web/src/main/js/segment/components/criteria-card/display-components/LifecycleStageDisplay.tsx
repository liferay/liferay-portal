import React from 'react';
import {sub} from 'shared/util/lang';
import {useLifecycleStageOptions} from 'shared/hooks/useLifecycleStageOptions';
import {useParams} from 'react-router-dom';

const LifecycleStageDisplay: React.FC<{label: string; value: string}> = ({
	label,
	value,
}) => {
	const {groupId} = useParams<{groupId: string}>();

	const {loading, options} = useLifecycleStageOptions({groupId});

	const option = options.find(({value: stageId}) => stageId === value);

	if (option) {
		return <b>{`'${option.label}'`}</b>;
	}

	if (loading) {
		return null;
	}

	return (
		<b className="undefined-entity">
			{sub(Liferay.Language.get('undefined-x'), [label])}
		</b>
	);
};

export default LifecycleStageDisplay;
