import {
	DisplayType,
	LifecycleStages,
	lifecycleStagesLabelMap,
} from 'contacts/pages/account/utils/constants';
import {toThousands} from 'shared/util/numbers';

/**
 * Maps an account's raw firmographics onto the values the Account Info card
 * displays. A value the account does not carry maps to blank, never to a
 * placeholder, and an unmapped lifecycle stage keeps its raw name.
 */

interface IAccountInfoFirmographics {
	annualRevenue?: number;
	lifecycleStage?: string | null;
}

export interface IAccountInfoLifecycleStage {
	displayType: DisplayType;
	label: string;
}

interface IAccountInfoDisplayValues {
	lifecycleStage?: IAccountInfoLifecycleStage;
	revenue: string;
}

export const getAccountInfoDisplayValues = ({
	annualRevenue,
	lifecycleStage,
}: IAccountInfoFirmographics): IAccountInfoDisplayValues => {
	const lifecycleStageEntry =
		lifecycleStagesLabelMap[lifecycleStage as LifecycleStages];

	return {
		lifecycleStage: lifecycleStage
			? {
					displayType:
						lifecycleStageEntry?.displayType ?? 'secondary',
					label: lifecycleStageEntry?.label ?? lifecycleStage,
				}
			: undefined,
		revenue: annualRevenue ? toThousands(annualRevenue) : '',
	};
};
