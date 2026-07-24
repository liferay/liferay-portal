import {setUriQueryValues} from 'shared/util/router';
import {useHistory} from 'react-router-dom';
import {useQueryParams} from 'shared/hooks/useQueryParams';

type Account = {
	id: string;
	name: string;
};

interface IAccountFilter {
	accountId?: string;
	accountName?: string;
	setAccount: (account: Account | null) => void;
}

/**
 * Reads the dashboard account filter from the URL query and writes it back
 * there. The URL is the single source of truth, so the pages rendering the
 * filter do not mirror the selection in component state.
 */

export const useAccountFilter = (): IAccountFilter => {
	const {accountId, accountName} = useQueryParams();
	const history = useHistory();

	return {
		accountId,
		accountName,
		setAccount: (account) =>
			history.push(
				setUriQueryValues({
					accountId: account?.id ?? null,
					accountName: account?.name ?? null,
				})
			),
	};
};
