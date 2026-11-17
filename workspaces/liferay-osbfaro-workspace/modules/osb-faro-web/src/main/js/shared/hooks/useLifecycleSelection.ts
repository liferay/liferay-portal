import {ILifecycleDetail} from 'shared/api/lifecycle';
import {useChannelLifecycles} from 'shared/hooks/useChannelLifecycles';
import {useState} from 'react';

export const useLifecycleSelection = ({
	channelId,
	groupId,
}: {
	channelId?: string;
	groupId: string;
}): {
	lifecycles: ILifecycleDetail[];
	loading: boolean;
	selectedLifecycleId?: string;
	setSelectedLifecycleId: (lifecycleId: string) => void;
} => {
	const {lifecycles, loading} = useChannelLifecycles({channelId, groupId});

	const [selectedLifecycleId, setSelectedLifecycleId] = useState<string>();

	const defaultLifecycle =
		lifecycles.find(
			({defaultAccountLifecycle}) => defaultAccountLifecycle
		) ?? lifecycles[0];

	return {
		lifecycles,
		loading,
		selectedLifecycleId: selectedLifecycleId ?? defaultLifecycle?.id,
		setSelectedLifecycleId,
	};
};
