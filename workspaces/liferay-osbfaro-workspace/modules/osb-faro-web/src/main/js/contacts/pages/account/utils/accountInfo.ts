import {
	DisplayType,
	isLifecycleStage,
	lifecycleStagesLabelMap,
} from 'contacts/pages/account/utils/constants';
import {formatDataValue} from 'shared/util/data-value';

/**
 * Maps an account's raw firmographics onto the values the Account Info card
 * displays. A value the account does not carry maps to no data, which the
 * bar hides, and an unmapped lifecycle stage keeps its raw name.
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
	let accountInfoLifecycleStage: IAccountInfoLifecycleStage | undefined;

	if (lifecycleStage) {
		accountInfoLifecycleStage = isLifecycleStage(lifecycleStage)
			? lifecycleStagesLabelMap[lifecycleStage]
			: {displayType: 'secondary', label: lifecycleStage};
	}

	return {
		lifecycleStage: accountInfoLifecycleStage,
		revenue: formatDataValue(annualRevenue, 'count'),
	};
};
