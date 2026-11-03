import React from 'react';
import {CustomValue} from 'shared/util/records';
import {
	findLifecycleFromValue,
	getLifecycleStageERCsFromValue,
} from 'segment/segment-editor/dynamic/utils/custom-inputs';
import {sub} from 'shared/util/lang';
import {useChannelLifecycles} from 'shared/hooks/useChannelLifecycles';
import {useParams} from 'react-router-dom';

interface ILifecycleStageDisplayProps {
	propertyLabel: string;
	value: CustomValue;
}

const LifecycleStageDisplay: React.FC<ILifecycleStageDisplayProps> = ({
	propertyLabel,
	value,
}) => {
	const {channelId, groupId = ''} = useParams<{
		channelId: string;
		groupId: string;
	}>();

	const {lifecycles, loading} = useChannelLifecycles({channelId, groupId});

	const lifecycleStageERCs = getLifecycleStageERCsFromValue(value);

	const lifecycle = findLifecycleFromValue(lifecycles, value);

	if (lifecycle?.name) {
		if (!lifecycleStageERCs.length) {
			return <b>{`'${lifecycle.name}'`}</b>;
		}

		return (
			<>
				{lifecycleStageERCs.map((lifecycleStageERC, index) => {
					const stage = lifecycle.accountLifecycleStages?.find(
						({externalReferenceCode}) =>
							externalReferenceCode === lifecycleStageERC
					);

					return (
						<React.Fragment key={lifecycleStageERC}>
							{index > 0 && ', '}

							{stage ? (
								<b>{`'${lifecycle.name}: ${stage.name}'`}</b>
							) : (
								<b className="undefined-entity">
									{Liferay.Language.get('undefined-stage')}
								</b>
							)}
						</React.Fragment>
					);
				})}
			</>
		);
	}

	if (loading) {
		return null;
	}

	return (
		<b className="undefined-entity">
			{sub(Liferay.Language.get('undefined-x'), [propertyLabel])}
		</b>
	);
};

export default LifecycleStageDisplay;
